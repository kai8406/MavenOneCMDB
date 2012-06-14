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
package org.onecmdb.core.tests.xml;

import java.util.List;

import junit.framework.TestCase;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiService;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.tests.OnecmdbTestUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestAttributeInheritage extends AbstractOneCmdbTestCase {
	
	
	
	
	@Override
	protected String getProviderResource() {
		return("org/onecmdb/core/tests/resources/providers/xml/test-inherite-provider.xml");
	}




	public void testSimpelAttributeInheritage() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(session);
		IModelService model = (IModelService)this.session.getService(IModelService.class);
		
		ICi root = model.findCi(new Path("SimpleAttributeRoot"));
		ICi child3 = model.findCi(new Path("Child3"));
		
		
		String aliases[] = {"name", "category"};
		for (String aAlias : aliases) {
			List<IAttribute> rootAttr = root.getAttributesWithAlias(aAlias);
			List<IAttribute> child3Attr = child3.getAttributesWithAlias(aAlias);
			
			for (IAttribute a : rootAttr) {
				System.out.println("RootAttribute " + aAlias + "= "+ a.getValue());
			}
			for (IAttribute a : child3Attr) {
				System.out.println("Child3Attribute " + aAlias + "=" + a.getValue());
			}
		}
	}

	

}
