package be.techniquez.homeautomation.homematic.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.techniquez.homeautomation.homematic.api.CCU;
import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Dimmer;
import be.techniquez.homeautomation.homematic.api.Switch;

/**
 * CCU implementation, uses XMLRPC.
 * 
 * @author alex
 */
public final class CCUImpl implements CCU {
	
	/** Logger instance. */
	private static final Logger logger = Logger.getLogger(CCUImpl.class.getName());

	/** The channel. */
	private CCUChannel channel;
	
	/** The dimmers. */
	private final List<Dimmer> dimmers = new ArrayList<>();
	
	/** The switches. */
	private final List<Switch> switches = new ArrayList<>();
	
	/** Indicates whether the CCU is connected. */
	private volatile boolean connected;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	channel		The CCU channel.
	 */
	public CCUImpl(final CCUChannel channel) {
		this.channel = channel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized final void connect() throws IOException {
		if (!this.isConnected()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "CCU [" + this.channel.getBaseURL() + "] : loading devices.");
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
			
			this.connected = true;
			
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "Found : [" + this.dimmers.size() + "] dimmers, [" + this.switches.size() + "] switches.");
			}
		} else {
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "Tried to connect when already connected.");
			}
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
	public final List<Switch> getSwitches() {
		return Collections.unmodifiableList(this.switches);
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
	public synchronized final void disconnect() throws IOException {
		if (this.isConnected()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "CCU [" + this.channel.getBaseURL() + "] : disconnecting.");
			}
			
			this.channel.disconnect();
			this.connected = false;
			
			this.dimmers.clear();
			this.switches.clear();
			
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "CCU [" + this.channel.getBaseURL() + "] : disconnected.");
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Tried to disconnect while not connected.");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isConnected() {
		return this.connected;
	}
}
