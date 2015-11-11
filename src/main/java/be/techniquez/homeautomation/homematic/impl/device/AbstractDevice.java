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
	
	/** The serial number. */
	private final String serialNumber;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	name			The name of the device.
	 * @param	serialNumber	The serial number of the device.
	 */
	protected AbstractDevice(final String name, final String serialNumber) {
		this.name = name;
		this.serialNumber = serialNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final String getSerialNumber() {
		return this.serialNumber;
	}
}
