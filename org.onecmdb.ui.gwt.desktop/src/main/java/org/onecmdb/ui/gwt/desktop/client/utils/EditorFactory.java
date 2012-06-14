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
package org.onecmdb.ui.gwt.desktop.client.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.control.CIProxy;
import org.onecmdb.ui.gwt.desktop.client.fixes.AttributeColumnModel;
import org.onecmdb.ui.gwt.desktop.client.fixes.CIColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.fixes.CIReferenceColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.fixes.CITableColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.fixes.CITableColumnConfig2;
import org.onecmdb.ui.gwt.desktop.client.fixes.CITemplateColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.fixes.MultiColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.fixes.MyCheckColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.fixes.PopupTextColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.fixes.URLColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.LoadConfigModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions.PermissionState;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupCollection;
import org.onecmdb.ui.gwt.desktop.client.widget.multi.MultiValueComboBox;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.opensymphony.oscache.base.Config;

public class EditorFactory {
	
	protected static DateTimeFormat dateFmt1 = DateTimeFormat.getFormat("yyyy-MM-dd");
	protected static DateTimeFormat dateFmt2 = DateTimeFormat.getFormat("yyyy-MM");
	protected static DateTimeFormat dateFmt3 = DateTimeFormat.getFormat("yyyy");
	
	
	public static ListStore<CIModel> getCIStore(final ContentData mdr, final CIModel type, final boolean matchTemplate) {
		RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<CIModel>> proxy = new RpcProxy<BasePagingLoadConfig, BasePagingLoadResult<CIModel>>() {
			@Override
			protected void load(BasePagingLoadConfig loadConfig,
					final AsyncCallback<BasePagingLoadResult<CIModel>> callback) {
				LoadConfigModelItem load = new LoadConfigModelItem();
				load.setRoot(type);
				load.setLimit(loadConfig.getLimit());
				load.setOffset(loadConfig.getOffset());
				final String query = loadConfig.getParams().get("query");
				load.setQuery(query);
				
				if (matchTemplate) {
					load.setAllChildren(true);
					load.setMatchTemplate(true);
					load.setLimit(-1);
					ModelServiceFactory.get().getTemplateInstances(CMDBSession.get().getToken(), mdr, load, new AsyncCallback<BasePagingLoadResult<CIModel>>() {

						public void onFailure(Throwable arg0) {
							callback.onFailure(arg0);
						}


						public void onSuccess(BasePagingLoadResult<CIModel> arg0) {
							
							// Uppdate nameAndIcon...
							List<CIModel> models = new ArrayList<CIModel>();
							for (CIModel m : arg0.getData()) {
								m.set(CIModel.CI_NAME_AND_ICON, m.getNameAndIcon());
								if (query != null) {
									if (m.getAlias().startsWith(query)) {
										models.add(m);
									}
								} else {
									models.add(m);
								}
							}
							arg0.setData(models);
							arg0.setTotalLength(arg0.getData().size());
							callback.onSuccess(arg0);
						}

					});
					
				} else {
					load.setAllChildren(true);
					ModelServiceFactory.get().getTemplateInstances(CMDBSession.get().getToken(), mdr, load, new AsyncCallback<BasePagingLoadResult<CIModel>>() {

						public void onFailure(Throwable arg0) {
							callback.onFailure(arg0);
						}


						public void onSuccess(BasePagingLoadResult<CIModel> arg0) {
							for (CIModel m : arg0.getData()) {
								m.set(CIModel.CI_NAME_AND_ICON, m.getNameAndIcon());
								
						
							}
							callback.onSuccess(arg0);
						}

					});
				}
			}
		};
		
		BasePagingLoader loader = new BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<CIModel>>(proxy);
		ListStore<CIModel> store = new ListStore<CIModel>(loader);
	
		return(store);
	}
	
	
	public static ColumnConfig getColumnConfig(final AttributeColumnConfig config, boolean overideMulti, final CMDBPermissions perm) {
		
		ColumnConfig column = new PopupTextColumnConfig(config, perm);
		//if (config.isEditable()) {
			Field field = null;
			if (config.getType() == null) {
				config.setType("xs:string");
			}
			if (!overideMulti && config.getMaxOccurs() != 1) {
					column = new MultiColumnConfig(config, perm);
					
					/*
					MultiValueComboBox multi = new MultiValueComboBox(config, config.getType(), perm);
					
					CellEditor editor = new CellEditor(multi) {
						@Override  
						public Object preProcessValue(Object value) {  
							if (value == null) {  
								return value;  
							}
							return(value);
						}  

						@Override  
						public Object postProcessValue(Object value) {  
							return(value);
						}  
					};  
					column.setEditor(editor);
					*/
			} else if (config.isReference()) {
				column = new CIReferenceColumnConfig(config);
				((CIReferenceColumnConfig)column).setPermissions(perm);
			} else if (config.isComplex()) {
				if (config.isSelectTemplates()) {
					config.setSelectTemplates(true);
					column = new CITemplateColumnConfig(config);
					((CITemplateColumnConfig)column).setPermissions(perm);
				
				} else if (CMDBSession.get().getConfig().useTableComboBox()) {
					column = new CITableColumnConfig2(config, perm);	
				} else {
					column = new CIColumnConfig(config);					
					((CIColumnConfig)column).setPermissions(perm);
					
					//((CITableColumnConfig)column).setPermissions(perm);
				}
				/*
				final ComboBox<CIModel> combo = new ComboBox<CIModel>();
				combo.setPageSize(20);
				combo.setMinListWidth(250);
				combo.setWidth(250);
				combo.setTriggerAction(TriggerAction.ALL);
				CIModel type = new CIModel();
				type.setAlias(config.getType());
				combo.setStore(getCIStore(config.getMDR(), type, config.isSelectTemplates()));
				combo.setTypeAhead(true);
				combo.setSimpleTemplate("{" + CIModel.CI_NAME_AND_ICON + "}");
				combo.setDisplayField(CIModel.CI_NAME_AND_ICON);
				CellEditor editor = new CellEditor(combo) {
					@Override  
					public Object preProcessValue(Object value) {  
						if (value == null) {  
							return value;  
						}
						if (value instanceof ValueModel) {
							CIModel model = new CIModel();
							model.setAlias(((ValueModel)value).getValue());
							return(model);
						}
						if (value instanceof CIModel) {
							return(value);
						}
						return value.toString();  
					}  

					@Override  
					public Object postProcessValue(Object value) {  
						return(value);
					}  
				};  
				column.setEditor(editor);
				*/
			} else if (config.getType().equals("xs:attribute")) {
				column = new AttributeColumnModel(config);
				((AttributeColumnModel)column).setPermissions(perm);
			} else if (config.getType().equals("xs:date")) {
			
				DateField dateField = new DateField();
				dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
				column.setDateTimeFormat(DateTimeFormat.getMediumDateFormat());
				dateField.setAutoValidate(true);
				//column.setEditor(new CellEditor(dateField));
				field = dateField;
			} else if (config.getType().equals("xs:boolean")) {
				column = new MyCheckColumnConfig();
				if (!config.isEditable()) {
					((MyCheckColumnConfig)column).setReadonly(true);
				} else {
					((MyCheckColumnConfig)column).setReadonly(!perm.getCurrentState().equals(PermissionState.EDIT));
				}
			} else if (config.getType().equals("xs:password")) {
				
				TextField textField = new TextField();
				textField.setAllowBlank(true);
				textField.setAutoValidate(true);
				textField.setPassword(true);
				field = textField;
				
			} else if (config.getType().equals("xs:anyURI")) {
				column = new URLColumnConfig();
				((URLColumnConfig)column).setReadonly(!perm.getCurrentState().equals(PermissionState.EDIT));
				TextField textField = new TextField();
				textField.setAllowBlank(true);
				textField.setAutoValidate(true);
				field = textField;
			} else if (config.getType().equals("xs:string")) {
				TextField textField = new TextField();
				textField.setAllowBlank(true);
				textField.setAutoValidate(true);
				field = textField;
			} else if (config.getType().equals("xs:icon")) {
				TextField textField = new TextField();
				textField.setAllowBlank(true);
				textField.setAutoValidate(true);
				field = textField;
				column.setRenderer(new GridCellRenderer<ModelData>() {
					public String render(ModelData row, String property,
							ColumnData cfg, int rowIndex, int colIndex,
							ListStore<ModelData> store) {
						String url = row.get(property);
						url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
						String text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>";
						return(text);
					}
				});
			} else {
				TextField textField = new TextField();
				textField.setAllowBlank(true);
				textField.setAutoValidate(true);
				field = textField;
			}
			
			if (field != null) {
				CellEditor editor = new CellEditor(field) {
					@Override  
					public Object preProcessValue(Object value) {  
						if (value == null) {  
							return value;  
						}
						if (value instanceof ValueModel) {
							String string = ((ValueModel)value).getValueDisplayName();
							
							if (config.getType().equals("xs:date")) {
								if (string == null || string.length() == 0) {
									return(null);
								}
								// XML dates can be yyyy or yyyy-MM or yyyy-MM-dd...
								try {
									return(dateFmt1.parse(string));
								} catch (Exception e) {
									try {
										return(dateFmt2.parse(string));
									} catch (Exception e1) {
										try {
											return(dateFmt3.parse(string));
										} catch (Exception e2) {
											return(null);
										}
									}
								} 
							}
							return(string);
						}
						return value.toString();  
					}  

					@Override  
					public Object postProcessValue(Object value) {
						if (value instanceof Date) {
							DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd");
							return(format.format((Date)value));
						}
						return(value);
					}  
				};  
				column.setEditor(editor);
			}
		//}
		
		// Set as readonly as default...
		if (!config.isEditable()) {
			column.setEditor(null);
		}
		
		if (!perm.getCurrentState().equals(CMDBPermissions.PermissionState.EDIT)) {
			column.setEditor(null);
		}
	
		column.setId(config.getId());
		column.setHeader(config.getName());
		column.setWidth(config.getWidth());
		column.setHidden(config.isHidden());
		if (column.getRenderer() == null) {
			column.setRenderer(new GridCellRenderer<ModelData>() {
				public String render(ModelData row, String property,
						ColumnData cfg, int rowIndex, int colIndex,
						ListStore<ModelData> store) {
					
					String text = "";
					if (row instanceof GroupCollection) {
						Object o = row.get(property);
						if (o == null) {
							o = "";					
						}
						return(o.toString());
					} else if (row instanceof CIModelCollection) {
						String split[] = property.split("\\.");
						String name = split[0];
						String attr = split[1];

						CIModel model = ((CIModelCollection)row).getCIModel(name);

						if (config.isInternal()) {
							if (attr.equals(CIModel.CI_DISPLAYNAME)) {
								if (perm.getCurrentState().equals(CMDBPermissions.PermissionState.READONLY)) {
									text = model.getDisplayName();
									String url = model.get(CIModel.CI_ICON_PATH);
									if (url != null) {
										url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
										text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>"
										+ "<a href='javascript:void()'>" +text + "</a>";
									}
								} else {
									text = model.getNameAndIcon();
								}

							} else if (attr.equals(CIModel.CI_CREATED)) {
								Date date = model.getCreateDate();
								if (date != null) {
									text = CMDBSession.get().getDateTimeFormat().format(date);
								}
							} else if (attr.equals(CIModel.CI_LASTMODIFIED)) {
								Date date = model.getLastModifiedDate();
								if (date != null) {
									text = CMDBSession.get().getDateTimeFormat().format(date);
								}
							} else if (attr.equals(CIModel.CI_ID)) {
								text = model.getIdAsString();
							} else if (attr.equals(CIModel.CI_ALIAS)) {
								text = model.getAlias();
							} else if (attr.equals(CIModel.CI_DERIVEDFROM)) {
								text = model.getDerivedFrom();
							} else if (attr.equals(CIModel.CI_DESCRIPTION)){
								text = model.getDescription();
							} else {
								text = attr + " not supported";
							}
						} else {
							ValueModel v = model.get(attr);
							if (v != null) {
								if (v instanceof ValueListModel) {
									ValueListModel listModel = (ValueListModel)v;
									List<ValueModel> list = listModel.getValues();
									text = "[" + list.size() + "]";
									String sep = "";
									for (ValueModel val : list) {
										text = text + sep + renderValueModel(val);
										if (sep.length() == 0) {
											sep = ", ";
										}
									}
								} else {
									text = renderValueModel(v);
								}
								/*
							text = v.getValueDisplayName();
							if (config.isComplex() && v.getValue() != null) {
								String url = v.get(CIModel.CI_ICON_PATH);
								if (url != null) {
									url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
									text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
								}
								//text = "<a style='background-image:url(http://localhost/onecmdb/icons/computer16.gif);background-repeat: no-repeat; background-position: left center; font-size:20px;'>&nbsp;&nbsp;</a>" + text;
								//text = "<img src='http://localhost/onecmdb/icons/CiscoRouter.png' width='16px' heigth='16px' />" + text;	
							}
								 */
							}
						}
					} else if (row instanceof ValueModel) {
						text = ((ValueModel)row).getValueDisplayName();
					} else if (row instanceof BaseModel) {
						Object value = row.get(property);
						if (value != null) {
							text = renderObject(value);
						}
					}
					if (config.getType().equals("xs:password")) {
						String t = "";
						if (text != null) {
							for (int i = 0; i < text.length(); i++) {
								t += "*";
							}
						}
						return(t);
					}
					
					return(text);
				}

			});
		}
		return(column);
	}

	public static String renderObject(Object value) {
		if (value == null) {
			return("");
		}
		if (value instanceof ValueModel) {
			return(renderValueModel((ValueModel)value));
		}
		if (value instanceof CIModel) {
			return(renderCIModel((CIModel)value));
		}
		
		
		return(value.toString());
	}

	
	private static String renderCIModel(CIModel value) {
		if (value == null) {
			return("");
		}
		return(value.getNameAndIcon());
	
	}


	public static String renderValueModel(ValueModel item) {
		String text = item.getValue();
		if (item.isComplex()) {
			text = item.getValueDisplayName();
			if (item.getValue() != null) {
				String url = item.get(CIModel.CI_ICON_PATH);
				if (url != null) {
					url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
					text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
				}
			}
			//text = "<a style='background-image:url(http://localhost/onecmdb/icons/computer16.gif);background-repeat: no-repeat; background-position: left center; font-size:20px;'>&nbsp;&nbsp;</a>" + text;
			//text = "<img src='http://localhost/onecmdb/icons/CiscoRouter.png' width='16px' heigth='16px' />" + text;	
		}
		if (text == null) {
			return("");
		}
		return(text);
	}
	/**
	 * 
	 * @param mdr
	 * @param id
	 * @param header
	 * @param width
	 * @param editable
	 * @param type
	 * @param maxOccurs
	 * @param complex
	 * @param selectTemplate
	 * @return
	 */
	public static ColumnConfig getColumn(ContentData mdr, String id, String header, int width, boolean editable, final String type, int maxOccurs, final boolean complex, boolean selectTemplate) {
		ColumnConfig column = new ColumnConfig();
		
		
			Field field = null;
		
			if (complex) {
				if (CMDBSession.get().getConfig().useTreeComboBox()) {
					AttributeColumnConfig config = new AttributeColumnConfig();
					config.setMDR(mdr);
					config.setComplex(complex);
					config.setType(type);
					config.setId(id);
					config.setSelectTemplates(selectTemplate);

					column = new CITemplateColumnConfig(config);
				} else {

					final ComboBox<CIModel> combo = new ComboBox<CIModel>();
					if (selectTemplate) {
						combo.setPageSize(20);
					} else {
						combo.setPageSize(20);
					}
					combo.setTriggerAction(TriggerAction.ALL);
					CIModel ci = new CIModel();
					ci.setAlias(type);
					CIProxy proxy = new CIProxy(mdr, ci, selectTemplate);

					BasePagingLoader loader = new BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<CIModel>>(proxy);
					ListStore<CIModel> store = new ListStore<CIModel>(loader);
					//combo.setStore(getCIStore(mdr, ci, selectTemplate));
					combo.setStore(store);
					combo.setTypeAhead(true);
					combo.setSimpleTemplate("{" + CIModel.CI_NAME_AND_ICON + "}");
					combo.setDisplayField(CIModel.CI_NAME_AND_ICON);
					CellEditor editor = new CellEditor(combo) {
						@Override  
						public Object preProcessValue(Object value) {  
							if (value == null) {  
								return value;  
							}
							if (value instanceof ValueModel) {
								CIModel model = new CIModel();
								model.setAlias(((ValueModel)value).getValue());
								return(model);
							}
							if (value instanceof CIModel) {
								return(value);
							}
							return value.toString();  
						}  

						@Override  
						public Object postProcessValue(Object value) {  
							return(value);
						}  
					};  
					column.setEditor(editor);

				}
			} else if (type.equals("xs:date")) {
				DateField dateField = new DateField();
				dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
				column.setDateTimeFormat(DateTimeFormat.getMediumDateFormat());
				dateField.setAutoValidate(true);
				column.setEditor(new CellEditor(dateField));
				field = dateField;
			} else if (type.equals("xs:simpleTypes")) {
				final SimpleComboBox<String> combo = new SimpleComboBox<String>();  
				combo.add("xs:string");  
				combo.add("xs:boolean");  
				combo.add("xs:integer");  
				combo.add("xs:float");  
				combo.add("xs:date");  
				combo.add("xs:time");  
				combo.add("xs:dateTime");  
				combo.add("xs:password");  
				combo.add("xs:anyURI");  
				
				CellEditor editor = new CellEditor(combo) {  
					@Override  
					public Object preProcessValue(Object value) {  
						if (value == null) {  
							return value;  
						}  
						return combo.findModel(value.toString());  
					}  

					@Override  
					public Object postProcessValue(Object value) {  
						if (value == null) {  
							return value;  
						} 
						if (value instanceof ModelData) {
							return ((ModelData) value).get("value");
						}
						return(null);
					}  
				};  
				column.setEditor(editor);
				
			} else if (type.equals("xs:integer")) {
				column.setAlignment(HorizontalAlignment.RIGHT);    
				column.setEditor(new CellEditor(new NumberField()));  
			} else if (type.equals("xs:boolean")) {
				column = new MyCheckColumnConfig();
				((MyCheckColumnConfig)column).setReadonly(!editable);
			} else if (type.equals("xs:password")) {
				TextField textField = new TextField();
				textField.setAllowBlank(false);
				textField.setAutoValidate(true);
				textField.setPassword(true);
				field = textField;
			} else if (type.equals("xs:string")) {
				TextField textField = new TextField();
				//textField.setAllowBlank(false);
				textField.setAutoValidate(true);
				field = textField;
			} else {
				TextField textField = new TextField();
				//textField.setAllowBlank(false);
				textField.setAutoValidate(true);
				field = textField;
			}
			if (field != null) {
				CellEditor editor = new CellEditor(field) {
					@Override  
					public Object preProcessValue(Object value) {  
						if (value == null) {  
							return value;  
						}
						if (value instanceof ValueModel) {
							return(((ValueModel)value).getValueDisplayName());
						}
						return value.toString();  
					}  

					@Override  
					public Object postProcessValue(Object value) {
						if (value instanceof Date) {
							DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd");
							return(format.format((Date)value));
						}
						
						if (value instanceof Integer) {
							return(value.toString());
						}
						
						return(value);
					}  
				};  
				column.setEditor(editor);
			}
		
		
		// Set as readonly as default...
		if (!editable) {
			column.setEditor(null);
			//column.getEditor().getField().setReadOnly(true);
		}
		
		
		
		column.setId(id);
		column.setHeader(header);
		column.setWidth(width);
		if (column.getRenderer() == null) {
		column.setRenderer(new GridCellRenderer<ModelItem>() {
			public String render(ModelItem row, String property,
					ColumnData cfg, int rowIndex, int colIndex,
					ListStore<ModelItem> store) {
				if (row instanceof CIModelCollection) {
					
					String split[] = property.split("\\.");
					String name = split[0];
					String attr = split[1];

					CIModel model = ((CIModelCollection)row).getCIModel(name);

					String text = "";
					if (attr.equals(CIModel.CI_DISPLAYNAME)) {
						text = model.getNameAndIcon();
						/*
						if (model.isTemplate()) {
							text = model.getAlias();
						} else {
							text = model.getDisplayName();
						}
						String url = model.get(CIModel.CI_ICON_PATH);
						if (url != null) {
							url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
							text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
						}
						*/
					} else {
						ValueModel v = model.get(attr);
						if (v != null) {
							text = v.getValueDisplayName();
							if (complex && v.getValue() != null) {
								String url = v.get(CIModel.CI_ICON_PATH);
								if (url != null) {
									url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
									text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
								}
								//text = "<a style='background-image:url(http://localhost/onecmdb/icons/computer16.gif);background-repeat: no-repeat; background-position: left center; font-size:20px;'>&nbsp;&nbsp;</a>" + text;
								//text = "<img src='http://localhost/onecmdb/icons/CiscoRouter.png' width='16px' heigth='16px' />" + text;	
							}
						}
					}
					return(text);
				}
				if (row instanceof AttributeModel) {
					Object o = row.get(property);
					if (o == null) {
						return("");
					}
					if (o instanceof String) {
						return((String)o);
					}
					if (o instanceof Boolean) {
						return(o.toString());
					}
					if (o instanceof CIModel) {
						CIModel m = (CIModel)o;
						String text = m.getAlias();
						if (text == null) {
							text = m.getDisplayName();
						}
						String url = m.get(CIModel.CI_ICON_PATH);
						if (url != null) {
							url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
							text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
						}
						return(text);
					}
					return(o.toString());
				}
				Object value = row.get(property);
				if (value instanceof CIModel) {
					CIModel m = (CIModel)value;
					String text = m.getAlias();
					if (text == null) {
						text = m.getDisplayName();
					}
					String url = m.get(CIModel.CI_ICON_PATH);
					if (url != null) {
						url = CMDBSession.get().getContentRepositoryURL() + "/" + url;
						text = "<a style='background-image:url(" + url + ");background-repeat: no-repeat; background-position: left center; font-size:16px;'>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;</a>" + text;
					}
					return(text);
				}
				if (type.equals("xs:boolean")) {
					if (value == null) {
						return("false");
					}
				}
				if (value == null) {
					return("");
				}
				return(value.toString());
			}
		});
		}
		return(column);
	}
}
