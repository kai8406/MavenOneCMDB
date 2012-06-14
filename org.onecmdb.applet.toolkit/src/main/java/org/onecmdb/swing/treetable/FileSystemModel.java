package org.onecmdb.swing.treetable;

import java.io.File;
import java.util.Date;

public class FileSystemModel extends AbstractTreeTableModel
  implements TreeTableModel
{
  protected static String[] cNames = { "Name", "Size", "Type", "Modified" };
  protected static Class[] cTypes = { TreeTableModel.class, Integer.class, String.class, Date.class };
  public static final Integer ZERO = new Integer(0);

  public FileSystemModel()
  {
    super(new FileNode(new File(File.separator)));
  }

  protected File getFile(Object paramObject)
  {
    FileNode localFileNode = (FileNode)paramObject;
    return localFileNode.getFile();
  }

  protected Object[] getChildren(Object paramObject)
  {
    FileNode localFileNode = (FileNode)paramObject;
    return localFileNode.getChildren();
  }

  public int getChildCount(Object paramObject)
  {
    Object[] arrayOfObject = getChildren(paramObject);
    return arrayOfObject == null ? 0 : arrayOfObject.length;
  }

  public Object getChild(Object paramObject, int paramInt)
  {
    return getChildren(paramObject)[paramInt];
  }

  public boolean isLeaf(Object paramObject)
  {
    return getFile(paramObject).isFile();
  }

  public int getColumnCount()
  {
    return cNames.length;
  }

  public String getColumnName(int paramInt)
  {
    return cNames[paramInt];
  }

  public Class getColumnClass(int paramInt)
  {
    return cTypes[paramInt];
  }

  public Object getValueAt(Object paramObject, int paramInt)
  {
    File localFile = getFile(paramObject);
    try
    {
      switch (paramInt)
      {
      case 0:
        return localFile.getName();
      case 1:
        return localFile.isFile() ? new Integer((int)localFile.length()) : ZERO;
      case 2:
        return localFile.isFile() ? "File" : "Directory";
      case 3:
        return new Date(localFile.lastModified());
      }
    }
    catch (SecurityException localSecurityException)
    {
    }
    return null;
  }
}

/* Location:           D:\Program Files (x86)\OneCMDB\2.1.0\repository\Content\applet\onecmdb-applet-dependencies.jar
 * Qualified Name:     org.onecmdb.swing.treetable.FileSystemModel
 * JD-Core Version:    0.6.0
 */