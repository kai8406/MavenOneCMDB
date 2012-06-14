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
package org.onecmdb.rest.graph.io;

import java.net.MalformedURLException;
import java.net.URL;

import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.result.Graph;

public class OneCMDBConnection {

	private static OneCMDBConnection instance;
	private String token;
	private String url = "http://localhost:8080/webservice/onecmdb";
	private String username = "admin";
	private String password = "123";
	private String iconURL;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

	public void setup() throws Exception {
		if (token == null) {
			token = auth(username, password);
		}
	}
	

	public static void setInstance(OneCMDBConnection connection) {
		instance = connection;
	}
	
	public static OneCMDBConnection instance() {
		if (instance == null) {
			throw new IllegalArgumentException("No OneCMDB Connection initiated!");
		}
		return(instance);
	}

	public Graph query(GraphQuery q) {
		try {
			URL url = new URL(getUrl());
			Graph g = new OneCMDBRESTQuery().post(url, q, getToken());
			return(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return(new Graph());
	}
	
	private String auth(String username, String password) {
		return null;
	}
	
	public CiBean getBeanFromAlias(String alias) {
		ItemAliasSelector sel = new ItemAliasSelector(alias, "Root");
		sel.setAlias(alias);
		sel.setPrimary(true);
		
		GraphQuery q = new GraphQuery();
		q.addSelector(sel);
		
		Graph result = query(q);
		result.buildMap();
		if (result.fetchAllNodeOffsprings().size() == 1) {
			return(result.fetchAllNodeOffsprings().iterator().next());
		}
		return(null);
	}

	public void setIconURL(String url) {
		this.iconURL = url;
	}
	
	public String getIconURL() {
		if (this.iconURL == null) {
			return("http://localhost:8080/onecmdb-desktop/onecmdb/icon");
		}
		return(this.iconURL);
	}

}
