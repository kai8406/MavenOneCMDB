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
package org.onecmdb.core.internal;

import java.util.List;

import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.ImportBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;

/**
 * Abstract class that is aware of a default schema to import at init time.
 * @author niklas
 *
 */
public abstract class SchemaService {

	private IBeanProvider beanProvider;
	private ISession session;


	public void setSchemaProvider(IBeanProvider provider) {
		this.beanProvider = provider;
	}
	
	public void setSession(ISession session) {
		this.session = session;
	}
	
	protected ISession getSession() {
		return(this.session);
	}
	
	protected void setupSchema() {
		// Login Session
		if (this.session.isAnonymous()) {
			this.session.login();
		}
		ImportBeanProvider importBeans = new ImportBeanProvider();
		importBeans.setSession(this.session);
		importBeans.setProvider(this.beanProvider);
		
		importBeans.processProvider();
		
		List<CiBean> beans = this.beanProvider.getBeans();
		IModelService mSvc = (IModelService) session.getService(IModelService.class);
		if (mSvc != null) {
			for (CiBean bean : beans) {
				mSvc.addProtectedCI(bean.getAlias());
			}
		}
		// Logout session.
		this.session.logout();
	}

}
