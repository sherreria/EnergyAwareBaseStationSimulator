package es.uvigo.det.labredes.bs;

/**
 * This class extends Event class to simulate the service of a task.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class TaskServiceEvent extends Event<BaseStation> {
    /**
     * The task service time (in seconds).
     */
    public double task_stime;
    /**
     * The unique identifier of the task served.
     */
    public long task_id;

    /**
     * Creates a new event representing the service of a task.
     *
     * @param t      instant at which the service of the task finishes
     * @param method name of the method that handles the service of the task
     * @param tid    identifier of the task served
     * @param stime  time required to serve the task
     */
    public TaskServiceEvent (double t, String method, long tid, double stime) {
	super(t, method);
	task_id = tid;
	task_stime = stime;
    }

    /**
     * Compares two task service events.
     *
     * @param obj the Object to be compared
     * @return true if the specified event is equal to this event
     */
    public boolean equals (Object obj) {
	if (obj == this) {
	    return true;
	}
	if (!(obj instanceof TaskServiceEvent)) {
            return false;
        }
	TaskServiceEvent event = (TaskServiceEvent) obj;
	if (time == event.time && task_id == event.task_id) {
	    return true;
	}
	return false;
    }

    /**
     * Prints on standard output a message describing the task service event.
     */
    public void print () {
	System.out.format("%.3f TaskServiceEvent %d %d %d%n", time, task_id, EnergyAwareBaseStationSimulator.bs.queue_size, EnergyAwareBaseStationSimulator.sleep_to_active_qth);
    }
}
