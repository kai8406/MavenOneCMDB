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
package org.onecmdb.core.utils.graph.query.selector;

import java.util.List;



public class ItemRelationSelector extends ItemSelector {
	
	private String target;
	private String source;
	private List<Long> sourceRange;
	private List<Long> targetRange;
	private boolean mandatory = true;
	private String sourceAttribute;
	
	
	public ItemRelationSelector(String id, String template, String target, String source) {
		super(id, template);
		setTarget(target);
		setSource(source);
	}
	
	public ItemRelationSelector() {
		super();
	}

	
	public boolean reference() {
		return(true);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setSourceRange(List<Long> ids) {
		this.sourceRange = ids;
		
	}

	public void setTargetRange(List<Long> ids) {
		this.targetRange = ids;
	}

	public List<Long> getSourceRange() {
		return sourceRange;
	}

	public List<Long> getTargetRange() {
		return targetRange;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * Set if this relation is mandatory, default=true. If true only instances 
	 * that has the relation is retrurned, else all instances will be returned.
	 * @param mandatory
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getSourceAttribute() {
		return sourceAttribute;
	}

	public void setSourceAttribute(String sourceAttribute) {
		this.sourceAttribute = sourceAttribute;
	}
	
	
	
}
