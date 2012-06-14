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
package org.onecmdb.ui.gwt.modeller.client.view.screen.transform;

import org.onecmdb.ui.gwt.modeller.client.OneCMDBModelCreator;
import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIWidget;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class ViewTransformScreen extends EditTransformScreen implements ClickListener {
	
	public IAttributeFilter getAttributeFilter() {
		DefaultAttributeFilter aFilter = new DefaultAttributeFilter();
		aFilter.setIsReadonly(true);
		aFilter.setClickListener(this);
		return(aFilter);
	}


	public void onClick(Widget sender) {
		if (sender instanceof CIWidget) {
			CIWidget widget = (CIWidget)sender;
			GWT_CiBean bean = widget.getCI();
			if (bean != null) {
				getBaseEntryScreen().showScreen(OneCMDBModelCreator.EDIT_CI_SCREEN, bean.getAlias(), new Long(0));
			}
		}
	}
	
	
	protected CellPanel getMainPanel() {
		return(new HorizontalPanel());
	}


	protected Widget getButtonPanel() {
		// Add edit button.
		VerticalPanel actionPanel = new VerticalPanel();
		
		/*
		relationPanel = new VerticalPanel();
		relationPanel.setStyleName("mdv-form");
		*/
		
		//HorizontalPanel hPanel = new HorizontalPanel();
		
		HTML edit = new HTML("[<a href='javascript:;'>edit</a>]");
		edit.setTitle("Edit this instance");
		HTML editASelector = new HTML("[<a href='javascript:;'>edit Attribute Selectors</a>]");
		editASelector.setTitle("Edit attribute selectors.");
		HTML delete = new HTML("[<a href='javascript:;'>delete</a>]");
		delete.setTitle("Delete this instance");
		HTML reference = new HTML("[<a href='javascript:;'>show references</a>]");
		reference.setTitle("Show inbound/outbound reference for this CI.");
		HTML testTransform = new HTML("[<a href='javascript:;'>test transform</a>]");
		testTransform.setTitle("Select a data source a test this transform.");
		
		edit.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				getBaseEntryScreen().showScreen(OneCMDBModelCreator.EDIT_TRANSFORM_SCREEN, 
						control.getBase().getAlias(),
						new Long(0)); 		
			}
			
		});	
		editASelector.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				getBaseEntryScreen().showScreen(OneCMDBModelCreator.EDIT_ATTRIBUTE_SELECTOR_SCREEN, 
						control.getBase().getAlias(),
						new Long(0)); 		
			}
			
		});	
	

		delete.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				if (Window.confirm("Delete " + control.getBase().getDisplayName()  +"\nAre you sure?")) {
					control.delete(new AsyncCallback() {

						public void onFailure(Throwable caught) {
							setErrorText("ERROR: " + caught);
							
						}

						public void onSuccess(Object result) {
							History.back();
						}
					});
				}
			}
		});	
		reference.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					getBaseEntryScreen().showScreen(OneCMDBApplication.REFERENCE_CI_SCREEN, 
							control.getBase().getAlias(),
							new Long(0)); 		
				}
		});	
		
		testTransform.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				getBaseEntryScreen().showScreen(OneCMDBModelCreator.TEST_TRANSFORM_SCREEN, 
						control.getBase().getAlias(),
						new Long(0)); 		
			}
		});	
		
		actionPanel.add(edit);
		actionPanel.add(editASelector);
		actionPanel.add(delete);
		actionPanel.add(reference);
		actionPanel.add(testTransform);
		return(actionPanel);
	}


}
