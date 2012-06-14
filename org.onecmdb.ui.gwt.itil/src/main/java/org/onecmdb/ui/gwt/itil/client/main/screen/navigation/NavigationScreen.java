/*
 * Copyright 2007 Aditya Kapur <addy AT gwtiger.org>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onecmdb.ui.gwt.itil.client.main.screen.navigation;

import org.gwtiger.client.screen.BaseScreen;
import org.gwtiger.client.widget.ScreenMenuItem;
import org.onecmdb.ui.gwt.itil.client.ITILApplication;
import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.control.tree.InheritanceTreeControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.tree.ChangeTreeRootTree;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;


public class NavigationScreen extends OneCMDBBaseScreen implements ClickListener{ 
	private Tree treePanel = new Tree();

	private ScreenMenuItem newIncidentScreen = new ScreenMenuItem(createHTML("images/incident_16.gif", "New Incident"), ITILApplication.NEW_INCDIENT_SCREEN);
	private ScreenMenuItem monitorIncidentScreen = new ScreenMenuItem(createHTML("images/incident_16.gif", "List Incidents"), ITILApplication.LIST_INCDIENT_SCREEN);
	private ScreenMenuItem groupMonitorIncidentScreen = new ScreenMenuItem(createHTML("images/incident_16.gif", "List Incidents by Status"), ITILApplication.GROUP_LIST_INCDIENT_SCREEN);

	private ScreenMenuItem newProblemScreen = new ScreenMenuItem(createHTML("images/problem_16.gif", "New Problem"), ITILApplication.NEW_PROBLEM_SCREEN);
	private ScreenMenuItem monitorProblemScreen = new ScreenMenuItem(createHTML("images/problem_16.gif", "List Problems"), ITILApplication.LIST_PROBLEM_SCREEN);
	private ScreenMenuItem groupMonitorProblemScreen = new ScreenMenuItem(createHTML("images/problem_16.gif", "List Problems by Status"), ITILApplication.GROUP_LIST_PROBLEM_SCREEN);

	
	
	// TREE
	// ITIL
	//   Operation 
	//     Incident Management
	// 			New
	//			List
	//			Edit
	//     Problem Management
	//			New
	//			List
	//			Edit
	public NavigationScreen()	{
		
		/*
		newIncidentScreen.addClickListener(this);
		monitorIncidentScreen.addClickListener(this);
		groupMonitorIncidentScreen.addClickListener(this);
		newProblemScreen.addClickListener(this);
		monitorProblemScreen.addClickListener(this);
		groupMonitorProblemScreen.addClickListener(this);
		*/
		
		ScreenObjectTypeMenuItem itilMain = new ScreenObjectTypeMenuItem(
				createHeaderHTML("images/ITIL/itil_32.gif", "ITIL Applications"),
				false,
				ITILApplication.SHOW_STATIC_CONTENT,
				"static/welcome_itil_applications.html");
		
		TreeItem itil = addItem(treePanel, itilMain);
		
		ScreenObjectTypeMenuItem operationMenu = new ScreenObjectTypeMenuItem(
				createHeaderHTML("images/ITIL/service-operation_32.gif", "Service Operation"),
				false,
				ITILApplication.SHOW_STATIC_CONTENT,
				"static/welcome_operation.html");
		TreeItem operationItem = addItem(itil, operationMenu);
	
		ScreenObjectTypeMenuItem transitionMenu = new ScreenObjectTypeMenuItem(
				createHeaderHTML("images/ITIL/service-transition_32.gif", "Service Transition"),
				false,
				ITILApplication.SHOW_STATIC_CONTENT,
				"static/welcome_transition.html");
		TreeItem transitionItem = addItem(itil, transitionMenu);
		
		
		/**
		 * Incident navigation
		 */
		ScreenObjectTypeMenuItem incidentMenu = new ScreenObjectTypeMenuItem(
				createHTML("images/incident_16.gif", "Incident Management"),
				false,
				ITILApplication.SHOW_STATIC_CONTENT,
				"static/welcome_incident.html");
		TreeItem incidentItem = addItem(operationItem, incidentMenu);
		
		addItem(incidentItem, newIncidentScreen);
		addItem(incidentItem, monitorIncidentScreen);
		addItem(incidentItem, groupMonitorIncidentScreen);
		
		/*
		TreeItem newIncidentItem = new TreeItem();
		newIncidentItem.setWidget(newIncidentScreen);
		incidentItem.addItem(newIncidentItem);
		
		TreeItem monitorIncidentItem = new TreeItem();
		monitorIncidentItem.setWidget(monitorIncidentScreen);
		incidentItem.addItem(monitorIncidentItem);
		
		TreeItem groupMonitorIncidentItem = new TreeItem();
		groupMonitorIncidentItem.setWidget(groupMonitorIncidentScreen);
		incidentItem.addItem(groupMonitorIncidentItem);
		*/
		/**
		 * Problem navigation
		 */
		ScreenObjectTypeMenuItem problemMenu = new ScreenObjectTypeMenuItem(
				createHTML("images/problem_16.gif", "Problem Management"),
				false,
				ITILApplication.SHOW_STATIC_CONTENT,
				"static/welcome_problem.html");
		TreeItem problemItem = addItem(operationItem, problemMenu);
		
		addItem(problemItem, newProblemScreen);
		addItem(problemItem, monitorProblemScreen);
		addItem(problemItem, groupMonitorProblemScreen);
		
		/*
		TreeItem newProblemItem = new TreeItem();
		newProblemItem.setWidget(newProblemScreen);
		problemItem.addItem(newProblemItem);
		
		TreeItem monitorProblemItem = new TreeItem();
		monitorProblemItem.setWidget(monitorProblemScreen);
		problemItem.addItem(monitorProblemItem);
		
		TreeItem groupMonitorProblemItem = new TreeItem();
		groupMonitorProblemItem.setWidget(groupMonitorProblemScreen);
		problemItem.addItem(groupMonitorProblemItem);
		*/
		/**
		 * Configuration Management
		 */
		ScreenObjectTypeMenuItem configurationMenu = new ScreenObjectTypeMenuItem(
				createHTML("Configuration Management"),
				false,
				ITILApplication.SHOW_STATIC_CONTENT,
				"static/welcome_configuration.html");
		
		TreeItem configItem = addItem(transitionItem, configurationMenu);
		
		/*
		TreeItem configItem = new TreeItem();
		configItem.setWidget(new Label("Configuration Management"));
		transitionItem.addItem(configItem);
		*/
		ScreenObjectTypeMenuItem assetMenu = new ScreenObjectTypeMenuItem(
				createHTML("Assets"),
				false,
				ITILApplication.SHOW_STATIC_CONTENT,
				"static/welcome_asset.html");
		
		TreeItem assetItem = addItem(configItem, assetMenu);
		
		/*
		TreeItem listCIItem = new TreeItem();
		listCIItem.setWidget(new Label("Assets"));
		configItem.addItem(listCIItem);
		*/
		assetItem.addItem("Loading....");
		//treePanel.addItem(itil);
		treePanel.setStyleName("mdv-form");
		
		InheritanceTreeControl treeControl = new InheritanceTreeControl("Ci");
		treeControl.setFilterInstances(Boolean.TRUE);
		// Add this to show that the CI is clickable.
		treeControl.setClickListener(new ClickListener() {
			public void onClick(Widget sender) {
			}
		});
		treeControl.setTreeListener(new TreeListener() {

			public void onTreeItemSelected(TreeItem item) {
				// Show a list of that template.
				Object data = item.getUserObject();
				if (data instanceof GWT_CiBean) {
					
					ITILApplication.get().showScreen(ITILApplication.LIST_CI_SCREEN, 
							((GWT_CiBean)data).getAlias(), new Long(-1));
				}
			}

			public void onTreeItemStateChanged(TreeItem item) {
				// TODO Auto-generated method stub
				
			}
			
		});
		ChangeTreeRootTree templateTree = new ChangeTreeRootTree(treePanel, treeControl);
		templateTree.setTriggerItem(assetItem);
		
		initWidget(treePanel);
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
					ITILApplication.get().showScreen(screen.getScreenIndex(), screen.getObjectType(), new Long(0));
					return;
				}
				if (sender instanceof ScreenMenuItem) {
					ScreenMenuItem screen= (ScreenMenuItem)sender;
					ITILApplication.get().showScreen(screen.getScreenIndex());
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
