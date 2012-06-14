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
package org.onecmdb.core.tests.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.internal.storage.IDaoReader;
import org.onecmdb.core.internal.storage.expression.SourceRelationExpression;
import org.onecmdb.core.internal.storage.hibernate.HibernateDao;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;

public class TestDao extends AbstractOneCmdbTestCase {
	private OneCMDBWebServiceImpl cmdbService;

	public void setUp() {
		super.setUp();
		// Create IWebService interface.
		// Directly without going through the XFire!
		OneCMDBWebServiceImpl impl = new OneCMDBWebServiceImpl();
		impl.setOneCmdb(getCmdbContext());
		cmdbService = impl;
	}
	
	public void testQueryAttributeID() {
		IModelService mSvc = (IModelService) session.getService(IModelService.class);
		ICi ci = mSvc.findCi(new Path<String>("Root"));
		IAttribute attr = ci.getAttributeDefinitionWithAlias("icon");
		System.out.println("attr=" + attr);
		
		HibernateDao dao = (HibernateDao) getSpringApplicationContext().getBean("daoHibbe");
		
		String token = null;
		try {
			token = cmdbService.auth("admin", "123");
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
		
		QueryCriteria crit = new QueryCriteria();
		crit.setOffspringOfAlias("Reference");
		crit.setMatchCiInstances(true);
		crit.setMatchCiTemplates(false);
	
		int startRefInstance = cmdbService.searchCount(token, crit);
		
		List<CiBean> beans = new ArrayList<CiBean>();
		
		CiBean t1 = new CiBean("Root", "t1", true);
		t1.addAttribute(new AttributeBean("a1", "t2", "Reference", true));
		t1.addAttributeValue(new ValueBean("a1", "t2-1", true));
		beans.add(t1);
		
		CiBean t2 = new CiBean("Root", "t2", true);
		beans.add(t2);
		CiBean t2i = new CiBean("t2", "t2-1", false);
		beans.add(t2i);
		for (int i = 0; i < 10; i++) {
			CiBean t1i = new CiBean("t1", "t1-" + i, false);
			beans.add(t1i);
		}
		
		IRfcResult result = cmdbService.update(token, beans.toArray(new CiBean[0]), null);
		Assert.assertEquals(null, result.getRejectCause());
		
		int endRefInstance = cmdbService.searchCount(token, crit);
		
		Assert.assertEquals(11, (endRefInstance-startRefInstance));
		
		SourceRelationExpression relExpression = new SourceRelationExpression();
		CiBean target = getCIFromAlias(token, "t2-1");
		CiBean sourceTemplate = getCIFromAlias(token, "t1");
		
		
		relExpression.setTargetId(target.getId());
		relExpression.setSourceTemplateId(sourceTemplate.getId());
		
		QueryResult<ICi> respons = dao.queryExpression(relExpression);
		
		Assert.assertEquals(10, respons.size());
		
		
	}
	
	private CiBean getCIFromAlias(String token, String alias) {
		QueryCriteria crit = new QueryCriteria();
		crit.setCiAlias(alias);
		CiBean beans[] = cmdbService.search(token, crit);
		if (beans.length > 0) {
			return(beans[0]);
		}
		return(null);
	}

}
