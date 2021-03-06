package be.techniquez.homeautomation.homematic.impl.device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Switch;
import be.techniquez.homeautomation.homematic.api.SwitchListener;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;
import be.techniquez.homeautomation.homematic.xmlapi.devicelist.Channel;

/**
 * Implementation of the {@link Switch} interface.
 * 
 * @author alex
 */
public final class SwitchImpl extends AbstractDevice implements Switch {
	
	/** The state. */
	private volatile boolean state;
	
	/** The listeners. */
	private final Set<SwitchListener> listeners = new HashSet<>();

	/**
	 * Create a new instance.
	 * 
	 * @param 	channel				The CCU channel.
	 * @param 	channelNumber		The channel number.
	 * @param 	iseId				The ISE ID.
	 * @param 	name				The name.
	 * @param 	serialNumber		The serial number.
	 */
	private SwitchImpl(final CCUChannel channel, final int channelNumber, final int iseId, final String name, final String serialNumber) {
		super(channel, DatapointType.STATE, channelNumber, iseId, name, serialNumber);
		
		this.state = this.getState(Boolean::parseBoolean);
	}
	
	/**
	 * Creates a list of switches.
	 * 
	 * @param 	channel			The CCU channel.
	 * @param 	xmlDevice		The XML device.
	 * @return
	 */
	public static final List<Device> create(final CCUChannel channel, final be.techniquez.homeautomation.homematic.xmlapi.devicelist.Device xmlDevice) {
		final List<Device> devices = new ArrayList<>();
		
		for (int i = 0; i < xmlDevice.getChannel().size(); i++) {
			final Channel currentChannel = xmlDevice.getChannel().get(i);
			
			if (currentChannel.getName().startsWith("O_")) {
				devices.add(new SwitchImpl(channel, i + 1, currentChannel.getIseId().intValue(), currentChannel.getName(), xmlDevice.getAddress()));
			}
		}
		
		return devices;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isOn() {
		return this.state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setOn(boolean on) {
		this.setState(String.valueOf(on));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void attributeChanged(final String name, final String value) {
		if (name.equals(DatapointType.STATE.getTypeName())) {
			this.state = Boolean.parseBoolean(value);
			
			this.listeners.stream()
						  .forEach(listener -> listener.stateChanged(this.state));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addListener(final SwitchListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeListener(final SwitchListener listener) {
		this.listeners.remove(listener);
	}
}
