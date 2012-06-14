/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.onecmdb.core.tests.wsdl;

import java.net.MalformedURLException;

import junit.framework.Assert;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OneCMDBTestConfig;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;

public class TestWebServiceReleation extends AbstractOneCmdbTestCase {
	IOneCMDBWebService cmdbService = null;
	private boolean useRemote = false;
	private String remoteURL = "http://192.168.1.15:8080/webservice/OneCMDB";
	private String token;
	
	public TestWebServiceReleation() {
		super();
	}

	public TestWebServiceReleation(OneCMDBTestConfig config) {
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
	
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
	
	}
	
	/**
	 * Create the following relations:
	 * A-->B<--C-->D
	 * 
	 */
	public void testRelation1() {
		BeanFactory factory = new BeanFactory();
		
		
		CiBean a = factory.newBean("Root", "A", true);		
		CiBean b = factory.newBean("Root", "B", true);
		CiBean c = factory.newBean("Root", "C", true);
		CiBean d = factory.newBean("Root", "D", true);
		
		a.addAttribute(new AttributeBean("b", "B", "Reference", true));		
		a.addAttribute(new AttributeBean("name", "xs:string", null, false));
		
		b.addAttribute(new AttributeBean("name", "xs:string", null, false));		
		
		c.addAttribute(new AttributeBean("b", "B", "Reference", true));
		c.addAttribute(new AttributeBean("d", "D", "Reference", true));
		c.addAttribute(new AttributeBean("name", "xs:string", null, false));
		
		d.addAttribute(new AttributeBean("name", "xs:string", null, false));
		
		// Create instnace
		CiBean ia1 = factory.newBean("A", "ia1", false);
		CiBean ia2 = factory.newBean("A", "ia2", false);
		
		CiBean ib1 = factory.newBean("B", "ib1", false);
		CiBean ib2 = factory.newBean("B", "ib2", false);
	
		CiBean ic1 = factory.newBean("C", "ic1", false);
		CiBean ic2 = factory.newBean("C", "ic2", false);
	
		CiBean id1 = factory.newBean("D", "id1", false);
		CiBean id2 = factory.newBean("D", "id2", false);
		
		ia1.addAttributeValue(new ValueBean("b", "ib1", true));
		ia2.addAttributeValue(new ValueBean("b", "ib2", true));
		
		ic1.addAttributeValue(new ValueBean("b", "ib1", true));
		ic1.addAttributeValue(new ValueBean("d", "id1", true));
		
		ic2.addAttributeValue(new ValueBean("b", "ib2", true));
		ic2.addAttributeValue(new ValueBean("d", "id2", true));
	
		
		IRfcResult result = cmdbService.update(token, factory.getBeans(), null);
		Assert.assertEquals(null, result.getRejectCause());
		
		{
			// Find relations a-->b		
			String relAToB = "<$template{B}";
			CiBean rels[] = cmdbService.evalRelation(token, ia1, relAToB, null);
			Assert.assertEquals(1, rels.length);
			Assert.assertEquals(ib1, rels[0]);
			
		}
		{
			// Find relations c-->b		
			String relCToB = ">$attr{b}";
			CiBean rels[] = cmdbService.evalRelation(token, ic1, relCToB, null);
			Assert.assertEquals(1, rels.length);
			Assert.assertEquals(ib1, rels[0]);
			
		}
		
		{
			// Find relations a-->c		
			String relAToB = "<$template{B}";
			String relBToC = "<$template{C}";
			CiBean rels[] = cmdbService.evalRelation(token, ia1, relAToB + "|" + relBToC, null);
			Assert.assertEquals(1, rels.length);
			Assert.assertEquals(ic1, rels[0]);
			
		}
	}
	
	/**
	 * Create relation.
	 * 
	 * A-->B<--C-->D
	 *
	 */
	public void testAnyRelation() {
	BeanFactory factory = new BeanFactory();
		
		
		CiBean a = factory.newBean("Root", "A", true);		
		CiBean b = factory.newBean("Ci", "B", true);
		CiBean c = factory.newBean("Ci", "C", true);
		CiBean d = factory.newBean("Ci", "D", true);
		
		a.addAttribute(new AttributeBean("b", "B", "Reference", true));		
		a.addAttribute(new AttributeBean("name", "xs:string", null, false));
		
		b.addAttribute(new AttributeBean("name", "xs:string", null, false));		
		
		c.addAttribute(new AttributeBean("b", "B", "Reference", true));
		c.addAttribute(new AttributeBean("d", "D", "Reference", true));
		c.addAttribute(new AttributeBean("name", "xs:string", null, false));
		
		d.addAttribute(new AttributeBean("name", "xs:string", null, false));
		
		// Create instnace
		CiBean ia1 = factory.newBean("A", "ia1", false);
		CiBean ia2 = factory.newBean("A", "ia2", false);
		
		CiBean ib1 = factory.newBean("B", "ib1", false);
		CiBean ib2 = factory.newBean("B", "ib2", false);
	
		CiBean ic1 = factory.newBean("C", "ic1", false);
		CiBean ic2 = factory.newBean("C", "ic2", false);
	
		CiBean id1 = factory.newBean("D", "id1", false);
		CiBean id2 = factory.newBean("D", "id2", false);
		
		ia1.addAttributeValue(new ValueBean("b", "ib1", true));
		ia2.addAttributeValue(new ValueBean("b", "ib2", true));
		
		ic1.addAttributeValue(new ValueBean("b", "ib1", true));
		ic1.addAttributeValue(new ValueBean("d", "id1", true));
		
		ic2.addAttributeValue(new ValueBean("b", "ib2", true));
		ic2.addAttributeValue(new ValueBean("d", "id2", true));
		
		IRfcResult result = cmdbService.update(token, factory.getBeans(), null);
		Assert.assertEquals(null, result.getRejectCause());
		
		
		// Find all relations to B, should be A and C.
		{
			// Find relations a-->c		
			String relAnyToB = "<$template{Root}";
			CiBean rels[] = cmdbService.evalRelation(token, ib1, relAnyToB, null);
			Assert.assertEquals(2, rels.length);
			for (CiBean rel : rels) {
				if (rel.equals(ia1)) {
					continue;
				}
				if (rel.equals(ic1)) {
					continue;
				}
				Assert.assertEquals("Found ia1 and ic1", "Found the following ci <" + rel.getAlias() + ">");
			}
		}
		
	}

}
