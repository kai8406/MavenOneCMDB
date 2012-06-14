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

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.control.CIProxy;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.multi.MultiValueComboBox;
import org.onecmdb.ui.gwt.desktop.client.widget.multi.MultiValueGrid;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dev.jjs.impl.CloneExpressionVisitor;

public class MultiColumnConfig extends ColumnConfig implements ComponentPlugin {

	
	private Grid grid;
	private AttributeColumnConfig config;
	private CMDBPermissions permissions;
	
	public MultiColumnConfig() {
		super();
		init();
	}
	public MultiColumnConfig(AttributeColumnConfig config, CMDBPermissions perm) {
		super();
		this.config = config;
		this.permissions = perm;
		init();
	}
	public MultiColumnConfig(String id, String name, int width) {
		super(id, name, width);
		init();
	}
	
	
	protected void init() {
		setEditor(getMultiEditor());
		setRenderer(new GridCellRenderer() {


			public String render(ModelData model, String property, ColumnData config, int rowIndex,
					int colIndex, ListStore store) {

				if (model instanceof CIModelCollection) {
					String split[] = property.split("\\.");
					String name = split[0];
					
					String text = "";
					CIModel ci = ((CIModelCollection)model).getCIModel(name);
					
					Object v = null;
					if (split.length > 1) {
						String attr = split[1];
						v = ci.get(attr);
					}
					 
					if (v != null) {
						if (v instanceof ValueListModel) {
							ValueListModel listModel = (ValueListModel)v;
							List<ValueModel> list = listModel.getValues();
							text = "[" + list.size() + "]";
							String sep = "";
							int count = 0;
							for (ValueModel val : list) {
								// Only add 5
								if (count > 4) {
									text = text + "...";
									break;
								}
								count++;
								text = text + sep + EditorFactory.renderValueModel(val);
								if (sep.length() == 0) {
									sep = ", ";
								}
							}
						}
					} else {
						text = "[0]";
					}
					
					config.css = "x-grid3-multi-col-td";
					return("<div class='x-grid3-multi-col x-grid3-multi-" + getId() + "'>" +
						text + 
						"</div>");
					
				}
				return("<Illgeal Value>");
			}
		});
	}

	protected CellEditor getMultiEditor() {
		if (this.config == null) {
			return(null);
		}
		
		MultiValueComboBox multi = new MultiValueComboBox(config, config.getType(), permissions);
		multi.setPropertyEditor(new PropertyEditor<ValueListModel>() {

			public String getStringValue(ValueListModel value) {
				if (value == null) {
					return("[0]");
				}
				List<ValueModel> list = value.getValues();
				String text = "[" + list.size() + "]";
				String sep = "";
				int count = 0;
				for (ValueModel val : list) {
					// Only add 5
					if (count > 4) {
						text = text + "...";
						break;
					}
					count++;
					String valueStr = val.getValueDisplayName();
					if (valueStr == null) {
						valueStr = "";
					}
					text = text + sep + valueStr;
					if (sep.length() == 0) {
						sep = ", ";
					}
				}
				return(text);
			}

			public ValueListModel convertStringValue(String value) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		CellEditor editor = new CellEditor(multi) {
			@Override  
			public Object preProcessValue(Object value) {  
				if (value == null) {  
					return value;  
				}
				return(value);
			}  

			@Override  
			public Object postProcessValue(Object value) {  
				return(value);
			}  
		};  
		return(editor);
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
		if (!permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
			return;
		}
		
	    String cls = ge.getTarget().getClassName();
	    if (cls != null && cls.indexOf("x-grid3-multi-" + getId()) != -1) {
	      ge.stopEvent();
	      
	      
	      int index = grid.getView().findRowIndex(ge.getTarget());
	      ModelData m = grid.getStore().getAt(index);
	      if (m instanceof CIPropertyGrid.ValueWrapper) {
	    	  m = ((CIPropertyGrid.ValueWrapper)m).getModel();
	      }
	  
	      int col = grid.getView().findCellIndex(ge.getTarget(), null);
	      
	      ColumnConfig colModel = grid.getColumnModel().getColumn(col);
	      
	     
	      
	      String property = config.getId();
	      
	      MultiValueGrid grid = new MultiValueGrid(config);
	      grid.setPermissions(this.permissions);
	      grid.setValue((ValueListModel)m.get(property));
	      final Popup p = new Popup();
	      grid.addListener(CMDBEvents.POPUP_HIDE_EVENT, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				p.hide();
			}
	    	  
	      });
	      p.setLayout(new FitLayout());
	      p.add(grid);
	      /*Align...
	       * align = "tl-tr";
        adj = new int[] {0, 24};
        break;
      case EAST:
        icon = "left";
        align = "tr-tl";
        adj = new int[] {0, 24};
        break;
      case NORTH:
        icon = "down";
        align = "tl-bl";
        break;
      case SOUTH:
        icon = "up";
        align = "bl-tl";
	       */
	      p.show(ge.getTarget(), "tl-bl");
	     
	      //getMultiEditor().startEdit(ge.getTarget(), );
	      /*MultiValueComboBox comboBox = new MultiValueComboBox(config, config.getType(), permission);
	      comboBox.setValue((ValueListModel)m.get(property));
	      comboBox.expand();
	      */
	      //String url = getStringValue(m, property);
	      
	    
	    }
	  }
}
