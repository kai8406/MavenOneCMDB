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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.onecmdb.core.internal.model.ConfigurationItem;

public class TemplateRelationExpression extends RelationExpression {

	@Override
	protected DetachedCriteria getCriteria() {
		DetachedCriteria relCrit = super.getCriteria();
		DetachedCriteria instanceCrit = relCrit.setProjection(Projections.property("derivedFromId"));
	
		DetachedCriteria templateRelation = DetachedCriteria.forClass(ConfigurationItem.class);
		templateRelation.add(Property.forName("longId").in(instanceCrit));	
		
		return(templateRelation);
	}
}
