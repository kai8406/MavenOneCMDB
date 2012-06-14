/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.ui.gwt.toolkit.client.control;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

public class OneCMDBUtils {
	public static String PASSWORD_TYPE = "xs:password";
	public static String STRING_TYPE = "xs:string";
	public static String DATE_TYPE = "xs:date";
	public static String DATETIME_TYPE = "xs:dateTime";
	public static String BOOLEAN_TYPE = "xs:boolean";
	public static String ANYURI_TYPE = "xs:anyURI";
	public static String INTEGER_TYPE = "xs:integer";
	
	protected static String[] simpleTypes = {
			STRING_TYPE,
			PASSWORD_TYPE,
			DATE_TYPE,
			DATETIME_TYPE,
			BOOLEAN_TYPE,
			ANYURI_TYPE,
			INTEGER_TYPE
			
	};
	
	public static String[] getSimpleTypes() {
		return(simpleTypes);
	}

	public static List getSimpleTypesAsList() {
		return(Arrays.asList(getSimpleTypes()));
	}
	
	
	public static String getIconForCI(String alias) {
		String name = "";
		if (alias != null) {
			name = alias;
		}
		return(OneCMDBSession.getOneCMDBURL() + "/icons/generate?iconid=" + name);
	}

	public static String getXMLDateString() {
		Date date = new Date();
		// YYYY-MM-DDThh:mm:ss
		
		// Must be a simpler way to do this....
		String xmlDateFormat = "" + (date.getYear() + 1900);
		int month = (date.getMonth() + 1);
		String monthStr = fillZero(month);
		String dateStr = fillZero(date.getDate());
		xmlDateFormat = xmlDateFormat + "-" + monthStr;
		xmlDateFormat = xmlDateFormat + "-" + dateStr;
		xmlDateFormat = xmlDateFormat + "T" + fillZero(date.getHours());
		xmlDateFormat = xmlDateFormat + ":" + fillZero(date.getMinutes());
		xmlDateFormat = xmlDateFormat + ":" + fillZero(date.getSeconds());
	
		return(xmlDateFormat);
	}
	
	protected static String fillZero(int v) {
		if (v < 10) {
			return("0" + v);
		}
		return("" + v);
	}
	
	public static String getIconForCI(GWT_CiBean ci) {
		if (ci == null) {
			return("images/ci/icon-notset.gif");
		}
		GWT_ValueBean vBean = ci.fetchAttributeValueBean("icon", 0);
		if (vBean == null) {
			return("images/ci/icon-notset.gif");
		}
		return(getIconForCI(vBean.getValue()));
	}

	//	redirect the browser to the given url
	public static native void redirect(String url)/*-{
	      $wnd.location = url;
	}-*/;

}
	
