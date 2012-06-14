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
package org.onecmdb.ui.gwt.desktop.client.window.mdr;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.mvc.CMDBEvents;
import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.AttributeColumnConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.grid.GridModelConfig;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.TransformConfig;
import org.onecmdb.ui.gwt.desktop.client.widget.CompareGridWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.form.InputFormWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.help.HelpInfo;
import org.onecmdb.ui.gwt.desktop.client.widget.help.TabWizardBar;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.MDRDataSourceConfig;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.MDRStartWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.MDRTransformFinished;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.MDRTransformTableConfigurator;
import org.onecmdb.ui.gwt.desktop.client.widget.mdr.PreviewTableWidget;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;
import org.onecmdb.ui.gwt.desktop.client.window.model.CMDBModelDesignerView;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class MDRConfigureWindow extends CMDBAbstractWidget {
	public static final String ID = "cmdb-mdr-config";
	protected CIModel mdr;
	protected CIModel mdrConfig;
	protected TransformConfig transformConfig = new TransformConfig();
	private String mdrName;
	private String mdrConfigName;
	private TextArea dataSourceArea;
	//private MDRTransformConfigurator transformPanel;
	private MDRTransformTableConfigurator transformPanel;
	private boolean doClose = false;
	private PreviewTableWidget preview;
	private boolean modelChanged;

	
	public MDRConfigureWindow(CMDBDesktopWindowItem item) {
		super(item);
	}

	
	
	public MDRConfigureWindow(CMDBPermissions permissions, CIModel mdr,
			CIModel mdrConfig) {
		super(null);
		
		this.permissions = permissions;
		setMdr(mdr);
		setMdrConfig(mdrConfig);
	}



	public CIModel getMdr() {
		return mdr;
	}



	public void setMdr(CIModel mdr) {
		this.mdr = mdr;
		this.mdrName = mdr.getValue("name").getValue();
	}



	public CIModel getMdrConfig() {
		return mdrConfig;
	}



	public void setMdrConfig(CIModel mdrConfig) {
		this.mdrConfig = mdrConfig;
		this.mdrConfigName = mdr.getValue("name").getValue();
	}



	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		ModelServiceFactory.get().loadTransformConfig(CMDBSession.get().getToken(), CMDBSession.get().getDefaultCMDB_MDR(), mdr, mdrConfig, new CMDBAsyncCallback<TransformConfig>() {

			@Override
			public void onSuccess(TransformConfig arg0) {
				transformConfig = arg0;
				initUI();
			}
		});
	}



	protected void initUI() {
		if (transformConfig.getDataSourceType().equals("xml") || !transformConfig.isConfigurabe()) {
			MessageBox.alert("Alert", "This MDR is not currently configurable!", new Listener<WindowEvent>() {

				public void handleEvent(WindowEvent be) {
					// Close window...
					if (getParent() instanceof Window) {
						((Window)getParent()).close();
					}
				}
			
			});
			return;
		}
		TabPanel tab = new TabPanel();
		// Listen on changes.
		this.transformConfig.addChangeListener(new ChangeListener() {

			public void modelChanged(ChangeEvent event) {
				modelChanged = true;				
			}
		});
		
		TabItem tabItem1 = new TabItem("1. Data Source");
		tabItem1.setLayout(new FitLayout());
		
		TabItem tabItem2 = new TabItem("2. Data Source Preview");
		tabItem2.setLayout(new FitLayout());
	
		TabItem tabItem3 = new TabItem("3. Transform Setup");
		tabItem3.setLayout(new FitLayout());
	
		TabItem tabItem4 = new TabItem("4. Transform Execute");
		tabItem4.setLayout(new FitLayout());
		
		TabItem tabItem5 = new TabItem("5. Execution Result");
		tabItem5.setLayout(new FitLayout());
		
		
		tabItem1.add(new MDRDataSourceConfig(this.mdrName, transformConfig));
		tab.add(tabItem1);
		
		tabItem1.addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				HelpInfo.show("help/mdr/help-mdr-datasource.html");
			}
		});
		
		
		final ContentPanel previewPanel = getDataSourcePreviewTab();
		tabItem2.add(previewPanel);
		tabItem2.addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				HelpInfo.show("help/mdr/help-mdr-datasource-preview.html");
				reloadPreview(previewPanel);
			}
		});
		tab.add(tabItem2);
		
		/*
		tabItem = new TabItem("3. OneCMDB Model");
		tabItem.setLayout(new FitLayout());
		final CMDBModelDesignerView modelWidget = getCMDBModelPanel();
		tabItem.add(modelWidget);
		tabItem.setLayoutOnChange(true);
		tab.add(tabItem);
		*/
		
		final MDRTransformTableConfigurator transform = getTransformPanel();
		tabItem3.add(transform);
		tabItem3.addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				transform.update();
			}
		});
		tab.add(tabItem3);
		
		
		
		
		/*
		tabItem4.add(getTransformExecutePanel());
		tabItem4.addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				HelpInfo.show("help/mdr/help-mdr-execute.html");
				if (modelChanged()) {
					MessageBox.confirm("Save!","Save Configuration before execution?", new Listener<WindowEvent>() {


						public void handleEvent(WindowEvent be) {
							Button b = be.buttonClicked;
							if (b.getItemId().equals(Dialog.YES)) {
								storeConfig(false);
							}
						}

					});
				}
				
				
			}
			
		});
		tab.add(tabItem4);
		*/
		/*
		final CompareGridWidget cmp = getTransformPreviewPanel();
		tabItem5.add(cmp);
		tabItem5.addListener(Events.Select, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				
				HelpInfo.show("help/mdr/help-mdr-execute-result.html");
				// Find lateset history....
				ModelServiceFactory.get().getLatsetMDRConfigHistory(CMDBSession.get().getToken(),
						CMDBSession.get().getDefaultCMDB_MDR(),
						mdrConfig,
						new CMDBAsyncCallback<CIModel>() {
							@Override
							public void onSuccess(CIModel arg0) {
								if (arg0 == null) {
									MessageBox.alert("Problem", "Can't find any history", null);
									return;
								}
								String status = arg0.getValueAsString("status");
								if (!status.equals("READY")) {
									MessageBox.alert("Problem", "Not ready to preview, run execute first", null);
									return;
								}
								cmp.setModels(mdr, mdrConfig, arg0);
							}
					
				});
			}
		});
		tab.add(tabItem5);
		*/
		

		
		ToolBar bar = new ToolBar();
		//bar.add(new FillToolItem());
		TextToolItem save = new TextToolItem("Save", "save-icon");
		save.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				storeConfig(false);
				
					
			}
			
		});
		//bar.add(save);
		//bar.add(new FillToolItem());
		TextToolItem close = new TextToolItem("Finish", "close-icon");
		close.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				if (getParent() instanceof Window) {
					((Window)getParent()).close();
				}
			}
			
		});
		//bar.add(close);
		
		TabItem finishTab = new TabItem("4. Finish");
		finishTab.setLayout(new FitLayout());
		finishTab.add(new MDRTransformFinished(save, close));
		tab.add(finishTab);
		
		setLayout(new FitLayout());
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		//panel.setTopComponent(bar);
		panel.add(tab);
		add(panel);
		layout();
		
		// Add dialog close warning about saving....
		if (getParent() instanceof Window) {
			final Window parent = (Window)getParent();
			parent.addListener(Events.BeforeClose, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					if (doClose) {
						return;
					}
					if (!modelChanged()) {
						return;
					}
					be.doit = false;
					MessageBox.confirm("Save!","Save Transform Configuration?", new Listener<WindowEvent>() {

			
						public void handleEvent(WindowEvent be) {
							Button b = be.buttonClicked;
							if (b.getItemId().equals(Dialog.YES)) {
								storeConfig(true);
								return;
							}
							doClose = true;
							parent.close();
						}
						
					});
				}
				
			});
		}
		
	}

	
	


	/**
	 * Need to know if something changed.
	 * 
	 * @return
	 */
	protected boolean modelChanged() {
		return(this.modelChanged);
	}



	protected void storeConfig(final boolean close) {
		ModelServiceFactory.get().storeTransformConfig(CMDBSession.get().getToken(), 
				CMDBSession.get().getDefaultCMDB_MDR(), 
				mdr, 
				mdrConfig, 
				transformConfig, 
				new CMDBAsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean arg0) {
						modelChanged = false;
						MessageBox.info("Saved", "Transform Configuration Saved", new Listener<WindowEvent>() {

							public void handleEvent(WindowEvent be) {
								if (close) {
									if (getParent() instanceof Window) {
										doClose = true;
										((Window)getParent()).close();
									}
								}
							}
							
						});	
						
					}
			
		});
	}



	


	private CompareGridWidget getTransformPreviewPanel() {
		CompareGridWidget widget = new CompareGridWidget();
		return(widget);
	}

	private Widget getTransformExecutePanel() {
		List<CIModel> configs = new ArrayList<CIModel>();
		configs.add(mdrConfig);
		return(new MDRStartWidget(mdr, configs));
	}

	private CMDBModelDesignerView getCMDBModelPanel() {
		CMDBDesktopWindowItem item = new CMDBDesktopWindowItem();
		BaseModel params = new BaseModel();
		params.set("rootCI", "Ci");
		params.set("rootReferenceType", "Reference");
		item.setParams(params);
		CMDBPermissions perm = new CMDBPermissions();
		perm.setCurrentState(CMDBPermissions.PermissionState.READONLY);
		BaseModel allowed = new BaseModel();
		allowed.set("readony", true);
		allowed.set("editable", true);
		allowed.set("deletable", false);
		allowed.set("clasify", false);
			
		perm.setLocalPermission(allowed);
		CMDBModelDesignerView view = new CMDBModelDesignerView(item);
		return(view);
	}



	private MDRTransformTableConfigurator getTransformPanel() {
		//transformPandel = new MDRTransformConfigurator(transformConfig); 
		transformPanel = new MDRTransformTableConfigurator(transformConfig);
		return(transformPanel);
	}
	/*
	protected LayoutContainer getDataSourcePanel() {
		
		TabItem confTab = new TabItem("DataSource");
		confTab.setLayout(new FitLayout());
		confTab.add(getDataSourceConfigTab());
		
		TabItem previewTab = new TabItem("Preview");
		previewTab.setLayout(new FitLayout());
		previewTab.add(getDataSourcePreviewTab());
		
		
		TabPanel tab = new TabPanel();
		tab.add(confTab);
		tab.add(previewTab);
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new FitLayout());
		container.add(tab);
		return(container);
	}
	*/
	private ContentPanel getDataSourcePreviewTab() {
		final ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				reloadPreview(panel);
			}
			
		}));
	    preview = new PreviewTableWidget(transformConfig);
		/*
	    preview.addListener(CMDBEvents.MDR_GRID_AVAILIABLE, new Listener<BaseEvent>()  {

			
			public void handleEvent(BaseEvent be) {
				if (be.source instanceof GridModelConfig) {
					setSourceColumns((GridModelConfig)be.source);
				}
			}

			
		});
		*/
		panel.add(preview);
		return(panel);
		/*
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new FitLayout());
		container.add(new WidgetComponent(new TextArea()));
		return(container);
		*/
	}

	protected void reloadPreview(final ContentPanel panel) {
		panel.removeAll();
		PreviewTableWidget preview = new PreviewTableWidget(transformConfig);
		/*
		preview.addListener(CMDBEvents.MDR_GRID_AVAILIABLE, new Listener<BaseEvent>()  {

			public void handleEvent(BaseEvent be) {
				if (be.source instanceof GridModelConfig) {
					setSourceColumns((GridModelConfig)be.source);
				}
			}

			
		});
		*/
		panel.add(preview);
		panel.layout();
	}



	private void setSourceColumns(GridModelConfig source) {
		/*
		if (transformPanel != null) {
			transformPanel.setSourceGridConfig(source);
		}
		*/
	}





	/*
	private List<AttributeColumnConfig> getDataSourceConfig() {
		AttributeColumnConfig type = new AttributeColumnConfig();
		type.setType("xs:radiogroup");
		type.setId("sourceType");
		type.setName("Type");
		type.addRadio("csv");
		type.addRadio("excel");
		type.addRadio("jdbc");
		type.addRadio("xml");
		type.set("csv", getDataSourceCSVConfig());
		type.set("excel", getDataSourceExcelConfig());
		type.set("jdbc", getDataSourceJDBCConfig());
		//type.set("xml", getDataSourceXMLConfig());
		
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		list.add(type);
		return(list);
	}
	
	private List<AttributeColumnConfig> getDataSourceExcelConfig() {
		AttributeColumnConfig url = new AttributeColumnConfig();
		url.setType("xs:content");
		url.setId("excel.url");
		url.setContentRoot("/MDR/" + mdrName);
		url.setName("Excel File");
		url.setTooltip("The excel file, must be located in the MDR repository directory on the server");
		
		AttributeColumnConfig sheet = new AttributeColumnConfig();
		sheet.setType("xs:string");
		sheet.setId("excel.sheet");
		sheet.setName("Sheet");
		sheet.setTooltip("The name of the sheet or the index of the sheet starting from 0");
			
		AttributeColumnConfig nHeaders = new AttributeColumnConfig();
		nHeaders.setType("xs:integer");
		nHeaders.setId("excel.headerLines");
		nHeaders.setName("Data Start Row");
		nHeaders.setTooltip("The first row in the excel sheet that should be regarded as data");
		
		AttributeColumnConfig nHeaderRow = new AttributeColumnConfig();
		nHeaderRow.setType("xs:integer");
		nHeaderRow.setId("excel.headerRow");
		nHeaderRow.setName("Header Row");
		nHeaderRow.setTooltip("Which row should be used for column headers");
		
	
		
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		list.add(url);
		list.add(sheet);
		list.add(nHeaderRow);
		list.add(nHeaders);
		return(list);
	}
	
	private List<AttributeColumnConfig> getDataSourceJDBCConfig() {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();
		
		AttributeColumnConfig col = new AttributeColumnConfig();
		col.setType("xs:content");
		col.setId("jdbc.lib");
		col.setContentRoot("MDR/" + mdrName);
		col.setName("JDBC Lib");
		col.setTooltip("The jar-file containing the hdbc driver for the database");
		list.add(col);
		
		col = new AttributeColumnConfig();
		col.setType("xs:string");
		col.setId("jdbc.url");
		col.setName("DB URL");
		col.setTooltip("The url to connect to the database");
		list.add(col);
		
		col = new AttributeColumnConfig();
		col.setType("xs:string");
		col.setId("jdbc.driverClass");
		col.setName("DB Driver Class");
		col.setTooltip("The driver in the driver jar to use");
		list.add(col);
		
		col = new AttributeColumnConfig();
		col.setType("xs:string");
		col.setId("jdbc.user");
		col.setName("User");
		col.setTooltip("The username to use when connecting to the database");
		list.add(col);
		
		col = new AttributeColumnConfig();
		col.setType("xs:password");
		col.setId("jdbc.password");
		col.setName("Password");
		col.setTooltip("The password to use when connecting to the database");
		list.add(col);
	
		col = new AttributeColumnConfig();
		col.setType("xs:textarea");
		col.setId("jdbc.query");
		col.setName("SQL Query");
		col.setTooltip("Specifies the SQL query to retrive data from the database");
		list.add(col);
			
		return(list);
	}

	
	private List<AttributeColumnConfig> getDataSourceCSVConfig() {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();

		AttributeColumnConfig url = new AttributeColumnConfig();
		url.setType("xs:content");
		url.setId("csv.url");
		url.setName("CSV File");
		url.setTooltip("The csv file, must be located in the MDR repository directory on the server");
		
		list.add(url);
		
		AttributeColumnConfig nHeaderRow = new AttributeColumnConfig();
		nHeaderRow.setType("xs:integer");
		nHeaderRow.setId("csv.headerRow");
		nHeaderRow.setName("Header Row");
		nHeaderRow.setTooltip("Which row should be used for column headers");
		list.add(nHeaderRow);
			
		AttributeColumnConfig nHeaders = new AttributeColumnConfig();
		nHeaders.setType("xs:integer");
		nHeaders.setId("csv.headerLines");
		nHeaders.setName("Data Start Row");
		nHeaders.setTooltip("The first row in the csv file that should be regarded as data");
		list.add(nHeaders);
				
		AttributeColumnConfig col = new AttributeColumnConfig();
		col.setType("xs:string");
		col.setId("csv.colDel");
		col.setName("Column Delimiter");
		list.add(col);
	
		
		return(list);
	}

	private List<AttributeColumnConfig> getDataSourceXMLConfig() {
		List<AttributeColumnConfig> list = new ArrayList<AttributeColumnConfig>();

		AttributeColumnConfig url = new AttributeColumnConfig();
		url.setType("xs:content");
		url.setId("xml.url");
		url.setName("URL");
		list.add(url);
		
		return(list);
	}


	private Widget getDataSourceConfigTab(TabItem next) {
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());
			
		TabWizardBar bar = new TabWizardBar(next);
		cp.setTopComponent(bar);
		InputFormWidget input = new InputFormWidget(transformConfig, getDataSourceConfig());
		
		input.setFieldWidth(300);
		input.setLabelWidth(100);
		cp.add(input);
		return(cp);
	}
	*/
	
	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("OneCMDB MDR Configuratior");
		desc.setDescription("A Widget that views all defined MDR(s). It's also possiable to start importing from MDR.");
		desc.addParameter("<li>MDR - MDR</li>");
		desc.addParameter("<li>MDR config</li>");
		return(desc);	

	}

}
