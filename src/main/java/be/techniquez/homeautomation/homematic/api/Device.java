package be.techniquez.homeautomation.homematic.api;

/**
 * Device.
 * 
 * @author alex
 */
public interface Device {
	
	/**
	 * The device type.
	 * 
	 * @author alex
	 */
	public enum Type {
		DIMMER("HMW-LC-Dim1L-DR");
		
		/** The typeName. */
		private String typeName;
		
		/**
		 * Create a new instance.
		 * 
		 * @param typeName	The type name.
		 */
		private Type(final String typeName) {
			this.typeName = typeName;
		}
		
		/**
		 * Returns the type with the given name. Returns <code>null</code> if none found.
		 * 
		 * @param 		typeName		The name of the type.
		 * 
		 * @return		The matching type, <code>null</code> if none found.
		 */
		public static final Type forName(final String typeName) {
			for (final Type type : Type.values()) {
				if (type.typeName.equals(typeName)) {
					return type;
				}
			}
			
			return null;
		}
	}

	/**
	 * Returns the name of the device.
	 * 
	 * @return	The name of the device.
	 */
	String getName();
}
