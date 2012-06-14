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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class CMDBSession extends BaseModel {
	
	private static CMDBSession session;
	private static String defaultDateTimeFmt = "yyyy-MM-dd HH:mm:ss";
	private static String defaultDateFmt = "yyyy-MM-dd";
	private String dateTimeFmt;
	private String dateFmt;
	private transient DateTimeFormat dateTimeFormat;
	private transient DateTimeFormat dateFormat;
	
	// Need to include this for the RPC serierilizer to accept it...
	private CMDBDesktopConfig desktopConfig;
	private Config config;
	private transient Desktop desktop;
	private List<String> roles;
	private String defaultRole;
	private Map<String, List<String>> urlValues;
	
	public static void setSession(CMDBSession s) {
		session = s;
	}
	
	public static CMDBSession get() {
		if (session == null) {
			CMDBSession session = new CMDBSession();
			session.setDateTimeFmt(defaultDateTimeFmt);
			session.setSession(session);
		}
		return(session);
	}
	
	public ContentData getDefaultCMDB_MDR() {
		ContentFile mdr = new ContentFile();
		String mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		mdr.setPath(mdrConf);
		return(mdr);
	}
	
	public String getToken() {
		return(get("token", "token"));
	}
	
	public void setToken(String token) {
		set("token", token);
	}
	
	public void setUsername(String username) {
		set("username", username);
	}
	
	public String getUsername() {
		return(get("username"));
	}
	
	public String getContentRepositoryURL() {
		return(GWT.getModuleBaseURL() + "/onecmdb/content");
	}
	public String getExportURL() {
		return(GWT.getModuleBaseURL() + "/onecmdb/export");
	}
	
	public CMDBDesktopConfig getDesktopConfig() {
		return(get("desktopConfig"));
	}
	
	public void setDesktopConfig(CMDBDesktopConfig config) {
		set("desktopConfig", config);
	}
	
	
	public String getDateTimeFmt() {
		return dateTimeFmt;
	}

	public void setDateTimeFmt(String dateTimeFmt) {
		this.dateTimeFmt = dateTimeFmt;
	}
	
	public void setDateFmt(String dateFmt) {
		this.dateFmt = dateFmt;
	}
	
	public String getDateFmt() {
		return(this.dateFmt);
	}
	public DateTimeFormat getDateFormat() {
		if (dateFormat == null) {
			String fmt = defaultDateFmt;
			if (getDateFmt() != null) {
				fmt = getDateFmt();
			}
			dateFormat = DateTimeFormat.getFormat(fmt);
		}
		return(dateFormat);
	}

	public DateTimeFormat getDateTimeFormat() {
		if (dateTimeFormat == null) {
			String fmt = defaultDateTimeFmt;
			if (getDateTimeFmt() != null) {
				fmt = getDateTimeFmt();
			}
			dateTimeFormat = DateTimeFormat.getFormat(fmt);
		}
		return(dateTimeFormat);
	}

	public void setConfig(Config cfg) {
		set("config", cfg);
	}
	public Config getConfig() {
		return(get("config", new Config()));
	}

	public void setDesktop(Desktop desktop) {
		this.desktop = desktop;
	}
	
	public Desktop getDesktop() {
		return(this.desktop);
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getDefaultRole() {
		return defaultRole;
	}

	public Map<String, List<String>> getURLValues() {
		return(urlValues);
	}
	
	public void setURLValues(Map<String, List<String>> map) {
		this.urlValues = map;
	}

	public boolean showRegistration() {
		return((Boolean)get("registration", false));
	}
	
	public void setRegistration(boolean v) {
		set("registration",v);
	}

	public String getInstallId() {
		return(get("installid"));
	}
	
	public void setInstallId(String id) {
		set("installid", id);
	}

	public void setUserPreference(UserPreference pref) {
		set("userPreferences", pref);
	}
	
	public UserPreference getUserPreference() {
		return(get("userPreferences"));
	}
	
	
	
	
}
