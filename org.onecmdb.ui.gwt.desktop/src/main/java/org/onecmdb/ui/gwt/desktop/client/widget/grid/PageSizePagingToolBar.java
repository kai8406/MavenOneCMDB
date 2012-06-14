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


public class PageSizePagingToolBar extends PagingToolBar {


	@Override
	public void bind(PagingLoader loader) {
		super.bind(loader);
		
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
		
		SortInfo sortInfo = new SortInfo();
		sortInfo.setSortDir(loader.getSortDir());
		sortInfo.setSortField(loader.getSortField());
		config.setLimit(pageSize);
		config.setSortInfo(sortInfo);
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
	
	public PageSizePagingToolBar(int pageSize) {
		super(pageSize);
		add(new SeparatorToolItem());
		pageSizeField = new TextField<String>();
		pageSizeField.setWidth(30);
		pageSizeField.setValue("" + pageSize);
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
}
