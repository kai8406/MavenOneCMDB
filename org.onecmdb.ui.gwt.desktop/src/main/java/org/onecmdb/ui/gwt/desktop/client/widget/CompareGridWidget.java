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
package org.onecmdb.ui.gwt.desktop.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.tagext.PageData;

import org.onecmdb.ui.gwt.desktop.client.fixes.MyMessageBox;
import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeItem;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeRecord;
import org.onecmdb.ui.gwt.desktop.client.service.change.ChangeServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.client.utils.HTMLGenerator;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class CompareGridWidget extends LayoutContainer {
	
	private CIModel mdrConfigHistory;
	private CIModel mdr;
	private CIModel mdrConfig;
	
	private BasePagingLoader loader;
	private List<? extends ChangeItem> resultSet;
	private ChangeRecord changeRecord;
	private boolean rejectEnabled = true;
	private boolean deleteEnabled = true;
	private HTML overviewHtml;
	private GroupingStore<ChangeItem> store;

	public CompareGridWidget() {
	}
	
	
	
	public boolean isDeleteEnabled() {
		return deleteEnabled;
	}



	public void setDeleteEnabled(boolean deleteEnabled) {
		this.deleteEnabled = deleteEnabled;
	}



	public boolean isRejectEnabled() {
		return rejectEnabled;
	}



	public void setRejectEnabled(boolean rejectEnabled) {
		this.rejectEnabled = rejectEnabled;
	}



	@Override
	protected void onRender(Element parent, int index) {
		// TODO Auto-generated method stub
		super.onRender(parent, index);
	
		init();
	}


	public void init() {
		
		RpcProxy proxy = new RpcProxy() {
			@Override
			public void load(Object loadConfig, final AsyncCallback callback) {
				final BasePagingLoadConfig config = (BasePagingLoadConfig)loadConfig;
			
				if (resultSet != null) {
					int offset = config.getOffset();
					int limit = config.getLimit();
					List<ChangeItem> items = new ArrayList<ChangeItem>();
					for (int i = offset; i < (limit+offset); i++) {
						if (i >= resultSet.size()) {
							break;
						}
						ChangeItem item = resultSet.get(i);
						items.add(item);
					}
					PagingLoadResult result = new BasePagingLoadResult(items, offset, resultSet.size());
					callback.onSuccess(result);
					return;
				}
				ChangeServiceFactory.get().loadChanges(CMDBSession.get().getToken(), mdr, mdrConfig, mdrConfigHistory, null, new CMDBAsyncCallback<ChangeRecord>() {

	
			
					@Override
					public void onSuccess(ChangeRecord arg0) {
						changeRecord = arg0;
						resultSet = arg0.getChangeItems();
						if (resultSet.size() == 0) {
							Info.display("MDR compare info", "No changes found"); 
							//("Changes", "No changes found!");
						}
						int offset = config.getOffset();
						int limit = config.getLimit();
						List<ChangeItem> items = new ArrayList<ChangeItem>();
						for (int i = offset; i < limit; i++) {
							if (i >= resultSet.size()) {
								break;
							}
							ChangeItem item = resultSet.get(i);
							items.add(item);
						}
						
						PagingLoadResult result = new BasePagingLoadResult(items, offset, resultSet.size());
						callback.onSuccess(result);
					}
				});
				
			}
		};

		    // loader
		loader = new BasePagingLoader(proxy);
		loader.setRemoteSort(false);
		loader.addLoadListener(new LoadListener() {

			@Override
			public void loaderLoad(LoadEvent le) {
				updateOverview(le);
			}

			@Override
			public void loaderLoadException(LoadEvent le) {
				updateOverview(le);
			}
			
		});
		
		final PagingToolBar toolBar = new PagingToolBar(1000);
		
	
		store = new GroupingStore<ChangeItem>(loader);
		store.groupBy("derivedFrom");
		//store.setRemoteGroup(true);
		TextToolItem reject = new TextToolItem("Reject", "reject-icon");
		reject.setToolTip("Mark this data as rejected");
		reject.setEnabled(this.rejectEnabled);
		reject.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				// TODO Auto-generated method stub
				final MessageBox commitInfo = MessageBox.wait("Progress",  
			             "Reject your data, please wait...", "Rejecting..."); 
				
				CIModel copy = mdrConfigHistory.copy();
				ValueModel mv = mdrConfigHistory.getValue("status");
				if (mv == null) {
					mv = new ValueModel();
					mv.setAlias("status");
					mv.setIsComplex(false);
					mdrConfigHistory.setValue(mv.getAlias(), mv);
					
				}
				mv.setValue(MDRHistoryState.REJECTED);
				ArrayList<CIModel> local = new ArrayList<CIModel>();
				ArrayList<CIModel> base = new ArrayList<CIModel>();
				local.add(mdrConfigHistory);
				base.add(copy);
				ModelServiceFactory.get().store(CMDBSession.get().getDefaultCMDB_MDR(), CMDBSession.get().getToken(), local, base, new CMDBAsyncCallback<StoreResult>() {

			
					public void onSuccess(StoreResult result) {
						commitInfo.close();
						if (result.isRejected()) {
							MessageBox.alert("Failed when rejecting", result.getRejectCause(), null);
							return;
						}
						
						MyMessageBox.info("Rejected", "The history entry has been marked as rejected", new Listener<WindowEvent>() {

							public void handleEvent(WindowEvent be) {
								// Will reload and verify.
								resultSet = null;
								loader.load();
							}
							
						});
					}
					
				});
			}
		});
		TextToolItem commit = new TextToolItem("Commit to CMDB", "commit-icon");
		commit.setToolTip("Commit the selected data to the cmdb.");
		commit.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				final MessageBox commitInfo = MessageBox.wait("Progress",  
			             "Commit your data, please wait...", "Committing..."); 
				store.commitChanges();
				
				ChangeServiceFactory.get().commit(CMDBSession.get().getToken(), mdr, mdrConfig, mdrConfigHistory, store.getModels(), new AsyncCallback<StoreResult>() {

					public void onFailure(Throwable arg0) {
						commitInfo.close();
						ExceptionErrorDialog.showError("Can't Save", arg0);			
					}

					public void onSuccess(StoreResult result) {
						commitInfo.close();
						if (result.isRejected()) {
							MessageBox.alert("Commit Failed", result.getRejectCause(), null);
							return;
						}
						
						MyMessageBox.info("Committed", HTMLGenerator.toHTML(result), new Listener<WindowEvent>() {

					
							public void handleEvent(WindowEvent be) {
								if (getParent() instanceof Window) {
									((Window)getParent()).close();
								}
								fireEvent(CMDBEvents.COMMIT_EVENT);
							}
							
						});
					}
				});
			}
		});
		
		TextToolItem delete = new TextToolItem("Remove", "delete-icon");
		delete.setToolTip("Remove all previously committed CIs.");
		delete.setEnabled(this.deleteEnabled);

		delete.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				MessageBox.confirm("Remove", "Remove previously committed CIs?", new Listener<WindowEvent>() {

					public void handleEvent(WindowEvent be) {
						Button button = be.buttonClicked;
						if (button.getItemId().equals(Dialog.YES)) {
							// Do remove...
							doRemove();
						}
					}

					
				});
			}
				
		});

		TextToolItem cancel = new TextToolItem("Cancel", "cancel-icon");
		cancel.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (getParent() instanceof Window) {
					((Window)getParent()).close();
				}
			}
			
		});
		ToolBar buttonBar = new ToolBar();
		buttonBar.add(new FillToolItem());
		buttonBar.add(commit);
		buttonBar.add(new SeparatorToolItem());
		buttonBar.add(cancel);
		
		buttonBar.add(delete);
		//buttonBar.add(reject);
		//toolBar.add(commit);
	    toolBar.bind(loader);

		ColumnConfig derivedFrom = new ColumnConfig("derivedFrom", "Derived From", 100);
		ColumnConfig ci = new ColumnConfig("alias", "CI", 100);
		ColumnConfig status = new ColumnConfig("status", "Status", 50);
		ColumnConfig type = new ColumnConfig("type", "Type", 50);
		ColumnConfig info = new ColumnConfig("info", "Info", 50);
	
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		XTemplate tpl = XTemplate.create("<b>Summary:</b> {summary}");

		RowExpander expander = new RowExpander();
		expander.setTemplate(tpl);

		config.add(expander);
		
		CheckColumnConfig exclude = new CheckColumnConfig("include", "Include", 30);
		config.add(exclude);
		
		config.add(type);
		config.add(ci);
		config.add(derivedFrom);
		config.add(status);
		config.add(info);
		final ColumnModel cm = new ColumnModel(config);

		GroupingView view = new GroupingView();
		view.setForceFit(true);
		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(GroupColumnData data) {
				String style = "#00cd00";
				if (data.field.equals("status") && data.group.startsWith("ERROR:")) {
					style = "#ff1010";
				}
				// check how many...
				int totalCount = 0; 
				if (resultSet != null) {
					
					for (ChangeItem c : resultSet) {
						String id = data.field;
						Object o = c.get(id);
						if (o != null) {
							if (o.equals(data.group)) {
								totalCount++;
							}
						}
					}
				}
				String f = cm.getColumnById(data.field).getHeader();
				String l = data.models.size() == 1 ? "Item" : "Items";
				return f + ": " + "<span style='color:" + style + "'>" + data.group + "</span> " + data.models.size() + "(" + totalCount +") "  + l;
			}
		});

		
		
		Grid grid = new Grid(store, cm);
		grid.setView(view);
		grid.setBorders(true);
		grid.addPlugin(exclude);
		grid.addPlugin(expander);
		grid.getView().setForceFit(true);
		setLayout(new RowLayout());
		
		
		ContentPanel gridPanel = new ContentPanel();
		gridPanel.setHeaderVisible(false);
		gridPanel.setLayout(new FitLayout());
		gridPanel.setTopComponent(toolBar);
		gridPanel.add(grid);
		
		TabPanel tab = new TabPanel();
		TabItem overview = new TabItem("Overview");
		overview.setLayout(new FitLayout());
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setScrollMode(Scroll.AUTO);
		cp.setLayoutOnChange(true);
		cp.setLayout(new FitLayout());
		
		overviewHtml = new HTML("<i>Loading...</i>");
		overviewHtml.setStyleName("property-panel-background");
		cp.setStyleName("property-panel-background");
		cp.add(overviewHtml);
		overview.add(cp);
		TabItem details = new TabItem("Details");
		
		details.setLayout(new FitLayout());
		details.add(gridPanel);
		
		tab.add(overview);
		tab.add(details);
		
		add(tab, new RowData(1, 1));
		add(buttonBar, new RowData(1,-1));
		
		layout();
		    
	}
	private void doRemove() {
		final MessageBox commitInfo = MessageBox.wait("Progress",  
	             "Delete your data, please wait...", "Deleting..."); 
		store.commitChanges();
		
		ChangeServiceFactory.get().delete(CMDBSession.get().getToken(), mdr, mdrConfig, mdrConfigHistory, store.getModels(), new AsyncCallback<StoreResult>() {

			public void onFailure(Throwable arg0) {
				commitInfo.close();
				ExceptionErrorDialog.showError("Can't Delete", arg0);			
			}

			public void onSuccess(StoreResult result) {
				commitInfo.close();
				if (result.isRejected()) {
					MessageBox.alert("Delete Failed", result.getRejectCause(), null);
					return;
				}
				
				MyMessageBox.info("Deleted", HTMLGenerator.toHTML(result), new Listener<WindowEvent>() {

					public void handleEvent(WindowEvent be) {
						// Will reload and verify.
						resultSet = null;
						loader.load();
					}
					
				});
			}
		});
	}

	public void setModels(CIModel mdr, CIModel config, CIModel history) {
		this.mdrConfigHistory = history;
		this.mdr = mdr;
		this.mdrConfig = config;
		resultSet = null;
		if (overviewHtml != null) {
			overviewHtml.setHTML("<i>Loading...</i>");
		}
		if (loader != null) {
			loader.load();
		}
	}



	protected void updateOverview(LoadEvent le) {
		StringBuffer b = new StringBuffer();
		if (le.exception != null) {
			b.append("<p><b>Error</b><p/>");
			b.append("<p>" + le.exception.toString() + "</p>");
		}
		if (le.data instanceof PagingLoadResult) {
			List<? extends ChangeItem> result = resultSet;
			if (result == null) {
				result = new ArrayList<ChangeItem>();
			}
			if (changeRecord != null) {
				b.append(changeRecord.getSummary());
			}
			HashMap<String, ResultStatusEntry> resultMap = new HashMap<String, ResultStatusEntry>();
			
			int totalChanges = result.size();
			for (ChangeItem item : result) {
				String status = item.get("status");
				String type = item.get("type");
				updateResultStatus(resultMap, status, type);
			}
			b.append("<p><b>Compare result</b><br><br>");
			for (String status : resultMap.keySet()) {
				ResultStatusEntry resultType = resultMap.get(status);
				if (status.toLowerCase().contains("error")) {
					b.append("<span style='color:#ff1010'>");
				}
				b.append(resultType.occurenc + " " + status + " CIs (" + resultType.templates + " templates,  " + resultType.instances + " instances)<br/>");
				if (status.toLowerCase().contains("error")) {
					b.append("</span>");
				}
		
			}
			b.append("----------------------<br>");
			b.append("<b>" + totalChanges + "</b> Total changes<br>");
			b.append("</p>");
		}
		overviewHtml.setHTML(b.toString());
		layout();
	}



	private void updateResultStatus(Map<String, ResultStatusEntry> map, String status, String type) {
		ResultStatusEntry result = map.get(status);
		if (result == null) {
			result = new ResultStatusEntry();
			result.status = status;
			map.put(status, result);
		}
		if (type.equalsIgnoreCase("template")) {
			result.templates++;
		} else {
			result.instances++;
		}
		result.occurenc++;
	}
	
	class ResultStatusEntry {
		String status;
		int templates;
		int instances;
		int occurenc;
	}
}
