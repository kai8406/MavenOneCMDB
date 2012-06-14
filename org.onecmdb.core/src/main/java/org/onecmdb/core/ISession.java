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
package org.onecmdb.core;

import java.util.GregorianCalendar;
import java.util.Set;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UserDetails;
import org.onecmdb.core.internal.UsernamePassword;
import org.onecmdb.core.internal.authorization.RBACSession;

/**
 * The session is the entity <em>operating</em> in OneCMDB. In case the
 * session do not contain the correct <em>credentials</em> operations
 * may be rejected.
 */
public interface ISession {

	/**
	 * Fetch a service <em>module</em> identified by its class, or interface,
	 * identifier.
	 * 
	 * @param module
	 * @return an IService implementation, or NULL if none found with the given
	 * class name, interface name or identifier.
     * 
     * @throws AccessDeniedException in case the current user is not allowed 
     * to access the resource.
	 */
	IService getService(Class<? extends IService> moudule) throws AccessDeniedException;

	/**
	 * Create a new session from this session.
	 * 
	 */
	ISession newSession();
	
	/**
	 * Retrieve who is logged in with this session.
	 * 
	 * @return
	 */
	UserDetails getPrincipal();

    /** current roles */
    Set<GrantedAuthority> getRoles();
    
    public boolean isAnonymous();
    
    public Authentication getSubject();
    
    public void login() throws AuthenticationException;

    void logout();
    
    GregorianCalendar getDateCreated();
    
    /** used to populate an authorization request */
    public UsernamePassword getAuthentication();

	RBACSession getRBACSession();

}
