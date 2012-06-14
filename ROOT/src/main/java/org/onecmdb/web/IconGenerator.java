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

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.onecmdb.core.ICi;
import org.onecmdb.core.IOneCmdbContext;
import org.onecmdb.core.ISession;
import org.onecmdb.core.internal.model.ItemId;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class IconGenerator extends MultiActionController {

    private static String[] exts = { ".gif", ".png", ".jpg", ".jpeg" };
    
    private SiteController siteController;

    
    /** location holding an image repository */
    private String imageDirectory = "/WEB-INF/jsp/images";

    // {{{ bean support 
    
    /**
     * WARNING: Used to satisfy spring only
     */
    
    public void setSiteController(SiteController site) {
        this.siteController = site;
    }
    public SiteController getSiteController() {
        return this.siteController;
    }
    
    
    public void init() {
        if (getSiteController() == null) {
            throw new IllegalStateException("No SiteController set!");
        }
        
        if (imageDirectory == null) {
            throw new IllegalStateException("No image directory specified!");
        }
        if (!imageDirectory.endsWith("/")) {
            imageDirectory += "/";
        }
        
    }
    // }}}

    
    
    public ModelAndView generateHandler(HttpServletRequest request,
            HttpServletResponse respone, IconCommand iconCommand) throws MalformedURLException {
    	/*
        ISession session = getSiteController().getSiteCommand(request).getSession();

        
        ServletRequestDataBinder binder = new ServletRequestDataBinder(iconCommand);
        binder.registerCustomEditor(ItemId.class, new ItemIdEditor());

        
        binder.registerCustomEditor(ICi.class, new ItemEditor(session));
        binder.registerCustomEditor(IconSize.class, new IconSizeEditor());
        binder.bind(request);
        */
        
        String base = iconCommand.getIconid();
        if (base == null) {
            base = iconCommand.getIconFile();
        }
        
        if (base == null || "".equals(base)) {
            base = "unknown";
        }

        String iconSpec = null;
        if (!ResourceUtils.isUrl(base)) {
            ServletContext ctx = getServletContext();

            int intSize = 16;
            if (iconCommand.getSize() == null ||
                    iconCommand.getSize() == IconSize.SMALL ) {
                intSize  = 16;
            } else if (iconCommand.getSize() == IconSize.MEDIUM) {
                intSize = 32;
            } else if (iconCommand.getSize() == IconSize.LARGE) {
                intSize = 48;
            }

            URL res = null;
            for (int i = 0; res == null && i < exts.length; i++) {
                 iconSpec = this.imageDirectory + base + intSize + exts[i];
                res = ctx.getResource(iconSpec);
            }
            if (res == null) {
                return null;
            }

            
        } else {
            
        }
        
        Map<String, String> data = new HashMap<String, String>();
        data.put("file", iconSpec);
        return new ModelAndView("image", "image", data);

        
        
    }


    private Map<String, List<Image>> getImageMap() throws IOException {
        
        ServletContextResource imagesRes 
        = new ServletContextResource(getServletContext(), this.imageDirectory);
        
        File imagesFile = imagesRes.getFile();
        
        final Map<String, List<Image>> images = new TreeMap<String, List<Image>>();
        File[] imageFiles = imagesFile.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {

                FileSystemResource file = new FileSystemResource(new File(dir, name));
                
                for (String ext : exts) {
                    if (name.toLowerCase().endsWith(ext)) { 
                        try {
                            BufferedImage img = ImageIO.read(file.getFile());
                            
                            String key = name.substring(0, name.length() - ext.length());
                            key = name.substring(0, key.length() - 2);
                            
                            List<Image> imageList = images.get(key);
                            if (imageList == null) {
                                imageList = new ArrayList<Image>(1);
                                images.put(key, imageList);
                            }
                            imageList.add(img);
                            return true;
                        } catch (IOException e) { 
                            return false;
                        }
                    }
                }
                return false;
                
            }});


        return images;
        
    }
    
    
    public ModelAndView listHandler(HttpServletRequest request,
            HttpServletResponse respone, IconCommand iconCommand) throws IOException {

    
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, List<Image>> images = getImageMap();
        data.put("images", images);
        return new ModelAndView("imageList", "data", data);
    }

    
    
    public ModelAndView optionsHandler(HttpServletRequest request,
            HttpServletResponse respone, IconOptionsCommand optionsCommand) throws IOException {
    
        ISession session = getSiteController().getSiteCommand(request).getSession();

        
        ServletRequestDataBinder binder = new ServletRequestDataBinder(optionsCommand);
        binder.registerCustomEditor(ItemId.class, new ItemIdEditor());
        
        binder.registerCustomEditor(ICi.class, new ItemEditor(session));
        binder.registerCustomEditor(IconSize.class, new IconSizeEditor());
        binder.bind(request);


        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, List<Image>> images = getImageMap();
        data.put("images", images);
        data.put("selected", optionsCommand.getSelected());
        
        return new ModelAndView("imageOptions", "data", data);
        
    }
     
    @Override
    protected void initBinder(ServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);

        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

    }
    
    
    public ModelAndView addHandler(HttpServletRequest request,
            HttpServletResponse respone, IconOptionsCommand optionsCommand) throws IOException {

        HashMap<String, Object> data = new HashMap<String, Object>();

        
        
        ServletContextResource depot
        = new ServletContextResource(getServletContext(), this.imageDirectory);

        
        
        
        String imageid = optionsCommand.getIconid();
        if (imageid != null) {
        
            
            final ByteArrayInputStream in = new ByteArrayInputStream(optionsCommand.getIconData());
            final BufferedImage image = ImageIO.read(in);
            
            for (double f = 16.0; f <= 48.0; f += 16)
            {
                
                Image scaled = image.getScaledInstance((int) f, (int) f, java.awt.Image.SCALE_AREA_AVERAGING);
                
                
                BufferedImage scaledImage = toBufferedImage(scaled);
                
                final File file = new File(depot.getFile(), imageid  + ((int) f) + ".png");
                ImageIO.write(scaledImage, "png", file);
                
            }

            data.put("successful", true);
        
        }
        
        
        
        return new ModelAndView("imageAdd", "imageAdd", data);

        
    }
    
    
//  This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
    
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
    
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);
    
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
    
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
    
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
    
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
    
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
    
        return bimage;
    }
        
//  This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }
    
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
    
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
}