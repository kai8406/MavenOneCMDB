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

import java.util.ArrayList;
import java.util.List;


public abstract class ItemGroupConstraint extends ItemConstraint {
	private List<ItemConstraint> constraints = new ArrayList<ItemConstraint>();

	public abstract boolean conjunction();

	public List<ItemConstraint> fetchConstraints() {
		return(constraints);
	}
	
	public void applyConstraints(List<ItemConstraint> constraints) {
		this.constraints = constraints;
	}
	
	public void add(ItemConstraint con) {
		constraints.add(con);
	}

	
	/**
	 * Need to add these so WSDL will work with inheritance.
	 * XFire is said to support inheritance but 
	 * I can't get it to work!
	 *
	 * @return
	 */
	public List<ItemIdConstraint> getItemIdConstraints() {
		List<ItemIdConstraint> idCon = new ArrayList<ItemIdConstraint>();
		for (ItemConstraint con : this.constraints) {
			if (con instanceof ItemIdConstraint) {
				idCon.add((ItemIdConstraint)con);
			}
		}
		return(idCon);
	}
	
	public void setItemIdConstraints(List<ItemIdConstraint> constraints) {
		for (ItemIdConstraint c : constraints) {
			add(c);
		}
	}

	
	public List<AttributeValueConstraint> getAttributeValueConstraints() {
		List<AttributeValueConstraint> cons = new ArrayList<AttributeValueConstraint>();
		for (ItemConstraint con : this.constraints) {
			if (con instanceof AttributeValueConstraint) {
				cons.add((AttributeValueConstraint)con);
			}
		}
		return(cons);
	}
	
	public void setAttributeValueConstraints(List<AttributeValueConstraint> constraints) {
		for (AttributeValueConstraint c : constraints) {
			add(c);
		}
	}

	public List<AttributeSourceRelationConstraint> getAttributeSourceRelationConstraints() {
		List<AttributeSourceRelationConstraint> cons = new ArrayList<AttributeSourceRelationConstraint>();
		for (ItemConstraint con : this.constraints) {
			if (con instanceof AttributeSourceRelationConstraint) {
				cons.add((AttributeSourceRelationConstraint)con);
			}
		}
		return(cons);
	}
	
	public void setAttributeSourceRelationConstraints(List<AttributeSourceRelationConstraint> constraints) {
		for (AttributeSourceRelationConstraint c : constraints) {
			add(c);
		}
	}

	
	public List<ItemSecurityConstraint> getItemSecurityConstraints() {
		List<ItemSecurityConstraint> cons = new ArrayList<ItemSecurityConstraint>();
		for (ItemConstraint con : this.constraints) {
			if (con instanceof ItemSecurityConstraint) {
				cons.add((ItemSecurityConstraint)con);
			}
		}
		return(cons);
	}
	
	public void setItemNotConstraints(List<ItemNotConstraint> constraints) {
		for (ItemNotConstraint c : constraints) {
			add(c);
		}
	}

	
	public List<ItemNotConstraint> getItemNotConstraints() {
		List<ItemNotConstraint> cons = new ArrayList<ItemNotConstraint>();
		for (ItemConstraint con : this.constraints) {
			if (con instanceof ItemNotConstraint) {
				cons.add((ItemNotConstraint)con);
			}
		}
		return(cons);
	}
	public void setItemSecurityConstraints(List<ItemSecurityConstraint> constraints) {
		for (ItemSecurityConstraint c : constraints) {
			add(c);
		}
	}

	public List<RelationConstraint> getRelationConstraints() {
		List<RelationConstraint> cons = new ArrayList<RelationConstraint>();
		for (ItemConstraint con : this.constraints) {
			if (con instanceof RelationConstraint) {
				cons.add((RelationConstraint)con);
			}
		}
		return(cons);
	}
	
	public void setRelationConstraints(List<RelationConstraint> constraints) {
		for (RelationConstraint c : constraints) {
			add(c);
		}
	}
	
	public List<RFCTargetConstraint> getRFCTargetConstraints() {
		List<RFCTargetConstraint> cons = new ArrayList<RFCTargetConstraint>();
		for (ItemConstraint con : this.constraints) {
			if (con instanceof RFCTargetConstraint) {
				cons.add((RFCTargetConstraint)con);
			}
		}
		return(cons);
	}
	
	public void setRFCTargetConstraints(List<RFCTargetConstraint> constraints) {
		for (RFCTargetConstraint c : constraints) {
			add(c);
		}
	}
	


}
