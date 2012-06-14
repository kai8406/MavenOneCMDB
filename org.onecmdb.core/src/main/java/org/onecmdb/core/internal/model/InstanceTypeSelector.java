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
package org.onecmdb.core.internal.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IType;
import org.onecmdb.core.ITypeSelector;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.OneCmdb;

public class InstanceTypeSelector implements ITypeSelector {

    private IAttribute attr;

    /** 
     * Construct a new type selector.
     * 
     * @param item The attribute, on which the type selector should act on.
     */
    public InstanceTypeSelector(IAttribute item) {
        this.attr = item;
    }

    public Set<IType> getSet() {
        Set<IType> types = new HashSet<IType>();

        /* the attribute's value type itself first */
        types.add(this.attr.getValueType());

        /* then all offsprings */
        IType type = this.attr.getValueType();
        if (type == null) {
            Log log = OneCmdb.getLogger(InstanceTypeSelector.class);
            log.warn("No type found for attribute '" + attr.getId() + ":" 
                    + attr.getAlias() + "'");
            
        } else {
        
        
            types.addAll(type.getAllOffspringTypes());
        }
        
        return types;
    }

    public Iterator iterator() {
        return getSet().iterator();
    }

}
