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
package org.onecmdb.ui.gwt.desktop.client.window.mdr;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.PageSizePagingToolBar;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PreviewTableWidget extends LayoutContainer {

	private TransformConfig config;
	private GridModelConfig gridConfig;
	private int pageSize = 25;


	public PreviewTableWidget(TransformConfig config) {
		this.config = config;
	}
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setupUI();
	}



	protected void setupUI() {
		final MessageBox info = MessageBox.progress("Setup", "Fetch Columns", "wait....");
		
		ModelServiceFactory.get().loadDataSourceColumns(CMDBSession.get().getToken(), config, new CMDBAsyncCallback<GridModelConfig>() {
			
			
			@Override
			public void onFailure(Throwable t) {
				info.close();
				super.onFailure(t);
			}

			@Override
			public void onSuccess(GridModelConfig arg0) {
				info.close();
				gridConfig = arg0;
				fireEvent(CMDBEvents.MDR_GRID_AVAILIABLE, new BaseEvent(gridConfig));
				initUI();
				
			}
			
		});
	}
	
	protected void initUI() {
		
		// Create Proxy
		RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<BaseModel>> proxy = new RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<BaseModel>> () {

			@Override
			protected void load(BasePagingLoadConfig loadConfig, final AsyncCallback callback) {
				ModelServiceFactory.get().loadDataSourceData(CMDBSession.get().getToken(), 
						loadConfig, new CMDBAsyncCallback<BasePagingLoadResult<BaseModel>>() {
					
							
						
							@Override
							public void onFailure(Throwable t) {
								callback.onFailure(t);
								// Show error dialog...
								super.onFailure(t);
							}

							@Override
							public void onSuccess(
									BasePagingLoadResult<BaseModel> arg0) {
								callback.onSuccess(arg0);
								
							}
					
				});
			}

		};
		
		// Create Loader
		BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<BaseModel>> loader = new BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<BaseModel>>(proxy);  
			
		// Create Store
		ListStore<BaseModel> store = new ListStore<BaseModel>(loader);
		
		// Setup the load config.
		BasePagingLoadConfig loadConfig = new BasePagingLoadConfig();
		loadConfig.set("transformConfig", config);
		loadConfig.setOffset(0);
		loadConfig.setLimit(pageSize);
		loader.setReuseLoadConfig(true);
		loader.useLoadConfig(loadConfig);
		
		// Setup Column model.
		CMDBPermissions perm = new CMDBPermissions();
		perm.setCurrentState(CMDBPermissions.PermissionState.READONLY);
		List<ColumnConfig> columns = gridConfig.getColumnConfigs(null, perm);
		RowNumberer rowNumber = new RowNumberer();
		columns.add(0, rowNumber);
		ColumnModel cm = new ColumnModel(columns);
	
		// Create Grid
		Grid<BaseModel> grid = new Grid<BaseModel>(store, cm);
		grid.addPlugin(rowNumber);
		grid.setLoadMask(true);
		
		// Create Page Control
		PageSizePagingToolBar pageToolBar = new PageSizePagingToolBar(pageSize);
		pageToolBar.bind(loader);
		
		
		// Setup Layout
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setFrame(true);
		cp.setLayout(new FitLayout());
		
		// Add gid
		cp.add(grid);
		
		// Add Page Control
		cp.setBottomComponent(pageToolBar);
		
		// Add panel
		setLayout(new FitLayout());
		add(cp);
		
		cp.setLayoutOnChange(true);
		
		layout();
		
		loader.load();
	}
	
}
