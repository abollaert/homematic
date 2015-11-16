package be.techniquez.homeautomation.homematic.impl.event;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;

import be.techniquez.homeautomation.homematic.impl.CCUEventing;

/**
 * Eventing implementation.
 * 
 * @author alex
 */
public final class CCUEventingImpl implements CCUEventing {
	
	/** Logger. */
	private static final Logger logger = Logger.getLogger(CCUEventingImpl.class.getName());
	
	/** The port we will be listening on for events. */
	private static final int EVENT_PORT = 20001;
	
	/**
	 * The URL of the server.
	 */
	private final String url;
	
	/** The event client socket. */
	private XmlRpcClient eventClient;
	
	/** The event server. */
	private WebServer eventServer;
	
	/** The server URL. */
	private String serverURL;

	/**
	 * Create a new instance.
	 * 
	 * @param 	hostname		The host name.
	 * @param 	port			The port.
	 */
	public CCUEventingImpl(final String hostname, final int port) {
		this.url = new StringBuilder("http://").append(hostname)
											   .append(":")
											   .append(port)
											   .append("/")
											   .toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start() throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU eventing : URL [" + this.url + "] : starting.");
		}
		
		if (this.eventServer == null) {
			this.initXmlRpcServer(this.getPublicInetAddress());
		}
		
		if (this.eventClient == null) {
			this.initXmlRpcClient();
		}
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU eventing : URL [" + this.url + "] : started.");
		}
	}
	
	/**
	 * Initializes the XML RPC client (to call init). Must happen after {@link #initXmlRpcServer()}.
	 */
	private final void initXmlRpcClient() throws IOException {
		this.eventClient = new XmlRpcClient(this.url);
		
		final Vector<String> parameters = new Vector<>();
		parameters.add(this.serverURL);
		parameters.add("HomeMaticDriver");
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU Eventing RPC client : calling init, using parameters [ " + this.serverURL + "], [HomeMaticDriver]");
		}
		
		try {
			final XmlRpcClientRequest initRequest = new XmlRpcRequest("init", parameters);
			final Object response = this.eventClient.execute(initRequest);
			
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "CCU Eventing RPC client : init called, response [" + response + "]");
			}
		} catch (XmlRpcException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Error initializing callbacks : [" + e.getMessage() + "]", e);
			}
			
			throw new IllegalStateException("Error initializing callbacks : [" + e.getMessage() + "]", e);
		}
	}

	/**
	 * Initializes the XML RPC server. Must be done before {@link #initXmlRpcClient()}.
	 */
	private final void initXmlRpcServer(final InetAddress address) {
		this.serverURL = new StringBuilder("http://").append(address.getHostAddress())
													 .append(":")
													 .append(EVENT_PORT)
													 .toString();
		
		this.eventServer = new WebServer(EVENT_PORT);
		
		final XmlRpcHandler eventHandler = ((method, parameters) -> {
			System.out.println("Event called : method : [" + method + "], parameters [" + parameters + "]");
			
			return null;
		});
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU Eventing RPC server : starting.");
		}
		
		this.eventServer.addHandler("$default", eventHandler);
		this.eventServer.start();
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU Eventing RPC server : started.");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void stop() throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU eventing : URL [" + this.url + "] : stopping.");
		}
		
		final Vector<String> parameters = new Vector<>();
		parameters.add(this.serverURL);
		parameters.add("");
		
		if (this.eventClient != null) {
			try {
				final XmlRpcClientRequest initRequest = new XmlRpcRequest("init", parameters);
				final Object response = this.eventClient.execute(initRequest);
				
				if (logger.isLoggable(Level.INFO)) {
					logger.log(Level.INFO, "CCU Eventing RPC client : init (disconnect) called, response [" + response + "]");
				}
			} catch (XmlRpcException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.log(Level.WARNING, "Error disconnecting callbacks : [" + e.getMessage() + "]", e);
				}
				
				throw new IllegalStateException("Error disconnecting callbacks : [" + e.getMessage() + "]", e);
			}
		}
		
		if (this.eventServer != null) {
			this.eventServer.shutdown();
		}
		
		this.eventClient = null;
		this.eventServer = null;
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU eventing : URL [" + this.url + "] : stopped.");
		}
	}
	
	/**
	 * Returns the public IP address.
	 * 
	 * @return	Thye public IP address.
	 */
	private final InetAddress getPublicInetAddress() {
		try {
			// FIXME : make it configurable.
			return InetAddress.getByName("192.168.100.12");
		} catch (UnknownHostException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Could not determine public IP address : [" + e.getMessage() + "]", e);
			}
			
			throw new IllegalStateException("Could not determine public IP address : [" + e.getMessage() + "]", e);
		}
	}
}
