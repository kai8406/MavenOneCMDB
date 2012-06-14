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
package org.onecmdb.ui.gwt.toolkit.client.model.onecmdb;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <code>GWT_QueryQriteria</code> is used to search OneCmdb for CI's<br>
 * Is adopted for GWT messages.<br>
 * <br>
 * Supports query for
 * <ul>
 * <li>Finding offsprings</li>
 * <li>Free text search</li>
 * <br><br>
 * </ul>
 * Limit the result by search for
 * <ul>
 * <li>Text in</li>
 * <ul>
 * <li>alias</li>
 * <li>value</li>
 * <li>description</li>
 * </ul>
 * <li>template and/or instances</li>
 * <li>attributes and/or ci's</li>
 * <li>maxResult - number of items to return.</li>
 * <li>firstResult - offset to first item.</li>
 * </ul>
 * <br>  	
 *
 * @param <E>
 */
public class GWT_QueryCriteria implements IsSerializable {
	// Search for CI with specific alias.
	private String ciAlias;
	
	// Search for CI with specific Id. 
	private String ciId;
	
	// Only search for offspring's on an id.
	private String offspringOfId = null;
	
	// Only search for offspring's on an alias.
	private String offspringOfAlias = null;
		
	// match ci template  
	private boolean matchCiTemplates = false;
	
	// match ci instance 
	private boolean matchCiInstances = false;
	
	// match attribute template  
	private boolean matchAttributeTemplates = false;
	
	// match attribute instance 
	private boolean matchAttributeInstances = false;
	
	// Amount to return.
	private Integer maxResult = null;

	// First to return.
	private Integer firstResult = null;
	
	// Match on attributes
	private boolean matchAttribute = false;

	// Match on ci.
	private boolean matchCi = false;
	
	// Match a type.
	private String matchType;
	
	// Free text to search for.
	private String text = null;
	
	// Text matching on alias.
	private boolean textMatchAlias = false;
	// Text matching on value.
	private boolean textMatchValue = false;
	// Text matching on description.
	private boolean textMatchDescription = false;
	
	private Integer offspringDepth;

	private String matchCiPath;
	
	// Order info.
	private String orderAttAlias;
	private String orderType = "valueAsString";
	private boolean orderAscending;
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Criteria: <");
		buf.append("offspringsOf=" + getOffspringOfAlias());
		buf.append("aliasOf=" + getCiAlias());
		buf.append(", first=" + getFirstResult());
		buf.append(", max=" + getMaxResult());
		buf.append(", text=" + text); 
		buf.append(", matchAlias=" + textMatchAlias);
		buf.append(", matchValue=" + textMatchValue);
		buf.append(", matchDescr=" + textMatchDescription);
		buf.append(">");
		
		return(buf.toString());
	}

	
	
	public String getOrderAttAlias() {
		return orderAttAlias;
	}



	public void setOrderAttAlias(String orderAttAlias) {
		this.orderAttAlias = orderAttAlias;
	}



	public String getOrderType() {
		return orderType;
	}



	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}



	public boolean isOrderAscending() {
		return orderAscending;
	}



	public void setOrderAscending(boolean orderAscending) {
		this.orderAscending = orderAscending;
	}



	public Integer getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	public Integer getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}

	public String getOffspringOfId() {
		return offspringOfId;
	}

	public void setOffspringOfId(String offspringOfId) {
		this.offspringOfId = offspringOfId;
	}
	
	
	public String getOffspringOfAlias() {
		return offspringOfAlias;
	}

	public void setOffspringOfAlias(String offspringOfAlias) {
		this.offspringOfAlias = offspringOfAlias;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isMatchAttribute() {
		return matchAttribute;
	}

	public void setMatchAttribute(boolean matchAttribute) {
		this.matchAttribute = matchAttribute;
	}

	public boolean isMatchCi() {
		return matchCi;
	}

	public void setMatchCi(boolean matchCi) {
		this.matchCi = matchCi;
	}


	public boolean isTextMatchAlias() {
		return textMatchAlias;
	}

	public void setTextMatchAlias(boolean textMatchAlias) {
		this.textMatchAlias = textMatchAlias;
	}

	public boolean isTextMatchDescription() {
		return textMatchDescription;
	}

	public void setTextMatchDescription(boolean textMatchDescription) {
		this.textMatchDescription = textMatchDescription;
	}

	public boolean isTextMatchValue() {
		return textMatchValue;
	}

	public void setTextMatchValue(boolean textMatchValue) {
		this.textMatchValue = textMatchValue;
	}

	public boolean isMatchAttributeInstances() {
		return matchAttributeInstances;
	}

	public void setMatchAttributeInstances(boolean matchAttributeInstances) {
		this.matchAttributeInstances = matchAttributeInstances;
	}

	public boolean isMatchAttributeTemplates() {
		return matchAttributeTemplates;
	}

	public void setMatchAttributeTemplates(boolean matchAttributeTemplates) {
		this.matchAttributeTemplates = matchAttributeTemplates;
		setMatchCi(true);
	}

	public boolean isMatchCiInstances() {
		return matchCiInstances;
	}

	public void setMatchCiInstances(boolean matchCiInstances) {
		this.matchCiInstances = matchCiInstances;
		setMatchCi(true);
	}

	public boolean isMatchCiTemplates() {
		return matchCiTemplates;
	}

	public void setMatchCiTemplates(boolean matchCiTemplates) {
		this.matchCiTemplates = matchCiTemplates;
	}

	public String getCiAlias() {
		return ciAlias;
	}

	public void setCiAlias(String ciAlias) {
		this.ciAlias = ciAlias;
	}

	public String getCiId() {
		return ciId;
	}

	public void setCiId(String ciId) {
		this.ciId = ciId;
	}
	
	public Integer getOffspringDepth() {
		return(this.offspringDepth);
	}
	
	public void setOffspringDepth(Integer depth) {
		this.offspringDepth = depth;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public void setMatchCiPath(String path) {
		this.matchCiPath = path;
	}
	
	public String getMatchCiPath() {
		return(this.matchCiPath);
	}
	
}
