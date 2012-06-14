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
package org.onecmdb.core.utils.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;

public class CsvTemplateToXml {
	private static final int FIND_TEMPLATE = 0x1;
	private static final int FIND_TEMPLATE_DISPLAY = 0x2;
	private static final int ADD_ATTRIBUTE = 0x4;
	
	
	
	List<CiBean> bans = new ArrayList<CiBean>();
	private String file;
	private String del = ";";
	private String templateName = "Template Name";
	private String displayName = "Display Name";
	private String defaultRefType = "Reference";
	private String defaultDerivedFrom = "Ci";
	private String outFile;
	List<CiBean> beans = new ArrayList<CiBean>();
	
	public static String help() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Arguments:" +"\n");
		buffer.append("\tInputFile [OutputFile] [RootTemplate] [RootReference]" +"\n");
		buffer.append("\t - Outputfile default = InputFile-template.xml" +"\n");
		buffer.append("\t - RootTemplate default = Ci" +"\n");
		buffer.append("\t - RootRefeference default = Reference" +"\n");
		return(buffer.toString());
	}
	
	public static void main(String argv[]) {
		if (argv.length < 1) {
			System.out.println(help());
			System.exit(1);
		}
		String in = argv[0];
		String out = in + "-template.xml";
		if (argv.length > 1) {
			out = argv[1];
		}
		
		CsvTemplateToXml csvToXml  = new CsvTemplateToXml(in, out);
		if (argv.length > 2) {
			csvToXml.setDefaultRootCi(argv[2]);
		}
		if (argv.length > 3) {
			csvToXml.setDefaultReferenceCi(argv[3]);
		}
		try {
			csvToXml.parse();
		} catch (IOException e) {
			System.out.println("ERROR:" + e.toString());
			e.printStackTrace();
			System.out.println("help");
		}
		
		// Generate Instance file
		XmlParser parser = new XmlParser();
		parser.setURL("file:" + out);
		CsvTemplateToCsvInstance beanToCsv = new CsvTemplateToCsvInstance(parser);
		FileOutputStream csvInstance = null;
		try {
			csvInstance = new FileOutputStream(in + "-instance.csv");
			beanToCsv.transfer(csvInstance);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (csvInstance != null) {
				try {
					csvInstance.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	
	}

	public CsvTemplateToXml(String in, String out) {
		this.file = in;
		this.outFile = out;
	}

	private void setDefaultReferenceCi(String alias) {
		this.defaultRefType = alias;
		
	}

	private void setDefaultRootCi(String alias) {
		this.defaultDerivedFrom = alias;
		
	}

	
	
	public void parse() throws IOException {
		FileInputStream in = new FileInputStream(this.file);
		FileOutputStream out = null;
		if (this.outFile != null) {
			out = new FileOutputStream(this.outFile);
		}
		
		try {
			transfer(in, out);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}
	
	public List<CiBean> getBeans() {
		return(beans);
	}
	
	public void transfer(InputStream in, OutputStream out) throws IOException {
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
		boolean eof = false;
		int state = FIND_TEMPLATE;
		try {
			CiBean currentBean = null;
			while(!eof) {
				String line = reader.readLine();
				if (line == null) {
					eof = true;
					continue;
				}
				String split[] = line.split(del, 2);
				
				if (split.length != 2) {
					state = FIND_TEMPLATE;
					continue;
				}
				
				String name = split[0].trim();
				String value = split[1].trim();
				switch(state) {
				case FIND_TEMPLATE:
					if (name.startsWith(templateName)) {
						currentBean = new CiBean();
						currentBean.setAlias(getAlias(value));						
						currentBean.setDerivedFrom(defaultDerivedFrom);
						currentBean.setTemplate(true);
						state = FIND_TEMPLATE_DISPLAY;
					}
					break;
				case FIND_TEMPLATE_DISPLAY:
					if (name.startsWith(displayName)) {
						currentBean.setDisplayNameExpression(value);
						beans.add(currentBean);
						state=ADD_ATTRIBUTE;
						continue;
					} 
					state = FIND_TEMPLATE;
					break;
						
				case ADD_ATTRIBUTE:
					AttributeBean aBean = new AttributeBean();
					aBean.setAlias(getAlias(name));
					aBean.setDisplayName(name);
					if (value.startsWith(">")) {
						aBean.setComplexType(true);						
						aBean.setRefType(defaultRefType);
						aBean.setType(value.substring(1));
					} else {
						aBean.setComplexType(false);
						aBean.setType("xs:string");
					}
					currentBean.addAttribute(aBean);
				}
			}
									
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		if (out != null) {
			// Generate XML.
			XmlGenerator gen = new XmlGenerator();
			gen.setBeans(beans);
			gen.transfer(out);
		}
		
		
		
	}

	/**
	 * Remove unwanted characters like spaces.
	 * @param value
	 * @return
	 */
	private String getAlias(String value) {
		String alias = value.trim();
		String aliases[] = alias.split(" ");
		StringBuffer a = new StringBuffer();
		for (int i = 0; i < aliases.length; i++) {
			a.append(aliases[i]);
		}
		return(a.toString());
	}
	
}

