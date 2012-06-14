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
package org.onecmdb.core.tests.wsdl;

import junit.framework.Assert;

import org.onecmdb.core.tests.AbstractOneCmdbTestCase;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBWebServiceImpl;

public abstract class AWebServiceTestCase extends AbstractOneCmdbTestCase {
	protected IOneCMDBWebService cmdbService = null;
	protected String token;
	
	public void setUp() {
		super.setUp();
		
		// Create IWebService interface.
		// Directly without going through the XFire!
		OneCMDBWebServiceImpl impl = new OneCMDBWebServiceImpl();
		impl.setOneCmdb(getCmdbContext());
		cmdbService = impl;
		
		// Use remote host.
		
		
		// Correct login.
		try {
			token = cmdbService.auth("admin", "123");
			Assert.assertNotNull(token);
		} catch (Exception e) {
			fail("Login-failed" + e);
		}
	}
}
