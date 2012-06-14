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

import java.util.Collection;
import java.util.Map;

import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;

import com.extjs.gxt.ui.client.data.BaseModel;

public abstract class ModelItem extends BaseModel {
	
	
	public ModelItem() {
		super.allowNestedValues = false;
	}
	
	
	@Override
	public <X> X get(String property) {
		//System.out.println("MODEL_ITEM:GET - " + property + "=" + super.get(property));
		return (X)super.get(property);
	}
	
	public void propagatePropertyChanged(String name, String value, String oldValue) {
		/*
		if (getParent() != null) {
			getParent().propagatePropertyChanged(name, value, oldValue);
		} else {
			notifyPropertyChanged(name, value, oldValue);
		}
		*/
	}


	/**
	 * Copy this item to the input parameter item.<br>
	 * Deep Copy of all properties. Will NOT copy any collections.
	 * The specific ModelItem object has to deal with them explicit.
	 * @param m
	 */
	public void copy(ModelItem m) {
		Map<String, Object> map = getProperties();
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (value instanceof ModelItem) {
				value = ((ModelItem)value).copy();
			}
			if (value instanceof Collection) {
				continue;
			}
			m.set(key, value);
		}
	}
	 
	/**
	 * Deep copy of an item.
	 * <pre>
	 * Usage:
	 *  public X copy() {
	 *     X x = new X();
	 *     super.copy(x);
	 *     return(x);
	 *  }
	 * </pre>
	 */
	public abstract ModelItem copy();
	
}
