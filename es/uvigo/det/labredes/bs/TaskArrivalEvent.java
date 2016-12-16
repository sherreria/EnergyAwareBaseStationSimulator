package es.uvigo.det.labredes.bs;

/**
 * This class extends Event class to simulate the arrival of a new task.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class TaskArrivalEvent extends Event<BaseStation> {
    /**
     * The global task counter.
     */
    static public long task_counter = 0;
    /**
     * The task service time (in seconds).
     */
    public double task_stime;
    /**
     * The unique identifier of the new task.
     */
    public long task_id;

    /**
     * Creates a new event representing the arrival of a new task.
     *
     * @param t      instant at which the new task arrives
     * @param method name of the method that handles the task arrival
     * @param stime  time required to serve the new task
     */
    public TaskArrivalEvent (double t, String method, double stime) {
	super(t, method);
	task_stime = stime;
	task_id = task_counter;
	task_counter++;
    }

    /**
     * Compares two task arrival events.
     *
     * @param obj the Object to be compared
     * @return true if the specified event is equal to this event
     */
    public boolean equals (Object obj) {
	if (obj == this) {
	    return true;
	}
	if (!(obj instanceof TaskArrivalEvent)) {
            return false;
        }
	TaskArrivalEvent event = (TaskArrivalEvent) obj;
	if (time == event.time && task_id == event.task_id) {
	    return true;
	}
	return false;
    }

    /**
     * Prints on standard output a message describing the task arrival event.
     */
    public void print () {
	System.out.format("%.3f TaskArrivalEvent %d %d %n", time, task_id, EnergyAwareBaseStationSimulator.bs.queue_size);
    }
}
