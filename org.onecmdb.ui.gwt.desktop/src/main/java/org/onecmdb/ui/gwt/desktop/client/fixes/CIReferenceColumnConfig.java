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

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.AdaptableTriggerField;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyPanel;
import org.onecmdb.ui.gwt.desktop.client.widget.group.table.GroupTableWidget;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class CIReferenceColumnConfig extends ColumnConfig implements ComponentPlugin {

	private Grid grid;
	private AttributeColumnConfig config;
	private CMDBPermissions permissions;

	public CIReferenceColumnConfig() {
		super();
		init();
	}
	public CIReferenceColumnConfig(AttributeColumnConfig config) {
		super();
		this.config = config;
		init();
	}
	public CIReferenceColumnConfig(String id, String name, int width) {
		super(id, name, width);
		init();
	}

	public void setPermissions(CMDBPermissions perm) {
		this.permissions = perm;
	}

	protected void init() {
		setEditor(getCIEditor());
		setRenderer(new GridCellRenderer() {


			public String render(ModelData model, String property, ColumnData cfg, int rowIndex,
					int colIndex, ListStore store) {
				String text = "";
				String image = "";
				
				if (model instanceof CIModelCollection) {
					String split[] = property.split("\\.");
					String name = split[0];
			
					Object value = ((CIModelCollection)model).get(name);
				
					if (text == null) {
						return("");
					}
					if (value == null) {
						text = "0" + config.getType();
					} else if (value instanceof List) {
						List values = (List)value;
						
						if (values.size() == 0) {
							text = "0 " + config.getType();
						} else {
							text = values.size() + config.getType() + "s";
						}
					} else {
						text = "[unknown]" + value.getClass().getName();
					}
				} else {
					Object value = model.get(property);
					if (value == null) {
						text = "[empty]";
					} else if (value instanceof Integer) {
						int counts = (Integer)value;
						text = counts + " " + config.getType();
						if (counts > 1) {
							text = text + "s";
						}
					} else if (value instanceof String) {
						text = (String)value;
					} else {
						text = value.toString();
					}
				}
				cfg.css = "x-grid3-url-col-td";
				if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
					return "<div class='x-grid3-ci-col x-grid3-ci-" + getId() + "'>" +
					image + "<a href='javascript:void()'>" + text + "</a>" +
					"</div>";
				} else {
					return(image +  text);
				}
			}
		});
	}

	protected CellEditor getCIEditor() {
		if (this.config == null) {
			return(null);
		}
		GroupTableWidget table = new GroupTableWidget((String)this.config.get("tableName"), this.config.getId(), this.config.getGroupDescription());
		AdaptableTriggerField<CIModel> combo = new AdaptableTriggerField<CIModel>(table, "");
		/*
		final ComboBox<CIModel> combo = new ComboBox<CIModel>();
		combo.setPageSize(20);
		combo.setMinListWidth(250);
		combo.setWidth(250);
		combo.setTriggerAction(TriggerAction.ALL);
		CIModel type = new CIModel();
		type.setAlias(config.getType());
		CIProxy proxy = new CIProxy(config.getMDR(), type, config.isSelectTemplates());
		
		BasePagingLoader loader = new BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<CIModel>>(proxy);
		ListStore<CIModel> store = new ListStore<CIModel>(loader);
		combo.setStore(store);
		combo.setTypeAhead(true);
		combo.setSimpleTemplate("{" + CIModel.CI_NAME_AND_ICON + "}");
		combo.setDisplayField(CIModel.CI_NAME_AND_ICON);
		*/
		
		CellEditor editor = new CellEditor(combo) {
			@Override  
			public Object preProcessValue(Object value) {  
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
	    String cls = ge.getTarget().getParentElement().getClassName();
	    if (cls == null || cls.indexOf("x-grid3-ci-" + getId()) < 0) {
	    	return;
	    }
	    
	    ge.stopEvent();

	    int index = grid.getView().findRowIndex(ge.getTarget());
	    ModelData m = grid.getStore().getAt(index);
	  
	    int col = grid.getView().findCellIndex(ge.getTarget(), null);

	    ColumnConfig colModel = grid.getColumnModel().getColumn(col);

	    System.out.println("GOT Model: " + m);
	    
	    GroupTableWidget grid = new GroupTableWidget((String)this.config.get("tableName"), (String)config.get("object"), config.getGroupDescription());
	    grid.setValue(m);
	    grid.setPermission(this.permissions);
	    grid.setSize(400, 300);
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
	    
	}

	private void openPropertyWindow(String displayName, CIModel model) {
		CIModelCollection col = new CIModelCollection();
		col.addCIModel("offspring", model);
		final CIPropertyPanel panel = new CIPropertyPanel(config.getMDR(), col, "Root");
		panel.setPermissions(permissions);
		Window w = WindowFactory.getWindow("Properties for " + displayName, panel);
		//w.add(new CIValueForm(gridConfig, store, data));
		w.show();
		w.layout();
		w.toFront();
	}


}
