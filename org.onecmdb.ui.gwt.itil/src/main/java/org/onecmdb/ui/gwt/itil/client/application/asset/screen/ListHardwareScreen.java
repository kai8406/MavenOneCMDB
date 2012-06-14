/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.ui.gwt.itil.client.application.asset.screen;

import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.table.CIInheritanceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.control.table.ITableControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ListCIScreen;

public class ListHardwareScreen extends ListCIScreen {
	
	private String[] orders = new String[] {
			"A_Name",
			"A_Type",
			"L_IP_Address",
			"L_MAC_Address",
			"M_Hostname"
	};


	
	public ListHardwareScreen(ITableControl ctrl) {
		super();
		this.ctrl = ctrl;
		if (ctrl instanceof CIInheritanceTableControl) {
		
			DefaultAttributeFilter aFilter = new DefaultAttributeFilter();
			aFilter.setSimpleAttributeControl(orders);
		
			((CIInheritanceTableControl)this.ctrl).setAttributeFilter(aFilter);
			//((CIInheritanceTableControl)this.ctrl).setOnSelectScreenIndex(ITILApplication.EDIT_INCDIENT_SCREEN);
		}
		
	}
	
	public ListHardwareScreen() {
		super();
		this.setTitleText("List Hardware Asset(s)");
		this.setLoadingText("Loading....");
	}

	
	public void load() {
		super.load("Hardware", null);
	}	
	
	
	public ITableControl getTableControl(GWT_CiBean bean) {
		if (this.ctrl == null) {
			CIInheritanceTableControl control = new CIInheritanceTableControl();
			DefaultAttributeFilter aFilter = new DefaultAttributeFilter();
			aFilter.setSimpleAttributeControl(orders);
			control.setAttributeFilter(aFilter);
			//control.setOnSelectScreenIndex(ITILApplication.EDIT_INCDIENT_SCREEN);
			this.ctrl = control;
		}
		this.ctrl.setTemplate(bean);
		return(this.ctrl);
	}
	
}
