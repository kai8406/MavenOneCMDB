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
package org.onecmdb.web.remote;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;

public class ClassPathController extends UrlFilenameViewController {
	private static final int BUF_SIZE = 8192;
	private Log log = null;
	private String pathPrefix = "/classpath/"; 
	private URLClassLoader additionalLoader = null;
	
	public void setLog(Log log) {
    	this.log = log;
    }
    
    public Log getLog() {
    	if (this.log == null) {
    		this.log = LogFactory.getLog(this.getClass());
    	}
    	return(this.log);
    }
	public void init() {
		getLog().info("INIT: Classpath controller.");
	}
	
	/*
	 * Make the path prefix spring configurable.
	 */
	public void setPathPrefix(String prefix) {
		this.pathPrefix = prefix;
	}
	
	public void setAdditionURLS(List<String> urlStrings) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		for (String urlString : urlStrings) {
				urls.add(new URL(urlString));
				getLog().info("Adding additional url <" + urlString +">");
		}
		this.additionalLoader = new URLClassLoader(urls.toArray(new URL[0]));
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse resp) {
		
		try {
			String path = request.getPathInfo();
			// Remove Prefix
			String resource = path;
			if (path.startsWith(pathPrefix)) {
				resource = path.substring(pathPrefix.length());
			}
			getLog().info("Load Resource <" + path + "> - " + resource);
			System.out.println("Load Resource <" + path + "> - " + resource);
			OutputStream out = resp.getOutputStream();
			
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(resource);
			
			if (in == null) {
				// Check additional loader.
				if (additionalLoader != null) {
					in = additionalLoader.getResourceAsStream(resource);
				}
			}
			
			if (in == null) {
				getLog().info("Resource <" + resource + "> not found!");
				resp.setStatus(404);
				return(null);
			}
			
			try {		
				// Transfer stream
				boolean eof = false;
				byte data[] = new byte[BUF_SIZE];
				long totalBytes = 0;
				while(!eof) {
					int len = in.read(data, 0, BUF_SIZE);
					if (len < 0) {
						eof = true;
						continue;
					}
					out.write(data, 0, len);
					totalBytes += len;
				}
				getLog().info("Resource <" + resource + "> bytes " + totalBytes);
			} finally {
				try {
					in.close();
				} catch (Exception e) {
					// Not much to do...
				}
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return(null);
	}
	


}

