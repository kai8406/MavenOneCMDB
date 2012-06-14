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

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;

public class DataSetModel extends ModelItem {
	
	public DataSetModel() {
		setInstanceSelector(new InstanceSelectorModel());
	}
	
	@Override
	public ModelItem copy() {
		DataSetModel m = new DataSetModel();
		 super.copy(m);
		 return(m);
	}
	public void setInstanceSelector(InstanceSelectorModel is) {
		set("instance", is);
	}

	public InstanceSelectorModel getInstanceSelector() {
		return(get("instance"));
	}
	public List<AttributeSelectorModel> getAttributeSelector() {
		ArrayList list = new ArrayList();
		List<AttributeSelectorModel> data = get("attributes");
		if (data != null) {
			list.addAll(data);
		}
		return(list);
	}
	
	public List<? extends BaseModel> getChildren() {
		ArrayList list = new ArrayList();
		//list.add(getInstanceSelector());
		list.addAll(getAttributeSelector());
		return(list);
	}

	public void removeAttributeSelector(AttributeSelectorModel as) {
		List<AttributeSelectorModel> list = getAttributeSelector();
		list.remove(as);
		setAttributeSelector(list);

		// Fire Delete event...
		PropertyChangeEvent evnt = new PropertyChangeEvent(Remove, this, "datasets", null, as);
		evnt.parent = this;
		evnt.item = as;
		
		notify(evnt);
		
	}

	public void addAttributeSelector(
			AttributeSelectorModel as) {
		List<AttributeSelectorModel> oldList = getAttributeSelector();
		ArrayList<AttributeSelectorModel> list = new ArrayList<AttributeSelectorModel>();
		list.addAll(oldList);
		list.add(as);
		setAttributeSelector(list);
		as.setParent(this);
		
		// Fire Add event...
		PropertyChangeEvent evnt = new PropertyChangeEvent(Add, this, "datasets", null, as);
		evnt.parent = this;
		evnt.item = as;
		
		notify(evnt);
	}

	private void setAttributeSelector(List<AttributeSelectorModel> list) {
		// Reset old list.
		set("attributes", list);
	}

	public CIModel getTemplate() {
		return(get("template"));
	}

	public String getName() {
		return(get("name"));
	}

	public void setName(String name) {
		set("name", name);
		
	}

	public void setTemplate(CIModel ci) {
		set("template", ci);
	}



}
