package org.tolweb.treegrow.main;

import java.util.*;
import javax.swing.*;

import org.tolweb.treegrow.tree.*;


/**
 * Prompts the user for upload or check-in if that is necessary before closing
 * the file
 */
public class CloseDialog  {
    private boolean fromOpen;
    private JOptionPane optionPane;

    public CloseDialog() {
        this(false);
    }

    /**
     * If this gets called from the open item on the menu, we don't want
     * to exit the program, so remember that.
     *
     * @param fromOpenItem Whether this was called from the "open" menu item
     */
    public CloseDialog(boolean fromOpenItem) {
        fromOpen = fromOpenItem;
    }

    /**
     * The method that actually goes through the process of prompting the user
     * to upload or check in their files
     * @return -1 when things go wrong as well or if the user chooses to cancel,
     *         1 otherwise
     */
    public int saveConfirm() {
        Controller controller = Controller.getController();
        FileManager fileManager = controller.getFileManager();
        try {
            if(controller.getCancelWindow() != null && controller.getCancelWindow().isVisible()) {
                System.out.println("cancelling download");
                controller.cancelDownload();
                System.exit(0);                
            }
            if (controller.getTreeEditor() == null) {
                if (!fromOpen) {
                    if (controller.getTreeEditor() != null) {
                        controller.getTreeEditor().dispose();
                    }                                    
                    System.exit(0);
                }
            }
            controller.saveChanges();
            boolean changedFromServer = false;
            // There were changes made to the currently opened local file from the last
            // time it was opened locally
            if (controller.getFileChanged()) {
                changedFromServer = true;                        
            }

            TreePanel treePanel = TreePanel.getTreePanel();                     

            Iterator it = treePanel.getTree().getNodeList().iterator();
            while (!changedFromServer && it.hasNext()) {
                Node node = (Node) it.next();
                changedFromServer = node.getChanged();
            }

            CheckNetConnection check = new CheckNetConnection();

            if (changedFromServer  &&  check.isConnected() > 0 ) {
                String tempString = controller.getFileName() + Controller.EXTENSION;
                System.out.println("tempString = " + tempString);
                fileManager.getMetadataFromFile(tempString);
                String CHK_UPLOAD_CHNGS = controller.getMsgString("CHK_UPLOAD_CHNGS");
                String JUST_CLOSE_FILE = controller.getMsgString("JUST_CLOSE_FILE");
                String UPLOAD_FILE_AND_KEEP = controller.getMsgString("UPLOAD_FILE_AND_KEEP");
                String UPLOAD_FILE_AND_CHECKIN = controller.getMsgString("UPLOAD_FILE_AND_CHECKIN");
                         
                String[] choices = new String[] {
                    JUST_CLOSE_FILE,
                    UPLOAD_FILE_AND_KEEP,
                    UPLOAD_FILE_AND_CHECKIN };
                int returnValue = -1;
                int response = RadioOptionPane.showRadioButtonDialog(controller.getTreeEditor(), "What would you like to do?", CHK_UPLOAD_CHNGS, choices);
                controller.setWaitCursors();
                if (response == 0) {
                    if (!fromOpen) {
                        openTreeGrowManager();
                    }
                    returnValue = 1;                    
                } else if (response == 1) {
                    if (doUpload() && !fromOpen) {                   
                        openTreeGrowManager();
                    }
                    returnValue = 1;
                } else  if (response == 2) {
                    if (doUpload()) {
                        fileManager.checkIn();
                        if (!fromOpen) {
                            openTreeGrowManager();
                        }
                    }
                    returnValue = 1;
                }
                controller.setDefaultCursors();
                // They cancelled and decided they didn't want to exit                           
                return -1;
            } else if (!fromOpen) {
                openTreeGrowManager();
            }
                        
            controller.setDefaultCursors();
            return 1;
        } catch(Exception error) {
            error.printStackTrace();
            openTreeGrowManager();
            return -1;
        }
    }
    
    public static void openTreeGrowManager() {
        Controller controller = Controller.getController();
        controller.saveChanges();
        controller.setCriticalMenusEnabled(false);
        if (controller.getTreeEditor() != null) {
            controller.getTreeEditor().dispose();
        }
        controller.openFileManager();    
    }

    public boolean doUpload() {
        UrlXMLWriter writer = new UrlXMLWriter();
        return writer.writeXML();
    }
}



