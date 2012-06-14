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
package org.onecmdb.ui.gwt.desktop.client.service.model;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;

public class SaveDeleteRequest extends ModelItem {
	
	@Override
	public ModelItem copy() {
		SaveDeleteRequest req = new SaveDeleteRequest();
		super.copy(req);
		return(req);
	}

	public ContentData getContent() {
		return(get("contentData"));
	}

	public List<SaveItem> getTemplates() {
		return(get("templates"));
	}
	
	public List<SaveItem> getReferences() {
		return(get("references"));
	}

	public void setContent(ContentData file) {
		set("contentData", file);
	}

	public void setTemplates(List<SaveItem> items) {
		set("templates", items);
	}
	
	public void setReferences(List<SaveItem> item) {
		set("references", item);
	}

	public boolean isVerify() {
		return(get("verify", true));
	}
	
	public void setVerify(boolean verify) {
		set("verify", verify);
	}
	
	public String toString() {
		List<SaveItem> templates = getTemplates();
		List<SaveItem> refs = getReferences();
		int templateItems = -1;
		int refItems = -1;
		if (templates != null) {
			templateItems = templates.size();
		}
		if (refs != null) {
			refItems = refs.size();
		}
		return("SaveDeleteRequest[verify=" + isVerify() + ", templateItems=" + templateItems + ", referenceItems=" + refItems + "]");
	}
}
