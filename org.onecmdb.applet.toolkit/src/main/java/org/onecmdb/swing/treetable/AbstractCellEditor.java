package org.onecmdb.swing.treetable;

import java.util.EventObject;
import javax.swing.CellEditor;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

public class AbstractCellEditor
  implements CellEditor
{
  protected EventListenerList listenerList = new EventListenerList();

  public Object getCellEditorValue()
  {
    return null;
  }

  public boolean isCellEditable(EventObject paramEventObject)
  {
    return true;
  }

  public boolean shouldSelectCell(EventObject paramEventObject)
  {
    return false;
  }

  public boolean stopCellEditing()
  {
    return true;
  }

  public void cancelCellEditing()
  {
  }

  public void addCellEditorListener(CellEditorListener paramCellEditorListener)
  {
    this.listenerList.add(CellEditorListener.class, paramCellEditorListener);
  }

  public void removeCellEditorListener(CellEditorListener paramCellEditorListener)
  {
    this.listenerList.remove(CellEditorListener.class, paramCellEditorListener);
  }

  protected void fireEditingStopped()
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
    {
      if (arrayOfObject[i] != CellEditorListener.class)
        continue;
      ((CellEditorListener)arrayOfObject[(i + 1)]).editingStopped(new ChangeEvent(this));
    }
  }

  protected void fireEditingCanceled()
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
    {
      if (arrayOfObject[i] != CellEditorListener.class)
        continue;
      ((CellEditorListener)arrayOfObject[(i + 1)]).editingCanceled(new ChangeEvent(this));
    }
  }
}

/* Location:           D:\Program Files (x86)\OneCMDB\2.1.0\repository\Content\applet\onecmdb-applet-dependencies.jar
 * Qualified Name:     org.onecmdb.swing.treetable.AbstractCellEditor
 * JD-Core Version:    0.6.0
 */