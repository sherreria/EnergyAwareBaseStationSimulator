package es.uvigo.det.labredes.bs;

import java.util.Random;

/**
 * This class generates random service times for tasks.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
abstract public class ServiceTimeGenerator {
    /**
     * The average service time (in seconds).
     */
    public double service_time;
    /**
     * The random number generator.
     */
    public Random rng;

    /**
     * Creates a new random service time generator.
     *
     * @param stime average service time (in seconds)
     */
    public ServiceTimeGenerator (double stime) {
	service_time = stime;
	rng = new Random();
    }

    /**
     * Sets a new average service time.
     *
     * @param stime average service time (in seconds)
     */
    public void setServiceTime (double stime) {
	service_time = stime;
    }

    /**
     * Sets the seed for the random number generator.
     *
     * @param seed initial seed
     */
    public void setSeed (long seed) {
	rng.setSeed(seed);
    }

    /**
     * Returns the time required to serve the next task.
     *
     * @return the time required to serve the next task (in seconds)
     */
    abstract public double getNextServiceTime ();
}
