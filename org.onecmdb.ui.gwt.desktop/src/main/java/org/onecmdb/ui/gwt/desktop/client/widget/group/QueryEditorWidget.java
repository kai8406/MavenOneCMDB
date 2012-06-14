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
package org.onecmdb.ui.gwt.desktop.client.widget.group;

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.widget.group.graph.GWT_GraphQuery;
import org.onecmdb.ui.gwt.desktop.client.widget.group.graph.GWT_GraphTreeItem;
import org.onecmdb.ui.gwt.desktop.client.widget.group.graph.GWT_XMLQueryGraphParser;
import org.onecmdb.ui.gwt.desktop.client.widget.group.graph.QueryGraphTree;
import org.onecmdb.ui.gwt.desktop.client.widget.group.graph.QueryGraphTreeBuilder;

import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.TextArea;

public class QueryEditorWidget extends LayoutContainer {

	
	private List<String> roots;
	private ContentData mdr;
	private CMDBPermissions permission;

	public QueryEditorWidget(ContentData mdr, List<String> roots) {
		this.mdr = mdr;
		this.roots = roots;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		initUI();
	}

	protected void initUI() {
		// Temple browser...
		// Template Reference browser.
		setLayout(new FitLayout());
		
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
	
		TabPanel tab = new TabPanel();
		panel.add(tab);
		tab.setTabPosition(TabPanel.TabPosition.BOTTOM);
		
		TabItem treeItem = new TabItem("Design");
		TabItem xmlItem = new TabItem("Source");
		tab.add(treeItem);
		tab.add(xmlItem);
		
		final QueryGraphTree tree = new QueryGraphTree();		
		treeItem.add(tree);	
		
		final TextArea area = new TextArea();
		ContentPanel sourcePanel = new ContentPanel();
		sourcePanel.setHeaderVisible(false);
		sourcePanel.setLayout(new FitLayout());
		sourcePanel.add(area);
		ToolBar bar = new ToolBar();
		bar.add(new TextToolItem("Update", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				String text = area.getText();
				GWT_GraphQuery query = GWT_XMLQueryGraphParser.parse(text);
				GWT_GraphTreeItem item = QueryGraphTreeBuilder.buildTree(query);
				tree.setGraphTreeItem(item);
			}
			
		}));
		sourcePanel.setTopComponent(bar);
		xmlItem.setLayout(new FitLayout());
		xmlItem.add(sourcePanel);
		
		add(panel);
		layout();
	}

	public void setPermission(CMDBPermissions permissions) {
		this.permission = permissions;
		
	}
}
