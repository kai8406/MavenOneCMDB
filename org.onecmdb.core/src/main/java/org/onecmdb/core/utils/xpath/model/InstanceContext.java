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
package org.onecmdb.core.utils.xpath.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ICiModifiable;
import org.onecmdb.core.ICmdbTransaction;

/**
 * Dynamic Wrapper for an instance ICi.
 * <br>
 * <br>Path /instance/<i>template-alias</i>/<i>isntance-alias</i> 
 *
 */
public class InstanceContext extends AbstractCacheContext implements ICmdbObjectDestruction {
	private ICi ci = null;
	private HashMap<String, List<IAttribute>> attributeMap = new HashMap<String, List<IAttribute>>();
	
	public InstanceContext(Map<String, Object> context, ICi ci) {
		super(context);
		this.ci = ci;
	}

	public Object getCi() {
		// TODO Auto-generated method stub
		return(this.ci);
	}

	public Object getNewProperty(String propertyName) {
		if (this.ci != null) {
			if (propertyName.equals("alias")) {
				return(this.ci.getAlias());
			} 
			if (propertyName.equals("displayName")) {
				return(this.ci.getDisplayName());
			}
			
			if (propertyName.equals("id")) {
				return(this.ci.getId());
			}
			
			if (propertyName.equals("displayNameExpression")) {
				return(this.ci.getDisplayNameExpression());
			}

			
			if (propertyName.equals("derivedFrom")) {
				ICi parent = this.ci.getDerivedFrom();
				if (parent == null) {
					return(null);
				}
				return(new TemplateContext(this.context, parent));
			}
			
			if (propertyName.equals("description")) {
				return(this.ci.getDescription());
			}
			
			if (propertyName.equals("lastModified")) {
				Date lastModified = this.ci.getLastModified();
				if (lastModified != null) {
					return(lastModified);
				}
				return(this.ci.getCreateTime());
			}
			
			if (propertyName.equals("created")) {
				return(this.ci.getCreateTime());
			}
	
			long start = System.currentTimeMillis();
			
			// Else a dynamic attribute.
			List<IAttribute> attributes = attributeMap.get(propertyName);
			if (attributes == null) {
				
				attributes = this.ci.getAttributesWithAlias(propertyName);
				if (attributes == null) {
					IAttribute definition = ci.getAttributeDefinitionWithAlias(propertyName);
					if (definition != null) {
						if (this.context.get("create") != null) {
							return(new AttributeValueContext(this.context, this.ci, propertyName));
						}
					}
					return(null);
				}
			}
			List<Object> objects = new ArrayList<Object>();
			for (IAttribute value : attributes) {
				objects.add(new AttributeValueContext(this.context, value));
				/*
				IValue iValue = value.getIValue();
				if (iValue instanceof ICi) {
					objects.add(new InstanceContext((ICi)iValue));
				} else {
					objects.add(iValue.getDisplayName());
				}
				*/
			}
			/*
			if (objects.size() == 1) {
				return(objects.get(0));
			}
			*/
			long stop = System.currentTimeMillis();
			log.debug(this.ci.getAlias() + "::getProperty(" + propertyName + ")=" + objects.size() + "found" +  + (stop-start) + "ms");
			return(objects);
		}
		return(null);
		
	}
	
	public String[] getNewProperties() {
		if (this.ci != null) {
			Set<IAttribute> attributes = this.ci.getAttributes();
			List<String> propertyNames = new ArrayList<String>();
			
			propertyNames.add("id");
			propertyNames.add("alias");
			propertyNames.add("derivedFrom");
			propertyNames.add("displayName");
			propertyNames.add("displayNameExpression");
			propertyNames.add("description");
			propertyNames.add("lastModified");
			propertyNames.add("created");
				
							
			for (IAttribute attribute : attributes) {
				List<IAttribute> attrSet = attributeMap.get(attribute.getAlias());
				if (attrSet == null) {
					attrSet = new ArrayList<IAttribute>();
					attributeMap.put(attribute.getAlias(), attrSet);
					propertyNames.add(attribute.getAlias());
				}
				attrSet.add(attribute);
			}
			return((String[])propertyNames.toArray(new String[0]));
		}
		return(new String[0]);
	}

	public void setProperty(String propertyName, Object value) {
		log.debug("SetValue(" + propertyName +"," + value +")");
		ICmdbTransaction tx = (ICmdbTransaction) this.context.get("tx");
		if (tx == null) {
			throw new IllegalAccessError("No transaction setup!");
		}
		ICiModifiable instanceMod = tx.getTemplate(this.ci);
		InstanceModifiableContext modContext = new InstanceModifiableContext(this.context, instanceMod);
		modContext.setProperty(propertyName, value);
		updateProperty(propertyName, modContext.getNewProperty(propertyName));
	}
	
	public String toString() {
		return(this.ci.getDisplayName());
	}

	public void destory() {
		log.debug("Destory(" + this.ci.getAlias() + ")");
		ICmdbTransaction tx = (ICmdbTransaction) this.context.get("tx");
		if (tx == null) {
			throw new IllegalAccessError("No transaction setup!");
		}
		ICiModifiable mod = tx.getTemplate(this.ci);
		mod.delete();		
	}

}
