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
import java.util.List;

import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.MemoryBeanProvider;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.ItemSecurityConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;

public class OneCMDBCompare {
	private String url;
	private String user;
	private String pwd;
	private String token;
	private String group;
	private String input1;
	private String input2;
	private IOneCMDBWebService service;
	private List<CiBean> beans1;
	private List<CiBean> beans2;
	
	
	
	
	
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"token", "Used instead of username/pwd", null},
		{"group", "Group alias that CI belongs to", null},
		{"input1", "Input file 1", null},
		{"input2", "Input file 2, - from OneCMDB", "-"}
	};
	
	
	
	public static void main(String argv[]) {
		SimpleArg arg = new SimpleArg(ARGS);
		
		String url = arg.getArg(ARGS[0][0], argv);
		String user = arg.getArg(ARGS[1][0], argv);
		String pwd = arg.getArg(ARGS[2][0], argv);
		String token = arg.getArg(ARGS[3][0], argv);
		String group = arg.getArg(ARGS[4][0], argv);
		String input1 = arg.getArg(ARGS[5][0], argv);
		String input2 = arg.getArg(ARGS[6][0], argv);
		
		
		OneCMDBCompare cmp = new OneCMDBCompare();
		cmp.setUrl(url);
		cmp.setUser(user);
		cmp.setPwd(pwd);
		cmp.setToken(token);
		cmp.setGroup(group);
		cmp.setInput1(input1);
		cmp.setInput2(input2);
		
		
		
		try {
			long start = System.currentTimeMillis();
			
			cmp.process();
			
			long stop = System.currentTimeMillis();
			long dt = (stop-start);
			System.exit(0);
			
		} catch (Exception e) {
			System.err.println("ERROR:" + e.toString());
			e.printStackTrace();
			arg.showHelp();
		}
	}

	private void login() throws Exception {
		service = OneCMDBServiceFactory.getWebService(url);
		
		if (token == null) {
			token = service.auth(user, pwd);
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
	
	public void process() throws Exception {
		// Validate input.
		
		try {
			loadBeans();
			
			compare(new MemoryBeanProvider(beans1.toArray(new CiBean[0])), new MemoryBeanProvider(beans2.toArray(new CiBean[0])));
		} finally {
			logout();
		}
		
		
		
	}
	
	private void compare(IBeanProvider p1, IBeanProvider p2) {
		System.out.println("Input1: " + p1.getBeans().size() + " CI:s");
		System.out.println("Input2: " + p2.getBeans().size() + " CI:s");
		int notfound = 0;
		int found = 0;
		
		for (CiBean b : p1.getBeans()) {
			CiBean b2 = p2.getBean(b.getAlias());
			if (b2 == null) {
				notfound++;
				System.out.println("NOT FOUND : [" + b.getAlias() + "] " + b.getDisplayName());
			} else {
				found++;
				System.out.println("FOUND : [" + b.getAlias() + "] " + b.getDisplayName());
			}
		}
		System.out.println("Found:" + found + ", not found:" + notfound);
	}

	private void loadBeans() throws Exception {
		if (input1 == null) {
			throw new Exception("input1 must be specified!");
		}
		if (input2 == null) {
			throw new Exception("input2 must be specified!");
		}
	
		beans1 = OneCMDBImport.getBeans(input1);
		
		if (input2.equals("-")) {
			// Need to login...
			login();
			
			beans2 = new ArrayList<CiBean>();
			GraphQuery q = new GraphQuery();
			ItemAliasSelector sel = new ItemAliasSelector();
			sel.setId("alias");
			if (group != null) {
				ItemSecurityConstraint sec = new ItemSecurityConstraint();
				sec.setGroupName(group);
				sel.applyConstraint(sec);
			}
			sel.setPrimary(true);
			q.addSelector(sel);
			for (CiBean bean : beans1) {
				sel.setAlias(bean.getAlias());
				sel.setTemplateAlias(bean.getDerivedFrom()); 
				Graph result = service.queryGraph(token, q);
				Template n = result.fetchNode("alias");
				if (n == null || n.getOffsprings() == null) {
					continue;
				}
				beans2.addAll(n.getOffsprings());
			}
		} else {
			beans2 = OneCMDBImport.getBeans(input2);
		}
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getInput1() {
		return input1;
	}

	public void setInput1(String input1) {
		this.input1 = input1;
	}

	public String getInput2() {
		return input2;
	}

	public void setInput2(String input2) {
		this.input2 = input2;
	}

}
