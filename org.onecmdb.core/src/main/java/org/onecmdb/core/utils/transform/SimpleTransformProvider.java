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
package org.onecmdb.core.utils.transform;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
/**
 * Transform a easy understandable transform to transform model.
 * 
 * @author niklas
 *
 */
public class SimpleTransformProvider implements IBeanProvider {

	private static final String XPATH_INSTANCE_SELECTOR = "XPathInstanceSelector";
	private static final String XPATH_COMPLEX_ATTRIBUTE_SELECTOR = "XPathComplexAttributeSelector";
	private String input;
	private String name;
	private String attributeSelectorType;
	private String attrSelectorAlias;
	private String complexAttributeSelectorType;
	private String instanceSelectorType;
	private HashMap<String, CiBean> beanMap = new HashMap<String, CiBean>();
	private boolean transformed = false;
	private Set<String> dataSets = new HashSet<String>();
	private Set<String> referencedDataSets = new HashSet<String>();
	private String attrSelectorNameAlias;
	private String instanceSelectorTemplateAlias;
	private String instanceSelectorTemplateNameAlias;
	private Reader inputReader;
	
	

	public void setInput(String input) {
		this.input = input;
	}
	
	public Reader getInputReader() {
		return inputReader;
	}

	public void setInputReader(Reader inputReader) {
		this.inputReader = inputReader;
	}

	/**
	 * Currently allowed types are 
	 * xml,csv,jdbc,excel.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		if (type != null) {
			if (type.equalsIgnoreCase("xml")) {
				complexAttributeSelectorType =  XPATH_COMPLEX_ATTRIBUTE_SELECTOR;
				instanceSelectorType = XPATH_INSTANCE_SELECTOR;
				attributeSelectorType = "XPathAttributeSelector";
				attrSelectorAlias = "xpath";
				instanceSelectorTemplateAlias = "templatePath";
				return;
			} else if (type.equalsIgnoreCase("csv") || type.equalsIgnoreCase("excel")) {
				complexAttributeSelectorType =  "ComplexAttributeSelector";
				instanceSelectorType = "CSVInstanceSelector";
				attributeSelectorType = "CSVAttributeSelector";
				attrSelectorAlias = "colIndexStr";
				instanceSelectorTemplateAlias = "templateColString";
				attrSelectorNameAlias ="colName";
				return;
			} else if (type.equalsIgnoreCase("jdbc")) {
				complexAttributeSelectorType =  "ComplexAttributeSelector";
				instanceSelectorType = "JDBCInstanceSelector";
				attributeSelectorType = "JDBCAttributeSelector";
				attrSelectorAlias = "colString";
				attrSelectorNameAlias ="colName";
				instanceSelectorTemplateAlias = "templateColString";
				instanceSelectorTemplateNameAlias = "templateColName";
				
				return;
			}
		}
		throw new IllegalArgumentException("Type " + type + " not allowed. Require XML, CSV or JDBC");
		
	}
	
	public CiBean getBean(String alias) {
		try {
			transform();
		} catch (Throwable t) {
			throw new IllegalArgumentException("Error transforming: " + t.getMessage(), t);
		}
		CiBean bean = beanMap.get(alias);
		return(bean);	
	}

	public List<CiBean> getBeans() {
		try {
			transform();
		} catch (Throwable t) {
			throw new IllegalArgumentException("Error transforming: " + t.getMessage(), t);
		}
		List<CiBean> list = new ArrayList<CiBean>(beanMap.values());
		return(list);		
	}
	
	
	public Document getInputDocument() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = null;
		if (this.inputReader != null) {
			document = reader.read(this.inputReader);
		} else {
			document = reader.read(new File(input));
		}
		return(document);
		
	}
	
	protected void transform() throws Exception {
		if (transformed) {
			return;
		}
		transformed  = true;
		beanMap.clear();
		
		Document document = getInputDocument();
	
		Element root = document.getRootElement();
		
		//this.name = getAttributeValue(root, "name", true);
		this.name = "primary-forward";
		CiBean forward = newBean(name);
		forward.setDerivedFrom("DataSet");
		forward.setTemplate(false);
		forward.addAttributeValue(new ValueBean("name", name, false));
		// Add Dummy InstanceSelector
		forward.addAttributeValue(new ValueBean("instanceSelector", getForwardInstanceSelector(name).getAlias(), true));
		
		for (Element dataSetEl : (List<Element>)root.elements()) {
			CiBean dataSet = getDataSet(dataSetEl);
			if (dataSet == null) {
				continue;
			}
			String exclude = getAttributeValue(dataSetEl, "exclude", false);
			if (exclude != null && exclude.equalsIgnoreCase("true")) {
				continue;
			}
			CiBean bean = newBean("fw-" + dataSet.getAlias());
			bean.setDerivedFrom("ComplexAttributeSelector");
			bean.addAttributeValue(new ValueBean("dataSet", dataSet.getAlias(), true));

			forward.addAttributeValue(new ValueBean("attributeSelector", bean.getAlias(), true));
		}
		
		// Validate DataSet names.
		StringBuffer errorMsg = new StringBuffer();
		boolean error = false;
		errorMsg.append("DataSet name '"); 
		for (String refDataSet : referencedDataSets) {
			if (dataSets.contains(refDataSet)) {
				continue;
			}
			if (error) {
				errorMsg.append(", ");
			}
			errorMsg.append(refDataSet);
			error = true;
		}
		
		if (error) {
			
			errorMsg.append("' not found");
			throw new IllegalArgumentException(errorMsg.toString());
		}
		/*
		for (Element dataSetEl : (List<Element>)root.elements("DataSetSelector")) {
			CiBean dataSet = getDataSetSelector(dataSetEl);
		}
		*/
		
		
	}

	private CiBean newBean(String alias) {
		CiBean bean = beanMap.get(alias);
		if (bean == null) {	
			bean = new CiBean();
			bean.setAlias(alias);
			beanMap.put(alias, bean);
		}
		return(bean);
		
	}
	private CiBean getForwardInstanceSelector(String name) {
		CiBean bean = newBean("fw-" + name);
		bean.setDerivedFrom("ForwardInstanceSelector");
		return(bean);
	}

	private CiBean getDataSet(Element dataSetEl) {
		if (dataSetEl.getName().equals("DataSetSelector")) {
			return(getDataSetSelector(dataSetEl));
		}
		if (!dataSetEl.getName().equals("DataSet")) {
			return(null);
		}
		
		String name = getAttributeValue(dataSetEl, "name", true);
		name = "DataSet-" + name;
		dataSets.add(name);
		
		CiBean ds = newBean(name);
		ds.setDerivedFrom("DataSet");
		ds.setTemplate(false);
		
		ds.addAttributeValue(new ValueBean("name", name, false));
		
		String parent = getAttributeValue(dataSetEl, "parent", false);
		if (parent != null) {
			ds.addAttributeValue(new ValueBean("parent", parent, true));
		}
		int count = 0;
		for (Element el : (List<Element>)dataSetEl.elements()) {
			if (el.getName().contains("ForwardInstanceSelector")) {
				CiBean instance = getForwardInstanceSelector(name + count);
				ds.addAttributeValue(new ValueBean("instanceSelector", instance.getAlias(), true));
			} else if (el.getName().contains("InstanceSelector")) {
				CiBean instance = getInstanceSelector(name, el);
				ds.addAttributeValue(new ValueBean("instanceSelector", instance.getAlias(), true));
			} else {
				//if (el.getName().contains("AttributeSelector")) {
				CiBean attr = getAttributeSelector(name + "-" + count, el);
				
				ds.addAttributeValue(new ValueBean("attributeSelector", attr.getAlias(), true));
			}
			count++;
		}
		
		return(ds);
	}

	

	private CiBean getDataSetSelector(Element dataSetEl) {
		String name = getAttributeValue(dataSetEl, "name", true);
		CiBean ds = newBean(name);
		ds.setDerivedFrom("DataSetSelector");
		ds.setTemplate(false);
		
		dataSets.add(name);

		ds.addAttributeValue(new ValueBean("name", name, false));
		int count = 0;
		for (Element el : (List<Element>)dataSetEl.elements()) {
			if (el.getName().contains("InstanceSelector")) {
				CiBean instance = getInstanceSelector(name, el);
				ds.addAttributeValue(new ValueBean("instanceSelector", instance.getAlias(), true));
			} 
			
			if (el.getName().contains("RegExpMatcher")) {
				CiBean regExpr = getRegExpr(name, el, count);
				ds.addAttributeValue(new ValueBean("dataSetMatcher", regExpr.getAlias(), true));
				count++;
			}
		}
		
		return(ds);
	}

	private CiBean getRegExpr(String name, Element el, int count) {
		CiBean ds = newBean("regExpr-" + name + "-" + count);
		ds.setDerivedFrom("RegExprMatcher");
		ds.setTemplate(false);
		
		ds.addAttributeValue(new ValueBean("regExpr", getAttributeValue(el, "regExpr", true), false));
		String lowerCase = getAttributeValue(el, "lowerCase", false);
		if (lowerCase != null) {
			ds.addAttributeValue(new ValueBean("lowerCase", lowerCase, false));
		}
		String value = getElementValue(el, "DataSet", true);
		
		referencedDataSets.add(value);
		
		ds.addAttributeValue(new ValueBean("dataSet", value, true));
		
		Element attrSel = el.element("AttributeSelector");
		if (attrSel == null) {
			throw new IllegalArgumentException("Element <" + "AttributeSelector" + "> is missing in <" + 
					el.getName() + "> [" + el.getPath() + "]");
		}
		CiBean bean = getAttributeSelector(name + "-" + count, attrSel);
		ds.addAttributeValue(new ValueBean("attributeSelector", bean.getAlias(), true));
		
		return(ds);
	}

	
	private CiBean getInstanceSelector(String name, Element el) {
		CiBean ds = newBean("instanceSelector-" + name);
		
		String type = el.getName();
		if (type.equals("EmptyInstanceSelector")) {
			ds.setDerivedFrom("EmptyInstanceSelector");
			ds.setTemplate(false);
			return(ds);
		} 
		
		ds.setDerivedFrom(instanceSelectorType);
		ds.setTemplate(false);
		String autoCreate = getAttributeValue(el, "auto-create", false);
		if (autoCreate == null || autoCreate.length() == 0) {
			autoCreate = "true";
		}
		ds.addAttributeValue(new ValueBean("autoCreate", autoCreate, false));
		ds.addAttributeValue(new ValueBean("template", getAttributeValue(el, "template", true), false));

		String templCol = getAttributeValue(el, "templateSelector", false);
		if (templCol != null && templCol.length() > 0) {
			ds.addAttributeValue(new ValueBean(instanceSelectorTemplateAlias, templCol, false));
		}
		
		String templNameCol = getAttributeValue(el, "templateNameSelector", false);
		if (templNameCol != null && templNameCol.length() > 0) {
			ds.addAttributeValue(new ValueBean(instanceSelectorTemplateNameAlias, templNameCol, false));
		}
	
		
		if (instanceSelectorType.equals(XPATH_INSTANCE_SELECTOR)) {
			String match = getAttributeValue(el, "match", true);
			ds.addAttributeValue(new ValueBean("xpath", match, false));
		}
		
		return(ds);
	}
	
	private CiBean getAttributeSelector(String name, Element el) {
		
		String attrAliasName = getAttributeValue(el, "name", true);
		String key = getAttributeValue(el, "naturalKey", false);
		String defaultValue = getAttributeValue(el, "defaultValue", false);
			
		if (key == null) {
			key = "false";
		}
		String selector = getAttributeValue(el, "selector", false);
		String selectorName = getAttributeValue(el, "selectorName", false);
		
		CiBean bean = newBean(name + "-" + attrAliasName);
		if (el.getName().equals("ComplexAttributeSelector")) {
			bean.setDerivedFrom(complexAttributeSelectorType);
			String dsName = getAttributeValue(el, "dataSet", false);
			String ds = "DataSet-" + dsName;
			referencedDataSets.add(ds);
			
			bean.addAttributeValue(new ValueBean("dataSet", ds, true));
			if (complexAttributeSelectorType.equals(XPATH_COMPLEX_ATTRIBUTE_SELECTOR)) {
				String xpath = getAttributeValue(el, "selector", true);
				bean.addAttributeValue(new ValueBean("xpath", xpath, false));
			}
		} else if (el.getName().equals("DefaultAttributeSelector")){
			bean.setDerivedFrom("DefaultAttributeSelector");
			String value = getAttributeValue(el, "value", true);
			bean.addAttributeValue(new ValueBean("value", value, false));
			String complex = getAttributeValue(el, "complex", false);
			if (complex != null) {
				bean.addAttributeValue(new ValueBean("complex", complex, false));
			}
		} else if (el.getName().equals("AttributeExpressionSelector")){
			bean.setDerivedFrom("AttributeExpressionSelector");
			String expr = getElementValue(el, "Expression", true);
			bean.addAttributeValue(new ValueBean("expression", expr, false));
			for (Element child : (List<Element>)el.elements()) {
				int count = 0;
				if (child.getName().equals("AttributeSelector")) {
					CiBean aSel = getAttributeSelector(attrAliasName + "-" + name + "-" + count, child);
					bean.addAttributeValue(new ValueBean("selector", aSel.getAlias(), true));
					count++;
				}
			}
		} else {
			bean.setDerivedFrom(attributeSelectorType);
			// Fix to support names insted of index in jdbc queries.
			if (selectorName != null && attrSelectorNameAlias != null) {
				bean.addAttributeValue(new ValueBean(attrSelectorNameAlias, selectorName, false));
			} else {
				bean.addAttributeValue(new ValueBean(attrSelectorAlias, selector, false));
			}
		}
		bean.addAttributeValue(new ValueBean("name", attrAliasName, false));
		bean.addAttributeValue(new ValueBean("naturalKey", key, false));
		bean.addAttributeValue(new ValueBean("defaultValue", defaultValue, false));	
		
		return(bean);
	}
	

	/**
	 * XML helper functions
	 */
	private String getElementValue(Element sel,
			String elementName, boolean requiered) {
		
		Element el = sel.element(elementName);
		if (el == null) {
			if (requiered) {
				throw new IllegalArgumentException("Element <" + elementName + "> is missing in <" + 
						sel.getName() + "> [" + sel.getPath() + "]");
			}
			return(null);
		}
		String text = el.getTextTrim();
		if (requiered && (text == null || text.length() == 0)) {
			throw new IllegalArgumentException("Element <" + elementName + "> has no value in <" + 
					sel.getName() + "> [" + sel.getPath() + "]");
		}
		return(text);
	}

	private String getAttributeValue(Element sel,
			String attributeName, boolean requiered) {
		
		Attribute el = sel.attribute(attributeName);
		if (el == null) {
			if (requiered) {
				throw new IllegalArgumentException("Attribute <" + attributeName + "> is missing in <" + 
						sel.getName() + "> [" + sel.getPath() + "]");
			}
			return(null);
		}
		String text = el.getText();
		if (requiered && (text == null || text.length() == 0)) {
			throw new IllegalArgumentException("Element <" + attributeName + "> has no value in <" + 
					sel.getName() + "> [" + sel.getPath() + "]");
		}
		return(text);
	}

	

	

}
