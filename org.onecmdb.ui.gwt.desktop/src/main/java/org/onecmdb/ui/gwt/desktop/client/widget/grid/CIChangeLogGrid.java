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

import org.onecmdb.ui.gwt.desktop.client.fixes.MyGroupingView;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.HistoryModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.LoadConfigModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SplitToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CIChangeLogGrid extends LayoutContainer {

	private CIModel model;
	private ContentData mdrData;
	private int pageSize = 50;
	
	public CIChangeLogGrid(ContentData mdr, CIModel model) {
		this.model = model;
		this.mdrData = mdr;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}

	public void initUI() {
		setLayout(new FitLayout());
		
		
		RpcProxy proxy = new RpcProxy<LoadConfigModelItem, BasePagingLoadResult<HistoryModel>>() {


			@Override
			protected void load(LoadConfigModelItem loadConfig,
					AsyncCallback<BasePagingLoadResult<HistoryModel>> callback) {
				loadConfig.set("data", model);
				ModelServiceFactory.get().getHistory(mdrData, CMDBSession.get().getToken(), loadConfig, callback);
			}
			
		};
		
		
			
			
		final BasePagingLoader<LoadConfigModelItem, BasePagingLoadResult<HistoryModel>> loader = new BasePagingLoader<LoadConfigModelItem, BasePagingLoadResult<HistoryModel>>(proxy);  
		loader.setRemoteSort(true); 
		
		final LoadConfigModelItem loadConfig = new LoadConfigModelItem();
		loadConfig.setOffset(0);
		loadConfig.setLimit(pageSize );

		loader.useLoadConfig(loadConfig);
		loader.setReuseLoadConfig(true);
		
		final PagingToolBar pageToolBar = new PagingToolBar(pageSize);
		final SplitToolItem splitItem = new SplitToolItem("Value Changes");  
		splitItem.setIconStyle("list-items-icon");  
		   
		Menu menu = new Menu();  
		menu.add(new MenuItem("All Changes", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				splitItem.setText("All Changes");
				loadConfig.set("rfcType", "all");
				loader.load();
			}
			
		}));  
		menu.add(new MenuItem("Value Changes", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				splitItem.setText("Value Changes");
				loadConfig.set("rfcType", "values");
				loader.load();
			}
			
		}));  
		
		splitItem.setMenu(menu);  
		pageToolBar.add(new SeparatorToolItem());
		pageToolBar.add(splitItem);
		pageToolBar.bind(loader);
			
		GroupingStore<HistoryModel> store = new GroupingStore<HistoryModel>(loader);
		store.clearGrouping();
			
			
			
		final ColumnModel cm = new ColumnModel(getHistoryColumns());
			
		Grid<HistoryModel> grid = new Grid<HistoryModel>(store, cm);
			
		MyGroupingView view = new MyGroupingView();
		view.setForceFit(true);
		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(GroupColumnData data) {
				String f = cm.getColumnById(data.field).getHeader();
				String l = data.models.size() == 1 ? "Item" : "Items";
				return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
			}
		});
		grid.setView(view);
		grid.setBorders(true);
		grid.setLoadMask(true);
			
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());
		cp.setTopComponent(pageToolBar);
		cp.add(grid);
		add(cp);
		layout();
		loader.load();
	}

	private List<ColumnConfig> getHistoryColumns() {
		List<ColumnConfig> list = new ArrayList<ColumnConfig>();
		list.add(new ColumnConfig("txid", "TX-ID", 80));
		list.add(new ColumnConfig("rfcid", "RFC-ID", 80));
		list.add(new ColumnConfig("issuer", "Who", 80));
		list.add(new ColumnConfig("summary", "Summary", 250));
		ColumnConfig column = new ColumnConfig("ts", "Date", 150);
		column.setDateTimeFormat(CMDBSession.get().getDateTimeFormat()); 
		list.add(column);
				
		return(list);
		
	}
	

}
