package be.techniquez.homeautomation.homematic.impl;

import java.io.IOException;

/**
 * Handles the events coming from the CCU.
 * 
 * @author alex
 */
public interface CCUEventing {

	/**
	 * Starts processing events.
	 * 
	 * @throws 	IOException		If an IO error occurs.
	 */
	void start() throws IOException;
	
	/**
	 * Stops processing events.
	 * 
	 * @throws 	IOException		If an IO error occurs.
	 */
	void stop() throws IOException;
}
