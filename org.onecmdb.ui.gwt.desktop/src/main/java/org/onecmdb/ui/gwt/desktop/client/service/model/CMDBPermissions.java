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
package org.onecmdb.ui.gwt.desktop.client.service.model;

import com.extjs.gxt.ui.client.data.BaseModel;

public class CMDBPermissions {
	private BaseModel defaultModel;
	private BaseModel localModel;
	private PermissionState currentState;
	
	public enum PermissionState {
		READONLY,
		EDIT,
		DELETE,
		CLASSIFY
	}
	
	public CMDBPermissions() {
	}
	
	public CMDBPermissions(PermissionState currentState) {
		setCurrentState(currentState);
	}

	public void setDefaultPermission(BaseModel perm) {
		this.defaultModel = perm;
	}
	
	public void setLocalPermission(BaseModel perm) {
		this.localModel = perm;
	}

	public String getDefault() {
		return(getString("default", "readonly"));
	}

	public PermissionState getDefaultState() {
		String defaultString = getDefault();		
		if (defaultString.equals("readonly")) {
			return(PermissionState.READONLY);
		} 
		if (defaultString.equals("deletable")) {
			return(PermissionState.DELETE);
		} 
		if (defaultString.equals("editable")) {
			return(PermissionState.EDIT);
		} 
		if (defaultString.equals("classify")) {
			return(PermissionState.CLASSIFY);
		} 
		
		return(PermissionState.READONLY);
	}
	private String getString(String property, String def) {
		if (localModel != null) {
			String val = localModel.get(property);
			if (val != null) {
				return(val);
			}
		}
		if (defaultModel != null) {
			String val = defaultModel.get(property);
			if (val != null) {
				return(val);
			}
		}
		return(def);
	}
	private boolean getBoolean(String property, boolean def) {
		if (localModel != null) {
			Object val = localModel.get(property);
			if (val != null) {
				if (val instanceof Boolean) {
					return((Boolean)val);
				}
				return("true".equals(val.toString()));
			}
		}
		if (defaultModel != null) {
			Object val = defaultModel.get(property);
			if (val != null) {
				if (val instanceof Boolean) {
					return((Boolean)val);
				}
				return("true".equals(val.toString()));
			}
		}
		return(def);
	}

	public boolean isReadonly() {
		return(getBoolean("readonly", true));
	}

	public boolean isEditable() {
		return(getBoolean("editable", true));
	}

	public boolean isDeletable() {
		return(getBoolean("deletable", true));
	}

	public boolean isClassify() {
		return(getBoolean("classify", true));
	}
	
	public PermissionState getCurrentState() {
		return(currentState);
	}

	public void setCurrentState(PermissionState currentState) {
		this.currentState = currentState;
	}

	public CMDBPermissions copy() {
		CMDBPermissions copy = new CMDBPermissions();
		copy.setDefaultPermission(defaultModel);
		copy.setLocalPermission(localModel);
		copy.setCurrentState(getCurrentState());
	
		return(copy);
	}
	
	
}
