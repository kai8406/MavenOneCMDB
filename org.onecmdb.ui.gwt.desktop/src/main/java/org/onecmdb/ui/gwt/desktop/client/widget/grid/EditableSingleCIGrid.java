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

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.control.GridModelConfigLoader;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.ExpressionHandler;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel.CellSelection;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EditableSingleCIGrid extends LayoutContainer {
	protected List<AttributeModel> attributes;
	private ContentData mdr;
	private ContentData gridData;
	private GridModelConfig gridConfig;
	private CIModel ci;
	private ListStore<CIModelCollection> store;

	//private boolean readonly;
	
	protected CMDBPermissions permissions;
	private String rootCI;

	public EditableSingleCIGrid(ContentData mdr, CIModel instance) {
		this.mdr = mdr;
		this.ci = instance;
	}
	
	
	public void setRootCI(String root) {
		this.rootCI = root;
	}
	/*
	public boolean isReadonly() {
		return readonly;
	}



	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	*/


	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		loadGridConfig();
	}
	
	protected void loadGridConfig() {
		ContentData gridData = new ContentData();
		if (ci.isTemplate()) {
			gridData.set("template", ci.getAlias());
		} else {
			gridData.set("template", ci.getDerivedFrom());
		}
		new GridModelConfigLoader(mdr, gridData, permissions, ci.isTemplate()).load(new AsyncCallback<GridModelConfig>() {

			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
			}

			public void onSuccess(final GridModelConfig arg0) {
				EditableSingleCIGrid.this.gridConfig = arg0;
				init(arg0.getColumns());
				
				// Need to reload ci, why?
				/*
				if (!ci.isTemplate()) {
					List<String> aliases = new ArrayList<String>();
					aliases.add(ci.getAlias());
					ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), mdr, aliases, new CMDBAsyncCallback<List<CIModel>>() {

						@Override
						public void onSuccess(List<CIModel> list) {
							if (list.size() == 1) {
								ci = list.get(0);
							}
							init(arg0.getColumns());
						}
						
					});
				} else {
					init(arg0.getColumns());
				}
				*/
			}
		});
	}
	
	protected void init(List<ColumnConfig> cols) {
		setLayout(new FillLayout());
		
		store = new ListStore<CIModelCollection>();
		//store.clearGrouping();
		//store.groupBy("status");
		//final ListStore store = new ListStore(loader);
		/*
		ContentPanel cp = new ContentPanel();
		cp.setHeading("Edit - " + ci.getNameAndIcon());
		cp.setFrame(true);
		cp.setLayout(new FitLayout());
		*/
		
		CIModelCollection col = new CIModelCollection();
		col.addCIModel("offspring", ci);
		store.add(col);
		
		final ColumnModel cm = new ColumnModel(cols);
		
		Grid newGrid = null;
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT)) {
			newGrid = new EditorGrid(store, cm);
		} else {
			newGrid = new Grid(store, cm);
		}
		final Grid grid = newGrid;
		
		for (ColumnConfig cfg : cols) {
			if (cfg instanceof ComponentPlugin) {
				grid.addPlugin((ComponentPlugin)cfg);
			}
		}
		/*
		if (gridConfig.getAutoExpandColumnId() != null) {
			grid.setAutoExpandColumn(gridConfig.getAutoExpandColumnId());
		}
		*/
		/*
		MyGroupingView view = new MyGroupingView();
		view.setForceFit(true);
		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(GroupColumnData data) {
				String f = cm.getColumnById(data.field).getHeader();
				String l = data.models.size() == 1 ? "Item" : "Items";
				return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
			}
		});
		grid.setView(view);
		*/
		grid.setBorders(true);
		//grid.setLoadMask(true);
		
		grid.setContextMenu(getGridContextMenu(grid, cols));
		
		/*
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
		 	grid.addListener(Events.RowDoubleClick, getPropertySelection(grid));
		}
		*/
		
		add(grid);
		
		/*
		ToolBar toolBar = new ToolBar();
		if (gridConfig.isSupportAddRow()) {
			TextToolItem add = new TextToolItem("Add Row");
			add.addSelectionListener(new SelectionListener<ToolBarEvent>() {
				@Override
				public void componentSelected(ToolBarEvent ce) {

					CIModelCollection model = gridConfig.createNewInstance();
					grid.stopEditing();
					store.insert(model, 0);
					grid.startEditing(0, 0);
				}
			});
			//toolBar.add(add);
			pageToolBar.add(add);
		}
		cp.setTopComponent(pageToolBar);
    	*/
		/*
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.addButton(new Button("Reset", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// Remove all new Objects...
				for (CIModelCollection col : store.getModels()) {
					if (col.isNewCollection()) {
						store.remove(col);
					}
				}
				store.rejectChanges();
			}
		}));

		cp.addButton(new Button("Save", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final List<CIModelCollection> base = new ArrayList<CIModelCollection>();
				final List<CIModelCollection> local = new ArrayList<CIModelCollection>();
						
				Listener<StoreEvent> listener = new Listener<StoreEvent>() {

					public void handleEvent(StoreEvent be) {
						if (be.operation == RecordUpdate.COMMIT) {
							if (be.model instanceof CIModelCollection) {
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
				        old.set(p, original.get(p));
				    }
					base.add(old);
				}
				
				store.addListener(Store.Update, listener);
				store.commitChanges();
				store.removeListener(Store.Update, listener);
				
				// Do store on modfied.
				
				ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

					public void onFailure(Throwable caught) {
						
					}

					public void onSuccess(StoreResult result) {
						
					}
					
				});
				
			}
		}));
		*/
		//add(cp);
		layout();
  }

	
	public boolean commit() {
		store.commitChanges();
		return(true);
	}
	
	public void restore() {
		store.rejectChanges();
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
						CIPropertyPanel panel = new CIPropertyPanel(mdr, dataCol, rootCI);
						panel.setPermissions(permissions);
						Window w = WindowFactory.getWindow("Properties for " + dataCol.getCIModels().get(0).getDisplayName(), panel);
						//w.add(new CIValueForm(gridConfig, store, data));
						w.show();
						w.layout();
						w.toFront();
					}
				});
			}
			
		};
		return(selection);
	}
	
	private Menu getGridContextMenu(final Grid grid, final List<ColumnConfig> cols) {
		Menu menu = new Menu();
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



	public void setPermissions(CMDBPermissions permissions) {
		this.permissions = permissions;
	}
	
}
