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
package org.onecmdb.core.internal.job.workflow;

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

public class CiToJavaObject {
	private Log log = LogFactory.getLog(this.getClass());
	private HashMap<String, String> aliasToClassNameMap = new HashMap<String, String>();
	private HashMap<String, Class> aliasToClassMap = new HashMap<String, Class>();
	private List<URL> urls;

	public void setClasspath(List<URL> urls) {
		this.urls = urls;
	}
	
	private ClassLoader getClassLoader() {
		if (this.urls != null) {
			return(new URLClassLoader((URL[])this.urls.toArray(), this.getClass().getClassLoader()));
		}
		return(this.getClass().getClassLoader());
	}
	
	public void addAliasToClass(String alias, String javaClass) {
		this.aliasToClassNameMap.put(alias, javaClass);
	}

	public void setAliasToClass(Map<String, String> map) {
		this.aliasToClassNameMap.putAll(map);
	}

	public Object toBeanObject(ICi ci) {
		String alias;
		if (ci.isBlueprint()) {
			alias = ci.getAlias();
		} else {
			ICi parent = ci.getDerivedFrom();
			alias = parent.getAlias();
		}
		Class cl = getClassForAlias(alias);
		if (cl == null) {
			log.warn("No class with alias " + alias + " found!");
			return(null);
			/*
			throw new IllegalArgumentException("No class found for alias <"
					+ alias + ">");
			*/
		}
		Object instance = null;
		try {
			instance = cl.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("alias <" + alias + "> "
					+ e.toString(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("alias <" + alias + "> "
					+ e.toString(), e);
		}

		HashMap<String, Object> map = buildAttributeMap(ci);

		Method[] methods = instance.getClass().getMethods();
		HashMap<String, Method> methodMap = new HashMap<String, Method>();
		for (int i = 0; i < methods.length; i++) {
			String name = methods[i].getName().toLowerCase();
			methodMap.put(name, methods[i]);
		}
		for (String methodName : map.keySet()) {
			Object o = map.get(methodName);
			String setMethodName = "set" + methodName;
			String setMethodNameLowerCase = setMethodName.toLowerCase();
			Method m = methodMap.get(setMethodNameLowerCase);
			if (m == null) {
				log.warn("No method " + setMethodName +" found in class " + cl.getName() +" with alias " + alias + " found!");
							
				// For now we skip the set method...
				continue;
				/*
				throw new IllegalArgumentException(
						"No method in " + cl.getName() + "." + setMethodName + "(" + o
								+ ")");
				*/				
			}
			
			try {
				m.invoke(instance, new Object[] {o});
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Error:" + cl.getName() + "." + m.getName() + "(" + o
								+ ") : " + e.toString(), e);
			}
		}
		return (instance);
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
				List objects = new ArrayList();
				List<IAttribute> list = (List<IAttribute>) o;
				for (IAttribute a : list) {
					Object value = getValueFromAttribute(a);
					if (value != null) {
						objects.add(value);
					}
				}
				setterMap.put(alias, objects);
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
			// Simple value.
			return (toBeanObject((ICi) value));
		}
		// Simple type.
		return (value.getAsString());
	}

	public String getAliasForClass(Class triggerClass) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Class getClassForAlias(String alias) {
		Class clazz = aliasToClassMap.get(alias);
		if (clazz == null) {
			
			ClassLoader loader = getClassLoader();
			
			String className = aliasToClassNameMap.get(alias);
			if (className == null) {
				return(null);
			}
			try {
				clazz = loader.loadClass(className);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Class " + className + " for alias " + alias + " not found: " + e.toString(), e);
			}
			aliasToClassMap.put(alias, clazz);
		}
		return(clazz);
	}


}
