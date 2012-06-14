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
package org.onecmdb.ui.gwt.desktop.client.widget.grid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.control.CIGridProxy;
import org.onecmdb.ui.gwt.desktop.client.control.GridModelConfigLoader;
import org.onecmdb.ui.gwt.desktop.client.fixes.DebugCheckBoxSelectionModel;
import org.onecmdb.ui.gwt.desktop.client.fixes.MyGroupingView;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.AdaptableMenu;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.AdaptableTriggerField;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.KeyEnterEvent;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.SelectContentPanel;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ColumnFilter;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.ExpressionHandler;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CITemplateBrowser;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel.CellSelection;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class EditableCIInstanceGrid extends LayoutContainer {

	protected List<AttributeModel> attributes;
	private ContentData mdr;
	private ContentData gridData;
	private GridModelConfig gridConfig;
	//private CIModel model;
	private GroupingStore<CIModelCollection> store;
	private GridQueryLoader loader;
	/*
	private boolean readonly;
	private boolean deletable;
	private boolean classify = false;
	*/
	private boolean selectable = false;
	private SelectionMode selectionMode = SelectionMode.MULTI;
	private Listener<SelectionChangedEvent> selectionListener;
	private String header;
	private boolean headerVisible;
	private MenuItem contextMenuItem;
	private Listener menuListener;
	private ColumnFilter columnFilter;
	private String query;
	private INewInstance newInstanceCallback;
	private HashMap<CIModel, List<ModelItem>> moveTable;
	private String templatePath;
	private int pageSize = 50;
	private CMDBPermissions permissions;
	private boolean silentInfo;

	private String rootCI = "Ci";
	protected boolean selectNewTemplate = false;
	

	public EditableCIInstanceGrid(ContentData mdr, ContentData gridData, String header) {
		this.mdr = mdr;
		this.gridData = gridData;
		this.header = header;
	}
	
	public boolean isSelectNewTemplate() {
		return selectNewTemplate;
	}

	public void setSelectNewTemplate(boolean selectNewTemplate) {
		this.selectNewTemplate = selectNewTemplate;
	}

	public boolean isSilentInfo() {
		return silentInfo;
	}

	public void setSilentInfo(boolean silentInfo) {
		this.silentInfo = silentInfo;
	}

	public void setRootCI(String root) {
		this.rootCI = root;
	}
	
	public void setColumnFilter(ColumnFilter columnFilter) {
		this.columnFilter = columnFilter;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setSelectable(boolean value) {
		this.selectable = value;
	}
	public void setSelectionMode(SelectionMode mode) {
		this.selectionMode = mode;
	}
	
	public void setSelectionListener(Listener<SelectionChangedEvent> selectionListener) {
		this.selectionListener = selectionListener;
	}

	/*
	public void setReadonly(boolean value) {
		this.readonly = value;
	}
	
	
	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
		if (this.deletable) {
			setSelectable(true);
		}
	}
	*/
	public void setHeaderVisible(boolean value) {
		this.headerVisible = value;
	}

	public void setContextMenuItem(MenuItem item) {
		this.contextMenuItem = item;
	}
	
	public void setMenuListener(Listener<MenuEvent> menuListener) {
		this.menuListener = menuListener;
	}

	public INewInstance getNewInstanceCallback() {
		return newInstanceCallback;
	}

	public void setNewInstanceCallback(INewInstance newInstanceCallback) {
		this.newInstanceCallback = newInstanceCallback;
	}

	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		loadGridConfig();
	}
	
	protected void loadGridConfig() {
		
		new GridModelConfigLoader(mdr, gridData, permissions).load(new AsyncCallback<GridModelConfig>() {

			public void onFailure(Throwable arg0) {
				ExceptionErrorDialog.showError("Problem loading grid config", arg0);
			}

			public void onSuccess(GridModelConfig arg0) {
				if (query != null) {
					arg0.setQuery(query);
				}
				EditableCIInstanceGrid.this.gridConfig = arg0;
				init(arg0.getColumns());
			}
		});
	}
	
	protected void init(List<ColumnConfig> cols) {
		setLayout(new FillLayout());
		
		CIGridProxy proxy = new CIGridProxy();
		
		
		loader = new GridQueryLoader(proxy, gridConfig);  
		loader.setRemoteSort(true);  
		
		// Setup the load config.
		BasePagingLoadConfig loadConfig = new BasePagingLoadConfig();
		loadConfig.set("query", gridConfig.getQuery());
		loadConfig.setOffset(0);
		loadConfig.setLimit(pageSize);
		((BasePagingLoader)loader).setReuseLoadConfig(true);
		((BasePagingLoader)loader).useLoadConfig(loadConfig);
	
		
		final GridControllToolBar topToolBar = new GridControllToolBar(gridConfig);
		topToolBar.bind(loader, loadConfig);
		//final SearchPagingToolBar pageToolBar = new SearchPagingToolBar(pageSize, gridConfig);
		//pageToolBar.bind(loader);
		
		store = new GroupingStore<CIModelCollection>(loader);
		store.clearGrouping();
		Comparator<Object> comparator = new Comparator<Object>() {
			private String getCompareValue(Object value) {
				if (value == null) {
					return("");
				}
				if (value instanceof ValueModel) {
					String text = ((ValueModel)value).getValueDisplayName();
					if (text == null) {
						text = "";
					}
					return(text);
				}
				return(value.toString());
			}
			
			public int compare(Object o1, Object o2) {
				String v1 = getCompareValue(o1);
				String v2 = getCompareValue(o2);
				return(v1.compareTo(v2));
			}
			
		};
		store.setStoreSorter(new StoreSorter<CIModelCollection>(comparator));
		//store.setRemoteGroup(true);
		
		ContentPanel cp = new ContentPanel();
		cp.setHeading(header);
		cp.setFrame(true);
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(headerVisible);
		
		if (columnFilter != null) {
			cols = columnFilter.handleColumnFilter(cols);
		}
		
		CheckBoxSelectionModel<CIModelCollection> sm = null;
		if (this.selectable) {
			sm = new DebugCheckBoxSelectionModel<CIModelCollection>();
			sm.setSelectionMode(selectionMode);
			if (this.selectionListener != null) {
				sm.addListener(Events.SelectionChange, this.selectionListener); 			
			}
			cols.add(0, sm.getColumn());			
		}
		
		final ColumnModel cm = new ColumnModel(cols);
		
		Grid<CIModelCollection> grid = null;
		
		
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT)) {
			grid = new EditorGrid<CIModelCollection>(store, cm);
			/*
			for (ColumnConfig cfg : cols) {
				cfg.setStyle("border-right-width:1px;border-right-color:#EDEDED;border-right-style:solid;");
			}
			*/
			if (selectable) {
				((EditorGrid)grid).setClicksToEdit(ClicksToEdit.TWO);
			} else {
				((EditorGrid)grid).setClicksToEdit(ClicksToEdit.ONE);
			}
		} else {
			grid = new Grid<CIModelCollection>(store, cm);
			if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
				grid.setSelectionModel(new CellSelectionModel<CIModelCollection>());
			}
		} 		
		
		/*
		if (gridConfig.getAutoExpandColumnId() != null) {
			grid.setAutoExpandColumn(gridConfig.getAutoExpandColumnId());
		}
		*/
		
		grid.setContextMenu(getGridContextMenu(grid, cols));
		
		// Add support for draging this to another component.
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.CLASSIFY)) {
			new GridDragSource(grid);
		}
		
		MyGroupingView view = new MyGroupingView();
		view.setForceFit(false);
		
		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(GroupColumnData data) {
				String f = cm.getColumnById(data.field).getHeader();
				String l = data.models.size() == 1 ? "Item" : "Items";
				return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
			}
		});
		grid.setView(view);
		grid.setBorders(true);
		grid.setLoadMask(true);
		
	
		if (selectable) {
			grid.setSelectionModel(sm);
			grid.addPlugin(sm);
		} 
		for (ColumnConfig cfg : cols) {			
			if (cfg instanceof ComponentPlugin) {
				grid.addPlugin((ComponentPlugin)cfg);
			}
		}
		cp.add(grid);
		
		ToolBar toolBar = new ToolBar();
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.DELETE)) {		
			// Delete toolbar.	
			TextToolItem remove = new TextToolItem("Delete", "delete-icon");
			final Grid<CIModelCollection> finalGrid = ((Grid)grid);
			remove.addSelectionListener(new SelectionListener<ToolBarEvent>() {
				@Override
				public void componentSelected(ToolBarEvent ce) {
					final List<CIModelCollection> items = finalGrid.getSelectionModel().getSelectedItems();
					MessageBox.confirm("Delete", "Delete " + items.size() +" objects?", new Listener<WindowEvent>() {
						public void handleEvent(WindowEvent be) {
							//Dialog dialog = (Dialog) ce.component;  
							Button btn = be.buttonClicked;
							if (btn.getItemId().equals(Dialog.YES)) {
								doDelete(items);	
							} 
						}
					});
				}
			});
			//toolBar.add(add);
			topToolBar.add(remove);
			topToolBar.add(new SeparatorToolItem());
		}
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT)) {		
			if (gridConfig.isSupportAddRow()) {
					final TextToolItem add = new TextToolItem("Add", "add-icon");
					final EditorGrid<CIModelCollection> editGrid = ((EditorGrid)grid);
					add.addSelectionListener(new SelectionListener<ToolBarEvent>() {
						@Override
						public void componentSelected(ToolBarEvent ce) {
							if (selectNewTemplate) {
								selectNewTemplate(editGrid, add.getElement());

							} else {
								addNewItem(editGrid, gridConfig.getNewModel());
							}
						}
					});
					//toolBar.add(add);
					topToolBar.add(add);
					topToolBar.add(new SeparatorToolItem());
			}
		
			TextToolItem save = new TextToolItem("Save", "save-icon");
			save.addSelectionListener(getSaveAction());

			TextToolItem undo = new TextToolItem("Undo", "restore-icon");
			undo.addSelectionListener(getUndoAction());
			topToolBar.add(undo);
			topToolBar.add(save);
		}
		
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.CLASSIFY)) {		
			TextToolItem save = new TextToolItem("Commit", "save-icon");
			save.addSelectionListener(getCommitMoveAction());
			TextToolItem undo = new TextToolItem("Undo", "restore-icon");
			undo.addSelectionListener(getUndoMoveAction());
			topToolBar.add(undo);
			topToolBar.add(save);
		}
		
		/*
		if (readonly) {
		 	grid.addListener(Events.RowDoubleClick, getPropertySelection(grid));
		}	
		*/
		
		topToolBar.add(new SeparatorToolItem());
		final CheckBox check = new CheckBox();
		check.setBoxLabel("All children");
		check.setToolTip("If checked, ALL instances that is derived<br/> " +
				"from the selected template is shown.<br/>" +
				"Else instances directly derived from <br/>" + 
				"the selected template is shown"
				);
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.CLASSIFY)) {
			check.setValue(Boolean.FALSE);
			check.setEnabled(false);
		} else {
			check.setValue(Boolean.TRUE);
		}
		topToolBar.getLoadConfig().set("limitToChild", !check.getValue());
		
		AdapterToolItem allInstances = new AdapterToolItem(check);
		topToolBar.add(allInstances);
		check.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent be) {
				topToolBar.getLoadConfig().set("limitToChild", !check.getValue());
				//pageToolBar.getLoadConfig().setAllChildren(check.getValue());
			}
		});
		
		
		cp.setTopComponent(topToolBar);
		
		PagingToolBar paging = new PageSizePagingToolBar(this.pageSize);
		paging.bind(loader);
		cp.setBottomComponent(paging);
		
		add(cp);
		layout();
		loader.load();
  }

	protected void addNewItem(EditorGrid<CIModelCollection> editGrid, CIModel newModel) {
		CIModelCollection model = null;
		if (newInstanceCallback != null) {
			model = newInstanceCallback.createInstance(gridConfig);
		} else {
			model = gridConfig.createNewInstance(newModel);
		}
		editGrid.stopEditing();
		// Disable Store Filter when we add.
		StoreSorter sorter = store.getStoreSorter();
		store.setStoreSorter(null);
		store.insert(model, 0);
		store.setStoreSorter(sorter);
		editGrid.startEditing(0, 1);

		
	}

	protected void selectNewTemplate(final EditorGrid<CIModelCollection> editGrid, Element target) {
		List<String> types = new ArrayList<String>();
		types.add(gridConfig.getNewModel().getAlias());
		CITemplateBrowser template = new CITemplateBrowser(gridConfig.getMDR(), types);
		//template.setCheckable(true, null);
		final SelectContentPanel<CIModel> sel = new SelectContentPanel<CIModel>("Select a template", template);
		final AdaptableMenu menu = new AdaptableMenu(sel, "");
		
		
		menu.addListener(Events.Select, new Listener<ComponentEvent>() {
	        public void handleEvent(ComponentEvent ce) {
	          menu.hide();
	          CIModel model = sel.getValue();  
	          addNewItem(editGrid, model);
	        }
	      });
	      menu.addListener(Events.Hide, new Listener<ComponentEvent>() {
	        public void handleEvent(ComponentEvent be) {
	          menu.hide();
	        }
	      });
	      
	      menu.show(target, "tl-bl?");
		
	}

	private SelectionListener getUndoMoveAction() {
		return(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				doUndo(true);
			}
		});
	}

	private SelectionListener getCommitMoveAction() {
		return(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				final List<CIModel> base = new ArrayList<CIModel>();
				final List<CIModel> local = new ArrayList<CIModel>();
				
				for (CIModel model : moveTable.keySet()) {
					List<ModelItem> list = moveTable.get(model);
					for (ModelItem item : list) {
						if (item instanceof CIModelCollection) {
							CIModelCollection col = (CIModelCollection)item;
							if (col.getCIModels().size() == 1) {
								CIModel m = col.getCIModels().get(0);
								CIModel copy = m.copy();
								copy.setDerivedFrom(model.getAlias());
								local.add(copy);
								base.add(m);
							}
						}
					}
				}
				// Call update...
				MessageBox.confirm("Confirm", "Move " + 
						local.size() + 
						" instances!" + 
						"<br>Are you sure?", new Listener<WindowEvent>() {
					
					public void handleEvent(WindowEvent ce) {
						Dialog dialog = (Dialog) ce.component;  
						Button btn = dialog.getButtonPressed();  
						if (btn.getItemId().equals(Dialog.YES)) {
							doMove(local, base);
						}
					}

				});
			}
		});
	}

	private void doUndo(boolean addToStore) {
		if (moveTable != null) {
			CIModel aModel = null;
			for (CIModel model : moveTable.keySet()) {
				List<ModelItem> list = moveTable.remove(model);
				if (addToStore) {
					for (ModelItem item : list) {
						if (item instanceof CIModelCollection) {
							store.add((CIModelCollection)item);
						}
					}
				}
				// Update Reload....
				boolean b = model.get("updateFlag", false);
				model.set("updateFlag", !b);
				aModel = model;
			}
			if (!addToStore && aModel != null) {
				aModel.set("reloadFlag", true);
			}
			moveTable.clear();
		}
	}
	
	private void doMove(List<CIModel> local, List<CIModel> base) {
		final MessageBox saveInfo = MessageBox.wait("Progress",  
	             "Move instances", "Moving..."); 
		
		ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

			public void onFailure(final Throwable caught) {
				// Error.
				saveInfo.close();
				ExceptionErrorDialog.showError("Can't Move", caught, new Listener() {

					public void handleEvent(BaseEvent be) {
						doUndo(true);
					}
				});
			}

			public void onSuccess(final StoreResult result) {
				saveInfo.close();
				// saved
				if (result.isRejected()) {
					MessageBox.alert("Save Failed", result.getRejectCause(), new Listener<WindowEvent>() {
						public void handleEvent(WindowEvent be) {
							doUndo(true);
						}
					});
					return;
				} else {
					doUndo(false);
					loader.load();
				}
			}

		});
	}



	private SelectionListener<ComponentEvent> getUndoAction() {
		return(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				// Remove all new Objects...
				for (CIModelCollection col : store.getModels()) {
					if (col.isNewCollection()) {
						store.remove(col);
					}
				}
				store.rejectChanges();
			}
		});
	}

	private void doDelete(List<CIModelCollection> delete) {
		final MessageBox deleteInfo = MessageBox.wait("Progress",  
	             "Deleting your data, please wait...", "Deleting...");  
		List<CIModelCollection> base = new ArrayList<CIModelCollection>();
			
		
		// Do store on modfied.
		ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), base, delete, new AsyncCallback<StoreResult>() {

			public void onFailure(Throwable caught) {
				deleteInfo.close();

				ExceptionErrorDialog.showError("Can't Delete", caught);
				//loader.load();
			}

			public void onSuccess(StoreResult result) {
				deleteInfo.close();
				if (result.isRejected()) {
					MessageBox.alert("Delete Failed", result.getRejectCause(), null);
					return;
				}
				loader.load();
			}
		});
	}
		
	private SelectionListener getSaveAction() {
		return(new SelectionListener<ComponentEvent>() {

		
			@Override
			public void componentSelected(ComponentEvent ce) {
				MessageBox info = null;
			
				if (!silentInfo) {
					info = MessageBox.progress("Progress",  
			             "Saving your data, please wait...", "Saving...");  
				}
				final MessageBox saveInfo = info;
				final List<CIModelCollection> base = new ArrayList<CIModelCollection>();
				final List<CIModelCollection> local = new ArrayList<CIModelCollection>();
						
				Listener<StoreEvent> listener = new Listener<StoreEvent>() {

					public void handleEvent(StoreEvent be) {
						if (be.operation == RecordUpdate.COMMIT) {
							if (be.model instanceof CIModelCollection) {
								CIModelCollection item = (CIModelCollection) be.model;
								
								if (item.isNewCollection()) {
									return;
								}
							
								local.add((CIModelCollection) be.model);
							}
						}
					}
				};
				
				List<Record> records = store.getModifiedRecords();
				for (Record record : records) {
					CIModelCollection item = (CIModelCollection) record.getModel();
					if (item.isNewCollection()) {
						continue;
					}
					CIModelCollection old = item.copy();
					Map<String, Object> original = record.getChanges();
					for (String p : original.keySet()) {
						Object v = original.get(p);
						/*
						if (v instanceof ValueModel) {
							// Don't add added attributes to the base.
							if (((ValueModel)v).getIdAsString() == null) {
								continue;
							}
						}
						*/
				        old.set(p, original.get(p));
				    }
					base.add(old);
				}
				
				store.addListener(Store.Update, listener);
				store.commitChanges();
				store.removeListener(Store.Update, listener);
				
				for (CIModelCollection col : store.getModels()) {
					if (col.isNewCollection()) {
						local.add(col);
					}
				}
				// Do store on modfied.
				ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

					public void onFailure(Throwable caught) {
						if (saveInfo  != null) {
							saveInfo.close();
						}
						
						ExceptionErrorDialog.showError("Can't Save", caught);
						//loader.load();
					}

					public void onSuccess(StoreResult result) {
						if (saveInfo  != null) {
							saveInfo.close();
						}
						if (result.isRejected()) {
							MessageBox.alert("Save Failed", result.getRejectCause(), null);
							return;
						}
						loader.load();
					}
				});
			}
		});
	}

	private  SelectionListener<ComponentEvent> getPropertySelection(final Grid grid) {
		SelectionListener<ComponentEvent> selection = new SelectionListener<ComponentEvent>() {

			
			@Override
			public void handleEvent(ComponentEvent ce) {
				super.handleEvent(ce);
				 if (ce.type == Events.RowDoubleClick) {
					 componentSelected(ce);
				 }
			}

			@Override
			public void componentSelected(ComponentEvent ce) {
				
				GridSelectionModel<ModelData> selectionModel = (GridSelectionModel<ModelData>) grid.getSelectionModel();
				ModelData data = selectionModel.getSelectedItem();
				if (selectionModel instanceof CellSelectionModel) {
					data = ((CellSelectionModel)selectionModel).getSelectCell().model;
				}
				if (!(data instanceof CIModelCollection)) {
					return;
				}
				final CIModelCollection dataCol = (CIModelCollection)data;
				
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						final CIPropertyPanel panel = new CIPropertyPanel(mdr, dataCol, rootCI);
						panel.setPermissions(permissions);
						Window w = WindowFactory.getWindow("Properties for " + dataCol.getCIModels().get(0).getDisplayName(), panel);
						//w.add(new CIValueForm(gridConfig, store, data));
						w.show();
						w.layout();
						w.toFront();
						w.addListener(Events.Close, new Listener<BaseEvent>() {

							public void handleEvent(BaseEvent be) {
								if (panel.isModelChanged()) {
									loader.load();
								}
							}
							
						});
					}
				});
			}
			
		};
		return(selection);
	}
	
	private Menu getGridContextMenu(final Grid grid, final List<ColumnConfig> cols) {
		final Menu menu = new Menu();
		MenuItem item = new MenuItem("Properties", "property-icon");
		item.addSelectionListener(getPropertySelection(grid));
		
		menu.add(item);
		
		final MenuItem open = new MenuItem("Open in Browser", "open-icon");
		open.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				String url = open.getData("url");
				com.google.gwt.user.client.Window.open(url,"OneCMDB_URL", "");
			}
			
		});
		open.setVisible(false);
		menu.add(open);
		
		if (contextMenuItem != null) {
			menu.add(new SeparatorMenuItem());
			menu.add(contextMenuItem);
		}
		if (this.menuListener != null) {
			menu.addListener(Events.BeforeShow, this.menuListener);
		}
		menu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				open.setVisible(false);
				GridSelectionModel<ModelData> selectionModel = (GridSelectionModel<ModelData>) grid.getSelectionModel();
				ModelData data = selectionModel.getSelectedItem();
				if (selectionModel instanceof CellSelectionModel) {
					
					CellSelection cell = ((CellSelectionModel)selectionModel).getSelectCell();
					if (cell == null) {
						return;
					}
					
					if (gridConfig.getColumnConfig() == null) {
						return;
					}
					if (cell.cell < 0 || cell.cell >= gridConfig.getColumnConfig().size()) {
						return;
					}
					AttributeColumnConfig cfg = gridConfig.getColumnConfig().get(cell.cell);
					if (cfg == null) {
						return;
					}
					if (cell.model == null) {
						return;
					}
					
					if ("xs:anyURI".equals(cfg.getType())) {
						open.setVisible(true);
						Object value = cell.model.get(cfg.getId());
						String url = "";
						if (value instanceof ValueModel) {
							url = ((ValueModel)value).getValue();
							url = ExpressionHandler.replaceURL(url);
						}
						open.setData("url", url);
					}
				}
				
				
	
			}
			
		});
		
		return(menu);
	}

	public void setQuery(String query) {
		this.query = query;
		
	}
	/*
	public void setClassify(boolean classify) {
		this.classify = classify;
		
	}
	*/
	public void setMoveTable(HashMap<CIModel, List<ModelItem>> moveTable) {
		this.moveTable = moveTable;
		
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public void setPermissions(CMDBPermissions permissions) {
		this.permissions = permissions;
		if (this.permissions.getCurrentState().equals(CMDBPermissions.PermissionState.DELETE)) {
			setSelectable(true);
		}
		
	}

	public void reload() {
		this.loader.load();
	}

}
