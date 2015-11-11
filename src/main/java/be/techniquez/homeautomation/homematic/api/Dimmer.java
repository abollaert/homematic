package be.techniquez.homeautomation.homematic.api;

/**
 * Dimmer interface.
 * 
 * @author alex
 */
public interface Dimmer extends Device {
	
	/**
	 * Dims to the given percentage.
	 * 
	 * @param 	percentage		The percentage to dim to. Between 0 and 100.
	 */
	void setDimmerValue(final int percentage);
	
	/**
	 * Returns the percentage dimmed.
	 * 
	 * @return	The percentage dimmed.
	 */
	int getDimmerValue();
}
