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

import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.util.property.PropertyAdapter;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.IContentService;
import org.onecmdb.ui.gwt.desktop.server.service.ServiceLocator;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;

public class ShellMapper {

	private static Log log = LogFactory.getLog(ShellMapper.class);
	
	private static Properties properties;

	private static long lastRead = -1;

	public static Properties getShellProperties() {
		try {
			// Check if updated...
			ContentFile shellMapping = new ContentFile(ConfigurationFactory.get("ShellMapperPath"));
			IContentService svc = (IContentService) ServiceLocator.getService(IContentService.class);
			if (svc != null) {
				ContentData stat = svc.stat(shellMapping);
				long lastModified = stat.getLastModified();
				if (lastModified > lastRead) {
					properties = null;
				}
			}
			if (properties == null) {
				properties = (Properties) ContentParserFactory.get().getAdaptor(shellMapping, Properties.class);
				lastRead = System.currentTimeMillis();
			}
		} catch (Throwable t) {
			log.error("Can't open ShellMapper property file " + ConfigurationFactory.get("ShellMapperPath"));
		}
		
		return(properties);
		
	}

}
