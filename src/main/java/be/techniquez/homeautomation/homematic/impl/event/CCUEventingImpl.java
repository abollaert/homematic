package be.techniquez.homeautomation.homematic.impl.event;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;

import be.techniquez.homeautomation.homematic.impl.CCUEventLoop;
import be.techniquez.homeautomation.homematic.impl.CCUChannel.CCUChannelEventHandler;

/**
 * Eventing implementation.
 * 
 * @author alex
 */
public final class CCUEventingImpl implements CCUEventLoop {
	
	/** Logger. */
	private static final Logger logger = Logger.getLogger(CCUEventingImpl.class.getName());
	
	/** The port we will be listening on for events. */
	private static final int EVENT_PORT = 20001;
	
	/** The multicall method. */
	private static final String METHOD_MULTICALL = "system.multicall";
	
	/** The event method. */
	private static final String METHOD_EVENT = "event";
	
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
	
	/** The event handler. */
	private XmlRpcHandler eventHandler;
	
	/** The event handlers. */
	private final Set<CCUChannelEventHandler> eventHandlers;
	
	/** Indicates whether we have started. */
	private volatile boolean started;

	/**
	 * Create a new instance.
	 * 
	 * @param 	hostname		The host name.
	 * @param 	port			The port.
	 */
	public CCUEventingImpl(final String hostname, final int port, final Set<CCUChannelEventHandler> handlers) {
		this.url = new StringBuilder("http://").append(hostname)
											   .append(":")
											   .append(port)
											   .append("/")
											   .toString();
		
		this.eventHandlers = handlers;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized final void start() throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU eventing : URL [" + this.url + "] : starting.");
		}
		
		if (this.eventServer == null) {
			this.initXmlRpcServer(this.getPublicInetAddress());
		}
		
		if (this.eventClient == null) {
			this.initXmlRpcClient();
		}
		
		this.started = true;
		
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
	@SuppressWarnings("unchecked")
	private final void initXmlRpcServer(final InetAddress address) {
		this.serverURL = new StringBuilder("http://").append(address.getHostAddress())
													 .append(":")
													 .append(EVENT_PORT)
													 .toString();
		
		this.eventServer = new WebServer(EVENT_PORT);
		
		this.eventHandler = ((method, parameters) -> {
			if (method != null) {
				if (method.equals(METHOD_EVENT)) {
					this.handleEvent(parameters);
				} else if (method.equals(METHOD_MULTICALL)) {
					this.handleMulticall(parameters);
				}
			}
			
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
	 * Handles an incoming multicall.
	 * 
	 * @param 	parameters		The parameters of the multicall.
	 */
	private final void handleMulticall(final Vector<Object> parameters) {
		for (final Object methodCalls : parameters) {
			@SuppressWarnings("unchecked")
			final Vector<Object> calls = (Vector<Object>)methodCalls;
			
			for (final Object call : calls) {
				@SuppressWarnings("unchecked")
				final Hashtable<String, Object> callParams = (Hashtable<String, Object>)call;
				
				final String methodName = (String)callParams.get("methodName");
				@SuppressWarnings("unchecked")
				final Vector<Object> params = (Vector<Object>)callParams.get("params");
				
				try {
					this.eventHandler.execute(methodName, params);
				} catch (Exception e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.log(Level.WARNING, "Error processing event : [" + e.getMessage() + "]", e);
					}
				}
			}
		}
	}
	
	/**
	 * Handles an event.
	 * 
	 * @param 	parameters		The event parameters.
	 */
	private final void handleEvent(final Vector<Object> parameters) {
		if (parameters.size() == 4) {
			final String address = (String)parameters.get(1);
			final String attribute = (String)parameters.get(2);
			final String value = String.valueOf(parameters.get(3));
			
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "CCU eventing : event received for [" + address + "] : attribute [" + attribute + "] - value [" + value + "]");
			}
			
			this.eventHandlers.stream().forEach((handler) -> handler.eventReceived(address, attribute, value));
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
		this.eventHandler = null;
		
		this.started = false;
		
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isStarted() {
		return this.started;
	}
}
