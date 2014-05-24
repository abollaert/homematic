package be.techniquez.homeautomation.homematic.impl.channel;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Device.Type;

/**
 * Channel to the CCU.
 * 
 * @author alex
 */
public final class CCUChannelImpl implements CCUChannel {
	
	/** Logger instance. */
	private static final Logger logger = Logger.getLogger(CCUChannelImpl.class.getName());

	/** The default host. */
	private static final String DEFAULT_HOST = "homematic";
	
	/** Method used to list the devices. */
	private static final String GET_VALUE_METHOD = "getValue";
	
	/** Method used to list the devices. */
	private static final String GET_PARAMSET_METHOD = "getParamset";
	
	/** Method used to set values. */
	private static final String SET_VALUE_METHOD = "setValue";
	
	/** The wired port. */
	private static final int BIDCOS_WIRED_PORT = 2000;
	
	/** The RF port. */
	private static final int BIDCOS_RF_PORT = 2001;
	
	/** The system port. */
	private static final int SYSTEM_PORT = 2002;
	
	/** Address of the CCU. */
	private final String ccuAddress;
	
	/** The RF port. */
	private XmlRpcClient rfClient;
	
	/** The wired port. */
	private XmlRpcClient wiredClient;
	
	/** The system port. */
	private XmlRpcClient systemClient;
	
	/**
	 * Create a new instance using a specific address.
	 * 
	 * @param 	ccuAddress		The address.
	 */
	public CCUChannelImpl(final String ccuAddress) {
		this.ccuAddress = ccuAddress;
	}
	
	/**
	 * Create a new instance.
	 * 
	 * @throws 	UnknownHostException		If the default host is not known.
	 */
	public CCUChannelImpl() throws UnknownHostException {
		this.ccuAddress = DEFAULT_HOST;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final void connect() throws IOException {
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Connecting to the Homematic system at [" + this.ccuAddress + "]");
		}
		
		this.rfClient = new XmlRpcClient(this.ccuAddress, BIDCOS_RF_PORT);
		this.wiredClient = new XmlRpcClient(this.ccuAddress, BIDCOS_WIRED_PORT);
		this.systemClient = new XmlRpcClient(this.ccuAddress, SYSTEM_PORT);
	}

	/**
	 * Returns the client to use for a particular device type.
	 * 
	 * @param 		type		The device type.
	 * 
	 * @return		The client to use.
	 */
	private final XmlRpcClient getClientFor(final Device.Type type) {
		switch (type) {
			case SYSTEM: {
				return this.systemClient;
			}
			
			case WIRED: {
				return this.wiredClient;
			}
			
			case WIRELESS: {
				return this.rfClient;
			}
			
			default: {
				throw new IllegalArgumentException("There is no XML-RPC interface known to this channel that corresponds to device type [" + type + "]");
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T> T getValue(final Device.Type deviceType, final String address, final String parameterName, final Class<T> expectedClass) throws IOException {
		final XmlRpcClient client = this.getClientFor(deviceType);
		
		final Vector<String> parameters = new Vector<String>(2);
		parameters.add(address);
		parameters.add(parameterName);
		
		final XmlRpcClientRequest request = new XmlRpcRequest(GET_VALUE_METHOD, parameters);
		
		try {
			return expectedClass.cast(client.execute(request));
		} catch (XmlRpcException e) {
			throw new IOException("XML RPC error when getting value [" + parameterName + "] from device at address [" + address + "] : [" + e.getMessage() + "]", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final Hashtable<String, Object> getParamSet(final String address, final String paramSetKey, final Device.Type deviceType) throws IOException {
		final XmlRpcClient client = this.getClientFor(deviceType);
		
		final Vector<String> parameters = new Vector<String>(2);
		parameters.add(address);
		parameters.add(paramSetKey);
		
		final XmlRpcClientRequest request = new XmlRpcRequest(GET_PARAMSET_METHOD, parameters);
		
		try {
			final Object response = client.execute(request);

			return (Hashtable<String, Object>)response;
		} catch (XmlRpcException e) {
			throw new IOException("XML RPC error when getting param set [" + paramSetKey + "] from device at address [" + address + "] : [" + e.getMessage() + "]", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T> void setValue(final Type deviceType, final String address, final String parameterName, final T value) throws IOException {
		final XmlRpcClient client = this.getClientFor(deviceType);
		
		final Vector<Object> parameters = new Vector<Object>(3);
		parameters.add(address);
		parameters.add(parameterName);
		parameters.add(value);
		
		final XmlRpcClientRequest request = new XmlRpcRequest(SET_VALUE_METHOD, parameters);
		
		try {
			client.execute(request);
		} catch (XmlRpcException e) {
			throw new IOException("XML RPC error when setting value [" + parameterName + "] from device at address [" + address + "] : [" + e.getMessage() + "]", e);
		}
	}
}
