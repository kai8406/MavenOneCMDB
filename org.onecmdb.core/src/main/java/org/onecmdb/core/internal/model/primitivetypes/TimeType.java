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

import javax.xml.datatype.XMLGregorianCalendar;

public class TimeType extends DateAndTimeType {

	public String getUniqueName() {
		return ("xs:time");
	}

    
    public int getHour() {
        XMLGregorianCalendar cal = getCalendar();
        return cal.getHour();
    }
    public void setHour(int hour) {
        XMLGregorianCalendar cal = getCalendar();
        cal.setHour(hour); 
        
    }
    
    public int getMinute() {
        XMLGregorianCalendar cal = getCalendar();
        return cal != null ? cal.getMinute() : 0;
    }
    public void setMinute(int minute) {
        XMLGregorianCalendar cal = getCalendar();
        cal.setMinute(minute); 
    }
    
    public int getSecond() {
        XMLGregorianCalendar time = getCalendar();
        return time != null ? time.getSecond() : 0;
    }
    public void setSecond(int second) {
        XMLGregorianCalendar cal = getCalendar();
        cal.setSecond(second); 
    }

    public int getMillisecond() {
        XMLGregorianCalendar time = getCalendar();
        return time != null ? time.getMillisecond() : 0;
    }
    public void setMillisecond(int millisecond) {
        XMLGregorianCalendar cal = getCalendar();
        cal.setMillisecond(millisecond); 
    }
    
    public int getTimezone() {
        XMLGregorianCalendar time = getCalendar();
        return time != null  ? time.getTimezone() : 0;
    }

    /**
     * Set the number of minutes  
     * @param timezone
     */
    public void setTimezone(int timezone) {
        XMLGregorianCalendar cal = getCalendar();
        cal.setTimezone(timezone); 
    }

}
