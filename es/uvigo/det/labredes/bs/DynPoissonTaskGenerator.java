package es.uvigo.det.labredes.bs;

/**
 * This class extends TaskGenerator class assuming that task arrivals 
 * follow a Poisson process with varying arrival rate.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class DynPoissonTaskGenerator extends TaskGenerator {
    /**
     * Current step number.
     */
    private int step;
    /**
     * The variation of the task arrival rate in each step (in tasks/s).
     */
    private double rateStep;
    /**
     * The duration of each step (in seconds).
     */
    private double timeStep;
    
    /**
     * Creates a new Poisson task generator with varying arrival rate.
     *
     * @param rate initial task arrival rate (in tasks/s)
     * @param rateS variation of the task arrival rate in each step (in tasks/s)
     * @param timeS duration of each step (in seconds)
     */
    public DynPoissonTaskGenerator (double rate, double rateS, double timeS) {
	super(rate);
	step = 1;
	rateStep = rateS;
	timeStep = timeS;	
    }

    /**
     * Returns the instant at which the next task arrives.
     *
     * @return instant at which the next task arrives (in seconds)
     */
    public double getNextArrival () {
	double rand = rng.nextDouble();
	arrival_time += -1.0 * Math.log(rand) / task_rate;
	if (arrival_time > step * timeStep) {
	    setArrivalRate(task_rate + rateStep);
	    step++;
	}
	return arrival_time;
    }    
}
