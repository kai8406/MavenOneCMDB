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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class CMDBChangeUpload extends AbstractCMDBCommand {
	
	private String history;
	private String input;
	private String postURL;
	
	private static String ARGS[][] = {
		{"postURL", "URL to post changes to", "http://localhost:8080/onecmdb-desktop/onecmdb/change"},
		{"history", "History entry for this upload", null},
		{"input", "Input file", null},
	};
	
	public static void main(String argv[]) {
		CMDBChangeUpload upload = new CMDBChangeUpload();
		//upload.handleArgs(ARGS, argv);
		upload.start(upload, ARGS, argv);
	}

	public String getPostURL() {
		return postURL;
	}

	public void setPostURL(String postURL) {
		this.postURL = postURL;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	@Override
	public void process() throws Exception {
		String pURL = postURL + "?token=" + getToken() + "&history=" + getHistory();
		System.out.println("Post URL: " + pURL);
		sendFiles(pURL, this.input.split(","));
	}
	
	public void sendFiles(String targetURL, String files[]) {
		 PostMethod filePost = new PostMethod(targetURL);
	       

		 /*
         filePost.getParams().setBooleanParameter(
                 HttpMethodParams.USE_EXPECT_CONTINUE,
                 cbxExpectHeader.isSelected());
         */
         try {
        	 List<Part> partList = new ArrayList<Part>();
             for (String file : files) {
            	 System.out.println("Send file : " + file);
               File f = new File(file);
               partList.add(new FilePart(f.getName(),f));
             }
             Part[] parts = partList.toArray(new Part[0]);
             /*
             Part[] parts = {
                 new FilePart(targetFile.getName(), targetFile)
             };
             */
             filePost.setRequestEntity(
                     new MultipartRequestEntity(parts, 
                     filePost.getParams())
                     );
             
             HttpClient client = new HttpClient();
             client.getHttpConnectionManager().
                     getParams().setConnectionTimeout(10000);
             
             int status = client.executeMethod(filePost);
             
             if (status == HttpStatus.SC_OK) {
              System.out.println(
                         "Upload complete, response=" + 
                         filePost.getResponseBodyAsString()
                         );
             } else {
                 System.out.println(
                         "Upload failed, response=" + 
                         HttpStatus.getStatusText(status)
                         );
             }
         } catch (Exception ex) {
             System.out.println("Error: " + ex.getMessage());
             ex.printStackTrace();
         } finally {
             filePost.releaseConnection();
         }
         
     }
}
