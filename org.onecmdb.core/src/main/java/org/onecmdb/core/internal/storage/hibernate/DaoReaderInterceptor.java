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
package org.onecmdb.core.internal.storage.hibernate;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.onecmdb.core.internal.ccb.rfc.RFC;
import org.onecmdb.core.internal.model.ConfigurationItem;
import org.onecmdb.core.internal.storage.IDaoReader;

public class DaoReaderInterceptor extends EmptyInterceptor {

	private IDaoReader reader;

	public void setDaoReader(IDaoReader reader) {
		this.reader = reader;
	}

	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		if (entity instanceof ConfigurationItem) {
			ConfigurationItem ci = (ConfigurationItem) entity;
			ci.setDaoReader(this.reader);
			// Should we return true here, since we modify the object,
			// but that has nothing to do with the persistnacy state?
			// For now return false....
		}
		if (entity instanceof RFC) {
			RFC rfc = (RFC)entity;
			rfc.setDaoReader(this.reader);
		}

		return super.onLoad(entity, id, state, propertyNames, types);
	}
}
