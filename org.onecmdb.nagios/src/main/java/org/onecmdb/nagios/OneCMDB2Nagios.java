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
package org.onecmdb.nagios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.modelmbean.XMLParseException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;
import org.jgroups.util.GetNetworkInterfaces1_4;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.utils.wsdl.AbstractCMDBCommand;
import org.onecmdb.utils.wsdl.CMDBChangeUpload;

/**
 * Convert Nagios XML description to OneCMDB Template/Instance Model.
 * Special handling:
 * 	use == Derived From
 *  name == Template name.
 *  register==0 --> No instance only template
 * @author niklas
 *
 */
public class OneCMDB2Nagios extends AbstractCMDBCommand {
	
	private String output;
	private HashSet<String> excludes = new HashSet<String>();
	
	private static String ARGS[][] = {
		{"output", "Output file, - stdout", "-"},
	};
	
	public static void main(String argv[]) {
		OneCMDB2Nagios nagios2cmdb = new OneCMDB2Nagios();
		nagios2cmdb.init(argv);
		
		try {
			nagios2cmdb.process();
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("ERROR:" + t.getMessage());
			System.exit(-1);
		}
		System.exit(0);
	}

	public void init(String argv[]) {
		handleArgs(ARGS, argv);
	}
	
	public void process(PrintWriter out) throws Exception {
		out.println("##");
		out.println("# Nagios config generated from OneCMDB " + new Date());
		out.println("##");
		out.println("");
		out.println("");
			
		
		// Query OneCMDB for beans.
		excludes.add("icon");
		excludes.add("check_command_arg");
		excludes.add("use");
		excludes.add("objectType");
		excludes.add("useName");


		GraphQuery q = new GraphQuery();
		ItemOffspringSelector nagios = new ItemOffspringSelector("nagios", "NAGIOS");
		nagios.setPrimary(true);
		q.addSelector(nagios);

		Graph result = getService().queryGraph(getToken(), q);
		result.buildMap();
		
		SortedSet<CiBean> sort = new TreeSet<CiBean>(new Comparator<CiBean>() {

			public int compare(CiBean o1, CiBean o2) {
				String s1 = o1.toStringValue("objectType");
				String s2 = o2.toStringValue("objectType");
				if (s1 == null || s2 == null) {
					return(0);
				}
				return(s1.compareTo(s2));
			}
			
		}); 
		List<CiBean> beans = new ArrayList<CiBean>(result.fetchAllNodeOffsprings());
		Collections.sort(beans, new Comparator<CiBean>() {

			public int compare(CiBean o1, CiBean o2) {
				String s1 = o1.toStringValue("objectType");
				String s2 = o2.toStringValue("objectType");
				if (s1 == null || s2 == null) {
					return(0);
				}
				return(s1.compareTo(s2));
			}
		});
		String currentType = "";
		for (CiBean bean : beans) {
			if (bean.isTemplate()) {
				if (bean.getDerivedFrom().equals("NAGIOS")) {
					continue;
				}
				// If register == 0 we have only created a template, else both a template and an instance
				if (!bean.toStringValue("register").equals("0")) {
					continue;
				}

			}
			String type = bean.toStringValue("objectType");
			if (type != null) {
				if (!currentType.equals(type)) {
					if (currentType.length() > 0) { 
						out.println("# End object definitions for " + currentType);
						out.println("####");
						out.println("");
						out.println("");
					}	
					out.println("####");
					out.println("# Start object definitions for " + type);
					currentType = type;
				}
			}
			handleBean(result, bean, out);
		}
	}
	
	@Override
	public void process() throws Exception {
		if (output == null || output.length() == 0) {
			throw new IllegalArgumentException("No output given!");
		}
		PrintWriter out = null;
		boolean close = false;
		try {

			if (output.equals("-")) {
				out = new PrintWriter(System.out);
			} else {
				out = new PrintWriter(new FileOutputStream(output));
				close = true;
			}
			process(out);
		} finally {
			out.flush();
			if (close && out != null) {
				out.close();
			}
		}

	}
	

	private void handleBean(Graph result, CiBean bean, PrintWriter out) {
		String name = null;
		CiBean template = bean;
		if (bean.isTemplate()) {
			name = bean.getAlias();
		} else {
			name = bean.getDerivedFrom();
			template = result.findOffspringAlias(name);
		}
		
		String defName = getNagiosType(bean);
		out.println("define " + defName + " {");
		
		// Check if we are derived.
		CiBean parent = result.findOffspringAlias(bean.getDerivedFrom());
		if (parent != null) {
			String use = parent.toStringValue("name");
			if (use != null && use.length() > 0) {
				out.println("\tuse\t" + use);
			}
		}
		for (AttributeBean aBean : template.getAttributes()) {
			
			String key = aBean.getAlias();
			if (excludes.contains(key)) {
				continue;
			}
			String value = null;
			if (aBean.isComplexType()) {
				value = handleComplex(result, defName, aBean, bean);
				if (value != null) {
					out.println("\t" + key + "\t" + value);
				}
			} else {
				// Name only on templates.
				if (key.equals("name")) {
					if (!isNameUsed(parent, bean, defName)) {
						continue;
					}
				}
				// Register only on templates
				if (key.equals("register") && !bean.isTemplate()) {
					continue;
				}
				
				List<ValueBean> list = bean.fetchAttributeValueBeans(key);
				if (list == null) {
					continue;
				}
				for (ValueBean v : list) {
					if (v == null) {
						continue;
					}
					value = v.getValue();
					if (value == null) {
						continue;
					}
					String attrName = key;
					if (defName.equals("timeperiod")) {
						if (key.equals("weekday") || key.equals("exception")) {
							String timeperiod[] = value.split(" ", 2);
							if (timeperiod.length == 2) {
								attrName = timeperiod[0];
								value = timeperiod[1];
							}
						}
					}
					if (value != null) {
						out.println("\t" + attrName + "\t" + value);
					}
				}
			}
		}
		out.println("}");
	}

	// Check if we have created both an instance and a template...
	// Then 
	private boolean isNameUsed(CiBean parent, CiBean child, String type) {
		if (child.isTemplate()) {
			return(true);
		}
		String useName = child.toStringValue("useName");
		if ("true".equals(useName)) {
			return(true);
		}
		return(false);
	}


	private String handleComplex(Graph result, String defName, AttributeBean aBean, CiBean bean) {
		boolean first = true;
		String value = null;
		for (ValueBean vBean : bean.fetchAttributeValueBeans(aBean.getAlias())) {
			if (vBean.hasEmptyValue()) {
				continue;
			}
			CiBean refBean = result.findOffspringAlias(vBean.getValue());
			
			String v = refBean.toStringValue(getNagiosType(refBean) + "_name");
			String typeObject = getNagiosType(result.findOffspringAlias(aBean.getType()));
			if (typeObject.equals("command")) {
				String args = bean.toStringValue("check_command_arg");;
				if (args != null && args.length() > 0) {
					v = v + "!" + args;
				}
			}
			if (first) {
				value = v;
				first = false;
			} else {
				value = value + "," + v;
			}
		}
		return(value);
	}


	private String getNagiosType(CiBean bean) {
		String type = bean.toStringValue("objectType");
		return(type);
		/*
		String name = null;
		if (bean.isTemplate()) {
			name = bean.getAlias();
		} else {
			name = bean.getDerivedFrom();
		}
		return(getNagiosType(name));
		*/
	}
	/*
	private String getNagiosType(String name) {	
		
		if (!name.startsWith("NAGIOS_")) {
			throw new IllegalArgumentException("CI:s alias must have prefix NAGIOS_ !");
		}
		String split[] = name.split("_");
		if (split.length < 2)  {
			throw new IllegalArgumentException("Can't determine nagios object type from "+ name + "!");
		}
		String defName = split[1];
		defName = defName.toLowerCase();
		
		return(defName);
	}
	*/
	
	public String getOutput() {
		return output;
	}


	public void setOutput(String output) {
		this.output = output;
	}

	
	
	
}
