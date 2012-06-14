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
package org.onecmdb.rest.graph.main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.rest.graph.events.CEvents;
import org.onecmdb.rest.graph.events.Event;
import org.onecmdb.rest.graph.events.EventDispatcher;
import org.onecmdb.rest.graph.events.IEventListener;
import org.onecmdb.rest.graph.model.CIModel;
import org.onecmdb.rest.graph.model.prefuse.TemplateModelControl;
import org.onecmdb.rest.graph.panels.TemplateReferencePanel;

import prefuse.Visualization;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;

public class MainTemplateReferenceBrowser extends JPanel implements IEventListener {

	
	private JTabbedPane graphTab;
	private MainInstanceBrowser instanceGraph;
	private JTabbedPane tp;
	private TemplateModelControl model;
	private String alias;
	private String root;

	public MainTemplateReferenceBrowser(String root, String alias) {
		model = TemplateModelControl.get(root);
		this.alias = alias;
		this.root = root;
		
		initUI();
		
		EventDispatcher.addEventListener(this);
	}
	
	public void initUI() {
		graphTab = new JTabbedPane();
		//graphTab.setTabPlacement(JTabbedPane.BOTTOM);
		TemplateReferencePanel refPanel = new TemplateReferencePanel(model);
		
		Dimension minimumSize = new Dimension(200, 200);
		refPanel.setMinimumSize(minimumSize);

		instanceGraph = new MainInstanceBrowser(this.root);
		instanceGraph.setInstanceTemplate(alias, null);
		graphTab.addTab("Template References", refPanel);
		graphTab.addTab("Instances of " + alias, instanceGraph);
		//graphTab.addTab("All References", allRefPanel);
	
		setLayout(new BorderLayout());
		add(graphTab, BorderLayout.CENTER);
		
		refPanel.setSelectedAlias(alias);
	}

	public void onEvent(Event e) {
		switch(e.getType()) {
		case(CEvents.RELATION_ITEM_SELECTED):
		{
			String alias = (String) e.getData();
			graphTab.setTitleAt(1, "Instances of " + alias);			
			instanceGraph.setInstanceTemplate(alias, null);
		}
		break;
		}
	}
}
