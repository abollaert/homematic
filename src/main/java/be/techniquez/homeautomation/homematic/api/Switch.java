package be.techniquez.homeautomation.homematic.api;

/**
 * Represents an on/off switch.
 * 
 * @author alex
 */
public interface Switch extends Device {
	
	/**
	 * Indicates whether the switch is on.
	 * 
	 * @return	<code>true</code> if on, <code>false</code> otherwise.
	 */
	boolean isOn();
	
	/**
	 * Sets the state to on of off.
	 * 
	 * @param 	on		Set on or off.
	 */
	void setOn(final boolean on);
}
