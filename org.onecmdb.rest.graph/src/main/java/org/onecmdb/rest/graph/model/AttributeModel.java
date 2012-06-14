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
import org.onecmdb.core.utils.bean.ValueBean;

public class AttributeModel implements ITreeTableModel {
	
	private CIModel ci;
	private AttributeBean aBean;
	private ValueModel valueModel;
	private List<ValueModel> children = null;

	public AttributeModel(CIModel ci, AttributeBean aBean) {
		this.ci = ci;
		this.aBean = aBean;
		
	}

	public Object getChild(int index) {
		updateChildren();
		if (aBean.fetchMaxOccursAsInt() == 1) {
			if (aBean.isComplexType()) {
				return(children.get(0).getChild(index));
			}
		}
		return(children.get(index));
	}

	public int getChildCount() {
		updateChildren();
		
		if (aBean.fetchMaxOccursAsInt() == 1) {
			if (aBean.isComplexType()) {
				return(children.get(0).getChildCount());
			}
			return(0);
		}
		return(ci.getCI().fetchAttributeValueBeans(aBean.getAlias()).size());
	}

	private void updateChildren() {
		if (children == null)  {
			children = new ArrayList<ValueModel>(); 

			if (aBean.fetchMaxOccursAsInt() == 1) {

				ValueBean vBean = ci.getCI().fetchAttributeValueBean(aBean.getAlias(), 0);
				if (vBean == null) {
					vBean = new ValueBean(aBean.getAlias(), null, aBean.isComplexType());
				}
				children.add(new ValueModel(this, vBean));
			} else {
				for (ValueBean vBean : ci.getCI().fetchAttributeValueBeans(aBean.getAlias())) {
					children.add(new ValueModel(this, vBean));
				}
			}

		}
		return;
	}
	public Object getValue(int col) {
		switch(col) {
		case CIAttributeModel.DISPLAY_NAME:
			if (aBean.getDisplayName() == null || aBean.getDisplayName().length() == 0) {
				return("[" + aBean.getAlias() + "]");
			}
			return(aBean.getDisplayName());
		case CIAttributeModel.ALIAS:
			return(aBean.getAlias());	
		case CIAttributeModel.TYPE:
			return(aBean.getType());	
		case CIAttributeModel.REF_TYPE:
			return(aBean.getRefType() == null ? "N/A" : aBean.getRefType());	
		case CIAttributeModel.VALUE:
			updateChildren();
			if (aBean.fetchMaxOccursAsInt() == 1) {
				return(children.get(0).getValue(col));
			}
			break;
		}
		return("");
	}

	public AttributeBean getAttribute() {
		return(aBean);
	}
	
	public String toString() {
		if (aBean.getDisplayName() == null || aBean.getDisplayName().length() == 0) {
			return("[" + aBean.getAlias() + "]");
		}
		return(aBean.getDisplayName());
	}

	public CIModel getModel() {
		return(ci);
	}
}
