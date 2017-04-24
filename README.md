# EnergyAwareBaseStationSimulator
A Java program that simulates an energy-aware base station that can be autonomously governed.

# Invocation
java EnergyAwareBaseStationSimulator [-l simulation_length] [-s simulation_seed] [-f config_file] [-v]

# Output
The simulator outputs a summary of the main base station statistics:

    - Number of tasks received, served and discarded

    - Average and maximum task delay (in seconds)

    - Time in each power state (in seconds)

    - Average power consumption (in Watts and normalized)

    - Transitions rate (in number of transitions per hour)

With option -v, the simulator outputs a line for every simulated event:

    `event_time event_type event_info`

# Legal
Copyright ⓒ Sergio Herrería Alonso <sha@det.uvigo.es> 2016

This simulator is licensed under the GNU General Public License, version 3 (GPL-3.0). For more information see LICENSE.txt

