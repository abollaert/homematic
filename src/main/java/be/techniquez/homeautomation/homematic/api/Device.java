package be.techniquez.homeautomation.homematic.api;

/**
 * Device.
 * 
 * @author alex
 */
public interface Device {

	/** The type of a device. */
	public enum Type {
		WIRED,
		WIRELESS,
		SYSTEM;
	}

	/**
	 * Returns the name of the device.
	 * 
	 * @return	The name of the device.
	 */
	String getName();
}
