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
package org.onecmdb.ui.gwt.desktop.server.transform;

import java.awt.ItemSelectable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.AttributeValueConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemAndGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemOrGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;

public class QueryParser {
	
	private static final String ELEMENT_ROOT = "GraphQuery";

	private static final String ELEMENT_ITEM_OFFSPRING_SELECTOR = "ItemOffspringSelector";
	private static final String ELEMENT_ITEM_ALIAS_SELECTOR = "ItemAliasSelector";
	private static final String ELEMENT_ITEM_RELATION_SELECTOR = "ItemRelationSelector";

	private static final String ELEMENT_ITEM_ATTRIBUTE_VALUE_CONSTRAINT = "ItemAttributeValueConstraint";
	private static final String ELEMENT_ITEM_AND_GROUP_CONSTRAINT = "ItemAndGroupConstraint";
	private static final String ELEMENT_ITEM_OR_GROUP_CONSTRAINT = "ItemOrGroupConstraint";
	private static final String ELEMENT_ITEM_SECURITY_CONSTRAINT = "ItemSecurityConstraint";

	private static final String ELEMENT_ITEM_RELATION_TARGET = "target";
	private static final String ELEMENT_ITEM_RELATION_SOURCE = "source";

	private static final String ATTRIBUTE_ITEM_ID = "id";
	private static final String ATTRIBUTE_ITEM_TEMPLATE = "template";
	private static final String ATTRIBUTE_ITEM_PRIMARY = "primary";

	private static final String ATTRIBUTE_VALUE_CONSTRAINT_ALIAS = "alias";
	private static final String ATTRIBUTE_VALUE_CONSTRAINT_VALUE = "value";
	private static final String ATTRIBUTE_VALUE_CONSTRAINT_VALUETYPE = "valueType";
	private static final String ATTRIBUTE_VALUE_CONSTRAINT_OPERATION = "operation";

	private static final String ELEMENT_SECURITY_CONSTRAINT_GROUP = "securityGroupAlias";

	private static final String ELEMENT_EXCLUDE_RELATION = "excludeRelation";

	private static final String ELEMENT_ITEM_ALIAS_SELECTOR_ALIAS = "alias";

	private static final String ELEMENT_ITEM_OFFSPRING_LIMIT_CHILD = "limitToChild";
	private static final String ELEMENT_ITEM_OFFSPRING_MATCH_TEMPLATE = "matchTemplate";

	private static final String ELEMENT_CONSTRAINT = "constraint";

	private static final String ELEMENT_PAGEINFO = "pageInfo";
	private static final String ELEMENT_PAGEINFO_FIRSTRESULT = "firstResult";
	private static final String ELEMENT_PAGEINFO_MAXRESULT = "maxResult";
	
	public String queryURL;

	private Properties attrMap;

	public String getQueryURL() {
		return queryURL;
	}

	public void setQueryURL(String queryURL) {
		this.queryURL = queryURL;
	}
	
	public void setAttributeMap(Properties attrMap) {
		this.attrMap = attrMap;
	}
	
	public GraphQuery parse() throws DocumentException, IOException {
		URL url = null;
		if (queryURL.startsWith("classpath:")) {
			String name = queryURL.substring("classpath:".length());
			url = getClass().getClassLoader().getResource(name);
		} else {
			url = new URL(queryURL);
		}
	
		InputStream in = url.openStream();
		
		try {
			GraphQuery g = parse(in);
			return(g);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// Silently ignore...
				}
			}
		}
	}
	
	
	public GraphQuery parse(InputStream input) throws DocumentException {
		Reader r = new InputStreamReader(input);
		GraphQuery result = parse(r);
		return(result);
	}
	
	public GraphQuery parse(Reader r) throws DocumentException {
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(r);
		List<CiBean> beans = new ArrayList<CiBean>();
		Element root = document.getRootElement();
		
		if (!root.getName().equals(ELEMENT_ROOT)) {
			throw new IllegalArgumentException("Not a correct header element, found " + root.getName() + " requiers " + ELEMENT_ROOT);
		}
		
		GraphQuery result = new GraphQuery();
		for (Element eSel : (List<Element>)root.elements()) {
			if (eSel.getName().equals(ELEMENT_ITEM_OFFSPRING_SELECTOR)) {
				ItemSelector iSel = parseOffspringSelector(eSel);
				result.addSelector(iSel);
				continue;
			}
			if (eSel.getName().equals(ELEMENT_ITEM_ALIAS_SELECTOR)) {
				ItemSelector iSel = parseAliasSelector(eSel);
				result.addSelector(iSel);
				continue;
			}
			if (eSel.getName().equals(ELEMENT_ITEM_RELATION_SELECTOR)) {
				ItemSelector iSel = parseRelationSelector(eSel);
				result.addSelector(iSel);
				continue;
			}
			throw new IllegalArgumentException("ItemSelector <" + eSel.getName() + "> is not supported!");
		}
		System.out.println(XMLPrinter.toXML(result, 0));
		return(result);
		
	}
	
	/**
	 * Common parser for item selectors.
	 * Handles: id, template, primary, constraints.
	 * 
	 * @param sel
	 * @param sel
	 * @return
	 */
	private ItemSelector parseItemSelector(Element sel, ItemSelector iSel) {
		String id = getAttributeValue(sel, ATTRIBUTE_ITEM_ID, true);
		String template = getAttributeValue(sel, ATTRIBUTE_ITEM_TEMPLATE, true);
		String primary = getAttributeValue(sel, ATTRIBUTE_ITEM_PRIMARY, false);
		
		iSel.setId(id);
		iSel.setTemplateAlias(template);
		if (primary != null && primary.equals("true")) {
			iSel.setPrimary(true);
		}
		
		// Handle Contsraints.
		Element con = sel.element(ELEMENT_CONSTRAINT);
		if (con != null) {
			List constraints = con.elements(); 
			if (constraints.size() > 0) {
				Element conEl = (Element)constraints.get(0);
				ItemConstraint constraint = parseItemConstraints(conEl);
				if (constraint != null) {
					iSel.applyConstraint(constraint);
				}
			}
		}
		
		// Handle pageinfo...
		Element pageInfoEl = sel.element(ELEMENT_PAGEINFO);
		if (pageInfoEl != null) {
			PageInfo pageInfo = new PageInfo();
			String firstResult = getElementValue(pageInfoEl, ELEMENT_PAGEINFO_FIRSTRESULT, true);
			String maxResult = getElementValue(pageInfoEl, ELEMENT_PAGEINFO_MAXRESULT, true);
			
			pageInfo.setFirstResult(Integer.parseInt(firstResult));
			pageInfo.setMaxResult(Integer.parseInt(maxResult));
			
			iSel.setPageInfo(pageInfo);
		}
		// Handle exclude relations.
		for (Element excludeEl : (List<Element>)sel.elements()) {
			if (excludeEl.getName().equals(ELEMENT_EXCLUDE_RELATION)) {
				String excludeId = getAttributeValue(excludeEl, ATTRIBUTE_ITEM_ID, true);
				iSel.addExcludeRelation(excludeId);
			}
		}
		return(iSel);
	}

	private ItemConstraint parseItemConstraints(Element eCon) {
			if (eCon.getName().equals(ELEMENT_ITEM_ATTRIBUTE_VALUE_CONSTRAINT)) {
				AttributeValueConstraint con = new AttributeValueConstraint();
				String alias = getElementValue(eCon, ATTRIBUTE_VALUE_CONSTRAINT_ALIAS, false);
				String operation = getElementValue(eCon, ATTRIBUTE_VALUE_CONSTRAINT_OPERATION, true);
				String value = getElementValue(eCon, ATTRIBUTE_VALUE_CONSTRAINT_VALUE, false);
				String valueType = getElementValue(eCon, ATTRIBUTE_VALUE_CONSTRAINT_VALUETYPE, false);
				
				int op = con.getOperation(operation);
				if (op < 0) {
					throw new IllegalArgumentException("Operation <" + operation + "> is not supported in <" + eCon.getPath() +">");
				}
				int vt = con.STRING_DATA_TYPE;
				if (valueType != null) {
					if (valueType.equalsIgnoreCase("number")) {
						vt = con.NUMBER_DATA_TYPE;
					} else if (valueType.equalsIgnoreCase("date")) {
						vt = con.DATE_DATA_TYPE;
					}
				}
				con.setAlias(alias);
				con.setOperation(op);
				con.setValue(value);
				con.setValueType(vt);
				return(con);
			}
			if (eCon.getName().equals(ELEMENT_ITEM_AND_GROUP_CONSTRAINT)) {
				ItemAndGroupConstraint and = new ItemAndGroupConstraint();
				for (Element child : (List<Element>)eCon.elements()) {
					and.add(parseItemConstraints(child));
				}
				return(and);
			}
			if (eCon.getName().equals(ELEMENT_ITEM_OR_GROUP_CONSTRAINT)) {
				ItemOrGroupConstraint and = new ItemOrGroupConstraint();
				for (Element child : (List<Element>)eCon.elements()) {
					and.add(parseItemConstraints(child));
				}
				return(and);
				
			}
			if (eCon.getName().equals(ELEMENT_ITEM_SECURITY_CONSTRAINT)) {
				ItemSecurityConstraint con = new ItemSecurityConstraint();
				String groupName = getElementValue(eCon, ELEMENT_SECURITY_CONSTRAINT_GROUP, true);
				con.setGroupName(groupName);
				return(con);
			}
			throw new IllegalArgumentException("Constraint " + eCon.getName() + " not supperted! " + eCon.getPath());
	}

	private ItemSelector parseRelationSelector(Element sel) {
		ItemRelationSelector rSel = new ItemRelationSelector();
		parseItemSelector(sel, rSel);
		// Handle target, source
		String target = getElementValue(sel, ELEMENT_ITEM_RELATION_TARGET, true);
		String source = getElementValue(sel, ELEMENT_ITEM_RELATION_SOURCE, true);
		rSel.setTarget(target);
		rSel.setSource(source);
		return(rSel);
	}


	private ItemSelector parseAliasSelector(Element sel) {
		ItemAliasSelector aSel = new ItemAliasSelector();
		parseItemSelector(sel, aSel);
		// Handle target, source
		String alias = getElementValue(sel, ELEMENT_ITEM_ALIAS_SELECTOR_ALIAS, true);
		aSel.setAlias(alias);
		return(aSel);
	}

	private ItemSelector parseOffspringSelector(Element sel) {
		ItemOffspringSelector oSel = new ItemOffspringSelector();
		parseItemSelector(sel, oSel);
		// Handle target, source
		String limitToChild = getElementValue(sel, ELEMENT_ITEM_OFFSPRING_LIMIT_CHILD, false);
		if (limitToChild != null) {
			oSel.setLimitToChild(Boolean.parseBoolean(limitToChild));
		}
		String matchTemplate = getElementValue(sel, ELEMENT_ITEM_OFFSPRING_MATCH_TEMPLATE, false);
		if (matchTemplate != null) {
			oSel.setMatchTemplate(Boolean.parseBoolean(matchTemplate));
		}
		return(oSel);
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
		text = subsituteAttr(text);
		return(text);
	}
	
	private String subsituteAttr(String text) {
		if (this.attrMap == null) {
			return(text);
		}
		if (!text.contains("{")) {
			return(text);
		}
		String newString = text;
		for (Object key : attrMap.keySet()) {
			String value = (String) attrMap.get(key);
			newString = newString.replace("{" + key + "}", value);
		}
		return(newString);
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
		text = subsituteAttr(text);
		return(text);
	}

}
