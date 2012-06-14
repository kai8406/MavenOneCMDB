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
package org.onecmdb.core.utils.graph.query.selector;

import java.util.HashSet;
import java.util.Set;

import org.onecmdb.core.ICi;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.graph.query.OrderInfo;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemAndGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemIdConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemNotConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemOrGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.constraint.RFCTargetConstraint;


public class ItemSelector implements Cloneable {
	private String id;
	private String templateAlias;
	private ItemConstraint constraint;
	private ICi template;
	private PageInfo pageInfo;
	private boolean primary;
	private Set<String> excludeRelationMap = null;
	private boolean excludedInResultSet = false;
	private OrderInfo orderInfo;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ItemSelector(String id, String template) {
		this.id = id;
		this.templateAlias = template;
	}
	
	public ItemSelector() {
	}

	public void setTemplateAlias(String alias) {
		this.templateAlias = alias;
	}

	public String getTemplateAlias() {
		return(this.templateAlias);
	}
	
	public AttributeValueConstraint getAttributeValueSelector() {
		if (constraint instanceof AttributeValueConstraint) {
			return((AttributeValueConstraint)constraint);
		}
		return(null);
	}
	
	public void setAttributeValueSelector(AttributeValueConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public RFCTargetConstraint getRFCTargetConstraint() {
		if (constraint instanceof RFCTargetConstraint) {
			return((RFCTargetConstraint)constraint);
		}
		return(null);
	}
	
	public void setRFCTargetConstraint(RFCTargetConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemIdConstraint getItemIdConstraint() {
		if (constraint instanceof ItemIdConstraint) {
			return((ItemIdConstraint)constraint);
		}
		return(null);
	}
	
	public void setItemIdConstraint(ItemIdConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemAndGroupConstraint getItemAndGroupConstraint() {
		if (constraint instanceof ItemAndGroupConstraint) {
			return((ItemAndGroupConstraint)constraint);
		}
		return(null);
	}
	public void setItemAndGroupConstraint(ItemAndGroupConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemOrGroupConstraint getItemOrGroupConstraint() {
		if (constraint instanceof ItemOrGroupConstraint) {
			return((ItemOrGroupConstraint)constraint);
		}
		return(null);
	}
	public void setItemOrGroupConstraint(ItemOrGroupConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemSecurityConstraint getItemSecurityConstraint() {
		if (constraint instanceof ItemSecurityConstraint) {
			return((ItemSecurityConstraint)constraint);
		}
		return(null);
		
	}
	
	public void setItemSecurityConstraint(ItemSecurityConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemNotConstraint getItemNotGroupConstraint() {
		if (constraint instanceof ItemNotConstraint) {
			return((ItemNotConstraint)constraint);
		}
		return(null);
		
	}
	
	public void setItemNotGroupConstraint(ItemNotConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public void applyConstraint(ItemConstraint constraint) {
		this.constraint = constraint;
	}
	
	public ItemConstraint fetchConstraint() {
		return(this.constraint);
	}

	public ICi fetchTemplate() {
		return template;
	}

	public void bindTemplate(ICi template) {
		this.template = template;
	}
	
	public boolean reference() {
		return(false);
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	
	public ItemSelector clone() {
		try {
			return ((ItemSelector) super.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(null);
	}

	public boolean isPrimary() {
		return(primary);
	}
	
	public void setPrimary(boolean value) {
		this.primary = value;
	}

	public String toString() {
		return("ItemSelector{id=" + id + "}");
	}

	public boolean hasTemplate() {
		return(true);
	}
	
	
	public boolean excludeRelation(String relationId) {
		if (excludeRelationMap == null) {
			return(false);
		}
		return(excludeRelationMap.contains(relationId));
	}
	
	public void setExcludeRelations(Set<String> exclude) {
		this.excludeRelationMap = exclude;
	}
	public Set<String> getExcludeRelations() {
		return(this.excludeRelationMap);
	}
	public void addExcludeRelation(String id) {
		if (this.excludeRelationMap == null) {
			this.excludeRelationMap = new HashSet<String>();
		}
		this.excludeRelationMap.add(id);
	}
	
	public boolean isExcludedInResultSet() {
		return excludedInResultSet;
	}

	public void setExcludedInResultSet(boolean excludeInResultSet) {
		this.excludedInResultSet = excludeInResultSet;
	}

	public OrderInfo getOrderInfo() {
		return(this.orderInfo);
	}
	
	public void setOrderInfo(OrderInfo info) {
		this.orderInfo = info;
	}
	
	
	
}
