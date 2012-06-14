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
package org.onecmdb.ui.gwt.desktop.client.mvc.contoller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.onecmdb.ui.gwt.desktop.client.Version;
import org.onecmdb.ui.gwt.desktop.client.WindowFactory;
import org.onecmdb.ui.gwt.desktop.client.fixes.MyDesktop;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBLoginException;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopMenuItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.UserPreference;
import org.onecmdb.ui.gwt.desktop.client.utils.DesktopMenuFactory;
import org.onecmdb.ui.gwt.desktop.client.widget.ExceptionErrorDialog;
import org.onecmdb.ui.gwt.desktop.client.widget.LoginWidget;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBWidgetFactory;
import org.onecmdb.ui.gwt.desktop.client.window.DesktopWidgetFactory;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetParameterEntry;
import org.onecmdb.ui.gwt.desktop.client.window.misc.CMDBURLFrameWidget;
import org.onecmdb.ui.gwt.desktop.client.window.misc.RegistrationWindow;

import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.desktop.client.TaskBar;
import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class DesktopContoller extends Controller {

	private MyDesktop desktop;
	private Widget viewport;
	private Map<String, List<String>> urlMap;
	protected long lastActivityTime;
	private Timer activityTimer;
	
	
	public DesktopContoller() {
		registerEventTypes(CMDBEvents.DESKTOP_LOGIN);
		registerEventTypes(CMDBEvents.DESKTOP_LOGOUT);
		registerEventTypes(CMDBEvents.DESKTOP_LOGGED_IN);
		registerEventTypes(CMDBEvents.DESKTOP_ABOUT);
		registerEventTypes(CMDBEvents.DESKTOP_MENU_SELECTED);
		registerEventTypes(CMDBEvents.DESKTOP_CHANGE_ROLE);
		registerEventTypes(CMDBEvents.DESKTOP_CHECK_SESSION);
		registerEventTypes(CMDBEvents.DESKTOP_LOCK_TIMEOUT);
		
		
	
		// Need to start ContentServlet.
		ContentServiceFactory.get().stat(new ContentData(), new AsyncCallback<ContentData>() {
			public void onFailure(Throwable caught) {
			}

			public void onSuccess(ContentData result) {
			}
			
		});
		
		// Register Desktop Views.
		DesktopWidgetFactory.get().addWidgetFactory(new CMDBWidgetFactory());
	}
	
	@Override
	public void handleEvent(AppEvent<?> event) {
		switch(event.type) {
			// Main entry point.
			case CMDBEvents.DESKTOP_CHECK_SESSION:
				// Save URL arguments
				urlMap = com.google.gwt.user.client.Window.Location.getParameterMap();
				
				// Check for autologin.
				List<String> alids = urlMap.get("alid"); 
				if (alids != null && alids.size() > 0 ) {
					String alid = alids.get(0);
					// Need to validate this token.
					ModelServiceFactory.get().autoLogin(alid, new AsyncCallback<CMDBSession>() {
							public void onFailure(Throwable arg0) {
								Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOGIN);
								return;
							}

							public void onSuccess(CMDBSession arg0) {
								arg0.setURLValues(urlMap);
								CMDBSession.get().setSession(arg0);
								initDesktop();
								setupDesktop(arg0.getDesktopConfig());
							}
						});
					return;
				}
				
				// Check if we have a cookie set.
				String token = Cookies.getCookie("auth_token");
				if (token != null) {
					// Need to validate this token.
					ModelServiceFactory.get().validateToken(token, new AsyncCallback<CMDBSession>() {
							public void onFailure(Throwable arg0) {
								Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOGIN);
								return;
							}

							public void onSuccess(CMDBSession arg0) {
								arg0.setUsername(Cookies.getCookie("auth_username"));
								arg0.setURLValues(urlMap);
								CMDBSession.get().setSession(arg0);
								initDesktop();
								setupDesktop(arg0.getDesktopConfig());
							}
							
							
						});
					return;
				}
				
				
				// Normal login.
				Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOGIN);
				break;
			case CMDBEvents.DESKTOP_MENU_SELECTED:
				CMDBDesktopMenuItem item = (CMDBDesktopMenuItem) event.data;
				CMDBDesktopWindowItem wItem = item.getWindowItem();
				Window window = WindowFactory.showWindow(desktop, wItem);
				break;
			case CMDBEvents.DESKTOP_LOCK_TIMEOUT:
				stopActivityTimer();
				LoginWidget.login(true, new AsyncCallback<CMDBSession> () {

					public void onFailure(Throwable arg0) {
						if (arg0 instanceof CMDBLoginException) {
							CMDBLoginException ex = (CMDBLoginException)arg0;
							MessageBox.alert(ex.getHeader(), ex.getMessage(), new Listener<WindowEvent>() {

								public void handleEvent(WindowEvent be) {
									Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOCK_TIMEOUT);
								}
								
							});  
							
							return;
						}
						ExceptionErrorDialog.showError("Login Failed!", arg0, new Listener<WindowEvent>() {

							public void handleEvent(WindowEvent be) {
							}
						});
					}

					public void onSuccess(CMDBSession arg0) {
						 Date date = new Date();
					     long dateLong = date.getTime();
					     dateLong += (1000*60*60*8);// 8h convert days to ms
					     date.setTime(dateLong); // Set the new date

						Cookies.setCookie("auth_token", arg0.getToken(), date);
						Cookies.setCookie("auth_username", arg0.getUsername(), date);
						arg0.setURLValues(urlMap);
						CMDBSession.get().setSession(arg0);
						startActivityTimeout(CMDBSession.get().getConfig().getDesktopLockTimeout());
					}
				});
				
				break;
			case CMDBEvents.DESKTOP_LOGIN:
				LoginWidget.login(false, new AsyncCallback<CMDBSession> () {

					public void onFailure(Throwable arg0) {
						if (arg0 instanceof CMDBLoginException) {
							CMDBLoginException ex = (CMDBLoginException)arg0;
							MessageBox.alert(ex.getHeader(), ex.getMessage(), new Listener<WindowEvent>() {

								public void handleEvent(WindowEvent be) {
									Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOGIN);
								}
								
							});  
							
							return;
						}
						ExceptionErrorDialog.showError("Login Failed!", arg0, new Listener<WindowEvent>() {

							public void handleEvent(WindowEvent be) {
							}
						});
					}

					public void onSuccess(CMDBSession arg0) {
						 Date date = new Date();
					     long dateLong = date.getTime();
					     dateLong += (1000*60*60*8);// 8h convert days to ms
					     date.setTime(dateLong); // Set the new date

						Cookies.setCookie("auth_token", arg0.getToken(), date);
						Cookies.setCookie("auth_username", arg0.getUsername(), date);
						arg0.setURLValues(urlMap);
						CMDBSession.get().setSession(arg0);
						initDesktop();
						setupDesktop(arg0.getDesktopConfig());
					}
				});
				break;
			case CMDBEvents.DESKTOP_LOGOUT:
				// close all windows.
				clearWindow();
				
				// Logout
				ModelServiceFactory.get().logout(CMDBSession.get().getToken(), new AsyncCallback<Boolean>() {

					public void onFailure(Throwable caught) {
					}

					public void onSuccess(Boolean result) {
					}
					
				});
				
				// Clear session...
				CMDBSession.setSession(null);
				
				// Stop Lock timer
				stopActivityTimer();
				
				// Clear Cookies.
				Cookies.removeCookie("auth_token");
				Cookies.removeCookie("auth_username");
				
				// Call login.
				// Reload application....
				reloadApplication();
				//Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOGIN);
				break;
			case CMDBEvents.DESKTOP_ABOUT:
				CMDBDesktopWindowItem about = new CMDBDesktopWindowItem();
				about.setID(CMDBURLFrameWidget.ID);
				about.getParams().set("url", GWT.getModuleBaseURL() + "/about.html");
				about.getParams().set("newWindow", "true");
				about.setHeading("About - OneCMDB");
				WindowFactory.showWindow(desktop, about);
				break;
			case CMDBEvents.DESKTOP_CHANGE_ROLE:
				final String role = (String) event.data;
				//desktop.getStartMenu().hide();
				final MessageBox info = MessageBox.wait("Progress",  
			             "Change Role to " + role, "Wait...");  

				ModelServiceFactory.get().getDesktopConfig(CMDBSession.get().getUsername(), CMDBSession.get().getToken(), role, new CMDBAsyncCallback<CMDBDesktopConfig>() {

					@Override
					public void onFailure(Throwable t) {
						info.close();
						super.onFailure(t);
					}

					@Override
					public void onSuccess(CMDBDesktopConfig arg0) {
						info.close();
						clearWindow();
						CMDBSession.get().setDesktopConfig(arg0);
						CMDBSession.get().setDefaultRole(role);
						initDesktop();
						setupDesktop(arg0);
					}
				});
				break;
			  
		}
	}
	
	/**
	 * Check if the url request a single stdalone window.
	 * @param config 
	 * @param urlMap
	 * @return
	 */
	 private boolean useWindowID(CMDBDesktopConfig config) {
		 Map<String, List<String>> map = CMDBSession.get().getURLValues();
		 List<String> windowIds = map.get("window.id");
		 if (windowIds == null || windowIds.size() == 0) {
			 return(false);
		 }
		 String windowID = windowIds.get(0);
		 CMDBDesktopWindowItem item = DesktopWidgetFactory.get().createWidgetItem(windowID, map);
		 Widget w = DesktopWidgetFactory.get().createWidget(item);
		 
		 // Don't show any taskbar.
		 desktop.getTaskBar().setVisible(false);
		 
		 if (w == null) {
			 StringBuffer html = new StringBuffer();
			 html.append("<table>");
			 html.append("<tr style=\"background:#DFE8F6;\">");
			 html.append("<td colspan=2>");
			 html.append("<h1>Window ID <i>" + windowID + "</i> is not supported</h1>");
			 html.append("<td>");
			 html.append("</tr>");
			 html.append("<tr style=\"background:#DFE8F6;\">");
			 html.append("<td colspan=2>");
			 html.append("Available widgets are:</br>");
			 html.append("</td>");
			 html.append("</tr>");
			 
			 List<WidgetDescription> descs = DesktopWidgetFactory.get().getWidgetDescriptions();
			 html.append("<tr style=\"background:#DFE8F6;\"><th>Widget ID</th><th>Description</th></tr>");
			 for (WidgetDescription desc : descs) {
				 String id = desc.getId();
				 String info =  desc.getDescription();
				 StringBuffer params = new StringBuffer();
				 List<WidgetParameterEntry> pEntries = desc.getParameterEntries();
				 for (WidgetParameterEntry pEntry : pEntries) {
					 String key = pEntry.getKey();
					 String defaultValue = pEntry.getDefaultValue();
					 params.append("&");
					 params.append(key);
					 params.append("=");
					 params.append(defaultValue == null ? "" : defaultValue);;
				 }
				 html.append("<tr style=\"background:#f5f5f5;\">");
				 String url = "?window.id=" + id + params.toString();
				 html.append("<td><a href=\""+ url + "\">" + id + "</a></td>");
				 html.append("<td><i>" + info + "</i></td>");
				 html.append("</tr>");
			}
			 html.append("</table>");
			 w = new HTML(html.toString());
				
		 }
		 // Show maximized window...
		 Window window = new Window();
		 window.setLayout(new FitLayout());
		 window.setClosable(false);
		 window.setHeaderVisible(false);
		 window.setMaximizable(true);
		 ContentPanel cp = new ContentPanel();
		 cp.setHeaderVisible(false);
		 cp.setScrollMode(Scroll.AUTO);
		 cp.setLayout(new FitLayout());
		 cp.add(w);
		 window.add(cp);
		 window.show();
		 window.maximize();
		 
		 window.layout();
		 //window.setMaximizable(false);
		 
		 return true;
	}

	public native void reloadApplication() /*-{
     	$wnd.location.reload();
 	}-*/; 

	private void clearWindow() {
		desktop.logout();
		
		/*
		for (Window w : desktop.getWindows()) {
			desktop.removeWindow(w);
		}
		
		//desktop.getStartMenu().removeAll();
		//desktop.getTaskBar().removeAll();
		
		// Remove Desktop, need to find the Viewport...
		int count = RootPanel.get().getWidgetCount();
		for (int i = 0; i < count; i++) {
			Widget widget = RootPanel.get().getWidget(i);
			if (widget instanceof Viewport) {
				this.viewport = widget;
				RootPanel.get().remove(widget);
				break;
			}
		}
		*/
		//desktop = null;
	}

	protected void initDesktop() {
		// First time create a new desktop, that adds a viewport.
		if (desktop == null) {
			desktop = new MyDesktop();
			addActivityTimer();
		}
		
		// Add check for time out... 
		// TODO: have this specified in the config....
		startActivityTimeout(CMDBSession.get().getConfig().getDesktopLockTimeout());
		
		if (desktop.getTaskBar() == null) {
			desktop.relogin();
		}
		CMDBSession.get().setDesktop(desktop);
		// Next login we have a viewport just attache it.
		if (viewport != null) {
			RootPanel.get().add(viewport);
		}
	}
	
	protected void addActivityTimer() {
		if (CMDBSession.get().getConfig().getDesktopLockTimeout() < 0) {
			return;
		}
		lastActivityTime = System.currentTimeMillis();
		
		//add event capture to detect user activity and reset idle timeout
	    DOM.setEventListener(RootPanel.get().getElement(), new EventListener() {

	
			public void onBrowserEvent(Event event) {
				
				lastActivityTime = System.currentTimeMillis();
			}
	    });
	    DOM.sinkEvents(RootPanel.get().getElement(), Event.ONMOUSEDOWN);
	}
	

	protected void stopActivityTimer() {
		if (activityTimer == null) {
			return;
		}
		
		activityTimer.cancel();
		activityTimer = null;
	}
	/**
	 * Start a timer that will logout user when timeout time has elapsed 
	 * and no browser activity has been reached.
	 * 
	 * @param timeout
	 */
	protected void startActivityTimeout(final int timeout) {
		if (CMDBSession.get().getConfig().getDesktopLockTimeout() < 0) {
			return;
		}
		// Check every min
		final int checkIntervall = 60*1000; 
		if (activityTimer != null) {
			return;
		}
		activityTimer = new Timer() {

			@Override
			public void run() {
				long now = System.currentTimeMillis();
				if ((lastActivityTime + timeout) < now) {
					Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOCK_TIMEOUT);
				} else {
					this.schedule(checkIntervall);
				}
				
			}
		};
		activityTimer.schedule(checkIntervall);
		
	}
	
	private void setupDesktop(CMDBDesktopConfig config) {
		if (useWindowID(config)) {
			return;
		}
		
		ModelServiceFactory.get().checkForNewUpdate(CMDBSession.get().getToken(), false, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			public void onSuccess(String result) {
				if (result != null) {
					Info.display("Updates", "New updates are now available");
				}
			}
		});
		
		// Setup default values.
		CMDBSession.get().getConfig().set("defaultWindowHeight", config.get("defaultHeight"));
		CMDBSession.get().getConfig().set("defaultWindowWidth", config.get("defaultWidth"));
		
		
		TaskBar taskBar = desktop.getTaskBar();
		 
		
		
		
		/*
		for (int i = 0; i < menu.getItemCount(); i++) {
			Item item = menu.getItem(i);
			if (item != null) {
				menu.remove(item);
			}
		}
		*/
		String defaultRole = CMDBSession.get().getDefaultRole();
		
		String info = CMDBSession.get().getUsername();
		if (defaultRole != null) {
			info = defaultRole + "/" + info;
		}
		

	    MenuItem logout = new MenuItem("Logout", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOGOUT);
			}
	    	
	    });
	    logout.setIconStyle("logout");
	    
	    MenuItem roles = null;
	    if (defaultRole != null) {
		    roles = new MenuItem("Roles");
		    final Menu roleMenu = new Menu();  
		    
		    for (final String role : CMDBSession.get().getRoles()) {
		    	     final CheckMenuItem r = new CheckMenuItem("Role " + role);
		    	     r.addSelectionListener(new SelectionListener<ComponentEvent>() {

						@Override
						public void componentSelected(ComponentEvent ce) {
							
							DeferredCommand.addCommand(new Command() {

								public void execute() {
									if (desktop.getTaskBar().getStartMenu().isVisible()) {
										desktop.getTaskBar().getStartMenu().hide(true);
									}
									
									MessageBox.confirm("Change Role", "Change role to " + role, new Listener<WindowEvent>() {

										public void handleEvent(WindowEvent be) {
											//Dialog dialog = (Dialog) ce.component;  
											Button btn = be.buttonClicked;
											if (btn.getItemId().equals(Dialog.YES)) {
												Dispatcher.get().dispatch(CMDBEvents.DESKTOP_CHANGE_ROLE, role);	
											} 
										}
										
									});
								}
							
							});
						}
		    	     });  
			         r.setGroup("roles");  
			         r.setChecked(role.equals(defaultRole));  
			         roleMenu.add(r);
		    }
		    roles.setSubMenu(roleMenu);
		    
	    } 
	    
	    MenuItem about = new MenuItem("About", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				Dispatcher.get().dispatch(CMDBEvents.DESKTOP_ABOUT);
			}
	    	
	    });
	    about.setIconStyle("about-icon");
	  
	    MenuItem feedback = new MenuItem("Feedback");
	    feedback.setIconStyle("feedback-icon");     
	    Menu feedbackMenu = new Menu();
	    MenuItem good = new MenuItem("Good", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				CMDBDesktopWindowItem about = new CMDBDesktopWindowItem();
				about.setID(CMDBURLFrameWidget.ID);
				about.getParams().set("url", "http://www.onecmdb.org/feedback/good.html" + 
						"?version=" + Version.getVersionString() + 
						"&action=" + "Menu");
				about.setHeading("Good Feedback on OneCMDB");
				WindowFactory.showWindow(desktop, about);
			}
	    	
	    });
	    good.setIconStyle("feedback-good");
	    MenuItem bad = new MenuItem("Bad", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				
				CMDBDesktopWindowItem about = new CMDBDesktopWindowItem();
				about.setID(CMDBURLFrameWidget.ID);
				about.getParams().set("url", "http://www.onecmdb.org/feedback/bad.html" + 
						"?version=" + Version.getVersionString() + 
						"&action=" + "Menu");
				about.setHeading("Bad Feedback on OneCMDB");
				WindowFactory.showWindow(desktop, about);
			}
	    	
	    });
	    bad.setIconStyle("feedback-bad");
		  
	    feedbackMenu.add(good);
	    feedbackMenu.add(bad);
	    feedback.setSubMenu(feedbackMenu);
	    
	    
		
	    MenuItem updates = new MenuItem("Updates", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				final MessageBox info = MessageBox.wait("Progress",  
			             "Check onecmdb.org for updates", "Please Wait...");  
				ModelServiceFactory.get().checkForNewUpdate(CMDBSession.get().getToken(), true, new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						info.close();
						MessageBox.alert("Problem", "Can't contact update server", null);
					}

					public void onSuccess(String result) {
						info.close();
						if (result == null) {
							result = "<html><b>No updates found...</b></html>";
						}
						Dialog simple = new Dialog();  
						simple.setHeading("OneCMDB Update Info");  
						simple.setButtons(Dialog.OK);  
						simple.setBodyStyleName("pad-text");  
						simple.addText(result);  
						simple.setScrollMode(Scroll.AUTO);  
						simple.setHideOnButtonClick(true);  
						simple.setSize(300, 300);
						simple.show();
					}
					
				});
			}
	    	
	    });
	    updates.setIconStyle("update-icon");
	    updates.setToolTip("Check if any updates are avaliable");
	   
	    MenuItem showDesktop = new MenuItem("Desktop", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				for (Window w : desktop.getWindows()) {
					if (w.isVisible()) {
						w.minimize();
					}
				}
			}
	    });
	    showDesktop.setIconStyle("desktop-icon");
	    showDesktop.setToolTip("Minimize all windows on the desktop");
	    

	    MenuItem register = new MenuItem("Register", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				showRegisterWindow();
			}
	    });
	    register.setIconStyle("register-icon");
	    register.setToolTip("Register your self");

	    MenuItem lockDesktop = new MenuItem("Lock", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				Dispatcher.get().dispatch(CMDBEvents.DESKTOP_LOCK_TIMEOUT);
			}
	    });
	    lockDesktop.setIconStyle("desktop-lock-icon");
	    lockDesktop.setToolTip("Lock the desktop with a login window.");
	   

	    MenuItem quickHelpDesktop = new MenuItem("Quick Help");
	    quickHelpDesktop.setIconStyle("help-icon");
	    Menu quickHelpMenu = new Menu();
	    quickHelpDesktop.setSubMenu(quickHelpMenu);
	    final MenuItem enableQuickHelpDesktop = new MenuItem("Enable");
	    enableQuickHelpDesktop.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				updateQuickHelp(false);
			}
	    	
	    });
	    final MenuItem disableQuickHelpDesktop = new MenuItem("Disable");
	    disableQuickHelpDesktop.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				updateQuickHelp(true);
			}
	    	
	    });
	    quickHelpMenu.add(enableQuickHelpDesktop);
	    quickHelpMenu.add(disableQuickHelpDesktop);
		quickHelpMenu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				if (CMDBSession.get().getUserPreference().hideQuickHelp()) {
					enableQuickHelpDesktop.setEnabled(true);
					disableQuickHelpDesktop.setEnabled(false);
				} else {
					enableQuickHelpDesktop.setEnabled(false);
					disableQuickHelpDesktop.setEnabled(true);
				}
			}
			
		});
	    
	    
	    lockDesktop.setIconStyle("desktop-lock-icon");
	    lockDesktop.setToolTip("Lock the desktop with a login window.");

	    
	    StartMenu menu = taskBar.getStartMenu();
		// Setup layout of tools
	    menu.setHeading(info);
		menu.setIconStyle("user");
	    menu.addTool(logout);
		if (roles != null) {	
			menu.addTool(roles);
		}
		menu.addTool(showDesktop);
		menu.addTool(lockDesktop);
		
		menu.addToolSeperator();
		menu.addTool(quickHelpDesktop);
		menu.addToolSeperator();
	    
		menu.addTool(feedback);
	    menu.addTool(updates);
	    menu.addTool(register);
	    menu.addToolSeperator();
	    
	    menu.addTool(about);
	      
	    List<CMDBDesktopMenuItem> menus = config.getMenuItems();
		for (final CMDBDesktopMenuItem item : menus) {
			// Setup menu
			menu.add(DesktopMenuFactory.getMenuItem(item));
		
			// Setup shortcuts.
			DesktopMenuFactory.updateShortcuts(desktop, item);
		}
		
		// Open registration window...
		if (CMDBSession.get().showRegistration()) {
			showRegisterWindow();
		}
		
		
	}

	protected void updateQuickHelp(boolean b) {
		UserPreference pref = CMDBSession.get().getUserPreference();
		pref.setHideQuickHelp(b);
		ModelServiceFactory.get().saveUserPreferences(CMDBSession.get().getToken(), 
				CMDBSession.get().getUsername(), pref, new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						Info.display("User Preferences", "Save failed!!!");
					}

					public void onSuccess(Void result) {
						Info.display("User Preferences", "Saved");
					}
			
		});
	}

	protected void showRegisterWindow() {
		CMDBDesktopMenuItem regItem = new CMDBDesktopMenuItem();
		CMDBDesktopWindowItem wItem = new CMDBDesktopWindowItem();
		wItem.setID(CMDBURLFrameWidget.ID);
		wItem.setHeading("Register to OneCMDB");
		wItem.setWidth("600px");
		BaseModel params = new BaseModel();
		params.set("url", "http://www.onecmdb.org/reg/reg.php?id=" + CMDBSession.get().getInstallId());
		wItem.setParams(params);
		regItem.getWindowItem(wItem);
		Dispatcher.get().dispatch(new AppEvent<CMDBDesktopMenuItem>(CMDBEvents.DESKTOP_MENU_SELECTED, regItem ));
	}
	
}
