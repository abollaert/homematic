package be.techniquez.homeautomation.homematic.impl.device;

import be.techniquez.homeautomation.homematic.api.Device;

/**
 * The device type.
 * 
 * @author alex
 */
public enum DeviceType {
	DIMMER("HMW-LC-Dim1L-DR", (xml) -> DimmerImpl.create(xml));
	
	/** The typeName. */
	private final String typeName;
	
	/** The type class. */
	private final DeviceFactory<? extends Device> factory;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	typeName		The type name.
	 * @param	factory			The factory.
	 */
	private DeviceType(final String typeName, final DeviceFactory<? extends Device> factory) {
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
	 * 
	 * @return		The parsed device, <code>null</code> if not supported.
	 */
	public final Device parse(final be.techniquez.homeautomation.homematic.xmlapi.devicelist.Device xmlDevice) {
		if (xmlDevice.getDeviceType().equals(this.typeName)) {
			return this.factory.createDevice(xmlDevice);
		}
		
		return null;
	}
}