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
package org.onecmdb.ui.gwt.desktop.server.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.server.service.content.ContentParserFactory;


public class ContentServiceServlet extends HttpServlet {
	private static final int BUF_SIZE = 8192;
	private String contentPath = "onecmdb/content";
	
	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		
		File root = ContentParserFactory.get().getRootPath();
		if (root == null) {
			resp.sendError(404, "Root path not found!");
			return;
		}

		resp.setContentType("text/html");

		FileItem uploadItem;
		try {
			getFileItem(req, root);
			
			resp.getWriter().write("OK");
		} catch (Throwable t) { 
			resp.sendError(500, t.getMessage());
			t.printStackTrace();
		}
	}

	private void getFileItem(HttpServletRequest request, File root) throws FileUploadException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		if (!isMultipart) {
			throw new IllegalArgumentException("Not multipart...");
		}
		
		ServletFileUpload upload = new ServletFileUpload();
		
		// Parse the request
		FileItemIterator iter = upload.getItemIterator(request);
		
		String fileName = null;
		String path = null;
		while (iter.hasNext()) {			
			FileItemStream item = iter.next();
			
			String name = item.getFieldName();
			InputStream stream = item.openStream();
			System.out.println("Name=" + item.getName());
			
			
			if (item.isFormField()) {
				String value = Streams.asString(stream);
				System.out.println("FormField " + name + "=" + value);
				if (name.equals("name")) {
					fileName = value;
				}
				if (name.equals("path")) {
					path = value;
				}
				
			} else {
				System.out.println("File field " + name + " with file name "
						+ item.getName() + " detected.");
				
				
				File output = new File(root, path + "/" + fileName);
				System.out.println("Write upload to " + output.getPath());
				
				IOUtil.copyCompletely(stream, new FileOutputStream(output));
			}
			
		}
	}

	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		File root = ContentParserFactory.get().getRootPath();
		if (root == null) {
			resp.sendError(404, "Root path not found!");
			return;
		}
		
		String path = req.getPathInfo();
		// Trim content path...
		int offset = path.indexOf(getContentPath());
		if (offset > 0) {
			path = path.substring(offset + getContentPath().length());
			
			//resp.sendError(404, "Path incorrect, [" + requestPath + "]");
			
		}
		
		File file = new File(root, path);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			resp.setContentLength((int) file.length());
			transfer(in, resp.getOutputStream());
			resp.getOutputStream().flush();
		} catch (Throwable t) {
			resp.sendError(404, path);
			return;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					// Ignore...
				}
			}
		}
	}

	public long transfer(InputStream in, OutputStream out) throws IOException {
		byte b[] = new byte[BUF_SIZE];
		boolean eof = false;
		long totalLength = 0;
		while (!eof) {
			int len = in.read(b, 0, BUF_SIZE);
			if (len < 0) {
				eof = true;
				continue;
			}
			totalLength += len;
			out.write(b, 0, len);
		}
		return(totalLength);	
	}

	protected void transfer() {
		
	}
	
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		System.out.println("INIT");
	}

}
