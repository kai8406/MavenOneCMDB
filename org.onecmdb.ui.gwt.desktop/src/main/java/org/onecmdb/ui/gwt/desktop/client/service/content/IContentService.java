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
package org.onecmdb.ui.gwt.desktop.client.service.content;

import java.io.IOException;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IContentService extends RemoteService {
	public List<? extends ContentData> list(String token, ContentData parent);
	public String get(String token, ContentData content);
	public String get(String token, ContentData parent, String enc);
	public boolean put(String token, ContentData content, String data);
	public boolean put(String token, ContentData content, String enc, String data);
	public ContentData updateMetaData(ContentData data);
	public ContentData stat(ContentData iconMapping);
	public boolean delete(String token, ContentData data);
	public boolean create(String token, ContentData data);
	public boolean copy(String token, ContentData source, ContentData target, boolean override) throws CMDBRPCException;
	public ContentFolder mkdir(String token, ContentData folder) throws CMDBRPCException;
	
	//public void move(String token, ContentData from, ContentData to);
	
	//public String delete(String token, ContentData parent);
}
