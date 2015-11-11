package be.techniquez.homeautomation.homematic.impl.channel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;
import be.techniquez.homeautomation.homematic.impl.channel.XMLAPIURLBuilder.Endpoint;
import be.techniquez.homeautomation.homematic.impl.device.DeviceType;
import be.techniquez.homeautomation.homematic.xmlapi.devicelist.DeviceList;

public final class CCUChannelImpl implements CCUChannel {
	
	private static final Logger logger = Logger.getLogger(CCUChannelImpl.class.getName());

	/** The package name. */
	private static final String PACKAGE_DEVICELIST = "be.techniquez.homeautomation.homematic.xmlapi.devicelist";
	
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Device> getDevices() throws IOException {
		final URL url = XMLAPIURLBuilder.forHost(this.hostname, this.port)
				 						.endpoint(Endpoint.DEVICELIST)
				 						.build();
		try {
			final JAXBContext context = JAXBContext.newInstance(PACKAGE_DEVICELIST);
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			
			try (final InputStream stream = url.openStream()) {
				final DeviceList xmlDevices = (DeviceList)unmarshaller.unmarshal(url.openStream());
				
				if (xmlDevices != null) {
					return xmlDevices.getDevice().stream()
										  		 .filter(xml -> DeviceType.forName(xml.getDeviceType()) != null)
										  		 .map(xml -> DeviceType.forName(xml.getDeviceType()).parse(xml))
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getHostname() {
		return this.hostname;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPort() {
		return this.port;
	}
}
