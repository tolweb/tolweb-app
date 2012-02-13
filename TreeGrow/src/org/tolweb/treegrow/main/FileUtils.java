/*
 * Created on Aug 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileUtils {
    public static void copyFile(File existingFile, File destFile) {
        FileInputStream source = null;
        FileOutputStream destination = null;
        byte[] buffer;
        int bytes_read;
                          
        try {
		     // If we've gotten this far, then everything is okay; we can
		     // copy the file.
		     source = new FileInputStream(existingFile);
		     destination = new FileOutputStream(destFile);
		     buffer = new byte[1024];
		     while(true) {
		         bytes_read = source.read(buffer);
		         if (bytes_read == -1) break;
		         destination.write(buffer, 0, bytes_read);
		     }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
             if (source != null) {
                 try { source.close(); } catch (IOException e) { ; }
             }
             if (destination != null) {
                 try { destination.close(); } catch (IOException e) { ; }
             }
        }        
    }

    /**
     * Fetch the entire contents of a text file, and return it in a String.
     * This style of implementation does not throw Exceptions to the caller.
     *
     * @param aFile is a file which already exists and can be read.
     */    
    public static String getFileContents(String filePath) throws Exception {
        StringBuffer contents = new StringBuffer();

        BufferedReader input = null;
        //use buffering
        //this implementation reads one line at a time
        input = new BufferedReader(new FileReader(new File(filePath)));
        String line = null; //not declared within while loop
        while ((line = input.readLine()) != null) {
            contents.append(line);
            contents.append(System.getProperty("line.separator"));
        }      
        return contents.toString();
    }
    /**
     * Converts a list of files into a zip file
     * @param files the list of valid file handles
     * @param destStream where the output should go to
     * @return the ZipOutputStream
     * @throws IOException
     */
    public static ZipOutputStream createZipFile(List files, OutputStream destStream) throws IOException {
    	byte b[] = new byte[512];
        ZipOutputStream zout = new ZipOutputStream(destStream);
        int totalSize = 0;
        for (Iterator iter = files.iterator(); iter.hasNext();) {
			File nextFile = (File) iter.next();
	        InputStream in = new FileInputStream(nextFile);
	        ZipEntry e = new ZipEntry(nextFile.getName());
	        zout.putNextEntry(e);
	        int len=0;
	        while((len=in.read(b)) != -1) {
	        	zout.write(b,0,len);
	        	totalSize += len;
	        }
	        zout.closeEntry();
		}
        // only call finish() is we did something to the stream. 
        if (files != null && !files.isEmpty()) {
        	zout.finish();
        }
        return zout;
    }
}
