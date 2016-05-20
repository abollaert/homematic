package be.techniquez.homeautomation.homematic.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import be.techniquez.homeautomation.homematic.api.Device;
import be.techniquez.homeautomation.homematic.api.Dimmer;
import be.techniquez.homeautomation.homematic.api.Switch;

/**
 * Tests for the {@link CCUImpl}.
 * 
 * @author alex
 */
@RunWith(MockitoJUnitRunner.class)
public final class CCUImplTest {

	/** Mocked channel. */
	@Mock
	private CCUChannel channel;
	
	/** Mocked dimmer. */
	@Mock
	private Dimmer dimmer1;
	
	/** Mocked dimmer. */
	@Mock
	private Dimmer dimmer2;
	
	/** Mocked dimmer. */
	@Mock
	private Dimmer dimmer3;
	
	/** Mocked switch. */
	@Mock
	private Switch switch1;
	
	/** Mocked switch. */
	@Mock
	private Switch switch2;
	
	/**
	 * Run before a test.
	 */
	@Before
	public final void beforeTest() throws Exception {
		when(this.dimmer1.getName()).thenReturn("D1");
		when(this.dimmer1.getSerialNumber()).thenReturn("DM1");
		
		when(this.dimmer2.getName()).thenReturn("D2");
		when(this.dimmer2.getSerialNumber()).thenReturn("DM2");

		when(this.dimmer3.getName()).thenReturn("D3");
		when(this.dimmer3.getSerialNumber()).thenReturn("DM3");

		when(this.switch1.getName()).thenReturn("S1");
		when(this.switch1.getSerialNumber()).thenReturn("SW1");
		
		when(this.switch2.getName()).thenReturn("S2");
		when(this.switch2.getSerialNumber()).thenReturn("SW2");

		final List<Device> devices = Arrays.asList(this.dimmer1,
												   this.dimmer2,
												   this.dimmer3,
												   this.switch1,
												   this.switch2);
		
		when(this.channel.getDevices()).thenReturn(devices);
	}
	
	/**
	 * Tests the connect.
	 * 
	 * @throws	Exception	If something goes wrong.
	 */
	@Test
	public final void testCCUImpl() throws Exception {
		final CCUImpl ccu = new CCUImpl(this.channel);
		ccu.connect();
		
		verify(this.channel).connect();
		when(this.channel.isConnected()).thenReturn(true);
		
		assertThat(ccu.isConnected()).isTrue();
		
		verify(this.channel).getDevices();
		
		assertThat(ccu.getDimmers()).hasSize(3);
		assertThat(ccu.getSwitches()).hasSize(2);
		
		assertThat(ccu.getSwitch("S1").getSerialNumber()).isEqualTo("SW1");
		assertThat(ccu.getSwitch("S2").getSerialNumber()).isEqualTo("SW2");
		
		assertThat(ccu.getDimmer("D1").getSerialNumber()).isEqualTo("DM1");
		assertThat(ccu.getDimmer("D2").getSerialNumber()).isEqualTo("DM2");
		assertThat(ccu.getDimmer("D3").getSerialNumber()).isEqualTo("DM3");
		
		ccu.connect();
		
		verify(this.channel, times(1)).connect();
		
		ccu.disconnect();
		
		verify(this.channel).disconnect();
		when(this.channel.isConnected()).thenReturn(false);
		
		assertThat(ccu.isConnected()).isFalse();
		assertThat(ccu.getDimmers()).hasSize(0);
		assertThat(ccu.getSwitches()).hasSize(0);
		
		assertThat(ccu.getSwitch("S1")).isNull();
		assertThat(ccu.getSwitch("S2")).isNull();
		
		assertThat(ccu.getDimmer("D1")).isNull();
		assertThat(ccu.getDimmer("D2")).isNull();
		assertThat(ccu.getDimmer("D3")).isNull();

		ccu.disconnect();
		
		verify(this.channel, times(1)).disconnect();
	}
	
	/**
	 * Check that the connect and disconnect are thread safe. Just in case.
	 */
	@Test
	public final void testThreadSafety() throws Exception {
		final int numberOfThreads = 3;
		final int numberOfLoops = 20;
		
		final ExecutorService executor = Executors.newFixedThreadPool(3);
		
		final CCUImpl ccu = new CCUImpl(this.channel);
		final CyclicBarrier barrier = new CyclicBarrier(numberOfThreads + 1);
		
		for (int i = 0; i < numberOfLoops; i++) {
			for (int j = 0; j < numberOfThreads; j++) {
				 executor.submit(() -> { 
					 try { 
						 ccu.connect();
					 } catch (IOException e) { 
						 fail(e.getMessage(), e);
					 } finally {
						 try {
							 barrier.await();
						 } catch (Exception e) {
							 e.printStackTrace();
						 }
					 }
				 });
			}
			
			barrier.await();
			barrier.reset();
		}
		
		verify(this.channel, times(1)).connect();
		
		assertThat(ccu.isConnected()).isTrue();
		
		barrier.reset();
		
		for (int i = 0; i < numberOfLoops; i++) {
			for (int j = 0; j < numberOfThreads; j++) {
				 executor.submit(() -> { 
					 try { 
						 ccu.disconnect();
					 } catch (IOException e) { 
						 fail(e.getMessage(), e);
					 } finally {
						 try {
							 barrier.await();
						 } catch (Exception e) {
							 e.printStackTrace();
						 }
					 }
				 });
			}
			
			barrier.await();
			barrier.reset();
		}
		
		verify(this.channel, times(1)).disconnect();
		
		assertThat(ccu.isConnected()).isFalse();
	}
}
