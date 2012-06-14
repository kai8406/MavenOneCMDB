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

import javax.activation.FileTypeMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.ParentBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.transform.IDataSource;
import org.onecmdb.core.utils.transform.SimpleTransformProvider;
import org.onecmdb.core.utils.transform.TransformBeanProvider;
import org.onecmdb.core.utils.transform.csv.CSVDataSource;
import org.onecmdb.core.utils.transform.excel.ExcelDataSource;
import org.onecmdb.core.utils.transform.jdbc.JDBCDataSourceWrapper;
import org.onecmdb.core.utils.transform.xml.XMLDataSource;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.wsdl.WSDLBeanProvider;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;


public class OneCMDBTransform extends AbstractCMDBCommand {
	private String model;
	private String transform;
	private String name;
	private String fileSource;
	private String jdbcSource;
	private String sourceType;
	private String transformType;
	private String csvProperty;
	private String output;
	private String source;
	private String validate;
	
	private HashMap<String, String> valueMap;
	
	private static String ARGS[][] = {	
		{"model", "Path to model file", null},
		{"transform", "Path to transform model file", null},
		{"name", "Name of dataset to use in the transform", null},
		{"fileSource", "URL to file data source.", null},
		{"jdbcSource", "JDBC dataSource configuration", null},
		{"output", "Output file, - stdout", "-"},
		{"valueMap", "{name1}=value1,{name2}=value2", null},
		{"sourceType", "Type of fileSource data xml, csv", null},
		{"transformType", "Type of input transform, [simple|complex]", "complex"},
		{"csvProperty", "Properties for CSV data source, [headerLines=n,delimiter=x,colTextDel=x]", "headerLines=1,delimiter=|,colTextDel=\""},
		{"source", "Path to source description", null},
		{"validate", "Validate natrural keys", "true"}
	};
	
	public static void main(String argv[]) {
		if (true) {
			start(new OneCMDBTransform(), ARGS, argv);
		}
		/*
		SimpleArg arg = new SimpleArg(ARGS);
		String url = arg.getArg(ARGS[0][0], argv);
		String username = arg.getArg(ARGS[1][0], argv);
		String pwd = arg.getArg(ARGS[2][0], argv);
		String token = arg.getArg(ARGS[3][0], argv);
		String model = arg.getArg(ARGS[4][0], argv);
		String transform = arg.getArg(ARGS[5][0], argv);
		String name = arg.getArg(ARGS[6][0], argv);
		String fileSource = arg.getArg(ARGS[7][0], argv);
		String jdbcSource = arg.getArg(ARGS[8][0], argv);
		String output = arg.getArg(ARGS[9][0], argv);
		String valueMap = arg.getArg(ARGS[10][0], argv);
		String fileSourceType = arg.getArg(ARGS[11][0], argv);
		String transformType = arg.getArg(ARGS[12][0], argv);
		String csvProperties = arg.getArg(ARGS[13][0], argv);
		
		// Check mandatory ...
		if (transform == null) {
			System.out.println("--transform is mandatory");
			arg.showHelp();
		}
		if (name == null) {
			System.out.println("--name is mandatory");
			arg.showHelp();
		}
		if (fileSource == null && jdbcSource == null) {
			System.out.println("Need a data source");
			arg.showHelp();
		}
		
		OneCMDBTransform t = new OneCMDBTransform();
		t.setServiceURL(url);
		t.setUsername(username);
		t.setPwd(pwd);
		t.setToken(token);
		t.setModel(model);
		t.setTransform(transform);
		t.setName(name);
		t.setFileDataSource(fileSource);
		t.setJdbcDataSource(jdbcSource);
		t.setFileSourceType(fileSourceType);
		t.setOutput(output);
		t.setTransformType(transformType);
		//t.setCSVProperties(csvProperties);
		// Handle valuemap.
		if (valueMap != null) {
			String values[] = valueMap.split(",");
			HashMap<String, String> map = new HashMap<String, String>();
			for (String pair : values) {
				String split[] = pair.split("=");
				map.put(split[0], split[1]);
			}
			t.setValueMap(map);
		}
		try {
			t.process();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		*/
	}

	public void process() throws Exception {
		if (getOutput() == null || getOutput().equals("-")) {
			process(System.out);
		} else {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(getOutput());
				process(out);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
						// Ignore.
					}
				}
			}
		}
	}
	
	public void process(OutputStream out) throws Exception {
		// Need to call this first to determine the source type.
		IDataSource dataSource = getDataSource();
		
		TransformBeanProvider transformWorker = new TransformBeanProvider();
		transformWorker.setValueMap(valueMap);
		ParentBeanProvider transformProvider = new ParentBeanProvider();
		
		transformProvider.setInstanceProvider(getInstanceProvider());
		transformProvider.setTemplateProvider(getTemplateProvider());
		
		transformWorker.setDataSource(dataSource);
		transformWorker.setTransformProvider(transformProvider);
		transformWorker.setName(this.name);
	
		// TODO: Handle if.
		transformWorker.setWebService(getService());
		transformWorker.setToken(getToken());
		transformWorker.setValidate("true".equalsIgnoreCase(this.validate));
		
		List<CiBean> beans = transformWorker.getBeans();
	
		XmlGenerator gen = new XmlGenerator();
		gen.setBeans(beans);
		gen.transfer(out);
		
		
		// Show statistics...
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (CiBean bean : beans) {
			String key = bean.getDerivedFrom();
			Integer count = map.get(key);
			if (count == null) {
				count = new Integer(0);
				map.put(key, count);
			}
			count++;
			map.put(key, count);
		}
		for (String key : map.keySet()) {
			System.out.println(map.get(key)  + " " + key + " CIs");
		}
		System.out.println(beans.size() + " total CI's, " + transformWorker.getQueryCount() + " wsdl queries");
		
	}
	public IDataSource getDataSource(Properties p) throws IOException {
		String type = p.getProperty("type");
		setSourceType(type);
		if (type.equals("jdbc")) {
			/*
			BasicDataSource jdbcSrc = new BasicDataSource();
			jdbcSrc.setUrl(p.getProperty("db.url"));
			jdbcSrc.setUrl(p.getProperty("jdbc.url"));
			
			jdbcSrc.setDriverClassName(p.getProperty("db.driverClass"));
			jdbcSrc.setDriverClassName(p.getProperty("jdbc.driverClass"));
			
			jdbcSrc.setUsername(p.getProperty("db.user"));
			jdbcSrc.setUsername(p.getProperty("jdbc.user"));
			
			jdbcSrc.setPassword(p.getProperty("db.password"));
			jdbcSrc.setPassword(p.getProperty("jdbc.password"));
			*/
			
			JDBCDataSourceWrapper src = new JDBCDataSourceWrapper();
			
			src.setRootPath(p.getProperty("jdbc.rootPath"));
			src.setDriverLib(p.getProperty("jdbc.lib"));
		
			//src.setDataSource(jdbcSrc);
			src.setQuery(p.getProperty("db.query"));
			src.setQuery(p.getProperty("jdbc.query"));
			src.setupDataSource(p);
			return(src);
		}
		if (type.equals("csv")) {
			CSVDataSource dSource = new CSVDataSource();
			URL url = null;
			try {
				url = new URL(p.getProperty("csv.url"));
			} catch (Exception e) {
				// Append file:
				url = new URL("file:" + p.getProperty("csv.url"));
			}
			
			dSource.addURL(url);
			dSource.setRootPath(p.getProperty("csv.rootPath"));
				
			String headerLines = p.getProperty("csv.headerLines");
			String headerRow = p.getProperty("csv.headerRow");
			
			String delimiter = p.getProperty("csv.delimiter");
			String colTextDel = p.getProperty("csv.colTextDel");
			if (headerLines != null) {	
				dSource.setHeaderLines(Long.parseLong(headerLines)-1);
			}
			if (headerRow != null) {
				dSource.setHeaderRow(Integer.parseInt(headerRow)-1);
			}
			dSource.setColDelimiter(delimiter);
			dSource.setTextDelimiter(colTextDel);
			return(dSource);
		}
		if (type.equals("xml")) {
			XMLDataSource dSource = new XMLDataSource();
			URL url = null;
			try {
				url = new URL(p.getProperty("xml.url"));
			} catch (Exception e) {
				// Append file:
				url = new URL("file:" + p.getProperty("xml.url"));
			}
			
			dSource.addURL(url);
			dSource.setRootPath(p.getProperty("xml.rootPath"));
			
			return(dSource);
		}
		if (type.equals("excel")) {
			ExcelDataSource dSource = new ExcelDataSource();
			URL url = null;
			try {
				url = new URL(p.getProperty("excel.url"));
			} catch (Exception e) {
				// Append file:
				url = new URL("file:" + p.getProperty("excel.url"));
			}
			
			dSource.addURL(url);
			
			dSource.setRootPath(p.getProperty("excel.rootPath"));
			String headerLines = p.getProperty("excel.headerLines");
			String headerRow = p.getProperty("excel.headerRow");
			if (headerLines != null) {	
				dSource.setHeaderLines(Long.parseLong(headerLines)-1);
			}
			if (headerRow != null) {
				dSource.setHeaderRow(Integer.parseInt(headerRow)-1);
			}
		
			String sheet = p.getProperty("excel.sheet");
			dSource.setSheet(sheet);
			
			return(dSource);
		}
		return(null);
	}
	
	public IDataSource getDataSource() throws Exception {
		if (this.source != null) {
			Properties p = new Properties();
			FileInputStream in = new FileInputStream(this.source);
			boolean loaded = false;
			try {
				p.loadFromXML(in);
				loaded = true;
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				in.close();
			}
			if (!loaded) {
				in = new FileInputStream(this.source);
				try {
					p.load(in);
				} finally {
					in.close();
				}
			}
			return(getDataSource(p));
		}
		
		if (this.jdbcSource != null) {
			Properties p = new Properties();
			FileInputStream in = new FileInputStream(this.jdbcSource);
			try {
				p.loadFromXML(in);
			} finally {
				in.close();
			}
			BasicDataSource jdbcSrc = new BasicDataSource();
			jdbcSrc.setUrl(p.getProperty("db.url"));
			jdbcSrc.setDriverClassName(p.getProperty("db.driverClass"));
			jdbcSrc.setUsername(p.getProperty("db.user"));
			jdbcSrc.setPassword(p.getProperty("db.password"));
			
			JDBCDataSourceWrapper src = new JDBCDataSourceWrapper();
			src.setDataSource(jdbcSrc);
			
			return(src);
		}
		// Else plain file...
		if (fileSource == null) {
			throw new IOException("No data source specified!");
		}
		if ("xml".equalsIgnoreCase(sourceType) || fileSource.endsWith(".xml")) {
			XMLDataSource dSource = new XMLDataSource();
			dSource.addURL(new URL(fileSource));
			return(dSource);
		}
		if ("csv".equalsIgnoreCase(sourceType) || fileSource.endsWith(".csv")) {
			CSVDataSource dSource = new CSVDataSource();
			dSource.addURL(new URL("file:" + fileSource));
			if (csvProperty != null) {
				HashMap<String, String> map = toMap(csvProperty, ",");
				String headerLines = map.get("headerLines");
				String delimiter = map.get("delimiter");
				String colTextDel = map.get("colTextDel");
				if (headerLines != null) {	
					dSource.setHeaderLines(Long.parseLong(headerLines));
				}
				dSource.setColDelimiter(delimiter);
				dSource.setTextDelimiter(colTextDel);
			}
			return(dSource);
		}
		throw new IOException("Data source <" + fileSource + "> extension not supported!");
	}

	private HashMap<String, String> toMap(String propList, String del) {
		String split[] = propList.split(del);
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < split.length; i++) {
			String keyValue[] = split[i].split("=", 2);
			if (keyValue.length == 2) {
				String key = keyValue[0];
				String value = keyValue[1];
				map.put(key, value);
			}
		}
		return(map);
	}

	private IBeanProvider getTemplateProvider() throws Exception {
		return(new WSDLBeanProvider(getService(), getToken()));
	}

	private IBeanProvider getInstanceProvider() throws Exception {
		if (this.transform == null) {
			throw new IllegalArgumentException("No transform is set!");
		}
		
		IBeanProvider provider = null;
		
		if ("simple".equalsIgnoreCase(transformType)) {
			SimpleTransformProvider simpleProvider = new SimpleTransformProvider();
			simpleProvider.setInput(this.transform);
			simpleProvider.setType(getSourceType());
			provider = simpleProvider;
		} else {
			XmlParser parser = new XmlParser();
			File f = new File(this.transform);
			parser.addURL(f.toURL().toExternalForm());

			if (this.model != null) {
				f = new File(this.model);
				parser.addURL(f.toURL().toExternalForm());
			}
			provider = parser;
		}
		return(provider);
	}

	public HashMap<String, String> getValueMap() {
		return valueMap;
	}

	public void setValueMap(HashMap<String, String> valueMap) {
		this.valueMap = valueMap;
	}
	
	public void setValueMap(String valueMap) {
		if (valueMap != null) {
			String values[] = valueMap.split(",");
			HashMap<String, String> map = new HashMap<String, String>();
			for (String pair : values) {
				String split[] = pair.split("=");
				map.put(split[0], split[1]);
			}
			setValueMap(map);
		}
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTransform() {
		return transform;
	}

	public void setTransform(String transform) {
		this.transform = transform;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}

	public String getJdbcSource() {
		return jdbcSource;
	}

	public void setJdbcSource(String jdbcSource) {
		this.jdbcSource = jdbcSource;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getTransformType() {
		return transformType;
	}

	public void setTransformType(String transformType) {
		this.transformType = transformType;
	}

	public String getCsvProperty() {
		return csvProperty;
	}

	public void setCsvProperty(String csvProperty) {
		this.csvProperty = csvProperty;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	
}
