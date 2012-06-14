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
package org.onecmdb.ui.gwt.desktop.server.service.change;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeItem;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentServiceImpl;
import org.onecmdb.ui.gwt.desktop.server.service.model.CMDBWebServiceFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.ConfigurationFactory;

import com.sun.corba.se.impl.transport.CorbaInboundConnectionCacheImpl;

public class ChangeStoreImpl implements IChangeStore {
	private ContentFile config;
	private ICIMDR remoteService;
	private Log log = LogFactory.getLog(this.getClass());
	
	private String getRoot() {
		String path = ConfigurationFactory.get(Config.ChangeStoreRootPath);
		if (path != null) {
			File contentRoot = ContentParserFactory.get().getRootPath();
			try {
				return(contentRoot.getCanonicalPath() + "/" + path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		throw new IllegalArgumentException("No ChangeStoreRootPath specified!");
	}
	
	public ChangeStoreImpl() {
	}
	
	public void commit(String token, ICIMDR local, List<ChangeItem> items) throws Exception {
		/*
		if (local instanceof CIMDRCollection) {
			CIMDRCollection col = (CIMDRCollection)local;
			CIMDRCollection result = new CIMDRCollection();
			for (ICIMDR mdr : col.getMDRs()) {
				commit(token, mdr, items);
			}
			return;
		}
		*/
		
		XmlGenerator gen = new XmlGenerator();
		File path = new File(getRoot(), local.getID());
		if (!path.exists()) {
			path.mkdirs();
		}
		File base = new File(path, "base.xml");
		String output = base.getCanonicalPath();
		gen.setOutput(output);
		List<CiBean> beans = local.getCI(token);
		
		List<CiBean> storeBeans = new ArrayList<CiBean>();
		// Update id's....
		ICIMDR remoteMDR = getRemote();
		List<String> aliases = new ArrayList<String>();
		for (CiBean bean : beans) {
			aliases.add(bean.getAlias());			
		}
		
		List<CiBean> remotes = remoteMDR.getCIs(token, aliases);
		
		for (CiBean remote : remotes) {
			CiBean bean = local.getCI(token, remote.getAlias());
			if (bean == null) {
				continue;
			}
			// Remove Attributes/Values not defined here.
			for (AttributeBean aBean : remote.getAttributes()) {
				if (bean.getAttribute(aBean.getAlias()) == null) {
					remote.removeAttribute(aBean);
				}
			}
			for (ValueBean vBean : remote.getAttributeValues()) {
				boolean found = false;
				List<ValueBean> localValues = bean.fetchAttributeValueBeans(vBean.getAlias());
				for (ValueBean localValue : localValues) {
					if (localValue.getValue() == null) {
						if (vBean.getValue() == null) {
							found = true;
							break;
						}
					}
					if (localValue.getValue().equals(vBean.getValue())) {
						found = true;
						break;
					}
				}
				if (!found) {
					remote.removeAttributeValue(vBean);
				}
			}
			
			storeBeans.add(remote);
		}
		// Filter Change items
		gen.setBeans(storeBeans);
		gen.process();
	}

	public ICIMDR getBase(ICIMDR localMDR) {
		
		ICIMDR base = new CIMDRCollection(localMDR.getID());
		File path = new File(getRoot(), localMDR.getID());
		if (!path.exists()) {
			return(base);
		}
		File file = new File(path, "base.xml");
		if (!file.exists()) {
			return(base);
		}
		try {
			XmlParser provider = new XmlParser();
			provider.setURL(file.toURL().toExternalForm());
			CIMDRBeanProvider mdr = new CIMDRBeanProvider();
			mdr.setProvider(provider);
			return(mdr);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(base);
		/*
		if (localMDR instanceof CIMDRCollection) {
			CIMDRCollection col = (CIMDRCollection)localMDR;
			CIMDRCollection result = new CIMDRCollection();
			for (ICIMDR mdr : col.getMDRs()) {
				ICIMDR b = getBase(mdr);
				if (b != null) {
					result.add(b);
				}
			}
			base = result;
		} else {
			
			// Try to open the latest for this 
			File path = new File(getRoot(), localMDR.getID());
			
			if (path.exists()) {
				try {
					XmlParser provider = new XmlParser();
					provider.setURL(path.toURL().toExternalForm());
					CIMDRBeanProvider mdr = new CIMDRBeanProvider();
					mdr.setProvider(provider);
					base = mdr;
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return(base);
		*/
	}

	

	public ICIMDR getRemote() throws Exception  {
		return(new OneCMDBWebServiceMDR(CMDBWebServiceFactory.get().getOneCMDBWebService()));
	}

	public void reset(String token, CIMDRCollection local,
			List<ChangeItem> items) throws IOException {
		XmlGenerator gen = new XmlGenerator();
		File path = new File(getRoot(), local.getID());
		if (!path.exists()) {
			path.mkdirs();
		}
		File base = new File(path, "base.xml");
		String output = base.getCanonicalPath();
		gen.setOutput(output);
		
		List<CiBean> storeBeans = new ArrayList<CiBean>();
		// Filter Change items
		gen.setBeans(storeBeans);
		gen.process();
		
	}
	

}
