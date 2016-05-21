package be.techniquez.homeautomation.homematic.impl.channel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	private final String baseURL;
	
	/** The endpoint. */
	private Endpoint endpoint;
	
	/** The parameters to be added. */
	private final Map<String, String> parameters = new LinkedHashMap<>();
	
	/**
	 * Create a new instance.
	 * 
	 * @param 	baseURL		The base URL.
	 */
	private XMLAPIURLBuilder(final String baseURL) {
		this.baseURL = Objects.requireNonNull(baseURL);
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
		
		/**
		 * Returns the cgi.
		 * 
		 * @return	The cgi.
		 */
		final String getCGI() {
			return this.cgi;
		}
	}
	
	/**
	 * Create a new URL builder for said host name and port.
	 * 
	 * @param 	baseURL		The base URL.
	 * 
	 * @return	The builder.
	 */
	static final XMLAPIURLBuilder withBaseURL(final String baseURL) {
		return new XMLAPIURLBuilder(baseURL);
	}
	
	/**
	 * Sets the endpoint.
	 * 
	 * @param 		endpoint	The endpoint.
	 * 
	 * @return		The builder.
	 */
	final XMLAPIURLBuilder endpoint(final Endpoint endpoint) {
		this.endpoint = Objects.requireNonNull(endpoint);
		
		return this;
	}
	
	/**
	 * Build the URL.
	 * 
	 * @return	The URL.
	 */
	final URL build() {
		final StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(this.baseURL);
		
		if (!this.baseURL.endsWith("/")) {
			urlBuilder.append("/");
		}
		
		urlBuilder.append(this.endpoint.getCGI());
		
		if (this.parameters.size() > 0) {
			urlBuilder.append("?");
			
			for (final Iterator<String> parameterIterator = this.parameters.keySet().iterator(); parameterIterator.hasNext(); ) {
				final String name = parameterIterator.next();
				final String value = this.parameters.get(name);
				
				urlBuilder.append(name).append("=").append(value).append(parameterIterator.hasNext() ? "&" : "");
			}
		}
		
		final String url = urlBuilder.toString();
		
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Using URL [" + url + "]");
		}
		
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
