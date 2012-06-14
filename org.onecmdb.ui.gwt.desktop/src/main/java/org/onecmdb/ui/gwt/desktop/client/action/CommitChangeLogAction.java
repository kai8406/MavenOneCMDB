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
package org.onecmdb.ui.gwt.desktop.client.action;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeItem;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeResult;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CommitChangeLogAction<E extends ComponentEvent> extends Action<E> {

			
	private Store<ChangeItem> store;

	public CommitChangeLogAction(Store<ChangeItem> store) {
		this.store = store;
	}

	@Override
	public void onAction() {
		store.commitChanges();
		
		List<ChangeItem> changes = store.getModels();
		/*
		ChangeServiceFactory.get().commit(CMDBSession.get().getToken(), changes, new AsyncCallback<ChangeResult>() {

			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onSuccess(ChangeResult arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		*/
	}

}
