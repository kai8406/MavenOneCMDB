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
package org.onecmdb.ui.gwt.desktop.server.service.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.authorization.RBACSession;
import org.onecmdb.core.internal.authorization.Role;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.OrderInfo;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemAndGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemNotConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemOrGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RFCTargetConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRFCSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.RFCBean;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBLoginException;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.content.IContentService;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.HistoryModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.IModelService;
import org.onecmdb.ui.gwt.desktop.client.service.model.LoadConfigModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.SaveDeleteRequest;
import org.onecmdb.ui.gwt.desktop.client.service.model.SaveItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.SaveResponse;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.UserPreference;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.CITreeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.RelationCollectionModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.RelationTypeModel;
import org.onecmdb.ui.gwt.desktop.client.utils.GXTModel2XML;
import org.onecmdb.ui.gwt.desktop.server.command.ExecCommand;
import org.onecmdb.ui.gwt.desktop.server.service.CMDBRPCHandler;
import org.onecmdb.ui.gwt.desktop.server.service.ServiceLocator;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;
import org.onecmdb.ui.gwt.desktop.server.service.change.OneCMDBWebServiceMDR;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentServiceImpl;
import org.onecmdb.ui.gwt.desktop.server.service.content.adapter.GXTModelContentAdapter;
import org.onecmdb.ui.gwt.desktop.server.service.model.group.GroupTransform;
import org.onecmdb.ui.gwt.desktop.server.service.model.mdr.AutoResolveRelation;
import org.onecmdb.ui.gwt.desktop.server.service.model.mdr.MDRSetupService;
import org.onecmdb.utils.xml.GraphQuery2XML;
import org.onecmdb.utils.xml.XML2GraphQuery;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ModelServiceImpl extends RemoteServiceServlet implements IModelService {



	private Log log = LogFactory.getLog(this.getClass());
	
	class GridResult {
		public List<CIModelCollection> data;
		public int totalCount;
	}
	
	public ModelServiceImpl() {
		super();
		log.info("Starting ModelServiceImpl....");
	}
	
	public CMDBSession validateToken(String token) throws Exception {
		log.info("validateToken(token=" + token + ")");
		IOneCMDBWebService service = CMDBWebServiceFactory.get().getOneCMDBWebService();
		/*
		CiBean bean = service.getAuthAccount(token);
		String username = bean.toStringValue("username");
		*/
		CMDBSession session = allocSession(service, token, "unknown");
		
		return(session);
	}

	public Boolean logout(String token) {
		try {
			IOneCMDBWebService service = CMDBWebServiceFactory.get().getOneCMDBWebService();
			service.logout(token);
		} catch (Throwable t) {
			// Silently ignore...
		}
		return(true);
	}
	
	public CMDBSession autoLogin(String id) throws Exception {
		log.info("autoLogin(id=" + id + ")");
		IOneCMDBWebService service = CMDBWebServiceFactory.get().getOneCMDBWebService();
		Config config = ConfigurationFactory.getConfig();
		String autoLoginDef = config.get(Config.AUTO_LOGIN_DEF);
		if (autoLoginDef == null) {
			autoLoginDef = "OneCMDB_Desktop/autologin/AutoLogin.xml";
		}
		ContentData data = new ContentData();
		data.setPath(autoLoginDef);
		
		BaseModel m = (BaseModel) ContentParserFactory.get().getCachedAdaptor(data, BaseModel.class);
		
		Object list = m.get("AutoLogin");
		if (list instanceof List) {
			for (BaseModel login : (List<BaseModel>)list) {
				if (id.equals(login.get("id"))) {
					String username = login.get("username"); 
					String passwd = login.get("password");
					return(autenticate(username, passwd));
				}
			}
		}
		log.warn("autoLogin(id=" + id + ") not found");
		throw new IllegalArgumentException("Not allowd");
	}

	protected CMDBSession allocSession(IOneCMDBWebService service, String token, String username) throws CMDBLoginException {
		CMDBSession session = new CMDBSession();
		session.setToken(token);
		session.setUsername(username);
		
		// Check if registartion has been asked for.
		IContentService cService = (IContentService) ServiceLocator.getService(IContentService.class);
		if (cService == null) {
			cService = new ContentServiceImpl();
		}
		// Load rbac..
		RBACSession rbac = service.getRBACSession(token);
	
		// Find desktop config for role/user.
		// TODO:
		String defaultRole = null;
		List<String> roles = new ArrayList<String>();
		for (Role role : rbac.getRoles()) {
			if (defaultRole == null) {
				defaultRole = role.getName();
			}
			roles.add(role.getName());
		}
		Config config = ConfigurationFactory.getConfig();
		String reqRole = config.get(Config.RequireRoleToLogin);
		if (reqRole != null && reqRole.equalsIgnoreCase("true")) {
			if (roles.size() == 0) {
				throw new CMDBLoginException("Login Failed", username + " not part of any role!");
			}
		}
		session.setRoles(roles);
		session.setDefaultRole(defaultRole);
		
		session.setDesktopConfig(getDesktopConfig(username, token, defaultRole));
		session.setConfig(config);
		
		// Load User preferences.
		session.setUserPreference(loadUserPreferences(token, username));

		ContentFile reg = new ContentFile();
		reg.setPath("OneCMDB_Desktop/users/registration");
		cService.stat(reg);
		session.setRegistration(!reg.isExists());
		session.setInstallId(getCMDB_ID());
		if (!reg.isExists()) {
			cService.create(token, reg);
		}
	
		return(session);
	}
	
	public CMDBSession autenticate(String username, String passwd) throws CMDBLoginException {
		log.info("autenticate(username=" + username + ", pwd=*******)");
		IOneCMDBWebService service = null;
		try {
			service = CMDBWebServiceFactory.get().getOneCMDBWebService();
		} catch (Exception e1) {
			throw new CMDBLoginException("Login Failed", "Can't contact OneCMDB Core WebService");
		}
		String token = null;
		
		try {
			token = service.auth(username, passwd);
		} catch (IllegalAccessException e) {
			throw new CMDBLoginException("Login Failed", "Bad credentials for " + username);
		} catch (Throwable t) {
			throw new CMDBLoginException("Login Failed", t.getMessage());
		} 
		
		log.info("autenticate(username=" + username + ", pwd=*******) --> token=" + token);
		
		CMDBSession session  = allocSession(service, token, username);
		
		return(session);
	
	}

	/**
	 * Search for a defaultView.xml according to the following.
	 * 1) OneCMDB_Desktop/roles/<roleName>/users/<username>/defaultView.xml
	 * 2) OneCMDB_Desktop/roles/<roleName>/defaultView.xml
	 * 3) OneCMDB_Desktop/defaultView.xml
	 * @param session
	 * @param role
	 * @return
	 */
	public CMDBDesktopConfig getDesktopConfig(String username, String token, String role) {
		log.info("getDesktopConfig(username=" + username + ", token=" + token + ", role=" + role + ")");
		
		Config config = ConfigurationFactory.getConfig();

		IContentService cService = (IContentService) ServiceLocator.getService(IContentService.class);
		CMDBDesktopConfig desktopConfig = null;
		
		if (role != null) {
			ContentData userConfig = new ContentData();
			userConfig.setPath((String)config.get(Config.DesktopRoleConfigPath) + "/" + role + "/users/" + username + "/desktopView.xml");
			cService.stat(userConfig);
			if (userConfig.isExists()) {
				log.info("getDesktopConfig(username=" + username + ", token=" + token + ", role=" + role + ") load " + userConfig.getPath());
				desktopConfig = (CMDBDesktopConfig) ContentParserFactory.get().getAdaptor(userConfig, CMDBDesktopConfig.class);
				return(desktopConfig);
			} 
			userConfig.setPath((String)config.get(Config.DesktopRoleConfigPath) + "/" + role + "/desktopView.xml");
			cService.stat(userConfig);
			if (userConfig.isExists()) {
				log.info("getDesktopConfig(username=" + username + ", token=" + token + ", role=" + role + ") load " + userConfig.getPath());
				desktopConfig = (CMDBDesktopConfig) ContentParserFactory.get().getAdaptor(userConfig, CMDBDesktopConfig.class);
				return(desktopConfig);
			} 
		}
		// Use global.
		ContentData configData = new ContentData();
		configData.setPath((String)config.get(Config.DesktopDefaultConfig));

		log.info("getDesktopConfig(username=" + username + ", token=" + token + ", role=" + role + ") load default " + configData.getPath());
		
		// Check if user have own view.
		desktopConfig = (CMDBDesktopConfig) ContentParserFactory.get().getAdaptor(configData, CMDBDesktopConfig.class);
		return(desktopConfig);
	}
	
	/**
	 * User Prefeferences.
	 * @throws CMDBRPCException 
	 */
	public void saveUserPreferences(String token, String username, UserPreference preferences) throws CMDBRPCException {
		Config config = ConfigurationFactory.getConfig();
		IContentService cService = (IContentService) ServiceLocator.getService(IContentService.class);
		ContentFolder folder = new ContentFolder(config.get(Config.DesktopUserConfigPath) + "/" + username);
		cService.stat(folder);
		if (!folder.isExists()) {
			cService.mkdir(token, folder);
		}
		ContentFile userPref = new ContentFile(config.get(Config.DesktopUserConfigPath) + "/" + username + "/desktop_preferences.xml");
		
		// Store preferences...
		String xml = GXTModel2XML.toXML("UserPreference", preferences);
		cService.put(token, userPref, xml);
	}
	
	public UserPreference loadUserPreferences(String token, String username) {
		Config config = ConfigurationFactory.getConfig();
		IContentService cService = (IContentService) ServiceLocator.getService(IContentService.class);
		ContentFile userPref = new ContentFile(config.get(Config.DesktopUserConfigPath) + "/" + username + "/desktop_preferences.xml");
		cService.stat(userPref);
		UserPreference pref = null;
		if (userPref.isExists()) {
			log.info("getUserPreferences(username=" + username + ", token=" + token + ") load " + userPref.getPath());
			try {
				pref = (UserPreference) ContentParserFactory.get().getAdaptor(userPref, UserPreference.class);
			} catch (Throwable t) {
				t.printStackTrace();
				log.warn("Can't load user preferences...", t);
			}
		} 
		if (pref == null) {
			pref = new UserPreference();
		}
		return(pref);
	}
	
	public SaveResponse delete(String token, ContentData mdrData, SaveDeleteRequest request) {
		log.info("delete(token=" + token + ", request=" + request + ")");
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		
		List<CiBean> deleteBeans = getSaveDeleteItems(mdr, token, request);
		// Don't delete Root CI and CIReference.
		CiBean ciBean = null;
		CiBean refBean = null;
		
		for (CiBean bean : deleteBeans) {
			if (bean.getAlias().equals("Ci")) {
				ciBean = bean;
			}
			if (bean.getAlias().equals("CIReference")) {
				refBean = bean;
			}
		}
		
		if (refBean != null) {
			deleteBeans.remove(refBean);
		}
	
		if (ciBean != null) {
			deleteBeans.remove(ciBean);
		}
		
		if (request.isVerify()) {
			SaveResponse result = new SaveResponse();
			updateSaveDeleteResponse(result, deleteBeans);
			return(result);
		}
		
		// Do the delete.
		IRfcResult result = mdr.update(token, new CiBean[0], deleteBeans.toArray(new CiBean[0]));
		SaveResponse resp = new SaveResponse();
		updateSaveDeleteResponse(resp, deleteBeans);
			
		resp.setFailed(result.isRejected());
		resp.setFailedCause(result.getRejectCause());
		log.info("delete(token=" + token + ")- rejected=" + result.isRejected() + ", cause=" + result.getRejectCause()); 
		return(resp);
	}


	public SaveResponse save(String token, ContentData mdrData, SaveDeleteRequest request) {
		log.info("export(token=" + token + ", request=" + request + ")");
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		
		List<CiBean> saveBeans = getSaveDeleteItems(mdr, token, request);
		
		// Do the save.
		XmlGenerator gen = new XmlGenerator();
		
		// Generate directory.
		File f = new File(ContentParserFactory.get().getRootPath() + "/" + request.getContent().getPath());
		if (f.getParentFile() != null && !f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		
		gen.setOutput(ContentParserFactory.get().getRootPath() + "/" + request.getContent().getPath());
		gen.setBeans(saveBeans);
		gen.setCompactMode(true);
		
		SaveResponse result = new SaveResponse();
		updateSaveDeleteResponse(result, saveBeans);
		result.setFailed(false);
		try {
			gen.process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Export failed:", e);
			result.setFailed(true);
			result.setFailedCause(e.toString());
		}
		log.info("export(token=" + token + ")- to=" + request.getContent().getPath() + ", failed=" + result.isFailed() + ", cause=" + result.getFailedCause()); 
		return(result);
	}

	public CIModel execMDR(String token, CIModel mdr, CIModel mdrConfig) throws CMDBRPCException {
		log.info("execMDR(token=" + token + ", mdr=" + mdr + ", config=" + mdrConfig + ")");
		ExecCommand cmd = new ExecCommand();
		cmd.setConfig(mdrConfig.getValueAsString("name"));
		cmd.setMdr(mdr.getValueAsString("name"));
		cmd.setVerbose("true");
		cmd.setCmd("start");
		cmd.setToken(token);
		try {
			File root = ContentParserFactory.get().getRootPath();
			if (root == null) {
				throw new CMDBRPCException("Internal Error", "can't find repositry root!", null);
			}
			String mdrHome = ConfigurationFactory.get(Config.MDR_HOME);
			if (mdrHome == null) {
				mdrHome = "MDR";
			}
			root = new File(root, mdrHome);
			cmd.setRoot(root.getCanonicalPath());
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new CMDBRPCException("Internal Error", "can't find repositry root!", null);
		}
		String historyTemplate = ConfigurationFactory.get(Config.MDR_HISTORY_ITEM);
		if (historyTemplate == null) {
			historyTemplate = MDRHistoryState.getHistoryTemplate();
		}
		cmd.setHistory(historyTemplate);
		try {
			cmd.transfer(System.out);
			CiBean history = cmd.getHistoryBean();
			if (history == null) {
				throw new CMDBRPCException("Internal Error", "No History Item created!", null);
			}
			if (cmd.getExecError() != null) {
				throw new CMDBRPCException("Internal Error", "Error running exec", CMDBRPCHandler.getStackTrace(cmd.getExecError()));
			}
			ICIMDR cmdb = CMDBWebServiceFactory.get().getOneCMDBCIMDR();
			CiBean parent = cmdb.getCI(token, history.getAlias());
			
			return(new Transform().convert(cmdb, token, parent, history));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CMDBRPCException("Internal Error", e.getMessage(), CMDBRPCHandler.getStackTrace(e));
		}
		
	}
	
	public BaseListLoadResult<BaseModel> loadMDROverview(String token,
			ContentData mdrData, BaseListLoadConfig loadConfig) {
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		
		GraphQuery q = new GraphQuery();
		
		ItemOffspringSelector config = new ItemOffspringSelector("config", "MDR_ConfigEntry");
		config.setMatchTemplate(false);
		config.setPrimary(true);
			//config.setPrimary(true);
	
		ItemOffspringSelector mdrD = new ItemOffspringSelector("mdr", "MDR_Repository");
		mdrD.setMatchTemplate(false);
		
		ItemRelationSelector config2mdr = new ItemRelationSelector("config2mdr", "Reference", "mdr", "config");
			
		/*
		ItemOffspringSelector history = new ItemOffspringSelector("history", "MDR_HistoryEntry");
		history.setMatchTemplate(false);
		history.setPageInfo(new PageInfo(0,1));
		
		ItemRelationSelector history2config = new ItemRelationSelector("history2config", "Reference", "config", "history");
		history2config.setMandatory(false);
		*/
		
		q.addSelector(config);
		q.addSelector(mdrD);
		q.addSelector(config2mdr);
		
	
		Graph queryResult = mdr.query(token, q);
		queryResult.buildMap();
		
		List<BaseModel> resultList = new ArrayList<BaseModel>();
		Template cfg = queryResult.fetchNode("config");
		if (cfg != null) {
			for (CiBean cfgBean : cfg.getOffsprings()) {
				BaseModel data = new BaseModel();
				
				CiBean mdrBean = null;
					
				Template mdrTemp = queryResult.fetchReference(cfgBean, RelationConstraint.SOURCE, config2mdr.getId());
				if (mdrTemp == null) {
					continue;
				}
				
				if (mdrTemp.getOffsprings().size() != 1) {
					continue;
				}
				mdrBean = mdrTemp.getOffsprings().get(0);
				
				CiBean historyBean = getLatestHistory(token, mdr, cfgBean);
					
				 data.set("name", cfgBean.toStringValue("name"));
				 data.set("mdr", mdrBean.toStringValue("name"));
				 data.set("mdrAlias", mdrBean.getAlias());
				 data.set("configAlias", cfgBean.getAlias());
			
				 if (historyBean != null) {
					 data.set("status", historyBean.toStringValue("status"));  
					 Date date = historyBean.getLastModified();
					 if (date != null) {
						 try {
							 SimpleDateFormat fmt = new SimpleDateFormat(ConfigurationFactory.getConfig().getDateTimeFmt());
							 data.set("date", fmt.format(date));
						 } catch (Throwable t) {
							 data.set("date", date.toString());
						}
					 }
					 data.set("added", historyBean.toStringValue("added"));
					 data.set("deleted", historyBean.toStringValue("deleted"));
					 data.set("modified", historyBean.toStringValue("modified"));
					 data.set("historyAlias", historyBean.getAlias());
						 
				 }
					
				 resultList.add(data);
			}
		}
		
		BaseListLoadResult<BaseModel> result = new BaseListLoadResult<BaseModel>(resultList);
		return(result);
		
	}

	public CIModel getLatsetMDRConfigHistory(String token, ContentData mdrData, CIModel cfgModel) {
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		CiBean cfgBean = new CiBean();
		cfgBean.setAlias(cfgModel.getAlias());
		CiBean bean = getLatestHistory(token, mdr, cfgBean);
		if (bean == null) {
			return(null);
		}
		CiBean template = mdr.getCI(token, bean.getDerivedFrom());
		Transform t = new Transform();
		CIModel model = t.convert(mdr, token, template, bean);
		return(model);
	}
	
	private CiBean getLatestHistory(String token, ICIMDR mdr, CiBean cfgBean) {
		GraphQuery q = new GraphQuery();
		
		ItemAliasSelector config = new ItemAliasSelector("config", "MDR_ConfigEntry");
		config.setAlias(cfgBean.getAlias());
	
			
		ItemOffspringSelector history = new ItemOffspringSelector("history", "MDR_HistoryEntry");
		history.setMatchTemplate(false);
		history.setPageInfo(new PageInfo(0,1));
		history.setPrimary(true);
		
		ItemRelationSelector history2config = new ItemRelationSelector("history2config", "Reference", "config", "history");
		history2config.setMandatory(true);
		
		q.addSelector(config);
		q.addSelector(history);
		q.addSelector(history2config);
			
		Graph queryResult = mdr.query(token, q);
		
		Template hist = queryResult.fetchNode(history.getId());
		if (hist == null) {
			return(null);
		}
		if (hist.getOffsprings().size() == 0) {
			return(null);
		}
		return(hist.getOffsprings().get(0));
	}
	
	

	public BaseModel loadModelMDRInfo(String token, BaseModel param) throws CMDBRPCException {
		log.info("loadModelMDRInfo(token=" + token + ")");
		try {
			ICIMDR mdr = CMDBWebServiceFactory.get().getOneCMDBCIMDR();

			GraphQuery query = new GraphQuery();
			ItemOffspringSelector mdrs = new ItemOffspringSelector("mdr", "MDR_Repository");
			mdrs.setPrimary(true);
			AttributeValueConstraint mdrCon = new AttributeValueConstraint();
			mdrCon.setAlias("name");
			mdrCon.setOperation(AttributeValueConstraint.EQUALS);
			String mdrName = (String)param.get("mdrName"); 
			mdrCon.setValue(mdrName);
			mdrs.applyConstraint(mdrCon);

			ItemOffspringSelector mdrConfigs = new ItemOffspringSelector("config", "MDR_Model_Config");
			AttributeValueConstraint mdrConfigCon = new AttributeValueConstraint();
			mdrConfigCon.setAlias("name");
			mdrConfigCon.setOperation(AttributeValueConstraint.EQUALS);
			String configName = (String)param.get("mdrConfigName");
			mdrConfigCon.setValue(configName);
			mdrConfigs.applyConstraint(mdrConfigCon);

			ItemRelationSelector rel = new ItemRelationSelector("config2mdr", "Reference", "mdr", "config");

			query.addSelector(mdrs);
			query.addSelector(mdrConfigs);
			query.addSelector(rel);

			Graph result = mdr.query(token, query);

			Template mdrTempl = result.fetchNode(mdrs.getId());
			if (mdrTempl == null) {
				throw new CMDBRPCException("Internal Error", "No Model MDR with name '" + param.get("mdrName") + "' found!", null);
			}
			if (mdrTempl.getOffsprings() == null || mdrTempl.getOffsprings().size() == 0) {
				throw new CMDBRPCException("Internal Error", "No Model MDR with name '" + param.get("mdrName") + "' found!", null);
			}
			if (mdrTempl.getOffsprings().size() != 1) {
				throw new CMDBRPCException("Internal Error", "More than one MDR with name '" + param.get("mdrName") + "' found!", null);
			}
			CiBean mdrBean = mdrTempl.getOffsprings().get(0);
			CIModel mdrModel = new Transform().convert(mdr, token, mdrTempl.getTemplate(), mdrBean);

			Template mdrConfigTempl = result.fetchNode(mdrConfigs.getId());
			if (mdrConfigTempl == null) {
				throw new CMDBRPCException("Internal Error", "No Model MDR Config with name '" + param.get("mdrName") + "' found!", null);
			}
			if (mdrConfigTempl.getOffsprings() == null || mdrConfigTempl.getOffsprings().size() == 0) {
				throw new CMDBRPCException("Internal Error", "No Model MDR Config with name '" + param.get("mdrName") + "' found!", null);
			}
			if (mdrConfigTempl.getOffsprings().size() != 1) {
				throw new CMDBRPCException("Internal Error", "More than one MDR Config with name '" + param.get("mdrName") + "' found!", null);
			}
			CiBean mdrConfigBean = mdrConfigTempl.getOffsprings().get(0);
			CIModel mdrConfigModel = new Transform().convert(mdr, token, mdrConfigTempl.getTemplate(), mdrConfigBean);


			BaseModel model = new BaseModel();
			model.set("mdr", mdrModel);
			model.set("mdrConfig", mdrConfigModel);
			log.info("loadModelMDRInfo(token=" + token + ") OK");
			return(model);

		} catch (Exception  e) {
			log.error("loadModelMDRInfo(token=" + token + ") FAILED", e);
			
			if (e instanceof CMDBRPCException) {
				throw (CMDBRPCException)e;
			}
			throw new CMDBRPCException("Internal error", "Can't load Model info", CMDBRPCHandler.getStackTrace(e));
		}
		
	}
	
	public List<CIModel> getTemplateChildren(String token, ContentData mdrData, CIModel parent, List<String> roots) throws Exception {
		log.info("getTemplateChildren(token=" + token + ", parent=" + parent + ", roots=" + roots + ")");
		List<CIModel> items = new ArrayList<CIModel>();
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		
		if (parent == null) {
			if (roots == null) {
				roots = new ArrayList<String>();
				roots.add(ConfigurationFactory.get(Config.OneCMDBRootCI));
				roots.add(ConfigurationFactory.get(Config.OneCMDBRootReference));
			}
			for (String root : roots) {
				CiBean ci = mdr.getCI(token, root);
				if (ci != null) {
					CIModel model = new Transform().convert(mdr, token, ci, ci);
					items.add(updateCount(model, mdr, token, ci));
				}	
			}
			log.info("getTemplateChildren(token=" + token + ") OK [result=" + items.size() + "]");

			return(items);
		} else {
			ItemOffspringSelector offsprings = new ItemOffspringSelector("offsprings", (String)parent.getAlias());
			offsprings.setMatchTemplate(true);
			offsprings.setPrimary(true);
			offsprings.setLimitToChild(true);
			GraphQuery q = new GraphQuery();
			q.addSelector(offsprings);
			
			Graph g = mdr.query(token, q);
			g.buildMap();
			Template t = g.fetchNode(offsprings.getId());
			
			if (t != null && t.getOffsprings() != null) {
				Transform transform = new Transform();
				transform.setCache(g);
				for (CiBean bean : t.getOffsprings()) {
					items.add(updateCount(transform.convert(mdr, token, bean, bean), mdr, token, bean));
				}
				//items.addAll(convert(mdr, token, null, t.getOffsprings()));
			}
		}
		log.info("getTemplateChildren(token=" + token + ") OK [result=" + items.size() + "]");
		return(items);
	}
	
	private CIModel updateCount(CIModel model, ICIMDR mdr, String token, CiBean ci) {
		Object decorate = ConfigurationFactory.get(Config.DECORATE_TEMPLATE_COUNT);
		if (decorate != null && decorate.toString().equals("true")) {
			model.setProperty(CIModel.CI_TOTAL_INSTANCE_COUNT, getChildCount(mdr, token, ci, false, false));
			model.setProperty(CIModel.CI_INSTANCE_CHILD_COUNT, getChildCount(mdr, token, ci, true, false));
		}
		model.setProperty(CIModel.CI_TEMPLATE_CHILD_COUNT, getChildCount(mdr, token, ci, true, true));
		
		return(model);
	}
	
	private int getChildCount(ICIMDR mdr, String token, CiBean template, boolean limitToChild, boolean matchTemplate) {
		ItemOffspringSelector offsprings = new ItemOffspringSelector("offsprings", (String)template.getAlias());
		offsprings.setMatchTemplate(matchTemplate);
		offsprings.setLimitToChild(limitToChild);
		offsprings.setPrimary(true);
		offsprings.setPageInfo(new PageInfo(0, 0));
		GraphQuery q = new GraphQuery();
		q.addSelector(offsprings);
		
		
		Graph size = mdr.query(token, q);
		Template sizeT = size.fetchNode(offsprings.getId());
		if (sizeT == null) {
			return(-1);
		}
		return(sizeT.getTotalCount());
	}
	
	
	public List<CIModel> getCIModel(String token, ContentData mdrData, List<String> aliases) {
		log.info("getCIModel(token=" + token + ", aliases=" + aliases + ")");
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		GraphQuery q = new GraphQuery();
		ItemOffspringSelector sel2 = new ItemOffspringSelector("cache_serach", "Root");
		q.addSelector(sel2);
		for (String alias : aliases) {
			ItemAliasSelector aSel = new ItemAliasSelector(alias, "Root");
			aSel.setAlias(alias);
			aSel.setPrimary(true);
			q.addSelector(aSel);
			
			ItemRelationSelector rel = new ItemRelationSelector(alias + "2ref", "Reference", sel2.getId(), aSel.getId());
			q.addSelector(rel);
			aSel.addExcludeRelation(rel.getId());
		}
		
		Graph result = mdr.query(token, q);
		result.buildMap();
		
		List<CIModel> models = new ArrayList<CIModel>();
		Transform transform = new Transform();
		transform.setCache(result);
		for (String alias : aliases) {
			Template t = result.fetchNode(alias);
			if (t == null || t.getOffsprings() == null) {
				continue;
			}
		
			for (CiBean bean : t.getOffsprings()) {
				CiBean template = bean;
				if (!bean.isTemplate()) {
					template = transform.getCI(mdr, token, bean.getDerivedFrom());
				}
				CIModel model = transform.convert(mdr, token, template, bean);
				models.add(model);
			}
		}
		log.info("getCIModel(token=" + token + ", aliases=" + aliases + ") OK [result=" + models.size() + "]");
		return(models);
	}
	
	public BasePagingLoadResult<HistoryModel> getHistory(ContentData mdrData, String token, LoadConfigModelItem loadConfig) {
		log.info("getHistory(token=" + token + ",loadConfig=" + loadConfig + ")");
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		
		String rfcType = ItemRFCSelector.RFC_MODIFY_VALUE_TYPE;; 
		
		
		String type = loadConfig.get("rfcType");
		if ("all".equalsIgnoreCase(type)) {
			rfcType = ItemRFCSelector.RFC_ANY_TYPE;
		} else if ("values".equals(type)) {
			rfcType = ItemRFCSelector.RFC_MODIFY_VALUE_TYPE;
		} else if ("new".equals(type)) {
			rfcType = ItemRFCSelector.RFC_NEW_CI_TYPE;
		}
		
		GraphQuery query = new GraphQuery();
		ItemRFCSelector rfc = new ItemRFCSelector("history", rfcType);
		rfc.setPrimary(true);
		
		ItemOrGroupConstraint or = new ItemOrGroupConstraint();
		ModelItem item = loadConfig.get("data");
		if (item instanceof CIModel) {
			CIModel model = (CIModel)item;

			or.add(new RFCTargetConstraint(Long.parseLong(model.getIdAsString())));
			for (ValueModel v : model.getValues()) {
				if (v instanceof ValueListModel) {
					for (ValueModel vv : ((ValueListModel)v).getValues()) {
						or.add(new RFCTargetConstraint(Long.parseLong(vv.getIdAsString())));
					}
				} else {
					or.add(new RFCTargetConstraint(Long.parseLong(v.getIdAsString())));
				}
			}
		} 
		if (item instanceof AttributeModel) {
			AttributeModel model = (AttributeModel)item;
			or.add(new RFCTargetConstraint(Long.parseLong(model.getIdAsString())));
		}

		if (item instanceof ValueModel) {
			ValueModel model = (ValueModel)item;
			or.add(new RFCTargetConstraint(Long.parseLong(model.getIdAsString())));
		}
		
		rfc.applyConstraint(or);
		
		if (loadConfig.getLimit() > 0) {
			PageInfo pageInfo = new PageInfo(loadConfig.getOffset(), loadConfig.getLimit());
			rfc.setPageInfo(pageInfo);
		}
		query.addSelector(rfc);
		
		Graph resultGraph = mdr.query(token, query);
		Template t = resultGraph.fetchNode("history");
		List<RFCBean> beans = t.getRFC();
		List<HistoryModel> items = new Transform().transform(beans);
		
		BasePagingLoadResult<HistoryModel> result = new BasePagingLoadResult<HistoryModel>(items);
		result.setOffset(loadConfig.getOffset());
		result.setTotalLength(t == null ? 0 : t.getTotalCount());
		log.info("getHistory(token=" + token + ",loadConfig=" + loadConfig + ") OK [result=" + items.size() + "]");
		return(result);
	}
	
	
	public BasePagingLoadResult<CIModel> getTemplateInstances(String token,
			ContentData mdrData, LoadConfigModelItem loadConfig) {
		log.info("loadTemplateInstances(token=" + token + ", loadConfig=" + loadConfig + ")");

		List<CIModel> items = new ArrayList<CIModel>();
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		
		ItemOffspringSelector offsprings = new ItemOffspringSelector("offsprings", (String)loadConfig.getRoot().get(CIModel.CI_ALIAS));
		offsprings.setMatchTemplate(loadConfig.isMatchTemplate());
		offsprings.setPrimary(true);
		if (loadConfig.isAllChildren()) {
			offsprings.setLimitToChild(false);
		} else {
			offsprings.setLimitToChild(true);
		}
		if (loadConfig.getLimit() > 0) {
			PageInfo pageInfo = new PageInfo(loadConfig.getOffset(), loadConfig.getLimit());
			offsprings.setPageInfo(pageInfo);
		}
		
		String search = loadConfig.get("query");
		if (search != null && search.length() > 0) {
			ItemAndGroupConstraint and = new ItemAndGroupConstraint();
			
			AttributeValueConstraint vCon = SearchAttributeHandler.parse(search, null); 
			and.add(vCon);
			if (loadConfig.isMatchTemplate()) {
				vCon.setValueType(AttributeValueConstraint.INTERNAL_ALIAS);
			}
			if (offsprings.fetchConstraint() != null) {
				and.add(offsprings.fetchConstraint());
			}
			offsprings.applyConstraint(and);
		}
		
		GraphQuery q = new GraphQuery();
		q.addSelector(offsprings);
		
		Graph g = mdr.query(token, q);
		g.buildMap();
		Template t = g.fetchNode(offsprings.getId());
		if (t != null && t.getOffsprings() != null) {
			Transform transform = new Transform();
			transform.setCache(g);
			for (CiBean bean : t.getOffsprings()) {
				items.add(transform.convert(mdr, token, t.getTemplate(), bean));
			}
		}
		BasePagingLoadResult<CIModel> result = new BasePagingLoadResult<CIModel>(items);
		result.setOffset(loadConfig.getOffset());
		result.setTotalLength(t == null ? 0 : t.getTotalCount());
		
		log.info("loadTemplateInstances(token=" + token + ", loadConfig=" + loadConfig + ") OK [result=" + items.size() + "]");
		
		return(result);
			
	}

	
	public BasePagingLoadResult<GroupCollection> loadGroupData(String token, BasePagingLoadConfig config) throws CMDBRPCException {
		
		try {
			GroupDescription desc = config.get("groupDescription");
			ContentData mdrData = desc.getMDR();
			if (mdrData == null) {
				String path = ConfigurationFactory.getConfig().get(Config.OneCMDBWebService);
				mdrData = new ContentFile(path);
			}
			String xmlQuery = desc.getQuery();
			XML2GraphQuery parser = new XML2GraphQuery();
			GraphQuery query = parser.parse(xmlQuery);
			
			ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
			
			// TODO: Handle Paging...
			ItemSelector prim = query.fetchPrimarySelectors();
			prim.setPageInfo(new PageInfo(config.getOffset(), config.getLimit()));
			Graph result = mdr.query(token, query);
			result.buildMap();
			GroupTransform gTransform = new GroupTransform();
			List<GroupCollection> group = gTransform.generateGroupData(mdr, token, query, result);
			BasePagingLoadResult<GroupCollection> data = new BasePagingLoadResult<GroupCollection>(group);
			
			data.setOffset(config.getOffset());
			data.setTotalLength(result.fetchNode(prim.getId()).getTotalCount());
			
			return(data);
		} catch (Throwable t) {
			throw new CMDBRPCException("Load Group Data", t.getMessage(), CMDBRPCHandler.getStackTrace(t));	
		}
	
	}
	
	
	/*
	public List<CIGroupItem> getGroupMemebers(ContentData mdrData, String token, String refId, CIGroupItem parent, GroupDescription desc) throws CMDBRPCException {
		List<CIGroupItem> result = new ArrayList<CIGroupItem>();
		try {

			String xmlQuery = desc.getQuery();
			XML2GraphQuery parser = new XML2GraphQuery();
			Map map = desc.getParams();
			parser.setAttributeMap(map);
			GraphQuery q = parser.parse(xmlQuery);
			
			GraphQuery query = new GraphQuery();
			String resultSelectorId = null;
			if (parent == null) {
				ItemSelector primary = q.fetchPrimarySelectors();
				query.addSelector(primary);
				resultSelectorId = primary.getId();;
			} else {
				
				// Add ItemAlias selector.
				ItemAliasSelector aliasSel = new ItemAliasSelector(parent.getSelectorId(), parent.getCI().getDerivedFrom());
				aliasSel.setAlias(parent.getCI().getAlias());
				query.addSelector(aliasSel);

				// Add References to this.
				ItemRelationSelector relSel = (ItemRelationSelector) q.findSelector(refId);
				query.addSelector(relSel);
					
				// Add Target/Source
				String targetId = relSel.getTarget();
				if (parent.getSelectorId().equals(relSel.getSource())) {
					targetId = relSel.getSource();
				}
				query.addSelector(q.findSelector(targetId));
				resultSelectorId = targetId;
			}
			// Find all references to from this 
			List<String> refs = new ArrayList<String>();
			for (ItemRelationSelector rel : q.fetchRelationSelectors()) {
				if (refId.equals(rel.getId())) {
					continue;
				}
				if (rel.getTarget().equals(resultSelectorId)) {
					refs.add(rel.getId());
					continue;
				}
				if (rel.getSource().equals(resultSelectorId)) {
					refs.add(rel.getId());
					continue;
				}
			}
			
			
			// Do Query...
			ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
			Graph graph = mdr.query(token, query);
			graph.buildMap();

			
			Transform transform = new Transform();
			transform.setCache(graph);
			Template t = graph.fetchNode(resultSelectorId);
			for (CiBean bean : t.getOffsprings()) {
				CIModel m = transform.convert(mdr, token, t.getTemplate(), bean);
				CIGroupItem item = new CIGroupItem();
				item.setCI(m);
				item.setReferences(refs);
				item.setSelectorId(resultSelectorId);
			}
		} catch (Throwable t) {
			throw new CMDBRPCException("Load Group", t.getMessage(), CMDBRPCHandler.getStackTrace(t));		
		}
		return(result);
	}
	*/
	public List<CITreeModel> loadGroupDefinitions(String token, ContentData mdrData, ContentData root)  {
		
		IContentService cService = (IContentService) ServiceLocator.getService(IContentService.class);
		if (cService == null) {
			cService = new ContentServiceImpl();
		}
		List<CITreeModel> models = new ArrayList<CITreeModel>();
		List<? extends ContentData> list = cService.list(null, root);
		for (ContentData data : list) {
			if (data instanceof ContentFile) {
				if (data.getPath().endsWith(".xml")) {
					GroupDescription group = (GroupDescription) ContentParserFactory.get().getAdaptor(data, GroupDescription.class);
					group.setMDR(mdrData);
					CITreeModel m = new CITreeModel();
					m.setName(group.getName());
					m.set(CITreeModel.ICON_PATH, group.get("icon"));
					m.setGroupDescription(group);
					models.add(m);
				}
			}
			
			if (data instanceof ContentFolder) {
				CITreeModel m = new CITreeModel();
				m.setName(data.getName());
				m.setPath(data.getPath());
				m.setFolder(true);
				models.add(m);
			}
		}
		return(models);
		
	}
	
	
	/**
	 * Load group service
	 */
	public List<CITreeModel> loadGroup(String token, ContentData mdrData,
			CITreeModel parent) throws CMDBRPCException {
		try {
			if (parent == null) {
				ContentData root = new ContentData();
				root.setPath("Groups");
				return(loadGroupDefinitions(token, mdrData, root));
			}
			if (parent.isFolder()) {
				ContentData data = new ContentData();
				data.setPath(parent.getPath());
				return(loadGroupDefinitions(token, mdrData, data));
			}
			List<CITreeModel> resultList = new ArrayList<CITreeModel>();
			/*
			String path = parent.getGroupDescription();
			ContentFile f = new ContentFile(path);
			GroupDescription group = (GroupDescription) ContentParserFactory.get().getAdaptor(f, GroupDescription.class);

			ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
			GraphQuery q = group.getQuery();
			Graph result = mdr.query(token, q);
			ItemSelector sel = q.fetchPrimarySelectors();


			Transform transform = new Transform();
			result.buildMap();
			transform.setCache(result);

			Template t = result.fetchNode(sel.getId());
			resultList = populateTree(group, mdr, token, q, t, result, transform, sel);
			*/
			return(resultList);
		} catch (Throwable t) {
			throw new CMDBRPCException("Error ", "Group Load Error", CMDBRPCHandler.getStackTrace(t));
		}
	}
	/*
	protected List<CITreeModel> populateTree(GroupDescription desc, ICIMDR mdr, String token, GraphQuery q, Template t, Graph result, Transform transform, ItemSelector sel) {
		List<CITreeModel> modelList = new ArrayList<CITreeModel>();
		
		for (CiBean bean : t.getOffsprings()) {
			CITreeModel tree = new CITreeModel();
			tree.setCIModel(transform.convert(mdr, token, t.getTemplate(), bean));
			modelList.add(tree);
			
			for (ItemRelationSelector rel : q.fetchRelationSelectors()) {
				String groupName = desc.getRelationGroupName(rel.getId());
				
				if (rel.getTarget().equals(sel.getId())) {
					CITreeModel m = new CITreeModel();
					m.setName(groupName);
					tree.addChild(m);
					Template refs = result.fetchReference(bean, RelationConstraint.SOURCE, rel.getId());
					m.setChildren(populateTree(desc, mdr, token, q, refs, result, transform, sel));
				}
				if (rel.getSource().equals(sel.getId())) {
					CITreeModel m = new CITreeModel();
					m.setName(groupName);
					tree.addChild(m);
					Template refs = result.fetchReference(bean, RelationConstraint.TARGET, rel.getId());
					m.setChildren(populateTree(desc, mdr, token, q, refs, result, transform, sel));
				}
			}
		}
		return(modelList);
	}
	*/
	/**
	 * Store service
	 */
	public StoreResult store(ContentData mdrData, String token,
			List<? extends ModelItem> local, List<? extends ModelItem> base) {
		
		int baseSize = (base == null ? -1 : base.size());
		int localSize = (local == null ? -1 : local.size());
		log.info("store(token=" + token + ", localSize=" + localSize + ", baseSize=" + baseSize +")");
		
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		
		StoreResult storeResult = new StoreResult();
		try {
			Transform transform = new Transform();
			List<CiBean> localBeans = transform.convert(mdr, token, local);
			List<CiBean> baseBeans = transform.convert(mdr, token, base);
			
			IRfcResult result = mdr.update(token, localBeans.toArray(new CiBean[0]), baseBeans.toArray(new CiBean[0]));
		
			storeResult.setRejected(result.isRejected());
			storeResult.setRejectCause(result.getRejectCause());
		} catch (Throwable e) {
			e.printStackTrace();
			storeResult.setRejected(true);
			storeResult.setRejectCause(e.getMessage());
		}
		log.info("store(token=" + token + ", localSize=" + localSize + ", baseSize=" + baseSize +") [rejected=" + storeResult.isRejected() + ", cause=" + storeResult.getRejectCause() + "]");
		return(storeResult);
	}
	
	/**
	 * Custom view
	 */
	public BaseModel loadCustomView(String token, ContentFile file) {
		BaseModel m =  (BaseModel) ContentParserFactory.get().getAdaptor(file, BaseModel.class);
		return(m);
	}
	
	/**
	 * ==================================================================
	 * Grid Service functions.
	 */
	public BaseListLoadResult<CIModelCollection> loadGrid(String token, BaseListLoadConfig config) {
		log.info("loadGrid(token=" + token + ", config=" + config + ")");
		
		String xmlQuery = config.get("query");
		XML2GraphQuery parser = new XML2GraphQuery();
		Map map = config.getParams();
		parser.setAttributeMap(map);
		GraphQuery query = null;
		int firstResult = 0;
		try {
			query = parser.parse(xmlQuery);
			if (config instanceof BasePagingLoadConfig) {
				firstResult = ((BasePagingLoadConfig)config).getOffset();
				int maxResult = ((BasePagingLoadConfig)config).getLimit();
				if (maxResult > 0) {
					query.fetchPrimarySelectors().setPageInfo(new PageInfo(firstResult, maxResult));
				}
				
				boolean limitToChild = ((BasePagingLoadConfig)config).get("limitToChild", false);
				if (limitToChild) {
					if (query.fetchPrimarySelectors() instanceof ItemOffspringSelector) {
						ItemOffspringSelector sel = (ItemOffspringSelector)query.fetchPrimarySelectors();
						sel.setLimitToChild(limitToChild);
					}
				}
				
				boolean isNot = ((BasePagingLoadConfig)config).get("searchOperationNot", false);
				Integer op = ((BasePagingLoadConfig)config).get("searchOperation");
				AttributeColumnConfig aCfg = ((BasePagingLoadConfig)config).get("searchAttributeConfig");
				
				String search = ((BasePagingLoadConfig)config).get("searchText");
				if (search != null && search.length() > 0 || (op != null && op == AttributeValueConstraint.IS_NULL)) {
					ItemSelector sel = query.fetchPrimarySelectors();
					
					ItemAndGroupConstraint and = new ItemAndGroupConstraint();
					
					AttributeValueConstraint vCon = SearchAttributeHandler.parse(search,op);
					if (op != null) {
						vCon.setOperation(op);
					}
					
					if (aCfg != null) {
						if (aCfg.isComplex()) {
							if (op != null && AttributeValueConstraint.IS_NULL == op) {
								vCon.setAlias(aCfg.getAlias());
							} else {
								ItemOffspringSelector ofSel = new ItemOffspringSelector("target", aCfg.getType());
								ItemRelationSelector relSel = new ItemRelationSelector("source2target", aCfg.getRefType(), "target", sel.getId());
								sel = ofSel;
								query.addSelector(ofSel);
								query.addSelector(relSel);
							}
						} else {
							if (aCfg.isInternal()) {
								if (aCfg.getAlias().equals("alias")) {
									vCon.setValueType(AttributeValueConstraint.INTERNAL_ALIAS);
								} else if (aCfg.getAlias().equals("description")) {
									vCon.setValueType(AttributeValueConstraint.INTERNAL_DESCRIPTION);
								} else if (aCfg.getAlias().equals("lastModified")) {
									vCon.setValueType(AttributeValueConstraint.INTERNAL_LASTMODFIED);
								} else if (aCfg.getAlias().equals("created")) {
									vCon.setValueType(AttributeValueConstraint.INTERNAL_CREATED);
								}
							} else {
								vCon.setAlias(aCfg.getAlias());
							}
						}
					}
					
					and.add(vCon);
					if (isNot) {
						ItemNotConstraint not = new ItemNotConstraint();
						not.applyConstraint(and);
						and = new ItemAndGroupConstraint();
						and.add(not);
					} 
					
					if (sel.fetchConstraint() != null) {
						and.add(sel.fetchConstraint());
					}
					sel.applyConstraint(and);
				}
				
				// Check for order...
				SortInfo sort = ((BasePagingLoadConfig)config).getSortInfo();
				if (sort != null) {
					String field = sort.getSortField();
					if (field != null) {
						String split[] = field.split("\\.");
						String selId = split[0];
						String attr = split[1];
						
						ItemSelector sel = query.findSelector(selId);
						if (sel != null) {
							OrderInfo info = new OrderInfo();
							info.setDescenden(sort.getSortDir().equals(SortDir.DESC));
							if (attr.startsWith(CIModel.INTERNAL_PREFIX)) {
								info.setCiAttr(convertToDB(attr));
							} else {
								if (attr.startsWith(CIModel.VALUE_PREFIX)) {
									attr = attr.substring(CIModel.VALUE_PREFIX.length());
								}
								info.setAttrAlias(attr);
								// TODO check for type...
								String attrType = config.get("attrType", "valueAsString");
								info.setAttrType(attrType);
							}
							sel.setOrderInfo(info);
						}
					}
				}
			}
				
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException("Problem parsing query: " + xmlQuery, e);
		}
		ContentData mdrData = config.get("mdr");
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		Graph graphResult = mdr.query(token, query);
		
		Graph2GridTransform transform = new Graph2GridTransform();
		List<CIModelCollection> gridResult = transform.transform(mdr, token, query, graphResult);
		
		// Do the query..
		BasePagingLoadResult<CIModelCollection> loadResult = new BasePagingLoadResult<CIModelCollection>(gridResult);
		loadResult.setOffset(firstResult);
		Template primaryNode = graphResult.fetchNode(query.fetchPrimarySelectors().getId());
		loadResult.setTotalLength(primaryNode.getTotalCount());
	
		log.info("loadGrid(token=" + token + ", config=" + config + ") OK [result=" + gridResult.size() + "]");
		return(loadResult);
	}

	private String convertToDB(String attr) {
		if (CIModel.CI_CREATED.equals(attr)) {
			return("createTime");
		}
		if (CIModel.CI_LASTMODIFIED.equals(attr)) {
			return("lastModified");
		}
		if (CIModel.CI_DESCRIPTION.equals(attr)) {
			return("description");
		}
		if (CIModel.CI_ALIAS.equals(attr)) {
			return("alias");
		}
		if (CIModel.CI_DERIVEDFROM.equals(attr)) {
			return("derivedFromId");
		}
		if (CIModel.CI_DISPLAYNAME.equals(attr)) {
			return("displayName");
		}
		
		
		// Default id lastModified.
		return("lastModified");
	}

	public GridModelConfig loadGridConfig(String token, ContentData mdr, ContentData data) throws CMDBRPCException {
		log.info("loadGridConfig(token=" + token + ", config=" + data + ")");
		try {
			GridModelConfig config = new GridModelConfig();
			if (data.get("template") != null) {
				config = getDefaultGridConfig(token, mdr, (String)data.get("template"), (String)data.get("instanceAlias"));
			} else {
				// Load form disk...
				// Check if file exists.
				ContentParserFactory.get().stat(data);
				if (!data.isExists()) {
					throw new CMDBRPCException("Load Grid Config", "Config file '" + data.getPath() + "' is not found", null);
				}
				config = (GridModelConfig) ContentParserFactory.get().getAdaptor(data, GridModelConfig.class);
			}
			config.setMDR(mdr);
			log.info("loadGridConfig(token=" + token + ", config=" + data + ") OK");
			return(config);
		} catch (Throwable t) {
			log.error("LoadGridConfig failed", t);
			throw new CMDBRPCException("Load Grid Config", t.getMessage(), CMDBRPCHandler.getStackTrace(t));
		}
	}
	/**
	 * Grid Service Functions End.
	 * ===================================================================
	 */
	
	/**
	 * Tree Service Functions.
	 * ====================================================================
	 * @throws CMDBRPCException 
	 */
	public List<CIModel> loadRelations(String token, ContentData mdrData,
			RelationTypeModel relation) throws CMDBRPCException {
		log.info("loadRelations(token=" + token + ", relation=" + relation + ")");
		try {
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		
		// Build query..
		CIModel ci = relation.getInstance();
		CIModel type = relation.getSourceType();
		if (relation.isOutbound()) {
			type = relation.getTargetType();
		}
		CIModel refType = relation.getRefType();
			
		ItemAliasSelector subject = new ItemAliasSelector("subject", ci.getDerivedFrom());
		subject.setAlias(ci.getAlias());
		subject.setPrimary(true);
		
		ItemOffspringSelector targets = new ItemOffspringSelector("targets", type.getAlias());
		targets.setMatchTemplate(false);
		targets.setLimitToChild(false);
		
		String source = subject.getId();
		String target = targets.getId();
		
		if (!relation.isOutbound()) {
			source = targets.getId();
			target = subject.getId();
		}
		ItemRelationSelector rel = new ItemRelationSelector("relation", refType.getAlias(), target, source);
		
		GraphQuery query = new GraphQuery();
		query.addSelector(subject);
		query.addSelector(targets);
		query.addSelector(rel);
	
		log.debug("Relation " + query.findSelector(source).getTemplateAlias() + "-->" + query.findSelector(target).getTemplateAlias());
		
		
		Graph graphResult = mdr.query(token, query);	
		graphResult.buildMap();
		
			
		Template t = graphResult.fetchNode(targets.getId());
		List<CIModel> result = new ArrayList<CIModel>(); 
		if (t != null && t.getOffsprings() != null) {
			Transform transform = new Transform();
			transform.setCache(graphResult);
			
			// Fetch alias ci.
			Template subjectT = graphResult.fetchNode(subject.getId());
			if (subjectT != null && subjectT.getTemplate() != null) {
				CiBean ciBean = graphResult.findOffspringAlias(ci.getAlias());
				if (ciBean != null) {
					ci = transform.convert(mdr, token, subjectT.getTemplate(), ciBean);
				}
			}
			for (CiBean bean : t.getOffsprings()) {
				
				CIModel refBean = transform.convert(mdr, token, t.getTemplate(), bean);
				// Update the CI with the reference id from where it was loaded.
				refBean.set(CIModel.SELECTOR_ID, relation.getId());
				boolean found = false;
				if (relation.getAttributeAlias() == null) {
					found = true;
				} else {
					if (relation.isOutbound()) {
						ValueModel v = ci.getValue(relation.getAttributeAlias());
						if (v instanceof ValueListModel) {
							for (ValueModel vm : ((ValueListModel)v).getValues()) {
								if (refBean.getAlias().equals(vm.getValue())) {
									found = true;
									break;
								}
							}
						}
						if (v instanceof ValueModel) {
							if (refBean.getAlias().equals(v.getValue())) {
								found = true;
							}
						}
					} else {
						ValueModel v = refBean.getValue(relation.getAttributeAlias());
						if (v instanceof ValueListModel) {
							for (ValueModel vm : ((ValueListModel)v).getValues()) {
								if (ci.getAlias().equals(vm.getValue())) {
									found = true;
									break;
								}
							}
						} 
						if (v instanceof ValueModel) {
							if (ci.getAlias().equals(v.getValue())) {
								found = true;
							}
						}

					}
				}
				if (found) {
					result.add(refBean);
				}
			}
		}
		log.info("loadRelations(token=" + token + ", relation=" + relation + ") OK [result=" + result.size() + "]");
		
		return(result);
		} catch (Throwable t) {
			log.error("Load Realtions failed", t);
			throw new CMDBRPCException("Load Relations", t.getMessage(), CMDBRPCHandler.getStackTrace(t));
		}
	}
	
	public RelationCollectionModel loadRelationTypes(String token, ContentData mdrData, CIModel ci, GroupDescription desc) throws CMDBRPCException {
		log.info("loadRelationTypes(token=" + token + ", CI=" + ci + ")");
		
		try {
			ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		if (desc != null) {
			/**
			 * The Group Description contains info about which relations to follow.
			 */
			GroupHandler handler = new GroupHandler(mdr, token);
			RelationCollectionModel model = handler.getRelationCollection(ci, desc);
			return(model);
		}
		
		
		
		// Fetch All Templates Inbound relations.
		String rootCI = mdrData.get("rootCi", "Ci");
		
		GraphQuery query = new GraphQuery();
		ItemOffspringSelector sel = new ItemOffspringSelector("templates", rootCI);
		sel.setLimitToChild(false);
		sel.setMatchTemplate(true);
		sel.setPrimary(true);
		ItemOffspringSelector ref = new ItemOffspringSelector("refs", "Reference");
		ref.setLimitToChild(false);
		ref.setMatchTemplate(true);
		ref.setPrimary(false);
		
		query.addSelector(sel);
		query.addSelector(ref);
		Graph result = mdr.query(token, query);
		result.buildMap();
		
		Transform transform = new Transform();
		transform.setCache(result);
		
		// Fetch outbound relations.
		CiBean template;
		if (ci.isTemplate()) {
			template = result.findOffspringAlias(ci.getAlias());
			if (template == null) {
				template = mdr.getCI(token, ci.getAlias());
			}
		} else {
			template = result.findOffspringAlias(ci.getDerivedFrom());
			if (template == null) {
				template = mdr.getCI(token, ci.getDerivedFrom());
			}
		}
		
		
		
		RelationCollectionModel model = new RelationCollectionModel();
		for (AttributeBean a : template.getAttributes()) {
			if (a.isComplexType()) {
				RelationTypeModel type = new RelationTypeModel();
				type.setOutbound(true);
				type.setInstance(ci);
				type.setAttributeAlias(a.getAlias());
				CiBean bean = result.findOffspringAlias(a.getType());
				if (bean == null) {
					bean = mdr.getCI(token, a.getType());
				}
				
				CiBean refBean = result.findOffspringAlias(a.getRefType());
				if (refBean == null) {
					refBean = mdr.getCI(token, a.getRefType());
				}
				
				type.setSourceType(transform.convert(mdr, token, template, template));
				type.setTargetType(transform.convert(mdr, token, bean, bean));
				type.setRefType(transform.convert(mdr, token, refBean, refBean));
				
				type.set(CIModel.CI_ICON_PATH, IconMapper.getIcon(null, "RelationOutboundIcon"));
				String roleSource = refBean.toStringValue("roleSource");
				if (roleSource.length() == 0) {
					roleSource = "[" + refBean.getAlias() + "]";
				}
				type.set(CIModel.CI_DISPLAYNAME,  roleSource);
				model.addRelationType(type);
			}
		}
		
		
		String templatePath = buildTemplatePath(result, template, "Ci");
		Template t = result.fetchNode(sel.getId());
		if (t == null || t.getOffsprings() == null) {
			return(model);
		}
		for (CiBean bean : t.getOffsprings()) {
			
			for (AttributeBean abean : bean.getAttributes()) {
				if (abean.isDerived()) {
					continue;
				}
				if (!abean.isComplexType()) {
					continue;
				}
				
				if (templatePath.contains("/" + abean.getType())) {
					RelationTypeModel type = new RelationTypeModel();
					type.setInstance(ci);
					type.setOutbound(false);
					type.setAttributeAlias(abean.getAlias());
					
					CiBean refBean = result.findOffspringAlias(abean.getRefType());
					
					if (refBean == null) {
						refBean = mdr.getCI(token, abean.getRefType());
					}
					
					type.setRefType(transform.convert(mdr, token, refBean, refBean));
					type.setSourceType(transform.convert(mdr, token, bean, bean));
					type.setTargetType(transform.convert(mdr, token, template, template));
					
					type.set(CIModel.CI_ICON_PATH, IconMapper.getIcon(null, "RelationInboundIcon"));
					String roleTarget = refBean.toStringValue("roleTarget");
					if (roleTarget.length() == 0) {
						roleTarget = "[" + refBean.getAlias() + "]";
					}
					type.set(CIModel.CI_DISPLAYNAME, roleTarget);
							
					model.addRelationType(type);
				}
			}
		}
		log.info("loadRelationTypes(token=" + token + ", CI=" + ci + ") OK [result=" + model.getRelationTypes().size() + "]");
		
		return(model);
		} catch (Throwable t) {
			log.error("LoadGridConfig failed", t);
			throw new CMDBRPCException("Load Relation Types", t.getMessage(), CMDBRPCHandler.getStackTrace(t));
		}
	}

	private String buildTemplatePath(Graph result, CiBean template, String root) {
		if (template == null) {
			return("/" + root);
		}
		if (template.getAlias().equals(root)) {
			return("/" + template.getAlias());
		}
		CiBean derivedFrom = result.findOffspringAlias(template.getDerivedFrom());
		return(buildTemplatePath(result,derivedFrom, root) + "/" + template.getAlias());
	}

	private GridModelConfig getDefaultGridConfig(String token, ContentData mdrData, String template, String instance) {
		GridModelConfig config = new GridModelConfig();
		List<CIModel> items = new ArrayList<CIModel>();
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
		CiBean bean = mdr.getCI(token, template);
		if (bean == null) {
			return(config);
		}
		Transform tr = new Transform();
		if (instance != null) {
			CiBean iBean = mdr.getCI(token, instance);
			CIModel iModel = tr.convert(mdr, token, bean, iBean);
			config.set("instance", iModel);
		}
		
		
		List<AttributeColumnConfig> cols = new ArrayList<AttributeColumnConfig>();
		
		AttributeColumnConfig idCol = new AttributeColumnConfig();
		idCol.setName("ID");
		idCol.setId("offspring." + CIModel.CI_ID);
		idCol.setAlias("longId");
		idCol.setHidden(true);
		idCol.setEditable(false);
		cols.add(idCol);
		
		AttributeColumnConfig aliasCol = new AttributeColumnConfig();
		aliasCol.setName("Alias");
		aliasCol.setAlias("alias");
		aliasCol.setId("offspring." + CIModel.CI_ALIAS);
		aliasCol.setHidden(true);
		aliasCol.setEditable(true);
		cols.add(aliasCol);
		
		AttributeColumnConfig derivedCol = new AttributeColumnConfig();
		derivedCol.setName("Derived From");
		derivedCol.setId("offspring." + CIModel.CI_DERIVEDFROM);
		derivedCol.setHidden(true);
		derivedCol.setEditable(false);
		derivedCol.setType("xs:string");
		derivedCol.setComplex(false);
		derivedCol.setSearchable(false);
		cols.add(derivedCol);
	
		
		// Add displayName.
		AttributeColumnConfig aDisplayName = new AttributeColumnConfig();
		aDisplayName.setName("Display Name");
		aDisplayName.setId("offspring." + CIModel.CI_DISPLAYNAME);
		aDisplayName.setIconPath(IconMapper.getIcon(bean.toStringValue("icon"), template));
		aDisplayName.setEditable(false);
		aDisplayName.setSearchable(false);
		
		cols.add(aDisplayName);
		
		config.setAutoExpandColumnId(aDisplayName.getId());
		
		for (AttributeBean aBean : bean.getAttributes()) {
			AttributeColumnConfig col = new AttributeColumnConfig();
			Transform.update("offspring", col, aBean);
			if (aBean.isComplexType()) {
				col.setIconPath(IconMapper.getIcon(null, aBean.getType()));
			}
			cols.add(col);
		}
		
		// Add Hidden lastModifed, created.
		AttributeColumnConfig aLastModifed = new AttributeColumnConfig();
		aLastModifed.setName("Last Modified");
		aLastModifed.setId("offspring." + CIModel.CI_LASTMODIFIED);
		aLastModifed.setEditable(false);
		aLastModifed.setAlias("lastModifed");
		cols.add(aLastModifed);
		
		AttributeColumnConfig aCreated = new AttributeColumnConfig();
		aCreated.setName("Created");
		aCreated.setId("offspring." + CIModel.CI_CREATED);
		aCreated.setHidden(true);
		aCreated.setEditable(false);
		aCreated.setAlias("created");
		
		cols.add(aCreated);
		
		AttributeColumnConfig aDesc = new AttributeColumnConfig();
		aDesc.setName("Description");
		aDesc.setId("offspring." + CIModel.CI_DESCRIPTION);
		aDesc.setHidden(false);
		aDesc.setEditable(true);
		aDesc.setAlias("description");
		cols.add(aDesc);
		
		GraphQuery query = new GraphQuery();
		ItemOffspringSelector sel = new ItemOffspringSelector("offspring", template);
		sel.setPrimary(true);
		sel.setMatchTemplate(false);
		query.addSelector(sel);
		ItemOffspringSelector sel2 = new ItemOffspringSelector("cache_serach", "Root");
		ItemRelationSelector rel = new ItemRelationSelector("off2ref", "Reference", sel2.getId(), sel.getId());
		query.addSelector(sel2);
		query.addSelector(rel);
		//rel.setMandatory(true);
		sel.addExcludeRelation(rel.getId());
			
		
		config.setQuery(GraphQuery2XML.toXML(query, 0));
		
		config.setColumnConfig(cols);
		config.setNewCIModel("offspring", tr.convert(mdr, token, bean, bean));
		config.setSupportAddRow(true);
		return(config);
	}

	
	protected void updateSaveDeleteResponse(SaveResponse resp,
			List<CiBean> beans) {
		
		int instances = 0;
		int templates = 0;
		for (CiBean bean : beans) {
			if (bean.isTemplate()) {
				templates++;
			} else {
				instances++;
			}
		}
		
		resp.setInstanceCIs(instances);
		resp.setTemplateCIs(templates);
	}

	protected List<CiBean> getSaveDeleteItems(ICIMDR mdr, String token, SaveDeleteRequest request) {
		// Query all data.
		GraphQuery query = new GraphQuery();
		for (SaveItem item : request.getTemplates()) {
			ItemOffspringSelector sel = new ItemOffspringSelector(item.getAlias(), item.getAlias());
			if (item.saveInstances() && item.saveTemplates()) {
				// Don't set anytning.
			} else {
				sel.setMatchTemplate(item.saveTemplates());
			}
			sel.setLimitToChild(!item.isAllChildren());
			query.addSelector(sel);
			sel.setPrimary(true);
		}
		for (SaveItem item : request.getReferences()) {
			ItemOffspringSelector sel = new ItemOffspringSelector(item.getAlias(), item.getAlias());
			sel.setMatchTemplate(true);
			sel.setLimitToChild(!item.isAllChildren());
			query.addSelector(sel);
			sel.setPrimary(true);
		}
		Graph graphResult = mdr.query(token, query);
		
		Set<CiBean> saveBeans = new HashSet<CiBean>();
		
		for (SaveItem saveItem : request.getTemplates()) {
			Template t = graphResult.fetchNode(saveItem.getAlias());
			if (saveItem.isAllChildren()) {
				if (t.getOffsprings() != null) {
					saveBeans.addAll(t.getOffsprings());
				}	
			}
			if (saveItem.saveTemplates()) {
				saveBeans.add(t.getTemplate());
			}
		}
		for (SaveItem saveItem : request.getReferences()) {
			Template t = graphResult.fetchNode(saveItem.getAlias());
			if (saveItem.isAllChildren()) {
				if (t.getOffsprings() != null) {
					saveBeans.addAll(t.getOffsprings());
				}	
			}
			if (saveItem.saveTemplates()) {
				saveBeans.add(t.getTemplate());
			}
		}
		return(new ArrayList<CiBean>(saveBeans));
	}

	public String checkForNewUpdate(String token, boolean force) throws CMDBRPCException {
		IOneCMDBWebService service = null;
		try {
			service = CMDBWebServiceFactory.get().getOneCMDBWebService();
		} catch (Exception e1) {
			throw new CMDBRPCException("Internal Error", "Can't contact OneCMDB Core WebService", null);
		}
		
		if (service.isUpdateAvailable(token, force)) {
			return(service.getUpdateInfo(token, false));
		}
		
		return(null);
	}

	private String getCMDB_ID() {
		String id = "unkown";
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			id = localhost.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return("" + id.hashCode());
	}

	/**
	 * MDR Service Functions.
	 * ====================================================================
	 * @throws CMDBRPCException 
	 */
	public BaseModel createMDR(String token, BaseModel data) throws CMDBRPCException {
		IOneCMDBWebService service = null;
		try {
			service = CMDBWebServiceFactory.get().getOneCMDBWebService();
		} catch (Exception e1) {
			throw new CMDBRPCException("Internal Error", "Can't contact OneCMDB Core WebService", null);
		}
		
		String name = data.get("name");
		String cfgName = data.get("cfgName");
	
		// Create data model
		String configAlias =  "MDR-CONFIG-" + name;
		CiBean cfg = new CiBean("MDR_ConfigEntry", configAlias, true);
		cfg.addAttributeValue(new ValueBean("program", "bin/transform-import-cmdb", false));
	
		// Create mdr instance..
		CiBean mdr = new CiBean("MDR_Repository", "MDR-" + name, false);
		mdr.addAttributeValue(new ValueBean("name", name, false));
		mdr.addAttributeValue(new ValueBean("configAlias", configAlias, false));
			
		// Create MDR config instance.
		CiBean mdrCfg = new CiBean(configAlias, "MDR-CONFIG-" + name + "-" + cfgName, false);
		mdrCfg.addAttributeValue(new ValueBean("name", cfgName, false));
		mdrCfg.addAttributeValue(new ValueBean("mdrRepository", mdr.getAlias(), true));
		
		
		// Commit this.
		IRfcResult result = service.update(token, new CiBean[] {mdr, cfg, mdrCfg}, new CiBean[0]);
		if (result.isRejected()) {
			throw new CMDBRPCException("Can't create MDR " + name, result.getRejectCause(), "");
		}
		
		// Reload Data.
		ICIMDR ciMdr = new OneCMDBWebServiceMDR(service);
		CiBean mdrBean = ciMdr.getCI(token, mdr.getAlias());
		CiBean mdrCfgBean = ciMdr.getCI(token, mdrCfg.getAlias());
		// Get Templates.
		CiBean mdrBeanT = ciMdr.getCI(token, mdrBean.getDerivedFrom());
		CiBean mdrCfgBeanT = ciMdr.getCI(token, mdrCfgBean.getDerivedFrom());
		
		
		// Copy MDR template to MDR/name
		// Copy the directory structure.
		IContentService content = (IContentService) ServiceLocator.getService(IContentService.class);
		if (content == null) {
			content = new ContentServiceImpl();
		}
		ContentFolder mdrFolder = new ContentFolder("MDR/" + name);
		ContentFolder sourceMdrFolder = new ContentFolder("MDR_Template/Default/");
		content.stat(mdrFolder);
		if (!mdrFolder.isExists()) {
			content.copy(token, sourceMdrFolder,mdrFolder, false);
		}
	
		BaseModel ret = new BaseModel();
		Transform t = new Transform();
		ret.set("mdr", t.convert(ciMdr, token, mdrBeanT, mdrBean));
		ret.set("mdrCfg", t.convert(ciMdr, token, mdrCfgBeanT, mdrCfgBean));
		
		return(ret);
	}

	
	/**
	 * Store Tranform config 
	 */
	public boolean storeTransformConfig(String token, ContentData ciMDRData, CIModel mdr, CIModel mdrCfg, TransformConfig cfg) throws CMDBRPCException {
		try {
			return(new MDRSetupService().storeTransformConfig(token, ciMDRData, mdr, mdrCfg, cfg));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CMDBRPCException("Error loading source header", e.getMessage(), CMDBRPCHandler.getStackTrace(e));
		}
	}
	
	public List<BaseModel> calculateMDRInstances(String token, TransformConfig cfg) throws CMDBRPCException {
		try {
			return(new MDRSetupService().calcInstances(token, cfg));
		} catch (Throwable t) {
			t.printStackTrace();
			if (t instanceof CMDBRPCException) {
				throw (CMDBRPCException)t;
			}
			throw new CMDBRPCException("Error", "Can't load transform", CMDBRPCHandler.getStackTrace(t));
		}
		
	}
	/**
	 * Load 
	 * @param token
	 * @param mdr
	 * @param mdrCfg
	 * @return
	 */
	public TransformConfig loadTransformConfig(String token, ContentData ciMDRData, CIModel mdr, CIModel mdrCfg) throws CMDBRPCException {
		try {
			return(new MDRSetupService().loadTransformConfig(token, ciMDRData, mdr, mdrCfg));
		} catch (Throwable t) {
			t.printStackTrace();
			throw new CMDBRPCException("Error loading source header", t.getMessage(), CMDBRPCHandler.getStackTrace(t));
		}
		/*
		String mdrName = mdr.getValue("name").getValue();
		String mdrConfigName = mdrCfg.getValue("name").getValue();
			
		TransformConfig config = new TransformConfig();
		config.setMDRName(mdrName);
		config.setMDRConfigName(mdrConfigName);
		ContentFolder sourceMdrFolder = new ContentFolder("MDR_Template/source-templates");
		IContentService svc = (IContentService) ServiceLocator.getService(IContentService.class);
		if (svc == null) {
			svc = new ContentServiceImpl();
		}
		// Get datasources.
		List<? extends ContentData> sourceTemplates = svc.list(token, sourceMdrFolder);
		for (ContentData data : sourceTemplates) {
			updateDataSource(svc, token, data, config);
		}
		ContentFile defTransform = new ContentFile("MDR_Template/transform-template.xml");
		updateDataTransform(svc, token, defTransform, config);
		
		
		// Try to find datasource.
		ContentFile dataSource = new ContentFile("MDR/" + mdrName + "/" + "conf/" + mdrConfigName +"/source.xml");		
		updateDataSource(svc, token, dataSource, config);
		ContentFile dataTransform = new ContentFile("MDR/" + mdrName + "/" + "conf/" + mdrConfigName +"/transform.xml");
		updateDataTransform(svc, token, dataTransform, config);
		*/
		/*
		// Load all templates
		ICIMDR ciMDR = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(ciMDRData, ICIMDR.class);
		GraphQuery q = new GraphQuery();
		ItemOffspringSelector templates = new ItemOffspringSelector("templates", "Ci");
		templates.setMatchTemplate(true);
		templates.setLimitToChild(false);
		templates.setPrimary(true);
		ItemOffspringSelector references = new ItemOffspringSelector("refs", "CIReference");
		references.setMatchTemplate(true);
		references.setLimitToChild(false);
		
		q.addSelector(templates);
		q.addSelector(references);
		
		Graph result = ciMDR.query(token, q);
		result.buildMap();
		Transform tr = new Transform();
		tr.setCache(result);
		List<CIModel> templateCIs = new ArrayList<CIModel>();
		for (CiBean bean : result.fetchNode("templates").getOffsprings()) {
			CIModel m = tr.convert(ciMDR, token, bean, bean);
			templateCIs.add(m);
		}
		config.setTemplates(templateCIs);
		
		List<CIModel> refsCIs = new ArrayList<CIModel>();
		for (CiBean bean : result.fetchNode("refs").getOffsprings()) {
			CIModel m = tr.convert(ciMDR, token, bean, bean);
			refsCIs.add(m);
		}
		config.setReferences(refsCIs);
		
		*return(config);
		**/
		
	}
	
	public GridModelConfig loadDataSourceColumns(String token, TransformConfig config) throws CMDBRPCException {
		try {
			return(new MDRSetupService().loadDataSourceColumns(token, config));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CMDBRPCException("Error loading source header", e.getMessage(), CMDBRPCHandler.getStackTrace(e));
		}
	}
	
	public BasePagingLoadResult<BaseModel> loadDataSourceData(String token, BasePagingLoadConfig config) throws CMDBRPCException {
		try {
			return(new MDRSetupService().loadDataSourceData(token, config));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CMDBRPCException("Error loading datasource data", e.getMessage(), CMDBRPCHandler.getStackTrace(e));
		}
	}
	
	public TransformModel autoResolveTransformRelations(String token, TransformModel m) throws CMDBRPCException {
		try {
			ContentFile mdrData = new ContentFile();
			String mdrConf = ConfigurationFactory.getConfig().get(Config.OneCMDBWebService);
			mdrData.setPath(mdrConf);
			ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(mdrData, ICIMDR.class);
			
			return(new AutoResolveRelation(mdr, token).autoResolve(m));

		} catch (Throwable e) {
			e.printStackTrace();
			throw new CMDBRPCException("Error loading datasource data", e.getMessage(), CMDBRPCHandler.getStackTrace(e));
		}
	}
	

	public void createMDRConfig(String token, BaseModel data)
			throws CMDBRPCException {
		// TODO Auto-generated method stub
		
	}

	public void deleteMDR(String token, BaseModel data) throws CMDBRPCException {
		// TODO Auto-generated method stub
		
	}

	public void deleteMDRConfig(String token, BaseModel data)
			throws CMDBRPCException {
		// TODO Auto-generated method stub
		
	}



	

	
	

}
