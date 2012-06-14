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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.core.utils.graph.query.constraint.RelationConstraint;
import org.onecmdb.core.utils.graph.query.selector.ItemAliasSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemOffspringSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.utils.wsdl.SimpleArg;
import org.onecmdb.utils.xml.XML2GraphQuery;

/**
 * <code>DumpOneCMDB</code> retrieve CI and produce XML as output.
 * 
 *
 */
public class OneCMDBQuery2XML {
	
	//protected static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	protected static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	private HashMap<String, CiBean> templateMap = new HashMap<String, CiBean>();
	private Throwable transformException;
	private Properties attrMap;
	
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"token", "A login token", null},
		{"queryURL", "XPath to query file holding the query", null},
		{"xslt", "Transform output...", null},
		{"output", "Output file, - stdout", "-"},
		{"transform", "Perfom the transformation", "true"}
	};
	
	public static void main(String argv[]) {
		SimpleArg arg = new SimpleArg(ARGS);
		String url = arg.getArg(ARGS[0][0], argv);
		String username = arg.getArg(ARGS[1][0], argv);
		String pwd = arg.getArg(ARGS[2][0], argv);
		String token = arg.getArg(ARGS[3][0], argv);
		String queryFile = arg.getArg(ARGS[4][0], argv);
		String xslt = arg.getArg(ARGS[5][0], argv);
		String output = arg.getArg(ARGS[6][0], argv);
		String transform = arg.getArg(ARGS[7][0], argv);
	
		if (transform.equals("false")) {
			xslt = null;
		}
		if (queryFile == null) {
			System.out.println("--queryURL is missing!");
			arg.showHelp();
		}
		// Disable Console logger.
		Appender consoleAppender = Logger.getRootLogger().getAppender("stdout");
		Logger.getRootLogger().removeAppender(consoleAppender);
		
		OneCMDBQuery2XML query = new OneCMDBQuery2XML();
		query.setServiceURL(url);
		query.setUsername(username);
		query.setPwd(pwd);
		query.setToken(token);
		query.setQueryFile(queryFile);
		query.setOutput(output);
		query.setXSLT(xslt);
		
		try {
			query.process();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void process() throws Throwable {
		PrintWriter rStream = new PrintWriter(System.out);
		if (!getOutput().equals("-")) {
			rStream = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getOutput()), "UTF-8"));
		}
		process(rStream);
	}
	
	public void process(Reader queryReader, final Reader xsltReader, final PrintWriter resultPrinter) throws Throwable {
		GraphQuery query = null;
		XML2GraphQuery parser = new XML2GraphQuery();
		parser.setAttributeMap(attrMap);
		query = parser.parse(queryReader);
		
		
		ItemSelector primary = query.fetchPrimarySelectors();
		if (primary == null) {
			throw new IllegalArgumentException("No primary selector found!");
		}
		
		Graph result = getService().queryGraph(getToken(), query);
		
		result.buildMap();
		
		PipedOutputStream pOut = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(pOut);
		
		//final Reader input = new InputStreamReader(in, "UTF-8");
		final Reader input = new InputStreamReader(in);
		//OutputStreamWriter out = new OutputStreamWriter(pOut, "UTF-8");
		OutputStreamWriter out = new OutputStreamWriter(pOut);
			
		// Start consumer...
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					if (xsltReader != null) {
						transform(input, xsltReader, resultPrinter);
						resultPrinter.flush();
					} else {
						// Transfer to System.out;
						boolean eof = false;
						int bufSize = 1024;
						char[] buf = new char[bufSize];

						while(!eof) {
							//System.out.println("START READ");
							int len = input.read(buf, 0, bufSize);
							//System.out.println("READ[" + len + "]");
								
							if (len < 0) {
								eof = true;
								continue;
							}
							resultPrinter.write(buf, 0, len);
						}
						resultPrinter.flush();
						System.out.println("EOF...");
					}
				} catch (Exception e) {
					e.printStackTrace();
					transformException = e;
				}
				System.out.println("THREAd DIEs");
			}
		});
		thread.start();
		
		
		// Produce tree...
		PrintWriter pw = new PrintWriter(out, true);
		pw.println(XML_HEADER);
		pw.println("<OneCMDBResult>");
		pw.println("<Statistics>");
		printStat(pw, query, result, 1);
		pw.println("</Statistics>");
		pw.println("<Graph>");
		Template t = result.fetchNode(primary.getId());
		printTemplateTree(pw, t, query, result, "/" + t.getId(), 1);
		pw.println("</Graph>");
		pw.println("</OneCMDBResult>");
			
		pw.flush();
		
		//Thread.sleep(1000);
		pw.close();
		
		synchronized(thread) {
			thread.join();
		}
		// Check if Transform has raised any exception.
		if (transformException != null) {
			throw transformException;
		}
		System.out.println("End of thread..");
	
	}
	
	private void printStat(PrintWriter pw, GraphQuery query, Graph result, int i) {
		for (ItemSelector sel : query.fetchSelectors()) {
			Template t = result.fetchNode(sel.getId());
			if (t == null) {
				t = result.fetchEdge(sel.getId());
			}
			int size = 0;
			int totalCount = -1;
			if (t != null) {
				totalCount = t.getTotalCount();
				if (t.getOffsprings() != null) {
					size = t.getOffsprings().size();
				}
			}
			pw.println(getTab(i) + "<selector id=\"" + sel.getId() + "\" matched=\"" +  + size + "\" totalCount=\"" + totalCount + "\"/>");
		}
		
	}


	public void process(final PrintWriter resultStream) throws Throwable {
		GraphQuery query = null;
		XML2GraphQuery parser = new XML2GraphQuery();
		parser.setAttributeMap(attrMap);
		if (queryFile.equals("-")) {
			query = parser.parse(System.in, "ISO-8859-1");
		} else {
			parser.setQueryURL(queryFile);
			query = parser.parse();
		}
		
		
		ItemSelector primary = query.fetchPrimarySelectors();
		if (primary == null) {
			throw new IllegalArgumentException("No primary selector found!");
		}
		
		Graph result = getService().queryGraph(getToken(), query);
		
		result.buildMap();
		
		PipedOutputStream pOut = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(pOut);
		
		//final Reader input = new InputStreamReader(in, "UTF-8");
		final Reader input = new InputStreamReader(in);
		//OutputStreamWriter out = new OutputStreamWriter(pOut, "UTF-8");
		OutputStreamWriter out = new OutputStreamWriter(pOut);
			
		// Start consumer...
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					if (getXSLT() != null) {
						URL url = null;
						if (getXSLT().startsWith("classpath:")) {
							String name = getXSLT().substring("classpath:".length());
							url = getClass().getClassLoader().getResource(name);
						} else {
							url = new URL(getXSLT());
						}
					
						InputStream xsltStream = url.openStream();
						transform(input, xsltStream, resultStream);
						resultStream.flush();
					} else {
						// Transfer to System.out;
						boolean eof = false;
						int bufSize = 1024;
						char[] buf = new char[bufSize];

						while(!eof) {
							//System.out.println("START READ");
							int len = input.read(buf, 0, bufSize);
							//System.out.println("READ[" + len + "]");
								
							if (len < 0) {
								eof = true;
								continue;
							}
							resultStream.write(buf, 0, len);
						}
						resultStream.flush();
						System.out.println("EOF...");
					}
				} catch (Exception e) {
					e.printStackTrace();
					transformException = e;
				}
				System.out.println("THREAd DIEs");
			}
		});
		thread.start();
		
		
		// Produce tree...
		PrintWriter pw = new PrintWriter(out, true);
		pw.println(XML_HEADER);
		pw.println("<Graph>");
		Template t = result.fetchNode(primary.getId());
		printTemplateTree(pw, t, query, result, "/" + t.getId(), 1);
		pw.println("</Graph>");
		pw.flush();
		
		//Thread.sleep(1000);
		pw.close();
		
		synchronized(thread) {
			thread.join();
		}
		// Check if Transform has raised any exception.
		if (transformException != null) {
			throw transformException;
		}
		System.out.println("End of thread..");
	}
	
	public void transform(Reader xmlInputStream, Reader xslt, Writer resultStream) throws javax.xml.transform.TransformerException {
	    javax.xml.transform.Source xmlSource =
	        new javax.xml.transform.stream.StreamSource(xmlInputStream);
	    
	    javax.xml.transform.Source xsltSource =
	        new javax.xml.transform.stream.StreamSource(xslt);
	    
	    javax.xml.transform.Result result =
	        new javax.xml.transform.stream.StreamResult(resultStream);
	 
	    // create an instance of TransformerFactory
	    javax.xml.transform.TransformerFactory transFact =
	        javax.xml.transform.TransformerFactory.newInstance( );
	 
	    javax.xml.transform.Transformer trans =
	        transFact.newTransformer(xsltSource);
	 
	    trans.transform(xmlSource, result);
	}
	
	public void transform(Reader xmlInputStream, InputStream xslt, Writer resultStream) throws javax.xml.transform.TransformerException {
	    javax.xml.transform.Source xmlSource =
	        new javax.xml.transform.stream.StreamSource(xmlInputStream);
	    
	    javax.xml.transform.Source xsltSource =
	        new javax.xml.transform.stream.StreamSource(xslt);
	    
	    javax.xml.transform.Result result =
	        new javax.xml.transform.stream.StreamResult(resultStream);
	 
	    // create an instance of TransformerFactory
	    javax.xml.transform.TransformerFactory transFact =
	        javax.xml.transform.TransformerFactory.newInstance( );
	 
	    javax.xml.transform.Transformer trans =
	        transFact.newTransformer(xsltSource);
	 
	    trans.transform(xmlSource, result);
	}
	
	public static String toXmlString(String s) {
		if (s == null) {
			return("");
		}
		s = s.trim();
	    
		StringBuffer sb = new StringBuffer();
	    int len = s.length();
	    for (int i = 0; i < len; i++) {
	      char c = s.charAt(i);
	      switch (c) {
	      default:
	        sb.append(c);
	        break;
	      case '<':
	        sb.append("&lt;");
	        break;
	      case '>':
	        sb.append("&gt;");
	        break;
	      case '&':
	        sb.append("&amp;");
	        break;
	      case '"':
	        sb.append("&quot;");
	        break;
	      case '\'':
	        sb.append("&apos;");
	        break;
	      }
	    }
	    return(sb.toString());
	}
	
	private void printTemplateTree(PrintWriter pw, Template t, GraphQuery query, Graph result, String path, int level) throws Exception {
		for (CiBean bean : t.getOffsprings()) {
			if (bean.isTemplate()) {
				pw.println(getTab(level) + "<" + t.getId() + ">");
				pw.print(bean.toXML(level+1));
				pw.println(getTab(level) + "</" + t.getId() + ">");
				continue;
			}
			CiBean tBean = getTemplateFor(bean);
	
			pw.println(getTab(level) + "<" + t.getId() + " alias=\"" + bean.getAlias() + "\" id=\"" + bean.getIdAsString() + "\">");
			for (AttributeBean aBean : tBean.getAttributes()) {
				if (aBean.isComplexType()) {
					continue;
				}
				List<ValueBean> vBeans = bean.fetchAttributeValueBeans(aBean.getAlias());
				if (vBeans.size() == 0) {
					pw.println(getTab(level+1) + "<" + aBean.getAlias() + "></" + aBean.getAlias() + ">");
				} else {
					for (ValueBean vBean : vBeans) {
						pw.println(getTab(level+1) + "<" + aBean.getAlias() + ">" + toXmlString(vBean.getValue()) + "</" + aBean.getAlias() + ">");
					}
				}
			}
			// Handle references.
			for (ItemRelationSelector rel : query.getItemRelationSelector()) {
				if (path.contains("/" + rel.getId())) {
					continue;
				}
				if (rel.getSource().equals(t.getId())) {
					Template referenced = result.fetchReference(bean, RelationConstraint.SOURCE, rel.getId());
					referenced.setId(rel.getTarget());
					
					Template refT = result.fetchNode(rel.getTarget());
					referenced.setTemplate(refT.getTemplate());
					
					pw.println(getTab(level+1) + "<" + rel.getId() + " direction=\"OUTBOUND\">");
					printTemplateTree(pw, referenced, query, result, path + "/" + rel.getId(), level+2);
					pw.println(getTab(level+1) + "</" + rel.getId() + ">");
				} 
				if (rel.getTarget().equals(t.getId())) {
					Template referenced = result.fetchReference(bean, RelationConstraint.TARGET, rel.getId());
					referenced.setId(rel.getSource());
					
					Template refT = result.fetchNode(rel.getSource());
					referenced.setTemplate(refT.getTemplate());
				
					pw.println(getTab(level+1) + "<" + rel.getId() + " direction=\"INBOUND\">");
					printTemplateTree(pw, referenced, query, result, path + "/" + rel.getId(), level+2);
					pw.println(getTab(level+1) + "</" + rel.getId() + ">");
				}
			}
			pw.println(getTab(level) + "</" + t.getId() + ">");
		}
	}
	
	private CiBean getTemplateFor(CiBean bean) throws Exception {
		CiBean t = templateMap.get(bean.getDerivedFrom());
		if (t != null) {
			return(t);
		}
		// Query for template...
		ItemAliasSelector off = new ItemAliasSelector(bean.getDerivedFrom(), bean.getDerivedFrom());
		off.setAlias(bean.getDerivedFrom());
		off.setPrimary(true);
		GraphQuery q = new GraphQuery();
		q.addSelector(off);
		
		Graph result = getService().queryGraph(getToken(), q);
		Template tRes = result.fetchNode(off.getId());
		if (tRes.getOffsprings().size() == 0) {
			throw new IllegalArgumentException("Template <" + bean.getDerivedFrom() + "> is missing!");
		}
		CiBean template = tRes.getOffsprings().get(0);
		templateMap.put(template.getAlias(), template);
		
		return(template);
		
	}


	private String getTab(int n) {
		String tab = "";
		for (int i = 0; i < n; i++) {
			tab += "\t";
		}
		return(tab);
	}



	private String username;
	private String pwd;
	private String queryFile;
	private String serviceURL;
	private IOneCMDBWebService service;
	private String token;
	private String output;
	private String xslt;

	
	public Properties getAttrMap() {
		return attrMap;
	}


	public void setAttrMap(Properties attrMap) {
		this.attrMap = attrMap;
	}


	public String getXSLT() {
		return xslt;
	}


	public void setXSLT(String xslt) {
		this.xslt = xslt;
	}


	public String getQueryFile() {
		return queryFile;
	}

	public void setQueryFile(String queryFile) {
		this.queryFile = queryFile;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
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
	
	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String service) {
		this.serviceURL = service;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	private IOneCMDBWebService getService() throws Exception {
		if (this.service == null) {
			this.service = OneCMDBServiceFactory.getWebService(serviceURL);
			if (this.token == null) {
				this.token = this.service.auth(getUsername(), getPwd());
			}
		}
		return(service);
	}
	
	
	public void setService(IOneCMDBWebService service) {
		this.service = service;
	}
}

