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

package org.onecmdb.ui.gwt.toolkit.client.view.screen.navigation;

import org.gwtiger.client.widget.ScreenMenuItem;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Addy
 *
 */
public class NavigationScreen extends OneCMDBBaseScreen implements ClickListener{ 
	protected Tree treePanel = new Tree();


	public NavigationScreen()	{
		TreeItem root = new TreeItem();
		initWidget(treePanel);
	}
	
	public boolean isRightPanel() {
		return(false);
	}

	protected void addItem(TreeItem parentItem, ScreenMenuItem widget) {
		TreeItem childItem = new TreeItem();
		childItem.setWidget(widget);
		parentItem.addItem(childItem);
	
		widget.addClickListener(this);

		
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
	      + "<td style='vertical-align:middle'><b style='white-space:nowrap'>"
	      + caption + "</b></td>" + "</tr></table>";
	  }
	  
	  private String createHTML(String caption) {
		    return "<a href='javascript:;'>"
		      + caption + "</a>";
		  }
	
	  private String createHTMLWithTable(String caption) {
		    return "<table align='left'><tr>" 
		      + "<td><img src='mdv-menu.gif'></td><td style='vertical-align:middle'><a href='javascript:;'>"
		      + caption + "</a></td>" + "</tr></table>";
		  }
	
	  /**
	   * 
	   */
	  public void onClick(Widget sender) {
			try	{
		  	  ScreenMenuItem screen=(ScreenMenuItem)sender;
		  	  getBaseEntryScreen().showScreen(screen.getScreenIndex());
			}catch(Exception e)	{
				e.printStackTrace();
			}
			      
		}
	
}
