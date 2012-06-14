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
package org.onecmdb.ui.gwt.desktop.client.widget.composite.editor.query;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescriptionConfig;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class GraphQueryEditor extends LayoutContainer {

	private GroupDescriptionConfig model;
	private CMDBPermissions permission;

	public GraphQueryEditor(CMDBPermissions perm, GroupDescriptionConfig model) {
		this.model = model;
		this.permission = perm;
	}
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}


	public void initUI() {
		setLayout(new FitLayout());
		
		TabPanel tabs = new TabPanel();
		
		TabItem item = new TabItem("Templates");
		item.setLayout(new FitLayout());
		item.add(new TemplateQueryTableEditor(this.permission, this.model));
		tabs.add(item);
		
		item = new TabItem("References");
		item.setLayout(new FitLayout());
		item.add(new ReferenceQueryTableEditor(this.permission, this.model));
		tabs.add(item);
		
		item = new TabItem("Preview");
		item.setLayout(new FitLayout());	
		item.add(new PreviewQueryTableEditor(this.permission, this.model));
		tabs.add(item);
		
		
		add(tabs);
	}


}
