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

import java.util.Collection;
import java.util.List;

import org.onecmdb.core.internal.authorization.RBACSession;
import org.onecmdb.core.internal.session.Session;


public interface IAuthorizationService extends IService {
	
	public void validateCreatePermission(ISession session, ICi ci);
	public void validateWritePermission(ISession session, ICi ci);
	public void validateDeletePermission(ISession session, ICi ci);

	public ICi getGroup(String group);
	
	public void setupAuthorization(ISession session);
	public boolean hasReadConstraints(ISession session);
	public List<Long> getReadConstraints(ISession session);
	public RBACSession setupRBAC(Session session, List<String> roleNames);
}
