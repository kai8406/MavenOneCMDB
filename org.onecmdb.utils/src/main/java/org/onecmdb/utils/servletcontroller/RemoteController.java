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
package org.onecmdb.utils.servletcontroller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IOneCmdbContext;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class RemoteController extends MultiActionController {
	
	private IOneCmdbContext onecmdb;
	private Log log = null;
	/**
     * NOTE: Used to satisfy spring only.
     */
    
    public void setOneCmdb(IOneCmdbContext onecmdb) {
        this.onecmdb = onecmdb;
    }
    
    public IOneCmdbContext getOneCmdb() {
        return this.onecmdb;
    }
    
    public void setLog(Log log) {
    	this.log = log;
    }
    
    public Log getLog() {
    	if (this.log == null) {
    		this.log = LogFactory.getLog(this.getClass());
    	}
    	return(this.log);
    }
    /**
     * Initilize this controller.
     *
     */
    public void init() {
    	getLog().info("RemoteController init()");
    }
    
    public ModelAndView exportHandler(HttpServletRequest request, HttpServletResponse resp) throws Exception {
    	getLog().info("ExportHandler()");
    	ExportCommand command = new ExportCommand();
    	command.setContext(onecmdb);
    	
    	ServletRequestDataBinder binder = new ServletRequestDataBinder(command);
		binder.bind(request);
	
    	try {
			resp.setContentType(command.getContentType());
			
			//resp.setContentLength(-1);
			OutputStream out = resp.getOutputStream();
			command.transfer(out);
			out.flush();
			
		} catch (Throwable e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST, "Error:" + e.getMessage());
		}		
    	return(null);
     }
    	
    

}
