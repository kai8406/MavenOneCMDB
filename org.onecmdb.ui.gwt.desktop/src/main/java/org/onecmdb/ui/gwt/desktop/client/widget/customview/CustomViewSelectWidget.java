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
package org.onecmdb.ui.gwt.desktop.client.widget.customview;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.action.CloseTextToolItem;
import org.onecmdb.ui.gwt.desktop.client.fixes.MyGroupingView;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBWidgetFactory;
import org.onecmdb.ui.gwt.desktop.client.window.content.CMDBContentBrowserWidget;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.XmlReader;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Element;

public class CustomViewSelectWidget extends LayoutContainer {
	
	private String customViewSource;
	private String root;
	private String recordName;

	
	public CustomViewSelectWidget(String root, String recordName) {
		this.root = root;
		this.recordName = recordName;
	}
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}

	public void initUI() {
		setLayout(new FitLayout());  
	   
	     List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
	  
	     
	     XTemplate tpl = XTemplate.create("<b>Description:</b><br/> {description}<br/><b>Definition:</b><br/>{definition}</br>");

		 RowExpander expander = new RowExpander();
		 expander.setTemplate(tpl);

		 columns.add(expander);
		 columns.add(new ColumnConfig("name", "Name", 100));  
		 columns.add(new ColumnConfig("group", "Group", 100));  
	     // create the column model  
	     final ColumnModel cm = new ColumnModel(columns);  
	   
	     // defines the xml structure  
	     ModelType type = new ModelType();  
	     type.root = this.root;  
	     type.recordName = this.recordName;
	     
	     type.addField("name", "name");  
	     type.addField("description");  
	     type.addField("group");  
	     type.addField("definition");  
		     
	    
	     // use a http proxy to get the data  
	     String sourceURL = CMDBSession.get().getContentRepositoryURL() + "/" + customViewSource;
	     RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, sourceURL);  
	     HttpProxy proxy = new HttpProxy(builder);  
	   
	     // need a loader, proxy, and reader  
	     XmlReader reader = new XmlReader(type);  
	   
	     BaseListLoader loader = new BaseListLoader(proxy, reader); 
	     GroupingStore<ModelData> store = new GroupingStore<ModelData>(loader);  
	     store.groupBy("group");
	     
	    	     
	     final Grid grid = new Grid<ModelData>(store, cm);  
	     grid.setBorders(true);  
	     grid.setAutoExpandColumn("name");  
	     grid.addListener(Events.RowDoubleClick, new Listener<GridEvent>() {

			public void handleEvent(GridEvent be) {
				// Open it...
				ModelData data = be.grid.getStore().getAt(be.rowIndex);
				openView(data);
			}
	     });
	     
	     grid.addPlugin(expander);
	     MyGroupingView view = new MyGroupingView();
	     view.setForceFit(false);
	     view.setGroupRenderer(new GridGroupRenderer() {
	    	 public String render(GroupColumnData data) {
	    		 String f = cm.getColumnById(data.field).getHeader();
	    		 String l = data.models.size() == 1 ? "Item" : "Items";
	    		 return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
	    	 }
	     });
	     grid.setView(view);
	    
	     ToolBar toolBar = new ToolBar();
	     TextToolItem open = new TextToolItem("Open", "open-icon");
	     open.setToolTip("Open selected view");
	     open.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				ModelData item = grid.getSelectionModel().getSelectedItem();
				if (item != null) {
					openView(item);
				}
			}
	     });
	     /*
	     TextToolItem close = new TextToolItem("Close", "close-icon");
	     close.setToolTip("Close this window");
	     close.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (getParent() instanceof Window) {
					((Window)getParent()).close();
				}
				if (getParent().getParent() instanceof Window) {
					((Window)getParent().getParent()).close();
				}
				
			}
	    	 
	     });
	     */
	     toolBar.add(new FillToolItem());
	     toolBar.add(open);
	     toolBar.add(new SeparatorToolItem());
	     toolBar.add(new CloseTextToolItem(this));
	     
	     ContentPanel panel = new ContentPanel();
	     panel.setHeaderVisible(false);
	     panel.setLayout(new FitLayout());
	     panel.add(grid);
	     panel.setBottomComponent(toolBar);
	     add(panel);
	  	 layout();
	  	 loader.load();

	}

	protected void openView(ModelData data) {
		String def = data.get("definition");
		ContentFile f = new ContentFile();
		f.setPath(def);
		ModelServiceFactory.get().loadCustomView(CMDBSession.get().getToken(), f, new CMDBAsyncCallback<BaseModel>() {

			@Override
			public void onSuccess(BaseModel arg0) {
				BaseModel widgets = arg0.get("widgets");
				Object o = widgets.get("widget");
				if (o instanceof List) {
					for (CMDBDesktopWindowItem item : (List<CMDBDesktopWindowItem>)o) {
						WindowFactory.showWindow(CMDBSession.get().getDesktop(), item);
					}
				}
			}
		});
	}


	public void setCustomFile(String customDef) {
		this.customViewSource = customDef;
		
	}
}
