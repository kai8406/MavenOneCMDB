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
package org.onecmdb.core.tests;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.onecmdb.core.tests.core.TestOneCmdbCore;
import org.onecmdb.core.tests.wsdl.TestWSDLPerformance;
import org.onecmdb.core.tests.wsdl.TestWebServiceHistorySerach;
import org.onecmdb.core.tests.wsdl.TestWebServiceQueryUpdate;
import org.onecmdb.core.tests.wsdl.TestWebServiceReleation;



public class OneCMDBTestSuite extends TestSuite {

	public OneCMDBTestSuite() {   	
		this("OneCMDB Test Suite", new OneCMDBTestConfig());
	}
	
	public OneCMDBTestSuite(String name, OneCMDBTestConfig config) {
		super(name);
	    
		// Add one entry for each test class 
        // or test suite.
        addAllTests(TestOneCmdbCore.class, config);
        
        
        addAllTests(TestWebServiceQueryUpdate.class, config);
        addAllTests(TestWebServiceHistorySerach.class, config);
        addAllTests(TestWebServiceReleation.class, config);
        addAllTests(TestWSDLPerformance.class, config);
        
	}
	
	private void addAllTests(Class<? extends TestCase> testClass, OneCMDBTestConfig config) {
		
		for (Method m : testClass.getDeclaredMethods()) {
			if (m.getName().startsWith("test")) {
				TestCase test = null;
				Constructor constructor;
				try {
					constructor = testClass.getConstructor(new Class[] {OneCMDBTestConfig.class});
				
					test = (TestCase)constructor.newInstance(new Object[] {config});
					test.setName(m.getName());
				} catch (SecurityException e1) {
					throw new IllegalArgumentException("Security Issue constructor..." + e1 + "");
				} catch (NoSuchMethodException e1) {
					throw new IllegalArgumentException("No config constructor..." + e1 + "");
				} catch (InstantiationException e) {
					throw new IllegalArgumentException("Cannot instantiate test case: "+ m.getName() +" ("+e+")");
				} catch (InvocationTargetException e) {
					throw new IllegalArgumentException("Exception in constructor: "+m.getName()+" ("+ e +")");
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException("Cannot access test case: "+m.getName()+" ("+e+")");
				}
				if (test != null) {
					addTest(test);
				}
			}
		}
	}
	public static Test suite() { 
        TestSuite suite = new OneCMDBTestSuite();
        return suite; 
   }
}
