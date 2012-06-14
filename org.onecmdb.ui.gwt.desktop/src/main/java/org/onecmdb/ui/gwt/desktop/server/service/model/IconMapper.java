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

public class IconMapper {

	private static Log log = LogFactory.getLog(IconMapper.class);
	
	public static final String DEFAULT_ATTRIBUTE_ICON = "DEFAULT.ATTRIBUTE.ICON";
	public static final String DEFAULT_CI_ICON = "DEFAULT.CI.ICON";
	public static final String DEFAULT_ATTRIBUTE_ICON_PATH = "Content/Images/icons/defaultAttribute16.gif";
	public static final String DEFAULT_CI_ICON_PATH = "Content/Images/icons/defaultCI16.gif";
	
	private static Properties properties;

	private static long lastRead = -1;

	public static String getIcon(String ciIconName, String type) {
		try {
			// Check if updated...
			ContentFile iconMapping = new ContentFile(ConfigurationFactory.get("IconMapperPath"));
			IContentService svc = (IContentService) ServiceLocator.getService(IContentService.class);
			if (svc != null) {
				ContentData stat = svc.stat(iconMapping);
				long lastModified = stat.getLastModified();
				if (lastModified > lastRead) {
					properties = null;
				}
			}
			if (properties == null) {
				properties = (Properties) ContentParserFactory.get().getAdaptor(iconMapping, Properties.class);
				lastRead = System.currentTimeMillis();
			}
		} catch (Throwable t) {
			log.error("Can't open IconMapper property file " + ConfigurationFactory.get("IconMapperPath"));
		}
		
		String icon = null;
		if (properties == null) {
			return(defaultIcon(type));
		}
		if (ciIconName != null) {
			icon = properties.getProperty("ci.icon." + ciIconName);
			if (icon != null) {
				return(icon);
			}
		}
		if (type != null) {
			icon = properties.getProperty(type);
			if (icon != null) {
				return(icon);
			}
			if (type.startsWith("xs:")) {
				icon = properties.getProperty(IconMapper.DEFAULT_ATTRIBUTE_ICON);
			} else {
				icon = properties.getProperty(IconMapper.DEFAULT_CI_ICON);
			}
		}
		if (icon != null && icon.length() > 0) {
			return(icon);
		}
		return(defaultIcon(type));
	}

	private static String defaultIcon(String type) {
		if (type == null) {
			return(IconMapper.DEFAULT_CI_ICON_PATH);
		}
		if (type.startsWith("xs:")) {
			return(IconMapper.DEFAULT_ATTRIBUTE_ICON_PATH);
		} else {
			return(IconMapper.DEFAULT_CI_ICON_PATH);
		}
	}
}
