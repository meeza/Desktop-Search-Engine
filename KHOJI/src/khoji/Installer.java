/*
 * Copyright 2014 developers7.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package khoji;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author developers7
 */
public class Installer {

    Installer(File installation_folder) {
        //      to  put .jar file and lib folder into Khoji dorectory we need the current directory
//      so we find the current directory
        URL jar_location = KHOJI.class.getProtectionDomain().getCodeSource().getLocation();
        final String jar_path = new String(jar_location.getFile());
        File jar_file = new File(jar_path);
        File current_directory = jar_file.getParentFile();
        try {
            if(copyDirectory(current_directory,installation_folder))
                JOptionPane.showMessageDialog(null, "Khoji installed successfully");
            else
                JOptionPane.showMessageDialog(null, "There is any error to copy installation files", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "There is any error to copy installation files", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(KHOJI.class.getName()).log(Level.SEVERE, null, ex);
        }

        // copy .jar file, lib foder, and any other file into C:\khoji
    }
    
      // method to copy a directory and its subfolders and files to another directory
    public static boolean copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        try {
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdir();
                }
                String[] children = sourceLocation.list();
                for (String children1 : children) {
                    copyDirectory(new File(sourceLocation, children1), new File(targetLocation, children1));
                }
            } else {
                
                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
            return true;
        } catch (IOException iOException) {
            return false;
        }
    }    
    
    
}
