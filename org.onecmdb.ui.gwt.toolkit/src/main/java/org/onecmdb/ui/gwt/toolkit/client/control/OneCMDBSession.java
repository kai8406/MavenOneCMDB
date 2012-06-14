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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeChangeEvent;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeChangeListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;







/**
 * <code>OneCMDBSession is used to handle static 
 * variables, like auth token.
 * 
 */
public class OneCMDBSession {
	private static String oneCMDBServerHost = null;
	
	static HashMap variables = new HashMap();
	private static List listeners = new ArrayList(); 
	
	public static void setAttribute(String name, Object o) {
		Object old = variables.get(name);
		
		variables.put(name, o);
		
		fireEvent(new AttributeChangeEvent(name, old, o));
	}
	
	public static Object getAttribute(String name) {
		return(variables.get(name));
	}

	public static boolean hasAttribute(String name) {
		return(variables.containsKey(name));
	}
	
	public static String getAuthToken() {
		// Validate token...
		
		String token = (String)getAttribute("auth_token");
		if (token == null) {
			token = getCookie("auth_token");
		}
		
		return(token);
	}
	public static void setAuthToken(String token, boolean storeAsCookie) {
		setAttribute("auth_token", token);
		if (storeAsCookie) {
			setCookie("auth_token", token, 1);
		}
	}
	public static boolean isAuthenticated() {
		if (hasAttribute("auth_token")) {
			return(true);
		}
		
		String token = getCookie("auth_token");
		if (token != null) {
			return(true);
		}
		
		return(false);

	}

	public static void setOneCMDBURL(String url) {
		oneCMDBServerHost = url;
	}
	
	public static String getOneCMDBURL() {
		if (oneCMDBServerHost != null) {
			return(oneCMDBServerHost);
		}
	
		if (!GWT.isScript()) {
			return("http://localhost:8080");
		}
		return("..");
		
	}

	public static void addCallStat(String name, int objects, long start, long stopCall, long stopUI) {
		String key = "PERFORMANCE." + name;
		// For now just add a string entry...
		String value = name + "[" + objects +"]rpcCall=" + (stopCall-start) +"ms, uiUpdate=" + (stopUI-stopCall) + "ms";
		setAttribute(key, value);
	}
	
	public static void addAttributeChangeListener(IAttributeChangeListener listener) {
		listeners.add(listener);
	}
	
	private static void fireEvent(AttributeChangeEvent event) {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			IAttributeChangeListener listener = (IAttributeChangeListener)iter.next();
			listener.onChangeEvent(event);
		}
	}

	public static void addWarning(String string) {
		// TODO Auto-generated method stub
		
	}

	/*
	public static void setUser(User user) {
		variables.put("user", user);
	}
	
	public static User getUser() {
		Object user = variables.get("user");
		if (user instanceof User) {
			return((User)user);
		}
		return(null);
	}
	*/
	
	/**
     * Helper function to save Cookie
     * 
     * @param cookieName
     *            name of the cookie
     * @param value -
     *            value to be saved in the cookie
     * @param days -
     *            number of days this cookie should be kept alive
     */
    public static void setCookie(String cookieName, String value, int days) {

        Date date = new Date();
        long dateLong = date.getTime();
        dateLong += (1000 * 60 * 60 * 24 * days);// convert days to ms
        date.setTime(dateLong); // Set the new date

        Cookies.setCookie(cookieName, value, date);
    }

    /**
     * Helper function to save Cookie. The cookie will be saved for 30 days by
     * default
     * 
     * @param cookieName
     *            name of the cookie
     * @param value -
     *            value to be saved in the cookie
     */
    public static void setCookie(String cookieName, String value) {
        setCookie(cookieName, value, 30);
    }

    /**
     * Helper function to get the value from the cookie
     * 
     * @param cookieName
     *            Name of the cookie
     * @return value of the cookie
     */
    public static String getCookie(String cookieName) {
        return Cookies.getCookie(cookieName);
    }
}
