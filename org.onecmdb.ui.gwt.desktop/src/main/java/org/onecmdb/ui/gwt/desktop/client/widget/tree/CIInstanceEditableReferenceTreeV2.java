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

import org.apache.commons.digester.SetTopRule;
import org.onecmdb.ui.gwt.desktop.client.control.GridModelConfigLoader;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyPanel;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.EditableSingleCIGrid;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CIInstanceEditableReferenceTreeV2 extends LayoutContainer {

		
	private ContentData mdr;
	private CIModel model;
	private ContentPanel propertyContainer;
	private CIPropertyGrid editCI;
	
	private CIModelCollection localData;
	private CIModelCollection baseData;

	/*
	private boolean readonly;
	private boolean deletable;
	*/
	private CIInstanceReferenceTree refTree;
	protected CMDBPermissions permissions;
	
	
	public CIInstanceEditableReferenceTreeV2(ContentData mdr, CIModel model) {
		this.mdr = mdr;
		this.model = model;
	}	
	
	
	
	public void setPermissions(CMDBPermissions permissions) {
		this.permissions = permissions;
	}


	
	/*
	public boolean isReadonly() {
		return readonly;
	}



	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
		if (refTree != null) {
			refTree.setReadonly(readonly);
		}
	}
	



	public boolean isDeletable() {
		return deletable;
	}



	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
		if (refTree != null) {
			refTree.setDeletable(deletable);
		}
	}

	*/

	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
	}
	
	public void init() {
		final ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		setLayout(new FitLayout());
		add(cp);
		cp.setLayout(new BorderLayout());
		propertyContainer = new ContentPanel();
		//propertyContainer.setHeight("150px");
		propertyContainer.setLayout(new FitLayout());
		propertyContainer.setLayoutOnChange(true);
		
		if (permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT)) {
			propertyContainer.setBottomComponent(getToolBar());
		}
		//propertyContainer.setBottomComponent(bottomComponent);
		refTree = new CIInstanceReferenceTree(this.mdr, this.model);
		//cp.setTopComponent(refTree.getToolbar());
		
		refTree.setPermission(permissions);
		/*
		refTree.setReadonly(readonly);
		refTree.setDeletable(deletable);
		*/
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	       
	       
	    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 0.35f);  
	    eastData.setSplit(true);  
	    eastData.setCollapsible(true);  
	    eastData.setMargins(new Margins(5));  
	 
		cp.add(refTree, centerData);
		cp.add(propertyContainer, eastData);
		
		refTree.addListener(CIInstanceReferenceTree.CI_SELECTED_EVENT, new Listener<BaseEvent>() {
	
			public void handleEvent(BaseEvent be) {
				if (be.source instanceof CIModel) {
					final CIModel m = (CIModel)be.source;
					// Load GridConfig for this model.
					ContentData gridData = new ContentData();
					if (m.isTemplate()) {
						gridData.set("template", m.getAlias());
					} else {
						gridData.set("template", m.getDerivedFrom());
					}
					gridData.set("instanceAlias", m.getAlias());
					new GridModelConfigLoader(mdr, gridData, permissions).load(new AsyncCallback<GridModelConfig>() {


						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
						}

						public void onSuccess(final GridModelConfig result) {
							DeferredCommand.addCommand(new Command() {

								public void execute() {
									CIModel ci = m;
									Object o = result.get("instance");
									if (o instanceof CIModel) {
										ci = (CIModel)o; 
									}
									baseData = new CIModelCollection();
									baseData.addCIModel("offspring", ci);
									localData = baseData.copy();

									editCI = new CIPropertyGrid(result, localData);
									editCI.setPermissions(permissions);
									//editCI.setReadonly(readonly);
									propertyContainer.removeAll();
									propertyContainer.setHeading("Properties for " + ci.getDisplayName());
									//propertyContainer.add(getToolBar(), new RowData(1,-1));
									propertyContainer.add(editCI);
									propertyContainer.layout();
									CIInstanceEditableReferenceTreeV2.this.layout();
								}
							});
						}
						
					});
				}
			}
			
		});
	}
	
private ToolBar getToolBar() {
		
		ToolBar bar = new ToolBar();
		if (!permissions.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT)) {
			return(bar);
		}
		TextToolItem restore = new TextToolItem("Undo", "restore-icon");
		restore.addSelectionListener(getUndoSelection());
		restore.setToolTip("Undo");
		bar.add(restore);
		
		bar.add(new SeparatorToolItem());
		
		TextToolItem  save = new TextToolItem("Save", "save-icon");
		save.addSelectionListener(getSaveSelection());
		save.setToolTip("Save");
		bar.add(save);
			
		return(bar);
	}
	
	private SelectionListener getSaveSelection() {
		return(new SelectionListener<ComponentEvent>() {
		

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (!editCI.commit()) {
					return;
				}
				
				final MessageBox saveInfo = MessageBox.wait("Progress",  
			             "Saving your data, please wait...", "Saving..."); 
				
				
				List<CIModel> local = new ArrayList<CIModel>();
				List<CIModel> base = new ArrayList<CIModel>();
	
				local.add(localData.getCIModels().get(0));
				base.add(baseData.getCIModels().get(0));
				
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
							DeferredCommand.addCommand(new Command() {

								public void execute() {
									refTree.reload(localData.getCIModels().get(0));
								}
								
							});
						}
					}
				});
			}
		});
	}
	
	private SelectionListener getUndoSelection() {
		return(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				editCI.restore();
			}
		});
	}
}
