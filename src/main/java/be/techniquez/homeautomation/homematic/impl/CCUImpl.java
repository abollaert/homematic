package be.techniquez.homeautomation.homematic.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import be.techniquez.homeautomation.homematic.api.CCU;
import be.techniquez.homeautomation.homematic.api.Dimmer;
import be.techniquez.homeautomation.homematic.impl.channel.CCUChannel;
import be.techniquez.homeautomation.homematic.impl.channel.CCUChannelImpl;
import be.techniquez.homeautomation.homematic.impl.device.DimmerImpl;

/**
 * CCU implementation, uses XMLRPC.
 * 
 * @author alex
 */
public final class CCUImpl implements CCU {

	/** The channel. */
	private final CCUChannel channel;
	
	/** The dimmers. */
	private final Map<Dimmer.Instance, Dimmer> dimmers = new HashMap<>();
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	address		The CCU host.
	 */
	public CCUImpl(final String address) {
		this.channel = new CCUChannelImpl(address);
		
		this.dimmers.put(Dimmer.Instance.BUREAU, new DimmerImpl("Dimmer bureau", "JEQ0545914:3", this.channel));
		this.dimmers.put(Dimmer.Instance.EETTAFEL, new DimmerImpl("Dimmer eettafel", "JEQ0546057:3", this.channel));
		this.dimmers.put(Dimmer.Instance.KEUKENTAFEL, new DimmerImpl("Dimmer keukentafel", "JEQ0714786:3", this.channel));
		this.dimmers.put(Dimmer.Instance.LIVINGRAAM, new DimmerImpl("Dimmer livingraam", "JEQ0714732:3", this.channel));
		this.dimmers.put(Dimmer.Instance.ZETEL, new DimmerImpl("Dimmer zetel", "JEQ0545894:3", this.channel));
		this.dimmers.put(Dimmer.Instance.ZOLDER, new DimmerImpl("Dimmer zolder", "JEQ0714817:3", this.channel));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void connect() throws IOException {
		this.channel.connect();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Dimmer getDimmer(Dimmer.Instance dimmer) {
		return this.dimmers.get(dimmer);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Collection<Dimmer> getDimmers() {
		return this.dimmers.values();
	}
	
	/**
	 * Test code.
	 * 
	 * @param 	args			The command line args.
	 * 
	 * @throws 	Exception
	 */
	public static void main(String[] args) throws Exception {
		final CCU ccu = new CCUImpl("homematic");
		ccu.connect();
		
		final Dimmer d = ccu.getDimmer(Dimmer.Instance.BUREAU);
		d.setPercentage(50);
	}
}
