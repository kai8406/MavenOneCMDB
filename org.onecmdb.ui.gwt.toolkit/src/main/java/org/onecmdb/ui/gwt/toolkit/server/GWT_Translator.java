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
package org.onecmdb.ui.gwt.toolkit.server;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.IRfcResult;
import org.onecmdb.core.internal.authorization.RBACSession;
import org.onecmdb.core.internal.authorization.Role;
import org.onecmdb.core.internal.model.QueryCriteria;
import org.onecmdb.core.utils.bean.AttributeBean;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.bean.ValueBean;
import org.onecmdb.core.utils.wsdl.RFCBean;
import org.onecmdb.core.utils.wsdl.TransactionBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_QueryCriteria;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RBACSession;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RFCBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_TransactionBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;

public class GWT_Translator {
	
	// Convert between 
	public static CiBean convert(GWT_CiBean gwtBean) {
		if (gwtBean == null) {
			return(null);
		}
		CiBean bean = new CiBean();
		// Using reflection???
		// Noop not now...
		
		// Internal states
		bean.setAlias(gwtBean.getAlias());
		bean.setId(gwtBean.getId());
		bean.setDerivedFrom(gwtBean.getDerivedFrom());
		bean.setDescription(gwtBean.getDescription());
		bean.setDisplayName(gwtBean.getDisplayName());
		bean.setDisplayNameExpression(gwtBean.getDisplayNameExpression());
		bean.setTemplate(gwtBean.isTemplate());
		bean.setGroup(gwtBean.getGroup());
		
		// Attributes.
		for (GWT_AttributeBean aBean : (List<GWT_AttributeBean>)gwtBean.getAttributes()) {
				bean.addAttribute(convert(aBean));
		}
		
		// Values..
		for (GWT_ValueBean vBean: (List<GWT_ValueBean>)gwtBean.getAttributeValues()) {
			bean.addAttributeValue(convert(vBean));
		}
		
		return(bean);
	}

	private static ValueBean convert(GWT_ValueBean value) {
		if (value == null) {
			return(null);
		}
		ValueBean vBean = new ValueBean();
		vBean.setAlias(value.getAlias());
		vBean.setComplexValue(value.isComplexValue());
		vBean.setId(value.getId());
		vBean.setValueBean(convert(value.getValueBean()));
		vBean.setValue(value.getValue());
		return(vBean);
	}
	
	private static AttributeBean convert(GWT_AttributeBean attr) {
		if (attr == null) {
			return(null);
		}
		AttributeBean aBean = new AttributeBean();
		aBean.setAlias(attr.getAlias());
		aBean.setComplexType(attr.isComplexType());
		aBean.setDerived(attr.isDerived());
		aBean.setDescription(attr.getDescription());
		aBean.setDisplayName(attr.getDisplayName());
		aBean.setId(attr.getId());
		aBean.setIdAsString(attr.getIdAsString());
		aBean.setMaxOccurs(attr.getMaxOccurs());
		aBean.setMinOccurs(attr.getMinOccurs());
		aBean.setRefType(attr.getRefType());
		aBean.setType(attr.getType());
		return(aBean);
	}
	
	public static QueryCriteria convert(GWT_QueryCriteria gwtCrit) {
		if (gwtCrit == null) {
			return(null);
		}
		QueryCriteria crit = new QueryCriteria();
		crit.setCiAlias(gwtCrit.getCiAlias());
		crit.setCiId(gwtCrit.getCiId());
		crit.setFirstResult(gwtCrit.getFirstResult());
		crit.setMatchAttribute(gwtCrit.isMatchAttribute());
		crit.setMatchAttributeInstances(gwtCrit.isMatchAttributeInstances());
		crit.setMatchAttributeTemplates(gwtCrit.isMatchAttributeTemplates());
		crit.setMatchCi(gwtCrit.isMatchCi());
		crit.setMatchCiInstances(gwtCrit.isMatchCiInstances());
		crit.setMatchCiTemplates(gwtCrit.isMatchCiTemplates());
		crit.setMaxResult(gwtCrit.getMaxResult());
		crit.setOffspringOfAlias(gwtCrit.getOffspringOfAlias());
		crit.setOffspringOfId(gwtCrit.getOffspringOfId());
		crit.setText(gwtCrit.getText());
		crit.setTextMatchAlias(gwtCrit.isTextMatchAlias());
		crit.setTextMatchDescription(gwtCrit.isTextMatchDescription());
		crit.setTextMatchValue(gwtCrit.isTextMatchValue());
		crit.setOffspringDepth(gwtCrit.getOffspringDepth());
		crit.setMatchType(gwtCrit.getMatchType());
		crit.setMatchCiPath(gwtCrit.getMatchCiPath());
		
		crit.setOrderAscending(gwtCrit.isOrderAscending());
		crit.setOrderAttAlias(gwtCrit.getOrderAttAlias());
		crit.setOrderType(gwtCrit.getOrderType());
		
		
		return(crit);
	}


	// Convert between 
	public static GWT_CiBean convert(CiBean gwtBean) {
		if (gwtBean == null) {
			return(null);
		}
		GWT_CiBean bean = new GWT_CiBean();
		// Using reflection???
		// Noop not now...
		
		// Internal states
		bean.setAlias(gwtBean.getAlias());
		bean.setId(gwtBean.getId());
		bean.setDerivedFrom(gwtBean.getDerivedFrom());
		bean.setDescription(gwtBean.getDescription());
		bean.setDisplayName(gwtBean.getDisplayName());
		bean.setDisplayNameExpression(gwtBean.getDisplayNameExpression());
		bean.setTemplate(gwtBean.isTemplate());
		bean.setGroup(gwtBean.getGroup());
		bean.setLastModified(gwtBean.getLastModified());
		bean.setCreateDate(gwtBean.getCreateDate());

		// Attributes.
		for (AttributeBean aBean : gwtBean.getAttributes()) {
				bean.addAttribute(convert(aBean));
		}
		
		// Values..
		for (ValueBean vBean: gwtBean.getAttributeValues()) {
			bean.addAttributeValue(convert(vBean));
		}
		
		return(bean);
	}

	private static GWT_ValueBean convert(ValueBean value) {
		if (value == null) {
			return(null);
		}
		GWT_ValueBean vBean = new GWT_ValueBean();
		vBean.setAlias(value.getAlias());
		vBean.setComplexValue(value.isComplexValue());
		//vBean.setId(value.getId());
		vBean.setIdAsString(value.getIdAsString());
		vBean.setValueBean(convert(value.getValueBean()));
		vBean.setValue(value.getValue());
		vBean.setLastModified(value.getLastModified());
	
		return(vBean);
	}
	
	public static GWT_AttributeBean convert(AttributeBean attr) {
		if (attr == null) {
			return(null);
		}
		GWT_AttributeBean aBean = new GWT_AttributeBean();
		aBean.setAlias(attr.getAlias());
		aBean.setComplexType(attr.isComplexType());
		aBean.setDerived(attr.isDerived());
		aBean.setDescription(attr.getDescription());
		aBean.setDisplayName(attr.getDisplayName());
		aBean.setId(attr.getId());
		aBean.setMaxOccurs(attr.getMaxOccurs());
		aBean.setMinOccurs(attr.getMinOccurs());
		aBean.setRefType(attr.getRefType());
		aBean.setType(attr.getType());
		aBean.setLastModified(attr.getLastModified());
		aBean.setCreateDate(attr.getCreateDate());
		
		
		return(aBean);
	}
	
	public static GWT_QueryCriteria convert(QueryCriteria gwtCrit) {
		GWT_QueryCriteria crit = new GWT_QueryCriteria();
		crit.setCiAlias(gwtCrit.getCiAlias());
		crit.setCiId(gwtCrit.getCiId());
		crit.setFirstResult(gwtCrit.getFirstResult());
		crit.setMatchAttribute(gwtCrit.isMatchAttribute());
		crit.setMatchAttributeInstances(gwtCrit.isMatchAttributeInstances());
		crit.setMatchAttributeTemplates(gwtCrit.isMatchAttributeTemplates());
		crit.setMatchCi(gwtCrit.isMatchCi());
		crit.setMatchCiInstances(gwtCrit.isMatchCiInstances());
		crit.setMatchCiTemplates(gwtCrit.isMatchAttributeTemplates());
		crit.setMaxResult(gwtCrit.getMaxResult());
		crit.setOffspringOfAlias(gwtCrit.getOffspringOfAlias());
		crit.setOffspringOfId(gwtCrit.getOffspringOfId());
		crit.setText(gwtCrit.getText());
		crit.setTextMatchAlias(gwtCrit.isTextMatchAlias());
		crit.setTextMatchDescription(gwtCrit.isTextMatchDescription());
		crit.setTextMatchValue(gwtCrit.isTextMatchValue());
		crit.setOffspringDepth(gwtCrit.getOffspringDepth());
		crit.setMatchType(gwtCrit.getMatchType());
		crit.setMatchCiPath(gwtCrit.getMatchCiPath());
		
		crit.setOrderAscending(gwtCrit.isOrderAscending());
		crit.setOrderAttAlias(gwtCrit.getOrderAttAlias());
		crit.setOrderType(gwtCrit.getOrderType());
	
		return(crit);
	}

	
	public static GWT_CiBean[] convert(CiBean[] beans) {
		if (beans == null) {
			return(null);
		}
		GWT_CiBean gwtBeans[] = new GWT_CiBean[beans.length];
		int index = 0;
		for (CiBean bean: beans) {
			gwtBeans[index++] = convert(bean);
		}
		return(gwtBeans);
	}

	public static CiBean[] convert(GWT_CiBean[] beans) {
		if (beans == null) {
			return(null);
		}
		CiBean gwtBeans[] = new CiBean[beans.length];
		int index = 0;
		for (GWT_CiBean bean: beans) {
			gwtBeans[index++] = convert(bean);
		}
		return(gwtBeans);
	}

	public static GWT_RfcResult convert(IRfcResult from) {
		GWT_RfcResult to = new GWT_RfcResult();
		to.setRejectCause(from.getRejectCause());
		to.setRejected(from.isRejected());
		to.setTxId(from.getTxId());
		
		return(to);
		
	}

	public GWT_RFCBean convert(RFCBean from) {
		if (from == null) {
			return(null);
		}
		GWT_RFCBean to = new GWT_RFCBean();
		to.setId(from.getId());
		to.setIssuer(from.getIssuer());
		to.setSummary(from.getSummary());
		to.setTargetCIId(from.getTargetCIId());
		to.setTargetId(from.getTargetId());
		to.setTransactionId(from.getTransactionId());
		to.setTs(from.getTs());
	
		return(to);
	}

	public GWT_TransactionBean convert(TransactionBean from) {
		if (from == null) {
			return(null);
		}
		GWT_TransactionBean to = new GWT_TransactionBean();
		to.setBeginTs(from.getBeginTs());
		to.setCiAdded(from.getCiAdded());
		to.setCiDeleted(from.getCiDeleted());
		to.setCiModified(from.getCiModified());
		to.setEndedTs(from.getEndedTs());
		to.setId(from.getId());
		to.setInsertTs(from.getInsertTs());
		to.setIssuer(from.getIssuer());
		to.setName(from.getName());
		to.setRejectCause(from.getRejectCause());
		to.setStatus(from.getStatus());
		
		return(to);
		
	}

	public static GWT_RBACSession convert(RBACSession rbac) {
		GWT_RBACSession gwtRBAC = new GWT_RBACSession();
		
		gwtRBAC.setWrite(rbac.canWrite());
		
		List<String> roleNames = new ArrayList<String>();
		for (Role r : rbac.getRoles()) {
			roleNames.add(r.getName());
		}
		gwtRBAC.setRoles(roleNames);
		
		for (String group : rbac.groupNames()) {
			if (rbac.canRead(group)) {
				gwtRBAC.addRead(group);
			}
			if (rbac.canWrite(group)) {
				gwtRBAC.addWrite(group);
			}
			if (rbac.canCreate(group)) {
				gwtRBAC.addCreate(group);
			}
			if (rbac.canDelete(group)) {
				gwtRBAC.addDelete(group);
			}
		}
		
		return(gwtRBAC);
	}
	
	
}
