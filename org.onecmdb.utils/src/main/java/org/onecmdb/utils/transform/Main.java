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
package org.onecmdb.utils.transform;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.BeanClassInjector;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IDataSource;
import org.onecmdb.core.utils.transform.TransformEngine;
import org.onecmdb.core.utils.transform.csv.CSVDataSource;
import org.onecmdb.core.utils.transform.xml.XMLDataSource;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.wsdl.WSDLBeanProvider;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.utils.wsdl.SimpleArg;

/**
 * Main class to preform transformation's to/from onecmdb.
 */
public class Main {
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"list", "List all transforms", null},
		{"name", "Transform using this name", null},
		{"input", "Input data source", null},
		{"output", "Output data source", "-"},
		{"verbose", "Verbose mode true/false", "false"}
	};
	
	private IOneCMDBWebService service;
	private String token;

	public Main(String wsdl, String user, String pwd) throws Exception {
		this.service = OneCMDBServiceFactory.getWebService(wsdl);
		this.token = service.auth(user, pwd);
	}
	
	public static void main(String argv[]) {
		
		SimpleArg args = new SimpleArg(ARGS);
		String wsdl = args.getArg("url", argv);
		String user = args.getArg("user", argv);
		String pwd = args.getArg("pwd", argv);
		
		String list = args.getArg("list", argv);
		
		String input = args.getArg("input", argv);
		String name = args.getArg("name", argv);
		String output = args.getArg("output", argv);
		String verbose = args.getArg("verbose", argv);
		
		if (verbose.equals("false")) {
			Appender consoleAppender = Logger.getRootLogger().getAppender("stdout");
			Logger.getRootLogger().removeAppender(consoleAppender);
		}
		
		try {
			Main start = new Main(wsdl, user, pwd);
			if (list != null) {
				start.list();
			} else {
				start.transform(input, name, output);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void transform(String source, String name, String output) throws IOException {
		WSDLBeanProvider provider = new WSDLBeanProvider(this.service, this.token);
		CiBean bean = provider.getBean(name);
		if (bean == null) {
			throw new IOException("Data Set name <" + name + "> not found!");
		}
		
		BeanClassInjector injector = new BeanClassInjector();
		injector.setBeanProvider(provider);
		Object o = injector.beanToObject(bean);
		
		if (o instanceof DataSet) {
			IDataSource dataSource = null;
			OutputStream out = null;
			try {
				DataSet dataSet = (DataSet)o;
				dataSource = getDataSource(source);
				dataSet.setDataSource(dataSource);
				TransformEngine engine = new TransformEngine();
				IBeanProvider result = engine.transform(provider, (DataSet)o);
				XmlGenerator gen = new XmlGenerator();
				gen.setBeans(result.getBeans());
				out = getOutputStream(output);
				gen.transfer(out);
			} finally {
				if (dataSource != null) {
					try {
						dataSource.close();
					} catch (IOException e) {
						// Silently ignore.
					}
			
				}
				if (out != null && !out.equals(System.out)) {
					try {
						out.close();
					} catch (IOException e) {
						// Silently ignore.
					}
				}
			}
			 
		}
	}

	private OutputStream getOutputStream(String output) throws FileNotFoundException {
		if (output.equals("-")) {
			return(System.out);
		}
		FileOutputStream out = new FileOutputStream(output);
		return(out);
	}

	private IDataSource getDataSource(String sourceURL) throws IOException {
		if (sourceURL == null) {
			throw new IOException("No data source specified!");
		}
		if (sourceURL.endsWith(".xml")) {
			XMLDataSource dSource = new XMLDataSource();
			dSource.addURL(new URL(sourceURL));
			return(dSource);
		}
		if (sourceURL.endsWith(".csv")) {
			CSVDataSource dSource = new CSVDataSource();
			dSource.addURL(new URL(sourceURL));
			return(dSource);
		}
		throw new IOException("Data source <" + sourceURL + "> extension not supported!");
		
	}

	private void list() {
		// Query for all DataSet's instances.
		CiBean dataSets[] = this.service.query(this.token, "/instance/DataSet/*", null);
		for (CiBean dataSet : dataSets) {
			System.out.println("[" + dataSet.getAlias() + "] " + dataSet.getDisplayName() + " - " + dataSet.getDescription());
		}
	}
	
}
