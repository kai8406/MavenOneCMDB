package org.onecmdb.swing.treetable;

import java.io.File;

class FileNode
{
  File file;
  Object[] children;
  private static MergeSort fileMS = new MergeSort()
  {
    public int compareElementsAt(int paramInt1, int paramInt2)
    {
      return ((String)this.toSort[paramInt1]).compareTo((String)this.toSort[paramInt2]);
    }
  };

  public FileNode(File paramFile)
  {
    this.file = paramFile;
  }

  public String toString()
  {
    return this.file.getName();
  }

  public File getFile()
  {
    return this.file;
  }

  protected Object[] getChildren()
  {
    if (this.children != null)
      return this.children;
    try
    {
      String[] arrayOfString = this.file.list();
      if (arrayOfString != null)
      {
        fileMS.sort(arrayOfString);
        this.children = new FileNode[arrayOfString.length];
        String str = this.file.getPath();
        for (int i = 0; i < arrayOfString.length; i++)
        {
          File localFile = new File(str, arrayOfString[i]);
          this.children[i] = new FileNode(localFile);
        }
      }
    }
    catch (SecurityException localSecurityException)
    {
    }
    return this.children;
  }
}

/* Location:           D:\Program Files (x86)\OneCMDB\2.1.0\repository\Content\applet\onecmdb-applet-dependencies.jar
 * Qualified Name:     org.onecmdb.swing.treetable.FileNode
 * JD-Core Version:    0.6.0
 */