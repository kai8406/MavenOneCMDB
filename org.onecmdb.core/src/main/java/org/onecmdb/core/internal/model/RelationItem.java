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
package org.onecmdb.core.internal.model;

public class RelationItem extends ConfigurationItem {
	
	private Long sourceId;
	private Long targetId;
	private String sourceTemplatePath;
	private String targetTemplatePath;
	private Long sourceAttributeId;
	
	
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Long getTargetId() {
		return targetId;
	}
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	public String getSourceTemplatePath() {
		return sourceTemplatePath;
	}
	public void setSourceTemplatePath(String sourceTemplatePath) {
		this.sourceTemplatePath = sourceTemplatePath;
	}
	public String getTargetTemplatePath() {
		return targetTemplatePath;
	}
	public void setTargetTemplatePath(String targetTemplatePath) {
		this.targetTemplatePath = targetTemplatePath;
	}
	public Long getSourceAttributeId() {
		return sourceAttributeId;
	}
	public void setSourceAttributeId(Long sourceAttributeId) {
		this.sourceAttributeId = sourceAttributeId;
	}
	
	
	@Override
	protected String toString(int level, int maxLevel) {
		StringBuffer buf = new StringBuffer();
		buf.append("[" + sourceId +"-->" + targetId + "] ");
		buf.append(super.toString(level, maxLevel));
		return(buf.toString());
	}
	
	
	
	
}
