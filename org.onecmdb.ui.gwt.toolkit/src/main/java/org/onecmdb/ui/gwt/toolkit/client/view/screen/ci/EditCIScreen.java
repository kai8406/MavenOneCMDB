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
package org.onecmdb.ui.gwt.toolkit.client.view.screen.ci;

import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;



public class EditCIScreen extends NewCIScreen {
	
	public EditCIScreen() {
		super();
		this.setTitleText("Edit Instance");
	}

	
	protected boolean isNew() {
		return(false);
	}

	
	protected void onCommitSuccess(Object result) {
		// TODO Auto-generated method stub
		super.onCommitSuccess(result);
	}

	protected void onCommitFailure(Throwable caught) {
		// TODO Auto-generated method stub
		super.onCommitFailure(caught);
	}
	
	public void onLoadComplete(Object sender) {
		super.onLoadComplete(sender);
		
		setTitleText("Edit");
		
	}
	
	
	
	/*
	private void updateValues(GWT_CiBean mod, List values) {
		List valueMap = new ArrayList();
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			GWT_ValueBean aValue = (GWT_ValueBean) iter.next();
			valueMap.add(aValue.getAlias());
		}
		for (Iterator iter = valueMap.iterator(); iter.hasNext();) {
			String alias = (String) iter.next();
			mod.removeAttributeValues(alias);
		}
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			GWT_ValueBean aValue = (GWT_ValueBean) iter.next();
			mod.addAttributeValue(aValue);	
		}
	}
	*/
	

}
