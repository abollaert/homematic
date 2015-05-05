package be.techniquez.homeautomation.homematic.impl.channel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;






import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Device.Type;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;
import be.techniquez.homeautomation.homematic.impl.channel.XMLAPIURLBuilder.Endpoint;
import be.techniquez.homeautomation.homematic.impl.device.DimmerImpl;
import be.techniquez.homeautomation.homematic.xmlapi.DeviceList;

public final class CCUChannelImpl implements CCUChannel {
	
	private static final Logger logger = Logger.getLogger(CCUChannelImpl.class.getName());

	/** The package name. */
	private static final String PACKAGE = "be.techniquez.homeautomation.homematic.xmlapi";
	
	/** The default port. */
	private static final int DEFAULT_PORT = 80;
	
	/** The host name of the CCU. */
	private final String hostname;
	
	/** The port the API is running on. */
	private final int port;
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	hostname		The hostname.
	 * @param 	port			The port.
	 */
	public CCUChannelImpl(final String hostname, final int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	hostname		The hostname.
	 */
	public CCUChannelImpl(final String hostname) {
		this(hostname, DEFAULT_PORT);
	}

	@Override
	public final List<Device> getDevices() throws IOException {
		final URL url = XMLAPIURLBuilder.forHost(this.hostname, this.port)
				 						.endpoint(Endpoint.DEVICELIST)
				 						.build();
		try {
			final JAXBContext context = JAXBContext.newInstance(PACKAGE);
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			
			try (final InputStream stream = url.openStream()) {
				final DeviceList xmlDevices = (DeviceList)unmarshaller.unmarshal(url.openStream());
				
				if (xmlDevices != null) {
					return xmlDevices.getDevice().stream()
										  		 .filter((xmlDevice) -> Type.forName(xmlDevice.getDeviceType()) != null)
										  		 .map((xmlDevice) -> {
										  			 final Type t = Type.forName(xmlDevice.getDeviceType());
											  
										  			 switch (t) {
										  			 	case DIMMER: {
										  			 		return new DimmerImpl(xmlDevice.getName());
										  			 	}
										  			 }
										  			 
										  			 return null;
										  		 })
										  		 .collect(Collectors.toList());
				}
			}
		} catch (JAXBException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "JAXB error : [" + e.getMessage() + "]", e); 
			}
			
			throw new IllegalStateException("JAXB error : [" + e.getMessage() + "]", e);
		}
		
		return Collections.emptyList();
	}

	@Override
	public String getHostname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
