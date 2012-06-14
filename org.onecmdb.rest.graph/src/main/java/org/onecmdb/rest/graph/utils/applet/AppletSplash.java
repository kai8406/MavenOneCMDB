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
package org.onecmdb.rest.graph.utils.applet;
import java.awt.*;
import java.net.*;
import java.applet.*;

import javax.swing.JApplet;
import javax.swing.JLabel;

public class AppletSplash extends JApplet implements AppletStub, Runnable {

	//
	// Private member variables
	//
	private String m_strTargetAppletClassName;
	private Applet m_appletTarget;
	private URL m_urlSplashImage;
	private Image m_imgSplash;
	private boolean m_fStarted;

	//
	// In init, a background thread responsible for downloading
	// the target Applet class is created after the splash image is 
	// downloaded.
	public void init() {
		// Gets splash image URL and target class. This code assumes 
		// URLs are
		// relative URLs.
		try {
			m_urlSplashImage = new URL(getDocumentBase(),
					getParameter("splash-SplashImage"));
			m_strTargetAppletClassName =
				getParameter("splash-TargetApplet");
		} catch(MalformedURLException e) {
			System.out.println("FlashSplash loading problem: " + e);
			m_urlSplashImage = null;
			getContentPane().add(new JLabel("FlashSplash loading problem: " + e));
			getContentPane().doLayout();
			getContentPane().repaint();
			return;
		}

		// Download image.
		try {
			MediaTracker mt = new MediaTracker(this);
			m_imgSplash = getImage(m_urlSplashImage);
			mt.addImage(m_imgSplash, 0);
			mt.waitForID(0);
			repaint();
		} catch(Exception e) {
			System.out.println("Splash image loading problem: " + e);
			m_urlSplashImage = null;
		}

		// Start background thread to download target Applet class.
		Thread t = new Thread(this);
		t.start();
	}

	//
	// start() method passes through to target Applet, if created.
	//
	public void start() {
		if(null != m_appletTarget)
			m_appletTarget.start();
		m_fStarted = true;
	}

	//
	// stop() method passes through to target Applet, if created.
	//
	public void stop() {
		if(null != m_appletTarget)
			m_appletTarget.stop();
		m_fStarted = false;
	}

	//
	// destroy() passes through to target Applet, if created.
	//
	public void destroy() {
		if(null != m_appletTarget)
			m_appletTarget.destroy();
	}

	//
	// In paint, only draw the splash image.
	//
	public void paint(Graphics g) {
		if(null != m_imgSplash)
			g.drawImage(m_imgSplash, 0, 0, this);
	}

	//
	// Runnable interface methods: run.
	// run() is responsible for loading the target applet class
	// and creating an instance.
	//
	public void run() {
		try {
			System.out.println("Target applet: " + 
					m_strTargetAppletClassName);
			Class c = Class.forName(m_strTargetAppletClassName);
			Applet a = (Applet)c.newInstance();
			a.setStub((AppletStub)this);
			setLayout(new BorderLayout());
			add("Center", a);
			validate();

			a.init();
			m_appletTarget = a;
			if(m_fStarted)
				m_appletTarget.start();
		} catch (Exception e) {
			System.out.println("Trouble loading target Applet class: "
					+ e);
			m_appletTarget = null;
		}
	}

	//
	// Methods of the AppletStub interface. These implementations
	// make FlashSplashes simple proxy AppletStubs. These methods
	// are already implemented sufficiently by inherited implementations
	// from the Applet class: isActive, getDocumentBase, getCodeBase,
	// getParameter, getAppletContext. To be implemented: appletResize
	//
	public void appletResize(int width, int height) {
		resize(width, height);
	}
}
