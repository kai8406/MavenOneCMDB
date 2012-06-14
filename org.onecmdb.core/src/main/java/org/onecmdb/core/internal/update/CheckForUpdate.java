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
package org.onecmdb.core.internal.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.Version;
import org.onecmdb.core.internal.storage.hibernate.PageInfo;
import org.onecmdb.core.utils.graph.handler.QueryHandler;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;

public class CheckForUpdate extends Thread {
	Log log = LogFactory.getLog(this.getClass());
	
	private ISession session;
	private String rootURL;
	private String lastResponse;
	private long intervall;
	private volatile boolean terminate = false;
	private int count = 0;
	
	
	
	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

	@Override
	public void run() {
		log.info("Update Checker Started");
		try {
			Thread.sleep(2*60*1000);
		} catch (Throwable t) {
			// Ignore..
		}
			
		while(!terminate) {
			try {
				this.lastResponse = getUpdateInfo();
			} catch (Throwable e1) {
				log.error("Can't check for update " + rootURL + ":" + e1.getMessage());
			}
			try {
				sleep(intervall);
			} catch (Throwable e) {
			}
		}
	}
	
	public String getUpdateInfo() throws Throwable {
		InputStream in = null;
		try {
			URL url = generateURL();
			
			log.info("Check for update from " + getRootURL());

			in = url.openStream();
			String response = getResponse(in);
			this.lastResponse = response;
			log.info("Response:(" + response.length() + ")" + response);
			return(response);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					// Silently Ignore.
				}
			}
		}
	}
	
	private String getResponse(InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();

		String line = null;
		boolean eof = false;
		boolean first = true;
		while (!eof) {
			
			line = reader.readLine();
			if (line == null) {
				eof = true;
				continue;
			}
			if (!first) {
				sb.append("\n");
				first = false;
			}
			sb.append(line);
		}
		return sb.toString().trim();
	}

	private URL generateURL() throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append(rootURL);
		// increase count.
		count++;
		// Add options.
		sb.append("?count=" + count + "&id=" + getCMDB_ID() + "&component=core&version=" + Version.VERSION_STRING + "&build=" + Version.BUILD_DATE_STRING  + getSizeInfo());
		return(new URL(sb.toString().replace(" ", "%20")));
	}
	
	private String getSizeInfo() {
		int rootCount = -1;
		int ciCount = -1;
		int refCount = -1;
		try {
			GraphQuery q = new GraphQuery();
			ItemOffspringSelector total = new ItemOffspringSelector("root", "Root");
			total.setPrimary(true);
			total.setPageInfo(new PageInfo(0,1));

			ItemOffspringSelector ci = new ItemOffspringSelector("ci", "Ci");
			ci.setPageInfo(new PageInfo(0,1));

			ItemOffspringSelector reference = new ItemOffspringSelector("reference", "Reference");
			reference.setPageInfo(new PageInfo(0,1));
			
			q.addSelector(total);
			q.addSelector(ci);
			q.addSelector(reference);
			
			
			this.session.login();
			QueryHandler handler = new QueryHandler(session);
			Graph result = handler.execute3(q);
			this.session.logout();
			
			Template rootTempl = result.fetchNode("root");
			Template ciTempl = result.fetchNode("ci");
			Template refTempl = result.fetchNode("reference");

			if (rootTempl != null) {
				rootCount = rootTempl.getTotalCount();
			}
			if (ciTempl != null) {
				ciCount = ciTempl.getTotalCount();
			}
			if (refTempl != null) {
				refCount = refTempl.getTotalCount();
			}
		} catch (Throwable t) {
			// Ignore...
			//t.printStackTrace();
		}
		return("&root=" + rootCount + "&ci=" + ciCount + "&refs=" + refCount);
	}
	
	private String getCMDB_ID() {
		String id = "unkown";
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			id = localhost.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return("" + id.hashCode());
	}

	public ISession getSession() {
		return session;
	}

	public void setSession(ISession session) {
		this.session = session;
	}

	public String getRootURL() {
		return rootURL;
	}

	public void setRootURL(String rootURL) {
		this.rootURL = rootURL;
	}

	public long getIntervall() {
		return intervall;
	}

	public void setIntervall(long intervall) {
		this.intervall = intervall;
	}

	public String getLastResponse() {
		return lastResponse;
	}
}


