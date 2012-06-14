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
package org.onecmdb.ui.gwt.toolkit.client.model.onecmdb;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Relation implements IsSerializable, Serializable {

	private GWT_CiBean center;
	private GWT_CiBean referred;
	private String direction;
	private GWT_AttributeBean attribute;
	private GWT_CiBean relationType;
	
	public static String CENTER_SOURCE = "centerSource";
	public static String CENTER_TARGET = "centerTarget";
	
	public GWT_CiBean getCenter() {
		return center;
	}
	
	public void setCenter(GWT_CiBean center) {
		this.center = center;
	}
	public GWT_CiBean getReferred() {
		return referred;
	}
	public void setReferred(GWT_CiBean referred) {
		this.referred = referred;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public GWT_AttributeBean getAttribute() {
		return attribute;
	}
	public void setAttribute(GWT_AttributeBean attribute) {
		this.attribute = attribute;
	}
	public GWT_CiBean getRelationType() {
		return relationType;
	}
	public void setRelationType(GWT_CiBean relationType) {
		this.relationType = relationType;
	}

	
	
	
	
	
}
