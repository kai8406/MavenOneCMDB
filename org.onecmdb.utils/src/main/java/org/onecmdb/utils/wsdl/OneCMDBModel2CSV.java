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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.onecmdb.core.utils.MemoryBeanProvider;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;

/**
 * <code>DumpOneCMDB</code> retrieve CI and produce XML as output.
 * 
 *
 */
public class OneCMDBModel2CSV {
	private boolean compact;
	private String output;
	private String input;
	
	private MemoryBeanProvider beanProvider;
	
	
	private static String ARGS[][] = {
		{"compact", "Compact Mode. Don't export derived attriutes", "false"},
		{"input", "Input source, - stdin", "-"},
		{"output", "Output file, - stdout", "-"},
	};
	
	public static void main(String argv[]) {
		SimpleArg arg = new SimpleArg(ARGS);
		String compactStr = arg.getArg(ARGS[0][0], argv);
		String input = arg.getArg(ARGS[1][0], argv);
		String output = arg.getArg(ARGS[2][0], argv);
		
		boolean compact = Boolean.parseBoolean(compactStr);
	
		OneCMDBModel2CSV export = new OneCMDBModel2CSV();
		export.setCompact(compact);
		export.setOutput(output);
		export.setInput(input);
		try {
			export.process();
		} catch (Exception e) {
			System.err.println("ERROR:" + e.toString());
			//e.printStackTrace();
			arg.showHelp();
		}
	}

	public void process() throws Exception {
		// Disable Console logger.
		Appender consoleAppender = Logger.getRootLogger().getAppender("stdout");
		Logger.getRootLogger().removeAppender(consoleAppender);
		
		XmlParser parser = new XmlParser();
		List<CiBean> beans = null;
		if (getInput().equals("-")) {
			beans = parser.parseInputStream(System.in);
		} else {
			// Check if we have a cotrrect url else append file://
			try {
				URL url = new URL(getInput());
			} catch (Exception e) {
				setInput("file:///" + getInput());
			}
			parser.setURL(getInput());
			beans = parser.getBeans();
		}
		beanProvider = new MemoryBeanProvider(beans.toArray(new CiBean[0]));
		
		OutputStream out = null;
		if (getOutput().equals("-")) {
			// Fetch output stream.
			out = System.out;
		} else {
			out = new FileOutputStream(getOutput());
		}
		
		try {
			transfer(out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public void transfer(OutputStream out) throws IOException {
		OutputStreamWriter outw = new OutputStreamWriter(out, "UTF-8");
		PrintWriter pw = new PrintWriter(outw, true);
	
		// Header...
		String del = "|";
		pw.println("Template|DerivedFrom|Alias|Attribute|Type|Reference|Mix|Max|Derived|Descrption");
		
		showAsTree(getRoots(), pw, del);
	}
	
	private void showAsTree(List<CiBean> beans, PrintWriter pw, String del) {
		for (CiBean bean : beans) {
			bean2CSV(bean, pw, del);
			showAsTree(getChildren(bean), pw, del);
		}
	}
	
	private void bean2CSV(CiBean bean, PrintWriter pw, String del) {
		pw.print(getDerivedFromPath(bean));
		pw.print(del);
		pw.print(bean.getAlias());
		pw.print(del);
		pw.println();
		for (AttributeBean a : bean.getAttributes()) {
			if (compact && a.isDerived())  {
				continue;
			}
				
			pw.print(del);
			pw.print(del);
			pw.print(a.getAlias());
			pw.print(del);
			pw.print(a.getDisplayName());
			pw.print(del);
			pw.print(a.getType());
			pw.print(del);
			pw.print(a.getRefType());
			pw.print(del);
			pw.print(a.getMinOccurs());
			pw.print(del);
			pw.print(a.getMaxOccurs());
			pw.print(del);
			pw.print(a.isDerived());
			pw.print(del);
			pw.print(a.getDescription());
			pw.println();
		}
	}
	private String getDerivedFromPath(CiBean bean) {
		if (bean == null) {
			return("");
		}
		String parent = bean.getDerivedFrom();
		String path = getDerivedFromPath(beanProvider.getBean(parent)) + "/" + parent;
		return(path);
	}

	private List<CiBean> getRoots() {
		List<CiBean> beans = new ArrayList<CiBean>();
		for (CiBean bean : beanProvider.getBeans()) {
			String parent = bean.getDerivedFrom();
			if (beanProvider.getBean(parent) == null) {
				beans.add(bean);
			}
		}
		return(beans);
	}
	
	private List<CiBean> getChildren(CiBean parent) {
		List<CiBean> beans = new ArrayList<CiBean>();
		for (CiBean bean : beanProvider.getBeans()) {
			String parentAlias = bean.getDerivedFrom();
			if (parentAlias == null) {
				continue;
			}
			if (parentAlias.equals(parent.getAlias())) {
				beans.add(bean);
			}
		}
		return(beans);
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}


	public boolean isCompact() {
		return compact;
	}

	public void setCompact(boolean compact) {
		this.compact = compact;
	}
}
