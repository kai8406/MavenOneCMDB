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
package org.onecmdb.ui.gwt.desktop.client.service.model.grid;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;


public class CIModelCollection extends ModelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean newCollection;

	public CIModelCollection() {
		super();
	}
	
	public boolean isNewCollection() {
		return newCollection;
	}


	public void setNewCollection(boolean newCollection) {
		this.newCollection = newCollection;
	}


	public List<CIModel> getCIModels() {
		List<CIModel> models = new ArrayList<CIModel>();
		for (String key : getPropertyNames()) {
			Object o = super.get(key);
			if (o instanceof CIModel) {
				models.add((CIModel)o);
			}
		}
		return(models);
	}
	
	
	public void addCIModel(String id, CIModel model) {
		super.set(id, model);
	}
	
	public CIModel getCIModel(String id) {
		Object ret = super.get(id);
		if (ret instanceof CIModel) {
			return((CIModel)ret);
		}
		return(null);
	}

	@Override
	public <X> X set(String name, X value) {
		
		if (this.allowNestedValues) {
			return(super.set(name, value));
		}
		
		String split[] = name.split("\\.");
		if (split.length == 2) {
			String ci = split[0];
			String attr = split[1];

			CIModel model = getCIModel(ci);
			return(model.set(attr, value));
		}
		return(super.set(name, value));
	}

	@Override
	public <X> X get(String property) {
		if (this.allowNestedValues) {
			return((X)super.get(property));
		}
	
		
		String split[] = property.split("\\.");
		if (split.length == 2) {
			String ci = split[0];
			String attr = split[1];

			CIModel model = getCIModel(ci);
			return((X)model.get(attr));
		}
		return((X)super.get(property));
	}

	public CIModelCollection copy() {
		CIModelCollection m = new CIModelCollection();
		copy(m);
		return(m);
	}

}
