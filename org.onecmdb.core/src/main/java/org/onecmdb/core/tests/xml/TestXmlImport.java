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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiService;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.tests.OnecmdbTestUtils;
import org.onecmdb.core.utils.OnecmdbUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestXmlImport extends TestCase {
	private ISession session;

	private ICi ciRoot;

	private ICi ciRelationRoot;

	public void setUp() {
		// Resource res = new
		// ClassPathResource("org/onecmdb/core/example/application.xml");
		
		String[] resources = {
				"core-onecmdb.xml", 
				"org/onecmdb/core/tests/hsql-inproc-datasource.xml",
				"org/onecmdb/core/tests/xml/thomas-provider.xml",				
		};
		/*
		String[] resources = {
				"org/onecmdb/core/tests/xml/onecmdb-factory.xml", 
		};
		*/
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				resources);
		

		final IOneCmdbContext cmdb = (IOneCmdbContext) appContext
				.getBean("onecmdb");

		session = cmdb.createSession();
		IModelService modelsvc = (IModelService) session
				.getService(IModelService.class);

		// well known name is ``root''
		ICi root = modelsvc.getRoot();
		assertNotNull(root);

		ICiService cisvc = (ICiService) session.getService(ICiService.class);

		// well known name is ``CI''
		ciRoot = cisvc.getRootCi();
		assertNotNull(ciRoot);

		IReferenceService refsvc = (IReferenceService) session
				.getService(IReferenceService.class);

		ciRelationRoot = refsvc.getRootReference();
		assertNotNull(ciRelationRoot);

	}

	public void testThomas() {
		IModelService mSvc = (IModelService)this.session.getService(IModelService.class);
		ICi th = mSvc.findCi(new Path<String>("Thomas"));
		System.out.println(th); 
		OnecmdbTestUtils utils = new OnecmdbTestUtils(this.session);
		System.out.println(utils.dumpOffsprings(th, 0));
		
		utils.destroyCi(th);
		
		
	}
	/**
	 * Import test xml file from test-provider.xml
	 *
	 *	Two templates Item and ChildItem
	 */
	public void xtestImport() {
		IModelService model = (IModelService)this.session.getService(IModelService.class);
		ICi item = model.findCi(new Path("Item"));
		Assert.assertNotNull(item);
		
		ICi childItem = model.findCi(new Path("ChildItem"));
		Assert.assertNotNull(childItem);
		
		List<IAttribute> aList = item.getAttributesWithAlias("a1");
		Assert.assertEquals(1, aList.size());
		Assert.assertEquals("parent", aList.get(0).getValue().getAsString());
		
		aList = childItem.getAttributesWithAlias("a1");
		Assert.assertEquals(1, aList.size());
		Assert.assertEquals("child", aList.get(0).getValue().getAsString());
		
	}

}
