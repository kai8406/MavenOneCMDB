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
package org.onecmdb.core.tests.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.RfcQueryCriteria;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OneCMDBTestConfig;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;
import org.onecmdb.core.utils.wsdl.RFCBean;
import org.onecmdb.core.utils.xml.XmlParser;

public class TestWebServiceHistorySerach extends AbstractOneCmdbTestCase {
	IOneCMDBWebService cmdbService = null;
	private String token;
	
	public TestWebServiceHistorySerach() {
		super();
	}

	public TestWebServiceHistorySerach(OneCMDBTestConfig config) {
		super(config);
	}

	public void setUp() {
		super.setUp();
		
		// Create IWebService interface.
		// Directly without going through the XFire!
		OneCMDBWebServiceImpl impl = new OneCMDBWebServiceImpl();
		impl.setOneCmdb(getCmdbContext());
		cmdbService = impl;
		
		// Use remote host.
		
		
		// Correct login.
		try {
			token = cmdbService.auth("admin", "123");
			Assert.assertNotNull(token);
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
	}
	
	public void testSearch1() {
		// Query for Root Template alias 
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setText("Root");
			crit.setTextMatchAlias(true);
			crit.setMatchCiTemplates(true);
			crit.setMatchCi(true);
			CiBean beans[] = cmdbService.search(token, crit);

			Assert.assertNotNull(beans);
			Assert.assertEquals(1, beans.length);
		}
		{
			// Query for all templates.
			QueryCriteria crit = new QueryCriteria();
			crit.setMatchCiTemplates(true);
			crit.setMatchCi(true);
			CiBean beans[] = cmdbService.search(token, crit);

			Assert.assertNotNull(beans);
			Assert.assertTrue(beans.length > 1);
		}
		
		{
			// Query for description, key with different charatecrs, like swedish, hebrew, curelic, chinese
			String url = "classpath:" + this.getClass().getPackage().getName().replace('.', '/') + "/ModelSearch.xml";
			try {
				testUtils.importXml(url);
			} catch (Throwable e) {
				fail("Import " + url + ":" + e);
			}
			
			// Get the the key text parsing xml, to have something to compare to.
			XmlParser parser = new XmlParser();
			parser.addURL(url);
			CiBean bean = parser.getBean("CiSerach");
			Assert.assertNotNull(bean);
			ValueBean vBean = bean.fetchAttributeValueBean("key", 0);
			Assert.assertNotNull(vBean);
			String matchText = vBean.getValue();
			Assert.assertNotNull(matchText);
			
			String searchKey = null;
			CiBean searchBean = null;
			{
				QueryCriteria crit = new QueryCriteria();
				crit.setText("CiSerach");
				crit.setTextMatchAlias(true);
				crit.setMatchCiTemplates(true);
				crit.setMatchCi(true);
				CiBean beans[] = cmdbService.search(token, crit);

				Assert.assertNotNull(beans);
				Assert.assertEquals(1, beans.length);

				searchBean = beans[0];

				List<ValueBean> values = searchBean.fetchAttributeValueBeans("key");
				Assert.assertEquals(1, values.size());
				ValueBean key = values.get(0);
				searchKey = key.getValue();
			
				Assert.assertNotNull(searchKey);
				
				// Make sure the searchkey is the same after it has been to
				// the backend and back!
				Assert.assertEquals(matchText, searchKey);
				
				
			}
			{
				// Finnally serach the description.
				QueryCriteria critUnicode = new QueryCriteria();
				critUnicode.setText(searchKey);
				critUnicode.setTextMatchDescription(true);
				critUnicode.setMatchCi(true);
				CiBean beans[] = cmdbService.search(token, critUnicode);
				Assert.assertEquals(1, beans.length);
				Assert.assertEquals(searchBean, beans[0]);
			}
		}
		
	}
	
	public void testSearchId() {
		CiBean ci = new CiBean();
		ci.setDerivedFrom("Root");
		ci.setAlias("T1");
		ci.setTemplate(true);
		
		IRfcResult result = cmdbService.update(token, new CiBean[] {ci}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		CiBean beans[] = cmdbService.query(token, "/template/T1", "");
		Assert.assertEquals(1, beans.length);
		CiBean rBean = beans[0];
		Assert.assertNotNull(rBean.getId());
		Assert.assertNotNull(rBean.getIdAsString());
		
		
		{
			QueryCriteria crit = new QueryCriteria();
			crit.setCiId(rBean.getId().toString());
			int count = cmdbService.searchCount(token, crit);
			Assert.assertEquals(1, count);
		}
		
		
	}
	
	public void testSearchAttributes() {
		int COUNT = 500;
		{
			CiBean template = new CiBean();
			template.setDerivedFrom("Root");
			template.setAlias("T1");
			template.setTemplate(true);
			AttributeBean a1 = new AttributeBean();
			a1.setAlias("a1");
			a1.setType("xs:string");
			AttributeBean a2 = new AttributeBean();
			a2.setAlias("a2");
			a2.setType("xs:string");
			template.addAttribute(a1);
			template.addAttribute(a2);
			
			
			
			
			// Create COUNT instances.
			List<CiBean> beans = new ArrayList<CiBean>();
			beans.add(template);
			for (int i = 0 ; i < COUNT; i++) {
				CiBean bean = new CiBean();
				bean.setDerivedFrom("T1");
				bean.setAlias("instance-" + i);
				bean.setTemplate(false);
				
				// Set attribute values.
				ValueBean v1 = new ValueBean();
				v1.setAlias("a1");
				v1.setValue("value-a1-" + i);
				
				ValueBean v2 = new ValueBean();
				v2.setAlias("a2");
				v2.setValue("value-a2-" + i);
				
				bean.addAttributeValue(v1);
				bean.addAttributeValue(v2);
				
				beans.add(bean);
			}

			IRfcResult result = cmdbService.update(token, beans.toArray(new CiBean[0]), null);
			Assert.assertEquals(null, result.getRejectCause());
		}
		
		// Test Counting on Ci
		{ 
			QueryCriteria crit = new QueryCriteria();		
			crit.setMatchCiInstances(true);
			crit.setText("instance-");
			crit.setTextMatchAlias(true);
			int i  = cmdbService.searchCount(token, crit);
			Assert.assertEquals(COUNT, i);
			
			CiBean cis[] = cmdbService.search(token, crit);
			Assert.assertEquals(i, cis.length);
		}
		
		// Test Counting on Attribute...
		{ 
			QueryCriteria crit = new QueryCriteria();		
			crit.setMatchAttribute(true);
			crit.setText("value-a1");
			crit.setTextMatchValue(true);
			int i  = cmdbService.searchCount(token, crit);
			Assert.assertEquals(COUNT, i);
			
			CiBean cis[] = cmdbService.search(token, crit);
			Assert.assertEquals(i, cis.length);
	
		}
		
		// Test Counting on Attribute...
		{ 
			QueryCriteria crit = new QueryCriteria();
			crit.setMatchCi(true);
			crit.setMatchAttribute(true);
			crit.setText("value-a2");
			crit.setTextMatchValue(true);
			int i  = cmdbService.searchCount(token, crit);
			Assert.assertEquals(COUNT, i);
	
			CiBean cis[] = cmdbService.search(token, crit);
			Assert.assertEquals(i, cis.length);	
		}
		
		// Test Counting on Attribute...
		{ 
			QueryCriteria crit = new QueryCriteria();		
			crit.setMatchAttribute(true);
			crit.setText("value-");
			crit.setTextMatchValue(true);
			int i  = cmdbService.searchCount(token, crit);
			Assert.assertEquals(COUNT, i);
			
			CiBean cis[] = cmdbService.search(token, crit);
			Assert.assertEquals(i, cis.length);
		}
		
		// Test Counting on Attribute...
		{ 
			QueryCriteria crit = new QueryCriteria();		
			crit.setMatchAttribute(true);
			crit.setText("value-a1-126");
			crit.setTextMatchValue(true);
			int i  = cmdbService.searchCount(token, crit);
			Assert.assertEquals(1, i);
			
			CiBean cis[] = cmdbService.search(token, crit);
			Assert.assertEquals(i, cis.length);
	
		}
		
		// Test Counting on Attribute...
		{ 
			QueryCriteria crit = new QueryCriteria();		
			crit.setMatchAttribute(true);
			crit.setText("Should not exists .lsdj+jal");
			crit.setTextMatchValue(true);
			int i  = cmdbService.searchCount(token, crit);
			Assert.assertEquals(0, i);
	
			CiBean cis[] = cmdbService.search(token, crit);
			Assert.assertEquals(i, cis.length);
		}
	}
	
	public void testAllSearch() {
		int COUNT = 10;
		
		{ // Create test CI's
			CiBean template = new CiBean();
			template.setDerivedFrom("Root");
			template.setAlias("T1");
			template.setTemplate(true);
			AttributeBean a1 = new AttributeBean();
			a1.setAlias("a1");
			a1.setType("xs:string");
			AttributeBean a2 = new AttributeBean();
			a2.setAlias("a2");
			a2.setType("xs:string");
			template.addAttribute(a1);
			template.addAttribute(a2);




			// Create COUNT instances.
			List<CiBean> beans = new ArrayList<CiBean>();
			beans.add(template);
			for (int i = 0 ; i < COUNT; i++) {
				CiBean bean = new CiBean();
				bean.setDerivedFrom("T1");
				bean.setAlias("instance-" + i);
				bean.setTemplate(false);

				// Set attribute values.
				ValueBean v1 = new ValueBean();
				v1.setAlias("a1");
				v1.setValue("value-a1-" + i);

				ValueBean v2 = new ValueBean();
				v2.setAlias("a2");
				v2.setValue("value-a2-" + i);

				bean.addAttributeValue(v1);
				bean.addAttributeValue(v2);

				beans.add(bean);
			}

			IRfcResult result = cmdbService.update(token, beans.toArray(new CiBean[0]), null);
			Assert.assertEquals(null, result.getRejectCause());
		}

		{
			String text = "value-a1-9";
			QueryCriteria crit = new QueryCriteria();
			crit.setText(text);
			// Search for CI and attributes.
			crit.setMatchCi(true);
			crit.setMatchAttribute(true);
			// Tex match on alias,description and value.
			crit.setTextMatchAlias(true);
			crit.setTextMatchDescription(true);
			crit.setTextMatchValue(true);

			// Match both template and instance attributes.
			crit.setMatchAttributeTemplates(true);
			crit.setMatchAttributeInstances(true); 

			// Always return instances
			crit.setMatchCiInstances(true);
			crit.setMatchCiTemplates(true);
			
			int i  = cmdbService.searchCount(token, crit);
			Assert.assertEquals(1, i);
			
		} 
	        
	}
	
	public void testSearchInstanceTemplates() {
		QueryCriteria crit = new QueryCriteria();
		crit.setOffspringOfAlias("Root");
		crit.setMatchCiInstances(true);
		
		{
			int i = cmdbService.searchCount(token, crit);
			Assert.assertEquals(0, i);
		}
		
		crit.setMatchCiTemplates(true);
		crit.setMatchCiInstances(false);
		{
			int i = cmdbService.searchCount(token, crit);
			Assert.assertTrue(i > 0);
		}
	}
	
	public void testSearchPaging() {
		int COUNT = 500;
		{
			// Create COUNT instances.
			List<CiBean> beans = new ArrayList<CiBean>();
			for (int i = 0 ; i < COUNT; i++) {
				CiBean bean = new CiBean();
				bean.setDerivedFrom("Root");
				bean.setAlias("instance-" + i);
				bean.setTemplate(false);
				beans.add(bean);
			}

			IRfcResult result = cmdbService.update(token, beans.toArray(new CiBean[0]), null);
			Assert.assertEquals(null, result.getRejectCause());
		}
		{
			// Lookup the root id.
			CiBean roots[] = cmdbService.query(token, "/template/Root", "*");
			Assert.assertEquals(1, roots.length);
			CiBean root = roots[0];
			Long rootId = root.getId();
			Assert.assertNotNull(rootId);
			
			// Serach for instances of root.
			QueryCriteria critPageing = new QueryCriteria();
			int pageSize = 10;
			
			Set<String> aliasNames = new HashSet<String>();
			
			for (int i = 0; i < COUNT/pageSize; i++) {
				critPageing.setFirstResult(i*pageSize);
				critPageing.setMaxResult(pageSize);
				critPageing.setOffspringOfId("" + rootId);
				critPageing.setMatchCi(true);
				critPageing.setMatchCiInstances(true);


				CiBean beans[] = cmdbService.search(token, critPageing);
				Assert.assertEquals(10, beans.length);
				
				// Make sure we get all instances.
				for (int j = 0; j < beans.length; j++) {
					if (aliasNames.contains(beans[j].getAlias())) {
						fail("Search page=" + i + " bean alias " + beans[j].getAlias() +" already found!");						
					}
					Assert.assertEquals(true, aliasNames.add(beans[j].getAlias()));
				}
			}
			Assert.assertEquals(COUNT, aliasNames.size());
			
			critPageing.setFirstResult(COUNT);
			critPageing.setMaxResult(pageSize);
			critPageing.setOffspringOfId("" + rootId);
			critPageing.setMatchCi(true);
			critPageing.setMatchCiInstances(true);

			CiBean beans[] = cmdbService.search(token, critPageing);
			Assert.assertEquals(0, beans.length);
		}
	}
	
	
	
	public void testHistory1() {
		CiBean iBean = null;
		{
			{
				// Setup.
				CiBean template = new CiBean();
				template.setTemplate(true);
				template.setAlias("Template");
				template.setDerivedFrom("Root");

				AttributeBean aBean = new AttributeBean();
				aBean.setAlias("count");
				aBean.setType("xs:string");
				template.addAttribute(aBean);

				ValueBean vBean = new ValueBean();
				vBean.setAlias("count");
				vBean.setValue("Init");
				template.addAttributeValue(vBean);

				CiBean instance = new CiBean();
				instance.setDerivedFrom("Template");
				instance.setAlias("instance1");

				IRfcResult result = cmdbService.update(token, new CiBean[] {template, instance}, null);
				Assert.assertEquals(null, result.getRejectCause());
			}
			{
				// Start modifying count attribute.
				
				// Find the instance CI.
				CiBean beans[] = cmdbService.query(token, "/instance/Template/instance1", "count");
				Assert.assertEquals(1, beans.length);
				iBean = beans[0];
				ValueBean countValue =  iBean.fetchAttributeValueBean("count", 0);
				Assert.assertNotNull(countValue);
				
				int MOD_COUNT = 500;
				String value = "Started";
				double amount = 0;
				for (int i = 0; i < MOD_COUNT; i++) {
					long start = System.currentTimeMillis();
					countValue.setValue(value);
					IRfcResult result = cmdbService.update(token, new CiBean[] {iBean}, null);
					long stop = System.currentTimeMillis();
					long dt = (stop-start);
					amount += dt;
					value = i + "Modified in " + dt + " ms, avg=" + (amount/i) + " ms"; 
				}
			}
			
			
		}
		
		
		RfcQueryCriteria rfcCrit = new RfcQueryCriteria();
		rfcCrit.setRfcClass(RFCModifyAttributeValue.class.getName());
		rfcCrit.setAttributeAlias("count");
		rfcCrit.setMaxResult(500);
		
		RFCBean[] result = cmdbService.history(token, iBean, rfcCrit);
		Assert.assertEquals(500, result.length);
		
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i].toString());
		}
		
		
	}
	
	
	
}
