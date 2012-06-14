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
package org.onecmdb.ui.gwt.desktop.client.control;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBRPCException;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.utils.EditorFactory;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class GridModelConfigLoader {

	private ContentData mdr;
	private ContentData gridData;
	private CMDBPermissions perm;
	private boolean editTemplate;

	public GridModelConfigLoader(ContentData mdr, ContentData gridData, CMDBPermissions perm) {
		this.mdr = mdr;
		this.gridData = gridData;
		this.perm = perm;
	}
	
	public GridModelConfigLoader(ContentData mdr, ContentData gridData, CMDBPermissions perm, boolean editTemplate) {
		this.mdr = mdr;
		this.gridData = gridData;
		this.perm = perm;
		this.editTemplate = editTemplate;
	}

	public void load(final AsyncCallback<GridModelConfig> asyncCallback) {
		
		ModelServiceFactory.get().loadGridConfig(CMDBSession.get().getToken(), mdr, gridData,  new AsyncCallback<GridModelConfig>() {

			public void onFailure(Throwable arg0) {
				asyncCallback.onFailure(arg0);
			}

			public void onSuccess(GridModelConfig arg0) {
				parseConfig(arg0, asyncCallback);
				
			}
			
		});
		
	}

	/**
	 * Generate ColumnConfig items...
	 * 
	 * @param arg0
	 * @param asyncCallback
	 */	
	protected void parseConfig(GridModelConfig arg0,
			AsyncCallback<GridModelConfig> asyncCallback) {
		List<ColumnConfig> cols = new ArrayList<ColumnConfig>();
		// Transform AttributeColumnModel to ColumnModel....
		if (arg0.getColumnConfig() == null || arg0.getColumnConfig().size() == 0) {
			asyncCallback.onFailure(new CMDBRPCException("Invalid table config", "No columns is set", null));
			return;
		}
		if (arg0.getQuery() == null) {
			asyncCallback.onFailure(new CMDBRPCException("Invalid table config", "No query is set", null));
			return;
		}
		
		
		for (AttributeColumnConfig config : arg0.getColumnConfig()) {
			config.setMDR(arg0.getMDR());
			config.setEditTemplate(editTemplate);
			ColumnConfig c = getColumnConfig(config);
			cols.add(c);
		}
		
		arg0.setColumns(cols);
		
		asyncCallback.onSuccess(arg0);
	}

	/**
	 * Generate EXT ColumnConfig from Attribute...
	 * @param config
	 * @return
	 */
	private ColumnConfig getColumnConfig(AttributeColumnConfig config) {
		ColumnConfig column = EditorFactory.getColumnConfig(config, false, perm);
		return(column);
	}

	
}
