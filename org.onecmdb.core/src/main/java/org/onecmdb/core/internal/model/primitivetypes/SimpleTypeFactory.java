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
package org.onecmdb.core.internal.model.primitivetypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.onecmdb.core.IType;

public class SimpleTypeFactory {

	/**
	 * Easy way to get hold of all primitive types, from java code.
	 * 
	 */
	public static IType STRING = new StringType();

	public static IType BOOLEAN = new BooleanType();

	public static IType UBYTE = new UnsignedByteType();

	public static IType INTEGER = new IntegerType();

	//public static IType LONG = new LongType();

	//public static IType INT = new IntType();

	//public static IType SHORT = new ShortType();

	public static IType USHORT = new UnsignedShortType();

	//public static IType BYTE = new ByteType();

	public static IType FLOAT = new FloatType();

	//public static IType DOUBLE = new DoubleType();

	public static IType DATETIME = new DateTimeType();

	public static IType TIME = new TimeType();

	public static IType DATE = new DateType();

	public static IType DURATION = new DurationType();

	public static IType ANYURI = new URIType();

	public static IType PASSWORD = new PasswordType();


    public static IType MULTIPLICITY = new URIType();

    
    public static IType ALL_TYPES[] = { STRING, BOOLEAN, 
			INTEGER, FLOAT, DATETIME, TIME, DURATION, DATE,
			ANYURI, USHORT, MULTIPLICITY, PASSWORD};

	private HashMap<String, IType> typeMap;

	private HashSet<IType> typeSet = new HashSet<IType>();

	private static SimpleTypeFactory instance = null;

	private SimpleTypeFactory() {
		// Build hash map.
		typeMap = new HashMap<String, IType>();
		for (int i = 0; i < ALL_TYPES.length; i++) {
			typeMap.put(ALL_TYPES[i].getUniqueName(), ALL_TYPES[i]);
			typeSet.add(ALL_TYPES[i]);
		}

	}

	public static SimpleTypeFactory getInstance() {
		if (instance == null) {
			instance = new SimpleTypeFactory();
		}
		return (instance);
	}

	public Set<IType> getAllTypes() {
		return(typeSet);
	}
	
	public boolean isSimpleType(String type) {
		if (type.startsWith("xs:")) {
			return (true);
		}
		return false;
	}

	public IType toType(String alias) {
		IType type = typeMap.get(alias);
		return (type);

	}

}
