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
package org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform;

import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;

public class AttributeSelectorModel extends ModelItem {
	public static final String AS_TYPE_VALUE = "value";
	public static final String AS_TYPE_SELECTOR = "selector";
	
	@Override
	public ModelItem copy() {
		AttributeSelectorModel m = new AttributeSelectorModel();
		 super.copy(m);
		 return(m);
	}

	public AttributeModel getAttribute() {
		return(get("attribute"));
	}

	public void setAttribute(AttributeModel a) {
		set("attribute", a);
	}
	public boolean isNaturalKey() {
		return(get("naturalKey", false));
	}
	public void setNaturalKey(boolean v) {
		set("naturalKey", v);
	}

	public String getSelector() {
		return(get("selector"));
	}
	
	public void setSelector(String sel) {
		set("selector", sel);
	}
	
	public String getValue() {
		return(get("value"));
	}
	
	public void setValue(String v) {
		set("value", v);
	}
	
	
	public String getSelectorType() {
		return(get("selectorType"));
	}

	public void setSelectorType(String v) {
		set("selectorType", v);
	}

	public DataSetModel getParent() {
		return(get("parent"));
	}
	
	public void setParent(DataSetModel parent) {
		set("parent", parent);
	}

	public void setUseSelectorName(boolean b) {
		set("useSelectorName", b);
	}
	
	public boolean isUseSelectorName() {
		return(get("useSelectorName", false));
	}
	
	

}
