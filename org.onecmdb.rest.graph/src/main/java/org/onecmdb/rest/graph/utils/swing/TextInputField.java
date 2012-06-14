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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextInputField extends JPanel {
	 	private JTextField query  = new JTextField(15);
	    private JLabel     label = new JLabel("search >> ");
	    private Box        m_sbox    = new Box(BoxLayout.X_AXIS);

	public TextInputField(String labelText, String valueText) {
		super();
		 query.setText(valueText);
		 label.setText(labelText);
		 query.setPreferredSize(new Dimension(80, 20));
		 query.setMaximumSize(new Dimension(80, 20));
		 query.setMinimumSize(new Dimension(80, 20));
			 
		 label.setPreferredSize(new Dimension(190, 20));
		 label.setMaximumSize(new Dimension(190, 20));
		 label.setMinimumSize(new Dimension(190, 20));
		 label.setBackground(Color.WHITE);
		 label.setOpaque(true);
		 initUI();
	}
	 	
	 private void initUI() {
		 this.setPreferredSize(new Dimension(290, 20));
		 this.setMaximumSize(new Dimension(290, 20));
		 this.setMinimumSize(new Dimension(290, 20));
		 	
	        this.removeAll();
	        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	        
	        m_sbox.removeAll();
	        m_sbox.add(Box.createHorizontalStrut(3));
	        m_sbox.add(label);
	        m_sbox.add(Box.createHorizontalStrut(3));
	        m_sbox.add(query);
	        this.add(m_sbox);
	        
	    }

	public JTextField getInput() {
		return query;
	}
	    
}
