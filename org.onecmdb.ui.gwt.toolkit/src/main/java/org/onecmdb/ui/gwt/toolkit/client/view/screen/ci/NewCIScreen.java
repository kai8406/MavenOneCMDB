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

import org.gwtiger.client.widget.panel.ButtonCallback;
import org.gwtiger.client.widget.panel.ButtonPanel;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIAttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeLoader;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeRender;
import org.onecmdb.ui.gwt.toolkit.client.view.input.CIValueInputPanel;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewCIScreen extends OneCMDBBaseScreen implements ButtonCallback,LoadListener {

	
	protected CIValueInputPanel attributePanel;
	protected AttributeRender render;
	protected CIAttributeValueInputControl control;
	protected String templateAlias;
	protected CellPanel input = null;
	
	public NewCIScreen() {
		this.setTitleText("New Instance of ...");
		input = getMainPanel();
		dockPanel.add(input, CENTER);
		dockPanel.setCellWidth(input, "100%");
		initWidget(this.dockPanel);
		showError(false);
	}

	protected CellPanel getMainPanel() {
		return(new VerticalPanel());
	}
	
	public void save() {
		setLoadingText("Saving....");
		showLoading(true);
		
		control.commit(new AsyncCallback() {
			public void onFailure(Throwable caught) {
				showLoading(false);
				onCommitFailure(caught);
			}
	
			public void onSuccess(Object result) {
				showLoading(false);
				onCommitSuccess(result);
			}
		});
	}

	protected void onCommitSuccess(Object result) {
		History.back();
	}

	protected void onCommitFailure(Throwable caught) {		
		setErrorText("ERROR: Create new Instance exception:" + caught.getMessage());
		showError(true);
	}

	
	/*
	protected GWT_CiBean newCI(GWT_CiBean template, List values) {
		GWT_CiBean newCI = new GWT_CiBean();
		newCI.setDerivedFrom(template.getAlias());
		newCI.setAlias(template.getAlias() + System.currentTimeMillis());
		newCI.setAttributeValues(values);
		newCI.setTemplate(false);
		return(newCI);
	}
	*
	*/
	
	public boolean validate() {
		if (this.attributePanel == null) {
			return(true);
		}
		return(this.attributePanel.validate());
	}

	
	public void clear() {
		if (this.attributePanel != null) {
			this.attributePanel.clear();
		}
		showError(false);
	}
	
	/**
	 * Load with an object id.
	 */
	public void load(String objectType, Long objectId) {
		setErrorText("");
		input.clear();
		update(objectType);
	}
	
	protected void update(String objectType) {
		this.templateAlias = objectType;
		
		input.setStyleName("one-new-screen-panel");
		attributePanel = new CIValueInputPanel();
			
		Widget buttonPanel = getButtonPanel();
		buttonPanel.setStyleName("one-button-panel");
		input.add(attributePanel);
		input.add(buttonPanel);
			
		input.setCellHorizontalAlignment(buttonPanel, HorizontalPanel.ALIGN_CENTER);
		input.setCellVerticalAlignment(buttonPanel, VerticalPanel.ALIGN_TOP);
				
			
		System.out.println("NewCIScreen.Load(" + objectType + ")");
			
		// Create a new Render every time, since it holds mapping between widget and values!
		render = new AttributeRender();
		render.setIsNew(isNew());
		attributePanel.setAttributeRender(render);
		/*
		if (this.templateAlias == objectType) {
			this.attributePanel.clear();
		} else {
		*/
		
		control = getControl();
		control.setAttributeFilter(getAttributeFilter());
		this.attributePanel.setAttributeValueControl(control);
		this.attributePanel.addLoadListener(this);
		this.attributePanel.load();
		//}
	}

	protected CIAttributeValueInputControl getControl() {
		//if (this.control == null) {
			this.control = new CIAttributeValueInputControl(templateAlias, isNew());
		//}
		return(this.control);
	}

	protected boolean isNew() {
		return(true);
	}

	protected Widget getButtonPanel() {
		// Add Button Panel..
		ButtonPanel b = new ButtonPanel();
		b.addSaveButton();
		b.addCancelButton();
		b.setCallback(this);
		
		return(b);
	}
	
	public IAttributeFilter getAttributeFilter() {
		return(null);
	}
	
	protected IAttributeLoader loadTickIssuer() {
		return(new IAttributeLoader() {

			public void load(final AsyncCallback callback) {
				// Load people for the account.
				GWT_CiBean account = getBaseEntryScreen().getAccount();
				OneCMDBConnector.getInstance().evalRelation(OneCMDBSession.getAuthToken(), 
						account, 
						"<$template{Person}", 
						null, 
						new AsyncCallback() {

							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}

							public void onSuccess(Object result) {
								if (result instanceof GWT_CiBean[]) {
									GWT_CiBean peoples[] = (GWT_CiBean[])result;
									if (peoples.length > 0) {
										callback.onSuccess(peoples[0]);
									}
								}
							}
						});
			}
			
		});
	}

	public void onLoadComplete(Object sender) {
		// Update header....
		setTitleText("New Instance of");
		setTitleWidget(new CIIconDisplayNameWidget(control.getBase()));
		showLoading(false);
	}

	public void onLoadFailure(Object sender, Throwable caught) {
	}

	public void onLoadStart(Object sender) {
	}

	
	public void close() {
		History.back();
	}

	

}
