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
package org.onecmdb.utils.wsdl;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;

public abstract class AbstractCMDBCommand {
	private String url;
	private String username;
	private String pwd;
	private String token;
	private IOneCMDBWebService service;
	
	protected static String DEF_ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"username", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"token", "Used instead of username/pwd", null}
	};
	
	public void handleArgs(String ARGS[][], String argv[]) {
		String allARGS[][] = new String[DEF_ARGS.length+ARGS.length][3];
		int i = 0;
		for (String[] arg : DEF_ARGS) {
			allARGS[i] = arg;
			i++;
		}
		for (String[] arg : ARGS) {
			allARGS[i] = arg;
			i++;
		}
		
		SimpleArg args = new SimpleArg(allARGS);
		for (String arg[] : allARGS) {
			String key = arg[0];
			String value = args.getArg(key, argv);
			if (value == null) {
				continue;
			}
			setArg(key, value);
		}
	}
	
	protected void setArg(String key, Object value) {
		// Invoke setter.
		Character c = key.charAt(0);
		String setMethod = "set" + Character.toUpperCase(c) + key.substring(1);
		
		Method m = null;
		Method methods[] = this.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(setMethod)) {
				m = method;
				break;
			}
		}
		if (m == null) {
			System.err.println("Method " + setMethod + " is missing.");
			return;
		}
		try {
			m = this.getClass().getMethod(setMethod, new Class[] {value.getClass()});
			m.invoke(this, new Object[] {value});
		} catch (Exception e) {
			System.err.println("Can't set object '" + value.getClass() + "' on method " + setMethod +" in class '" + this.getClass().getName() + "'");
		}

	}
	
	public abstract void process() throws Exception;
	
	public void handleProperties(Properties p) {
		for (Object key : p.keySet()) {
			setArg(key.toString(), p.get(key));
		}
	}
	
	public static void start(AbstractCMDBCommand cmd, String[][] ARGS_DEF, String[] argv) {
		long start = System.currentTimeMillis();
		cmd.handleArgs(ARGS_DEF, argv);
		try {
			cmd.process();
		} catch (Throwable t) {
			t.printStackTrace();
			long stop = System.currentTimeMillis();
			System.out.println("Runtime " + (stop-start) + "ms");	
			System.exit(-1);
		}
		long stop = System.currentTimeMillis();
		System.out.println("Runtime " + (stop-start) + "ms");	
		System.exit(0);
	}
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String service) {
		this.url = service;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	

	protected IOneCMDBWebService getService() throws Exception {
		if (this.service == null) {
			this.service = OneCMDBServiceFactory.getWebService(getUrl());
			if (this.token == null) {
				this.token = this.service.auth(getUsername(), getPwd());
			}
		}
		return(service);
	}
	
	public void setService(IOneCMDBWebService service) {
		this.service = service;
	}

}
