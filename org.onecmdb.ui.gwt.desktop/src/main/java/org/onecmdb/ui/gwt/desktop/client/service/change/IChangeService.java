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
package org.onecmdb.ui.gwt.desktop.client.service.change;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IChangeService extends RemoteService {
	public ChangeRecord loadChanges(String token, CIModel mdr, CIModel config, CIModel mdrHistory, ChangeFilter filter) throws CMDBRPCException;
	public StoreResult commit(String token, CIModel mdr, CIModel config, CIModel mdrHistory, List<ChangeItem> items) throws Exception;
	public StoreResult delete(String token, CIModel mdr, CIModel mdrConfig, CIModel mdrHistory, List<ChangeItem> items) throws Exception;

	/*
	public void update(ContentFile change, List<ChangeItem> items);
	public void commit(ContentFile change);
	*/
}
