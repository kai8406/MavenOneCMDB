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
package org.onecmdb.ui.gwt.toolkit.client.view.screen.ci;


import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.control.table.CIInheritanceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.control.table.CIReferenceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.control.table.ITableControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeRender;
import org.onecmdb.ui.gwt.toolkit.client.view.input.IAttributeRender;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.table.CITablePageControlPanel;
import org.onecmdb.ui.gwt.toolkit.client.view.table.CITablePanel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListCIScreen extends OneCMDBBaseScreen implements TableListener, LoadListener {
	protected CITablePanel table = null;
	protected ITableControl ctrl;
	private VerticalPanel vPanel;
	private GWT_CiBean template;
	private boolean isNewSupport;
	protected ITableControl tableCtrl;
	
	
	public ListCIScreen() {
		super();
		vPanel = new VerticalPanel();
		dockPanel.add(vPanel, CENTER);
		dockPanel.setCellHeight(vPanel, "100%");
		initWidget(dockPanel);
	}
	
	public void setTemplate(GWT_CiBean template) {
		this.template = template;
	}
	
	public void load() {
		setupTable();
	}
	
	public void load(String objectType, Long objectId) {
		// Messy to set this here....
		if (objectId != null && objectId.longValue() == -1) {
			isNewSupport = true;
		}
		
		setErrorText("");
		showLoading(true);
		OneCMDBConnector.getCIFromAlias(objectType, new AsyncCallback() {
			

			public void onFailure(Throwable caught) {
				showLoading(false);
				setErrorText("ERROR: " + caught.getMessage());
			}

			public void onSuccess(Object result) {
				showLoading(false);
				if (result instanceof GWT_CiBean) {
					template = (GWT_CiBean)result;
					setupTable();
					return;
				}	
				setErrorText("Not a correct object returned!");
			}	
		});
	}
	
	protected void setupTable() {
		if (this.template == null) {
			return;
		}
		setTitleText("Instances of ");
		setTitleWidget(new CIIconDisplayNameWidget(this.template));
		
		
		vPanel.clear();
		table = null;
		if (isNewSupport()) {
			HorizontalPanel actionPanel = new HorizontalPanel();
			HTML add = new HTML("[<a href='javascript:;'>new</a>]");
			add.setTitle("Create a new instance");
			actionPanel.add(add);
			add.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					getBaseEntryScreen().showScreen(OneCMDBApplication.NEW_CI_SCREEN, template.getAlias(), new Long(0));
				}
			
			});
			actionPanel.add(add);
			actionPanel.setCellHorizontalAlignment(add, HorizontalPanel.ALIGN_RIGHT);
			actionPanel.setWidth("100%");
			vPanel.add(actionPanel);
		}
		VerticalPanel tablePanel = new VerticalPanel();
		tablePanel.setStyleName("onecmdb-table-panel");

		if (table == null) {
			table = new CITablePanel();
			table.addTableListener(ListCIScreen.this);
			table.addLoadListener(ListCIScreen.this);
			CITablePageControlPanel tablePageControl = new CITablePageControlPanel(table);
			tablePanel.add(tablePageControl);
			tablePanel.add(table);
			tablePanel.setCellVerticalAlignment(tablePageControl, VerticalPanel.ALIGN_TOP);
			tablePanel.setCellVerticalAlignment(table, VerticalPanel.ALIGN_TOP);
		}
		vPanel.add(tablePanel);
		table.setAttributeRender(getAttributeRender());
		
		table.setTabelControl(getTableControl(this.template));
		table.load();
	}

	protected IAttributeRender getAttributeRender() {
		return(new AttributeRender());
	}
	
	protected boolean isNewSupport() {
		return(isNewSupport);
	}
	
	public void setNewSupport(boolean value) {
		this.isNewSupport = value;
	}

	public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
		int index = table.getTableControl().getSelectScreenIndex();
		
		if (index >= 0) {
			String type = table.getTableControl().getObjectName(row, cell);
			if (type != null) {
				getBaseEntryScreen().showScreen(index, type, new Long(0));
			}
		}
	}
	
	/**
	 * Override this to implement different table control flavors.
	 * 
	 * @param bean
	 * @return
	 */
	public ITableControl getTableControl(GWT_CiBean bean) {
		if (this.tableCtrl != null) {
			this.ctrl = tableCtrl;
			this.ctrl.setTemplate(bean);
			return(ctrl);
		}
		//if (this.ctrl == null) {
		this.ctrl = new CIInheritanceTableControl();
		//}
		this.ctrl.setTemplate(bean);
		this.ctrl.setOnSelectScreenIndex(getBaseEntryScreen().VIEW_CI_SCREEN);
		
		return(ctrl); 
	}


	public void onLoadComplete(Object sender) {
		showLoading(false);
	}

	public void onLoadFailure(Object sender, Throwable caught) {
		setErrorText("Loading FAILED: " + caught.getMessage());
		showLoading(false);
	}

	public void onLoadStart(Object sender) {
		System.out.println("LOADINING........");
		setLoadingText("Loading....");
	}

	public void setTabelControl(ITableControl tableCtrl) {
		this.tableCtrl = tableCtrl;
		
	}

}
