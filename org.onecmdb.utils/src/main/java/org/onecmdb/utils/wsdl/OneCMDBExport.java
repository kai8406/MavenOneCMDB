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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.xml.XmlGenerator;

/**
 * <code>DumpOneCMDB</code> retrieve CI and produce XML as output.
 * 
 *
 */
public class OneCMDBExport {
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"path", "XPath to query for.", "/template/*"},
		{"attributes", "Attributes to return", "*"},
		{"compact", "Compact Mode. Don't export derived attriutes", "true"},
	};
	
	public static void main(String argv[]) {
		SimpleArg arg = new SimpleArg(ARGS);
		String url = arg.getArg(ARGS[0][0], argv);
		String username = arg.getArg(ARGS[1][0], argv);
		String pwd = arg.getArg(ARGS[2][0], argv);
		String path = arg.getArg(ARGS[3][0], argv);
		String attributes = arg.getArg(ARGS[4][0], argv);
		String compact = arg.getArg(ARGS[5][0], argv);
		boolean compactMode = Boolean.parseBoolean(compact);	
		// Disable Console logger.
		Appender consoleAppender = Logger.getRootLogger().getAppender("stdout");
		Logger.getRootLogger().removeAppender(consoleAppender);
		
		try {
			IOneCMDBWebService service = OneCMDBServiceFactory.getWebService(url);
		
			String token = service.auth(username, pwd);
			
			CiBean[] beanArray = service.query(token, path, attributes);
			List<CiBean> beans = Arrays.asList(beanArray);
			XmlGenerator gen = new XmlGenerator();
			gen.setCompactMode(compactMode);
			gen.setBeans(beans);
			gen.transfer(System.out);
		} catch (Exception e) {
			System.err.println("ERROR:" + e.toString());
			//e.printStackTrace();
			arg.showHelp();
		}
	}
}
