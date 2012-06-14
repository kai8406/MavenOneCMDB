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
package org.onecmdb.ui.gwt.itil.client.application.problem.screen;

import org.onecmdb.ui.gwt.itil.client.ITILApplication;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.table.CIInheritanceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.control.table.CIReferenceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.control.table.ITableControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ListCIScreen;

public class ListProblemScreen extends ListCIScreen { 


	private static String[] orders = new String[] {
			"ID",
			"title", 
			"priority",
			"status",
			"reportDate",
			"ticketIssuer"
	};
	
	public static String[] getOrder() {
		return(orders);
	}
	 
	
	public ListProblemScreen() {
		super();
		this.setTitleText("List Problems");
		this.setLoadingText("Loading....");
	}


	public ListProblemScreen(CIReferenceTableControl refProblemControl) {
		super();
		this.setTitleText("List Problems");
		this.ctrl = refProblemControl;
		if (ctrl instanceof CIInheritanceTableControl) {
		
			DefaultAttributeFilter aFilter = new DefaultAttributeFilter();
			
			aFilter.setSimpleAttributeControl(orders);
		
			((CIInheritanceTableControl)this.ctrl).setAttributeFilter(aFilter);
			((CIInheritanceTableControl)this.ctrl).setOnSelectScreenIndex(ITILApplication.EDIT_PROBLEM_SCREEN);
		}
		setBaseEntryScreen(ITILApplication.get());
	}


	public void load() {
		super.load("ITIL_Problem", null);
	}	

	public ITableControl getTableControl(GWT_CiBean bean) {
		if (this.ctrl == null) {
			CIInheritanceTableControl control = new CIInheritanceTableControl();
			control.setTemplate(bean);
			DefaultAttributeFilter aFilter = new DefaultAttributeFilter();
			aFilter.setSimpleAttributeControl(orders);
			control.setAttributeFilter(aFilter);
			control.setOnSelectScreenIndex(ITILApplication.EDIT_PROBLEM_SCREEN);
			this.ctrl = control;
		}
		this.ctrl.setTemplate(bean);
		return(this.ctrl);
	}
	

}
