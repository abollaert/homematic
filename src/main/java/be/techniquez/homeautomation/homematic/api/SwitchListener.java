package be.techniquez.homeautomation.homematic.api;

/**
 * Listener for switch events.
 * 
 * @author alex
 */
@FunctionalInterface
public interface SwitchListener {

	/**
	 * Switch listener.
	 * 
	 * @param 	on			The new state. <code>true</code> for on, <code>false</code> for off.
	 */
	void stateChanged(final boolean on);
}
