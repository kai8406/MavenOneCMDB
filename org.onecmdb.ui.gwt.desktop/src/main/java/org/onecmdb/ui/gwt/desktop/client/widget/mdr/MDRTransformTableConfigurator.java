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
package org.onecmdb.ui.gwt.desktop.client.widget.mdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.AttributeSelectorModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.help.HelpInfo;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MDRTransformTableConfigurator extends LayoutContainer {
	
	private TransformConfig cfg;
	private TransformModel model;
	private GridModelConfig sourceGrid;
	private BaseListLoader<ListLoadConfig, ListLoadResult<BaseModel>> loader;
	private MDRTransformSelectorTable selectorTable;
	private ContentPanel south;

	public MDRTransformTableConfigurator(TransformConfig cfg) {
		this.cfg = cfg;
		this.model = cfg.getTransformModel();
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}

	protected void initUI() {
		setLayout(new BorderLayout());
		
		ContentPanel centerPanel = new ContentPanel();
		centerPanel.setLayout(new FitLayout());
		
		south = new ContentPanel();
		south.setHeading("DataSource Preview");
		south.setLayout(new FitLayout());
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	 
	    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 200);  
	    southData.setSplit(true);  
	    southData.setCollapsible(true);  
	    southData.setMargins(new Margins(5));  
		
	    
	    TabPanel tabPanel = new TabPanel();
	    
	    TabItem tab = new TabItem("3.1 Template/Attribute Mapping");
	    tab.setLayout(new FitLayout());
	    selectorTable = new MDRTransformSelectorTable(cfg);
	    tab.add(selectorTable);
	    tab.addListener(Events.Select, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				HelpInfo.show("help/mdr/help-mdr-transform-attribute.html");
				selectorTable.update();
			}
	    });
	    
	    tabPanel.add(tab);
		
		tab = new TabItem("3.2 Additional Data");
		tab.setLayout(new FitLayout());
		final MDRTransformDefaultValueTable defaultTable = new MDRTransformDefaultValueTable(cfg);
		tab.add(defaultTable);
		  tab.addListener(Events.Select, new Listener<BaseEvent>() {
				public void handleEvent(BaseEvent be) {
					HelpInfo.show("help/mdr/help-mdr-transform-defaultvalue.html");
					defaultTable.update();
				}
		    });
		
		tabPanel.add(tab);

		tab = new TabItem("3.3 References");
		tab.setLayout(new FitLayout());
		final MDRTransformRelationTable relTable = new MDRTransformRelationTable(cfg);
		tab.add(relTable);
		tab.addListener(Events.Select, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				HelpInfo.show("help/mdr/help-mdr-transform-reference.html");
				relTable.update();
			}
	    });
	  

		tabPanel.add(tab);
		
		tab = new TabItem("3.4 Natural Keys");
		tab.setLayout(new FitLayout());
		final MDRTransformNaturalKeyTable mdrNatural = new MDRTransformNaturalKeyTable(cfg);
		tab.add(mdrNatural);
		tab.addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				HelpInfo.show("help/mdr/help-mdr-transform-naturalkey.html");
				mdrNatural.update();
			}
			
		});
		tabPanel.add(tab);
		
		
	    centerPanel.add(tabPanel);
	    centerPanel.setHeaderVisible(false);
		
		 // Add preview panel...
	   
	   
	    // Left is the tree...
		add(centerPanel, centerData);
		add(south, southData);
		
		//update();
		
	}
	
	
	public void update() {
		// Remove 
		south.removeAll();
		
		 PreviewTableWidget preview = new PreviewTableWidget(cfg);
		    preview.addListener(CMDBEvents.MDR_GRID_AVAILIABLE, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					if (be.source instanceof GridModelConfig) {
						selectorTable.setSourceGridConfig((GridModelConfig)be.source);
					}
				}
		    	
		    });
	
		    south.add(preview);

		    layout();
	}

}
