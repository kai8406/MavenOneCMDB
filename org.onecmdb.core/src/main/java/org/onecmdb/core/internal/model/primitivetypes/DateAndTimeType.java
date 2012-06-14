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
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.onecmdb.core.IType;

/** 
 * Base class for all simple types based on time. The value hold is backed by
 * a {@link javax.xml.datatype.XMLGregorianCalendar}. 
 * 
 * @see javax.xml.datatype.XMLGregorianCalendar
 *
 */
public abstract class DateAndTimeType extends SimpleType {

	public abstract String getUniqueName();
    
    private final DateAndTimeType createValue() {
        DateAndTimeType value = null;
        Exception exception = null;
        try {
            Constructor<? extends DateAndTimeType> 
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
    
    final private static GregorianCalendar EPOCH;
    static {
        EPOCH = new GregorianCalendar();
        EPOCH.setTime(new Date(0));
    }
    protected final SimpleType doParseString(String s) {
		try {
            DatatypeFactory f = DatatypeFactory.newInstance();
			XMLGregorianCalendar time = ( s == null || s.equals("") )
                    ? f.newXMLGregorianCalendar() 
                    : f.newXMLGregorianCalendar(s);
            
            DateAndTimeType newValue = createValue();         
            newValue.setPayload(time);
            
            return  newValue;

		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

    protected final XMLGregorianCalendar getCalendar() {
        XMLGregorianCalendar cal = (XMLGregorianCalendar) getPayload();
        if (cal == null) {
            try {
                DatatypeFactory f = DatatypeFactory.newInstance();
                cal = f.newXMLGregorianCalendar();
            } catch (DatatypeConfigurationException e) { e.printStackTrace();
            throw new RuntimeException("Failed to create calendar");
            }
        }
        return cal;
        
        
    }
    
    public String getDescription() {
		return("A DateTime type. Example of String format yyyy-mm-ddThh:mm:ss. See XML-Schema for more details.");
	}
    
    
	public final IType getValueType() {
		return this;
	}

    
    @Override
    public boolean isNullValue() {
        if (super.isNullValue()) 
            return true;
        
        XMLGregorianCalendar cal = getCalendar();
        if ( cal == null 
             || !cal.isValid() )
            return true;

        try {
            DatatypeFactory f = DatatypeFactory.newInstance();
            XMLGregorianCalendar empty = f.newXMLGregorianCalendar();
            return cal.equals(empty);
            
        } catch (DatatypeConfigurationException e) { 
            e.printStackTrace();
            throw new RuntimeException("Failed to create calendar");
        }
    }
    
    public void setNullValue(boolean b) {
        if (b) {
            setPayload(null);
        }
    }


}
