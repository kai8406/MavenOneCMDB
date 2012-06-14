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
package org.onecmdb.core.utils.graph.query;

/**
 * Specifies the order to retrive CI:s<br>
 * <br>
 * setCiAttr() specifies that order is determined by<br> 
 * internal attr in the CI, like <br>
 * <code>createTime, lastModfied, gid, description, ...</code><br>
 * see Ci.hbm.xml for full details<br>
 * <br>
 * <br>
 * attAlias specifies a attribute attached to a CI.<br>
 * attrType specifies the type of the attribute, like:<br>
 * <code>valueAsString, valueAsLong, valueAsDate</code>
 * 
 */
public class OrderInfo {
	private String attrAlias;
	private String attrType;
	private boolean descenden;
	private String ciAttr;
	
	public String getAttrAlias() {
		return attrAlias;
	}
	public void setAttrAlias(String attrAlias) {
		this.attrAlias = attrAlias;
	}
	public String getAttrType() {
		if (attrType == null || attrType.length() == 0) {
			return("valueAsString");
		}
		return attrType;
	}
	public void setAttrType(String attrType) {
		this.attrType = attrType;
	}
	public boolean isDescenden() {
		return descenden;
	}
	public void setDescenden(boolean descenden) {
		this.descenden = descenden;
	}
	public String getCiAttr() {
		return ciAttr;
	}
	public void setCiAttr(String ciAttr) {
		this.ciAttr = ciAttr;
	}

}
