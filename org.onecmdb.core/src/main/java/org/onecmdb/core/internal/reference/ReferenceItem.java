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
package org.onecmdb.core.internal.reference;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IReference;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;

public class ReferenceItem extends ConfigurationItem implements IReference  {

	private ICi ci;
	
	public ReferenceItem(ICi ci) {
		// Set all internal variables.
		super.copy((ConfigurationItem)ci);
	}
	
	public ICi getTarget() {
		List<IAttribute> attributes = getAttributesWithAlias("target");
		if (attributes == null || attributes.size() == 0) {
			return (null);
		}
		ICi ci = null;
		IAttribute target = attributes.get(0);
		IValue value = target.getValue();
		if (value instanceof ICi) {
			ci = (ICi)value;
		}
		return (ci);
	}

	public Set<ICi> getSourceCis() {
		List<IAttribute> refs = getAttributesWithAlias("source");
		
		if (refs == null) {
			return (Collections.EMPTY_SET);
		}
		Set<ICi> references = new HashSet<ICi>();
		for (IAttribute refAttribute : refs) {
			IValue value = refAttribute.getValue();
			if (value instanceof ICi) {
				references.add((ICi)value);
			}
		}
		return (references);
	}
	
	public Set<IAttribute> getSourceAttributes() {
		List<IAttribute> refs = daoReader.getAttributesReferringTo(this);
		if (refs == null) {
			return (Collections.EMPTY_SET);
		}
		Set<IAttribute> references = new HashSet<IAttribute>();
		references.addAll(refs);
		return (references);
	}
	
}
