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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValueListModel extends ValueModel {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;

	public ValueListModel() {
		super();
	}
	
	public void setValues(List<ValueModel> values) {
		set("valueList", values);
	}
	
	public void addValue(ValueModel convert) {		
		List<ValueModel> values = get("valueList"); 
		if (values == null) {
			values = new ArrayList<ValueModel>();
			set("valueList", values);
		}
		values.add(convert);
	}
	
	public List<ValueModel> getValues() {
		List<ValueModel> values = get("valueList");
		if (values == null) {
			return(new ArrayList<ValueModel>());
		}
		return(values);
	}
	
	public void removeValues() {
		setValues(new ArrayList<ValueModel>());
	}

	public void removeValue(ValueModel realValue) {
		List<ValueModel> values = getValues();
		values.remove(realValue);
	}

	@Override
	public String getValueDisplayName() {
		StringBuffer b = new StringBuffer();
		b.append("[");
		b.append(getValues().size());
		b.append("]");
		boolean first = true;
		for (ValueModel vModel : getValues()) {
			if (!first) {
				b.append(", ");
			}
			first = false;
			b.append(vModel.getValueDisplayName());
		}
		
		return(b.toString());
			
	}

	@Override
	public ValueListModel copy() {
		ValueListModel model = new ValueListModel();
		super.copy(model);
		
		for (ValueModel vm : getValues()) {
			model.addValue(vm.copy());
		}
	
		return(model);
	}


	
	
}
