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

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.server.service.change.ICIMDR;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.PropertyAdaptor;

public class CMDBWebServiceFactory {
	private static CMDBWebServiceFactory factory;
	private Log log = LogFactory.getLog(this.getClass());
	
	public static CMDBWebServiceFactory get() {
		if (factory == null) {
			factory = new CMDBWebServiceFactory();
			ContentFile config = new ContentFile();
			config.setPath(ConfigurationFactory.get(Config.OneCMDBWebService));
			config.set(Properties.class.getName(), PropertyAdaptor.class.getName());
			factory.setOneCMDBConfig(config);
		}
		return(factory);
	}

	private IOneCMDBWebService service;
	private ContentFile config;
	
	public void setOneCMDBConfig(ContentFile config) {
		this.config = config;
	}
	
	public IOneCMDBWebService getOneCMDBWebService() throws Exception {
		if (this.service == null) {
			Properties p = (Properties) ContentParserFactory.get().getAdaptor(this.config, Properties.class);
			String wsdl = p.getProperty("oneCMDBwsdl");
			log.info("OneCMDB WSDL URL '" + wsdl + "'");
			service = OneCMDBServiceFactory.getWebService(wsdl);
		}
		return(this.service);
	}
	
	public ICIMDR getOneCMDBCIMDR() {
		ICIMDR mdr = (ICIMDR) ContentParserFactory.get().getAdaptor(config, ICIMDR.class);
		return(mdr);
	}
		

	
}
