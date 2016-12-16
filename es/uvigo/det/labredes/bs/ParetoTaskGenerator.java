package es.uvigo.det.labredes.bs;

/**
 * This class extends TaskGenerator class assuming that task arrivals
 * follow a Pareto process.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class ParetoTaskGenerator extends TaskGenerator {
    /**
     * The shape parameter.
     */
    private double alpha;
    /**
     * The scale parameter.
     */
    private double xm;

    /**
     * Creates a new Pareto task generator.
     *
     * @param rate task arrival rate (in tasks/s)
     * @param a shape parameter (alpha)
     */
    public ParetoTaskGenerator (double rate, double a) {
	super(rate);
	alpha = a;
	xm = (alpha - 1) / alpha / task_rate;
    }

    /**
     * Sets the shape parameter (alpha) for the Pareto task generator.
     *
     * @param a shape parameter (alpha)
     */
    public void setAlpha (double a) {
	alpha = a;
	xm = (alpha - 1) / alpha / task_rate;
    }

    /**
     * Returns the instant at which the next task arrives.
     *
     * @return instant at which the next task arrives (in seconds)
     */
    public double getNextArrival () {
	double rand = rng.nextDouble();
	arrival_time += xm / Math.pow(rand, 1 / alpha);	
	return arrival_time;
    }    
}
