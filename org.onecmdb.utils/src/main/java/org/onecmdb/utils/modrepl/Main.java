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
package org.onecmdb.utils.modrepl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	// Since version 1.4.0 Beta there exists only one model!
    	if (true) {
    	     System.out.println("OneCMDB Model Replace Utility");
    	     System.out.println("=============================");
    	     System.out.println();
    	     System.out.println("OneCMDB 1.4.0 Beta only contains ONE model, this utility");
    	     System.out.println("is not longer valid.");
    	     System.out.println();
    	     System.out.println("The default installations includes a number of demo instances.");
    	     System.out.println("To remove the demo instances check documentation on http://www.onecmdb.org.");
    	     System.exit(1);
    	}

        if ( args.length != 3 ) {
            System.err.println("usage: java ..." + Main.class.getName() + " MODELS_HOME HSQLDB_HOME TOMCAT_HOME ") ;
            System.err.println();
            System.err.println("Where");
            System.err.println();
            System.err.println("`MODELS_HOME' directory containing OneCMDB models");
            System.err.println("`HSQLDB_HOME' directory where OneCMDB HSQLDB is installed.");
            System.err.println("`TOMCAT_HOME' directory where OneCMDB Tomcat is installed.");

            System.exit(1);
        }
        
        File  modelsHome = new File(args[0]);
        
        
        File hsqldbHome = new File(args[1]);
        if (!hsqldbHome.isDirectory()) {
            System.err.println("TOMCAT_HOME does not exist!");
            System.exit(1);
        }
        
        
        
        File tomcatHome = new File(args[2]);
        
        ReplaceModel chmodel = null;
        try {
             chmodel = new ReplaceModel(tomcatHome, modelsHome);
        } catch (IOException e) {
            System.err.println("Error while initializing: " + e.getMessage());
            System.err.println();
            System.err.println("Make sure you have run OneCMDB once, before issuing this utility.");
            System.exit(1);
        }
        File currentModel = chmodel.getInstalledModel();

        System.out.println("OneCMDB Model Replace Utility");
        System.out.println("=============================");
        System.out.println();
        System.out.println("This utility changes the model to be used by OneCMDB. Current");
        System.out.println("data in the database will be deleted if you let it perform");
        System.out.println("the change.");
        System.out.println();
        System.out.println("Make sure OneCMDB is stopped before using this utility.");
        System.out.println();
        
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String y = null;
        do {
            System.out.print("Continue [Y/N]? ");
            y = reader.readLine();
            if (y == null) return;
        } while (!y.equalsIgnoreCase("y") && !y.equalsIgnoreCase("yes") 
                && !y.equalsIgnoreCase("n") && !y.equalsIgnoreCase("no"));

         if (y.toLowerCase().startsWith("n") )
         {
             System.out.println("OK. Nothing will be altered.");
             System.exit(0);
         }

        System.out.println();
        System.out.println("Available models:");
        System.out.println();
        
        File[] models = chmodel.getModels();
        if (models.length == 0) {
            System.out.println("No models found!");
        }
        
        boolean footnote = false;
        for (int i = 0; i < models.length; i++) {
            System.out.print((i + 1) + ". " + models[i].getName() );
            if (models[i].length() == currentModel.length()) {
                System.out.print("*");
                footnote = true;
            }
            System.out.println();
        }
        if (footnote) {
            System.out.println("----");
            System.out.println("* Model currently installed/in use.");
        }
        System.out.println();

        
        int chosen = 0;
        while (chosen == 0) {
            System.out.print("Select model [1-"+models.length+"]/[Q]uit: ");
            String s = reader.readLine();
            if (s.equalsIgnoreCase("q") || s.equalsIgnoreCase("quit")) {
                System.exit(0);
            }

            try {
                chosen = Integer.parseInt(s);
                if (chosen < 1 || chosen > models.length) {
                    throw new NumberFormatException("No such model");
                }
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
                chosen = 0;
            }
        }
        
        // stop system
        
        chmodel.useModel(models[chosen -1]);


        // wipe out old data
        File data = new File(hsqldbHome, "data");
        boolean success = recursiveDelete(data, false);
        

        System.out.println();
        System.out.println("Successfully changed the initial data model. You may now");
        System.out.println("start OneCmdb to start using it.");

        // start
        
    }

    static boolean recursiveDelete(File data, boolean delete) throws IOException {
        boolean ok = true;
        if (data.isDirectory()) {
            File[] files = data.listFiles();
            for (File f : files) {
                ok = recursiveDelete(f, true);
            }
        }
        if (ok && delete) {
            ok = data.delete();
        }
        if (!ok) {
            throw new IOException("Failed to delete `" + data + "'");
        }
        return ok;
        
    }

    final File stopExe;
    final String[] stopArgs;

    final File startExe;
    final String[] startArgs;

    {
        String osType = System.getenv("os.type").toLowerCase();
        if (osType.startsWith("windows")) {
        
            stopExe = new File("bin/shutdown.bat");
            stopArgs = new String[] {};

            startExe = new File("bin/onecmdb.bat");
            startArgs = new String[] { "--no-splash" };
            
        } else if (osType.startsWith("linux")) {
        
            stopExe = new File("bin/onsecmdb.sh");
            stopArgs = new String[] { "stop" };
        
            startExe = new File("bin/onecmdb.sh");
            startArgs = new String[] { "start" };
        } else {

            stopExe = null;
            stopArgs = null;
        
            startExe = null;
            startArgs = null;
        }
    }

    
    private void runNative(File exe, String[] args) 
        throws IOException, InterruptedException 
    {
        Runtime rt = Runtime.getRuntime();
        
        String[] cmdarr = new String[args.length + 1];
        
        System.arraycopy(args, 0, cmdarr ,1, args.length);
        cmdarr[0] = exe.getPath();
        
        Process p = rt.exec(cmdarr);
        int rc = p.waitFor();
    }
    
}
