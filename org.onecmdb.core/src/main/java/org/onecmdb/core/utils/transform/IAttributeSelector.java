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
package org.onecmdb.core.utils.transform;

import java.io.IOException;

/**
 * Interface <code>IAttributeSelector</code> selects an attribute from a IInstance object.<br/> 
 * The IInstance object is a <em>row</em> of instance data.<br/> 
 * the IAttributeSelector convert one or many <em>columns</em> to an IAttributeValue.<br/>
 * <br/>
 * The natural key specifies of this attribute is to be used as a natural key for the instance<br>
 * Each IInstance can contain zero natural key's, the each IInstance row is regarded as<br/>
 * a new instance. The combination of natural keys for a IInstance must be unique for the dataset.</br>
 */
public interface IAttributeSelector {
	public String getName();	
	public boolean isNaturalKey();
	public IAttributeValue getAttribute(IInstance row) throws IOException;
}
