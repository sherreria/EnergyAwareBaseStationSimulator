package es.uvigo.det.labredes.bs;

/**
 * This class extends ServiceTimeGenerator class assuming that service times
 * follow a lognormal distribution.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class LognormalServiceTimeGenerator extends ServiceTimeGenerator {
    /**
     * The average of the corresponding normal distribution (in seconds).
     */
    public double normal_avg;
    /**
     * The standard deviation of the corresponding normal distribution (in seconds).
     */
    public double normal_std;
    
    /**
     * Creates a new lognormal service time generator.
     *
     * @param stime    average service time (in seconds)
     * @param varstime service time variance (in seconds^2)
     */
    public LognormalServiceTimeGenerator (double stime, double varstime) {
	super(stime);
	normal_avg = Math.log(stime*stime / Math.sqrt(varstime + stime*stime));
	normal_std = Math.sqrt(Math.log(1 + varstime/stime/stime));
    }

    /**
     * Returns the time required to serve the next task.
     *
     * @return the time required to serve the next task
     */
    public double getNextServiceTime () {
	return Math.pow(Math.E, normal_avg + normal_std*rng.nextGaussian());
    }
}
