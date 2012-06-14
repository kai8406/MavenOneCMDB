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

import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.CITreeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.RelationCollectionModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.RelationTypeModel;

import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IModelServiceAsync {
	public void autenticate(String username, String passwd, AsyncCallback<CMDBSession> callback);
	public void validateToken(String token, AsyncCallback<CMDBSession> callback);
	public void autoLogin(String id, AsyncCallback<CMDBSession> callback);
	public void logout(String token, AsyncCallback<Boolean> callback);
	
	public void getDesktopConfig(String username, String token, String role, AsyncCallback<CMDBDesktopConfig> callback);
	
	public void saveUserPreferences(String token, String username, UserPreference preferences, AsyncCallback<Void> callback);
	
	public void getTemplateChildren(String token, ContentData mdr, CIModel parent, List<String> roots, AsyncCallback<List<CIModel>> callback);
	public void getTemplateInstances(String token, ContentData mdr, LoadConfigModelItem loadConfig, AsyncCallback<BasePagingLoadResult<CIModel>> callback);
	public void getCIModel(String token, ContentData mdr, List<String> aliases, AsyncCallback<List<CIModel>> asyncCallback);
	//public void getModelAttributes(String token, ContentData mdr, CIModel model, AsyncCallback<List<AttributeModel>> asyncCallback);
	public void save(String token, ContentData mdr, SaveDeleteRequest request, AsyncCallback<SaveResponse> callback);
	public void delete(String token, ContentData mdr, SaveDeleteRequest request, AsyncCallback<SaveResponse> callback);
	public void getHistory(ContentData mdrData, String token, LoadConfigModelItem loadConfig, AsyncCallback<BasePagingLoadResult<HistoryModel>> callback);

	/**
	 * Model MDR info...
	 * @param token
	 * @return
	 * @throws CMDBRPCException
	 */
	public void loadModelMDRInfo(String token, BaseModel conf, AsyncCallback<BaseModel> callback);
	public void execMDR(String token, CIModel mdr, CIModel mdrConfig, AsyncCallback<CIModel> asyncCallback);
	public void loadMDROverview(String token, ContentData mdr,  BaseListLoadConfig loadConfig, AsyncCallback<BaseListLoadResult<BaseModel>> callback);
	public void getLatsetMDRConfigHistory(String token, ContentData mdrData, CIModel cfgModel,  AsyncCallback<CIModel> callback);

	
	/**
	 * Common Store interface.
	 */
	public void store(ContentData mdr, String token, List<? extends ModelItem> local, List<? extends ModelItem> base, AsyncCallback<StoreResult> callback);

	/**
	 * Load grid data.
	 * 
	 * @param token
	 * @param loadConfig
	 * @param callback
	 */
	public void loadGrid(String token, BaseListLoadConfig loadConfig, AsyncCallback<BaseListLoadResult<CIModelCollection>> callback);
	public void loadGridConfig(String token, ContentData mdr, ContentData config,  AsyncCallback<GridModelConfig> callback);

	/**
	 * Load Relation Tree
	 */
	public void loadRelationTypes(String token, ContentData mdrData, CIModel ci, GroupDescription desc, AsyncCallback<RelationCollectionModel> callback);
	public void loadRelations(String token, ContentData mdrData, RelationTypeModel relation, AsyncCallback<List<? extends ModelItem>> callback);
	
	/**
	 * Load custom views.
	 * @param token
	 * @param f
	 * @param asyncCallback
	 */
	public void loadCustomView(String token, ContentFile f, AsyncCallback<BaseModel> callback);
	
	/**
	 * Load a tree view.
	 * 
	 * @param token
	 * @param mdr
	 * @param parent
	 * @param callback
	 */
	public void loadGroup(String token, ContentData mdr, CITreeModel parent,
			AsyncCallback<List<CITreeModel>> callback);
	public void loadGroupData(String token, BasePagingLoadConfig config, 
			AsyncCallback<BasePagingLoadResult<GroupCollection>> callback);

	/**
	 * Update handling
	 */
	public void checkForNewUpdate(String token, boolean force, AsyncCallback<String> callback);

	/**
	 * MDR Handling...
	 */
	public void createMDR(String token, BaseModel data, AsyncCallback<BaseModel> callback);
	public void deleteMDR(String token, BaseModel data, AsyncCallback<Void> callback);
	public void createMDRConfig(String token, BaseModel data, AsyncCallback<Void> callback);
	public void deleteMDRConfig(String token, BaseModel data, AsyncCallback<Void> callback);
	public void autoResolveTransformRelations(String token, TransformModel m, AsyncCallback<TransformModel> callback);
	public void loadTransformConfig(String token, ContentData ciMDRData, CIModel mdr, CIModel mdrCfg, AsyncCallback<TransformConfig> callback);
	public void loadDataSourceColumns(String token, TransformConfig config, AsyncCallback<GridModelConfig> callback);
	public void loadDataSourceData(String token, BasePagingLoadConfig config, AsyncCallback<BasePagingLoadResult<BaseModel>> callback);
	public void storeTransformConfig(String token, ContentData ciMDRData, CIModel mdr, CIModel mdrCfg, TransformConfig cfg, AsyncCallback<Boolean> callback);
	public void calculateMDRInstances(String token, TransformConfig cfg, AsyncCallback<List<BaseModel>> callback);

	
}
