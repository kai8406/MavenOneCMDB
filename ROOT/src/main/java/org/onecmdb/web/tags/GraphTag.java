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
package org.onecmdb.web.tags;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;


public class GraphTag extends TagSupport {
    private static final long serialVersionUID = 7805318893437519664L;
    private int width = 320;
    private int height = 200;
    private Color background;
    private int[] sizes;
    private Color[] cols;
    private static int ordinal;

    {
        background = Color.lightGray;
  
        cols = new Color[] { Color.red, Color.green, Color.blue, Color.yellow };
    }
    
    
    
    
    
    
    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            out.print("Chart: <img src=" + createImage() + ">");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SKIP_BODY;
    }

    @Override
    public int doAfterBody() throws JspException {
        return super.doAfterBody();
    }
    
    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
    
    
    
    
    private String createImage() throws IOException {

        
        sizes = new int[] { 40, 20, 10, 12, 35, 12, 80, 60, 30,
                20, 11, 10,9,8,7,6,5, -1 };

        
        BufferedImage buffer =
            new BufferedImage(width,
                              height,
                              BufferedImage.TYPE_INT_RGB);

        Graphics g = buffer.createGraphics();
        g.setColor(background);
        g.fillRect(0,0,width,height);
        int arc = 0;
        for(int i=0; arc < 360 && i<sizes.length; i++) {
            int size = sizes[i];
            if (size == -1) {
                size = 360 - arc;
            }
            
            g.setColor(cols[(ordinal + i) % cols.length]);
            g.fillArc(0,0,width,height,arc, size);
            arc += sizes[i];
        }

        
        String name = "chart" + (ordinal++) +  ".png";
        String fo = pageContext.getServletContext().getRealPath(name);
        FileOutputStream os = new FileOutputStream(fo);
        ImageIO.write(buffer, "png", os);
        os.close();
        return name;
    }    
    
    
}
