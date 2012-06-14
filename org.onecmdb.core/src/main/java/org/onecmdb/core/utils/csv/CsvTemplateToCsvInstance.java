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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;

public class CsvTemplateToCsvInstance {
	HashMap<CiBean, List<CiBean>> beanMap = new HashMap<CiBean, List<CiBean>>();
	IBeanProvider provider;
	
	
	public static String help() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Arguments:" +"\n");
		buffer.append("\tInputFile OutputFile" + "\n");
		return(buffer.toString());
	}
	
	public static void main(String argv[]) {
		
		if (argv.length < 2) {
			System.out.println(help());
			System.exit(1);
		}
		
		String inFile = argv[0];
		String outFile = argv[1];
		OutputStream out = null;
		final CsvTemplateToXml csvTemplate = new CsvTemplateToXml(inFile, null);
		try {	
			out = new FileOutputStream(outFile);
			csvTemplate.parse();
			CsvTemplateToCsvInstance csvConv = new CsvTemplateToCsvInstance(new IBeanProvider() {

				public List<CiBean> getBeans() {
					return(csvTemplate.getBeans());
				}

				public CiBean getBean(String alias) {
					// TODO Auto-generated method stub
					return null;
				}
				
			});
			csvConv.transfer(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	public CsvTemplateToCsvInstance(IBeanProvider provider) {
		this.provider = provider;
	}
	
	public void transfer(OutputStream out) {
		PrintStream pout = null;
		
		if (out instanceof PrintStream) {
			pout = (PrintStream)out;
		} else {
			pout = new PrintStream(out);
		}
		
		
		for (CiBean bean : provider.getBeans()) {
			if (bean.isTemplate()) {
				getInstanceList(bean);
			} else {
				CiBean template = provider.getBean(bean.getDerivedFrom());
				if (template != null) {
					getInstanceList(template).add(bean);
				}
			}
		}
		
		for (CiBean template : beanMap.keySet()) {			
			printBean(pout, template, beanMap.get(template));			
		}
		try {
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<CiBean> getInstanceList(CiBean bean) {
		List<CiBean> instanceList = beanMap.get(bean);
		if (instanceList == null) {
			instanceList = new ArrayList<CiBean>();
			beanMap.put(bean, instanceList);
		}
		return(instanceList);
	}
	
	private void printBean(PrintStream pout, CiBean template, List<CiBean> instances) {
		pout.print(template.getAlias() + ":Template");
		pout.print(";");
		List<String> attributeOffset = new ArrayList<String>();
		for (AttributeBean aBean : template.getAttributes()) {
			if (aBean.isComplexType()) {
				pout.print(">" + aBean.getAlias());
			} else {
				pout.print(aBean.getAlias());
			}
			pout.print(";");
			attributeOffset.add(aBean.getAlias());
		}
		pout.println();
		
		for (CiBean instance : instances) {
			pout.print(";");
			pout.print(template.getAlias() + ":" + instance.getAlias());
			pout.print(";");
			for (String aAlias : attributeOffset) {
				List<ValueBean> values = instance.fetchAttributeValueBeans(aAlias);
				if (values == null || values.size() == 0) {
					pout.print(";");
					continue;
				}
				boolean first = true;
				for (ValueBean value : values) {
					if (!first) {
						pout.print(", ");
						first = false;
					}
					pout.print(value.getValue());
				}
			}
		}
		pout.println();
	
		
	}
}
