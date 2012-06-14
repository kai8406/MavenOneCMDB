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
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.fixes.MyTreeTableBinder;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.LoadConfigModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.RelationCollectionModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.tree.RelationTypeModel;
import org.onecmdb.ui.gwt.desktop.client.utils.LoaderProxy;
import org.onecmdb.ui.gwt.desktop.client.utils.StoreAction;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;
import org.onecmdb.ui.gwt.desktop.client.widget.multi.MultiSelectCI;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelStringProvider;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.Window.CloseAction;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumn;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumnModel;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.KeyboardListener;

public class CITemplateReferenceTree extends LayoutContainer {
	public static final int CI_SELECTED_EVENT = 50000;
	public static final int CI_TOOLBAR_AVAIL = 50001;
	
	private ContentData mdr;
	private CIModel root;

	public static final int DETAIL_REFRENCE_DISPLAY = 0x1;
	public static final int COMPACT_REFRENCE_DISPLAY = 0x2;

	public static int INBOUND_MODE = 0x1;
	public static int OUTBOUND_MODE = 0x2;
	public static int INBOUND_OUTBOUND_MODE = 0x3;
	
	private int displayMode = COMPACT_REFRENCE_DISPLAY;
	private int relTypeMode = INBOUND_OUTBOUND_MODE;

	
	private BaseTreeLoader loader;
	/*
	private boolean readonly;
	private boolean deletable;
	*/
	private TextField<String> searchField;

	private LoaderProxy lP;

	private CMDBPermissions permissions;

	
	
	class RelationSpanningTree {
		
		
		public void addChildren(CIModel instance,
				List<? extends ModelItem> result) {
			for (ModelItem item : result) {
				List<String> parentList = instance.get("path", new ArrayList<String>());
				
				if (item instanceof CIModel) {
					CIModel child = (CIModel)item;
					List<String >childList = new ArrayList<String>(parentList);
					childList.add(child.getAlias());
					item.set("path", childList);
				}
			}
		}
		
		public boolean isAlreadyInPath(CIModel model) {
			List<String> path = model.get("path", new ArrayList<String>());
			int count = 0;
			for (String item : path) {
				if (item.equals(model.getAlias())) {
					count++;
				}
			}
			return(count > 1);
		}

		public void setRoots(List<? extends ModelData> result) {
			for (ModelData data : result) {
				if (data instanceof CIModel) {
					ArrayList<String> list = new ArrayList<String>();
					list.add(((CIModel)data).getAlias());
					data.set("path", list);
				}
			}
		}
		
	}
	
	private RelationSpanningTree spanningTree = new RelationSpanningTree();

	private TextField<String> pageSizeField;

	private Listener<MenuEvent> refDisplListener;

	private Listener<MenuEvent> refTypeListener;

	private PagingToolBar pageingToolBar;
	private ToolBar toolBar;

	private LoadConfigModelItem cfg;
	private MyTreeTableBinder binder;
	private GroupDescription groupDescription;
	
	public CITemplateReferenceTree(ContentData mdr, CIModel model) {
		this.mdr = mdr;
		setRoot(model);
	}
	
	private void setRoot(CIModel model) {
		this.root = model;
	}
	
	public void setGroupDescription(GroupDescription desc) {
		this.groupDescription = desc;
	}
	
	
	/*
	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}
	*/
	
	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
	}
	
	public void init() {
		setLayout(new FitLayout());
		
		List<TreeTableColumn> columns = new ArrayList<TreeTableColumn>();
		TreeTableColumn column = new TreeTableColumn("name", "Display Name", 0.99f);
		
		
		columns.add(column);

		TreeTableColumnModel cm = new TreeTableColumnModel(columns);

		final TreeTable table = new TreeTable(cm);
		
		table.setAnimate(false);
		
		table.addListener(Events.SelectionChange, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				TreeItem item = table.getSelectedItem();
				if (item == null) {
					return;
				}
				if (item.getModel() instanceof CIModel) {
					fireEvent(item.getModel());
				}
			}
				
		});
		
		final Menu menu = new Menu();
		table.setContextMenu(menu);
		table.addListener(Events.ContextMenu, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				TreeItem item = table.getSelectedItem();
				updateMenu(item, menu);
			}

			
		});
		//table.setItemIconStyle("icon-page"); 

		RpcProxy<? extends ModelData, List<? extends ModelData>> proxy = new RpcProxy<ModelData, List<? extends ModelData>>() {

			@Override
			protected void load(final ModelData loadConfig,
					final AsyncCallback<List<? extends ModelData>> callback) {
				if (loadConfig instanceof LoadConfigModelItem) {
					List<CIModel> result = new ArrayList<CIModel>();
					result.add(root);
					spanningTree.setRoots(result);
					
					callback.onSuccess(result);
					
					return;
				}
				if (loadConfig instanceof CIModel) {
					ModelServiceFactory.get().loadRelationTypes(CMDBSession.get().getToken(), mdr, (CIModel)loadConfig, groupDescription, new CMDBAsyncCallback<RelationCollectionModel>() {

				

						public void onFailure(Throwable arg0) {
							super.onFailure(arg0);
							callback.onFailure(arg0);
						}
						

						public void onSuccess(RelationCollectionModel arg0) {
							List<RelationTypeModel> types = new ArrayList<RelationTypeModel>();
							for (RelationTypeModel type : arg0.getRelationTypes()) {
								// Add all
								if ((relTypeMode & INBOUND_OUTBOUND_MODE) == INBOUND_OUTBOUND_MODE) {
									types.add(type);
									continue;
								}
								if (type.isOutbound() && ((relTypeMode & OUTBOUND_MODE) == OUTBOUND_MODE)) {
									types.add(type);
									continue;
								}
								if (!type.isOutbound() && ((relTypeMode & INBOUND_MODE) == INBOUND_MODE)) {
									types.add(type);
									continue;
								}
							}
							callback.onSuccess(types);
						}
						
					});
					return;
				}
				
				if (loadConfig instanceof RelationTypeModel) {
					RelationTypeModel model = (RelationTypeModel) loadConfig;
					List<CIModel> result = new ArrayList<CIModel>();
					if (model.isOutbound()) {
						result.add(model.getTargetType());
					} else {
						result.add(model.getSourceType());
					}
					spanningTree.addChildren(((RelationTypeModel)loadConfig).getInstance(), result);
					
					callback.onSuccess(result);
					return;
				}
			}
		};

		loader = new BaseTreeLoader(proxy) {
				@Override
				public boolean hasChildren(ModelData parent) {
					if (parent instanceof ModelItem) {
						//return(((ModelItem)parent).hasChildren());
					}
					if (parent instanceof CIModel) {
						if (spanningTree.isAlreadyInPath(((CIModel)parent))) {
							return(false);
						}
					}
					return(true);
				}
			
				@Override
				public boolean loadChildren(ModelData parent) {
					// Need to convert the parent (CIModel) to a 
					// Tree Load config.
					// TODO Auto-generated method stub
					return super.loadChildren(parent);
				}
			};


		
		// trees store
		final TreeStore store = new TreeStore(loader);
		store.setMonitorChanges(true);
		/*
		store.setStoreSorter(new StoreSorter<ModelData>() {

			@Override
			public int compare(Store store, ModelData m1, ModelData m2, String property) {
				return super.compare(store, m1, m2, property);
			}
		});
		*/
		
		
		binder = new MyTreeTableBinder(table, store);
		binder.setDisplayProperty(null);
		binder.setCaching(false);
		binder.setStringProvider(new BaseModelStringProvider<ModelData>() {

	
			@Override
			public String getStringValue(ModelData model, String property) {
				if (model instanceof CIModel) {
					if (spanningTree.isAlreadyInPath(((CIModel)model))) {
						return("<i>" + model.get(CIModel.CI_DISPLAYNAME) + "</i>");
					}
					return(model.get(CIModel.CI_DISPLAYNAME));
				}
				if (model instanceof RelationTypeModel) {
					RelationTypeModel rel = (RelationTypeModel)model;
					switch (displayMode) {
					case DETAIL_REFRENCE_DISPLAY:
						CIModel type = null;
						if (rel.isOutbound()) {
							type = rel.getTargetType();
							return(rel.getAttributeAlias() + " " + model.get(CIModel.CI_DISPLAYNAME) + " " + type.getNameAndIcon());
						} else {
							type = rel.getSourceType();
							return(model.get(CIModel.CI_DISPLAYNAME) + " " + type.getNameAndIcon() + " " + rel.getAttributeAlias());
						}
						case COMPACT_REFRENCE_DISPLAY:
							if (rel.isOutbound()) {
								type = rel.getTargetType();
								return(type.getNameAndIcon());
							} else {
								type = rel.getSourceType();
								return(type.getNameAndIcon());
							}
					}
					//return(rel.getAttributeAlias() + " " + model.get(CIModel.CI_DISPLAYNAME) + " " + type.getNameAndIcon());
				}
				return(model.toString());
				//return super.getStringValue(model, property);
			}
			
		});
		 
		
		binder.setIconProvider(new ModelStringProvider<ModelData>() {  
			
			public String getStringValue(ModelData model, String property) {
				String icon = model.get(CIModel.CI_ICON_PATH);
				if (icon != null) {
					return(CMDBSession.get().getContentRepositoryURL() + "/" + icon);
				}

				return(null);
			} 
			 
		});
		
		
		
		//editPanel.add(editForm);
		pageingToolBar = new PagingToolBar(50);
		lP = new LoaderProxy(null);
		lP.setBaseTreeLoader(loader);
	    pageingToolBar.bind(lP);
	    
		
	    cfg = new LoadConfigModelItem(root);
	    //cfg.setGroupDescription(this.groupDescription);
		cfg.setLimit(lP.getLimit());
		lP.useLoadConfig(cfg);
	
		toolBar = new ToolBar();
		searchField = new TextField<String>();
		searchField.setToolTip("Search");
		searchField.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				if (event.getKeyCode() ==  KeyboardListener.KEY_ENTER) {
					reload();
				}
			}

			
		});
		toolBar.add(new AdapterToolItem(searchField));
		IconButton searchButton = new IconButton("search-icon");;
		searchButton.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				reload();
			}
			
		});
		toolBar.add(new AdapterToolItem(searchButton));
		
		TextToolItem optionItem = new TextToolItem("Options");
		Menu rootMenu = new Menu();
		
		optionItem.setMenu(rootMenu);
		{
			Menu refMenu = new Menu();
			MenuItem referenceItem = new MenuItem("Reference Direction");

			CheckMenuItem outboundItem = new CheckMenuItem("Outbound References");
			outboundItem.setGroup("refDirection");
			outboundItem.addListener(Events.CheckChange, getRefDirectionListener());
			outboundItem.setData("value", OUTBOUND_MODE);
			refMenu.add(outboundItem);
			
			CheckMenuItem inboundItem = new CheckMenuItem("Inbound References");
			inboundItem.setGroup("refDirection");
			inboundItem.setData("value", INBOUND_MODE);
			inboundItem.addListener(Events.CheckChange, getRefDirectionListener());
			refMenu.add(inboundItem);
			
			CheckMenuItem outinboundItem = new CheckMenuItem("In/Outbound References");
			outinboundItem.setGroup("refDirection");
			outinboundItem.setChecked(true);
			outinboundItem.setData("value", INBOUND_OUTBOUND_MODE);
			outinboundItem.addListener(Events.CheckChange, getRefDirectionListener());
			refMenu.add(outinboundItem);
				
			referenceItem.setSubMenu(refMenu);
			rootMenu.add(referenceItem);
		}
		{
			Menu displayMenu = new Menu();
			MenuItem displayOp = new MenuItem("Reference Display");
			{
				CheckMenuItem item = new CheckMenuItem("Advanced");
				item.setGroup("refDispl");
				item.setData("value", DETAIL_REFRENCE_DISPLAY);
				item.addListener(Events.CheckChange, getRefDisplListener());
				displayMenu.add(item);
			}
			{
				CheckMenuItem item = new CheckMenuItem("Simple");
				item.setGroup("refDispl");
				item.setChecked(true);
				item.setData("value", COMPACT_REFRENCE_DISPLAY);
				item.addListener(Events.CheckChange, getRefDisplListener());
				displayMenu.add(item);
			}
			displayOp.setSubMenu(displayMenu);
			rootMenu.add(displayOp);
		}
		
		toolBar.add(optionItem);
		
		toolBar.add(new SeparatorToolItem());
		
		pageSizeField = new TextField<String>();
		pageSizeField.setWidth(30);
		pageSizeField.setValue("50");
		pageSizeField.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				if (event.getKeyCode() ==  KeyboardListener.KEY_ENTER) {
					reload();
				}
			}

		});
		toolBar.add(new AdapterToolItem(pageSizeField));
		toolBar.add(new AdapterToolItem(new LabelField("Page Size")));

	
		final CheckBox check = new CheckBox();
		check.setBoxLabel("All children");
		check.setToolTip("If checked, ALL instances that is derived<br/> " +
				"from the selected template is shown.<br/>" +
				"Else instances directly derived from <br/>" + 
				"the selected template is shown"
				);
		check.setValue(Boolean.TRUE);
		cfg.setAllChildren(check.getValue());
		
		AdapterToolItem allInstances = new AdapterToolItem(check);
		toolBar.add(allInstances);
		check.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent be) {
				cfg.setAllChildren(check.getValue());
				//pageToolBar.getLoadConfig().setAllChildren(check.getValue());
			}
		});
		toolBar.add(new SeparatorToolItem());
		if (!permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
			TextToolItem add = new TextToolItem("Add", "add-icon");
			add.addSelectionListener(new SelectionListener<ToolBarEvent>() {
				@Override
				public void componentSelected(ToolBarEvent ce) {
					doNew();
				}
			});
			toolBar.add(add);
		}
	
		
		ContentPanel cp = new ContentPanel();
		//cp.setHeading(root.getNameAndIcon());
		cp.setHeaderVisible(false);
		cp.setFrame(true);
		//cp.setSize("100%", "100%");
		cp.setLayout(new FitLayout());
		cp.add(table);
		
		cp.setTopComponent(toolBar);
		cp.setBottomComponent(pageingToolBar);	
		add(cp);
		
		layout();
		
		
		
		reload();
	}
	
	
	private Listener<MenuEvent> getRefDisplListener() {
		if (refDisplListener == null) {
			refDisplListener = new Listener<MenuEvent>() {

				public void handleEvent(MenuEvent be) {
					CheckMenuItem item = (CheckMenuItem) be.item;
					if (item.isChecked()) {
						setDisplayMode((Integer) item.getData("value"));
					} 
				}

			};
		}
		return(refDisplListener);
	}

	private void reload() {
		// Need to update the page size.
		int pageSize = 50;
		try {
			pageSize = Integer.parseInt(pageSizeField.getValue());
		} catch (Throwable t) {
			// Ignore
		}
		pageingToolBar.setPageSize(pageSize);
		cfg.setLimit(lP.getLimit());
		lP.load();
	}

	private Listener<MenuEvent> getRefDirectionListener() {
		if (refTypeListener == null) {
			refTypeListener = new Listener<MenuEvent>() {

				public void handleEvent(MenuEvent be) {
					CheckMenuItem item = (CheckMenuItem) be.item;
					if (item.isChecked()) {
						setRelTypeMode((Integer) item.getData("value"));
					} 
				}

			};
		}
		return(refTypeListener);
	}

	protected void doNew() {
		// InputDialog for new AliasName.
		
		/*
		final MessageBox box = MessageBox.prompt("Name", "Please enter instance alias:");  
		
		box.addCallback(new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent be) {
				
				//be.buttonClicked.
				if (be.value == null || be.value.length() == 0) {
					return;
				}
		*/
				// Create new template.
				CIModel newInstance = root.newInstance();
				//newInstance.setAlias(be.value);
				List<CIModel> base = new ArrayList<CIModel>();
				List<CIModel> local = new ArrayList<CIModel>();
				local.add(newInstance);
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
						DeferredCommand.addCommand(new Command() {
							public void execute() {
								lP.load();
							}
						});
					}
					
				});

/*				
			}  
		});  
*/		
	}

	
	protected void fireEvent(ModelData model) {
		BaseEvent be = new BaseEvent(model);
		//be.source = model;
		super.fireEvent(CI_SELECTED_EVENT, be);
		
	}
	
	private void updateMenu(TreeItem item, Menu menu) {
		final ModelData model = item.getModel();
		menu.removeAll();
		if (!permissions.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
			if (model instanceof RelationTypeModel) {

				MenuItem addRelation = new MenuItem("Add Relation", "add-icon");
				addRelation.addSelectionListener(getAddRelation((CIModel)item.getParentItem().getModel(), (RelationTypeModel)model));

				MenuItem newRelation = new MenuItem("New Relation", "new-icon");
				newRelation.addSelectionListener(getNewRelation((CIModel)item.getParentItem().getModel(), (RelationTypeModel)model));

				menu.add(addRelation);
				menu.add(newRelation);
				menu.add(new SeparatorMenuItem());

			}
			if (model instanceof CIModel) {
				boolean insert = false;
				if (!(item.getParentItem().getParentItem() == null)) {
					MenuItem deleteRel = new MenuItem("Delete Relation", "delete-relation-icon");
					CIModel parent = (CIModel) item.getParentItem().getParentItem().getModel();
					CIModel child = (CIModel)model;
					RelationTypeModel relation = (RelationTypeModel)item.getParentItem().getModel();
					deleteRel.addSelectionListener(getDeleteRelation(parent, relation, child));
					menu.add(deleteRel);
					insert = true;
				}
				if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.DELETE)) {
					MenuItem deleteCI = new MenuItem("Delete CI", "delete-icon");
					deleteCI.addSelectionListener(getDeleteCI((CIModel)model));
					menu.add(deleteCI);
					insert = true;
				}
				if (insert) {
					menu.add(new SeparatorMenuItem());
				}
			}
			
		}

		MenuItem refresh = new MenuItem("Referesh", "refresh-icon");
		refresh.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				reloadChildren((ModelItem)model);
			}
			
		});
		menu.add(refresh);
	}

	private SelectionListener getDeleteCI(final CIModel model) {
		return(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				ArrayList<CIModel> local = new ArrayList<CIModel>();
				ArrayList<CIModel> base = new ArrayList<CIModel>();
				base.add(model);
				StoreAction.store(mdr, local, base, new CMDBAsyncCallback<StoreResult>() {

					public void onSuccess(StoreResult result) {
						if (result.isRejected()) {
							MessageBox.alert("Failed", "Delete rejected, cause " + result.getRejectCause(), null);
							return;
						}
						lP.load();
					}
				});
			}

		});

	}

	private SelectionListener getDeleteRelation(final CIModel parent,
			final RelationTypeModel relation, final CIModel child) {
		return(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				// TODO Auto-generated method stub

				List<CIModel> local = new ArrayList<CIModel>();
				List<CIModel> base = new ArrayList<CIModel>();

				CIModel source = null;
				CIModel target = null;
				AttributeModel aM = null;

				if (relation.isOutbound()) {
					source = parent;
					target = child;
					aM = relation.getSourceType().getAttribute(relation.getAttributeAlias());
				} else {
					source = child;
					target = parent;
					aM = relation.getTargetType().getAttribute(relation.getAttributeAlias());
				}

				base.add(source.copy());
				local.add(source);

				// Validate if we remove value or reset value...
				ValueModel vM = parent.getValue(relation.getAttributeAlias());
				if (vM instanceof ValueListModel) {
					ValueListModel vListM = (ValueListModel)vM;
					List<ValueModel> values = vListM.getValues();
					ValueModel realValue = null;
					for (ValueModel v : values) {
						if (target.getAlias().equals(v.getValue())) {
							realValue = v;
							break;
						}
					}
					if (realValue != null) {
						int minOccurs = Integer.parseInt(aM.getMinOccur());
						if (minOccurs == 0 || values.size() > minOccurs) {
							// Remove value
							vListM.removeValue(realValue);
						} else {
							// Reset value.
							realValue.setValue(null);
						}
					}
				} else {
						if (aM.getMinOccur().equals("0")) {
							// remove value
							source.removeValue(vM);
						} else {
							// reset value
							vM.setValue(null);
						}
				}
				StoreAction.store(mdr, local, base, new AsyncCallback<StoreResult>() {

					public void onFailure(Throwable caught) {
					}

					public void onSuccess(StoreResult result) {
						reloadChildren(relation);
					}

				});	
			}
		});
	}

	private void addRelation(CIModel ci,
			final RelationTypeModel relation, List objects, boolean newObjects) {
		
		List<CIModel> local = new ArrayList<CIModel>();
		List<CIModel> base = new ArrayList<CIModel>();
		if (relation.isOutbound()) {
			base.add(ci.copy());
		}
		
		for (Object o : objects) {
			CIModel relCI = null;
			if (o instanceof CIModelCollection) {
				CIModelCollection col = (CIModelCollection)o;
				relCI = col.getCIModel("offspring");
			}
			if (o instanceof CIModel) {
				relCI = (CIModel)o;
			}
			if (relCI == null) {
				continue;
			}
		
			String attrAlias = relation.get(RelationTypeModel.REL_ATTRIBUTE_ALIAS);
			if (relation.isOutbound()) {
				ValueModel v = ci.getValue(attrAlias);
				if (v == null) { 
					v = new ValueModel();
					v.setAlias(attrAlias);
					v.setIsComplex(true);
					ci.setValue(attrAlias, v);
				}
				if (v instanceof ValueListModel) {
					// Check if w have empty slots
					ValueListModel vListM = (ValueListModel)v;
					ValueModel nV = null;
					for (ValueModel eV : vListM.getValues()) {
						if (eV.getValue() == null || eV.getValue().length() == 0) {
							nV = eV;
							break;
						}
					}
					if (nV == null) {
						nV = new ValueModel();
						nV.setAlias(attrAlias);
						((ValueListModel)v).addValue(nV);
					}			
					nV.setIsComplex(true);
					nV.setValue(relCI.getAlias());
				} else {
					v.setValue(relCI.getAlias());
				}
				local.add(ci);
				if (newObjects) {
					local.add(relCI);
				}
			} else {
				if (!newObjects) {
					base.add(relCI.copy());
				}
				ValueModel v = relCI.getValue(attrAlias);
				if (v == null) { 
					v = new ValueModel();
					v.setAlias(attrAlias);
					v.setIsComplex(true);
					relCI.setValue(attrAlias, v);
				}
				if (v instanceof ValueListModel) {
					ValueModel nV = new ValueModel();
					nV.setAlias(attrAlias);
					nV.setValue(ci.getAlias());
					nV.setIsComplex(true);
					
					((ValueListModel)v).addValue(nV);
				} else {
					v.setValue(ci.getAlias());
				}
			
				local.add(relCI);
			}
		}
		// TODO: Save local, base...
		final MessageBox saveInfo = MessageBox.wait("Progress",  
	             "Saving your data, please wait...", "Saving..."); 
		
		
		ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

			public void onFailure(Throwable caught) {
				// Error.
				saveInfo.close();
				ExceptionErrorDialog.showError("Can't Save", caught);
			}

			public void onSuccess(StoreResult result) {
				saveInfo.close();
				// saved
				if (result.isRejected()) {
					MessageBox.alert("Save Failed", result.getRejectCause(), new Listener<WindowEvent>() {
						public void handleEvent(WindowEvent be) {
						
						}
					});
					return;
				} else {
					reloadChildren(relation);
				}
			}

			
		});
	}

		
	public void reloadChildren(ModelItem model) {
		loader.loadChildren(model);
	}
	
	public void reload(ModelItem model) {
		TreeItem item = (TreeItem) binder.findItem(model);
		if (item == null) {
			return;
		}
		TreeItem parent = item.getParentItem();
		if (parent == null) {
			lP.load();
			return;
		}
		if (parent.isRoot()) {
			lP.load();
			return;
		}
		if (parent.getModel() != null) {
			loader.loadChildren(parent.getModel());
		}
	}
	

	private SelectionListener getAddRelation(final CIModel ci, final RelationTypeModel relation) {
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
	
				CIModel source = relation.getSourceType();
				AttributeModel aModel = source.getAttribute((String)relation.get(RelationTypeModel.REL_ATTRIBUTE_ALIAS));
				
				SelectionMode selMod = SelectionMode.MULTI;
				if (relation.isOutbound() && aModel.getMaxOccur().equals("1")) {
					selMod = SelectionMode.SINGLE;
				}
				CIModel type = relation.getSourceType();
				if (relation.isOutbound()) {
					type = relation.getTargetType();
				}
				final MultiSelectCI select = new MultiSelectCI(mdr, type, selMod, null, permissions);
				ContentPanel cp = new ContentPanel();
				cp.setLayout(new FillLayout());
				cp.setHeading("Select...");
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
						addRelation(ci, relation, objects, false);
					}

				});
				cp.addButton(selectButton);
				cp.addButton(cancelButton);
			
			
				w.add(cp);
				w.setVisible(true);
			}
			
		});
	}
	private SelectionListener getNewRelation(final CIModel ci, final RelationTypeModel relation) {
		return(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				
				
				String derivedFrom = relation.getSourceType().getAlias();
				if (relation.isOutbound()) {
					derivedFrom = relation.getTargetType().getAlias();
				}
				// Select SubTypes.
				List<String> roots = new ArrayList<String>();
				roots.add(derivedFrom);
				
				final CITemplateBrowser browser = new CITemplateBrowser(mdr, roots);
				
				final Window w = new Window();
			    w.setCloseAction(CloseAction.CLOSE);
			    w.setMinimizable(false);
			    w.setMaximizable(true);
			    w.setIconStyle("accordion");
			    //w.setHeading();
			    w.setWidth(600);
			    w.setHeight(400);
			    w.setLayout(new FitLayout());
				w.add(browser);
				
				ContentPanel cp = new ContentPanel();
				cp.setLayout(new FillLayout());
				cp.setHeading("Select...");
				cp.add(browser);
				
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
						TreeItem item = browser.getSelected();
						if (item == null) {
							return;
						}
						if (item.getModel() instanceof CIModel) {
							CIModel model = (CIModel) item.getModel();
							CIModel newCI = model.newInstance();
							List objects = new ArrayList();
							objects.add(newCI);
							addRelation(ci, relation, objects, true);
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

	public void setPermission(CMDBPermissions permissions) {
		this.permissions = permissions;
		
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	public int getRelTypeMode() {
		return relTypeMode;
	}

	public void setRelTypeMode(int relTypeMode) {
		this.relTypeMode = relTypeMode;
	}


}
