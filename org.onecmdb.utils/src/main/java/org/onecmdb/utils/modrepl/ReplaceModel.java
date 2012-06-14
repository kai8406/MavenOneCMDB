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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Comparator;

public class ReplaceModel {

    /** name of the file containing the model */
    private static final String MODEL_XML = "Model.xml";

    /** location holding the model to load */
    private final File dataModel;

    /** holder of all models available */
    private final File modelStore;
    
    
    /**
     * @param tomcatHome
     * @param modelStore
     * @throws IOException
     */
    ReplaceModel(File tomcatHome, File modelStore) throws IOException {
        File dataDir = new File(tomcatHome, "webapps/ROOT/WEB-INF/classes");
        this.dataModel = new File(dataDir, MODEL_XML);
        if (!this.dataModel.exists()) {
            throw new FileNotFoundException("Cannot locate model file '"+ MODEL_XML +"'.");
        }
    
        this.modelStore = modelStore;
        if ( !this.modelStore.exists() ) {
            throw new FileNotFoundException("No model store '" + modelStore + "' found.");
        }
    }
    
    File[] getModels() throws IOException {
        File[] files = modelStore.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.matches("^.*Model\\.xml$");
            }}); 
        
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        
        return files;
    }
    

    public void useModel(File newModel) {
        try {
            // Create channel on the source
            FileChannel srcChannel = new FileInputStream(newModel).getChannel();
        
            // Create channel on the destination
            FileChannel dstChannel = new FileOutputStream(this.dataModel).getChannel();
        
            // Copy file contents from source to destination
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        
            // Close the channels
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
        }
    }

    public File getInstalledModel() {
        return this.dataModel;
    }
    
}
