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
package org.onecmdb.core.internal.storage.expression;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;

public class SourceTemplateRelationExpression extends OneCMDBExpression {

	private Long targetId;
	private Long referenceTemplateId;

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	
	public void setReferenceTemplateId(Long id) {
		this.referenceTemplateId = id;
	}
	
	
	@Override
	protected DetachedCriteria getCriteria() {
		// Select Target/Source attribute id.
		DetachedCriteria attrTarget = DetachedCriteria.forClass(BasicAttribute.class);
		attrTarget.add(Expression.eq("valueAsLong", targetId));
		attrTarget.add(Expression.eq("alias", "target"));
		DetachedCriteria relOwnerCrit = attrTarget.setProjection(Projections.property("ownerId"));
		
		DetachedCriteria relCi = DetachedCriteria.forClass(ConfigurationItem.class);
		relCi.add(Property.forName("longId").in(relOwnerCrit));
		relCi.add(Expression.eq("derivedFromId", referenceTemplateId));
		
		DetachedCriteria targetOwnerCrit = relCi.setProjection(Projections.property("longId"));
		
		DetachedCriteria attrSource = DetachedCriteria.forClass(BasicAttribute.class);
		attrSource.add(Expression.eq("alias", "source"));
		attrSource.add(Property.forName("ownerId").in(targetOwnerCrit));
		
		DetachedCriteria sourceIdCrit = attrSource.setProjection(Projections.property("valueAsLong"));
		
		DetachedCriteria sourceCi = DetachedCriteria.forClass(ConfigurationItem.class);
		sourceCi.add(Property.forName("longId").in(sourceIdCrit));
		
		return(sourceCi);
	}
	

}
