package be.techniquez.homeautomation.homematic.impl.channel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;
import be.techniquez.homeautomation.homematic.impl.CCUEventLoop;
import be.techniquez.homeautomation.homematic.impl.channel.XMLAPIURLBuilder.Endpoint;
import be.techniquez.homeautomation.homematic.impl.device.DeviceType;
import be.techniquez.homeautomation.homematic.impl.event.CCUEventingImpl;
import be.techniquez.homeautomation.homematic.xmlapi.devicelist.DeviceList;
import be.techniquez.homeautomation.homematic.xmlapi.state.Result;
import be.techniquez.homeautomation.homematic.xmlapi.state.State;

/**
 * CCU channel implementation.
 * 
 * @author alex
 */
public final class CCUChannelImpl implements CCUChannel {

	/** Logger instance. */
	private static final Logger logger = Logger.getLogger(CCUChannelImpl.class.getName());

	/** The channel ID parameter. */
	private static final String PARAMETER_CHANNEL_ID = "channel_id";

	/** The ISE ID parameter. */
	private static final String PARAMETER_ISE_ID = "ise_id";

	/** The new value parameter. */
	private static final String PARAMETER_NEW_VALUE = "new_value";
	
	/** The base URL. */
	private static final String BASE = "/addons/xmlapi/";

	/** The default port. */
	private static final int DEFAULT_PORT = 80;

	/** The event port. */
	private static final int DEFAULT_EVENT_PORT = 2000;

	/** The host name of the CCU. */
	private final String urlBase;

	/** The eventing. */
	private CCUEventLoop eventing;

	/**
	 * Create a new instance.
	 * 
	 * @param 	hostname	The hostname.
	 * @param 	port		The port to send requests on.
	 * @param	eventPort	The eventing port.
	 */
	public CCUChannelImpl(final String hostname, final int port, final int eventPort) {
		this.urlBase = new StringBuilder("http://").append(hostname)
												   .append(":")
												   .append(port)
												   .append(BASE)
												   .toString();
		
		this.eventing = new CCUEventingImpl(hostname, eventPort);
	}

	/**
	 * Create a new instance.
	 * 
	 * @param hostname
	 *            The hostname.
	 */
	public CCUChannelImpl(final String hostname) {
		this(hostname, DEFAULT_PORT, DEFAULT_EVENT_PORT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Device> getDevices() throws IOException {
		final URL url = XMLAPIURLBuilder.withBaseURL(this.urlBase)
										.endpoint(Endpoint.DEVICELIST)
										.build();

		return doRequest(url, DeviceList.class).getDevice()
											   .stream()
											   .filter(xml -> DeviceType.forName(xml.getDeviceType()) != null)
											   .flatMap(xml -> DeviceType.forName(xml.getDeviceType()).parse(xml, this).stream())
											   .collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getBaseURL() {
		return this.urlBase;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final State getState(final int channelId) throws IOException {
		final URL url = XMLAPIURLBuilder.withBaseURL(this.urlBase)
										.endpoint(Endpoint.GET_STATE)
										.parameter(PARAMETER_CHANNEL_ID, channelId)
										.build();

		return doRequest(url, State.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setState(int channelId, String newValue) throws IOException {
		final URL url = XMLAPIURLBuilder.withBaseURL(this.urlBase)
										.endpoint(Endpoint.STATECHANGE)
										.parameter(PARAMETER_ISE_ID, channelId)
										.parameter(PARAMETER_NEW_VALUE, newValue)
										.build();

		final Result result = doRequest(url, Result.class);

		if (result.getChanged() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "State change failed : change element was null.");
			}
		}
	}

	/**
	 * Parses the response as JAXB.
	 * 
	 * @param url			The URL.
	 * @param responseType	The type of the response.
	 * 
	 * @return The parsed data.
	 */
	private static final <T> T doRequest(final URL url, final Class<T> responseType) {
		try (final InputStream stream = url.openStream()) {
			final JAXBContext jaxbContext = JAXBContext.newInstance(responseType.getPackage().getName());
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			return responseType.cast(unmarshaller.unmarshal(stream));
		} catch (JAXBException | IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Error while parsing : [" + e.getMessage() + "]", e);
			}

			throw new IllegalStateException("Error while parsing : [" + e.getMessage() + "]", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void connect() throws IOException {
		this.eventing.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void disconnect() throws IOException {
		this.eventing.stop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addEventHandler(final CCUChannelEventHandler handler) {
		this.eventing.addEventHandler(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeEventHandler(final CCUChannelEventHandler handler) {
		this.eventing.removeEventHandler(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isConnected() {
		return this.eventing.isStarted();
	}
}
