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
package org.onecmdb.ui.gwt.desktop.client.window.factory;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.DesktopWidgetFactory;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.XmlReader;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.Element;

public class WidgetDescriptionFactory extends CMDBAbstractWidget {

	public static final String ID = "desktop-widget-list";

	public WidgetDescriptionFactory(CMDBDesktopWindowItem item) {
		super(item);
		setLayout(new FillLayout());
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		List<WidgetDescription> descriptions = DesktopWidgetFactory.get().getWidgetDescriptions();
		ContentPanel cp = new ContentPanel();
		cp.setHeading("Available Widget(s)");
		cp.setScrollMode(Scroll.AUTO);
		cp.setIconStyle("icon-table"); 
		cp.setLayoutOnChange(true);
		
		cp.setLayout(new FillLayout());
		
	    List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		   
		XTemplate tpl = XTemplate.create("<p><b>Name:</b> {name}</p><br><p><b>Description:</b> {description}</p><p><b>Parameters:</b> {parameters}</p>");  
		   
		RowExpander expander = new RowExpander();  
		expander.setTemplate(tpl);  
		   
		configs.add(expander);  
	
		ColumnConfig column = new ColumnConfig();  
		     column.setId("id");  
		     column.setHeader("ID");  
		     column.setWidth(100);  
		     configs.add(column);  
		       
		 column = new ColumnConfig();  
		     column.setId("name");  
		     column.setHeader("Name");  
		     column.setWidth(200);  
		     configs.add(column);  
		   
			 column = new ColumnConfig();  
		     column.setId("factoryName");  
		     column.setHeader("Factory Name");  
		     column.setWidth(100);  
		     configs.add(column);  
		   
		     ListStore<WidgetDescription> store = new ListStore<WidgetDescription>();  
		     store.add(descriptions);  
		   
		     ColumnModel cm = new ColumnModel(configs);  
		   
		   
		     Grid<WidgetDescription> grid = new Grid<WidgetDescription>(store, cm);  
		     grid.addPlugin(expander);  
		     grid.getView().setForceFit(true);  
		     cp.add(grid);  
		     add(cp);  
		     cp.layout();
		     layout();
		     
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("Available Widget List");
		desc.setDescription("Show all available widgets that are registered to the DesktopWidgetFactory.<br> The current window is displaying this widget");
		desc.setParameters("None");
		
		return(desc);
	}
}
