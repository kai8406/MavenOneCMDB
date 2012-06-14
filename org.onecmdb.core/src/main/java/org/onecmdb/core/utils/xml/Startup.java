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
package org.onecmdb.core.utils.xml;

import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class Startup {
	public static void main(String argv[]) {
		Resource res1 = new ClassPathResource("onecmdb.xml");
		XmlBeanFactory beanFactory1 = new XmlBeanFactory(res1);
		GenericApplicationContext svrctx1 = new GenericApplicationContext(
				beanFactory1);

		IOneCmdbContext ctx = (IOneCmdbContext) svrctx1.getBean("onecmdb");
		ISession session = ctx.createSession();

		// Startup Spring context.
		Resource res2 = new ClassPathResource("bean-provider-application.xml");
		XmlBeanFactory beanFactory2 = new XmlBeanFactory(res2);
		GenericApplicationContext svrctx2 = new GenericApplicationContext(
				beanFactory2);

		BeanScope scope = (BeanScope) svrctx2.getBean("xmlToRfc");
		scope.process();

		ImportRfcs importRfcs = (ImportRfcs) svrctx2.getBean("rfcSender");
		importRfcs.setSession(session);
		importRfcs.setRfcs(scope.getRFCs());
		importRfcs.run();
	}

}
