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
package org.onecmdb.core.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IOneCmdbContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringFactoryBean {
	public static IOneCmdbContext context = null;
	public static ClassPathXmlApplicationContext appContext = null;
	public static int handlers = 0;
	
	
    // resources are loaded as classpath resources
    
    private String dataSource = "datasource.xml";
	private String dataProvider = "provider.xml";
	private String onecmdbProvider = "onecmdb-basic.xml";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setDataProvider(String provider) {
		this.dataProvider = provider;
	}

	public void setOnecmdbProvider(String provider) {
		this.onecmdbProvider  = provider;
	}
	
	
	
	public synchronized Object getInstance() {
		log.info("LOAD ONECMDB CONTEXT=" + context +", handlers=" + handlers);
		
		if (context == null) {
			String[] resources = {
					onecmdbProvider, 
					dataSource,
					dataProvider,
			};
			appContext = new ClassPathXmlApplicationContext(resources);
	
			context = (IOneCmdbContext) appContext
				.getBean("onecmdb");
		}
		this.handlers++;
		
		return(context);
	}
	
	public synchronized void close() {

		this.handlers--;
		log.info("CLOSE ONECMDB CONTEXT=" + context +", handlers=" + handlers);

	//	if (this.handlers <= 0) {
			
			log.info("DESTORY ONECMDB CONTEXT=" + context +", handlers=" + handlers);
			
			if (appContext != null) {
				// Close onecmdb context.
				context.close();
				
				// Close application context 
				appContext.close();
			
				appContext = null;
			
				context = null;
			}
		//	this.handlers = 0;
		//}
	}

	public ConfigurableApplicationContext getSpringContext() {
		return(this.appContext);
	}
}
