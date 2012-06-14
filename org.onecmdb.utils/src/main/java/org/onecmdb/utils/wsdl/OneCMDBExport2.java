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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.xml.XmlGenerator;

/**
 * <code>DumpOneCMDB</code> retrieve CI and produce XML as output.
 * 
 *
 */
public class OneCMDBExport2 {
	private String url;
	private String user;
	private String pwd;
	private String group;
	private String templateAlias;
	private boolean template;
	private boolean instance;
	private boolean compact;
	private IOneCMDBWebService service;
	private String token;
	private String output;
	
	
	
	
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"token", "Used instead of username/pwd", null},
		{"group", "Group alias that CI belongs to", null},
		{"templateAlias", "Template name", "Ci"},
		{"template", "Template name", "true"},
		{"instance", "Template name", "false"},
		{"compact", "Compact Mode. Don't export derived attriutes", "true"},
		{"output", "Output file, - stdout", "-"},
	};
	
	public static void main(String argv[]) {
		SimpleArg arg = new SimpleArg(ARGS);
		String url = arg.getArg(ARGS[0][0], argv);
		String user = arg.getArg(ARGS[1][0], argv);
		String pwd = arg.getArg(ARGS[2][0], argv);
		String token = arg.getArg(ARGS[3][0], argv);
		String group = arg.getArg(ARGS[4][0], argv);
		String templateAlias = arg.getArg(ARGS[5][0], argv);
		String templateStr = arg.getArg(ARGS[6][0], argv);
		String instanceStr = arg.getArg(ARGS[7][0], argv);
		String compactStr = arg.getArg(ARGS[8][0], argv);
		String output = arg.getArg(ARGS[9][0], argv);
			
		
		
		boolean compact = Boolean.parseBoolean(compactStr);
		boolean template = Boolean.parseBoolean(templateStr);
		boolean instance = Boolean.parseBoolean(instanceStr);
		
		
		OneCMDBExport2 export = new OneCMDBExport2();
		export.setUrl(url);
		export.setUser(user);
		export.setPwd(pwd);
		export.setToken(token);
		export.setGroup(group);
		export.setTemplateAlias(templateAlias);
		export.setTemplate(template);
		export.setInstance(instance);
		export.setCompact(compact);
		export.setOutput(output);
		
		try {
			export.process();
		} catch (Exception e) {
			System.err.println("ERROR:" + e.toString());
			//e.printStackTrace();
			arg.showHelp();
		}
	}

	public void process() throws Exception {
		// Disable Console logger.
		Appender consoleAppender = Logger.getRootLogger().getAppender("stdout");
		Logger.getRootLogger().removeAppender(consoleAppender);
		
		try {
			login();

			List<CiBean> beans = new ArrayList<CiBean>();
			if (template) {
				beans.addAll(query(true));
			}
			if (instance) {
				beans.addAll(query(false));
			}
			XmlGenerator gen = new XmlGenerator();
			gen.setCompactMode(compact);
			gen.setBeans(beans);
			gen.setOutput(output);
			gen.process();
			//gen.transfer(System.out);
		} finally {
			logout();
		}
		
	}
	
	private void logout() {
		if (service == null) {
			return;
		}
		if (token == null) {
			return;
		}
		service.logout(token);
	}
	private void login() throws Exception {
		service = OneCMDBServiceFactory.getWebService(url);
		if (token == null) {
			token = service.auth(user, pwd);
		}
	}

	private List<CiBean> query(boolean matchTemplate) {
		List<CiBean> beans = new ArrayList<CiBean>();
		ItemSecurityConstraint sec = null;

		if (group != null) {
			sec = new ItemSecurityConstraint();
			sec.setGroupName(group);
		}

		GraphQuery q = new GraphQuery();
		ItemOffspringSelector o = new ItemOffspringSelector("t", templateAlias);
		o.setPrimary(true);
		o.setMatchTemplate(matchTemplate);
		if (sec != null) {
			o.applyConstraint(sec);
		}
		q.addSelector(o);
		Graph result = service.queryGraph(token, q);
		Template t = result.fetchNode("t");
		if (t.getTemplate() != null) {
			beans.add(t.getTemplate());
		}
		if (t.getOffsprings() != null) {
			beans.addAll(t.getOffsprings());
		}
		return(beans);
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getTemplateAlias() {
		return templateAlias;
	}

	public void setTemplateAlias(String templateAlias) {
		this.templateAlias = templateAlias;
	}

	public boolean isTemplate() {
		return template;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	public boolean isInstance() {
		return instance;
	}

	public void setInstance(boolean instance) {
		this.instance = instance;
	}

	public boolean isCompact() {
		return compact;
	}

	public void setCompact(boolean compact) {
		this.compact = compact;
	}

	public IOneCMDBWebService getService() {
		return service;
	}

	public void setService(IOneCMDBWebService service) {
		this.service = service;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
	
}
