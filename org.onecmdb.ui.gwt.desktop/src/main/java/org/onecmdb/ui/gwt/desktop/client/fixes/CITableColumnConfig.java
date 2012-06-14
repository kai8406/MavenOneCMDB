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
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.AdaptableMenu;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.AdaptableTriggerField;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.IValueComponent;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.SelectContentPanel;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.ValueContentPanel;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyPanel;
import org.onecmdb.ui.gwt.desktop.client.widget.multi.MultiSelectCI;
import org.onecmdb.ui.gwt.desktop.client.widget.multi.SingleSelectCI;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class CITableColumnConfig extends ColumnConfig implements ComponentPlugin {

	private Grid grid;
	private AttributeColumnConfig config;
	private CMDBPermissions permissions;

	public CITableColumnConfig() {
		super();
		init();
	}
	public CITableColumnConfig(AttributeColumnConfig config, CMDBPermissions perm) {
		super();
		this.config = config;
		this.permissions = perm;
		init();
	}
	public CITableColumnConfig(String id, String name, int width) {
		super(id, name, width);
		init();
	}

	public void setPermissions(CMDBPermissions perm) {
		this.permissions = perm;
	}

	protected void init() {
		setEditor(getCIEditor());
		setRenderer(new GridCellRenderer() {


			public String render(ModelData model, String property, ColumnData config, int rowIndex,
					int colIndex, ListStore store) {
				String text = "";
				String image = "";
				if (model instanceof CIModelCollection) {
					String split[] = property.split("\\.");
					String name = split[0];
					String attr = split[1];

					CIModel ci = ((CIModelCollection)model).getCIModel(name);
					ValueModel item = ci.get(attr);
					if (item == null) {
						return("");
					}
					text = item.getValue();
					if (text == null) {
						return("");
					}
					
					if (item.isComplex()) {
						text = item.getValueDisplayName();
						if (item.getValue() != null) {
							String url = item.get(CIModel.CI_ICON_PATH);
							if (url != null) {
								url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
								image= "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>";
							}
						}
					}
				}
				if (model instanceof ValueModel) {
					ValueModel item = (ValueModel)model;
					text = item.getValue();
					if (text == null) {
						return("");
					}
					
					if (item.isComplex()) {
						text = item.getValueDisplayName();
						if (item.getValue() != null) {
							String url = item.get(CIModel.CI_ICON_PATH);
							if (url != null) {
								url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
								image= "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>";
							}
						}
					}
				}
				config.css = "x-grid3-url-col-td";
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

	protected Component getSelectionMenu() {
		//SelectionMode selMod = SelectionMode.MULTI;
		CIModel type = new CIModel();
		type.setAlias(config.getType());
		type.setTemplate(true);
		SingleSelectCI select = new SingleSelectCI(config.getMDR(), type, permissions);
		SelectContentPanel<CIModel> selCp = new SelectContentPanel<CIModel>("Select 1 " + config.getType(), select);
		return(selCp);
	}
	
	protected CellEditor getCIEditor() {
		if (this.config == null) {
			return(null);
		}
		
		AdaptableTriggerField<CIModel> field = new AdaptableTriggerField<CIModel>(getSelectionMenu(), "");
	
		field.setPropertyEditor(new PropertyEditor<CIModel>() {

			public CIModel convertStringValue(String value) {
				return null;
			}

			public String getStringValue(CIModel value) {
				if (value == null) {
					return("");
				}
				return(value.getDisplayName());
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
		CellEditor editor = new CellEditor(field) {
			@Override  
			public Object preProcessValue(Object value) {  
				
				System.out.println("PreProcess:" + value);
				if (value == null) {  
					return value;  
				}
				
				if (value instanceof ValueModel) {
					CIModel model = new CIModel();
					model.setAlias(((ValueModel)value).getValue());
					model.setDisplayName(((ValueModel)value).getValueDisplayName());
					model.set(CIModel.CI_ICON_PATH, ((ValueModel)value).get(CIModel.CI_ICON_PATH));
					return(model);
				}
				
				if (value instanceof List) {
					List l = (List)value;
					if (l.size() == 0) {
						return(null);
					}
					return(l.get(0));
				}
				if (value instanceof CIModel) {
					return(value);
				}
				return value.toString();  
			}  

			@Override  
			public Object postProcessValue(Object value) {  
				System.out.println("PostProcess:" + value);
				return(value);
			}  
		};
		editor.setCancelOnEsc(true);
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
	    if (m instanceof CIPropertyGrid.ValueWrapper) {
	    	  m = ((CIPropertyGrid.ValueWrapper)m).getModel();
	    }
	  
	    int col = grid.getView().findCellIndex(ge.getTarget(), null);

	    ColumnConfig colModel = grid.getColumnModel().getColumn(col);

	    String property = colModel.getDataIndex();
	    Object value = m.get(property);

	    if (value instanceof ValueModel) {
	    	final ValueModel vModel = (ValueModel)value;
	    	if (vModel.isComplex()) {
	    		if (vModel.getValue() == null) {
	    			return;
	    		}
	    		
	    		fireEvent(CMDBEvents.POPUP_HIDE_EVENT);
	    		
	    		// Open property for this alias.
	    		DeferredCommand.addCommand(new Command() {
	    			public void execute() {
	    				// Need to load the alias....
	    				String alias = vModel.getValue();
	    				List<String> array = new ArrayList<String>();
	    				array.add(alias);

	    				ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), config.getMDR(), array, new CMDBAsyncCallback<List<CIModel>>() {

	    					@Override
	    					public void onSuccess(List<CIModel> arg0) {
	    						if (arg0 == null || arg0.size() > 0) {
	    							openPropertyWindow(vModel.getValueDisplayName(), arg0.get(0));
	    						}
	    					}

	    				});
	    			}
	    		});
	    	}
	    }
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
