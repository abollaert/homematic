package be.techniquez.homeautomation.homematic.impl.device;

import be.techniquez.homeautomation.homematic.api.Device;

/**
 * Defines a factory for devices.
 * 
 * @author alex
 */
public interface DeviceFactory<T extends Device> {
	
	/**
	 * Creates a device based on the definition.
	 * 
	 * @param 	deviceDefinition		The device definition.
	 * 
	 * @return	The device.
	 */
	T createDevice(final be.techniquez.homeautomation.homematic.xmlapi.devicelist.Device deviceDefinition);
}
