/*
 * ToLFileChooser.java
 *
 * Created on October 6, 2003, 4:51 PM
 */

package org.tolweb.treegrow.main;


import java.io.*;
import java.awt.*;
import javax.swing.*;

/**
 * Class that determines whether to show a FileDialog or a JFileChooser.  If
 * the user is on a Mac, the FileDialog is shown since that is more in line
 * with what users expect to see on Mac.  Otherwise, the JFileChooser is used
 * since it is more modern looking and provides more flexible functionality
 */
public class ToLFileChooser {
    private boolean userCancelled = false;
    FileDialog fileDialog;
    FilenameFilter fileNameFilter;
    
    JFileChooser fileChooser;
    javax.swing.filechooser.FileFilter fileFilter;
    
    Frame parent;
    
    public ToLFileChooser(Frame parent, String title) {
        this(parent, title, false);
    }
    
    /** Creates a new instance of ToLFileChooser */
    public ToLFileChooser(Frame parent, String title, boolean isSave ) {
        this.parent = parent;
        
        if (Controller.getController().isMac()) {
            fileDialog = new FileDialog(parent, title);
            fileDialog.setDirectory(Controller.getController().getFileManager().getDataPath());
            if (isSave) {
                fileDialog.setMode(FileDialog.SAVE);
            }
        } else {
            fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(title);
        }
    }

    /**
     * Sets the filter for the dialog or chooser
     *
     * @param fileNameFilter A FilenameFilter used for the fileDialog
     * @param fileFilter A FileFilter used for the JFileChooser
     */
    public void setFilter (FilenameFilter fileNameFilter, javax.swing.filechooser.FileFilter fileFilter) {
        if (Controller.getController().isMac()) {
            fileDialog.setFilenameFilter(fileNameFilter);
        } else {
            fileChooser.setFileFilter(fileFilter);   
            fileChooser.setAcceptAllFileFilterUsed(false);
        }
    }
    
    public void setDirectory(File dir) {
        if (Controller.getController().isMac()) {
            fileDialog.setDirectory(dir.getAbsolutePath());
        } else {
            fileChooser.setCurrentDirectory(dir);
        }
    }
    
    public File getDirectory() {
        if (Controller.getController().isMac()) {
            if (fileDialog.getDirectory() != null) {
                return new File(fileDialog.getDirectory());
            } else {
                return null;
            }
        } else {
            return fileChooser.getCurrentDirectory();
        }        
    }

    /**
     * Shows the dialog or asks the chooser to show a dialog
     */
    public void show() {
        if (Controller.getController().isMac()) {    
            fileDialog.show();
        } else {
            int response = fileChooser.showDialog(parent,"OK");
            if (response == JFileChooser.CANCEL_OPTION) {
                userCancelled = true;
            }
        }
    }
            
    /**
     * Returns a string which is the path to the selected file from either the
     * dialog or chooser
     *
     * @return the path to the selected file
     */
    public String getSelectedFileName() {
        if (Controller.getController().isMac()) {    
            if (fileDialog.getFile() == null || fileDialog.getFile().equals("")) {
                return null;
            } else {
                return fileDialog.getDirectory() + fileDialog.getFile();
            }
        } else {
            if (userCancelled || fileChooser.getSelectedFile() == null) {
                return null;
            } else {
                return fileChooser.getSelectedFile().getAbsolutePath();
            }
        }
    }
   
}
