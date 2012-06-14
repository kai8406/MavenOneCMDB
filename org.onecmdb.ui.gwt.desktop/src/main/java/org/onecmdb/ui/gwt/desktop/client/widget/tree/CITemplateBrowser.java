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
package org.onecmdb.ui.gwt.desktop.client.widget.tree;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.fixes.combo.IValueComponent;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.TestModelData;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.AttributeGrid;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseModelStringProvider;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.dnd.TreeDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeSelectionModel;
import com.extjs.gxt.ui.client.widget.tree.Tree.CheckCascade;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sun.tools.xjc.api.Reference;

public class CITemplateBrowser extends LayoutContainer implements IValueComponent<CIModel> {

	private BaseTreeLoader loader;
	private Listener<TreeEvent> selectionListener;
	private Tree tree;
	private boolean checkable;
	private ContentData mdr;
	private Listener<BaseEvent> checkListener;
	private List<String> roots;
	private boolean readonly;
	protected HashMap<CIModel, List<ModelItem>> moveTable;
	private TreeBinder<CIModel> binder;
	private TreeSelectionModel selectionModel;
	private TreeStore<CIModel> store;

	public CITemplateBrowser(ContentData mdr, List<String> roots) {
		this.mdr = mdr;
		this.roots = roots;
	}

	/**
	 * Call this to enable move function.
	 * 
	 * @param table
	 */
	public void setMoveTable(HashMap<CIModel, List<ModelItem>> table) {
		this.moveTable = table;
	}
	
	public boolean isCheckable() {
		return checkable;
	}

	public void setCheckable(boolean checkable, Listener<BaseEvent> listener) {
		this.checkable = checkable;
		this.checkListener = listener;
	}



	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
		getLoader().load();
	}

	private BaseTreeLoader getLoader() {
		return(loader);
	}

	public void init() {
		setLayout(new FitLayout());  
		setLayoutOnChange(true);
		setScrollMode(Scroll.AUTO);
		
		// data proxy  
		RpcProxy<CIModel, List<CIModel>> proxy = new RpcProxy<CIModel, List<CIModel>>() {

			@Override 
			protected void load(CIModel parent, final AsyncCallback<List<CIModel>> callback){// TODO Auto-generated method stub
				
				ModelServiceFactory.get().getTemplateChildren(CMDBSession.get().getToken(), mdr, parent, roots, new CMDBAsyncCallback<List<CIModel>>() {
					@Override
					public void onSuccess(List<CIModel> arg0) {
						// TODO Auto-generated method stub
						callback.onSuccess(arg0);
					}
				});
			}  
		};  

		// tree loader  
		loader = new BaseTreeLoader(proxy) {

			@Override
			public boolean hasChildren(ModelData parent) {
				
				if (parent instanceof CIModel) {
					int templateCount = (Integer)((CIModel)parent).get(CIModel.CI_TEMPLATE_CHILD_COUNT);
					
					return(templateCount > 0);
				}
				
				return(true);
			}  
		};  

		
		// trees store  
		store = new TreeStore<CIModel>(loader);  
		store.setMonitorChanges(true);
		store.setStoreSorter(new StoreSorter<CIModel>() {

			@Override
			public int compare(Store<CIModel> store, CIModel m1,
					CIModel m2, String property) {
				// TODO Auto-generated method stub
				return super.compare(store, m1, m2, property);
			}  
		});  
		
		tree = new Tree();  
		if (checkable) {
			tree.setCheckStyle(CheckCascade.NONE);
			selectionModel = new TreeSelectionModel(SelectionMode.SINGLE);
			tree.setSelectionModel(selectionModel);
			tree.setCheckable(true);
			if (checkListener != null) {
				tree.addListener(Events.CheckChange, checkListener);
			}
		}
		Menu contextMenu = getTemplateContextMenu(tree);
		if (contextMenu != null) {
			tree.setContextMenu(contextMenu);
		}
		binder = new TreeBinder<CIModel>(tree, store);
		binder.setCaching(false);
		//binder.setMask(true);
		
		store.addListener(Store.Update, new Listener<StoreEvent>() {

			public void handleEvent(StoreEvent be) {
				ModelData data = be.model;
				if (data instanceof CIModel) {
					boolean update = ((CIModel)data).get("reloadFlag", false);
					if (update) {
						refreshTree();
						/*
						// Calculate items to reload...
						for (CIModel m : moveTable.keySet()) {
							loader.loadChildren(m);
						}
						*/
						//moveTable.keySet();
					}
				}
			}
		});

		
		binder.setStringProvider(new BaseModelStringProvider<CIModel>() {

			@Override
			public String getStringValue(CIModel model, String property) {
				if (CIModel.CI_ALIAS.equals(property)) {
					
					String text = model.getProperty(property);
					Object decorate = CMDBSession.get().getConfig().get(Config.DECORATE_TEMPLATE_COUNT);
					if (decorate != null && decorate.toString().equals("true")) {
						int instanceCount = (Integer)model.getProperty(model.CI_INSTANCE_CHILD_COUNT);
						int totalInstanceCount = (Integer)model.getProperty(model.CI_TOTAL_INSTANCE_COUNT);
					
						 text += "<a style='color:blue; font-size:smaller;'>[" + instanceCount + "/" + totalInstanceCount  + "]</a>";
					}
					
					if (moveTable != null) {
						List<ModelItem> items = moveTable.get(model);
						if (items != null) {
							text = text + "<a style='color:green; font-size:smaller;'>(" +  items.size() + ")</a>";
						}
					}
					return(text);
				}
				return((Object)model.getProperty(property) == null ? "" : ((Object)model.getProperty(property)).toString());
			}
			
		});
		binder.setIconProvider(new ModelStringProvider<CIModel>() {  

			public String getStringValue(CIModel model, String property) {  
				
				String icon = model.get(model.CI_ICON_PATH);
				if (icon != null) {
					return(CMDBSession.get().getContentRepositoryURL() + "/" + icon);
				}
				return(null);
			}  

		});  
		binder.setDisplayProperty(CIModel.CI_ALIAS);  
		if (getSelectionListener() != null) {
			tree.addListener(Events.SelectionChange, getSelectionListener());
		}
		add(tree); 
		
		
		new TreeDropTarget(binder) {

			TreeItem currentItem = null;
			
			@Override
			protected void showFeedback(DNDEvent event) {
				final TreeItem item = tree.findItem(event.getTarget());
			    if (item == null || moveTable == null) {
			      event.status.setStatus(false);
			      return;
			    }
			    currentItem = item;
			    handleAppend(event, item);
			}

			
			@Override
			protected void onDragLeave(DNDEvent e) {
				super.onDragLeave(e);
				currentItem = null;
			}


			@Override
			protected void onDragDrop(DNDEvent event) {
				component.enableEvents(true);
				Insert.get().hide();
				
				if (currentItem != null && moveTable != null) {
					currentItem.el().firstChild().removeStyleName("my-tree-drop");
					CIModel model = (CIModel) currentItem.getModel();
					List<ModelItem> items = moveTable.get(model);
					if (items == null) {
						items = new ArrayList<ModelItem>();
						moveTable.put(model, items);
					}
					if (event.data instanceof List) {
						for (Object o : (List)event.data) {
							if (o instanceof ModelItem) {
								items.add((ModelItem)o);
							}
						}
					} else if (event.data instanceof ModelData) {
						items.add((ModelItem)event.data);
					}
					
					// Update Item....
					boolean b = model.get("updateFlag", false);
					model.set("updateFlag", !b);
					
					//currentItem.recalculate()
				}
			}
		};  
		
		layout();
	}
	
	protected void refreshTree() {
		
	}
	
	public void setSelected(CIModel model) {
		if (model == null || binder == null || tree == null) {
			return;
		}
		TreeItem item = (TreeItem) binder.findItem(model);
		if (item != null) {
			tree.setSelectedItem(item);
		}
	}

	private Menu getTemplateContextMenu(final Tree tree) {
		Menu contextMenu = new Menu();  
		contextMenu.setWidth(130);  
		/*
		if (!this.readonly) {
			MenuItem insert = new MenuItem();  
			insert.setText("New Template");  
			insert.setIconStyle("add-icon");  
			insert.addSelectionListener(new SelectionListener<MenuEvent>() {  
				public void componentSelected(MenuEvent ce) {

					TreeItem item = (TreeItem)tree.getSelectionModel().getSelectedItem();  
					// Add new template dialog
					final CIModel model = (CIModel) item.getModel();
					// InputDialog for new AliasName.
					final MessageBox box = MessageBox.prompt("Name", "Please enter template alias:");  
					box.addCallback(new Listener<MessageBoxEvent>() {  
						public void handleEvent(MessageBoxEvent be) {

							//be.buttonClicked.
							if (be.value == null || be.value.length() == 0) {
								return;
							}
							// Create new template.
							CIModel newTemplate = new CIModel();
							newTemplate.setAlias(be.value);
							newTemplate.setDerivedFrom(model.getAlias());
							newTemplate.setTemplate(true);
							List<CIModel> base = new ArrayList<CIModel>();
							List<CIModel> local = new ArrayList<CIModel>();
							local.add(newTemplate);
							// Call create.
							ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

								public void onFailure(Throwable caught) {
									ExceptionErrorDialog.showError("Can't Create", caught);
									//loader.load();
								}

								public void onSuccess(StoreResult result) {
									if (result.isRejected()) {
										MessageBox.alert("Create Failed", result.getRejectCause(), null);
										return;
									}

									// TODO: Check errors on storeResult...
									getLoader().loadChildren(model);
								}

							});


						}  
					});  

				}  
			});  
			contextMenu.add(insert);  

			MenuItem delete = new MenuItem();  
			delete.setText("Delete Template");  
			delete.setIconStyle("delete-icon");  
			delete.addSelectionListener(new SelectionListener<MenuEvent>() {  
				public void componentSelected(MenuEvent ce) {

					TreeItem item = (TreeItem)tree.getSelectionModel().getSelectedItem();  

					// Add new template dialog
					final CIModel model = (CIModel) item.getModel();
					final TreeItem parentItem = item.getParentItem();
					if (parentItem == null || parentItem.getParentItem() == null) {
						MessageBox.alert("Info", "Not allowed to delete Root CI's", null);
						return;
					}

					final MessageBox box = MessageBox.confirm("Delete " + model.getNameAndIcon(), "Are you sure ?", new Listener<WindowEvent>() {  
						public void handleEvent(WindowEvent be) {
							Dialog dialog = (Dialog) be.component;  
							Button btn = dialog.getButtonPressed();  
							if (!btn.getItemId().equals(Dialog.YES)) {
								return;
							}
							List<CIModel> base = new ArrayList<CIModel>();
							List<CIModel> local = new ArrayList<CIModel>();
							base.add(model);
							// Call create.
							final MessageBox deleteInfo = MessageBox.wait("Progress",  
									"Delete, please wait...", "Deleting...");  

							ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

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

									// TODO: Check errors on storeResult...
									getLoader().loadChildren(parentItem.getModel());
								}

							});
						}  
					});  
				}  
			});  
			contextMenu.add(delete);  
			contextMenu.add(new SeparatorMenuItem());
		}		
		*/
		MenuItem refresh = new MenuItem();  
		refresh.setText("Refresh");  
		refresh.setIconStyle("refresh-icon");
		refresh.addSelectionListener(new SelectionListener<MenuEvent>() {  
			public void componentSelected(MenuEvent ce) {
				TreeItem item = (TreeItem)tree.getSelectionModel().getSelectedItem();  
				if (item != null) {
					getLoader().loadChildren(item.getModel());
				}
			}  
		});  
	
		contextMenu.add(refresh);  
		return(contextMenu);
	}

	public void reload() {
		TreeItem item = (TreeItem)tree.getSelectionModel().getSelectedItem();
		if (item != null) {
			TreeItem parent = item.getParentItem();
			if (parent != null) {
				getLoader().loadChildren(parent.getModel());
			}
		}
	}
	
	public TreeItem getSelected() {
		if (tree == null) {
			return(null);
		}
		return(tree.getSelectedItem());
	}
	private Listener<TreeEvent> getSelectionListener() {
		return(this.selectionListener);
	}

	public void setSelectionListsner(Listener<TreeEvent> listener) {
		this.selectionListener = listener;
	}

	public void setReadonly(boolean value) {
		this.readonly = value;
	}

	public void reloadChildren(CIModel modelBase) {
		getLoader().loadChildren(modelBase);
		
	}
	
	public void reloadChildren(CIModel modelBase, final CIModel newSelection) {
		final LoadListener l = new LoadListener() {

			@Override
			public void loaderLoad(LoadEvent le) {
				super.loaderLoad(le);
				setSelected(newSelection);
				getLoader().removeLoadListener(this);
			}
			
		};
		getLoader().addLoadListener(l);
		getLoader().loadChildren(modelBase);
		
	}

	public CIModel getValue() {
		TreeItem item = getSelected();
		if (item == null) {
			return(null);
		}
		return((CIModel)item.getModel());
	}

	public void setValue(CIModel value) {
		//setSelected(value);
	}

	public void updateStore(CIModel model) {
		this.store.update(model);
		
	}
}
