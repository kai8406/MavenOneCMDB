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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWT_RBACSession implements IsSerializable {
	/**
	 * @gwt.typeArgs <java.lang.String>
	 */
	List roles = new ArrayList();
	
	/**
	 * @gwt.typeArgs <java.lang.String>
	 */
	HashSet readGroups = new HashSet();
	/**
	 * @gwt.typeArgs <java.lang.String>
	 */
	HashSet writeGroups = new HashSet();
	/**
	 * @gwt.typeArgs <java.lang.String>
	 */
	HashSet createGroups = new HashSet();
	/**
	 * @gwt.typeArgs <java.lang.String>
	 */
	HashSet deleteGroups = new HashSet();

	boolean write;
	
	
	public void setRoles(List roles) {
		this.roles = roles;
	}
	
	public void addRead(String group) {
		readGroups.add(group);
	}
	public void addWrite(String group) {
		writeGroups.add(group);
	}
	public void addCreate(String group) {
		createGroups.add(group);
	}
	public void addDelete(String group) {
		deleteGroups.add(group);
	}
	
	public boolean canWrite() {
		return(write);
	}
	public void setWrite(boolean value) {
		this.write = value;
	}
	
	public boolean canRead(String group) {
		return(readGroups.contains(group));
	}
	
	public boolean canWrite(String group) {
		return(writeGroups.contains(group));
	}
	public boolean canCreate(String group) {
		return(createGroups.contains(group));
	}
	
	public boolean canDelete(String group) {
		return(deleteGroups.contains(group));
	}

	public List getRoles() {
		return(roles);
	}
	
	
}
