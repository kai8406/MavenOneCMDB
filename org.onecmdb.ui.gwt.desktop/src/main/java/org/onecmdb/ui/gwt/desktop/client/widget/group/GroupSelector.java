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
package org.onecmdb.ui.gwt.desktop.client.widget.group;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.CITreeModel;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.util.ThemeManager;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GroupSelector extends ComboBox<CITreeModel> {

	public GroupSelector() {
		
		/*
		DataProxy<ListLoadConfig, ListLoadResult<CITreeModel>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<CITreeModel>>() {
			
			@Override
			protected void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<CITreeModel>> callback) {
				ModelServiceFactory.get().loadGroup(new CMDBAsyncCallback<List<CITreeModel>>() {

					@Override
					public void onSuccess(List<CITreeModel> arg0) {
						BaseListLoadResult<CITreeModel> result = new BaseListLoadResult<CITreeModel>(arg0);
						callback.onSuccess(result);
					}
				});
			}
		};
		
		ListLoader<ListLoadConfig> loader = new BaseListLoader<ListLoadConfig, ListLoadResult<CITreeModel>>(proxy );
		ListStore<CITreeModel> store = new ListStore<CITreeModel>(loader);
	    
	    setStore(store);
	    
	    setDisplayField("name");  
		*/
	}	
	
	
	
	
}
