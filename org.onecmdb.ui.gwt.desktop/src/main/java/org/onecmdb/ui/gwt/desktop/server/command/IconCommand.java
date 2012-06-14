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
package org.onecmdb.ui.gwt.desktop.server.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.xmlbeans.impl.common.IOUtil;
import org.onecmdb.ui.gwt.desktop.server.service.model.IconMapper;

public class IconCommand extends AbstractOneCMDBCommand {
	
	private String icon;
	private String type;
	private String root;
	
	
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getContentType() {
		String file = IconMapper.getIcon(icon, type);
		int index = file.lastIndexOf(".");
		String ext = "xxx";
		if (index > 0) {
			ext = file.substring(index+1);
		}
		return("image/" + ext);	
	}

	@Override
	public void transfer(OutputStream out) throws Throwable {
		String file = IconMapper.getIcon(icon, type);
		
		File f = new File(getRoot(), file);
		
		FileInputStream in = new FileInputStream(f);
		IOUtil.copyCompletely(in, out); 
		
	}

}
