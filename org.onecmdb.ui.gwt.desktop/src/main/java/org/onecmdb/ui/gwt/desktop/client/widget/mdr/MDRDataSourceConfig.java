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
package org.onecmdb.ui.gwt.desktop.client.widget.mdr;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.transform.TransformModel;
import org.onecmdb.ui.gwt.desktop.client.widget.form.InputFormWidget;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class MDRDataSourceConfig extends LayoutContainer {

	private TransformModel model;
	private TransformConfig cfg;
	private String mdrName;

	public MDRDataSourceConfig(String mdrName, TransformConfig cfg) {
		this.cfg = cfg;
		this.model = cfg.getTransformModel();
		this.mdrName = mdrName;
	}
	
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}



	public void initUI() {
		setLayout(new FitLayout());
		InputFormWidget input = new InputFormWidget(this.cfg, getDataSourceConfig());
		
		input.setFieldWidth(300);
		input.setLabelWidth(100);
		add(input);
	}
	
	private List<AttributeColumnConfig> getDataSourceConfig() {
		AttributeColumnConfig type = new AttributeColumnConfig();
		type.setType("xs:radiogroup");
		type.setId("sourceType");
		type.setName("Type");
		type.addRadio("csv");
		type.addRadio("excel");
		type.addRadio("jdbc");
		type.addRadio("xml");
		type.set("csv", getDataSourceCSVConfig());
		type.set("excel", getDataSourceExcelConfig());
		type.set("jdbc", getDataSourceJDBCConfig());
		//type.set("xml", getDataSourceXMLConfig());
		
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		list.add(type);
		return(list);
	}
	
	private List<AttributeColumnConfig> getDataSourceExcelConfig() {
		AttributeColumnConfig url = new AttributeColumnConfig();
		url.setType("xs:content");
		url.setId("excel.url");
		url.setContentRoot("/MDR/" + mdrName);
		url.setName("Excel File");
		url.setTooltip("The excel file, must be located in the MDR repository directory on the server");
		
		AttributeColumnConfig sheet = new AttributeColumnConfig();
		sheet.setType("xs:string");
		sheet.setId("excel.sheet");
		sheet.setName("Sheet");
		sheet.setTooltip("The name of the sheet or the index of the sheet starting from 0");
			
		AttributeColumnConfig nHeaders = new AttributeColumnConfig();
		nHeaders.setType("xs:integer");
		nHeaders.setId("excel.headerLines");
		nHeaders.setName("Data Start Row");
		nHeaders.setTooltip("The first row in the excel sheet that should be regarded as data");
		
		AttributeColumnConfig nHeaderRow = new AttributeColumnConfig();
		nHeaderRow.setType("xs:integer");
		nHeaderRow.setId("excel.headerRow");
		nHeaderRow.setName("Header Row");
		nHeaderRow.setTooltip("Which row should be used for column headers");
		
	
		
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		list.add(url);
		list.add(sheet);
		list.add(nHeaderRow);
		list.add(nHeaders);
		return(list);
	}
	
	private List<AttributeColumnConfig> getDataSourceJDBCConfig() {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		
		AttributeColumnConfig col = new AttributeColumnConfig();
		col.setType("xs:content");
		col.setId("jdbc.lib");
		col.setContentRoot("MDR/" + mdrName);
		col.setName("JDBC Lib");
		col.setTooltip("The jar-file containing the hdbc driver for the database");
		list.add(col);
		
		/*
		<entry key="db.user">root</entry>
		<entry key="db.password"></entry>
		<entry key="db.url"></entry>
		<entry key="db.driverClass"></entry>
		<entry key="db.query">
		</entry>
		*/
		col = new AttributeColumnConfig();
		col.setType("xs:string");
		col.setId("jdbc.url");
		col.setName("DB URL");
		col.setTooltip("The url to connect to the database");
		list.add(col);
		
		col = new AttributeColumnConfig();
		col.setType("xs:string");
		col.setId("jdbc.driverClass");
		col.setName("DB Driver Class");
		col.setTooltip("The driver in the driver jar to use");
		list.add(col);
		
		col = new AttributeColumnConfig();
		col.setType("xs:string");
		col.setId("jdbc.user");
		col.setName("User");
		col.setTooltip("The username to use when connecting to the database");
		list.add(col);
		
		col = new AttributeColumnConfig();
		col.setType("xs:password");
		col.setId("jdbc.password");
		col.setName("Password");
		col.setTooltip("The password to use when connecting to the database");
		list.add(col);
	
		col = new AttributeColumnConfig();
		col.setType("xs:textarea");
		col.setId("jdbc.query");
		col.setName("SQL Query");
		col.setTooltip("Specifies the SQL query to retrive data from the database");
		list.add(col);
			
		return(list);
	}

	
	private List<AttributeColumnConfig> getDataSourceCSVConfig() {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();

		AttributeColumnConfig url = new AttributeColumnConfig();
		url.setType("xs:content");
		url.setId("csv.url");
		url.setName("CSV File");
		url.setTooltip("The csv file, must be located in the MDR repository directory on the server");
		
		list.add(url);
		
		AttributeColumnConfig nHeaderRow = new AttributeColumnConfig();
		nHeaderRow.setType("xs:integer");
		nHeaderRow.setId("csv.headerRow");
		nHeaderRow.setName("Header Row");
		nHeaderRow.setTooltip("Which row should be used for column headers");
		list.add(nHeaderRow);
			
		AttributeColumnConfig nHeaders = new AttributeColumnConfig();
		nHeaders.setType("xs:integer");
		nHeaders.setId("csv.headerLines");
		nHeaders.setName("Data Start Row");
		nHeaders.setTooltip("The first row in the csv file that should be regarded as data");
		list.add(nHeaders);
				
		AttributeColumnConfig col = new AttributeColumnConfig();
		col.setType("xs:string");
		col.setId("csv.colDel");
		col.setName("Column Delimiter");
		list.add(col);
	
		
		return(list);
	}

	private List<AttributeColumnConfig> getDataSourceXMLConfig() {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();

		AttributeColumnConfig url = new AttributeColumnConfig();
		url.setType("xs:content");
		url.setId("xml.url");
		url.setName("URL");
		list.add(url);
		
		return(list);
	}

}
