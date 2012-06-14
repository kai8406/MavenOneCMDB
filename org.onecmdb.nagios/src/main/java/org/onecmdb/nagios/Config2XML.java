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
package org.onecmdb.nagios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class Config2XML {

	private static final int STATE_FIND_DEFINE = 1;
	private static final int STATE_INSIDE_DEFINE = 2;


	public static void main(String argv[]) {
		File from = new File(argv[0]);
		if (!from.exists()) {
			System.err.println("Input " + from + " directory not found:");
			System.exit(-1);
		}
		if (from.isDirectory()) {
			File to = new File(argv[1]);
			if (!to.isDirectory()) {
				System.err.println("Need output directory!");
				System.exit(-1);
			}
			for (File f : from.listFiles()) {
				if (f.getName().endsWith(".cfg")) {
					File toFile = new File(to, f.getName().replace(".cfg", ".xml"));
					transform(f, toFile);
				}
			}
		} else {
			File to = new File(argv[1]);
			if (!to.isDirectory()) {
				System.err.println("Need output directory!");
				System.exit(-1);
			}
			File toFile = new File(to, from.getName().replace(".cfg", ".xml"));
			transform(from, toFile);
		}
		
	}
	
	public static void transform(File from, File to) {
		System.out.println("Transform " + from.getPath() + "-->" + to.getPath());
		
		FileInputStream in = null;
		OutputStream out = System.out;
		
		try {
			in = new FileInputStream(from);
			
			if (to != null) {
				out = new FileOutputStream(to);
			}
			new Config2XML().transform(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
	}


	public void transform(InputStream in, OutputStream out) throws IOException {
		LineNumberReader lin = new LineNumberReader(new InputStreamReader(in));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));

		boolean eof = false;
		pw.println("<?xml version=\"1.0\"?>");
		pw.println("<NagiosConfig>");
		int state = STATE_FIND_DEFINE;
		String currentType =  null;
		while(!eof) {
			String line = lin.readLine();
			if (line == null) {
				eof = true;
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			switch(state) {
			case STATE_FIND_DEFINE:
				if (line.matches(".*define.*\\{.*")) {
					state = STATE_INSIDE_DEFINE;
					String split[] = line.split(" ");
					if (split.length == 1) {
						split = line.split("\t");
					}
					for (int offset = 1; offset < split.length; offset++) {
						currentType = split[offset];
						if (currentType.length() > 1) {
							break;
						}
					}
					
					
					String split2[] = currentType.split("\\{");
					currentType = split2[0];
					currentType = currentType.trim();
					pw.println(getTab(1) + "<" + currentType + ">");
				}
				break;
			case STATE_INSIDE_DEFINE:
				if (line.contains("}")) {
					// End of define.
					state = STATE_FIND_DEFINE;
					pw.println(getTab(1) + "</" + currentType +">");
					currentType = null;
					pw.flush();
					continue;
				}
				// store value.
				line = line.trim();
				if (line.startsWith("#")) {
					continue;
				}
				String split[] = line.split("\\s+", 2);
				if (split.length == 2) {
					String varName = split[0];
					if (varName.startsWith(";")) {
						continue;
					}
					String varValue = split[1];

					String value[] = varValue.split(";", 2);

					pw.println(getTab(2) + "<" + varName + ">");
					pw.println(getTab(3) + "<value>" + toXmlString(value[0].trim()) + "</value>");
					if (value.length > 1 && value[1].length() > 0) {
						pw.println(getTab(3) + "<description>" + toXmlString(value[1].trim()) + "</description>");
					}
					pw.println(getTab(2) + "</" + varName + ">");

				}
			}
		}
		pw.println("</NagiosConfig>");
		pw.flush();
	}
	
	public static String toXmlString(String s) {
		if (s == null) {
			return(null);
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
	public static String getTab(int index) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < index; i++) {
			b.append("\t");
		}
		return(b.toString());
	}
}