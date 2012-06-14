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

import org.onecmdb.core.ICi;
import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.primitivetypes.SimpleTypeFactory;
import org.onecmdb.core.internal.storage.IDaoReader;

/**
 * Responsable to convert value/type object's into different reperesentations.
 * Common way to do serialization of a value. Need to think how this could be
 * done, I just put the logic in one class to know where to refactor.
 * 
 */
public class ObjectConverter {

	/**
	 * Used to convert a ICi (IValue, IType) to a unqiue name(String)
	 * 
	 * @param daoReader
	 * @param item
	 * @return
	 */
	public static String convertICiToUniqueName(IDaoReader daoReader, ICi item) {
		return (daoReader.getNamespace() + ":#" + convertItemIdToLong(item
				.getId()));
	}

	public static ICi convertUniqueNameToICi(IDaoReader daoReader, String s) {
		int prefixLength = daoReader.getNamespace().length() + 2;
		if (s == null || s.length() < prefixLength) {
			return (null);
		}
		String id = s.substring(prefixLength);
		Long l = new Long(id);
		ItemId itemId = new ItemId(l);
		ICi ci = daoReader.findById(itemId);
		return (ci);
	}

	public static ItemId convertUniqueNameToItemId(IDaoReader daoReader,
			String uniqueName) {
        
        if (uniqueName == null) {
            throw new NullPointerException("Name to convert cannot be null!");
        }
        if (daoReader == null) {
            throw new NullPointerException("DaoReader must not be null!");
        }
        int offset = daoReader.getNamespace().length() + 2;
        if (uniqueName.length() < offset) {
        	return(null);
        }
		String id = uniqueName.substring(offset);
		Long l = null; 
		try {
			l = new Long(id);
		} catch (NumberFormatException e) {
			return(null);
		}
		ItemId itemId = new ItemId(l);
		return (itemId);
	}

	public static IValue convertUniqueStringToIValue(IDaoReader daoReader,
			String s) {
		IValue value = (IValue) convertUniqueNameToICi(daoReader, s);
		return (value);
	}

	public static IType convertStringToType(IDaoReader daoReader, String name) {
		if (name == null) {
			return (null);
		}
		if (SimpleTypeFactory.getInstance().isSimpleType(name)) {
			return (SimpleTypeFactory.getInstance().toType(name));
		}

		// Need fetch something here.
		// Is a unique string
		IType type = (IType) convertUniqueNameToICi(daoReader, name);
		return (type);
	}

	public static String convertTypeToString(IType type) {
		if (type == null) {
			return (null);
		}
		return (type.getUniqueName());
	}

	public static ItemId convertLongToItemId(Long id) {
		if (id == null) {
			return (null);
		}
		return (new ItemId(id));
	}

	public static Long convertItemIdToLong(ItemId id) {
		if (id == null) {
			return (null);
		}
		return (id.asLong());
	}
}
