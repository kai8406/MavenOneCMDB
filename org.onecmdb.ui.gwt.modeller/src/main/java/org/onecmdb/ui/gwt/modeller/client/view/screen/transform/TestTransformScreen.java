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

import org.gwtiger.client.widget.panel.ButtonCallback;
import org.gwtiger.client.widget.panel.ButtonPanel;
import org.onecmdb.ui.gwt.modeller.client.control.transform.NewTransformControl;
import org.onecmdb.ui.gwt.modeller.client.control.transform.TestTransformControl;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.listener.LoadListener;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeValidatePanel;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TestTransformScreen extends OneCMDBBaseScreen implements ButtonCallback, LoadListener {

	protected AttributeValidatePanel vp;
	protected VerticalPanel vPanel;
	private TestTransformControl control;
	private GWT_CiBean dataSet;
	private VerticalPanel resultTablePanel = new VerticalPanel();
	
	public TestTransformScreen() {
		super();
		setTitleText("New DataSet Transform");
		vPanel = new VerticalPanel();
		dockPanel.add(vPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(vPanel, "100%");
		initWidget(dockPanel);
		
	}
	
	public void load() {
		
		ButtonPanel bPanel = getButtonPanel();
		this.control = new TestTransformControl();
		this.control.setDataSet(this.dataSet);
		vp = new AttributeValidatePanel(this.control);
		vp.addLoadListener(this);
		vp.load();
		vp.add(bPanel);
		
		vPanel.clear();
		vPanel.add(vp);
		
		vPanel.add(resultTablePanel );
	}

	
	
	protected ButtonPanel getButtonPanel() {
		ButtonPanel bPanel = new ButtonPanel();
		bPanel.addSaveButton("Test");
		bPanel.addCancelButton("Cancel");
		bPanel.setCallback(this);
		return(bPanel);
	}


	
	public void load(final String objectType, Long objectId) {
		
		OneCMDBConnector.getCIFromAlias(objectType, new AsyncCallback() {

			

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					dataSet = (GWT_CiBean)result;
					load();
				}
			}
			
		});
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
		System.out.println("CALL TRANSFORM......................");
		final long start = System.currentTimeMillis();
		OneCMDBConnector.getInstance().transform(OneCMDBSession.getAuthToken(),
				control.getDataSet().getAlias(),
				control.getDataSource().getAlias(),
				new AsyncCallback() {

			public void onFailure(Throwable caught) {

				resultTablePanel.add(new Label("ERROR:" + caught));
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean[]) {
					resultTablePanel.add(new Label("# Beans=" + 
							((GWT_CiBean[])result).length + 
							", time=" + (System.currentTimeMillis()-start) + "ms"));
				}

			}

		}
		);
	}
}
