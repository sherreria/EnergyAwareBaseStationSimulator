package es.uvigo.det.labredes.bs;

import java.util.Random;

/**
 * This class simulates the arrival of a stream of tasks.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
abstract public class TaskGenerator {
    /**
     * The task arrival rate (in tasks/s).
     */
    public double task_rate;
    /**
     * The instant at which the last task arrived (in seconds).
     */
    public double arrival_time;
    /**
     * The random number generator.
     */
    public Random rng;

    /**
     * Creates a new random task generator.
     *
     * @param rate task arrival rate (in tasks/s)
     */
    public TaskGenerator (double rate) {
	task_rate = rate;
	arrival_time = 0.0;
	rng = new Random();
    }

    /**
     * Sets a new task arrival rate.
     *
     * @param rate task arrival rate (in tasks/s)
     */
    public void setArrivalRate (double rate) {
	task_rate = rate;
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
     * Returns the instant at which the next task arrives.
     *
     * @return instant at which the next task arrives (in seconds)
     */
    abstract public double getNextArrival ();
}
