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

import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;

import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;

public class ParentBeanProvider implements IBeanProvider {
	
	private IBeanProvider instanceProvider;
	private IBeanProvider templateProvider;
	
	
	public IBeanProvider getInstanceProvider() {
		return instanceProvider;
	}

	public void setInstanceProvider(IBeanProvider instanceProvider) {
		this.instanceProvider = instanceProvider;
	}

	public IBeanProvider getTemplateProvider() {
		return templateProvider;
	}

	public void setTemplateProvider(IBeanProvider templateProvider) {
		this.templateProvider = templateProvider;
	}

	public CiBean getBean(String alias) {
		CiBean bean = instanceProvider.getBean(alias);
		if (bean == null) {
			return(this.templateProvider.getBean(alias));
		}
		CiBean instance = updateBean(bean);
		return(instance);
	}

	/**
	 * Update instance with values from template!
	 * @param bean
	 * @return
	 */
	private CiBean updateBean(CiBean bean) {
		if (bean == null) {
			return(null);
		}
		if (bean.isTemplate()) {
			// Update Attributes.
			return(updateTemplate(bean));
		} 
		return(updateInstance(bean));
	}
	
	private CiBean updateTemplate(CiBean bean) {
		// Update attributes...
		CiBean copy = bean.copy();
		
		// Check if found in template provider.
		CiBean template = this.templateProvider.getBean(bean.getDerivedFrom());
		if (template == null) {
			template = getBean(bean.getDerivedFrom());
		}
		if (template != null) {
			for (AttributeBean aBean : template.getAttributes()) {
				if (copy.getAttribute(aBean.getAlias()) == null) {
					copy.addAttribute(aBean.copy());
				}
			}
		}
		return(copy);		
	}

	private CiBean updateInstance(CiBean bean) {
		CiBean instance = bean.copy();
		CiBean template = getBean(bean.getDerivedFrom());
		if (template == null) {
			throw new IllegalArgumentException("Template <" + bean.getDerivedFrom() + "> not found!");
		}
		List<ValueBean> templateValues = template.getAttributeValues();
		for (ValueBean tValue : templateValues) {
			List<ValueBean> iValues = instance.fetchAttributeValueBeans(tValue.getAlias());
			if (iValues.size() == 0) {
				instance.addAttributeValue(tValue.copy());
			}
		}
		return(instance);
	}

	public List<CiBean> getBeans() {
		List<CiBean> beans = instanceProvider.getBeans();
		List<CiBean> instances = new ArrayList<CiBean>();
		for (CiBean bean : beans) {
			instances.add(updateInstance(bean));
		}
		return(instances);
	}
	

}
