package be.techniquez.homeautomation.homematic.impl.device;

import be.techniquez.homeautomation.homematic.api.Dimmer;
import be.techniquez.homeautomation.homematic.xmlapi.devicelist.Device;

/**
 * Dimmer class.
 * 
 * @author alex
 */
public final class DimmerImpl extends AbstractDevice implements Dimmer {
	
	/** Indicates the channel is a receiver. */
	private static final String DIRECTION_RECEIVER = "RECEIVER";
	
	/** The output channel ID. */
	private final int outputChannelId;
	
	/**
	 * Creates a new dimmer based on the given device definition.
	 * 
	 * @param 		xmlDevice		The XML definition.
	 * 
	 * @return		The {@link Dimmer} device.
	 */
	public static final Dimmer create(final Device xmlDevice) {
		final String deviceName = xmlDevice.getName();
		final String serial = xmlDevice.getAddress();
		final int outputChannelId = xmlDevice.getChannel().stream()
														  .filter((channel) -> channel.getDirection().equals(DIRECTION_RECEIVER))
														  .findFirst()
														  .get()
														  .getIseId().intValue();
		
		return new DimmerImpl(deviceName, serial, outputChannelId);
	}

	/**
	 * Create a new instance.
	 * 
	 * @param 	name	The name.
	 */
	public DimmerImpl(final String name, final String serialNumber, final int outputChannelId) {
		super(name, serialNumber);
		
		this.outputChannelId = outputChannelId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setState(final int percentage) {
		if (percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException("Percentage should be between 0 and 100, you specified [" + percentage + "]");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder("Dimmer ");
		
		builder.append("name : [").append(this.getName()).append("], ");
		builder.append("serial number [").append(this.getSerialNumber()).append("], ");
		builder.append("output channel ID [").append(this.outputChannelId).append("]");
		
		return builder.toString();
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getState() {
		// TODO Auto-generated method stub
		return 0;
	}
}
