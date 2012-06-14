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
package org.onecmdb.core.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiService;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.ImportBeanProvider;
import org.onecmdb.core.utils.SpringFactoryBean;
import org.onecmdb.core.utils.xml.XmlParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class AbstractOneCmdbTestCase extends TestCase {
	protected ISession session;
	protected ICi ciRoot;
	protected ICi root;
	protected ICi ciRelationRoot;
	protected OnecmdbTestUtils testUtils;
	protected IOneCmdbContext cmdbContext;
	protected ConfigurableApplicationContext springContext;
	protected SpringFactoryBean onecmdbFactory;
	private OneCMDBTestConfig config;
	/**
	 * Example Providers.
	 * Currently empty.
	 */
	
	
	public AbstractOneCmdbTestCase() {
		this(new OneCMDBTestConfig());
	}
	
	public AbstractOneCmdbTestCase(OneCMDBTestConfig config) {
		setName(this.getClass().getName());
		this.config = config;
	}

	public OneCMDBTestConfig getConfig() {
		return(this.config);
	}
	
	public void setUp() {
		
		onecmdbFactory = new SpringFactoryBean();
		
		onecmdbFactory.setOnecmdbProvider("org/onecmdb/core/tests/resources/onecmdb-basic.xml");
		onecmdbFactory.setDataSource(getDatasourceResource());
		onecmdbFactory.setDataProvider(getProviderResource());
		
		cmdbContext = (IOneCmdbContext) onecmdbFactory.getInstance();
		springContext = onecmdbFactory.getSpringContext();
		
		/*
		String[] resources = {
				"core-onecmdb.xml", 
				getDatasourceResource(),
				getProviderResource()
		};
		
		springContext = new ClassPathXmlApplicationContext(
				resources);
		

		cmdbContext = (IOneCmdbContext) springContext
				.getBean("onecmdb");
		*/	
		initVariables();
	}
	
	protected IOneCmdbContext getCmdbContext() {
		return(this.cmdbContext);
	}
	
	protected ConfigurableApplicationContext getSpringApplicationContext() {
		return(this.springContext);
	}
	
	
	protected void initVariables() {
		session = cmdbContext.createSession();
		session.getAuthentication().setUsername("admin");
		session.getAuthentication().setPassword("123");
		session.login();
		
		IModelService modelsvc = (IModelService) session
				.getService(IModelService.class);

		// well known name is ``root''
		this.root = modelsvc.getRoot();
		assertNotNull(root);

		// Check for Ci Service.
		ICiService cisvc = (ICiService) session.getService(ICiService.class);
		if (cisvc != null) {
			// well known name is ``CI''
			this.ciRoot = cisvc.getRootCi();
			assertNotNull(ciRoot);
		}
		
		// Check for Reference Service.	
		IReferenceService refsvc = (IReferenceService) session
		.getService(IReferenceService.class);
		if (refsvc != null) {
			this.ciRelationRoot = refsvc.getRootReference();
			assertNotNull(ciRelationRoot);
		}
		
		testUtils = new OnecmdbTestUtils(this.session);
	}

	private Object getAddonServices() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void tearDown() {
		if (onecmdbFactory != null) {
			onecmdbFactory.close();
			
			// Reset all variables to minimize memory loss.
			onecmdbFactory = null;
			session = null;
			ciRoot = null;
			root = null;
			ciRelationRoot = null;
			testUtils = null;
			cmdbContext = null;
			springContext = null;
			onecmdbFactory = null;


		}
	}
	
	protected void importTestProvider(IBeanProvider provider) {
		ImportBeanProvider importBeans = new ImportBeanProvider();
		importBeans.setSession(this.session);
		importBeans.setProvider(provider);
		
		importBeans.processProvider();
	}
	
	/**
	 * Return a resource (found in class path) that describes
	 * onecmdb 'datasource' spring bean definition.
	 * 
	 * @return a path to the resource
	 */
	protected String getDatasourceResource() {
		return(getConfig().getDataSourceProvider());
	}
	
	public void setDatasourceResource(String source) {
		getConfig().setDataSourceProvider(source);
	}
	
	
	/**
	 * Retrive a inilized common test utility.
	 * 
	 * @return
	 */
	protected OnecmdbTestUtils getTestUtil() {
		return(this.testUtils);
	}
	
	/**
	 * Return a resource (found in class path) that describes
	 * onecmdb 'provider' spring bean definition.
	 * 
	 * Override to add one provider, default is no model.
	 * To add more than one overide getProviderResources()
	 * @return a path to the resource
	 */
	protected String getProviderResource() {
		return(getConfig().getModelProvider());
	}
	

	/**
	 * Return a list of resources (found in class path) that describes
	 * a 'service' spring bean definition. The name of the bean
	 * must be service.
	 * 
	 * Override to add services, default is empty.
	 * @return a path to the resource
	 */
	protected List<String> getAddonServiceResources() {
		return(Collections.EMPTY_LIST);
	}
	
}
