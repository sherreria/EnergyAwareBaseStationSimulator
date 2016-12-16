package es.uvigo.det.labredes.bs;

/**
 * This class extends ServiceTimeGenerator class assuming that service times
 * follow a deterministic process.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class DeterministicServiceTimeGenerator extends ServiceTimeGenerator {
    /**
     * Creates a new deterministic service time generator.
     *
     * @param stime average service time (in seconds)
     */
    public DeterministicServiceTimeGenerator (double stime) {
	super(stime);
    }

    /**
     * Returns the time required to serve the next task.
     *
     * @return the time required to serve the next task
     */
    public double getNextServiceTime () {
	return service_time;
    }
}
