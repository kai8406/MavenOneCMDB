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
package org.onecmdb.utils.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.onecmdb.core.utils.graph.query.constraint.ItemIdConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemNotConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemOrGroupConstraint;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;

public class XML2GraphQuery {
	Log log = LogFactory.getLog(getClass());
	public static final String ELEMENT_ROOT = "GraphQuery";

	public static final String ELEMENT_ITEM_OFFSPRING_SELECTOR = "ItemOffspringSelector";
	public static final String ELEMENT_ITEM_ALIAS_SELECTOR = "ItemAliasSelector";
	public static final String ELEMENT_ITEM_RELATION_SELECTOR = "ItemRelationSelector";

	public static final String ELEMENT_ITEM_ATTRIBUTE_VALUE_CONSTRAINT = "AttributeValueConstraint";
	public static final String ELEMENT_ITEM_AND_GROUP_CONSTRAINT = "ItemAndGroupConstraint";
	public static final String ELEMENT_ITEM_OR_GROUP_CONSTRAINT = "ItemOrGroupConstraint";
	public static final String ELEMENT_ITEM_SECURITY_CONSTRAINT = "ItemSecurityConstraint";
	public static final String ELEMENT_ITEM_NOT_CONSTRAINT = "ItemNotConstraint";
	public static final String ELEMENT_ITEM_ID_CONSTRAINT = "ItemIdConstraint";

	public static final String ELEMENT_ITEM_RELATION_TARGET = "target";
	public static final String ELEMENT_ITEM_RELATION_SOURCE = "source";

	public static final String ATTRIBUTE_ITEM_ID = "id";
	public static final String ATTRIBUTE_ITEM_TEMPLATE = "template";
	public static final String ATTRIBUTE_ITEM_PRIMARY = "primary";

	public static final String ATTRIBUTE_VALUE_CONSTRAINT_ALIAS = "alias";
	public static final String ATTRIBUTE_VALUE_CONSTRAINT_VALUE = "value";
	public static final String ATTRIBUTE_VALUE_CONSTRAINT_VALUETYPE = "valueType";
	public static final String ATTRIBUTE_VALUE_CONSTRAINT_OPERATION = "operation";

	public static final String ELEMENT_SECURITY_CONSTRAINT_GROUP = "securityGroupAlias";

	public static final String ELEMENT_EXCLUDE_RELATION = "excludeRelation";

	public static final String ELEMENT_ITEM_ALIAS_SELECTOR_ALIAS = "alias";

	public static final String ELEMENT_ITEM_OFFSPRING_LIMIT_CHILD = "limitToChild";
	public static final String ELEMENT_ITEM_OFFSPRING_MATCH_TEMPLATE = "matchTemplate";

	public static final String ELEMENT_CONSTRAINT = "constraint";

	public static final String ELEMENT_PAGEINFO = "pageInfo";
	public static final String ELEMENT_PAGEINFO_FIRSTRESULT = "firstResult";
	public static final String ELEMENT_PAGEINFO_MAXRESULT = "maxResult";

	public static final String ELEMENT_EXCLUDE_IN_RESULT = "excludeInResult";

	public static final String ELEMENT_ITEM_RELATION_SOURCE_ATTREIBUTE = "sourceAttribute";
	
	private String queryURL;

	private Map<String, String> attrMap;

	public String getQueryURL() {
		return queryURL;
	}

	public void setQueryURL(String queryURL) {
		this.queryURL = queryURL;
	}
	
	public void setAttributeMap(Map attrMap) {
		this.attrMap = attrMap;
	}
	
	public GraphQuery parse() throws DocumentException, IOException {
		URL url = null;
		
		if (queryURL.startsWith("classpath:")) {
			String name = queryURL.substring("classpath:".length());
			url = getClass().getClassLoader().getResource(name);
		} else {
			try {
				url = new URL(queryURL);
			} catch (MalformedURLException e) {
			}
		}
	
		InputStream in = null;
	    if (url == null) {
	    	// Try plain file.
	    	File f = new File(queryURL);
	    	in = new FileInputStream(f);
	    } else {
	    	in = url.openStream();
	    }
		
		try {
			GraphQuery g = parse(in, "UTF-8");
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
	
	public GraphQuery parse(String input) throws DocumentException {
		Reader in = new StringReader(input);
		try {
			GraphQuery query = parse(in);
			return(query);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// Silently ignore
			}
		}
	}
	
	public GraphQuery parse(InputStream input, String enc) throws DocumentException, UnsupportedEncodingException {
		Reader in = new InputStreamReader(input, enc);
		GraphQuery query = parse(in);
		return(query);
	}
	
	public GraphQuery parse(Reader input) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(input);
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
		log.debug(GraphQuery2XML.toXML(result, 0));
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
		String id = XMLUtils.getAttributeValue(attrMap, sel, ATTRIBUTE_ITEM_ID, true);
		String template = XMLUtils.getAttributeValue(attrMap, sel, ATTRIBUTE_ITEM_TEMPLATE, true);
		String primary = XMLUtils.getAttributeValue(attrMap, sel, ATTRIBUTE_ITEM_PRIMARY, false);
		
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
			String firstResult = XMLUtils.getElementValue(attrMap, pageInfoEl, ELEMENT_PAGEINFO_FIRSTRESULT, true);
			String maxResult = XMLUtils.getElementValue(attrMap, pageInfoEl, ELEMENT_PAGEINFO_MAXRESULT, true);
			
			pageInfo.setFirstResult(Integer.parseInt(firstResult));
			pageInfo.setMaxResult(Integer.parseInt(maxResult));
			
			iSel.setPageInfo(pageInfo);
		}
		// Handle exclude relations.
		for (Element excludeEl : (List<Element>)sel.elements()) {
			if (excludeEl.getName().equals(ELEMENT_EXCLUDE_RELATION)) {
				String excludeId = XMLUtils.getAttributeValue(attrMap, excludeEl, ATTRIBUTE_ITEM_ID, true);
				iSel.addExcludeRelation(excludeId);
			}
		}
		for (Element excludeEl : (List<Element>)sel.elements()) {
			if (excludeEl.getName().equals(ELEMENT_EXCLUDE_IN_RESULT)) {
				iSel.setExcludedInResultSet(true);
			}
		}
		
		return(iSel);
	}

	private ItemConstraint parseItemConstraints(Element eCon) {
			if (eCon.getName().equals(ELEMENT_ITEM_ATTRIBUTE_VALUE_CONSTRAINT)) {
				AttributeValueConstraint con = new AttributeValueConstraint();
				String alias = XMLUtils.getElementValue(attrMap, eCon, ATTRIBUTE_VALUE_CONSTRAINT_ALIAS, false);
				String operation = XMLUtils.getElementValue(attrMap, eCon, ATTRIBUTE_VALUE_CONSTRAINT_OPERATION, true);
				String value = XMLUtils.getElementValue(attrMap, eCon, ATTRIBUTE_VALUE_CONSTRAINT_VALUE, false);
				String valueType = XMLUtils.getElementValue(attrMap, eCon, ATTRIBUTE_VALUE_CONSTRAINT_VALUETYPE, false);
				
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
				String groupName = XMLUtils.getElementValue(attrMap, eCon, ELEMENT_SECURITY_CONSTRAINT_GROUP, true);
				con.setGroupName(groupName);
				return(con);
			}
			if (eCon.getName().equals(ELEMENT_ITEM_NOT_CONSTRAINT)) {
				ItemNotConstraint con = new ItemNotConstraint();
				List<Element> elements = (List<Element>)eCon.elements();
				if (elements == null || elements.size() != 1) {
					throw new IllegalArgumentException(ELEMENT_ITEM_NOT_CONSTRAINT + " must contain ONE constraint!");
				}
				con.applyConstraint(parseItemConstraints(elements.get(0)));
				return(con);
			}
			if (eCon.getName().equals(ELEMENT_ITEM_ID_CONSTRAINT)) {
				ItemIdConstraint con = new ItemIdConstraint();
				String alias = XMLUtils.getElementValue(attrMap, eCon, "alias",false);
				if (alias != null) {
					con.setAlias(alias);
				}
				String id = XMLUtils.getElementValue(attrMap, eCon, "id", false);
				if (id != null) {
					con.setId(Long.parseLong(id));
				}
				return(con);
			}
			throw new IllegalArgumentException("Constraint " + eCon.getName() + " not supperted! " + eCon.getPath());
	}

	private ItemSelector parseRelationSelector(Element sel) {
		ItemRelationSelector rSel = new ItemRelationSelector();
		parseItemSelector(sel, rSel);
		// Handle target, source
		String target = XMLUtils.getElementValue(attrMap, sel, ELEMENT_ITEM_RELATION_TARGET, true);
		String source = XMLUtils.getElementValue(attrMap, sel, ELEMENT_ITEM_RELATION_SOURCE, true);
		rSel.setTarget(target);
		rSel.setSource(source);
		String mandatory = XMLUtils.getElementValue(attrMap, sel, "mandatory", false);
		if ("false".equalsIgnoreCase(mandatory)) {
			rSel.setMandatory(false);
		}
		String sourceAttribute = XMLUtils.getElementValue(attrMap, sel, ELEMENT_ITEM_RELATION_SOURCE_ATTREIBUTE, false);
		if (sourceAttribute != null) {
			rSel.setSourceAttribute(sourceAttribute);
		}
		return(rSel);
	}


	private ItemSelector parseAliasSelector(Element sel) {
		ItemAliasSelector aSel = new ItemAliasSelector();
		parseItemSelector(sel, aSel);
		// Handle target, source
		String alias = XMLUtils.getElementValue(attrMap, sel, ELEMENT_ITEM_ALIAS_SELECTOR_ALIAS, true);
		aSel.setAlias(alias);
		return(aSel);
	}

	private ItemSelector parseOffspringSelector(Element sel) {
		ItemOffspringSelector oSel = new ItemOffspringSelector();
		parseItemSelector(sel, oSel);
		// Handle target, source
		String limitToChild = XMLUtils.getElementValue(attrMap, sel, ELEMENT_ITEM_OFFSPRING_LIMIT_CHILD, false);
		if (limitToChild != null) {
			oSel.setLimitToChild(Boolean.parseBoolean(limitToChild));
		}
		String matchTemplate = XMLUtils.getElementValue(attrMap, sel, ELEMENT_ITEM_OFFSPRING_MATCH_TEMPLATE, false);
		if (matchTemplate != null) {
			oSel.setMatchTemplate(Boolean.parseBoolean(matchTemplate));
		}
		return(oSel);
	}
	
	
	
	public static void main(String argv[]) {
		GraphQuery q1 = new GraphQuery();
		ItemOffspringSelector test = new ItemOffspringSelector("test", "Test");
		ItemOffspringSelector test2 = new ItemOffspringSelector("test2", "Test2");
		ItemRelationSelector t2t = new ItemRelationSelector("t2t", "Reference", test.getId(), test2.getId());
		
		test.setPrimary(true);
		ItemAndGroupConstraint and = new ItemAndGroupConstraint();
		AttributeValueConstraint v1 = new AttributeValueConstraint("a1", AttributeValueConstraint.EQUALS, "v1");
		AttributeValueConstraint v2 = new AttributeValueConstraint("a2", AttributeValueConstraint.IS_NULL, null);
		and.add(v1);
		and.add(v2);
		test.applyConstraint(and);
		
		q1.addSelector(test);
		q1.addSelector(test2);
		q1.addSelector(t2t);
		
		XML2GraphQuery xml2query = new XML2GraphQuery();
		String xml = GraphQuery2XML.toXML(q1, 0);
		System.out.println(xml);
		try {
			GraphQuery q2 = xml2query.parse(xml);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
