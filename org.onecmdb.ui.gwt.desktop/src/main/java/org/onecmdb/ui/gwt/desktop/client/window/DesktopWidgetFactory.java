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
package org.onecmdb.ui.gwt.desktop.client.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.widget.ContentSelectorWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.EditableCIInstanceGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CIInstanceEditableReferenceTree;
import org.onecmdb.ui.gwt.desktop.client.window.factory.WidgetDescriptionFactory;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class DesktopWidgetFactory implements IWidgetFactory {
	private static DesktopWidgetFactory factory;
	
	
	private List<IWidgetFactory> factories = new ArrayList<IWidgetFactory>();
	 
	public static DesktopWidgetFactory get() {
		if (factory == null) {
			factory = new DesktopWidgetFactory();
		}
		return(factory);
	}
	
	public void addWidgetFactory(IWidgetFactory wFact) {
		factories.add(wFact);
	}
	
	/*
	static {
		// Content Windows.
		  widgetMap.put("cmdb-content-browser-window", ContentBrowserWidget.class);
			  
		  // Model windows.
		  widgetMap.put("cmdb-model-open-window", ModelOpenWidget.class);
		  widgetMap.put("cmdb-model-clear-window", ModelClearWidget.class);
		  widgetMap.put("cmdb-model-saveas-window", ModelSaveAsWidget.class);
		  widgetMap.put("cmdb-model-designer-window", ModelBrowser.class);
		  widgetMap.put("cmdb-model-overview-window", ModelOverviewWidget.class);
		  
		  // MDR Windows...
		  widgetMap.put("cmdb-mdr-designer", MDRDesignerWidget.class);
		  widgetMap.put("cmdb-mdr-browse", MDRWidget.class);
		
		  // Visualizations windows.
		  widgetMap.put("cmdb-instance-table-designer", CIInstanceGridDesignerWidget.class);
		  widgetMap.put("cmdb-instance-table", EditableCIInstanceGrid.class);
		  widgetMap.put("cmdb-instance-graph-designer",CIInstnaceGraphDesigner.class);
		  widgetMap.put("cmdb-instance-graph", CIInstanceGraph.class);
		  widgetMap.put("cmdb-instance-tree-designer", CIInstanceTreeDesigner.class);
		  widgetMap.put("cmdb-instance-tree", CIInstanceEditableReferenceTree.class);
		  
		  // Export
		  widgetMap.put("cmdb-instance-export-designer", CMDBExportDesigner.class);
		  
		  // Misc
		  widgetMap.put("cmdb-url-frame", URLFrameWidget.class);
		  widgetMap.put("cmdb-applet-frame", AppletFrameWidget.class);
	}
	*/
	
	public Widget createWidget(CMDBDesktopWindowItem item) {
		if (WidgetDescriptionFactory.ID.equals(item.getID())) {
			return(new WidgetDescriptionFactory(item));
		}
		for (IWidgetFactory f : factories) {
			Widget w = f.createWidget(item);
			if (w != null) {
				return(w);
			}
		}
		return(null);
	}
	
	public List<WidgetDescription> getWidgetDescriptions() {
		List<WidgetDescription> descs = new ArrayList<WidgetDescription>();
		for (IWidgetFactory f : factories) {
			for (WidgetDescription desc : f.getWidgetDescriptions()) {
				desc.setFactoryName(f.getName());
				descs.add(desc);
			}
		}
		WidgetDescription desc = new WidgetDescriptionFactory(null).getDescription();
		desc.setFactoryName(getName());
		descs.add(desc);
		
		return(descs);
	}

	public String getName() {
		return("DesktopWidgetFactory");
	}

	/**
	 * Create a new CMDBDesktopWindowItem from a map, usally passed in the URL.
	 * 
	 * @param windowId
	 * @param map
	 * @return
	 */
	public CMDBDesktopWindowItem createWidgetItem(String widgetId,
			Map<String, List<String>> map) {
		CMDBDesktopWindowItem item = new CMDBDesktopWindowItem();
		item.setID(widgetId);
		
		WidgetDescription desc = getWidgetDescription(widgetId);
		if (desc == null) {
			return(item);
		}
		BaseModel params = item.getParams();
		List<WidgetParameterEntry> entries = desc.getParameterEntries();
		for (WidgetParameterEntry entry : entries) {
			List<String> values = map.get(entry.getKey());
			if (values == null || values.size() == 0) {
				continue;
			}
			if (entry.isList()) {
				params.set(entry.getKey(), values);
			} else {
				params.set(entry.getKey(), values.get(0));
			}
		}
		// Setup permissions.
		BaseModel perm = new BaseModel();
		perm.set("readonly", "true");
		perm.set("editable", "false");
		perm.set("deletable", "false");
		perm.set("classify", "false");
		BaseModel permCon = new BaseModel();
		permCon.set("permissions", perm);
		item.set("permissions", permCon);
		return(item);
	}
	
	public WidgetDescription getWidgetDescription(String id) {
		List<WidgetDescription> descs = getWidgetDescriptions();
		for (WidgetDescription desc : descs) {
			if (desc.getId().equals(id)) {
				return(desc);
			}
		}
		return(null);
	}
}
