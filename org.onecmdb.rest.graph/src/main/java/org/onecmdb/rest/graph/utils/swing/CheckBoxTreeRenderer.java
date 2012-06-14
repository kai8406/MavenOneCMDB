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
package org.onecmdb.rest.graph.utils.swing;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.onecmdb.rest.graph.events.Event;
import org.onecmdb.rest.graph.events.IEventListener;

import prefuse.data.Edge;
import prefuse.data.tuple.TableNode;
import prefuse.render.ImageFactory;

public class CheckBoxTreeRenderer extends DefaultTreeCellRenderer {

	ImageFactory iFactory = new ImageFactory();
	JCheckBox check = new JCheckBox("");
	IEventListener listener = null;
	
	
	public CheckBoxTreeRenderer() {
		//iFactory.setAsynchronous(false);
	}
	
	public void setEventListsner(IEventListener listener) {
		this.listener = listener;
	}
	
	private JPanel getPanel(JLabel label) {
		JPanel panel = new JPanel();
		

		panel.setBackground(UIManager.getColor("Tree.textBackground"));
		check.setBackground(UIManager.getColor("Tree.textBackground"));
		setOpaque(true);
		panel.setOpaque(true);
		check.setOpaque(true);
		
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0,0));
		
		panel.add(check);
		panel.add(label);
		panel.add(Box.createHorizontalStrut(5));
		
		panel.invalidate();
		
		
		return(panel);
		
	}
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		DefaultTreeCellRenderer label = new DefaultTreeCellRenderer();
		// Update icons.
		String refType = "";
		if (value instanceof TableNode) {
			final TableNode n = (TableNode)value;
			Edge edge = n.getParentEdge();
			if (edge != null) {
				
				if (edge.canGet("type", String.class)) {
					refType = edge.getString("type");
					if (refType == null) {
						refType = "";
					}
					boolean inBound = edge.getBoolean("inBound");
					if (refType.length() != 0) {
						if (inBound) {
							refType = "<-- [" + refType + "] ";
						} else {
							refType = "--> [" + refType + "] ";
						}
					}
				}
			}
			if (n.canGet("image", String.class)) {
				String imageURL = n.getString("image");
				if (imageURL != null) {
					Image image = iFactory.getImage(imageURL);
					//Image image = iFactory.getImage("http://localhost:8080/icons/generate?iconid=" + iconId);

					ImageIcon icon = new ImageIcon(image);
					label.setOpenIcon(icon);
					label.setClosedIcon(icon);
					label.setLeafIcon(icon);
				}
			}
		
			if (n.canGet("checked", Boolean.class)) {
				check.setSelected(n.getBoolean("checked"));
				check.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						n.set("checked", check.isSelected());
						if (listener != null) {
							listener.onEvent(new Event(0, n));
						}
						System.out.println("Checkbox checked....");
					}
					
				});
			}
		}
		String valueStr = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
		valueStr = refType + valueStr;
		label.getTreeCellRendererComponent(tree, valueStr, sel, expanded, leaf,
				row, hasFocus);
		
		
		
		return(getPanel(label));
		
	}
	
	
	
	
	

}
