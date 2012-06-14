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

import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public class AttributeColumnModel extends ColumnConfig implements ComponentPlugin {

	private Grid grid;
	private AttributeColumnConfig cfg;
	private AttributeSelectorField field;

	public AttributeColumnModel(AttributeColumnConfig cfg) {
		this.cfg = cfg;
		init();
	}
	
	public void init(Component component) {
		if (component instanceof Grid) {
			this.grid = (Grid)component;
			this.field.init(component);
			grid.addListener(CMDBSession.get().getConfig().getGridToolClick(), new Listener<GridEvent>() {
				public void handleEvent(GridEvent e) {
					onMouseDown(e);
				}

			});
			
		}
	}
	
	private void onMouseDown(GridEvent e) {
		int row = grid.getView().findRowIndex(e.getTarget());
		System.out.println("ROW=" + row);
		field.setCurrentRow(row);
	}
	
	protected void init() {
		field = new AttributeSelectorField(this.cfg);
		CellEditor editor = new CellEditor(field) {
			@Override  
			public Object preProcessValue(Object value) {  
				if (true) {
					return(value);
				}
				
				if (value == null) {  
					return value;  
				}
				if (value instanceof ValueModel) {
					CIModel model = new CIModel();
					model.setAlias(((ValueModel)value).getValue());
					return(model);
				}
				if (value instanceof CIModel) {
					return(value);
				}
				return value.toString();  
			}  

			@Override  
			public Object postProcessValue(Object value) {  
				return(value);
			}  
		};  
		setEditor(editor);
		setRenderer(new GridCellRenderer() {


			public String render(ModelData model, String property, ColumnData config, int rowIndex,
					int colIndex, ListStore store) {
				String text = "";
				Object o = (Object)model.get(property);
				if (o instanceof AttributeModel) {
					text = ((AttributeModel)model.get(property)).getAlias();
				}
				return(text);
			}
		});
	}

	public void setPermissions(CMDBPermissions perm) {
		// TODO Auto-generated method stub
		
	}

}
