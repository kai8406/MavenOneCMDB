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

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyPanel;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class CMDBCIPropertyWidget extends CMDBAbstractWidget {
	public static final String ID = "cmdb-ci-property";
	protected CIModel model;
	private ContentFile mdr;
	//private boolean readonly;
	private String rootCI;
	
	public CMDBCIPropertyWidget(CMDBDesktopWindowItem item) {
		super(item);
	}



	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setLayout(new FitLayout());
		
		mdr = new ContentFile();
		String mdrConf = item.getParams().get("mdrConfig");
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		
		String alias = item.getParams().get("alias");
		/*
		readonly = true;
		if ("false".equals(item.getParams().get("readonly"))) {
			if (permissions.isEditable()) {
				readonly = false;	
			}
		}
		*/
		rootCI = item.getParams().get("rootCI");
		if (rootCI == null || rootCI.length() == 0) {
			rootCI = "Ci";
		}
		
		List<String> list = new ArrayList<String>();
		list.add(alias);
		ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), mdr, list, new CMDBAsyncCallback<List<CIModel>>() {

	
			@Override
			public void onSuccess(List<CIModel> arg0) {
				if (arg0.size() == 1) {
					model = arg0.get(0);
					initUI();
				}
			}
			
		});
		
	}


	protected void initUI() {
		CIModelCollection col = new CIModelCollection();
		col.addCIModel("offspring", model);
		
		CIPropertyPanel panel = new CIPropertyPanel(mdr, col, rootCI);
		panel.setPermissions(permissions);
		add(panel);
		
		layout();
	}


	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB CI Property");
		desc.setDescription("A Widget that views property for one or more CI's");
		desc.addParameter("<li>readonly - if this table is readonly<li>");
		desc.addParameter("<li>mdrConf - path to configuration for the MDR. Default is to OneCMDB.<li>");
		desc.addParameter("<li>alias - Alias of template/instance to show properties for<li>");
		desc.addParameter("<li>rootCI - Alias of root template from where references will be searched for. Default is Ci.<li>");	
		return(desc);	
	}
	

}
