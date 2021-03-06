package be.techniquez.homeautomation.homematic.impl.device;

import java.util.List;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;

/**
 * Defines a factory for devices.
 * 
 * @author alex
 */
@FunctionalInterface
public interface DeviceFactory {
	
	/**
	 * Creates a device based on the definition.
	 * 
	 * @param 	deviceDefinition		The device definition.
	 * @param	ccuChannel				The CCU Channel.
	 * 
	 * @return	The device.
	 */
	List<Device> createDevices(final be.techniquez.homeautomation.homematic.xmlapi.devicelist.Device deviceDefinition, final CCUChannel ccuChannel);
}
