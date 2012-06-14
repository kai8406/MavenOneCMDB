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

import java.net.URI;
import java.net.URISyntaxException;

import org.onecmdb.core.IType;
import org.onecmdb.core.IValue;

public class URIType extends SimpleType {

	public String getUniqueName() {
		return ("xs:anyURI");
	}

    protected SimpleType doParseString(String s) {
        if (s == null || "".equals(s)) {
            // the null value 
            URIType nType = new URIType();
            nType.setPayload(null);
            return (nType);
        }
        
        try {
			URI nuri = new URI(s);
			URIType nType = new URIType();
			nType.setPayload(nuri);
			return (nType);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Not a correct URI:" + s, e);
		}

	}

	public IType getValueType() {
		return (this);
	
		
    }
    
    
    public IValue getNullValue() {
        return parseString(null);
    }

    public String getDescription() {
		return("A URI/URL Type");
	}
    
    
   

}
