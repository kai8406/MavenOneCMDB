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
package org.onecmdb.ui.gwt.desktop.client.widget.group.table;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModelList;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.ListModelItem;

import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GroupProxy extends RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<GroupCollection>> {

	private GroupDescription desc;
	private String id;
	private GroupCollection scope;

	public GroupProxy(String id, GroupCollection scope, GroupDescription desc) {
		this.desc = desc;
		this.scope = scope;
		this.id = id;
	}
	
	@Override
	protected void load(BasePagingLoadConfig loadConfig,
			final AsyncCallback<BasePagingLoadResult<GroupCollection>> callback) {
		
		
		if (scope == null) {
			//loadTestData(callback);
			
			// Load new GroupDescription.
			loadConfig.set("groupDescription", desc);
			ModelServiceFactory.get().loadGroupData(CMDBSession.get().getToken(), loadConfig, new CMDBAsyncCallback<BasePagingLoadResult<GroupCollection>>() {

				
				@Override
				public void onFailure(Throwable t) {
					super.onFailure(t);
					callback.onFailure(t);
				}

				@Override
				public void onSuccess(
						BasePagingLoadResult<GroupCollection> arg0) {
					callback.onSuccess(arg0);		
				}
				
			});
			
			return;
		}
		
		// Need to transform the model.
		
		Object group = scope.get(id);
		List<GroupCollection> data = new ArrayList<GroupCollection>();
		if (group instanceof ListModelItem) {
			data = ((ListModelItem)group).toList();
		}
		BasePagingLoadResult<GroupCollection> result = new BasePagingLoadResult<GroupCollection>(data);
		result.setOffset(0);
		result.setTotalLength(data.size());
		callback.onSuccess(result);
	}

}
