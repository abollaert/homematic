package be.techniquez.homeautomation.homematic.impl.device;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Dimmer;
import be.techniquez.homeautomation.homematic.impl.channel.CCUChannel;

/**
 * Dimmer implementation.
 * 
 * @author alex
 */
public final class DimmerImpl extends AbstractDevice implements Dimmer {
	
	/** Logger. */
	private static final Logger logger = Logger.getLogger(DimmerImpl.class.getName());
	
	/** The level parameter. */
	private static final String PARAMETER_LEVEL = "LEVEL";

	/**
	 * Create a new instance.
	 * 
	 * @param 	address		The address.
	 * @param 	channel		The channel.
	 */
	public DimmerImpl(final String name, final String address, final CCUChannel channel) {
		super(Device.Type.WIRED, name, address, channel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getPercentage() throws IOException {
		try {
			System.out.println(this.getChannel().getParamSet(this.getAddress(), "VALUES", Type.WIRED));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.getParameterValue(PARAMETER_LEVEL, Double.class) * 100;
	}

	@Override
	public void setInhibited(boolean inhibited) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInhibited() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setPercentage(double percentage) throws IOException {
		if (percentage < 0) {
			percentage = 0;
		}
		
		if (percentage > 100) {
			percentage = 100;
		}
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Setting dimmer [" + this.getName() + "] level to [" + percentage + "]");
		}
		
		this.setParameterValue(PARAMETER_LEVEL, percentage / 100);
	}	
}
