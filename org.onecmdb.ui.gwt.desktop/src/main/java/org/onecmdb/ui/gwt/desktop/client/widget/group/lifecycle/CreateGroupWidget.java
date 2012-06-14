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
package org.onecmdb.ui.gwt.desktop.client.widget.group.lifecycle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.group.GroupDescription;
import org.onecmdb.ui.gwt.desktop.client.utils.BaseModelInspection;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EditorEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimpleCheckBox;

public class CreateGroupWidget extends LayoutContainer {

	private GroupDescription desc;
	private BaseModel inputModel;

	public CreateGroupWidget(GroupDescription desc, BaseModel input) {
		this.desc = desc;
		this.inputModel = input;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}

	public void initUI() {
		setLayout(new FitLayout());
		FormPanel simple = new FormPanel();  
		simple.setHeading("Simple Form");  
		simple.setFrame(true);  
		simple.setWidth(350);  
		
		BaseModel inputForm = this.inputModel.get("InputForm");
		
		final BaseModel data = new BaseModel();
		
		GridModelConfig gridConfig = new GridModelConfig();
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		CMDBPermissions perm = new CMDBPermissions();
		perm.setCurrentState(CMDBPermissions.PermissionState.EDIT);
		
		FormBinding binding = new FormBinding(simple);
		
		for (BaseModel param: (List<BaseModel>)inputForm.get("InputItem")) {
			
			final AttributeColumnConfig config = new AttributeColumnConfig();
			String id = param.get("id");
			String type = param.get("type");
			boolean complex = "true".equalsIgnoreCase((String)param.get("complexType"));
			String name = param.get("name");
			String refType = param.get("refType");
			
			config.setId(id);
			config.setComplex(complex);
			config.setType(type);
			config.setMaxOccurs(1);
			config.setRefType(refType);
			config.setName(name);
			config.setMDR(this.desc.getMDR());
			config.setDescription("Testing the description....");
			// Special handling for check box, and radioss
			Field field = null;
			if (type.equals("xs:enum")) {
				field = new SimpleComboBox<String>();  
				((SimpleComboBox<String>)field).add("Test 1");
				((SimpleComboBox<String>)field).add("Test 2");
				((SimpleComboBox<String>)field).add("Test 3");
				((SimpleComboBox<String>)field).add("Test 4");
				binding.addFieldBinding(new SimpleComboBoxFieldBinding(((SimpleComboBox<String>)field), config.getId()));		
			} else if (type.equals("xs:boolean")) {
				field = new CheckBox();
				data.set(config.getId(), false);
			} else if (type.equals("xs:radiogroup")) {
				field = new RadioGroup();
				List<Radio> radios = new ArrayList<Radio>();
				for (int i = 0; i < 4; i++) {
					Radio r = new Radio();
					r.setFieldLabel("R" + i);
					r.setName("R"+ i);
					data.set("R" + i, true);
					((RadioGroup)field).add(r);
				}
			} else if (type.equals("xs:textarea")) {
				field = new TextArea();
			} else {
				final ColumnConfig column = EditorFactory.getColumnConfig(config, false, perm);
				
				
				final CellEditor editor = column.getEditor();
				field = column.getEditor().getField();
			}
			field.setFieldLabel(name);
			/*
			field.setPropertyEditor(new PropertyEditor<BaseModel>() {

				public BaseModel convertStringValue(String value) {
					BaseModel m = data.get(config.getId());
					return(m);
				}
					
				public String getStringValue(BaseModel value) {
					//data.set(column.getId(), value);
					String text = "";
					if (value instanceof ValueModel) {
						ValueModel item = (ValueModel)value;
						text = item.getValue();
						if (text == null) {
							return("");
						}
						
						if (item.isComplex()) {
							text = item.getValueDisplayName();
						}
					} else if (value != null) {
						text = value.toString();
					} else {
						text = "";					
					}
					
					System.out.println("Render Value:" + value + "id=" + config.getId() + "text=" + text);
					return(text);
					
				}
				
			});
			*/
			field.addListener(Events.Change, new Listener<FieldEvent>() {

				public void handleEvent(FieldEvent be) {
					data.set(config.getId(), be.value);
				}

			});
			field.setName(id);
			simple.add(field);
		}
		binding.autoBind();
		binding.bind(data);
		
		Button verify = new Button("Verify");
		verify.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				System.out.println(BaseModelInspection.toString(0, data));
			}
			
		});
		simple.addButton(verify);
		add(simple);
	}
}
