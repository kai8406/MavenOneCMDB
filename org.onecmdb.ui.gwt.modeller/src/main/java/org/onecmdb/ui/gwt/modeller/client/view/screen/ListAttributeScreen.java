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
package org.onecmdb.ui.gwt.modeller.client.view.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.gwtiger.client.widget.HoverGridWidget;
import org.onecmdb.ui.gwt.modeller.client.OneCMDBModelCreator;
import org.onecmdb.ui.gwt.modeller.client.model.TemplateCache;
import org.onecmdb.ui.gwt.toolkit.client.control.AttributeComparator;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBConnector;
import org.onecmdb.ui.gwt.toolkit.client.control.OneCMDBSession;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_AttributeBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_CiBean;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_RfcResult;
import org.onecmdb.ui.gwt.toolkit.client.model.onecmdb.GWT_ValueBean;
import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.OneCMDBBaseScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListAttributeScreen extends OneCMDBBaseScreen {

	private static final int NAME_COL = 0;
	private static final int ALIAS_COL = 1;
	private static final int TYPE_COL = 2;
	private static final int REF_TYPE_COL = 3;
	private static final int DERIVED_COL = 4;
	private static final int MIN_COL = 5;
	private static final int MAX_COL = 6;
	private static final int DEFAULT_VALUE_COL = 7;
	private GWT_CiBean currentTemplate;
	private VerticalPanel vPanel = new VerticalPanel();
	private HashMap rowMap = new HashMap();
	
	public ListAttributeScreen() {
		super();
		dockPanel.add(vPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(vPanel, "100%");
		initWidget(dockPanel);
	}
	
	
	public void setTemplate(GWT_CiBean template) {
		this.currentTemplate = template;
	}

	
	
	public void load(String objectType, Long objectId) {
		TemplateCache.load(objectType, new AsyncCallback() {
	
			public void onFailure(Throwable caught) {
				setErrorText("ERROR:" + caught);
			}

			public void onSuccess(Object result) {
				if (result instanceof GWT_CiBean) {
					setTemplate((GWT_CiBean)result);
					load();
					return;
				}
			}
		});
	}


	public void load() {
		if (this.currentTemplate == null) {
			setErrorText("No Template to show!");
			return;
		}
		setErrorText("");
		setTitleText("Attributes for ");
		setTitleWidget(new CIIconDisplayNameWidget(this.currentTemplate));
		vPanel.clear();
		
		HTML add = new HTML("[<a href='javascript:;'>new</a>]");
		add.setTitle("Add a new attribute to this template");
		add.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				OneCMDBModelCreator.get().showScreen(OneCMDBModelCreator.ADD_ATTRIBUTE_SCREEN, currentTemplate.getAlias(), new Long(0));
			}
			
		});
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(add);
		hPanel.setCellHorizontalAlignment(add, HorizontalPanel.ALIGN_RIGHT);
		hPanel.setWidth("100%");
		vPanel.add(hPanel);
		vPanel.add(getAttributeTable());
		
	}
	
	private Widget getAttributeTable() {
		HoverGridWidget widget = new HoverGridWidget();
		widget.addTableListener(new TableListener() {

			public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
				Object o = rowMap.get(new Integer(row));
				if (o instanceof GWT_AttributeBean) {
					
					GWT_AttributeBean aBean = (GWT_AttributeBean)o;
					if (!aBean.isDerived()) {
						// Edit this.
						OneCMDBModelCreator.get().showScreen(
								OneCMDBModelCreator.EDIT_ATTRIBUTE_SCREEN, 
								currentTemplate.getAlias() + "." + aBean.getAlias(), new Long(0));
					} else {
						Window.alert("Only attributes defined on this CI may be editable!");
					}
				}
				
			}
			
		});
		widget.addHeader(NAME_COL, "Name");
		widget.addHeader(ALIAS_COL, "Alias");
		widget.addHeader(TYPE_COL, "Type");
		widget.addHeader(REF_TYPE_COL, "Reference Type");
		widget.addHeader(DERIVED_COL, "Derived");
		widget.addHeader(MIN_COL, "Min");
		widget.addHeader(MAX_COL, "Max");
		widget.addHeader(DEFAULT_VALUE_COL, "Default");
		
		ArrayList derivedSet = new ArrayList();
		ArrayList localSet = new ArrayList();
		for (Iterator iter = this.currentTemplate.getAttributes().iterator(); iter.hasNext();) {
			GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
			if (aBean.isDerived()) {
				derivedSet.add(aBean);
			} else {
				localSet.add(aBean);
			}
		}
		Collections.sort(derivedSet, getAttributeComparator());
		Collections.sort(localSet, getAttributeComparator());
		
		int row = 1;
		for (Iterator iter = derivedSet.iterator(); iter.hasNext();) {
			GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
			addAttributeRow(widget, row, aBean);
			row++;
		}
		
		for (Iterator iter = localSet.iterator(); iter.hasNext();) {
			GWT_AttributeBean aBean = (GWT_AttributeBean)iter.next();
			addAttributeRow(widget, row, aBean);
			row++;
		}
		return(widget);
	}

	private Comparator getAttributeComparator() {
		return(new AttributeComparator());
	}
	
	private void addAttributeRow(HoverGridWidget widget, int row, final GWT_AttributeBean aBean) {
		widget.setText(row, NAME_COL, aBean.getDisplayName());
		widget.setText(row, ALIAS_COL, aBean.getAlias());
		widget.setWidget(row, TYPE_COL, new Hyperlink(aBean.getType(), null));
		widget.setText(row, REF_TYPE_COL, aBean.getRefType());
		widget.setText(row, DERIVED_COL, aBean.isDerived() ? "*" : " ");
		widget.setText(row, MIN_COL, aBean.getMinOccurs());
		widget.setText(row, MAX_COL, aBean.getMaxOccurs());
		List values = currentTemplate.fetchAttributeValueBeans(aBean.getAlias());
		if (values != null && values.size() > 0) {
			GWT_ValueBean value = (GWT_ValueBean) values.get(0);
			widget.setText(row, DEFAULT_VALUE_COL, value.getValue());
		} else {
			widget.setText(row, DEFAULT_VALUE_COL, "");
		}
		if (!aBean.isDerived()) {
			Image delete = new Image("images/trashcan16.gif");
			delete.setTitle("Delete attribute " + aBean.getDisplayName());
			delete.addClickListener(new ClickListener() {

				public void onClick(Widget sender) {
					delete(aBean);
				}
				
			});
			widget.setWidget(row, DEFAULT_VALUE_COL+1, delete);
		}
		
		rowMap.put(new Integer(row), aBean);
	}


	protected void delete(GWT_AttributeBean bean) {
		if (Window.confirm("Delete attribute " + bean.getDisplayName() + "?")) {
			GWT_CiBean copy = this.currentTemplate.copy();
			copy.removeAttribute(bean.getAlias());
			
			// Invalidate cache...
			TemplateCache.add(this.currentTemplate.getAlias(), null);
			
			//	Call update of attribute.
			OneCMDBConnector.getInstance().update(OneCMDBSession.getAuthToken(),
					new GWT_CiBean[] {copy},
					new GWT_CiBean[] {this.currentTemplate},
					new AsyncCallback() {

						public void onFailure(Throwable caught) {
							setErrorText("ERROR:" +  caught);
							
						}

						public void onSuccess(Object result) {
							if (result instanceof GWT_RfcResult) {
								GWT_RfcResult rfcResult = (GWT_RfcResult)result;
								if (!rfcResult.isRejected()) {
									String alias = currentTemplate.getAlias();
									currentTemplate = null;
									TemplateCache.remove(alias);
									load(alias, new Long(0));
									return;
								}
								setErrorText(rfcResult.getRejectCause());
								return;
							}
							setErrorText("Unknown result object!");
						}
					}
			);
			
		}
	}
	
}
