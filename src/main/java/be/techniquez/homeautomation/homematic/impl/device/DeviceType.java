package be.techniquez.homeautomation.homematic.impl.device;

import java.util.Arrays;
import java.util.List;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;

/**
 * The device type.
 * 
 * @author alex
 */
public enum DeviceType {
	DIMMER("HMW-LC-Dim1L-DR", (xml, channel) -> {
		return Arrays.asList(new Device[] { DimmerImpl.create(xml, channel)});
	}),
	
	SWITCH("HMW-IO-12-Sw7-DR", (xml, channel) -> {
		return SwitchImpl.create(channel, xml);
	});
	
	/** The typeName. */
	private final String typeName;
	
	/** The type class. */
	private final DeviceFactory factory;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	typeName		The type name.
	 * @param	factory			The factory.
	 */
	private DeviceType(final String typeName, final DeviceFactory factory) {
		this.typeName = typeName;
		this.factory = factory;
	}
	
	/**
	 * Returns the type with the given name. Returns <code>null</code> if none found.
	 * 
	 * @param 		typeName		The name of the type.
	 * 
	 * @return		The matching type, <code>null</code> if none found.
	 */
	public static final DeviceType forName(final String typeName) {
		for (final DeviceType type : DeviceType.values()) {
			if (type.typeName.equals(typeName)) {
				return type;
			}
		}
		
		return null;
	}
	
	/**
	 * Parses the device if possible, returns <code>null</code> if this type is not supported.
	 * 
	 * @param 		xmlDevice		The XML device.
	 * @param		channel			The channel.
	 * 
	 * @return		The parsed device, <code>null</code> if not supported.
	 */
	public final List<Device> parse(final be.techniquez.homeautomation.homematic.xmlapi.devicelist.Device xmlDevice, final CCUChannel channel) {
		if (xmlDevice.getDeviceType().equals(this.typeName)) {
			return this.factory.createDevices(xmlDevice, channel);
		}
		
		return null;
	}
}