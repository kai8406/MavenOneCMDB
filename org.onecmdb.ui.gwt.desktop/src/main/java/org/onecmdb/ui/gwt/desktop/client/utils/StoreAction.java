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
package org.onecmdb.ui.gwt.desktop.client.utils;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StoreAction {

	public static void store(ContentData mdr, List<? extends ModelItem> local, List<? extends ModelItem> base, final AsyncCallback<StoreResult> callback) {
		// TODO: Save local, base...
		final MessageBox saveInfo = MessageBox.wait("Progress",  
	             "Saving your data, please wait...", "Saving..."); 
		
		
		ModelServiceFactory.get().store(mdr, CMDBSession.get().getToken(), local, base, new AsyncCallback<StoreResult>() {

			public void onFailure(final Throwable caught) {
				// Error.
				saveInfo.close();
				ExceptionErrorDialog.showError("Can't Save", caught, new Listener() {

					public void handleEvent(BaseEvent be) {
						if (callback != null) {
							callback.onFailure(caught);
						}
					}
				});
			}

			public void onSuccess(final StoreResult result) {
				saveInfo.close();
				// saved
				if (result.isRejected()) {
					MessageBox.alert("Save Failed", result.getRejectCause(), new Listener<WindowEvent>() {
						public void handleEvent(WindowEvent be) {
							if (callback != null) {
								callback.onFailure(new IllegalArgumentException(result.getRejectCause()));
							}
						}
					});
					return;
				} else {
					if (callback != null) {
						callback.onSuccess(result);
					}
				}
			}
		});
	}
}
