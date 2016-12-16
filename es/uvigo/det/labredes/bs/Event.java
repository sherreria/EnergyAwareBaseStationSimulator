package es.uvigo.det.labredes.bs;

/**
 * This class implements each of the simulated individual events.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
abstract public class Event<BaseStation> implements Comparable {
    /**
     * The instant at which the event occurs.
     */
    public double time;
    /**
     * The name of the method that handles the event.
     */
    public String handler_method_name;

    /**
     * Creates a new event ocurring at the specified time.
     *
     * @param t      instant at which the event occurs
     * @param method name of the method that handles the event
     */
    public Event (double t, String method) {
	time = t;
	handler_method_name = method;
    }

    /**
     * Compares two events based on the instant at which each event occurs.
     *
     * @param event the Event to be compared
     * @return 0 if both the specified event and this event occur at the same instant; -1 if this event is later than the specified event; and +1 if this event is earlier than the specified event
     */
    public int compareTo (Object event) {	
	if (((Event) event).time > this.time) {
	    return 1;
	} else if (((Event) event).time < this.time) {
	    return -1;
	}
	return 0;	
    }

    /**
     * Prints on standard output a message describing this event.
     */
    abstract public void print ();
}
