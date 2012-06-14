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
package org.onecmdb.ui.gwt.desktop.client.service.content;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.BaseModel;

public class Config extends BaseModel {

	public static String ChangeStoreRootPath = "ChangeStoreRootPath";

	// Path to the IconMapper file.
	public static String IconMapperPath = "IconMapperPath";

	// Path to OneCMDB configuration.
	public static String OneCMDBWebService = "OneCMDBWebService";

	// Path to default desktop config.
	public static String DesktopDefaultConfig = "DesktopDefaultConfig";
	public static String DesktopUserConfigPath = "DesktopUserConfigPath";
	public static String DesktopRoleConfigPath = "DesktopRoleConfigPath";
	
	public static String OneCMDBRootReference = "OneCMDBRootReference";
	public static String OneCMDBRootCI = "OneCMDBRootCI";
	
	public static String MDR_HOME = "MDR_HOME";
	public static final String MDR_HISTORY_ITEM = "MDR_HISTORY_ITEM";

	public static final String AUTO_LOGIN_DEF = "AutoLoginConfig";

	private static final String DesktopLockTimeout = "DesktopLockTimeout";

	private static final String GridToolTipClick = "GridToolTipClick";
	
	public static String DEFAULT_WINDOW_WIDTH = "defaultWindowWidth";
	public static String DEFAULT_WINDOW_HEIGHT = "defaultWindowHeight";
	
	public static String REPORT_HOME = "REPORT_HOME";
	public static String DEFAULT_PAGE_SIZE = "defaultPageSize";
	
	public static String SHELL_LIST = "ShellList";
	
	public static String DECORATE_TEMPLATE_COUNT = "DecorateTemplateCount";

	public static String RESTRICTED_TEMPLATES = "RestrictedTemplates";

	public static String DEFAULT_DATE_TIME_FMT = "DefaultDateTimeFmt";
	public static String DEFAULT_DATE_FMT = "DefaultDateFmt";
	public static String RequireLoginForReport = "RequireLoginForReport";
	public static String RequireRoleToLogin = "RequireRoleToLogin";
	
	public static String UseTableComboBox = "UseTableComboBox";
	public static String UseTreeComboBox = "UseTreeComboBox";
	
	
	public String getDateTimeFmt() {
		String fmt = get(DEFAULT_DATE_TIME_FMT);
		if (fmt == null) {
			fmt = "yyyy-MM-dd HH:mm:ss";
		}
		return(fmt);
	}
	
	public String getDateFmt() {
		String fmt = get(DEFAULT_DATE_FMT);
		if (fmt == null) {
			fmt = "yyyy-MM-dd";
		}
		return(fmt);
	}
	
	public boolean allowEditTemplate(String alias) {
		String restricted = get(RESTRICTED_TEMPLATES);
		if (restricted == null) {
			return(true);
		}
		String split[] = restricted.split(",");
		for (int i = 0; i < split.length; i++) {
			if (alias.equalsIgnoreCase(split[i])) {
				return(false);
			}
		}
		return(true);
	}

	public boolean useTableComboBox() {
		String use = get(UseTableComboBox);
		if (use == null) {
			return(false);
		}
		return ("true".equalsIgnoreCase(use));
	}
	
	public boolean useTreeComboBox() {
		String use = get(UseTreeComboBox);
		if (use == null) {
			return(false);
		}
		return ("true".equalsIgnoreCase(use));
	}

	/**
	 * Get the desktop locktime out in miliseconds.
	 * If time is < 0 no need to lock desktop.
	 * @return
	 */
	public int getDesktopLockTimeout() {
		String timeout = get(DesktopLockTimeout);
		if (timeout == null) {
			return(-1);
		}
		int timeInMin = -1;
		try {
			timeInMin = Integer.parseInt(timeout);
		} catch (Throwable t) {
			timeInMin = -1;
		}
		// Convert to ms....
		return(timeInMin * 60 * 1000);
	}

	public int getGridToolClick() {
		String clicks = get(GridToolTipClick);
		if ("single".equalsIgnoreCase(clicks)) {
			return(Events.CellClick);
		}
		return(Events.CellDoubleClick);
	}
	

}
