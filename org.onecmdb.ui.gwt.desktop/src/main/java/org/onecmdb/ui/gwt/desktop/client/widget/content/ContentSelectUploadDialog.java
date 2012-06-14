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
package org.onecmdb.ui.gwt.desktop.client.widget.content;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.widget.ContentSelectorWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.FileUploadWidget;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.button.FillButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class ContentSelectUploadDialog extends Dialog {

	private ContentSelectorWidget selector;

	public ContentSelectUploadDialog(ContentSelectorWidget sel) {
		this.selector = sel;
		setSize(300, 400);
		setupButtons();
	}
	
	protected void setupButtons() {
		ButtonBar bar = getButtonBar();
		bar.removeAll();
		//addButton(new FillButton());
		addButton(new Button("Upload", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				List<ContentData> list = selector.getSelected();
				ContentData root = selector.getRoot();
				if (list != null && list.size() == 1) {
					ContentData d = list.get(0);
					if (d.isDirectory()) {
						root = d;
					}
				}
				FileUploadWidget upload = new FileUploadWidget(root);
				upload.setComplex(false);
				upload.addListener(Events.Close, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {
						selector.reload();
					}
					
				});
				upload.show();
				
			}
			
		}));
		addButton(new Button("Select", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				fireEvent(Events.Select);
			}
			
		}));
		
		addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				close();
			}
			
		}));
		setHeading("Select file from " + selector.getRoot().getPath());
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
		panel.add(selector);
		
		
		add(panel);
	}
	
}
