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
package org.onecmdb.utils.wsdl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.dom4j.DocumentException;
import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;

public class OneCMDBJobSchedulare {
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"token", "A credential token to use", null},
		{"job", "Job alias", "JobSchedulareTrigger"},
	};
	
	public static void main(String argv[]) {
		SimpleArg arg = new SimpleArg(ARGS);
		
		String url = arg.getArg(ARGS[0][0], argv);
		String username = arg.getArg(ARGS[1][0], argv);
		String pwd = arg.getArg(ARGS[2][0], argv);
		String token = arg.getArg(ARGS[3][0], argv);
		String job = arg.getArg(ARGS[4][0], argv);
		
		try {
			long start = System.currentTimeMillis();
			IOneCMDBWebService service = OneCMDBServiceFactory.getWebService(url);
			boolean logout = false;
			if (token == null) {
				token = service.auth(username, pwd);
				logout = true;
			}
			
			// Find job Trigger...
			ItemAliasSelector sel = new ItemAliasSelector("trigger", "Root");
			sel.setAlias(job);
			sel.setPrimary(true);
			GraphQuery q = new GraphQuery();
			q.addSelector(sel);
			
			Graph g = service.queryGraph(token, q);
			Template triggerTemplate = g.fetchNode("trigger");
			List<CiBean> triggers = triggerTemplate.getOffsprings();
			if (triggers.size() == 0) {
				System.err.println("Trigger alias '" + job +"' not found!");
				System.exit(-1);
			}
			
			CiBean trigger = triggers.get(0);
			service.reschedualeTrigger(token, trigger);
			
			if (logout) {
				// Logout.
				service.logout(token);
			}
			
			System.exit(0);
			
		} catch (Exception e) {
			System.err.println("ERROR:" + e.toString());
			e.printStackTrace();
			arg.showHelp();
		}
	}
}
