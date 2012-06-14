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

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.EditableSingleCIGrid;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CIInstanceEditableReferenceTree extends LayoutContainer {

		
	private ContentData mdr;
	private CIModel model;
	private ContentPanel editContainer;
	private EditableSingleCIGrid editCI;
	private CIModel modelLocal;
	private CIModel modelBase;
	/*
	private boolean readonly;
	private boolean deletable;
	*/
	private CIInstanceReferenceTree refTree;
	protected CMDBPermissions permissions;
	
	
	public CIInstanceEditableReferenceTree(ContentData mdr, CIModel model) {
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
		setLayout(new RowLayout());
		editContainer = new ContentPanel();
		editContainer.setHeight("150px");
		editContainer.setLayout(new RowLayout());
		editContainer.setLayoutOnChange(true);
		
		refTree = new CIInstanceReferenceTree(this.mdr, this.model);
		refTree.setPermission(permissions);
		/*
		refTree.setReadonly(readonly);
		refTree.setDeletable(deletable);
		*/
		add(refTree, new RowData(1,1));
		add(editContainer, new RowData(1,-1));
		
		refTree.addListener(CIInstanceReferenceTree.CI_SELECTED_EVENT, new Listener<BaseEvent>() {
	
			public void handleEvent(BaseEvent be) {
				if (be.source instanceof CIModel) {
					modelLocal = (CIModel)be.source;
					modelBase = modelLocal.copy();
					editCI = new EditableSingleCIGrid(mdr, modelLocal);
					editCI.setPermissions(permissions);
					//editCI.setReadonly(readonly);
					editContainer.removeAll();
					editContainer.setHeading("Attributes for " + modelLocal.getDisplayName());
					editContainer.add(getToolBar(), new RowData(1,-1));
					editContainer.add(editCI, new RowData(1,1));
					editContainer.layout();
					CIInstanceEditableReferenceTree.this.layout();
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
								
								}
							});
							return;
						} else {
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
