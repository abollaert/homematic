package be.techniquez.homeautomation.homematic.impl.channel;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import be.techniquez.homeautomation.homematic.impl.channel.XMLAPIURLBuilder.Endpoint;

/**
 * Tests for the {@link XMLAPIURLBuilder}.
 * 
 * @author alex
 */
public final class XMLAPIURLBuilderTest {
	
	/** The base URL. */
	private static final String BASE_URL = "http://somehost";

	/**
	 * Run before the test.
	 * 
	 */
	@Before
	public final void beforeTest() throws Exception {
	}
	
	/**
	 * Tests the parameters.
	 */
	@Test
	public final void testWithTrailingSlash() throws Exception {
		final URL url = XMLAPIURLBuilder.withBaseURL(BASE_URL + "/")
										.endpoint(Endpoint.DEVICELIST)
										.build();
	
		assertThat(url).isEqualTo(new URL(BASE_URL + "/" + Endpoint.DEVICELIST.getCGI()));
	}
	
	/**
	 * Tests the parameters.
	 */
	@Test
	public final void testWithoutTrailingSlash() throws Exception {
		final URL url = XMLAPIURLBuilder.withBaseURL(BASE_URL)
										.endpoint(Endpoint.DEVICELIST)
										.build();
	
		assertThat(url).isEqualTo(new URL(BASE_URL + "/" + Endpoint.DEVICELIST.getCGI()));
	}
	/**
	 * Tests the parameters.
	 */
	@Test(expected = IllegalStateException.class)
	public final void testMalformedURL() throws Exception {
		XMLAPIURLBuilder.withBaseURL("malformed.url.")
										.endpoint(Endpoint.DEVICELIST)
										.build();
	}
	
	/**
	 * Tests the endpoints.
	 */
	@Test
	public final void testParameters() throws Exception {
		final URL url = XMLAPIURLBuilder.withBaseURL(BASE_URL)
										.endpoint(Endpoint.DEVICELIST)
										.parameter("param1", 3)
										.parameter("param2", "some-value")
										.parameter("param3", 0.21)
										.build();
		
		assertThat(url).isEqualTo(new URL(new StringBuilder(BASE_URL).append("/")
																	 .append(Endpoint.DEVICELIST.getCGI())
																	 .append("?param1=3&param2=some-value&param3=0.21").toString()));
	}
	
	/**
	 * Tests the endpoints.
	 */
	@Test
	public final void testEndpoints() throws Exception {
		Arrays.stream(Endpoint.values())
			  .forEach(XMLAPIURLBuilderTest::checkURLForEndpoint);
		

	}
	
	/**
	 * Generate an url for an endpoint.
	 * 
	 * @param 		endpoint		The endpoint.
	 * 
	 * @return		The URL.
	 */
	private static final void checkURLForEndpoint(final Endpoint endpoint) {
		final URL url = XMLAPIURLBuilder.withBaseURL(BASE_URL)
										.endpoint(endpoint)
										.build();

		try {
			assertThat(url).isEqualTo(new URL(new StringBuilder(BASE_URL).append("/")
												 .append(endpoint.getCGI())
												 .toString()));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
