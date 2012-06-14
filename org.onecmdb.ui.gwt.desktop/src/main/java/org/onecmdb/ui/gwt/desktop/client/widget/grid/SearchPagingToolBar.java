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

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.LoadConfigModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridColumnFilter;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.KeyboardListener;


public class SearchPagingToolBar extends PagingToolBar {

	class FilterSelector extends ComboBox<GridColumnFilter> {
		public FilterSelector(List<GridColumnFilter> filters) {
			 ListStore store = new ListStore();
			 store.add(filters);
			 setStore(store);
		}
	}

	@Override
	public void bind(PagingLoader loader) {
		super.bind(loader);
		if (loader instanceof BasePagingLoader) {
			
			config = getLoadConfig();
			((BasePagingLoadConfig)config).set("query", gridConfig.getQuery());
			config.setOffset(0);
			config.setLimit(pageSize);
			((BasePagingLoader)loader).setReuseLoadConfig(true);
			((BasePagingLoader)loader).useLoadConfig(config);
		}
		loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {

			public void handleEvent(LoadEvent be) {
				if (be.config instanceof BasePagingLoadConfig) {
					beforeLoad((BasePagingLoadConfig)be.config);
				}
			}
			
		});
	}
	
	protected void beforeLoad(BasePagingLoadConfig config) {
		//config.setOffset(start);
		//config.setLimit(pageSize);
		((BasePagingLoadConfig)config).set("searchText", mapNameToID(searchField.getValue()));
		SortInfo sortInfo = new SortInfo();
		sortInfo.setSortDir(loader.getSortDir());
		sortInfo.setSortField(loader.getSortField());
		config.setLimit(pageSize);
		config.setSortInfo(sortInfo);
	}

	private String mapNameToID(String search) {
		if (search == null) {
			return(null);
		}
		if (!search.contains("==")) {
			return(search);
		}
		String split[] = search.split("==");
		String colName = split[0];
		for (AttributeColumnConfig cfg : this.gridConfig.getColumnConfig()) {
			if (cfg.getName().equals(colName)) {
				colName = cfg.getId();
				break;
			}
		}
		return(colName + "==" + split[1]);
		
	}

	@Override
	public void refresh() {
		String pageSizeValue = pageSizeField.getValue();
		if (pageSizeValue != null) {
			int newPageSize = this.pageSize;
			try {
				newPageSize = Integer.parseInt(pageSizeValue);
			} catch (Exception t) {
			}
			setPageSize(newPageSize);
		}
		loader.load(start, pageSize);
		//doLoadRequest(start, pageSize);
	}

	public BasePagingLoadConfig getLoadConfig() {
		if (config == null) {
			config = new BasePagingLoadConfig();
		}
		return((BasePagingLoadConfig)config);
	}

	private GridModelConfig gridConfig;
	private TextField<String> searchField;
	private TextField<String> pageSizeField;
	private TextToolItem searchOptions;
	private Listener<MenuEvent> opSelection;
	private Listener<MenuEvent> searchOnListener;
	
	public SearchPagingToolBar(int pageSize, final GridModelConfig config) {
		super(pageSize);
		this.gridConfig = config;
		// Add Search tool Items.
		if (config.getGridColumnFilter() != null) {
			AdapterToolItem filter = new AdapterToolItem(new FilterSelector(config.getGridColumnFilter()));
			add(filter);
		}
		searchField = new TextField<String>();
		searchField.setToolTip("Search");
		searchField.setWidth(60);
		searchField.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				if (event.getKeyCode() ==  KeyboardListener.KEY_ENTER) {
					refresh();
				}
			}
			
		});
		add(new AdapterToolItem(searchField));
		IconButton searchButton = new IconButton("search-icon");;
		searchButton.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				refresh();
			}
			
		});
		add(new AdapterToolItem(searchButton));

		//add(new TextToolItem("", "search-icon"));
		searchOptions = new TextToolItem("Options");
		Menu rootMenu = new Menu();
		
		searchOptions.setMenu(rootMenu);
		{
			Menu menu = new Menu();
			MenuItem searchOn = new MenuItem("Search On");

			CheckMenuItem allItem = new CheckMenuItem("All Simple Attr.");
			
			allItem.setGroup("columns");
			allItem.addListener(Events.CheckChange, getSearchOnListener());
			allItem.setChecked(true);
			menu.add(allItem);
			for (AttributeColumnConfig cfg : this.gridConfig.getColumnConfig()) {
				if (!cfg.isSearchable()) {
					continue;
				}
				CheckMenuItem item = new CheckMenuItem(cfg.getName());
				item.setData("cfg", cfg);
				item.setGroup("columns");
				item.addListener(Events.CheckChange, getSearchOnListener());
				menu.add(item);
			}
			searchOn.setSubMenu(menu);
			rootMenu.add(searchOn);
		}
		{
			Menu menu = new Menu();
			MenuItem searchOp = new MenuItem("Operation");
			{
				CheckMenuItem item = new CheckMenuItem("Like");
				item.addListener(Events.CheckChange, getOperationListener());
				item.setChecked(true);
				//AttributeValueConstraint.LIKE == 5
				item.setGroup("operations");
				item.setData("operation", 5);
				item.setData("notOperation", false);
				menu.add(item);
			}
			{
				CheckMenuItem item = new CheckMenuItem("Not Like");
				item.setGroup("operations");
				//AttributeValueConstraint.LIKE == 5
				item.setData("operation", 5);
				item.setData("notOperation", true);
				menu.add(item);
				item.addListener(Events.CheckChange, getOperationListener());
			}
			{
				CheckMenuItem item = new CheckMenuItem("Equals");
				item.setGroup("operations");
				//AttributeValueConstraint.EQUALS == 0
				item.setData("operation", 0);
				item.setData("notOperation", false);
				item.addListener(Events.CheckChange, getOperationListener());
				menu.add(item);
			}
			{
				CheckMenuItem item = new CheckMenuItem("Not Equals");
				item.setGroup("operations");
				//AttributeValueConstraint.EQUALS == 0
				item.setData("operation", 0);
				item.setData("notOperation", true);
				item.addListener(Events.CheckChange, getOperationListener());
				menu.add(item);
			}
			{
				CheckMenuItem item = new CheckMenuItem("Is Empty");
				item.setGroup("operations");
				//AttributeValueConstraint.IS_NULL == 7
				item.setData("operation", 7);
				item.setData("notOperation", false);
				item.addListener(Events.CheckChange, getOperationListener());
				menu.add(item);
			}
			{
				CheckMenuItem item = new CheckMenuItem("Is Not Empty");
				item.setGroup("operations");
				//AttributeValueConstraint.IS_NULL
				item.setData("operation", 7);
				item.setData("notOperation", true);
				item.addListener(Events.CheckChange, getOperationListener());
				menu.add(item);
			}
			searchOp.setSubMenu(menu);
			rootMenu.add(searchOp);
		}
		add(searchOptions);
		add(new SeparatorToolItem());
		pageSizeField = new TextField<String>();
		pageSizeField.setWidth(30);
		pageSizeField.setValue("50");
		pageSizeField.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				if (event.getKeyCode() ==  KeyboardListener.KEY_ENTER) {
					refresh();
				}
			}
			
		});
		add(new AdapterToolItem(pageSizeField));
		add(new AdapterToolItem(new LabelField("Page Size")));
		//add(new AdapterToolItem(getSecurityGroupCombo()));
	}

	private Listener<MenuEvent> getOperationListener() {
		if (opSelection == null) {
			opSelection = new Listener<MenuEvent>() {

				public void handleEvent(MenuEvent be) {
					CheckMenuItem item = (CheckMenuItem) be.item;
					if (item.isChecked()) {
						getLoadConfig().set("searchOperation", item.getData("operation"));
						getLoadConfig().set("searchOperationNot", item.getData("notOperation"));
						
					} else {
										
					}
				}

			};
		}
		return(opSelection);
	}

	private Listener<MenuEvent> getSearchOnListener() {
		if (searchOnListener == null) {
			searchOnListener = new Listener<MenuEvent>() {

				public void handleEvent(MenuEvent be) {
					CheckMenuItem item = (CheckMenuItem) be.item;
					if (item.isChecked()) {
						getLoadConfig().set("searchAttributeConfig", item.getData("cfg"));
					} else {
										
					}
					}

			};
		}
		return(searchOnListener);
	}

	@Override
	protected void doLoadRequest(int offset, int limit) {
		if (config == null) {
			config = getLoadConfig();
			((BasePagingLoadConfig)config).set("query", gridConfig.getQuery());			
		}
		config.setLimit(limit);
		config.setOffset(offset);
		
		loader.load(config);
	}
	
	private ComboBox<CIModel> getSecurityGroupCombo() {
		final ComboBox<CIModel> combo = new ComboBox<CIModel>();
		combo.setPageSize(10);
		combo.setTriggerAction(TriggerAction.ALL);
		CIModel type = new CIModel();
		type.setAlias("CMDBSecurityGroup");

		combo.setStore(getComboStore(gridConfig.getMDR(), type, false));
		combo.setTypeAhead(true);
		combo.setDisplayField(CIModel.CI_DISPLAYNAME);
		
		return(combo);
	}
	
	private  ListStore<CIModel> getComboStore(final ContentData mdr, final CIModel type, final boolean matchTemplate) {
		RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<CIModel>> proxy = new RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<CIModel>>() {
			@Override
			protected void load(BasePagingLoadConfig loadConfig,
					final AsyncCallback<BasePagingLoadResult<CIModel>> callback) {
				
				LoadConfigModelItem load = new LoadConfigModelItem();
				load.setRoot(type);
				load.setLimit(loadConfig.getLimit());
				load.setOffset(load.getOffset());
				if (matchTemplate) {
					load.setAllChildren(true);
					ModelServiceFactory.get().getTemplateInstances(CMDBSession.get().getToken(), mdr, load, new AsyncCallback<BasePagingLoadResult<CIModel>>() {

						public void onFailure(Throwable arg0) {
							callback.onFailure(arg0);
						}


						public void onSuccess(BasePagingLoadResult<CIModel> arg0) {
							callback.onSuccess(arg0);
						}

					});
					
				} else {
					ModelServiceFactory.get().getTemplateInstances(CMDBSession.get().getToken(), mdr, load, new AsyncCallback<BasePagingLoadResult<CIModel>>() {

						public void onFailure(Throwable arg0) {
							callback.onFailure(arg0);
						}


						public void onSuccess(BasePagingLoadResult<CIModel> arg0) {
							callback.onSuccess(arg0);
						}

					});
				}
			}
		};
		
		BasePagingLoader loader = new BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<CIModel>>(proxy);
		ListStore<CIModel> store = new ListStore<CIModel>(loader);
	
		return(store);
	}

	
	
}
