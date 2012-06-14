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
package org.onecmdb.ui.gwt.desktop.client.window;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.window.composite.CompositeEditorWindow;
import org.onecmdb.ui.gwt.desktop.client.window.content.CMDBContentBrowserWidget;
import org.onecmdb.ui.gwt.desktop.client.window.customview.CustomViewWindow;
import org.onecmdb.ui.gwt.desktop.client.window.group.CIGroupDesignWindow;
import org.onecmdb.ui.gwt.desktop.client.window.group.CIGroupWindow;
import org.onecmdb.ui.gwt.desktop.client.window.instance.CMDBCIPropertyWidget;
import org.onecmdb.ui.gwt.desktop.client.window.instance.CMDBInstanceOverviewWidget;
import org.onecmdb.ui.gwt.desktop.client.window.instance.CMDBInstanceTableWidget;
import org.onecmdb.ui.gwt.desktop.client.window.instance.CMDBInstanceTreeView;
import org.onecmdb.ui.gwt.desktop.client.window.mdr.MDRViewWindow;
import org.onecmdb.ui.gwt.desktop.client.window.misc.CMDBAppletWidget;
import org.onecmdb.ui.gwt.desktop.client.window.misc.CMDBURLFrameWidget;
import org.onecmdb.ui.gwt.desktop.client.window.model.CMDBImportModelWindow;
import org.onecmdb.ui.gwt.desktop.client.window.model.CMDBModelClearWindow;
import org.onecmdb.ui.gwt.desktop.client.window.model.CMDBModelDesignerView;
import org.onecmdb.ui.gwt.desktop.client.window.model.CMDBModelSaveWindow;
import org.onecmdb.ui.gwt.desktop.client.window.test.TestWindow;

import com.google.gwt.user.client.ui.Widget;

public class CMDBWidgetFactory implements IWidgetFactory {
	List<WidgetDescription> descriptions = new ArrayList<WidgetDescription>();
	
	public CMDBWidgetFactory() {
		CMDBAbstractWidget w = new CMDBContentBrowserWidget(null);
		descriptions.add(w.getDescription());
		
		w = new CMDBAppletWidget(null);
		descriptions.add(w.getDescription());
	
		w = new CMDBURLFrameWidget(null);
		descriptions.add(w.getDescription());
		
		w = new CMDBInstanceTableWidget(null);
		descriptions.add(w.getDescription());
	
		w = new CMDBInstanceTreeView(null);
		descriptions.add(w.getDescription());
		
		w = new CMDBModelDesignerView(null);
		descriptions.add(w.getDescription());
		
		w = new CMDBImportModelWindow(null);
		descriptions.add(w.getDescription());
	
		w = new CMDBModelSaveWindow(null);
		descriptions.add(w.getDescription());
		
		w = new CMDBModelClearWindow(null);
		descriptions.add(w.getDescription());
		
		w = new MDRViewWindow(null);
		descriptions.add(w.getDescription());
		
		w = new CMDBInstanceOverviewWidget(null);
		descriptions.add(w.getDescription());
		
		w = new CustomViewWindow(null);
		descriptions.add(w.getDescription());
		
		w = new CMDBCIPropertyWidget(null);
		descriptions.add(w.getDescription());
		
		w = new CIGroupWindow(null);
		descriptions.add(w.getDescription());
		
		w = new CIGroupDesignWindow(null);
		descriptions.add(w.getDescription());
		
		w = new TestWindow(null);
		descriptions.add(w.getDescription());
		
		w = new CompositeEditorWindow(null);
		descriptions.add(w.getDescription());
	}
	
	
	public Widget createWidget(CMDBDesktopWindowItem item) {
		// Need to do if here, since GWT don't support GWT.create(Class)
		if (item.getID().equals(CMDBContentBrowserWidget.ID)) {
			return(new CMDBContentBrowserWidget(item));
		}
		
		if (item.getID().equals(CMDBModelDesignerView.ID)) {
			return(new CMDBModelDesignerView(item));
		}
		
		if (item.getID().equals(CMDBAppletWidget.ID)) {
			return(new CMDBAppletWidget(item));
		}
		
		if (item.getID().equals(CMDBURLFrameWidget.ID)) {
			return(new CMDBURLFrameWidget(item));
		}
		
		if (item.getID().equals(CMDBInstanceTableWidget.ID)) {
			return(new CMDBInstanceTableWidget(item));
		}
		
		if (item.getID().equals(CMDBInstanceTreeView.ID)) {
			return(new CMDBInstanceTreeView(item));
		}
	
		if (item.getID().equals(CMDBImportModelWindow.ID)) {
			return(new CMDBImportModelWindow(item));
		}
		
		if (item.getID().equals(CMDBModelSaveWindow.ID)) {
			return(new CMDBModelSaveWindow(item));
		}
		if (item.getID().equals(CMDBModelClearWindow.ID)) {
			return(new CMDBModelClearWindow(item));
		}
		
		if (item.getID().equals(MDRViewWindow.ID)) {
			return(new MDRViewWindow(item));
		}
		if (item.getID().equals(CMDBInstanceOverviewWidget.ID)) {
			return(new CMDBInstanceOverviewWidget(item));
		}
	
		if (item.getID().equals(CustomViewWindow.ID)) {
			return(new CustomViewWindow(item));
		}
		if (item.getID().equals(CMDBCIPropertyWidget.ID)) {
			return(new CMDBCIPropertyWidget(item));
		}
		
		if (item.getID().equals(CIGroupWindow.ID)) {
			return(new CIGroupWindow(item));
		}
		
		if (item.getID().equals(CIGroupDesignWindow.ID)) {
			return(new CIGroupDesignWindow(item));
		}
		
		if (item.getID().equals(TestWindow.ID)) {
			return(new TestWindow(item));
		}
		
		if (item.getID().equals(CompositeEditorWindow.ID)) {
			return(new CompositeEditorWindow(item));
		}
		
		return(null);
	}

	public List<WidgetDescription> getWidgetDescriptions() {
		return(descriptions);
	}


	public String getName() {
		return("OneCMDB");
	}

}
