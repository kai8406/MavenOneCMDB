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
package org.onecmdb.core.internal.job;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.ImportBeanProvider;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.IBeanProviderConfig;

public class InitModelProviderConfig implements IBeanProviderConfig {

	/**
	 * Root<T> 
	 * 	OneCmdbConfig<<T>>
	 * 	    thisIOneCmdbConfig<<I>>
	 * 			models<attribute>.
	 */
	
	private ISession session;
	private List<String> urls = new ArrayList<String>();

	public InitModelProviderConfig(ISession session) {
		this.session = session;
	}
	
	public boolean isImported(String url) {
		IModelService modSvc = (IModelService)this.session.getService(IModelService.class);
		ICi config = modSvc.findCi(new Path<String>("thisOneCmdbConfig"));
		if (config == null) {
			return(false);
		}
		List<IAttribute> attributes = config.getAttributesWithAlias("models");
		for (IAttribute a : attributes) {
			IValue value = a.getValue();
			if (value == null) {
				continue;				
			}
			if (value.getAsJavaObject().equals(url)) {
				return(true);
			}
		}
		return(false);
	}

	public void importURL(String url) {
		urls.add(url);
	}

	public void updateConfig() {
		final List<CiBean> beans = new ArrayList<CiBean>();
		
		// Create Onecmdb Config Template.
		{
			CiBean bean = new CiBean();
			bean.setDerivedFrom("Root");
			bean.setTemplate(true);
			bean.setAlias("OneCmdbConfig");
			bean.setDisplayNameExpression("OneCMDB Configuration");
			bean.setDescription("Holds read-only information of the current running OneCMDB.");
			bean.addAttributeValue(new ValueBean("icon", "one", false));
			beans.add(bean);
			AttributeBean aBean = new AttributeBean();
			aBean.setDescription("Stores loaded models at startup time. Models will not be loaded again.");
			aBean.setAlias("models");
			aBean.setComplexType(false);
			aBean.setType("xs:string");
			aBean.setMaxOccurs("unbound");
			bean.addAttribute(aBean);
			beans.add(bean);
		}
		
		// This Provider instance.
		CiBean thisOnecmdb = new CiBean();
		thisOnecmdb.setDerivedFrom("OneCmdbConfig");
		thisOnecmdb.setTemplate(false);
		thisOnecmdb.setDisplayNameExpression("OneCMDB Configuration");
		thisOnecmdb.setAlias("thisOneCmdbConfig");
		beans.add(thisOnecmdb);
		
		for (String url : urls) {
			thisOnecmdb.addAttributeValue(new ValueBean("models", url, false));
		}
		
		// Commit these two ci's.
		ImportBeanProvider importBeans = new ImportBeanProvider();
		importBeans.setSession(this.session);
		importBeans.setProvider(new IBeanProvider() {

			public List<CiBean> getBeans() {
				return(beans);
			}

			public CiBean getBean(String alias) {
				for (CiBean bean: beans) {
					if (bean.getAlias().equals(alias)) {
						return(bean);
					}
				}
				return(null);
			}
		});
		
		importBeans.processProvider();
	}

}
