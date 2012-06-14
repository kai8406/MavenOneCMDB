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
package org.onecmdb.ui.gwt.desktop.client.service.change;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;

public class TestChangeItems {
	public static List<ChangeItem> getTestChangeItems() {
		List<ChangeItem> changes = new ArrayList<ChangeItem>();
		for (int i = 0; i < 300; i++) {
			ChangeItem item = new ChangeItem();
			item.set("local", "Server");
			item.set("change", "Equals");
			item.set("remote", "Server" + i);
			changes.add(item);
		}
		for (int i = 0; i < 5; i++) {
			ChangeCI item = new ChangeCI();
			item.set("local", "Server" + i);
			item.set("change", "New");
			item.set("remote", "Server" + i);
			changes.add(item);
		}
		
		for (int i = 0; i < 5; i++) {
			ChangeItem item = new ChangeItem();
			item.set("local", "Server" + i);
			item.set("change", "Deleted");
			item.set("remote", "Server" + i);
			changes.add(item);
		}
		
		for (int i = 0; i < 5; i++) {
			ChangeItem item = new ChangeItem();
			item.set("local", "Server" + i);
			item.set("change", "Modified");
			item.set("remote", "Server" + i);
			changes.add(item);
		}
		
		
		return(changes);
	}
	
	public static List<CIModel> getTestCIModels(int size) {
		List<CIModel> models = new ArrayList<CIModel>();
		for (int i = 0; i < size; i++) {
			models.add(getTestCIModel());
		}
		return(models);
	}
	
	public static CIModel getTestCIModel() {
		CIModel ci = new CIModel();
		
		
		return(ci);
	}
	
}
