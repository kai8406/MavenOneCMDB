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
package org.onecmdb.core.utils.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.IBeanProviderSource;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.dtd.DtdAttribute;
import org.onecmdb.core.utils.xml.dtd.DtdElement;

public class XmlParser implements IBeanProvider, IBeanProviderSource {

	// Known Template Elements.
	public static final DtdElement ROOT_ELEMENT = new DtdElement("onecmdb");

	public static final DtdElement TEMPLATES_ELEMENT = new DtdElement(
			"templates");

	public static final DtdElement TEMPLATE_ELEMENT = new DtdElement("template");

	public static final DtdElement INSTANCES_ELEMENT = new DtdElement(
			"instances");

	public static final DtdElement ATTRIBUTE_ELEMENT = new DtdElement(
			"attribute");

	public static final DtdElement COMPLEX_TYPE_ELEMENT = new DtdElement(
			"complexType");

	public static final DtdElement SIMPLE_TYPE_ELEMENT = new DtdElement(
			"simpleType");

	public static final DtdElement DERIVED_FROM_ELEMENT = new DtdElement(
			"derivedFrom");

	public static final DtdElement SET_COMPLEX_VALUE_ELEMENT = new DtdElement(
			"setComplexValue");

	public static final DtdElement SET_SIMPLE_VALUE_ELEMENT = new DtdElement(
			"setSimpleValue");

	public static final DtdElement REF_ELEMENT = new DtdElement("ref");

	public static final DtdElement REF_TYPE_ELEMENT = new DtdElement("refType");

	public static final DtdElement POLICY_ELEMENT = new DtdElement("policy");

	public static final DtdElement MAX_OCCURS_ELEMENT = new DtdElement(
			"maxOccurs");

	public static final DtdElement MIN_OCCURS_ELEMENT = new DtdElement(
			"minOccurs");

	public static final DtdElement DESCRIPTION_ELEMENT = new DtdElement(
			"description");

	// Known Template Attributes.
	// Template Attribute
	public static final DtdAttribute ALIAS_ATTR = new DtdAttribute("alias",
			DtdAttribute.DEFAULT_OPTIONAL);
	
	public static final DtdAttribute GROUP_ATTR = new DtdAttribute("group",
			DtdAttribute.DEFAULT_OPTIONAL);
	
	public static final DtdAttribute CREATE_DATE_ATTR = new DtdAttribute("created",
			DtdAttribute.DEFAULT_OPTIONAL);
	
	public static final DtdAttribute LAST_MODIFIED_ATTR = new DtdAttribute("lastModified",
			DtdAttribute.DEFAULT_OPTIONAL);
	
	

	public static final DtdAttribute INSTANCE_NAME_EXPRESSION_ATTR = new DtdAttribute(
			"displayName", DtdAttribute.DEFAULT_OPTIONAL);

	// Attribute Attributes
	public static final DtdAttribute ATT_ALIAS_ATTR = new DtdAttribute(
			"attAlias", DtdAttribute.DEFAULT_REQUIRED);

	public static final DtdAttribute NAME_ATTR = new DtdAttribute("displayName",
			DtdAttribute.DEFAULT_OPTIONAL);
	
	public static final DtdAttribute ATTR_DERIVED = new DtdAttribute("derived",
			DtdAttribute.DEFAULT_OPTIONAL);
	
	public static final DtdAttribute ID_ATTR = new DtdAttribute("id",
			DtdAttribute.DEFAULT_OPTIONAL);
	

	private List<String> urls;

	private ArrayList<CiBean> beans;

	private boolean beansLoaded = false;

	private HashMap<String, CiBean> beanMap;

	//private Log log = LogFactory.getLog(this.getClass());

	private IBeanProviderConfig beanConfig = null;
	
	public static String getTab(int level) {
		StringBuffer tab = new StringBuffer();
		for (int i = 0; i < level; i++) {
			tab.append("\t");
		}
		return (tab.toString());
	}

	public static void main(String argv[]) {
		String path = argv[0];
		XmlParser p = new XmlParser();
		p.setURL(path);
		List<CiBean> beans = p.getBeans();

		System.out.println("FOUND: " + beans.size());

		XmlGenerator gen = new XmlGenerator();
		gen.setBeans(beans);
		gen.setOutput("outOnecmdb.xml");
		try {
			gen.process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setBeanProviderConfig(IBeanProviderConfig config) {
		this.beanConfig = config;
	}
	
	public void setURL(String url) {
		addURL(url);
	}

	public void addURL(String url) {
		if (this.urls == null) {
			this.urls = new ArrayList<String>();
		}
		this.urls.add(url);
		beansLoaded = false;
	}

	public void setURLs(List<String> urls) {
		this.urls = urls;
		beansLoaded = false;
	}

	public CiBean getBean(String alias) {
		if (!beansLoaded) {
			loadBeans();
		}
		CiBean bean = beanMap.get(alias);
		return (bean);
	}

	public List<CiBean> getBeans() {
		if (!beansLoaded) {
			loadBeans();
		}
		return (beans);
	}

	private void loadBeans() {
		beans = new ArrayList<CiBean>();
		for (String url : urls) {
			//log.info("Load provider url '" + url + "'");
			if (this.beanConfig != null) {
				if (this.beanConfig.isImported(url)) {
					continue;
				}
				this.beanConfig.importURL(url);
			}
			
			// Need to locate this url.
			InputStream input = null;
			URL u = null;
			if (url.startsWith("res:") || url.startsWith("classpath:")) {
				int index = url.indexOf(':');
				String resName = url.substring(index+1);
				
				u = this.getClass().getClassLoader().getResource(resName);
				if (u == null) {
					//log.error("Resource '" + resName + "' not found");
					throw new IllegalArgumentException("No resource with name "
							+ resName + " found");
				}

			} else {
				try {
					u = new URL(url);
				} catch (MalformedURLException e) {
					//log.error("Url '" + url + "' malformed");					
					e.printStackTrace();
					throw new IllegalArgumentException("Not a corect url:"
							+ e.toString(), e);
				}
			}
			//log.debug("load url <" + u.getPath() + ">");
			try {
				input = u.openStream();
			} catch (IOException e) {
				e.printStackTrace();
				//log.error("Can't open url '" + u.toExternalForm() + "'");				
				throw new IllegalArgumentException(
						"Can't open inputStream for url " + url + " :"
								+ e.toString(), e);
			}
			try {
				beans.addAll(parseInputStream(input));
			} catch (DocumentException e) {
				//log.error("Parsing url '" + u.toExternalForm() + "'", e);				
				
				//e.printStackTrace();
				System.out.println("ERROR in URL " + url);
				throw new IllegalAccessError("Can't parse XML URL[" + url
						+ "]:" + e.toString());
			} finally {
				// Close it or ?
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		// Build bean map.
		beanMap = new HashMap<String, CiBean>();
		for (CiBean bean : beans) {
			beanMap.put(bean.getAlias(), bean);
		}
		this.beansLoaded = true;
	}

	public List<CiBean> parseInputStream(InputStream input) throws DocumentException {
		
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(input);
		List<CiBean> beans = new ArrayList<CiBean>();
		
		Element el = document.getRootElement();
		if (el.getName().equals(ROOT_ELEMENT.getName())) {
			for (Element childEl : (List<Element>) el.elements()) {
				if (childEl.getName().equals(
						TEMPLATES_ELEMENT.getName())) {
					beans.addAll(parseBlueprints(childEl));
					continue;
				}
				if (childEl.getName().equals(
						INSTANCES_ELEMENT.getName())) {
					beans.addAll(parseInstances(childEl));
					continue;
				}
				throw new IllegalAccessError("Unkown element <"
						+ childEl.getName() + "> must be "
						+ TEMPLATES_ELEMENT + "|" + INSTANCES_ELEMENT);
			}
		} else {
			throw new IllegalAccessError("Unkown root element <"
					+ el.getName() + "> must be <"
					+ ROOT_ELEMENT.getName() + ">");
		}
		return(beans);
	}
	
	public List<CiBean> parseBlueprints(Element rootElement) {
		List els = rootElement.elements();
		List<CiBean> ciBeans = new ArrayList<CiBean>();
		for (int i = 0; i < els.size(); i++) {
			CiBean bean = parseBlueprint((Element) els.get(i));
			ciBeans.add(bean);
		}
		return (ciBeans);
	}

	public List<CiBean> parseInstances(Element rootElement) {
		List els = rootElement.elements();
		List<CiBean> ciBeans = new ArrayList<CiBean>();
		for (int i = 0; i < els.size(); i++) {
			CiBean bean = parseInstance((Element) els.get(i));
			ciBeans.add(bean);
		}
		return (ciBeans);
	}

	public CiBean parseInstance(Element ci) {

		String derivedFrom = ci.getName();
		String alias = getAttributeValue(ci, ALIAS_ATTR, generateAlias(derivedFrom));
		String id = getAttributeValue(ci, ID_ATTR, null);
		String displayNameExpr = getAttributeValue(ci, INSTANCE_NAME_EXPRESSION_ATTR, null);
		
		//log.debug("found instance " + alias);

		CiBean ciBean = new CiBean();
		ciBean.setTemplate(false);
		ciBean.setAlias(alias);
		ciBean.setDerivedFrom(derivedFrom);
		if (id != null) {
			ciBean.setId(Long.parseLong(id));
		}
		
		ciBean.setDisplayNameExpression(displayNameExpr);
		
		if (ci.getText() != null) {
			ciBean.setDisplayName(ci.getText().trim());
		}
		
		String group = getAttributeValue(ci, GROUP_ATTR, null);
		ciBean.setGroup(group);

		List<Element> attributes = ci.elements();
		
		List<ValueBean> vBeans = new ArrayList<ValueBean>();
		for (Element attribute : attributes) {
			String attAlias = attribute.getName();
			String valueId = getAttributeValue(attribute, ID_ATTR, null);
			if (attAlias.equals(DESCRIPTION_ELEMENT.getName())) {
				ciBean.setDescription(getValue(attribute));
				continue;
			}
			ValueBean vBean = new ValueBean();
			vBean.setAlias(attAlias);
			if (valueId != null) {
				vBean.setId(Long.parseLong(valueId));
			}
		
			Element ref = attribute.element(REF_ELEMENT.getName());
			if (ref != null) {
				if (ref.elements().size() == 1) {
					Element compositeElement = (Element)ref.elements().get(0);
					CiBean refBean = parseInstance(compositeElement);
					beans.add(refBean);
					vBean.setValue(refBean.getAlias());
					vBean.setComplexValue(true);
				} else {
					String aliasValue = getAttributeValue(ref, ALIAS_ATTR, null);
					if (aliasValue != null) {
						vBean.setValue(aliasValue);
						vBean.setComplexValue(true);
					} 
				}
			} else {
				vBean.setValue(getValue(attribute));
				vBean.setComplexValue(false);
			}
			vBeans.add(vBean);
		}
		ciBean.setAttributeValues(vBeans);
		return (ciBean);
	}

	private String generateAlias(String prefix) {
		ItemId id = new ItemId();
		return(prefix + "-" + id.toString());
	}

	public CiBean parseBlueprint(Element ci) {
		if (!ci.getName().equals(TEMPLATE_ELEMENT.getName())) {
			throw new IllegalAccessError("Unknown element <" + ci.getName()
					+ "> must be <" + TEMPLATE_ELEMENT.getName() + ">");
		}
		String alias = getAttributeValue(ci, ALIAS_ATTR, null);
		if (alias == null) {
			throw new IllegalAccessError("Alias must be specified on element <" + ci.getName() + ">");
		}
		String instanceNameExpression = getAttributeValue(ci,
				INSTANCE_NAME_EXPRESSION_ATTR, null);
		//log.debug("found template " + alias);
		
		String id = getAttributeValue(ci, ID_ATTR, null);
		CiBean ciBean = new CiBean();
		ciBean.setAlias(alias);
		ciBean.setDisplayNameExpression(instanceNameExpression);
		ciBean.setTemplate(true);
		
		String group = getAttributeValue(ci, GROUP_ATTR, null);
		ciBean.setGroup(group);
		
		if (id != null) {
			ciBean.setId(Long.parseLong(id));
		}
		
		List<Element> attributes = ci.elements();
		List<AttributeBean> aBeans = new ArrayList<AttributeBean>();
		for (Element el : attributes) {
			if (el.getName().equals(ATTRIBUTE_ELEMENT.getName())) {
				String attrAlias = getAttributeValue(el, ATT_ALIAS_ATTR, null);
				String name = getAttributeValue(el, NAME_ATTR, null);
				String derived = getAttributeValue(el, ATTR_DERIVED, "false");
				String attrId = getAttributeValue(el, ID_ATTR, null);

				AttributeBean aBean = new AttributeBean();
				aBean.setDisplayName(name);
				aBean.setAlias(attrAlias);
				aBean.setDerived(Boolean.parseBoolean(derived));
				if (attrId != null) {
					aBean.setId(Long.parseLong(attrId));	
				}
				for (Element aEl : (List<Element>) el.elements()) {
					if (aEl.getName().equals(SIMPLE_TYPE_ELEMENT.getName())) {
						aBean.setType(aEl.getTextTrim());
						aBean.setComplexType(false);
					}
					if (aEl.getName().equals(POLICY_ELEMENT.getName())) {
						Element max = aEl.element(MAX_OCCURS_ELEMENT.getName());
						if (max != null) {
							aBean.setMaxOccurs(max.getTextTrim());
						}
						Element min = aEl.element(MIN_OCCURS_ELEMENT.getName());
						if (min != null) {
							aBean.setMinOccurs(min.getTextTrim());
						}
					}
					if (aEl.getName().equals(COMPLEX_TYPE_ELEMENT.getName())) {
						Element ref = aEl.element(REF_ELEMENT.getName());
						if (ref == null) {
							throw new IllegalAccessError("Missing element <"
									+ REF_ELEMENT.getName() + "> in element <"
									+ aEl.getName() + "> template=" + alias
									+ ", attribute=" + attrAlias);
						}
						Element refType = aEl.element(REF_TYPE_ELEMENT
								.getName());
						// It's ok, atleast for references.
						String refTypeAlias = null;
						if (refType != null) {
							/*
							throw new IllegalAccessError("Missing element <"
									+ REF_TYPE_ELEMENT.getName()
									+ "> in element <" + aEl.getName()
									+ ">  template=" + alias + ", attribute="
									+ attrAlias);
							*/
						
						
							Element refTypeRef = refType.element(REF_ELEMENT
								.getName());
							if (refTypeRef == null) {
								throw new IllegalAccessError("Missing element <"
										+ REF_ELEMENT.getName() + "> in element <"
										+ refType.getName() + "> template=" + alias
										+ ", attribute=" + attrAlias);
							}
							refTypeAlias = getAttributeValue(refTypeRef,
									ALIAS_ATTR, null);
					
						}
						String refAlias = getAttributeValue(ref, ALIAS_ATTR,
								null);
						aBean.setRefType(refTypeAlias);
						aBean.setType(refAlias);
						aBean.setComplexType(true);
					}
					if (aEl.getName().equals(DESCRIPTION_ELEMENT.getName())) {
						
						aBean.setDescription(aEl.getTextTrim());
					}
					if (aEl.getName().equals(SET_SIMPLE_VALUE_ELEMENT.getName())) {
						ValueBean vBean = new ValueBean();
						String vId = getAttributeValue(aEl, ID_ATTR, null);
						if (vId != null) {
							vBean.setId(Long.parseLong(vId));
						}
						vBean.setValue(getValue(aEl));
						vBean.setAlias(attrAlias);
						vBean.setComplexValue(false);
						ciBean.addAttributeValue(vBean);
					}
					if (el.getName().equals(SET_COMPLEX_VALUE_ELEMENT.getName())) {
						ValueBean vBean = new ValueBean();
						String vId = getAttributeValue(aEl, ID_ATTR, null);
						if (vId != null) {
							vBean.setId(Long.parseLong(vId));
						}
						Element ref = aEl.element(REF_ELEMENT.getName());
						if (ref == null) {
							throw new IllegalAccessError("Missing element <"
									+ REF_ELEMENT.getName() + "> in element <"
									+ el.getName() + "> template=" + alias
									+ ", attribute=" + attrAlias);
						}
						String aliasValue = getAttributeValue(ref, ALIAS_ATTR, null);
						vBean.setValue(aliasValue);
						vBean.setComplexValue(true);
						
						vBean.setAlias(attrAlias);
						ciBean.addAttributeValue(vBean);
					}
				}
				ciBean.addAttribute(aBean);
			} else if (el.getName().equals(SET_SIMPLE_VALUE_ELEMENT.getName())) {
				String attrAlias = getAttributeValue(el, ATT_ALIAS_ATTR, null);
				ValueBean vBean = new ValueBean();
				
				String vId = getAttributeValue(el, ID_ATTR, null);
				if (vId != null) {
					vBean.setId(Long.parseLong(vId));
				}
			
				vBean.setValue(getValue(el));
				vBean.setAlias(attrAlias);
				vBean.setComplexValue(false);
				ciBean.addAttributeValue(vBean);
			} else if (el.getName().equals(SET_COMPLEX_VALUE_ELEMENT.getName())) {
				String attrAlias = getAttributeValue(el, ATT_ALIAS_ATTR, null);
				ValueBean vBean = new ValueBean();
				String vId = getAttributeValue(el, ID_ATTR, null);
				if (vId != null) {
					vBean.setId(Long.parseLong(vId));
				}
			
				Element ref = el.element(REF_ELEMENT.getName());
				if (ref == null) {
					throw new IllegalAccessError("Missing element <"
							+ REF_ELEMENT.getName() + "> in element <"
							+ el.getName() + "> template=" + alias
							+ ", attribute=" + attrAlias);
				}
				String aliasValue = getAttributeValue(ref, ALIAS_ATTR, null);
				vBean.setValue(aliasValue);
				vBean.setComplexValue(true);

				vBean.setAlias(attrAlias);
				ciBean.addAttributeValue(vBean);
			} else if (el.getName().equals(DERIVED_FROM_ELEMENT.getName())) {
				Element ref = el.element(REF_ELEMENT.getName());
				if (ref == null) {
					throw new IllegalAccessError("Missing element <"
							+ REF_ELEMENT.getName() + "> in element <"
							+ el.getName() + "> template=" + alias);
				}
				String aliasValue = getAttributeValue(ref, ALIAS_ATTR, null);
				ciBean.setDerivedFrom(aliasValue);
			} else if (el.getName().equals(DESCRIPTION_ELEMENT.getName())) {
				ciBean.setDescription(el.getTextTrim());
			} else {
				throw new IllegalAccessError("Unkown element <" + el.getName()
						+ "> in element <" + ci.getName() + "> template="
						+ alias);
			}
		}
		// Validate alias and derivedFrom
		if (ciBean.getAlias() == null) {
			throw new IllegalArgumentException("No alias specified on element '" + ci.getName() + "'");
		}
		if (ciBean.getDerivedFrom() == null) {
			//log.warn("No derivedFrom specified on element '" + ciBean.getAlias() + "'");
			//throw new IllegalArgumentException("No derivedFrom specified on element '" + ciBean.getAlias() + "'");
		}
		
		return (ciBean);
	}

	private String getValue(Element el) {
		String trimmedValue = el.getTextTrim();
		if (trimmedValue == null) {
			return(null);
		}
		if (trimmedValue.equals("")) {
			return(null);
		}
		return(trimmedValue);
	}

	private String getAttributeValue(Element e, DtdAttribute a,
			String defaultValue) {
		Attribute attr = e.attribute(a.getName());
		if (attr == null) {
			if (a.isRequiered()) {
				throw new IllegalArgumentException("Missing attribute "
						+ a.getName() + " in element " + e.getName());
			}
			return (defaultValue);
		}
		String value = attr.getText();
		return (value);
	}

	private String getAttribute(Element e, String name, String defaultValue,
			boolean requiered, String error) {
		// Must be there.
		Attribute a = e.attribute(name);
		if (a == null) {
			if (!requiered) {
				return (defaultValue);
			}
			//log.error(error + " Missing attribute " + name);
			throw new IllegalArgumentException(error + " Missing attribute "
					+ name);
		}
		String value = a.getText();
		if (value == null) {
			if (!requiered) {
				return (defaultValue);
			}
			//log.error(error + " Missing attribute " + name);
			throw new IllegalArgumentException(error + " Missing attribute "
					+ name);
		}
		return (value);
	}

	public void dumpElement(Element el, int level) {
		System.out.println(tab(level) + el.getName() + ":" + el.getData()
				+ " {");
		List attributes = el.attributes();
		for (int i = 0; i < attributes.size(); i++) {
			Attribute a = (Attribute) attributes.get(i);
			System.out.println(tab(level) + " " + a.getName() + "="
					+ a.getData());
		}
		List els = el.elements();
		for (int i = 0; i < els.size(); i++) {
			dumpElement((Element) els.get(i), level + 1);
		}
		System.out.println(tab(level) + "}");

	}

	private String tab(int level) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < level; i++) {
			buffer.append(" ");
		}
		return (buffer.toString());
	}

}
