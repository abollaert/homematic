package be.techniquez.homeautomation.homematic.api;

import java.io.IOException;

/**
 * Dimmer interface.
 * 
 * @author alex
 */
public interface Dimmer extends Device {
	
	/** Enumerate the instances. This should be configurable for general purpose, but I'm doing this for myself :-) */
	public enum Instance {
		BUREAU,
		EETTAFEL,
		KEUKENTAFEL,
		LIVINGRAAM,
		ZETEL,
		ZOLDER;
	}
	
	/**
	 * Gets the dim level.
	 * 
	 * @return	The dim level.
	 */
	double getLevel() throws IOException;
	
	/**
	 * Inhibits the device.
	 * 
	 * @param 		inhibited		<code>true</code> if it should be inhibited. <code>false</code> if not.
	 * 
	 * @throws 		IOException		If an IO error occurs.
	 */
	void setInhibited(final boolean inhibited) throws IOException;
	
	/**
	 * Indicates whether or not the device is inhibited.
	 * 
	 * @param 		inhibited		<code>true</code> if it should be inhibited. <code>false</code> if not.
	 * 
	 * @throws 		IOException		If an IO error occurs.
	 */
	boolean isInhibited() throws IOException;
	
	/**
	 * Sets the level of the dimmer.
	 * 
	 * @param 		level		The new level.
	 * 
	 * @throws 		IOException		If an IO error occurs.
	 */
	void setLevel(final double level) throws IOException;
}
