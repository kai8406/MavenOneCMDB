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

public class TestWebServiceReleationQuery extends AbstractOneCmdbTestCase {
	IOneCMDBWebService cmdbService = null;
	private boolean useRemote = false;
	private String remoteURL = "http://192.168.1.15:8080/webservice/OneCMDB";
	private String token;
	
	public TestWebServiceReleationQuery() {
		super();
	}

	public TestWebServiceReleationQuery(OneCMDBTestConfig config) {
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
		//		 Correct login.
		try {
			token = cmdbService.auth("admin", "123");
			Assert.assertNotNull(token);
		} catch (Exception e) {
			fail("Login-failed" + e);
		} 
	}
	
	
	public void testRelationExpr() {
		List<CiBean> beans = new ArrayList<CiBean>();
		
		CiBean t1 = new CiBean("Root", "t1", true);
		t1.addAttribute(new AttributeBean("a1", "t2", "Reference", true));
		t1.addAttributeValue(new ValueBean("a1", "t2-1", true));
		t1.addAttribute(new AttributeBean("test", "xs:integer", null, false));
		beans.add(t1);
		
		CiBean t2 = new CiBean("Root", "t2", true);
		beans.add(t2);
		CiBean t2i = new CiBean("t2", "t2-1", false);
		beans.add(t2i);
		int COUNT = 100;
		for (int i = 0; i < COUNT; i++) {
			CiBean t1i = new CiBean("t1", "t1-" + i, false);
			t1i.addAttributeValue(new ValueBean("test", "" + i, false));
			beans.add(t1i);
		}
		
		IRfcResult result = cmdbService.update(token, beans.toArray(new CiBean[0]), null);
		Assert.assertEquals(null, result.getRejectCause());
		
		
		int count = cmdbService.evalRelationCount(token, t2i, "<$template{t1}", null);
		Assert.assertEquals(COUNT, count);
		
		CiBean resBeans[] = cmdbService.evalRelation(token, t2i, "<$template{t1}", null);
		Assert.assertEquals(COUNT, resBeans.length);
		
		QueryCriteria crit = new QueryCriteria();
		crit.setOrderAscending(true);
		crit.setOrderAttAlias("test");
		crit.setOrderType("valueAsString");
		crit.setMaxResult(10);
		crit.setFirstResult(1);
		
		CiBean res2Beans[] = cmdbService.evalRelation(token, t2i, "<$template{t1}", crit);
		Assert.assertEquals(10, res2Beans.length);
		Assert.assertEquals("t1-1", res2Beans[0].getAlias());
	}

	
}
