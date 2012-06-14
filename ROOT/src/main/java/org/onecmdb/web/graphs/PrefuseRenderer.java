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
package org.onecmdb.web.graphs;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.web.Area;
import org.onecmdb.web.ImageMap;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.*;
import prefuse.activity.Activity;
import prefuse.activity.ActivityAdapter;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.visual.DecoratorItem;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.InGroupPredicate;

public class PrefuseRenderer implements GraphRenderer {
    
    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
    public static final String EDGE_DECORATORS = "edgeDeco";

    private static final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema(); 
    static { 
        DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false); 
        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(128)); 
    }

    
    
   /* (non-Javadoc)
 * @see org.onecmdb.web.graphs.GraphRenderer#render(java.io.InputStream, java.io.OutputStream)
 */
public void render(final InputStream graphML, 
        final OutputStream output, final String format, 
        final ImageMap imagemap) throws IOException {
       
    final Graph g;
    try {
        g = new GraphMLReader().readGraph(graphML);
    } catch (DataIOException e1) {
        e1.printStackTrace();
        throw new IOException("Cannot parse input:" + e1.getMessage());
    }
       
       
    //     add the graph to the visualization as the data group "graph"
    //     nodes and edges are accessible as "graph.nodes" and "graph.edges"
    Visualization vis = new Visualization();
    vis.add(GRAPH, g);

       
    //     draw the "label" label for NodeItems
    final Renderer nodeR; {
        LabelRenderer r = new LabelRenderer("label");
        r.setRoundedCorner(4, 4); // round the corners
        r.setVerticalPadding(6);
        r.setHorizontalPadding(6);
        nodeR = r;
    }

    final Renderer edgeR = new EdgeRenderer(Constants.EDGE_TYPE_CURVE,
            Constants.EDGE_ARROW_FORWARD);

       
       //     create a new default renderer factory
       //     return our name label renderer as the default for all non-EdgeItems
       //     includes straight line edges for EdgeItems by default
       
       DefaultRendererFactory drf = new DefaultRendererFactory(nodeR, edgeR);
       drf.add(new InGroupPredicate(EDGE_DECORATORS), new LabelRenderer("label") );
       vis.setRendererFactory(drf);

       
       // adding decorators, one for the edges
       vis.addDecorators(EDGE_DECORATORS, EDGES, DECORATOR_SCHEMA);

       
       int[] palette = ColorLib.getGrayscalePalette(256);   
       palette[0] = ColorLib.rgb(0xFF,0xA5,0x00);
       
       
       
       //     map nominal data values to colors using our provided palette

       DataColorAction fill = new DataColorAction("graph.nodes", "distance",
               Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
       
       ItemAction outline = new StrokeAction("graph.nodes", new BasicStroke(2));
       ItemAction outlineColor = new ColorAction("graph.nodes",
               VisualItem.STROKECOLOR, ColorLib.gray(248));
       
       ColorAction text = new ColorAction("graph.nodes", 
               VisualItem.TEXTCOLOR, ColorLib.gray(255));
       
       ColorAction edges = new ColorAction("graph.edges",
               EdgeItem.STROKECOLOR, ColorLib.gray(100));

       ColorAction arrows = new ColorAction("graph.edges", 
               EdgeItem.FILLCOLOR, ColorLib.gray(100));
       

       //FontAction font = new FontAction("graph.nodes", new Font("Verdana", 0, 8));
       
       //     create an action list containing all color assignments
       ActionList color = new ActionList();
       color.add(outline);
       color.add(outlineColor);
       color.add(fill);
       color.add(text);
       color.add(edges);
       color.add(arrows);

       //color.add(font);

 
//     create an action list with an animated layout
//     the INFINITY parameter tells the action list to run indefinitely
       
       ActionList layout = new ActionList(vis);
       
       Layout alg = new FruchtermanReingoldLayout("graph", (int) (250 /* + Math.sqrt( (double) g.getNodeCount() + 1) */)  );
       layout.add(alg);
       
       layout.add(new LabelLayout2(EDGE_DECORATORS));
       
       //layout.add(new RepaintAction()); 
       
       
//     add the actions to the visualization
       vis.putAction("color", color);
       vis.putAction("layout", layout);
       

//     create a new Display that pull from our Visualization
       Display display = new Display(vis);
//       display.setOpaque(false);
//       display.setBackground(ColorLib.getColor(255, 255, 255, 255));
       
      
       
       int w = Math.min(g.getNodeCount() * 120 + 80, 720); 
       int h = Math.min(g.getNodeCount() * 60 + 40, 500);
       display.setSize(w, h); // set display size

       // used to make sure all actions have run before trying to gnerate
       // a picture.
       final CountDownLatch mutex = new CountDownLatch(2);
       class MutexData {
           boolean doneLayout = false;
       };

       ActivityAdapter listener = new ActivityAdapter() {
           @Override
           public void activityScheduled(Activity a) {
               super.activityScheduled(a);
           }
           @Override
           public void activityStarted(Activity a) {
               super.activityStarted(a);
           }
           @Override
           public void activityFinished(Activity a) {
               super.activityFinished(a);
               mutex.countDown();
           }
       };
       layout.addActivityListener(listener);
       color.addActivityListener(listener);
              
       {
           vis.run("color");
           vis.run("layout");
           try {
               mutex.await();
           } catch (InterruptedException e) { }
       }

 
       
       
       layout.removeActivityListener(listener);
       color.removeActivityListener(listener);
       listener = null;
       
       display.saveImage(output, format, 1);

       
       
       { // TODO: use a separate thread for this (to save some cycles)
           VisualTable nodes = (VisualTable) display.getVisualization().getGroup("graph.nodes");
           Object cookie = imagemap.startFilling();
           for (int r=0,rows=nodes.getRowCount(); r<rows; r++) {
               Rectangle2D rect = nodes.getBounds(r);
               Rectangle b = rect.getBounds();
               String s = nodes.getString(r, "label");
               ItemId ciid = new ItemId(nodes.getString(r, "id"));
               imagemap.addArea(new Area(ciid, b));
           }
           imagemap.stopFilling(cookie);
           
           display.setVisualization(null);
           g.dispose();
           display = null;
       }

       
       
   }
   
   
   /** displays a graph using swing */
   public void display(JComponent display) {
//       create a new window to hold the visualization
       JFrame frame = new JFrame("prefuse example");
//     ensure application exits when window is closed
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.add(display);
       frame.pack();           // layout components in window
       frame.setVisible(true); // show the window

//     display.addControlListener(new DragControl()); // drag items around
//     display.addControlListener(new PanControl());  // pan with background left-drag
//     display.addControlListener(new ZoomControl()); // zoom with vertical right-drag



       
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
           while ( iter.hasNext() ) {
               DecoratorItem item = (DecoratorItem)iter.next();
               VisualItem node = item.getDecoratedItem();
               Rectangle2D bounds = node.getBounds();
               setX(item, null, bounds.getCenterX());
               setY(item, null, bounds.getCenterY());
           }
       }
   } // end of inner class LabelLayout


   
}