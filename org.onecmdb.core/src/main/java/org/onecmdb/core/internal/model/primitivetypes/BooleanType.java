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

import org.onecmdb.core.IType;

public class BooleanType extends SimpleType {

	private final static BooleanType TRUE_VALUE = new BooleanType(true);

	private final static BooleanType FALSE_VALUE = new BooleanType(false);

	private BooleanType(boolean b) {
		setPayload(b);
	}

	public BooleanType() {

	}

	public String getUniqueName() {
		return ("xs:boolean");
	}

	protected SimpleType doParseString(String s) {
		if (s == null || s.equals("false") || s.equals(""))
			return FALSE_VALUE;
		if (s.equals("true"))
			return TRUE_VALUE;

		throw new IllegalArgumentException(
				"Boolean must be 'true' or 'false'; was '" + s + "'");

	}

	public IType getValueType() {
		return (this);
	}

	public String getDescription() {
		return("A boolean [true|false] type");
	}

}
