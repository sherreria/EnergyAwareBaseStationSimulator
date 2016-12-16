package es.uvigo.det.labredes.bs;

/**
 * This class extends Event class to simulate the drop of a new arriving task.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class TaskDropEvent extends Event<BaseStation> {
    /**
     * The unique identifier of the discarded task.
     */
    public long task_id;

    /**
     * Creates a new event representing the drop of a new arriving task.
     *
     * @param t      instant at which the new arriving task is discarded
     * @param method name of the method that handles the task drop
     * @param tid    identifier of the discarded task
     */
    public TaskDropEvent (double t, String method, long tid) {
	super(t, method);
	task_id = tid;
    }

    /**
     * Compares two task drop events.
     *
     * @param obj the Object to be compared
     * @return true if the specified event is equal to this event
     */
    public boolean equals (Object obj) {
	if (obj == this) {
	    return true;
	}
	if (!(obj instanceof TaskDropEvent)) {
            return false;
        }
	TaskDropEvent event = (TaskDropEvent) obj;
	if (time == event.time && task_id == event.task_id) {
	    return true;
	}
	return false;
    }

    /**
     * Prints on standard output a message describing the task drop event.
     */
    public void print () {
	System.out.format("%.3f TaskDropEvent %d %d %n", time, task_id, EnergyAwareBaseStationSimulator.bs.queue_size);
    }
}
