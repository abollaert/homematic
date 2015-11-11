package be.techniquez.homeautomation.homematic.impl.channel;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.net.SocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.impl.CCUChannel;
import be.techniquez.homeautomation.homematic.impl.channel.XMLAPIURLBuilder.Endpoint;
import be.techniquez.homeautomation.homematic.impl.device.DeviceType;
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

	/** The default port. */
	private static final int DEFAULT_PORT = 80;
	
	/** The event port. */
	private static final int DEFAULT_EVENT_PORT = 2000;

	/** The host name of the CCU. */
	private final String hostname;

	/** The port the API is running on. */
	private final int port;
	
	/** The event port. */
	private final int eventPort;
	
	/** The event socket. */
	private Socket eventSocket;
	
	/** The event reader. */
	private final ExecutorService eventReader = Executors.newSingleThreadExecutor((runnable) -> {
		final Thread t = new Thread(runnable, "CCU channel : event reader thread");
		t.setDaemon(true);
		
		return t;
	});

	/** The event reader future. */
	private Future<?> eventReaderFuture;

	/**
	 * Create a new instance.
	 * 
	 * @param hostname
	 *            The hostname.
	 * @param port
	 *            The port.
	 */
	public CCUChannelImpl(final String hostname, final int port, final int eventPort) {
		this.hostname = hostname;
		this.port = port;
		this.eventPort = eventPort;
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
		final URL url = XMLAPIURLBuilder.forHost(this.hostname, this.port).endpoint(Endpoint.DEVICELIST).build();

		return parseJAXB(url, DeviceList.class).getDevice().stream()
				.filter(xml -> DeviceType.forName(xml.getDeviceType()) != null)
				.flatMap(xml -> DeviceType.forName(xml.getDeviceType()).parse(xml, this).stream())
				.collect(Collectors.toList());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final State getState(final int channelId) throws IOException {
		final URL url = XMLAPIURLBuilder.forHost(this.hostname, this.port).endpoint(Endpoint.GET_STATE)
				.parameter(PARAMETER_CHANNEL_ID, channelId).build();

		return parseJAXB(url, State.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setState(int channelId, String newValue) throws IOException {
		final URL url = XMLAPIURLBuilder.forHost(this.hostname, this.port).endpoint(Endpoint.STATECHANGE)
				.parameter(PARAMETER_ISE_ID, channelId).parameter(PARAMETER_NEW_VALUE, newValue).build();

		final Result result = parseJAXB(url, Result.class);
		
		if (result.getChanged() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "State change failed : change element was null.");
			}
		}
	}

	/**
	 * Parses the response as JAXB.
	 * 
	 * @param url
	 *            The URL.
	 * @param pkg
	 *            The package of the JAXB generated code.
	 * 
	 * @return The parsed data.
	 */
	private static final <T> T parseJAXB(final URL url, final Class<T> clazz) {
		try (final InputStream stream = url.openStream()) {
			final JAXBContext jaxbContext = JAXBContext.newInstance(clazz.getPackage().getName());
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			return clazz.cast(unmarshaller.unmarshal(stream));
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
		final SocketFactory socketFactory = SocketFactory.getDefault();
		this.eventSocket = socketFactory.createSocket(this.hostname, this.eventPort);
		
		this.eventReaderFuture = this.eventReader.submit(() -> {
			try (final InputStream stream = this.eventSocket.getInputStream()) {
				final byte[] buffer = new byte[1024];
				int bytesRead = 0;
				
				while (!Thread.currentThread().isInterrupted() && bytesRead != -1) {
					bytesRead = stream.read(buffer);
					
					if (bytesRead != -1) {
						System.out.println(new String(buffer));
					}
				}
			} catch (IOException e) {
				if (!(e.getMessage().equals("Socket closed"))) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.log(Level.WARNING, "IO error while closing event input stream : [" + e.getMessage() + "]", e);
					}
				}
			}
		});
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void disconnect() throws IOException {
		if (this.eventSocket != null) {
			this.eventReaderFuture.cancel(true);
			
			if (!this.eventSocket.isClosed()) {
				this.eventSocket.close();
			}
			
			this.eventReaderFuture = null;
			this.eventSocket = null;
		}
	}
}
