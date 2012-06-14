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

import org.onecmdb.ui.gwt.desktop.client.action.CloseTextToolItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.utils.ExpressionHandler;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;

public class CMDBURLFrameWidget extends CMDBAbstractWidget {
	public static final String ID = "cmdb-url-frame-widget";

	public CMDBURLFrameWidget(CMDBDesktopWindowItem item) {
		super(item);
		setLayout(new FitLayout());
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		// Handle parameters...
		BaseModel param = item.getParams();
		String url = (String)param.get("url");
		String replacedURL = ExpressionHandler.replace(url);
		String newWindow = param.get("newWindow");
		
		if ("true".equalsIgnoreCase(newWindow)) {
			String newWindowName = param.get("newWindowName");
			if (newWindowName == null) {
				newWindowName = "OneCMDB_Window";
			}
			String newWindowFeatures = param.get("newWindowFeatures");
			if (newWindowFeatures == null) {
				newWindowFeatures = "";
			}
			newWindowName = newWindowName.replace(" ", "_");
			com.google.gwt.user.client.Window.open(replacedURL, newWindowName, "");
			if (getParent() instanceof Window) {
				((Window)getParent()).close();
			}
			return;
		}
		
		Frame f = new Frame();
		f.setUrl(replacedURL);
		f.setSize("100%", "100%");
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());
		cp.add(f);
		ToolBar bottom = new ToolBar();
		bottom.add(new FillToolItem());
		bottom.add(new CloseTextToolItem(this));
		cp.setBottomComponent(bottom);
		add(cp);
		layout();
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("Frame URL Widget");
		desc.setDescription("Add a url to a frame and show in window.");
		desc.addParameter("<li>url - the url to display<li>");
		desc.addParameter("<li>newWindow - true/false Open a new browser window</li>");
		desc.addParameter("<li>newWindowName - optional. Name of new browser window</li>");
		desc.addParameter("<li>newWindowFeatures - optional. Features of new browser window</li>");
		return(desc);
	}
	
	
	
	

	
}
