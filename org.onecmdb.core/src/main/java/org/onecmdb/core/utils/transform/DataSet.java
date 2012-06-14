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
package org.onecmdb.core.utils.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onecmdb.core.utils.bean.CiBean;

/**
 * Class <code>DataSet</code> is the place holder for the <br/>
 * instance selector and all attribute selectors.<br/>
 * <br/>
 * The DataSet can also have a parent that will reflect a inherited template.</br>
 * In this way the definition of a super template only has to defined in one place.</br>
 *
 * 
 * 
 * @author Niklas
 *
 */
public class DataSet extends ANameObject implements Cloneable {
	private IInstanceSelector instanceSelector;
	private List<IAttributeSelector> attributeSelectors = new ArrayList<IAttributeSelector>();
	private IDataSource dataSource;
	private DataSet parent;
	private TransformReport report; 
	
	
	public void addAttributeSelector(IAttributeSelector selector) {
		this.attributeSelectors.add(selector);
	}
	
	public void setAttributeSelector(List<IAttributeSelector> colSelectors) {
		for (IAttributeSelector sel : colSelectors) {
			addAttributeSelector(sel);
		}
	}
	
	public List<IAttributeSelector> getAttributeSelector() {
		List<IAttributeSelector> aSel = new ArrayList<IAttributeSelector>();
		if (this.parent != null) {
			aSel.addAll(this.parent.getAttributeSelector());
		}
		aSel.addAll(this.attributeSelectors);
		return(aSel);
	}

	
	public IInstanceSelector getInstanceSelector() {
		return instanceSelector;
	}
	
	public void setInstanceSelector(IInstanceSelector rowSelector) {
		this.instanceSelector = rowSelector;
	}
	
	public IAttributeSelector getAttributeSelectorWithName(String attrName) {
		if (attrName == null) {
			return(null);
		}
		for (IAttributeSelector aSel : getAttributeSelector()) {
			if (attrName.equals(aSel.getName())) {
				return(aSel);
			}
		}
		return(null);
	}

	
	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<IInstance> getInstances() throws IOException {
		List<IInstance> rows = getInstanceSelector().getInstances(this);
		return(rows);
	}

	@Override
	public String toString() {
		return("DataSet name=" + getName());
	}

	@Override
	public DataSet clone() throws CloneNotSupportedException {
		return ((DataSet)super.clone());
	}

	public List<IAttributeSelector> getNaturalKeys() {
		List<IAttributeSelector> aSelList = new ArrayList<IAttributeSelector>();
		for (IAttributeSelector aSel : getAttributeSelector()) {
			if (aSel.isNaturalKey()) {
				aSelList.add(aSel);
			}
		}
		return(aSelList);
	}

	public void setParent(DataSet ds) {
		this.parent = ds;
	}

	public void setReport(TransformReport report) {
		this.report = report;
	}
	
	public TransformReport getReport() {
		return(this.report);
	}


	

	
	

}
