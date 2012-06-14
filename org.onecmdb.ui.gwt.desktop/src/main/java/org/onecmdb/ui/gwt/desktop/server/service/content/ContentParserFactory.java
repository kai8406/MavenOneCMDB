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
package org.onecmdb.ui.gwt.desktop.server.service.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.IContentService;
import org.onecmdb.ui.gwt.desktop.server.service.ServiceLocator;
import org.onecmdb.ui.gwt.desktop.server.service.content.adapter.IAdaptable;

public class ContentParserFactory {
	private static Log log = LogFactory.getLog(ContentParserFactory.class);
	
	private static ContentParserFactory factory;
	
	public static ContentParserFactory get() {
		if (factory == null) {
			ContentParserFactory f = new ContentParserFactory();
			Properties p = new Properties();
			
			String repositoryHome = System.getProperty("ONECMDB_REPOSITORY_HOME");
			File rHomeFile = null;
			if (repositoryHome != null) {
				rHomeFile = new File(repositoryHome);
			}
			
			String oneCMDBHome = System.getProperty("ONECMDB_HOME");
			File etcRepFile = null;
			if (oneCMDBHome != null) {
				etcRepFile = new File(oneCMDBHome, "etc/repositry.cfg");
			}
			InputStream in = null;
			try {
				if (rHomeFile != null && rHomeFile.exists() && rHomeFile.isFile()) {
					log.info("Use '" + rHomeFile.getPath() + "' repository config");
					in = new FileInputStream(rHomeFile); 
				} else if (etcRepFile != null && etcRepFile.exists() && rHomeFile.isFile()) {
					log.info("Use '" + etcRepFile.getPath() + "' repository config");
					in = new FileInputStream(etcRepFile); 
				} else {
					log.info("Use class resource repository config");
					in = ContentParserFactory.class.getClassLoader().getResourceAsStream("repository.cfg");
				}
				p.load(in);
			} catch (Throwable t) {
				log.fatal("No 'repository.cfg' found!", t);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {
						// Silently ignore
					}
				}
			}
			// Fetch Reposity root.
			try {
				String repositoryRoot = p.getProperty("repositoryRoot");
				File rRoot = new File(repositoryRoot);
				if (!rRoot.exists()) {
					String msg = "'repository.cfg' is invalid. repositoryRoot=" + repositoryRoot + " don't exists!";
					log.fatal(msg);
					throw new IllegalArgumentException(msg);			
				}
				if (!rRoot.isDirectory()) {
					String msg = "'repository.cfg' is invalid. repositoryRoot=" + repositoryRoot + " is not a directory!";
					log.fatal(msg);
					throw new IllegalArgumentException(msg);			
				}
				log.info("Repository Root is set to '" + rRoot.getCanonicalPath() + "'");
				f.setRootPath(rRoot);
				f.setFactory(f);
			} catch (IOException e) {
				log.fatal("No 'repository.cfg' found i class path!", e);
			}
		}
		return(factory);
	}


	private File rootPath;

	private HashMap<String, Object> adaptorCache = new HashMap<String, Object>();

	public void setRootPath(File path) {
		this.rootPath = path;
	}
	
	public File getRootPath() {
		return rootPath;
	}

	public void setFactory(ContentParserFactory factory) {
		this.factory = factory;
	}

	public void stat(ContentData data) {
		IContentService cService = (IContentService) ServiceLocator.getService(IContentService.class);
		if (cService == null) {
			cService = new ContentServiceImpl();
		}
		cService.stat(data);
	}
	
	public Object getCachedAdaptor(ContentData data, Class type)  {
		Object o = adaptorCache .get(data.getPath());
		if (o == null) {
			o = getAdaptor(data, type);
			adaptorCache.put(data.getPath(), o);
		}
		return(o);
	}
	
	public Object getAdaptor(ContentData data, Class type)  {
		// Validate that all data have the same adaptor...
		data.setAllowNestedValues(false);
		String name = type.getName();
		String impl = data.get(name);
		
		if (impl == null) {
			// Try to update metadata.
			IContentService cService = (IContentService) ServiceLocator.getService(IContentService.class);
			if (cService == null) {
				cService = new ContentServiceImpl();
			}
			if (cService != null) {
				impl = cService.updateMetaData(data).get(name);
			}
		}
		log.info("Adapt file '" + data.getPath() + "' to '" + type.getName() + "' with '" + impl + "'");
		if (impl == null) {
			throw new IllegalArgumentException("Name " + name + " has no impl class ");
		}
		
		Object instance = null;
		try {
			Class clazz = Class.forName(impl);
			instance = clazz.newInstance();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Can't adopt " + data.getName() + " to class " + type.getName(), e);
		} 
		
		if (instance instanceof IContentDataAware) {
			((IContentDataAware)instance).setContentData(data);
		}
		if (instance instanceof IAdaptable) {
			return(((IAdaptable)instance).getAdapter(type));
		}
		return(instance);
	}
	
	public URL getURL(ContentData data) {
		try {
			File content = new File(getRootPath(), data.getPath());
			return(content.toURL());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("URL not corret<" + data.getPath() + ">", e);
		}
	}

	public InputStream getInputStream(ContentData data) throws IOException {
		URL url = getURL(data);
		return(url.openStream());
	}
	

}
