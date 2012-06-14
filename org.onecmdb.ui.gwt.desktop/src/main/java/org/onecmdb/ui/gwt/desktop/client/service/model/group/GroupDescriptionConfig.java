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
package org.onecmdb.ui.gwt.desktop.client.service.model.group;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;

/**
 * Responsable for configuring the GroupDescription. 
 * This object is modified by the Composite Editor.
 * The transformation between tis class and GroupDescription is 
 * handled by #GroupConfig2GroupDescription. 
 * 
 *
 */
public class GroupDescriptionConfig extends ModelItem {

	public static final String NAME = "name";
	public static final String ICON = "icon";
	public static final String DESCRIPTION = "description";
	
	

	
	public static final String TEMPLATE_QUERY = "templateQuery";
	public static final String TEMPLATE_QUERY_ID = "id";
	public static final String TEMPLATE_QUERY_TEMPLATE = "template";
	public static final String TEMPLATE_QUERY_PRIMARY = "primary";


	public static final String REFERENCE_QUERY = "referenceQuery";
	public static final String REFERENCE_QUERY_ID = "id";
	public static final String REFERENCE_QUERY_SOURCE = "source";
	public static final String REFERENCE_QUERY_TARGET = "target";
	public static final String REFERENCE_QUERY_MANDATORY = "mandatory";

	
	
	@Override
	public GroupDescriptionConfig copy() {
		GroupDescriptionConfig copy = new GroupDescriptionConfig();
		this.copy(copy);
		
		return(copy);
	}
	

	public void addQueryTemplate(BaseModel m) {
		List<BaseModel> oldList = getQueryTemplates();
		ArrayList<BaseModel> list = new ArrayList<BaseModel>();
		list.addAll(oldList);
		list.add(0, m);
		setQueryTemplates(list);
		list.add(m);
		
		// Fire add event...
		PropertyChangeEvent evnt = new PropertyChangeEvent(Add, this, TEMPLATE_QUERY, null, m);
		evnt.parent = this;
		evnt.item = m;
		notify(evnt);
	}
	
	public void setQueryTemplates(List<BaseModel> list) {
		set(TEMPLATE_QUERY, list);
	}
	
	public List<BaseModel> getQueryTemplates() {
		List<BaseModel> list = get(TEMPLATE_QUERY);
		if (list == null) {
			list = new ArrayList<BaseModel>();
			set(TEMPLATE_QUERY, list);
		}
		return(list);
	}
	
	public void addQueryReference(BaseModel m) {
		List<BaseModel> oldList = getQueryReferences();
		ArrayList<BaseModel> list = new ArrayList<BaseModel>();
		list.addAll(oldList);
		list.add(0, m);
		setQueryReferences(list);
		list.add(m);
		
		// Fire add event...
		PropertyChangeEvent evnt = new PropertyChangeEvent(Add, this, TEMPLATE_QUERY, null, m);
		evnt.parent = this;
		evnt.item = m;
		notify(evnt);
	}
	
	public void setQueryReferences(List<BaseModel> list) {
		set(REFERENCE_QUERY, list);
	}
	
	public List<BaseModel> getQueryReferences() {
		List<BaseModel> list = get(REFERENCE_QUERY);
		if (list == null) {
			list = new ArrayList<BaseModel>();
			set(REFERENCE_QUERY, list);
		}
		return(list);
	}
	
	
	public List<BaseModel> getPresentations() {
		return(null);
	}




	
	
}
