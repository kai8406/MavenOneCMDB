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

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SimpleApplet extends Applet {
    
    ArcControls controls;   // The controls for marking and filling arcs
    ArcCanvas canvas;       // The drawing area to display arcs
    
    @Override
    public void init() {
        
        setLayout(new BorderLayout());
        canvas = new ArcCanvas();
        add("Center", canvas);
        add("South", controls = new ArcControls(canvas));
        
        super.init();
    }
    
    public void destroy() {
        remove(controls);
        remove(canvas);
    }

    public void start() {
    controls.setEnabled(true);
    }

    public void stop() {
    controls.setEnabled(false);
    }

    public void processEvent(AWTEvent e) {
        if (e.getID() == Event.WINDOW_DESTROY) {
            System.exit(0);
        }
    }

    public static void main(String args[]) {
    Frame f = new Frame("ArcTest");
    SimpleApplet arcTest = new SimpleApplet();

    arcTest.init();
    arcTest.start();

    f.add("Center", arcTest);
    f.setSize(300, 300);
    f.setVisible(true);
    }

    public String getAppletInfo() {
        return "An interactive test of the Graphics.drawArc and \nGraphics.fillArc routines. Can be run \neither as a standalone application by typing 'java ArcTest' \nor as an applet in the AppletViewer.";
    }

}

class ArcCanvas extends Canvas {
    int     startAngle = 0;
    int     endAngle = 45;
    boolean filled = false;
    Font    font;

    public void paint(Graphics g) {
    Rectangle r = getBounds();
    int hlines = r.height / 10;
    int vlines = r.width / 10;

    g.setColor(Color.pink);
    for (int i = 1; i <= hlines; i++) {
        g.drawLine(0, i * 10, r.width, i * 10);
    }
    for (int i = 1; i <= vlines; i++) {
        g.drawLine(i * 10, 0, i * 10, r.height);
    }

    g.setColor(Color.red);
    if (filled) {
        g.fillArc(0, 0, r.width - 1, r.height - 1, startAngle, endAngle);
    } else {
        g.drawArc(0, 0, r.width - 1, r.height - 1, startAngle, endAngle);
    }

    g.setColor(Color.black);
    g.setFont(font);
    g.drawLine(0, r.height / 2, r.width, r.height / 2);
    g.drawLine(r.width / 2, 0, r.width / 2, r.height);
    g.drawLine(0, 0, r.width, r.height);
    g.drawLine(r.width, 0, 0, r.height);
    int sx = 10;
    int sy = r.height - 28;
    g.drawString("S = " + startAngle, sx, sy);
    g.drawString("E = " + endAngle, sx, sy + 14);
    }

    public void redraw(boolean filled, int start, int end) {
    this.filled = filled;
    this.startAngle = start;
    this.endAngle = end;
    repaint();
    }
}

class ArcControls extends Panel
                  implements ActionListener {
    TextField s;
    TextField e;
    ArcCanvas canvas;

    public ArcControls(ArcCanvas canvas) {
    Button b = null;

    this.canvas = canvas;
    add(s = new TextField("0", 4));
    add(e = new TextField("45", 4));
    b = new Button("Fill");
    b.addActionListener(this);
    add(b);
    b = new Button("Draw");
    b.addActionListener(this);
    add(b);
    }

    public void actionPerformed(ActionEvent ev) {
    String label = ev.getActionCommand();

    canvas.redraw(label.equals("Fill"),
                  Integer.parseInt(s.getText().trim()),
                  Integer.parseInt(e.getText().trim()));
    }
}

