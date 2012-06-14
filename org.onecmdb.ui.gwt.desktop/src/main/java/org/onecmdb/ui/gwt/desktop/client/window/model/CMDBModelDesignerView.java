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
package org.onecmdb.ui.gwt.desktop.client.window.model;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.widget.CIModelInternalBrowser;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.Element;

public class CMDBModelDesignerView extends CMDBAbstractWidget {
	
	
	
	public static final String ID = "cmdb-model-designer";

	public CMDBModelDesignerView(CMDBDesktopWindowItem item) {
		super(item);
	}

	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setLayout(new FillLayout());
		
		ContentFile mdr = new ContentFile();
		String mdrConf = item.getParams().get("mdrConfig");
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		List<String> roots = null;
		Object o = item.getParams().get("rootCI"); 
		if (o instanceof List) {
			roots = item.getParams().get("rootCI");
		}
		String rootType = item.getParams().get("rootType", "Ci");
		String rootReferenceType = item.getParams().get("rootReferenceType", "CIReference");
		
		//permissions.setCurrentState(CMDBPermissions.PermissionState.EDIT);
		
		CIModelInternalBrowser i = new CIModelInternalBrowser(mdr, roots, rootType, rootReferenceType);
		i.setPermission(permissions);
		add(i);
		layout();
	}



	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB Designer");
		desc.setDescription("Model designer." + 
				"<br>Functions supported:<br>" +
				"<li>create/delete CI and references</li>" +
				"<li>add/delete/modify Attributes<li>" + 
				"<li>handle default values for templates</li>"
				);
		desc.addParameter("<li>permissions - permissions on this widget (readonly/editable/deletable/classify)<li>");
		desc.addParameter("<li>mdrConf - path to configuration for the MDR. Default is OneCMDB<li>");
		desc.addParameter("<li>rootCI - [List] Root CI's to display.<li>");
			
		return(desc);	
	}

}
