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
package org.onecmdb.ui.gwt.desktop.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.utils.PermissionMenu;
import org.onecmdb.ui.gwt.desktop.client.widget.form.CIIdentityForm;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.AttributeGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyPanel;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.EditableCIInstanceGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.EditableSingleCIGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CITemplateBrowser;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CITemplateReferenceTree;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class CIModelInternalBrowser extends LayoutContainer {
	
	private ContentData mdr;
	private ContentPanel center;
	private AttributeGrid attributeGrid;
	private EditableSingleCIGrid defaultValueGrid;
	private CIModel modelLocal;
	private CIModel modelBase;
	private TreeItem currentTreeItem;
	private CITemplateBrowser browser;
	private List<String> roots;
	private CMDBPermissions permission;
	private TextToolItem removeTemplate;
	private String rootType;
	private String rootReferenceType;
	

	public CIModelInternalBrowser(ContentData mdr, List<String> roots, String rootType, String rootReferenceType) {
		this.mdr = mdr;
		this.roots = roots;
		this.rootType = rootType;
		this.rootReferenceType = rootReferenceType;
		
	}
	
	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
	}


	public void init() {
		
		setLayout(new BorderLayout());
		browser = new CITemplateBrowser(mdr, roots);
		
		//final CIInstanceBrowser center = new CIInstanceBrowser(mdr);
		center = new ContentPanel();
		center.setLayout(new FitLayout());
		center.setLayoutOnChange(true);
		//center.setScrollMode(Scroll.AUTO);
		
		browser.setSelectionListsner(new Listener<TreeEvent>() {

		
			public void handleEvent(TreeEvent te) {  
				TreeItem item = te.tree.getSelectedItem();  
				if (item != null) {  
					if (item.getModel() instanceof CIModel) {
						currentTreeItem = item;
						
						CIModel model = (CIModel)item.getModel();
					
						updateModel(model);
						
					}					
					//Info.display("Selection Changed", "The '{0}' item was selected", item.getText());  
				}
			}

		});  

		
		ContentPanel left = new ContentPanel();
		//left.setScrollMode(Scroll.AUTO);
		left.setHeading("Template Hierarchy");
		left.setLayout(new FitLayout());
		left.setLayoutOnChange(true);
		PermissionMenu menu = new PermissionMenu(permission, PermissionMenu.READONLY_MASK|PermissionMenu.EDIT_MASK|PermissionMenu.DELETE_MASK);
		menu.addListener(CMDBEvents.PERMISSION_CHANGED, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				if (currentTreeItem != null) {
					CIModel model = (CIModel)currentTreeItem.getModel();
					updateModel(model);
				}
			}
		});
		
		ToolBar toolBar = new ToolBar();
		toolBar.add(new FillToolItem());
		toolBar.add(menu);  		
		left.setTopComponent(toolBar);
		left.add(browser);
		
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	       
	       
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(5));  
	 
		
		add(left, westData);
		add(center, centerData);
		
		layout();
		
	}

	protected void updateModel(CIModel model) {
		this.modelBase = model.copy();
		this.modelLocal = model;
		center.removeAll();
		center.setHeading("Template " + model.getAlias());
		
		TabPanel panel = new TabPanel();
		center.add(panel);
		
		{
			TabItem attr = new TabItem("Attributes");
			attr.setLayout(new RowLayout());

			attr.add(getToolBar(), new RowData(1,-1));

			CIIdentityForm idForm = new CIIdentityForm(model);
			idForm.setPermission(permission);
			attr.add(idForm, new RowData(1, -1));
			attributeGrid = new AttributeGrid(mdr, model, rootType, rootReferenceType);
			attributeGrid.setPermission(permission);
			attr.add(attributeGrid, new RowData(1, 1));

			ContentPanel defaultPanel = new ContentPanel();
			defaultPanel.setHeading("Default values for " + model.getAlias());
			defaultPanel.setLayout(new FitLayout());
			defaultValueGrid = new EditableSingleCIGrid(mdr, model);
			defaultValueGrid.setPermissions(permission);
			defaultPanel.setHeight(120);
			defaultPanel.add(defaultValueGrid);
			attr.add(defaultPanel, new RowData(1,-1));

			panel.add(attr);

		}
		{
			TabItem refs = new TabItem("References");
			refs.setLayout(new FitLayout());
			CITemplateReferenceTree tree = new CITemplateReferenceTree(mdr, modelBase);
			tree.setPermission(permission);
			refs.add(tree);
			panel.add(refs);
			
		}
		center.layout();
		layout();
	}

	private ToolBar getToolBar() {
		
		ToolBar bar = new ToolBar();
		if (isEditAllowed()) {
			TextToolItem addAttribute = new TextToolItem("Add Attribute", "add-attribute-icon");
			addAttribute.addSelectionListener(getAddAttributeSelection());
			addAttribute.setToolTip("Add Attribute");
			addAttribute.setEnabled(isEditAllowed());
			bar.add(addAttribute);
			bar.add(new SeparatorToolItem());
		}
		if (isDeleteAllowed()) {
			TextToolItem deleteAttribute = new TextToolItem("Delete Attribute", "delete-icon");
			deleteAttribute.addSelectionListener(getDeleteAttributeSelection());
			deleteAttribute.setToolTip("Delete Selected Attributes");
			deleteAttribute.setEnabled(isDeleteAllowed());
			bar.add(deleteAttribute);
			bar.add(new SeparatorToolItem());
		}
		if (isEditAllowed()) {
			TextToolItem restore = new TextToolItem("Undo", "restore-icon");
			restore.addSelectionListener(getUndoSelection());
			restore.setToolTip("Undo");
			restore.setEnabled(isEditAllowed());
			bar.add(restore);

			bar.add(new SeparatorToolItem());

			TextToolItem  save = new TextToolItem("Save", "save-icon");
			save.addSelectionListener(getSaveSelection());
			save.setToolTip("Save");
			save.setEnabled(isEditAllowed());
			bar.add(save);

			bar.add(new SeparatorToolItem());

			TextToolItem  addTemplate = new TextToolItem("New Template", "add-icon");
			addTemplate.addSelectionListener(getNewSelection());
			addTemplate.setToolTip("Create new template derived from current template");
			addTemplate.setEnabled(isEditAllowed());
			bar.add(addTemplate);
			bar.add(new SeparatorToolItem());

		}
		
		if (isDeleteAllowed()) {
			removeTemplate = new TextToolItem("Delete Template", "delete-icon");
			removeTemplate.addSelectionListener(getRemoveSelection());
			removeTemplate.setToolTip("Delete current template");
			removeTemplate.setEnabled(isDeleteAllowed());
			bar.add(removeTemplate);
			bar.add(new SeparatorToolItem());
		}
		
		TextToolItem  propertiesTemplate = new TextToolItem("Properties", "property-icon");
		propertiesTemplate.addSelectionListener(getPropertySelection());
		propertiesTemplate.setToolTip("Show properties for this template");
		bar.add(propertiesTemplate);
		
		return(bar);
	}

	private SelectionListener getPropertySelection() {
		SelectionListener<ComponentEvent> selection = new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				
				CIModelCollection dataCol = new CIModelCollection();
				dataCol.addCIModel("offspring", modelLocal);
				String rootCI = "Ci";
				if (roots.contains("Root")) {
					rootCI = "Root";
				}
				final CIPropertyPanel panel = new CIPropertyPanel(mdr, dataCol, rootCI);
				panel.setPermissions(permission);
				Window w = WindowFactory.getWindow("Properties for " + dataCol.getCIModels().get(0).getDisplayName(), panel);
				w.addListener(Events.Close, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {
						if (panel.isModelChanged()) {
							reloadModel();
						}
					}
					
				});
				
				w.show();
				w.layout();
				w.toFront();
		}
			
		};
		
		return(selection);
	}

	private SelectionListener getRemoveSelection() {
		SelectionListener selection = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {

				final MessageBox box = MessageBox.confirm("Delete template " + modelBase.getAlias(), "Are you sure ?", new Listener<WindowEvent>() {  
					public void handleEvent(WindowEvent be) {
						Dialog dialog = (Dialog) be.component;  
						Button btn = dialog.getButtonPressed();  
						if (!btn.getItemId().equals(Dialog.YES)) {
							return;
						}
						List<CIModel> base = new ArrayList<CIModel>();
						List<CIModel> local = new ArrayList<CIModel>();
						base.add(modelBase);
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
								
								center.removeAll();
								center.setHeading("");
								// TODO: Check errors on storeResult...
								TreeItem parent = currentTreeItem.getParentItem();
								if (parent == null) {
									browser.reload();
									return;
								}
								if (parent.getModel() instanceof CIModel) {
									browser.reloadChildren((CIModel)parent.getModel());
								}
							}

						});
					}  
				});  
			}
		};
		return(selection);
	}

	private SelectionListener getNewSelection() {
		SelectionListener selection = new SelectionListener<ComponentEvent>() {  
			
			public void componentSelected(ComponentEvent ce) {

				//final CIModel model = (CIModel) item.getModel();
				// InputDialog for new AliasName.
				final MessageBox box = MessageBox.prompt("Name", 
						"<a style='font-size:smaller;'>" +
						"<i>Note: Alias needs to be unique and no special<br/>" +
						"characters including space is allowed.</i></a><br/><br/>" +
						"Please enter template alias:");  
				box.addCallback(new Listener<MessageBoxEvent>() {  
					public void handleEvent(MessageBoxEvent be) {

						//be.buttonClicked.
						if (be.value == null || be.value.length() == 0) {
							return;
						}
						
						final MessageBox createInfo = MessageBox.wait("Progress",  
								"Create, please wait...", "Creating...");  
						
						// Create new template.
						final CIModel newTemplate = new CIModel();
						newTemplate.setAlias(be.value);
						newTemplate.setDerivedFrom(modelBase.getAlias());
						newTemplate.setTemplate(true);
						List<CIModel> base = new ArrayList<CIModel>();
						List<CIModel> local = new ArrayList<CIModel>();
						local.add(newTemplate);
						// Call create.
						ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

							public void onFailure(Throwable caught) {
								createInfo.close();
								ExceptionErrorDialog.showError("Can't Create", caught);
								//loader.load();
							}

							public void onSuccess(StoreResult result) {
								if (result.isRejected()) {
									createInfo.close();
									MessageBox.alert("Create Failed", result.getRejectCause(), null);
									return;
								}
								// Load CiModel again....
								List<String> aliases = new ArrayList<String>();
								aliases.add(newTemplate.getAlias());
								ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), mdr, aliases, new CMDBAsyncCallback<List<CIModel>>() {

									@Override
									public void onFailure(Throwable t) {
										createInfo.close();
										super.onFailure(t);
									}

									@Override
									public void onSuccess(final List<CIModel> arg0) {
										createInfo.close();
										
										if (arg0.size() == 1) {
											DeferredCommand.addCommand(new Command() {
												public void execute() {
													CIModel parent = modelBase;
													browser.reloadChildren(parent, arg0.get(0));
													updateModel(arg0.get(0));
												}
											});
										}
										
									}
									
								});	
							}

						});


					}  
				});  
			}
		};
		return(selection);
	}

	private boolean isEditAllowed() {
		if (permission != null) {
			return(permission.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT));
		}
		return false;
	}
	
	private boolean isDeleteAllowed() {
		if (permission != null) {
			return(permission.getCurrentState().equals(CMDBPermissions.PermissionState.DELETE));
		}
		return false;
	}
	
	private SelectionListener getDeleteAttributeSelection() {
		
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
		
				List<AttributeModel> deleteAttributes = attributeGrid.getDeleteAttributes();
				
				final MessageBox box = MessageBox.confirm("Delete " + deleteAttributes.size()  + " Attributes", "Are you sure ?", new Listener<WindowEvent>() {  
					public void handleEvent(WindowEvent be) {

						if (!be.buttonClicked.getItemId().equals(Dialog.YES)) {
							return;
						}
						if (!attributeGrid.commitDelete()) {
							return;
						}

						final MessageBox saveInfo = MessageBox.wait("Progress",  
								"Deleting your data, please wait...", "Deleting..."); 


						List<CIModel> local = new ArrayList<CIModel>();
						List<CIModel> base = new ArrayList<CIModel>();

						local.add(modelLocal);
						base.add(modelBase);

						ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

							public void onFailure(Throwable caught) {
								// Error.
								saveInfo.close();
								ExceptionErrorDialog.showError("Can't Delete", caught);
							}

							public void onSuccess(StoreResult result) {
								saveInfo.close();
								// saved
								if (result.isRejected()) {
									MessageBox.alert("Delete Failed", result.getRejectCause(), new Listener<WindowEvent>() {
										public void handleEvent(WindowEvent be) {
											reloadModel();
										}
									});
									return;
								} else {
									reloadModel();
								}
							}
						});
					}
				});
			}
		});
	}
	
	private SelectionListener getSaveSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				if (!attributeGrid.commitSave()) {
					return;
				}
				
				if (!defaultValueGrid.commit()) {
					return;
				}
				
				final MessageBox saveInfo = MessageBox.wait("Progress",  
			             "Saving your data, please wait...", "Saving..."); 
				
				
				List<CIModel> local = new ArrayList<CIModel>();
				List<CIModel> base = new ArrayList<CIModel>();
	
				local.add(modelLocal);
				base.add(modelBase);
				
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
									reloadModel();
								}
							});
							return;
						} else {
							reloadModel();
						}
					}
				});
			}
		});
	}
	
	private void reloadModel() {
		
		//browser.reload();
		List<String> aliases = new ArrayList<String>();
		aliases.add(modelBase.getAlias());
		ModelServiceFactory.get().getCIModel(CMDBSession.get().getToken(), mdr, aliases, new AsyncCallback<List<CIModel>>() {

			public void onFailure(Throwable caught) {
				ExceptionErrorDialog.showError("ReLoad CI Failed", caught);
				return;
			}

			public void onSuccess(List<CIModel> result) {
				
				/**
				if (currentTreeItem != null) {
					currentTreeItem.getModel()
				}
				*/
				if (result.size() == 1) {
					updateModel(result.get(0));
					browser.updateStore(result.get(0));
				}
			}
		});
	}
	
	private SelectionListener getUndoSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				attributeGrid.restore();
				defaultValueGrid.restore();
			}
		});
	}

	private SelectionListener getAddAttributeSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				attributeGrid.addAttribute();
			}
			
		});
	}

	public void setPermission(CMDBPermissions permissions) {
		this.permission = permissions;
	}  
	
	
	
}
