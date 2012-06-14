package org.onecmdb.swing.treetable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

public abstract class AbstractTreeTableModel
  implements TreeTableModel
{
  protected Object root;
  protected EventListenerList listenerList = new EventListenerList();

  public AbstractTreeTableModel(Object paramObject)
  {
    this.root = paramObject;
  }

  public Object getRoot()
  {
    return this.root;
  }

  public boolean isLeaf(Object paramObject)
  {
    return getChildCount(paramObject) == 0;
  }

  public void valueForPathChanged(TreePath paramTreePath, Object paramObject)
  {
  }

  public int getIndexOfChild(Object paramObject1, Object paramObject2)
  {
    for (int i = 0; i < getChildCount(paramObject1); i++)
      if (getChild(paramObject1, i).equals(paramObject2))
        return i;
    return -1;
  }

  public void addTreeModelListener(TreeModelListener paramTreeModelListener)
  {
    this.listenerList.add(TreeModelListener.class, paramTreeModelListener);
  }

  public void removeTreeModelListener(TreeModelListener paramTreeModelListener)
  {
    this.listenerList.remove(TreeModelListener.class, paramTreeModelListener);
  }

  protected void fireTreeNodesChanged(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
    {
      if (arrayOfObject[i] != TreeModelListener.class)
        continue;
      if (localTreeModelEvent == null)
        localTreeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2);
      ((TreeModelListener)arrayOfObject[(i + 1)]).treeNodesChanged(localTreeModelEvent);
    }
  }

  protected void fireTreeNodesInserted(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
    {
      if (arrayOfObject[i] != TreeModelListener.class)
        continue;
      if (localTreeModelEvent == null)
        localTreeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2);
      ((TreeModelListener)arrayOfObject[(i + 1)]).treeNodesInserted(localTreeModelEvent);
    }
  }

  protected void fireTreeNodesRemoved(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
    {
      if (arrayOfObject[i] != TreeModelListener.class)
        continue;
      if (localTreeModelEvent == null)
        localTreeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2);
      ((TreeModelListener)arrayOfObject[(i + 1)]).treeNodesRemoved(localTreeModelEvent);
    }
  }

  protected void fireTreeStructureChanged(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
    {
      if (arrayOfObject[i] != TreeModelListener.class)
        continue;
      if (localTreeModelEvent == null)
        localTreeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2);
      ((TreeModelListener)arrayOfObject[(i + 1)]).treeStructureChanged(localTreeModelEvent);
    }
  }

  public Class getColumnClass(int paramInt)
  {
    return Object.class;
  }

  public boolean isCellEditable(Object paramObject, int paramInt)
  {
    return getColumnClass(paramInt) == TreeTableModel.class;
  }

  public void setValueAt(Object paramObject1, Object paramObject2, int paramInt)
  {
  }
}

/* Location:           D:\Program Files (x86)\OneCMDB\2.1.0\repository\Content\applet\onecmdb-applet-dependencies.jar
 * Qualified Name:     org.onecmdb.swing.treetable.AbstractTreeTableModel
 * JD-Core Version:    0.6.0
 */