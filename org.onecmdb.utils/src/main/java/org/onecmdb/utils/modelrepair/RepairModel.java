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
package org.onecmdb.utils.modelrepair;

import java.util.List;

import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;
import org.onecmdb.utils.wsdl.AbstractCMDBCommand;

public class RepairModel extends AbstractCMDBCommand {
	private static String ARGS[][] = {
		{"in", "Input Model File", null},
		{"out", "Output Model File (- stdout)", "-"}
	};
	public static void main(String argv[]) {
		RepairModel cmd = new RepairModel();
		start(cmd, ARGS, argv);
		
	}
	
	String in;
	String out;
	
	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}


	@Override
	public void process() throws Exception {
		XmlParser parser = new XmlParser();
		parser.setURL("file:" + in);
		
		List<CiBean> beans = parser.getBeans();
		
		// Fix 1: Remove Illegal derived from values...
		for (CiBean bean : beans) {
			if (!bean.isTemplate()) {
				continue;
			}
			CiBean parent = parser.getBean(bean.getDerivedFrom());
			if (parent == null) {
				continue;
			}
			for (AttributeBean aBean : bean.getAttributes()) {
				// Check if this defined here...
				if (parent.getAttribute(aBean.getAlias()) != null) {
					// Modify ...
					if (!aBean.isDerived()) {
						System.out.println("Corrected Attribute " + aBean.getAlias() + " in " + bean.getAlias());
						aBean.setDerived(true);
					}
				}
			}
		}
		
		// Fix 2: Check for not valid values.
		for (CiBean bean : beans) {
			for (ValueBean vBean : bean.getAttributeValues()) {
				if (!vBean.isComplexValue()) {
					continue;
				}
				String alias = vBean.getValue();
				if (!validateComplexValue(parser, bean, vBean)) {
					// Reset value...
					System.out.println("Corrected Value " + alias + " on " + vBean.getAlias() + " in " + bean.getAlias());
					vBean.setValue((String)null);
					vBean.setValue((CiBean)null);
				}
			}
		}
		
		
		// Write this out ....
		XmlGenerator gen = new XmlGenerator();
		gen.setBeans(beans);
		gen.setOutput(out);
		gen.process();
	}

	private AttributeBean findAttribute(XmlParser parser, CiBean bean, String alias) {
		String templateAlias = bean.getDerivedFrom();
		if (bean.isTemplate()) {
			templateAlias = bean.getAlias();
		}
		CiBean template = parser.getBean(templateAlias);
		AttributeBean aBean = template.getAttribute(alias);
		if (aBean != null) {
			return(aBean);
		}
		CiBean parent = parser.getBean(template.getDerivedFrom());
		if (parent == null) {
			return(null);
		}
		return(findAttribute(parser, parent, alias));
		
	}
	private boolean validateComplexValue(XmlParser parser, CiBean bean, ValueBean value) {
		AttributeBean aBean = findAttribute(parser, bean, value.getAlias());
		if (aBean == null) {
			System.out.println("Attr " + value.getAlias() + " not found in template " + bean.getDerivedFrom());
			return(false);
		}
		String typeAlias = aBean.getType();
		String alias = value.getValue();
		if (alias == null) {
			return(true);
		}
		return (isValueFromType(parser, typeAlias, alias));
		
	}

	private boolean isValueFromType(XmlParser parser, String typeAlias,
			String valueType) {
	
		CiBean bean = parser.getBean(valueType);
		if (bean == null) {
			return(false);
		}
		if (typeAlias.equals(bean.getDerivedFrom())) {
			return(true);
		}
		
		return(isValueFromType(parser, typeAlias, bean.getDerivedFrom()));
	}
	

}
