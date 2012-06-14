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

import org.onecmdb.ui.gwt.desktop.client.desktop.Plant;
import org.onecmdb.ui.gwt.desktop.client.desktop.Stock;
import org.onecmdb.ui.gwt.desktop.client.desktop.TestData;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;


public class TestEditableGrid  extends LayoutContainer { 
		     
	public TestEditableGrid() {  
		setLayout(new FlowLayout(10));  

		List<Stock> stocks = TestData.getStocks();  
		for (Stock s : stocks) {  
			DateWrapper w = new DateWrapper();  
			w = w.clearTime();  
			w = w.addDays((int) (Math.random() * 1000));  
			s.set("date", w.asDate());  
		}  

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		ColumnConfig column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("Common Name");  
		column.setWidth(220);  

		TextField<String> text = new TextField<String>();  
		text.setAllowBlank(false);  
		text.setAutoValidate(true);  
		column.setEditor(new CellEditor(text));  
		configs.add(column);  

		final SimpleComboBox<String> combo = new SimpleComboBox<String>();  
		combo.add("Shade");  
		combo.add("Mostly Shady");  
		combo.add("Sun or Shade");  
		combo.add("Mostly Sunny");  
		combo.add("Sunny");  

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
				return ((ModelData) value).get("value");  
			}  
		};  

		column = new ColumnConfig();  
		column.setId("light");  
		column.setHeader("Light");  
		column.setWidth(130);  
		column.setEditor(editor);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("price");  
		column.setHeader("Price");  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setWidth(70);  
		column.setNumberFormat(NumberFormat.getCurrencyFormat());  
		column.setEditor(new CellEditor(new NumberField()));  

		configs.add(column);  

		DateField dateField = new DateField();  
		dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("MM/dd/y"));  

		column = new ColumnConfig();  
		column.setId("available");  
		column.setHeader("Available");  
		column.setWidth(95);  
		column.setEditor(new CellEditor(dateField));  
		column.setDateTimeFormat(DateTimeFormat.getMediumDateFormat());  
		configs.add(column);  

		CheckColumnConfig checkColumn = new CheckColumnConfig("indoor", "Indoor?", 55);  
		configs.add(checkColumn);  

		final ListStore<Plant> store = new ListStore<Plant>();  
		store.add(TestData.getPlants());  

		ColumnModel cm = new ColumnModel(configs);  

		ContentPanel cp = new ContentPanel();  
		cp.setHeading("Edit Plants");  
		cp.setFrame(true);  
		cp.setSize(600, 300);  
		cp.setLayout(new FitLayout());  

		final EditorGrid<Plant> grid = new EditorGrid<Plant>(store, cm);  
		grid.setAutoExpandColumn("name");  
		grid.setBorders(true);  
		grid.addPlugin(checkColumn);  
		cp.add(grid);  

		ToolBar toolBar = new ToolBar();  
		TextToolItem add = new TextToolItem("Add Plant");  
		add.addSelectionListener(new SelectionListener<ToolBarEvent>() {  

			@Override  
			public void componentSelected(ToolBarEvent ce) {  
				Plant plant = new Plant();  
				plant.setName("New Plant 1");  
				plant.setLight("Mostly Shade");  
				plant.setPrice(0);  
				plant.setAvailable(new DateWrapper().clearTime().asDate());  
				plant.setIndoor(false);  

				grid.stopEditing();  
				store.insert(plant, 0);  
				grid.startEditing(0, 0);  

			}  

		});  
		toolBar.add(add);  
		cp.setTopComponent(toolBar);  
		cp.setButtonAlign(HorizontalAlignment.CENTER);  
		cp.addButton(new Button("Reset", new SelectionListener<ButtonEvent>() {  

			@Override  
			public void componentSelected(ButtonEvent ce) {  
				store.rejectChanges();  
			}  
		}));  

		cp.addButton(new Button("Save", new SelectionListener<ButtonEvent>() {  

			@Override  
			public void componentSelected(ButtonEvent ce) {  
				store.commitChanges();  
			}  
		}));  

		add(cp);  
	}  
}
