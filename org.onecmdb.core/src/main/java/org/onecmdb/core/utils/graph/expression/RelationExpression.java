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

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.RelationItem;

public class RelationExpression extends OneCMDBExpression {
	
	private DetachedCriteria target;
	private DetachedCriteria source;
	private List<Long> targetIds;
	private List<Long> sourceIds;
	private String targetRelationProjection = "targetId";
	private String sourceRelationProjection = "sourceId";
	
	private String targetProjection = "longId";
	private String sourceProjection = "longId";
	
	
	
	public List<Long> getTargetIds() {
		return targetIds;
	}
	

	public void setTargetIds(List<Long> targetIds) {
		this.targetIds = targetIds;
	}


	public List<Long> getSourceIds() {
		return sourceIds;
	}


	public void setSourceIds(List<Long> sourceIds) {
		this.sourceIds = sourceIds;
	}


	public DetachedCriteria getTarget() {
		return target;
	}


	public void setTarget(DetachedCriteria target) {
		this.target = target;
	}


	public DetachedCriteria getSource() {
		return source;
	}


	public void setSource(DetachedCriteria source) {
		this.source = source;
	}
	
	protected String getRelationTargetProperty() {
		return(targetRelationProjection);
	}
	
	protected String getTargetProjection() {
		return(targetProjection);
	}
	
	protected String getRelationSourceProperty() {
		return(sourceRelationProjection);
	}
	
	protected String getSourceProjection() {
		return(sourceProjection);
	}
	
	protected DetachedCriteria getObjectCriteria() {
		DetachedCriteria crit = DetachedCriteria.forClass(ConfigurationItem.class);
		
		return(crit);
	}
	
	
	@Override
	public DetachedCriteria getCriteria() {
		DetachedCriteria crit = getObjectCriteria(); //DetachedCriteria.forClass(ConfigurationItem.class);
		
		if (getTargetIds() != null) {
			crit.add(Property.forName(getRelationTargetProperty()).in(targetIds));
		} else {
			DetachedCriteria targetCrit = target.setProjection(Projections.property(getTargetProjection()));
			crit.add(Property.forName(getRelationTargetProperty()).in(targetCrit));
		}
		
		if (getSourceIds() != null) {
			crit.add(Property.forName(getRelationSourceProperty()).in(sourceIds));
		} else {
			DetachedCriteria sourceCrit = source.setProjection(Projections.property(getSourceProjection()));
			crit.add(Property.forName(getRelationSourceProperty()).in(sourceCrit));
		}
		return(crit);
	}
	
	
	public DetachedCriteria getTargetCriteria() {
		DetachedCriteria crit = getObjectCriteria(); //DetachedCriteria.forClass(ConfigurationItem.class);
		if (this.targetIds != null) {
			crit.add(Property.forName(getRelationTargetProperty()).in(targetIds));
		} else {
			DetachedCriteria targetCrit = target.setProjection(Projections.property(getTargetProjection()));
		
			crit.add(Property.forName(getRelationTargetProperty()).in(targetCrit));
		}
		return(crit);
	}
	
	public DetachedCriteria getSourceCriteria() {
		DetachedCriteria crit = getObjectCriteria(); //DetachedCriteria.forClass(ConfigurationItem.class);
		
		if (this.sourceIds != null) {
			crit.add(Property.forName(getRelationSourceProperty()).in(this.sourceIds));
		} else {
			DetachedCriteria sourceCrit = source.setProjection(Projections.property(getSourceProjection()));
			crit.add(Property.forName(getRelationSourceProperty()).in(sourceCrit));
		}
		return(crit);
	}


	public Criterion getTargetCriterion() {
		DetachedCriteria crit = getTargetCriteria();
		Criterion relation = Property.forName(getSourceProjection()).in(crit.setProjection(Projections.property(getRelationSourceProperty())));
		return(relation);
	}


	public Criterion getSourceCriterion() {
		DetachedCriteria crit = getSourceCriteria();
		Criterion relation = Property.forName(getTargetProjection()).in(crit.setProjection(Projections.property(getRelationTargetProperty())));
		return(relation);
	}
	
	
}
