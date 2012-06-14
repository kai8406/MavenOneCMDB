/*
 * Lokomo OneCMDB - An Open Source Software for Configuration
 * Management of Datacenter Resources
 *
 * Copyright (C) 2006 Lokomo Systems AB
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * Lokomo Systems AB can be contacted via e-mail: info@lokomo.com or via
 * paper mail: Lokomo Systems AB, Svärdvägen 27, SE-182 33
 * Danderyd, Sweden.
 *
 */
package org.onecmdb.core.tests.policy;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiService;
import org.onecmdb.core.ICmdbTransaction;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IPolicyService;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.ISession;
import org.onecmdb.core.ITicket;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.internal.policy.AttributePolicy;
import org.onecmdb.core.internal.policy.EventPolicy;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OnecmdbTestUtils;
import org.onecmdb.core.tests.performance.TestWritePerformance;
import org.onecmdb.core.utils.RfcGenerator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestPolicy extends AbstractOneCmdbTestCase {
	
	public static void main(String argv[]) {
		junit.textui.TestRunner.run(TestWritePerformance.class);		
	}


	private ICi ciEventPolicyRoot;
	private ICi ciCiPolicyRoot;
	private ICi ciAttributePolicyRoot;
	private ICi ciPolicyTriggerRoot;
	private ICi ciPolicyRoot;
	
	/**
	 * Overide to get hold of the policy CI's used for many
	 * tests. 
	 */
	@Override
	public void setUp() {
		super.setUp();
		
		IPolicyService policySvc = (IPolicyService) session
		.getService(IPolicyService.class);
	
		ciPolicyRoot = policySvc.getRootPolicy();
		assertNotNull(ciPolicyRoot);
		
		ciPolicyTriggerRoot = policySvc.getRootPolicyTrigger();
		assertNotNull(ciPolicyTriggerRoot);
	
		ciAttributePolicyRoot = policySvc.getRootAttributePolicy();
		assertNotNull(ciAttributePolicyRoot);
		
		ciCiPolicyRoot = policySvc.getRootCiPolicy();
		assertNotNull(ciCiPolicyRoot);
		
		ciEventPolicyRoot = policySvc.getRootEventPolicy();
		assertNotNull(ciEventPolicyRoot);
	}
	
	public void testDefaultPolicy() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		
		ICi tp1 = utils.createTemplate(ciRoot, "TP1");
		
		// Validate that we have attribute icon.
		List<IAttribute> attributes = tp1.getAttributesWithAlias("icon");
		Assert.assertEquals(1, attributes.size());
		
		// Modify the tp1 to an instance.
		ICi ip1 = utils.modifyTemplate(tp1, false);
		attributes = tp1.getAttributesWithAlias("icon");
		Assert.assertEquals(1, attributes.size());
						
		// Should not be able to create offsprings...
		utils.createInstance(ip1, "test", true);
		
		
		tp1 = utils.modifyTemplate(tp1, true);
		attributes = tp1.getAttributesWithAlias("icon");
		Assert.assertEquals(1, attributes.size());
		
		// Should not be able to create offsprings...
		utils.createInstance(tp1, "test");
		
		// Should not be ok.
		utils.modifyTemplate(tp1, false, true);
			
		
		
	}

	public void testAttributePolicy() {
		
		ICi tp1 = getTestUtil().createTemplate(ciRoot, "TP1");
		getTestUtil().newAttribute(tp1, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		
		ICi policyTrigger = getTestUtil().createInstance(ciPolicyTriggerRoot, "Policy1");
		
		// Connect the trigger to the template.
		getTestUtil().setValue(policyTrigger, IPolicyService.POLICY_FOR_ATT, tp1, false);
		
		// Create the CiPolicy
		// Create the AttributePolicy
		ICi ciAttrPolicy = getTestUtil().createInstance(ciAttributePolicyRoot, "I1Attribute");
		AttributePolicy attrPolicy = new AttributePolicy(ciAttrPolicy);
		attrPolicy.setAllowValueChange(false);
		
		RfcGenerator gen = new RfcGenerator();
		List<IRFC> rfcs = gen.generateRfc(ciAttrPolicy, attrPolicy);
		
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(this.session);
		tx.setRfc(rfcs);
		ITicket ticket = ccb.submitTx(tx);
		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		
		// Addit to the trigger.
		getTestUtil().setValue(policyTrigger, "attributePolicy", ciAttrPolicy, false);
		
		
		IPolicyService pSvc = (IPolicyService)session.getService(IPolicyService.class);
	
		ICi i1 = getTestUtil().createInstance(tp1, "I1");
		
		// Should be ok.
		getTestUtil().setValue(i1, "a1", SimpleTypeFactory.STRING.parseString("Testing"), false);
	
		// Apply the policy
		pSvc.updatePolicyTrigger(policyTrigger);
		
		// Should throw assertion.
		getTestUtil().setValue(i1, "a1", SimpleTypeFactory.STRING.parseString("Testing"), true);
		
		
	}

	
	public void testEventPolicy() {
		
		ICi tp1 = getTestUtil().createTemplate(ciRoot, "TP1");
		getTestUtil().newAttribute(tp1, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		
		ICi policyTrigger = getTestUtil().createInstance(ciPolicyTriggerRoot, "Policy1");
		
		// Connect the trigger to the template.
		getTestUtil().setValue(policyTrigger, IPolicyService.POLICY_FOR_ATT, tp1, false);
		
		// Create the CiPolicy
		// Create the AttributePolicy
		// Create the EventPolicy
		ICi ciEventPolicy = getTestUtil().createInstance(ciEventPolicyRoot, "I1Event");
		EventPolicy eventPolicy = new EventPolicy(ciEventPolicy);
		
		eventPolicy.setOnRfc(RFCModifyAttributeValue.class.getName());
		eventPolicy.setCallbackClass(EventPolicyCallback.class.getName());
		eventPolicy.setAttributePattern(".*.");
		
		RfcGenerator gen = new RfcGenerator();
		List<IRFC> rfcs = gen.generateRfc(ciEventPolicy, eventPolicy);
		
		ICcb ccb = (ICcb) session.getService(ICcb.class);
		ICmdbTransaction tx = ccb.getTx(this.session);
		tx.setRfc(rfcs);
		ITicket ticket = ccb.submitTx(tx);
		IRfcResult result = ccb.waitForTx(ticket);
		Assert.assertEquals(false, result.isRejected());

		
		// Addit to the trigger.
		getTestUtil().setValue(policyTrigger, "eventPolicy", eventPolicy, false);
		
		
		IPolicyService pSvc = (IPolicyService)session.getService(IPolicyService.class);
		
		pSvc.updatePolicyTrigger(policyTrigger);
		
		ICi i1 = getTestUtil().createInstance(tp1, "I1");
		
		getTestUtil().setValue(i1, "a1", SimpleTypeFactory.STRING.parseString("Testing1"), false);
		getTestUtil().setValue(i1, "a1", SimpleTypeFactory.STRING.parseString(EventPolicyCallback.VALUE_NOT_ALLOWED), true);
	}

}
