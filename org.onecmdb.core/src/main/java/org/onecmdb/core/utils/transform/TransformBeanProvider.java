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
package org.onecmdb.core.utils.transform;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.MemoryBeanProvider;
import org.onecmdb.core.utils.bean.BeanClassInjector;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;

public class TransformBeanProvider implements IBeanProvider {

	private IDataSource dataSource;
	private IBeanProvider transformProvider;
	private String name;
	private IBeanProvider resultProvider = null;
	private HashMap<String, String> valueMap;
	private IOneCMDBWebService webService;
	private String token;
	private boolean validate = true;
	private int queryCount;
	TransformEngine engine = new TransformEngine();
	Log log = LogFactory.getLog(this.getClass());
	//private MemoryBeanProvider memProvider = new MemoryBeanProvider();
	
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public IOneCMDBWebService getWebService() {
		return webService;
	}
	
	/**
	 * set this if validation to a cmdb is to be performed.
	 * 
	 * @param webService
	 */
	public void setWebService(IOneCMDBWebService webService) {
		this.webService = webService;
	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public IBeanProvider getTransformProvider() {
		return transformProvider;
	}

	public void setTransformProvider(IBeanProvider transformProvider) {
		this.transformProvider = transformProvider;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CiBean getBean(String alias) {
		if (resultProvider == null) {
			try {
				transform();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return(null);
			}
		}
		return(transformProvider.getBean(alias));
	}

	public List<CiBean> getBeans() {
		if (resultProvider == null) {
			try {
				transform();
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
				//return(Collections.EMPTY_LIST);
			}
		}
		return(resultProvider.getBeans());
	}
	
	protected void transform() throws IOException {
		// Setup engine...
		engine.setWebService(webService);
		engine.setToken(token);
		engine.setValidate(this.validate);
		
		resultProvider = new MemoryBeanProvider();
		if (this.name == null) {
			this.name = "primary-forward";
		}
		if (this.name != null) {
				
			CiBean bean = transformProvider.getBean(this.name);
			if (bean == null) {
				// Try the primary-forward...
				bean = transformProvider.getBean("primary-forward");
				if (bean == null) {
					throw new IOException("Data Set name <" + this.name + "> not found!");
				}
			}
			process((MemoryBeanProvider)resultProvider, bean);
		} else {
			for (CiBean bean : transformProvider.getBeans()) {
				process((MemoryBeanProvider)resultProvider, bean);
			}
		}
	}

	protected void process(MemoryBeanProvider result, CiBean bean) throws IOException {
		if (!bean.getDerivedFrom().startsWith("DataSet")) {
			return;
		}
		BeanClassInjector injector = new BeanClassInjector();
		injector.setBeanProvider(transformProvider);
		injector.setValueMap(this.valueMap);
		Object o = injector.beanToObject(bean);
	
		
		
	
		if (o instanceof DataSet) {
			DataSet dataSet = (DataSet)o;
			dataSet.setDataSource(dataSource);
			//TransformEngine engine = new TransformEngine();
			IBeanProvider partResult = engine.transform(transformProvider, (DataSet)o);
			List<CiBean> beans = partResult.getBeans();
			log.info(dataSet.getName() + " --> " + beans.size());
			for (CiBean rBean : beans) {
				result.addBean(rBean);
			}
			this.queryCount += engine.getQueryCount();
		}
	}
	public int getQueryCount() {
		return queryCount;
	}
	
	public Set<CiBean> getBeansForDataSet(String dsName) {
		return(engine.getBeansForDataSet("DataSet-" + dsName));
	}
	
	public void setValueMap(HashMap<String, String> valueMap) {
		this.valueMap = valueMap;
	}

	public void setValidate(boolean value) {
		this.validate = value;
		
	}
}
