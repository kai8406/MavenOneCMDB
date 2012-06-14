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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.onecmdb.core.utils.xpath.OneCMDBFactory;
import org.onecmdb.core.utils.xpath.model.OneCMDBContext;

/**
 * Abstract class for all Path Commands
 * Common class to create JXPath Contexts.
 *
 */
public class AbstractPathCommand {
	// Arguments to AbstractPathCommand.
	// Subclasses might need more arguments.
	private String path;
	private String auth;
	
	private IOneCmdbContext oneCmdbContext;
	private JXPathContext xPathContext;

	protected Map<String, Object> context = new HashMap<String, Object>();

	protected Log log = LogFactory.getLog(this.getClass());
	
	public AbstractPathCommand(IOneCmdbContext context) {
		this.oneCmdbContext = context;
	}
	
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getPath() {
		return path;
	}
	
	public Map<String, Object> getDataContext() {
		return(this.context);
	}
	
	public void setPath(String path) {
		this.path = path;
		if (this.path.startsWith("'")) {
			this.path = path.substring(1);
		}
		if (this.path.endsWith("'")) {
			this.path = this.path.substring(0, this.path.length()-1);
		}
	}
	
	public ISession getCurrentSession() {
		if (this.auth == null) {
			return(null);
		}
		ISession session = oneCmdbContext.getSession(this.auth);
		getDataContext().put("session", session);
		return(session);
	}
	
	public JXPathContext getXPathContext() {
		
		if (this.xPathContext == null) {
			log.debug("Create new OneCMDbContext");
			ISession session = this.oneCmdbContext.getSession(getAuth());
			if (session == null) {
				throw new SecurityException("No Session found! Try to do auth() first!");
			}
			/*
			IModelService mSvc = (IModelService)session.getService(IModelService.class);
			this.xPathContext = JXPathContext.newContext(new OneCMDBContext(new OneCMDBContextBeanProvider(mSvc)));
			*/
			this.context.put("session", session);
			this.xPathContext = JXPathContext.newContext(new OneCMDBContext(this.context, session));
			this.xPathContext.setFactory(new OneCMDBFactory());
		}
		return(this.xPathContext);
	}
	
	public Iterator<Pointer> getPathPointers() {
		JXPathContext context = getXPathContext();
		return((Iterator<Pointer>)context.iteratePointers(getPath()));
	}
	
	public Iterator<Pointer> getRelativePointers(Pointer p, String outputAttribute) {		
		JXPathContext relContext = getXPathContext().getRelativeContext(p);
		return((Iterator<Pointer>)relContext.iteratePointers(outputAttribute));
	}
	
	public JXPathContext getRelativeContext(Pointer p) {		
		JXPathContext relContext = getXPathContext().getRelativeContext(p);
		return(relContext);
	}
	
}
