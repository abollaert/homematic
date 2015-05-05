package be.techniquez.homeautomation.homematic.impl.device;

import be.techniquez.homeautomation.homematic.api.Dimmer;

/**
 * Dimmer class.
 * 
 * @author alex
 */
public final class DimmerImpl extends AbstractDevice implements Dimmer {

	/**
	 * Create a new instance.
	 * 
	 * @param 	name	The name.
	 */
	public DimmerImpl(final String name) {
		super(name);
	}
}
