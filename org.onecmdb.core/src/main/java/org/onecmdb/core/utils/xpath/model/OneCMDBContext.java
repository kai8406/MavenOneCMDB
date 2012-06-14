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
package org.onecmdb.core.utils.xpath.model;

import java.util.Map;

import org.apache.commons.jxpath.JXPathIntrospector;
import org.onecmdb.core.ISession;

/**  
 * OneCMDB Bean Context, handled as JavaBean, not a dynamic bean. 
 * <br>
 * <br>Support /template, /instance or /primitive as path.
 *
 */
public class OneCMDBContext {
	private ISession session;
	
	private InstancesContext instances;
	private TemplatesContext templates;

	private InstancesContext instance;

	private Map<String, Object> context;
	
	public OneCMDBContext(Map<String, Object> context, ISession session) {
		this.session = session;
		this.context = context;
		// Register Dynamic Handlers.
		JXPathIntrospector.registerDynamicClass(
                IDynamicHandler.class,
                OneCMDBContextHandler.class);
		
		
	}
	/*
	public InstancesContext getInstances() {
		if (this.instances == null) {
			this.instances = new InstancesContext(this.context, this.session, true);
		}
		return(this.instances);
	}
	*/
	
	public InstancesContext getInstance() {
		if (this.instance == null) {
			this.instance = new InstancesContext(this.context, this.session, false);
		}
		return(this.instance);
	}
	
	/*
	public TemplatesContext getTemplates() {
		if (this.templates == null) {
			this.templates = new TemplatesContext(this.context, this.session, true);
		}
		return(this.templates);
	}
	*/
	public TemplatesContext getTemplate() {
		if (this.templates == null) {
			this.templates = new TemplatesContext(this.context, this.session, false);
		}
		return(this.templates);
	}
	
	public PrimitiveTypesContext getPrimitive() {
		return(new PrimitiveTypesContext(this.context, this.session));
	}
}
