package be.techniquez.homeautomation.homematic.impl;

import java.io.IOException;
import java.util.List;

import be.techniquez.homeautomation.homematic.api.Device;

/**
 * Channel to the CCU. Encapsulates the communication.
 * 
 * @author alex
 */
public interface CCUChannel {
	
	/**
	 * Returns a {@link List} of devices.
	 * 
	 * @return	A list of devices.
	 * 
	 * @throws 	IOException
	 */
	List<Device> getDevices() throws IOException;
	
	/**
	 * Returns the hostname.
	 * 
	 * @return	The hostname.
	 */
	String getHostname();
	
	/**
	 * Returns the port.
	 * 
	 * @return	The port.
	 */
	int getPort();
}
