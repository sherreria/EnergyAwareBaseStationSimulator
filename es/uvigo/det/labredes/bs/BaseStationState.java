package es.uvigo.det.labredes.bs;

/**
 * All the possible states of the energy aware base station.
 */
public enum BaseStationState {
    /**
     * The base station is active but idle.
     */
    ACTIVE_IDLE,
	/**
	 * The base station is active and serving a task.
	 */
	ACTIVE_SERVE,
	/**
	 * The base station is in the sleep mode.
	 */
	SLEEP,
	/**
	 * The base station is transitioning to active from the sleep mode.
	 */
	TRANSITION_TO_ACTIVE,
	/**
	 * The base station is transitioning from active to the sleep mode.
	 */
	TRANSITION_TO_SLEEP;
}
