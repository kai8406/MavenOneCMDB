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
package org.onecmdb.ui.gwt.toolkit.client.view.tree;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QueryCriteriaWidget extends Composite {
	
	private CITreeWidget treeWidget;

	public QueryCriteriaWidget(CITreeWidget widget) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("100%");
		this.treeWidget = widget;
		
		// Create input form....
		//List l = new ArrayList();
		final TextBox text = new TextBox();
		text.setWidth("100%");
		Label l = new Label("Search");
		panel.clear();
		panel.add(l);
		panel.add(text);
		panel.setCellWidth(text, "100%");
		text.addKeyboardListener(new KeyboardListener() {

			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				// TODO Auto-generated method stub
				
			}

			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if (keyCode == KeyboardListener.KEY_ENTER) {
					treeWidget.getTreeControl().setSearchText(text.getText());
					treeWidget.reload();
				}
			}

			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				// TODO Auto-generated method stub
				
			}
			
		});
		/*
		l.add(new TextBoxInputItem(new TextBoxControl("Search", null, false), new ChangeListener() {

			public void onChange(Widget sender) {
				if (sender instanceof InputItem) {
					InputItem item = (InputItem)sender;
					crit.setText((String)item.getValue());
					crit.setTextMatchAlias(true);
					crit.setTextMatchDescription(true);
					crit.setTextMatchValue(true);
					
					treeWidget.reload();
				}
			}
		}));
		
		InputFormWidget input = new InputFormWidget(l);
		*/
		initWidget(panel);
	}

}
