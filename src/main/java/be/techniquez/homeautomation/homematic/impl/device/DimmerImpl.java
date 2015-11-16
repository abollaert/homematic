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

	/** The state. */
	private volatile int state;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	name	The name.
	 * @param	channel	The CCU channel.
	 */
	public DimmerImpl(final CCUChannel channel, final String name, final String serialNumber, final int outputChannelId) {
		super(channel, DatapointType.LEVEL, 3, outputChannelId, name, serialNumber);
		
		this.state = this.getStateFromCCU();
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
		return this.state;
	}
	
	/**
	 * Returns the state from the CCU.
	 * 
	 * @return	The state returned from the CCU.
	 */
	private final int getStateFromCCU() {
		return convert(this.getState((string) -> Double.parseDouble(string)));
	}
	
	/**
	 * Converts the given value to a percentage.
	 * 
	 * @param 		value		The value.
	 * 
	 * @return		The percentage.
	 */
	private static final int convert(final double value) {
		final int percentage = (int)(value * 100.0);
		
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
		if (name.equals(DatapointType.LEVEL.getTypeName())) {
			this.state = convert(Double.parseDouble(value));
		}
	}
}
