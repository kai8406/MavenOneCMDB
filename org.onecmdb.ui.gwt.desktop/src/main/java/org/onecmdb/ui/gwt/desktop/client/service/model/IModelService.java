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

import org.onecmdb.ui.gwt.desktop.client.service.CMDBLoginException;
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
import com.google.gwt.user.client.rpc.RemoteService;

public interface IModelService extends RemoteService {
	public CMDBSession autenticate(String username, String passwd) throws CMDBLoginException;
	public CMDBSession validateToken(String token) throws Exception;
	public CMDBSession autoLogin(String id) throws Exception;
	public Boolean logout(String token);
	
	public CMDBDesktopConfig getDesktopConfig(String username, String token, String role);
	
	public void saveUserPreferences(String token, String username, UserPreference preferences) throws CMDBRPCException;
	
	public List<CIModel> getTemplateChildren(String token, ContentData mdr, CIModel parent, List<String> roots) throws Exception;
	public BasePagingLoadResult<CIModel> getTemplateInstances(String token, ContentData mdr, LoadConfigModelItem loadConfig);
	public List<CIModel> getCIModel(String token, ContentData mdr, List<String> aliases);
	public BasePagingLoadResult<HistoryModel> getHistory(ContentData mdrData, String token, LoadConfigModelItem loadConfig);
	//public List<AttributeModel> getModelAttributes(String token, ContentData mdr, CIModel model);

	/**
	 * MDR(s)
	 */
	public BaseModel loadModelMDRInfo(String token, BaseModel param) throws CMDBRPCException;
	public BaseListLoadResult<BaseModel> loadMDROverview(String token, ContentData mdr, BaseListLoadConfig loadConfig);
	public CIModel execMDR(String token, CIModel mdr, CIModel mdrConfig) throws CMDBRPCException;
	public CIModel getLatsetMDRConfigHistory(String token, ContentData mdrData, CIModel cfgModel);

	
	/**
	 * Save Model.
	 */
	public SaveResponse save(String token, ContentData mdr, SaveDeleteRequest request);
	public SaveResponse delete(String token, ContentData mdr, SaveDeleteRequest request);
	/** 
	 * Default store procedure.
	 */
	public StoreResult store(ContentData mdr, String token, List<? extends ModelItem> local, List<? extends ModelItem> base);
	
	/**
	 * Load grid data
	 */
	public BaseListLoadResult<CIModelCollection> loadGrid(String token, BaseListLoadConfig config);
	public GridModelConfig loadGridConfig(String token, ContentData mdr, ContentData config) throws CMDBRPCException;

	/**
	 * Load relation tree
	 * @throws CMDBRPCException 
	 */
	public RelationCollectionModel loadRelationTypes(String token, ContentData mdrData, CIModel ci, GroupDescription desc) throws CMDBRPCException;
	public List<CIModel> loadRelations(String token, ContentData mdrData, RelationTypeModel relation) throws CMDBRPCException;

	/**
	 * Custom view
	 */
	public BaseModel loadCustomView(String token, ContentFile f);
	
	/**
	 * Group handling.
	 * @throws CMDBRPCException 
	 */
	public List<CITreeModel> loadGroup(String token, ContentData mdr, CITreeModel parent) throws CMDBRPCException;
	//public List<CITreeModel> loadGroupDefinitions();
	public BasePagingLoadResult<GroupCollection> loadGroupData(String token, BasePagingLoadConfig config) throws CMDBRPCException;

	/**
	 * Update handling.
	 */
	public String checkForNewUpdate(String token, boolean force) throws CMDBRPCException;
	
	
	/**
	 * MDR Desgin.
	 */
	public BaseModel createMDR(String token, BaseModel data) throws CMDBRPCException;
	public void deleteMDR(String token, BaseModel data) throws CMDBRPCException;
	public TransformConfig loadTransformConfig(String token, ContentData ciMDRData, CIModel mdr, CIModel mdrCfg) throws CMDBRPCException;
	public void createMDRConfig(String token, BaseModel data) throws CMDBRPCException;
	public void deleteMDRConfig(String token, BaseModel data) throws CMDBRPCException;
	public boolean storeTransformConfig(String token, ContentData ciMDRData, CIModel mdr, CIModel mdrCfg, TransformConfig cfg) throws CMDBRPCException;
	public TransformModel autoResolveTransformRelations(String token, TransformModel m) throws CMDBRPCException;
	public GridModelConfig loadDataSourceColumns(String token, TransformConfig config) throws CMDBRPCException;
	public BasePagingLoadResult<BaseModel> loadDataSourceData(String token, BasePagingLoadConfig config) throws CMDBRPCException;
	public List<BaseModel> calculateMDRInstances(String token, TransformConfig cfg) throws CMDBRPCException;
	
	
}
