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
package org.onecmdb.ui.gwt.toolkit.client.view.input;

import java.util.Iterator;

import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.MultipleAttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.TooltipPopup;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class MultiValueTableWidget extends Composite {

	private MultipleAttributeValue mValue;
	private Label label;

	public MultiValueTableWidget(MultipleAttributeValue mValue) {
		this.mValue = mValue;
		label = new Label();
		label.setWordWrap(false);
		load();
		initWidget(label);
	}
	
	protected void load() {
		String value = null;
		String toolTip = "";
		
		final TooltipPopup tooltipPopup = new TooltipPopup(label, toolTip);
			
		for (Iterator iter = mValue.getAttributeValues().iterator(); iter.hasNext(); ) {
			AttributeValue aValue = (AttributeValue) iter.next();
			if (aValue.isComplex()) {
				OneCMDBConnector.getCIFromAlias(aValue.getStringValue(), new AsyncCallback() {

					public void onFailure(Throwable caught) {
						label.setTitle("Error: " + caught);
						label.setText("Error");
					}

					public void onSuccess(Object result) {
						if (result instanceof GWT_CiBean) {
							GWT_CiBean bean = (GWT_CiBean)result;
							tooltipPopup.setTooltipText(tooltipPopup.getTooltipText() + "<br>" + bean.getDisplayName());
							String text = label.getText();
							if (text == null || text.length() < (AttributeRender.MAX_TABLE_COLUMN_CHARS-3)) {
								text += " " + bean.getDisplayName();
								if (text.length() > (AttributeRender.MAX_TABLE_COLUMN_CHARS-3)) {
									text = text.substring(0, (AttributeRender.MAX_TABLE_COLUMN_CHARS-3)) + "...";
								}
								label.setText(text);
							}
						}
					}
				});
			} else {
				toolTip += aValue.getStringValue() + "<br>";
				if (value == null) {
					value = aValue.getStringValue();
				} else {
					value += ", " + aValue.getStringValue();
				}
			}
			
		}
		
		if (!mValue.isComplex()) {
			if (value != null) {
				if (value.length() > (AttributeRender.MAX_TABLE_COLUMN_CHARS-3)) {
					value = value.substring(0, (AttributeRender.MAX_TABLE_COLUMN_CHARS-3)) + "...";
				}
				label.setText(value);
			}
			//label.setTitle(toolTip);
		}
	

	}
}
