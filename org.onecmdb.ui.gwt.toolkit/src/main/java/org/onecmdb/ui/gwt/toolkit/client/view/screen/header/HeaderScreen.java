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

package org.onecmdb.ui.gwt.toolkit.client.view.screen.header;

/**
 * @author Addy
 *
 */

import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class HeaderScreen extends OneCMDBBaseScreen implements ClickListener {

  //private HTML signOutLink = new HTML("<font size=\"small\"><a href='javascript:;'>Sign Out</a></font>");
  private HTML welcomeString = new HTML("Anonymous"); 
  private HTML logout = new HTML("<a href='javascript:;'>[logout]</a>");
  private Image icon = new Image("images/onecmdblogo.jpg");
 
  public HeaderScreen() {
    HorizontalPanel outer = new HorizontalPanel();
    VerticalPanel inner = new VerticalPanel();
    outer.setWidth("100%");
    outer.setHeight("3em");
    
    //outer.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
    inner.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
    inner.setWidth("100%");
    inner.setHeight("100%");
    
    
    HorizontalPanel authInfo = new HorizontalPanel();
    //authInfo.setSpacing(4);
    logout.setStyleName("logout-style");
    authInfo.add(welcomeString);
    authInfo.add(logout);
    logout.setVisible(false);
    logout.addClickListener(this);
    inner.add(authInfo);
 
    HorizontalPanel actionPanel = new HorizontalPanel();
    actionPanel.setStyleName("onecmdb-header-label");
    actionPanel.setSpacing(10);
    actionPanel.add(getFeedbackWidget());
    actionPanel.add(getCheckUpdateWidget());
    
    actionPanel.add(getHomeWidegt());
    actionPanel.add(getHelpWidget());
    
    inner.add(actionPanel);
    inner.setCellVerticalAlignment(actionPanel, VerticalPanel.ALIGN_BOTTOM);
    inner.setCellVerticalAlignment(authInfo, VerticalPanel.ALIGN_TOP);
     
    
    //DockPanel iconPanel = new DockPanel();
    //iconPanel.setWidth("100%");
    //welcomePanel.setSpacing(10);
    //iconPanel.add(icon,DockPanel.EAST);
//    welcomePanel.add(new HTML("<b>&nbsp;</b>"),DockPanel.CENTER);
    outer.add(icon);
    outer.setCellHorizontalAlignment(icon, HorizontalPanel.ALIGN_LEFT);
    outer.add(inner);
    outer.setCellHorizontalAlignment(inner, HorizontalPanel.ALIGN_RIGHT);
    
    
    // Show welcome page.
    icon.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
			getBaseEntryScreen().showScreen(OneCMDBApplication.WELCOME_SCREEN);
		}
    });
    
    initWidget(outer);
    //outer.setStyleName("one-top-panel");
  }
 
  
  private Widget getHelpWidget() {
	  HTML html = new HTML("<a href='http://www.onecmdb.org/wiki/index.php/Documentation'><img src='images/help16.gif'</a>");  
	  html.setTitle("Help - On onecmdb.org");
	  return(html);
  }

  private Widget getHomeWidegt() {
	HTML html = new HTML("<a href='../../index.html'><img src='images/home16.gif'</a>");  
	html.setTitle("Goto first page!");
	return(html);
  }


  private Widget getFeedbackWidget() {
	  HTML good = new HTML("<a href='javascript:;'>[Good]</a>&nbsp;");
	  HTML bad = new HTML("<a href='javascript:;'>[Bad]</a>");
	  good.addClickListener(new ClickListener() {

		public void onClick(Widget sender) {
			Window.open("http://www.onecmdb.org/feedback/good.html" + 
					"?version=" + getBaseEntryScreen().getVersion() + 
					"&action=" + getBaseEntryScreen().getCurrentPage(),
					"Good", "height=500,width=500");
		}
	  	
	  	}
	  );
	  bad.addClickListener(new ClickListener() {

		  public void onClick(Widget sender) {
			  Window.open("http://www.onecmdb.org/feedback/bad.html" + 
					  "?version=" + getBaseEntryScreen().getVersion() + 
					  "&action=" + getBaseEntryScreen().getCurrentPage(),
					  "Bad", "height=500,width=500");
		  }
	  }
	  );
	  
	  HTML info = new HTML("Your feedback to onecmdb.org:&nbsp;");
	  HorizontalPanel panel = new HorizontalPanel();
	  panel.setStyleName("onecmdb-header-label");
	  
	  panel.add(info);
	  panel.add(good);
	  panel.add(bad);
	  return(panel);
  }

  public Widget getCheckUpdateWidget() {
	HTML html = new HTML("<a href='javascript:;'>[Check for updates]</a>");
	html.addClickListener(new ClickListener() {

		public void onClick(Widget sender) {
			Window.open("http://sourceforge.net/project/showfiles.php?group_id=176340", 
					"_blank", "");
		}
		
	});
	return(html);
  }
 
  public boolean isScrollable() {
	  return(false);
  }
	
  public boolean isRightPanel() {
	return(false);
  }


  public void onClick(Widget sender) {
	  if (sender == logout) {
		  // Log out
		  AsyncCallback callback = new AsyncCallback() {
			  public void onSuccess (Object result)
			  {
				  OneCMDBUtils.redirect("../../index.html");
				  //getBaseEntryScreen().setLogoutScreen();
			  }
			  public void onFailure (Throwable ex)
			  {
				  System.out.println("Error "+ex);
				  ex.printStackTrace();

			  }
		  };
		  getService().logout(OneCMDBSession.getAuthToken(), callback);

	  }
  }


  public void setAccount(GWT_CiBean account) {
	  String username = "admin";
	  if (account != null) {
		  GWT_ValueBean aBean = account.fetchAttributeValueBean("username", 0);
		  if (aBean != null) {
			  username = aBean.getValue();
		  }
	  }
	  welcomeString.setHTML("Logged in as <b>" + username + "</b> on server " + OneCMDBSession.getOneCMDBURL() + "&nbsp");
	  logout.setVisible(true); 

  }

}