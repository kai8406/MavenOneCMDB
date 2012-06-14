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
package org.onecmdb.ui.gwt.desktop.server.service.model.mdr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.service.ServiceFactory;
import org.dom4j.DocumentException;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.ParentBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IDataSource;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.SimpleTransformProvider;
import org.onecmdb.core.utils.transform.TransformBeanProvider;
import org.onecmdb.core.utils.transform.csv.CSVDataSource;
import org.onecmdb.core.utils.transform.csv.CSVInstanceSelector;
import org.onecmdb.core.utils.transform.csv.CSVRow;
import org.onecmdb.core.utils.transform.excel.ExcelDataSource;
import org.onecmdb.core.utils.transform.jdbc.JDBCDataSourceWrapper;
import org.onecmdb.core.utils.transform.jdbc.JDBCInstanceSelector;
import org.onecmdb.core.utils.transform.jdbc.JDBCRow;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.WSDLBeanProvider;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.content.IContentService;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.DataSetModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.server.service.CMDBRPCHandler;
import org.onecmdb.ui.gwt.desktop.server.service.ServiceLocator;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentServiceImpl;
import org.onecmdb.ui.gwt.desktop.server.service.model.CMDBWebServiceFactory;
import org.onecmdb.utils.wsdl.OneCMDBTransform;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;

public class MDRSetupService {
	private Log log = LogFactory.getLog(this.getClass());
	
	public TransformConfig loadTransformConfig(String token, ContentData ciMDRData, CIModel mdr, CIModel mdrCfg) throws CMDBRPCException, DocumentException {
		
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
		ICIMDR ciMDR = (ICIMDR) ContentParserFactory.get().getCachedAdaptor(ciMDRData, ICIMDR.class);
		// Get datasources.
		List<? extends ContentData> sourceTemplates = svc.list(token, sourceMdrFolder);
		for (ContentData data : sourceTemplates) {
			updateDataSource(svc, token, data, config);
		}
		
		// Set defaul to excel...
		config.setDataSourceType("excel");
		
		ContentFile defTransform = new ContentFile("MDR_Template/transform-template.xml");
		updateDataTransform(token, svc, ciMDR, defTransform, config);
		
		
		// Try to find datasource.
		ContentFile configurable = new ContentFile("MDR/" + mdrName + "/" + "conf/configurable");
		svc.stat(configurable);
		config.setConfigurabe(configurable.isExists());
		
		ContentFile dataSource = new ContentFile("MDR/" + mdrName + "/" + "conf/" + mdrConfigName +"/source.xml");		
		updateDataSource(svc, token, dataSource, config);
		ContentFile dataTransform = new ContentFile("MDR/" + mdrName + "/" + "conf/" + mdrConfigName +"/transform.xml");
		updateDataTransform(token, svc, ciMDR, dataTransform, config);
		
		if (config.getTransformModel().getName() == null) {
			config.getTransformModel().setName(mdrConfigName);
		}
		return(config);
	}

	private void updateDataTransform(String token, IContentService svc, ICIMDR mdr, ContentData data,
			TransformConfig config) throws DocumentException {
		svc.stat(data);
		if (!data.isExists() || data.isDirectory()) {
			return;
		}
		
		String content = svc.get(token, data, "UTF-8");
		TransformModel model = TransformConverter.fromXML(token, mdr, content);
		config.setTransformModel(model);
	}
	
	private void updateDataSource(IContentService svc, String token, ContentData data,
			TransformConfig config) {
		svc.stat(data);
		if (!data.isExists() || data.isDirectory()) {
			return;
		}
		String content = svc.get(token, data);
		Properties p = new Properties();
		try {
			ByteArrayInputStream array = new ByteArrayInputStream(content.getBytes());
			p.loadFromXML(array);
		} catch (Exception e) {
			log.error("Can't read '" + data.getPath() + "'", e);
			// Ignore this.
			return;
		}
		
		Enumeration<?> keys = p.propertyNames();
		BaseModel dataSource = new BaseModel();
		String type = (String) p.get("type");
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = p.getProperty(key);
			if (key.startsWith(type + ".")) {
				key = key.substring((type + ".").length());
			}
			dataSource.set(key, value);
		}
		
		config.setDataSourceType(type);
		config.setDataSource(type, dataSource);
	}

	
	
	public boolean storeTransformConfig(String token, ContentData ciMDRData, CIModel mdr, CIModel mdrCfg, TransformConfig cfg) throws Exception {
		IContentService svc = (IContentService) ServiceLocator.getService(IContentService.class);
		if (svc == null) {
			svc = new ContentServiceImpl();
		}
	
		String mdrPath = "MDR/" + mdr.getValueAsString("name") + "/conf/" + mdrCfg.getValueAsString("name");
		
		
		ContentFolder mdrFolder = new ContentFolder(mdrPath);
		svc.stat(mdrFolder);
		if (!mdrFolder.isExists()) {
			svc.mkdir(token, mdrFolder);
		}
		
		// Store data source.
		Properties p = getDataSourceProperties(cfg, false);
		String sPath = mdrPath + "/source.xml";
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		p.storeToXML(out, "", "UTF-8");
		svc.put(token, new ContentFile(sPath), out.toString("UTF-8"));
		
		// Store transform data.
		TransformModel transform = cfg.getTransformModel();
		
		String tPath = mdrPath + "/transform.xml";
		ContentFile f = new ContentFile(tPath);
		String transformXML = TransformConverter.toXML(cfg.getDataSourceType(), transform);
		svc.put(token, f,  "UTF-8", transformXML);
		
		return(true);
	}
	
	public GridModelConfig loadDataSourceColumns(String token, TransformConfig config) throws IOException {
		GridModelConfig gridCfg = new GridModelConfig();
		gridCfg.setColumnConfig(getDataSourceColumns(config));
		return(gridCfg);
	}
	
	public BasePagingLoadResult<BaseModel> loadDataSourceData(String token, BasePagingLoadConfig config) throws IOException {
		TransformConfig tCfg = config.get("transformConfig");
		List<IInstance> rows = loadData(tCfg);
		int total = rows.size();
		List<BaseModel> data = new ArrayList<BaseModel>();
		int maxIndex = config.getOffset() + config.getLimit();
		if (maxIndex > total) {
			maxIndex = total;
		}
		for (int i = config.getOffset(); i < maxIndex; i++) {
			IInstance ins = rows.get(i);
			BaseModel rowData = new BaseModel();
			if (ins instanceof CSVRow) {
				CSVRow row = (CSVRow)ins;
				List<String> cols = row.getColumnNames();

				for (String key : cols) {
					Object value = row.getCol(key);
					String v = (value == null ? null : value.toString());
					rowData.set(key, v);
				}
				/*
				String cols[] = ((CSVRow)ins).getColumns();
				for (int col = 0; col < cols.length; col++) {
					String value = ((CSVRow)ins).getCol(col+1);
					rowData.set("" + (col+1), value);
				}
				*/
			}
			if (ins instanceof JDBCRow) {
				JDBCRow row = (JDBCRow)ins;
				List<String> cols = row.getColumnNames();

				for (String key : cols) {
					Object value = row.getCol(key);
					String v = (value == null ? null : value.toString());
					rowData.set(key, v);
				}
			}
			data.add(rowData);
		}
		
		BasePagingLoadResult<BaseModel> result = new BasePagingLoadResult<BaseModel>(data, config.getOffset(), total);
			
		return(result);
		
	}
	
	public List<AttributeColumnConfig> getDataSourceColumns(TransformConfig config) throws IOException {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		List<IInstance> data = loadData(config);
		if (data.size() > 0) {
			IInstance ins = data.get(0);
			if (ins instanceof CSVRow) {
				CSVRow row = (CSVRow)ins;
				//String cols[] = row.getColumns();
				List<String> cols = row.getColumnNames();
				for (int i = 0; i < cols.size(); i++) {
					String name = cols.get(i);
					if (name == null || name.length() == 0) {
						name = "EMPTY-" + i;
					}
					AttributeColumnConfig aCfg = new AttributeColumnConfig();
					aCfg.setId(name);
					aCfg.setName(name);
					aCfg.setType("xs:string");
					list.add(aCfg);
				}
			}
			if (ins instanceof JDBCRow) {
				JDBCRow row = (JDBCRow)ins;
				List<String> cols = row.getColumnNames();

				for (int i = 0; i < cols.size(); i++) {
					String name = cols.get(i);
					AttributeColumnConfig aCfg = new AttributeColumnConfig();
					aCfg.setId(name);
					aCfg.setName(name);
					aCfg.setType("xs:string");
					list.add(aCfg);
				}
				
			}
		}
		return(list);
	}
	
	protected List<IInstance> loadData(TransformConfig config) throws IOException {
		OneCMDBTransform trans = new OneCMDBTransform();
		Properties p = getDataSourceProperties(config, true);
		IDataSource source = trans.getDataSource(p);
		DataSet dataSet = new DataSet();
		dataSet.setDataSource(source);
		
		if (source instanceof ExcelDataSource || source instanceof CSVDataSource) {
			CSVInstanceSelector isel = new CSVInstanceSelector();
			List<IInstance> instances = isel.getInstances(dataSet);
			return(instances);
		}
		if (source instanceof JDBCDataSourceWrapper) {
			JDBCInstanceSelector isel = new JDBCInstanceSelector();
			List<IInstance> instances = isel.getInstances(dataSet);
			return(instances);
		}
		return(new ArrayList<IInstance>());
	}
	
	private Properties getDataSourceProperties(TransformConfig config, boolean appendRoot) {
		String type = config.getDataSourceType();
		Properties p = new Properties();
		p.setProperty("type", type);
		
		// Need to set the root path.
		if (appendRoot) {
			p.setProperty(type + ".rootPath", ContentParserFactory.get().getRootPath().getPath() + "/MDR/" + config.getMDRName()); 
		}
		BaseModel base = config.get(type);
		for (String key : base.getPropertyNames()) {
			Object o = base.get(key);
			if (key.equals("rootPath")) {
				continue;
			}
			if (o instanceof String) {
				p.setProperty(type + "." + key, (String)base.get(key));
			}
		}
		return(p);
	}

	public List<BaseModel> calcInstances(String token, TransformConfig config) throws CMDBRPCException {
		IOneCMDBWebService service = null;
		try {
			service = CMDBWebServiceFactory.get().getOneCMDBWebService();
		} catch (Exception e1) {
			throw new CMDBRPCException("Internal Error", "Can't contact OneCMDB Core WebService", null);
		}
		
		
		
		OneCMDBTransform trans = new OneCMDBTransform();
		Properties p = getDataSourceProperties(config, true);
		IDataSource dataSource = null;
		try {
			dataSource = trans.getDataSource(p);
		} catch (IOException e) {
			throw new CMDBRPCException("Internal Error", "Can't resolve data source", CMDBRPCHandler.getStackTrace(e));
		}
		
		//IDataSource dataSource = getDataSource();
		
		
		TransformBeanProvider transformWorker = new TransformBeanProvider();
		//transformWorker.setValueMap(valueMap);
		ParentBeanProvider transformProvider = new ParentBeanProvider();
		
		
		// Create reader
		String transformXML = TransformConverter.toXML(config.getDataSourceType(), config.getTransformModel());
		
		// Create transform provider.
		SimpleTransformProvider simpleProvider = new SimpleTransformProvider();
		simpleProvider.setInputReader(new StringReader(transformXML));
		simpleProvider.setType(config.getDataSourceType());
	
		
		// Create template provider.
		IBeanProvider templateProvider = new WSDLBeanProvider(service, token);
		transformProvider.setInstanceProvider(simpleProvider);
		transformProvider.setTemplateProvider(templateProvider);
		
		transformWorker.setDataSource(dataSource);
		transformWorker.setTransformProvider(transformProvider);
		//transformWorker.setName(config.getMDRConfigName());
	
		// TODO: Handle if.
		transformWorker.setWebService(service);
		transformWorker.setToken(token);
		transformWorker.setValidate(true);
		
		List<CiBean> allBeans = transformWorker.getBeans();
		
		// Caclulate # instances of each template.
		List<BaseModel> result = new ArrayList<BaseModel>();
		for (DataSetModel ds : config.getTransformModel().getDataSets()) {
			Set<CiBean> beans = transformWorker.getBeansForDataSet(ds.getName());
			if (beans == null) {
				continue;
			}
			BaseModel m = new BaseModel();
			m.set("ds", ds.getName());
			m.set("count", beans.size());
			int foundCount = 0;
			for (CiBean bean: beans) {
				if (bean.getId() != null) {
					foundCount++;
				}
			}
			m.set("foundCount", foundCount);
			result.add(m);
		}
		return(result);
	}
	
	
}
