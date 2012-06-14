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
package org.onecmdb.ui.gwt.itil.client.application.incident.control;


import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIAttributeValueInputControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

public class NewIncidentAttributeControl extends CIAttributeValueInputControl {

	
	public NewIncidentAttributeControl(String templateAlias, boolean isNew) {
		super(templateAlias, isNew);
	}

	public NewIncidentAttributeControl(GWT_CiBean bean) {
		super(bean);
	}
	
	protected void beforeStore() {
		// Update ID ...
		String alias = getLocal().getAlias();
		String split[] = alias.split("-");
		GWT_ValueBean id = getLocal().fetchAttributeValueBean("ID", 0);
		if (id != null) {
			id.setValue(split[1]);
		}
		GWT_ValueBean reportDate = getLocal().fetchAttributeValueBean("reportDate", 0);
		if (reportDate != null) {
			reportDate.setValue(OneCMDBUtils.getXMLDateString());
		}
	}

}
