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
package org.onecmdb.ui.gwt.desktop.client.fixes;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.control.CIProxy;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyPanel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HTML;

public class PopupTextColumnConfig extends ColumnConfig implements ComponentPlugin {
	private Grid grid;
	private AttributeColumnConfig aConfig;
	private CMDBPermissions permissions;
	private GridCellRenderer specificRender;

	public PopupTextColumnConfig() {
		super();
		init();
	}
	public PopupTextColumnConfig(AttributeColumnConfig config, CMDBPermissions perm) {
		super();
		this.aConfig = config;
		this.permissions = perm;
		init();
	}
	public PopupTextColumnConfig(String id, String name, int width) {
		super(id, name, width);
		init();
	}

	public void setPermissions(CMDBPermissions perm) {
		this.permissions = perm;
	}

	protected void init() {
	}

	public void init(Component component) {
		if (component instanceof Grid) {
			this.grid = (Grid) component;
			grid.addListener(CMDBSession.get().getConfig().getGridToolClick(), new Listener<GridEvent>() {
				public void handleEvent(GridEvent e) {
					onMouseDown(e);
				}
			});
		}
	}
	
	
	@Override
	public void setRenderer(GridCellRenderer renderer) {
		this.specificRender = renderer;
		super.setRenderer(new GridCellRenderer<ModelData>() {

			public String render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store) {
				String text = specificRender.render(model, property, config, rowIndex, colIndex, store);
				
				config.css = "x-grid3-popup-col-td";
				if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
					
					return "<div class='x-grid3-popup-col x-grid3-popup-" + getId() + "'>" +
					text +
					"</div>";
				} else {
					return(text);
				}
			}
			
		});
		
	}
	
	protected void onMouseDown(GridEvent ge) {
		if (permissions == null) {
			return;
		}
		if (!permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
			return;
		}
		String cls = ge.getTarget().getClassName();
		String pcls = ge.getTarget().getParentElement().getClassName();
	    
	    if (cls == null || pcls == null) {
	    	return;
	    }
	    String id = getId();
	    if (cls.indexOf("x-grid3-popup-" + id) >= 0) {
	    	// Continue;
	    } else if (pcls.indexOf("x-grid3-popup-" + id) >= 0) {
	    	// Continue
	    } else {
	    	return;
	    }
	    
	    ge.stopEvent();

	    int rowIndex = grid.getView().findRowIndex(ge.getTarget());
	    ModelData m = grid.getStore().getAt(rowIndex);
	    
	    if (m instanceof CIPropertyGrid.ValueWrapper) {
	    	  m = ((CIPropertyGrid.ValueWrapper)m).getModel();
	    }
	    
	    int colIndex = grid.getView().findCellIndex(ge.getTarget(), null);

	    ColumnConfig colModel = grid.getColumnModel().getColumn(colIndex);

	    String property = aConfig.getId();
	    Object value = m.get(property);
	    if (aConfig.isInternal() && getId().endsWith("." + CIModel.CI_DISPLAYNAME)) {
	    	if (!(m instanceof CIModelCollection)) {
	    		return;
	    	}
	     	fireEvent(CMDBEvents.POPUP_HIDE_EVENT);
	 	   
	     	CIModelCollection col = (CIModelCollection)m;
	 	   	// Open property window.
	    	CIPropertyPanel propPanel = new CIPropertyPanel(aConfig.getMDR(), col, "Root");
			propPanel.setPermissions(permissions);
			Window w = WindowFactory.getWindow("Properties for " + col.getCIModels().get(0).getDisplayName(), propPanel);
			//w.add(new CIValueForm(gridConfig, store, data));
			w.show();
			w.layout();
			w.toFront();
		} else {
			String text = "";
			if (this.specificRender != null) {
				text = this.specificRender.render(m, property, null, rowIndex, colIndex, grid.getStore());
			} else {
				if (value != null) {
					text = value.toString();
				}
			}

			Popup p = new Popup();
			p.setAnimate(false);
			p.setBorders(true);
			p.setLayout(new FitLayout());
			ContentPanel cp = new ContentPanel();
			cp.setWidth(250);
			cp.setHeaderVisible(false);
			cp.setAutoHeight(true);
			cp.addText(text);
			p.add(cp);
			p.show(ge.getTarget(), "tl-bl");
		}
	}
}
