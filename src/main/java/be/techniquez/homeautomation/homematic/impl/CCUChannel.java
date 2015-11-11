package be.techniquez.homeautomation.homematic.impl;

import java.io.IOException;
import java.util.List;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.xmlapi.state.State;

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
	 * Returns the {@link State} of a channel.
	 * 
	 * @param 		channelId		The ID of the channel.
	 * 
	 * @return		The {@link State} of the channel.
	 */
	State getState(final int channelId) throws IOException;
	
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
