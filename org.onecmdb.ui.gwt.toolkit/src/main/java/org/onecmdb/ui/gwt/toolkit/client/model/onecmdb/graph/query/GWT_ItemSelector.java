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
package org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.graph.query;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;


public abstract class GWT_ItemSelector implements IsSerializable, Serializable {
	private String templateAlias;
	private GWT_ItemConstraint constraint;
	private String id;
	private boolean primary;
	private GWT_PageInfo pageInfo;
	
	/**
	 * @gwt.typeArgs <java.lang.String>
	 */
	private Set excludeRelationMap = null;
	
	public GWT_ItemSelector() {
	}
	
	
	public boolean isPrimary() {
		return primary;
	}


	public GWT_PageInfo getPageInfo() {
		return pageInfo;
	}


	public void setPageInfo(GWT_PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}


	public void setPrimary(boolean primary) {
		this.primary = primary;
	}


	protected GWT_ItemSelector(String template) {
		this.templateAlias = template;
	}
	protected GWT_ItemSelector(String id, String template) {
		this.templateAlias = template;
		this.id = id;
	}
	

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTemplateAlias() {
		return(this.templateAlias);
	}
	
	public void setTemplateAlias(String alias) {
		this.templateAlias = alias;
	}
	
	public void setConstraint(GWT_ItemConstraint constraint) {
		this.constraint = constraint;
	}
	
	public GWT_ItemConstraint getConstraint() {
		return(this.constraint);
	}
	

	public void setExcludeRelations(Set exclude) {
		this.excludeRelationMap = exclude;
	}
	public Set getExcludeRelations() {
		return(this.excludeRelationMap);
	}
	public void addExcludeRelation(String id) {
		if (this.excludeRelationMap == null) {
			this.excludeRelationMap = new HashSet();
		}
		this.excludeRelationMap.add(id);
	}


	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		/*
		if (getClass() != obj.getClass())
			return false;
		*/
		final GWT_ItemSelector other = (GWT_ItemSelector) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
