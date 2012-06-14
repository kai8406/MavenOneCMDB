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
package org.onecmdb.ui.gwt.desktop.client.widget.multi;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.fixes.combo.AdaptableMenu;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.Window.CloseAction;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class MultiValueGrid extends LayoutContainer {
	
	private ValueListModel values;
	private ListStore<ValueModel> store;
	private EditorGrid<ValueModel> grid;
	private List<ValueModel> newValues;
	private AttributeColumnConfig config;
	private CMDBPermissions permissions;
	private boolean cancel;

	
	public MultiValueGrid(AttributeColumnConfig config) {
		this.config = config;
		setSize(340, 400);
	}
	
	public void setValue(ValueListModel model) {
		if (model != null) {
			this.values = model.copy();
		} else {
			this.values = new ValueListModel();
			this.values.setIsComplex(config.isComplex());
		}
		updateStore();
	}
	
	public CMDBPermissions getPermissions() {
		return permissions;
	}

	public void setPermissions(CMDBPermissions permissions) {
		this.permissions = permissions;
	}

	protected void updateStore() {
		if (store != null) {
			store.commitChanges();
			store.removeAll();
			if (this.values != null) {
				List<ValueModel> list = this.values.getValues();
				for (ValueModel vModel : list) {
					vModel.set("delete", false);	
				}
				if (list.size() > 0) {
					store.add(list);
				}
			}
		}
		if (grid != null) {
			grid.getView().refresh(true);
		}
	}

	public ValueListModel getValue() {
		return(this.values);
	}
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		initUI();
	}

	private void initUI() {
		setLayout(new FitLayout());
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		newValues = new ArrayList<ValueModel>();
		
		/*
		
		*/
		configs.add(new RowNumberer());  
		if (!isReadonly()) { 
			CheckColumnConfig checkColumn = new CheckColumnConfig("delete", "Delete", 50);  
			configs.add(checkColumn);  		
		}
		ColumnConfig column = EditorFactory.getColumnConfig(config, true, permissions); 
		column.addListener(CMDBEvents.POPUP_HIDE_EVENT, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				fireEvent(CMDBEvents.POPUP_HIDE_EVENT);
			}
			
		});
		column.setId("this");
		//column.setWidth(100);
		//column.setEditor(new CellEditor());
		/*
		column.setRenderer(new GridCellRenderer<ValueModel>() {

			public String render(ValueModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ValueModel> store) {
				
				String text = model.getValue();
				if (model.isComplex()) {
					text = model.getValueDisplayName();
					if (model.getValue() != null) {
						String url = model.get(CIModel.CI_ICON_PATH);
						if (url != null) {
							url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
							text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
						}
					}
				}
				return(text);
			}
		});
		*/
		//configs.add(new RowNumberer());  
		/*
		valueColumn.setId("value");  
			valueColumn.setHeader("Value");  
		valueColumn.setWidth(200);  
		*/
		configs.add(column);  

		
		
		
		ColumnModel cm = new ColumnModel(configs);  
		
		store = new ListStore<ValueModel>();  
		store.setMonitorChanges(true);
		updateStore();
		
		grid = new EditorGrid<ValueModel>(store, cm);  
		for (ColumnConfig cfg : configs) {
			if (cfg instanceof ComponentPlugin) {
				grid.addPlugin((ComponentPlugin)cfg);
			}
		}
		grid.setBorders(true);
		//grid.setAutoExpandColumn("this");  
		
		ContentPanel cp = new ContentPanel();
		cp.setLayout(new FitLayout());
		cp.setLayoutOnChange(true);
		if (!isReadonly()) {
			cp.setTopComponent(getTopToolBar());
		}
		cp.add(grid);
		cp.setHeading("Select " + config.getType() + "s");
		//cp.setHeaderVisible(false);
		add(cp);
		layout();
		
	}

	private boolean isReadonly() {
		return(permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY));
	}

	private void addValues(List objects) throws Exception {
		
		for (Object o : objects) {
			CIModel ci = null;
			ValueModel vModel = null;
			if (o instanceof ValueModel) {
				vModel = (ValueModel)o;
			}
			if (o instanceof CIModelCollection) {
				CIModelCollection col = (CIModelCollection)o;
				o = col.getCIModel("offspring");
			}
			if (o instanceof CIModel) {
				ci = (CIModel)o;
				// Check if existsing ...
				boolean exists = false;
				for (ValueModel availValue : store.getModels()) {
					if (ci.getAlias().equals(availValue.getValue())) {
						exists = true;
						break;
					}
				}
				if (exists) {
					continue;
				}
				vModel = new ValueModel();
				vModel.setAlias(values.getAlias());
				vModel.setIsComplex(true);
				vModel.setValue(ci.getAlias());
				vModel.setValueDisplayName(ci.getDisplayName());
				vModel.set(CIModel.CI_ICON_PATH, ci.get(CIModel.CI_ICON_PATH));
				vModel.set("delete", false);
			}
			if (vModel == null) {
				continue;
			}
			if (this.config.getMaxOccurs() > 0) {
				if (store.getCount() >= this.config.getMaxOccurs()) {
					throw new Exception("Attribute only allows " + this.config.getMaxOccurs() + " values");
				}
			}
			newValues.add(vModel);
			grid.stopEditing();
			store.insert(vModel, 0);
		}
		grid.getView().refresh(false);
	}

	
	private ToolBar getTopToolBar() {
		
		ToolBar bar = new ToolBar();
		TextToolItem addAttribute = new TextToolItem("Add", "add-icon");
		addAttribute.addSelectionListener(getAddValueSelection());
		addAttribute.setToolTip("Add a new value");
		bar.add(addAttribute);
		
		
		
		if (this.config.isComplex()) {
			TextToolItem selectValues = new TextToolItem("Select", "select-icon");
			selectValues.setMenu(getSelectMenu());
					
			//selectValues.addSelectionListener(getSelectValueSelection());
			selectValues.setToolTip("Select multiple values");
			bar.add(selectValues);
		}
		
		new SeparatorToolItem();
		/*
		TextToolItem removeAttribute = new TextToolItem("Remove", "delete-icon");
		removeAttribute.addSelectionListener(getRemoveSelection());
		removeAttribute.setToolTip("Remove checkd values.");
		bar.add(removeAttribute);
		
		new SeparatorToolItem();
		*/
		/*
		TextToolItem restore = new TextToolItem("Undo", "restore-icon");
		restore.addSelectionListener(getUndoSelection());
		restore.setToolTip("Undo");
		bar.add(restore);
		*/
		bar.add(new FillToolItem());
		TextToolItem  save = new TextToolItem("Ok", "commit-icon");
		save.addSelectionListener(getOkSelection());
		save.setToolTip("Ok");
		bar.add(save);
		
		TextToolItem cancel = new TextToolItem("Cancel", "cancel-icon");
		cancel.addSelectionListener(getCancelSelection());
		cancel.setToolTip("Close");
		bar.add(cancel);
		
		return(bar);
	}

	private SelectionListener getCancelSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				cancel = true;
				fireEvent(Events.Select, new ComponentEvent(MultiValueGrid.this));
				//fireEvent(Events.Hide, new ComponentEvent(MultiValueGrid.this));
			}
		});
	}
	
	private SelectionListener getOkSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				
				List<ValueModel> removeValues = store.findModels("delete", true);
				for (ValueModel removeValue : removeValues) {
					newValues.remove(removeValue);
					if (config.isEditTemplate()) {
						if (values.getValues().size() == 1) {
							MessageBox.alert("Not allowed", "On template one attribute must exists!", new Listener<WindowEvent>() {

								public void handleEvent(WindowEvent be) {
									cancel = true;
									fireEvent(Events.Select, new ComponentEvent(MultiValueGrid.this));
									return;
								}
								
							});
							return;
												
						}
					}
					values.removeValue(removeValue);
				}
				store.commitChanges();
				// Add all new values.
				for (ValueModel vModel : newValues) {
					values.addValue(vModel);
				}
				
				fireEvent(Events.Select, new ComponentEvent(MultiValueGrid.this));
			}
		});
	}
	
	private SelectionListener getSelectValueSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
			 	final Window w = new Window();
			    w.setCloseAction(CloseAction.CLOSE);
			    w.setMinimizable(false);
			    w.setMaximizable(true);
			    w.setIconStyle("accordion");
			    //w.setHeading();
			    w.setWidth(600);
			    w.setHeight(400);
			    w.setLayout(new FitLayout());
	
				
				SelectionMode selMod = SelectionMode.MULTI;
				CIModel type = new CIModel();
				type.setAlias(config.getType());
				type.setTemplate(true);
				final MultiSelectCI select = new MultiSelectCI(config.getMDR(), type, selMod, null, permissions);
				ContentPanel cp = new ContentPanel();
				cp.setLayout(new FillLayout());
				cp.setHeading("Select " + config.getType() + "s");
				cp.add(select);
				
				Button cancelButton = new Button("Cancel");
				cancelButton.addSelectionListener(new SelectionListener<ComponentEvent>() {

					@Override
					public void componentSelected(ComponentEvent ce) {
						w.setVisible(false);
					}
				});
				Button selectButton = new Button("Select");
				selectButton.addSelectionListener(new SelectionListener<ComponentEvent>() {

					@Override
					public void componentSelected(ComponentEvent ce) {
						w.setVisible(false);
						List objects = select.getSelection();
						try {
							addValues(objects);
						} catch (Exception e) {
							MessageBox.alert("Can't add values", e.getMessage(), null);
						}
					}


				});
				cp.addButton(selectButton);
				cp.addButton(cancelButton);
			
			
				w.add(cp);
				w.setVisible(true);
			}
		});
	}
	
	private Menu getSelectMenu() {
		SelectionMode selMod = SelectionMode.MULTI;
		CIModel type = new CIModel();
		type.setAlias(config.getType());
		type.setTemplate(true);
		final MultiSelectCI select = new MultiSelectCI(config.getMDR(), type, selMod, getValue(), permissions);
		ContentPanel cp = new ContentPanel();
		cp.setLayout(new FillLayout());
		cp.setHeading("Select 1.." + config.getMaxOccurs() + " " + config.getType() + "s");
		cp.add(select);
		
		
		final AdaptableMenu menu = new AdaptableMenu(cp, "select-icon");
		
		
		TextToolItem cancelItem = new TextToolItem("Cancel", "cancel-icon");
		cancelItem.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				menu.hide();
			}
		});
		TextToolItem selectItem = new TextToolItem("Select", "select-icon");
		selectItem.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				List objects = select.getSelection();
				menu.hide();
				try {
					addValues(objects);
				} catch (Exception e) {
					MessageBox.alert("Can't add values", e.getMessage(), null);
				}
			}
		});
		
		ToolBar bar = new ToolBar();
		bar.add(new FillToolItem());
		bar.add(selectItem);
		bar.add(cancelItem);
		cp.setTopComponent(bar);
		return(menu);

	}
	
	private SelectionListener getUndoSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				// Check all 
				for (ValueModel newValue : newValues) {
					store.remove(newValue);
				}
				grid.getView().refresh(false);
				store.rejectChanges();
			}
		});
	}

	private SelectionListener getRemoveSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				// Check all 
				List<ValueModel> values = store.findModels("delete", true);
				for (ValueModel value : values) {
					store.remove(value);
				}
				grid.getView().refresh(false);
			}
		});
	}
	private SelectionListener getAddValueSelection() {
		return(new SelectionListener<ComponentEvent>() {
	
			@Override
			public void componentSelected(ComponentEvent ce) {
				ValueModel newValue = new ValueModel();
				newValue.setAlias(values.getAlias());
				newValue.setIsComplex(values.isComplex());
				newValue.setValueDisplayName("");
				newValue.set("delete", false);
				List<ValueModel> addValues = new ArrayList<ValueModel>();
				addValues.add(newValue);
				try {
					addValues(addValues);
				} catch (Exception e) {
					e.printStackTrace();
					Info.display("MaxOccurs Limit", "Attribute " + config.getName() + " max allows " + config.getMaxOccurs() + " values");
					//MessageBox.alert("Can't add values", e.getMessage(), null);
				}

				/*
				newValues.add(newValue);
				
				grid.stopEditing();
				store.insert(newValue, 0);
				grid.getView().refresh(false);
				grid.startEditing(0, 2);
				*/
			}	
		});
	}
	
	/**
	 * Called when opened.
	 */
	public void onOpen() {
		if (newValues != null) {
			newValues.clear();
		}
		this.values = null;
		cancel = false;
	}

	public boolean isCancel() {
		return(cancel);
	}  
}
