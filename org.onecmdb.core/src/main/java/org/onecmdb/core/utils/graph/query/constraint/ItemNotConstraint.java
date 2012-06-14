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

public class ItemNotConstraint extends ItemConstraint {

	private ItemConstraint constraint;
	
	public AttributeValueConstraint getAttributeValueSelector() {
		if (constraint instanceof AttributeValueConstraint) {
			return((AttributeValueConstraint)constraint);
		}
		return(null);
	}
	
	public void setAttributeValueSelector(AttributeValueConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public RFCTargetConstraint getRFCTargetConstraint() {
		if (constraint instanceof RFCTargetConstraint) {
			return((RFCTargetConstraint)constraint);
		}
		return(null);
	}
	
	public void setRFCTargetConstraint(RFCTargetConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemIdConstraint getItemIdConstraint() {
		if (constraint instanceof ItemIdConstraint) {
			return((ItemIdConstraint)constraint);
		}
		return(null);
	}
	
	public void setItemIdConstraint(ItemIdConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemAndGroupConstraint getItemAndGroupConstraint() {
		if (constraint instanceof ItemAndGroupConstraint) {
			return((ItemAndGroupConstraint)constraint);
		}
		return(null);
	}
	public void setItemAndGroupConstraint(ItemAndGroupConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemOrGroupConstraint getItemOrGroupConstraint() {
		if (constraint instanceof ItemOrGroupConstraint) {
			return((ItemOrGroupConstraint)constraint);
		}
		return(null);
	}
	public void setItemOrGroupConstraint(ItemOrGroupConstraint constraint) {
		applyConstraint(constraint);
	}
	
	public ItemSecurityConstraint getItemSecurityConstraint() {
		if (constraint instanceof ItemSecurityConstraint) {
			return((ItemSecurityConstraint)constraint);
		}
		return(null);
		
	}
	
	public void setItemSecurityConstraint(ItemSecurityConstraint constraint) {
		applyConstraint(constraint);
	}
	
	
	public void applyConstraint(ItemConstraint constraint) {
		this.constraint = constraint;
	}
	
	public ItemConstraint fetchConstraint() {
		return(this.constraint);
	}


}
