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
package org.onecmdb.core.internal.ccb;

import java.util.Date;

import org.onecmdb.core.internal.ccb.rfc.RFC;

/**
 * <code>RfcQueryCriteria</code> is used to query changes on CI's in OneCMDB.<br>
 * <br>
 * Things to set restriction's on
 * <ul>
 * <li>
 * RFC-Class
 * <li>
 * fromDate()
 * <li>
 * toDate().
 * <li>
 * ascendingOrder()
 * <li>
 * descendingOrder()
 * <li>
 * maxResult()
 * <li>
 * firstResult()
 * <li>
 * fetchAttribute()
 * </ul>
 */
public class RfcQueryCriteria {
	public static String RFC_NEW_CI = "org.onecmdb.core.internal.ccb.rfc.RFCNewCi";
	public static String RFC_NEW_ATTRIBUTE = "org.onecmdb.core.internal.ccb.rfc.RFCNewAttribute";
	public static String RFC_DESTROY ="org.onecmdb.core.internal.ccb.rfc.RFCDestroy";
	public static String RFC_MODIFY_ALIAS ="org.onecmdb.core.internal.ccb.rfc.RFCModifyAlias";
	public static String RFC_MODIFY_ATTRIBUTE_TYPE ="org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeType";
	public static String RFC_MODIFY_ATTRIBUTE_REF_TYPE ="org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeReferenceType";
	public static String RFC_MODIFY_DERIVED_ATTRIBUTE ="org.onecmdb.core.internal.ccb.rfc.RFCModifyDerivedAttributeValue";
	public static String RFC_MODIFY_ATTRIBUTE_VALUE ="org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue";
	public static String RFC_MODIFY_IS_TEMPLATE ="org.onecmdb.core.internal.ccb.rfc.RFCModifyIsTemplate";
	public static String RFC_MODIFY_DESCRIPTION ="org.onecmdb.core.internal.ccb.rfc.RFCModifyDescription";
	public static String RFC_MODIFY_DISPLAYNAME_EXRESSION ="org.onecmdb.core.internal.ccb.rfc.RFCModifyDisplayNameExpression";
	public static String RFC_MODIFY_ATTRIBUTE_MAXOCCURS ="org.onecmdb.core.internal.ccb.rfc.RFCModifyMaxOccurs";
	public static String RFC_MODIFY_ATTRIBUTE_MINOCCURS ="org.onecmdb.core.internal.ccb.rfc.RFCModifyMinOccurs";

	// Which RFC type (class) should be included in the query.
	private String rfcClass = RFC.class.getName();
	// Change occured from when
	private Date fromDate;
	// Change occured until.
	private Date toDate;
	// Time order of the result
	private boolean descendingOrder;
	// How many changes should be retured.
	private Integer maxResult;
	// How many changes should be skipped.
	private Integer firstResult;
	// Query for changes on all attributes on the CI.
	private boolean fetchAttributes;
	// Query for changes on a specific attribute alias.
	private String attributeAlias;
	// Query for changes in a transaction.
	private Long txId;
	
	/**
	 * Select only rfcs of a specific class, if not set all rfcs will be fetched.
	 * <br>
	 * Rfc classes commonly used are
	 * <br>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCNewCi</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCNewAttribute</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCDestroy</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyAlias</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeType</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeReferenceType</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyDerivedAttributeValue</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyAttributeValue</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyDescription</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyIsTemplate</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyDescription</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyDisplayNameExpression</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyMaxOccurs</li>
	 * <li>org.onecmdb.core.internal.ccb.rfc.RFCModifyMinOccurs</li>
	 * @param clazz
	 */
	public void setRfcClass(String clazz) {
		this.rfcClass = clazz;
	}
	
	public String getRfcClass() {
		return rfcClass;
	}

	/**
	 * Set the from date where rfc's will be received from.
	 * If not set all rfc's will be fetch.
	 */
	public void setFromDate(Date date) {
		this.fromDate = date;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	
	
	/**
	 * Set the to date where rfc's will be received to.
	 * If not set all rfc's will be fetch.
	 */
	public void setToDate(Date date) {
		this.toDate = date;
	}

	public Date getToDate() {
		return toDate;
	}

	/**
	 * Fetch rfc's for all attributes connected to the ci, aswell
	 * as the rfc for the actuall ci.
	 * <br>
	 * <br>
	 * default is false.
	 * 
	 */
	public void setFetchAttributes(boolean fetchAttributes) {
		this.fetchAttributes = fetchAttributes;
	}
	public boolean isFetchAttributes() {
		return(fetchAttributes);
	}


	/**
	 * Retrive the RFCs in descending order, meaning that the 
	 * latest, accroding to ts, rfc will be come first in the list.
	 * <br>
	 * Only one of descending/ascending can be set to true. If
	 * both are set the descending order is used.
	 * <br>
	 * <br>
	 * default is false.
	 */
	public void setDescendingOrder(boolean descendingOrder) {
		this.descendingOrder = descendingOrder;
	}
	
	public boolean isDescendingOrder() {
		return(descendingOrder);
	}

	

	/**
	 * Set how many rfc's to skip. Used in conjuction with
	 * setMaxResult() paging can be implemented.
	 * <br>
	 * <br>
	 * default value is null, meaning that criteria is not used.   
	 * @param firstResult
	 */
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	
	public Integer getFirstResult() {
		return(this.firstResult);
	}
	/**
	 * Set how many rfc's to fetch. Used in conjuction with 
	 * setFirstResult() pageing can be implemented.
	 * <br>
	 * <br>
	 * default value is null, meaning tar all rfc's will be retrived.   
	 * @param maxResult
	 */
	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}

	public Integer getMaxResult() {
		return(maxResult);
	}
	
	/**
	 * Search rfc for a specific attribute, connected to the ci.
	 * Will enable fetching rfc's for attributes.
	 * 
	 * @param attributeAlias The attribute's alias.  
	 * @return
	 */

	public void setAttributeAlias(String attributeAlias) {
		this.attributeAlias = attributeAlias;
		setFetchAttributes(true);
	}

	public String getAttributeAlias() {
		return attributeAlias;
	}

	/**
	 * Search for RFC contained in a transaction.
	 * 
	 * @return
	 */
	public Long getTxId() {
		return txId;
	}

	public void setTxId(Long txId) {
		this.txId = txId;
	}
	
	public String getTxIdAsString() {
		if (this.txId == null) {
			return(null);
		}
		return(this.txId.toString());
	}
	
	public void setTxIdAsString(String id) {
		if (id == null) {
			return;
		}
		this.txId = Long.parseLong(id);
	}
	
	

	

	
	
}
