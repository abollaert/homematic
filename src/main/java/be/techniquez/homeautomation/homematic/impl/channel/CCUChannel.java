package be.techniquez.homeautomation.homematic.impl.channel;

import java.io.IOException;
import java.util.Hashtable;

import be.techniquez.homeautomation.homematic.api.Device;

/**
 * Channel to the CCU.
 * 
 * @author alex
 */
public interface CCUChannel {

	/**
	 * Connect to the homematic system.
	 * 
	 * @throws 	IOException		If an IO error occurs.
	 */
	void connect() throws IOException;
	
	/**
	 * Gets a particular value for a device.
	 * 
	 * @param 	address				The address of the device.
	 * @param 	parameterName		The name of the parameter.
	 * @param	deviceType			The type of device.
	 * @param	expectedClass		The expected class.
	 * 
	 * @return	The parameter value.
	 */
	<T> T getValue(final Device.Type deviceType, final String address, final String parameterName, final Class<T> expectedClass) throws IOException;
	
	/**
	 * Sets the value of a parameter.
	 * 
	 * @param 	address				The address of the device.
	 * @param 	parameterName		The name of the parameter to set.
	 * @param 	deviceType			The type of device.
	 * @param 	value				The value to set.
	 * 
	 * @throws 	IOException
	 */
	<T> void setValue(final Device.Type deviceType, final String address, final String parameterName, final T value) throws IOException;
	
	/**
	 * Returns the parameter set for a particular device.
	 * 
	 * @param 	address				The address of the device.
	 * @param 	paramSetKey			The param set key.
	 * @param 	deviceType			The type of device.
	 * 
	 * @return	The parameter set.
	 * 
	 * @throws 	IOException			If an IO error occurs.
	 */
	Hashtable<String, Object> getParamSet(final String address, final String paramSetKey, final Device.Type deviceType) throws IOException;
}