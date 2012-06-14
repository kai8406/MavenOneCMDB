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

import java.util.Arrays;

import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIDescriptionControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ViewCIScreen;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ViewProblemScreen extends ViewCIScreen {
	TextAttributeControl addSolution = new TextAttributeControl("solution", true, false, TextAttributeControl.TEXT_AREA_TYPE, new Integer(5), null);
	private static DefaultAttributeFilter defFilter = new DefaultAttributeFilter();
	
	public ViewProblemScreen() {
		super();
		setTitleText("View Problem/Known Error");
		defFilter.setAttributeControl(Arrays.asList(orders));
		
	}
	
	protected Widget getButtonPanel() {
		HTML back = new HTML("<a href='javascript:;'>[back]</a>");
		back.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				History.back();
			}
		});
		return(back);
	}

	private AttributeControl[] orders = new AttributeControl[] {
			new AttributeControl("ID", true, false),
			new AttributeControl("title", true, false), 
			new CIDescriptionControl(),
			//new TextAttributeControl("opDescription", true, false, TextAttributeControl.TEXT_AREA_TYPE, new Integer(5), null),
			new AttributeControl("affectedCIs", true, false),
			//new AttributeControl("problem", true, false),
			new AttributeControl("priority", true, false),
			new AttributeControl("status", true, false),
			/*
			new AttributeControl("reportedBy", true, false),
			new AttributeControl("reportedDate", true, false),
			*/
			new AttributeControl("ticketIssuer", true, false),
			new AttributeControl("actionHistory", true, false),
			addSolution
	};
	
	public IAttributeFilter getAttributeFilter() {
		return(defFilter);
	}
	
	public void onLoadComplete(Object sender) {
		showLoading(false);
	}
	
	
	
	
}
