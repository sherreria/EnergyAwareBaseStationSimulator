package es.uvigo.det.labredes.bs;

/**
 * This class extends TaskGenerator class assuming that task arrivals
 * follow a deterministic process.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class DeterministicTaskGenerator extends TaskGenerator {
    /**
     * Creates a new deterministic traffic generator.
     *
     * @param rate task arrival rate (in tasks/s)
     */
    public DeterministicTaskGenerator (double rate) {
	super(rate);
    }

    /**
     * Returns the instant at which the next task arrives.
     *
     * @return instant at which the next task arrives (in seconds)
     */
    public double getNextArrival () {
	arrival_time += 1.0 / task_rate;
	return arrival_time;
    }
}
