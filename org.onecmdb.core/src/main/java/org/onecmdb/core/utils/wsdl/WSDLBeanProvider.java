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
package org.onecmdb.core.utils.wsdl;

import java.util.Arrays;
import java.util.List;

import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;

public class WSDLBeanProvider implements IBeanProvider {
	
	
	private IOneCMDBWebService service;
	private String token;

	public WSDLBeanProvider(String wsdl, String user, String pwd) throws Exception {
		this.service = OneCMDBServiceFactory.getWebService(wsdl);
		this.token = service.auth(user, pwd);
	}
	
	
	public WSDLBeanProvider(IOneCMDBWebService srvc, String authToken) {
		this.service = srvc;
		this.token = authToken;
	}


	public CiBean getBean(String alias) {
		QueryCriteria criteria = new QueryCriteria();
		criteria.setCiAlias(alias);
		CiBean beans[] = this.service.search(this.token, criteria);
		if (beans == null) {
			return(null);
		}
		if (beans.length != 1) {
			return(null);
		}
		return(beans[0]);
	}

	public List<CiBean> getBeans() {
		QueryCriteria criteria = new QueryCriteria();		
		CiBean beans[] = this.service.search(this.token, criteria);
		List<CiBean> beanList = Arrays.asList(beans);
		return(beanList);
	}

}
