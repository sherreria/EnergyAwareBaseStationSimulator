package es.uvigo.det.labredes.bs;

/**
 * This class extends ServiceTimeGenerator class assuming that service times
 * follow an exponential distribution.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class ExponentialServiceTimeGenerator extends ServiceTimeGenerator {
    /**
     * Creates a new exponential service time generator.
     *
     * @param stime average service time (in seconds)
     */
    public ExponentialServiceTimeGenerator (double stime) {
	super(stime);
    }

    /**
     * Returns the time required to serve the next task.
     *
     * @return the time required to serve the next task
     */
    public double getNextServiceTime () {
	return -1.0 * Math.log(1.0 - rng.nextDouble()) / service_time;
    }
}
