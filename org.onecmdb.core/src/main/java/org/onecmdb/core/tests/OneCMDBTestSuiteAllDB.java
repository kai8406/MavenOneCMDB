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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.onecmdb.core.tests.core.TestOneCmdbCore;
import org.onecmdb.core.tests.core.TestQuery;
import org.onecmdb.core.tests.policy.TestPolicy;
import org.onecmdb.core.tests.wsdl.TestWebServiceHistorySerach;
import org.onecmdb.core.tests.wsdl.TestWebServiceQueryUpdate;
import org.onecmdb.core.tests.wsdl.TestWebServiceReleation;

public class OneCMDBTestSuiteAllDB  {
	
	
	public static Test suite() { 
        TestSuite suite = new TestSuite("OneCMDB Test Suite");
        // Will be closed when unit test exits!
        PrintStream ps = null;
	    
    
		try {
			ps = new PrintStream(new FileOutputStream("wsdl-performace-report.txt", true));			
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Problem setup performace report file: " + e.toString());			
		}
        
        // Add one entry for each test class 
        // or test suite.
        // Test All DB's
		{ // HSQL
			OneCMDBTestConfig config = new OneCMDBTestConfig();
	        config.setDataSourceProvider(OneCMDBTestConfig.HSQL_INPROC_CREATE_DROP_DATASOURCE);
	        config.setPerformanceReportTitle(OneCMDBTestConfig.HSQL_INPROC_CREATE_DROP_DATASOURCE);
	        config.setReportPrinter(ps);
	        suite.addTest(new OneCMDBTestSuite("OneCMDB HSQL Test", config));
		}
		/*
		{ // MYSQL
			OneCMDBTestConfig config = new OneCMDBTestConfig();
		    config.setDataSourceProvider(OneCMDBTestConfig.MYSQL_CREATE_DROP_DATASOURCE);
		    config.setPerformanceReportTitle(OneCMDBTestConfig.MYSQL_CREATE_DROP_DATASOURCE);
		    config.setReportPrinter(ps);
			suite.addTest(new OneCMDBTestSuite("OneCMDB MYSQL Test", config));
		}
    
		{ // Oracle
			OneCMDBTestConfig config = new OneCMDBTestConfig();
		    config.setDataSourceProvider(OneCMDBTestConfig.ORACLE_10_SERVER_CREATE_DROP_DATASOURCE);
		    config.setPerformanceReportTitle(OneCMDBTestConfig.ORACLE_10_SERVER_CREATE_DROP_DATASOURCE);
		    config.setReportPrinter(ps);
			suite.addTest(new OneCMDBTestSuite("OneCMDB Oracle Test", config));
		}
       
		{ // MS SQL
			OneCMDBTestConfig config = new OneCMDBTestConfig();
		    config.setDataSourceProvider(OneCMDBTestConfig.SQLSERVER2005_CREATE_DROP_DATASOURCE);
		    config.setPerformanceReportTitle(OneCMDBTestConfig.SQLSERVER2005_CREATE_DROP_DATASOURCE);
		    config.setReportPrinter(ps);
			suite.addTest(new OneCMDBTestSuite("OneCMDB MS SQLSERVER Test", config));
		}
		*/
        return suite; 
   }



}
