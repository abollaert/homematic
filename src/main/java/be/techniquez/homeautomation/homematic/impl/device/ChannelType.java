package be.techniquez.homeautomation.homematic.impl.device;

/**
 * Enumerates the types of channels we use.
 * 
 * @author alex
 */
public enum ChannelType {

	LEVEL("LEVEL");
	
	private final String name;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	name		The name of the type.
	 */
	private ChannelType(final String name) {
		this.name = name;
	}
	
	/**
	 * Returns the type name.
	 * 
	 * @return	The name of the type.
	 */
	final String getTypeName() {
		return this.name;
	}
}
