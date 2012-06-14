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
package org.onecmdb.core.internal.storage.expression;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;

public abstract class OneCMDBExpression  {

	private Integer maxResult;
	private Integer firstResult;
	private boolean count;
	private OrderExpression order;
	private String textMatch;
	private boolean textMatchValue;
	private boolean textMatchDescription;
	private boolean textMatchAlias;
	

	public String getTextMatch() {
		return textMatch;
	}
	public void setTextMatch(String textMatch) {
		this.textMatch = textMatch;
	}
	public boolean isTextMatchAlias() {
		return textMatchAlias;
	}
	public void setTextMatchAlias(boolean textMatchAlias) {
		this.textMatchAlias = textMatchAlias;
	}
	public boolean isTextMatchDescription() {
		return textMatchDescription;
	}
	public void setTextMatchDescription(boolean textMatchDescription) {
		this.textMatchDescription = textMatchDescription;
	}
	public boolean isTextMatchValue() {
		return textMatchValue;
	}
	public void setTextMatchValue(boolean textMatchValue) {
		this.textMatchValue = textMatchValue;
	}
	public Integer getMaxResult() {
		return(maxResult);
	}
	public Integer getFirstResult() {
		return(firstResult);
	}
	
	public boolean isCount() {
		return(count);
	}
	public void setCount(boolean count) {
		this.count = count;
	}
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}
	
	public void setOrder(OrderExpression order) {
		this.order = order;
	}

	
	public DetachedCriteria composeCriteria() {
		DetachedCriteria dCrit = getCriteria();
		
		// Add CI search 
		dCrit = addCISearch(dCrit);
		
		
		if (isCount()) {
			// Skip order.
			return(dCrit);
		}
		if (this.order != null) {
			this.order.setCiCriteria(dCrit);
			dCrit = this.order.getCriteria();
		}
		return(dCrit);
	}
	
	public DetachedCriteria getOrderCriteria() {
		
		if (this.order != null) {
			DetachedCriteria dCrit = getCriteria();
			this.order.setCiCriteria(dCrit);
			dCrit = this.order.getAttributeCriteria();
			return(dCrit);
		}
		return(null);
	}
	
	protected DetachedCriteria getAttributeSearch(DetachedCriteria ciCrit) {
		if (getTextMatch() != null) {
			DetachedCriteria attribute = DetachedCriteria.forClass(BasicAttribute.class);
			//ciCrit.setProjection();
			Disjunction orAttribute = Restrictions.disjunction();
			boolean orAttributeAdded = false;
			
			if (isTextMatchAlias()) {
				orAttribute.add(Expression.ilike("alias", getTextMatch(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
				
			}
			
			if (isTextMatchDescription()) {
				orAttribute.add(Expression.ilike("description", getTextMatch(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
		
			}
			
			if (isTextMatchValue()) {
				orAttribute.add(Expression.ilike("valueAsString", getTextMatch(), MatchMode.ANYWHERE));
				orAttributeAdded = true;
			}
			if (orAttributeAdded) {
				DetachedCriteria ciIdCriteria = ciCrit.setProjection(Projections.property("longId"));
				DetachedCriteria attributeCriteira = DetachedCriteria.forClass(BasicAttribute.class);
				attributeCriteira.add(Property.forName("ownerId").in(ciIdCriteria));
				attributeCriteira.add(orAttribute);
				
				return(attributeCriteira);
			}
		}
		return(null);
	}
	
	protected DetachedCriteria addCISearch(DetachedCriteria ciCrit) {
		if (getTextMatch() != null) {
			Disjunction orCi = Restrictions.disjunction();
			 
			boolean orCiAdded = false;
			if (isTextMatchAlias()) {
				orCi.add(Expression.ilike("alias", getTextMatch(), MatchMode.ANYWHERE));
				orCiAdded = true;
			}
			
			if (isTextMatchDescription()) {
				orCi.add(Expression.ilike("description", getTextMatch(), MatchMode.ANYWHERE));
				orCiAdded = true;
			}
			if (orCiAdded) {
				DetachedCriteria attributeCriteria = getAttributeSearch(ciCrit);
				DetachedCriteria attrOwnerIdCriteria = attributeCriteria.setProjection(Projections.property("ownerId"));
				orCi.add(Property.forName("longId").in(attrOwnerIdCriteria));
				
				
				DetachedCriteria selectSearchCi = DetachedCriteria.forClass(ConfigurationItem.class);
				selectSearchCi.add(orCi);
				return(selectSearchCi);
			}
		}
		return(ciCrit);
	}
		
	protected abstract DetachedCriteria getCriteria();
	
	

}
