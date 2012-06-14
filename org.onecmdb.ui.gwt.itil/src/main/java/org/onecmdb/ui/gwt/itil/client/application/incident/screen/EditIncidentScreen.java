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
package org.onecmdb.ui.gwt.itil.client.application.incident.screen;


import java.util.Arrays;

import org.gwtiger.client.widget.field.Validate;
import org.onecmdb.ui.gwt.itil.client.ITILApplication;
import org.onecmdb.ui.gwt.itil.client.application.problem.screen.ListProblemScreen;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBUtils;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.AttributeValue;
import org.onecmdb.ui.gwt.toolkit.client.control.input.CIDescriptionControl;
import org.onecmdb.ui.gwt.toolkit.client.control.input.DefaultAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.IAttributeFilter;
import org.onecmdb.ui.gwt.toolkit.client.control.input.TextAttributeControl;
import org.onecmdb.ui.gwt.toolkit.client.control.table.CIReferenceTableControl;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeRender;
import org.onecmdb.ui.gwt.toolkit.client.view.input.ValidateVerticalPanel;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewTextAreaFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewTextFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.DragablePopup;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.EditCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.table.CITablePageControlPanel;
import org.onecmdb.ui.gwt.toolkit.client.view.table.CITablePanel;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditIncidentScreen extends EditCIScreen  {
	
	/*
	AttributeValue uiMessage = new AttributeValue("message", "xs:string", false, true);
	AttributeValue newProblemMessage = new AttributeValue("message", "xs:string", false, true);
	AttributeValue problemMessage = new AttributeValue("message", "ITIL_Problem", true, true);
	AttributeValue knownErrorMessage = new AttributeValue("message", "ITIL_KnownError", true, true);
	AttributeValue closeMessage = new AttributeValue("message", "xs:string", false, true);
	*/
	TextAttributeControl addHistory = new TextAttributeControl("Action Note", false, false, TextAttributeControl.TEXT_BOX_TYPE, new Integer(1), null);
	AttributeControl problemControl = new AttributeControl("problem", true, false);
	private HTML actionInfo;
	
	private AttributeControl[] ctrls = new AttributeControl[] {
			new AttributeControl("ID", true, false),
			new AttributeControl("title", false, true), 
			new CIDescriptionControl(),
			//new TextAttributeControl("opDescription", false, false, TextAttributeControl.TEXT_AREA_TYPE, new Integer(5), null),
			new AttributeControl("affectedCIs", false, false),
			problemControl,
			new AttributeControl("priority", false, true),
			new AttributeControl("status", true, false),
			new AttributeControl("reportedBy", true, false),
			new AttributeControl("reportDate", true, false),
			new AttributeControl("ticketIssuer", true, false),
			new AttributeControl("actionHistory", true, false),
	};
	//private TextFieldWidget actionHistory;
	private ValidateVerticalPanel actionHistory;
	
	
	private static DefaultAttributeFilter defFilter = new DefaultAttributeFilter();
	
	public EditIncidentScreen() {
		super();
		setTitleText("Edit Incident");
		defFilter.setAttributeControl(Arrays.asList(ctrls));
		problemControl.setClickListener(getShowProblemListener());
	}
	
	
	private ClickListener getShowProblemListener() {
		return(new ClickListener() {

			public void onClick(Widget sender) {
				GWT_ValueBean v = control.getLocal().fetchAttributeValueBean("problem", 0);
				if (v != null) {
					ITILApplication.get().showScreen(ITILApplication.VIEW_PROBLEM_SCREEN, 
							v.getValue(), 
							new Long(0));
				}
				
			}
			
		});
	}

	protected CellPanel getMainPanel() {
		return(new HorizontalPanel());
	}

	
	public IAttributeFilter getAttributeFilter() {
		return(defFilter);
	}
	
	protected void updateActionInfo() {
		GWT_ValueBean vBean = control.getLocal().fetchAttributeValueBean("status", 0);
		String alias = vBean.getValue();
		OneCMDBConnector.getCIFromAlias(alias, new AsyncCallback() {

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					String displayName = ((GWT_CiBean)result).getDisplayName();
					actionInfo.setHTML("This incident has status <em>" + displayName + "</em>.<br/> What do you like to do now?");
				}
				
			}
			
		});
		
	}
	
	
	public void onLoadComplete(Object sender) {
		updateActionInfo();
	}

	protected Widget getButtonPanel() {
		
		
		VerticalPanel vPanel = new VerticalPanel();
		Label actionHeader = new Label("Take Action on this Incident", false);
		actionHeader.setStyleName("mdv-form-label");
		vPanel.add(actionHeader);
		

		actionInfo = new HTML("This incident has status (Loading). <br/>" +
				"What do you like to do now?", true);
		actionInfo.setStyleName("one-action-header");
		vPanel.add(actionInfo);
		
		// Action history
		AttributeRender render = new AttributeRender();
		
		//actionHistory = render.getWidget();
		Label actionNote = new Label("Action Note");
		actionNote.setStyleName("mdv-form-label");
		final NewTextFieldWidget field = new NewTextFieldWidget((AttributeValue)addHistory.allocAttributeValue(null, null));
		field.getBaseField().setVisible(false);
		actionHistory = new ValidateVerticalPanel(field);
		actionHistory.add(field);
		// Workaround to get the input box below label.
		// Didn't what to modify gwtiger framework right now....
		final TextBox box = new TextBox();
		box.addKeyboardListener(new KeyboardListener() {

			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				// TODO Auto-generated method stub
				
			}

			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				// TODO Auto-generated method stub
				
			}

			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				field.setText(box.getText());
				
			}
			
		});
		box.setStyleName("mdv-form-input");
		actionHistory.add(box);
		//actionHistory = new NewTextFieldWidget("New Action", "250px");
		//actionHistory.setRequired(true);
		
		//actionHistory.setStyleName("one-action-input");
		
		
		// UI Action.
		Button ui = new Button("OK", new ClickListener() {

			public void onClick(Widget sender) {
				saveAs("ui", addHistory);
			}
		});
		HorizontalPanel uiAction = new HorizontalPanel();
		HTML uiText = new HTML("<b>Save this Incident with status <em>Open</em></b>");
		uiText.setTitle("Update Action history and " +
				"save the Incident with status " +
				"<em>Open</em>.");
		uiAction.add(uiText);
		uiAction.add(ui);								
		uiAction.setCellHorizontalAlignment(ui, HorizontalPanel.ALIGN_RIGHT);
		uiAction.setCellHorizontalAlignment(uiText, HorizontalPanel.ALIGN_LEFT);
		uiAction.setStyleName("incident-action-select");
		
		// Close Action.
		Button close = new Button("Close", new ClickListener() {

			public void onClick(Widget sender) {
				saveAs("close", addHistory);
			}
		});
		HorizontalPanel closeAction = new HorizontalPanel();
		HTML closeText = new HTML("<b>Close and Archive this Incident</b>");
		closeText.setTitle("Update Action history and " +
				"save the Incident with status " +
				"Closed.");
		
		closeAction.add(closeText);
		closeAction.add(close);								
		closeAction.setCellHorizontalAlignment(close, HorizontalPanel.ALIGN_RIGHT);
		closeAction.setCellHorizontalAlignment(closeText, HorizontalPanel.ALIGN_LEFT);
		closeAction.setStyleName("incident-action-select");
	
		// New problem
		Button newProblem = new Button("OK", new ClickListener() {
			public void onClick(Widget sender) {
				saveAs("newProblem", addHistory);
			}
		});
		HorizontalPanel newProblemAction = new HorizontalPanel();
		
		
		HTML newProblemText = new HTML("<b>Associate this Incident with a <em>New Problem</em></b>");
		newProblemText.setTitle("Update Action history, " +
				"create a new Problem and " +
				"link it to that Problem and save " +
				"the incident with status Problem");
		
		newProblemAction.add(newProblemText);
		newProblemAction.add(newProblem);	
		newProblemAction.setCellHorizontalAlignment(newProblem, HorizontalPanel.ALIGN_RIGHT);
		newProblemAction.setCellHorizontalAlignment(newProblemText, HorizontalPanel.ALIGN_LEFT);
		newProblemAction.setStyleName("incident-action-select");
		
		
		
		// Find problem
		HorizontalPanel problemAction = new HorizontalPanel();
		final Button problem = new Button("OK");
		problem.addClickListener(getProblemSelector(true));
		HTML problemText = new HTML("<b>Associate this Incident with an existing <em>Problem</em></b>");
		problemText.setTitle("Update Action history, " +
				"link it to a Problem and save the " +
				"incident with status Problem");
		problemAction.add(problemText);
		problemAction.add(problem);	
		problemAction.setCellHorizontalAlignment(problem, HorizontalPanel.ALIGN_RIGHT);
		problemAction.setCellHorizontalAlignment(problemText, HorizontalPanel.ALIGN_LEFT);
		problemAction.setStyleName("incident-action-select");
		
		// Find Knownerror.
		HorizontalPanel knownErrorAction = new HorizontalPanel();
		final Button error = new Button("OK");
		error.addClickListener(getProblemSelector(false));
		HTML errorText = new HTML("<b>Associate this Incident with an existing <em>Known Error</em></b>");
		errorText.setTitle("Update Action history, " +
				"link it to a Known Error and save the " +
				"incident with status Known Error");
		knownErrorAction.add(errorText);
		knownErrorAction.add(error);	
		knownErrorAction.setCellHorizontalAlignment(error, HorizontalPanel.ALIGN_RIGHT);
		knownErrorAction.setCellHorizontalAlignment(errorText, HorizontalPanel.ALIGN_LEFT);
		knownErrorAction.setStyleName("incident-action-select");

		
		vPanel.add(uiAction);
		vPanel.add(problemAction);
		vPanel.add(knownErrorAction);
		vPanel.add(newProblemAction);
		vPanel.add(closeAction);
		vPanel.add(actionHistory);
		return(vPanel);
	}
	

	public void saveAs(String action, Object arg) {
		
		// Validate..
		if (!super.validate()) {
			return;
		}
		addHistory.setRequiered(false);
		
		if (action.equals("ui")) {
			if (actionHistory instanceof Validate) {
				addHistory.setRequiered(true);
				if (!((Validate)actionHistory).validate()) {
					return;
				}
			}
			saveAsUI(arg);
		}
		if (action.equals("newProblem")) {
			if (actionHistory instanceof Validate) {
				addHistory.setRequiered(true);
				if (!((Validate)actionHistory).validate()) {
					return;
				}
			}
			saveAsNewProblem(arg);
		}
		if (action.equals("problem")) {
			saveAsProblem(arg);
		}
		if (action.equals("knownError")) {
			saveAsKnownError(arg);
		}
		if (action.equals("close")) {
			if (actionHistory instanceof Validate) {
				addHistory.setRequiered(true);
				if (!((Validate)actionHistory).validate()) {
					return;
				}
			}
			saveAsClosed(arg);
		}
	}
	
	protected ClickListener getProblemSelector(final boolean findProblem) {
		return(new ClickListener() {
			public void onClick(Widget sender) {
				CIReferenceTableControl ref = null;
				if (findProblem) {
					ref = new CIReferenceTableControl("problemStatus_Problem", "<$template{ITIL_Problem}", "ITIL_Problem");
				} else {
					ref = new CIReferenceTableControl("problemStatus_KnownError", "<$template{ITIL_Problem}", "ITIL_Problem");
				}
				final CIReferenceTableControl refControl = ref; 
				DefaultAttributeFilter aFilter = new DefaultAttributeFilter();
				
				aFilter.setSimpleAttributeControl(ListProblemScreen.getOrder());
				refControl.setAttributeFilter(aFilter);
			
				CITablePanel table = new CITablePanel();
				table.setAttributeRender(new AttributeRender());
				table.setTabelControl(refControl);
				CITablePageControlPanel tablePageControl = new CITablePageControlPanel(table);
			
				table.load();
				
				
				VerticalPanel vPanel = new VerticalPanel();
				vPanel.add(tablePageControl);
				vPanel.add(table);
				vPanel.setCellVerticalAlignment(tablePageControl, VerticalPanel.ALIGN_TOP);
				vPanel.setCellVerticalAlignment(table, VerticalPanel.ALIGN_TOP);
				String title = "Find Known Error";
				if (findProblem) {
					title = "Find Problem";
				}
				final DragablePopup popup = new DragablePopup(title, false);
				popup.setContent(vPanel);
				
				int top = dockPanel.getAbsoluteTop() + 50;
				int left = dockPanel.getAbsoluteLeft() + 50;
				popup.setPopupPosition(left, top);  
				
				table.addTableListener(new TableListener() {
		
					public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
						String alias = refControl.getObjectName(row, cell);
						if (alias != null) {
							String msg = "Save incident as a Known Error";
							if (findProblem) {
								msg = "Save incident as a Problem";
							}
							if (Window.confirm(msg)) {
								((AttributeValue)problemControl.getAttributeValue()).setValue(alias);
								if (findProblem) {
									saveAs("problem", problemControl);
								} else {
									saveAs("knownError", problemControl);
								}
							}
						}
						popup.hide();
					}
					public void onClick(Widget sender) {
						// TODO Auto-generated method stub
						
					}
				});
		
				popup.show();
			}
		});
	}
	
	private void saveAsClosed(Object arg) {
		GWT_ValueBean vBean = control.getLocal().fetchAttributeValueBean("status", 0);
		vBean.setComplexValue(true);
		vBean.setValue("incidentStatus_Closed");
		// Create a Action History entry.
		AttributeValue aValue = (AttributeValue)addHistory.getAttributeValue();
		allocNewActionHistory(aValue.getStringValue(), new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("ERROR: " + caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					GWT_ValueBean actionRef = new GWT_ValueBean();
					actionRef.setAlias("actionHistory");
					actionRef.setComplexValue(true);
					actionRef.setValue(((GWT_CiBean)result).getAlias());
					control.getLocal().addAttributeValue(actionRef);
					control.addNewBean(((GWT_CiBean)result));
					save();
				}
			}
			
		});
	}

	private void saveAsKnownError(Object arg) {
		
		GWT_ValueBean vBean = control.getLocal().fetchAttributeValueBean("status", 0);
		vBean.setComplexValue(true);
		vBean.setValue("incidentStatus_KnownError");
		// Create a Action History entry.
		allocNewActionHistory("Mark it as a known error", new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("ERROR: " + caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					// Add Action history
					GWT_ValueBean actionRef = new GWT_ValueBean();
					actionRef.setAlias("actionHistory");
					actionRef.setComplexValue(true);
					actionRef.setValue(((GWT_CiBean)result).getAlias());
					
					// Connect the incident to the problem.
					// Need to load the problem and then update that value to this.
					/*
					GWT_ValueBean problemValue = control.getLocal().fetchAttributeValueBean("problem", 0);
					problemValue.setValue(knownErrorMessage.getStringValue());
					*/
					control.getLocal().addAttributeValue(actionRef);
					control.addNewBean(((GWT_CiBean)result));
					
					save();
				}
			}
			
		});
	}

	private void saveAsProblem(Object arg) {
		
		GWT_ValueBean vBean = control.getLocal().fetchAttributeValueBean("status", 0);
		vBean.setComplexValue(true);
		vBean.setValue("incidentStatus_Problem");
		// Create a Action History entry.
		allocNewActionHistory("Mark it as a problem", new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("ERROR: " + caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					// Add Action history
					GWT_ValueBean actionRef = new GWT_ValueBean();
					actionRef.setAlias("actionHistory");
					actionRef.setComplexValue(true);
					actionRef.setValue(((GWT_CiBean)result).getAlias());
					
					// Connect the problem to this incident.
					// Need to load the problem and then update that value to this.
					// Connect the incident to the problem.
					// Need to load the problem and then update that value to this.
					/*
					GWT_ValueBean problemValue = control.getLocal().fetchAttributeValueBean("problem", 0);
					problemValue.setValue(knownErrorMessage.getStringValue());
					*/
					
					control.getLocal().addAttributeValue(actionRef);
					control.addNewBean(((GWT_CiBean)result));
					
					save();
				}
			}
			
		});
		
	}

	private void saveAsNewProblem(Object arg) {
		if (arg instanceof Validate) {
			if (!((Validate)arg).validate()) {
				return;
			}
		}
		GWT_ValueBean vBean = control.getLocal().fetchAttributeValueBean("status", 0);
		vBean.setComplexValue(true);
		vBean.setValue("incidentStatus_Problem");
		// Create a Action History entry.
		allocNewActionHistory("Saved as a problem", new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("ERROR: " + caught);
			}

			public void onSuccess(final Object newActionHistory) {
				if (newActionHistory instanceof GWT_CiBean) {
					allocateNewProblem(new AsyncCallback() {

						public void onFailure(Throwable caught) {
							// Show error.
							setErrorText("ERROR: " + caught.getMessage());
						}

						public void onSuccess(Object newProblem) {
							if (newProblem instanceof GWT_CiBean) {
								
								// Connect the incident to the problem.
								// Need to load the problem and then update that value to this.
								GWT_ValueBean problemValue = control.getLocal().fetchAttributeValueBean("problem", 0);
								problemValue.setValue(((GWT_CiBean)newProblem).getAlias());
								
								// Connect the action history
								GWT_ValueBean actionRef = new GWT_ValueBean();
								actionRef.setAlias("actionHistory");
								actionRef.setComplexValue(true);
								actionRef.setValue(((GWT_CiBean)newActionHistory).getAlias());
								control.getLocal().addAttributeValue(actionRef);
								control.addNewBean(((GWT_CiBean)newActionHistory));
								control.addNewBean(((GWT_CiBean)newProblem));
								save();
							}
						}
						
					});
				}
			}

		});
	}

	protected void saveAsUI(Object arg) {
		GWT_ValueBean vBean = control.getLocal().fetchAttributeValueBean("status", 0);
		vBean.setComplexValue(true);
		vBean.setValue("incidentStatus_UI");
		// Create a Action History entry.
		AttributeValue aValue = (AttributeValue)addHistory.getAttributeValue();
		
		allocNewActionHistory(aValue.getStringValue(), new AsyncCallback() {

			public void onFailure(Throwable caught) {
				setErrorText("ERROR: " + caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					GWT_ValueBean actionRef = new GWT_ValueBean();
					actionRef.setAlias("actionHistory");
					actionRef.setComplexValue(true);
					actionRef.setValue(((GWT_CiBean)result).getAlias());
					control.getLocal().addAttributeValue(actionRef);
					control.addNewBean(((GWT_CiBean)result));
					save();
				}
			}
			
		});
	}
	
	private void allocateNewProblem(final AsyncCallback callback) {
		final GWT_CiBean bean = new GWT_CiBean();
		bean.setDerivedFrom("ITIL_Problem");
		bean.setTemplate(false);
		
		String xmlDateFormat = OneCMDBUtils.getXMLDateString();
		
		GWT_ValueBean reportDate = new GWT_ValueBean("reportDate", xmlDateFormat, false);
		bean.addAttributeValue(reportDate);
		
		GWT_ValueBean incidentTitle = control.getLocal().fetchAttributeValueBean("title", 0); 
		String  incidentTitleText = "";
		if (incidentTitle != null) {
			incidentTitleText = incidentTitle.getValue();
		}
			
		GWT_ValueBean title = new GWT_ValueBean("title", "Incident: " + incidentTitleText, false);
		bean.addAttributeValue(title);
		
		// Copy the description!
		bean.setDescription("Incident:" + control.getLocal().getDescription());
		
		// Alloc issuer
		loadTickIssuer().load(new AsyncCallback()  {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					GWT_ValueBean actionDate = new GWT_ValueBean("ticketIssuer", ((GWT_CiBean)result).getAlias(), true);
					bean.addAttributeValue(actionDate);
				
					// Alloc alias.
					OneCMDBConnector.getInstance().newInstanceAlias(OneCMDBSession.getAuthToken(),
							"ITIL_Problem", new AsyncCallback() {

						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						public void onSuccess(Object result) {
							if (result instanceof String) {
								bean.setAlias((String)result);
								
								// Set ID, should be done somewhere else.
								String alias = bean.getAlias();
								String split[] = alias.split("-");
								GWT_ValueBean id = new GWT_ValueBean("ID", split[1], false);
								bean.addAttributeValue(id);
							
								callback.onSuccess(bean);
							}
						}
					});
				}	
			}
			
		});
	}

	
	protected void allocNewActionHistory(String message, final AsyncCallback callback) {
		final GWT_CiBean bean = new GWT_CiBean();
		bean.setDerivedFrom("ActionHistory");
		bean.setTemplate(false);
		
		
		
		String xmlDateFormat = OneCMDBUtils.getXMLDateString();
		
		GWT_ValueBean actionDate = new GWT_ValueBean("actionDate", xmlDateFormat, false);
		bean.addAttributeValue(actionDate);
		GWT_ValueBean actionMessage = new GWT_ValueBean("actionMessage", message, false);
		bean.addAttributeValue(actionMessage);
		
		// Alloc issuer
		loadTickIssuer().load(new AsyncCallback()  {

			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					GWT_ValueBean actionDate = new GWT_ValueBean("actionIssuer", ((GWT_CiBean)result).getAlias(), true);
					bean.addAttributeValue(actionDate);
				
					// Alloc alias.
					OneCMDBConnector.getInstance().newInstanceAlias(OneCMDBSession.getAuthToken(), "ActionHistory", new AsyncCallback() {

						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						public void onSuccess(Object result) {
							if (result instanceof String) {
								bean.setAlias((String)result);
								callback.onSuccess(bean);
							}
						}
					});
				}	
			}
			
		});
		
		
		
		
		 
	
	}
	
	protected void onCommitSuccess(Object result) {
		History.back();		
	}
	
	
	
	
}
