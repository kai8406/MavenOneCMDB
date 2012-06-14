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
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.control.CIGridProxy;
import org.onecmdb.ui.gwt.desktop.client.control.GridModelConfigLoader;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InstanceList extends LayoutContainer {
	protected List<AttributeModel> attributes;
	private ContentData mdr;
	private ContentData gridData;
	private GridModelConfig gridConfig;
	//private CIModel model;
	private ListStore<CIModelCollection> store;
	private GridQueryLoader loader;
	private boolean readonly;
	private boolean selectable;
	private SelectionMode selectionMode;
	private Listener<SelectionChangedEvent> selectionListener;
	private String header;
	private List<String> filterPatterns;
	private boolean filterExclude = true;
	private CMDBPermissions permissions;


	public InstanceList(ContentData mdr, ContentData gridData, String colHeader) {
		this.mdr = mdr;
		this.gridData = gridData;
		this.header = colHeader;
	}

	
	
	public CMDBPermissions getPermissions() {
		return permissions;
	}



	public void setPermissions(CMDBPermissions permissions) {
		this.permissions = permissions;
	}



	public List<String> getFilterPatterns() {
		return filterPatterns;
	}



	public void setFilterPatterns(List<String> filterPatterns) {
		this.filterPatterns = filterPatterns;
	}

	public boolean isFilterExclude() {
		return filterExclude;
	}
	/**
	 * Decide if the filterPattern should be exclude/include filter.
	 * Defualt is true, means that the pattern is excluding all matches.
	 * @param filterExclude
	 */
	public void setFilterExclude(boolean filterExclude) {
		this.filterExclude = filterExclude;
	}



	public void setSelectable(boolean value) {
		this.selectable = value;
	}
	public void setSelectionMode(SelectionMode mode) {
		this.selectionMode = mode;
	}
	
	public void setSelectionListener(Listener<SelectionChangedEvent> selectionListener) {
		this.selectionListener = selectionListener;
	}

	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		loadGridConfig();
	}
	
	protected void loadGridConfig() {
		
		new GridModelConfigLoader(mdr, gridData, permissions).load(new AsyncCallback<GridModelConfig>() {

			public void onFailure(Throwable arg0) {
				ExceptionErrorDialog.showError("Problem loading grid config", arg0);
			}

			public void onSuccess(GridModelConfig arg0) {
				gridConfig = arg0;
				init(arg0.getColumns());
			}
		});
	}
	
	protected void init(List<ColumnConfig> cols) {
		setLayout(new FitLayout());
		
		CIGridProxy proxy = new CIGridProxy();
		
		
		loader = new GridQueryLoader(proxy, gridConfig);  
		loader.setRemoteSort(true);  
		
		store = new ListStore<CIModelCollection>(loader);
		
		
		CheckBoxSelectionModel<CIModelCollection> sm = null;
		if (this.selectable) {
			sm = new CheckBoxSelectionModel<CIModelCollection>();
			sm.setSelectionMode(selectionMode);
			if (this.selectionListener != null) {
				sm.addListener(Events.SelectionChange, this.selectionListener); 			
			}
			cols.add(0, sm.getColumn());
		}
		
		ArrayList<ColumnConfig> newCols = new ArrayList<ColumnConfig>();
		
		// Take the first column
		ColumnConfig selColumn = null;
		if (gridConfig.getAutoExpandColumnId() != null) {
			for (ColumnConfig c : cols) {
				if (c.getId().equals(gridConfig.getAutoExpandColumnId())) {
					selColumn = c;
					break;
				}
			}
		}
		if (selColumn == null) {
			selColumn = cols.get(0);
		}
		selColumn.setHeader(header);
		newCols.add(selColumn);
		
		final ColumnModel cm = new ColumnModel(newCols);
		if (filterPatterns != null) {
			store.applyFilters(selColumn.getId());
			store.addFilter(new StoreFilter<CIModelCollection>() {
	
				

				public boolean select(Store<CIModelCollection> store,
						CIModelCollection parent, CIModelCollection item,
						String property) {
					String value = item.get(property);
					if (value == null) {
						return(filterExclude);
					}
					for (String pattern : filterPatterns) {
					     if (value.matches(pattern)) {
					    	 return(!filterExclude);
					     }
					}
					return(filterExclude);
				}
			});
		}
		final Grid<CIModelCollection> grid = new Grid<CIModelCollection>(store, cm);
		//grid.setClicksToEdit(ClicksToEdit.TWO);
		
		/*
		if (gridConfig.getAutoExpandColumnId() != null) {
			grid.setAutoExpandColumn(gridConfig.getAutoExpandColumnId());
		}
		*/
		grid.setContextMenu(getGridContextMenu(grid));
		
		grid.setBorders(true);
		grid.setLoadMask(true);
		if (selectable) {
			grid.setSelectionModel(sm);
			grid.addPlugin(sm);
		} 
		if (selectionListener != null) {
			grid.getSelectionModel().addListener(Events.SelectionChange, this.selectionListener); 	
		}
		add(grid);
		layout();
		loader.load();
  }
	
	public Loader getLoader() {
		return(loader);
	}


	private Menu getGridContextMenu(final Grid grid) {
		Menu menu = new Menu();
		MenuItem item = new MenuItem("Properties", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				CellSelectionModel<ModelData> selectionModel = (CellSelectionModel<ModelData>) grid.getSelectionModel();
				ModelData data = selectionModel.getSelectCell().model;
			}
			
		});
		menu.add(item);
		
		return(menu);
	}



	


	public Listener<SelectionChangedEvent> getSelectionListener() {
		return selectionListener;
	}
}
