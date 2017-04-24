package es.uvigo.det.labredes.bs;

import java.util.Map;
import java.util.HashMap;

/**
 * This class simulates an energy aware base station.
 *
 * @author Sergio Herreria-Alonso 
 * @version 1.0
 */
public class BaseStation {
    /**
     * The task generator.
     */
    public TaskGenerator task_generator;
    /**
     * The service time generator.
     */
    public ServiceTimeGenerator service_time_generator;
    /**
     * The task queue.
     */
    public EventList queue;
    /**
     * The amount of tasks stored in the task queue.
     */
    public int queue_size;
    /**
     * The maximum amount of tasks that can be stored in the task queue.
     */
    public int max_queue_size;
    /**
     * The base station state.
     */
    private BaseStationState state;

    // Statistics variables
    private long tasks_received, tasks_served, tasks_discarded, num_transitions;
    private double sum_tasks_delay, maximum_task_delay;
    private double last_state_transition_time;
    private long tasks_received_in_current_cycle;
    private double prev_cycle_end_time, weighted_sum_sleep_to_active_qth;
    private double power_ratio;
    private Map<BaseStationState, Double> time_in_states;

    /**
     * Creates a new base station.
     * Its load is simulated with the specified task and service time generators.
     *
     * @param tg the task generator
     * @param stg the service time generator
     */
    public BaseStation (TaskGenerator tg, ServiceTimeGenerator stg) {
	task_generator = tg;
	service_time_generator = stg;
        queue = new EventList(EnergyAwareBaseStationSimulator.simulation_length);
        queue_size = max_queue_size = 0;

	last_state_transition_time = 0;
        time_in_states = new HashMap<BaseStationState, Double>();
        for (BaseStationState st : BaseStationState.values()) {
            time_in_states.put(st, 0.0);
        }

	state = BaseStationState.ACTIVE_IDLE;
	EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (0, "handleStateTransitionEvent", state));
	EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (EnergyAwareBaseStationSimulator.inactivity_t, "handleStateTransitionEvent", BaseStationState.TRANSITION_TO_SLEEP));

	tasks_received = tasks_served = tasks_discarded = num_transitions = 0;
	sum_tasks_delay = maximum_task_delay = 0.0;
	tasks_received_in_current_cycle = 0;
	prev_cycle_end_time = 0.0;
	weighted_sum_sleep_to_active_qth = 0.0;
	power_ratio = (EnergyAwareBaseStationSimulator.idle_consumption - EnergyAwareBaseStationSimulator.sleep_consumption) / 
	    (EnergyAwareBaseStationSimulator.transition_consumption - EnergyAwareBaseStationSimulator.idle_consumption);
	EnergyAwareBaseStationSimulator.event_handler.addEvent(new TaskArrivalEvent (task_generator.getNextArrival(), "handleTaskArrivalEvent", service_time_generator.getNextServiceTime()));
    }

    /**
     * Handles the specified task arrival event.
     *
     * @param event the TaskArrivalEvent to be handled
     */
    public void handleTaskArrivalEvent (TaskArrivalEvent event) {
	tasks_received++;
        if (max_queue_size == 0 || queue_size + 1 <= max_queue_size) {
            queue_size++;
	    if (EnergyAwareBaseStationSimulator.dynamic_qth) {
		tasks_received_in_current_cycle++;
	    }
            queue.addEvent(event);
            if (EnergyAwareBaseStationSimulator.simulation_verbose) {
                event.print();
            }
        } else {
            EnergyAwareBaseStationSimulator.event_handler.addEvent(new TaskDropEvent (event.time, "handleTaskDropEvent", event.task_id));
        }

        EnergyAwareBaseStationSimulator.event_handler.addEvent(new TaskArrivalEvent (task_generator.getNextArrival(), "handleTaskArrivalEvent", service_time_generator.getNextServiceTime()));
	if (queue_size == 1) {
	    if (state == BaseStationState.ACTIVE_IDLE) {
		if (EnergyAwareBaseStationSimulator.inactivity_t > 0) {
		    EnergyAwareBaseStationSimulator.event_handler.removeStateTransitionEvent(BaseStationState.TRANSITION_TO_SLEEP);
		}
		EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time, "handleStateTransitionEvent", BaseStationState.ACTIVE_SERVE));
	    }
	    if (EnergyAwareBaseStationSimulator.scheme.equals("coalescing") && EnergyAwareBaseStationSimulator.sleep_to_active_tth > 0 && 
		(state == BaseStationState.TRANSITION_TO_SLEEP || state == BaseStationState.SLEEP)) {
		EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time + EnergyAwareBaseStationSimulator.sleep_to_active_tth, "handleStateTransitionEvent", BaseStationState.TRANSITION_TO_ACTIVE));
	    }
	}

	if (EnergyAwareBaseStationSimulator.scheme.equals("coalescing") && state == BaseStationState.SLEEP && queue_size >= EnergyAwareBaseStationSimulator.sleep_to_active_qth) {
	    if (EnergyAwareBaseStationSimulator.sleep_to_active_tth > 0) {
		EnergyAwareBaseStationSimulator.event_handler.removeStateTransitionEvent(BaseStationState.TRANSITION_TO_ACTIVE);
	    }
	    EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time, "handleStateTransitionEvent", BaseStationState.TRANSITION_TO_ACTIVE));
	}
    }

    /**
     * Handles the specified task drop event.
     *
     * @param event the TaskDropEvent to be handled
     */
    public void handleTaskDropEvent (TaskDropEvent event) {
        tasks_discarded++;
        if (EnergyAwareBaseStationSimulator.simulation_verbose) {
            event.print();
        }
    }

    /**
     * Handles the specified task service event.
     *
     * @param event the TaskServiceEvent to be handled
     */
    public void handleTaskServiceEvent (TaskServiceEvent event) {
        if (queue_size == 0 || ((TaskArrivalEvent) (queue.getNextEvent(false))).task_id != event.task_id) {
	    event.print();
            EnergyAwareBaseStationSimulator.printError("Trying to serve an invalid task!");
        }
	queue_size--;
        tasks_served++;
        double current_task_delay = event.time - queue.getNextEvent(true).time - event.task_stime;
        if (current_task_delay > maximum_task_delay) {
            maximum_task_delay = current_task_delay;
        }
        sum_tasks_delay += current_task_delay;
	if (EnergyAwareBaseStationSimulator.simulation_verbose) {
            event.print();
        }
	if (queue_size > 0) {
	    TaskArrivalEvent next_task = (TaskArrivalEvent) (queue.getNextEvent(false));
	    double task_stime = next_task.task_stime;
	    EnergyAwareBaseStationSimulator.event_handler.addEvent(new TaskServiceEvent (event.time + task_stime, "handleTaskServiceEvent", next_task.task_id, task_stime));
	} else {
	    if (EnergyAwareBaseStationSimulator.dynamic_qth) {
		double avg_arrival_rate = tasks_received_in_current_cycle / (event.time - prev_cycle_end_time);
		weighted_sum_sleep_to_active_qth += EnergyAwareBaseStationSimulator.sleep_to_active_qth * (event.time - prev_cycle_end_time);
		prev_cycle_end_time = event.time;
		tasks_received_in_current_cycle = 0;
		EnergyAwareBaseStationSimulator.sleep_to_active_qth = (int) Math.ceil(avg_arrival_rate * (2.0 * EnergyAwareBaseStationSimulator.target_delay - EnergyAwareBaseStationSimulator.sleep_to_active_t));
		if (EnergyAwareBaseStationSimulator.sleep_to_active_qth < 1) {
		    EnergyAwareBaseStationSimulator.sleep_to_active_qth = 1;
		}
	    }
	    if (EnergyAwareBaseStationSimulator.inactivity_t > 0) {
		EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time, "handleStateTransitionEvent", BaseStationState.ACTIVE_IDLE));
	    }
	    EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time + EnergyAwareBaseStationSimulator.inactivity_t, "handleStateTransitionEvent", BaseStationState.TRANSITION_TO_SLEEP));
	}
    }

    /**
     * Handles the specified state transition event.
     *
     * @param event the StateTransitionEvent to be handled
     */
    public void handleStateTransitionEvent (StateTransitionEvent event) {

	if (event.new_state == BaseStationState.ACTIVE_SERVE) {
	    if (queue_size > 0) {
		long tid = ((TaskArrivalEvent) (queue.getNextEvent(false))).task_id;
		double service_time = service_time_generator.getNextServiceTime();
		EnergyAwareBaseStationSimulator.event_handler.addEvent(new TaskServiceEvent (event.time + service_time, "handleTaskServiceEvent", tid, service_time));
	    } else if (EnergyAwareBaseStationSimulator.scheme.equals("single")) {
		event.new_state = BaseStationState.ACTIVE_IDLE;
	    } else {
		EnergyAwareBaseStationSimulator.printError("Trying to activate the base station with no task to serve!");
	    }
	} else if (event.new_state == BaseStationState.TRANSITION_TO_SLEEP) {
	    EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time + EnergyAwareBaseStationSimulator.active_to_sleep_t, "handleStateTransitionEvent", BaseStationState.SLEEP));
	} else if (event.new_state == BaseStationState.TRANSITION_TO_ACTIVE) {
	    if (queue_size == 0 && EnergyAwareBaseStationSimulator.scheme.equals("multiple")) {
		EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time + EnergyAwareBaseStationSimulator.sleeping_t, "handleStateTransitionEvent", BaseStationState.TRANSITION_TO_ACTIVE));
		event.new_state = BaseStationState.SLEEP;
	    } else {
		EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time + EnergyAwareBaseStationSimulator.sleep_to_active_t, "handleStateTransitionEvent", BaseStationState.ACTIVE_SERVE));
	    }
	    num_transitions++;
	} else if (event.new_state == BaseStationState.SLEEP) {
	    if (EnergyAwareBaseStationSimulator.scheme.equals("coalescing")) {
		if (queue_size >= EnergyAwareBaseStationSimulator.sleep_to_active_qth) {
		    EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time, "handleStateTransitionEvent", BaseStationState.TRANSITION_TO_ACTIVE));
		} 
	    } else {
		EnergyAwareBaseStationSimulator.event_handler.addEvent(new StateTransitionEvent (event.time + EnergyAwareBaseStationSimulator.sleeping_t, "handleStateTransitionEvent", BaseStationState.TRANSITION_TO_ACTIVE));
	    }
	}

	time_in_states.put(state, time_in_states.get(state) + event.time - last_state_transition_time);
        state = event.new_state;
        last_state_transition_time = event.time;
        if (EnergyAwareBaseStationSimulator.simulation_verbose) {
            event.print();
        }
    }

    /**
     * Prints on standard output some statistics.
     */
    public void printStatistics () {
        System.out.format("Tasks: received %d served %d discarded %d %n", tasks_received, tasks_served, tasks_discarded);
        if (tasks_served > 0) {
            System.out.format("Task delay: average %.3f max %.3f %n", sum_tasks_delay / tasks_served, maximum_task_delay);
        }
	time_in_states.put(state, time_in_states.get(state) + EnergyAwareBaseStationSimulator.simulation_length - last_state_transition_time);
        for (BaseStationState st : BaseStationState.values()) {
            System.out.format("Time in state %s: %.3f %.2f %% %n", st, time_in_states.get(st), 100.0 * time_in_states.get(st) / EnergyAwareBaseStationSimulator.simulation_length);
        }

	double power_consumption = (time_in_states.get(BaseStationState.ACTIVE_SERVE) * EnergyAwareBaseStationSimulator.active_consumption + 
				    time_in_states.get(BaseStationState.ACTIVE_IDLE) * EnergyAwareBaseStationSimulator.idle_consumption + 
				    time_in_states.get(BaseStationState.TRANSITION_TO_SLEEP) * EnergyAwareBaseStationSimulator.transition_consumption + 
				    time_in_states.get(BaseStationState.TRANSITION_TO_ACTIVE) * EnergyAwareBaseStationSimulator.transition_consumption + 
				    time_in_states.get(BaseStationState.SLEEP) * EnergyAwareBaseStationSimulator.sleep_consumption) / EnergyAwareBaseStationSimulator.simulation_length;
	double rho = task_generator.task_rate * service_time_generator.service_time;
	double norm_power_consumption = power_consumption / (rho * EnergyAwareBaseStationSimulator.active_consumption + (1 - rho) * EnergyAwareBaseStationSimulator.idle_consumption);
	System.out.format("Average power consumption: %.4f %.4f %n", power_consumption, norm_power_consumption);

	if (EnergyAwareBaseStationSimulator.dynamic_qth) {
	    weighted_sum_sleep_to_active_qth += EnergyAwareBaseStationSimulator.sleep_to_active_qth * (EnergyAwareBaseStationSimulator.simulation_length - prev_cycle_end_time);
	    System.out.format("Average coalescing threshold: %.4f %n", weighted_sum_sleep_to_active_qth / EnergyAwareBaseStationSimulator.simulation_length);
	} else {
	    System.out.format("Average coalescing threshold: %d %n", EnergyAwareBaseStationSimulator.sleep_to_active_qth);
	}
	System.out.format("Transitions rate: %.4f %n", num_transitions * 3600.0 / EnergyAwareBaseStationSimulator.simulation_length);
    }
}
