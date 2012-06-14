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
package org.onecmdb.core.tests.profiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class AnalysProfile {
	
	private HashMap allCalls = new HashMap();
	private int lines = 0;
	private int showCounts = 1;
	private Stack current;
	
	public void setShowCount(int count) {
		this.showCounts = count;
	}
	
	public int addData(String line, int call) {
		if (line.startsWith("##")) {
			return(0);
		}
		StringTokenizer tok = new StringTokenizer(line, ";");
		if (!tok.hasMoreElements()) {
			return(0);
		}
		int nCalls = call;
		if (call == 0) {
			String threadName = (String)tok.nextElement();
			String totalCalls = (String)tok.nextElement();
			current = new Stack();
			nCalls = Integer.parseInt(totalCalls);
		}
		
		lines++;
		/**
		 * Looks like this:
		 * "Name;Calls;StartTime;StopTime;DeltaTime;StartMem;StopMem;DeltaMem"
		 */
		ArrayList list = new ArrayList();
		
		int callsLeft = 0;
		
		while(tok.hasMoreElements()) {
			ProfileData data = new ProfileData();
			String s = null;
			data.name = (String)tok.nextElement();
			s = (String)tok.nextElement();
			data.calls = new Integer(s).intValue();
			data.start = new Long((String)tok.nextElement()).longValue();
			data.stop = new Long((String)tok.nextElement()).longValue();
			data.dt = new Long((String)tok.nextElement()).longValue();
			data.startMem = new Long((String)tok.nextElement()).longValue();
			data.stopMem = new Long((String)tok.nextElement()).longValue();
			data.dm = new Long((String)tok.nextElement()).longValue();
			
			
			data.addSame(data);
			
			ProfileData stored = (ProfileData)allCalls.get(data.name);
			
			if (stored != null)  {
				stored.addSame(data);
			} else {
				allCalls.put(data.name, data);
				stored = data;
			}
			if (!current.isEmpty()) {
				ProfileData parent = (ProfileData)current.peek();
				
				if (parent != null) {
					parent.addSonData(data);
				}
				if (parent.getSons().size() == parent.calls) {
					current.pop();
				}
			}
			if (data.calls > 0) {
				current.push(data);
			}
		}
		return(nCalls-1);
	}
	
	public void showResult() {
		
		System.out.println("Parsed Lines " + lines + " Entries " + allCalls.size());
		
		Collection sorted = getSortedCalls();
		int count = 0;
		System.out.println("Sorted By TIME");
		for (Iterator iter = sorted.iterator(); iter.hasNext();) {
			ProfileData pData = (ProfileData)iter.next();
			System.out.println("\t" + "["+ count + "] " +  pData.name + " Used[" + pData.used + "]");
			System.out.println("\t\tTime[" + pData.avgDt +"]");
			System.out.println("\t\tDM[" + pData.avgDm + "]"); 
			System.out.println("\t\tMEM[" + pData.avgMem + "]");
			count++;
		}
		
		
		count = 0;
		System.out.println("Sorted By Usage");
		for (Iterator iter = getSortedByUse().iterator(); iter.hasNext();) {
			ProfileData pData = (ProfileData)iter.next();
			System.out.println("\t" + "["+ count + "] " +  pData.name + " Used[" + pData.used + "]");
			System.out.println("\t\tTime[" + pData.avgDt +"]");
			System.out.println("\t\tDM[" + pData.avgDm + "]"); 
			System.out.println("\t\tMEM[" + pData.avgMem + "]");
			count++;
			
		}
		
		count = 0;
		System.out.println("Sorted By MEM");
		for (Iterator iter = getSortedByMem().iterator(); iter.hasNext();) {
			ProfileData pData = (ProfileData)iter.next();
			System.out.println("\t" + "["+ count + "] " +  pData.name + " Used[" + pData.used + "]");
			System.out.println("\t\tTime[" + pData.avgDt +"]");
			System.out.println("\t\tDM[" + pData.avgDm + "]"); 
			System.out.println("\t\tMEM[" + pData.avgMem + "]");
			count++;		   
		}
		
		count = 0;
		for (Iterator iter = sorted.iterator(); iter.hasNext();) {
			count++;
			if (count > showCounts) {
				System.out.println("More.....");
				break;
			}
			ProfileData pData = (ProfileData)iter.next();
			ArrayList list = new ArrayList();
			list.add(pData);
			showAll(list, 0, null);
		}
	}
	public Collection getSortedCalls() {
		Comparator comp = new Comparator() {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof ProfileData && o2 instanceof ProfileData) {
					return((int) (((ProfileData)o2).dt - ((ProfileData)o1).dt));
				}
				return(0);
			}
		};
		SortedSet sorted = new TreeSet(comp);
		sorted.addAll(allCalls.values());
		return(sorted);
	}
	
	public Collection getSortedByUse() {
		
		Comparator comp = new Comparator() {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof ProfileData && o2 instanceof ProfileData) {
					return((int) (((ProfileData)o2).used - ((ProfileData)o1).used));
				}
				return(0);
			}
		};
		SortedSet sorted = new TreeSet(comp);
		sorted.addAll(allCalls.values());
		return(sorted);
	}
	
	public Collection getSortedByMem() {
		Comparator comp = new Comparator() {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof ProfileData && o2 instanceof ProfileData) {
					return((int) (((ProfileData)o2).dm - ((ProfileData)o1).dm));
				}
				return(0);
			}
		};
		SortedSet sorted = new TreeSet(comp);
		sorted.addAll(allCalls.values());
		return(sorted);    	
	}
	
	public void showAll(Collection col, int count, ProfileData first) {
		
		if (col == null) {
			return;
		}
		
		ProfileData pLast = null;
		
		for (Iterator iter = col.iterator(); iter.hasNext();) {
			
			ProfileData pData = (ProfileData)iter.next();
			ProfileData parent = pData.getParent();
			if (first == null) {
				first = pData;
			}
			StringBuffer pre = new StringBuffer();
			pre.append((pData.start - first.start));
			int fillout = (7-pre.length());
			for (int i = 0; i < fillout; i++) {
				pre.append(" ");
			}
			for (int i = 0; i < count; i++) {
				pre.append("\t");
			}
			
			if (parent != null) {
				pre.append("-[" + (pData.start - parent.start));
				if (pLast != null) {
					pre.append("," + (pData.start - pLast.start));
				}
				pre.append("]->");
			}
			pre.append(pData.name + " [" + pData.dt + "]");
			System.out.println(pre.toString());
			showAll(pData.getSons(), (count+1), first);
			pLast = pData;
		}
		
	}
	
	public void parseFile(String file) {
		LineNumberReader lin = null;
		try {
			lin = new LineNumberReader(new FileReader(file));
			String line = null;
			int calls = 0;
			while((line = lin.readLine()) != null) {
				//System.out.print("ParseLine: " + lines + '\r');
				try {
					calls = addData(line, calls);
				} catch (Throwable t) {
					System.out.println("Can't parse line<" + line +"> : " + t);
				}
			}
			System.out.print("Parsed Lines: " + lines + '\n');
			
			showResult();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		} finally {
			try {
				lin.close();
			} catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use Options | File Templates.
			}
		}
	}
	
	public static void main(String argv[]) {
		String fileName = argv[0];
		int count = 0;
		if (argv.length > 1) {
			count = Integer.parseInt(argv[1]);
		}
		AnalysProfile ap = new AnalysProfile();
		ap.setShowCount(count);
		ap.parseFile(fileName);
	}
	
	
	
}

