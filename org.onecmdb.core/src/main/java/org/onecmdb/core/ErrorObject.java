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


public class ErrorObject {

	private String code;

	private String defaultMessage;

	/**
	 * A new error object
	 * 
	 * @param code
	 *            An error code, suitable for lookup via message services
	 * @param message
	 *            A default message
	 */
	public ErrorObject(String code, String defaultMessage) {
		super();
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	/** An error code, suitable for message bundle lookup */
	public String getCode() {
		return this.code;
	}

	/**
	 * The <em>default</em> error message for this error object.
	 * 
	 * @return A textual string representation of the default message
	 */
	public String getDefaultMessage() {
		return this.defaultMessage;
	}

}
