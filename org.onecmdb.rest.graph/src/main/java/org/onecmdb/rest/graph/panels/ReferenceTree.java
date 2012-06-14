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
package org.onecmdb.rest.graph.panels;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.rest.graph.events.CEvents;
import org.onecmdb.rest.graph.events.Event;
import org.onecmdb.rest.graph.events.EventDispatcher;
import org.onecmdb.rest.graph.events.IEventListener;
import org.onecmdb.rest.graph.io.OneCMDBConnection;
import org.onecmdb.rest.graph.model.prefuse.TemplateModelControl;
import org.onecmdb.rest.graph.model.prefuse.TemplateRelationGraphControl;
import org.onecmdb.rest.graph.utils.swing.CheckBoxTreeRenderer;
import org.onecmdb.rest.graph.utils.swing.NodTableEditor;


import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.util.ui.JPrefuseTree;

public class ReferenceTree extends JPanel {

	private TemplateModelControl templateModel;
	JPanel panel = new JPanel();
	
	public ReferenceTree(TemplateModelControl tModel) {
		System.out.println("ReferenceTree[" + hashCode() + "].constructor(" + tModel + ")");
		this.templateModel = tModel;
		setLayout(new BorderLayout());
	}
	
	public void update(String template, String alias) {
		System.out.println("ReferenceTree[" + hashCode() + "].update() " + templateModel);
		TemplateRelationGraphControl refCtrl = new TemplateRelationGraphControl(templateModel);
		//Node n = refCtrl.reloadGraph(alias);
		Tree tree = refCtrl.getReferenceTree(template);
		//Tree tree = g.getSpanningTree(n);
		//n.set("checked", true);
		if (alias != null) {
			CiBean bean = OneCMDBConnection.instance().getBeanFromAlias(alias);
			if (bean != null) {
				tree.getRoot().set("name", bean.getDisplayName());
			}
			tree.getRoot().set("alias", alias);
		}
		
		JPrefuseTree jtree = new JPrefuseTree(tree, "name");
		CheckBoxTreeRenderer render = new CheckBoxTreeRenderer();

		jtree.setCellRenderer(render);
		jtree.setEditable(true);

		NodTableEditor editor = new NodTableEditor();
		editor.setListener(new IEventListener() {

			public void onEvent(Event e) {
				if (e.getData() instanceof Node) {
					Node n = (Node)e.getData();
					if (n.getBoolean("checked")) {
						EventDispatcher.fireEvent(this, new Event(CEvents.RELATION_NODE_SELECTED, n));
					} else {
						EventDispatcher.fireEvent(this, new Event(CEvents.RELATION_NODE_UNSELECTED, n));
					}
					/*
						n.getP
						Edge parentEdge = tree.getParentEdge(n);
						if (parentEdge != null) {
							System.out.println(parentEdge.getSourceNode().getString("alias")
									+ "-->[" + 
									parentEdge.getString("type") 
									+ "]-->" + 
									parentEdge.getTargetNode().getString("alias"));

							ModifyRelationEvent modEvent = new ModifyRelationEvent();
							modEvent.setSource(parentEdge.getSourceNode());
							modEvent.setTarget(parentEdge.getTargetNode());
							modEvent.setEdge(parentEdge);
							modEvent.setChecked(n.getBoolean("checked"));
							modEvent.setPrimary(getRoot(tree, n));
							modEvent.setClicked(n);


							EventDispatcher.fireEvent(this, modEvent);

						} else {

							Edge edge = refGraph.getEdge(n,n);
							//if (edge != null) {
								ModifyRelationEvent modEvent = new ModifyRelationEvent();
								modEvent.setClicked(n);
								modEvent.setSource(n);
								modEvent.setTarget(n);
								modEvent.setEdge(edge);
								modEvent.setChecked(n.getBoolean("checked"));
								modEvent.setPrimary(n);
								EventDispatcher.fireEvent(this, modEvent);
							//}

						}
					 */

				}

			}
		});
		jtree.setCellEditor(editor);
		
		//tp.add(new JScrollPane(jtree), "Reference(s)");

		/*
			GraphView view = new GraphView(ref2Graph, "name", null);
			tp.add(new JScrollPane(view), "Graph");
		 */
		/*
			if (refNode != null) {
				Tree t = refGraph.getSpanningTree(refNode);
				tp.add(new JScrollPane(new TreeView(t, "name")), "Tree View");
			}
		 */

		removeAll();
		add(new JScrollPane(jtree), BorderLayout.CENTER);
		revalidate();
	}
}
