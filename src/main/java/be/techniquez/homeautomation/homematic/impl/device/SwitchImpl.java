package be.techniquez.homeautomation.homematic.impl.device;

import java.util.ArrayList;
import java.util.List;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Switch;
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
				devices.add(new SwitchImpl(channel, (i + 1), currentChannel.getIseId().intValue(), currentChannel.getName(), xmlDevice.getAddress()));
			}
		}
		
		return devices;
	}

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
		
		this.state = this.getState((value) -> Boolean.parseBoolean(value));
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
		System.out.println("Switch : [" + this.toString() + "] : attribute [" + name + "] changed to [" + value + "]");
	}
}
