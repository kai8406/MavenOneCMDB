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
package org.onecmdb.ui.gwt.desktop.client.window.group;

import java.util.HashMap;

import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.Presentaion;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.TablePresentaion;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.CITreeModel;
import org.onecmdb.ui.gwt.desktop.client.widget.group.GroupNavigationTree;
import org.onecmdb.ui.gwt.desktop.client.widget.group.table.GroupTableWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CIInstanceReferenceTree;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;
import org.onecmdb.ui.gwt.desktop.client.window.misc.CMDBAppletWidget;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class CIGroupWindow extends CMDBAbstractWidget {

	public static final String ID = "cmdb-group-widget";
	private ContentFile mdr;
	
	public CIGroupWindow(CMDBDesktopWindowItem item) {
		super(item);
		setLayout(new FillLayout());
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		mdr = new ContentFile();
		String mdrConf = item.getParams().get("mdrConfig");
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new BorderLayout());
		
		ContentPanel left = new ContentPanel();
		final ContentPanel center = new ContentPanel();
		center.setLayout(new FitLayout());
		// Add left and center panels.
		final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	       
	       
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(5));  
	 
		
		panel.add(left, westData);
		panel.add(center, centerData);

		
		// Populate left
		left.setLayout(new FitLayout());
		ToolBar bar = new ToolBar();
		bar.add(new AdapterToolItem(new TextField<String>()));
		left.setTopComponent(bar);
		
		GroupNavigationTree tree = new GroupNavigationTree(mdr);
		tree.setSelectionChange(new SelectionChangedListener<CITreeModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<CITreeModel> se) {
				System.out.println("Selection Changed: " + se.getSelectedItem());
				
				GroupDescription desc = se.getSelectedItem().getGroupDescription();
				System.out.println("GroupDescription = " + desc);
				
				if (desc != null) {
					center.removeAll();
					TabPanel panel = new TabPanel();
					for (BaseModel view : desc.getPresentations()) {
						if (Presentaion.TABLE_TAG.equals(view.get("tag"))) {
							String name = (String)view.get(Presentaion.NAME);
							GroupTableWidget table = new GroupTableWidget(name, (String)view.get("primary"), desc);
							table.setPermission(permissions);
							TabItem item = new TabItem(name);
							item.setLayout(new FitLayout());
							item.add(table);
							panel.add(item);
						}
						if (Presentaion.GRAPH_TAG.equals(view.get("tag"))) {
							String name = (String)view.get(Presentaion.NAME);
							TabItem item = new TabItem(name);
							item.setLayout(new FitLayout());
							// Create the applet 
							
							CMDBDesktopWindowItem desktopItem = new CMDBDesktopWindowItem();
							BaseModel model = view.get("applet");
							BaseModel param = model.get("param");
							desktopItem.set("params", model);
							
							HashMap<String, String> prop = new HashMap<String, String>();
							prop.put("group", desc.getPath());
							prop.put("graphid", (String)view.get(Presentaion.ID));
							
							desktopItem.setParams(model);
							CMDBAppletWidget applet = new CMDBAppletWidget(desktopItem );
							applet.setProperty(prop);
							item.add(applet);
							panel.add(item);
							
						}
									
					}
					center.add(panel);
					center.layout();
				}
				
			}
		});
		tree.setPermission(permissions);
		
		left.add(tree);
		
		add(panel);
		layout();
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("CI Dynamic Grouping");
		desc.setDescription("A Widget that group CI's togheter and act on all as one.");
		desc.addParameter("<li>mdrConf - path to configuration for the MDR. Default is to OneCMDB.<li>");
		return(desc);	
	}

}
