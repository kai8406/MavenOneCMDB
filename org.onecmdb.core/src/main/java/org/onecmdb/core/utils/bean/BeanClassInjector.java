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
package org.onecmdb.core.utils.bean;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.utils.IBeanProvider;



/**
 * Class that can populate java object's from a CI with attributes.
 * The injection will only warn if attribute can not be injected. 
 */
public class BeanClassInjector {
	private static final String CLASS_ATTRIBUTE_NAME = "javaClass";
	
	private Log log = LogFactory.getLog(this.getClass());
	private HashMap<String, String> aliasToClassNameMap = new HashMap<String, String>();
	private HashMap<String, Class> aliasToClassMap = new HashMap<String, Class>();
	private List<URL> urls;
	private IBeanProvider provider;
	private HashMap<String, String> valueMap;

	/**
	 * Optional only needed if the class is not included in<br> 
	 * the startup classpath<br> 
	 * <br>
	 * Location of classpath where java classes can be loaded from.
	 * <br>
	 * @param urls
	 */
	public void setClasspath(List<URL> urls) {
		this.urls = urls;
	}
	
	public void setValueMap(HashMap<String, String> map) {
		this.valueMap = map;
	}
	private ClassLoader getClassLoader() {
		if (this.urls != null) {
			return(new URLClassLoader((URL[])this.urls.toArray(), this.getClass().getClassLoader()));
		}
		return(this.getClass().getClassLoader());
	}
	
	public void setBeanProvider(IBeanProvider provider) {
		this.provider = provider;
	}
	
	/**
	 * Map CI' alias names to classes that will be instantiated.
	 * 
	 * @param alias
	 * @param javaClass
	 */
	public void addAliasToClass(String alias, String javaClass) {
		this.aliasToClassNameMap.put(alias, javaClass);
	}

	public void setAliasToClass(Map<String, String> map) {
		this.aliasToClassNameMap.putAll(map);
	}

	public Object beanToObject(CiBean ci) {
		if (ci == null) {
			return(null);
		}
		Class cl = getClassForCi(ci);
		if (cl == null) {
			log.warn("No class with alias " + ci.getAlias() + " found!");
			return(null);
			/*
			throw new IllegalArgumentException("No class found for alias <"
					+ alias + ">");
			*/
		}
		
		Object instance = null;
		
		// Default constructor.
		if (instance == null) {
			try {
				
				instance = cl.newInstance();
			} catch (Throwable e) {
				log.error("Instantiation exception with alias '" + ci.getAlias() +"' class '" + cl.getName() +"' : " + e.toString());
				return(null);
			}
		}
		// Start to inject attributes
		injectAttributes(instance, ci);
		return (instance);
	}

	public void injectAttributes(Object instance, CiBean ci) {
		HashMap<String, Object> map = buildAttributeMap(ci);
		
		for (String alias : map.keySet()) {
			Object o = map.get(alias);
			if (valueMap != null) {
				
				if (o instanceof String) {
					String replace = valueMap.get(o);
					if (replace != null) {
						o = replace;
					}
				}
			}
			
			Character c = alias.charAt(0);
			String setMethod = "set" + Character.toUpperCase(c) + alias.substring(1);
			
			Method m = null;
			if (true) {
				Method methods[] = instance.getClass().getMethods();
				for (Method method : methods) {
					if (method.getName().equals(setMethod)) {
						m = method;
						break;
					}
				}
				if (m == null) {
					if (!alias.equals(CLASS_ATTRIBUTE_NAME)) {
						log.warn("No method '" + setMethod +"(" + o.getClass() +")' found in class '" + instance.getClass().getName() +"'");
					}
					continue;
				}
			} else {
				try {
					m = instance.getClass().getMethod(setMethod, new Class[] {o.getClass()});
					log.debug("INJECT : " + ci.getAlias() +"." + setMethod + "(" + o + ":" + o.getClass().getSimpleName() + ")");
				} catch (Throwable e) {
					log.warn("Problem getting method '" + setMethod +"(" + o.getClass() +")' in class '" + instance.getClass().getName() +"' : " + e.toString());
					
					// For now we skip the set method...
					continue;
					/*
					 throw new IllegalArgumentException(
					 "No method in " + cl.getName() + "." + setMethodName + "(" + o
					 + ")");
					 */				
				}
			} 		
			try {
				m.invoke(instance, new Object[] {o});
			} catch (Exception e) {
				log.error("Can't set object '" + o.getClass() + "' on method " + setMethod +" in class '" + instance.getClass().getName() + "'");
				/*			
				throw new IllegalArgumentException(
						"Error:" + instance.getName() + "." + m.getName() + "(" + o
								+ ") : " + e.toString(), e);
				*/
			}
		}
	}
	
	
	private HashMap<String, Object> buildAttributeMap(CiBean ci) {
		HashMap<String, Object> setterMap = new HashMap<String, Object>();

		CiBean template = null;
		if (ci.isTemplate()) {
			template = ci;
		} else {
			template = this.provider.getBean(ci.getDerivedFrom());
		}
		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
		for (AttributeBean a : template.getAttributes()) {
			List<ValueBean> values = ci.fetchAttributeValueBeans(a.getAlias());
			if (a.getMaxOccurs().equals("0") || values == null || values.size() < 1) {
				continue;
			}
			
			if (a.getMaxOccurs().equals("1")) {
				// Add One object
				attributeMap.put(a.getAlias(), values.get(0));
			} else {
				List<ValueBean> list = (List<ValueBean>) attributeMap.get(a
						.getAlias());
				if (list == null) {
					list = new ArrayList<ValueBean>();
					attributeMap.put(a.getAlias(), list);
				}
				for (ValueBean vBean : values) {
					list.add(vBean);
				}
			}
		}

		for (String alias : attributeMap.keySet()) {
			Object o = attributeMap.get(alias);
			if (o instanceof ValueBean) {
				
				Object value = getValueFromAttribute(template, (ValueBean) o);
				if (value != null) {
					setterMap.put(alias, value);
				}
			}
			if (o instanceof List) {
				List objects = null;
				List<ValueBean> list = (List<ValueBean>) o;
				for (ValueBean v : list) {
					Object value = getValueFromAttribute(template, v);
					if (value != null) {
						if (objects  == null) {
							objects = new ArrayList();
						}
						objects.add(value);
					}
				}
				if (objects != null) {
					setterMap.put(alias, objects);
				}
			}
		}
		return (setterMap);
	}

	private Object getValueFromAttribute(CiBean owner, ValueBean value) {
		if (value == null) {
			return (null);
		}
		if (value.isComplexValue()) {
			// Complex value.
			// Check if we have a alias to build a class, else the ci is returned.
			if (value.getValue() == null || value.getValue().length() == 0) {
				return(null);
			}
			Object o = beanToObject(provider.getBean(value.getValue()));
			if (o != null) {
				return(o);
			}
			return(value);
		}
		// Simple type, here we return a String but we need to convert it...
		Object javaType = getJavaValue(owner.getAttribute(value.getAlias()).getType(), value.getValue());
		return (javaType);
	}

	
	private Object getJavaValue(String typeAlias, String valueString) {
		IType type = SimpleTypeFactory.getInstance().toType(typeAlias);
		if (type == null) {
			throw new IllegalArgumentException("Simple Type <" + typeAlias + "> is not found!");
		}
		IValue value = type.parseString(valueString);
		return(value.getAsJavaObject());
	}

	public Class getClassForCi(CiBean ci) {
		if (ci == null) {
			return(null);
		}
		String alias = ci.getAlias();
		
		// Need a class name
		String className = null;
		
		// First check if the ci is configured with a javaClass 
		// attribute, use that.
		List<ValueBean> clazzes = ci.fetchAttributeValueBeans(CLASS_ATTRIBUTE_NAME);
		if (clazzes != null && clazzes.size() == 1) {
			className = clazzes.get(0).getValue();
		}
		// If no className is provided by the ci,
		// check the aliasToClassName map.
		if (className == null) {
			className = aliasToClassNameMap.get(alias);
		}
		
		Class clazz = aliasToClassMap.get(alias);
		if (clazz == null) {
			
			ClassLoader loader = getClassLoader();
			
			if (className == null) {
				clazz = getClassForCi(provider.getBean(ci.getDerivedFrom()));
			} else {
				try {
					clazz = loader.loadClass(className);
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException("Class " + className + " for alias " + alias + " not found: " + e.toString(), e);
				}
			}
			if (clazz != null) {
				log.debug("Map alias '" + alias + "' to class " + clazz.getSimpleName() + "'");
				aliasToClassMap.put(alias, clazz);
			}
		}
		return(clazz);
	}


}
