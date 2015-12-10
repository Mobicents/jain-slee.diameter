/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.slee.resource.diameter.base.tests.factories;

import static org.jdiameter.client.impl.helpers.Parameters.*;
import static org.jdiameter.server.impl.helpers.Parameters.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.AbortSessionRequest;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

/**
 * Test class for JAIN SLEE Diameter Base RA Message and AVP Factories
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class BaseFactoriesTest {

	public static final int NO_APP_ID_AVPS = 0;
	public static final int VENDOR_SPECIFIC_APP_ID = 16;
	public static final int AUTH_APP_ID = 32;
	public static final int ACCT_APP_ID = 64;
	public static final int VENDOR_SPECIFIC_APP_ID_AUTH = VENDOR_SPECIFIC_APP_ID * AUTH_APP_ID; // 512
	public static final int VENDOR_SPECIFIC_APP_ID_ACCT = VENDOR_SPECIFIC_APP_ID * ACCT_APP_ID; // 1024
	public static final int VENDOR_SPECIFIC_APP_ID_AUTH_AND_ACCT = VENDOR_SPECIFIC_APP_ID * AUTH_APP_ID * ACCT_APP_ID; // 32768
	public static final int AUTH_AND_ACCT_APP_ID = AUTH_APP_ID + ACCT_APP_ID; // 96
	public static final int AUTH_AND_VENDOR_SPECIFIC_APP_ID = AUTH_APP_ID + VENDOR_SPECIFIC_APP_ID; // 48
	public static final int ACCT_AND_VENDOR_SPECIFIC_APP_ID = ACCT_APP_ID + VENDOR_SPECIFIC_APP_ID; // 80
	public static final int AUTH_ACCT_AND_VENDOR_SPECIFIC_APP_ID = AUTH_APP_ID + ACCT_APP_ID + VENDOR_SPECIFIC_APP_ID; // 112

	private static String clientHost = "127.0.0.1";
	private static String clientPort = "13868";
	private static String clientURI = "aaa://" + clientHost + ":" + clientPort;

	private static String serverHost = "localhost";
	private static String serverPort = "3868";
	private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

	private static String realmName = "mobicents.org";

	private static DiameterMessageFactoryImpl messageFactory;
	private static DiameterAvpFactoryImpl avpFactory;

	static {
		Stack stack = new org.jdiameter.client.impl.StackImpl();
		try {
			stack.init(new MyConfiguration());
			AvpDictionary.INSTANCE.parseDictionary(BaseFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
		}
		catch (Exception e) {
			throw new RuntimeException("");
		}

		messageFactory = new DiameterMessageFactoryImpl(stack);
		avpFactory = new DiameterAvpFactoryImpl();
	}

	@Test
	public void isRequestASR() throws Exception {
		AbortSessionRequest asr = messageFactory.createAbortSessionRequest();
		assertTrue("Request Flag in Abort-Session-Request is not set.", asr.getHeader().isRequest());
	}

	@Test
	public void isProxiableASR() throws Exception {
		AbortSessionRequest asr = messageFactory.createAbortSessionRequest();
		assertTrue("The 'P' bit is not set by default in Abort-Session-Request, it should.", asr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersASR() throws Exception {
		AbortSessionRequest asr = messageFactory.createAbortSessionRequest();

		int nFailures = AvpAssistant.INSTANCE.testMethods(asr, AbortSessionRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerASA() throws Exception {
		AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer(messageFactory.createAbortSessionRequest());
		assertFalse("Request Flag in Abort-Session-Answer is set.", asa.getHeader().isRequest());
	}

	@Test
	public void isProxiableCopiedASA() throws Exception {
		AbortSessionRequest asr = messageFactory.createAbortSessionRequest();
		AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer(asr);
		assertEquals("The 'P' bit is not copied from request in Abort-Session-Answer, it should. [RFC3588/6.2]", asr.getHeader().isProxiable(), asa.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) asr).getGenericData().setProxiable(!asr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Abort-Session-Request, it should.", asr.getHeader().isProxiable() != asa.getHeader().isProxiable());

		asa = messageFactory.createAbortSessionAnswer(asr);
		assertEquals("The 'P' bit is not copied from request in Abort-Session-Answer, it should. [RFC3588/6.2]", asr.getHeader().isProxiable(), asa.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetASA() throws Exception {
		AbortSessionRequest asr = messageFactory.createAbortSessionRequest();
		((DiameterMessageImpl) asr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Abort-Session-Request", asr.getHeader().isPotentiallyRetransmitted());

		AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer(asr);
		assertFalse("The 'T' flag should not be set in Abort-Session-Answer", asa.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void testGettersAndSettersASA() throws Exception {
		AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer(messageFactory.createAbortSessionRequest());

		int nFailures = AvpAssistant.INSTANCE.testMethods(asa, AbortSessionAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostASA() throws Exception {
		AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer(messageFactory.createAbortSessionRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", asa.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmASA() throws Exception {
		AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer(messageFactory.createAbortSessionRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", asa.getDestinationRealm());
	}

	@Test
	public void isRequestACR() throws Exception {
		AccountingRequest acr = messageFactory.createAccountingRequest();
		assertTrue("Request Flag in Accounting-Request is not set.", acr.getHeader().isRequest());
	}

	@Test
	public void isProxiableACR() throws Exception {
		AccountingRequest acr = messageFactory.createAccountingRequest();
		assertTrue("The 'P' bit is not set by default in Accounting-Request, it should.", acr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersACR() throws Exception {
		AccountingRequest acr = messageFactory.createAccountingRequest();

		int nFailures = AvpAssistant.INSTANCE.testMethods(acr, AccountingRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerACA() throws Exception {
		AccountingAnswer aca = messageFactory.createAccountingAnswer(messageFactory.createAccountingRequest());
		assertFalse("Request Flag in Accounting-Answer is set.", aca.getHeader().isRequest());
	}

	@Test
	public void isProxiableCopiedACA() throws Exception {
		AccountingRequest acr = messageFactory.createAccountingRequest();
		acr.setSessionId("sssxxx");
		AccountingAnswer aca = messageFactory.createAccountingAnswer(acr);
		System.out.println(aca);
		assertEquals("The 'P' bit is not copied from request in Accounting-Answer, it should. [RFC3588/6.2]", acr.getHeader().isProxiable(), aca.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) acr).getGenericData().setProxiable(!acr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Accounting-Request, it should.", acr.getHeader().isProxiable() != aca.getHeader().isProxiable());

		aca = messageFactory.createAccountingAnswer(acr);
		assertEquals("The 'P' bit is not copied from request in Accounting-Answer, it should. [RFC3588/6.2]", acr.getHeader().isProxiable(), aca.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetACA() throws Exception {
		AccountingRequest acr = messageFactory.createAccountingRequest();
		((DiameterMessageImpl) acr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Accounting-Request", acr.getHeader().isPotentiallyRetransmitted());

		AccountingAnswer aca = messageFactory.createAccountingAnswer(acr);
		assertFalse("The 'T' flag should not be set in Accounting-Answer", aca.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void testGettersAndSettersACA() throws Exception {
		AccountingAnswer aca = messageFactory.createAccountingAnswer(messageFactory.createAccountingRequest());

		int nFailures = AvpAssistant.INSTANCE.testMethods(aca, AccountingAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostACA() throws Exception {
		AccountingAnswer aca = messageFactory.createAccountingAnswer(messageFactory.createAccountingRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aca.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmACA() throws Exception {
		AccountingAnswer aca = messageFactory.createAccountingAnswer(messageFactory.createAccountingRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aca.getDestinationRealm());
	}

	@Test
	public void isRequestCER() throws Exception {
		CapabilitiesExchangeRequest cer = messageFactory.createCapabilitiesExchangeRequest();
		assertTrue("Request Flag in Capabilities-Exchange-Request is not set.", cer.getHeader().isRequest());
	}

	@Test
	public void isProxiableCER() throws Exception {
		CapabilitiesExchangeRequest cer = messageFactory.createCapabilitiesExchangeRequest();
		assertFalse("The 'P' bit is set by default in Capabilities-Exchange-Request, it shouldn't.", cer.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersCER() throws Exception {
		CapabilitiesExchangeRequest cer = messageFactory.createCapabilitiesExchangeRequest();

		int nFailures = AvpAssistant.INSTANCE.testMethods(cer, CapabilitiesExchangeRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerCEA() throws Exception {
		CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer(messageFactory.createCapabilitiesExchangeRequest());
		assertFalse("Request Flag in Capabilities-Exchange-Answer is set.", cea.getHeader().isRequest());
	}

	@Test
	public void isProxiableCopiedCEA() throws Exception {
		CapabilitiesExchangeRequest cer = messageFactory.createCapabilitiesExchangeRequest();
		CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer(cer);
		assertEquals("The 'P' bit is not copied from request in Capabilities-Exchange-Answer, it should. [RFC3588/6.2]", cer.getHeader().isProxiable(), cea.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) cer).getGenericData().setProxiable(!cer.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Capabilities-Exchange-Request, it should.", cer.getHeader().isProxiable() != cea.getHeader().isProxiable());

		cea = messageFactory.createCapabilitiesExchangeAnswer(cer);
		assertEquals("The 'P' bit is not copied from request in Capabilities-Exchange-Answer, it should. [RFC3588/6.2]", cer.getHeader().isProxiable(), cea.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetCEA() throws Exception {
		CapabilitiesExchangeRequest cer = messageFactory.createCapabilitiesExchangeRequest();
		((DiameterMessageImpl) cer).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Capabilities-Exchange-Request", cer.getHeader().isPotentiallyRetransmitted());

		CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer(cer);
		assertFalse("The 'T' flag should not be set in Capabilities-Exchange-Answer", cea.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void testGettersAndSettersCEA() throws Exception {
		CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer(messageFactory.createCapabilitiesExchangeRequest());

		int nFailures = AvpAssistant.INSTANCE.testMethods(cea, CapabilitiesExchangeAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostCEA() throws Exception {
		CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer(messageFactory.createCapabilitiesExchangeRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cea.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmCEA() throws Exception {
		CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer(messageFactory.createCapabilitiesExchangeRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cea.getDestinationRealm());
	}

	@Test
	public void isRequestDWR() throws Exception {
		DeviceWatchdogRequest dwr = messageFactory.createDeviceWatchdogRequest();
		assertTrue("Request Flag in Device-Watchdog-Request is not set.", dwr.getHeader().isRequest());
	}

	@Test
	public void isProxiableDWR() throws Exception {
		DeviceWatchdogRequest dwr = messageFactory.createDeviceWatchdogRequest();
		assertFalse("The 'P' bit is set by default in Device-Watchdog-Request, it shouldn't.", dwr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersDWR() throws Exception {
		DeviceWatchdogRequest dwr = messageFactory.createDeviceWatchdogRequest();

		int nFailures = AvpAssistant.INSTANCE.testMethods(dwr, DeviceWatchdogRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerDWA() throws Exception {
		DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer(messageFactory.createDeviceWatchdogRequest());
		assertFalse("Request Flag in Device-Watchdog-Answer is set.", dwa.getHeader().isRequest());
	}

	@Test
	public void isProxiableCopiedDWA() throws Exception {
		DeviceWatchdogRequest dwr = messageFactory.createDeviceWatchdogRequest();
		DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer(dwr);
		assertEquals("The 'P' bit is not copied from request in Device-Watchdog-Answer, it should. [RFC3588/6.2]", dwr.getHeader().isProxiable(), dwa.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) dwr).getGenericData().setProxiable(!dwr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Device-Watchdog-Request, it should.", dwr.getHeader().isProxiable() != dwa.getHeader().isProxiable());

		dwa = messageFactory.createDeviceWatchdogAnswer(dwr);
		assertEquals("The 'P' bit is not copied from request in Device-Watchdog-Answer, it should. [RFC3588/6.2]", dwr.getHeader().isProxiable(), dwa.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetDWA() throws Exception {
		DeviceWatchdogRequest dwr = messageFactory.createDeviceWatchdogRequest();
		((DiameterMessageImpl) dwr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Device-Watchdog-Request", dwr.getHeader().isPotentiallyRetransmitted());

		DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer(dwr);
		assertFalse("The 'T' flag should not be set in Device-Watchdog-Answer", dwa.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void testGettersAndSettersDWA() throws Exception {
		DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer(messageFactory.createDeviceWatchdogRequest());

		int nFailures = AvpAssistant.INSTANCE.testMethods(dwa, DeviceWatchdogAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostDWA() throws Exception {
		DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer(messageFactory.createDeviceWatchdogRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dwa.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmDWA() throws Exception {
		DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer(messageFactory.createDeviceWatchdogRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dwa.getDestinationRealm());
	}

	@Test
	public void isRequestDPR() throws Exception {
		DisconnectPeerRequest dpr = messageFactory.createDisconnectPeerRequest();
		assertTrue("Request Flag in Disconnect-Peer-Request is not set.", dpr.getHeader().isRequest());
	}

	@Test
	public void isProxiableDPR() throws Exception {
		DisconnectPeerRequest dpr = messageFactory.createDisconnectPeerRequest();
		assertFalse("The 'P' bit is set by default in Disconnect-Peer-Request, it shouldn't.", dpr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersDPR() throws Exception {
		DisconnectPeerRequest dpr = messageFactory.createDisconnectPeerRequest();

		int nFailures = AvpAssistant.INSTANCE.testMethods(dpr, DisconnectPeerRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerDPA() throws Exception {
		DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer(messageFactory.createDisconnectPeerRequest());
		assertFalse("Request Flag in Disconnect-Peer-Answer is set.", dpa.getHeader().isRequest());
	}

	@Test
	public void isProxiableCopiedDPA() throws Exception {
		DisconnectPeerRequest dpr = messageFactory.createDisconnectPeerRequest();
		DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer(dpr);
		assertEquals("The 'P' bit is not copied from request in Disconnect-Peer-Answer, it should. [RFC3588/6.2]", dpr.getHeader().isProxiable(), dpa.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) dpr).getGenericData().setProxiable(!dpr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Disconnect-Peer-Request, it should.", dpr.getHeader().isProxiable() != dpa.getHeader().isProxiable());

		dpa = messageFactory.createDisconnectPeerAnswer(dpr);
		assertEquals("The 'P' bit is not copied from request in Disconnect-Peer-Answer, it should. [RFC3588/6.2]", dpr.getHeader().isProxiable(), dpa.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetDPA() throws Exception {
		DisconnectPeerRequest dpr = messageFactory.createDisconnectPeerRequest();
		((DiameterMessageImpl) dpr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Disconnect-Peer-Request", dpr.getHeader().isPotentiallyRetransmitted());

		DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer(dpr);
		assertFalse("The 'T' flag should not be set in Disconnect-Peer-Answer", dpa.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void testGettersAndSettersDPA() throws Exception {
		DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer(messageFactory.createDisconnectPeerRequest());

		int nFailures = AvpAssistant.INSTANCE.testMethods(dpa, DisconnectPeerAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostDPA() throws Exception {
		DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer(messageFactory.createDisconnectPeerRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dpa.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmDPA() throws Exception {
		DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer(messageFactory.createDisconnectPeerRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dpa.getDestinationRealm());
	}

	@Test
	public void isRequestRAR() throws Exception {
		ReAuthRequest rar = messageFactory.createReAuthRequest();
		assertTrue("Request Flag in Re-Auth-Request is not set.", rar.getHeader().isRequest());
	}

	@Test
	public void isProxiableRAR() throws Exception {
		ReAuthRequest acr = messageFactory.createReAuthRequest();
		assertTrue("The 'P' bit is not set by default in Re-Auth-Request, it should.", acr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersRAR() throws Exception {
		ReAuthRequest rar = messageFactory.createReAuthRequest();

		int nFailures = AvpAssistant.INSTANCE.testMethods(rar, ReAuthRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerRAA() throws Exception {
		ReAuthAnswer raa = messageFactory.createReAuthAnswer(messageFactory.createReAuthRequest());
		assertFalse("Request Flag in Re-Auth-Answer is set.", raa.getHeader().isRequest());
	}

	@Test
	public void isProxiableCopiedRAA() throws Exception {
		ReAuthRequest asr = messageFactory.createReAuthRequest();
		ReAuthAnswer asa = messageFactory.createReAuthAnswer(asr);
		assertEquals("The 'P' bit is not copied from request in Re-Auth-Answer, it should. [RFC3588/6.2]", asr.getHeader().isProxiable(), asa.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) asr).getGenericData().setProxiable(!asr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Re-Auth-Request, it should.", asr.getHeader().isProxiable() != asa.getHeader().isProxiable());

		asa = messageFactory.createReAuthAnswer(asr);
		assertEquals("The 'P' bit is not copied from request in Re-Auth-Answer, it should. [RFC3588/6.2]", asr.getHeader().isProxiable(), asa.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetRAA() throws Exception {
		ReAuthRequest rar = messageFactory.createReAuthRequest();
		((DiameterMessageImpl) rar).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Re-Auth-Request", rar.getHeader().isPotentiallyRetransmitted());

		ReAuthAnswer raa = messageFactory.createReAuthAnswer(rar);
		assertFalse("The 'T' flag should not be set in Re-Auth-Answer", raa.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void testGettersAndSettersRAA() throws Exception {
		ReAuthAnswer raa = messageFactory.createReAuthAnswer(messageFactory.createReAuthRequest());

		int nFailures = AvpAssistant.INSTANCE.testMethods(raa, ReAuthAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostRAA() throws Exception {
		ReAuthAnswer raa = messageFactory.createReAuthAnswer(messageFactory.createReAuthRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", raa.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmRAA() throws Exception {
		ReAuthAnswer raa = messageFactory.createReAuthAnswer(messageFactory.createReAuthRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", raa.getDestinationRealm());
	}

	@Test
	public void isRequestSTR() throws Exception {
		SessionTerminationRequest str = messageFactory.createSessionTerminationRequest();
		assertTrue("Request Flag in Disconnect-Peer-Request is not set.", str.getHeader().isRequest());
	}

	@Test
	public void isProxiableSTR() throws Exception {
		SessionTerminationRequest acr = messageFactory.createSessionTerminationRequest();
		assertTrue("The 'P' bit is not set by default in Session-Termination-Request, it should.", acr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersSTR() throws Exception {
		SessionTerminationRequest str = messageFactory.createSessionTerminationRequest();

		int nFailures = AvpAssistant.INSTANCE.testMethods(str, SessionTerminationRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerSTA() throws Exception {
		SessionTerminationAnswer sta = messageFactory.createSessionTerminationAnswer(messageFactory.createSessionTerminationRequest());
		assertFalse("Request Flag in Disconnect-Peer-Answer is set.", sta.getHeader().isRequest());
	}

	@Test
	public void isProxiableCopiedSTA() throws Exception {
		SessionTerminationRequest asr = messageFactory.createSessionTerminationRequest();
		SessionTerminationAnswer asa = messageFactory.createSessionTerminationAnswer(asr);
		assertEquals("The 'P' bit is not copied from request in Session-Termination-Answer, it should. [RFC3588/6.2]", asr.getHeader().isProxiable(), asa.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) asr).getGenericData().setProxiable(!asr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Session-Termination-Request, it should.", asr.getHeader().isProxiable() != asa.getHeader().isProxiable());

		asa = messageFactory.createSessionTerminationAnswer(asr);
		assertEquals("The 'P' bit is not copied from request in Session-Termination-Answer, it should. [RFC3588/6.2]", asr.getHeader().isProxiable(), asa.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetSTA() throws Exception {
		SessionTerminationRequest str = messageFactory.createSessionTerminationRequest();
		((DiameterMessageImpl) str).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Session-Termination-Request", str.getHeader().isPotentiallyRetransmitted());

		SessionTerminationAnswer sta = messageFactory.createSessionTerminationAnswer(str);
		assertFalse("The 'T' flag should not be set in Session-Termination-Answer", sta.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void testGettersAndSettersSTA() throws Exception {
		SessionTerminationAnswer str = messageFactory.createSessionTerminationAnswer(messageFactory.createSessionTerminationRequest());

		int nFailures = AvpAssistant.INSTANCE.testMethods(str, SessionTerminationAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostSTA() throws Exception {
		SessionTerminationAnswer sta = messageFactory.createSessionTerminationAnswer(messageFactory.createSessionTerminationRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sta.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmSTA() throws Exception {
		SessionTerminationAnswer sta = messageFactory.createSessionTerminationAnswer(messageFactory.createSessionTerminationRequest());
		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sta.getDestinationRealm());
	}

	@Test
	public void testAvpFactoryCreateExperimentalResult() {
		ExperimentalResultAvp erAvp1 = avpFactory.createExperimentalResult(10609L, 9999L);

		Assert.assertNotNull("Created Experimental-Result AVP from objects should not be null.", erAvp1);

		ExperimentalResultAvp erAvp2 = avpFactory.createExperimentalResult(erAvp1.getExtensionAvps());
        Assert.assertEquals("Created Experimental-Result AVP from extension avps should be equal to original.", erAvp1, erAvp2);

		ExperimentalResultAvp erAvp3 = avpFactory.createExperimentalResult(erAvp2.getVendorIdAVP(), erAvp2.getExperimentalResultCode());

		Assert.assertEquals("Created Experimental-Result AVP from getters should be equal to original.", erAvp1, erAvp3);
	}

	@Test
	public void testAvpFactoryCreateProxyInfo() {
		ProxyInfoAvp piAvp1 = avpFactory.createProxyInfo(new DiameterIdentity("diameter.mobicents.org"), "INITIALIZED".getBytes());

		Assert.assertNotNull("Created Proxy-Info AVP from objects should not be null.", piAvp1);

		ProxyInfoAvp piAvp2 = avpFactory.createProxyInfo(piAvp1.getExtensionAvps());

		Assert.assertEquals("Created Proxy-Info AVP from extension avps should be equal to original.", piAvp1, piAvp2);

		ProxyInfoAvp piAvp3 = avpFactory.createProxyInfo(piAvp2.getProxyHost(), piAvp2.getProxyState());

		Assert.assertEquals("Created Proxy-Info AVP from getters should be equal to original.", piAvp1, piAvp3);
	}

	@Test
	public void testAvpFactoryCreateVendorSpecificApplicationId() {
		VendorSpecificApplicationIdAvp vsaidAvp1 = avpFactory.createVendorSpecificApplicationId(10609L);

		Assert.assertNotNull("Created Vendor-Specific-Application-Id AVP from objects should not be null.", vsaidAvp1);

		VendorSpecificApplicationIdAvp vsaidAvp2 = avpFactory.createVendorSpecificApplicationId(vsaidAvp1.getExtensionAvps());

		Assert.assertEquals("Created Vendor-Specific-Application-Id AVP from extension avps should be equal to original.", vsaidAvp1, vsaidAvp2);

		VendorSpecificApplicationIdAvp vsaidAvp3 = avpFactory.createVendorSpecificApplicationId(vsaidAvp2.getVendorIdsAvp()[0]);

		Assert.assertEquals("Created Vendor-Specific-Application-Id AVP from getters should be equal to original.", vsaidAvp1, vsaidAvp3);
	}

	@Test
	public void testMessageCreationVendorSpecificApplicationIdAvp() {
		// Relates to Issue #1555 (http://code.google.com/p/mobicents/issues/detail?id=1555)
		List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

		try {
			long vendorId = 2312L;
			long acctApplicationId = 23121L;

			DiameterAvp avpVendorId = avpFactory.createAvp(DiameterAvpCodes.VENDOR_ID, vendorId);
			DiameterAvp avpAcctApplicationId = avpFactory.createAvp(DiameterAvpCodes.ACCT_APPLICATION_ID, acctApplicationId);

			avps.add(avpFactory.createAvp(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[] { avpVendorId, avpAcctApplicationId }));

			DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
			avpArray = avps.toArray(avpArray);

			AccountingRequest acr = messageFactory.createAccountingRequest(avpArray);

			VendorSpecificApplicationIdAvp vsaidAvp = acr.getVendorSpecificApplicationId();
			assertNotNull("Vendor-Specific-Application-Id should be present in message.", vsaidAvp);

			// hack: vendor id is Avp vendor-id value, need to do this way.
			long msgAppVendorId = vsaidAvp.getVendorIdsAvp()[0];
			assertTrue("Vendor-Specific-Application-Id / Vendor-Id should be [" + vendorId + "] it is [" + msgAppVendorId + "]", vendorId == msgAppVendorId);

			long msgAppId = vsaidAvp.getAcctApplicationId();
			assertTrue("Vendor-Specific-Application-Id / Acct-Application-Id should be [" + acctApplicationId + "] it is [" + msgAppId + "]", acctApplicationId == msgAppId);

		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testMessageCreationApplicationIdAvp() {
		// Relates to Issue #1555 (http://code.google.com/p/mobicents/issues/detail?id=1555)
		List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

		try {
			long acctApplicationId = 23121L;

			DiameterAvp avpAcctApplicationId = avpFactory.createAvp(DiameterAvpCodes.ACCT_APPLICATION_ID, acctApplicationId);

			avps.add(avpAcctApplicationId);

			DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
			avpArray = avps.toArray(avpArray);

			AccountingRequest acr = messageFactory.createAccountingRequest(avpArray);

			VendorSpecificApplicationIdAvp vsaidAvp = acr.getVendorSpecificApplicationId();
			assertNull("Vendor-Specific-Application-Id should not be present in message.", vsaidAvp);

			long msgAppId = acr.getAcctApplicationId();
			assertTrue("Acct-Application-Id should be [" + acctApplicationId + "] it is [" + msgAppId + "]", acctApplicationId == msgAppId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Class representing the Diameter Configuration
	 */
	public static class MyConfiguration extends EmptyConfiguration {

		public MyConfiguration() {
			super();

			add(Assembler, Assembler.defValue());
			add(OwnDiameterURI, clientURI);
			add(OwnRealm, realmName);
			add(OwnVendorID, 193L);
			// Set Ericsson SDK feature
			// add(UseUriAsFqdn, true);
			// Set Common Applications
			add(ApplicationId,
			// AppId 1
					getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L));
			// Set peer table
			add(PeerTable,
			// Peer 1
					getInstance().add(PeerRating, 1).add(PeerName, serverURI));
			// Set realm table
			add(RealmTable,
					// Realm 1
					getInstance().add(
							RealmEntry,
							getInstance().add(RealmName, realmName).add(ApplicationId, getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L))
									.add(RealmHosts, clientHost + ", " + serverHost).add(RealmLocalAction, "LOCAL").add(RealmEntryIsDynamic, false).add(RealmEntryExpTime, 1000L)));
		}
	}

	// Util methods for several applications

	public static int getApplicationIdAvpsHash(DiameterMessage msg) {
		int result = NO_APP_ID_AVPS;
		int vsaiResult = 1;
		for (DiameterAvp avp : msg.getAvps()) {
			if (avp.getCode() == Avp.AUTH_APPLICATION_ID) {
				result += AUTH_APP_ID;
			}
			else if (avp.getCode() == Avp.ACCT_APPLICATION_ID) {
				result += ACCT_APP_ID;
			}
			if (avp.getCode() == Avp.VENDOR_SPECIFIC_APPLICATION_ID) {
				result += VENDOR_SPECIFIC_APP_ID;
				GroupedAvp vsai = (GroupedAvp) avp;
				for (DiameterAvp subAvp : vsai.getExtensionAvps()) {
					if (subAvp.getCode() == Avp.AUTH_APPLICATION_ID) {
						vsaiResult *= AUTH_APP_ID;
					}
					else if (subAvp.getCode() == Avp.ACCT_APPLICATION_ID) {
						vsaiResult *= ACCT_APP_ID;
					}
				}
			}
		}

		if (result == VENDOR_SPECIFIC_APP_ID) {
			result *= vsaiResult;
		}

		return result;
	}

	public static void checkCorrectApplicationIdAVPs(boolean isVendor, boolean isAuth, boolean isAcct, DiameterMessage message) {
		int appIdCode = getApplicationIdAvpsHash(message);

		assertFalse("Invalid Application ID AVPs found (" + appIdCode + ")", appIdCode == AUTH_AND_ACCT_APP_ID || appIdCode == AUTH_AND_VENDOR_SPECIFIC_APP_ID
				|| appIdCode == ACCT_AND_VENDOR_SPECIFIC_APP_ID || appIdCode == AUTH_ACCT_AND_VENDOR_SPECIFIC_APP_ID || appIdCode == VENDOR_SPECIFIC_APP_ID_AUTH_AND_ACCT);

		if (!isVendor) {
			if (isAuth) {
				assertEquals("Message should have Auth-Application-Id AVP. Application ID AVPs Hashcode mismatch... ", AUTH_APP_ID, appIdCode);
			}
			else if (isAcct) {
				assertEquals("Message should have Acct-Application-Id AVP. Application ID AVPs Hashcode mismatch... ", ACCT_APP_ID, appIdCode);
			}
		}
		else {
			if (isAuth) {
				assertEquals("Message should have Auth-Application-Id AVP. Application ID AVPs Hashcode mismatch... ", VENDOR_SPECIFIC_APP_ID_AUTH, appIdCode);
			}
			else if (isAcct) {
				assertEquals("Message should have Acct-Application-Id AVP. Application ID AVPs Hashcode mismatch... ", VENDOR_SPECIFIC_APP_ID_ACCT, appIdCode);
			}
		}

	}

}
