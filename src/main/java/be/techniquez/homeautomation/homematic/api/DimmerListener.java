package be.techniquez.homeautomation.homematic.api;

/**
 * Listener for dimmer events.
 * 
 * @author alex
 */
@FunctionalInterface
public interface DimmerListener {

	/**
	 * Called when the dimmer changes state.
	 * 
	 * @param 	value		The new value.
	 */
	void dimmed(final int value);
}
