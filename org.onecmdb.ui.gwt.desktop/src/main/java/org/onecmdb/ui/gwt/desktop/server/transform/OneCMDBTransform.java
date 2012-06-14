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
package org.onecmdb.ui.gwt.desktop.server.transform;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.onecmdb.core.utils.graph.query.selector.ItemRelationSelector;
import org.onecmdb.core.utils.graph.query.selector.ItemSelector;
import org.onecmdb.core.utils.graph.result.Graph;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.utils.wsdl.SimpleArg;
import org.onecmdb.utils.xml.Graph2XML;
import org.onecmdb.utils.xml.XML2GraphQuery;

/**
 * <code>DumpOneCMDB</code> retrieve CI and produce XML as output.
 * 
 *
 */
public class OneCMDBTransform {
	
	protected static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	//protected static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	private HashMap<String, CiBean> templateMap = new HashMap<String, CiBean>();
	private Throwable transformException;
	private Throwable writerException;
	private Properties attrMap;
	private String xmlStyle = "";
	private boolean deepTree = false;
	
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"token", "A login token", null},
		{"queryURL", "A url containing a cmdb query", null},
		{"xslt", "Transform output...", null},
		{"output", "Output file, - stdout", "-"},
		{"transform", "Perfom the transformation", "true"},
		{"style", "Structure of xml data generated before xslt[onecmdb, tree, deeptree, graph]", "onecmdb"}
			
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
		String style = arg.getArg(ARGS[8][0], argv);
	
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
		
		OneCMDBTransform query = new OneCMDBTransform();
		query.setServiceURL(url);
		query.setUsername(username);
		query.setPwd(pwd);
		query.setToken(token);
		query.setQueryFile(queryFile);
		query.setOutput(output);
		query.setXSLT(xslt);
		query.setXmlStyle(style);
		
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
	
	public void process(final PrintWriter resultStream) throws Throwable {
		GraphQuery selQuery = null;
		XML2GraphQuery parser = new XML2GraphQuery();
		parser.setAttributeMap(attrMap);
		if (this.query != null) {
			StringReader reader = new StringReader(this.query);
			selQuery = parser.parse(reader);
		} else if (queryFile.equals("-")) {
			selQuery = parser.parse(System.in, "UTF-8");
		} else {
			parser.setQueryURL(queryFile);
			selQuery = parser.parse();
		}
		
		final GraphQuery query = selQuery;
		
		final ItemSelector primary = query.fetchPrimarySelectors();
		if (primary == null) {
			throw new IllegalArgumentException("No primary selector found!");
		}
		
		final Graph result = getService().queryGraph(getToken(), query);
		
		result.buildMap();
		
		PipedOutputStream pOut = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(pOut);
		
		final Reader input = new InputStreamReader(in, "UTF-8");
		//final Reader input = new InputStreamReader(in);
		OutputStreamWriter out = new OutputStreamWriter(pOut, "UTF-8");
		//final OutputStreamWriter out = new OutputStreamWriter(pOut);
		final PrintWriter pw = new PrintWriter(out, true);
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
						InputStreamReader reader = new InputStreamReader(xsltStream);
						transform(input, reader, resultStream);
						resultStream.flush();
					} else if (getXsltData() != null) {
						StringReader reader = new StringReader(getXsltData());
						transform(input, reader, resultStream);
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
				} finally {
					
				}
				System.out.println("READER THREAD DIEs");
			}
		});
		thread.start();
		Thread writer = new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("Writer starts");
				
				try {
					if (xmlStyle == null) {
						xmlStyle = "";
					}
					if (xmlStyle.equals("onecmdb")) {
						List<CiBean> beans = new ArrayList<CiBean>();
						beans.addAll(result.fetchAllNodeOffsprings());
						XmlGenerator gen = new XmlGenerator();
						gen.setBeans(beans);
						gen.transfer(pw);
					} else if (xmlStyle.equals("tree") || xmlStyle.equals("deeptree")) {
						// Produce tree...
						if (xmlStyle.equals("deeptree")) {
							deepTree = true;
						}
						pw.println(XML_HEADER);
						pw.println("<Tree>");
						for (ItemSelector sel : query.fetchSelectors()) {
							if (sel.isPrimary()) {
								Template t = result.fetchNode(sel.getId());
								printTemplateTree(pw, t, query, result, "/" + t.getId(), 1);
							}
						}
						pw.println("</Tree>");
					} else if (xmlStyle.length() == 0 || xmlStyle.equals("graph")){
						// Default...
						pw.println(XML_HEADER);
						pw.println(new Graph2XML().toXML(result, 0));
						/*
						pw.println("<Graph>");
						printGraph(pw, result, 1);
						pw.println("</Graph>");
						*/
					} //else if (xmlStyle.length() == 0 || xmlStyle.equals(anObject))
				} finally {
					try {
						pw.flush();
						pw.close();
					} catch (Throwable t) {

					}
				}
				} catch (Throwable t) {
					writerException = t;
				}
				System.out.println("WRITER THREAD DIEs");
			}
		});
		writer.start();
		//Thread.sleep(1000);
		System.out.println("Wait for reader");
		synchronized(thread) {
			thread.join();
		}
		System.out.println("Wait for writer");
		// Kill Writer.
		
		synchronized (writer) {
			writer.interrupt();
		}
		// Check if Transform has raised any exception.
		if (transformException != null) {
			throw transformException;
		}
		System.out.println("End of thread..");
	}
	
	
	private void printGraph(PrintWriter pw, Graph result, int tab) {
		pw.println(getTab(tab) + "<Nodes>");
		for(Template t : result.getNodes()) {
			pw.println(getTab(tab+1) + "<Node id=\"" + t.getId() + "\" type=\"" + t.getTemplate().getAlias() + "\">");
			
			if (t.getOffsprings() != null) {
				for (CiBean bean : t.getOffsprings()) {
					pw.println(bean.toXML(tab+2));
				}
			}
			pw.println(getTab(tab+1) + "</Node>");
		}
		pw.println(getTab(tab) + "</Nodes>");
			
		pw.println(getTab(tab) + "<Edges>");
		for(Template t : result.getEdges()) {
			pw.println(getTab(tab+1) + "<Edge id=\"" + t.getId() + "\">");
			if (t.getTemplate() != null) {
				pw.println(getTab(tab+2) + "<Template>");
				pw.println(t.getTemplate().toXML(tab+3));
				pw.println(getTab(tab+2) + "</Template>");
			}
			if (t.getOffsprings() != null) {
				for (CiBean bean : t.getOffsprings()) {
					pw.println(bean.toXML(tab+2));
				}
			}
			pw.println(getTab(tab+1) + "</Edge>");
		}
		pw.print(getTab(tab) + "</Edges>");
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
			
			pw.println(getTab(level) + "<" + t.getId() + " alias=\"" + bean.getAlias() + 
					"\" id=\"" + bean.getIdAsString() + "\"" + 
					" type=\"" + bean.getDerivedFrom() + "\" " +
					" createDate=\"" + CiBean.toXmlDateTime(bean.getCreateDate()) + "\" " +
					" modifyDate=\"" + CiBean.toXmlDateTime(bean.getLastModified()) + "\"" +
					">");
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
				if (!deepTree) {
					if (path.contains("/" + rel.getId())) {
						continue;
					}
				}
				if (rel.getSource().equals(t.getId())) {
					Template referenced = result.fetchReference(bean, RelationConstraint.SOURCE, rel.getId());
					referenced.setId(rel.getTarget());
					
					Template refT = result.fetchNode(rel.getTarget());
					referenced.setTemplate(refT.getTemplate());
					
					pw.println(getTab(level+1) + "<" + rel.getId() + " direction=\"OUTBOUND\">");
					String newPath = path;
					if (deepTree) {
						if (path.contains("/" + bean.getAlias())) {
							continue;
						}

						newPath = path + "/" + bean.getAlias();
				
					} else {
						newPath = path + "/" + rel.getId();
					}
					printTemplateTree(pw, referenced, query, result, newPath, level+2);
					pw.println(getTab(level+1) + "</" + rel.getId() + ">");
				} 
				if (rel.getTarget().equals(t.getId())) {
					Template referenced = result.fetchReference(bean, RelationConstraint.TARGET, rel.getId());
					referenced.setId(rel.getSource());
					
					Template refT = result.fetchNode(rel.getSource());
					referenced.setTemplate(refT.getTemplate());
				
					pw.println(getTab(level+1) + "<" + rel.getId() + " direction=\"INBOUND\">");
					String newPath = path;
					if (deepTree) {
						if (path.contains("/" + bean.getAlias())) {
							continue;
						}

						newPath = path + "/" + bean.getAlias();
				
					} else {
						newPath = path + "/" + rel.getId();
					}
					printTemplateTree(pw, referenced, query, result, newPath, level+2);
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

	private String query;
	private String xsltData;
	
	
	
	
	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}


	public String getXsltData() {
		return xsltData;
	}


	public void setXsltData(String xsltData) {
		this.xsltData = xsltData;
	}


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


	public String getXmlStyle() {
		return xmlStyle;
	}


	public void setXmlStyle(String xmlStyle) {
		this.xmlStyle = xmlStyle;
	}
}

