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
package org.onecmdb.ui.gwt.desktop.client.fixes.combo;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class SelectContentPanel<M> extends ContentPanel implements IValueComponent<M> {

	private IValueComponent<M> valueComp;
	private String header;
	private boolean clear;
	private M cancelValue;
	private boolean canceled = true;

	public SelectContentPanel(String header, IValueComponent<M> comp) {
		super();
		this.header = header;
		this.valueComp = comp;
		
		TextToolItem cancelItem = new TextToolItem("Cancel", "cancel-icon");
		cancelItem.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				cancelValue();
			}
		});
		TextToolItem selectItem = new TextToolItem("Select", "select-icon");
		selectItem.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				selectValue();

			}
		});
		TextToolItem clearItem = new TextToolItem("Clear", "clear-icon");
		clearItem.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				clearValue();
			}
		});

		ToolBar bar = new ToolBar();	
		bar.add(new FillToolItem());
		bar.add(clearItem);
		bar.add(selectItem);
		bar.add(cancelItem);
		setTopComponent(bar);
	}
	
	protected void clearValue() {
		this.clear = true;
		selectValue();
		this.clear = false;
		
	}

	public M getValue() {
		if (this.canceled) {
			return(this.cancelValue);
		}
		if (this.clear) {
			return(null);
		}
		return(this.valueComp.getValue());
	}

	public void setValue(M value) {
		this.cancelValue = value;
		this.valueComp.setValue(value);
	}

	public void cancelValue() {
		fireEvent(Events.Hide);
	}

	public void selectValue() {
		this.canceled = false;
		fireEvent(Events.Select);
		this.canceled = true;
	}

	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
	
		initUI();
	}	

	public void initUI() {
		setLayout(new FillLayout());
		setHeading(this.header);
		add((Component)this.valueComp);
		layout();
	}
}
