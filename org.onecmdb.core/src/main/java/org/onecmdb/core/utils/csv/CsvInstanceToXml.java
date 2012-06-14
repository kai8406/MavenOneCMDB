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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.internal.model.AttrbuteValueSelector;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.XmlGenerator;

public class CsvInstanceToXml {
	private static final int FIND_TEMPLATE = 0x1;
	private static final int PARSE_INSTANCE = 0x2;
		
	
	private String del = ";";


	public static String help() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Arguments:" +"\n");
		buffer.append("\tInputFile [OutputFile]" + "\n");
		return(buffer.toString());
	}
	
	public static void main(String argv[]) {
		if (argv.length < 1) {
			System.out.println(help());
			System.exit(1);
		}
		String inFile = argv[0];
		String outFile = inFile + ".xml";
		if (argv.length > 1) {
			outFile = argv[1];
		}
	
		
		FileInputStream in = null;
		OutputStream out = null;
		try {
			 in = new FileInputStream(inFile);
			 if (outFile.equals("-")) {
				 out = System.out;
			 } else {
				 out = new FileOutputStream(outFile);
			 }
			 new CsvInstanceToXml().transfer(in, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(help());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}
	
	public void transfer(InputStream in, OutputStream out) throws IOException {
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
		
		boolean eof = false;
		int state = FIND_TEMPLATE;
		List<String> attributeList = null;
		List<CiBean> instances = new ArrayList<CiBean>();
		String templateName = null;
		CiBean currentInstance = null;
		int lineNo = 0;
		while(!eof) {
			String line = reader.readLine();
			if (line == null) {
				eof = true;
				continue;
			}
			lineNo++;
			String[] elements = line.split(del);
			switch(state) {
				case FIND_TEMPLATE:
					currentInstance = null;
					if (elements.length == 0) {
						continue;
					}
					if (elements[0].endsWith(":Template")) {
						state = PARSE_INSTANCE;
						attributeList = new ArrayList<String>();
						// Add all attributes.
						for (int i = 1; i < elements.length; i++) {
							attributeList.add(elements[i]);
						}
						templateName = elements[0].substring(0, elements[0].length() - ":Template".length());
					}
					break;
				case PARSE_INSTANCE:
					// Empty lines taht contains ;;;;;
					// Will not be splitted up....
					if (elements.length == 0) {
						state = FIND_TEMPLATE;
						continue;
					}
					
					if (elements[0].trim().equals("")) {
						// Check if multi value.
						boolean eofInstance = true;
						for (int i = 1; i < elements.length; i++) {
							String value = elements[i].trim();
							if (value.equals("")) {
								continue;
							}
							eofInstance = false;
							if (attributeList.size() <= (i-1)) {
								throw new IOException("Attribute name missing, index " + i + ", template " + templateName + ", lineNo " + lineNo);
							}
							String aName = attributeList.get(i-1);
							
							boolean complex = false;
							if (aName.startsWith(">")) {
								complex = true;
								aName = aName.substring(1);
							}
							currentInstance.addAttributeValue(new ValueBean(aName, value, complex));
			}
						if (eofInstance) {	
							state = FIND_TEMPLATE;
						}
						continue;
					}
					
					if (templateName == null) {
						state = FIND_TEMPLATE;
						continue;
					}
					
					currentInstance = new CiBean();
					currentInstance.setAlias(elements[0].trim());
					currentInstance.setTemplate(false);
					currentInstance.setDerivedFrom(templateName);
					
					instances.add(currentInstance);
					for (int i = 1; i < elements.length; i++) {
						String value = elements[i].trim();
						if (value.equals("")) {
							continue;
						}
						if (attributeList.size() <= (i-1)) {
							throw new IOException("Attribute name missing, index " + i + ", template " + templateName + ", lineNo " + lineNo);
						}
			
						String aName = attributeList.get(i-1);
						boolean complex = false;
						if (aName.startsWith(">")) {
							complex = true;
							aName = aName.substring(1);
						}
						currentInstance.addAttributeValue(new ValueBean(aName, value, complex));
					}
					break;
			}
			
		}
		XmlGenerator gen = new XmlGenerator();
		gen.setBeans(instances);
		gen.transfer(out);
	}

	
	private void addValue(CiBean currentInstance, ArrayList<String> attributeList, int index, String value) {
	
	}
}

