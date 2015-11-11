package be.techniquez.homeautomation.homematic.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.techniquez.homeautomation.homematic.api.CCU;
import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Dimmer;
import be.techniquez.homeautomation.homematic.api.Switch;
import be.techniquez.homeautomation.homematic.impl.channel.CCUChannelImpl;

/**
 * CCU implementation, uses XMLRPC.
 * 
 * @author alex
 */
public final class CCUImpl implements CCU {
	
	/** Logger instance. */
	private static final Logger logger = Logger.getLogger(CCUImpl.class.getName());

	/** The channel. */
	private final CCUChannel channel;
	
	/** The dimmers. */
	private final List<Dimmer> dimmers = new ArrayList<>();
	
	/** The switches. */
	private final List<Switch> switches = new ArrayList<>();
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	address		The CCU host.
	 */
	public CCUImpl(final String address) {
		this.channel = new CCUChannelImpl(address);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void connect() throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU [" + this.channel.getHostname() + ":" + this.channel.getPort() + "] : loading devices.");
		}
		
		final List<Device> devices = this.channel.getDevices();
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Device list received, processing.");
		}
		
		devices.stream()
			   .forEach((device) -> {
				   if (device instanceof Dimmer) {
					   this.dimmers.add((Dimmer)device);
				   } else if (device instanceof Switch) {
					   this.switches.add((Switch)device);
				   }
			   });
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Found : [" + this.dimmers.size() + "] dimmers, [" + this.switches.size() + "] switches.");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Dimmer getDimmer(final String name) {
		return this.dimmers.stream()
						   .filter((dimmer) -> dimmer.getName() != null && dimmer.getName().equals(name))
						   .findFirst()
						   .orElse(null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Dimmer> getDimmers() {
		return Collections.unmodifiableList(this.dimmers);
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
		

		ccu.getDimmers().stream()
						.forEach((dimmer) -> { 
							final int value = dimmer.getDimmerValue();
							
							dimmer.setDimmerValue(80);
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
							
							dimmer.setDimmerValue(value);
						});
		
		ccu.getSwitches().stream()
		.forEach((sw) -> { 
			final boolean on = sw.isOn();
			
			sw.setOn(true);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			sw.setOn(on);
		});
		
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Collection<Switch> getSwitches() {
		return this.switches;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Switch getSwitch(String name) {
		return this.switches.stream()
				   .filter((switchDevice) -> switchDevice.getName() != null && switchDevice.getName().equals(name))
				   .findFirst()
				   .orElse(null);
	}
}
