package be.techniquez.homeautomation.homematic.api;

/**
 * Device.
 * 
 * @author alex
 */
public interface Device {
	
	/**
	 * Returns the name of the device.
	 * 
	 * @return	The name of the device.
	 */
	String getName();
	
	/**
	 * Returns the serial number.
	 * 
	 * @return	The serial number.
	 */
	String getSerialNumber();
}
