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
package org.onecmdb.core.internal.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RBACSession {
	List<Role> roles = new ArrayList<Role>();
	HashMap<String, GroupPermission> groupMap = new HashMap<String, GroupPermission>();
	private boolean readonly = true;
	
	public RBACSession() {
	}
	
	
	public List<Role> getRoles() {
		return roles;
	}


	public void setRoles(List<Role> roles) {
		this.roles = roles;
		buildMap();
	}
	
	public Set<String> groupNames() {
		return(groupMap.keySet());
	}
	
	/**
	 * Create map for group --> permission for fast lookup
	 */
	private void buildMap() {
		groupMap.clear();
		for (Role r : roles) {
			for (GroupPermission p : r.getPermission()) {
				for (SecurityGroup g : p.getGroup()) {
					updateGroup(g, p);
				}
			}
		}
	}


	private void updateGroup(SecurityGroup g, GroupPermission p) {
		GroupPermission permission = groupMap.get(g.getName());
		if (permission == null) {
			permission = new GroupPermission();
			groupMap.put(g.getName(), permission);
		}
		permission.setRead(p.isRead());
		permission.setWrite(p.isWrite());
		permission.setCreate(p.isCreate());
		permission.setDelete(p.isDelete());
		if (p.isWrite()) {
			readonly  = false;
		}
		for (SecurityGroup child : g.getChildren()) {
			updateGroup(child, p);
		}
	}


	/**
	 * Check for readonly user.
	 * @return
	 */
	public boolean canWrite() {
		return(!readonly);
	}
	
	public boolean canWrite(String group) {
		GroupPermission p = groupMap.get(group);
		if (p == null) {
			return(false);
		}
		return(p.isRead());
	}	
	public boolean canRead(String group) {
		GroupPermission p = groupMap.get(group);
		if (p == null) {
			return(false);
		}
		return(p.isWrite());
	}
	public boolean canCreate(String group) {
		GroupPermission p = groupMap.get(group);
		if (p == null) {
			return(false);
		}
		return(p.isCreate());
	}
	public boolean canDelete(String group) {
		GroupPermission p = groupMap.get(group);
		if (p == null) {
			return(false);
		}
		return(p.isDelete());
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (Role role : roles) {
			b.append("\tRole:" + role.getName());
			b.append("\n");
		}
		for (String group : groupMap.keySet()) {
			b.append("\tGroup<" + group + "> - " + groupMap.get(group).toString());
			b.append("\n");
		}
		b.append("\tReadonly=" + !canWrite());
		return(b.toString());
	}
	
}
