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
package org.onecmdb.ui.gwt.desktop.client.mvc;

import com.extjs.gxt.ui.client.Events;


/**
 * Stores all events for CMDB Desktop application.
 *
 */
public class CMDBEvents {

	public static final int DESKTOP_LOGIN = 1;
	public static final int DESKTOP_LOGOUT = 2;
	public static final int DESKTOP_LOGGED_IN = 3;
	public static final int DESKTOP_ABOUT = 4;
	public static final int DESKTOP_CHANGE_ROLE = 5;
	public static final int DESKTOP_CHECK_SESSION = 6;
	public static final int DESKTOP_LOCK_TIMEOUT = 7;

	public static final int DESKTOP_MENU_SELECTED = 100;
	
	public static final int COMMIT_EVENT = Events.GXT_MAX_EVENT + 1;
	public static final int POPUP_HIDE_EVENT = Events.GXT_MAX_EVENT + 2;

	public static final int PERMISSION_CHANGED = Events.GXT_MAX_EVENT + 3;
	public static final int MDR_GRID_AVAILIABLE = Events.GXT_MAX_EVENT + 4;
}
