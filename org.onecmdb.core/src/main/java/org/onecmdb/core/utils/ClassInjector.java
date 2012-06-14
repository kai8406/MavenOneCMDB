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
package org.onecmdb.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IValue;

/**
 * Class that can populate java object's from a CI with attributes.
 * The injection will only warn if attribute can not be injected. 
 */
public class ClassInjector {
	private static final String CLASS_ATTRIBUTE_NAME = "javaClass";
	
	private Log log = LogFactory.getLog(this.getClass());
	private HashMap<String, String> aliasToClassNameMap = new HashMap<String, String>();
	private HashMap<String, Class> aliasToClassMap = new HashMap<String, Class>();
	private List<URL> urls;

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
	
	private ClassLoader getClassLoader() {
		if (this.urls != null) {
			return(new URLClassLoader((URL[])this.urls.toArray(), this.getClass().getClassLoader()));
		}
		return(this.getClass().getClassLoader());
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

	public Object toBeanObject(ICi ci) {
		Class cl = getClassForCi(ci);
		if (cl == null) {
			log.info("No class with alias " + ci.getAlias() + " found!");
			return(null);
			/*
			throw new IllegalArgumentException("No class found for alias <"
					+ alias + ">");
			*/
		}
		
		Object instance = null;
		Constructor constructors[] = cl.getConstructors();
		// Check if it support constructor with ICi.
		for (Constructor con : constructors) {
			Class conPar[] = con.getParameterTypes();
			if (conPar.length == 1) {
				// Check if we can create with ICi as constructor.
				if (conPar[0].isAssignableFrom(ICi.class)) {
					try {
						instance = con.newInstance(new Object[] {ci});
					} catch (Throwable e) {
						log.error(cl.getSimpleName()+ ".newInstance(ICi) error with alias '" + ci.getAlias() +"' class '" + cl.getName() +"' : " + e.toString());
					}
					break;
				}
			}
		}
		// Default constructor.
		if (instance == null) {
			try {
				
				instance = cl.newInstance();
			} catch (Throwable e) {
				log.error("Instacation exception with alias '" + ci.getAlias() +"' class '" + cl.getName() +"' : " + e.toString());
				return(null);
			}
		}
		// Start to inject attributes
		injectAttributes(instance, ci);
		return (instance);
	}

	public void injectAttributes(Object instance, ICi ci) {
		HashMap<String, Object> map = buildAttributeMap(ci);
		
		for (String alias : map.keySet()) {
			Object o = map.get(alias);
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
						log.info("No method '" + setMethod +"(" + o.getClass() +")' found in class '" + instance.getClass().getName() +"'");
					}
					
					continue;
				}
			} else {
				try {
					m = instance.getClass().getMethod(setMethod, new Class[] {o.getClass()});
					log.info("INJECT : " + ci.getAlias() +"." + setMethod + "(" + o + ":" + o.getClass().getSimpleName() + ")");
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
	
	
	private HashMap<String, Object> buildAttributeMap(ICi ci) {
		HashMap<String, Object> setterMap = new HashMap<String, Object>();

		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
		for (IAttribute a : ci.getAttributes()) {
			if (a.getMaxOccurs() == 0) {
				continue;
			}
			if (a.getMaxOccurs() == 1) {
				// Add One object
				attributeMap.put(a.getAlias(), a);
			} else {
				List<IAttribute> list = (List<IAttribute>) attributeMap.get(a
						.getAlias());
				if (list == null) {
					list = new ArrayList<IAttribute>();
					attributeMap.put(a.getAlias(), list);
				}
				list.add(a);
			}
		}

		for (String alias : attributeMap.keySet()) {
			Object o = attributeMap.get(alias);
			if (o instanceof IAttribute) {
				Object value = getValueFromAttribute((IAttribute) o);
				if (value != null) {
					setterMap.put(alias, value);
				}
			}
			if (o instanceof List) {
				List objects = null;
				List<IAttribute> list = (List<IAttribute>) o;
				for (IAttribute a : list) {
					Object value = getValueFromAttribute(a);
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

	private Object getValueFromAttribute(IAttribute a) {
		IValue value = a.getValue();
		if (value == null) {
			return (null);
		}
		if (value instanceof ICi) {
			// Complex value.
			// Check if we have a alias to build a class, else the ci is returned.
			Object o = toBeanObject((ICi) value);
			if (o != null) {
				return(o);
			}
			return(value);
		}
		// Simple type.
		return (value.getAsJavaObject());
	}

	public String getAliasForClass(Class triggerClass) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Class getClassForCi(ICi ci) {
		if (ci == null) {
			return(null);
		}
		String alias = ci.getAlias();
		
		// Need a class name
		String className = null;
		
		// First check if the ci is configured with a javaClass 
		// attribute, use that.
		List<IAttribute> clazzes = ci.getAttributesWithAlias(CLASS_ATTRIBUTE_NAME);
		if (clazzes != null && clazzes.size() == 1) {
			IValue value = clazzes.get(0).getValue();
			if (value != null) {
				className = value.getAsString();
			}
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
				clazz = getClassForCi(ci.getDerivedFrom());
			} else {
				try {
					clazz = loader.loadClass(className);
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException("Class " + className + " for alias " + alias + " not found: " + e.toString(), e);
				}
			}
			if (clazz != null) {
				log.info("Map alias '" + alias + "' to class " + clazz.getSimpleName() + "'");
				aliasToClassMap.put(alias, clazz);
			}
		}
		return(clazz);
	}


}
