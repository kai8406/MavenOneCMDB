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
package org.onecmdb.rest.graph.utils.transform;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.onecmdb.rest.graph.prefuse.view.GraphView;
import org.onecmdb.rest.graph.prefuse.view.RadialGraphView;
import org.onecmdb.rest.graph.prefuse.view.TreeView;
import org.onecmdb.utils.transform.ICMDBTransform;

import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.visual.VisualItem;

public class TemplateTree implements ICMDBTransform {

	public static void main(String argv[]) {
		try {
			new TemplateTree().process(new OutputStreamWriter(new FileOutputStream("d:/tmp/tree.jpg")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void process(OutputStream out) throws Exception {
		Tree tree = new Tree();
		tree.addColumn("name", String.class);
		tree.addColumn(VisualItem.EXPANDED, Boolean.class);
		Node root = tree.addNode();
		root.set("name", "Root");
		genTree(tree, root, 5, 2);	
		
		
		CommonTreeView view = new CommonTreeView(tree, "name", 6);
		view.setSize(1440, 900);
		Thread.sleep(800);
		view.zoomToFit();
		Thread.sleep(800);
		view.saveImage(out, "JPG", 1);
	}
	
	public boolean supportWriter() {
		return(false);
	}
	
 	public void process(Writer out) throws Exception {
		throw new IllegalArgumentException("Writer is not supported!");
	}

	private void genTree(Tree t, Node parent, int level, int child) {
		if (level == 0) {
			return;
		}
		for (int i = 0; i < child; i++) {
			Node n = t.addChild(parent);
			n.set("name", "Node[" + level + "," + child +"]");
			n.set(VisualItem.EXPANDED, true);
			genTree(t, n, level-1, child);
		}
	}
	
	public void setProperties(Properties attrMap) {
		// TODO Auto-generated method stub
		
	}

}
