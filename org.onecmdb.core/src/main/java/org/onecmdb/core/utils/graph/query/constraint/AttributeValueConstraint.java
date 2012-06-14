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
package org.onecmdb.core.utils.graph.query.constraint;


public class AttributeValueConstraint extends AttributeConstraint {
	public static final int EQUALS = 0;
	public static final int GREATER_THAN = 1;
	public static final int LESS_THAN = 2;
	public static final int GREATER_THAN_OR_EQUAL = 3;
	public static final int LESS_THAN_OR_EQUAL = 4;
	public static final int LIKE = 5;
	public static final int CONTAINS = 6;
	public static final int IS_NULL = 7;
	
	
	public static final int STRING_DATA_TYPE = 0;
	public static final int NUMBER_DATA_TYPE = 1;
	public static final int DATE_DATA_TYPE = 2;
	public static final int TYPE_DATA_TYPE = 3;
	public static final int REFTYPE_DATA_TYPE = 4;
	public static final int DERIVED_DATA_TYPE = 5;
	
	public static final int INTERNAL = 10;
	public static final int INTERNAL_ALIAS = 11;
	public static final int INTERNAL_DESCRIPTION = 12;
	public static final int INTERNAL_LASTMODFIED = 13;
	public static final int INTERNAL_CREATED = 14;
	
	
	private String value;
	private int operation;
	private int valueType;
	
	public AttributeValueConstraint() {
		super();
	}
	
	public static String getOperation(int id) {
		switch(id) {
		case EQUALS:
			return("EQUALS");
		case GREATER_THAN:
			return("GT");
		case LESS_THAN:
			return("LT");
		case GREATER_THAN_OR_EQUAL:
			return("GE");
		case LESS_THAN_OR_EQUAL:
			return("LE");
		case LIKE:
			return("LIKE");
		case CONTAINS:
			return("CONTAINS");
		case IS_NULL:
			return("IS_NULL");
		
		}
		return(null);
	}
	public static int getOperation(String name) {
		
		if (name.equalsIgnoreCase("EQ") || 
				name.equalsIgnoreCase("EQUALS")) {
			return(EQUALS);
		}
		if (name.equalsIgnoreCase("GREATER_THAN") || 
				name.equalsIgnoreCase("GT")) {  
			return(GREATER_THAN);
		}
		if (name.equalsIgnoreCase("LESS_THAN") || 
				name.equalsIgnoreCase("LT")) {
			return(LESS_THAN);
		}
		if (name.equalsIgnoreCase("GREATER_THAN_OR_EQUAL") ||  
				name.equalsIgnoreCase("GE")) { 
			return(GREATER_THAN_OR_EQUAL);
		}
		if (name.equalsIgnoreCase("LESS_THAN_OR_EQUAL") || 
				name.equalsIgnoreCase("LE")) { 
			return(LESS_THAN_OR_EQUAL);
		}
		if (name.equalsIgnoreCase("LIKE")) {
			return(LIKE);
		}
		if (name.equalsIgnoreCase("CONTAINS")) { 
			return(CONTAINS);
		}
		if (name.equalsIgnoreCase("IS_NULL") || name.equalsIgnoreCase("isnull")) { 
			return(IS_NULL);
		}
		return(-1);
	}
	
	public AttributeValueConstraint(String attrAlias, int operation, String attrValue) {
		super(attrAlias);
		this.value = attrValue;
		this.operation = operation;
	}

	
	public int getValueType() {
		return valueType;
	}


	public void setValueType(int valueType) {
		this.valueType = valueType;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}
	
	
	
	
	
	
	
}
