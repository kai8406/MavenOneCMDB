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
package org.onecmdb.ui.gwt.desktop.client.fixes;

import java.util.ArrayList;

import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AttributeSelectorField extends ComboBox<AttributeModel> {

	private AttributeColumnConfig config;
	private Grid grid;
	private int currentRow;

	public AttributeSelectorField(AttributeColumnConfig cfg) {
		super();
		this.config = cfg;
	
		setup();
	}
	public void init(Component comp) {
		if (comp instanceof Grid) {
			this.grid = (Grid)comp;
		}
	}
	
	
	@Override
	protected void onTriggerClick(ComponentEvent ce) {
		super.onTriggerClick(ce);
	}
	protected void setup() {
		// Setup Proxy..
		RpcProxy<ListLoadConfig, ListLoadResult<AttributeModel>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<AttributeModel>>() {

			@Override
			protected void load(ListLoadConfig loadConfig,
					AsyncCallback<ListLoadResult<AttributeModel>> callback) {
				ArrayList<AttributeModel> list = new ArrayList<AttributeModel>();
				ListLoadResult<AttributeModel> result = new BaseListLoadResult<AttributeModel>(list);
				
				
				if (grid != null & currentRow >= 0) {
					ModelData data = grid.getStore().getAt(currentRow);
					if (data != null && config.getCIProperty() != null) {
						Object obj = data.get(config.getCIProperty());
						if (obj instanceof CIModel) {
							CIModel ci = (CIModel)obj;
							for (AttributeModel a : ci.getAttributes()) {
								if ("simple".equals(config.getAttributeFilter())) {
									if (!a.isComplex()) {
										list.add(a);
									}
								} else if ("complex".equals(config.getAttributeFilter())) {
									if (a.isComplex()) {
										list.add(a);
									}
								} else {
									list.add(a);
								}
							}
							//list.addAll(ci.getAttributes());
						}
					}
				}
				callback.onSuccess(result);
			}
		};
		BaseListLoader<ListLoadConfig, ListLoadResult<AttributeModel>> loader = new BaseListLoader<ListLoadConfig, ListLoadResult<AttributeModel>>(proxy);
		
		ListStore<AttributeModel> store = new ListStore<AttributeModel>(loader);
		
		setStore(store);
		
		setDisplayField("alias");
	}
	
	
	
	@Override
	public void doQuery(String q, boolean forceAll) {
		// always load data.... 
		store.getLoader().load(getParams(q));
          expand();
        
		//super.doQuery(q, forceAll);
		
	}
	public void setCurrentRow(int row) {
		this.currentRow = row;
	}

	
	
	
}
