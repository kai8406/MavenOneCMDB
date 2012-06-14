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
package org.onecmdb.rest.graph.model;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.onecmdb.core.utils.bean.CiBean;
import org.onecmdb.swing.treetable.AbstractCellEditor;
import org.onecmdb.swing.treetable.AbstractTreeTableModel;
import org.onecmdb.swing.treetable.TreeTableModel;


public class CIAttributeModel extends AbstractTreeTableModel implements TreeTableModel {
	public static final int DISPLAY_NAME = 0;
	public static final int ALIAS = 1;
	public static final int TYPE = 2;
	public static final int REF_TYPE = 3;
	public static final int VALUE = 4;
	public static final int MAX_OCCURS = 5;
	public static final int MIN_OCCURS = 6;
	public static final int ID = 7;
	public static final int DISPAY_EXPR = 8;

	// Names of the columns.
    static protected String[]  advancedNames = {"Name", "Alias", "Type", "Ref Type", "Value"};
    static protected String[]  simpleNames = {"Name",  "Value"};
    static protected int[] simpleOrder = {DISPLAY_NAME, VALUE};
    static protected int[] advancedOrder = {DISPLAY_NAME, ALIAS, TYPE, REF_TYPE, VALUE};

    
    // Types of the columns.
    static protected Class[]  advancedTypes = {TreeTableModel.class, CiBean.class, CiBean.class, CiBean.class, CiBean.class};
    static protected Class[]  simpleTypes = {TreeTableModel.class, CiBean.class};

    protected Class[]  cTypes = simpleTypes;
    protected String[] cNames = simpleNames;
    protected int[] cOrder = simpleOrder;
 
 
    public void setAdvanced(boolean value) {
    	if (value) {
    		cTypes = advancedTypes;
    		cNames = advancedNames;
    		cOrder = advancedOrder;
    	} else {
     		cTypes = simpleTypes;
    		cNames = simpleNames;
    		cOrder = simpleOrder;
    	}
    }
    
    public boolean isCellEditable(Object node, int column) { 
        //return getColumnClass(column) == TreeTableModel.class; 
    	return(true);
    }
  
	
	public CIAttributeModel() {
		super(null);
	}
	
	public void setRoot(Object root) {
		this.root = root;
		fireTreeStructureChanged(this, new Object[] {this.root}, null, null);
		//fireTreeStructureChanged(this, path, childIndices, children)
	}
	
	public int getColumnCount() {
		return(cNames.length);
	}

	public String getColumnName(int column) {
		return(cNames[column]);
	}
	
	public Class getColumnClass(int column) {
		//return(CiBean.class);
		return cTypes[column];
	}
	
	public Object getValueAt(Object node, int column) {
		if (node instanceof ITreeTableModel) {
			int col = convertColumn(column);
			return(((ITreeTableModel)(node)).getValue(col));
		}
		return("");
	}

	public Object getChild(Object parent, int index) {
		if (parent instanceof ITreeTableModel) {
			return(((ITreeTableModel)parent).getChild(index));
		}
		return(null);
	}

	public int getChildCount(Object parent) {
		if (parent instanceof ITreeTableModel) {
			return(((ITreeTableModel)parent).getChildCount());
		}
		return(0);
	}

	
	public int convertColumn(int column) {
		return(cOrder[column]);
	}
	
	
	public void setValueAt(Object aValue, Object node, int column) {
		System.out.println("setValueAt(" + aValue + "," + node + "," + column);
	}

	
	
	public TableCellEditor getTableCellEditor() {
		return(null);
		//return(new TestTreeTableCellEditor());
	}
	
	public TableColumnModel getColumnModel() {
		// TODO Auto-generated method stub
		return null;
	}	
	
	 public class TestTreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
			
			public Component getTableCellEditorComponent(JTable table,
					Object value, boolean isSelected, int row, int column) {
				// TODO Auto-generated method stub
				final JTextField tField = new JTextField();
				tField.addKeyListener(new KeyListener() {

					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							fireEditingCanceled();
						}
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							System.out.println("Update value..." + tField.getText());
							fireEditingStopped();
						}
						
						
					}

					public void keyReleased(KeyEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void keyTyped(KeyEvent e) {
						// TODO Auto-generated method stub
						
					}
					
				});
				if (value != null) {
					tField.setText(value.toString());
				}
				return(tField);
			}

			public boolean isCellEditable(EventObject anEvent) {
				return(false);
				/*
				if (anEvent instanceof MouseEvent) { 
					return ((MouseEvent)anEvent).getClickCount() >= 2;
				}
				return false;
				*/
			}

			
			
			
			
			

	 }
}
