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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.onecmdb.core.IType;

public class DurationType extends SimpleType {

	public String getUniqueName() {
		return ("xs:duration");
	}

    private final DurationType createValue() {
        DurationType value = null;
        Exception exception = null;
        try {
            Constructor<? extends DurationType> 
            cstr = getClass().getConstructor((Class[]) null);
            value = cstr.newInstance((Object[]) null);
        } catch (SecurityException e) {
            exception = e;
        } catch (NoSuchMethodException e) {
            exception = e;
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        if (exception != null) {
            throw new RuntimeException("Could not create new value", exception);
        }

        return value;
        
    }
    
    protected final SimpleType doParseString(String s) {
        try {
            DatatypeFactory f = DatatypeFactory.newInstance();
           Duration dur = ( s == null || s.equals("") )
                    ? f.newDuration(0) 
                    : f.newDuration(s);
            
            DurationType newValue = createValue();         
            newValue.setPayload(dur);
            return  newValue;

        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {
		return("A Duration Type, see XML-Schema#duration for full definition. Example of String representation extended format PnYnMnDTnHnMnS, where nY defines how many years and so on. ");
	}
    
    
    public final IType getValueType() {
        return this;
    }

    
    @Override
    public boolean isNullValue() {
        if (super.isNullValue()) 
            return true;
        
        Duration dur = (Duration) getPayload();
        
        if ( dur == null ) 
            return true;

        try {
            DatatypeFactory f = DatatypeFactory.newInstance();
            Duration empty = f.newDuration(0);
            return dur.equals(empty);
            
        } catch (DatatypeConfigurationException e) { 
            e.printStackTrace();
            throw new RuntimeException("Failed to create duration");
        }
    }
    
    public void setNullValue(boolean b) {
        if (b) {
            setPayload(null);
        }
    }

}
