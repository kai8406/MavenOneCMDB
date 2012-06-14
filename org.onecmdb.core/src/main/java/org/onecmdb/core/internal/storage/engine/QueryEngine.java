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
package org.onecmdb.core.internal.storage.engine;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.IDataSourceReader;
import org.onecmdb.core.IExpression;


public class QueryEngine implements IDataSourceReader {
	public static final int PAGE_SIZE = 10;
	
	private static boolean useCmdbSessionCache = true;
	private List<IDataSourceReader> readers;
	
	
	public List evaluate(IExpression expr) {
		List result = new ArrayList();
		for (IDataSourceReader reader : getDataSourceReaders()) {
			List objects = reader.evaluate(expr);
			// TODO: handle paging.
			result.addAll(objects);
		}
		return(result);
	}


	public Object evaluateToUnique(IExpression expr) {
		for (IDataSourceReader reader : getDataSourceReaders()) {
			Object o = reader.evaluateToUnique(expr);
			if (o != null) {
				return(o);
			}
		}
		return(null);
	}

	public long evaluateTotalCount(IExpression expr) {
		long count = 0;
		for (IDataSourceReader reader : getDataSourceReaders()) {
			count += reader.evaluateTotalCount(expr);
		}
		return(count);
	}


	private List<IDataSourceReader> getDataSourceReaders() {
		return(this.readers);
	}
	
	public void setDataSourceReaders(List<IDataSourceReader> readers) {
		this.readers = readers;
	}


}
