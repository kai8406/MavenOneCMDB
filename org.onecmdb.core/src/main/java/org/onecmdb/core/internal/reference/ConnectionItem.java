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

import java.util.List;
import java.util.Set;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IObjectScope;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.BasicAttribute;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.storage.IDaoReader;

/**
 * Might create this as a real Object that exists in the db, but for now it's
 * just a Wrapper help class. Could think it could look like this: implements
 * IConnectionType
 * 
 * Internal class must be used under a Transaction scope.
 * 
 * @author niklas
 * 
 */
public class ConnectionItem {
	private ICi ci;

	private IDaoReader daoReader;

	public ConnectionItem(IDaoReader reader, ICi ci) {
		this.ci = ci;
		this.daoReader = reader;
	}

	public void setTarget(ICi target, IObjectScope scope) {
		IAttribute targetAttribute = getAttribute(scope.getAttributesForCi(ci),
				"target");
		if (targetAttribute instanceof BasicAttribute) {
			((BasicAttribute) targetAttribute).setType((IType) target);
			((BasicAttribute) targetAttribute)
					.setValueAsString(((IValue) target).getAsString());
		}
	}

	public ICi getTarget() {
		if (ci instanceof ConfigurationItem) {
			ConfigurationItem ciItem = (ConfigurationItem)ci;
			Long targetId = ciItem.getTargetId();
			if (targetId != null) {
				return(this.daoReader.findById(new ItemId(targetId)));
			}
		}
			
		IAttribute target = getUniqAttribute(ci.getAttributesWithAlias("target"));
		if (target == null) {
			return (null);
		}
		ICi ci = null;
		if (target instanceof BasicAttribute) {
			// Will use the Reference Ci's type (ICI type) to laod the ICi!
			ci = (ICi) target.getValue();
		}
		return (ci);
	}

	protected IAttribute getUniqAttribute(List<IAttribute> list) {
		if (list.size() == 1) {
			return(list.get(0));
		}
		return(null);
	}
	protected IAttribute getAttribute(Set<IAttribute> set, String alias) {
		for (IAttribute a : set) {
			if (a.getAlias().equals(alias)) {
				return (a);
			}
		}
		return (null);
	}

}
