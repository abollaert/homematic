package be.techniquez.homeautomation.homematic.impl.channel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XML API URL builder.
 * 
 * @author alex
 */
final class XMLAPIURLBuilder {
	
	/** Logger instance. */
	private static final Logger logger = Logger.getLogger(XMLAPIURLBuilder.class.getName());

	/** The base URL. */
	private static final String BASE = "/addons/xmlapi/";
	
	/** The CCUChannel. */
	private final String hostname;
	
	/** The port. */
	private final int port;
	
	/** The endpoint. */
	private Endpoint endpoint;
	
	/** The parameters to be added. */
	private final Map<String, String> parameters = new HashMap<>();
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	hostname		The host name.
	 * @param 	port			The port.
	 */
	private XMLAPIURLBuilder(final String hostname, final int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Endpoints.
	 * 
	 * @author alex
	 */
	enum Endpoint {
		DEVICELIST("devicelist.cgi"),
		STATECHANGE("statechange.cgi"),
		GET_STATE("state.cgi");
		
		/** The CGI url. */
		private final String cgi;
		
		/**
		 * Create a new instance.
		 * 
		 * @param 	cgi		The CGI URL.
		 */
		private Endpoint(final String cgi) {
			this.cgi = cgi;
		}
	}
	
	/**
	 * Create a new URL builder for said host name and port.
	 * 
	 * @param 	hostname		The host name.
	 * @param 	port			The port.
	 * 
	 * @return	The builder.
	 */
	static final XMLAPIURLBuilder forHost(final String hostname, final int port) {
		return new XMLAPIURLBuilder(hostname, port);
	}
	
	/**
	 * Sets the endpoint.
	 * 
	 * @param 		endpoint	The endpoint.
	 * 
	 * @return		The builder.
	 */
	final XMLAPIURLBuilder endpoint(final Endpoint endpoint) {
		assert(endpoint != null);
		
		this.endpoint = endpoint;
		
		return this;
	}
	
	/**
	 * Build the URL.
	 * 
	 * @return	The URL.
	 */
	final URL build() {
		assert this.endpoint != null;
		
		final StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("http://");
		urlBuilder.append(this.hostname);
		urlBuilder.append(":");
		urlBuilder.append(this.port);
		urlBuilder.append(BASE);
		urlBuilder.append(this.endpoint.cgi);
		
		if (this.parameters.size() > 0) {
			urlBuilder.append("?");
			
			for (final Iterator<String> parameterIterator = this.parameters.keySet().iterator(); parameterIterator.hasNext(); ) {
				final String name = parameterIterator.next();
				final String value = this.parameters.get(name);
				
				urlBuilder.append(name).append("=").append(value).append(parameterIterator.hasNext() ? "&" : "");
			}
		}
		
		final String url = urlBuilder.toString();
		
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Malformed URL : " + url + "] : [" + e.getMessage() + "]", e); 
			}
			
			throw new IllegalStateException("Malformed URL : " + url + "] : [" + e.getMessage() + "]", e);
		}
	}
	
	/**
	 * Adds a parameter to the list.
	 * 
	 * @param 	name		The name.
	 * @param 	value		The value.
	 */
	final <T> XMLAPIURLBuilder parameter(final String name, final T value) {
		this.parameters.put(name, Objects.requireNonNull(value).toString());
		
		return this;
	}
}
