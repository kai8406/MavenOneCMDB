package org.onecmdb.swing.treetable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;

public class JTreeTable extends JTable
{
  protected TreeTableCellRenderer tree;

  public JTreeTable(TreeTableModel paramTreeTableModel)
  {
    this.tree = new TreeTableCellRenderer(paramTreeTableModel);
    super.setModel(new TreeTableModelAdapter(paramTreeTableModel, this.tree));
    this.tree.setSelectionModel(new DefaultTreeSelectionModel()
    {
    });
    this.tree.setRowHeight(getRowHeight());
    setDefaultRenderer(TreeTableModel.class, this.tree);
    setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());
    setShowGrid(false);
    setIntercellSpacing(new Dimension(0, 0));
  }

  public int getEditingRow()
  {
    return getColumnClass(this.editingColumn) == TreeTableModel.class ? -1 : this.editingRow;
  }

  public class TreeTableCellEditor extends AbstractCellEditor
    implements TableCellEditor
  {
    public TreeTableCellEditor()
    {
    }

    public Component getTableCellEditorComponent(JTable paramJTable, Object paramObject, boolean paramBoolean, int paramInt1, int paramInt2)
    {
      return JTreeTable.this.tree;
    }
  }

  public class TreeTableCellRenderer extends JTree
    implements TableCellRenderer
  {
    protected int visibleRow;

    public TreeTableCellRenderer(TreeModel arg2)
    {
      super();
    }

    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.setBounds(paramInt1, 0, paramInt3, JTreeTable.this.getHeight());
    }

    public void paint(Graphics paramGraphics)
    {
      paramGraphics.translate(0, -this.visibleRow * getRowHeight());
      super.paint(paramGraphics);
    }

    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      if (paramBoolean1)
        setBackground(paramJTable.getSelectionBackground());
      else
        setBackground(paramJTable.getBackground());
      this.visibleRow = paramInt1;
      return this;
    }
  }
}

/* Location:           D:\Program Files (x86)\OneCMDB\2.1.0\repository\Content\applet\onecmdb-applet-dependencies.jar
 * Qualified Name:     org.onecmdb.swing.treetable.JTreeTable
 * JD-Core Version:    0.6.0
 */