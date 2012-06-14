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

import java.util.List;

import org.onecmdb.ui.gwt.desktop.client.service.content.ContentData;
import org.onecmdb.ui.gwt.desktop.client.service.model.CIModel;
import org.onecmdb.ui.gwt.desktop.client.service.model.CMDBPermissions;
import org.onecmdb.ui.gwt.desktop.client.widget.grid.EditableCIInstanceGrid;
import org.onecmdb.ui.gwt.desktop.client.widget.tree.CITemplateBrowser;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;

public class CIModelBrowser extends LayoutContainer {
	
	private ContentData mdr;
	private List<String> roots;
	//private boolean readonly;
	protected CMDBPermissions permissions;

	public CIModelBrowser(ContentData mdr, List<String> roots) {
		this.mdr = mdr;
		this.roots = roots;
	}
	
	
	public CMDBPermissions getPermissions() {
		return permissions;
	}


	public void setPermissions(CMDBPermissions permissions) {
		this.permissions = permissions;
	}


	@Override
	protected void onRender(Element parent, int index) {		
		super.onRender(parent, index);
		init();
	}


	public void init() {
		
		setLayout(new BorderLayout());
		CITemplateBrowser browser = new CITemplateBrowser(mdr, roots);
		//browser.setPermissions(permissions);
		//browser.setReadonly(readonly);
		
		//final CIInstanceBrowser center = new CIInstanceBrowser(mdr);
		final LayoutContainer center = new LayoutContainer();
		center.setLayout(new FitLayout());
		center.setLayoutOnChange(true);
		
		browser.setSelectionListsner(new Listener<TreeEvent>() {

			public void handleEvent(TreeEvent te) {  
				TreeItem item = te.tree.getSelectedItem();  
				if (item != null) {  
					if (item.getModel() instanceof CIModel) {
						final CIModel model = (CIModel)item.getModel();
						/*	
						center.setRoot(model);
						center.start();
						*/
						
						center.removeAll();
						ContentData cd = new ContentData();
						cd.set("template", model.getAlias());
						EditableCIInstanceGrid grid = new EditableCIInstanceGrid(mdr, cd, model.getNameAndIcon());
						if (roots != null && roots.contains("Root")) {
							grid.setRootCI("Root");
						}
						grid.setPermissions(permissions);
						//grid.setReadonly(readonly);
						center.add(grid);
					}					
					//Info.display("Selection Changed", "The '{0}' item was selected", item.getText());  
				}
			}  
		});  

		
		ContentPanel left = new ContentPanel();
		left.setScrollMode(Scroll.AUTO);
		left.setHeading("Template(s)");
		left.setLayout(new FitLayout());
		left.setLayoutOnChange(true);
		left.add(browser);
		
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(5, 0, 5, 0));  
	       
	       
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(5));  
	 
		
		add(left, westData);
		add(center, centerData);
		
		layout();
		
	}

	/*
	public void setReadonly(boolean value) {
		this.readonly = value;
	}
	*/

}
