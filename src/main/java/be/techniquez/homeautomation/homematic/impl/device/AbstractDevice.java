package be.techniquez.homeautomation.homematic.impl.device;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;
import be.techniquez.homeautomation.homematic.xmlapi.state.Channel;
import be.techniquez.homeautomation.homematic.xmlapi.state.Datapoint;
import be.techniquez.homeautomation.homematic.xmlapi.state.State;

/**
 * Abstract base class for devices.
 * 
 * @author alex
 */
public abstract class AbstractDevice implements Device {
	
	/** Logger instance. */
	private static final Logger logger = Logger.getLogger(AbstractDevice.class.getName());
	
	/** Can keep this static for now, don't have anything else. */
	private static final String DEVICE_TYPE = "BidCos-Wired";
	
	/** The CCU channel. */
	private final CCUChannel channel;

	/** The device name. */
	private final String name;
	
	/** The serial number. */
	private final String serialNumber;
	
	/** The channel number. */
	private final int channelNumber;
	
	/** The data point id. */
	private final String datapointName;
	
	/** The ISE ID. */
	private final int iseId;
	
	/** The type of channel. */
	private final ChannelType channelType;
	
	/**
	 * Create a new instance.
	 * 
	 * @param	ccuChannel		The channel.
	 * @param	iseId			The ISE (internal) ID.
	 * @param 	name			The name of the device.
	 * @param	serialNumber	The serial number of the device.
	 * @param	channelNumber	The channel number.
	 * @param	channelType		The type of the channel.
	 */
	protected AbstractDevice(final CCUChannel channel, final ChannelType channelType, final int channelNumber, final int iseId, final String name, final String serialNumber) {
		this.channel = Objects.requireNonNull(channel);
		this.name = Objects.requireNonNull(name);
		this.serialNumber = Objects.requireNonNull(serialNumber);
		this.channelNumber = channelNumber;
		this.iseId = iseId;
		this.channelType = Objects.requireNonNull(channelType);
		
		this.datapointName = new StringBuilder().append(DEVICE_TYPE)
												.append(".")
												.append(this.serialNumber)
												.append(":")
												.append(this.channelNumber)
												.append(".")
												.append(this.channelType.getTypeName())
												.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final String getSerialNumber() {
		return this.serialNumber;
	}
	
	/**
	 * Returns the state.
	 * 
	 * @param	converter			The conversion function.
	 * 
	 * @return	The state.
	 * 
	 * @throws	IOException			If an IO error occurs.
	 */
	protected final <T> T getState(final Function<String, T> converter) {
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Getting state for device : [" + this.serialNumber + "], channel [" + this.channelNumber + "], datapoint [" + this.channelType + "]");
		}
		
		try {
			final State state = this.channel.getState(this.iseId);
			
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "State received, processing, using datapoint name [" + this.datapointName + "]");
			}
			
			if (state != null) {
				final Channel channel = state.getDevice().getChannel()
														 .stream()
														 .filter((someChannel) -> (someChannel.getIseId().intValue() == this.iseId))
														 .findFirst()
														 .orElseThrow(() -> new IllegalStateException("Cannot find channel [" + this.iseId + "] in the state XML."));
				
				final Datapoint datapoint = channel.getDatapoint()
												   .stream()
												   .filter((dataPoint) -> dataPoint.getName().equals(this.datapointName))
												   .findFirst()
												   .orElseThrow(() -> new IllegalStateException("Cannot find channel [" + this.iseId + "] in the state XML."));
				
				return converter.apply(datapoint.getValue());
			}
			
			throw new IllegalStateException("No state returned for channel [" + this.iseId + "] !");
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "IO error caught when getting state : [" + e.getMessage() + "]", e);
			}
			
			throw new IllegalStateException("IO error caught when getting state : [" + e.getMessage() + "]", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return new StringBuilder().append("name: ").append("[").append(this.name).append("], ")
								  .append("serial number: ").append("[").append(this.serialNumber).append("], ")
								  .append("ISE ID: ").append("[").append(this.iseId).append("], ")
								  .append("channel number: ").append("[").append(this.channelNumber).append("], ")
								  .append("data point name: ").append("[").append(this.datapointName).append("]")
								  .toString();
	}
}
