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
import org.hibernate.criterion.Expression;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.primitivetypes.DateTimeType;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;

public class AttributeValueExpression extends OneCMDBExpression {
	

	
	
	private String alias;
	private String stringValue;
	
	private int operation;
	private int type;
	
	
	public String getAlias() {
		return alias;
	}





	public void setAlias(String alias) {
		this.alias = alias;
	}





	public String getStringValue() {
		return stringValue;
	}





	public void setStringValue(String value) {
		this.stringValue = value;
	}





	public int getOperation() {
		return operation;
	}





	public void setOperation(int operation) {
		this.operation = operation;
	}





	public int getType() {
		return type;
	}





	public void setType(int type) {
		this.type = type;
	}





	@Override
	public DetachedCriteria getCriteria() {
		DetachedCriteria attr = DetachedCriteria.forClass(BasicAttribute.class);
		/*
		DetachedCriteria owner = getParent().getCriteria();
		if (owner != null) {
			DetachedCriteria ownerID = owner.setProjection(Projections.property("longId"));
			attr.add(Property.forName("ownerId").in(ownerID));
		}
		*/
		String valueType = "";
		Object value = stringValue;


		if (alias != null) {
			attr.add(Expression.eq("alias", alias));
		}
		valueType = "valueAsString";

		switch(type) {
		case AttributeValueConstraint.STRING_DATA_TYPE:
			valueType = "valueAsString";
			value = stringValue;
			break;
		case AttributeValueConstraint.NUMBER_DATA_TYPE:
			valueType = "valueAsLong";
			value = Long.valueOf(stringValue);
			break;
		case AttributeValueConstraint.DATE_DATA_TYPE:
			valueType = "valueAsDate";
			value = (new DateTimeType()).parseString(stringValue).getAsJavaObject();
			break;
		case AttributeValueConstraint.TYPE_DATA_TYPE:
			valueType = "typeName";
			value = stringValue;
			break;
		case AttributeValueConstraint.REFTYPE_DATA_TYPE:
			valueType = "referenceTypeName";
			value = stringValue;
			break;
		case AttributeValueConstraint.DERIVED_DATA_TYPE:
			valueType = "isBlueprint";
			if ("true".equalsIgnoreCase(stringValue)) { 
				value = Boolean.TRUE;
			} else {
				value = Boolean.FALSE;
			}
			break;
		}
		attr.add(getCriterion(valueType, value));
		
		
		/*
		DetachedCriteria ownerCI = DetachedCriteria.forClass(ConfigurationItem.class);
		ownerCI.add(Property.forName("longId").in(attr.setProjection(Projections.property("ownerId"))));
		*/
		return(attr);
	}
	




	private Criterion getCriterion(String valueType, Object value) {
		switch(operation) {
		case AttributeValueConstraint.EQUALS:
			return(Expression.eq(valueType, value));
		case AttributeValueConstraint.GREATER_THAN:
			return(Expression.gt(valueType, value));
		case AttributeValueConstraint.LESS_THAN:
			return(Expression.lt(valueType, value));
		case AttributeValueConstraint.GREATER_THAN_OR_EQUAL:
			return(Expression.ge(valueType, value));
		case AttributeValueConstraint.LESS_THAN_OR_EQUAL:
			return(Expression.le(valueType, value));
		case AttributeValueConstraint.LIKE:
			return(Expression.ilike(valueType, value));
		case AttributeValueConstraint.CONTAINS:
			break;
		case AttributeValueConstraint.IS_NULL:
			return(Expression.isNull(valueType));
		}
		return(Expression.conjunction());
	}





	public boolean isInternal() {
		if (type >=  AttributeValueConstraint.INTERNAL) {
			return(true);
		}
		return(false);
	}





	public Criterion getInternalCriterion() {
		String valueType = "alias";
		String value = stringValue;
		switch(type) {
			case AttributeValueConstraint.INTERNAL_ALIAS:
				valueType = "alias";
				break;
			case AttributeValueConstraint.INTERNAL_DESCRIPTION:
				valueType = "description";
				break;
		}
		return(getCriterion(valueType, value));
	}
}
