package be.techniquez.homeautomation.homematic.impl.channel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XML API URL builder.
 * 
 * @author alex
 */
final class XMLAPIURLBuilder {
	
	private static final Logger logger = Logger.getLogger(XMLAPIURLBuilder.class.getName());

	/** The base URL. */
	private static final String BASE = "/config/xmlapi/";
	
	/** The CCUChannel. */
	private final String hostname;
	
	/** The port. */
	private final int port;
	
	/** The endpoint. */
	private Endpoint endpoint;
	
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
		DEVICELIST("devicelist.cgi");
		
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
}
