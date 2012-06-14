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
package org.onecmdb.ui.gwt.desktop.server.service.model;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.taskdefs.LoadProperties;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.content.IContentService;
import org.onecmdb.ui.gwt.desktop.server.service.ServiceLocator;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentServiceImpl;

public class ConfigurationFactory {

	private static final String CONF_PATH = "Configuration/content.cfg";
	
	
	private static long lastRead;
	private static Properties properties;
	private static Log log = LogFactory.getLog(ConfigurationFactory.class);
	
	public static String get(String key) {
		loadConfigFile();
		if (properties == null) {
			return(null);
		}
		return(properties.getProperty(key));
	}
	
	protected static void loadConfigFile() {
		// Check if it has changed....
		IContentService cService = (IContentService) ServiceLocator.getService(IContentService.class);
		if (cService == null) {
			cService = new ContentServiceImpl();
		}
		ContentData data = cService.stat(new ContentFile(CONF_PATH));
		if (data.getLastModified() > lastRead) {
			properties = null;
		}
		if (properties == null) {
			try {
				properties = (Properties) ContentParserFactory.get().getAdaptor(new ContentFile(CONF_PATH), Properties.class);
				lastRead = System.currentTimeMillis();
				log.info("OK - Configuration loaded...");
			} catch (Throwable t) {
				log.error("FAILED - Read configuration <" + CONF_PATH + ">");
			}
		}
		
	}
	
	public static Config getConfig() {
		Config config = new Config();
		loadConfigFile();
		if (properties != null) {
			for (Enumeration e = properties.propertyNames(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = properties.getProperty(key);
				config.set(key, value);
			}
		}
		return(config);
	}
}
