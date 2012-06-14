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

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public class DateTimeType extends DateAndTimeType {

	public String getUniqueName() {
		return ("xs:dateTime");
	}

    /**
     * @see XMLGregorianCalendar#getYear()()
     */
    public int getYear() {
        XMLGregorianCalendar cal = getCalendar();
        return cal.getYear();
    }
    public void setYear(int year) {
        XMLGregorianCalendar cal = getCalendar();
        cal.setYear(year);
    }
    
    /** 
     * @see XMLGregorianCalendar#getMonth()()
     */
    public int getMonth() {
        XMLGregorianCalendar cal = getCalendar();
        return cal.getMonth();
    }
    public void setMonth(int month) {
        XMLGregorianCalendar cal = getCalendar();
        cal.setMonth(month);
    }

    /**
     * @see XMLGregorianCalendar#getDay()
     */
    public int getDay() {
        XMLGregorianCalendar cal = getCalendar();
        return cal.getDay(); 
    }
    public void setDay(int day) {
        XMLGregorianCalendar cal = getCalendar();
        cal.setDay(day); 
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

	public static String parseDate(Date d) {
		
		DateTimeType t = new DateTimeType();
		XMLGregorianCalendar xmlCal = t.getCalendar();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		xmlCal.setYear(c.get(Calendar.YEAR));
		xmlCal.setMonth(c.get(Calendar.MONTH)+1);
		xmlCal.setDay(c.get(Calendar.DAY_OF_MONTH));
		xmlCal.setHour(c.get(Calendar.HOUR_OF_DAY));
		xmlCal.setMinute(c.get(Calendar.MINUTE));
		xmlCal.setSecond(c.get(Calendar.SECOND));
		xmlCal.setMillisecond(c.get(Calendar.MILLISECOND));
		String xmlDateTime = xmlCal.toXMLFormat();
		return(xmlDateTime);
	}
    

}
