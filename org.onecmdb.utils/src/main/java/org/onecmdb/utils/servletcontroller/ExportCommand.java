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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.handler.QueryHandler;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.xml.XmlGenerator;

public class ExportCommand {

	private String templates;
	private String token;
	
	private IOneCmdbContext context;
	
	public String getTemplates() {
		return templates;
	}

	public void setTemplates(String templates) {
		this.templates = templates;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
	public void setContext(IOneCmdbContext onecmdb) {
		this.context = onecmdb;
	}

	public String getContentType() {
		return null;
	}

	public void transfer(OutputStream out) {
		String templates[] = getTemplates().split(",");
		GraphQuery q = new GraphQuery();
		for (int i = 0; i < templates.length; i++) {
			ItemOffspringSelector sel = new ItemOffspringSelector(templates[i], templates[i]);
			sel.setPrimary(true);
			q.addSelector(sel);
		}
		ISession session = context.getSession(token);
		if (session == null) {
			throw new SecurityException("No Session found! Try to do auth() first!");
		}
		
		QueryHandler handler = new QueryHandler(session);
		Graph result = handler.execute3(q);
		List<CiBean> beans = new ArrayList<CiBean>();
		
		for (Template t : result.getNodes()) {
			beans.addAll(t.getOffsprings());
			beans.add(t.getTemplate());
		}
		XmlGenerator gen = new XmlGenerator();
		gen.setBeans(beans);
		try {
			gen.transfer(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
