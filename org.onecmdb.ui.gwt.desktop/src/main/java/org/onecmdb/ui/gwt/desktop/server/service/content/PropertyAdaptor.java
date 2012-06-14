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
package org.onecmdb.ui.gwt.desktop.server.service.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;

public class PropertyAdaptor extends Properties implements IContentDataAware {

	
	/**
	 * Need empty constructor..
	 */
	public PropertyAdaptor() {
	}
	
	public void setContentData(ContentData data) {
		boolean loaded = load(data, "xml");
		if (loaded) {
			return;
		}
		load(data, "default");
	}
	
	public boolean load(ContentData data, String type) {
		boolean loaded = true;
		InputStream in = null;
		try {
			in = ContentParserFactory.get().getInputStream(data);
			if ("xml".equals(type)) {
				loadFromXML(in);
			} else {
				load(in);
			}
		} catch (IOException e) {
			loaded = false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					// Ignore...
				}
			}
		}
		return(loaded);
	}
	
}
