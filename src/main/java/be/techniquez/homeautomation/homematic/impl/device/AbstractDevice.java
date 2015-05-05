package be.techniquez.homeautomation.homematic.impl.device;

import be.techniquez.homeautomation.homematic.api.Device;

/**
 * Abstract base class for devices.
 * 
 * @author alex
 */
public abstract class AbstractDevice implements Device {

	/** The device name. */
	private final String name;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	name		The name of the device.
	 */
	protected AbstractDevice(final String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
}
