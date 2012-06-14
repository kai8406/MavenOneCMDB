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
package org.onecmdb.core.tests.datacenter;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiService;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.tests.OnecmdbTestUtils;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class TestDataCenter extends TestCase {
	private ISession session;

	private ICi ciRoot;

	private ICi ciRelationRoot;

	public void setUp() {
		// Resource res = new
		// ClassPathResource("org/onecmdb/core/example/application.xml");
		Resource res = new ClassPathResource("onecmdb.xml");
		XmlBeanFactory beanFactory = new XmlBeanFactory(res);
		GenericApplicationContext svrctx = new GenericApplicationContext(
				beanFactory);

		final IOneCmdbContext cmdb = (IOneCmdbContext) svrctx
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
	
	public void testInitialModel() {
		OnecmdbTestUtils utils = new OnecmdbTestUtils(session);
		
		validateReferences(ciRelationRoot);
	}
	
	private void validateReferences(ICi ref) {
		if (!ref.isBlueprint()) {
			System.out.println("Validate Reference Instance " + ref.getDisplayName());
			List<IAttribute> targets = ref.getAttributesWithAlias("target");
			Assert.assertEquals(1, targets.size());
			IAttribute target = targets.get(0);
			IValue value = target.getValue();
			Assert.assertNotNull(value);
		}
		Set<ICi> cis = ref.getOffsprings();

		// Validate that we only have on target.
		for (ICi offspringRef : cis) {
			validateReferences(offspringRef);
		}
	}
}
