/*
 * OneCMDB, an open source configuration management project.
 * Copyright 2007, Lokomo Systems AB, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.onecmdb.core.utils.transform.export;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CSVExportSet extends ExportSet {
	private ItemSelector instanceSelector;
	private List<ColumnSelector> columnSelector;
	private String delimiter;
	
	public List<ColumnSelector> getColumnSelector() {
		return columnSelector;
	}
	
	public void setColumnSelector(List<ColumnSelector> columnSelector) {
		// Add sort order
		this.columnSelector = columnSelector;
		
		Collections.sort(columnSelector, 
				new Comparator<ColumnSelector>() {
					public int compare(ColumnSelector o1, ColumnSelector o2) {
						return(o1.getSortOrder().compareTo(o2.getSortOrder()));
					}
				}
		);
	}
	
	public ItemSelector getInstanceSelector() {
		return instanceSelector;
	}
	public void setInstanceSelector(ItemSelector instanceSelector) {
		this.instanceSelector = instanceSelector;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	
	
}
