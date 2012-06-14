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

import java.io.PrintStream;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IValue;
import org.onecmdb.core.tests.profiler.Profiler;

public class CSVExporter {

	private ISession session;

	public CSVExporter(ISession session) {
		this.session = session;		
	}
	
	
	public void toOutputStream(PrintStream out, CSVExportSet set) {
		
		{
			// Print headers.
			boolean first = true;
			for (ColumnSelector sel : set.getColumnSelector()) {
				if (!first) {
					out.print(set.getDelimiter());
				}
				first = false;
				out.print(sel.getHeader());
			}
			out.println();
		}
		for (ICi instance : set.getInstanceSelector().getInstance(session)) {
			boolean first = true;
			if (false) {
			for (IAttribute a : instance.getAttributes()) {
				if (!first) {
					out.print(set.getDelimiter());
				}
				first = false;
				if (!a.isComplexValue()) {
					IValue value = a.getValue();
					out.print(value == null ? "<Empty>" : value.getDisplayName());
				}
			}
			}
			if (true) {
				for (ColumnSelector sel : set.getColumnSelector()) {
					if (!first) {
						out.print(set.getDelimiter());
					}
					first = false;
					out.print(sel.getColumnValue(session, instance));
				}
			}
			out.println();
		}
		

	}
	
}
