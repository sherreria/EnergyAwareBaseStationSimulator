package es.uvigo.det.labredes.bs;

import java.io.*;

/**
 * EnergyAwareBaseStationSimulator: a Java program that simulates an energy aware base station.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public final class EnergyAwareBaseStationSimulator {

    /* Simulation parameters */
    /**
     * Length of the simulation (in seconds). Default = 10 seconds.
     */
    public static double simulation_length = 10;
    /**
     * Seed for the simulation. Default = 123456789.
     */
    public static long simulation_seed = 123456789;
    /**
     * If true a message for each simulated event is printed on standard output. Default = false.
     */
    public static boolean simulation_verbose = false;

    /* Base station parameters */
    public static BaseStation bs;
    public static double active_consumption = 1;
    public static double idle_consumption = 0.7;
    public static double sleep_consumption = 0.1;
    public static double transition_consumption = 0.7;
    public static double active_to_sleep_t = 0;
    public static double sleep_to_active_t = 5;

    /* Energy management parameters */
    public static String scheme = "coalescing";
    public static double inactivity_t = 2;
    public static int sleep_to_active_qth = 1; 
    public static double sleeping_t = 5;
    public static double sleep_to_active_tth = 0;
    public static boolean dynamic_qth = false;
    public static double target_delay = 0;

    /**
     * Event handler.
     */
    public static EventList event_handler;

    private EnergyAwareBaseStationSimulator () {}

    /**
     * Prints on standard error the specified message and exits.
     */
    public static void printError (String s) {
	System.err.println("ERROR: " + s);
	System.exit(1);
    }

    /**
     * Main method.
     * Usage: java EnergyAwareBaseStationSimulator [-l simulation_length] [-s simulation_seed] [-f config_file] [-v]
     */
    public static void main (String[] args) {
	BufferedReader simulation_file = null;

	// Task parameters
	String task_distribution = "deterministic";
        double arrival_rate = 0.25; // in tasks per second
	String service_time_distribution = "deterministic";
	double service_time = 1; // in seconds
	double alpha_pareto = 2.5; // if pareto task distribution
	double rate_step = 0.05; // in tasks per second (if dynamic poisson task distribution)
	double time_step = 3600; // in seconds (if dynamic poisson task distribution)
	double uniform_range = 1; // in seconds (if uniform service time distribution)

	// Arguments parsing
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-l")) {
		try {
		    simulation_length = Double.parseDouble(args[i+1]);
		} catch (NumberFormatException e) {
		    printError("Invalid simulation length!");
		}
		i++;
	    } else if (args[i].equals("-s")) {
		try {
		    simulation_seed = Integer.parseInt(args[i+1]);
		} catch (NumberFormatException e) {
		    printError("Invalid simulation seed!");
		}
		i++;
	    } else if (args[i].equals("-f")) {
		try {
		    simulation_file = new BufferedReader(new FileReader(args[i+1]));
		} catch (FileNotFoundException e) {
		    printError("Config file not found!");
		}
		i++;
	    } else if (args[i].equals("-v")) {
                simulation_verbose = true;
	    } else {
		printError("Unknown argument: " + args[i] + "\nUsage: java EnergyAwareBaseStationSimulator [-l simulation_length] [-s simulation_seed] [-f config_file] [-v]");
	    }
	}

	// Config file parsing	
	if (simulation_file != null) {
	    try {
		for (String line; (line = simulation_file.readLine()) != null;) {
		    if (line.startsWith(";")) {
			// Just a comment
			continue;
		    } else {
			String[] line_fields = line.split("\\s+");
			if (line_fields[0].equals("TASKS")) {
			    if (line_fields[1].equals("deterministic") || line_fields[1].equals("poisson") || line_fields[1].equals("pareto") || line_fields[1].equals("dynpoisson")) {
				task_distribution = line_fields[1];
			    } else {
				printError("Config file: invalid task distribution!");
			    }
			    try {
				arrival_rate = Double.parseDouble(line_fields[2]);
			    } catch (NumberFormatException e) {
				printError("Config file: invalid task arrival rate!");
			    }
			    if (arrival_rate <= 0) {
				printError("Config file: invalid task arrival rate!");
			    }
			    if (line_fields[1].equals("pareto")) {
				try {
				    alpha_pareto = Double.parseDouble(line_fields[3]);
				} catch (NumberFormatException e) {
				    printError("Config file: invalid alpha pareto parameter!");
				}
				if (alpha_pareto <= 1) {
				    printError("Config file: invalid alpha pareto parameter!");
				}
			    }
			    if (line_fields[1].equals("dynpoisson")) {
				try {
				    rate_step = Double.parseDouble(line_fields[3]);
				} catch (NumberFormatException e) {
				    printError("Config file: invalid rate step parameter!");
				}
				try {
				    time_step = Double.parseDouble(line_fields[4]);
				} catch (NumberFormatException e) {
				    printError("Config file: invalid time step parameter!");
				}				
				if (time_step <= 0) {
				    printError("Config file: invalid time step parameter!");
				}
			    }
			} else if (line_fields[0].equals("SERVICE")) {
			    if (line_fields[1].equals("deterministic") || line_fields[1].equals("uniform") || line_fields[1].equals("exponential")) {
				service_time_distribution = line_fields[1];
			    } else {
				printError("Config file: invalid service time distribution!");
			    }
			    try {
				service_time = Double.parseDouble(line_fields[2]);
			    } catch (NumberFormatException e) {
				printError("Config file: invalid service time!");
			    }
			    if (service_time <= 0) {
				printError("Config file: invalid service time!");
			    }
			    if (line_fields[1].equals("uniform")) {
				try {
				    uniform_range = Double.parseDouble(line_fields[3]);
				} catch (NumberFormatException e) {
				    printError("Config file: invalid uniform range length!");
				}
				if (uniform_range <= 0 || service_time - uniform_range/2.0 <= 0) {
				    printError("Config file: invalid uniform range length!");
				}
			    }
			} else if (line_fields[0].equals("POWER")) {
			    try {
				active_consumption = Double.parseDouble(line_fields[1]);
				idle_consumption = Double.parseDouble(line_fields[2]);
				sleep_consumption = Double.parseDouble(line_fields[3]);
				transition_consumption = Double.parseDouble(line_fields[4]);
			    } catch (NumberFormatException e) {
				printError("Config file: invalid power consumptions!");
			    }
			    if (active_consumption < 0 || idle_consumption < 0 || sleep_consumption < 0 || transition_consumption < 0 || 
				idle_consumption > active_consumption || sleep_consumption > idle_consumption) {
				printError("Config file: invalid power consumptions!");
			    }
			} else if (line_fields[0].equals("TRANSITIONS")) {
			    try {
				active_to_sleep_t = Double.parseDouble(line_fields[1]);
				sleep_to_active_t = Double.parseDouble(line_fields[2]);
			    } catch (NumberFormatException e) {
				printError("Config file: invalid transition times!");
			    }
			    if (active_to_sleep_t < 0 || sleep_to_active_t < 0) {
				printError("Config file: invalid transition times!");
			    }
			} else if (line_fields[0].equals("SCHEME")) {
			    if (line_fields[1].equals("single") || line_fields[1].equals("multiple") || line_fields[1].equals("coalescing")) {
				scheme = line_fields[1];
			    } else {
				printError("Config file: invalid energy management scheme!");
			    }
			    if (line_fields[1].equals("coalescing")) {
				try {
				    inactivity_t = Double.parseDouble(line_fields[2]);
				    sleep_to_active_qth = Integer.parseInt(line_fields[3]);
				    sleep_to_active_tth = Double.parseDouble(line_fields[4]);
				    if (sleep_to_active_qth == 0) {
					dynamic_qth = true;
					sleep_to_active_qth = 1;
					target_delay = Double.parseDouble(line_fields[5]);
				    }
				} catch (NumberFormatException e) {
				    printError("Config file: invalid coalescing parameters!");
				}
				if (inactivity_t < 0 || sleep_to_active_qth <= 0 || sleep_to_active_tth < 0 || target_delay < 0) {
				    printError("Config file: invalid coalescing parameters!");
				}
			    } else {
				try {
				    inactivity_t = Double.parseDouble(line_fields[2]);
				    sleeping_t = Double.parseDouble(line_fields[3]);
				} catch (NumberFormatException e) {
				    printError("Config file: invalid sleeping times!");
				}
				if (inactivity_t < 0 || sleeping_t <= 0) {
				    printError("Config file: invalid sleeping times!");
				}
			    }
			}
		    }
		}
		simulation_file.close();
	    } catch (IOException e) {
		printError("Error while reading config file!");
	    }
	}

	// Event handler initialization
	event_handler = new EventList(simulation_length);

	// Base station initialization
	TaskGenerator tg = null;
	if (task_distribution.equals("deterministic")) {
	    tg = new DeterministicTaskGenerator(arrival_rate);
	} else if (task_distribution.equals("poisson")) {
	    tg = new PoissonTaskGenerator(arrival_rate);
	} else if (task_distribution.equals("pareto")) {
	    tg = new ParetoTaskGenerator(arrival_rate, alpha_pareto);
	} else if (task_distribution.equals("dynpoisson")) {
	    tg = new DynPoissonTaskGenerator(arrival_rate, rate_step, time_step);
	}   
	tg.setSeed(simulation_seed);
	
	ServiceTimeGenerator stg = null;
	if (service_time_distribution.equals("deterministic")) {
	    stg = new DeterministicServiceTimeGenerator(service_time);
	} else if (service_time_distribution.equals("uniform")) {
	    stg = new UniformServiceTimeGenerator(service_time, uniform_range);
	} else if (service_time_distribution.equals("exponential")) {
	    stg = new ExponentialServiceTimeGenerator(service_time);
	}	
	stg.setSeed(simulation_seed + 1);
	bs = new BaseStation(tg, stg);	

	// Events processing
	Event event;
        while ((event = event_handler.getNextEvent(true)) != null) {
	    event_handler.handleEvent(event);
	}

	// Print statistics
	bs.printStatistics();
    }
}
