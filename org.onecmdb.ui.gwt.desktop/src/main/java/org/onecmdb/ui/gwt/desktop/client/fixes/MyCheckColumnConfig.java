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
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public class MyCheckColumnConfig extends CheckColumnConfig {
	
	protected boolean readonly = true;
	private IModelPermission modelPermission;
	
	
	
	public MyCheckColumnConfig() {
		super();
	}
	
	public MyCheckColumnConfig(String id, String name, int width) {
		super(id, name, width);
	}

	public IModelPermission getModelPermission() {
		return modelPermission;
	}

	public void setModelPermission(IModelPermission modelPermission) {
		this.modelPermission = modelPermission;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	protected void onMouseDown(GridEvent ge) {
		if (this.readonly) {
			return;
		}
	    String cls = ge.getTarget().getClassName();
	    if (cls != null && cls.indexOf("x-grid3-cc-" + getId()) != -1) {
	      ge.stopEvent();
	      int index = grid.getView().findRowIndex(ge.getTarget());
	      ModelData m = grid.getStore().getAt(index);
	      int col = grid.getView().findCellIndex(ge.getTarget(), null);
	      ColumnConfig colModel = grid.getColumnModel().getColumn(col);
	      String property = colModel.getDataIndex();
	    
	      if (!isModelEditable(m, property)) {
	    	  return;
	      }
	      Record r = grid.getStore().getRecord(m);
	      boolean v = getValue(m, property);
	      r.set(property, "" + !v);
	    }
	  }
	
	private boolean isModelEditable(ModelData m, String property) {
		if (modelPermission == null) {
			return(true);
		}
		return(modelPermission.allowEdit(m, property));
	}

		protected boolean getValue(ModelData model, String property) {
	  	  boolean v = false;
	    
	  	  if (model instanceof AttributeModel) {
    		  Object value = model.get(property);
    		  if (value != null) {
				  v = Boolean.parseBoolean(value.toString());
			  }
		  } else if (model instanceof CIModelCollection) { 
    		  String split[] = property.split("\\.");
    		  String name = split[0];
    		  String attr = split[1];

    		  CIModel ci = ((CIModelCollection)model).getCIModel(name);
    		  ValueModel vModel = ci.get(attr);
    		  if (vModel != null) {
    			  String value = vModel.getValue();
    			  if (value != null) {
    				  v = Boolean.parseBoolean(value);
    			  }
    		  }
    	  } else {
    		  Object value = model.get(property);
    		  if (value == null) {
    			  v = false;
    		  } else if (value instanceof ValueModel) {
    	  		  String str = ((ValueModel)value).getValue();
    	  		  v = "true".equalsIgnoreCase(str);
    	  	  } else if (value instanceof Boolean) {
    	  		  v = (Boolean)value;
    		  } else if (value instanceof String) {
    			  v = Boolean.parseBoolean((String)value);
    		  }
    	  }
    	  return(v);
	  }
	  protected void init() {
	    setRenderer(new GridCellRenderer() {
	      public String render(ModelData model, String property, ColumnData config, int rowIndex,
	    		  int colIndex, ListStore store) {

	    	  
	    	  boolean v = getValue(model, property);
	    	 
	    	  String on = v ? "-on" : "";
	    	  
	    	  config.css = "x-grid3-check-col-td";
	    	  if (readonly || !isModelEditable(model, property)) {
	    	  		return "<div class='x-grid3-check-ro-col" + on + " x-grid3-cc-" +getId() + "'>&#160;</div>";
	    	  } 
	    	  		return "<div class='x-grid3-check-col" + on + " x-grid3-cc-" +getId() + "'>&#160;</div>";
	    	  	
	    	  }
	    });
	  }
}
