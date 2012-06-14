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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JComponent;

public class SwingImageCreator {

    /**
     * Creates a buffered image of type TYPE_INT_RGB 
     * from the supplied component. This method will 
     * use the preferred size of the component as the 
     * image's size.
     * @param component the component to draw
     * @return an image of the component
     */
    public static BufferedImage createImage(JComponent component){
       return createImage(component, BufferedImage.TYPE_INT_RGB);
    }
    
    /**
     * Creates a buffered image (of the specified type) 
     * from the supplied component. This method will use 
     * the preferred size of the component as the image's size
     * @param component the component to draw
     * @param imageType the type of buffered image to draw
     * 
     * @return an image of the component
     */
    public static BufferedImage createImage(JComponent component, 
                                            int imageType){
       Dimension componentSize = component.getPreferredSize();
       component.setSize(componentSize); //Make sure these 
                                         //are the same
       BufferedImage img = new BufferedImage(componentSize.width,
                                             componentSize.height,
                                             imageType);
       Graphics2D grap = img.createGraphics();
       grap.fillRect(0,0,img.getWidth(),img.getHeight());
       component.paint(grap);
       return img;
    }
    
    /**
     * @throws IOException 
     * 
     */
    public static void writeImageToFile(BufferedImage image, String mimeType, File file) 
        throws IOException
    {
        FileOutputStream stream = new FileOutputStream(file);
        writeImageToStream(image, mimeType, stream);
        stream.close();
    }
    
    public static void writeImageToStream(BufferedImage image, String mimeType, OutputStream stream) 
        throws IOException 
    {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByMIMEType(mimeType);
        if (iter.hasNext()) {
            ImageWriter writer = iter.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(stream);
            writer.setOutput(ios);
            writer.write(image);
        }
    }
    
 }