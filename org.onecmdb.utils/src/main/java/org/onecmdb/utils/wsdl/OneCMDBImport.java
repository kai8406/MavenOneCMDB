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
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;

public class OneCMDBImport {
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"input", "Input file or '-'  from stdin", "-"},
		{"delete", "If true all beans in xml input will be deleted", "false"},
		{"group", "Group alias that all imorted files should belong to", null},
		{"syncInput", "Input model use to sync, supports alias changes etc", null}
		
	};
	
	public static void main(String argv[]) {
		SimpleArg arg = new SimpleArg(ARGS);
		
		String url = arg.getArg(ARGS[0][0], argv);
		String username = arg.getArg(ARGS[1][0], argv);
		String pwd = arg.getArg(ARGS[2][0], argv);
		String input = arg.getArg(ARGS[3][0], argv);
		String delete = arg.getArg(ARGS[4][0], argv);
		String group = arg.getArg(ARGS[5][0], argv);
		String sync = arg.getArg(ARGS[6][0], argv);
		
		
		try {
			long start = System.currentTimeMillis();
			List<CiBean> beans = getBeans(input);
			for (CiBean bean : beans)  {
				System.out.println("\t" + bean.getDerivedFrom() + "::" + bean.getAlias());
				if (group != null) {
					bean.setGroup(group);
				}
			}
			List<CiBean> syncBeans = null;
			if (sync != null) {
				 syncBeans = getBeans(sync);
			}
			
			IOneCMDBWebService service = OneCMDBServiceFactory.getWebService(url);
		
			String token = service.auth(username, pwd);
			IRfcResult result = null;
			String action = "";
			if (delete.equals("true")) {
				result  = service.update(token, null, beans.toArray(new CiBean[0]));
				action = "deleting";
			} else if (syncBeans != null) {
				result  = service.update(token, beans.toArray(new CiBean[0]), syncBeans.toArray(new CiBean[0]));
				action = "merge";		
			} else {
				result = service.update(token, beans.toArray(new CiBean[0]), null);
				action = "importing";
			}			
			// Logout.
			service.logout(token);
			
			if (result.isRejected()) {
				System.out.println("Problem " + action + " CI, cause " + result.getRejectCause());
				System.exit(-1);
			}
			long stop = System.currentTimeMillis();
			long dt = (stop-start);
			System.out.println("Completed [" + beans.size() + " CI in " + dt +"ms, " + dt/beans.size() + "ms/CI]");
			System.exit(0);
			
		} catch (Exception e) {
			System.err.println("ERROR:" + e.toString());
			e.printStackTrace();
			arg.showHelp();
		}
	}

	public static List<CiBean> getBeans(String input) throws Exception {
		XmlParser parser = new XmlParser();
		if (input.equals("-")) {
			List<CiBean> beans = parser.parseInputStream(System.in);
			return(beans);
		}
		
		// Can be URL or file or directory.
		URL url = null;
		try {
			url = new URL(input);
		} catch (MalformedURLException e) {
			// Could be a simple file.
		}
		if (url == null) {
			File f = new File(input);
			if (f.isFile()) {
				try {
					url = f.toURL();
				} catch (MalformedURLException e) {
				}
			}
		}
		if (url == null) {
			throw new Exception("Not a valid input format");
		}
		parser.setURL(url.toExternalForm());
		List<CiBean> beans = parser.getBeans();
		return(beans);
	}
}
