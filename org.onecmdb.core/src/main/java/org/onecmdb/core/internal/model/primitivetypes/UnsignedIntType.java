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


public class UnsignedIntType extends NonNegativeInteger {
	private static long MAX_UINT = (long) Math.pow(2, 32);

	@Override
	public String getUniqueName() {
		return ("xs:nonNegativeInteger");
	}

	@Override
    protected SimpleType doParseString(String s) {
        if (s == null || "".equals(s)) {
			return (null);
		}

		NonNegativeInteger v = (NonNegativeInteger) super.parseString(s);
		// Restricting.
		if (v.getLong() > MAX_UINT) {
			throw new IllegalArgumentException(getUniqueName()
					+ " must not be larger than " + MAX_UINT);
		}
		return (v);
	}

}
