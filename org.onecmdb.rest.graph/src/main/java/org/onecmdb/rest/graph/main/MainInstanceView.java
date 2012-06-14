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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.dom4j.DocumentException;
import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.query.GraphQuery;
import org.onecmdb.rest.graph.events.CEvents;
import org.onecmdb.rest.graph.events.Event;
import org.onecmdb.rest.graph.events.EventDispatcher;
import org.onecmdb.rest.graph.events.IEventListener;
import org.onecmdb.rest.graph.io.OneCMDBConnection;
import org.onecmdb.rest.graph.model.CIAttributeModel;
import org.onecmdb.rest.graph.model.CIModel;
import org.onecmdb.rest.graph.model.prefuse.InstanceGraphControl;
import org.onecmdb.rest.graph.model.prefuse.TemplateModelControl;
import org.onecmdb.rest.graph.prefuse.view.GraphView;
import org.onecmdb.rest.graph.prefuse.view.RadialGraphView;
import org.onecmdb.swing.treetable.JTreeTable;
import org.onecmdb.utils.xml.XML2GraphQuery;

import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;

public class MainInstanceView extends JPanel implements IEventListener {

	public static final int LAYOUT_FORCE_DIRECTED = 1;
	public static final int LAYOUT_RADIAL = 2;
	
	private CIAttributeModel instancePropertyControl;
	private JTreeTable pTree;
	
	private GraphQuery query;
	
	private GraphView gView;
	private RadialGraphView rView;
	
	private InstanceGraphControl gControl = new InstanceGraphControl();
	private int graphType;

	public MainInstanceView(int graphType) {
		EventDispatcher.addEventListener(this);
		this.graphType = graphType;
	}
	
	
	public void initUI() {
		instancePropertyControl = new CIAttributeModel();
		instancePropertyControl.setAdvanced(false);
		
		pTree = new JTreeTable(instancePropertyControl);
	
		final JSplitPane centerSplit = new JSplitPane();
		centerSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		JTabbedPane rightTab = new JTabbedPane();
		rightTab.setTabPlacement(JTabbedPane.TOP);
		rightTab.add(new JScrollPane(pTree), "Attributes");
		
		centerSplit.setTopComponent(getGraphView(graphType));
		centerSplit.setBottomComponent(rightTab);
		centerSplit.setOneTouchExpandable(true);
		centerSplit.setDividerLocation(0.7f);
		setLayout(new BorderLayout());
		add(centerSplit, BorderLayout.CENTER);
	}
	
	public void update(GraphQuery query) {
		gControl.queryCMDB(query);
		switch(graphType) {
			case LAYOUT_FORCE_DIRECTED:
				gView.getVisualization().run("updateGraph");
				gView.redrawAndZoomToFit();
				break;
			case LAYOUT_RADIAL:
				rView.getVisualization().run("updateGraph");
				rView.getVisualization().run("filter");
				break;
		}	
	}
	
	private JComponent getGraphView(int type) {
		switch(type) {
			case LAYOUT_FORCE_DIRECTED:
			{
				if (gView == null) {	
					gView = new GraphView(gControl.getGraph(), "name", null);
					gView.getVisualization().putAction("updateGraph", new Action() {
	
						@Override
						public void run(double frac) {
							gControl.updateGraph();
						}
	
					});
					gView.getVisualization().getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
							new TupleSetListener() {
								public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
									if (add.length == 1) {
										// Fire selection change...
										if (add[0] instanceof Node) {
											Node n = (Node)add[0];
											EventDispatcher.fireEvent(this, new Event(CEvents.INSTANCE_ITEM_SELECTED, n.getString("alias")));
										}
									}
								}
							}
					);
				}
				return(gView);
			}
			
			case LAYOUT_RADIAL:
			{
				if (rView == null) {
					rView = new RadialGraphView(gControl.getGraph(), "name");
					rView.getVisualization().putAction("updateGraph", new Action() {
						
						@Override
						public void run(double frac) {
							gControl.updateGraph();
						}
	
					});
					gView.getVisualization().getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
							new TupleSetListener() {
								public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
									if (add.length == 1) {
										// Fire selection change...
										if (add[0] instanceof Node) {
											Node n = (Node)add[0];
											EventDispatcher.fireEvent(this, new Event(CEvents.INSTANCE_ITEM_SELECTED, n.getString("alias")));
										}
									}
								}
							}
					);
				}
				return(rView);
			}	
			
		}
		return(null);
	}
	
	public void onEvent(Event e) {
		switch(e.getType()) {
		case(CEvents.INSTANCE_ITEM_SELECTED):
		{
			String alias = (String) e.getData();
			CiBean bean = gControl.getResult().findOffspringAlias(alias);
			if (bean != null) {
				CiBean templ = bean;
				if (!bean.isTemplate()) {
					templ = TemplateModelControl.get("Root").getBean(bean.getDerivedFrom());
				}
				//tp.setTitleAt(0, "Attributes for " + bean.getDisplayName());
				instancePropertyControl.setRoot(new CIModel(templ, bean));
				TableModel tModel = pTree.getModel();
				if (tModel instanceof AbstractTableModel) {
					((AbstractTableModel)tModel).fireTableDataChanged();
				}
			}
		}
		break;

		}
	}
	
	
	public static void main(String argv[]) {
		OneCMDBConnection con = new OneCMDBConnection();
		con.setUrl("http://localhost:8080/onecmdb-desktop/onecmdb/query");
		OneCMDBConnection.setInstance(con);
		
		
		MainInstanceView view = new MainInstanceView(MainInstanceView.LAYOUT_FORCE_DIRECTED);
		JFrame frame = new JFrame();
		frame.setSize(600, 800);
		frame.getContentPane().add(view);
		
		view.initUI();
		
		frame.setVisible(true);
		
		XML2GraphQuery query = new XML2GraphQuery();
		try {
			GraphQuery q = query.parse(new FileInputStream(argv[0]), "UTF-8");
			view.update(q);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
}
