package org.onecmdb.swing.treetable;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

public class TreeTableModelAdapter extends AbstractTableModel
{
  JTree tree;
  TreeTableModel treeTableModel;

  public TreeTableModelAdapter(TreeTableModel paramTreeTableModel, JTree paramJTree)
  {
    this.tree = paramJTree;
    this.treeTableModel = paramTreeTableModel;
    paramJTree.addTreeExpansionListener(new TreeExpansionListener()
    {
      public void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent)
      {
        TreeTableModelAdapter.this.fireTableDataChanged();
      }

      public void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent)
      {
        TreeTableModelAdapter.this.fireTableDataChanged();
      }
    });
  }

  public int getColumnCount()
  {
    return this.treeTableModel.getColumnCount();
  }

  public String getColumnName(int paramInt)
  {
    return this.treeTableModel.getColumnName(paramInt);
  }

  public Class getColumnClass(int paramInt)
  {
    return this.treeTableModel.getColumnClass(paramInt);
  }

  public int getRowCount()
  {
    return this.tree.getRowCount();
  }

  protected Object nodeForRow(int paramInt)
  {
    TreePath localTreePath = this.tree.getPathForRow(paramInt);
    return localTreePath.getLastPathComponent();
  }

  public Object getValueAt(int paramInt1, int paramInt2)
  {
    return this.treeTableModel.getValueAt(nodeForRow(paramInt1), paramInt2);
  }

  public boolean isCellEditable(int paramInt1, int paramInt2)
  {
    return this.treeTableModel.isCellEditable(nodeForRow(paramInt1), paramInt2);
  }

  public void setValueAt(Object paramObject, int paramInt1, int paramInt2)
  {
    this.treeTableModel.setValueAt(paramObject, nodeForRow(paramInt1), paramInt2);
  }
}

/* Location:           D:\Program Files (x86)\OneCMDB\2.1.0\repository\Content\applet\onecmdb-applet-dependencies.jar
 * Qualified Name:     org.onecmdb.swing.treetable.TreeTableModelAdapter
 * JD-Core Version:    0.6.0
 */