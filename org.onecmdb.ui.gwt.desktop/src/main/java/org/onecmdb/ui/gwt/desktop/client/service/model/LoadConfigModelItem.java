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
package org.onecmdb.ui.gwt.desktop.client.service.model;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoadConfigModelItem extends ModelItem implements PagingLoadConfig {

	protected BasePagingLoadResult<CIModel> loadResult;

	public LoadConfigModelItem(CIModel root) {
		this();
		setRoot(root);
	}
	
	public LoadConfigModelItem() {
		setSortInfo(new SortInfo());
		setOffset(0);
	}
	
	public CIModel getRoot() {
		return(get("root"));
	}
	
	public void setRoot(CIModel root) {
		set("root", root);
	}
	public boolean isAllChildren() {
		if (get("allChildren") == null) {
			return(false);
		}
		return((Boolean)get("allChildren"));
	}
	
	public void setAllChildren(boolean value) {
		set("allChildren", value);
	}
	

	public void getChildren(ContentData mdr,
			final AsyncCallback<List<? extends ModelData>> callback) {
		
		
		ModelServiceFactory.get().getTemplateInstances(CMDBSession.get().getToken(), mdr, this, new AsyncCallback<BasePagingLoadResult<CIModel>>() {

			public void onFailure(Throwable arg0) {
				callback.onFailure(arg0);
			}
			

			public void onSuccess(BasePagingLoadResult<CIModel> arg0) {
				loadResult = arg0;
				
				callback.onSuccess(loadResult.getData());
			}
			
		});
	}
	/*
	public void getChildren2(ContentData mdr,
			final AsyncCallback<PagingLoadResult<CIModel>> callback) {
		
		
		ModelServiceFactory.get().getTemplateInstances(CMDBSession.get().getToken(), mdr, this, new AsyncCallback<BasePagingLoadResult<CIModel>>() {

			public void onFailure(Throwable arg0) {
				callback.onFailure(arg0);
			}
			

			public void onSuccess(BasePagingLoadResult<CIModel> arg0) {
				callback.onSuccess(arg0);
			}
			
		});
	}
	*/
	
	public BasePagingLoadResult<CIModel> getLoadResult() {
		return loadResult;
	}
	

	
	public boolean hasChildren() {
		return true;
	}

	/**
	 * PageLoading Config Implementations....
	 * ....
	 */

	public int getOffset() {
		return((Integer)get("offset"));
	}

	public void setLimit(int limit) {
		set("limit", limit);
	}
	
	public int getLimit() {
		return((Integer)get("limit"));
	}

	public void setOffset(int offset) {
		set("offset", offset);
	}

	public SortInfo getSortInfo() {
		String dir = (String)get("sortInfoDir");
		SortDir sortDir = SortDir.NONE;
		if (dir != null && dir.length() > 0) {
			sortDir = SortDir.valueOf(dir);
		}
		return(new SortInfo((String)get("sortInfoField"), sortDir));
	}

	public void setSortInfo(SortInfo info) {
		set("sortInfoDir", info.getSortDir().toString());
		set("sortInfoField", info.getSortField());
		
	}

	@Override
	public LoadConfigModelItem copy() {
		LoadConfigModelItem copy = new LoadConfigModelItem();
		copy(copy);
		return(copy);
	}

	public void setMatchTemplate(boolean b) {
		set("matchTemplate", b);
	}

	public boolean isMatchTemplate() {
		return(get("matchTemplate", false));
	}

	public void setQuery(String text) {
		set("query", text);
	}
	public String getQuery(String text) {
		return(get("query"));
	}

	
}
