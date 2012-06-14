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
package org.onecmdb.ui.gwt.desktop.client.widget.form;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.AttributeSelectorModel;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.ContentSelectorWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.FileUploadWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.content.ContentSelectUploadDialog;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Field.FieldMessages;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InputFormWidget extends FormPanel {

	private List<AttributeColumnConfig> configs;
	private BaseModel model;
	protected ContentData lastSelected = null;


	public InputFormWidget(BaseModel model, List<AttributeColumnConfig> configs) {
		super();
		//setWidth(300);
		this.configs = configs;
		this.model = model;
		setStyleName("property-panel-background");
		setup();
	}
		

	private void setup() {
		FormBinding binding = new FormBinding(this);
		setupFields(this.configs, binding);
		binding.bind(model);
		binding.autoBind();
	
	}
	
	private void setupFields(List<AttributeColumnConfig> configs, FormBinding binding) {
		Field field = null;
		for (final AttributeColumnConfig config : configs) {
			List<AttributeColumnConfig> groupFields = null;
			if (config.getType().equals("xs:content")) {
				
				MultiField<Field> url = new MultiField<Field>();
				//url.setName(config.getId());
				url.setFieldLabel(config.getName());
				final TextField<String> text = new TextField<String>();
				text.setName(config.getId());
				text.setWidth(200);
				if (config.getTooltip() != null) {
					text.setToolTip(config.getTooltip());
				}
				// Add binder to this.
				binding.addFieldBinding(new FieldBinding(text, config.getId()));		
					
				url.add(text);
				Button upload = new Button("Upload", new SelectionListener<ComponentEvent>() {

					@Override
					public void componentSelected(ComponentEvent ce) {
						FileUploadWidget upload = new FileUploadWidget(new ContentFolder(config.getContentRoot()));
						upload.setComplex(false);
						upload.addListener(Events.Submit, new Listener<BaseEvent>() {

							public void handleEvent(BaseEvent be) {
								String name = (String)be.source;
								model.set(config.getId(), name);
							}
							
						});
						upload.show();
					}
					
				});
				upload.setToolTip("Upload a file from disk to the repository");
				url.add(new AdapterField(upload));
				Button browse = new Button("Browse", new SelectionListener<ComponentEvent>() {

					@Override
					public void componentSelected(ComponentEvent ce) {
						selectContent(config.getContentRoot(), model, config.getId());
					}
					
				});
				browse.setToolTip("Browse the repository and select a previously uploaded file");
				url.add(new AdapterField(browse));
				field = url;
			} else if (config.getType().equals("xs:combo")) {
				final ComboBox<BaseModel> scb = new ComboBox();
				ListStore<BaseModel> store = new ListStore();
				store.add(config.getComboValues());
				scb.setStore(store);
				scb.setDisplayField(config.getComboProperty());
				scb.setName(config.getId());
				/*
				scb.addListener(Events.Select, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {
						List<AttributeModel> list = scb.getSelection();
						if (list.size() == 1) {
							model.set(config.getId(), list.get(0));
							reload();
						}
					}
					
				});
				*/
				binding.addFieldBinding(new FieldBinding(scb, config.getId()));
				field = scb;
			} else if (config.getType().equals("xs:attribute")) {
				final ComboBox<AttributeModel> scb = new ComboBox<AttributeModel>();
				// Setup Proxy..
				RpcProxy<ListLoadConfig, ListLoadResult<AttributeModel>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<AttributeModel>>() {

					@Override
					protected void load(ListLoadConfig loadConfig,
							AsyncCallback<ListLoadResult<AttributeModel>> callback) {
						ArrayList<AttributeModel> list = new ArrayList<AttributeModel>();
						ListLoadResult<AttributeModel> result = new BaseListLoadResult<AttributeModel>(list);
						
						Object obj = model.get(config.getCIProperty());
						if (obj instanceof CIModel) {
							CIModel ci = (CIModel)obj;
							for (AttributeModel a : ci.getAttributes()) {
								if ("simple".equals(config.getAttributeFilter())) {
									if (!a.isComplex()) {
										list.add(a);
									}
								} else if ("complex".equals(config.getAttributeFilter())) {
									if (a.isComplex()) {
										list.add(a);
									}
								} else {
									list.add(a);
								}
							}
						}
						callback.onSuccess(result);
					}
				};
				BaseListLoader<ListLoadConfig, ListLoadResult<AttributeModel>> loader = new BaseListLoader<ListLoadConfig, ListLoadResult<AttributeModel>>(proxy);
				
				ListStore<AttributeModel> store = new ListStore<AttributeModel>(loader);
				
				/*
				BaseModel base = config.get("baseModel");
				Object o = base.get((String)config.get("ciProperty"));
				ListStore<AttributeModel> store = new ListStore<AttributeModel>();
				if (o instanceof CIModel) {
					CIModel ci = (CIModel)o;
					store.add(ci.getAttributes());
				} else {
					AttributeModel a = new AttributeModel();
					a.setAlias("Select template...");
					store.add(a);
				}
				*/
				scb.setStore(store);
				scb.setDisplayField("alias");
				scb.setName(config.getId());
				scb.addListener(Events.Select, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {
						List<AttributeModel> list = scb.getSelection();
						if (list.size() == 1) {
							model.set(config.getId(), list.get(0));
							reload();
						}
					}
					
				});
				AttributeModel aModel = model.get(config.getId());
				if (aModel != null) {
					if (aModel.isComplex()) {
						groupFields = config.get("complex");
					} else {
						groupFields = config.get("simple");
					}
				}
				binding.addFieldBinding(new FieldBinding(scb, config.getId()));
				field = scb;
				
			} else if (config.getType().equals("xs:enum")) {
				field = new SimpleComboBox<String>();
				for (String value : config.getEnumValues()) {
					((SimpleComboBox<String>)field).add(value);
				}
				field.setName(config.getId());
				binding.addFieldBinding(new SimpleComboBoxFieldBinding(((SimpleComboBox<String>)field), config.getId()));		
			} else if (config.getType().equals("xs:boolean")) {
				field = new CheckBox();
				if (model.get(config.getId()) == null) {
					model.set(config.getId(), false);
				}
				field.setName(config.getId());
			} else if (config.getType().equals("xs:radiogroup")) {
				final RadioGroup group = new RadioGroup();
				for (String value : config.getRadios()) {
					Radio r = new Radio();
					r.setBoxLabel(value);
					boolean checked = value.equals(model.get(config.getId()));
					if (checked) {
						groupFields = config.get(value);
					}
					r.setValue(checked);
					Object o = config.get(r.getBoxLabel());
					if (o == null) {
						r.setEnabled(false);
					}
					group.add(r);
				}
				
				group.addListener(Events.Change, new Listener<FieldEvent>() {

					public void handleEvent(FieldEvent be) {
						Radio r = group.getValue();
						model.set(config.getId(), r.getBoxLabel());
						reload();
					}
				});
				field = group;
			} else if (config.getType().equals("xs:textarea")) {
				field = new TextArea();
				field.setName(config.getId());
			} else {
				CMDBPermissions perm = new CMDBPermissions();
				perm.setCurrentState(CMDBPermissions.PermissionState.EDIT);
				final ColumnConfig column = EditorFactory.getColumnConfig(config, false, perm);
				field = column.getEditor().getField();
				field.setName(config.getId());
			}
			field.setFieldLabel(config.getName());
			if (config.getTooltip() != null) {
				field.setToolTip(config.getTooltip());
			}
			add(field);
			
			// Check if radio exapnd forms.
			if (groupFields != null) {
				setupFields(groupFields, binding);
			}
		}
	}	
	
	
	protected void selectContent(String root, BaseModel mode, final String property) {
		final ContentData rootData = new ContentData();
		if (root != null) {
			rootData.setPath(root);
			if (!rootData.getPath().startsWith("/")) {
				rootData.setPath("/" + rootData.getPath());
			}
		} 
		final ContentSelectorWidget sel = new ContentSelectorWidget(rootData);
		final ContentSelectUploadDialog dialog = new ContentSelectUploadDialog(sel);
		/*
		sel.setSelectionListener(new Listener<TreeEvent>() {

			public void handleEvent(TreeEvent be) {
				List<TreeItem> list = be.selected;
				if (list.size() == 1) {
					TreeItem item = list.get(0);
					lastSelected  = (ContentData) item.getModel();
					
				}
			}
			
		});
		*/
		sel.setDbClickListener(new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				if (be instanceof TreeEvent) {
					TreeEvent te = (TreeEvent)be;
					TreeItem item = te.item;
					if (item.getModel() instanceof ContentData) {
						lastSelected = (ContentData) item.getModel();
						String path = lastSelected.getPath();
						if (rootData != null) {
							path = path.substring(rootData.getPath().length()+1);
						}
						model.set(property, path);
						dialog.close();
					}
				}
				
			}
		});
	
		dialog.addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				List<ContentData> items = sel.getSelected();
				if (items == null) {
					return;
				}
				if (items.size() != 1) {
					
				}
				lastSelected = items.get(0);
				if (lastSelected != null) {
					if (lastSelected.isDirectory()) {
						Info.display("Select problem", "Select a file");
						return;
					}
					
					System.out.println("Selected=" + lastSelected.getPath() + " , root=" + rootData.getPath());
					String path = lastSelected.getPath(); 
					if (rootData.getPath() != null) {
						path = path.substring(rootData.getPath().length()+1);
					}	
					model.set(property, path);
					dialog.close();
				}
			}
			
		});
		
		dialog.show();
	}



	private void reload() {
		removeAll();
		setup();
		layout();
	}
	
	
	private void getComplexAttribute() {
		/*
		// Proxy to load attributes...
		RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<AttributeModel>> proxy = new RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<AttributeModel>>() {

			@Override
			protected void load(BasePagingLoadConfig loadConfig,
					AsyncCallback<BasePagingLoadResult<AttributeModel>> callback) {
				BaseModel model = config.get("baseModel");
				Object o = model.get((String)config.get("ciProperty"));
				List<AttributeModel> list = new ArrayList<AttributeModel>();
				if (o instanceof CIModel) {
					CIModel ci = (CIModel)o;
					list.addAll(ci.getAttributes());
				} else {
					AttributeModel a = new AttributeModel();
					a.setAlias("Select template...");
					list.add(a);
				}
				BasePagingLoadResult<AttributeModel> result = new BasePagingLoadResult<AttributeModel>(list);
				callback.onSuccess(result);
			}
		};
		BaseListLoader loader = new BaseListLoader(proxy);
		ListStore store = new ListStore(loader);
		ComboBox cb = new ComboBox();
		cb.setDisplayField("alias");
		cb.setStore(store);
		*/	
	}

}
