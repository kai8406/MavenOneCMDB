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
package org.onecmdb.ui.gwt.toolkit.client.control;

import java.util.Comparator;

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;

public class AttributeComparator implements Comparator {
	
	public AttributeComparator() {
	}
	
	public int compare(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return(0);
		}
		if (o1 == null) {
			return(-1);
		}
		if (o2 == null) {
			return(1);
		}
		
		if ((o1 instanceof GWT_AttributeBean) && (o2 instanceof GWT_AttributeBean)) {
			String a1 = ((GWT_AttributeBean)o1).getAlias();
			String a2 = ((GWT_AttributeBean)o2).getAlias();
			if (a1 != null && a2 != null) {
				return(a1.compareTo(a2));
			}
		}
		return(0);
	}
}
