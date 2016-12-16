package es.uvigo.det.labredes.bs;

/**
 * This class extends TaskGenerator class assuming that task arrivals 
 * follow a Poisson process.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class PoissonTaskGenerator extends TaskGenerator {
    /**
     * Creates a new Poisson task generator.
     *
     * @param rate task arrival rate (in tasks/s)
     */
    public PoissonTaskGenerator (double rate) {
	super(rate);
    }

    /**
     * Returns the instant at which the next task arrives.
     *
     * @return instant at which the next task arrives (in seconds)
     */
    public double getNextArrival () {
	double rand = rng.nextDouble();
	arrival_time += -1.0 * Math.log(rand) / task_rate;
	return arrival_time;
    }    
}
