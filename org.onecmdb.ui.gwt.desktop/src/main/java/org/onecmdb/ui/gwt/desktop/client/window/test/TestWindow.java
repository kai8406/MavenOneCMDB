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
package org.onecmdb.ui.gwt.desktop.client.window.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.AttributeModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.CIPropertyGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.multi.MultiValueComboBox;
import org.onecmdb.ui.gwt.desktop.client.widget.multi.MultiValueGrid;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class TestWindow extends CMDBAbstractWidget {
	public static final String ID = "test-window";

	public TestWindow(CMDBDesktopWindowItem item) {
		super(item);
		setLayout(new FitLayout());
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		//add(new CIPropertyGrid());
		
		layout();
		
		/*
		ContentFile mdr = new ContentFile();
		String mdrConf = item.getParams().get("mdrConfig");
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		AttributeColumnConfig config = new AttributeColumnConfig();
		config.setMDR(mdr);
		config.setName("Test");
		config.setType("Desktop");
		config.setComplex(true);
		config.setId("test");
		config.setEditable(true);
		MultiValueGrid grid = new MultiValueGrid(config);
		ValueListModel vList = new ValueListModel();
		vList.setIsComplex(true);
		vList.setAlias("test");
		
		grid.setValue(vList);
		add(grid);
		layout();
		*/
	}

	public ColumnConfig getMultiTest() {
		ColumnConfig column = new ColumnConfig();
		/*
		final MultiValueComboBox combo = new MultiValueComboBox();
		//final DateField combo = new DateField();
		
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
		column.setId("values");
		column.setHeader("Multi Editor");
		column.setWidth(220);  
		*/
		return(column);
	}
	
	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB Test View");
		desc.setDescription("A Widget that displays CI's as tables.");
		desc.addParameter("<li>None</li>");
	
		return(desc);	
	}
}
