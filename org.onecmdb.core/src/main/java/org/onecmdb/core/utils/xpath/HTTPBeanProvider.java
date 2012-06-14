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
package org.onecmdb.core.utils.xpath;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentException;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xpath.client.OneCMDBHTTPClient;

public class HTTPBeanProvider implements IBeanProvider {
	
	private OneCMDBHTTPClient client;
	private HashMap<String, CiBean> beanMap = null;
	
	public void setBaseURL(URL url) {
		client = new OneCMDBHTTPClient();
		client.setBaseURL(url);
		client.setUser("test");
		client.setPwd("1qaz1qaz");
		try {
			client.login();
		} catch (IOException e) {
			throw new IllegalAccessError("Can't login to URL " + url.toExternalForm() + ":" + e.getMessage());
		}
	}
	
	public List<CiBean> getBeans()  {
		if (beanMap == null) {
			beanMap = new HashMap<String, CiBean>();
		
			List<CiBean> beans;
			try {
				beans = client.getBeans("/template/*", "*");
				List<CiBean> instanceBeans = client.getBeans("/instance/*", "*");
				beans.addAll(instanceBeans);
		
				for (CiBean bean : beans) {
					beanMap.put(bean.getAlias(), bean);
				}
			} catch (IOException e) {
				throw new IllegalAccessError("Check url/user/pwd.");
			} catch (DocumentException e) {
				throw new IllegalArgumentException("Not a correct query! " + e.getMessage());
			}
		}
		List<CiBean> result = new ArrayList<CiBean>();
		result.addAll(beanMap.values());
		return(result);
	
	}

	public CiBean getBean(String alias) {
		CiBean bean = beanMap.get(alias);
		if (bean != null) {
			return(bean);
		}
		try {
			List<CiBean> beans = client.getBeans("/template/" + alias, "*");
			if (beans.size() == 1) {				
				return(beans.get(0));
			}
			
			beans = client.getBeans("/instance/*/" + alias, "*");
			
			if (beans.size() == 1) {
				return(beans.get(0));
			}
			return(null);
			
		} catch (IOException e) {
			throw new IllegalAccessError("Check url/user/pwd.");
		} catch (DocumentException e) {
			throw new IllegalArgumentException("Not a correct query!");
		}
	}

}
