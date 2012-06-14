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
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.fixes.IModelPermission;
import org.onecmdb.ui.gwt.desktop.client.fixes.MyCheckColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AttributeGrid extends LayoutContainer {
	
	private CIModel model;
	private ContentData mdr;
	private CIModel baseModel;
	private List<AttributeModel> newAttributes = new ArrayList<AttributeModel>();
	private GroupingStore<AttributeModel> store;
	private Grid<AttributeModel> grid;
	private CMDBPermissions permission;
	private String rootType;
	private String rootReference;
	
	class AttributeEditorGrid<M extends ModelData> extends EditorGrid {

		public AttributeEditorGrid(ListStore store, ColumnModel cm) {
			super(store, cm);
		}

		@Override
		public void startEditing(int row, int col) {
			// Check if this attribute row is editable.
			ModelData m = store.getAt(row);
			if (m == null) {
				return;
			}
			if (m instanceof AttributeModel) {
				if (((AttributeModel)m).isDerived()) {
					return;
				}
			}
			super.startEditing(row, col);
		}
		
		
		
	}
	
	public AttributeGrid(ContentData mdr, CIModel model, String rootType, String rootReference) {
		this.model = model;
		//this.baseModel = model.copy();
		this.mdr = mdr;
		this.rootType = rootType;
		this.rootReference = rootReference;
		
	}
	
	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
		updateModel(model);
	}

	
	private void updateModel(CIModel m) {
		this.model = m;
		this.baseModel = m.copy();
		store.removeAll();
		store.add(m.getAttributes());
	}

	private void init() {
		setLayout(new FitLayout());
		store = new GroupingStore<AttributeModel>();  
		store.clearGrouping();
		store.setMonitorChanges(true);
		
		//CheckBoxSelectionModel<AttributeModel> sm = new CheckBoxSelectionModel<AttributeModel>();  
		
		//store.groupBy("derived");  
		
		/*
		
		ColumnConfig name = new ColumnConfig("name", "Name", 60);  
		ColumnConfig alias = new ColumnConfig("alias", "Alias", 60);  
		ColumnConfig simpleType = new ColumnConfig("simpleType", "Simple Type", 60);  
		ColumnConfig complexType = new ColumnConfig("complexType", "Complex Type", 60);  
		ColumnConfig refType = new ColumnConfig("refType", "Ref. Type", 60);  
		ColumnConfig max = new ColumnConfig("maxOccurs", "Max Occurs", 40);  
		ColumnConfig min = new ColumnConfig("minOccurs", "Min Occurs", 40);  
		ColumnConfig derived = new ColumnConfig("derived", "Derived", 40);  
		*/	
		
		
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();  
		
		config.add(EditorFactory.getColumn(mdr, "name", "Name", 60, true, "xs:string", 1, false, false));
		config.add(EditorFactory.getColumn(mdr, "alias", "Alias", 60, true, "xs:string", 1, false, false));
		config.add(EditorFactory.getColumn(mdr, "simpleType", "Simple Type", 70, true, "xs:simpleTypes", 1, false, false));
		config.add(EditorFactory.getColumn(mdr, "complexType", "Complex Type", 80, true, rootType, 1, true, true));
		config.add(EditorFactory.getColumn(mdr, "refType", "Ref. Type", 80, true, rootReference, 1, true, true));
		config.add(EditorFactory.getColumn(mdr, "description", "Description", 100, true, "xs:string", 1, false, false));
		config.add(EditorFactory.getColumn(mdr, "minOccur", "Min Occurs", 30, true, "xs:string", 1, false, false));
		config.add(EditorFactory.getColumn(mdr, "maxOccur", "Max Occurs", 30, true, "xs:string", 1, false, false));
		config.add(EditorFactory.getColumn(mdr, "derived", "Derived", 60, false, "xs:boolean", 1, false, false));
		
		MyCheckColumnConfig remove = new MyCheckColumnConfig("remove", "Delete", 60);
		remove.setReadonly(false);
		remove.setModelPermission(new IModelPermission<AttributeModel>() {

			public boolean allowEdit(AttributeModel model, String property) {
				return(!model.isDerived());
			}
			
		});
		if (isAllowDelete()) {
			config.add(remove);
		}
		
		/*	
		config.add(name);  
		config.add(alias);  
		config.add(simpleType);  
		config.add(complexType);  
		config.add(refType);  
		config.add(min);  
		config.add(max);  
		config.add(derived);  
		*/
		final ColumnModel cm = new ColumnModel(config);  

		GroupingView view = new GroupingView();  
		view.setForceFit(true);  
		
		view.setGroupRenderer(new GridGroupRenderer() {  
			public String render(GroupColumnData data) {  
				String f = cm.getColumnById(data.field).getHeader();  
				String l = data.models.size() == 1 ? "Item" : "Items";  
				return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
			}  
		});  
		if (isEditAllowed()) {
			grid = new AttributeEditorGrid<AttributeModel>(store, cm);
		} else {
			grid = new Grid<AttributeModel>(store, cm);
		}
		grid.setView(view);  
		grid.setBorders(true);
		store.setStoreSorter(null);
		if (isAllowDelete()) {
			grid.addPlugin(remove);
		}
		add(grid); 
		layout();
	}

	private boolean isAllowDelete() {
		if (permission != null) {
			return(permission.getCurrentState().equals(CMDBPermissions.PermissionState.DELETE));
		}
		return false;
	}

	private boolean isEditAllowed() {
		if (permission != null) {
			return(permission.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT));
		}
		return false;
	}

	public void addAttribute() {
		if (grid instanceof EditorGrid) {
			AttributeModel attribute = new AttributeModel();
			attribute.setMaxOccur("1");
			attribute.setMinOccur("1");
			attribute.setDisplayName("New Attribute");

			newAttributes.add(attribute);
			//model.newAttribute(attribute);
			((EditorGrid)grid).stopEditing();
			store.insert(attribute, 0);
			((EditorGrid)grid).startEditing(0, 0);
		}
		
	}

	public void restore() {
		store.rejectChanges();
		// Remove all new items.
		for (AttributeModel a : newAttributes) {
			store.remove(a);
		}
		newAttributes.clear();
	}

	
	public List<AttributeModel> getDeleteAttributes() {
		List<AttributeModel> list = new ArrayList<AttributeModel>();
		for (AttributeModel a : model.getAttributes()) {
			if (a.isRemove()) {
				list.add(a);
			}
		}
		return(list);
	}
	public boolean commitDelete() {
		for (AttributeModel a : model.getAttributes()) {
			if (a.isRemove()) {
				if (a.isDerived()) {
					MessageBox.alert("Remove", "Can't remove a derived attribute", null);
					return(false);
				}
				//removedAttributes.add(a);
				newAttributes.remove(a);
				model.removeAttribute(a);
				//store.remove(a);
			}
		}
		store.commitChanges();
		return(true);
	}
	
	public boolean commitSave() {
		List<String> newAliases = new ArrayList<String>();
		for (AttributeModel aModel : newAttributes) {
			if (aModel.getAlias() == null || aModel.getAlias().length() == 0) {
				MessageBox.alert("Missing alias", "Attribute's must have a unique alias", null);
				return(false);
			}
			if (aModel.getSimpleType() == null && aModel.getComplexType() == null) {
				MessageBox.alert("Missing type", "Attribute's must have a type(simple or complex)", null);
				return(false);
			}
			if (aModel.getComplexType() != null && aModel.getSimpleType() != null) {
				MessageBox.alert("Type error", "Attribute's must have one type(simple or complex)", null);
				return(false);
			}
			if (aModel.getComplexType() != null && aModel.getRefType() == null) {
				MessageBox.alert("Type error", "Complex attribute's must have a reference type", null);
				return(false);
			}
			
			String alias = aModel.getAlias();
			if (newAliases.contains(alias)) {
				MessageBox.alert("Alias duplicated", "Attribute alias '" + alias + "' is not unique!", null);
				return(false);
			}
			newAliases.add(alias);
			for (AttributeModel a : model.getAttributes()) {
				if (a.getAlias().equals(alias)) {
					MessageBox.alert("Alias duplicated", "Attribute alias '" + a.getAlias() + "' is not unique!", null);
					return(false);
				}
			}
		}
		
		// Store all new attributes....
		for (AttributeModel aModel : newAttributes) {
			model.addAttribute(aModel);
		}
	
		newAttributes.clear();
		store.commitChanges();
		return(true);
	}

	public void setPermission(CMDBPermissions permission) {
		this.permission = permission;
		
	}

}
