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

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IContentServiceAsync  {
	public void get(String token, ContentData parent, AsyncCallback<String> callback);
	public void get(String token, ContentData parent, String enc, AsyncCallback<String> callback);
	public void list(String token, ContentData parent, AsyncCallback<List<? extends ContentData>> callback);
	public void put(String token, ContentData content, String data, AsyncCallback<Boolean> callback);
	public void put(String token, ContentData content, String enc, String data, AsyncCallback<Boolean> callback);
	public void updateMetaData(ContentData data, AsyncCallback<ContentData> callback);
	public void stat(ContentData iconMapping, AsyncCallback<ContentData> callback);
	public void delete(String token, ContentData data, AsyncCallback<Boolean> callback);
	public void create(String token, ContentData data, AsyncCallback<Boolean> callback);
	public void copy(String token, ContentData source, ContentData target, boolean override, AsyncCallback<Boolean> callback);
	public void mkdir(String token, ContentData folder, AsyncCallback<ContentFolder> callback);
	
	/*
	public void list(String token, ContentData parent, AsyncCallback<List<ContentData>> callback);
	public List<ContentData> mkdir(String token, ContentData parent, AsyncCallback<String> callback);
	public void put(String token, ContentData parent, String content, AsyncCallback<String> callback);
	public void delete(String token, ContentData parent, AsyncCallback<String> callback);
	*/
}
