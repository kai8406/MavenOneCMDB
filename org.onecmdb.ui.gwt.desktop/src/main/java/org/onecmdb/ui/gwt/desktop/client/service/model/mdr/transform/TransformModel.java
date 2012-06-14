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

import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;

import com.extjs.gxt.ui.client.data.PropertyChangeEvent;

public class TransformModel extends ModelItem {

	@Override
	public TransformModel copy() {
		TransformModel m = new TransformModel();
		 super.copy(m);
		 return(m);
	}

	public List<DataSetModel> getDataSets() {
		return(get("datasets", new ArrayList<DataSetModel>()));
	}
	
	public void setDataSets(List<DataSetModel> dataSets) {
		set("datasets", dataSets);
	}
	
	public void removeDataSet(DataSetModel ds) {
		List<DataSetModel> dataSets = get("datasets");
		if (dataSets != null) {
			dataSets.remove(ds);
		}
	}

	public DataSetModel getDataSet(String dsName) {
		if (dsName == null) {
			return(null);
		}
		for (DataSetModel ds : getDataSets()) {
			if (dsName.equals(ds.getName())) {
				return(ds);
			}
		}
		return(null);
	}

	/**
	 * Need to create a new list to fire update event!
	 * @param ds
	 */
	public void addDataSet(DataSetModel ds) {
		List<DataSetModel> oldList = getDataSets();
		ArrayList<DataSetModel> list = new ArrayList<DataSetModel>();
		list.addAll(oldList);
		list.add(ds);
		setDataSets(list);
		PropertyChangeEvent evnt = new PropertyChangeEvent(Add, this, "datasets", null, ds);
		evnt.parent = this;
		evnt.item = ds;
		notify(evnt);
	}

	public String getName() {
		return(get("name"));
	}
	
	public void setName(String name) {
		set("name", name);
	}



}
