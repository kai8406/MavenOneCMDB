package org.onecmdb.rest.graph.utils.applet;
/*
Important: in this case, namespaces are bad.
*/

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;


public class AppletLaunch extends JApplet implements AppletStub, Runnable {
   protected String loadAppletName = null;
   protected Color txtColor;
   protected Color txtBgColor;
   protected Color bgColor;
   protected String imgSplashName = null;

   private String minVersion = null;

   private Applet loadApplet = null;
   private volatile Image imgSplash = null;
   private boolean isStarted;
   private String txtMessage = null;
   private String imgErrorName;
   private Image imgError;
   private MediaTracker mt;
   private JLabel statusLabel = new JLabel();
   private JLabel splashLabel = new JLabel();
   private JPanel centerPanel = new JPanel();
   private JPanel statusPanel = new JPanel();
   
   JTextArea errorArea = new JTextArea();

   public void init() {
	  
	  getContentPane().setLayout(new BorderLayout());
	  statusPanel.setLayout(new GridLayout(1,1));
	  centerPanel.setLayout(new BorderLayout());
	  splashLabel.setHorizontalAlignment(SwingConstants.CENTER);
	  statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
	  centerPanel.add(splashLabel, BorderLayout.NORTH);
	  centerPanel.add(new JScrollPane(errorArea), BorderLayout.CENTER);
	  errorArea.setEditable(false);
	  statusPanel.add(statusLabel);
	  
	  getContentPane().add(statusPanel, BorderLayout.SOUTH);
	  getContentPane().add(centerPanel, BorderLayout.CENTER);
		   
	  AppletLogger.setAppletLauncher(this); 
	  
	  mt = new MediaTracker(this); 
      loadParms();
      if (bgColor!=null) setBackground(bgColor);

      if(loadAppletName==null) {
         setMessage("ERROR: appletlaunch.callcode required.");
         return;
      }

      setMessage("Loading...");

      imgSplash = loadImage(imgSplashName);
      if (imgSplash != null) {
    	  splashLabel.setIcon(new ImageIcon(imgSplash));
      }
        
      boolean okVer = checkVersion();

      paint(getGraphics());

      if (okVer) {
         setMessage("Launch " + loadAppletName);
         Thread t = new Thread(this);
         t.setName("Applet-Launcher-" + t.hashCode());
         t.setDaemon(true);
         t.start();
      }
   }

   private boolean checkVersion() {
      if (minVersion==null) return true;

      if(!isVersionEnough()) {
         setMessage("Java version need: " + minVersion
            + ",  Current Java version: " + System.getProperty("java.version")
         );
         return false;
      } else {
         return true;
      }
   }

   private Image loadImage(String image) {
	   Image img = null;
	   if (image != null) {
    	  URL url = getDocumentBase();
    	  String path = url.toExternalForm();
    	  
    	  // Remove path.
    	  int index = path.lastIndexOf("/");
    	  String baseURL = path.substring(0, index+1);
    	  
    	 //setMessage("Loading image " + baseURL +  image);
         try {
           
            img = getImage( new URL(baseURL) , image );
            mt.addImage(img, image.hashCode());
            mt.waitForID(image.hashCode());
            if(mt.isErrorAny()) {
            	Object errors[] = mt.getErrorsAny();
            	System.out.println("Image Load Error: " +  baseURL + image);
            	img = null;
            }
         } catch(Exception e) {
            setMessage("FAIL Splash image loading problem.");
            
            System.out.println(e.toString());
         }
      }
	  return(img); 
   }

   private void loadParms() {
      minVersion = getParameter("appletlaunch.version");
      loadAppletName = getParameter("appletlaunch.callcode");
      imgSplashName = getParameter("appletlaunch.splash");
      imgErrorName = getParameter("appletlaunch.splasherror");
      String bgColorName = getParameter("appletlaunch.color.background");
      String colorTextName = getParameter("appletlaunch.color.text");
      String bgColorTextName = getParameter("appletlaunch.color.textbackground");
      
      System.out.println("Parameters:");
      
      System.out.println("MinVersion:'" + minVersion +"'");
      System.out.println("AppletCode:'" + loadAppletName +"'");
      System.out.println("Splash Img:'" + imgSplashName +"'");
      System.out.println("Error Img:'" + imgErrorName +"'");
      System.out.println("Bg Color:'" + bgColorName +"'");
      System.out.println("Text Color:'" + colorTextName +"'");
      System.out.println("Bg Text Color:'" + bgColorTextName +"'");
       
      
      
      bgColor = parseColor(bgColorName, Color.white );
      txtColor = parseColor(colorTextName , Color.black );
      txtBgColor = parseColor(bgColorTextName , Color.lightGray );
      
      splashLabel.setBackground(bgColor);
      splashLabel.setForeground(bgColor);
      errorArea.setBackground(bgColor);
      centerPanel.setBackground(bgColor);
      getContentPane().setBackground(bgColor);
      statusPanel.setBackground(Color.LIGHT_GRAY);
      statusLabel.setForeground(txtColor);
   }



   public void start() {
      if(loadApplet != null) {
    	  loadApplet.start();
      }
      isStarted = true;
   }


   public void stop() {
      if(loadApplet != null) {
    	  loadApplet.stop();
      }
      isStarted = false;
   }

   public void destroy() {
      if(loadApplet != null) {
    	  loadApplet.destroy();
      }
      loadApplet = null;
   }

   /*
   public void paint(Graphics g) {
      Dimension d = size();
      FontMetrics fm = g.getFontMetrics();
      int space = fm.getMaxAscent() + fm.getDescent();
      
      if(imgSplash != null) {
         Dimension d2 = new Dimension( imgSplash.getWidth(this), imgSplash.getHeight(this)-space );
         int x= d.width - d2.width;
         if (x>0) {
            x = (int)x/2;
         } else {
            x=0;
         }

         int y = d.height- d2.height;
         if (y>0) {
            y = (int)y/2;
         } else {
            y=0;
         }
        
         g.drawImage(imgSplash, x, y, this);
      }
        
      
      if (txtBgColor!=null) g.setColor(txtBgColor);
      g.fillRect(0, d.height-space, d.width, space);

      if (txtColor!=null) g.setColor(txtColor);
      g.drawString( getMessage(), 5, d.height-fm.getDescent());
   }
	*/
   public void run() {
	  
      try {
         Class cls = Class.forName(loadAppletName);
         JApplet app = (JApplet)cls.newInstance();
         app.setStub((AppletStub)this);
         app.init();
         
        
         getContentPane().add("Center", app);
         loadApplet = app;
         validate();
        
         if(isStarted) {
        	 loadApplet.start();
         }
         validate();
      } catch (Throwable e) {
     	 loadApplet = null;
         AppletLogger.showError("Exception:", e);
         
         e.printStackTrace();
         validate();
      }
      System.out.println("Thread end");
   }
  
   public void showError(String error) {
	     imgSplash = loadImage(imgErrorName);
	     if (imgSplash != null) {
	    	 splashLabel.setIcon(new ImageIcon(imgSplash));
	     }
         errorArea.setForeground(Color.red);
         errorArea.append("\t======= FAILED =======\n");
         errorArea.append("\t" + error);
         
         //centerPanel.validate();
   }

     
   public void appletResize(int width, int height) {
      resize(width, height);
   }


   public void setMessage(String message) {
      this.statusLabel.setText(message);
      errorArea.append(message + "\n");
      System.out.println(message);
   }



   public String getMessage() { return this.txtMessage; }



   public boolean isVersionEnough() {
      String verStr = System.getProperty("java.version");
      return ( !(compareVersion(verStr, minVersion) < 0) );
   }


   public static double getVerNum(String version) {
      double retval = 0;
      double mask = 1;
      double maskVal = 0.01;
      int dotPos = version.indexOf(".");
      while(dotPos>-1) {
         String dotPart = version.substring(0,dotPos);
         version = version.substring(dotPos+1);
         try {
            retval += (mask * (Double.valueOf(dotPart)).doubleValue() );
         } catch(java.lang.NumberFormatException e) {}

         dotPos = version.indexOf(".");
         mask = mask * maskVal;
      }
      try {
         retval += (mask * (Double.valueOf(version)).doubleValue() );
      } catch(java.lang.NumberFormatException e) {}
      return retval;
   }

   public static int compareVersion(String verStr1, String verStr2) {
      double verNum1 = getVerNum(verStr1);
      double verNum2 = getVerNum(verStr2);
      if (verNum1<verNum2) {
         return -1;
      } if (verNum1>verNum2) {
         return 1;
      } else {
         return 0;
      }
   }


   private static Color parseColor(String colorValue, Color def) {
      if (colorValue != null) {
         return new Color(Integer.parseInt(colorValue, 16));
      } else {
         return def;
      }
   }

   private static Color parseColor(String colorValue) {
	  
      if (colorValue != null) {
         return new Color(Integer.parseInt(colorValue, 16));
      } else {
         return null;
      }
   }


}
