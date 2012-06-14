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
package org.onecmdb.core.tests;

import java.io.PrintStream;

public class OneCMDBTestConfig {
	/**
	 * Example Datasources:
	 * Using localhost and default port for specific db.
	 */
	public static String HSQL_INPROC_UPDATE_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/hsql-inproc-update-datasource.xml";
	public static String HSQL_INPROC_CREATE_DROP_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/hsql-inproc-create-drop-datasource.xml";

	public static String HSQL_SERVER_UPDATE_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/hsql-server-update-datasource.xml";
	public static String HSQL_SERVER_CREATE_DROP_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/hsql-server-create-drop-datasource.xml";
	
	public static String MYSQL_CREATE_DROP_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/mysql-create-datasource.xml";
	public static String MYSQL_UPDATE_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/mysql-update-datasource.xml";
	
	public static String DB2_CREATE_DROP_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/db2-create-drop-datasource.xml";
	public static String DB2_UPDATE_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/db2-update-datasource.xml";

	public static final String ORACLE_10_SERVER_CREATE_DROP_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/oracle10-create-datasource.xml";
	public static final String SQLSERVER2005_CREATE_DROP_DATASOURCE = "org/onecmdb/core/tests/resources/datasource/sqlserver-create-datasource.xml";
	
	public static String EMPTY_PROVIDER = "org/onecmdb/core/tests/resources/providers/empty-provider.xml";

	// Default data Source
	private String dataSourceResource = HSQL_SERVER_CREATE_DROP_DATASOURCE;
	private String performanceTitle = HSQL_SERVER_CREATE_DROP_DATASOURCE;
	private PrintStream reportPrintStream = System.out;
	private String modelProvider = EMPTY_PROVIDER;
	
	public String getModelProvider() {
		return(this.modelProvider);
	}
	
	public void setModelProvider(String p) {
		this.modelProvider = p;
	}
	
	public String getDataSourceProvider() {
		return(dataSourceResource);
	}
	
	public void setDataSourceProvider(String source) {
		dataSourceResource = source;
	}
	
	public String getPerformanceReportTitle() {
		return(performanceTitle);
	}
	public void setPerformanceReportTitle(String t) {
		performanceTitle = t;
	}

	public PrintStream getReportPrinter() {
		return(reportPrintStream );
	}
	
	public void setReportPrinter(PrintStream ps) {
		reportPrintStream = ps;
	}

}
