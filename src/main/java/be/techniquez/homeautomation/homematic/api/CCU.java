package be.techniquez.homeautomation.homematic.api;

import java.io.IOException;
import java.util.Collection;

/**
 * Interface for a CCU.
 * 
 * @author alex
 */
public interface CCU {

	/**
	 * Connect to the CCU.
	 * 
	 * @throws 	IOException		If the connect fails for some reason.
	 */
	void connect() throws IOException;
	
	/**
	 * Disconnect.
	 * 
	 * @throws 	IOException		If an IO error occurs.
	 */
	void disconnect() throws IOException;
	
	/**
	 * Returns all the dimmers known to the system.
	 * 
	 * @return	The dimmers known to the system.
	 */
	Collection<Dimmer> getDimmers();
	
	/**
	 * Returns all the switches.
	 * 
	 * @return	The switches.
	 */
	Collection<Switch> getSwitches();
	
	/**
	 * Returns the particular dimmer.
	 * 
	 * @param 		dimmer		The dimmer to return.
	 * 
	 * @return		The dimmer.
	 */
	Dimmer getDimmer(final String name);
	
	/**
	 * Returns the particular switch.
	 * 
	 * @param 		name		The switch to return.
	 * 
	 * @return		The switch.
	 */
	Switch getSwitch(final String name);
}
