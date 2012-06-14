package org.onecmdb.swing.treetable;

public abstract class MergeSort
{
  protected Object[] toSort;
  protected Object[] swapSpace;

  public void sort(Object[] paramArrayOfObject)
  {
    if ((paramArrayOfObject != null) && (paramArrayOfObject.length > 1))
    {
      int i = paramArrayOfObject.length;
      this.swapSpace = new Object[i];
      this.toSort = paramArrayOfObject;
      mergeSort(0, i - 1);
      this.swapSpace = null;
      this.toSort = null;
    }
  }

  public abstract int compareElementsAt(int paramInt1, int paramInt2);

  protected void mergeSort(int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2)
    {
      int i = (paramInt1 + paramInt2) / 2;
      mergeSort(paramInt1, i);
      mergeSort(i + 1, paramInt2);
      merge(paramInt1, i, paramInt2);
    }
  }

  protected void merge(int paramInt1, int paramInt2, int paramInt3)
  {
    int k;
	int i = k = paramInt1;
    int j = paramInt2 + 1;
    while ((i <= paramInt2) && (j <= paramInt3))
    {
      if (compareElementsAt(j, i) < 0)
      {
        this.swapSpace[(k++)] = this.toSort[(j++)];
        continue;
      }
      this.swapSpace[(k++)] = this.toSort[(i++)];
    }
    if (i <= paramInt2)
      while (i <= paramInt2)
        this.swapSpace[(k++)] = this.toSort[(i++)];
    while (j <= paramInt3)
      this.swapSpace[(k++)] = this.toSort[(j++)];
    for (int k2 = paramInt1; k2 <= paramInt3; k2++)
      this.toSort[k2] = this.swapSpace[k2];
  }
}

/* Location:           D:\Program Files (x86)\OneCMDB\2.1.0\repository\Content\applet\onecmdb-applet-dependencies.jar
 * Qualified Name:     org.onecmdb.swing.treetable.MergeSort
 * JD-Core Version:    0.6.0
 */