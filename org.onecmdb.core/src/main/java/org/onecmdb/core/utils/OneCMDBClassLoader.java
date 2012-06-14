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
package org.onecmdb.core.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OneCMDBClassLoader {
	private static Log log = LogFactory.getLog(OneCMDBClassLoader.class);
	
	public static Object newInstance(String className, List<String> classpath) {
		List<URL> urls = new ArrayList<URL>();
		if (classpath != null) {
			for (String urlStr : classpath) {
				URL url;
				try {
					url = new URL(urlStr);
					urls.add(url);
				} catch (MalformedURLException e) {
					log.warn("Classpath <" + urlStr + "> not an url! Ignoring...");
				}
				
			}
		}
		Object instance = null;
		
		ClassLoader loader = new URLClassLoader((URL[])urls.toArray(new URL[0]), OneCMDBClassLoader.class.getClassLoader());
		try {
			Class clazz = loader.loadClass(className);
			instance = clazz.newInstance();
		} catch (ClassNotFoundException e) {
			log.error("Class <" + className + "> not found!", e);
		} catch (InstantiationException e) {
			log.error("Class <" + className + "> not instanciable!", e);
		} catch (IllegalAccessException e) {
			log.error("Class <" + className + "> not accessiable!", e);
		}
		
		return(instance);
	}

}
