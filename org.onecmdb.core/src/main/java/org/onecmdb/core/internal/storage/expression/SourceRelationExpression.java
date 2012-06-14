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

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.onecmdb.core.ICi;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;

public class SourceRelationExpression extends OneCMDBExpression {

	private Long targetId;
	private Long sourceTemplateId;
	private String sourceTemplatePath;
	
	
	
	
	public Long getSourceTemplateId() {
		return sourceTemplateId;
	}

	public void setSourceTemplateId(Long sourceTemplateId) {
		this.sourceTemplateId = sourceTemplateId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public DetachedCriteria getCriteria() {
		// Select Target/Source attribute id.
		DetachedCriteria attrTarget = DetachedCriteria.forClass(BasicAttribute.class);
		attrTarget.add(Expression.eq("valueAsLong", targetId));
		attrTarget.add(Expression.eq("alias", "target"));
		DetachedCriteria targetOwnerCrit = attrTarget.setProjection(Projections.property("ownerId"));
		
		DetachedCriteria attrSource = DetachedCriteria.forClass(BasicAttribute.class);
		attrSource.add(Property.forName("ownerId").in(targetOwnerCrit));
		attrSource.add(Expression.eq("alias", "source"));
		
		
		DetachedCriteria sourceIdCrit = attrSource.setProjection(Projections.property("valueAsLong"));
		
		//DetachedCriteria sourceCi = DetachedCriteria.forClass(BasicAttribute.class);
		
		DetachedCriteria sourceCi = DetachedCriteria.forClass(ConfigurationItem.class);
		if (this.sourceTemplateId != null) {
			sourceCi.add(Expression.eq("derivedFromId", sourceTemplateId));
		} else if (this.sourceTemplatePath != null) {
			sourceCi.add(Expression.ilike("templatePath", sourceTemplatePath + "/%"));			
		}
		sourceCi.add(Property.forName("longId").in(sourceIdCrit));
		
		
		return(sourceCi);
	}

	public String getSourceTemplatePath() {
		return sourceTemplatePath;
	}
	
	/*
	public void setSourceTemplatePath(Long soutceTemplatePath) {
		this.sourceTemplatePath = soutceTemplatePath;
	}
	*/
	
	public void setSourceTemplatePathString(String templatePath) {
		this.sourceTemplatePath = templatePath;
		
	}
	
	
	

}
