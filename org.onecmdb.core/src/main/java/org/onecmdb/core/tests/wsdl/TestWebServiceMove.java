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

import junit.framework.Assert;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;

public class TestWebServiceMove extends AWebServiceTestCase {

	
	public void testMove() {
		CiBean t1 = new CiBean("Ci", "T1", true);
		t1.addAttribute(new AttributeBean("a1", "xs:string", null, false));
		
		CiBean t2 = new CiBean("T1", "T2", true);
		t2.addAttribute(new AttributeBean("a2", "xs:string", null, false));
	
		CiBean i1 = new CiBean("T1", "I-T1", false);
		CiBean beans[] = {t1, t2, i1};
		
		IRfcResult result = cmdbService.update(token, beans, null);
		Assert.assertEquals(null, result.getRejectCause());
		
		QueryCriteria crit = new QueryCriteria();
		crit.setCiAlias("I-T1");
		
		CiBean rBeans[] = cmdbService.search(token, crit);
		Assert.assertEquals(1, rBeans.length);
		
		CiBean base = rBeans[0];
		CiBean local = base.copy();
		local.setDerivedFrom("T2");
		
		result = cmdbService.update(token, new CiBean[] {local}, new CiBean[] {base});
		Assert.assertEquals(null, result.getRejectCause());
		
		rBeans = cmdbService.search(token, crit);
		Assert.assertEquals(1, rBeans.length);
		
		
		CiBean movedCi = rBeans[0];
		Assert.assertEquals("T2", movedCi.getDerivedFrom());
		// Set a value on a2;
		ValueBean vBean = movedCi.fetchAttributeValueBean("a2", 0);		
		Assert.assertNotNull(vBean);
		Assert.assertEquals(null, vBean.getValue());
		
	
	}
	
}
