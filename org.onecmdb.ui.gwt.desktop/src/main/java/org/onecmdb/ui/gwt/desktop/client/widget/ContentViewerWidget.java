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
import java.util.HashMap;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ContentViewerWidget extends LayoutContainer {
	
	private BaseTreeLoader loader;
	private Tree tree;
	//private TextArea textArea;
	private TabPanel tabFolder;
	private ContentFolder rootFolder;
	private boolean readonly;
	private CMDBPermissions permissions;
	private HashMap<String, TabItem> tabMap = new HashMap<String, TabItem>();
	
	public ContentViewerWidget() {
		this(new ContentFolder());
	}
	
	public ContentViewerWidget(ContentFolder root) {
		this.rootFolder = root;
	}

	
	public void setPermissions(CMDBPermissions permissions) {
		this.permissions = permissions;
	}

	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
	}
	
	public void init() {
		setLayout(new BorderLayout());
		
		  RpcProxy<? extends ContentData, List<? extends ContentData>> proxy = new RpcProxy<ContentData, List<? extends ContentData>>() {

			@Override 
			protected void load(ContentData loadConfig, AsyncCallback<List<? extends ContentData>> callback){
				loadConfig.getChildren(callback);
			}
		  };
			  
			   
		  // tree loader  
		 loader = new BaseTreeLoader(proxy) {  
			  @Override  
			  public boolean hasChildren(ModelData parent) {  
				  return (parent instanceof ContentFolder);  
			  }  
		  };  

		  // trees store  
		  TreeStore<? extends ContentData> store = new TreeStore<ContentData>(loader);  
		  store.setStoreSorter(new StoreSorter<ContentData>() {  

			  @Override  
			  public int compare(Store store, ContentData m1, ContentData m2, String property) {  
				  boolean m1Folder = m1 instanceof ContentFolder;  
				  boolean m2Folder = m2 instanceof ContentFolder;  

				  if (m1Folder && !m2Folder) {  
					  return -1;  
				  } else if (!m1Folder && m2Folder) {  
					  return 1;  
				  }  

				  return super.compare(store, m1, m2, property);  
			  }  
		  });  

		 tree = new Tree();  
		  
		  tree.setContextMenu(getContentMenu());
		 tree.addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				TreeItem item = tree.getSelectedItem();
				if (item.getModel() instanceof ContentFile) {
					final ContentFile file = (ContentFile)item.getModel();
					if (true) {
						updateEditArea(file, null, false);
						return;
					}
					Info.display("Loading...", (String)file.getName());
					ContentServiceFactory.get().get("token", file, new CMDBAsyncCallback<String>() {

						@Override
						public void onSuccess(String arg0) {
							updateEditArea(file, arg0, false);
						}

						
					});
				}
			}
			 
		 });
		  
		  TreeBinder<? extends ContentData> binder = new TreeBinder<ContentData>(tree, store);  
		 
		  binder.setIconProvider(new ModelStringProvider<ContentData>() {  

			  public String getStringValue(ContentData model, String property) {  
				   String name = model.getName();
				   if (name == null) {
					   return(null);
				   }
				   
				   if (name.endsWith(".gif")) {
					   return(CMDBSession.get().getContentRepositoryURL() + "/" + model.getPath());
				   }
				   if (name.endsWith(".jpg")) {
					   return(CMDBSession.get().getContentRepositoryURL() + "/" + model.getPath());
				   }
				   if (name.endsWith(".png")) {
					   return(CMDBSession.get().getContentRepositoryURL() + "/" + model.getPath());
				   }
				   if (model instanceof ContentFile) {
					   return(GWT.getModuleBaseURL() + "/images/file_obj.gif");
				   }
				   return(null);
			  }  

		  }); 
		   
		  binder.setDisplayProperty("name");  

		  loader.load(this.rootFolder);  
		  
		  BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
		  westData.setSplit(true);  
		  westData.setCollapsible(true);  
		  westData.setMargins(new Margins(5));  
		  
		  BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 50);  
		  //southData.setSplit(false);  
		  southData.setCollapsible(false);  
		  //southData.setFloatable(false);  
		  southData.setMargins(new Margins(0, 5, 5, 5));  

		  BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		  centerData.setMargins(new Margins(5, 0, 5, 0));  
		  
		  ContentPanel westPanel = new ContentPanel();
		  westPanel.setLayout(new FitLayout());
		  westPanel.setScrollMode(Scroll.AUTO);
		  westPanel.setLayoutOnChange(true);
		  westPanel.add(tree);
		   
		  westPanel.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				getLoader().load(rootFolder);
			}
			  
		  }));
		  
		 tabFolder = new TabPanel();  
		  tabFolder.setTabScroll(true);  
		  
		   
		  
		  LayoutContainer statusPanel = new LayoutContainer();
		  
		  add(westPanel, westData);
		  add(tabFolder, centerData);
		  add(statusPanel, southData);
		  layout();
	}

	protected void updateEditArea(final ContentFile file,
			String arg0, boolean edit) {
		
		
		//tabFolder.
		//TabItem item = tabMap.get(file.getPath());
		//if (item == null) {
			TabItem item = new TabItem();
			item.setClosable(true);
			item.setText((String)file.getName());
			item.setIconStyle("icon-tabs");  
			//tabMap.put(file.getPath(), item);
			tabFolder.add(item);
		//}
		
		if (edit) {
			final TextArea textArea = new TextArea();
			textArea.setText(arg0);

			ToolBar buttonBar = new ToolBar();  
			TextToolItem  save = new TextToolItem("Save",  "save-icon"); 
			save.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					ContentServiceFactory.get().put(CMDBSession.get().getToken(), file, textArea.getText(), new CMDBAsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean arg0) {
							Info.display("Saved", "Saved ok!");
						}
					});
				}

			});
			buttonBar.add(save);
			if (permissions != null) {
				save.setEnabled(permissions.isEditable());
			}
			if (this.readonly) {
				save.setEnabled(false);
			}
			/*
		ContentPanel centerPanel = new ContentPanel();
		centerPanel.setLayout(new RowLayout());
		centerPanel.add(toolBar, new RowData(1,-1));
		centerPanel.add(new WidgetComponent(textArea), new RowData(1, 1));
			 */

			item.setLayout(new RowLayout());
			item.add(buttonBar, new RowData(1,-1));
			item.add(new WidgetComponent(textArea), new RowData(1, 1));

		} else {
			// Add url.
			item.setUrl(CMDBSession.get().getContentRepositoryURL() + file.getPath());
		}
		
		tabFolder.setSelection(item);
		item.layout();
		
	}

	
	public  BaseTreeLoader getLoader() {
		return(loader);
	}
	
	private Tree getTree() {
		return(tree);
	}

	public Menu getContentMenu() {
		Menu contextMenu = new Menu();  
		contextMenu.setWidth(130);  
		
		{
			MenuItem open = new MenuItem();  
			open.setText("<b>Open in Browser</b>");  
			open.setIconStyle("icon-open");  
			open.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					final TreeItem item = (TreeItem)tree.getSelectionModel().getSelectedItem(); 
					if (item == null) {
						return;
					}
					ContentData folder = (ContentData) item.getModel();
					if (folder instanceof ContentFile) {
						updateEditArea((ContentFile)folder, null, false);
					}
				}
				
			});
			contextMenu.add(open);
		}
		
		if (permissions == null || permissions.isEditable()) {
			MenuItem edit = new MenuItem();
			edit.setIconStyle("icon-edit");
			edit.setText("Edit");
			
			edit.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					TreeItem item = tree.getSelectedItem();
					if (item.getModel() instanceof ContentFile) {
						final ContentFile file = (ContentFile)item.getModel();
						Info.display("Loading...", (String)file.getName());
						ContentServiceFactory.get().get("token", file, new CMDBAsyncCallback<String>() {

							@Override
							public void onSuccess(String arg0) {
								updateEditArea(file, arg0, true);
							}

							
						});
					}
				}
				
			});
			contextMenu.add(edit);
			
		}
		contextMenu.add(new SeparatorMenuItem());
			
		if (permissions == null || permissions.isEditable()) {
			MenuItem insert = new MenuItem();  
			insert.setText("New Folder");  
			insert.setIconStyle("icon-add");  
			insert.addSelectionListener(new SelectionListener<MenuEvent>() {  
				public void componentSelected(MenuEvent ce) {  
					final TreeItem item = (TreeItem)tree.getSelectionModel().getSelectedItem(); 
					ContentData folder = new ContentFolder();
					folder.setPath("");
					if (item != null) {
						folder = (ContentData) item.getModel();
					}
					final ContentData data = folder;
					final MessageBox box = MessageBox.prompt("Folder Name", "Please enter folder name:");  
					     box.addCallback(new Listener<MessageBoxEvent>() {  
					           public void handleEvent(MessageBoxEvent be) {  
					        	Button btn = be.buttonClicked;  
					        	if (btn.getItemId().equals(Dialog.OK)) {
					        		final ContentFolder folder = new ContentFolder();
					        		folder.setPath(data.getPath() + "/" + be.value);
									ContentServiceFactory.get().create(CMDBSession.get().getToken(), folder, new AsyncCallback<Boolean>() {

										public void onFailure(Throwable arg0) {
											Info.display("Failed","Can't create folder " + folder.getPath());
											
										}

										public void onSuccess(Boolean arg0) {
											if (arg0) {
												Info.display("Created", folder.getPath() + " created");
												if (item == null) {
													getLoader().load(rootFolder);
												} else { 
													getLoader().loadChildren(item.getModel());
												}
											} else {
												Info.display("Failed","Can't create folder " + folder.getPath());
											}
										}
										
									});
						    	}
					         }  
					     });
					}  
			});  
			contextMenu.add(insert);  
		}
		if (permissions == null || permissions.isEditable()) {
			MenuItem insert = new MenuItem();  
			insert.setText("New File");  
			insert.setIconStyle("icon-add");  
			insert.addSelectionListener(new SelectionListener<MenuEvent>() {  
				public void componentSelected(MenuEvent ce) {  
					final TreeItem item = (TreeItem)tree.getSelectionModel().getSelectedItem(); 
					ContentData folder = new ContentFolder();
					folder.setPath("");
					if (item != null) {
						folder = (ContentData) item.getModel();
					}
			
					final ContentData data = (ContentData) folder;
					final MessageBox box = MessageBox.prompt("File Name", "Please enter file name:");  
					     box.addCallback(new Listener<MessageBoxEvent>() {  
					           public void handleEvent(MessageBoxEvent be) {  
					        	Button btn = be.buttonClicked;  
					        	if (btn.getItemId().equals(Dialog.OK)) {
					        		final ContentFile folder = new ContentFile();
					        		folder.setPath(data.getPath() + "/" + be.value);
									ContentServiceFactory.get().create(CMDBSession.get().getToken(), folder, new AsyncCallback<Boolean>() {

										public void onFailure(Throwable arg0) {
											Info.display("Failed","Can't create file " + folder.getPath());
											
										}

										public void onSuccess(Boolean arg0) {
											if (arg0) {
												Info.display("Created", folder.getPath() + " created");
												if (item == null) {
													getLoader().load(rootFolder);
												} else {
													getLoader().loadChildren(item.getModel());
												}
											} else {
												Info.display("Failed","Can't create file " + folder.getPath());
											}
										}
										
									});
						    	}
					         }  
					     });
					}  
			});  
			contextMenu.add(insert);  
		}

		
		if (permissions == null || permissions.isDeletable()) {
			MenuItem remove = new MenuItem();  
			remove.setText("Delete");  
			remove.setIconStyle("icon-delete");  
			remove.addSelectionListener(new SelectionListener<MenuEvent>() {  
				public void componentSelected(MenuEvent ce) {  
					TreeItem item = (TreeItem)tree.getSelectionModel().getSelectedItem();  
					if (item == null) {
						return;
					}
					final TreeItem parent = item.getParentItem();
					final ContentData data = (ContentData) item.getModel();
					final MessageBox box = MessageBox.confirm("Delete", "Delete " + data.getPath() +"<br/>Are you sure?", new Listener<WindowEvent>() {  
					           public void handleEvent(WindowEvent be) {  
					        	Button btn = be.buttonClicked;  
					        	if (btn.getItemId().equals(Dialog.YES)) {
					        		ContentServiceFactory.get().delete(CMDBSession.get().getToken(), data, new AsyncCallback<Boolean>() {

										public void onFailure(Throwable arg0) {
											Info.display("Failed","Can't delete " + data.getPath());
											
										}

										public void onSuccess(Boolean arg0) {
											if (arg0) {
												Info.display("Delete", data.getPath() + " deleted");
												ModelData model = parent.getModel();
												if (model == null) {
													getLoader().load(rootFolder);
												} else {
													getLoader().loadChildren(model);
												}
											} else {
												Info.display("Failed", "Can't delete " + data.getPath());
											}
										}
										
									});
						    	}
					         }  
					     });
				}  
			});  
			contextMenu.add(remove);  
			contextMenu.add(new SeparatorMenuItem());
		}
		
		if (permissions == null || permissions.isEditable()) {
			MenuItem upload = new MenuItem();  
			upload.setText("Upload");  
			//remove.setIconStyle("icon-delete");  
			upload.addSelectionListener(new SelectionListener<MenuEvent>() {  
				public void componentSelected(MenuEvent ce) {  
					TreeItem item = (TreeItem)getTree().getSelectionModel().getSelectedItem();  
					new FileUploadWidget((ContentData)item.getModel()).show();
				}
			});  
			contextMenu.add(upload);
		}
			
		MenuItem download = new MenuItem();  
		download.setText("Download");  
		//remove.setIconStyle("icon-delete");  
		download.addSelectionListener(new SelectionListener<MenuEvent>() {  
			public void componentSelected(MenuEvent ce) {  
				TreeItem item = (TreeItem)getTree().getSelectionModel().getSelectedItem();
				if (item.getModel() instanceof ContentFile) {
					ContentFile file = (ContentFile)item.getModel();
					String url = CMDBSession.get().getContentRepositoryURL() + file.getPath(); 
					Window.open(url, "_blank", "");
				}
			}
		});  
		contextMenu.add(download);
			
		contextMenu.add(new SeparatorMenuItem());
		
		MenuItem refresh = new MenuItem();  
		refresh.setText("Refresh");  
		//remove.setIconStyle("icon-delete");  
		refresh.addSelectionListener(new SelectionListener<MenuEvent>() {  
			public void componentSelected(MenuEvent ce) {  
				DeferredCommand.addCommand(new Command() {

					public void execute() {
						TreeItem item = (TreeItem)getTree().getSelectionModel().getSelectedItem(); 
						if (item == null) {
							getLoader().load(rootFolder);
						} else {
							getLoader().loadChildren(item.getModel());
						}
						
					}
					
				});
			}
		});  
		contextMenu.add(refresh);
		
		return(contextMenu);
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
		
	}
}
