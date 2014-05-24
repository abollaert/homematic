package be.techniquez.homeautomation.homematic.api;

import java.io.IOException;

/**
 * Dimmer interface.
 * 
 * @author alex
 */
public interface Dimmer extends Device {
	
	public enum Instance {
		BUREAU,
		EETTAFEL,
		KEUKENTAFEL,
		LIVINGRAAM,
		ZETEL,
		ZOLDER;
	}
	
	/**
	 * Gets the dim percentage.
	 * 
	 * @return	The dim percentage.
	 */
	double getPercentage() throws IOException;
	
	void setInhibited(final boolean inhibited) throws IOException;
	
	boolean isInhibited() throws IOException;
	
	void setPercentage(final double percentage) throws IOException;
}
