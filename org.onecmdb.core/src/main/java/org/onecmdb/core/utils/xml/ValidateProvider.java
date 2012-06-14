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

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.onecmdb.core.utils.bean.CiBean;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ValidateProvider {

	public static void main(String argv[]) {
		// Startup Spring context.
		/*
		Resource res2 = new ClassPathResource(argv[0]);
		XmlBeanFactory beanFactory2 = new XmlBeanFactory(res2);
		GenericApplicationContext svrctx2 = new GenericApplicationContext(
				beanFactory2);
		*/
		if (argv.length != 1) {
			System.out.println("Need to specify file/directory to parse.");
			System.exit(-1);
		}
		File file = new File(argv[0]);
		if (!file.exists()) {
			System.out.println("File/directory '" + argv[0] + "' don't exists");
			System.exit(-1);
		}
		
		XmlParser provider = new XmlParser();
		
		File[] files = null;
		if (file.isDirectory()) {
			files = file.listFiles();
		} else {
			files = new File[1];
			files[0] = file;
		}
		
		try {
			for (File inFile : files) {
				provider.addURL(inFile.toURL().toExternalForm());
			}
		} catch (MalformedURLException e) {
			System.out.println("Can't convert file to url!");
			System.exit(-1);
		}

		
		BeanScope scope = new BeanScope();
		scope.setBeanProvider(provider);
		scope.setValidation(true);
		scope.process();
		HashMap<String, List<CiBean>> dup = scope.getDuplicatedBeans();		
		System.out.println("Duplicated:");
		for (String key : dup.keySet()) {
			System.out.println("\t" + key);
		}
		Set<String> unresolved = scope.getUnresolvedAliases();
		System.out.println("Unresolved:");
		for (String alias : unresolved) {
			System.out.println("\t" + alias);
		}
	}
	
}
