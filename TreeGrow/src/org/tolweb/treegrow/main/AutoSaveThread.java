/*
 * AutoSaveThread.java
 *
 * Created on July 1, 2003, 8:49 AM
 */

package org.tolweb.treegrow.main;

import java.io.File;
import java.lang.Thread;

/**
 * Thread subclass that saves the file every minute while the user is working
 */
public class AutoSaveThread extends Thread {
    private static final String BACKUP_EXTENSION = ".backup";
    private boolean keepGoing = true;
                
    public void run() {
        int oneMinute = 60 * 1000;
        System.out.println("getting called");
        while ( keepGoing ) {
            System.out.println("inside loop");
            System.out.println("going to write the file");
            // Synchronized so that once stopAutoSaving is called, we don't write again
            synchronized (this) {
                System.out.println("inside synchronized");
                if (keepGoing) {
                    System.out.println("going to create autosavewriter");
                    AutoSaveFileXMLWriter writer = new AutoSaveFileXMLWriter();   
                    // Copy the current file to a backup -- delete the backup first
                    File backupFile = new File(writer.buildOutputFileName() + BACKUP_EXTENSION);
                    if (backupFile.exists()) {
                        backupFile.delete();
                    }
                    File currentFile = new File(writer.buildOutputFileName());
                    copyFileToBackup(currentFile);
                    writer.writeXML();
                }
                try {
                    System.out.println("going to sleep");
                    // It's not waking up after it goes to sleep for the first time
                    // after a download occurs
                    Thread.sleep(oneMinute);
                    System.out.println("done sleeping");
                } catch (Exception e) { e.printStackTrace(); }            
            }
        }
        System.out.println("done");
    }
    
    /**
     * Stops auto saving the file.  Synchronized in order to prevent a write from occurring
     * after this is called.
     */
    public void stopAutoSaving() {
        keepGoing = false;
    }
    
    private void copyFileToBackup(File existingFile) {
        File backupFile = new File(existingFile.getAbsolutePath() + BACKUP_EXTENSION);
        FileUtils.copyFile(existingFile, backupFile);
    }
}
