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
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.AdaptableTriggerField;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.SelectContentPanel;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModelList;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyPanel;
import org.onecmdb.ui.gwt.desktop.client.widget.group.table.GroupTableWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CITemplateBrowser;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class CITemplateColumnConfig extends ColumnConfig implements ComponentPlugin {

	private Grid grid;
	private AttributeColumnConfig config;
	private CMDBPermissions permissions;

	public CITemplateColumnConfig() {
		super();
		init();
	}
	public CITemplateColumnConfig(AttributeColumnConfig config) {
		super();
		this.config = config;
		init();
	}
	public CITemplateColumnConfig(String id, String name, int width) {
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
				if (model instanceof CIModel) {
					text = ((CIModel)model).getNameAndIcon();
				} else if (model instanceof AttributeModel) {
					Object o = model.get(property);
					if (o == null) {
						return("");
					}
					if (o instanceof String) {
						return((String)o);
					}
					if (o instanceof Boolean) {
						return(o.toString());
					}
					if (o instanceof CIModel) {
						CIModel m = (CIModel)o;
						text = m.getAlias();
						if (text == null) {
							text = m.getDisplayName();
						}
						String url = m.get(CIModel.CI_ICON_PATH);
						if (url != null) {
							url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
							text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
						}
					}
				} else {
					Object value = model.get(property);
					text = EditorFactory.renderObject(value);
				}
				return(text);
			}
		});
	}

	protected CellEditor getCIEditor() {
		if (this.config == null) {
			return(null);
		}
		List<String> types = new ArrayList<String>();
		types.add(this.config.getType());
		CITemplateBrowser template = new CITemplateBrowser(this.config.getMDR(), types);
		//template.setCheckable(true, null);
		SelectContentPanel<CIModel> sel = new SelectContentPanel<CIModel>("Select a template", template);
		AdaptableTriggerField<CIModel> combo = new AdaptableTriggerField<CIModel>(sel, "");
		combo.setPropertyEditor(new PropertyEditor<CIModel>() {

			public CIModel convertStringValue(String value) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getStringValue(CIModel value) {
				if (value == null) {
					return("");
				}
				return(value.getAlias());
			}
			
		});
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
			grid.addListener(Events.CellClick, new Listener<GridEvent>() {
				public void handleEvent(GridEvent e) {
					onMouseDown(e);
				}
			});
		}
	}
	
	protected void onMouseDown(GridEvent ge) {
		return;
	}

}
