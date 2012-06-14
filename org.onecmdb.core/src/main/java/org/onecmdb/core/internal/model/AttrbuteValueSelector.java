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

import java.util.List;
import java.util.SortedSet;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IValue;

public class AttrbuteValueSelector extends InstanceValueSelector {

	private ICi type;
    
    private final IAttribute attr;
    private ICi owner;

	public AttrbuteValueSelector(IAttribute attr) {
        super(attr.getValueType());
        this.attr = attr;
        this.owner = attr.getOwner();
        
	}

	public boolean isInfinite() {
		return false;
	}

    /**
     * Fetch all offsprings to the parent, and add only instances to the
     * returned list.
     * 
     * @param parent
     * @return
     */
    protected SortedSet<IValue> getInstances(ICi type) {
        SortedSet<IValue> all = super.getInstances(type);
        if ( attr.getMaxOccurs() != 1) {
        
            // remove existing sibling's values from all avaliable
            List<IAttribute> siblings = owner.getAttributesWithAlias(attr.getAlias());
            for (IAttribute sibling : siblings) {
                        all.remove(sibling.getValue());
            }
        }
        
        
        return all;
        
    }   



}
