/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.mobicents.slee.resource.diameter.s6a.tests.factories;

import static org.junit.Assert.*;
import net.java.slee.resource.diameter.s6a.S6aAVPFactory;
import net.java.slee.resource.diameter.s6a.S6aMessageFactory;
import net.java.slee.resource.diameter.s6a.events.AuthenticationInformationAnswer;
import net.java.slee.resource.diameter.s6a.events.AuthenticationInformationRequest;
import net.java.slee.resource.diameter.s6a.events.CancelLocationAnswer;
import net.java.slee.resource.diameter.s6a.events.CancelLocationRequest;
import net.java.slee.resource.diameter.s6a.events.DeleteSubscriberDataAnswer;
import net.java.slee.resource.diameter.s6a.events.DeleteSubscriberDataRequest;
import net.java.slee.resource.diameter.s6a.events.InsertSubscriberDataAnswer;
import net.java.slee.resource.diameter.s6a.events.InsertSubscriberDataRequest;
import net.java.slee.resource.diameter.s6a.events.NotifyAnswer;
import net.java.slee.resource.diameter.s6a.events.NotifyRequest;
import net.java.slee.resource.diameter.s6a.events.PurgeUEAnswer;
import net.java.slee.resource.diameter.s6a.events.PurgeUERequest;
import net.java.slee.resource.diameter.s6a.events.ResetAnswer;
import net.java.slee.resource.diameter.s6a.events.ResetRequest;
import net.java.slee.resource.diameter.s6a.events.UpdateLocationAnswer;
import net.java.slee.resource.diameter.s6a.events.UpdateLocationRequest;
import net.java.slee.resource.diameter.s6a.events.avp.*;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Stack;
import org.jdiameter.api.s6a.ClientS6aSession;
import org.jdiameter.api.s6a.ServerS6aSession;
import org.jdiameter.common.impl.app.s6a.S6aSessionFactoryImpl;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.tests.factories.BaseFactoriesTest;
import org.mobicents.slee.resource.diameter.base.tests.factories.BaseFactoriesTest.MyConfiguration;
import org.mobicents.slee.resource.diameter.s6a.S6aAVPFactoryImpl;
import org.mobicents.slee.resource.diameter.s6a.S6aClientSessionImpl;
import org.mobicents.slee.resource.diameter.s6a.S6aMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.s6a.S6aServerSessionImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.*;

/**
 * Test class for JAIN SLEE Diameter S6a RA Message and AVP Factories
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class S6aFactoriesTest {

	private static S6aMessageFactory s6aMessageFactory;
	private static S6aAVPFactory s6aAvpFactory;

	private static Stack stack;

	static {
		stack = new org.jdiameter.client.impl.StackImpl();
		try {
			stack.init(new MyConfiguration());
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to initialize the stack.");
		}

		s6aAvpFactory = new S6aAVPFactoryImpl(new DiameterAvpFactoryImpl());
		try {
			s6aMessageFactory = new S6aMessageFactoryImpl(stack);

			S6aSessionFactoryImpl sf = new S6aSessionFactoryImpl(stack.getSessionFactory());
			ApplicationId s6aAppId = ApplicationId.createByAuthAppId(DiameterS6aAvpCodes.S6A_VENDOR_ID, DiameterS6aAvpCodes.S6A_AUTH_APP_ID);
			org.jdiameter.server.impl.app.s6a.S6aServerSessionImpl stackServerSession = (org.jdiameter.server.impl.app.s6a.S6aServerSessionImpl) sf.getNewSession("123",
					ServerS6aSession.class, s6aAppId, new Object[0]);
			org.jdiameter.client.impl.app.s6a.S6aClientSessionImpl stackClientSession = (org.jdiameter.client.impl.app.s6a.S6aClientSessionImpl) sf.getNewSession("321",
					ClientS6aSession.class, s6aAppId, new Object[0]);
			serverSession = new S6aServerSessionImpl(s6aMessageFactory, s6aAvpFactory, stackServerSession, stackServerSession, null, null, stack);
			clientSession = new S6aClientSessionImpl(s6aMessageFactory, s6aAvpFactory, stackClientSession, stackClientSession, null, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			AvpDictionary.INSTANCE.parseDictionary(S6aFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to parse dictionary file.");
		}
	}

	private static S6aServerSessionImpl serverSession;
	private static S6aClientSessionImpl clientSession;

	@Test
	public void isRequestULR() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();

		assertTrue("Request Flag in Update-Location-Request is not set.", ulr.getHeader().isRequest());
	}

	@Test
	public void isProxiableULR() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		assertTrue("The 'P' bit is not set by default in Update-Location-Request it should.", ulr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersULR() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(ulr, UpdateLocationRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerULA() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		serverSession.fetchSessionData(ulr);
		UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();

		assertFalse("Request Flag in Update-Location-Answer is set.", ula.getHeader().isRequest());
	}

	@Test
	public void testGettersAndSettersULA() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		serverSession.fetchSessionData(ulr);
		UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(ula, UpdateLocationAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	// Test for http://code.google.com/p/mobicents/issues/detail?id=3096
	public void testGroupedChildAVPsULA() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		serverSession.fetchSessionData(ulr);
		UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();
		SubscriptionDataAvp sd = s6aAvpFactory.createSubscriptionData();

		// AMBR
		AMBRAvp ambr = s6aAvpFactory.createAMBR();
		ambr.setMaxRequestedBandwidthDL(12L);
		ambr.setMaxRequestedBandwidthUL(6L);
		sd.setAMBR(ambr);

		// APNConfigurationProfile
		APNConfigurationProfileAvp apnCP = s6aAvpFactory.createAPNConfigurationProfile();
		apnCP.setAllAPNConfigurationsIncludedIndicator(AllAPNConfigurationsIncludedIndicator.ALL_APN_CONFIGURATIONS_INCLUDED);
		APNConfigurationAvp apnC = s6aAvpFactory.createAPNConfiguration();
		apnC.setContextIdentifier(123L);
		apnC.setPDNType(PDNType.IPv4_OR_IPv6);
		apnC.setServiceSelection("...");
		apnCP.setAPNConfiguration(apnC);
		sd.setAPNConfigurationProfile(apnCP);

		ula.setSubscriptionData(sd);
	}

	@Test
	public void hasDestinationHostULA() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		serverSession.fetchSessionData(ulr);
		UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", ula.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmULA() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		serverSession.fetchSessionData(ulr);
		UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", ula.getDestinationRealm());
	}

	@Test
	public void isProxiableCopiedULA() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		serverSession.fetchSessionData(ulr);
		UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();
		assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", ulr.getHeader().isProxiable(), ula.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) ulr).getGenericData().setProxiable(!ulr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Update-Location-Request, it should.", ulr.getHeader().isProxiable() != ula.getHeader().isProxiable());
		serverSession.fetchSessionData(ulr);

		ula = serverSession.createUpdateLocationAnswer();
		assertEquals("The 'P' bit is not copied from request in Update-Location-Answer, it should. [RFC3588/6.2]", ulr.getHeader().isProxiable(), ula.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetULA() throws Exception {
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		((DiameterMessageImpl) ulr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Update-Location-Request", ulr.getHeader().isPotentiallyRetransmitted());

		serverSession.fetchSessionData(ulr);
		UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();
		assertFalse("The 'T' flag should not be set in Update-Location-Answer", ula.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void isRequestAIR() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();

		assertTrue("Request Flag in Authentication-Information-Request is not set.", air.getHeader().isRequest());
	}

	@Test
	public void isProxiableAIR() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
		assertTrue("The 'P' bit is not set by default in Authentication-Information-Request it should.", air.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersAIR() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(air, AuthenticationInformationRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerAIA() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
		serverSession.fetchSessionData(air);
		AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();

		assertFalse("Request Flag in Authentication-Information-Answer is set.", aia.getHeader().isRequest());
	}

	@Test
	public void testGettersAndSettersAIA() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
		serverSession.fetchSessionData(air);
		AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(aia, AuthenticationInformationAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostAIA() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
		serverSession.fetchSessionData(air);
		AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aia.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmAIA() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
		serverSession.fetchSessionData(air);
		AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aia.getDestinationRealm());
	}

	@Test
	public void isProxiableCopiedAIA() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
		serverSession.fetchSessionData(air);
		AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();
		assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", air.getHeader().isProxiable(), aia.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) air).getGenericData().setProxiable(!air.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Authentication-Information-Request, it should.", air.getHeader().isProxiable() != aia.getHeader().isProxiable());
		serverSession.fetchSessionData(air);

		aia = serverSession.createAuthenticationInformationAnswer();
		assertEquals("The 'P' bit is not copied from request in Authentication-Information-Answer, it should. [RFC3588/6.2]", air.getHeader().isProxiable(), aia.getHeader()
				.isProxiable());
	}

	@Test
	public void hasTFlagSetAIA() throws Exception {
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
		((DiameterMessageImpl) air).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Authentication-Information-Request", air.getHeader().isPotentiallyRetransmitted());

		serverSession.fetchSessionData(air);
		AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();
		assertFalse("The 'T' flag should not be set in Authentication-Information-Answer", aia.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void isRequestCLR() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();

		assertTrue("Request Flag in Cancel-Location-Request is not set.", clr.getHeader().isRequest());
	}

	@Test
	public void isProxiableCLR() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
		assertTrue("The 'P' bit is not set by default in Cancel-Location-Request it should.", clr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersCLR() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(clr, CancelLocationRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerCLA() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
		clientSession.fetchSessionData(clr);
		CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();

		assertFalse("Request Flag in Cancel-Location-Answer is set.", cla.getHeader().isRequest());
	}

	@Test
	public void testGettersAndSettersCLA() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
		clientSession.fetchSessionData(clr);
		CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(cla, CancelLocationAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostCLA() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
		clientSession.fetchSessionData(clr);
		CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cla.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmCLA() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
		clientSession.fetchSessionData(clr);
		CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cla.getDestinationRealm());
	}

	@Test
	public void isProxiableCopiedCLA() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
		clientSession.fetchSessionData(clr);
		CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();
		assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", clr.getHeader().isProxiable(), cla.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) clr).getGenericData().setProxiable(!clr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Cancel-Location-Request, it should.", clr.getHeader().isProxiable() != cla.getHeader().isProxiable());
		clientSession.fetchSessionData(clr);

		cla = clientSession.createCancelLocationAnswer();
		assertEquals("The 'P' bit is not copied from request in Cancel-Location-Answer, it should. [RFC3588/6.2]", clr.getHeader().isProxiable(), cla.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetCLA() throws Exception {
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
		((DiameterMessageImpl) clr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Cancel-Location-Request", clr.getHeader().isPotentiallyRetransmitted());

		clientSession.fetchSessionData(clr);
		CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();
		assertFalse("The 'T' flag should not be set in Cancel-Location-Answer", cla.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void isRequestIDR() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();

		assertTrue("Request Flag in Insert-Subscriber-Data-Request is not set.", idr.getHeader().isRequest());
	}

	@Test
	public void isProxiableIDR() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
		assertTrue("The 'P' bit is not set by default in Insert-Subscriber-Data-Request it should.", idr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersIDR() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(idr, InsertSubscriberDataRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerIDA() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
		clientSession.fetchSessionData(idr);
		InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();

		assertFalse("Request Flag in Insert-Subscriber-Data-Answer is set.", ida.getHeader().isRequest());
	}

	@Test
	public void testGettersAndSettersIDA() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
		clientSession.fetchSessionData(idr);
		InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(ida, InsertSubscriberDataAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostIDA() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
		clientSession.fetchSessionData(idr);
		InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", ida.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmIDA() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
		clientSession.fetchSessionData(idr);
		InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", ida.getDestinationRealm());
	}

	@Test
	public void isProxiableCopiedIDA() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
		clientSession.fetchSessionData(idr);
		InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();
		assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", idr.getHeader().isProxiable(), ida.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) idr).getGenericData().setProxiable(!idr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Insert-Subscriber-Data-Request, it should.", idr.getHeader().isProxiable() != ida.getHeader().isProxiable());
		clientSession.fetchSessionData(idr);

		ida = clientSession.createInsertSubscriberDataAnswer();
		assertEquals("The 'P' bit is not copied from request in Insert-Subscriber-Data-Answer, it should. [RFC3588/6.2]", idr.getHeader().isProxiable(), ida.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetIDA() throws Exception {
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
		((DiameterMessageImpl) idr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Insert-Subscriber-Data-Request", idr.getHeader().isPotentiallyRetransmitted());

		clientSession.fetchSessionData(idr);
		InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();
		assertFalse("The 'T' flag should not be set in Insert-Subscriber-Data-Answer", ida.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void isRequestDSR() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();

		assertTrue("Request Flag in Delete-Subscriber-Data-Request is not set.", dsr.getHeader().isRequest());
	}

	@Test
	public void isProxiableDSR() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
		assertTrue("The 'P' bit is not set by default in Delete-Subscriber-Data-Request it should.", dsr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersDSR() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(dsr, DeleteSubscriberDataRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerDSA() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
		clientSession.fetchSessionData(dsr);
		DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();

		assertFalse("Request Flag in Delete-Subscriber-Data-Answer is set.", dsa.getHeader().isRequest());
	}

	@Test
	public void testGettersAndSettersDSA() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
		clientSession.fetchSessionData(dsr);
		DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(dsa, DeleteSubscriberDataAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostDSA() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
		clientSession.fetchSessionData(dsr);
		DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dsa.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmDSA() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
		clientSession.fetchSessionData(dsr);
		DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dsa.getDestinationRealm());
	}

	@Test
	public void isProxiableCopiedDSA() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
		clientSession.fetchSessionData(dsr);
		DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();
		assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", dsr.getHeader().isProxiable(), dsa.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) dsr).getGenericData().setProxiable(!dsr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Delete-Subscriber-Data-Request, it should.", dsr.getHeader().isProxiable() != dsa.getHeader().isProxiable());
		clientSession.fetchSessionData(dsr);

		dsa = clientSession.createDeleteSubscriberDataAnswer();
		assertEquals("The 'P' bit is not copied from request in Delete-Subscriber-Data-Answer, it should. [RFC3588/6.2]", dsr.getHeader().isProxiable(), dsa.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetDSA() throws Exception {
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
		((DiameterMessageImpl) dsr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Delete-Subscriber-Data-Request", dsr.getHeader().isPotentiallyRetransmitted());

		clientSession.fetchSessionData(dsr);
		DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();
		assertFalse("The 'T' flag should not be set in Delete-Subscriber-Data-Answer", dsa.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void isRequestPUR() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();

		assertTrue("Request Flag in Purge-UE-Request is not set.", pur.getHeader().isRequest());
	}

	@Test
	public void isProxiablePUR() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
		assertTrue("The 'P' bit is not set by default in Purge-UE-Request it should.", pur.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersPUR() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(pur, PurgeUERequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerPUA() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
		serverSession.fetchSessionData(pur);
		PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();

		assertFalse("Request Flag in Purge-UE-Answer is set.", pua.getHeader().isRequest());
	}

	@Test
	public void testGettersAndSettersPUA() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
		serverSession.fetchSessionData(pur);
		PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(pua, PurgeUEAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostPUA() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
		serverSession.fetchSessionData(pur);
		PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pua.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmPUA() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
		serverSession.fetchSessionData(pur);
		PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pua.getDestinationRealm());
	}

	@Test
	public void isProxiableCopiedPUA() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
		serverSession.fetchSessionData(pur);
		PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();
		assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", pur.getHeader().isProxiable(), pua.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) pur).getGenericData().setProxiable(!pur.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Purge-UE-Request, it should.", pur.getHeader().isProxiable() != pua.getHeader().isProxiable());
		serverSession.fetchSessionData(pur);

		pua = serverSession.createPurgeUEAnswer();
		assertEquals("The 'P' bit is not copied from request in Purge-UE-Answer, it should. [RFC3588/6.2]", pur.getHeader().isProxiable(), pua.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetPUA() throws Exception {
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
		((DiameterMessageImpl) pur).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Purge-UE-Request", pur.getHeader().isPotentiallyRetransmitted());

		serverSession.fetchSessionData(pur);
		PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();
		assertFalse("The 'T' flag should not be set in Purge-UE-Answer", pua.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void isRequestRSR() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();

		assertTrue("Request Flag in Reset-Request is not set.", rsr.getHeader().isRequest());
	}

	@Test
	public void isProxiableRSR() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();
		assertTrue("The 'P' bit is not set by default in Reset-Request it should.", rsr.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersRSR() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(rsr, ResetRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerRSA() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();
		clientSession.fetchSessionData(rsr);
		ResetAnswer rsa = clientSession.createResetAnswer();

		assertFalse("Request Flag in Reset-Answer is set.", rsa.getHeader().isRequest());
	}

	@Test
	public void testGettersAndSettersRSA() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();
		clientSession.fetchSessionData(rsr);
		ResetAnswer rsa = clientSession.createResetAnswer();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(rsa, ResetAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostRSA() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();
		clientSession.fetchSessionData(rsr);
		ResetAnswer rsa = clientSession.createResetAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", rsa.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmRSA() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();
		clientSession.fetchSessionData(rsr);
		ResetAnswer rsa = clientSession.createResetAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", rsa.getDestinationRealm());
	}

	@Test
	public void isProxiableCopiedRSA() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();
		clientSession.fetchSessionData(rsr);
		ResetAnswer rsa = clientSession.createResetAnswer();
		assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", rsr.getHeader().isProxiable(), rsa.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) rsr).getGenericData().setProxiable(!rsr.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Reset-Request, it should.", rsr.getHeader().isProxiable() != rsa.getHeader().isProxiable());
		clientSession.fetchSessionData(rsr);

		rsa = clientSession.createResetAnswer();
		assertEquals("The 'P' bit is not copied from request in Reset-Answer, it should. [RFC3588/6.2]", rsr.getHeader().isProxiable(), rsa.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetRSA() throws Exception {
		ResetRequest rsr = s6aMessageFactory.createResetRequest();
		((DiameterMessageImpl) rsr).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Reset-Request", rsr.getHeader().isPotentiallyRetransmitted());

		clientSession.fetchSessionData(rsr);
		ResetAnswer rsa = clientSession.createResetAnswer();
		assertFalse("The 'T' flag should not be set in Reset-Answer", rsa.getHeader().isPotentiallyRetransmitted());
	}

	@Test
	public void isRequestNOR() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();

		assertTrue("Request Flag in Notify-Request is not set.", nor.getHeader().isRequest());
	}

	@Test
	public void isProxiableNOR() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
		assertTrue("The 'P' bit is not set by default in Notify-Request it should.", nor.getHeader().isProxiable());
	}

	@Test
	public void testGettersAndSettersNOR() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(nor, NotifyRequest.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void isAnswerNOA() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
		serverSession.fetchSessionData(nor);
		NotifyAnswer noa = serverSession.createNotifyAnswer();

		assertFalse("Request Flag in Notify-Answer is set.", noa.getHeader().isRequest());
	}

	@Test
	public void testGettersAndSettersNOA() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
		serverSession.fetchSessionData(nor);
		NotifyAnswer noa = serverSession.createNotifyAnswer();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(noa, NotifyAnswer.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void hasDestinationHostNOA() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
		serverSession.fetchSessionData(nor);
		NotifyAnswer noa = serverSession.createNotifyAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", noa.getDestinationHost());
	}

	@Test
	public void hasDestinationRealmNOA() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
		serverSession.fetchSessionData(nor);
		NotifyAnswer noa = serverSession.createNotifyAnswer();

		assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", noa.getDestinationRealm());
	}

	@Test
	public void isProxiableCopiedNOA() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
		serverSession.fetchSessionData(nor);
		NotifyAnswer noa = serverSession.createNotifyAnswer();
		assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", nor.getHeader().isProxiable(), noa.getHeader().isProxiable());

		// Reverse 'P' bit ...
		((DiameterMessageImpl) nor).getGenericData().setProxiable(!nor.getHeader().isProxiable());
		assertTrue("The 'P' bit was not modified in Notify-Request, it should.", nor.getHeader().isProxiable() != noa.getHeader().isProxiable());
		serverSession.fetchSessionData(nor);

		noa = serverSession.createNotifyAnswer();
		assertEquals("The 'P' bit is not copied from request in Notify-Answer, it should. [RFC3588/6.2]", nor.getHeader().isProxiable(), noa.getHeader().isProxiable());
	}

	@Test
	public void hasTFlagSetNOA() throws Exception {
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
		((DiameterMessageImpl) nor).getGenericData().setReTransmitted(true);

		assertTrue("The 'T' flag should be set in Notify-Request", nor.getHeader().isPotentiallyRetransmitted());

		serverSession.fetchSessionData(nor);
		NotifyAnswer noa = serverSession.createNotifyAnswer();
		assertFalse("The 'T' flag should not be set in Notify-Answer", noa.getHeader().isPotentiallyRetransmitted());
	}

    @Test
    public void testGettersAndSettersCSGSubscriptionData() throws Exception {
        CSGSubscriptionDataAvp avp = s6aAvpFactory.createCSGSubscriptionData();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, CSGSubscriptionDataAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersCallBarringInforList() throws Exception {
        CallBarringInfoAvp avp = s6aAvpFactory.createCallBarringInforList();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, CallBarringInfoAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersExternalClient() throws Exception {
        ExternalClientAvp avp = s6aAvpFactory.createExternalClient();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, ExternalClientAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersGERANVector() throws Exception {
        GERANVectorAvp avp = s6aAvpFactory.createGERANVector();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, GERANVectorAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersGPRSSubscriptionData() throws Exception {
        GPRSSubscriptionDataAvp avp = s6aAvpFactory.createGPRSSubscriptionData();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, GPRSSubscriptionDataAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersLCSInfo() throws Exception {
        LCSInfoAvp avp = s6aAvpFactory.createLCSInfo();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, LCSInfoAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersMOLR() throws Exception {
        MOLRAvp avp = s6aAvpFactory.createMOLR();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, MOLRAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersPDPContext() throws Exception {
        PDPContextAvp avp = s6aAvpFactory.createPDPContext();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, PDPContextAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersServiceType() throws Exception {
        ServiceTypeAvp avp = s6aAvpFactory.createServiceType();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, ServiceTypeAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersTeleserviceList() throws Exception {
        TeleserviceListAvp avp = s6aAvpFactory.createTeleserviceList();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, TeleserviceListAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersTraceData() throws Exception {
        TraceDataAvp avp = s6aAvpFactory.createTraceData();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, TraceDataAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersUTRANVector() throws Exception {
        UTRANVectorAvp avp = s6aAvpFactory.createUTRANVector();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, UTRANVectorAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersLCSPrivacyException() throws Exception {
        LCSPrivacyExceptionAvp avp = s6aAvpFactory.createLCSPrivacyException();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, LCSPrivacyExceptionAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
	public void testGettersAndSettersActiveAPN() throws Exception {
		ActiveAPNAvp avp = s6aAvpFactory.createActiveAPN();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, ActiveAPNAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersAMBR() throws Exception {
		AMBRAvp avp = s6aAvpFactory.createAMBR();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, AMBRAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersAPNConfiguration() throws Exception {
		APNConfigurationAvp avp = s6aAvpFactory.createAPNConfiguration();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, APNConfigurationAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersAPNConfigurationProfile() throws Exception {
		APNConfigurationProfileAvp avp = s6aAvpFactory.createAPNConfigurationProfile();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, APNConfigurationProfileAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersAllocationRetentionPriority() throws Exception {
		AllocationRetentionPriorityAvp avp = s6aAvpFactory.createAllocationRetentionPriority();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, AllocationRetentionPriorityAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersAuthenticationInfo() throws Exception {
		AuthenticationInfoAvp avp = s6aAvpFactory.createAuthenticationInfo();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, AuthenticationInfoAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersEPSLocationInformation() throws Exception {
		EPSLocationInformationAvp avp = s6aAvpFactory.createEPSLocationInformation();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, EPSLocationInformationAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersEPSSubscribedQoSProfile() throws Exception {
		EPSSubscribedQoSProfileAvp avp = s6aAvpFactory.createEPSSubscribedQoSProfile();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, EPSSubscribedQoSProfileAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersEPSUserState() throws Exception {
		EPSUserStateAvp avp = s6aAvpFactory.createEPSUserState();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, EPSUserStateAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersEUTRANVector() throws Exception {
		EUTRANVectorAvp avp = s6aAvpFactory.createEUTRANVector();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, EUTRANVectorAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersMIP6AgentInfo() throws Exception {
		MIP6AgentInfoAvp avp = s6aAvpFactory.createMIP6AgentInfo();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, MIP6AgentInfoAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersMIPHomeAgentHost() throws Exception {
		MIPHomeAgentHostAvp avp = s6aAvpFactory.createMIPHomeAgentHost();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, MIPHomeAgentHostAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersMMELocationInformation() throws Exception {
		MMELocationInformationAvp avp = s6aAvpFactory.createMMELocationInformation();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, MMELocationInformationAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersMMEUserState() throws Exception {
		MMEUserStateAvp avp = s6aAvpFactory.createMMEUserState();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, MMEUserStateAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersRequestedEUTRANAuthenticationInfo() throws Exception {
		RequestedEUTRANAuthenticationInfoAvp avp = s6aAvpFactory.createRequestedEUTRANAuthenticationInfo();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, RequestedEUTRANAuthenticationInfoAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersRequestedUTRANGERANAuthenticationInfo() throws Exception {
		RequestedUTRANGERANAuthenticationInfoAvp avp = s6aAvpFactory.createRequestedUTRANGERANAuthenticationInfo();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, RequestedUTRANGERANAuthenticationInfoAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersSGSNLocationInformation() throws Exception {
		SGSNLocationInformationAvp avp = s6aAvpFactory.createSGSNLocationInformation();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, SGSNLocationInformationAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersSGSNUserState() throws Exception {
		SGSNUserStateAvp avp = s6aAvpFactory.createSGSNUserState();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, SGSNUserStateAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersSpecificAPNInfo() throws Exception {
		SpecificAPNInfoAvp avp = s6aAvpFactory.createSpecificAPNInfo();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, SpecificAPNInfoAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersSubscriptionData() throws Exception {
		SubscriptionDataAvp avp = s6aAvpFactory.createSubscriptionData();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, SubscriptionDataAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersSupportedFeatures() throws Exception {
		SupportedFeaturesAvp avp = s6aAvpFactory.createSupportedFeatures();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, SupportedFeaturesAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

	@Test
	public void testGettersAndSettersTerminalInformation() throws Exception {
		TerminalInformationAvp avp = s6aAvpFactory.createTerminalInformation();

		int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, TerminalInformationAvpImpl.class);

		assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
	}

    @Test
    public void testGettersAndSettersEquivalentPLMNList() throws Exception {
        EquivalentPLMNListAvp avp = s6aAvpFactory.createEquivalentPLMNList();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, EquivalentPLMNListAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersLocalTimeZone() throws Exception {
        LocalTimeZoneAvp avp = s6aAvpFactory.createLocalTimeZone();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, LocalTimeZoneAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
	public void testMessageFactoryApplicationIdChangeULR() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		UpdateLocationRequest originalULR = s6aMessageFactory.createUpdateLocationRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalULR);

		// now we switch..
		originalULR = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		UpdateLocationRequest changedULR = s6aMessageFactory.createUpdateLocationRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedULR);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testServerSessionApplicationIdChangeULA() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
		serverSession.fetchSessionData(ulr);
		UpdateLocationAnswer originalULA = serverSession.createUpdateLocationAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalULA);

		// now we switch..
		originalULA = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		UpdateLocationAnswer changedULA = serverSession.createUpdateLocationAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedULA);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testMessageFactoryApplicationIdChangeAIR() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		AuthenticationInformationRequest originalAIR = s6aMessageFactory.createAuthenticationInformationRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalAIR);

		// now we switch..
		originalAIR = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		AuthenticationInformationRequest changedAIR = s6aMessageFactory.createAuthenticationInformationRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedAIR);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testServerSessionApplicationIdChangeAIA() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
		serverSession.fetchSessionData(air);
		AuthenticationInformationAnswer originalAIA = serverSession.createAuthenticationInformationAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalAIA);

		// now we switch..
		originalAIA = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		AuthenticationInformationAnswer changedAIA = serverSession.createAuthenticationInformationAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedAIA);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testMessageFactoryApplicationIdChangeCLR() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		CancelLocationRequest originalCLR = s6aMessageFactory.createCancelLocationRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalCLR);

		// now we switch..
		originalCLR = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		CancelLocationRequest changedCLR = s6aMessageFactory.createCancelLocationRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedCLR);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testClientSessionApplicationIdChangeCLA() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
		clientSession.fetchSessionData(clr);
		CancelLocationAnswer originalCLA = clientSession.createCancelLocationAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalCLA);

		// now we switch..
		originalCLA = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		CancelLocationAnswer changedCLA = clientSession.createCancelLocationAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedCLA);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testMessageFactoryApplicationIdChangeIDR() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		InsertSubscriberDataRequest originalIDR = s6aMessageFactory.createInsertSubscriberDataRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalIDR);

		// now we switch..
		originalIDR = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		InsertSubscriberDataRequest changedIDR = s6aMessageFactory.createInsertSubscriberDataRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedIDR);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testClientSessionApplicationIdChangeIDA() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
		clientSession.fetchSessionData(idr);
		InsertSubscriberDataAnswer originalIDA = clientSession.createInsertSubscriberDataAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalIDA);

		// now we switch..
		originalIDA = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		InsertSubscriberDataAnswer changedIDA = clientSession.createInsertSubscriberDataAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedIDA);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testMessageFactoryApplicationIdChangeDSR() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		DeleteSubscriberDataRequest originalDSR = s6aMessageFactory.createDeleteSubscriberDataRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalDSR);

		// now we switch..
		originalDSR = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		DeleteSubscriberDataRequest changedDSR = s6aMessageFactory.createDeleteSubscriberDataRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedDSR);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testClientSessionApplicationIdChangeDSA() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
		clientSession.fetchSessionData(dsr);
		DeleteSubscriberDataAnswer originalDSA = clientSession.createDeleteSubscriberDataAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalDSA);

		// now we switch..
		originalDSA = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		DeleteSubscriberDataAnswer changedDSA = clientSession.createDeleteSubscriberDataAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedDSA);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testMessageFactoryApplicationIdChangePUR() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		PurgeUERequest originalPUR = s6aMessageFactory.createPurgeUERequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalPUR);

		// now we switch..
		originalPUR = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		PurgeUERequest changedPUR = s6aMessageFactory.createPurgeUERequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedPUR);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testServerSessionApplicationIdChangePUA() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
		serverSession.fetchSessionData(pur);
		PurgeUEAnswer originalPUA = serverSession.createPurgeUEAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalPUA);

		// now we switch..
		originalPUA = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		PurgeUEAnswer changedPUA = serverSession.createPurgeUEAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedPUA);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testMessageFactoryApplicationIdChangeRSR() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		ResetRequest originalRSR = s6aMessageFactory.createResetRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalRSR);

		// now we switch..
		originalRSR = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		ResetRequest changedRSR = s6aMessageFactory.createResetRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedRSR);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testClientSessionApplicationIdChangeRSA() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		ResetRequest rsr = s6aMessageFactory.createResetRequest();
		clientSession.fetchSessionData(rsr);
		ResetAnswer originalRSA = clientSession.createResetAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalRSA);

		// now we switch..
		originalRSA = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		ResetAnswer changedRSA = clientSession.createResetAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedRSA);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testMessageFactoryApplicationIdChangeNOR() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		NotifyRequest originalNOR = s6aMessageFactory.createNotifyRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalNOR);

		// now we switch..
		originalNOR = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		NotifyRequest changedNOR = s6aMessageFactory.createNotifyRequest();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedNOR);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

	@Test
	public void testServerSessionApplicationIdChangeNOA() throws Exception {
		long vendor = 10415L;
		ApplicationId originalAppId = ((S6aMessageFactoryImpl) s6aMessageFactory).getApplicationId();

		boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
		boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

		boolean isVendor = originalAppId.getVendorId() != 0L;

		assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

		System.out.println("Default VENDOR-ID for S6a is " + originalAppId.getVendorId());
		// let's create a message and see how it comes...
		NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
		serverSession.fetchSessionData(nor);
		NotifyAnswer originalNOA = serverSession.createNotifyAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalNOA);

		// now we switch..
		originalNOA = null;
		isVendor = !isVendor;
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

		// create a new message and see how it comes...
		NotifyAnswer changedNOA = serverSession.createNotifyAnswer();
		BaseFactoriesTest.checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedNOA);

		// revert back to default
		((S6aMessageFactoryImpl) s6aMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
	}

    @Test
    public void testGettersAndSettersAreaScope() throws Exception {
        AreaScopeAvp avp = s6aAvpFactory.createAreaScopeAvp();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, AreaScopeAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersMDTConfiguration() throws Exception {
        MDTConfigurationAvp avp = s6aAvpFactory.createMDTConfigurationAvp();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, MDTConfigurationAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersVPLMNCSGSubscriptionData() throws Exception {
        VPLMNCSGSubscriptionDataAvp avp = s6aAvpFactory.createVPLMNCSGSubscriptionData();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, VPLMNCSGSubscriptionDataAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersWLANOffloadability() throws Exception {
        WLANoffloadabilityAvp avp = s6aAvpFactory.createWLANoffloadability();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, WLANoffloadabilityAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }

    @Test
    public void testGettersAndSettersUserCSGInformation() throws Exception {
        UserCSGInformationAvp avp = s6aAvpFactory.createUserCSGInformation();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, UserCSGInformationAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }
    @Test
    public void testGettersAndSettersProSeSubscriptionData() throws Exception {
        ProSESubscriptionDataAvp avp = s6aAvpFactory.createProSeSubscriptionData();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, ProSeSubscriptionDataAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }
    @Test
    public void testGettersAndSettersProSeAllowedPLMN() throws Exception {
        ProSeAllowedPLMNAvp avp = s6aAvpFactory.createProSeAllowedPLMN();

        int nFailures = S6aAvpAssistant.INSTANCE.testMethods(avp, ProSeAllowedPLMNAvpImpl.class);

        assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
    }
}
