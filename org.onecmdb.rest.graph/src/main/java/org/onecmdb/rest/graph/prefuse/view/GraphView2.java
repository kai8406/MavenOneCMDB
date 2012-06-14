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
package org.onecmdb.rest.graph.prefuse.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JPanel;

import org.onecmdb.rest.graph.prefuse.action.EdgeVisibleAction;
import org.onecmdb.rest.graph.prefuse.action.VisabilityActivity;
import org.onecmdb.rest.graph.prefuse.action.distortion.ZoomDistortion;
import org.onecmdb.rest.graph.prefuse.controls.FlyInOutZoomControl;
import org.onecmdb.rest.graph.prefuse.layout.MyForcedDirectedLayout;
import org.onecmdb.rest.graph.prefuse.render.MyEdgeRenderer;
import org.onecmdb.rest.graph.utils.applet.AppletProperties;



import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.distortion.Distortion;
import prefuse.action.distortion.FisheyeDistortion;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.Layout;
import prefuse.activity.Activity;
import prefuse.controls.AnchorUpdateControl;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.DecoratorItem;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class GraphView2 extends JPanel {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
    public static final String EDGE_DECORATORS = "edgeDeco";

    private Visualization m_vis;
	private double m_scale_x = 2;
	private double m_scale_y = 2;
	private AnchorUpdateControl zoomDistortControl;
	private AnchorUpdateControl fisheyeDistortControl;
	private VisualGraph vg;
	private GraphDistanceFilter distanceFilter;
	private MyForcedDirectedLayout layout;
    
	 private static final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema(); 
	    static { 
	        DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false); 
	        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.blue(128)); 
	    }
	
    public Visualization getVisualization() {
    	return(m_vis);
    }
    
    public void magnify(boolean enable) {
    	Display d = m_vis.getDisplay(0);
    	if (enable) {
    		//animate(false);
    		d.addControlListener(zoomDistortControl);
    	} else {
    		d.removeControlListener(zoomDistortControl);
    		//animate(true);
    	}
    }
   
    public void fisheye(boolean enable) {
    	Display d = m_vis.getDisplay(0);
    	if (enable) {
    		animate(false);
    		d.addControlListener(fisheyeDistortControl);
    	} else {
    		d.removeControlListener(fisheyeDistortControl);
    		animate(true);
    	}
    }
    
    public void setEnforceBounds(boolean value) {
    	layout.setEnforceBounds(value);
    }
    
    public void animate(boolean enable) {
    	Action a = m_vis.getAction("layout");
    	if (a != null) {
    		a.setEnabled(enable);
    	}
    	if (!enable) {
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    public GraphView2(Graph g, String label, String edgeLabel) {
    	super(new BorderLayout());
    	
        // create a new, empty visualization for our data
        m_vis = new Visualization();
        
        // --------------------------------------------------------------------
        // set up the renderers
        EdgeRenderer m_edgeRenderer = new MyEdgeRenderer(Constants.EDGE_TYPE_CURVE, Constants.EDGE_ARROW_FORWARD);
        EdgeRenderer m_edgeRendererNoArrow = new EdgeRenderer(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_NONE);
         EdgeRenderer m_edgeRenderer2 = new EdgeRenderer(Constants.EDGE_TYPE_CURVE, Constants.EDGE_ARROW_NONE);
         m_edgeRenderer.setArrowHeadSize(4,12);
         
         ShapeRenderer sr = new ShapeRenderer();
         
        
        LabelRenderer tr = new LabelRenderer();
        tr.setRoundedCorner(8, 8);
        tr.setImageField("image");
        tr.setTextField(label);
        //tr.setMaxImageDimensions(32, 32);
        tr.setImagePosition(Constants.TOP);
        
        
        DefaultRendererFactory rf = new DefaultRendererFactory(tr, m_edgeRenderer);
        rf.add("[type] == \"Layer2Segment\"", sr);
        rf.add("[type] == \"PhysicalInterface\"", sr);
        rf.add("[type] == \"Layer1Segment\"", sr);
        rf.add("[type] == \"PhysicalSocket\"", sr);
        rf.add("[type] == \"Layer3Segment\"", sr);
        rf.add("[type] == \"LogicalInterface\"", sr);
        rf.add("[type] == \"Fiber\"", sr); 
        rf.add("[type] == \"SharedFiber\"", sr); 
        rf.add("[type] == \"Koppar\"", sr); 
         //rf.add(new InGroupPredicate(edges), m_edgeRenderer);
        
        if (edgeLabel != null) {
        	rf.add(new InGroupPredicate(EDGE_DECORATORS), new LabelRenderer(edgeLabel));
        }
        
        //rf.add(new EdgeTypeGroupPredicate(edges, "Inheritance"), m_edgeRenderer);
        //rf.add(new EdgeTypeGroupPredicate(edges, "Relation") , m_edgeRenderer2);
           
        m_vis.setRendererFactory(rf);

        // --------------------------------------------------------------------
        // register the data with a visualization
        
        // adds graph to visualization and sets renderer label field
        setGraph(g, label);
        if (edgeLabel != null) {
        	m_vis.addDecorators(EDGE_DECORATORS, edges, DECORATOR_SCHEMA);
        	m_vis.setValue(EDGE_DECORATORS, null, VisualItem.INTERACTIVE, Boolean.FALSE);
              
        }
        
        // fix selected focus nodes
        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS); 
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
            {
            	if (add.length == 1 && (add[0] instanceof EdgeItem)) {
            		return;
            	}
                for ( int i=0; i<rem.length; ++i ) {
                    VisualItem vi = (VisualItem)rem[i];
                	if (vi.getRow() >= 0) {
                		vi.setFixed(false);
                	}
                }
                for ( int i=0; i<add.length; ++i ) {
                    ((VisualItem)add[i]).setFixed(false);
                    ((VisualItem)add[i]).setFixed(true);
                }
                if ( ts.getTupleCount() == 0 ) {
                	/*
                    ts.addTuple(rem[0]);
                    ((VisualItem)rem[0]).setFixed(false);
                	*/
                }
                m_vis.run("draw");
            }
        });
        
        
        
        // --------------------------------------------------------------------
        // create actions to process the visual data
        VisabilityActivity visable = new VisabilityActivity();
        m_vis.putAction("allVisable", visable);
        
        distanceFilter = new GraphDistanceFilter(graph, 30);
        distanceFilter.setEnabled(false);
        
        ColorAction fill = new ColorAction(nodes, 
                VisualItem.FILLCOLOR, ColorLib.rgb(0x45,0x45,0x45));
        
        fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));
        fill.add("ingroup('" + Visualization.SEARCH_ITEMS + "')", ColorLib.rgb(255,190,190));
        
        ActionList draw = new ActionList();
        draw.add(distanceFilter);
        draw.add(fill);
        draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, ColorLib.rgb(255,255,255)));
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(EDGE_DECORATORS, VisualItem.TEXTCOLOR, ColorLib.rgb(200,200,200)));
        //draw.add(new EdgeVisibleAction(edges));     
        ActionList animate = new ActionList(Activity.INFINITY);
        
        ForceSimulator fsim = new ForceSimulator();
	    fsim.addForce(new NBodyForce());
	    fsim.addForce(new SpringForce(1E-5f, 150f));
	    fsim.addForce(new DragForce());
	  
        layout = new MyForcedDirectedLayout(graph, fsim);
        if (edgeLabel != null) {
        	animate.add(new LabelLayout2(EDGE_DECORATORS));
        } 
        animate.add(layout);
        animate.add(fill);
       
        animate.add(new RepaintAction());
        
        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        m_vis.putAction("draw", draw);
        m_vis.putAction("layout", animate);
        
        m_vis.runAfter("draw", "layout");
        
        // Zoom to Fit action.
        m_vis.putAction("zoomToFit", getZoomToFitAction());
        // --------------------------------------------------------------------
        // set up a display to show the visualization
        
        Display display = new Display(m_vis);
        /*
        display.setSize(700,700);
        display.pan(350, 350);
        */
        
        // main display controls
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new ZoomToFitControl());
        display.addControlListener(new NeighborHighlightControl());
        display.addControlListener(new FlyInOutZoomControl());
        // overview display
//        Display overview = new Display(vis);
//        overview.setSize(290,290);
//        overview.addItemBoundsListener(new FitOverviewListener());
        
        display.setForeground(Color.GRAY);
        Color bgColor = new Color(0x45, 0x45, 0x45);
        
        String bgColorValue = AppletProperties.get("graphBackgroundColor");
        
        if (bgColorValue != null) {
        	try {
        		if (bgColorValue.startsWith("0x")) {
        			bgColorValue = bgColorValue.substring(2);
        		}
        		int color = Integer.parseInt(bgColorValue, 16);
        		bgColor = new Color(color);
        	} catch (Throwable e) {
        		
        	}
        }
        display.setBackground(bgColor);
        
        /*
        // fisheye distortion based on the current anchor location
        Distortion feye = new FisheyeDistortion(0, m_scale_y);
        
        */
        {
        	ActionList distort = new ActionList();
        	Distortion mag = new ZoomDistortion();
        	mag.setGroup(nodes);
        	distort.add(mag);
        	//distort.add(new RepaintAction());

        	m_vis.putAction("distortZoom", distort);
        	zoomDistortControl = new AnchorUpdateControl(mag, "distortZoom");
        }
        {
        	ActionList distort = new ActionList();
        	Distortion feye = new FisheyeDistortion(3, 3);
        	feye.setGroup(nodes);
        	distort.add(feye);
        	distort.add(new RepaintAction());

        	m_vis.putAction("distortfEye", distort);
        	fisheyeDistortControl = new AnchorUpdateControl(feye, "distortfEye");
        }
        // update the distortion anchor position to be the current
        // location of the mouse pointer
        
        //display.addControlListener(new ToolTipControl(new String[] {"name", "image"}));
        
        // Handle Search
        SearchTupleSet search = new PrefixSearchTupleSet();
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
        search.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                //m_vis.cancel("animatePaint");
                //m_vis.run("recolor");
                //m_vis.run("animatePaint");
            }
        });
        
        // now we run our action list
        m_vis.run("draw");
        
        add(display);
    }
   
    private Action getZoomToFitAction() {
    	return(new Action() {

			@Override
			public void run(double frac) {
				Rectangle2D bounds = m_vis.getBounds(Visualization.ALL_ITEMS);
				Display display = m_vis.getDisplay(0);
		        GraphicsLib.expand(bounds, 50 + (int)(1/display.getScale()));
		        DisplayLib.fitViewToBounds(display, bounds, 2000);
			}
    		
    	});
	}
    
    /**
     * Set label positions. Labels are assumed to be DecoratorItem instances,
     * decorating their respective nodes. The layout simply gets the bounds
     * of the decorated node and assigns the label coordinates to the center
     * of those bounds.
     */
    class LabelLayout2 extends Layout {
        public LabelLayout2(String group) {
            super(group);
        }
        public void run(double frac) {
            Iterator iter = m_vis.items(m_group);
            
            int count = 0;
            while ( iter.hasNext() ) {
                DecoratorItem item = (DecoratorItem)iter.next();
                VisualItem node = item.getDecoratedItem();
                if (node.isVisible()) {
                  	  item.setVisible(true);
                      count++;
                	  Rectangle2D bounds = node.getBounds();
                      setX(item, null, bounds.getCenterX());
                      setY(item, null, bounds.getCenterY());
                } else {
                	item.setVisible(false);
                }
                
            }
        }
    } // end of inner class LabelLayout
    
	/**
     * Set node fill colors
     */
    public static class NodeColorAction extends ColorAction {
        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR, ColorLib.rgba(255,255,255,0));
            add("_hover", ColorLib.gray(220,230));
            add("ingroup('_search_')", ColorLib.rgb(255,190,190));
            add("ingroup('_focus_')", ColorLib.rgb(198,229,229));
        }
                
    } // end of inner class NodeColorAction
 
    public VisualGraph getVisaulGraph() {
    	return(vg);
    }
    public void setGraph(Graph g, String label) {
        // update labeling
    	/*
        DefaultRendererFactory drf = (DefaultRendererFactory)
                                                m_vis.getRendererFactory();
        ((LabelRenderer)drf.getDefaultRenderer()).setTextField(label);
         */
        // update graph
        m_vis.removeGroup(graph);
        vg = m_vis.addGraph(graph, g);
        m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);
        /*
        VisualItem f = (VisualItem)vg.getNode(0);
        m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
        f.setFixed(false);
    	*/
    }
    
    // ------------------------------------------------------------------------
   
   
    class FlyZoomAction extends Action {
    	
		@Override
		public void run(double frac) {
			
		}
    	
    }
    // ------------------------------------------------------------------------
    
    
    public static class FitOverviewListener implements ItemBoundsListener {
        private Rectangle2D m_bounds = new Rectangle2D.Double();
        private Rectangle2D m_temp = new Rectangle2D.Double();
        private double m_d = 15;
        public void itemBoundsChanged(Display d) {
            d.getItemBounds(m_temp);
            GraphicsLib.expand(m_temp, 25/d.getScale());
            
            double dd = m_d/d.getScale();
            double xd = Math.abs(m_temp.getMinX()-m_bounds.getMinX());
            double yd = Math.abs(m_temp.getMinY()-m_bounds.getMinY());
            double wd = Math.abs(m_temp.getWidth()-m_bounds.getWidth());
            double hd = Math.abs(m_temp.getHeight()-m_bounds.getHeight());
            if ( xd>dd || yd>dd || wd>dd || hd>dd ) {
                m_bounds.setFrame(m_temp);
                DisplayLib.fitViewToBounds(d, m_bounds, 0);
            }
        }
    }

    public void setFocus(Node n) {
    	 TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
    	 ts.clear();
    	 VisualItem item = m_vis.getVisualItem(nodes, n);

    	 if (item != null) {
        	 ts.setTuple(item);
         }
    }
    
	public void redrawAndZoomToFit() {
		/*
		VisualItem f = (VisualItem)vg.getNode(0);
	    if (f != null) {
	    	m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
	    	//f.set("_focus_", true);
	    }
	    */
	    m_vis.run("draw");
	    // Center focus node.
	    //m_vis.run("zoomToFit");
	    //zoomToFit();
	    
	    new Thread(new Runnable() {
	    	public void run() {
	    		try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		zoomToFit();
	    	}
	    }).start();
	}
	  
	
	public void zoomToFit() {
		Rectangle2D bounds = m_vis.getBounds(Visualization.ALL_ITEMS);
		Display display = m_vis.getDisplay(0);
        GraphicsLib.expand(bounds, 50 + (int)(1/display.getScale()));
        DisplayLib.fitViewToBounds(display, bounds, 2000);
	}

	public void setDistance(int intValue) {
		 distanceFilter.setDistance(intValue);
         m_vis.run("draw");
	}
	
	public boolean isDistanceEnabled() {
		return(distanceFilter.isEnabled());
	}
	
	public void setEnableDistanceFilter(boolean enable) {
		distanceFilter.setEnabled(enable);
		if (!enable) {
			m_vis.run("allVisable");
		}
		m_vis.run("draw");
	}
    
} // end of class GraphView
