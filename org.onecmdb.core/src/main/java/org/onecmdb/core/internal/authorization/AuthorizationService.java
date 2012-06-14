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
package org.onecmdb.core.internal.authorization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.IAuthorizationService;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.SchemaService;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.internal.session.Session;
import org.onecmdb.core.utils.ClassInjector;
import org.onecmdb.core.utils.graph.expression.ItemExpression;

public class AuthorizationService extends SchemaService implements IAuthorizationService {
	private String rootGroup;
	
	private boolean initilized = false;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public String getRootGroup() {
		return rootGroup;
	}

	public void setRootGroup(String rootGroup) {
		this.rootGroup = rootGroup;
	}

	public ICi getGroup(String group) {
		ItemExpression selector = new ItemExpression();
		selector.setAlias(group);
		
		return(null);
	}

	public void validateCreatePermission(ISession session, ICi ci) {
		if (!initilized) {
			return;
		}
	}

	public void validateDeletePermission(ISession session, ICi ci) {
		if (!initilized) {
			return;
		}
	}

	public void validateWritePermission(ISession session, ICi ci) {
		if (!initilized) {
			return;
		}
	}

	public void close() {
		// TODO Auto-generated method stub
	}

	public void init() {
		// TODO Auto-generated method stub
		super.setupSchema();
	
		// Update all objects to belong to the root group...
		
	}
	
	/**
	 * Get all group id's where the session has read priviliges in.
	 */
	public List<Long> getReadConstraints(ISession session) {
		return null;
	}

	/**
	 * If session has access to all groups then there is
	 * no constraints for the read operation...
	 */
	public boolean hasReadConstraints(ISession session) {
		return false;
	}

	public void setupAuthorization(ISession session) {
	}

	public RBACSession setupRBAC(Session session, List<String> roleNames) {
    	RBACSession rbac = new RBACSession();
    	List<Role> roles = new ArrayList<Role>();
    	
    	// Map this role to Role-->Permisions-->SecurityGroup
    	for (String roleName : roleNames) {
    		IModelService service = (IModelService) session.getService(IModelService.class);
    		QueryCriteria crit = new QueryCriteria();
    		crit.setOffspringOfAlias("CMDBRole");
    		crit.setText(roleName);
    		crit.setTextMatchValue(true);
    		crit.setMatchAttributeAlias("name");
    		QueryResult result = service.query(crit);
    		if (result.size() == 0) {
    			log.info("<" + session.getUsername() + "> role name <" + roleName + "> is not found!");
    		} else if (result.size() > 1) {
    			log.warn("<" + session.getUsername() + "> role name <" + roleName + "> is found more than once [" + result.size() + "]");
    		} else {
    			ICi role = (ICi) result.get(0);
    			roles.add(populateRBAC(roleName, role));
    		}
    	}
    	rbac.setRoles(roles);
    	return(rbac);
	}
	/**
	 * Populate OneCMDb RBAC objects.
	 * 
	 * Role -> Permission[] --> SecurityGroup
	 * 
	 * @param role
	 */
	private Role populateRBAC(String name, ICi role) {
		Role r = new Role();
		r.setName(name);

		if (role == null) {
			return(r);		
		}
		ClassInjector converter = new ClassInjector();
		converter.addAliasToClass("CMDBRole", Role.class.getName());
		converter.addAliasToClass("CMDBSecurityGroupPermission", GroupPermission.class.getName());
		converter.addAliasToClass("CMDBSecurityGroup", SecurityGroup.class.getName());

		r = (Role) converter.toBeanObject(role);

		return(r);
		/*
			// Fetch permissions.
	    	List<GroupPermission> permList = new ArrayList<GroupPermission>();

			List<IAttribute> permissions = role.getAttributesWithAlias("permission");
			for (IAttribute perm : permissions) {
				IValue permission = perm.getValue();
				if ((permission != null) && (permission instanceof ICi)) {
					ICi permCI = (ICi)permission;
					GroupPermission gp = new GroupPermission();
					// Find security groups.
					List<IAttribute> sGroups = ((ICi)permission).getAttributesWithAlias("group");
					for (IAttribute groupAttr : sGroups) {
						IValue group = groupAttr.getValue();
						// Need to follow the heiraricy of servers.

					}
				}
			}
		 */

	}
}
