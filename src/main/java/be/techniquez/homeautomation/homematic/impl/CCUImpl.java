package be.techniquez.homeautomation.homematic.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import be.techniquez.homeautomation.homematic.api.CCU;
import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Dimmer;
import be.techniquez.homeautomation.homematic.api.Switch;

/**
 * CCU implementation, uses XMLRPC.
 * 
 * @author alex
 */
@Component
@Service
@Property(name = "service.pid", value = "CCU")
public final class CCUImpl implements CCU {
	
	/** Logger instance. */
	private static final Logger logger = Logger.getLogger(CCUImpl.class.getName());

	/** The channel. */
	private CCUChannel channel;
	
	/** The dimmers. */
	private final List<Dimmer> dimmers = new ArrayList<>();
	
	/** The switches. */
	private final List<Switch> switches = new ArrayList<>();
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	address		The CCU host.
	 */
	public CCUImpl() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void connect() throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU [" + this.channel.getHostname() + ":" + this.channel.getPort() + "] : loading devices.");
		}
		
		this.channel.connect();
		
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void disconnect() throws IOException {
		this.channel.disconnect();
	}
}
