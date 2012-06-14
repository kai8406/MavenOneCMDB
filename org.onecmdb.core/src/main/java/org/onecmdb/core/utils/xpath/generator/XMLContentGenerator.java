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
package org.onecmdb.core.utils.xpath.generator;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.cglib.beans.BeanMap;

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.xml.OneCmdbBeanProvider;
import org.onecmdb.core.utils.xpath.IOneCMDBContentGenerator;
import org.onecmdb.core.utils.xpath.commands.QueryCommand;
import org.onecmdb.core.utils.xpath.model.AttributeValueContext;
import org.onecmdb.core.utils.xpath.model.InstanceCollectionContext;
import org.onecmdb.core.utils.xpath.model.InstanceContext;
import org.onecmdb.core.utils.xpath.model.TemplateCollectionContext;
import org.onecmdb.core.utils.xpath.model.TemplateContext;

/**
 * Generate XMLformat for query. 
 * On Templates the full (with all attributes) xml is retuned. 
 * On instnaces quiered attributes are returned.  
 *
 */
public class XMLContentGenerator implements IOneCMDBContentGenerator {
	private QueryCommand cmd;
	private HashMap<String, Object> beanInternalMap = new HashMap<String, Object>();
	private ArrayList<CiBean> templatesBeans = new ArrayList<CiBean>();
	private ArrayList<CiBean> instancesBeans = new ArrayList<CiBean>();
	private Log log = LogFactory.getLog(this.getClass());
	
	
	public XMLContentGenerator() {
		// Setup Internal hashmap.
		beanInternalMap.put("id", Boolean.TRUE);
		beanInternalMap.put("alias", Boolean.TRUE);	
		beanInternalMap.put("displayName", Boolean.TRUE);
		beanInternalMap.put("displayNameExpression", Boolean.TRUE);
		beanInternalMap.put("derivedFrom", Boolean.TRUE);
		beanInternalMap.put("description", Boolean.TRUE);
	}

	public void setCommand(QueryCommand cmd) {
		this.cmd = cmd;
	}

	public String getContentType() {
		return("text/xml");
	}

	public void transfer(OutputStream out) {
		log.debug("Debug Query path <" + cmd.getPath() + ">");
		PrintWriter text = new PrintWriter(new OutputStreamWriter(out), false);
		
		// Generate response beans.
		generateBeans();
		
		// Generate Response.
		generateResponse(text);
		
		// Flush text.
		text.flush();
	}
	
	private void generateBeans() {
		Iterator<Pointer> iter = cmd.getPathPointers();
		
		boolean first = true;
		while(iter.hasNext()) {
			Pointer p = (Pointer)iter.next();
			
			Object value = p.getValue();
			if (value instanceof InstanceCollectionContext) {
				Iterator colIterator = cmd.getRelativePointers(p, "*");
				while(colIterator.hasNext()) {
					Pointer instancePointer = (Pointer)colIterator.next();
					Object instanceValue = instancePointer.getValue();
					
					if (instanceValue instanceof InstanceContext) {
						generateInstanceXML(instancePointer, (InstanceContext)instanceValue);
					}
				}
				
			}
			
			if (value instanceof TemplateCollectionContext) {
				Iterator colIterator = cmd.getRelativePointers(p, "*");
				while(colIterator.hasNext()) {
					Pointer templatePointer = (Pointer)colIterator.next();
					Object templateValue = templatePointer.getValue();
					
					if (templateValue instanceof TemplateContext) {
						generateTemplateXML(templatePointer, (TemplateContext)templateValue);
					}
				}
			}
			
			if (value instanceof TemplateContext) {
				generateTemplateXML(p, (TemplateContext)value);
			}
			
			if (value instanceof InstanceContext) {
				InstanceContext context = (InstanceContext)value;
				generateInstanceXML(p, context);
			}
		}
	}
	
	public List<CiBean> getBeans() {
		// Generate requested beans from the command.
		generateBeans();
		
		// Collect all beans.
		List<CiBean> beans = new ArrayList<CiBean>();
		beans.addAll(templatesBeans);
		beans.addAll(instancesBeans);
		
		return(beans);
	}
	              
	private void generateResponse(PrintWriter text) {
		out(text, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out(text, "<onecmdb>");
		if (templatesBeans.size() > 0) {
			out(text, "\t<templates>");
			for (CiBean bean : templatesBeans) {
				out(text, bean.toXML(2));
			}
			out(text, "\t</templates>");
		}
		if (instancesBeans.size() > 0) {
			out(text, "\t<instances>");
			for (CiBean bean : instancesBeans) {
				out(text, bean.toXML(2));
			}
			out(text, "\t</instances>");
			
		}
		out(text, "</onecmdb>");
		
	}

	private void generateTemplateXML(Pointer p, TemplateContext context) {		
		ICi ci = context.getICi();
		
		// Convert ci to a bean.
		OneCmdbBeanProvider provider = new OneCmdbBeanProvider();
		
		CiBean bean = provider.convertCiToBean(ci); 
		
		// Add bean to template list.
		templatesBeans.add(bean);
	}
	
	private void generateInstanceXML(Pointer p, InstanceContext context) {		
		
		String[] outputAttributes = cmd.getOutputAttributeAsArray();
		
		ArrayList<Iterator<Pointer>> resultSet = new ArrayList<Iterator<Pointer>>();
		
		for (String outputAttribute : outputAttributes) {
			Iterator<Pointer> outputAttrPointersIter = cmd.getRelativePointers(p, outputAttribute);
			resultSet.add(outputAttrPointersIter);					
		}
		generateOutput(context, (NodePointer)p, resultSet);
	
	}
	
	private void generateOutput(InstanceContext context, NodePointer root, List<Iterator<Pointer>> resultSet) {
		CiBean bean = new CiBean();
		TemplateContext parent = (TemplateContext) context.getNewProperty("derivedFrom"); 
		bean.setDerivedFrom(parent.getProperty("alias").toString());
		bean.setAlias(context.getNewProperty("alias").toString());
		Object id = context.getProperty("id");
		if (id instanceof ItemId) {
			bean.setId(((ItemId)id).asLong());
		}
		//bean.setDisplayNameExpression(context.getNewProperty("displayNameExpression").toString());		
		bean.setDisplayName(context.getNewProperty("displayName").toString());
		bean.setTemplate(false);
		// Add attribute values.
		HashMap<String, CiBean> beanMap = new HashMap<String, CiBean>();
		beanMap.put(bean.getAlias(), bean);
		for (Iterator<Pointer> pointers : resultSet) {
			while(pointers.hasNext()) {
				Pointer p = pointers.next();
				fillBeanMap(beanMap, bean, root,(NodePointer)p);
			}
		}
		instancesBeans.add(bean);
	}
	
	private void fillBeanMap(HashMap<String, CiBean> map, CiBean currentBean, NodePointer root, NodePointer p) {
		// Get the attribute NodePointer name.
		NodePointer childToRoot = getChildToRoot(root, p);
		
		// Not a child of root, don't know what to do..
		if (childToRoot == null) {
			return;
		}
		
		
		
		// add the value.
		Object value = childToRoot.getValue();
		
	
		if (value instanceof AttributeValueContext) {
			AttributeValueContext attrValue = (AttributeValueContext)value;
			value = attrValue.getProperty("iValue");
		}
		
		
		if (value instanceof InstanceContext) {
			InstanceContext instance = (InstanceContext)value;
			String alias = instance.getProperty("alias").toString();
			CiBean instanceBean = map.get(alias);
			if (instanceBean == null) {
				instanceBean = new CiBean();
				instanceBean.setDerivedFrom(instance.getProperty("derivedFrom").toString());
				instanceBean.setAlias(alias);
				instanceBean.setDisplayName(instance.getNewProperty("displayName").toString());
				instanceBean.setTemplate(false);
				
				instancesBeans.add(instanceBean);
			}
				
			String attAlias = childToRoot.getName().toString();
			if (!isInternalState(attAlias)) {
				ValueBean vBean = new ValueBean();
				vBean.setAlias(attAlias);
				vBean.setValue(instanceBean.getAlias());
				vBean.setComplexValue(true);
				
				currentBean.addAttributeValue(vBean);
				map.put(alias, instanceBean);					
			}
			
			fillBeanMap(map, instanceBean, childToRoot, p);
			
		} else {
			String attAlias = childToRoot.getName().toString();
			if (!isInternalState(attAlias)) {
				ValueBean vBean = new ValueBean();
				vBean.setAlias(attAlias);
				if (value != null) {
					vBean.setValue(value.toString());
				}
				currentBean.addAttributeValue(vBean);
			}
		}
		
		
	}
	
	/**
	 * A Ci has internal variables like alias, displayName(Expression) and id. 
	 * @param attAlias
	 * @return
	 */
	public boolean isInternalState(String attAlias) {
		if (beanInternalMap.get(attAlias) == null) {
			return(false);
		}
		return(true);
	}

	private StringBuffer generateXMLTagForAttribute(PrintWriter text, NodePointer root, NodePointer p) {
		// Get the attribute NodePointer name.
		NodePointer childToRoot = getChildToRoot(root, p);
		
		StringBuffer sb = new StringBuffer();
		
		// Not a child of root, don't know what to do..
		if (childToRoot == null) {
			return(sb);
		}
		
		// start tag for attribute name
		sb.append("<" + childToRoot.getName() + ">\n");
		
		// add the value.
		Object value = childToRoot.getValue();
		if (value instanceof InstanceContext) {
			
			
		} else {
			sb.append("\t" +  value.toString() + "\n");
		}
		
		// End tag for attribute name
		sb.append("</" + childToRoot.getName() + ">\n");
		
		return(sb);
	}

	private NodePointer getChildToRoot(NodePointer root, NodePointer p) {
		NodePointer parent = p.getParent();
		if (parent == null) {
			return(null);
		}
		if (parent.equals(root)) {
			return(p);
		}
		return(getChildToRoot(root, parent));
	}

	private void out(PrintWriter w, String text) {
		w.println(text);
		//System.out.println(text);
	}

}
