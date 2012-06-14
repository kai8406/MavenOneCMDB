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
package org.onecmdb.ui.gwt.desktop.server.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.server.command.ExecCommand;
import org.onecmdb.ui.gwt.desktop.server.command.ExportQueryCommand;
import org.onecmdb.ui.gwt.desktop.server.command.IconCommand;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;
import org.onecmdb.ui.gwt.desktop.server.service.model.ConfigurationFactory;
import org.springframework.web.bind.ServletRequestDataBinder;

public class IconServiceServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
				
		File root = ContentParserFactory.get().getRootPath();
		if (root == null) {
			resp.sendError(404, "Root path not found!");
			return;
		}
		IconCommand cmd = new IconCommand();
		cmd.setRoot(root.getCanonicalPath());
		
		ServletRequestDataBinder binder = new ServletRequestDataBinder(cmd);
		binder.bind(req);
	
		
			resp.setContentType(cmd.getContentType());
			
			OutputStream out = resp.getOutputStream();
			cmd.transfer(out);
			out.flush();
			
		} catch (Throwable e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST, "Error:" + e.getMessage());
		}		
   }

}
