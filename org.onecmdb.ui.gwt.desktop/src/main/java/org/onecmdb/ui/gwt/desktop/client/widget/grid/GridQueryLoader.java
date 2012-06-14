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
package org.onecmdb.ui.gwt.desktop.client.widget.grid;

import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.CIModelCollection;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;

public class GridQueryLoader extends BasePagingLoader<BasePagingLoadConfig, BasePagingLoadResult<CIModelCollection>> {

	private GridModelConfig modelConfig;

	public GridQueryLoader(DataProxy proxy, GridModelConfig modelConfig) {
		super(proxy);
		this.modelConfig = modelConfig;
	}

	@Override
	protected BasePagingLoadConfig prepareLoadConfig(BasePagingLoadConfig config) {
		((BasePagingLoadConfig)config).set("query", modelConfig.getQuery());
		((BasePagingLoadConfig)config).set("mdr", modelConfig.getMDR());
		if (config.getSortInfo() != null) {
			config.getSortInfo().setSortDir(getSortDir());
			config.getSortInfo().setSortField(getSortField());
			
			String sortField = config.getSortInfo().getSortField();
			if (sortField != null) {
				for (AttributeColumnConfig colCfg : this.modelConfig.getColumnConfig()) {
					if (colCfg.getId().equals(sortField)) {
						String attrType = "valueAsString";

						if (colCfg.isComplex()) {
							attrType = "complex";
						}

						if (colCfg.getType().startsWith("xs:date")) {
							attrType = "valueAsDate";
						}
						if (colCfg.getType().startsWith("xs:integer")) {
							attrType = "valueAsLong";
						}
						((BasePagingLoadConfig)config).set("attrType", attrType);
						break;
					}
				}
			}
		}
		return(config);	
	}
	
	
	

}
