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
package org.onecmdb.ui.gwt.desktop.client.service.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class ColumnFilter extends BaseModel {
	
	public class Column {
		Integer width;
		boolean include;
		boolean exclude;
		String attAlias;
	}
	
	public ColumnFilter(BaseModel columnFilter) {
		if (columnFilter != null) {
			setProperties(columnFilter.getProperties());
		}
	}

	public List<ColumnConfig> handleColumnFilter(List<ColumnConfig> cols) {
		List<BaseModel> columns = get("Column");
	
		List<String> exclude = new ArrayList<String>();
		List<String> include = new ArrayList<String>();
		
		HashMap<String, Column> columnMap = new HashMap<String, Column>();
		
		if (columns != null) {
			for (BaseModel column : columns) {
				Column col = new Column();
				String attAlias = (String)column.get("attAlias");
				if (attAlias == null) {
					continue;
				}
				col.attAlias = attAlias;
				columnMap.put(col.attAlias, col);

				String width = column.get("width");
				if (width != null) {
					try {
						col.width = Integer.parseInt(width);
					} catch (Throwable t) {
					}
				}

				if ("true".equals(column.get("exclude"))) {
					exclude.add(col.attAlias);
					col.exclude = true;
				}
				if ("true".equals(column.get("include"))) {
					include.add(col.attAlias);
					col.include = true;
				}

			}
		}
		if (include.size() == 0) {
			include = null;
		}
		if (exclude.size() == 0) {
			exclude = null;
		}
		
		List<String> order = this.get("order");
		
		/*
		if (exclude == null && include == null) {
			return(orderColumns(cols, order));
		}
		*/
		
		// Default exclude --> all specified is hidden.
		boolean defaultHidden = false;
		List<String> list = exclude;
		if (include != null) {
			// Include --> all not specified is hidden.
			list = include;
			defaultHidden = true;
		}
		
		boolean found = false;
		if (columns != null) {
			for (ColumnConfig config : cols) {
				for (String key : columnMap.keySet()) {
					if (config.getId().contains(key)) {
						Column c = columnMap.get(key);
						// Update values like width.
						if (c.width != null) {
							config.setWidth(c.width);
						}
					}
				}
				if (list != null) {
					found = false;
					for (String col : list) {
						if (config.getId().contains(col)) {
							config.setHidden(!defaultHidden);
							found = true;
							break;
						}
					}
					if (!found) {
						// If not found in filter, set default....
						config.setHidden(defaultHidden);
					}
				}
			}
		}
		// Order the columns
		if (include != null && order == null) {
			order = include;
		}
		return(orderColumns(cols, order));
	}

	public List<ColumnConfig> orderColumns(List<ColumnConfig> cols,
			List<String> order) {
		if (order == null) {
			return(cols);
		}
		List<ColumnConfig> ordered = new ArrayList<ColumnConfig>();
		List<ColumnConfig> rests = new ArrayList<ColumnConfig>(cols);
		for (String id : order) {
			for (ColumnConfig cfg : cols) {
				if (cfg.getId().contains(id)) {
					ordered.add(ordered.size(), cfg);
					rests.remove(cfg);
					break;
				}
			}
		}
		for (ColumnConfig cfg : rests) {
			ordered.add(cfg);
		}
		return(ordered);
	}

}
