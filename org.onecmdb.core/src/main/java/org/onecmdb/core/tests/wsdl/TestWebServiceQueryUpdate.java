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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.acegisecurity.BadCredentialsException;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OneCMDBTestConfig;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;

public class TestWebServiceQueryUpdate extends AbstractOneCmdbTestCase {
	IOneCMDBWebService cmdbService = null;
	private boolean useRemote = false;
	private String remoteURL = "http://192.168.1.15:8080/webservice/OneCMDB";
	
	public TestWebServiceQueryUpdate() {
		super();
	}

	public TestWebServiceQueryUpdate(OneCMDBTestConfig config) {
		super(config);
	}

	public void setUp() {
		
		if (useRemote) {
			Service serviceModel = new ObjectServiceFactory().create(IOneCMDBWebService.class);
	
			try {
				cmdbService = (IOneCMDBWebService)
				    new XFireProxyFactory().create(serviceModel, remoteURL);
			} catch (MalformedURLException e) {				
				e.printStackTrace();
				fail("Can't connect to remote WebService ");
			}

		} else {
			super.setUp();
		
			// Create IWebService interface.
			// Directly without going through the XFire!
			OneCMDBWebServiceImpl impl = new OneCMDBWebServiceImpl();
			impl.setOneCmdb(getCmdbContext());
			cmdbService = impl;
		}
		 
	}
	
	public void testAuth() {
		// Correct login.
		try {
			String token = cmdbService.auth("admin", "123");
			Assert.assertNotNull(token);
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		// Incorrect login.
		try {
			String token = cmdbService.auth("admin", "1231");
			fail("Should not be able to login:" + token);
		} catch (Exception e) {
			// Ok.
		}
	}

	public void testQueryTypes() {
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		QueryCriteria crit = new QueryCriteria();
		crit.setMatchType("Root");
		crit.setMatchCiTemplates(true);
		crit.setOffspringOfAlias("Ci");
		crit.setOffspringDepth(new Integer(-1));
		CiBean beans[] = cmdbService.search(token, crit);
		for (int i = 0; i < beans.length; i++) {
			System.out.println("BEAN:" + beans[i]);
		}
	}
	
	public void testDefaultValues() {
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
	
		CiBean t1 = new CiBean("Root", "t1", true);
		t1.addAttribute(new AttributeBean("a1", "t2", "Reference", true));
		t1.addAttributeValue(new ValueBean("a1", "t2-1", true));
		
		CiBean t2 = new CiBean("Root", "t2", true);
		
		CiBean t2i = new CiBean("t2", "t2-1", false);
		
		CiBean t1i = new CiBean("t1", "t1-1", false);
		
		IRfcResult result = cmdbService.update(token, new CiBean[] {t1, t2, t2i, t1i}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
	}
	
	public void testQuery1()  {
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		CiBean rootBean[] = cmdbService.query(token, "/template/Root", "");
		Assert.assertNotNull(rootBean);
		Assert.assertEquals(1, rootBean.length);
		Assert.assertEquals("Root", rootBean[0].getAlias());
	
		CiBean beans[] = cmdbService.query(token, "/template/*", "*");
		Assert.assertNotNull(rootBean);
	
	}
	
	
	public void testUpdate1() {
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		// Will create a new instance of Root.
		CiBean newBean = new CiBean();
		newBean.setDerivedFrom("Root");
		newBean.setAlias("T1");
		
		{
			// Add instance
			IRfcResult result = cmdbService.update(token, new CiBean[] {newBean}, null);
			Assert.assertEquals(null, result.getRejectCause());

		}
		CiBean instances[] = cmdbService.query(token, "/instance/Root", "*");
		Assert.assertNotNull(instances);
		Assert.assertEquals(1, instances.length);
		Assert.assertEquals("T1", instances[0].getAlias());

		{
			// Remove instance.
			IRfcResult result = cmdbService.update(token, null, instances);
			Assert.assertEquals(null, result.getRejectCause());
	
			instances = cmdbService.query(token, "/instance/Root", "*");
			Assert.assertNotNull(instances);
			Assert.assertEquals(0, instances.length);
		}
		{
			// Test add/remove attribute
			CiBean templateBean = new CiBean();
			templateBean.setDerivedFrom("Root");
			templateBean.setAlias("T1");
			templateBean.setTemplate(true);
			
			AttributeBean a1 = new AttributeBean();
			a1.setType("xs:string");
			a1.setAlias("a1");
			
			AttributeBean a2 = new AttributeBean();
			a2.setType("xs:string");
			a2.setAlias("a2");
				
			templateBean.addAttribute(a1);
			templateBean.addAttribute(a2);
			
			// Create it
			IRfcResult result = cmdbService.update(token, new CiBean[] {templateBean}, null);
			Assert.assertEquals(null, result.getRejectCause());
			
			// Validate.
			CiBean baseTemplates[] = cmdbService.query(token, "/template/T1", "*");
			Assert.assertNotNull(baseTemplates);
			Assert.assertEquals(1, baseTemplates.length);
		
			CiBean baseTemplate = baseTemplates[0];
			Assert.assertEquals(3, baseTemplate.getAttributes().size());
			
			CiBean copy = baseTemplate.copy();
			Assert.assertEquals(true, copy.removeAttribute("a1"));
			
			// Remove attribute a1.
			result = cmdbService.update(token, new CiBean[] {copy}, baseTemplates);
			Assert.assertEquals(null, result.getRejectCause());
			
			baseTemplates = cmdbService.query(token, "/template/T1", "*");
			Assert.assertNotNull(baseTemplates);
			Assert.assertEquals(1, baseTemplates.length);
			Assert.assertEquals(2, baseTemplates[0].getAttributes().size());
		}
	}
	
	public void testUpdateCompare() {
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		List<CiBean> beans = new ArrayList<CiBean>();
		
		// Will create a new instance of Root.
		{
			CiBean newBean = new CiBean();
			newBean.setDerivedFrom("Root");
			newBean.setAlias("T1");
			newBean.setTemplate(true);
			
			beans.add(newBean);
		}
		{
			CiBean newBean = new CiBean();
			newBean.setDerivedFrom("Root");
			newBean.setAlias("T2");
			newBean.setTemplate(true);
			AttributeBean a1 = new AttributeBean();
			a1.setType("T1");
			a1.setComplexType(true);
			a1.setAlias("a1");
			a1.setMaxOccurs("unbound");
			a1.setMinOccurs("1");
			newBean.addAttribute(a1);
			
			beans.add(newBean);
		}
		
		
		// Create instances
		for (int i = 0; i < 10; i++) {
			beans.add(new CiBean("T1", "T1-" + i, false));
		}
		
		// Create reference T2
		CiBean T2I1 = new CiBean("T2", "T2-1", false);
		T2I1.addAttributeValue(new ValueBean("a1", "T1-1", true));
		T2I1.addAttributeValue(new ValueBean("a1", "T1-2", true));
		T2I1.addAttributeValue(new ValueBean("a1", "T1-3", true));
		T2I1.addAttributeValue(new ValueBean("a1", "T1-4", true));
		beans.add(T2I1);
		
		IRfcResult result = cmdbService.update(token, beans.toArray(new CiBean[0]), null);
		Assert.assertEquals(null, result.getRejectCause());
	
		// Load T"I1
		QueryCriteria crit = new QueryCriteria();
		crit.setCiAlias("T2-1");
		
		CiBean insts[] = cmdbService.search(token, crit);
		Assert.assertEquals(1, insts.length);
		CiBean inst = insts[0];
		Assert.assertEquals(4, inst.getAttributeValues().size());
		
		// Now remove one attribute.
		CiBean copy = inst.copy();
		// Remove T1-4 value.
		List<ValueBean> values = copy.fetchAttributeValueBeans("a1");
		for (ValueBean bean : values) {
			if (bean.getValue().equals("T1-4")) {
				copy.removeAttributeValue(bean);
			}
		}
		 
		result = cmdbService.update(token, new CiBean[] {copy}, new CiBean[] {inst});
		Assert.assertEquals(null, result.getRejectCause());
		
		// Load T2I1 again now it should have 3 values.
		crit = new QueryCriteria();
		crit.setCiAlias("T2-1");
			
		insts = cmdbService.search(token, crit);
		Assert.assertEquals(1, insts.length);
		inst = insts[0];
		Assert.assertEquals(3, inst.fetchAttributeValueBeans("a1").size());
	}
	
	public void testUpdateValue() {
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		// Will create a new instance of Root.
		CiBean newBean = new CiBean();
		newBean.setDerivedFrom("Root");
		newBean.setAlias("T1");
		newBean.setTemplate(true);
		
		AttributeBean a1 = new AttributeBean();
		a1.setType("xs:string");
		a1.setAlias("a1");
		
		AttributeBean a2 = new AttributeBean();
		a2.setType("xs:string");
		a2.setAlias("a2");
		
		AttributeBean aList = new AttributeBean();
		aList.setType("xs:string");
		aList.setAlias("aList1");
		aList.setMinOccurs("1");
		aList.setMaxOccurs("unbound");	
		
		newBean.addAttribute(a1);
		newBean.addAttribute(a2);
		newBean.addAttribute(aList);

		IRfcResult result = cmdbService.update(token, new CiBean[] {newBean}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		{
			// Add Default value's.
			ValueBean va1 = new ValueBean();
			va1.setAlias("a1");
			va1.setValue("Default-Value-A1");
			newBean.addAttributeValue(va1);

			ValueBean va2 = new ValueBean();
			va2.setAlias("a2");
			va2.setValue("Default-Value-A2");
			newBean.addAttributeValue(va2);
		}
		for (int i = 0; i < 10; i++) {
			ValueBean a = new ValueBean();
			a.setAlias("aList1");
			a.setValue("Default-Value-AList-" + i);
			newBean.addAttributeValue(a);
		}
		
		result = cmdbService.update(token, new CiBean[] {newBean}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		// Check the default values.
		CiBean beans[] = cmdbService.query(token, "/template/T1", "*");
		Assert.assertNotNull(beans);
		Assert.assertEquals(1, beans.length);
		{
			CiBean t1 = beans[0];
			List<ValueBean> va1 = t1.fetchAttributeValueBeans("a1");
			Assert.assertNotNull(va1);
			Assert.assertEquals(1, va1.size());
			Assert.assertEquals("Default-Value-A1", va1.get(0).getValue());
	
			List<ValueBean> va2 = t1.fetchAttributeValueBeans("a2");
			Assert.assertNotNull(va2);
			Assert.assertEquals(1, va2.size());
			Assert.assertEquals("Default-Value-A2", va2.get(0).getValue());
			
			List<ValueBean> vlist1 = t1.fetchAttributeValueBeans("aList1");
			Assert.assertNotNull(vlist1);
			// We have one unset value --> 11.
			Assert.assertEquals(11, vlist1.size());
		}
	}
	
	public void testUpdateManyValues() {
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		// Will create a new instance of Root.
		CiBean newBean = new CiBean();
		newBean.setDerivedFrom("Root");
		newBean.setAlias("T1");
		newBean.setTemplate(true);
		
		AttributeBean a1 = new AttributeBean();
		a1.setType("xs:string");
		a1.setAlias("a1");
		
		AttributeBean aList = new AttributeBean();
		aList.setType("xs:string");
		aList.setAlias("aList1");
		aList.setMinOccurs("0");
		aList.setMaxOccurs("unbound");	
		
		newBean.addAttribute(a1);
		newBean.addAttribute(aList);

		IRfcResult result = cmdbService.update(token, new CiBean[] {newBean}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		CiBean inst = new CiBean();
		inst.setDerivedFrom("T1");
		inst.setAlias("TEST");
		inst.setTemplate(false);
		{
			// Add Default value's.
			ValueBean va1 = new ValueBean();
			va1.setAlias("a1");
			va1.setValue("Default-Value-A1");
			inst.addAttributeValue(va1);
		}
		
		for (int i = 0; i < 10; i++) {
			ValueBean a = new ValueBean();
			a.setAlias("aList1");
			a.setValue("Default-Value-AList-" + i);
			inst.addAttributeValue(a);
		}
		
		result = cmdbService.update(token, new CiBean[] {inst}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		// Check the default values.
		CiBean beans[] = cmdbService.query(token, "/instance/T1/TEST", "*");
		Assert.assertNotNull(beans);
		Assert.assertEquals(1, beans.length);
		{
			CiBean t1 = beans[0];
			List<ValueBean> va1 = t1.fetchAttributeValueBeans("a1");
			Assert.assertNotNull(va1);
			Assert.assertEquals(1, va1.size());
			Assert.assertEquals("Default-Value-A1", va1.get(0).getValue());
	
			List<ValueBean> vlist1 = t1.fetchAttributeValueBeans("aList1");
			Assert.assertNotNull(vlist1);
			// We have one unset value --> 10.
			Assert.assertEquals(10, vlist1.size());
		}
		
		// Add some more attributes.
		CiBean inst1 = new CiBean();
		inst1.setDerivedFrom("T1");
		inst1.setAlias("TEST");
		inst1.setTemplate(false);
		
		for (int i = 0; i < 5; i++) {
			ValueBean a = new ValueBean();
			a.setAlias("aList1");
			a.setValue("Default-Value-AList-new" + i);
			inst1.addAttributeValue(a);
		}
		
		result = cmdbService.update(token, new CiBean[] {inst1}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		// Check the default values.
		{
			beans = cmdbService.query(token, "/instance/T1/TEST", "*");
			Assert.assertNotNull(beans);
			Assert.assertEquals(1, beans.length);
			{
				CiBean t1 = beans[0];
				List<ValueBean> va1 = t1.fetchAttributeValueBeans("a1");
				Assert.assertNotNull(va1);
				Assert.assertEquals(1, va1.size());
				Assert.assertEquals("Default-Value-A1", va1.get(0).getValue());

				List<ValueBean> vlist1 = t1.fetchAttributeValueBeans("aList1");
				Assert.assertNotNull(vlist1);
				// We have one unset value --> 10.
				Assert.assertEquals(15, vlist1.size());
			}
		}
		
		
	}

	
	public void testUpdateInheriteValue() {
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		// Will create a new instance of Root.
		CiBean newT1 = new CiBean();
		newT1.setDerivedFrom("Root");
		newT1.setAlias("T1");
		newT1.setTemplate(true);
		
		AttributeBean a1 = new AttributeBean();
		a1.setType("xs:string");
		a1.setAlias("a1");
		
		AttributeBean a2 = new AttributeBean();
		a2.setType("xs:string");
		a2.setAlias("a2");
		
		newT1.addAttribute(a1);
		newT1.addAttribute(a2);
		
		
		// Add Default value's.
		ValueBean va1 = new ValueBean();
		va1.setAlias("a1");
		va1.setValue("Default-Value-T1-A1");
		newT1.addAttributeValue(va1);

		ValueBean va2 = new ValueBean();
		va2.setAlias("a2");
		va2.setValue("Default-Value-T1-A2");
		newT1.addAttributeValue(va2);

		
		CiBean newT2 = new CiBean();
		newT2.setDerivedFrom("T1");
		newT2.setAlias("T2");
		newT2.setTemplate(true);

		ValueBean t2va2 = new ValueBean();
		t2va2.setAlias("a2");
		t2va2.setValue("Default-Value-T2-A2");
		newT2.addAttributeValue(t2va2);
	
		CiBean newT2I = new CiBean();
		newT2I.setDerivedFrom("T2");
		newT2I.setAlias("T2-I1");
		newT2I.setTemplate(false);
		
		IRfcResult result = cmdbService.update(token, new CiBean[] {newT1, newT2, newT2I}, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		CiBean beans[] = cmdbService.query(token, "/instance/T2/*", "*");
		Assert.assertNotNull(beans);
		Assert.assertEquals(1, beans.length);
		
		CiBean it2 = beans[0];
		List<ValueBean> a2vList = it2.fetchAttributeValueBeans("a2");
		Assert.assertNotNull(a2vList);
		Assert.assertEquals(1, a2vList.size());
		ValueBean a2i = a2vList.get(0);
		Assert.assertEquals("Default-Value-T2-A2", a2i.getValue());
		
	}
}
