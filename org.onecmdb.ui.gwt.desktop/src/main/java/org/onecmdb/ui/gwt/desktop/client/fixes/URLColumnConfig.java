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
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public class URLColumnConfig extends ColumnConfig implements ComponentPlugin {

	private Grid grid;
	private boolean readonly;
	
	public URLColumnConfig() {
		super();
		init();
	}

	public URLColumnConfig(String id, String name, int width) {
		super(id, name, width);
		init();
	}
		  
	 public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	protected void init() {
		    setRenderer(new GridCellRenderer() {
		      

			public String render(ModelData model, String property, ColumnData config, int rowIndex,
		          int colIndex, ListStore store) {
		    	  
		    	String value = getStringValue(model, property);
			    
		        config.css = "x-grid3-url-col-td";
		        if (readonly) {
		        	return "<div class='x-grid3-url-col x-grid3-url-" + getId() + "'>" +
		        		"<a href='javascript:void()'>" + value + "</a>" +
		        		"</div>";
		        } else {
		        	return(value);
		        }
		      }
		    });
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
	
	protected void onMouseDown(GridEvent ge) {
	    String cls = ge.getTarget().getParentElement().getClassName();
	    if (cls != null && cls.indexOf("x-grid3-url-" + getId()) != -1) {
	      ge.stopEvent();
	      
	      int index = grid.getView().findRowIndex(ge.getTarget());
	      ModelData m = grid.getStore().getAt(index);
	      String property = "";
	      if (m instanceof CIPropertyGrid.ValueWrapper) {
	    	  property = getId();
	    	  m = ((CIPropertyGrid.ValueWrapper)m).getModel();
	      } else {
	    	  int col = grid.getView().findCellIndex(ge.getTarget(), null);

	    	  ColumnConfig colModel = grid.getColumnModel().getColumn(col);

	    	  property = colModel.getDataIndex();
	      }
	      String url = getStringValue(m, property);
	      
	      com.google.gwt.user.client.Window.open(url, "_blank", "");
	    }
	  }
	
	protected String getStringValue(ModelData model, String property) {
	  	  String v = null;
	  	  
	  	  Object value = model.get(property);
  		  if (value instanceof ValueModel) {
  	  		  v = ((ValueModel)value).getValue();
  		  } else {
  			  if (value != null) {
  				  v = value.toString();
  			  }
  		  }
  		  if (v == null) {
  			  v = "";
  		  }
  		  return(v);
	  }

	
}
