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
package org.onecmdb.core.internal.authentication;

import java.util.ArrayList;
import java.util.List;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.internal.model.QueryResult;
import org.springframework.dao.DataAccessException;

public class OneCMDBAuthenticationDao implements UserDetailsService {
	
	
	private String userTemplateAlias = "CMDBAccount";
	private String userNameAlias = "username";
	private ISession session;

	public void setSession(ISession session) {
		this.session = session;
	}
	
	public ISession getSession() {
		return session;
	}

	public String getUserNameAlias() {
		return userNameAlias;
	}

	public void setUserNameAlias(String userNameAlias) {
		this.userNameAlias = userNameAlias;
	}

	public String getUserTemplateAlias() {
		return userTemplateAlias;
	}

	public void setUserTemplateAlias(String userTemplate) {
		this.userTemplateAlias = userTemplate;
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		ISession session = getSession();
		IModelService msrvc = (IModelService) session.getService(IModelService.class);
		IModelService mService = (IModelService)session.getService(IModelService.class);
		
		QueryCriteria<ICi> criteria = new QueryCriteria<ICi>();
		criteria.setOffspringOfAlias(this.userTemplateAlias);
		criteria.setMatchCiInstances(true);
		criteria.setMatchAttribute(true);
		criteria.setMatchAttributeAlias(userNameAlias);
		criteria.setText(username);
		criteria.setTextMatchValue(true);
		QueryResult<ICi> result = mService.query(criteria);
		if (result.size() == 0) {
			// Username not found.
			 throw new UsernameNotFoundException("Could not find user: " + username);
		}
		if (result.size() > 1) {
			// More than one username exists!
		    throw new UsernameNotFoundException("Found more then one (" + result.size() + ") user with name : " + username);
		}
		
		ICi account = result.get(0);
		String userName = getSingleStringValue(account, userNameAlias);
		String password = getSingleStringValue(account, "password");
		Boolean enabled = getSingleBooleanValue(account, "enabled");
		Boolean accountExpired = getSingleBooleanValue(account, "accountExpired");
		Boolean credentialsExpired = getSingleBooleanValue(account, "credentialsExpired");
		Boolean accountLocked = getSingleBooleanValue(account, "accountLocked");
		
		String defaultRole = getSingleStringValue(account, "defaultRole");
		
		List<IAttribute> roles = account.getAttributesWithAlias("role");
		List<GrantedAuthority> granted = new ArrayList<GrantedAuthority>();
		for (IAttribute role : roles) {
			IValue value = role.getValue();
			if (value != null) {
				String roleName = value.getAsString();
				if (roleName != null || roleName.length() > 0) {
					if (roleName.equalsIgnoreCase(defaultRole)) {
						granted.add(0, new GrantedAuthorityImpl(roleName));
					} else {
						granted.add(new GrantedAuthorityImpl(roleName));
					}
				}
			}
		}
		OneCMDBUser user = new OneCMDBUser(userName, 
						password, 
						enabled, 
						!accountExpired,
						!credentialsExpired, 
						!accountLocked, 
						granted.toArray(new GrantedAuthority[0]));
		
		user.setAccount(account);
		return(user);
	}

	private Boolean getSingleBooleanValue(ICi userCI, String alias) {
		IValue iValue = getSingleValue(userCI, alias);
		
		if (iValue == null) {
			return(Boolean.FALSE);
		}
		
		Object value = iValue.getAsJavaObject();
		if (value instanceof Boolean) {
			return((Boolean)value);
		}
		return(Boolean.FALSE);
	}


	private String getSingleStringValue(ICi userCI, String alias) {
		IValue iValue = getSingleValue(userCI, alias);
		
		if (iValue == null) {
			return(null);
		}
		
		Object value = iValue.getAsJavaObject();
		if (value instanceof String) {
			return((String)value);
		}
		return(null);
	}
	
	private IValue getSingleValue(ICi userCI, String alias) {
		List<IAttribute> list = userCI.getAttributesWithAlias(alias);
		if (list.size() == 1) {
			IAttribute a = list.get(0);
			IValue value = a.getValue();
			return(value);
		}
		return(null);
	}

}
