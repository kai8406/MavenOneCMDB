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
package org.onecmdb.ui.gwt.desktop.client.control;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.LoadConfigModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CIProxy extends RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<CIModel>> {

	private boolean matchTemplate;
	private CIModel type;
	private ContentData mdr;

	public CIProxy(ContentData mdr, final CIModel type, final boolean matchTemplate) {
		this.mdr = mdr;
		this.type = type;
		this.matchTemplate = matchTemplate;
	}
	
	@Override
	protected void load(BasePagingLoadConfig loadConfig,
			final AsyncCallback<BasePagingLoadResult<CIModel>> callback) {
		LoadConfigModelItem load = new LoadConfigModelItem();
		load.setRoot(type);
		load.setLimit(loadConfig.getLimit()-1);
		load.setOffset(loadConfig.getOffset()-1);
		final String query = loadConfig.getParams().get("query");
		load.setQuery(query);
		
		if (matchTemplate) {
			load.setAllChildren(true);
			load.setMatchTemplate(true);
			//load.setLimit(-1);
			ModelServiceFactory.get().getTemplateInstances(CMDBSession.get().getToken(), mdr, load, new AsyncCallback<BasePagingLoadResult<CIModel>>() {

				public void onFailure(Throwable arg0) {
					callback.onFailure(arg0);
				}


				public void onSuccess(BasePagingLoadResult<CIModel> arg0) {
					
					// Uppdate nameAndIcon...
					List<CIModel> models = new ArrayList<CIModel>();
					for (CIModel m : arg0.getData()) {
						m.set(CIModel.CI_NAME_AND_ICON, m.getNameAndIcon());
						if (query != null) {
							if (m.getAlias().startsWith(query)) {
								models.add(m);
							}
						} else {
							models.add(m);
						}
					}
					// Add null value.
					
					CIModel empty = new CIModel();
					empty.set(CIModel.CI_NAME_AND_ICON, "[reset]");
					empty.set(CIModel.CI_DISPLAYNAME, "[reset]");
					models.add(0, empty);
					
					arg0.setData(models);
					arg0.setTotalLength(arg0.getData().size());
					callback.onSuccess(arg0);
				}

			});
			
		} else {
			load.setAllChildren(true);
			ModelServiceFactory.get().getTemplateInstances(CMDBSession.get().getToken(), mdr, load, new AsyncCallback<BasePagingLoadResult<CIModel>>() {

				public void onFailure(Throwable arg0) {
					callback.onFailure(arg0);
				}


				public void onSuccess(BasePagingLoadResult<CIModel> arg0) {
					for (CIModel m : arg0.getData()) {
						m.set(CIModel.CI_NAME_AND_ICON, m.getNameAndIcon());
					}
					// Add null value.
					CIModel empty = new CIModel();
					empty.set(CIModel.CI_NAME_AND_ICON, "[reset]");
					empty.set(CIModel.CI_DISPLAYNAME, "[reset]");
					arg0.getData().add(0, empty);
					arg0.setTotalLength(arg0.getTotalLength()+1);
					callback.onSuccess(arg0);
				}

			});
		}
	}

}
