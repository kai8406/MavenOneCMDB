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
package org.onecmdb.core.utils.xpath.commands;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.Path;
import org.onecmdb.core.utils.ClassInjector;
import org.onecmdb.core.utils.bean.BeanClassInjector;
import org.onecmdb.core.utils.transform.export.CSVExportSet;
import org.onecmdb.core.utils.transform.export.CSVExporter;
import org.onecmdb.core.utils.transform.export.ExportSet;


public class ExportCommand {

	public String auth;
	public String name;
	
	private IOneCmdbContext oneCmdbContext;
	
	public ExportCommand(IOneCmdbContext ctx) {
		this.oneCmdbContext = ctx;
	}
	
	

	public ISession getAuthSession() {
		if (this.auth == null) {
			return(null);
		}
		ISession session = oneCmdbContext.getSession(this.auth);
		return(session);
	}
	
	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public String getContentType() {
		return("text/csv");
	}



	public void transfer(OutputStream out) {
		ISession session = getAuthSession();
		IModelService mSvc = (IModelService) session.getService(IModelService.class);
		//System.out.println("FIND alias=" + this.name);
		ICi ci = mSvc.findCi(new Path<String>(this.name));
		//System.out.println("FIND ci=" + ci);
		ClassInjector injector = new ClassInjector();
		Object o = injector.toBeanObject(ci);
		if (o instanceof CSVExportSet) {
			CSVExporter export = new CSVExporter(session);
			PrintStream text = new PrintStream(out, false);
			export.toOutputStream(text, (CSVExportSet)o);
			text.flush();
		}
	}
	
	
}
