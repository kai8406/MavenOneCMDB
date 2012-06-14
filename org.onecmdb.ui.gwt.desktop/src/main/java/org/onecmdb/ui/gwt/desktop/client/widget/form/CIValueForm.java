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

import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Editor;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class CIValueForm extends LayoutContainer {
	
	private GridModelConfig gridConfig;
	private ListStore<CIModelCollection> store;
	private ModelData model;
	private TabItem descriptionTab;
	private TabItem historyTab;

	/*
	public CIValueForm(GridModelConfig config, ListStore<CIModelCollection> store, ModelData m) {
		this.gridConfig = config;
		this.store = store;
		this.model = m;
	}
	*/
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		// Load GridConfig for all items.
		initUI();
	}

	protected void initUI() {
		// Layout Fields.
		setLayout(new BorderLayout());  
		
		List<ColumnConfig> internalAttr = new ArrayList<ColumnConfig>();
		List<ColumnConfig> ciAttr = new ArrayList<ColumnConfig>();
		/*
		for (AttributeColumnConfig aConfig : gridConfig.getColumnConfig()) {
			ColumnConfig cfg = EditorFactory.getColumnConfig(aConfig, false);
			if (aConfig.isInternal()) {
				internalAttr.add(cfg);
			} else {
				ciAttr.add(cfg);
			}
		}
		*/
		
		LayoutContainer internalPanel = getForm(internalAttr, 2, LabelAlign.LEFT);
		LayoutContainer ciPanel = getForm(ciAttr, 3, LabelAlign.TOP);
		
		ContentPanel internalCp = new ContentPanel(); 
	
		internalCp.setLayout(new FitLayout());
		internalCp.setAutoWidth(true);
		internalCp.setAutoHeight(true);
		internalCp.setHeading("Internal Attributes");  
		internalCp.setCollapsible(true);
		internalCp.add(internalPanel);
		
		
		ContentPanel attrCp = new ContentPanel();
		
		ToolBar toolbar = new ToolBar();
		attrCp.setTopComponent(toolbar);
		toolbar.add(new FillToolItem());
		toolbar.add(new TextToolItem("Ok"));		
		toolbar.add(new TextToolItem("Cancel"));
		attrCp.setLayout(new FitLayout());
		attrCp.setScrollMode(Scroll.ALWAYS);
		attrCp.setLayoutOnChange(true);
		attrCp.setCollapsible(true);
		attrCp.add(ciPanel);
		attrCp.setHeading("Attributes");
		
		TabPanel infoTab = new TabPanel();  
		descriptionTab = new TabItem("Description");
		descriptionTab.setStyleName("property-panel-background");

		infoTab.add(descriptionTab);
		
		historyTab = new TabItem("History");
		historyTab.setStyleName("property-panel-background");
		infoTab.add(historyTab);
		
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(0, 5, 0, 5));  
	       
	    /*   
	    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH);  
	    northData.setSplit(false);  
	    northData.setCollapsible(true);  
	    northData.setMargins(new Margins(5));  
	    */
	    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 0.3f);  
	    southData.setSplit(true);  
	    southData.setCollapsible(true);  
	    southData.setMargins(new Margins(5));  
		
	    LayoutContainer fieldCont = new LayoutContainer();
	    fieldCont.setLayout(new RowLayout());
	    fieldCont.add(internalCp, new RowData(1,-1));
	    fieldCont.add(attrCp, new RowData(1,1));
	    
	    
	    add(fieldCont, centerData);
		add(infoTab, southData);
		
		layout();
	}
	
	private AttributeColumnConfig getAttrConfig(ColumnConfig config) {
		for (AttributeColumnConfig cfg : gridConfig.getColumnConfig()) {
			if (cfg.getId().equals(config.getId())) {
				return(cfg);
			}
		}
		return(null);
	}
	
	protected LayoutContainer getForm(List<ColumnConfig> colCfgs, int cols, LabelAlign labelAlign) {
		List<LayoutContainer> fieldContainers = new ArrayList<LayoutContainer>();
		for (int i = 0; i < cols; i++) {
			LayoutContainer container = new LayoutContainer();
			
			fieldContainers.add(container);
			FormLayout layout = new FormLayout();  
			layout.setLabelAlign(labelAlign);  
			layout.setLabelWidth(150);
			layout.setDefaultWidth(150);
			container.setLayout(layout);  
		}
		
		int index = 0;
		for (final ColumnConfig colCfg : colCfgs) {
			Field f = null;
			if (colCfg.getEditor() != null) {
				
				final Editor editor = colCfg.getEditor();
				
				Object value = editor.preProcessValue(model.get(colCfg.getId()));
				
				if (editor.getField() instanceof ComboBox) {
					editor.getField().setPropertyEditor(new ListModelPropertyEditor() {
						
						public String getStringValue(Object value) {
							if (value == null) {
								return("");
							}
							
							if (value instanceof ValueModel) {
								return((ValueModel)value).getValueDisplayName();
							}
							return(value.toString());
						}
						
					});
				} else if (editor.getField() instanceof DateField) { 
				} else {
					editor.getField().setPropertyEditor(new PropertyEditor() {
			
					public String getStringValue(Object value) {
						if (value == null) {
							return("");
						}
						
						if (value instanceof ValueModel) {
							return((ValueModel)value).getValueDisplayName();
						}
						return(value.toString());
					}

					public Object convertStringValue(String value) {
						return(value);
					}
				});
				}
				editor.getField().setValue(value);
				
				f = editor.getField();
				f.addListener(Events.Focus, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {
						descriptionTab.removeAll();
						String text = "<b>Description for attribute " + colCfg.getHeader() + "</b><br>";
						text = text + "<hr><code>" + getAttrConfig(colCfg).getDescription() + "</code>";
						descriptionTab.addText(text);
						descriptionTab.layout();
					}
				});
				
			} else {
				f = new LabelField();
				f.setId(colCfg.getId());
				f.setValue(colCfg.getRenderer().render(model, colCfg.getId(), null, 0, 0, store));
			}
			
			f.setFieldLabel(colCfg.getHeader());
	
			LayoutContainer cont = fieldContainers.get(index);
			cont.add(f);
			index++;
			if (index >= fieldContainers.size()) {
				index = 0;
			}
		}
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleName("property-panel-background");
		hp.setTableWidth("100%");
		hp.setTableHeight("100%");
		for (LayoutContainer l : fieldContainers) {
			TableData td = new TableData();
			td.setHorizontalAlign(HorizontalAlignment.LEFT);
			hp.add(l, td);
		}
		return(hp);
	}
	/*
	public void updateModel(BaseModel m) {
		// TODO:
	}
	*/
}
