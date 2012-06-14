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
package org.onecmdb.core.utils.graph.expression;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.onecmdb.core.internal.model.ConfigurationItem;



public class OffspringExpression extends OneCMDBExpression {

	private Long templateID;
	private Boolean matchTemplate;
	private boolean limitToChild;
	private String templatePath;
	
	public void setTemplateID(Long id) {
		this.templateID = id;
	}
	
	public Long getTemplateID() {
		return templateID;
	}
	
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	
	public boolean isLimitToChild() {
		return limitToChild;
	}

	public void setLimitToChild(boolean limitToChild) {
		this.limitToChild = limitToChild;
	}

	public Boolean getMatchTemplate() {
		return matchTemplate;
	}

	public void setMatchTemplate(Boolean matchTemplate) {
		this.matchTemplate = matchTemplate;
	}

	public DetachedCriteria getCriteria() {
		DetachedCriteria offsprings = DetachedCriteria.forClass(ConfigurationItem.class);
		//offsprings.addOrder(Order.asc("alias"));
		if (this.templateID != null) {
			
			if (limitToChild) {
				offsprings.add(Property.forName("derivedFromId").eq(this.templateID));
			} else {
				if (this.templatePath != null) {
					offsprings.add(Expression.like("templatePath", this.templatePath + "/%"));
				} else {
					offsprings.add(Expression.like("templatePath", "%/" + this.templateID + "/%"));
				}
			}
			
			//offsprings.add(Expression.eq("derivedFromId", templateID));
					
		} else if (getParent() != null) {
			DetachedCriteria crit = getParent().getCriteria();
			DetachedCriteria ids = crit.setProjection(Projections.property("longId"));
			offsprings.add(Property.forName("derivedFromId").eq(ids));
		} else {
		}
		if (matchTemplate != null) {
			offsprings.add(Property.forName("isBlueprint").eq(matchTemplate));
		}
		return(offsprings);
	}


	
}
