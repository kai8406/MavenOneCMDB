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
package org.onecmdb.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.core.internal.storage.IDaoReader;

public interface IObjectScope {
	public Collection<ICi> getNewICis();

	public Collection<ICi> getModifiedICis();

	public Collection<ICi> getDestroyedICis();

	// Put this in the IRFC code instead...
	public ICi getCIFromRFC(IRFC rfc);
	public IAttribute getAttributeFromRFC(IRFC rfc);

	public void addNewICi(ICi ci);

	public void addModifiedICi(ICi ci);

	public void addDestroyedICi(ICi ci);

	// public void mapRfcToCi(IRFC rfc, ICi ci);

	public Set<IAttribute> getAttributesForCi(ICi ci);
	
	public ICi getAttributeOwner(IAttribute attribute);

	public void addAttributeToCi(ICi item, IAttribute ba);

	public ICi getICiById(ItemId id);

	public ICi getICiFromAlias(String alias);

	public IDaoReader getDaoReader();

	public void addOffspringToCi(ICi parent, ICi offspring);
	
	public Set<ICi> getOffspringForCi(ICi ci);

	public boolean isDestroyed(ICi derivedFrom);

	public int getCiModified();
	
	public int getCiAdded();
	
	public int getCiDeleted();
	
	public ISession getSession();

	public List<ICi> getReferrer(ICi ci);

	public List<IAttribute> getAttributeForReference(ICi referrer);
}
