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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Currency;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTML;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.core.utils.graph.result.Template;
import org.onecmdb.rest.graph.events.CEvents;
import org.onecmdb.rest.graph.events.Event;
import org.onecmdb.rest.graph.events.EventDispatcher;
import org.onecmdb.rest.graph.events.IEventListener;
import org.onecmdb.rest.graph.model.CIAttributeModel;
import org.onecmdb.rest.graph.model.CIModel;
import org.onecmdb.rest.graph.model.prefuse.InstanceGraphControl;
import org.onecmdb.rest.graph.model.prefuse.TemplateModelControl;
import org.onecmdb.rest.graph.panels.ReferenceTree;
import org.onecmdb.rest.graph.prefuse.view.GraphView;
import org.onecmdb.rest.graph.prefuse.view.RadialGraphView;
import org.onecmdb.rest.graph.utils.swing.TextInputField;
import org.onecmdb.swing.treetable.JTreeTable;

import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ui.JPrefuseTree;
import prefuse.util.ui.JValueSlider;

public class MainInstanceBrowser extends JPanel implements IEventListener {

	private static final int LAYOUT_FORCE_DIRECTED = 1;
	private static final int LAYOUT_RADIAL = 2;
	
	private String rootCI;
	private InstanceGraphControl gControl = new InstanceGraphControl();
	private ReferenceTree referenceTree;
	private GraphView gView;
	private String template;
	private CIAttributeModel instancePropertyControl;
	private JTreeTable pTree;
	private int graphType = LAYOUT_FORCE_DIRECTED;
	private JSplitPane rightSplit;
	private JLabel infoLabel = new JLabel();
	private Box input;
	private JTabbedPane tp;
	private JTabbedPane referenceTp;
	private RadialGraphView rView;
	private JCheckBox exclude;
	private String instanceAlias;

	


	public MainInstanceBrowser(String root) {
		EventDispatcher.addEventListener(this);
		this.rootCI = root;
		System.out.println("New MainInstanceBrowser");
		this.referenceTree = new ReferenceTree(TemplateModelControl.get(rootCI));		
		initUI();
		
	}
	
	public void setInstanceTemplate(String template, String instanceAlias) {
		this.template = template;
		this.instanceAlias = instanceAlias;
		this.referenceTree.update(template, instanceAlias);
		gControl.reset();
		gControl.setRoot(template, instanceAlias, true, true);
		if (instanceAlias != null) {
			gControl.setExclude(false);
			exclude.setSelected(false);
		}
		//gControl.update();
		updateGraph();
	}
	
	/**
	 * CenterSpilt
	 * 	Left 
	 * 	    Reference Tree
	 *  Right
	 * 		Top - GraphView
	 * 		Bottom - TabPannel
	 * 			Query
	 * 			Attribute
	 * 			Status
	 */
	private void initUI() {
		instancePropertyControl = new CIAttributeModel();
		instancePropertyControl.setAdvanced(false);
		
		pTree = new JTreeTable(instancePropertyControl);
		

		
		
		final JSplitPane centerSplit = new JSplitPane();
		centerSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		/*
		final JSplitPane leftSplit = new JSplitPane();
		leftSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		*/
		rightSplit = new JSplitPane();
		rightSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
		
		
		tp = new JTabbedPane();
		tp.setTabPlacement(JTabbedPane.TOP);
		tp.add(new JScrollPane(pTree), "Attributes");
		//tp.add(new JScrollPane(getControlPanel()), "Graph Control");
		
		
		//leftSplit.setBottomComponent(getTemplateTreePanel());
		//leftSplit.setTopComponent(getReferenceTreePanel());
		
		rightSplit.setTopComponent(getGraphView(graphType));
		rightSplit.setBottomComponent(tp);
		rightSplit.setOneTouchExpandable(true);
		rightSplit.setDividerLocation(200);
		
	/*
		JSplitPane refSplit = new JSplitPane();
		refSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		refSplit.setTopComponent(getReferenceTreePanel());
		refSplit.setBottomComponent(getControlPanel());
		*/
		centerSplit.setTopComponent(rightSplit);
		referenceTp = new JTabbedPane();
		referenceTp.setBackground(Color.WHITE);
		referenceTp.add(this.referenceTree, "References");
		referenceTp.add(getSearchPanel(), "Search");
		referenceTp.add(getControlPanel(), "Graph Control");
		
		centerSplit.setBottomComponent(referenceTp);
		centerSplit.setDividerLocation(200);
		centerSplit.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(centerSplit, BorderLayout.CENTER);
		
	
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				centerSplit.setDividerLocation(0.70D);
				rightSplit.setDividerLocation(0.75D);
				invalidate();
		}
		});
		
		
		// Start....
		
	}

	public JComponent getSearchPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		panel.setBackground(Color.WHITE);
	    
		input = new Box(BoxLayout.Y_AXIS);
	    
		
		Box right = new Box(BoxLayout.Y_AXIS);
		right.setAlignmentX(Component.LEFT_ALIGNMENT);
		right.add(input);
		right.add(infoLabel);
		//right.setBorder(BorderFactory.createTitledBorder("Info"));
		
		right.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(right);
		panel.add(Box.createHorizontalStrut(5));
		
		
		return(new JScrollPane(panel));
		
	}
	
	public JComponent getControlPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBackground(Color.WHITE);
	    
		final JCheckBox forceBounds = new JCheckBox("Keep objects whithin window");
	    forceBounds.setBackground(Color.WHITE);
	    forceBounds.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				gView.setEnforceBounds(forceBounds.isSelected());
			}
	    	
	    });
	    final JCheckBox animation = new JCheckBox("Dynamic graph", true);
	    animation.setBackground(Color.WHITE);
	    animation.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				gView.animate(animation.isSelected());
			}
	    	
	    });
	    
	    exclude = new JCheckBox("Include non-referenced objects", true);
	    exclude.setBackground(Color.WHITE);
	    exclude.addActionListener(new ActionListener() {

	    	public void actionPerformed(ActionEvent e) {
	    				gControl.setExclude(exclude.isSelected());
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						reloadGraph();		
					}
				});
			}
	    });

	    final JValueSlider slider = new JValueSlider("Max distance", 0, 10, 1);
	    slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               gView.setDistance(slider.getValue().intValue());
            }
        });
	    slider.setPreferredSize(new Dimension(290, 20));
	    slider.setMaximumSize(new Dimension(290, 20));
	    for (Component comp : slider.getComponents()) {
	    	comp.setEnabled(false);
	    }
	    final JCheckBox distanceFilter = new JCheckBox("Enable distance filter", false);
	    distanceFilter.setBackground(Color.WHITE);
	    distanceFilter.addActionListener(new ActionListener() {

	    	public void actionPerformed(ActionEvent e) {
	    		if (distanceFilter.isSelected()) {
	    			gView.setDistance(slider.getValue().intValue());
	    		}
	    		gView.setEnableDistanceFilter(distanceFilter.isSelected());
	    		 for (Component comp : slider.getComponents()) {
	    		    	comp.setEnabled(distanceFilter.isSelected());
	    		  }
	    	}
	    });
	    
	    
	    
	    final TextInputField limit = new TextInputField("Max no of instances per template", "" + gControl.getMaxSize());
	    limit.getInput().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						Integer i = Integer.parseInt(limit.getInput().getText());
						gControl.setMaxSize(i);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								reloadGraph();
							}
						});
					} catch (Throwable t) {
						limit.getInput().setText("NaN");
					}
				}
			}
	    	
	    });
	    
	    panel.setAlignmentX(Component.LEFT_ALIGNMENT);
	    forceBounds.setAlignmentX(Component.LEFT_ALIGNMENT);
	    animation.setAlignmentX(Component.LEFT_ALIGNMENT);
	    exclude.setAlignmentX(Component.LEFT_ALIGNMENT);
	    distanceFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
	    slider.setAlignmentX(Component.LEFT_ALIGNMENT);
	    
	    Box checks = new Box(BoxLayout.Y_AXIS);
        checks.add(forceBounds);
        checks.add(animation);
        checks.add(exclude);
        checks.add(distanceFilter);
        checks.add(slider);
        
        
        checks.setBorder(BorderFactory.createTitledBorder(""));
    
        Box max = new Box(BoxLayout.Y_AXIS);
        limit.setAlignmentX(Component.LEFT_ALIGNMENT);
        max.add(limit);
        max.setBorder(BorderFactory.createTitledBorder(""));
        
        
        Box left = new Box(BoxLayout.Y_AXIS);
        
       
        left.add(checks);
        left.add(Box.createVerticalStrut(5));
		left.add(max);
		
		left.add(Box.createVerticalStrut(5));
		
		Box help = new Box(BoxLayout.Y_AXIS);
	    help.setBorder(BorderFactory.createTitledBorder("Graph - Help"));
	          
	    StringBuffer b = new StringBuffer();
	    b.append("<html>");
	    b.append("Hold left mouse button and move mouse:<br>&nbsp;-Move the graph<br>");
	    b.append("Hold right mouse button and move mouse:<br>&nbsp;-Zoom in/out<br>");
	    b.append("Turn mouse wheel: <br>&nbsp;-Zoom in/out<br>");
	    b.append("Single-click with right mouse button:<br>&nbsp;-Adjust zoom so all objects fit within window<br>");
	    b.append("Double-click with left mouse button:<br>&nbsp;-Center where the cursor is and zoom in<br>");
	    b.append("Single-click with left mouse button on an object:<br>&nbsp;-Select this object<br>");
	    b.append("Hold left mouse button on an object and move mouse:<br>&nbsp;-Move this object<br>");
	    b.append("</html>");
	    JLabel label = new JLabel(b.toString());
	    label.setFont(new Font("Times", Font.PLAIN, 12));
	    help.add(label);
		left.add(help);
		
		left.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(left);
		panel.add(Box.createHorizontalStrut(5));
		
		return(new JScrollPane(panel));
	}
	/*
	private JComponent getTemplateTreeViewPanel() {
		Tree tree = TemplateModel.get(root).getTemplateGraph().getSpanningTree();
		TreeView view = new TreeView(tree, "name");
		view.getVisualization().getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
	            new TupleSetListener() {
	                public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
	                	if (add.length == 1) {
	                		// Fire selection change...
	                		Node n = (Node)add[0];
	                		EventDispatcher.fireEvent(this, new Event(CEvents.NODE_SELECTED, n));
	                	}
	                }
	            }
	        );
		return(view);
	}
	private JComponent getTemplateTreePanel() {
		if (true) {
			return(new JPanel());
		}
		Graph templateGraph = TemplateModel.get(root).getTemplateGraph();
		Tree tree = templateGraph.getSpanningTree();
		JPrefuseTree jtree = new JPrefuseTree(tree, "name");
		CheckBoxTreeRenderer render = new CheckBoxTreeRenderer();
		jtree.setCellRenderer(render);
		jtree.setEditable(true);
		
		NodTableEditor editor = new NodTableEditor();
		editor.setListener(new IEventListener() {

			public void onEvent(Event e) {
				Node n = (Node)e.getData();
				if (n.getBoolean("checked")) {
					EventDispatcher.fireEvent(this, new Event(CEvents.NODE_SELECTED, e.getData()));
				} else {
					EventDispatcher.fireEvent(this, new Event(CEvents.NODE_UNSELECTED, e.getData()));
				}
			}
			
		});

		jtree.setCellEditor(editor);
		
		return(new JScrollPane(jtree));
	}
	*/


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
		case CEvents.NODE_SELECTED:
		{
			Node node = (Node) e.getData();
			String alias = node.getString("alias");
			String template = node.getString("template");
			referenceTree.update(template, alias);
			gControl.reset();
			gControl.addNode(alias, true, true);
			//gControl.update();
			updateGraph();
			
		}
			break;
		case CEvents.NODE_UNSELECTED:
		{	
			Node node = (Node) e.getData();
			String alias = node.getString("alias");
			gControl.removeNode(alias);
			//gControl.update();
			updateGraph();
			
		}
			break;
		case CEvents.RELATION_NODE_UNSELECTED:
		{
			Node n = (Node) e.getData();
			if (n.getParent() != null) {
				String parentAlias = n.getParent().getString("alias");
				String alias = n.getString("alias");
				
				Edge edge = n.getParentEdge();
				
				Node source = edge.getSourceNode();
				Node target = edge.getTargetNode();
				String sourceAlias = source.getString("alias");
				String targetAlias = target.getString("alias");
				boolean inBound = edge.getBoolean("inBound");
				if (inBound) {
					String tmp = sourceAlias;
					sourceAlias = targetAlias;
					targetAlias = tmp;
				}
				String refType = edge.getString("type");
				if (!parentAlias.equals(alias)) {
					gControl.removeNode(alias);
				}
				// Remove relation.
				gControl.removeRelation(sourceAlias, refType, targetAlias);
				//gControl.update();
				updateGraph();
			} else {
				n.set("checked", true);
			}
		}
		break;
		case CEvents.RELATION_NODE_SELECTED:
		{	
			Node n = (Node) e.getData();
			
			while(n != null) {
				boolean checked = n.getBoolean("checked");
				String alias = n.getString("alias");
				System.out.println("REL: alias=" + alias + ", checked=" + checked);
				//gControl.addNode(alias, false, checked);
				Edge edge = n.getParentEdge();
				if (edge != null) {	
					String refType = "Reference";
				
					refType = edge.getString("type");
					boolean inBound = edge.getBoolean("inBound");
						
					Node source = edge.getSourceNode();
					Node target = edge.getTargetNode();
					String sourceAlias = source.getString("alias");
					String targetAlias = target.getString("alias");
					
					if (inBound) {
						String tmp = sourceAlias;
						sourceAlias = targetAlias;
						targetAlias = tmp;
					}
					gControl.addNode(sourceAlias, false, true);
					gControl.addNode(targetAlias, false, true);
							
					gControl.addRelation(sourceAlias, refType, targetAlias);
					System.out.println("\tSOURCE=" + source.get("alias") + ", target=" + target.get("alias"));
					
				}
				n = n.getParent();	
			}
			//gControl.update();
			updateGraph();
		}
			break;
		case(CEvents.INSTANCE_ITEM_SELECTED):
		{
			String alias = (String) e.getData();
			CiBean bean = gControl.getResult().findOffspringAlias(alias);
			if (bean != null) {
				CiBean templ = bean;
				if (!bean.isTemplate()) {
					templ = TemplateModelControl.get(rootCI).getBean(bean.getDerivedFrom());
				}
				tp.setTitleAt(0, "Attributes for " + bean.getDisplayName());
				instancePropertyControl.setRoot(new CIModel(templ, bean));
				TableModel tModel = pTree.getModel();
				if (tModel instanceof AbstractTableModel) {
					((AbstractTableModel)tModel).fireTableDataChanged();
				}
			}
		}
		break;
		case(CEvents.INSTANCE_SET_LAYOUT):
			int layout = (Integer)e.getData();
			setGraphType(layout);
			break;
		}
		System.out.println("EVENT: " + e.getType() + " : " + e.getData());
		
	}

	private void setGraphType(int type) {
		if (type == graphType) {
			return;
		}
		JComponent graph = getGraphView(type);
		if (graph == null) {
			return;
		}
		rightSplit.setTopComponent(null);
		rightSplit.setTopComponent(graph);
		invalidate();
	}

	private void reloadGraph() {
		//gControl.update();
		updateGraph();
	}
	
	private void updateGraph() {
		
		new Thread(new Runnable() {
			public void run() {
				gControl.update();

				// Update Info Label..
				updateInfo();


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
		}).start();

	}

	private void updateInfo() {
		infoLabel.setText("<html>Info<br>" + gControl.getResultAsHTML() + "</html>");
		// Update input field.
		input.removeAll();
		    
		for (final Template t : gControl.getResult().getNodes()) {
			if (instanceAlias != null) {
				if (t.getId().equals(instanceAlias)) {
					continue;
				}
			}
			String text = gControl.getSearch(t.getId());
			final TextInputField search = new TextInputField("Search " + t.getTemplate().getAlias(), text);
			search.setAlignmentX(Component.LEFT_ALIGNMENT);
			search.getInput().addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void keyReleased(KeyEvent e) {
					gControl.setSearch(t.getId(), search.getInput().getText());
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						SwingUtilities.invokeLater(new Runnable() {

							public void run() {
								reloadGraph();		
							}
						});
						
					}
				}

				public void keyTyped(KeyEvent e) {
					
				}

				
				
			});
			input.add(search);
		}
        input.setBorder(BorderFactory.createTitledBorder(""));
        input.revalidate();
        
	}

}
