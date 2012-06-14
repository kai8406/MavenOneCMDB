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

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;

public class ValueModel implements ITreeTableModel {

	private AttributeModel aModel;
	private ValueBean vBean;
	private CIModel model;

	public ValueModel(AttributeModel aModel, ValueBean vBean) {
		this.aModel = aModel;
		this.vBean = vBean;
		
		if (aModel.getModel().getCI().isTemplate() && model == null) {
			if (vBean.isComplexValue()) {
				CiBean template = aModel.getModel().getConnection().getBeanFromAlias(aModel.getAttribute().getType());
				
				model = new CIModel(aModel.getModel().getConnection(), template, template);
			}
		}
		if (vBean.isComplexValue() && !vBean.hasEmptyValue() && model == null) {
			// Load alias.
			CiBean value = aModel.getModel().getConnection().getBeanFromAlias(vBean.getValue());
			
			CiBean template = aModel.getModel().getConnection().getBeanFromAlias(value.getDerivedFrom());
			
			model = new CIModel(aModel.getModel().getConnection(), template, value);
		}
	}
	
	public Object getChild(int index) {
		if (model != null) {
			return(model.getChild(index));
		}
		return(null);
	}

	public int getChildCount() {
		if (model != null) {
			return(model.getChildCount());
		}
		/*
		if (aModel.getAttribute().isComplexType() && !vBean.hasEmptyValue()) {
			return(1);
		}
		*/
		return(0);
	}

	public Object getValue(int column) {
		
		switch(column) {
			case CIAttributeModel.VALUE:
				if (model == null) {
					return(vBean.getValue() == null ? "" : vBean.getValue());
				} else {
					return(model);
				}
		}
		return("");
	}
	
	public String toString() {
		if (this.model != null) {
			return(model.getCI().getDisplayName());
		}
		return("");
	}

}
