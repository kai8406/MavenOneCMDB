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
package org.onecmdb.rest.graph.model;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.rest.graph.io.OneCMDBConnection;


public class CIModel implements ITreeTableModel {
	
	private CiBean ci;
	private CiBean template;
	private List<AttributeModel> children = null;
	private OneCMDBConnection connection;
	
	public CIModel(CiBean template, CiBean ci) {
		this.ci = ci;
		this.template = template;
	}
	
	public CIModel(OneCMDBConnection connection, CiBean template, CiBean ci) {
		this.ci = ci;
		this.template = template;
		this.connection = connection;
	}

	public Object getChild(int index) {
		if (children == null) {
			children = new ArrayList<AttributeModel>();
			for (AttributeBean aBean : template.getAttributes()) {
				children.add(new AttributeModel(this, aBean));
			}
		}
		return(children.get(index));
	}

	public int getChildCount() {
		if (template == null) {
			return(0);
		}
		if (template.getAttributes() == null) {
			return(0);
		}
		return(template.getAttributes().size());
	}

	public Object getValue(int column) {
		switch(column) {
			case CIAttributeModel.DISPLAY_NAME:
				return(ci.getDisplayName());
			case CIAttributeModel.ALIAS:
				return(ci.getAlias());
		
						
		}
		return("");
	}

	public CiBean getCI() {
		return(this.ci);
	}
	
	public CiBean getTemplate() {
		return(this.template);
	}
	
	public String toString() {
		return(ci.getDisplayName());
	}

	public OneCMDBConnection getConnection() {
		if (connection == null) {
			return(OneCMDBConnection.instance());
		}
		return(connection);
	}
}
