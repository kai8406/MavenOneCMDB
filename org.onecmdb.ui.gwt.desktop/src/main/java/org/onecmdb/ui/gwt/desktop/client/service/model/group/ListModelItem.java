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

public class ListModelItem<M> extends ModelItem {

	public ListModelItem() {
		set("size", 0);
	}
	@Override
	public ModelItem copy() {
		ListModelItem copy = new ListModelItem();
		this.copy(copy);
		return(copy);
	}
	public void add(M item) {
		int size = (Integer)get("size");
		add(size, item);
		size++;
		set("size", size);
	}
	
	public void add(int index, M item) {
		set("" + index, item);
	}
	
	public void add(List<M> items) {
		for (int i = 0; i < items.size(); i++) {
			add(i, items.get(i));
		}
		set("size", items.size());
	}
	public List<M> toList() {
		List<M> list = new ArrayList<M>();
		int size = (Integer)get("size");
		for (int i = 0; i < size; i++) {
			Object o = get("" + i);
			list.add((M)o);
		}
		return(list);
	}

}
