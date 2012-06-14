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
package org.onecmdb.ui.gwt.desktop.client.window.composite;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescriptionConfig;
import org.onecmdb.ui.gwt.desktop.client.widget.composite.editor.GroupPropertyEditor;
import org.onecmdb.ui.gwt.desktop.client.widget.composite.editor.forms.FormEditor;
import org.onecmdb.ui.gwt.desktop.client.widget.composite.editor.presentation.PresentationEditor;
import org.onecmdb.ui.gwt.desktop.client.widget.composite.editor.query.GraphQueryEditor;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class CompositeEditorWindow extends CMDBAbstractWidget {
	public static final String ID = "cmdb-composite-editor";
	private GroupDescriptionConfig config;
	
	public CompositeEditorWindow(CMDBDesktopWindowItem item) {
		super(item);
	}
	
	public CompositeEditorWindow(CMDBPermissions perm, GroupDescriptionConfig group) {
		super(null);
		this.permissions = perm;
		this.config = group;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}

	protected void initUI() {
		if (this.config == null) {
			this.config = new GroupDescriptionConfig();
		}
		initUITree();
	}
	
	protected void initUITree() {
		setLayout(new BorderLayout());
		
		ContentPanel west = new ContentPanel();
		west.setLayout(new FitLayout());
		
		final ContentPanel center = new ContentPanel();
		center.setLayout(new FitLayout());
		
		Tree tree = new Tree();
		TreeItem rootItem = tree.getRootItem();
		
		TreeItem item = new TreeItem("1. Info");
		rootItem.add(item);
		item.setData("widget", new GroupPropertyEditor(permissions, config));
		item.setData("header", "Info Editor");

		item = new TreeItem("2. Query");
		rootItem.add(item);
		item.setData("widget", new GraphQueryEditor(permissions, config));
		item.setData("header", "Query Editor");

		
		tree.addListener(Events.SelectionChange, new Listener<TreeEvent>() {

			public void handleEvent(TreeEvent be) {
				center.removeAll();
				if (be.selected == null) {
					return;
				}
				if (be.selected.size() == 0) {
					return;
				}
				TreeItem item = be.selected.get(0);
				Object o = item.getData("widget");
				if (o instanceof Widget) {
					center.add((Widget)item.getData("widget"));
				}
				o = item.getData("header");
				if (o instanceof String) {
					center.setHeading((String)item.getData("header"));
				}
				center.layout();
			}
			
		});
		west.add(tree);
		
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	       
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(5));  
	 
		
		add(west, westData);
		add(center, centerData);
	}
		
	protected void initUITabs() {
		TabPanel tabs = new TabPanel();
		
		TabItem item = new TabItem("1. Query");
		item.setLayout(new FitLayout());
		item.add(new GraphQueryEditor(permissions, config));
		tabs.add(item);
		
		item = new TabItem("2. Presentation");
		item.setLayout(new FitLayout());
		item.add(new PresentationEditor(new BaseModel()));
		tabs.add(item);
		
		item = new TabItem("3. Forms");
		item.setLayout(new FitLayout());
		item.add(new FormEditor(new BaseModel()));
		tabs.add(item);
		
		add(tabs);
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB Composite Configuratior");
		desc.setDescription("A Widget configures a specific composite");
		return(desc);	

	}

}
