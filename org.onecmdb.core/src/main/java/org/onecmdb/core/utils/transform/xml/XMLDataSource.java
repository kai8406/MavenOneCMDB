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
package org.onecmdb.core.utils.transform.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.onecmdb.core.utils.transform.ANameObject;
import org.onecmdb.core.utils.transform.IDataSource;


public class XMLDataSource extends ANameObject implements IDataSource {
	
	List<URL> urls = new ArrayList<URL>();
	private String rootPath;

	public void setURLs(List<URL> urls) {
		for (URL url : urls) {
			this.addURL(url);
		}
	}
	
	public void addURL(URL sourceURL) {
		urls.add(sourceURL);
	}
	
	public List<Node> getNodes() throws IOException {
		List<Node> nodes = new ArrayList<Node>();
		for (URL url : urls) {
			URL nUrl = url;
			if (rootPath != null) {
				nUrl = new URL(nUrl.getProtocol(), nUrl.getHost(), nUrl.getPort(), rootPath + "/" + nUrl.getFile());
			}
			InputStream in = nUrl.openStream();
			try {
				SAXReader reader = new SAXReader();
				Document document = reader.read(in);
				nodes.add(document);
			} catch (DocumentException de) {
				IOException e = new IOException("Parse error in <" + url.toExternalForm() + ">, ");
				e.initCause(de);
				throw e;
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
		return(nodes);
	}

	public void close() throws IOException {	
		// Already closed.
		
	}

	public void reset() throws IOException {
		// Already reset (closed)
	}

	public void setRootPath(String path) {
		this.rootPath = path;
		
	}

	public String[] getHeaderData() {
		// TODO Auto-generated method stub
		return null;
	}
}
