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
package org.onecmdb.core.utils.xpath.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.dom4j.DocumentException;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.xml.XmlParser;

public class OneCMDBHTTPClient {

	private static final int BUF_SIZE = 8192;
	
	private String pwd;
	private String user;
	private URL baseURL;

	private String token;

	public void setBaseURL(URL url) {
		this.baseURL = url;  
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		
	}
	
	public void login() throws IOException {
		URL url = new URL(baseURL.toExternalForm() +  "/auth?user=" + this.user +"&pwd=" + this.pwd);
		InputStream input = url.openStream();
		this.token = getInputAsString(input);
	}

	public List<CiBean> getBeans(String path, String args) throws IOException, DocumentException {
		URL url = new URL(baseURL.toExternalForm() + 
				"/query?" + 
				"auth=" + this.token + 
				"&path=" + path + 
				"&outputAttributes=" + args + 
				"&outputFormat=xml");
		InputStream input = url.openStream();
		XmlParser parser = new XmlParser();
		List<CiBean> beans = parser.parseInputStream(input);
		return(beans);
		
	}
	
	public String query(String path, String args, String format) throws IOException {
		
		URL url = new URL(baseURL.toExternalForm() + 
				"/query?" +
				"auth=" + this.token + 
				"&path=" + path + 
				"&outputAttributes=" + args + 
				"&outputFormat=" + format);
		
		InputStream input = url.openStream();
		String resp = getInputAsString(input);
		
		return(resp);
	}

	
	private String getInputAsString(InputStream input) throws IOException {
		InputStreamReader reader = new InputStreamReader(input, "UTF-8");
		StringBuffer buffer = new StringBuffer();
		char data[] = new char[BUF_SIZE];
		boolean eof = false;
		while(!eof) {
			int len = reader.read(data, 0, BUF_SIZE);
			if (len < 0) {
				eof = true;
				continue;
			}
			buffer.append(data, 0, len);
		}
		return(buffer.toString());
	}

}
