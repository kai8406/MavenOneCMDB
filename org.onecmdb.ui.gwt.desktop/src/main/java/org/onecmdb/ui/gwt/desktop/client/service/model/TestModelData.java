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
import java.util.List;

public class TestModelData {
	public static List<CIModel> getTestIntsnaces() {
		List<CIModel> models = new ArrayList<CIModel>();
		
		for (int i = 0; i < 5; i++) {
			models.add(createTestServerCIModel("Server-" + i));
		}
		
		return(models);
	}
	public static CIModel createTestServerCIModel(String name) {
		CIModel model = createTestSimpleCIModel("computer16.gif");
		// Add complex Attribute
		AttributeModel aModel = new AttributeModel();
		aModel.set("icon", "hdd16.gif");
		aModel.set("alias", "disk");
		aModel.set("name", "Disk");
		aModel.setComplex(true);
		//model.addAttribute(aModel);
		
		ValueModel v = new ValueModel();
		v.setValue("hdd16.gif");		
		model.set("name", v);
		
		
		v = new ValueModel();
		v.setValue("hdd16.gif");		
		model.set("icon", v);
		
		
		aModel = new AttributeModel();
		aModel.set("icon", "device16.gif");
		aModel.set("alias", "memeory");
		aModel.set("name", "RAM");
		aModel.setComplex(true);
		//model.addAttribute(aModel);
		
		v = new ValueModel();
		v.setValue("device16.gif");
		aModel.set("icon", v);
		
		v = new ValueModel();
		v.setValue("device16.gif");
		model.set("icon", v);
		
		aModel = new AttributeModel();
		aModel.set("icon", "ip16.gif");
		aModel.set("alias", "nic");
		aModel.set("name", "NIC");
		aModel.setComplex(true);
		//model.addAttribute(aModel);
		
		v = new ValueModel();
		v.setValue("ip16.gif");
		model.set("icon", v);
		
		return(model);
		
	}
	
	public static CIModel createTestSimpleCIModel(String name) {
		CIModel model = new CIModel();
		model.set("icon", name);
		model.set("alias", "ci.alias." + name);
		model.set("name", "ci.name." + name);
		model.set("derivedFrom", "Server");
		
		// Add a simple Attribute
		AttributeModel aModel = new AttributeModel();
		aModel.set("alias", "name");
		aModel.set("name", "Name");
		//model.addAttribute(aModel);
		ValueModel v = new ValueModel();
		v.setValue("v.name." + name);
		model.set("name", v);
		
		aModel = new AttributeModel();
		aModel.set("alias", "serialNumber");
		aModel.set("name", "Serial number");
		//model.addAttribute(aModel);
		v = new ValueModel();
		v.setValue("90123-213-123-BD");
		model.set("serialNumber", v);
		
		aModel = new AttributeModel();
		aModel.set("alias", "model");
		aModel.set("name", "Model");
		//model.addAttribute(aModel);
		v = new ValueModel();
		v.setValue("HP MS-7219");
		model.set("model", v);
		
		// Add multiple Value Attribute
		aModel = new AttributeModel();
		aModel.set("alias", "notes");
		aModel.set("name", "Notes");
		//model.addAttribute(aModel);
		ValueListModel list = new ValueListModel();
		v = new ValueModel();
		v.setValue(name + ":Note1: testar detta hure detta skall se ut..");
		list.addValue(v);
		v = new ValueModel();
		v.setValue(name + ":Note2: testar detta hure detta skall se ut..");
		list.addValue(v);
		
		model.set("notes", list);
		return(model);
	}
}
