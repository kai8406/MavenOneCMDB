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
package org.onecmdb.ui.gwt.itil.client.application.problem.screen;

import java.util.Arrays;
import java.util.List;

import org.gwtiger.client.widget.field.Validate;
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
import org.onecmdb.ui.gwt.toolkit.client.view.ci.LabelCounter;
import org.onecmdb.ui.gwt.toolkit.client.view.input.AttributeRender;
import org.onecmdb.ui.gwt.toolkit.client.view.input.ValidateVerticalPanel;
import org.onecmdb.ui.gwt.toolkit.client.view.input.basefield.NewTextFieldWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.popup.DragablePopup;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.EditCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.table.CITablePageControlPanel;
import org.onecmdb.ui.gwt.toolkit.client.view.table.CITablePanel;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditProblemScreen extends EditCIScreen {
	
	TextAttributeControl addHistory = new TextAttributeControl("Action Note", false, false, TextAttributeControl.TEXT_BOX_TYPE, new Integer(1), null);
	TextAttributeControl addSolution = new TextAttributeControl("solution", false, false, TextAttributeControl.TEXT_AREA_TYPE, new Integer(5), null);
	private ValidateVerticalPanel actionHistory;
	private HTML actionInfo;
	private CIReferenceTableControl refIncidentControl;
	private HorizontalPanel incidentConnectedPanel = new HorizontalPanel();
	
	private AttributeControl[] orders = new AttributeControl[] {
			new AttributeControl("ID", true, false),
			new AttributeControl("title", false, true), 
			new CIDescriptionControl(),
			//new TextAttributeControl("opDescription", false, false, TextAttributeControl.TEXT_AREA_TYPE, new Integer(5), null),
			new AttributeControl("affectedCIs", false, false),
			//new AttributeControl("problem", true, false),
			new AttributeControl("priority", false, false),
			new AttributeControl("status", true, false),
			/*
			new AttributeControl("reportedBy", true, false),
			new AttributeControl("reportedDate", true, false),
			*/
			new AttributeControl("ticketIssuer", true, false),
			new AttributeControl("actionHistory", true, false),
			//addHistory,
			addSolution
	};
	
	private static DefaultAttributeFilter defFilter = new DefaultAttributeFilter();
	
	public EditProblemScreen() {
		super();
		setTitleText("Edit Problem/Known Error");
		defFilter.setAttributeControl(Arrays.asList(orders));
	}
	
	public void onLoadComplete(Object sender) {
		showLoading(false);
		updateActionInfo();
	}

	
	protected CellPanel getMainPanel() {
		return(new HorizontalPanel());
	}
	
	public void load(String objectType, Long objectId) {
		input.clear();
		updateConnectedIncidents(objectType);
		update(objectType);
	}

	protected void updateConnectedIncidents(String objectType) {
		// Add Number of incidents connected to this problem.
		refIncidentControl = new CIReferenceTableControl(objectType, "<$template{ITIL_Incident}", "ITIL_Incident");
		LabelCounter counter = new LabelCounter("Incidents connected", refIncidentControl);
		
		Button viewIncidents = new Button("View");
		viewIncidents.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				DefaultAttributeFilter aFilter = new DefaultAttributeFilter();
				
				aFilter.setSimpleAttributeControl(ListProblemScreen.getOrder());
				refIncidentControl.setAttributeFilter(aFilter);
			
				CITablePanel table = new CITablePanel();
				table.setAttributeRender(new AttributeRender());
				table.setTabelControl(refIncidentControl);
				CITablePageControlPanel tablePageControl = new CITablePageControlPanel(table);
				
				table.load();
					
				VerticalPanel vPanel = new VerticalPanel();
				vPanel.add(tablePageControl);
				vPanel.add(table);
				vPanel.setCellVerticalAlignment(tablePageControl, VerticalPanel.ALIGN_TOP);
				vPanel.setCellVerticalAlignment(table, VerticalPanel.ALIGN_TOP);
				final DragablePopup popup = new DragablePopup("Incidents", true);
				popup.setContent(vPanel);
				
				int top = dockPanel.getAbsoluteTop() + 50;
				int left = dockPanel.getAbsoluteLeft() + 50;
				popup.setPopupPosition(left, top);  
				popup.show();	
			}
			
		});
		
		
		counter.update();
			
		incidentConnectedPanel.clear();
		incidentConnectedPanel.add(counter);
		incidentConnectedPanel.add(viewIncidents);
		incidentConnectedPanel.setCellHorizontalAlignment(counter, HorizontalPanel.ALIGN_LEFT);
		incidentConnectedPanel.setCellHorizontalAlignment(viewIncidents, HorizontalPanel.ALIGN_RIGHT);
		
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
					actionInfo.setHTML("This Problem has status <em>" + displayName + "</em>.<br/> What do you like to do now?");
				}
				
			}
			
		});
		
	}


	public IAttributeFilter getAttributeFilter() {
		return(defFilter);
	}
	public Widget getButtonPanel() {
		VerticalPanel vPanel = new VerticalPanel();
		Label actionHeader = new Label("Take Action on this Problem", false);
		actionHeader.setStyleName("mdv-form-label");
		vPanel.add(actionHeader);


		actionInfo = new HTML("This Problem has status (Loading). <br/>" +
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
		Button problem = new Button("OK", new ClickListener() {

			public void onClick(Widget sender) {
				saveAsProblem();
			}
		});
		HorizontalPanel problemAction = new HorizontalPanel();
		HTML problemText = new HTML("<b>Save this Problem with status <em>Problem</em></b>");
		problemText.setTitle("Update Action history and " +
				"save the Problem with status " +
		"<em>Problem</em>.");
		problemAction.add(problemText);
		problemAction.add(problem);								
		problemAction.setCellHorizontalAlignment(problem, HorizontalPanel.ALIGN_RIGHT);
		problemAction.setCellHorizontalAlignment(problemText, HorizontalPanel.ALIGN_LEFT);
		problemAction.setStyleName("incident-action-select");

		// Close Action.
		Button knownError = new Button("OK", new ClickListener() {

			public void onClick(Widget sender) {
				saveAsKnownError();
			}
		});
		HorizontalPanel knownErrorAction = new HorizontalPanel();
		HTML knownErrorText = new HTML("<b>Save this Problem with status <em>Known Error</em></b>");
		knownErrorText.setTitle("Update Action history and " +
				"save the Problem with status <em>" +
		"Known Error<em>");

		knownErrorAction.add(knownErrorText);
		knownErrorAction.add(knownError);								
		knownErrorAction.setCellHorizontalAlignment(knownError, HorizontalPanel.ALIGN_RIGHT);
		knownErrorAction.setCellHorizontalAlignment(knownErrorText, HorizontalPanel.ALIGN_LEFT);
		knownErrorAction.setStyleName("incident-action-select");
	
		incidentConnectedPanel.setStyleName("incident-action-select");

		
		vPanel.add(problemAction);
		vPanel.add(knownErrorAction);
		vPanel.add(actionHistory);
		vPanel.add(incidentConnectedPanel);
		return(vPanel);
	}
	
	/*
	protected Widget getButtonPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		Button knownError = new Button("Known Error", new ClickListener() {

			public void onClick(Widget sender) {
				saveAsKnownError();
			}

			
		});
		
		Button problem = new Button("Problem", new ClickListener() {

			public void onClick(Widget sender) {
				saveAsProblem();			
			}
			
		});
		problem.setTitle("Save this problem with a added Comment");
		knownError.setTitle("Save this problem as an Known Error with a Solution/Workaround");
		
		panel.add(problem);
		panel.add(knownError);
		panel.setCellHorizontalAlignment(problem, HorizontalPanel.ALIGN_CENTER);
		panel.setCellHorizontalAlignment(knownError, HorizontalPanel.ALIGN_CENTER);
			
		return(panel);
	}
	*/
	
	protected void saveAsProblem() {
		addSolution.setRequiered(false);
		if (!super.validate()) {
			return;				
		}
		if (actionHistory instanceof Validate) {
			addHistory.setRequiered(true);
			if (!((Validate)actionHistory).validate()) {
				return;
			}
		}
		GWT_ValueBean vBean = control.getLocal().fetchAttributeValueBean("status", 0);
		vBean.setComplexValue(true);
		vBean.setValue("problemStatus_Problem");
	
		Object object = addHistory.getAttributeValue();
		String action = ((AttributeValue)object).getStringValue();
		// Create a Action History entry.
		allocNewActionHistory(action, new AsyncCallback() {

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
					
					// Make sure all incidents is marked as problem
					fetchIncidents(new AsyncCallback() {

						public void onFailure(Throwable caught) {
							setErrorText("Can't update incident status");
							
						}

						public void onSuccess(Object result) {
							if (result instanceof List) {
								List l = (List)result;
								for (int i = 0; i < l.size(); i++) {
									GWT_CiBean incident = refIncidentControl.getObject(i+1);
									if (incident != null) {
										GWT_CiBean copy = incident.copy();
										GWT_ValueBean vBean = copy.fetchAttributeValueBean("status", 0);
										if (vBean != null) {
											vBean.setValue("incidentStatus_Problem");
											control.addModifiedBean(copy, incident);
										}
										
									}
								}
								save();
								return;
							}
							
						}
						
					});
					
				}
			}
		});
	
	}
	
	protected void fetchIncidents(AsyncCallback callback) {
		// Select all.
		refIncidentControl.setMaxResult(null);
		refIncidentControl.setFirstItem(null);
		
		refIncidentControl.getRows(callback);
	}





	private void saveAsKnownError() {
		addSolution.setRequiered(true);
		if (!super.validate()) {
			return;
		}
		if (actionHistory instanceof Validate) {
			addHistory.setRequiered(true);
			if (!((Validate)actionHistory).validate()) {
				return;
			}
		}

		GWT_ValueBean vBean = control.getLocal().fetchAttributeValueBean("status", 0);
		vBean.setComplexValue(true);
		vBean.setValue("problemStatus_KnownError");
		
		// Create a Action History entry.
		allocNewActionHistory("Marked this as a Known Problem", new AsyncCallback() {

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
					// Make sure all incidents is marked as problem
					fetchIncidents(new AsyncCallback() {

						public void onFailure(Throwable caught) {
							setErrorText("Can't update incident status");
							
						}

						public void onSuccess(Object result) {
							if (result instanceof List) {
								List l = (List)result;
								for (int i = 0; i < l.size(); i++) {
									GWT_CiBean incident = refIncidentControl.getObject(i+1);
									if (incident != null) {
										GWT_CiBean copy = incident.copy();
										GWT_ValueBean vBean = copy.fetchAttributeValueBean("status", 0);
										if (vBean != null) {
											vBean.setValue("incidentStatus_KnownError");
											control.addModifiedBean(copy, incident);
										}
										
									}
								}
								save();
								return;
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
