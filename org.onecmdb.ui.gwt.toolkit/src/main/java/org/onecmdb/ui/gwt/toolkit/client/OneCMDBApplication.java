package org.onecmdb.ui.gwt.toolkit.client;




import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.BaseEntryScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.BaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.StaticContentScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.EditCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ListCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.MoveCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.NewCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ReferenceCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ViewCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.header.FooterScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.header.HeaderScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.header.WelcomeScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.login.LoginScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.login.LogoutScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.login.OneCMDBLoginScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.navigation.NavigationScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public abstract class OneCMDBApplication extends BaseEntryScreen implements WindowResizeListener { 


    
	protected static final int LOGIN_SCREEN = 0;
	protected static final int HEADER_SCREEN = 1;
	protected static final int NAVIGATION_SCREEN = 2;
	protected static final int FOOTER_SCREEN = 3;
	protected static final int LOGOUT_SCREEN = 4;
	
	
	// Core Screen(s).
	public static final int NEW_CI_SCREEN = 10;
	public static final int LIST_CI_SCREEN = 11;
	public static final int VIEW_CI_SCREEN = 12;
	public static final int EDIT_CI_SCREEN = 13;
	public static final int CI_BROWSER_SCREEN = 14;
	public static final int MOVE_CI_SCREEN = 15;
	public static final int REFERENCE_CI_SCREEN = 16;
	public static final int WELCOME_SCREEN = 17;
	public static final int SHOW_STATIC_CONTENT = 18;

	protected LoginScreen loginScreen;
	protected BaseScreen navigationScreen;
	protected BaseScreen headerScreen;
	protected BaseScreen footerScreen;
	protected BaseScreen logoutScreen;
	
	private String currentHistoryToken;
	private String cmdbHistoryPrefix;
	private ScrollPanel navigationScroll;
	private ScrollPanel mainPanelScroll;
	private HorizontalSplitPanel centerSplit;
	
	 
	protected static OneCMDBApplication singleton;
	
	/*
	public static OneCMDBApplication get() {
		return(singleton);
	}
	*/
	
	protected LoginScreen getLoginScreen() {
		if (loginScreen == null) {
			loginScreen = (LoginScreen)getScreenFirstTime(LOGIN_SCREEN);
		}
		return(loginScreen);
	}
	
	protected BaseScreen getHeaderScreen() {
		if (headerScreen == null) {
			headerScreen = getScreenFirstTime(HEADER_SCREEN);
		}
		return(headerScreen);
		
	}
	
	protected BaseScreen getNavigationScreen() {
		if (navigationScreen == null) {
			navigationScreen = getScreenFirstTime(NAVIGATION_SCREEN);
		}
		return(navigationScreen);
	}
	
	protected BaseScreen getFooterScreen() {
		if (footerScreen == null) {
			footerScreen = getScreenFirstTime(FOOTER_SCREEN);
		}
		return(footerScreen);
	}
	
	protected BaseScreen getLogoutScreen() {
		if (logoutScreen == null) {
			logoutScreen = getScreenFirstTime(LOGOUT_SCREEN);
		}
		return(logoutScreen);
	}

	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		singleton = this;

		addHistorySupport();
		setLoginScreen();
		// setMainScreen(null);

		Window.addWindowResizeListener(this);


	}

	public void onHistoryChanged(String historyToken) {
		// When adding a new History Item a onHistoryChange event will happen.
		if (historyToken.equals(currentHistoryToken)) {
			return;
		}
		// TODO Auto-generated method stub
		super.onHistoryChanged(historyToken);
	}

	protected String getHistoryName(int index)	{
		currentHistoryToken = super.getHistoryName(index);
		return(currentHistoryToken);
	}
	
	protected String getHistoryName(int index,String objectType,Long objectId)	{
		currentHistoryToken = super.getHistoryName(index, objectType, objectId);
		return(currentHistoryToken);
	}


	
	public void setMainScreen(GWT_CiBean account) {
		this.account = account;
		getHeaderScreen().setWidth("100%");
		DockPanel outer = new DockPanel();
		BaseScreen header = getHeaderScreen();
		if (header instanceof HeaderScreen) {
			((HeaderScreen)header).setAccount(account);
		}
		outer.add(header, DockPanel.NORTH);
			
		//outer.add(getNavigationScreen()), DockPanel.WEST);
		//outer.add(getMainPanel(), DockPanel.CENTER);
	
		//navigationScroll = new ScrollPanel(getNavigationScreen());
		//mainPanelScroll = new ScrollPanel(getMainPanel());
		//navigationScroll.setAlwaysShowScrollBars(true);
		//mainPanelScroll.setAlwaysShowScrollBars(true);
		
		centerSplit = new HorizontalSplitPanel();
		centerSplit.setLeftWidget(getNavigationScreen());
		centerSplit.setRightWidget(getMainPanel());
		getMainPanel().setStyleName("mdv-form");
		centerSplit.setSplitPosition("35%");
		//outer.add(navigationScroll, DockPanel.WEST);
		//outer.add(mainPanelScroll, DockPanel.CENTER);
		outer.add(centerSplit, DockPanel.CENTER);
		getMainPanel().setSize("100%", "100%");
		getNavigationScreen().setSize("100%", "100%");
		
		if (getFooterScreen() != null) {
			getFooterScreen().setWidth("100%");
			getFooterScreen().setStyleName("mdv-form");
			outer.add(getFooterScreen(), DockPanel.SOUTH);
		}
		
		outer.setWidth("100%");
		outer.setWidth("100%");
		outer.setSpacing(4);
		//outer.setCellWidth(getMainPanel(), "100%");
		//outer.setCellHeight(getMainPanel(), "100%");
		outer.setCellWidth(centerSplit, "100%");
		outer.setCellHeight(centerSplit, "100%");
		
		RootPanel.get().clear();
		RootPanel.get().add(outer);
		
		// No main scrolls..
		
		Window.enableScrolling(false);
		// Call the window resized handler to get the initial sizes setup.
		onWindowResized(Window.getClientWidth(), Window.getClientHeight());
		
		showScreen(WELCOME_SCREEN);
	}

	

	public void setLoginScreen() {
		
		if (getLoginScreen() != null) {
			DockPanel outer = new DockPanel();
			outer.add(getHeaderScreen(), DockPanel.NORTH);
			outer.add(getLoginScreen(), DockPanel.CENTER);
			getLoginScreen().checkIfLogedIn();
			RootPanel.get().clear();
			outer.setSize("100%", "100%");
			RootPanel.get().add(outer);			
			//center(RootPanel.get(), getLoginScreen());
		}
	}

	    // Center a widget in an absolute panel. 
	    private void center(AbsolutePanel panel, Widget w) {
	    	int left = panel.getAbsoluteLeft();
	    	int top = panel.getAbsoluteTop();
	    	
	    	int aHeight = Window.getClientHeight();
	    	int aWidth = Window.getClientWidth();
			
	    	int wHeight = w.getOffsetHeight();
	    	int wWidth = w.getOffsetWidth();
	    	
	    	int centerLeft = left + (aWidth/2) - (wWidth/2);
	    	int centerTop = top + (aHeight/2) - (wHeight/2);
	    	
	    	panel.setWidgetPosition(w, centerLeft, centerTop);
	    }

		public void setLogoutScreen() {
	    	RootPanel.get().clear();
	    	BaseScreen logout = getLogoutScreen();
	    	RootPanel.get().add(logout);
	    	center(RootPanel.get(), logout);
	    }

	    /**
	     * All new screens that are defined should be setup here
	     */
		
		protected BaseScreen getScreenFirstTime(int index) {
			BaseScreen base = null;
			
			base = getOneCMDBScreenFirstTime(index);
			
			if (base == null) {
				// Check default screens.
				switch (index) {
					case LOGIN_SCREEN:
						base = new OneCMDBLoginScreen();
						break;
					
					case LOGOUT_SCREEN:
						base = new LogoutScreen();
						break;
					
					case HEADER_SCREEN:
						base = new HeaderScreen();
						break;
						
					case NAVIGATION_SCREEN:
						base = new NavigationScreen();
						break;
					
					case FOOTER_SCREEN:
						base = new FooterScreen();
						break;
						
						// Core screens
					case NEW_CI_SCREEN:
						base = new NewCIScreen();
						break;
					case LIST_CI_SCREEN:
						base = new ListCIScreen();
						break;
					case EDIT_CI_SCREEN:
						base = new EditCIScreen();
						break;
					case VIEW_CI_SCREEN:
						base = new ViewCIScreen();
						break;
					case MOVE_CI_SCREEN:
						base = new MoveCIScreen();
						break;
					case REFERENCE_CI_SCREEN:
						base = new ReferenceCIScreen();
						break;
					
					case WELCOME_SCREEN:
						base = new WelcomeScreen();
						break;
					case SHOW_STATIC_CONTENT:
						base = new StaticContentScreen();
						break;

				}
			}
			if (base != null) {
				if (base instanceof OneCMDBBaseScreen) {
					OneCMDBBaseScreen screen = (OneCMDBBaseScreen)base;
					screen.setBaseEntryScreen(this);
					/*
					if (screen.isScrollable()) {
						
						base = new CompositeBaseScreen(screen);
						if (screen.isRightPanel()) {
							updateSize(base);
							//updateSize(screen);
						}
					}
					*/	
					
				}
				
			}
			return base;
		}

	  
		protected abstract BaseScreen getOneCMDBScreenFirstTime(int index);
	
		public void onWindowResized(int width, int height) {
			/*
	    	System.out.println("Window resize");
	    	if (RootPanel.get().getWidgetCount() > 0) {
		    	if (RootPanel.get().getWidget(0) == getLoginScreen()) {
		    		System.out.println("\tLogin Dialog Window resize");
		    		center(RootPanel.get(), getLoginScreen());
		    		return;
		    	}
	    	}
	        */
			if (true) {
				
				height = Window.getClientHeight();
				width = Window.getClientWidth();
				//width = RootPanel.get().getOffsetWidth();
				//height = RootPanel.get().getOffsetHeight();
				int navigationHeight = height - getNavigationScreen().getAbsoluteTop() - 8;
				if (getFooterScreen() != null) {
					navigationHeight -= getFooterScreen().getOffsetHeight();
				}
				
				if (centerSplit != null) {
					centerSplit.setPixelSize(width - 16, navigationHeight);
				}
				return;
			}
			if (true) {
				// Adjust the shortcut panel and detail area to take up the available
			        // room
				// in the window.
				int shortcutHeight = height - getNavigationScreen().getAbsoluteTop() - 8;
				if (shortcutHeight < 1) {
				    shortcutHeight = 1;
				}
				String heightStr = "" + shortcutHeight;
				System.out.println("Left Panel height:" + heightStr);
				getNavigationScreen().setHeight(heightStr);
				return;
			}
			height = Window.getClientHeight();
			width = Window.getClientWidth();
			
			width = RootPanel.get().getOffsetWidth();
			
			int navigatorHeight = height - getNavigationScreen().getAbsoluteTop() - 35;
			if (navigatorHeight < 1) {
				navigatorHeight = 1;
			}
			
			int navigatorWidth = 150;
			
			
			/*
			for (Iterator iter = screens.values().iterator(); iter.hasNext(); ) {
				Object screen = iter.next();
				if (screen instanceof OneCMDBBaseScreen) {
					updateSize((OneCMDBBaseScreen)screen);
				}
			}
			*/
	
			//getNavigationScreen().setSize(navigatorWidth + "px", navigatorHeight +"px");
			navigationScroll.setSize(navigatorWidth + "px", navigatorHeight +"px");
			int mainScreenWidth = width - navigatorWidth - 35;
			
			mainPanelScroll.setSize(mainScreenWidth + "px", navigatorHeight+ "px");

	    }
		 
		protected void updateSize(Widget screen) {
			System.out.println("UpdateSize on Screen....");
			int height = Window.getClientHeight();
			int width = Window.getClientWidth();
			
			//width = RootPanel.get().getOffsetWidth();
			
			int navigatorHeight = height - getNavigationScreen().getAbsoluteTop() - 35;
			if (navigatorHeight < 1) {
				navigatorHeight = 1;
			}
			
			int navigatorWidth = getNavigationScreen().getOffsetWidth();
			int mainScreenWidth = width - navigatorWidth - 35;
			
			if (mainScreenWidth <= 0) {
				mainScreenWidth = 1;
			}
			/*
			String heightStr = "" + shortcutHeight + "px";
			String restWidthStr = "" + restWidth + "px";
			String shortWidthStr = "" + shortcutWidth + "px";
			*/	
			
			//System.out.println("Test Left Panel height:" + heightStr);
			screen.setSize(mainScreenWidth + "px", navigatorHeight +"px");
		}

		public int getScreenIndex(String selectScreenName) {
			return(-1);
		}

		public String getVersion() {
			return("1.4.0 - Beta");
		}
		public String getCurrentPage() {
			return("" + currentPage);
		}
}
