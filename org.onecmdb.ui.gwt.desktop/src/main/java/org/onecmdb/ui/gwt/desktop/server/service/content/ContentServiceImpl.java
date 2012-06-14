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
package org.onecmdb.ui.gwt.desktop.server.service.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.xmlbeans.impl.common.IOUtil;
import org.onecmdb.core.internal.authorization.RBACSession;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.content.IContentService;
import org.onecmdb.ui.gwt.desktop.server.service.CMDBRPCHandler;
import org.onecmdb.ui.gwt.desktop.server.service.ServiceLocator;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ContentServiceImpl extends RemoteServiceServlet implements IContentService {


	private static final String META_DATA = "content.meta";
	private File root;

	public ContentServiceImpl() {
		// Default root.
		setRoot(ContentParserFactory.get().getRootPath().getPath());
		ServiceLocator.registerService(IContentService.class, this);
	}
	
	public ContentServiceImpl(String root) {
		setRoot(root);
	}
	
	protected RBACSession getRBACSession(String token) {
		return(null);
	}
	
	private void validateDelete(String token) {
	}
	private void validateRead(String token) {
	}
	private void validateWrite(String token) {
	}
	
	private File getRoot() {
		return(this.root);
	}
		
	private void setRoot(String path) {
		this.root = new File(path);
	}
	/**
	 * ====================================================================
	 * Interface methods goes below.
	 * 
	 * 
	 */

	public boolean delete(String token, ContentData parent) {
		validateDelete(token);
		File f = new File(getRoot(), parent.getPath());
		return(f.delete());
	}

	public String get(String token, ContentData parent, String enc) {
		validateRead(token);
		File f = new File(getRoot(), parent.getPath());
		try {
			
			FileInputStream fis = new FileInputStream(f);
			int x= fis.available();
			byte b[]= new byte[x];
			fis.read(b);
			String content = "";
			if (enc != null) {
				content = new String(b, enc);
			} else {
				content = new String(b);
			}
			return(content);
		} catch (Throwable t) {
			return(t.toString());
		}
	}
	public String get(String token, ContentData parent) {
		return(get(token,parent,null));
	}

	public boolean move(String token, ContentData fromData, ContentData toData) {
		File from = locateFile(fromData);
		File to = locateFile(toData);
		return(from.renameTo(to));	
	}
	
	public boolean create(String token, ContentData data) {
		if (data instanceof ContentFolder) {
			File newFolder = locateFile(data);
			boolean b = newFolder.mkdirs();
			return(b);
		} 
		if (data instanceof ContentFile) {
			File newFile = locateFile(data);
			if (!newFile.getParentFile().exists()) {
				newFile.getParentFile().mkdirs();
			}
			boolean b = false;
			try {
				b = newFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return(b);
		}
		return(false);
	}
	public boolean put(String token, ContentData content, String enc, String data) {
		validateWrite(token);
		File file = locateFile(content);
		/*
		if (!file.exists()) {
			throw new IllegalArgumentException("Can't find file " + content.getName());
		}
		*/
		if (file.isDirectory()) {
			throw new IllegalArgumentException("Is not a file " + content.getName());
		}
		OutputStreamWriter out = null;
		try {
			if (enc != null) {
				out = new OutputStreamWriter(new FileOutputStream(file), enc);
			} else {
				out = new OutputStreamWriter(new FileOutputStream(file));
			}

			//out = new FileOutputStream(file);
			 out.write(data, 0, data.length());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException("Can't write to file " + content.getName(), e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}
		return(true);
	}
	
	
	public boolean put(String token, ContentData content, String data) {
		return(put(token, content, null, data));
	}

	
	public List<? extends ContentData> list(String token, ContentData parent) {
		validateRead(token);
		File root = locateFile(parent);
		
		File files[] = root.listFiles();
		List<ContentData> result = new ArrayList<ContentData>();
		
		if (files == null) {
			return(result);
		}
		// Check for a property file.
		Properties meta = getMetaData(root);
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.getName().equals(META_DATA)) {
				//continue;
			}
			ContentData cData = null;
			if (f.isDirectory()) {
				cData = new ContentFolder();
			} else {
				cData = new ContentFile();
			}
			cData.setPath(f.getPath().substring(getRoot().getPath().length()));
			cData = stat(cData);
			cData = updateMetaData(cData, meta);
			
			result.add(cData);
		}
		return(result);
	}
	
	public ContentData stat(ContentData data) {
		File f = locateFile(data);
	
		data.setExists(f.exists());
		data.setLastModified(f.lastModified());
		data.setSize(f.length());
		data.setDirectory(f.isDirectory());
		data.setName(f.getName());
		return(data);
	}
	
	
	public ContentFolder mkdir(String token, ContentData parent, ContentData child) throws CMDBRPCException {
		File dir = locateFile(parent);
		if (!dir.exists()) {
			throw new CMDBRPCException("mkdir Error:", "Parent " + parent.getPath() + " don't exists", "");
		}
		if (!dir.isDirectory()) {
			throw new CMDBRPCException("mkdir Error:", "Parent " + parent.getPath() + " is not a directory", "");
		}
		ContentFolder newFolder = new ContentFolder();
		newFolder.setPath(parent.getPath() + "/" + child.getPath());
		
		File newDir = locateFile(newFolder);
		boolean result = newDir.mkdirs();
		if (!result) {
			throw new CMDBRPCException("mkdir Error:", "Can't create dir " + newFolder.getPath(), "");
		}
		return(newFolder);
	}
	
	public ContentFolder mkdir(String token, ContentData folder) throws CMDBRPCException {
		File dir = locateFile(folder);
		if (dir.exists()){
			throw new CMDBRPCException("mkdir Error:", "Folder " + folder.getPath() + " exists", "");
		}	
		File newDir = locateFile(folder);
		boolean result = newDir.mkdirs();
		if (!result) {
			throw new CMDBRPCException("mkdir Error:", "Can't create dir " + folder.getPath(), "");
		}
		return(new ContentFolder(folder.getPath()));
	}
	
	
	/**
	 * End of Interface Methods.
	 * ====================================================================
	 * Private functions here....
	 * 
	 * 
	 */
	
	
	
	private Properties getMetaData(File root) {
		if (root == null) {
			return(null);
		}
		
		File meta = new File(root, META_DATA);
		// Search upwards...
		if (!meta.exists()) {
			return(getMetaData(root.getParentFile()));
		}
		
		Properties p = new Properties();
		
		if (meta.exists()) {
			InputStream in = null;
			try {
				in = new FileInputStream(meta);
				p.loadFromXML(in);
			} catch (Throwable t) {
				// Can't load meta data
				
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Throwable t) {
						// Silent Ignore...
				}
				}
			}
		}
		return(p);
	}
	private File locateFile(ContentData parent) {
		if (parent == null) {
			return(getRoot());
		}
		String path = parent.getPath();
		if (path == null || path.length() == 0) {
			return(getRoot());
		}
	
		File f = new File(getRoot(), path);
		
		// Validate that the path is not above getRoot();
		try {
			
			String realPath = f.getCanonicalPath();
			String rootPath = getRoot().getCanonicalPath();
				if (!realPath.startsWith(rootPath)) {
					System.out.println("OutOfBound: root<" + rootPath + "> path<" + realPath + ">");
					//throw new IllegalArgumentException("Illegal Path!!");
				}
			return(f);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error", e);
		}
	}

	public boolean exists(ContentData data) {
		File f = locateFile(data);
		return(f.exists());
	}

	public ContentData updateMetaData(ContentData data) {
		File f = locateFile(data);
		return(updateMetaData(data, getMetaData(f.getParentFile())));
	}
	
	public ContentData updateMetaData(ContentData data, Properties p) {
		if (p == null) {
			return(data);
		}
		for (Enumeration<?> keys = p.propertyNames(); keys.hasMoreElements(); ) {
			String key = (String) keys.nextElement();
			data.set(key, p.getProperty(key));
		}
		return(data);
	}
	
	/**
	 * Copy content.
	 * If the source is a directory it will do a deep copy.
	 * If the traget exists and override is false then an exception 
	 * will be generated.
	 * @throws CMDBRPCExceptionException 
	 * 
	 */
	public boolean copy(String token, ContentData source, ContentData target,
			boolean override) throws CMDBRPCException {
		System.out.println("COPY '" + source.getPath() + "' --> '" + target.getPath() + "'");
		stat(target);
		if (target.isExists() && !override) {
			throw new CMDBRPCException("Copy Error", "Target " + target.getPath() + " exists.., will not copy", "");
		}
		
		stat(source);
		if (!source.isExists()) {
			throw new CMDBRPCException("Copy Error", "Source " + target.getPath() + " not found!", "");
		}
		// Create folder
		if (source.isDirectory()) {
			
			target = mkdir(token, target);
			ContentData childTarget = target;
			// Create childTarget...
			
			String sourceFolder = source.getLastPathEntry();
			// If Source folder ends with / we will not use its name
			/*
			if (sourceFolder == null || sourceFolder.length() == 0) {
				ContentData parent = source.getParent();
				childTarget = new ContentFolder(target.getPath() + "/" + parent.getLastPathEntry());
			}
			*/
			List<? extends ContentData> children = list(token, source);
			for (ContentData child : children) {
				ContentData targetChild = new ContentFolder(childTarget.getPath() + "/" + child.getLastPathEntry());
				copy(token, child, targetChild, override);
			}
			return(true);	
		}
		// Copy the file.
		try {
			copyFile(source, target);
		} catch (IOException e) {
			throw new CMDBRPCException("Copy Error", "Coping file " + source.getPath() + " to " + target.getPath(), CMDBRPCHandler.getStackTrace(e));
		}
		return true;
	}

	
	protected void copyFile(ContentData source, ContentData target) throws IOException {
		File s = locateFile(source);
		File t = locateFile(target);
		if (t.isDirectory()) {
			t = new File(t, source.getLastPathEntry());
		}
		OutputStream out = null;
		InputStream in = null;
		try {
			out = new FileOutputStream(t);
			in = new FileInputStream(s);
			
			IOUtil.copyCompletely(in, out);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Ignore!
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// Ignore!
				}
			}
		}
	}
	
	public static void main(String argv[]) {
		ContentServiceImpl svc = new ContentServiceImpl("d:/tmp/contentdata");
		ContentData src = new ContentFolder("Source/Default/");
		ContentData dest = new ContentFolder("Target/new");
		try {
			svc.copy("", src, dest, false);
		} catch (CMDBRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
