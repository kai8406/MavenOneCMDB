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

import java.util.ArrayList;
import java.util.List;

import org.gwtiger.client.widget.panel.ButtonCallback;
import org.gwtiger.client.widget.panel.ButtonPanel;
import org.onecmdb.ui.gwt.modeller.client.control.transform.NewTransformControl;
import org.onecmdb.ui.gwt.modeller.client.model.TemplateCache;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.IEventListener;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeValidatePanel;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.NewCIScreen;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewTransformScreen extends OneCMDBBaseScreen implements ButtonCallback, LoadListener {

	protected AttributeValidatePanel vp;
	protected VerticalPanel vPanel;
	private NewTransformControl control;

	public NewTransformScreen() {
		super();
		setTitleText("New DataSet Transform");
		vPanel = new VerticalPanel();
		dockPanel.add(vPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(vPanel, "100%");
		initWidget(dockPanel);
	}
	
	
	public void load() {
		
		ButtonPanel bPanel = getButtonPanel();
		this.control = new NewTransformControl();
		vp = new AttributeValidatePanel(this.control);
		vp.addLoadListener(this);
		vp.load();
		vp.add(bPanel);
		
		vPanel.clear();
		vPanel.add(vp);
	}

	
	

	
	protected ButtonPanel getButtonPanel() {
		ButtonPanel bPanel = new ButtonPanel();
		bPanel.addSaveButton("Create");
		bPanel.addCancelButton("Cancel");
		bPanel.setCallback(this);
		return(bPanel);
	}


	
	public void load(final String objectType, Long objectId) {
		load();
	}
	
		

	public boolean validate() {
		return(vp.validate());
	}

	public void close() {
		History.back();
	}

	public void clear() {
	}


	public void onLoadComplete(Object sender) {
	}

	public void onLoadFailure(Object sender, Throwable caught) {
	}


	public void onLoadStart(Object sender) {
	}


	public void save() {
		control.commit(new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("Failure:" + caught);
				
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_RfcResult) {
					GWT_RfcResult rfcResult = (GWT_RfcResult)result;
					if (rfcResult.isRejected()) {
						setErrorText("Rejected cause: " + rfcResult.getRejectCause());
					} else {
						History.back();
					}
				}
				
			}
			
		});
		
	}
	
	
}
