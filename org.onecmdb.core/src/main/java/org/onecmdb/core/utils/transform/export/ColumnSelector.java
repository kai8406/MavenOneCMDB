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

import org.onecmdb.core.ICi;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.internal.model.QueryResult;
import org.onecmdb.core.utils.OnecmdbUtils;

public class ColumnSelector {
	private String expr;
	private String sortOrder = "A";
	private String header;
	
	
	public ColumnSelector() {
	}
	
	public ColumnSelector(String header, String expr, String order) {
		this.expr = expr;
		this.header = header;
		this.sortOrder = order;
	}
	
	public String getColumnValue(ISession session, ICi instance) {
		// Lookup the source
		OnecmdbUtils utils = new OnecmdbUtils(session);
		
		QueryResult<IValue> result = utils.evaluate(instance, expr, null, false);
		if (result.size() == 0) {
			return(null);
		}
		if (result.size() == 1) {
			return(result.get(0).getDisplayName());
		}
		StringBuffer b = new StringBuffer();
		boolean first = true;
		for (IValue v : result) {
			if (!first) {
				b.append(",");
			}
			first = false;
			b.append(v.getDisplayName());
		}
		return(b.toString());
	}


	public String getExpr() {
		return expr;
	}


	public void setExpr(String expr) {
		this.expr = expr;
	}


	public String getSortOrder() {
		return sortOrder;
	}


	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getHeader() {
		return header;
	}


	public void setHeader(String header) {
		this.header = header;
	}
	
	
}
