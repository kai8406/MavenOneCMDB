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
package org.onecmdb.core.utils.xpath.commands;

import org.acegisecurity.AuthenticationException;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;

/**
 * Auth Command implementation.
 *
 */
public class AuthCommand {
	private String user;
	private String pwd;
	private IOneCmdbContext context;
	
	
	public AuthCommand(IOneCmdbContext context) {
		this.context = context;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getToken() throws AuthenticationException, IllegalAccessException {
		ISession session = this.context.createSession();
		if (session == null) {
			throw new IllegalAccessException("Can't allocate a new session!");
		}
		
		// Set credentials.
		session.getAuthentication().setPassword(getPwd());
		session.getAuthentication().setUsername(getUser());
		
		// Login.
		session.login();
		
		// Might what something more clever here...
		String token = Integer.toHexString(session.hashCode());
		
		this.context.addSession(token, session);
		
		return(token);		
	}
}
