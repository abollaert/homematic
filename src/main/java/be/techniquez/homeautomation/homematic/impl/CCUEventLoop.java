package be.techniquez.homeautomation.homematic.impl;

import java.io.IOException;

import be.techniquez.homeautomation.homematic.impl.CCUChannel.CCUChannelEventHandler;

/**
 * Handles the events coming from the CCU.
 * 
 * @author alex
 */
public interface CCUEventLoop {

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
	
	/**
	 * Adds the given handler.
	 * 
	 * @param 	handler		Handler.
	 */
	void addEventHandler(final CCUChannelEventHandler handler);
	
	/**
	 * Removes the event handler.
	 * 
	 * @param 	handler		The handler to remove.
	 */
	void removeEventHandler(final CCUChannelEventHandler handler);
	
	/**
	 * Indicates whether eventing is started.
	 * 
	 * @return	<code>true</code> if the event loop is running.
	 */
	boolean isStarted();
}
