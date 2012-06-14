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
package org.onecmdb.utils.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class RemoteClassLoader extends URLClassLoader {

	public RemoteClassLoader(URL[] arg0, ClassLoader parent) {
		super(arg0, parent);
	}
	
	
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.findClass(name);
	}


	public static String USAGE() {
		return("<remote classpath url> main-class [options]");
	}

	public static void main(String argv[]) {
		
		if (argv.length < 2) {
			System.out.println(USAGE());
			System.exit(-1);
		}
		try {
			String urlStr[] = argv[0].split(";");
			URL urls[] = new URL[urlStr.length];
			for (int i = 0; i < urlStr.length; i++) {
				urls[i] = new URL(urlStr[i]);
			}
			RemoteClassLoader loader = new RemoteClassLoader(urls, RemoteClassLoader.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(loader);
			try {
				Class cl = loader.findClass(argv[1]);
				
				int length = argv.length-2;
				String nArgv[] = new String[length]; 
				
				// Invoke main().
				System.arraycopy(argv, 2, nArgv, 0, length);
				
				// Get the main method.
				Method main = cl.getMethod("main", new Class[] {String[].class});
				
				// Invoke main...
				main.invoke(null, new Object[] {nArgv});
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
