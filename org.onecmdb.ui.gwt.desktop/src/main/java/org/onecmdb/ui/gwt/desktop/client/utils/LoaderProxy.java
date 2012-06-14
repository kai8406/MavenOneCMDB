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
package org.onecmdb.ui.gwt.desktop.client.utils;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.LoadConfigModelItem;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.TreeLoadEvent;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

public class LoaderProxy<C extends PagingLoadConfig> extends BasePagingLoader<C, PagingLoadResult> {

	private BaseTreeLoader loader;

	public LoaderProxy(DataProxy<C, PagingLoadResult> proxy) {
		super(proxy);
	}
	
	public void setBaseTreeLoader(BaseTreeLoader loader) {
		this.loader = loader;
		loader.addListener(Loader.BeforeLoad, new Listener() {

			public void handleEvent(BaseEvent be) {
				fireEvent(Loader.BeforeLoad, be);
			}
			
		});
	     loader.addListener(Loader.Load, new Listener() {

			public void handleEvent(BaseEvent be) {
				List<ModelData> data = new ArrayList<ModelData>();
				if (be instanceof TreeLoadEvent) {
					data = (List<ModelData>) ((TreeLoadEvent)be).data;
				}
				
				BasePagingLoadResult<ModelData> result = new BasePagingLoadResult(data);
				
				if (getLastConfig() instanceof LoadConfigModelItem) {
					LoadConfigModelItem lc = (LoadConfigModelItem)getLastConfig();
					if (lc.getLoadResult() != null) {
						result.setOffset(lc.getLoadResult().getOffset());
						result.setTotalLength(lc.getLoadResult().getTotalLength());
					}
				}
				
				LoadEvent event = new LoadEvent(LoaderProxy.this, new BasePagingLoadConfig(), result);
				
				fireEvent(Loader.Load, event);
			}
	    	 
	     });
	      loader.addListener(Loader.LoadException, new Listener() {

			public void handleEvent(BaseEvent be) {
				fireEvent(Loader.LoadException, be);
			}
	      });
	}

	@Override
	public boolean load(C loadConfig) {
		if (loader == null) {
			return(super.load(loadConfig));
		}
		return loader.load(loadConfig);
	}
	
	public C getLastConfig() {
		return(lastConfig);
	}
	
	

}
