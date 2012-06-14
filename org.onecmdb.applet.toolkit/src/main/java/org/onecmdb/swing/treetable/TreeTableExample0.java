package org.onecmdb.swing.treetable;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class TreeTableExample0
{
  public static void main(String[] paramArrayOfString)
  {
    new TreeTableExample0();
  }

  public TreeTableExample0()
  {
    JFrame localJFrame = new JFrame("TreeTable");
    JTreeTable localJTreeTable = new JTreeTable(new FileSystemModel());
    localJFrame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramWindowEvent)
      {
        System.exit(0);
      }
    });
    localJFrame.getContentPane().add(new JScrollPane(localJTreeTable));
    localJFrame.pack();
    localJFrame.setVisible(true);
  }
}

/* Location:           D:\Program Files (x86)\OneCMDB\2.1.0\repository\Content\applet\onecmdb-applet-dependencies.jar
 * Qualified Name:     org.onecmdb.swing.treetable.TreeTableExample0
 * JD-Core Version:    0.6.0
 */