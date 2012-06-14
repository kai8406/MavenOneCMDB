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

package org.onecmdb.ui.gwt.toolkit.client.view.screen;

import java.util.HashMap;


import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * The main class of your GWT project should inherit from BaseEntryScreen
 * instead of implementing EntryPoint
 * <p>
 * This class provides lazy loading functionality and some utility functions
 * 
 * @author Aditya Kapur
 * 
 */
public abstract class BaseEntryScreen implements EntryPoint,HistoryListener {

	// Delimiter for the history entry
	private static final String DELIMITER = "#";
	//private static String historyPrefix="OneCMDBScreen_";
	private DeckPanel rightPanel = new DeckPanel();

	private BaseScreen base;

	protected HashMap screens = new HashMap();
	
	protected GWT_CiBean account;
	protected int currentPage;
	
	/**
	 * Call this method from onModuleLoad()
	 * to add support for history. Nothing else needs to be done
	 *
	 */
	public void addHistorySupport()	{
		History.addHistoryListener(this);
		String initToken = History.getToken();
		if (initToken.length() == 0)
			initToken = "OneCMDBScreenEntry";

		// onHistoryChanged() is not called when the application first runs.
		// Call
		// it now in order to reflect the initial state.

		onHistoryChanged(initToken);
	}
	public void changeHistory(String historyToken) {
		if (historyToken.startsWith(getHistoryPrefix())) {
			try {
				String token = historyToken.substring(getHistoryPrefix().length());

				String[] tokenList = token.split(DELIMITER);
//				System.out.println("calling ChangeHistory "+historyToken+" index=" + token+" length="+tokenList.length);
				if(tokenList.length==3){
					int index = Integer.parseInt(tokenList[0]);
					String objectType=tokenList[1];
					String objectId=tokenList[2];
					showScreen(index,objectType,objectId);
				}else	{
					if(tokenList.length==1)	{
						int index = Integer.parseInt(tokenList[0]);
						showScreen(index);
					}
				}
					
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}

	public void onHistoryChanged(String historyToken) {
		// This method is called whenever the application's history changes. Set
		// the label to reflect the current history token.
//		System.out.println("The current history token is: " + historyToken);
		changeHistory(historyToken);
	}

	public GWT_CiBean getAccount() {
		return(this.account);
	}

	
	/**
	 * This method allows you to show the "Loading..." message on the screen
	 * 
	 * @param visible
	 *            if true then the message is displayed and is hidden when false
	 */
	public void showLoading(boolean visible) {
		base.showLoading(visible);
	}

	/**
	 * This method opens the screen identified by the index<br>
	 * If the screen has not been loaded earlier, it is initialized
	 * 
	 * @param index
	 *            ID of the screen to be loaded
	 * @see BaseScreen#load() load
	 */

	public void showScreen(int index) {
		base = getRightPanelWidget(index);
		
		if (base == null) {
			System.out.println("Screen index " + index + " not found!");
		}
		if(base!=null)	{
			 History.newItem(getHistoryName(index));
			 base.load();
			 currentPage = index;
		}
		
	}

	/**
	 * This method opens the screen identified by the index<br>
	 * If the screen has not been loaded earlier, it is initialized
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * showScreen(CUSTOMER_SCREEN, &quot;CUSTOMER&quot;, CustId);
	 * </pre>
	 * 
	 * </blockquote> Where CUSTOMER_SCREEN is a static int variable identifying
	 * the screen "CUSTOMER" is a parameter passed to the screen to help it
	 * identify what objectID that is being passed CustId is a variable that
	 * could potentially be the primary key of the customer table.
	 * 
	 * The expected behavior is that the screen displayed and the customer is
	 * loaded
	 * 
	 * @param index
	 *            ID of the screen to be loaded.
	 * @param objectType
	 *            This is a parameter that is passed to the screen that is being
	 *            loaded.
	 * @param objectId
	 *            This is a parameter passed to the screen.
	 * @see BaseScreen#load(String, Long) load
	 */

	public void showScreen(int index, String objectType, Long objectId) {

		base = getRightPanelWidget(index);
		if (base != null)	{
			History.newItem(getHistoryName(index,objectType,objectId));
            base.clear();
			base.load(objectType, objectId);
			currentPage = index;
		}

	}

	/**
	 * This method opens the screen identified by the index<br>
	 * If the screen has not been loaded earlier, it is initialized
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * showScreen(CUSTOMER_SCREEN, &quot;CUSTOMER&quot;, CustId);
	 * </pre>
	 * 
	 * </blockquote> Where CUSTOMER_SCREEN is a static int variable identifying
	 * the screen "CUSTOMER" is a parameter passed to the screen to help it
	 * identify what objectID that is being passed CustId is a variable that
	 * could potentially be the primary key of the customer table. In this
	 * method the ID is a string that is converted to a Long variable.
	 * 
	 * The expected behavior is that the screen displayed and the customer is
	 * loaded
	 * 
	 * @param index
	 *            ID of the screen to be loaded.
	 * @param objectType
	 *            This is a parameter that is passed to the screen that is being
	 *            loaded.
	 * @param objectId
	 *            This is a parameter passed to the screen.
	 */
	public void showScreen(int index, String objectType, String objectId) {
		showScreen(index, objectType, new Long(objectId));
	}

	/**
	 * This method returns tha panel that displays all the screen in the
	 * application
	 * 
	 * @return DeckPanel the panel
	 */
	protected DeckPanel getMainPanel() {
		return rightPanel;
	}

	/**
	 * This method should be implemented in your entry screen <blockquote>
	 * 
	 * <pre>
	 *   protected BaseScreen getScreenFirstTime(int index)	{
	 *  	BaseScreen base=null;
	 * 			case USER_EDIT_SCREEN: base=new UserEditScreen(); break;
	 * 		default: System.out.println(&quot;Screen #&quot;+index+&quot; not found&quot;);
	 * 		break;
	 * 
	 * 	}
	 * 	return base;
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param index
	 *            the ID of the screen to be loaded. This should be declared as
	 *            public static final variable in your class
	 * @return BaseScreen screen to be loaded
	 */
	protected abstract BaseScreen getScreenFirstTime(int index);

	protected String getHistoryName(int index)	{
		return getHistoryPrefix() + index;
	}
	protected String getHistoryName(int index,String objectType,Long objectId)	{
		return getHistoryPrefix() + index+ DELIMITER+ objectType+ DELIMITER+objectId;
	}
	
	private BaseScreen getRightPanelWidget(int index) {
		BaseScreen base;
		Integer idx = new Integer(index);
		base = (BaseScreen) screens.get(idx);
		if (base == null) {// The screen is not in our cache so needs to be
			// initialized
			base = getScreenFirstTime(index);
			if (base != null) {
				screens.put(idx, base);
				rightPanel.add(base);
			}
		}
		int panelIndex = rightPanel.getWidgetIndex(base);
		if (panelIndex >= 0)
			rightPanel.showWidget(panelIndex);
		
		return base;
	}
	
	/**
	 * Can be overridden to implement specific.
	 * @return
	 */
	protected String getHistoryPrefix() {
		return("OneCMDBScreen_");
	}

}
