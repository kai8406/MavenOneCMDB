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
package org.onecmdb.ui.gwt.desktop.client.utils;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;

public class CIModelUtils {
	
	public static void updateModel(CIModel bean, String alias, Object value, boolean complex) {
		if (value instanceof List) {
			// Remove existing list.
			ValueModel v = bean.getValue(alias);
			ValueListModel list = null;
			if (v instanceof ValueListModel) {
				list = (ValueListModel)v;
			} 
			if (list == null) {
				list = new ValueListModel();
				list.setAlias(alias);
				list.setIsComplex(complex);
				bean.setValue(alias, list);
			}
			
			list.removeValues();
			
			for (Object o : (List)value) {
				ValueModel vm = new ValueModel();
				vm.setAlias(alias);
				vm.setValue(o == null ? null : o.toString());
				vm.setIsComplex(complex);
				list.addValue(vm);
			}
			return;
		}
		ValueModel v = bean.getValue(alias);
		if (v == null) {
			v = new ValueModel();
			v.setAlias(alias);
			bean.setValue(alias, v);
			v.setIsComplex(complex);
		}
		v.setValue(value == null ? null : value.toString());
	}
	
	public static void updateModel(CIModel bean, String alias, Object value) {
		updateModel(bean, alias, value, false);
	}
	
}
