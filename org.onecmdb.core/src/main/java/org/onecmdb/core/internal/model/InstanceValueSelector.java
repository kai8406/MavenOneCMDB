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

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.IValueSelector;

public class InstanceValueSelector implements IValueSelector {

	private final ICi type;

	public InstanceValueSelector(ICi type) {
		this.type = type;
	}

	public InstanceValueSelector(IType type) {
	    if (type instanceof ICi) {
	        this.type = (ICi) type;
        } else {
            this.type = null;
        }
    }

    public boolean isInfinite() {
		return false;
	}

	/**
	 * Retrieve all instances of the CI specified as type.
	 */
	public final SortedSet<IValue> getSet() {
		SortedSet<IValue> instances = getInstances(this.type);
		return (instances);
	}

	/**
	 * Fetch all offsprings to the type, and add only <em>instances</em> to the
	 * returned list.
	 * 
	 * @param type
	 * @return
	 */
	protected SortedSet<IValue> getInstances(ICi type) {
		SortedSet<IValue> instances = new TreeSet<IValue>(IValue.VALUE_COMPARATOR);
        if (type != null) {
            Set<ICi> cis = type.getOffsprings();
            for (ICi ci : cis) {
                if (!ci.isBlueprint()) {
                    instances.add(ci); // we have an instance
                } else {
                    instances.addAll(getInstances(ci));
                }
            }
        }
		return instances;
	}

	public final Iterator iterator() {
		return (getSet().iterator());
	}

}
