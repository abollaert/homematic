package be.techniquez.homeautomation.homematic.impl.event;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Objects;
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

import be.techniquez.homeautomation.homematic.impl.CCUChannel.CCUChannelEventHandler;
import be.techniquez.homeautomation.homematic.impl.CCUEventLoop;

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
	
	/** The server URL. */
	private final String serverURL;
	
	/** The event client socket. */
	private XmlRpcClient eventClient;
	
	/** The event server. */
	private WebServer eventServer;
	
	/** The event handler. */
	private XmlRpcHandler eventHandler;
	
	/** The event handlers. */
	private final Set<CCUChannelEventHandler> eventHandlers = new HashSet<>();
	
	/** Indicates whether we have started. */
	private volatile boolean started;

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
		
		this.serverURL = new StringBuilder("http://").append(hostname)
				 									 .append(":")
				 									 .append(EVENT_PORT)
				 									 .toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized void start() throws IOException {
		if (!this.isStarted()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "CCU eventing : URL [" + this.url + "] : starting.");
			}
			
			if (this.eventServer == null) {
				this.initXmlRpcServer();
			}
			
			if (this.eventClient == null) {
				this.initXmlRpcClient();
			}
			
			this.started = true;
			
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "CCU eventing : URL [" + this.url + "] : started.");
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Tried to start me when already started.");
			}
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
	private final void initXmlRpcServer() {
		this.eventServer = new WebServer(EVENT_PORT);
		this.eventHandler = this::dispatchEvent;
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU Eventing RPC server : starting.");
		}
		
		this.eventServer.addHandler("$default", this.eventHandler);
		this.eventServer.start();
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "CCU Eventing RPC server : started.");
		}
	}
	
	/**
	 * Dispatch the event or multicall.
	 * 
	 * @param 	method			The method.
	 * @param 	parameters		The parameters.
	 * 
	 * @return	<code>null</code>.
	 */
	private final Object dispatchEvent(final String method, final Vector<?> parameters) {
		if (method != null) {
			if (method.equals(METHOD_EVENT)) {
				this.handleEvent(parameters);
			} else if (method.equals(METHOD_MULTICALL)) {
				this.handleMulticall(parameters);
			}
		}
		
		return null;
	}
	
	/**
	 * Handles an incoming multicall.
	 * 
	 * @param 	parameters		The parameters of the multicall.
	 */
	private final void handleMulticall(final Vector<?> parameters) {
		for (final Object methodCalls : parameters) {
			final Vector<?> calls = (Vector<?>)methodCalls;
			
			for (final Object call : calls) {
				@SuppressWarnings("unchecked")
				final Hashtable<String, ?> callParams = (Hashtable<String, ?>)call;
				
				final String methodName = (String)callParams.get("methodName");
				final Vector<?> params = (Vector<?>)callParams.get("params");
				
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
	private final void handleEvent(final Vector<?> parameters) {
		if (parameters.size() == 4) {
			final String address = (String)parameters.get(1);
			final String attribute = (String)parameters.get(2);
			final String value = String.valueOf(parameters.get(3));
			
			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "CCU eventing : event received for [" + address + "] : attribute [" + attribute + "] - value [" + value + "]");
			}
			
			this.eventHandlers.forEach(handler -> handler.eventReceived(address, attribute, value));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized void stop() throws IOException {
		if (this.isStarted()) {
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
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Tried to stop me when already stopped.");
			}			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isStarted() {
		return this.started;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addEventHandler(final CCUChannelEventHandler handler) {
		this.eventHandlers.add(Objects.requireNonNull(handler));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeEventHandler(final CCUChannelEventHandler handler) {
		this.eventHandlers.remove(Objects.requireNonNull(handler));
	}
}
