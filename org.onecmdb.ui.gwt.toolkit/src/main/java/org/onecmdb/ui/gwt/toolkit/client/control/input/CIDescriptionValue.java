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
package org.onecmdb.ui.gwt.toolkit.client.control.input;

import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;

public class CIDescriptionValue extends AttributeValue {

	public CIDescriptionValue(AttributeControl ctrl, GWT_CiBean bean) {
		super(ctrl, bean, null, null);
	}
	
	public String toString() {
		if (this.bean == null) {
			return("<empty>");
		}
		return(this.bean.getDescription());
	}
	
	
	public void setValue(String value) {
		if (this.bean != null) {
			this.bean.setDescription(value);
		}
	}

	public String getStringValue() {
		if (this.bean == null) {
			return(null);
		}
		return(this.bean.getDescription());
	}

	public void setValueAsCI(GWT_CiBean bean) {
	}
	
	public GWT_CiBean getValueAsCI() {
		return(null);
	}

	public boolean isNullValue() {
		if (this.bean == null) {
			return(true);
		}
		if (this.bean.getDescription() == null) {
			return(true);
		}
		if (this.bean.getDescription().length() == 0) {
			return(true);
		}
		return(false);
	}

	public String getLabel() {
		return("Description");
	}

	public String getType() {
		return("xs:string");
	}
	
	public boolean isComplex() {
		return(false);
	}

	public boolean isMultiValued() {
		return(false);
	}

	public String getDisplayName() {
		return(getLabel());
	}
	
	public String getAlias() {
		return("CI Description");
	}
	
	public String getDescription() {
		return("The description for a CI");
	}


	

}
