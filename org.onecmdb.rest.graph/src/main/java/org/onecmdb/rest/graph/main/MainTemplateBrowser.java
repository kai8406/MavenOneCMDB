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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.onecmdb.core.utils.bean.CiBean;

import org.onecmdb.rest.graph.events.CEvents;
import org.onecmdb.rest.graph.events.Event;
import org.onecmdb.rest.graph.events.EventDispatcher;
import org.onecmdb.rest.graph.events.IEventListener;
import org.onecmdb.rest.graph.io.OneCMDBConnection;
import org.onecmdb.rest.graph.model.CIAttributeModel;
import org.onecmdb.rest.graph.model.CIModel;
import org.onecmdb.rest.graph.model.prefuse.TemplateModelControl;
import org.onecmdb.rest.graph.panels.TemplateReferencePanel;
import org.onecmdb.rest.graph.prefuse.view.TreeView;
import org.onecmdb.swing.treetable.JTreeTable;

import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;

public class MainTemplateBrowser extends JPanel implements IEventListener {
	
	private CIAttributeModel templatePropertyControl;
	
	private JTreeTable pTree;
	private String root;
	private MainInstanceBrowser instanceGraph;

	private JTabbedPane graphTab;

	private String referenceRoot;

	private JTabbedPane tp;

	public MainTemplateBrowser(String root, String rootRef) {
		this.root = root;
		this.referenceRoot = rootRef;
		EventDispatcher.addEventListener(this);
		initUI();
	}
	
	public void initUI() {
		templatePropertyControl = new CIAttributeModel();
		templatePropertyControl.setAdvanced(true);
		
		
		pTree = new JTreeTable(templatePropertyControl);
		//pTree.setDefaultEditor(CiBean.class, templatePropertyControl.getTableCellEditor());
		
		TemplateModelControl model = TemplateModelControl.get(root);
		System.out.println("MainTemplateBrowser.initUI() model=" + model.toString());
		Tree tree = model.getTemplateGraph().getSpanningTree();
		
		TreeView treeView = new TreeView(tree, "alias");
		treeView.getVisualization().getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
	            new TupleSetListener() {
	                public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
	                	if (add.length == 1) {
	                		// Fire selection change...
	                		Node n = (Node)add[0];
	                		EventDispatcher.fireEvent(this, new Event(CEvents.ITEM_SELECTED, n.getString("alias")));
	                	}
	                }
	            }
	        );
		
		/*
		TemplateModel refModel = TemplateModel.get(referenceRoot);
		
		Tree refTree = refModel.getTemplateGraph().getSpanningTree();
		TreeView refTreeView = new TreeView(refTree, "name");
		*/
		/*
		refTreeView.getVisualization().getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
	            new TupleSetListener() {
	                public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
	                	if (add.length == 1) {
	                		// Fire selection change...
	                		Node n = (Node)add[0];
	                		EventDispatcher.fireEvent(this, new Event(CEvents.ITEM_SELECTED, n.getString("alias")));
	                	}
	                }
	            }
	        );
		*/
		
		graphTab = new JTabbedPane();
		//graphTab.setTabPlacement(JTabbedPane.BOTTOM);
		TemplateReferencePanel refPanel = new TemplateReferencePanel(model);
		Dimension minimumSize = new Dimension(200, 200);
		treeView.setMinimumSize(minimumSize);
		refPanel.setMinimumSize(minimumSize);

		instanceGraph = new MainInstanceBrowser(this.root);
		graphTab.addTab("References to/from ", refPanel);
		graphTab.addTab("Instances of ", instanceGraph);
		//graphTab.addTab("All References", allRefPanel);
		
		JTabbedPane centerTab = new JTabbedPane();
		centerTab.addTab("Template Hierarchy", treeView);
		//centerTab.addTab("CiReferences" + " Templates", refTreeView);
		
		tp = new JTabbedPane();
		tp.add(new JScrollPane(pTree), "Attributes");
		tp.add(getInfoPanel(), "OneCMDB Info");
		//tp.add(new TemplateReferencePanel(), "Reference(s)");
		tp.setTabPlacement(JTabbedPane.TOP);
		final JSplitPane leftSplit = new JSplitPane();

		leftSplit.setTopComponent(centerTab);
		leftSplit.setBottomComponent(tp);
		leftSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		leftSplit.setOneTouchExpandable(true);
	
		
		final JSplitPane centerSplit = new JSplitPane();
		centerSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		centerSplit.setTopComponent(leftSplit);
		centerSplit.setBottomComponent(graphTab);
		centerSplit.setOneTouchExpandable(true);
		centerSplit.setDividerLocation(200);
		
		
		//centerSplit.setDividerLocation(200);
		
		setLayout(new BorderLayout());
		add(centerSplit, BorderLayout.CENTER);
	
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				leftSplit.setDividerLocation(0.8D);
				centerSplit.setDividerLocation(0.5D);
				invalidate();
		}
		});
		
		
	}
	
	private JComponent getInfoPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);
		 
		Box info = new Box(BoxLayout.Y_AXIS);
		//info.add(Box.createHorizontalStrut(5));
		JLabel l = new JLabel(TemplateModelControl.get(root).getTemplateInfoAsHTML());
		l.setAlignmentY(Component.TOP_ALIGNMENT);
		info.add(l);
		info.setAlignmentY(Component.TOP_ALIGNMENT);
		info.setBorder(BorderFactory.createTitledBorder("OneCMDB - Statistics"));
		//panel.add(Box.createVerticalStrut(5));
		panel.add(info);
		
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		return(panel);
	}

	public static void main(String argv[]) {
		JFrame frame = new JFrame();
		frame.setSize(500, 600);
		OneCMDBConnection con = new OneCMDBConnection();
		con.setUrl("http://localhost:8080/onecmdb-desktop/onecmdb/query");
		OneCMDBConnection.setInstance(con);
		MainTemplateBrowser mainWindow = new MainTemplateBrowser("Ci", "CIReference");
		frame.getContentPane().add(mainWindow);
		frame.setVisible(true);
	}

	public void onEvent(Event e) {
		switch(e.getType()) {
			case(CEvents.ITEM_SELECTED):
			{
				String alias = (String) e.getData();
				graphTab.setTitleAt(0, "References to/from " + alias);
				graphTab.setTitleAt(1, "Instances of " + alias);
				tp.setTitleAt(0, "Attributes for " + alias);
				instanceGraph.setInstanceTemplate(alias, null);
				CiBean bean = TemplateModelControl.get(root).getBean(alias);
				if (bean != null) {
					templatePropertyControl.setRoot(new CIModel(bean, bean));
					TableModel tModel = pTree.getModel();
					if (tModel instanceof AbstractTableModel) {
						((AbstractTableModel)tModel).fireTableDataChanged();
					}
				}
			}
			break;
		}
		
	}
	
	
}
