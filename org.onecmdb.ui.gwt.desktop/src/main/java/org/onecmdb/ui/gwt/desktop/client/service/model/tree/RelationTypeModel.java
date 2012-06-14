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
package org.onecmdb.ui.gwt.desktop.client.service.model.tree;

import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;

public class RelationTypeModel extends ModelItem {

	public static final String REL_OUTBOUND = "rel_outbound";
	public static final String REL_TARGET_TYPE = "rel_target_type";
	public static final String REL_REFTYPE = "rel_reftype";
	public static final String REL_SOURCE_TYPE = "rel_source_type";
	public static final String REL_ATTRIBUTE_ALIAS = "rel_attribute_alias";
	public static final String REL_INSTANCE = "rel_instance";
	public static final String REL_ID = "rel_id";
	
	@Override
	public RelationTypeModel copy() {
		RelationTypeModel m = new RelationTypeModel();
		super.copy(m);
		return(m);
	}
	
	public boolean isOutbound() {
		return(get(REL_OUTBOUND, false));
	}

	public void setOutbound(boolean b) {
		set(REL_OUTBOUND, b);
	}

	public CIModel getSourceType() {
		return(get(REL_SOURCE_TYPE));
	}

	public CIModel getTargetType() {
		return(get(REL_TARGET_TYPE));
	}

	public CIModel getInstance() {
		return(get(REL_INSTANCE));
	}

	public CIModel getRefType() {
		return(get(REL_REFTYPE));
	}

	public void setAttributeAlias(String alias) {
		set(REL_ATTRIBUTE_ALIAS, alias);
		
	}

	public void setInstance(CIModel ci) {
		set(REL_INSTANCE, ci);
	}

	public void setSourceType(CIModel type) {
		set(REL_SOURCE_TYPE, type);
	}

	public void setTargetType(CIModel type) {
		set(REL_TARGET_TYPE, type);
	}

	public void setRefType(CIModel type) {
		set(REL_REFTYPE, type);
	}

	public String getAttributeAlias() {
		return(get(REL_ATTRIBUTE_ALIAS));
	}

	public void setId(String id) {
		set(REL_ID, id);
	}
	public String getId() {
		return(get(REL_ID));
	}
	
}
