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
package org.onecmdb.ui.gwt.modeller.client.view.navigation;

import org.gwtiger.client.widget.ScreenMenuItem;
import org.onecmdb.ui.gwt.modeller.client.OneCMDBModelCreator;
import org.onecmdb.ui.gwt.modeller.client.control.ModelInheritanceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.ChangeTreeRootTree;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

public class ModelCreatorNavigation extends OneCMDBBaseScreen implements ClickListener {
	private Tree treePanel = new Tree();

	public ModelCreatorNavigation() {
		ScreenObjectTypeMenuItem mainMenu = new ScreenObjectTypeMenuItem(
				createHeaderHTML("images/designer/model-designer_32.gif", "Model Designer"),
				false,
				OneCMDBModelCreator.SHOW_STATIC_CONTENT,
				"static/welcome_model_designer.html");
		
		TreeItem mainItem = addItem(treePanel, mainMenu);
		
		ScreenObjectTypeMenuItem templateMenu = new ScreenObjectTypeMenuItem(
				createHeaderHTML("images/designer/template-hierachy_32.gif", "Template Hierarchy"),
				false,
				OneCMDBModelCreator.SHOW_STATIC_CONTENT,
				"static/welcome_template_hierarchy.html");
		TreeItem templateHierachyItem = addItem(mainItem, templateMenu);
		addTemlateTreeHock(templateHierachyItem);
		
		
		// Transform menu.
		ScreenObjectTypeMenuItem transformMenu = new ScreenObjectTypeMenuItem(
				createHeaderHTML("images/designer/template-hierachy_32.gif", "Instance Import Control"),
				false,
				OneCMDBModelCreator.SHOW_STATIC_CONTENT,
				"static/welcome_template_hierarchy.html");
		TreeItem transformItem = addItem(mainItem, transformMenu);
		
		ScreenMenuItem newTransformMenu = new ScreenMenuItem(
				createHTML("images/designer/template-hierachy_16.gif", "New Transform"),
				false,
				OneCMDBModelCreator.NEW_TRANSFORM_SCREEN
				);
		
		addItem(transformItem, newTransformMenu);
		
		ScreenMenuItem listTransformMenu = new ScreenMenuItem(
				createHTML("images/designer/template-hierachy_16.gif", "List Transforms"),
				false,
				OneCMDBModelCreator.LIST_TRANSFORM_SCREEN);
				
		
		addItem(transformItem, listTransformMenu);
		
		initWidget(treePanel);

	}
	
	private void addTemlateTreeHock(TreeItem templateHierachyItem) {
		templateHierachyItem.addItem("Loading....");
		//treePanel.addItem(itil);
		treePanel.setStyleName("mdv-form");
		
		ModelInheritanceTreeControl control = new ModelInheritanceTreeControl();
		control.setFilterInstances(Boolean.TRUE);
		control.setRootState(true);
		// Dummy, so the ci is selectable...
		control.setClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				// TODO Auto-generated method stub
				
			}
			
		});
		control.setTreeListener(new TreeListener() {

			public void onTreeItemSelected(TreeItem item) {
				// Show a list of that template.
				Object data = item.getUserObject();
				if (data instanceof GWT_CiBean) {
					OneCMDBModelCreator.get().showScreen(OneCMDBModelCreator.TEMPLATE_VIEW_SCREEN, 
							((GWT_CiBean)data).getAlias(), new Long(0));
				}
			}

			public void onTreeItemStateChanged(TreeItem item) {
			}
			
		});
	
		ChangeTreeRootTree templateTree = new ChangeTreeRootTree(treePanel, control);
		templateTree.setTriggerItem(templateHierachyItem);
	}

	public boolean isRightPanel() {
		return(false);
	}
	
	private TreeItem addItem(Tree tree, ScreenMenuItem widget) {
		TreeItem childItem = new TreeItem();
		childItem.setWidget(widget);
		tree.addItem(childItem);

		widget.addClickListener(this);

		return(childItem);
	}

	private TreeItem addItem(TreeItem parentItem, ScreenMenuItem widget) {
		TreeItem childItem = new TreeItem();
		childItem.setWidget(widget);
		parentItem.addItem(childItem);

		widget.addClickListener(this);
		return(childItem);
	}
	/**
	 * Creates an HTML fragment that places an image & caption together, for use
	 * in a group header.
	 * 
	 * @param imageUrl the url of the icon image to be used
	 * @param caption the group caption
	 * @return the header HTML fragment
	 */
	private String createHeaderHTML(String imageUrl, String caption) {
		return "<table align='left'><tr>" + "<td><img src='" + imageUrl + "'></td>"
		+ "<td style='vertical-align:middle'><b style='white-space:nowrap'><a href='javascript:;'>"
		+ caption + "</a></b></td>" + "</tr></table>";
	}
	
	private String createHTML(String imageURL, String caption) {
		  return "<table align='left'><tr><td><img src='" + imageURL + "'></td>" 
	      + "<td style='vertical-align:middle'><a style='white-space:nowrap' href='javascript:;'>"
	      + caption + "</a></td>" + "</tr></table>";
		  /*
		  return "<a style='white-space:nowrap' href='javascript:;'>"
		      + caption + "</a>";
		   */
	 }	
	private String createHTML(String caption) {
		return "<table align='left'><tr>" 
		+ "<td style='vertical-align:middle'><a style='white-space:nowrap' href='javascript:;'>"
		+ caption + "</a></td>" + "</tr></table>";
		/*
		  return "<a style='white-space:nowrap' href='javascript:;'>"
		      + caption + "</a>";
		 */
	}	


	private String createHTMLWithTable(String caption) {
		return "<table align='left'><tr>" 
		+ "<td><img src='mdv-menu.gif'></td><td style='vertical-align:middle'><a href='javascript:;'>"
		+ caption + "</a></td>" + "</tr></table>";
	}

	public void onClick(Widget sender) {
		try	{
			if (sender instanceof ScreenObjectTypeMenuItem) {
				ScreenObjectTypeMenuItem screen= (ScreenObjectTypeMenuItem)sender;
				getBaseEntryScreen().showScreen(screen.getScreenIndex(), screen.getObjectType(), new Long(0));
				return;
			}
			if (sender instanceof ScreenMenuItem) {
				ScreenMenuItem screen= (ScreenMenuItem)sender;
				getBaseEntryScreen().showScreen(screen.getScreenIndex());
				return;
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}

	}
	class ScreenObjectTypeMenuItem extends ScreenMenuItem {

		private String objectType;

		public ScreenObjectTypeMenuItem(String html, boolean wordWrap, int screenIndex, String objectType) {
			super(html, wordWrap, screenIndex);
			this.objectType = objectType;
		}

		public String getObjectType() {
			return(this.objectType);
		}

	}
	
}
