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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.ccb.CiModifiable;

public class RfcGenerator {
	private Log log = LogFactory.getLog(this.getClass());
	
	public RfcGenerator() {
		
	}
	
	public List<IRFC> generateRfc(ICi ci, Object instance) {
		CiModifiable mod = new CiModifiable();
		mod.setTarget(ci);
		
		HashSet processAtributes = new HashSet<String>();
		for (IAttribute a : ci.getAttributes()) {
			if (processAtributes.contains(a.getAlias())) {
				continue;
			}
			if (a.getMaxOccurs() == 1) {
				Object o = invokeGetMethod(instance, a.getAlias());
				if (o instanceof ICi) {
					mod.setDerivedAttributeValue(a.getAlias(), 0, (IValue)o);
				} else {
					mod.setDerivedAttributeValue(a.getAlias(), 0 , (o == null ? null : a.getValueType().parseString(o.toString())));
				}
			} 
			if (a.getMaxOccurs() > 1) {
				Object list = invokeGetMethod(instance, a.getAlias());
				if (list instanceof List) {
					int index = 0;
					for (Object o : (List)list) {
						if (o instanceof ICi) {
							mod.setDerivedAttributeValue(a.getAlias(), index, (IValue)o);
						} else {
							mod.setDerivedAttributeValue(a.getAlias(), index , (o == null ? null : a.getValueType().parseString(o.toString())));
						}
										index++;
					}
				}
			}
		}
		ArrayList<IRFC> list = new ArrayList<IRFC>();
		list.add(mod);
		return(list);
	}
	
	private Object invokeGetMethod(Object instance, String name) {
		Character c = name.charAt(0);
		String baseMethodName = Character.toUpperCase(c) + name.substring(1);
		String getMethod = "get" + baseMethodName;
		try {
			Method m = instance.getClass().getMethod(getMethod, new Class[] {});
			Object o = m.invoke(instance, new Object[] {});
			return(o);
		} catch (Throwable e) {
			//log.warn("ERROR: " + instance.getClass().getSimpleName() + "." + getMethod + "() : " + e);
		}
		String isMethod = "is" + baseMethodName;
		try {
			Method m = instance.getClass().getMethod(isMethod, new Class[] {});
			Object o = m.invoke(instance, new Object[] {});
			return(o);
		} catch (Throwable e) {
			log.warn("ERROR: " + instance.getClass().getSimpleName() + ".[get|is]" + baseMethodName + "() : " + e);
		}
		
		
		
		return(null);
	}
		
}
