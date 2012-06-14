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
package org.onecmdb.ui.gwt.desktop.client.utils;

import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.google.gwt.user.client.Element;

public class PermissionMenu extends TextToolItem {

	
	
	public static final int READONLY_SELECTED = 1;
	public static final int EDIT_SELECTED = 2;	
	public static final int DELETE_SELECTED = 3;
	public static final int CLASSIFY_SELECTED = 4;
	
	public static final int READONLY_MASK = 0x01;
	public static final int EDIT_MASK = 0x02;
	public static final int DELETE_MASK = 0x04;
	public static final int CLASSIFY_MASK = 0x08;
	public static final int ALL_MASK = 0xff;
	
	
	private CMDBPermissions permissions;
	private int enableMask;

	public PermissionMenu(CMDBPermissions perm, int enableMask) {
		this.permissions = perm;
		this.enableMask = enableMask;
	}
	
	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		init();
	}


	protected void init() {
		Menu menu = new Menu();
		String defaultText = "Read Only";
		String defaultIconStyle = "lock-icon";
		//permissions.setCurrentState(CMDBPermissions.PermissionState.READONLY);
		//readonly = true;
		if (permissions != null) {
			if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
				fireEvent(CMDBEvents.PERMISSION_CHANGED, new BaseEvent(READONLY_SELECTED));
			}
			if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT)) {
				defaultText = "Edit Allowed";
				defaultIconStyle = "unlock-icon";
				permissions.setCurrentState(CMDBPermissions.PermissionState.EDIT);
				fireEvent(CMDBEvents.PERMISSION_CHANGED, new BaseEvent(EDIT_SELECTED));
			}
			if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.DELETE)) {
				defaultText = "Delete Allowed";
				defaultIconStyle = "unlock-delete-icon";
				permissions.setCurrentState(CMDBPermissions.PermissionState.DELETE);
				fireEvent(CMDBEvents.PERMISSION_CHANGED, new BaseEvent(DELETE_SELECTED));
			}
			if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.CLASSIFY)) {
				defaultText = "Classify Allowed";
				defaultIconStyle = "classify-icon";
				permissions.setCurrentState(CMDBPermissions.PermissionState.CLASSIFY);
				fireEvent(CMDBEvents.PERMISSION_CHANGED, new BaseEvent(CLASSIFY_SELECTED));
			}
		}
		
		
		setText(defaultText);  
		setIconStyle(defaultIconStyle);  
		if ((enableMask & READONLY_MASK) != 0) {
			if (permissions == null || permissions.isReadonly()) {
				CheckMenuItem r = new CheckMenuItem("Read Only");
				r.addSelectionListener(new SelectionListener<ComponentEvent>() {

					@Override
					public void componentSelected(ComponentEvent ce) {
						setIconStyle("lock-icon");
						setText("Read Only");
						permissions.setCurrentState(CMDBPermissions.PermissionState.READONLY);

						fireEvent(CMDBEvents.PERMISSION_CHANGED, new BaseEvent(READONLY_SELECTED));
					}

				});
				r.setGroup("radios");  
				r.setChecked(true); 

				menu.add(r);  
			}
		}
		if ((enableMask & EDIT_MASK) != 0) {

			if (permissions == null || permissions.isEditable()) {
				CheckMenuItem r = new CheckMenuItem("Edit Allowed");  
				r.addSelectionListener(new SelectionListener<ComponentEvent>() {

					@Override
					public void componentSelected(ComponentEvent ce) {
						setText("Edit Allowed");
						setIconStyle("unlock-icon");
						permissions.setCurrentState(CMDBPermissions.PermissionState.EDIT);
						fireEvent(CMDBEvents.PERMISSION_CHANGED, new BaseEvent(EDIT_SELECTED));
					}

				});
				r.setGroup("radios");  
				menu.add(r);  
			}
		}
		if ((enableMask & DELETE_MASK) != 0) {

			if (permissions == null || permissions.isDeletable()) {
				CheckMenuItem r = new CheckMenuItem("Delete Allowed");  
				r.addSelectionListener(new SelectionListener<ComponentEvent>() {

					@Override
					public void componentSelected(ComponentEvent ce) {
						setText("Delete Allowed");
						setIconStyle("unlock-delete-icon");
						permissions.setCurrentState(CMDBPermissions.PermissionState.DELETE);
						fireEvent(CMDBEvents.PERMISSION_CHANGED, new BaseEvent(DELETE_SELECTED));
					}

				});
				r.setGroup("radios");  
				menu.add(r);  
			}
		}
		if ((enableMask & CLASSIFY_MASK) != 0) {

			if (permissions == null || permissions.isClassify()) {
				CheckMenuItem r = new CheckMenuItem("Classify Allowed");  
				r.addSelectionListener(new SelectionListener<ComponentEvent>() {

					@Override
					public void componentSelected(ComponentEvent ce) {
						setText("Classify Allowed");
						setIconStyle("classify-icon");
						permissions.setCurrentState(CMDBPermissions.PermissionState.CLASSIFY);
						fireEvent(CMDBEvents.PERMISSION_CHANGED, new BaseEvent(CLASSIFY_SELECTED));
					}

				});
				r.setGroup("radios");  
				menu.add(r);  
			}
		}
		setMenu(menu);
	}
	
}
