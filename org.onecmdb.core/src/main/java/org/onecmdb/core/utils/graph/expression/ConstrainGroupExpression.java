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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

public class ConstrainGroupExpression extends OneCMDBExpression {
	private List attributeExpressions = new ArrayList();
	private boolean disjunction;
	
	@Override
	public DetachedCriteria getCriteria() {
		DetachedCriteria crit = getParent().getCriteria();
		Junction junction = null;
		if (disjunction) {
			junction = Restrictions.disjunction();
		} else {
			junction = Restrictions.conjunction();
		}
		for (Iterator iter = attributeExpressions.iterator(); iter.hasNext();) {
			AttributeExpression aExpr = (AttributeExpression) iter.next();
			DetachedCriteria attrCriteria = aExpr.getCriteria();
			junction.add(Property.forName("longId").in(attrCriteria.setProjection(Projections.property("ownerId"))));
		}
		crit.add(junction);
		
		return(crit);
	}
	
	public void add(AttributeExpression expr) {
		attributeExpressions.add(expr);
	}
}
