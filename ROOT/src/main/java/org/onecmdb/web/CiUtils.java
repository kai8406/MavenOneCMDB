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
package org.onecmdb.web;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;


/** 
 * Method operating on CIs. 
 * @author nogun
 *
 */
public class CiUtils {

    /** 
     * Fetch all attributes <em>keyed</em> after its alias. The key's value
     * is a list of contained attributes. This mechanism treats single valued
     * attributes and  multi valued ditos the same. To find out if the 
     * Attribute is multi valued, the attributes <em>meta data</em>, or 
     * policy should be investigated, or simple check the size of the list.
     * 
     * <p>An implicit sorting, based on the attribute's alias is used</p>
      */
    public static Map<String, BeanList<IAttribute>> getAttributeMap(ICi ci) {
        final Map<String, BeanList<IAttribute>> attrMap  
        = new TreeMap<String,BeanList<IAttribute>>();
        if (ci == null) {
            return attrMap;
        }
        
        ArrayList<IAttribute> attrs = new ArrayList<IAttribute>(ci.getAttributes());
        for (IAttribute attr : attrs) {
            
            String alias = attr.getAlias();
            BeanList<IAttribute> attrList = attrMap.get(alias);
            if (attrList == null) {
                attrList = new BeanList<IAttribute>(attr);
                attrMap.put(alias, attrList);
            }
            attrList.getValues().add(attr);
        } 
        // merge in addable attributes
        ArrayList<IAttribute> addable = new ArrayList<IAttribute>(ci.getAddableAttributes());
        for (IAttribute attr : addable) {
            String alias = attr.getAlias();
            BeanList<IAttribute> attrList = attrMap.get(alias);
            if (attrList == null) {
                attrList = new BeanList<IAttribute>(attr);
                attrMap.put(alias, attrList);
            }
            // the current attribute is not part of the actual attributes, 
            // therefore we dont't add it to the list of avail attributes.
        }
        return attrMap;
    }

    
    
}
