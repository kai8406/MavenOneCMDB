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
package org.onecmdb.ui.gwt.desktop.client.widget.mdr;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.action.CloseTextToolItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;

public class MDRStartWidget extends LayoutContainer {
	private TabPanel advanced;
	private CIModel mdr;
	private List<CIModel> configs;
	
	public MDRStartWidget(CIModel mdr, List<CIModel> configs) {
		super();
		this.mdr = mdr;
		this.configs = configs;
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		initUI();
	}

	protected void initUI() {
		setLayout(new FitLayout());
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		
		ToolBar bar = new ToolBar();
		TextToolItem start = new TextToolItem("Start", "start-icon");
		start.setToolTip("Start execution");
		start.addSelectionListener(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				doStart();
			}
		});
		bar.add(start);
		TextToolItem stop = new TextToolItem("Stop", "stop-icon");
		stop.setToolTip("Abort execution before it has executed to completion");
		stop.addSelectionListener(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				doStop();
			}
		});
		bar.add(stop);
			
		panel.setTopComponent(bar);
		
		advanced = new TabPanel();  
		advanced.setMinTabWidth(200);  
		advanced.setResizeTabs(true);  
		advanced.setAnimScroll(false);  
		advanced.setTabScroll(true);  
		for (CIModel model : this.configs) {
			  TabItem item = new TabItem();  
			  item.setText("Progress for  " + model.getDisplayName());  
			  item.setClosable(false);  
			  item.addStyleName("pad-text");  
			  advanced.add(item);
			  item.setLayout(new FitLayout());
			  item.setData("model", model);
		}
	
		panel.add(advanced);
		ToolBar bottom = new ToolBar();
		bottom.add(new FillToolItem());
		bottom.add(new CloseTextToolItem(this));
		panel.setBottomComponent(bottom);
		
		add(panel);
		layout();
	}
	
	 public void doStart() {
		 for (TabItem item : advanced.getItems()) {
			 CIModel model = (CIModel)item.getData("model");
			 item.removeAll();
			 Frame f = new Frame();
			 item.add(f);
			 f.setUrl(getStartURL(model));
			 item.layout();
		 }
	 }
	 public void doStop() {
		 for (TabItem item : advanced.getItems()) {
			 final CIModel model = (CIModel)item.getData("model");

			 // Do a HTTP request..
			 RequestBuilder req = new RequestBuilder(RequestBuilder.GET, getStopURL(model));

			 try {
				 Info.display("Stop", "Send stop to " + model.getDisplayName());
				 req.sendRequest(null, new RequestCallback() {

					 public void onResponseReceived(Request request, Response response) {
						 Info.display("Stop", "COMPLETED. Stop sent to " + model.getDisplayName());
					 }

					 public void onError(Request arg0, Throwable arg1) {
						 Info.display("Stop", "FAILED. to stop " + model.getDisplayName());
					 }
				 });

			 } catch (RequestException e) {
				 Info.display("Stop", "ERROR. to stop " + model.getDisplayName());
			 } 
		 }
	 }

	private String getStartURL(CIModel model) {
		String url = GWT.getModuleBaseURL() + "/" + 
			"onecmdb/exec?" + 
			"token=" + CMDBSession.get().getToken() +
			"&mdr=" + mdr.getValueAsString("name") +
			"&config=" + model.getValueAsString("name") +
			"&verbose=true" + 
			"&cmd=start";
		
		return(url);
	}
	private String getStopURL(CIModel model) {
		String url = GWT.getModuleBaseURL() + "/" + 
			"onecmdb/exec?" + 
			"token=" + CMDBSession.get().getToken() +
			"&mdr=" + mdr.getValueAsString("name") +
			"&config=" + model.getValueAsString("name") +
			"&verbose=true" + 
			"&cmd=stop";
		
		return(url);
	}

}
