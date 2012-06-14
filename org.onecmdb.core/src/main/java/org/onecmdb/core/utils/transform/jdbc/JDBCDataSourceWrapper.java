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
package org.onecmdb.core.utils.transform.jdbc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.onecmdb.core.utils.transform.IDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class JDBCDataSourceWrapper implements IDataSource  {

	private DataSource dataSource;
	private String query;
	private String rootPath;
	private String driverLib;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void reset() throws IOException {
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void setQuery(String q) {
		this.query = q;
	}

	public String getQuery() {
		return query;
	}

	public void setRootPath(String path) {
		this.rootPath = path;
	}

	public void setDriverLib(String lib) {
		this.driverLib = lib;
	}
	
	private URL[] getDriverLibURL() throws MalformedURLException {
		if (driverLib == null) {
			return(new URL[0]);
		}
		URL u = null;
		try {
			 u = new URL(driverLib);
		} catch (Throwable t) {
			u = new URL("file:" + driverLib);
		}
		if (this.rootPath != null) {
			u = new URL(u.getProtocol(), u.getHost(), u.getPort(), this.rootPath + "/" + u.getPath());
		}
		URL urls[] = new URL[1];
		urls[0] = u;
		return(urls);
	}

		
	public void setupDataSource(Properties p) throws IOException {
		// Load this under the provieded DriverLib class path.
		URLClassLoader loader = new URLClassLoader(getDriverLibURL(), this.getClass().getClassLoader());
		Thread.currentThread().setContextClassLoader(loader);
		
		try {
			Class driver = loader.loadClass(p.getProperty("jdbc.driverClass"));
			try {
				Object instance = driver.newInstance();
				System.out.println("Instance...." + instance);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		Class cl = null;
		String clazz = "org.apache.commons.dbcp.BasicDataSource";
		
		try {
			cl = loader.loadClass(clazz);
		} catch (ClassNotFoundException e) {
			throw new IOException("Can't load class '" + clazz + "', using driver lib '" + driverLib + "'");
		}
		BasicDataSource jdbcSrc;
		try {
			jdbcSrc = (BasicDataSource) cl.newInstance();
		} catch (Exception e) {
			throw new IOException("Can't instanciate class '" + clazz + "', using driver lib '" + driverLib + "'");
		}
		*/
		
		ClassLoaderBasicDataSource jdbcSrc = new ClassLoaderBasicDataSource();
		jdbcSrc.setDriverClassLoader(loader);
		jdbcSrc.setUrl(p.getProperty("db.url"));
		jdbcSrc.setUrl(p.getProperty("jdbc.url"));
		jdbcSrc.setDriverClassName(p.getProperty("db.driverClass"));
		jdbcSrc.setDriverClassName(p.getProperty("jdbc.driverClass"));
		
		jdbcSrc.setUsername(p.getProperty("db.user"));
		jdbcSrc.setUsername(p.getProperty("jdbc.user"));
		
		jdbcSrc.setPassword(p.getProperty("db.password"));
		jdbcSrc.setPassword(p.getProperty("jdbc.password"));
		/*
		String clazz2 = "org.onecmdb.core.utils.transform.jdbc.ClassLoaderBasicDataSource";
		try {
			cl = loader.loadClass(clazz2);
		} catch (ClassNotFoundException e) {
			throw new IOException("Can't load class '" + clazz + "', using driver lib '" + driverLib + "'");
		}
		
		ClassLoaderBasicDataSource jdbcSrc1;
		try {
			jdbcSrc1 = (ClassLoaderBasicDataSource) cl.newInstance();
			jdbcSrc1.setDs(jdbcSrc);
		} catch (Exception e) {
			throw new IOException("Can't instanciate class '" + clazz + "', using driver lib '" + driverLib + "'");
		}
		*/
		setDataSource(jdbcSrc);
	}

	public JdbcTemplate loadJDBCTemplate() throws IOException {
		/*
		URLClassLoader loader = new URLClassLoader(getDriverLibURL(), this.getClass().getClassLoader());
		Class cl = null;
		String clazz = "org.springframework.jdbc.core.JdbcTemplate";
		try {
			cl = loader.loadClass(clazz);
		} catch (ClassNotFoundException e) {
			throw new IOException("Can't load class '" + clazz + "', using driver lib '" + driverLib + "'");
		}
		JdbcTemplate jdbcSrc;
		try {
			jdbcSrc = (JdbcTemplate) cl.newInstance();
			jdbcSrc.setDataSource(getDataSource());
		} catch (Exception e) {
			throw new IOException("Can't instanciate class '" + clazz + "', using driver lib '" + driverLib + "'");
		}
		
		return(jdbcSrc);
		*/
		JdbcTemplate jdbcTmpl = new JdbcTemplate(getDataSource());
		return(jdbcTmpl);
	}

	public String[] getHeaderData() {
		// TODO Auto-generated method stub
		return null;
	}
}
