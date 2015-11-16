package be.techniquez.homeautomation.homematic.impl.device;

import be.techniquez.homeautomation.homematic.api.Dimmer;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;
import be.techniquez.homeautomation.homematic.xmlapi.devicelist.Device;

/**
 * Dimmer class.
 * 
 * @author alex
 */
public final class DimmerImpl extends AbstractDevice implements Dimmer {
	
	/** Indicates the channel is a receiver. */
	private static final String DIRECTION_RECEIVER = "RECEIVER";
	
	/**
	 * Creates a new dimmer based on the given device definition.
	 * 
	 * @param 		xmlDevice		The XML definition.
	 * @param		ccuChannel		The channel.
	 * 
	 * @return		The {@link Dimmer} device.
	 */
	public static final Dimmer create(final Device xmlDevice, final CCUChannel ccuChannel) {
		final String deviceName = xmlDevice.getName();
		final String serial = xmlDevice.getAddress();
		final int outputChannelId = xmlDevice.getChannel().stream()
														  .filter((channel) -> channel.getDirection().equals(DIRECTION_RECEIVER))
														  .findFirst()
														  .get()
														  .getIseId().intValue();
		
		return new DimmerImpl(ccuChannel, deviceName, serial, outputChannelId);
	}

	/**
	 * Create a new instance.
	 * 
	 * @param 	name	The name.
	 * @param	channel	The CCU channel.
	 */
	public DimmerImpl(final CCUChannel channel, final String name, final String serialNumber, final int outputChannelId) {
		super(channel, DatapointType.LEVEL, 3, outputChannelId, name, serialNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setDimmerValue(final int percentage) {
		if (percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException("Percentage should be between 0 and 100, you specified [" + percentage + "]");
		}
		
		final double percentageFraction = ((double)percentage) / 100;
		
		this.setState(String.valueOf(percentageFraction));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getDimmerValue() {
		final double percentageFraction = this.getState((string) -> Double.parseDouble(string));
		
		final int percentage = (int)(percentageFraction * 100.0);
		
		if (percentage < 0) {
			return 0;
		} else if (percentage > 100) {
			return 100;
		} else {
			return percentage;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void attributeChanged(final String name, final String value) {
		System.out.println("Dimmer : [" + this.toString() + "] : attribute [" + name + "] changed to [" + value + "]");
	}
}
