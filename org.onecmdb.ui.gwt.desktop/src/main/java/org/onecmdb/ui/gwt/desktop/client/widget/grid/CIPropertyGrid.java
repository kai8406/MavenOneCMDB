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
package org.onecmdb.ui.gwt.desktop.client.widget.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.ExpressionHandler;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel.CellSelection;

import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Element;

public class CIPropertyGrid extends LayoutContainer {

	

	private BaseModel instance;
	private GridModelConfig gridConfig;
	private HashMap<String, AttributeColumnConfig> colId2AttrConfig = new HashMap<String, AttributeColumnConfig>();
	//private boolean readonly;
	private GroupingStore<ValueWrapper> store;
	private CMDBPermissions permissions;
	
	public CIPropertyGrid(GridModelConfig gridConfig, BaseModel instance) {
		this.gridConfig = gridConfig;
		for (AttributeColumnConfig aCfg : gridConfig.getColumnConfig()) {
			colId2AttrConfig.put(aCfg.getId(), aCfg);
		}
		this.instance = instance;
		//this.readonly = readonly;
	}
	
	public void setPermissions(CMDBPermissions perm) {
		this.permissions = (perm == null ? perm : perm.copy());
	}
	
	class MyColumnModel extends ColumnModel {

		public MyColumnModel(List<ColumnConfig> columns) {
			super(columns);
		}
		
		public void bind(Grid grid) {
			this.grid = grid;
		}
		
		@Override
		public CellEditor getEditor(int colIndex) {
			if (grid.getSelectionModel() instanceof CellSelectionModel) {
				CellSelectionModel selModel = (CellSelectionModel) grid.getSelectionModel();
				if (selModel.getSelectCell() != null) {
					ValueWrapper wr = (ValueWrapper) selModel.getSelectCell().model;
				
					return(wr.col.getEditor());
				}
			}
			
			return super.getEditor(colIndex);
		}

		@Override
		public GridCellRenderer getRenderer(int colIndex) {
			ColumnConfig cfg = config.get(colIndex);
			if (cfg instanceof RowExpander) {
				return(cfg.getRenderer());
			}
			
			return(new GridCellRenderer<ModelData>() {

				public String render(ModelData model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore<ModelData> store) {
				
					
					if (property.equals("value")) {
						if (model instanceof ValueWrapper) {
							ValueWrapper wrapper = (ValueWrapper)model;
							GridCellRenderer<ModelData> r = wrapper.col.getRenderer();
							if (r != null) {
								return(r.render(wrapper.m, wrapper.col.getId(), config, rowIndex, colIndex, store));
							}
						}
					}
					
					Object o = model.get(property);
					if (o == null) {
						return("");
					}
					return("<b>" + o.toString() + "</b>");
				}
				
			});
		}
	}
	
	public class ValueWrapper extends BaseModel {
		private ColumnConfig col;
		private BaseModel m;
		private AttributeColumnConfig aCfg;
		
		public ValueWrapper(ColumnConfig col, BaseModel m, AttributeColumnConfig aCfg) {
			this.col = col;
			this.m = m;
			this.aCfg = aCfg;
	
			
			if (aCfg != null) {
				set("description", aCfg.getDescription());
				set("id", col.getId());
				set("type", aCfg.getType());
				set("refType", aCfg.getRefType());
				if (aCfg.isInternal()) {
					set("group", "Internal");
				} else  {
					set("group", "User Defined");
				}
			} else {
				set("group", "Unknown");
			}
		}
	
		public <X> X set(String name, X value) {
			if (name.equals("value")) {
				m.set(col.getId(), value);
				return(value);
			}
			return(super.set(name, value));
		}
		
		
		@Override
		public <X> X get(String property) {
			if (property.equals("name")) {
				return((X)col.getHeader());
			}
			if (property.equals("value")) {
				if (m == null) {
					return((X)"");
				}
				return((X)m.get(col.getId()));
			}
			
			return((X)super.get(property));
			
		}
		
		public AttributeColumnConfig getAttributeConfig() {
			return(aCfg);
		}
		
		public BaseModel getModel() {
			return(m);
		}
	}
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}

	

	protected void initUI() {
		
		List<ValueWrapper> demoModels = new ArrayList<ValueWrapper>();
		
		for (ColumnConfig m : gridConfig.getColumns()) {
			//Object v = instance.get(m.getId());
			demoModels.add(new ValueWrapper(m, instance, colId2AttrConfig.get(m.getId())));
		}
		
		
		setLayout(new FitLayout());
		 
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>(); 
		 
		
		XTemplate tpl = XTemplate.create("<p><b>Description:</b> <br/> {description}<br/><b>Column ID:</b> {id}</p>");  
		RowExpander expander = new RowExpander();  
		expander.setTemplate(tpl);  
		configs.add(expander);  
		
		ColumnConfig column = new ColumnConfig();  
		column = new ColumnConfig();  
		column.setId("group");  
		column.setHeader("Group");  
		column.setWidth(70);
		column.setHidden(true);
		configs.add(column);  
		
		column = new ColumnConfig();
		column.setId("alias");  
		column.setHeader("Alias");  
		column.setWidth(130); 
		column.setHidden(true);
		//column.setEditor(editor);  
		configs.add(column);  
		
		column = new ColumnConfig();
		column.setId("name");  
		column.setHeader("Name");  
		column.setWidth(130);  
		//column.setEditor(editor);  
		configs.add(column);  
		 
		column = new ColumnConfig();
		column.setId("type");  
		column.setHeader("Type");  
		column.setWidth(100); 
		column.setHidden(true);
		//column.setEditor(editor);  
		configs.add(column);  
		
		column = new ColumnConfig();
		column.setId("refType");  
		column.setHeader("Reference Type");  
		column.setWidth(100);  
		//column.setEditor(editor);
		column.setHidden(true);
		configs.add(column);  
		
		
		column = new ColumnConfig();  
		column.setId("value");  
		column.setHeader("Value");  
		column.setWidth(250);  
		column.setEditor(new CellEditor(new TextField()));
		configs.add(column);  
		
		//final ListStore<ValueWrapper> store = new ListStore<ValueWrapper>();  
	
		store = new GroupingStore<ValueWrapper>();  
	    store.groupBy("group");
		store.sort("group", SortDir.DESC);
		store.add(demoModels);
		    
		final MyColumnModel cm = new MyColumnModel(configs);  
		   
	    ContentPanel cp = new ContentPanel();  
	    cp.setLayout(new FitLayout());  
		cp.setHeaderVisible(false);
	    //cp.setFrame(true);  
	    
	    
	    
	    Grid<ValueWrapper> grid = null;
	    if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
	       	grid = new Grid<ValueWrapper>(store, cm);
	       	grid.setSelectionModel(new CellSelectionModel<ValueWrapper>());
	    } else {
	      	grid = new EditorGrid<ValueWrapper>(store, cm);
	    }
	    
       	grid.setStripeRows(true);
	    
	    GroupingView view = new GroupingView();  
	    view.setGroupRenderer(new GridGroupRenderer() {  
	    	public String render(GroupColumnData data) {  
	    		String f = cm.getColumnById(data.field).getHeader();  
	    		String l = data.models.size() == 1 ? "Item" : "Items";  
	    		return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
	    	}  
	    });  
	    //view.setForceFit(true);
	    grid.setView(view);
	    
	    // Bind Column Model.
	    cm.bind(grid);
		
	    // Defualt plugin to handle mouse events.
	    grid.addPlugin(expander);
	    
	    // Add other ComponentPlugins.
	    for (ColumnConfig col : gridConfig.getColumns()) {
	    	CellEditor editor = col.getEditor();
	    	if (editor instanceof ComponentPlugin) {
	    		grid.addPlugin((ComponentPlugin)editor);
	    	}
	    	// Check this for checkbox editors!
	    	if (col instanceof ComponentPlugin) {
	    		grid.addPlugin((ComponentPlugin)col);
	    	}
	    }
	        
	  
	    grid.setContextMenu(getGridContextMenu(grid));
	    
	    
	    //grid.setAutoExpandColumn("name");  
	    grid.setBorders(true);
	    
	    cp.add(grid);  
	    add(cp);
	    
	    //layout();
	    
	    //view.refresh(false);
	}


	private Menu getGridContextMenu(final Grid<ValueWrapper> grid) {
		Menu menu = new Menu();
	
		final MenuItem open = new MenuItem("Open in Browser", "open-icon");
		open.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				String url = open.getData("url");
				com.google.gwt.user.client.Window.open(url,"OneCMDB_URL", "");
			}
			
		});
		open.setVisible(false);
		menu.add(open);
		
		menu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				open.setVisible(false);
				
				GridSelectionModel<ValueWrapper> selectionModel = (GridSelectionModel<ValueWrapper>) grid.getSelectionModel();
				if (!(selectionModel instanceof CellSelectionModel)) {
					return;
				}
				CellSelection cell = ((CellSelectionModel)selectionModel).getSelectCell();
				if (cell == null) {
					return;
				}
				if (!(cell.model instanceof ValueWrapper)) {
					return;
				}
				ValueWrapper wr = (ValueWrapper) cell.model;
				if (wr == null) {
					return;
				}
				AttributeColumnConfig aCfg = wr.getAttributeConfig();
				if (aCfg == null) {
					return;
				}
					
				if ("xs:anyURI".equals(aCfg.getType())) {
						open.setVisible(true);
						Object value = wr.get("value");
						String url = "";
						if (value != null) {
							url = value.toString();
						}
						if (value instanceof ValueModel) {
							url = ((ValueModel)value).getValue();
							url = ExpressionHandler.replaceURL(url);
						} 
						open.setData("url", url);
					}
				}
				
			
		});

		return(menu);
	}



	public boolean commit() {
		store.commitChanges();
		return(true);
	}
	
	public void restore() {
		store.rejectChanges();
	}


	

	

	
	
}
