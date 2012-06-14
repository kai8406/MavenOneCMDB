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

/**
 * The notion of an the OneCmdb application. A client's first task is get hold
 * to a reference of a IOneCmdbContext, to be able to perform tasks.
 * 
 */
public interface IOneCmdbContext {

	/**
	 * Creates a new session to operate with the application. Implementations
	 * may use a cash mechanism, and a time to live (TTL), to be able to return
	 * the same session a number of times as long as we pass the correct
	 * <em>hash</em>.
	 * 
	 * @return A session from which actual services can be obtained
	 * 
	 * @see ISession
	 * 
	 */
	ISession createSession();

	/**
	 * Fetch a <em>reference</em> to a specific resource, for example, a
	 * service, which is controlled by this OneCMDB application.
	 * 
	 * @param session
	 *            A valid session, which is used to decide weather the specified
	 *            resource is allowed to be retrieved.
	 * @param type The service to be retrieved, which should be the Java
     * interface defining the service. 
	 * @return The <em>service</em> asked for.
	 */
	IService getService(ISession session, Class<? extends IService> type);

	/**
	 * Used to clean up and <em>close</em> the application. Further calls to
	 * this OneCMDB instance should render an <code>IllegalStateEception</code>.
	 */
	void close();
	
	/**
	 * Token to session handler. Connect a token, (object) some identifier, to
	 * an existing session. 
	 * 
	 * 
	 * @param token decided by the client.
	 * @param session retrived from createSession().
	 */
	void addSession(Object token, ISession session);
	
	/**
	 * Retrive a session from a token object.
	 * 
	 * @param token
	 * @return
	 */
	ISession getSession(Object token);
	
	/**
	 * Close and remove a session that is connected to the token.
	 * @param token
	 */
	void removeSession(Object token);

}
