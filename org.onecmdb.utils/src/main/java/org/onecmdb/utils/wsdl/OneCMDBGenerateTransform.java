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
package org.onecmdb.utils.wsdl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.ParentBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.transform.IDataSource;
import org.onecmdb.core.utils.transform.SimpleTransformProvider;
import org.onecmdb.core.utils.transform.TransformBeanProvider;
import org.onecmdb.core.utils.transform.csv.CSVDataSource;
import org.onecmdb.core.utils.transform.jdbc.JDBCDataSourceWrapper;
import org.onecmdb.core.utils.transform.xml.XMLDataSource;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.wsdl.WSDLBeanProvider;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;


public class OneCMDBGenerateTransform extends AbstractCMDBCommand {
	private String input;
	private String type;
	private String output;
	
	private static String ARGS[][] = {
		{"input", "Input source xml", null},
		{"type", "The type of transform, XML, CSV or JDBC", null},
		{"output", "Output file, - stdout", "-"},
	};

	
	public static void main(String argv[]) {
		start(new OneCMDBGenerateTransform(), ARGS, argv);
	}

	@Override
	public void process() throws Exception {
		SimpleTransformProvider provider = new SimpleTransformProvider();
		provider.setInput(getInput());
		provider.setType(getType());
		
		List<CiBean> beans = provider.getBeans();
		
		XmlGenerator gen = new XmlGenerator();
		gen.setOutput(getOutput());
		gen.setBeans(beans);
		gen.process();
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
