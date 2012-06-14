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

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.onecmdb.core.internal.ccb.rfc.RFC;

public class RFCRelationExpression extends RelationExpression {

	@Override
	protected String getRelationSourceProperty() {
		return("longId");
	}
	
	protected String getRelationTargetProperty() {
		return("targetCIId");
	}
	
	protected DetachedCriteria getObjectCriteria() {
		DetachedCriteria crit = DetachedCriteria.forClass(RFC.class);
		
		return(crit);
	}
	
	
	
	@Override
	public Criterion getTargetCriterion() {
		if (this.getTargetIds() != null) {
			return(Property.forName(getRelationTargetProperty()).in(this.getTargetIds()));
		} else {
			DetachedCriteria targetCrit = getTarget().setProjection(Projections.property(getTargetProjection()));
		
			return(Property.forName(getRelationTargetProperty()).in(targetCrit));
		}
	}
	
	@Override
	public Criterion getSourceCriterion() {
		if (this.getTargetIds() != null) {
			return(Property.forName(getRelationSourceProperty()).in(this.getSourceIds()));
		} else {
			DetachedCriteria sourceCrit = getSource().setProjection(Projections.property(getSourceProjection()));
		
			return(Property.forName(getRelationSourceProperty()).in(sourceCrit));
		}
	}

	@Override
	protected String getSourceProjection() {
		return("targetCIId");
	}
	

}
