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
package org.onecmdb.ui.gwt.desktop.client.window.misc;

import java.util.HashMap;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

/**
 * Generate an applet html tag. 
 * <br>
 * Params:
 * <li>code - Applet class name/li>
 * <li>archive - Applet jars/li>
 * <li>param - Model object containing params sent to the applet.</li>
 * <br>
 * Note: Will always append a authentication token param called token.
 * 
 * @author niklas
 *
 */
public class CMDBAppletWidget extends CMDBAbstractWidget {

	public static final String ID = "cmdb-applet-widget";
	private static String rootURL = null;
	private HashMap<String, String> property;

	public CMDBAppletWidget(CMDBDesktopWindowItem item) {
		super(item);
		setLayout(new FitLayout());
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		HTML html = new HTML();
		// Hosted mode don't support applet loading....
		if (GWT.isScript()) {
			html.setHTML(getAppletTag());
		} else {
			html.setText(getAppletTag());
		}
		add(html);
		
		layout();
	}
	
	protected String getAppletTag() {
		StringBuffer buf = new StringBuffer();
		
		String code = item.getParams().get("code");
		String archive = item.getParams().get("archive");
		BaseModel param = item.getParams().get("param");	
		
		buf.append("<applet archive=\"" + archive + "\"");
		buf.append(" code=\"" + code + "\" width=\"100%\" height=\"100%\">");
		if (param != null) {
			param.setAllowNestedValues(false);
			for (String name : param.getPropertyNames()) {
				String value = replace((String)param.get(name));
				buf.append("<param name=\"" + name + "\" value=\"" + value + "\">");
			}
		}
		buf.append("<param name=\"token\" value=\"" + CMDBSession.get().getToken() + "\">");
		buf.append(" <hr>");
		buf.append("If you were using a Java-enabled browser such as HotJava,");
		buf.append("you would see dancing text instead of this paragraph.");
		buf.append("<hr>");
		buf.append("</applet>");
		
		return(buf.toString());

	}
	
	public void setProperty(HashMap<String, String> prop) {
		this.property = prop;
	}

	
	private String replace(String value) {
	
		
		if (value == null) {
			return("");
		}
		String vTemp = value;
		
		value = value.replace("${baseURL}", GWT.getModuleBaseURL());
		value = value.replace("${rootURL}", getRootURL(GWT.getModuleBaseURL()));
		
		// handle properties.
		if (this.property != null) {
			for (String key : this.property.keySet()) {
				String keyValue = this.property.get(key);
				value = value.replace("${" + key + "}", keyValue);
			}
		}
		return(value); 
	}

	private String getRootURL(String moduleBaseURL) {
		if (rootURL != null) {
			return(rootURL);
		}
		int index = 0;
		String str = moduleBaseURL;
		for (int i = 0; i < 2; i++) {
			int idx = str.indexOf("/");
			if (idx >= 0) {
				index += idx+1;
				str = str.substring(index+1);
			} else {
				index = -1;
				break;
			}
		}
		if (index < 0) {
			index = moduleBaseURL.indexOf(GWT.getModuleName());
		}
		if (index > 0) {
			moduleBaseURL = moduleBaseURL.substring(0, index);
		}
		rootURL  = moduleBaseURL;
		return(moduleBaseURL);
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("Applet Widget");
		desc.setDescription("Add a applet tag to the window. The should then load the applet sepcified by parameters");
		desc.addParameter("<li>code - the applet class name<li>");
		desc.addParameter("<li>archive - the applet jars<li>");
		desc.addParameter("<li>param - the applet's params<li>");
		return(desc);
	}

	

}
