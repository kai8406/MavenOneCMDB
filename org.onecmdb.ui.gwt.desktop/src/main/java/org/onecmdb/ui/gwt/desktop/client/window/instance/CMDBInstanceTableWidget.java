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
package org.onecmdb.ui.gwt.desktop.client.window.instance;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.widget.CIModelBrowser;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.Element;

public class CMDBInstanceTableWidget extends CMDBAbstractWidget {

	public static final String ID = "cmdb-model-table-view";

	public CMDBInstanceTableWidget(CMDBDesktopWindowItem item) {
		super(item);
		setLayout(new FillLayout());

	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		BaseModel param = item.getParams();
		
		ContentFile file = new ContentFile();
		String mdrConf = param.get("mdrConfig");
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		file.setPath(mdrConf);
		List<String> roots = null;
		Object o = param.get("rootCI");
		if (o instanceof List) {
			roots = param.get("rootCI");
		}
		String readonlyStr = param.get("readonly");
		boolean readonly = false;
		if ("true".equalsIgnoreCase(readonlyStr)) {
			readonly = true;
		}
		CIModelBrowser browser = new CIModelBrowser(file, roots);
		//browser.setReadonly(readonly);
		browser.setPermissions(permissions);
		add(browser);
		layout();
	}

	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB Table Instance View");
		desc.setDescription("A Widget that displays CI's as tables.");
		desc.addParameter("<li>permission - permission for the model editor<li>");
		desc.addParameter("<li>mdrConf - path to configuration for the MDR. Default is to OneCMDB.<li>");
		desc.addParameter("<li>rootCI - [List] Root CI's to display.<li>");
		
		return(desc);	
	}

}
