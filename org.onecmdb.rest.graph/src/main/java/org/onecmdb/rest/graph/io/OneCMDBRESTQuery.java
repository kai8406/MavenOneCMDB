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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.xml.XmlParser;
import org.onecmdb.utils.xml.GraphQuery2XML;
import org.onecmdb.utils.xml.XML2Graph;
import org.onecmdb.utils.xml.XML2GraphQuery;

public class OneCMDBRESTQuery {

	
	
	public static void main(String argv[]) {
		GraphQuery q = new GraphQuery();
		ItemOffspringSelector ci = new ItemOffspringSelector("ci", "Ci");
		ci.setPrimary(true);
		ci.setMatchTemplate(true);
		q.addSelector(ci);
		
		try {
			new OneCMDBRESTQuery().post(new URL("http://localhost:8080/onecmdb-desktop/onecmdb/query"), q, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public Graph post(URL url, GraphQuery q, String token) throws Exception {
		
		// Construct data
		String xml = GraphQuery2XML.toXML(q, 0);
		String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(xml, "UTF-8");
		data += "&" + URLEncoder.encode("style", "UTF-8") + "=" + URLEncoder.encode("graph", "UTF-8");
		if (token != null) {
			data += "&" + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
		}
		
		//data += "&" + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(OneCMDBConnection.instance().getToken(), "UTF-8");
		// Send data
		URLConnection conn = url.openConnection();
	
		InputStream in = null;
		OutputStream out = null;

		try {
			conn.setDoOutput(true);
			
			out = conn.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(out);
			wr.write(data);
			wr.flush();

			// Get the response
			in = conn.getInputStream();
			
			Graph g = new XML2Graph().fromXML(in);
			
			return(g);
			// Dump to stdout...
			/*
			XmlParser parser = new XmlParser();
			List<CiBean> beans = parser.parseInputStream(in);
			System.out.println("Found beans :"  + beans.size());

			return(new Graph());
			*/
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}
	
}
