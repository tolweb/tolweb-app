/*
 * NewVersionDownloader.java
 *
 * Created on September 19, 2003, 2:29 PM
 */

package org.tolweb.treegrow.main;

import java.io.*;
import java.net.*;
import javax.swing.*;
import org.apache.commons.httpclient.*;
import org.tolweb.base.http.BaseHttpRequestMaker;

/**
 * Downloads new versions of jar files on the server
 */
public class NewVersionDownloader {
    private static final String CHANGED_XML_FILENAME = "xmlchanged.html";
    private static final String BASE_URL = "http://editor.tolweb.org/";
    private int bytesDownloaded = 0;
    private boolean done = false, error = false;
    public static final String USERNAME = "silvio";
    public static final String PASSWORD = "tree4me";
    public static final String REALM = "TreeGrow Users Only";
    
    static {
        java.net.Authenticator.setDefault (new ToLAuthenticator(USERNAME, PASSWORD));
    }       
    
    /** Creates a new instance of NewVersionDownloader */
    public NewVersionDownloader() {
    }
    
    /**
     * Check to see if there is a newer version of either the Primary jar file 
     * or the support jar file. If so, download it.
     * The dates of the current files are stored in the settings.xml file
     *
     * @return true normally, false if there is a new version with XML changes
     *         and the user has local files to check out.  This signifies that
     *         the Upload, Preview, Submit tab in the ManagerFrame should be
     *         selected
     */
    public boolean fetchNewVersion () {
        final Controller controller = Controller.getController();
        try {
            PreferenceManager manager = controller.getPreferenceManager();
            
            final String editorJar = "ToLEditor.jar";
            final String supportJar = "ToLSupportFiles.jar";

            URL editorJarURL = new URL(BASE_URL + editorJar);
            final URLConnection editorJarConn = editorJarURL.openConnection();
            final long serverEditorJarTime  = editorJarConn.getLastModified();
            
            URL supportJarURL = new URL(BASE_URL + supportJar);
            final URLConnection supportJarConn = supportJarURL.openConnection();
            final long serverSupportJarTime = supportJarConn.getLastModified();
            long editorJarTimestamp = manager.getEditorJarTimestamp();
            if (editorJarTimestamp == 0) {
                manager.setEditorJarTimestamp(serverEditorJarTime);
                manager.writePreferencesToDisk();
                editorJarTimestamp = serverEditorJarTime;
            }
            long supportJarTimestamp = manager.getSupportJarTimestamp();
            if (supportJarTimestamp == 0) {
                manager.setSupportJarTimestamp(serverSupportJarTime);
                manager.writePreferencesToDisk();
                supportJarTimestamp = serverSupportJarTime;
            }
            System.out.println("server editor time is: " + serverEditorJarTime + " local timestamp is: " + editorJarTimestamp);
            final boolean getEditorJar = serverEditorJarTime > editorJarTimestamp;
            final boolean getSupportJar = serverSupportJarTime > supportJarTimestamp;
         
            if ( getEditorJar || getSupportJar ) {            
                boolean xmlChanged = checkForXMLChanged();
                if (xmlChanged) {
                    if (Controller.getController().getFileManager().getLocalDataFiles().size() > 0) {
                        String NEW_VERSION_XML_CHANGES_REQUIRED = Controller.getController().getMsgString("NEW_VERSION_XML_CHANGES_REQUIRED");
                        JOptionPane.showMessageDialog(null, NEW_VERSION_XML_CHANGES_REQUIRED, "XML Changes", JOptionPane.OK_OPTION);
                        return false;
                    }
                } 
                
                String NEW_VERSION_TO_DOWNLOAD = Controller.getController().getMsgString("NEW_VERSION_TO_DOWNLOAD");
                int response = JOptionPane.showConfirmDialog(null, NEW_VERSION_TO_DOWNLOAD,
                                    "Update to new version?",
                                    JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);  
                if ( response == 1) { //no
                    return true;
                }
                
                int expectedSize = 0;
                /*if (getEditorJar){
                    expectedSize += editorJarConn.getContentLength();
                }
                if (getSupportJar){
                    expectedSize += supportJarConn.getContentLength();
                } */               
                
                final NewVersionDownloadFrame statusFrame = new NewVersionDownloadFrame(expectedSize);
               
                Thread downloadThread = new Thread () {
                    public void run () {
                        statusFrame.startSpinningUI();
                        PreferenceManager manager = Controller.getController().getPreferenceManager();
                        if ( getEditorJar ) {
                            boolean result = saveDownloadedFile(statusFrame, editorJarConn , editorJar);
                            if (!result) {
                                return;
                            }
                            manager.setEditorJarTimestamp(serverEditorJarTime);
                            manager.writePreferencesToDisk();
                        }           

                        if ( getSupportJar ) {
                            boolean result = saveDownloadedFile(statusFrame, supportJarConn , supportJar);           
                            if (!result) {
                                return;
                            }                            
                            manager.setSupportJarTimestamp(serverSupportJarTime);
                            manager.writePreferencesToDisk();
                        }  

                        statusFrame.dispose();

                        restartApp();

                        System.exit(0);
                    }
                };
                downloadThread.start();                
                downloadThread.join(); 
            }            
        } catch (Exception ex) {
        }
        return true;
    }
    
    /**
     * Checks to see if there is a special file on the server indicating whether
     * the XML structure has changed.
     *
     * @return true If the XML structure has changed in the new version, false
     *              otherwise
     */
    private boolean checkForXMLChanged() {
        try {
            URL fileURL = new URL(BASE_URL + CHANGED_XML_FILENAME);
            URLConnection fileURLConnection = fileURL.openConnection();
            long serverXMLChangedTime = fileURLConnection.getLastModified();
            if (serverXMLChangedTime > Controller.getController().getPreferenceManager().getEditorJarTimestamp()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    
    /**
     * Saves the downloaded file to disk
     *
     * @param statusFrame The frame to update progress on
     * @param conn The URLConnection to read data from
     * @param localName The name of the file to save locally
     */
    private boolean saveDownloadedFile (final NewVersionDownloadFrame statusFrame, final URLConnection conn, String localName)  {
        final Controller controller = Controller.getController();
        final String localFileName = controller.getFileManager().getSystemDir()+localName;
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(USERNAME, PASSWORD);
        byte[] bytes = BaseHttpRequestMaker.makeHttpRequest(conn.getURL().toString(), credentials, REALM);
        if (bytes == null) {
            return false;
        }
        File localFile = new File(localFileName);
        File prevLocalFile = new File(localFileName+".prev");
        localFile.renameTo(prevLocalFile);      
        boolean error = false;
        if (bytes != null) {
            try {
                FileOutputStream out = new FileOutputStream(localFileName);
                out.write(bytes);
            } catch (Exception e) {
                error = true;
            }
        }
        
        if (error || bytes == null) {
            localFile.delete();
            //file not downloaded successfully...revert to previous file
            boolean success = prevLocalFile.renameTo(localFile);
            if (!success) {
                String ERROR_NEW_VERSION_CANT_RENAME = controller.getMsgString("ERROR_NEW_VERSION_CANT_RENAME");
                JOptionPane.showMessageDialog(statusFrame, ERROR_NEW_VERSION_CANT_RENAME, "Error downloading new version", JOptionPane.ERROR_MESSAGE);
            } else {
                String ERROR_NEW_VERSION = controller.getMsgString("ERROR_NEW_VERSION");
                JOptionPane.showMessageDialog(statusFrame, ERROR_NEW_VERSION, "Error downloading new version", JOptionPane.ERROR_MESSAGE);
            }
            statusFrame.dispose();
            return false;
        } 

        return true;
    }    
    
    /**
     * Restarts the application using the new (just downloaded) jar files
     */
    private void restartApp(){
        Controller controller = Controller.getController();
        String strCmdLine = System.getProperty("java.home");
        if (strCmdLine == null || strCmdLine.length()==0) {
              //raise the error
              JOptionPane.showMessageDialog ( null,"Unable to find Java(TM) home. Please start the Application Upgrade Tool manually."); 
              return;
        }
        
        strCmdLine += File.separator+"bin"+File.separator+"java";
        String db = controller.getDatabase();

        if (controller.isMac()) {
            strCmdLine += " -Xdock:name=TreeGrow -Xdock:icon=TreeGrow.app/Contents/Resources/tol.icns -Dcom.apple.macos.useScreenMenuBar=true";
            strCmdLine += " -jar " + controller.getFileManager().getSystemDir()+ "ToLEditor.jar " + db;
        } else {
            strCmdLine += " -jar \"" + controller.getFileManager().getSystemDir()+ "ToLEditor.jar\" " + db;   
        }
        
        
        //String s[] = { strCmdLine, "-Xdock:name=dev_editor", "-Xdock:icon=ToLEditor_dev.app/Contents/Resources/tol.icns", "-Dcom.apple.macos.useScreenMenuBar=true", "-Dcom.apple.mrj.application.growbox.intrudes=false","-jar", "ToLEditor.jar"};
        Runtime rt = Runtime.getRuntime();
        System.out.println("restarting command-line = " + strCmdLine);
        try {
            rt.exec(strCmdLine);
        } catch (Exception e) {
            //System.out.println("oh geez");
        }
    }    
}
