package be.techniquez.homeautomation.homematic.impl.device;

import java.io.IOException;
import java.util.Hashtable;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.impl.channel.CCUChannel;


/**
 * Base class for homematic devices.
 * 
 * @author alex
 */
public abstract class AbstractDevice implements Device {
	
	/** The type property. */
	protected static final String PROPERTY_TYPE = "TYPE";
	
	/** The address property. */
	protected static final String PROPERTY_ADDRESS = "ADDRESS";
	
	/** The address of the device. */
	private final String address;
	
	/** The channel to be used. */
	private final CCUChannel channel;
	
	/** The device type. */
	private final Device.Type type;

	/** The name of the device. */
	private final String name;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	type		The device type.
	 * @param 	address		The address of the device.
	 * @param 	channel		The channel.
	 */
	protected AbstractDevice(final Device.Type type, final String name, final String address, final CCUChannel channel) {
		super();
		
		this.address = address;
		this.channel = channel;
		this.type = type;
		this.name = name;
	}
	
	/**
	 * Returns the name of the device.
	 * 
	 * @return	The name of the device.
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	/**
	 * Returns the channel being used.
	 * 
	 * @return	The channel being used.
	 */
	protected final CCUChannel getChannel() {
		return this.channel;
	}
	
	/**
	 * Returns the address.
	 * 
	 * @return	The address.
	 */
	protected final String getAddress() {
		return this.address;
	}
	
	/**
	 * Returns the value of a parameter.
	 * 
	 * @param 	name				The name of the parameter.
	 * @param 	objectClass			The expected object class.
	 * 
	 * @return	The value of the parameter.
	 * 
	 * @throws 	IOException			In case of an IO error.
	 */
	protected final <T> T getParameterValue(final String name, final Class<T> objectClass) throws IOException {
		return this.channel.getValue(this.type, this.address, name, objectClass);
	}
	
	/**
	 * Sets the value of a parameter.
	 * 
	 * @param 	name			The name.
	 * @param 	value			The value.
	 * 
	 * @throws 	IOException
	 */
	protected final <T> void setParameterValue(final String name, final T value) throws IOException {
		this.channel.setValue(this.type, this.address, name, value);
	}
	
	protected final Hashtable<String, Object> getParamSet() throws IOException {
		return this.channel.getParamSet(this.address, "VALUES", this.type);
	}
}
