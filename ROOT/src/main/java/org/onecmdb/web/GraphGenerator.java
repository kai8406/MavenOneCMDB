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
package org.onecmdb.web;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.onecmdb.core.IAttribute;
import org.onecmdb.core.ICi;
import org.onecmdb.core.IModelService;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.IReference;
import org.onecmdb.core.IReferenceService;
import org.onecmdb.core.ISession;
import org.onecmdb.core.IType;
import org.onecmdb.core.internal.model.ItemId;
import org.onecmdb.web.graphs.EGraphRelation;
import org.onecmdb.web.graphs.PrefuseRenderer;
import org.onecmdb.web.tags.SwingImageCreator;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


/**
 * A controller used to handle graph rendering
 * @author nogun
 *
 */
public class GraphGenerator extends MultiActionController {

    /** format to use for the images created */
    final static private String MIMETYPE = "image/png";

    private SiteController siteController;    

    // {{{ bean support 
    
    /**
     * NOTE: Used to satisfy spring only.
     */
    
    public void setSiteController(SiteController controller) {
        this.siteController = controller;
    }
    public SiteController getSiteController() {
        return this.siteController;
    }

    public void init() {
        if (getSiteController() == null) {
            throw new IllegalStateException("Reference to SiteController missing!");
        }
    }
    // }}}

    
    protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("scratch", new HashMap() );
        data.put("time", new Date());
        return data; 
     }
    
    
    @Override
    protected ServletRequestDataBinder createBinder(ServletRequest request, Object command) throws Exception {
        
        String n = getCommandName(command);
        ServletRequestDataBinder binder = super.createBinder(request, command);
        return binder;
    }
    
    @Override
    protected void initBinder(ServletRequest request, ServletRequestDataBinder binder) throws Exception {
            binder.registerCustomEditor(ItemId.class, new ItemIdEditor());
            binder.registerCustomEditor(EnumSet.class, new EnumSetEditor());
    }
    
    private Map<ItemId, ImageMap> getGraphs(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<ItemId, ImageMap> graphs = (Map<ItemId, ImageMap>) session.getAttribute("_graphs_");
        if (graphs == null) {
            graphs = Collections.synchronizedMap(new HashMap<ItemId, ImageMap>()); 
            session.setAttribute("_graphs_", graphs);
        }
        return graphs;
    }
    
    public ModelAndView imagemapHandler(
            HttpServletRequest request, HttpServletResponse response,
            final GraphCommand graphCommand) throws Exception 
    {
        
        Map<String,Object> data = new HashMap<String,Object>();

        
        SiteController sitectrl = getSiteController();
        SiteCommand sitecmd = sitectrl.getSiteCommand(request);
        if (sitecmd == null) {
            return new ModelAndView("main");
        }
        
        { // mimic ardinary requests
            Map refs = referenceData(request, graphCommand, null);
            data.putAll(refs);
        }
        
        ItemId ciid = graphCommand.getCiid();
        Map<String,String> params = new HashMap<String,String>();
        
        data.put("params", params);
        params.put("ci", ciid.toString());
        
        {
            SiteAction nav = sitecmd.getActionMap().get("viewci");
            data.put("action", nav);
        }
        
        
        String qs = request.getQueryString();
        Pattern p = Pattern.compile("^.*\\?(\\d+),(\\d+)$");
        Matcher m = p.matcher(qs);
        if (m.matches()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            Point click = new Point(x,y);

            Map<ItemId, ImageMap> graphs = getGraphs(request);
            ImageMap imagemap = null;
            try {
                synchronized(graphs) {
                    long start = System.currentTimeMillis();
                    long stop = start;
                    do {
                        imagemap = graphs.get(ciid);
                        if (imagemap == null) {
                            graphs.wait(10000);
                        }
                        stop = System.currentTimeMillis();;
                    } while (imagemap == null && stop-start < 10000);
                }
                if (imagemap != null) {
                    synchronized(imagemap) {
                        long start = System.currentTimeMillis();
                        long stop = start;
                        do {
                            if (imagemap.isFillng()) {
                                imagemap.wait(10000);
                            }
                            stop = System.currentTimeMillis();;
                        } while (imagemap.isFillng() && stop-start < 10000);
                    }
                    if (!imagemap.isFillng()) {
                        Area area = imagemap.getArea(click);
                        if (area != null) {
                            params.put("ci", area.getItemId().toString());
                        }
                     }
                    //graphs.remove(ciid);
                }
            } catch (InterruptedException e) {}
        }
        return new ModelAndView("forward", data);

    }

    
    public ModelAndView generateHandler(
            HttpServletRequest request, HttpServletResponse response,
            final GraphCommand graphCommand) 
    throws Exception {
        
        final ServletOutputStream out = response.getOutputStream();

        /** all content produced should follow the declared mime type */
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType(MIMETYPE);
        
        SiteController ctl = getSiteController();
        final SiteCommand siteSession = ctl.getSiteCommand(request);
        
        final BufferedImage image;

        final ItemId ciid = graphCommand.getCiid();

        if (siteSession == null) {
            image = createTextImage("NO SESSION");
            SwingImageCreator.writeImageToStream(image, MIMETYPE, out);
            
        } else {
            final ISession session = siteSession.getSession();
            final IModelService model = (IModelService) session.getService(IModelService.class);
            final IReferenceService refs = (IReferenceService) session.getService(IReferenceService.class);

            final ICi base = model.find(ciid);
            if (base == null) {
                image = createTextImage("MISSING CI: " + graphCommand.getCiid());
                SwingImageCreator.writeImageToStream(image, MIMETYPE, out);
            
            } else {
                final PipedInputStream pins = new PipedInputStream();
                final PipedOutputStream pouts = new PipedOutputStream(pins);
                
                ImageMap imagemap = new ImageMap();
                Map<ItemId, ImageMap> graphs = getGraphs(request);
                graphs.put(ciid, imagemap);
                synchronized (graphs) {
                    graphs.notify();
                }
                
              
                Thread consumer = conusumeMlInput(base.getId().toString(), pins, out, "PNG",
                        imagemap);

                boolean userMode = true;
                
                SiteCommand sitecmd = ctl.getSiteCommand(request);
                if (sitecmd != null) {
                	if (sitecmd != null) {
                		if (!sitecmd.getMode().equals("user")) {
                			userMode = false;
                		}
                	}
                }
                
                
                produceMlInput(session, graphCommand, userMode, base, pouts);
                pouts.close(); // nothing more to write
                consumer.join();
                pins.close();
            }
                
            //image = SwingImageCreator.createImage(graph);
        }
        
        out.flush();
        
        
        return null;
    }
    
    
    
    private void produceMlInput(final ISession session, 
            final GraphCommand grapgctl, final boolean userMode,
            final ICi base, PipedOutputStream pouts) throws UnsupportedEncodingException 
    {
        final MlGraph<ICi, ICi> graph = new MlGraph<ICi, ICi>(pouts);
        graph.populate(new GraphWorker<ICi,ICi>() {
            final IReferenceService refs = (IReferenceService) session.getService(IReferenceService.class);
            private MlGraph<ICi, ICi> ml;

            public void run(MlGraph<ICi,ICi> ml) {
                this.ml = ml;
                
                ml.addNode(base, 0); // Center
                this.advance(0, base);
            }

            private void outGoing(int level, final ICi base) {
                if (level == grapgctl.depth()) {
                    return;
                }
                
                Set<IAttribute> atts = base.getAttributes();
                Set<ICi> nextLevel = new HashSet<ICi>();
                for (IAttribute a : atts) {
                	ICi v = null;
                	ICi r = null;
                	if (base.isBlueprint()) {
                		IType type = a.getValueType();
                		
                		if (type instanceof ICi) {
                			v = (ICi)type;
                		}
                		IType refType = a.getReferenceType();
                		if (refType instanceof ICi) {
                			r = (ICi)refType;
                		}
                	} else {
                		r = a.getReference();
                    	if (r != null) {
                    		v = (ICi) a.getValue();
                    	}
                	}
                	if (v != null) {
                		if (!ml.containsNode(v)) { 
                			ml.addNode(v,  level); // one level
                			nextLevel.add(v);
                		}
                		if (userMode) {
                			ml.addEdge(base, v);
                		} else {
                			ml.addEdge(base, v, r, a);
                		}
                	}
                }
            
                for (ICi v : nextLevel) {
                    this.advance(level, v);
                }
            }
            
            private void inGoing(int level, ICi base) {
                if (level == grapgctl.depth()) 
                    return;
                
                Set<IReference> incoming = refs.getReferrers(base);
                Set<ICi> nextLevel = new HashSet<ICi>();
                for (IReference r : incoming) {
                    for (ICi s: r.getSourceCis()) {
                        if (!ml.containsNode(s)) { 
                            ml.addNode(s, level); //  
                            nextLevel.add(s);
                        }
                        if (userMode) {
                        	ml.addEdge(s, base);
                        } else {
                        	ml.addEdge(s, base, r);
                        }
                    }
                }                            
                for (ICi v : nextLevel) {
                    this.advance(level, v);
                }
            }

            private void advance(int level, ICi v) {
                EGraphRelation rtype = grapgctl.getRelationType();
                if (rtype.equals(EGraphRelation.OUTGOING) 
                        ||  rtype.equals(EGraphRelation.ALL)) {
                    outGoing(level + 1, v);
                }
                if (rtype.equals(EGraphRelation.INGOING) 
                        || rtype.equals(EGraphRelation.ALL)) {
                    inGoing(level + 1, v);
                }
            }
        }
        
        );
    }
    /**
     * <p>Spawn a thread to consume a ML graph input stream, and write the
     * content into an output stream, using a specified output type.</p>
     * 
     * <p>This method returns immediately, leaving a running thread.</p>
     * 
     * @param ml The ML graph input stream to be consumed.
     * @param out The place to write the image to.
     * @param outputType Type of output expected, for example "PNG".
     * @param imagemap An initialized {@link ImageMap} object onto which an 
     * image map will be put. 
     */
    protected Thread conusumeMlInput(String id, final InputStream ml, 
            final OutputStream out, final String outputType,
            final ImageMap imagemap) {
        
        Runnable consumer = new Runnable() {
            public void run() {
                try {
                    new PrefuseRenderer().render(ml, out, outputType, imagemap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(consumer, "MLCONSUMER-"+id);
        t.start();
        return t;
    }
    
    
    
    /**
     * Create dynamic text as an image
     * @param text Text to output
     * @return An image (PNG) representing the text 
     */
    private BufferedImage createTextImage(final String text) {
        final BufferedImage image = new BufferedImage(200,20, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setBackground(Color.white);
        g.fillRect(0,0, image.getWidth(), image.getHeight());
        g.setColor(Color.red);
        g.drawRect(0,0,image.getWidth() - 1,image.getHeight() - 1);
        
        FontMetrics fm = g.getFontMetrics();
        int baseX = 0;
        
        int width = fm.stringWidth(text);
        int asc = fm.getAscent();
        int dec = fm.getDescent();
        int height = asc + dec;
        
        g.drawString(text, (image.getWidth() - width) / 2, (image.getHeight() + asc - dec ) / 2 );
        g.dispose();
        return image;
    }
    
}


/**
 * <p>A simple abstraction (on to of an existing output stream) of an MLGraph, 
 * used to add nodes and edges. </p>
 *  
 * TODO: Use a XML package to write the data instead of simple `println'
 * @author nogun
 *
 */
class MlGraph<V,E> {
    
    private final Set<V> passed = new HashSet<V>();
    
    private final PrintWriter pw;
    
    /**
     * Create a new MLGraph abstraction on top of an existing output stream.
     * @param outs The stream where to put the produced graph.
     * @throws UnsupportedEncodingException
     */
    MlGraph(OutputStream outs) throws UnsupportedEncodingException {
        
        
        
        OutputStreamWriter osw = new OutputStreamWriter(outs, "UTF8");
        this.pw = new PrintWriter(osw, true);

        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\""  
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns"
                + " http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">");
    
    }
    
   
	public boolean containsNode(ICi v) {
        return this.passed.contains(v);
    }

    private void beginGraph() {
        pw.println("<graph edgedefault=\"directed\">");
        pw.println("<!-- data schema -->");
        pw.println("<key id=\"id\" for=\"node\" attr.name=\"id\" attr.type=\"string\"/>");
        pw.println("<key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>");
        pw.println("<key id=\"distance\" for=\"node\" attr.name=\"distance\" attr.type=\"integer\"/>");
        pw.println("<key id=\"label\" for=\"edge\" attr.name=\"label\" attr.type=\"string\"/>");

    }
    
    private void endGraph() {
        pw.println("</graph>");
        pw.println("</graphml>");
        this.passed.clear();

    }
    
    /** Populate the graph using a simple worker. The method takes care of
     * <em>opening</em> the graph, as well as <em>closing</em> it.
     * 
     * @param runnable Worker responsible to populate the graph with nodes, and
     * edges.
     */
    void populate(GraphWorker runnable) {
        beginGraph();
        runnable.run(this);
        endGraph();
    }

    void addNode(V v, int distance) {
        if (passed.contains(v)) {
            return;
        }
        passed.add(v);
        
        ICi ci = (ICi) v;
        if (ci == null || ci.getId()==null) {
            System.out.println("Not good!");
        }
        
        String id = ci.getId().toString();
        String lab = ci.getDisplayName();
        ICi t = ci.getDerivedFrom();
        if (t != null) {
            //« »
            lab =  "&#171;" + t.getAlias() + "&#187;\n" + lab;
        }
        
        String node = "<node id=\""+ id +"\">\n"
        + "<data key=\"id\">" + id + "</data>"
        + "<data key=\"label\">" + lab + "</data>"
        + "<data key=\"distance\">" + distance + "</data>"
        + "</node>"; 
        pw.println(node);
    }

    public void addEdge(ICi source, ICi target, ICi rel, IAttribute a) {
    	String refLabel = "";
    	if (rel != null) {    		
    		refLabel = rel.getDisplayName() + " [" + a.getMinOccurs() + ".." + (a.getMaxOccurs() < 0 ? "*" : a.getMaxOccurs()) + "]";
    	}
    	addEdge(source, target, refLabel);
	}

    void addEdge(ICi source, ICi target, ICi rel) {
    	String refLabel = "";
    	if (rel != null) {
    		refLabel = rel.getDisplayName();
    	}
        addEdge(source, target, refLabel);
    }
  
    void addEdge(ICi source, ICi target) {
    	String refLabel = "";
        addEdge(source, target, refLabel);
    }

    void addEdge(ICi source, ICi target, String label) {
        String srcId = source.getId().toString();
        String tgtId = target.getId().toString();

        pw.println("<edge"
                + " source=\""+ srcId + "\"" 
                + " target=\""+ tgtId +"\">\n"
                + "<data key=\"label\">" + label + "</data>"
                + "</edge>");
    }



}    


/**
 * 
 * @author nogun
 *
 * @param <V> Underlying type representing vertices
 * @param <E> Underlying type representing edges
 */
interface GraphWorker<V,E> {
    public void run(MlGraph<V,E> graph);
}
    
     
    
    
    
    
        
