package es.uvigo.det.labredes.bs;

/**
 * This class extends ServiceTimeGenerator class assuming that service times
 * follow an uniform process.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class UniformServiceTimeGenerator extends ServiceTimeGenerator {
    /**
     * The minimum service time (in seconds).
     */
    public double min_service_time;
    /**
     * The maximum service time (in seconds).
     */
    public double max_service_time;

    /**
     * Creates a new uniform service time generator.
     *
     * @param stime average service time (in seconds)
     * @param length length of the uniform range (in seconds)
     */
    public UniformServiceTimeGenerator (double stime, double length) {
	super(stime);
	min_service_time = stime - length/2.0;
	max_service_time = stime + length/2.0;
    }

    /**
     * Returns the time required to serve the next task.
     *
     * @return the time required to serve the next task
     */
    public double getNextServiceTime () {
	return min_service_time + (max_service_time - min_service_time) * rng.nextDouble();
    }
}
