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
package org.onecmdb.utils.internal.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CopyFile {
	public static void main(String argv[]) {
		String inFile = argv[0];
		String xPath = argv[1];
		String out = argv[2];

		CopyFile copy = new CopyFile();
		XmlXPathQuery query = new XmlXPathQuery();
		query.setXPath(xPath);
		query.setURL(inFile);
		copy.setInput(query);
		copy.setOutput(out);
		copy.start();
	}

	private XmlXPathQuery query;

	private String output;

	public void setInput(XmlXPathQuery query) {
		this.query = query;
	}

	public void setOutput(String path) {
		this.output = path;
	}

	public void start() {
		String infile = "";//query.getValue();
		System.out.println("Copy " + infile + " to " + this.output);
		URL inURL;
		try {
			inURL = new URL(infile);
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}
		File outFile = new File(this.output);
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = inURL.openStream();
			out = new FileOutputStream(outFile);
			// Copy bytes..
			int bLen = 2048;
			byte b[] = new byte[bLen];
			boolean eof = false;
			int len;
			while (!eof) {
				len = in.read(b, 0, bLen);
				if (len < 0) {
					eof = true;
					continue;
				}
				out.write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

}
