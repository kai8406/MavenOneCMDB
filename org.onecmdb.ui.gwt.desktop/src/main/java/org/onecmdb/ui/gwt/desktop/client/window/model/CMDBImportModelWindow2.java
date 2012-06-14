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
package org.onecmdb.ui.gwt.desktop.client.window.model;

import java.util.ArrayList;
import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.CMDBAsyncCallback;
import org.onecmdb.ui.gwt.desktop.client.service.content.Config;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFile;
import org.onecmdb.ui.gwt.desktop.client.service.content.ContentFolder;
import org.onecmdb.ui.gwt.desktop.client.service.model.AliasFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBDesktopWindowItem;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBSession;
import org.onecmdb.ui.gwt.desktop.client.service.model.ModelServiceFactory;
import org.onecmdb.ui.gwt.desktop.client.service.model.StoreResult;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueListModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.ValueModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.mdr.MDRHistoryState;
import org.onecmdb.ui.gwt.desktop.client.utils.CIModelUtils;
import org.onecmdb.ui.gwt.desktop.client.widget.CompareGridWidget;
import org.onecmdb.ui.gwt.desktop.client.widget.ContentSelectorWidget;
import org.onecmdb.ui.gwt.desktop.client.window.CMDBAbstractWidget;
import org.onecmdb.ui.gwt.desktop.client.window.WidgetDescription;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolItem;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class CMDBImportModelWindow2 extends CMDBAbstractWidget {

	public static final String ID = "cmdb-model-import";

	private CIModel mdrRepository;
	private CIModel mdrConfig;
	private CIModel mdrConfigBase;
	
	public CMDBImportModelWindow2(CMDBDesktopWindowItem item) {
		super(item);
	}
	
	public ContentFile getMDR() {
		ContentFile mdr = new ContentFile();
		String mdrConf = item.getParams().get("mdrConfig");
		
		if (mdrConf == null) {
			mdrConf = CMDBSession.get().getConfig().get(Config.OneCMDBWebService);
		}
		mdr.setPath(mdrConf);
		
		return(mdr);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		// Load modelMDR and modelConfig...
		ModelServiceFactory.get().loadModelMDRInfo(CMDBSession.get().getToken(), item.getParams(), new CMDBAsyncCallback<BaseModel>() {
			public void onSuccess(BaseModel result) {
				mdrRepository = result.get("mdr");
				mdrConfig = result.get("mdrConfig");
				mdrConfigBase = mdrConfig.copy();
				init();
			}
		});
	}
	
	protected void init() {
		setLayout(new BorderLayout());
		ContentFolder root = new ContentFolder();
		root.setPath((String)item.getParams().get("modelRoot"));
	
		final ContentSelectorWidget browser = new ContentSelectorWidget(root);
		ValueListModel contentFiles = (ValueListModel) mdrConfig.getValue("modelFiles");
		if (contentFiles != null) {
			List<ContentData> selected = new ArrayList<ContentData>();
			for (ValueModel vModel : contentFiles.getValues()) {
				if (vModel.getValue() == null) {
					continue;
				}
				ContentFile data = new ContentFile();
				data.setPath(vModel.getValue());
				selected.add(data);
			}
			
			browser.setSelected(selected);
		}
		final CompareGridWidget compare = new CompareGridWidget();
		compare.setRejectEnabled(false);
		compare.setDeleteEnabled(false);
		ContentPanel browserCp = new ContentPanel();
		browserCp.setHeading("Select Models");
		browserCp.setLayoutOnChange(true);
		browserCp.setLayout(new RowLayout());
		browserCp.setScrollMode(Scroll.AUTO);
		
		HTML html = new HTML("<p><b>Info</b></br>Select Model Files that shall be in the CMDB<br/>" +
				"De-select Model Files that shall not be in the CMDB</p>");
		html.setStyleName("property-panel-background");
		browserCp.add(html, new RowData(1,-1));
		
		browserCp.add(browser, new RowData(1,1));
		
		/*
		browserCp.getHeader().addTool(new ToolButton("x-tool-plus", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				browser.expandAll();
			}
		}));
		browserCp.getHeader().addTool(new ToolButton("x-tool-minus", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				browser.collapseAll();
			}
		}));
		*/
			
		ContentPanel compareCp = new ContentPanel();
		compareCp.setHeading("Status of selected models");
		compareCp.setScrollMode(Scroll.AUTO);
		compareCp.setLayout(new FitLayout());
		
		compareCp.add(compare);
		
		BorderLayoutData west = new BorderLayoutData(LayoutRegion.WEST, 300);
		west.setSplit(true);
		west.setCollapsible(true);
		west.setMargins(new Margins(5));
		
			
		TextToolItem compareTool = new TextToolItem("Open", "compare-icon");
		compareTool.setToolTip("Open and compare the selected files to<br/> the previous committed files");
		compareTool.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				List<ContentData> items = browser.getSelected();
				List<String> result = new ArrayList<String>();
				for (ContentData item : items) {
					if (item instanceof ContentFile) {
						result.add(item.getPath());
					}
				}
				CIModelUtils.updateModel(mdrConfig, "modelFiles", result);
				// store mdrConfig...
				List<CIModel> local = new ArrayList<CIModel>();
				List<CIModel> base = new ArrayList<CIModel>();
				local.add(mdrConfig);
				base.add(mdrConfigBase);
				
				final CIModel history = new CIModel();
				history.setDerivedFrom(MDRHistoryState.getHistoryTemplate());
				CIModelUtils.updateModel(history, "files", result);
				CIModelUtils.updateModel(history, "mdrConfigEntry", mdrConfig.getAlias(), true);
				history.setAlias(AliasFactory.generateAlias(MDRHistoryState.getHistoryTemplate()));
				
				ModelServiceFactory.get().store(getMDR(), CMDBSession.get().getToken(), local, base, new CMDBAsyncCallback<StoreResult>() {
					public void onSuccess(StoreResult result) {
						// Call upload script...
						compare.setModels(mdrRepository, mdrConfig, history);
					}
				});
			}
		 });

		browserCp.getHeader().addTool(compareTool);		
		
		add(browserCp, west);
		add(compareCp, new BorderLayoutData(LayoutRegion.CENTER));
		layout();
	}



	@Override
	public WidgetDescription getDescription() {
		WidgetDescription desc = new WidgetDescription();
		desc.setId(ID);
		desc.setName("Open OneCMDB Models");
		desc.setDescription("Open OneCMDB model data. The model can contain template/instances/references");
		desc.addParameter("modelRoot - Path to where models are keept");
		return(desc);
	}
	

}
