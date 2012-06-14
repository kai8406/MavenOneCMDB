/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.core.utils.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.internal.model.AbstractAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;

public class BeanCache extends HashMap<Long, CiBean> {
	private static BeanCache cache = new BeanCache();
	private List<Long> insertOrder = new ArrayList<Long>();
	private long misses;
	private long hits;
	// No limit, can be set by spring to ModelService.
	private int maxSize = -1;
	
	public static BeanCache getInstance() {
		return(cache);
	}
	
	public void invalidate(IObjectScope scope) {
		for (ICi ci: scope.getDestroyedICis()) {
			
			remove(ci);
		}
		for (ICi ci: scope.getModifiedICis()) {
			remove(ci);
		}
		for (ICi ci: scope.getNewICis()) {
			remove(ci);
		}
	}
	
	public void remove(ICi ci) {
		if (ci instanceof AbstractAttribute) {
			remove(((AbstractAttribute)ci).getOwnerId());
		} else {
			CiBean cBean = remove(ci.getId().asLong());
			if (ci instanceof ConfigurationItem) {
				ConfigurationItem cfgItem = (ConfigurationItem)ci;
				if (cfgItem.getSourceId() != null) {
					remove(cfgItem.getSourceId());
				}
				if (cfgItem.getTargetId() != null) {
					remove(cfgItem.getTargetId());
				}
			}
			handleAliasChange(ci, cBean);
			
		}
	}
	
	private void handleAliasChange(ICi ci, CiBean cBean) {
		if (cBean == null || ci == null) {
			return;
		}
		String alias = ci.getAlias();
		if (alias == null) {
			return;
		}
		String oldAlias = cBean.getAlias();
		if (oldAlias == null) {
			return;
		}
		if (!alias.equals(oldAlias)) {
			//  Clean up if alias has changed.
			List<Long> removeIds = new ArrayList<Long>();
			for (CiBean bean : values()) {
				if (!bean.isTemplate()) {
					continue;
				}
				for (AttributeBean aBean : bean.getAttributes()) {
					if (oldAlias.equals(aBean.getType())) {
						removeIds.add(bean.getId());
					}
					if (oldAlias.equals(aBean.getRefType())) {
						removeIds.add(bean.getId());
					}
				}
			}
			for (Long id : removeIds) {
				remove(id);
			}
		}
	}
	
	public CiBean get(ICi ci) {
		CiBean bean = get(ci.getId().asLong());
		if (bean == null) {
			misses++;
		} else {
			// Need to copy the bean so we don't destroy entries.
			// Validate lastModified.
			bean = bean.copy();
			hits++;
		}
		return(bean);
	}
	
	private boolean isModified(Date lastModified, Date lastModified2) {
		if (lastModified == null) {
			return(true);
		}
		if (lastModified2 == null) {
			return(true);
		}
		if (lastModified.compareTo(lastModified2) != 0) {
			return(true);
		}
		return(false);
	}

	public void add(ICi ci, CiBean bean) {
		put(ci.getId().asLong(), bean);
		insertOrder.add(ci.getId().asLong());
		
		// Check size...
		if (this.maxSize >= 0)  {
			if (insertOrder.size() > this.maxSize) {
				Long id = insertOrder.remove(insertOrder.size()-1);
				remove(id);
			}
		}
	}
	
	public String getStatistics() {
		return("BeanCache: Size=" + size() + ", hits=" + hits + ", misses=" + misses);
	}

	public void setMaxSize(int size) {
		this.maxSize = size;
	}
}
