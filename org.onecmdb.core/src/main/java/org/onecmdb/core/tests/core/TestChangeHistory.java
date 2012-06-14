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
package org.onecmdb.core.tests.core;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.hibernate.dialect.Dialect;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICcb;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiService;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.ccb.RfcQueryCriteria;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OnecmdbTestUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestChangeHistory extends AbstractOneCmdbTestCase {

	
	public void testChangeHistory() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(session);
		
		ICi ci = utils.createTemplate(ciRoot, "TEST");
		
		ICcb ccb = (ICcb)session.getService(ICcb.class);
		List<IRFC> list = ccb.findRFCForCi(ci);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
	}
	
	
	public void testChangeQuerySummery() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(session);
		
		ICi ci = utils.createTemplate(ciRoot, "TEST");
		IAttribute a1 = utils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null, 1, 1);
		IAttribute a2 = utils.newAttribute(ci, "a2", SimpleTypeFactory.STRING, null, 1, 1);
		
		Date startDate = new Date();
		for (int i = 0; i < 6; i++) {
			utils.setValue(a1, SimpleTypeFactory.STRING.parseString("Count:" + i), false);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Delete a2
		utils.destroyCi(a2);

		Date stopDate = new Date();
		RfcQueryCriteria crit = new RfcQueryCriteria();
		crit.setFetchAttributes(true);
		crit.setDescendingOrder(true);
		ICcb ccb = (ICcb)this.session.getService(ICcb.class);
		List<IRFC> rfcs = ccb.queryRFCForCi(ci, crit);
		for (IRFC rfc : rfcs) {
			System.out.println(rfc.getId() + "\t" + rfc.getTs() + "\t" + rfc.getSummary());
		}
		System.out.println("RFCS " + rfcs.size());
	}
	
	public void testChengeQuery() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(session);
		
		ICi ci = utils.createTemplate(ciRoot, "TEST");
		IAttribute a1 = utils.newAttribute(ci, "a1", SimpleTypeFactory.STRING, null, 1, 1);
	
		
		Date startDate = new Date();
		for (int i = 0; i < 6; i++) {
			utils.setValue(a1, SimpleTypeFactory.STRING.parseString("Count:" + i), false);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Date stopDate = new Date();
		
		ICcb ccb = (ICcb)this.session.getService(ICcb.class);
		
		RfcQueryCriteria crit1 = new RfcQueryCriteria();
		crit1.setRfcClass(RFCModifyAttributeValue.class.getName());
		crit1.setFromDate(startDate);
		crit1.setDescendingOrder(true);
		crit1.setMaxResult(1);
		for (int i = 0; i < 6; i++) {
			List<IRFC> rfcs = ccb.queryRFCForCi(a1, crit1);
			//Assert.assertEquals(1, rfcs.size());
			if (rfcs.size() > 0) {
				RFCModifyAttributeValue rfc = (RFCModifyAttributeValue) rfcs.get(0);
				System.out.println(crit1.getFromDate() + rfc.getNewValue() + "<" + rfc.getTs() + "> " );
			} else {
				System.out.println(crit1.getFromDate() +": EMPTY!!!");
			}
			crit1.setFromDate(new Date(crit1.getFromDate().getTime() + 5000));
		}
		RfcQueryCriteria crit2 = new RfcQueryCriteria();
		crit2 = new RfcQueryCriteria();
		crit2.setRfcClass(RFCModifyAttributeValue.class.getName());
		crit2.setFromDate(startDate);
		crit2.setToDate(stopDate);
		List<IRFC> rfcs = ccb.queryRFCForCi(a1, crit2);
		//Assert.assertEquals(1, rfcs.size());
		for (IRFC rfc: rfcs) {
			RFCModifyAttributeValue rfcModValue = (RFCModifyAttributeValue) rfc;
			System.out.println(rfcModValue.getNewValue() + "<" + rfc.getTs() + "> " );
		}
	}




}
