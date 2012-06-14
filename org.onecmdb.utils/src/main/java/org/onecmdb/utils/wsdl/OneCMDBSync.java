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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.ccb.workers.RfcResult;
import org.onecmdb.core.internal.model.primitivetypes.DateTimeType;
import org.onecmdb.core.utils.IBeanProvider;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.wsdl.IOneCMDBWebService;
import org.onecmdb.core.utils.wsdl.OneCMDBServiceFactory;
import org.onecmdb.core.utils.xml.XmlGenerator;
import org.onecmdb.core.utils.xml.XmlParser;

public class OneCMDBSync implements Runnable {

	private String syncPath;
	private String serviceURL;
	private String username;
	private String pwd;
	private String token;
	private String group;
	private String name;
	private IOneCMDBWebService service;
	private boolean doDelete;
	private IRfcResult result;
	private IRfcResult deleteResult = new RfcResult();
	
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSyncPath() {
		return syncPath;
	}

	public void setSyncPath(String path) {
		this.syncPath = path;
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

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
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

	public void run() {
		
		// Search for all files in inPath.
		File in = new File(getImportPath());
		if (!in.isDirectory()) {
			throw new IllegalArgumentException("Import Path '" + getImportPath() + "' is not a directory!");
		}
		File out = new File(getImportStatusPath());
		if (!out.isDirectory()) {
			throw new IllegalArgumentException("Complete Path '" + getImportStatusPath() + "' is not a directory!");
		}
	
		try {
			parseInputPath(in, in, out);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	
	}
	
	private String getImportStatusPath() {
		return(syncPath + "/status");
	}

	private String getImportPath() {
		return(syncPath + "/import");
	}
	
	private String getDeltaPath() {
		return(syncPath + "/delta");
	}
	
	
	public boolean isDoDelete() {
		return doDelete;
	}

	public void setDoDelete(boolean doDelete) {
		this.doDelete = doDelete;
	}

	private List<CiBean> getTemplateBeans() {
		List<CiBean> templates = new ArrayList<CiBean>();
		CiBean sync = new CiBean("Root", "Synchronisation", true);
		sync.setDisplayNameExpression("Synchronisation");

		CiBean importEntry = new CiBean("Synchronisation", "ImportEntry", true);
		importEntry.setDisplayNameExpression("${group}/${content}");
		
		importEntry.addAttribute(new AttributeBean("Rejected", "isRejected", "xs:boolean", null, false));
		importEntry.addAttribute(new AttributeBean("Cause", "rejectCause", "xs:string", null, false));		
		importEntry.addAttribute(new AttributeBean("TX ID", "txId", "xs:string", null,  false));
		importEntry.addAttribute(new AttributeBean("Entry Name", "content", "xs:anyURI", null, false));
		importEntry.addAttribute(new AttributeBean("Status", "status", "xs:string", null, false));
		importEntry.addAttribute(new AttributeBean("Group", "group", "ImportGroup", "PointsTo", true));
		importEntry.addAttribute(new AttributeBean("CI Added", "added", "xs:integer", null, false));
		importEntry.addAttribute(new AttributeBean("CI Deleted", "deleted", "xs:integer", null, false));
		importEntry.addAttribute(new AttributeBean("CI Modified", "modified", "xs:integer", null, false));
		importEntry.addAttribute(new AttributeBean("Started", "start", "xs:dateTime", null, false));
		importEntry.addAttribute(new AttributeBean("Ended", "stop", "xs:dateTime", null, false));
		importEntry.addAttribute(new AttributeBean("Total CI", "totalCI", "xs:integer", null, false));
		importEntry.addAttributeValue(new ValueBean("content", "ImportEntry", false));		
		
		CiBean importGroup = new CiBean("Synchronisation", "ImportGroup", true);
		importGroup.setDisplayNameExpression("${name}");
		AttributeBean gName = new AttributeBean("name", "xs:string", null, false);
		gName.setDisplayName("Name");
		importGroup.addAttribute(gName);		
		importGroup.addAttributeValue(new ValueBean("name", "ImportGroup", false));		
		
		templates.add(sync);
		templates.add(importEntry);
		templates.add(importGroup);
			
		return(templates);
	}

	private void parseInputPath(File in, File inRoot, File outRoot) throws Exception {
		System.out.println("Parse: " + in.toURI());
		if (in.isDirectory()) {
			File content[] = in.listFiles();
			for (int i = 0; i < content.length; i++) {
				File sub = content[i];
				parseInputPath(sub, inRoot, outRoot);
			}
			return;
		}
	
		System.out.println("Found: " + in.toURI());
		String url = in.toURI().toString();
		if (!url.endsWith(".xml")) {
			System.out.println(url + " :Import files must end width '.xml' skipping...");
			return;
		}
		String inRootPath = inRoot.toURI().toString();
		String outRootPath = outRoot.toURI().toString();
		
		String fileOffsetPath = url.substring(inRootPath.length());
		String outPath = outRootPath + fileOffsetPath;
		File statusFile = new File(new URI(outPath));
	
		if (statusFile.exists() && (statusFile.lastModified() > in.lastModified())) {
			System.out.println("Already done: " + url);
			return;
		}
		if (statusFile.exists()) {
			System.out.println(url + "File modified, re-import....");
		}
	
		// Add this file to input list...
		XmlParser parser = new XmlParser();
		parser.setURL(url);
		List<CiBean> beans = null;
		try {
			 beans = parser.getBeans();
		} catch (Throwable t) {
			System.out.println("Wrong format in file: " + url);
			return;
		}
		// Update security group on all CIS.
		if (group != null) {
			for (CiBean bean : beans) {
				bean.setGroup(group);
			}
		}
		// If we receive empty result set ignore, eq. don't remove all!
		
		if (beans.size() > 0) {
			// Create diff files, and generate delete entries.
			List<CiBean> deletedBeans = createDelta(in, fileOffsetPath, parser, statusFile);
			if (!doDelete) {
				deletedBeans = Collections.EMPTY_LIST;
			}

			this.result = getService().update(token, beans.toArray(new CiBean[0]), null);	

			if (this.result.isRejected()) {
				System.out.println("FAILED: Import <" + in.getPath() + "> :" + result.getRejectCause());
			} else {
				System.out.println("OK: Import <" + in.getPath() + ">");
			}
			
			// Handle Delete
			
			if (deletedBeans.size() > 0) {
				this.deleteResult = getService().update(token, null, deletedBeans.toArray(new CiBean[0]));	
				if (deleteResult.isRejected()) {
					System.out.println("FAILED: DELETE <" + in.getPath() + "> :" + result.getRejectCause());
				} else {
					System.out.println("OK: DELETE <" + in.getPath() + ">");
				}
			}
			
			if (!this.result.isRejected()) {
				// Move file to outRoot...
				statusFile.getParentFile().mkdirs();

				// Copy content..
				try {
					copyFile(in, statusFile);
				} catch (Exception e) {
					System.out.println("Can't save import file " + in.getPath() + " to " + statusFile.getPath() + ":" + e);
				}
			}
		} else {
			this.result = new RfcResult();
		}
		String groupName = this.name;
		if (groupName == null) {
			groupName = in.getPath();
		}
		CiBean importGroup = new CiBean();
		importGroup.setDerivedFrom("ImportGroup");
		importGroup.setAlias("ImportGroup-" + groupName.hashCode());
		importGroup.setTemplate(false);
		importGroup.setGroup(group);
		importGroup.addAttributeValue(new ValueBean("name", groupName, false));

		// Add this file so we keep track of it...
		String deletedCIs = "0";
		if (deleteResult != null) {
			if (deleteResult.getCiDeleted() != null) {
				deletedCIs = deleteResult.getCiDeleted().toString();
			}
		}
		CiBean importEntry = new CiBean();
		importEntry.setDerivedFrom("ImportEntry");
		importEntry.setAlias("ImportContent-" + in.toURI().toString().hashCode());
		importEntry.setTemplate(false);
		importEntry.setGroup(group);
		importEntry.addAttributeValue(new ValueBean("isRejected", "" + result.isRejected(), false));
		importEntry.addAttributeValue(new ValueBean("rejectCause", "" + result.getRejectCause(), false));
		importEntry.addAttributeValue(new ValueBean("txId", "" + result.getTxId(), false));
		importEntry.addAttributeValue(new ValueBean("content", fileOffsetPath, false));
		importEntry.addAttributeValue(new ValueBean("status", "Imported", false));
		importEntry.addAttributeValue(new ValueBean("group", importGroup.getAlias(), true));
		importEntry.addAttributeValue(new ValueBean("added", "" + (result.getCiAdded() == null ? 0 : result.getCiAdded()) , false));
		importEntry.addAttributeValue(new ValueBean("deleted", deletedCIs, false));
		importEntry.addAttributeValue(new ValueBean("modified", "" + (result.getCiModified() == null ? 0 : result.getCiModified()) + "" , false));
		importEntry.addAttributeValue(new ValueBean("totalCI", "" + beans.size(), false));
		
		
		Date start = result.getStart();
		if (start == null) {
			start = new Date();
		}
		importEntry.addAttributeValue(new ValueBean("start", DateTimeType.parseDate(start), false));
		Date stop = result.getStop();
		if (stop == null) {
			stop = new Date();
		}
		importEntry.addAttributeValue(new ValueBean("stop", DateTimeType.parseDate(stop), false));
		
		List<CiBean> templates = getTemplateBeans();
		templates.add(importEntry);
		templates.add(importGroup);
		IRfcResult statusResult = getService().update(token, templates.toArray(new CiBean[0]), null);
		if (statusResult.isRejected()) {
			System.out.println("FAILED: Status Import <" + importEntry.getAlias() + "> :" + statusResult.getRejectCause());
		} 
	}
	
	private List<CiBean> createDelta(File in, String fileOffsetPath, IBeanProvider imported, File statusFile) throws Exception {
		File delatPath = new File(getDeltaPath());
		
		File deltaFile = new File(getDeltaPath(), fileOffsetPath);
		
		// Move file to outRoot...
		deltaFile.getParentFile().mkdirs();
		
		if (!statusFile.exists()) {
			// Store all beans as new.
			copyFile(in, new File(deltaFile.getPath() + "-NEW-" + getTS() + ".xml"));
			return(Collections.EMPTY_LIST);
		}
	
		// Add this file to input list...
		XmlParser parser = new XmlParser();
		parser.setURL(statusFile.toURL().toExternalForm());
			 
		List<CiBean> newBeans = new ArrayList<CiBean>();
		List<CiBean> deletedBeans = new ArrayList<CiBean>();

		// Find new
		for (CiBean bean : imported.getBeans()) {
			if (parser.getBean(bean.getAlias()) == null) {
				newBeans.add(bean);
			}
		}
		// Find removed.
		for (CiBean bean : parser.getBeans()) {
			if (imported.getBean(bean.getAlias()) == null) {
				deletedBeans.add(bean);
			}
		}

		// Flush them out.
		XmlGenerator gen = new XmlGenerator();
		gen.setOutput(deltaFile.getPath() + "-NEW-" + getTS() + ".xml");
		gen.setBeans(newBeans);
		gen.process();

		XmlGenerator gen2 = new XmlGenerator();
		gen2.setOutput(deltaFile.getPath() + "-DELETED-" + getTS() + ".xml");
		gen2.setBeans(deletedBeans);
		gen2.process();
		
		return(deletedBeans);
	}

	private String getTS() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
		format.setTimeZone(TimeZone.getDefault());
		return(format.format(new Date()));
	}

	private void copyFile(File in, File out) throws Exception {
	    FileInputStream fis  = new FileInputStream(in);
	    FileOutputStream fos = new FileOutputStream(out);
	    try {
	        byte[] buf = new byte[8192];
	        int i = 0;
	        while ((i = fis.read(buf)) != -1) {
	            fos.write(buf, 0, i);
	        }
	    } catch (Exception e) {
	        throw e;
	    } finally {
	    	try {
	    		if (fis != null) {
	    			fis.close();
	    		}
	    	} finally {
	    		if (fos != null) { 
	    			fos.close();
	    		}
	    	}
	    }
	  }
	private static String ARGS[][] = {
		{"url", "WSDL URL excluding ?WSDL", "http://localhost:8080/webservice/onecmdb"},
		{"user", "The user to login as.", "admin"},
		{"pwd", "The user to login as.", "123"},
		{"syncPath", "Path to syncronization directory", null},
		{"group", "Group alias that all imorted files should belong to", null},
		{"token", "Used instead of username/pwd", null},
		{"name", "Group all import entry by this name", "Default Import Group"},
	};
	
	public static void main(String argv[]) {
		
		SimpleArg arg = new SimpleArg(ARGS);
		
		String url = arg.getArg(ARGS[0][0], argv);
		String username = arg.getArg(ARGS[1][0], argv);
		String pwd = arg.getArg(ARGS[2][0], argv);
		String syncPath = arg.getArg("syncPath", argv);
		String group = arg.getArg("group", argv);
		String token = arg.getArg("token", argv);
		String name = arg.getArg("name", argv);
				
		OneCMDBSync sync = new OneCMDBSync();
		sync.setServiceURL(url);
		sync.setSyncPath(syncPath);
		sync.setGroup(group);
		sync.setToken(token);
		sync.setUsername(username);
		sync.setPwd(pwd);
		sync.setName(name);
		try {
			sync.run();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public IRfcResult getResult() {
		return(result);
	}

	public IRfcResult getDeleteResult() {
		return(deleteResult);
	}
}
