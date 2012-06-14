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
package org.onecmdb.core.utils.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onecmdb.core.IRFC;
import org.onecmdb.core.internal.ccb.rfc.RFCAddAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCDestroy;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAlias;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeReferenceType;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeType;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDerivedAttributeValue;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDescription;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyDisplayNameExpression;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyIsTemplate;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyMaxOccurs;
import org.onecmdb.core.internal.ccb.rfc.RFCModifyMinOccurs;
import org.onecmdb.core.internal.ccb.rfc.RFCMoveCi;
import org.onecmdb.core.internal.ccb.rfc.RFCNewAttribute;
import org.onecmdb.core.internal.ccb.rfc.RFCNewCi;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;

public class BeanRFCGenerator {
	private Log log = LogFactory.getLog(this.getClass());
	private IBeanScope scope;
	private RfcContainer rfcContainer;
	
	public RfcContainer getRfcContainer() {
		return rfcContainer;
	}

	public void setRfcContainer(RfcContainer rfcContainer) {
		this.rfcContainer = rfcContainer;
	}

	public IBeanScope getScope() {
		return scope;
	}

	public void setScope(IBeanScope scope) {
		this.scope = scope;
	}


	/**
	 * Ci Modifications. 
	 * 
	 */
	
	
	public void removeCi(CiBean bean) {
		RFCDestroy destoryRfc = new RFCDestroy();
		destoryRfc.setTargetAlias(bean.getAlias());
		rfcContainer.addDestory(destoryRfc);
	}
	
	public void addCi(CiBean bean) {
		// Validate
		if (bean.getDerivedFrom() == null) {
			throw new IllegalArgumentException("No derived from set on alias=´'" + bean.getAlias() +"'");
		}
		if (bean.getAlias() == null) {
			throw new IllegalArgumentException("No alias set on (derivedFrom='" + bean.getDerivedFrom() +"')");
		}
		
		RFCNewCi newRfc = new RFCNewCi();
		newRfc.setTargetAlias(bean.getDerivedFrom());
		newRfc.setAlias(bean.getAlias());
		newRfc.setGroup(bean.getGroup());
		
		if (bean.getDisplayNameExpression() != null) {
			newRfc.setDisplayNameExpression(bean.getDisplayNameExpression());
		}
		
		newRfc.setIsBlueprint(bean.isTemplate());

		if (bean.getDescription() != null) {
			newRfc.setDescription(bean.getDescription());
		}

		// Add attributes
		if (bean.isTemplate()) {
			for (AttributeBean aBean : bean.getAttributes()) {
				if (aBean.isDerived()) {
					continue;
				}
				IRFC rfc = getNewAttributeRFC(bean, aBean);
				rfcContainer.addNewAttribute(rfc);
			}
		}
		
		for (String name : bean.fetchAttributeValueAliases()) {
			int index = 0;
			List<IRFC> rfcs = new ArrayList<IRFC>();
			for (ValueBean vBean : bean.fetchAttributeValueBeans(name)) {
				AttributeBean aBean = bean.getAttribute(name);
				
				IRFC rfc = getSetValueRFC(bean, vBean, index);
				rfcs.add(rfc);
				//rfc.setTargetAlias(newBean.getAlias());
				index++;
			}
			if (bean.isTemplate()) {
				rfcContainer.addNewTemplateValues(rfcs);
			} else {
				rfcContainer.addNewInstanceValues(rfcs);
			}
	
		}
		if (bean.isTemplate()) {
			rfcContainer.addNewTemplate(newRfc);
		} else {
			rfcContainer.addNewInstance(newRfc);
		}
	}

	public void modifyDisplayNameExpr(CiBean left) {
		RFCModifyDisplayNameExpression rfc = new RFCModifyDisplayNameExpression();
		rfc.setTargetAlias(left.getAlias());
		rfc.setNewDisplayNameExpression(left.getDisplayNameExpression());
		
		// Add it.
		if (left.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}

	public void modifyDescrption(CiBean left) {
		RFCModifyDescription rfc = new RFCModifyDescription();
		rfc.setTargetAlias(left.getAlias());
		rfc.setDescription(left.getDescription());
		
		// Add it.
		if (left.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}

	public void modifyTemplate(CiBean left) {
		RFCModifyIsTemplate rfc = new RFCModifyIsTemplate();
		rfc.setTargetAlias(left.getAlias());
		rfc.setNewTemplate(left.isTemplate());
		
		// Add it.
		if (left.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}


	
	public void addAttribute(CiBean parent, AttributeBean left) {
		IRFC rfc = getNewAttributeRFC(parent, left);
	
		rfcContainer.addNewAttribute(rfc);
	}

	/*
	public void modifyValue(CiBean parent, ValueBean value) {
		IRFC rfc = addValueRFC(parent, value);
	}
	*/
	
	public void addValue(CiBean parent, ValueBean value) {
		IRFC rfc = getAddValueRFC(parent, value);
		if (parent.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}
	
	public void removeAttribute(CiBean parent, AttributeBean right) {
		RFCDestroy destory = new RFCDestroy();
		destory.setTargetId(right.getId());
		rfcContainer.addDestory(destory);
	}

	public void modifyAttributeType(CiBean parent, AttributeBean left) {
		RFCModifyAttributeType rfc = new RFCModifyAttributeType();
		rfc.setTargetId(left.getId());
		rfc.setNewTypeAlias(left.getType());
	
		if (parent.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}

	public void modifyAttributeRefType(CiBean parent, AttributeBean left) {
		RFCModifyAttributeReferenceType rfc = new RFCModifyAttributeReferenceType();
		rfc.setTargetId(left.getId());
		rfc.setNewReferenceTypeAlias(left.getRefType());
	
		if (parent.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}

	public void modifyMaxOccurs(CiBean parent, AttributeBean left) {
		RFCModifyMaxOccurs rfc = new RFCModifyMaxOccurs();
		rfc.setTargetId(left.getId());
		rfc.setNewMaxOccurs(left.fetchMaxOccursAsInt());
		
		if (parent.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
			
	}

	public void modifyMinOccurs(CiBean parent, AttributeBean left) {
		RFCModifyMinOccurs rfc = new RFCModifyMinOccurs();
		rfc.setTargetId(left.getId());
		rfc.setNewMinOccurs(left.fetchMinOccursAsInt());
	
		if (parent.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}

	public void modifyAttributeDescription(CiBean parent, AttributeBean left) {
		RFCModifyDescription rfc = new RFCModifyDescription();
		rfc.setTargetId(left.getId());
		rfc.setDescription(left.getDescription());
	
		if (parent.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}
	
	public void removeValue(CiBean parent, ValueBean bean) {
		RFCDestroy rfc = new RFCDestroy();
		rfc.setTargetId(bean.getId());
		rfcContainer.addDestory(rfc);
	}
	
	
	public void addValues(CiBean parent, List<IRFC> rfcs) {
		if (parent.isTemplate()) {
			rfcContainer.addNewTemplateValues(rfcs);
		} else {
			rfcContainer.addNewInstanceValues(rfcs);
		}
	}
	
	public void modifyValue(CiBean parent, ValueBean value) {
		IRFC rfc = getModifyValueRFC(parent, value);
		rfcContainer.addInstanceModify(rfc);
	}
	
	public IRFC getModifyValueRFC(CiBean parent, ValueBean value) {
		RFCModifyAttributeValue rfc = new RFCModifyAttributeValue();
		rfc.setTargetId(value.getId());
		if (value.isComplexValue()) {
			rfc.setNewValueAsAlias(value.getValue());
		} else {
			rfc.setNewValue(value.getValue());
		}
		return(rfc);
	}

	
	public void modifyAttributeDisplayNameExpr(CiBean parent, AttributeBean left) {
		RFCModifyDisplayNameExpression rfc = new RFCModifyDisplayNameExpression();
		rfc.setTargetId(left.getId());
		rfc.setNewDisplayNameExpression(left.getDisplayName());
		
		if (parent.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}


	/**
	 * Internal modifications.
	 */
	private IRFC getNewAttributeRFC(CiBean bean, AttributeBean aBean) {
		// Make sure that the type bean is created first.
		
		log.debug("NEW Attribute '" + aBean.getAlias() + "' on ci '" + bean.getAlias());

		scope.referenceBean(bean, "Type", aBean.getType());

		if (aBean.getRefType() != null) {
			scope.referenceBean(bean, "Reference Type", aBean.getRefType());
		}
		
		
		RFCNewAttribute newRfc = new RFCNewAttribute();
		newRfc.setTargetAlias(bean.getAlias());
		if (aBean.getDisplayName() != null) {
			newRfc.setDisplayNameExpression(aBean.getDisplayName());
		}
		newRfc.setAlias(aBean.getAlias());
		if (aBean.getDescription() != null) {
			newRfc.setDescription(aBean.getDescription());
		}
		newRfc.setValueTypeAlias(aBean.getType());
		if (aBean.getRefType() != null) {
			newRfc.setReferenceTypeAlias(aBean.getRefType());
		}
		if (aBean.getMaxOccurs() != null) {
			newRfc.setMaxOccurs(aBean.fetchMaxOccursAsInt());
		} else {
			newRfc.setMaxOccurs(1);
		}
		if (aBean.getMinOccurs() != null) {
			newRfc.setMinOccurs(aBean.fetchMinOccursAsInt());
		} else {
			newRfc.setMinOccurs(1);
		}

		return (newRfc);
	}
	
	/*
	private IRFC getSetValueRFC(ValueBean vBean, Long aId) {
		log.info("Set Value '" + vBean.getAlias() + "' " +
						"complex='" + vBean.isComplexValue() + "' " +
						"value='" + vBean.getValue() + "' " +  
						"id='" + aId);
		RFCModifyAttributeValue mod = new RFCModifyAttributeValue();
		mod.setTargetId(aId);
		if (vBean.isComplexValue()) {
			String aliasName = vBean.getValue();
			
			if (aliasName != null) {
				scope.referenceBean(aliasName);
			}
	
			mod.setNewValueAsAlias(aliasName);
		} else {
			mod.setNewValue(vBean.getValue());
		}
		return(mod);
	}
	*/
	public IRFC getAddValueRFC(CiBean parent, ValueBean value) {
		log.debug("Add Value '" + value.getAlias() + "'" +
				" complex='" + value.isComplexValue() + "'" +  
				" value='" + value.getValue() + "'" +   
				" on ci '" + parent.getAlias() +"'");

		RFCAddAttribute add = new RFCAddAttribute();
		add.setAlias(value.getAlias());		
		add.setTargetAlias(parent.getAlias());
		RFCModifyAttributeValue mod = new RFCModifyAttributeValue();
		add.add(mod);
		if (value.isComplexValue()) {
			String aliasName = value.getValue();

			scope.referenceBean(parent, "Value", aliasName);

			mod.setNewValueAsAlias(aliasName);
		} else {
			mod.setNewValue(value.getValue());
		}
		return(add);
	}
	
	
	public IRFC getSetValueRFC(CiBean bean, ValueBean vBean, int index) {
		/*
		if (vBean.getId() != null) {
			return(getSetValueRFC(vBean, vBean.getId()));
		}
		*/
		RFCModifyDerivedAttributeValue modify = new RFCModifyDerivedAttributeValue();
		modify.setTargetAlias(bean.getAlias());
		if (vBean.isComplexValue()) {
			String aliasName = vBean.getValue();
			if (aliasName != null) {
				scope.referenceBean(bean, "Value", aliasName);
			}
			modify.setValueAsAlias(aliasName);
		} else {
			modify.setValue(vBean.getValue());
		}

		modify.setIndex(index);
		modify.setAlias(vBean.getAlias());
		log.debug("ModifyValue: CI=" + bean.getAlias() + " value=" + vBean.toString()); 
		return (modify);
	}

	public AttributeBean getAttributeTemplate(CiBean bean, String key) {
		
		if (bean == null) {
			log.debug("exit bean is null attr=" + key);
			return(null);
		}
		log.debug("inspect[" + bean.getAlias() +", " + bean.isTemplate() + "] attr=" + key);
		if (bean.isTemplate()) {
			AttributeBean aBean = bean.getAttribute(key);
			if (aBean != null) {
				log.debug("found[" + aBean.fetchMaxOccursAsInt() + "]attr=" + key);
				return(aBean);
			}
			CiBean remote = scope.getRemoteBean(bean.getAlias());
			if (aBean == null) {
				log.debug("not found in remote!attr=" + key);
				return(null);
			}
			aBean = bean.getAttribute(key);
			log.debug("found[" + aBean.fetchMaxOccursAsInt() +"]attr=" + key);
			return(aBean);
		}
		
		CiBean parent = scope.getLocalBean(bean.getDerivedFrom());
		if (parent == null) {
			parent = scope.getRemoteBean(bean.getDerivedFrom());
		}
		return(getAttributeTemplate(parent, key));
		
	}

	public void modifyCIAlias(CiBean left) {
		RFCModifyAlias rfc = new RFCModifyAlias();
		rfc.setTargetId(left.getId());
		rfc.setNewAlias(left.getAlias());
		if (left.isTemplate()) {
			rfcContainer.addTemplateModify(rfc);
		} else {
			rfcContainer.addInstanceModify(rfc);
		}
	}

	public void modifyDerivedFrom(CiBean left) {
		RFCMoveCi move = new RFCMoveCi();
		move.setTargetId(left.getId());
		move.setToAlias(left.getDerivedFrom());
		rfcContainer.addNewAttribute(move);
	}

	public void modifyAttributeAlias(CiBean parent, AttributeBean left) {
		RFCModifyAlias rfc = new RFCModifyAlias();
		rfc.setTargetId(left.getId());
		rfc.setNewAlias(left.getAlias());
		rfcContainer.addNewAttribute(rfc);
	}




}
