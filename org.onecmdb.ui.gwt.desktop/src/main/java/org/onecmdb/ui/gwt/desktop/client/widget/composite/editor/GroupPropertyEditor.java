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
package org.onecmdb.ui.gwt.desktop.client.widget.composite.editor;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescriptionConfig;
import org.onecmdb.ui.gwt.desktop.client.widget.form.InputFormWidget;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class GroupPropertyEditor extends LayoutContainer {

	private GroupDescriptionConfig config;
	private CMDBPermissions permission;

	public GroupPropertyEditor(CMDBPermissions permissions, GroupDescriptionConfig config) {
		this.config = config;
		this.permission = permissions;
	}
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}


	public void initUI() {
		
		List<AttributeColumnConfig> attrs = new ArrayList<AttributeColumnConfig>();
		AttributeColumnConfig aCfg = new AttributeColumnConfig();
		aCfg.setId(GroupDescriptionConfig.NAME);
		aCfg.setName("Name");
		aCfg.setType("xs:string");
		attrs.add(aCfg);
		
		aCfg = new AttributeColumnConfig();
		aCfg.setId(GroupDescriptionConfig.ICON);
		aCfg.setName("Icon");
		aCfg.setType("xs:content");
		attrs.add(aCfg);
		
		aCfg = new AttributeColumnConfig();
		aCfg.setId(GroupDescriptionConfig.DESCRIPTION);
		aCfg.setName("Description");
		aCfg.setType("xs:textarea");
		attrs.add(aCfg);
		
		
		InputFormWidget form = new InputFormWidget(config, attrs );
		setLayout(new FitLayout());
		add(form);
	}

}
