/*
 * Controller.java
 *
 * Created on July 11, 2003, 1:57 PM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.swing.*;

import org.jdom.*;
import org.jdom.input.*;
import org.tolweb.treegrow.main.Ancestor;
import org.tolweb.treegrow.tree.*;
import com.Ostermiller.util.Browser;

/**
 * Class used as a central repository of application information and window
 * management.  Contains methods for filesystem interaction, UI updates,
 * user information tracking, and assorted other functionality. 
 * 
 */
public class Controller {
    public static final double VERSION = 2.01;
    public static final String BUILD_NUMBER = "d43";
    public static final String EXTENSION = ".xml";  
    public static final String ERROR_IMG_NAME = "QuestionMark.jpg";
    public static final String LIGHTBULB_IMG_NAME = "lightbulb.gif";
    private Icon lightbulbIcon;
    private DateFormat dateFormatter;
    private static Controller controller;
    private FileManager fileManager;
    private PreferenceManager preferenceManager;
    //private Hashtable nodesToPageEditors;
    //private Hashtable pageEditorsToMenuItems;
    //private Hashtable menuItemsToPageEditors;
    private TreeFrame treeEditor;
    private boolean isMac, isWindows;
    private Font defaultFont;
    private boolean downloadComplete;
    private String fileName;
    private String userName;
    private String password;
    //private int editingRootNodeId;
    private boolean useCustomCursors;
    private long editorJarTimestamp;    
    private long supportJarTimestamp;    
    private String database;
    private PreferencesFrame prefFrame;
    private ToLManagerFrame managerFrame;
    private int depth;
    private int downloadId;
    private int uploadId = -1;
    private int batchId = -1;
    private boolean canPushPublic = false;
    private boolean fileChanged;
    private String editorBatchId = null;
    private String ancestors;
    private ArrayList ancestorList;
    /**
     * The list of contributors that can be edited and given permissions
     */
    private Vector editableContributorList;
    private int nodeId;
    //private PageFrame activePageFrame;
    private DataDownloader.FullDownloadControllerThread fullDownloadThread;
    private DataDownloader.SubtreeDownloadControllerThread subtreeDownloadThread;
    private CancelDownloadFrame cancelFrame;
    private boolean hasConnection = true;
    private boolean exportDone = false;
    
    /**
    *Used to allow "cut" and "paste" from menu to work with undo/redo. 
    *
    * @see setMenuCutting
    */
    private boolean menuCuttingPasting = false;

    private boolean warnAboutNodeDeletion = true; 
    private boolean warnAboutPageDeletion = true;
	private String obsoleteMessage;

    
    static {
        controller = new Controller();
        Browser.init();   
    }
    
    /** Creates a new instance of Controller */
    public Controller() {
        dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        ancestorList = new ArrayList();
        editableContributorList = new Vector();
        preferenceManager = new PreferenceManager();
        fileManager = new FileManager(this);
        String osName = System.getProperty("os.name").toUpperCase();
        isMac = osName.indexOf("MAC") > -1 ;
        isWindows = osName.indexOf("WIN") > -1;
        Font arialFont = Font.getFont("Arial");
        if (arialFont == null) {
            defaultFont = new Font("sansserif", Font.PLAIN, AbstractTreePanel.DEFAULT_FONT_SIZE);
        } else {
            defaultFont = new Font("Arial", Font.PLAIN, AbstractTreePanel.DEFAULT_FONT_SIZE);
        }
        try {
            lightbulbIcon = new ImageIcon(ClassLoader.getSystemResource("Images/" + LIGHTBULB_IMG_NAME));
        } catch (Exception e) {
        }
        ToolTipManager.sharedInstance().setDismissDelay(1000000000);
    }
    
    public void showFirstTimeUsersDialog() {
        String FIRST_TIME_TOOLTIP = getMsgString("FIRST_TIME_TOOLTIP");
        JOptionPane.showMessageDialog(getTreeEditor(), FIRST_TIME_TOOLTIP, "Hint for First-Time TreeGrow Users", JOptionPane.INFORMATION_MESSAGE, lightbulbIcon);
    }
    
    /**
     * Returns the file manager for this controller
     *
     * @return the file manager for this controller
     */
    public FileManager getFileManager() {
        return fileManager;
    }
    
    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
    
    /**
     * Returns the fileName of the currently open tree.  This reflects the fileName
     * according to what is sent down from the server
     * 
     * @return The fileName of the currently open tree
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Sets the filename of the open tree
     *
     * @param value The filename of the xml file containing the tree
     */
    public void setFileName(String value) {
        fileName = value;
    }

    /**
     * Sets the username of the user editing the tree
     *
     * @param user The username
     */
    public void setUserName(String user) {
        preferenceManager.setUserName(user);
    }

    /**
     * Returns the username of the user editing the tree
     *
     * @return username
     */
    public String getUserName() {
        return preferenceManager.getUserName();
    }
    
    public Tree getTree() {
        if (getTreeEditor() != null) {
            return getTreeEditor().getTree();
        } else {
            return null;
        }
    }
    
    /**
     * Sets the MD5 hash of the user's password.  This is stored locally
     *
     * @param pw TheMD5 hash of the password
     */
    public void setPassword(String pw) {
        preferenceManager.setPassword(pw);
    }
    
    /**
     * Returns the local stored password, which is an MD5 of the pw on server
     *
     * @return the local stored password
     */
    public String getPassword() {
        return preferenceManager.getPassword();
    }
    
    /**
     * Method to open a window that allows a user to cancel their download.
     *
     * @param The FullDownloadThread that is running the download
     */
    public void openCancelWindow(DataDownloader.FullDownloadControllerThread thread) {
        fullDownloadThread = thread;
        cancelFrame = new CancelDownloadFrame();
        if (managerFrame != null) {
            setLocationRelativeTo(cancelFrame, managerFrame);
        }
        cancelFrame.toFront();
    }
    
    /**
     * Method to open a window that allows a user to cancel their download.
     *
     * @param The SubtreeDownloadThread that is running the download
     */
    public void openCancelWindow(DataDownloader.SubtreeDownloadControllerThread thread) {
        subtreeDownloadThread = thread;
        cancelFrame = new CancelDownloadFrame();
        setLocationRelativeTo(cancelFrame, treeEditor);
        cancelFrame.toFront();
    }
    
    /**
     * Cancels the current download
     */
    public void cancelDownload() {
        if (fullDownloadThread != null) {
            cancelFullDownload();
        } else {
            cancelSubtreeDownload();
        }
    }
    
    /**
     * Returns the currently open CancelDownloadFrame
     *
     * @return The currently open CancelDownloadFrame
     */
    public CancelDownloadFrame getCancelWindow() {
        return cancelFrame;
    }
    
    /**
     * Implements the setLocationRelativeTo method available in jdk1.4 but 
     * not 1.3
     *
     * @param toSetLocation The window to position 
     * @param relative The window to use as the relative positioning
     */
    private void setLocationRelativeTo(Window toSetLocation, Window relative) {
        int newX = relative.getX() + (relative.getWidth() - toSetLocation.getWidth()) / 2;
        int newY = relative.getY() + (relative.getHeight() - toSetLocation.getHeight()) / 2;
        toSetLocation.setLocation(newX, newY);
    }
    
    /**
     * Cancels a full download, which opens the file manager back up
     */
    private void cancelFullDownload() {
        fullDownloadThread.cancelDownload();
        if (managerFrame != null) {
            managerFrame.getDownloadPanel().setCursor(Cursor.getDefaultCursor());
            managerFrame.setCursor(Cursor.getDefaultCursor());
        } else if (treeEditor != null) {
            treeEditor.dispose();
            openFileManager();
        }
        cancelFrame.dispose();
        cancelFrame = null;
        fullDownloadThread = null;
    }
    
    /**
     * Cancels a subtree download, which just goes back to the tree editor
     */
    private void cancelSubtreeDownload() {
        subtreeDownloadThread.cancelDownload();
        treeEditor.setDefaultCursors();
        cancelFrame.dispose();
        cancelFrame = null;
        subtreeDownloadThread = null;
    }

    /**
     * Method that's called to signify that whatever was downloading or reading
     * in from file has either just started or finished and that editing can 
     * commence or should be disallowed (depending on value).
     *
     * @param value  A boolean indicating whether downloading has started or finished
     */
    public void setDownloadComplete(boolean value) {
        downloadComplete = value;
        if (downloadComplete) {
            if (cancelFrame != null) {
                cancelFrame.dispose();
                cancelFrame = null;
            }
            fullDownloadThread = null;
            subtreeDownloadThread = null;
            if (treeEditor != null) {
                System.out.println("setting auto save thread on the tree frame");
                AutoSaveThread autoSaveThread = new AutoSaveThread(); 
                treeEditor.setAutoSaveThread(autoSaveThread);
                autoSaveThread.start();                
                treeEditor.setDefaultCursors();
                TreePanel.getTreePanel().rebuildTree();
                Iterator it = TreePanel.getTreePanel().getTree().getNodeList().iterator();
                while (it.hasNext()) {
                    Node node = (Node) it.next();
                    if (node.getCheckedOut()) {
                        node.setDownloadComplete(true);
                    }
                }
            } else if (managerFrame != null) {
                managerFrame.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    /**
     * Returns whether or not the current download is complete
     *
     * @return whether or not the current download is complete
     */
    public boolean getDownloadComplete() {
        return downloadComplete;
    }

    /**
     * Sets the wait cursors on all open windows
     */
    public void setWaitCursors() {
        if (getTreeEditor() != null) {
            getTreeEditor().setWaitCursors();
        }
    }

    /**
     * Sets the default (arrow) cursor on all open windows
     */
    public void setDefaultCursors() {
        if ( getTreeEditor()  != null ) {
            getTreeEditor().setDefaultCursors();
        }
    }
    
    /**
     * Sets the node id of the controller.  This should be the id of the root
     * node of the current tree.
     *
     * @param value The new node id.
     */
    public void setNodeId(int value) {
        nodeId = value;
    }
    
    /**
     * Returns the node id of the root of the current tree.
     *
     * @return the node id of the root of the current tree.
     */
    public int getNodeId() {
        // If a tree exists, grab the root and get its id, otherwise use
        // the value set from the XMLReader
        if (treeEditor != null) {
            return TreePanel.getTreePanel().getTree().getRoot().getId();
        }
        return nodeId;
    }
    
    /**
     * Sets the database value for the controller.  This is used in order to
     * form correct URLs for download.
     *
     * @param value The db string, should be one of "live", "dev", or "beta"
     */
    public void setDatabase(String value) {
        database = value;
    }
    
    /**
     * Returns the database currently used
     *
     * @return The database currently used
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Returns the path to the home page of the db that is currently used
     *
     * @return the path to the home page of the db that is currently used
     */
    public String getWebPath() {
        return "http://"+database+".tolweb.org/";
    }    
    
    /**
     * Returns the path to the cgi-bin of the db that is currently used
     *
     * @return the path to the cgi-bin of the db that is currently used
     */
    public String getCGIPath() {
        return "http://"+database+".tolweb.org/cgi-bin/";
    }
    
    public String getExternalServicePrefix() {
        return getExternalServicePrefix("treegrowserver", false, false);
    }
    
    public String getPageServicePrefix() {
        return getServicePrefix("treegrowserver", false, "page", false);
    }
    
    public String getExternalFrontendServicePrefix() {
        return getExternalServicePrefix("onlinecontributors", false, false);
    }
    
    private String getExternalServicePrefix(String applicationName, boolean includePort, boolean isTap4) {
        return getServicePrefix(applicationName, includePort, "external", isTap4);
        //return "http://localhost:8080/" + applicationName + "/app?service=external/";
    }
    
    private String getServicePrefix(String applicationName, boolean includePort, String serviceName, boolean isTap4) {
        String portString = includePort ? ":8080" : "";
        String hostString = StringUtils.notEmpty(database) ? database + "." : "";
        String urlString = "http://" + hostString + "tolweb.org" + portString + "/" + applicationName + "/app?service=" + serviceName;
        if (isTap4) {
        	urlString += "&page=";
        } else {
        	urlString += "/";
        }
        return  urlString;
    }
    
    public String getTap4ExternalFrontendServicePrefix() {
    	return getExternalServicePrefix("onlinecontributors", false, true);
    }
    
    /**
     * Returns the path to the "working" version of the homepage
     *
     * @return the path to the "working" version of the homepage
     */
    public String getWorkingPath() {
        return "http://working."+database+".tolweb.org/";
    }
    
    public String getWorkingCGIPath() {
        return "http://working." + database + ".tolweb.org/cgi-bin/";
    }
    
    public String getApprovalCGIPath() {
        return "http://approval." + database + ".tolweb.org/cgi-bin/";    
    }
    
    /**
     * Returns the path to the "approval" version of the homepage
     *
     * @return the path to the "approval" version of the homepage
     */
    public String getApprovalPath() {
        return "http://approval."+database+".tolweb.org/";
    }
    
    /**
     * Sets the page depth, which is associated with the current tree.
     *
     * @param value The new page depth value.
     */
    public void setDepth(int value) {
        depth = value;
    }
    
    /**
     * Returns the depth of the tree
     *
     * @return the depth of the tree
     */
    public int getDepth() {
        return depth;
    }
    
    /**
     * Sets the batch id of the upload batch that an editor is editing.  This is
     * only used if the editor is editing a node that is part of an already
     * submitted upload batch
     *
     * @param value The batch id of the upload batch that an editor is editing
     */
    public void setEditorBatchId(String value) {
        editorBatchId = value;
    }
    
    /**
     * Returns the batch id of the upload batch an editor is editing.  This is
     * only used if the editor is editing a node that is part of an already
     * submitted upload batch
     *
     * @return The batch id of the upload batch an editor is editing
     */
    public String getEditorBatchId() {
        return editorBatchId;
    }
    
    /**
     * Sets the file changed property
     *
     * @param value Whether the file has changed from server or not
     */
    public void setFileChanged(boolean value) {
        fileChanged = value;
    }
    
    /**
     * Returns whether there have been any changes to the file.  This will be
     * true in 2 cases:<ul><li>The user made changes in the current session</li>
     *<li>The user changed something in a previous session and wrote out to file
     *</li></ul>
     *
     * @return true if any changes from the server have been made, false otherwise
     */
    public boolean getFileChanged() {
        boolean pageHasChanges = false;       
        Node rootNode = getTree() != null ? getTree().getRoot() : null;
        
        return fileChanged || (TreePanel.getTreePanel() != null &&
            TreePanel.getTreePanel().getUndoManager().canUndo()) || pageHasChanges || contributorsChanged() || imagesChanged()
            || rootNode != null && getFileChanged(rootNode);
    }
    
    /**
     * Checks to see if any node rooted at some node has changed
     *
     * @param root The subtree root to check
     * @return true if any value in the subtree has changed, false otherwise
     */
    public boolean getFileChanged(Node root) {
        ArrayList nodesToCheck = new ArrayList();
        nodesToCheck.add(root);
        nodesToCheck.addAll(root.getSubtreeNodes());
        Iterator it = nodesToCheck.iterator();
        boolean somethingChanged = false;
        while (it.hasNext()) {
            Node nextNode = (Node) it.next();
            if (nextNode.getChanged()) {
                somethingChanged = true;
                break;
            }
        }
        return somethingChanged || fileChanged;
    }
    
    public boolean getNodeOneDeepChanged(Node root) {
        Stack stack = new Stack();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node node = (Node) stack.pop();
            if (node.getChanged()) {
                return true;
            }
            Iterator it = node.getChildren().iterator();
            while (it.hasNext()) {
                Node next = (Node) it.next();
                if (!next.hasPage()) {
                    stack.push(next);
                }
            }
        }
        return false;
    }
    
    /**
     * Sets the download id of the file
     *
     * @param value The download id
     */
    public void setDownloadId(int value) {
        downloadId = value;
    }
    
    /**
     * Returns the download id of the file
     *
     * @return the download id of the file
     */
    public int getDownloadId() {
        return downloadId;
    }
    
    /**
     * Sets the id of the last upload, which enables the undoUpload menu item
     * on all page editors and the tree editor
     *
     * @param value The new upload id
     */
    public void setUploadId(int value) {
        uploadId = value;
    }
    
    /**
     * Returns the id of the last upload that occurred, needed in order to undo
     * it if that's possible
     *
     * @return The id of the last upload that occurred
     */
    public int getUploadId() {
        return uploadId;
    }
    
    /**
     * Sets the batch id of the most recently uploaded file
     *
     * @param value The new batch id
     */
    public void setBatchId(int value) {
        batchId = value;
    }
    
    /**
     * Returns the batch id of the most recently uploaded file
     *
     * @param value The new batch id
     */    
    public int getBatchId() {
        return batchId;
    }
    
    public void setCanPushPublic(boolean value) {
        canPushPublic = value;
    }
    
    public boolean canPushPublic() {
        return canPushPublic;
    }
    
    /**
     * Sets all nodes rooted at root changed to false
     *
     * @param root The root node to start at
     */
    public void setChangedToFalse(Node root) {
        // set all the changed values to false once saving is done
        ArrayList nodes = root.getSubtreeNodes();
        nodes.add(root);
        Iterator it = nodes.iterator();
        while(it.hasNext()) {
            Node tempNode = (Node)it.next();
            tempNode.setChangedToFalse();
        }
        if (TreePanel.getTreePanel() != null && root == TreePanel.getTreePanel().getTree().getRoot()) {
            fileChanged = false;
        }
    }
    
    /**
     * Gets rid of all current edits on the undo stacks
     */
    public void clearAllEdits() {
        TreePanel panel = TreePanel.getTreePanel();
        panel.getUndoManager().discardAllEdits();
        panel.getUndoAction().updateUndoState();
        panel.getRedoAction().updateRedoState();
    }

    
    /**
     * Go through and check to see if there are any links to the accessory
     * page that is going to be removed.  If there are, warn the user and
     * let them know they cannot be undeleted.  If they still decide to do this,
     * delete the accessory page and any other accessory pages that previously
     * linked to it.
     *
     * @param page The Accessory page to delete
     * @param doPrompt Whether to actually prompt the user or just do it
     /
    public boolean removeAccessoryPageFromLinkComboBoxes(AccessoryPage page, boolean doPrompt) {
        ArrayList linkedAccPagesNodes = TreePanel.getTreePanel().getTree().getLinkedAccPagesNodes(page);
        if (doPrompt) {
            if (linkedAccPagesNodes.size() > 0) {
                String DELETE_LINKED_ACCESSORY_PAGES = getMsgString("DELETE_LINKED_ACCESSORY_PAGES");
                int response = JOptionPane.showConfirmDialog(treeEditor, DELETE_LINKED_ACCESSORY_PAGES, "Continue?", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
        }
        Iterator it = linkedAccPagesNodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            PageFrame frame = getPageEditor(node);
            if (frame != null) {
                frame.removeLinkedAccPage(page);
            } else {
                // There isn't a page editor open, so manually remove the 
                // linked accessory pages
                Page pageObj = node.getPageObject();
                Vector accPages = pageObj.getAccessoryPages();
                Iterator it2 = new ArrayList(accPages).iterator();
                while (it2.hasNext()) {
                    AccessoryPage accPage = (AccessoryPage) it2.next();
                    if (!accPage.useContent() && accPage.getLinkedAccPage() == page) {
                        accPages.remove(accPage);
                        pageObj.setAccessoriesChanged(true);
                    }
                }
            }
        }

        return true;
    }
    
    public boolean removeAccessoryPageFromLinkComboBoxes(AccessoryPage page) {
        return removeAccessoryPageFromLinkComboBoxes(page, true);
    }*/  

    /**
     * function to save the changes (called when the application is closed or 
     * when the tree editor is closed)
     */
    public void saveChanges() {
        FileXMLWriter writer = new FileXMLWriter();
        writer.writeXML();
    }
    
    /**
     * Returns the default font used throughout the application
     *
     * @return the default font used throughout the application
     */
    public Font getDefaultFont() {
        return defaultFont;
    }
    
    /**
     * Returns the user message from the XML messages file associated with the 
     * passed-in messageName.
     *
     * @param msg The name of the message to return
     * @return The user message
     */
    public String getMsgString(String msg) {
        if(msg == null) {
            return null;
        }
        String msgText = getFileManager().getMessageForName(msg);
        
        return replaceNewLine(msgText);
    }
    
    /**
     * Function that returns the user message associated with the passed in
     * message name.  The vector full of strings is used to replace substrings 
     * of the message (as denoted in the XML file) demarcated by $% and %$.
     * 
     * @param msgName The name of the message to look for
     * @param replace The Vector of replacement objects
     * @return The message string with replacements inserted
     */
    public String getMsgString(String msgName, Vector replace) {
        if(msgName == null) {
            return null;
        }

        if(replace == null) {
            return getMsgString(msgName);
        }

        String msgText = getFileManager().getMessageForName(msgName);
        String startPattern = "$%";
        String endPattern = "%$";
        msgText = replaceWithText(startPattern, endPattern, msgText, replace);

        return msgName + "\n" + replaceNewLine(msgText);
    }


    /**
     * Does substitution of a message string with the values in the vector
     * serving as parameters to substitute
     */
    public String replaceWithText(String start, String end, String message, Vector values) {
        if(message == null) {
            return null;
        }

        if(values == null) {
            return message;
        }

        String msg = message;
        String outputStr = "";

        //get the number of start patterns
        int startSize = 0;
        int pos = -1;
        while((pos = msg.indexOf(start)) != -1) {
                startSize++;
                if(msg.length() > (pos + start.length())) {
                        msg = msg.substring(pos + start.length());
                } else {
                        break;
                }
        }

        msg = message;
        int endSize = 0;
        pos = -1;
        while((pos = msg.indexOf(end)) != -1) {
                endSize++;
                if(msg.length() > (pos + end.length())) {
                        msg = msg.substring(pos + end.length());
                }
                else {
                        break;
                }
        }

        if(startSize != endSize) {
                return message;
        }

        if(values.size() < startSize) {
                return message;
        }

        int startPos = -1;
        int endPos = -1;
        int tempVecSize = 0;

        msg = message;
        while(startSize > 0) {
                startPos = msg.indexOf(start);
                endPos = msg.indexOf(end);

                outputStr = outputStr + msg.substring(0,startPos);

                String tempStr = (String)values.elementAt(tempVecSize);
                tempVecSize++;
                outputStr = outputStr + tempStr;

                msg = msg.substring(endPos + end.length());
                startSize--;
        }
        if((msg != null) || msg.equals("")) {
                outputStr = outputStr + msg;
        }

        return outputStr;
    }    
    
    /**
     * Replaces the newlines in the user message -- should be able to be
     * replaced?!?  No longer used...
     *
     * @param message The original string
     * @return The string with \n replaced with actual newline characters
     */
    private String replaceNewLine(String message) {
        if(message == null) {
            return null;
        }
        String start = "\\n";
        String msg = message;
        String outputStr = "";

        //get the number of start patterns
        int pos = -1;
        while((pos = msg.indexOf(start)) != -1) {
            if(msg.length() > (pos + start.length())) {
                outputStr = outputStr + msg.substring(0,pos) + "\n";
                msg = msg.substring(pos + start.length());
            } else {
                if(msg != null) {
                    outputStr = outputStr + msg;
                }
                break;
            }
        }
        if(msg != null) {
            outputStr = outputStr + msg;
        }
        return outputStr;
    }    
    
    /**
     * Convenience method for determining whether we are running on a Mac
     *
     * @return true if running on Mac, false otherwise
     */
    public boolean isMac() {
        return isMac;
    }
    
    /**
     * Convenience method for determining whether we are running on Windows
     *
     * @return true if running on Windows, false otherwise
     */
    public boolean isWindows() {
        return isWindows;
    }
    
    /**
     * Returns the currently open TreeFrame.
     *
     * @return The currently open TreeFrame
     */
    public TreeFrame getTreeEditor() {
        return treeEditor;
    }
    
    /**
     * Method to call in order to set the status Message in the Tree Editor.
     * The status message is located at the bottom of the TreeFrame.
     *
     * @param value The message to set as the current status message
     */
    public void setStatusMessage(String value) {
        if (getTreeEditor() != null && getTreeEditor().isVisible()) {
            getTreeEditor().setStatusMesg(value);
        }
        if (cancelFrame != null) {
            cancelFrame.updateStatus(value);
        }
    }

    
    /**
     * Used to either fetch preference information if it exists, or pop up
     * a dialog asking the user for their preferences if it doesn't.
     *
     * @return true normally, false if there is a new version with XML changes
     *         and the user has local files to check out.  This signifies that
     *         the Upload, Preview, Submit tab in the ManagerFrame should be
     *         selected
     */
    public boolean getUserDetails() {
        boolean returnVal = true;
        if(!fileManager.configFileExists()) {
            fileManager.createConfigFile();          
            showPreferencesWindow();
            spinOnPreferences();
        } else {
            preferenceManager.fetchConfigInfo();

            NewVersionDownloader downloader = new NewVersionDownloader();
            returnVal = downloader.fetchNewVersion();
            //fetchEditingRootNodeId();
            spinOnPreferences();
        }

        return returnVal;
    }
    
    private void spinOnPreferences() {
        // Spin until the window is closed -- i.e. they have been authenticated
        while (prefFrame != null) {
            try {
                Thread.sleep(250);
            } catch (Exception e) {
            }
        }            
    }
    
    /**
     * Sets the list of ancestors
     *
     * @param value The list of ancestors to use
     */
    public void setAncestorList(ArrayList value) {
        ancestorList = value;
    }
    
    /**
     * Returns the ancestor with the passed-in node id
     *
     * @param id The node id of the ancestor
     * @return The ancestor object
     */
    public Ancestor getAncestor(int id) {
        Iterator it = ancestorList.iterator();
        while (it.hasNext()) {
            Ancestor anc = (Ancestor)it.next();
            if (anc.getNodeID() == id) {
                return anc;
            }
        }
        return null;
    }
    
    /**
     * Returns a list of all ancestors that have accessory pages
     *
     * @return a list of all ancestors that have accessory pages
     */
    public Vector getAncestorsWithAccessoryPages() {
        Iterator it = ancestorList.iterator();
        Vector ancestorsWithAccPages = new Vector();
        while (it.hasNext()) {
            Ancestor anc = (Ancestor) it.next();
            Vector accPages = anc.getAccPages();
            if (accPages.size() > 0) {
                ancestorsWithAccPages.add(anc);
            }
        }
        return ancestorsWithAccPages;
    }
    
    /**
     * Returns all of ancestors of the tree
     *
     * @return A list of all the ancestors of the tree
     */
    public ArrayList getAncestors() {
        return ancestorList;
    }

    /**
     * Returns the Ancestor accessory page for the passed-in node and page ids
     *
     * @param nodeId The node id of the ancestor
     * @param accPageId The id of the accessory page
     * @return The Ancestor Accesssory Page object
     */
    public Ancestor.AncestorAccPage getAncestorAccPage(int nodeId, int accPageId) {
        Ancestor anc = getAncestor(nodeId);
        if (anc != null) {
            Iterator it = anc.getAccPages().iterator();
            while (it.hasNext()) {
                Ancestor.AncestorAccPage accPage = (Ancestor.AncestorAccPage)it.next();
                if (accPage.getId() == accPageId) {
                    return accPage;
                }
            }
        } else {
            // It's possible this is a subtree download so return the actual accessory page
            //Node node = TreePanel.getTreePanel().getTree().findNode(nodeId);
            
        }
        return null;
    }
    
    public void addToEditableContributors(Contributor value) {
        /*try {
            throw new RuntimeException();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        editableContributorList.add(value);
    }
    
    public void mergeEditableContributors(java.util.List values) {
        Hashtable hash = new Hashtable();
        Iterator it = editableContributorList.iterator();
        while (it.hasNext()) {
            Contributor contr = (Contributor) it.next();
            hash.put(new Integer(contr.getId()), "");
        }
        it = values.iterator();
        while (it.hasNext()) {
            Contributor contr = (Contributor) it.next();
            if (!hash.containsKey(new Integer(contr.getId()))) {
                editableContributorList.add(contr);
            }
        }
    }
    
    public void removeFromEditableContributors(Contributor value) {
        editableContributorList.remove(value);
    }
    
    public Vector getEditableContributors() {
        return editableContributorList;
    }
    
    public Contributor getEditableContributor(int id) {
        if (id == Contributor.BLANK_CONTRIBUTOR.getId()) {
            return Contributor.BLANK_CONTRIBUTOR;
        }
        Iterator it = editableContributorList.iterator();
        while (it.hasNext()) {
            Contributor contr = (Contributor) it.next();
            if (contr.getId() == id) {
                return contr;
            }
        }
        return null;
    }
    
    public Vector getChangedEditableContributors() {
        Vector vec = new Vector();
        Iterator it = editableContributorList.iterator();
        while (it.hasNext()) {
            Contributor contr = (Contributor) it.next();
            if (contr.changedFromServer()) {
                vec.add(contr);
            }
        }
        return vec;
    }
    
    public Vector getSortedNonLocalEditableContributors() {
        Vector vec = new Vector();
        Iterator it = editableContributorList.iterator();
        while (it.hasNext()) {
            Contributor contr = (Contributor) it.next();
            if (contr.getId() > 0) {
                vec.add(contr);
            }
        }
        Collections.sort(vec, new Contributor.ContributorComparator());
        return vec;
    }
    
    public Vector getSortedEditableContributors() {
        Vector vec = new Vector(editableContributorList);
        Collections.sort(vec, new Contributor.ContributorComparator());
        vec.add(0, Contributor.BLANK_CONTRIBUTOR);
        return vec;
    }
    
    public Vector getSortedEditableContributorsNoBlank() {
        Collections.sort(editableContributorList, new Contributor.ContributorComparator());
        return editableContributorList;
    }    
    
    public void setEditableContributors(Vector value) {
        editableContributorList = value;
    }
    
    /*public void refreshContributorBoxes() {
        Iterator it = getPageEditorIterator();
        while (it.hasNext()) {
            PageFrame frame = (PageFrame) it.next();
            CreditsPanel creditsPanel = frame.getCreditsPanel();
            if (creditsPanel != null) {
                creditsPanel.getTable().refreshContributorEditorBoxes();
            }
            NodeImagesPanel imgsPanel = frame.getNodeImagesPanel();
            /*if (imgsPanel != null) {
                imgsPanel.getDetailsPanel().refreshContributorsBox();
            }/
        }
        /*if (imagesFrame != null) {
            imagesFrame.getDetailsPanel().refreshContributorsBox();
        }/
    }*/
    
    public void showAboutDialog() {
        String aboutString = "<html>TreeGrow Version " + VERSION + " developed by the Tree of Life home team:<ul>" +
            "<li>David Maddison</li><li>Katja Schulz</li><li>Danny Mandel</li><li>Travis Wheeler</li></ul></html>";
        JFrame frame;
        if (managerFrame != null) {
            frame = managerFrame;
        } else {
            frame = treeEditor;
        }
        JOptionPane.showMessageDialog(frame, aboutString, "About TreeGrow", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Used for returning the minimum contributor id of all the current
     * contributors
     *
     * @return The minimum id of all the contributors
     */
    public int getMinContributorId() {
        Iterator it = editableContributorList.iterator();
        int minId = 0;
        while (it.hasNext()) {
            Contributor contr = (Contributor) it.next();
            if (contr.getId() <  minId) {
                minId = contr.getId();
            }
        }
        return minId;
    }
    
    public boolean imagesChanged() {
        if (TreePanel.getTreePanel() != null) {
            return TreePanel.getTreePanel().getTree().getChangedNodeImages().size() > 0;
        } else {
            return false;
        }
    }
    
    public boolean contributorsChanged() {
        Iterator it = editableContributorList.iterator();
        while (it.hasNext()) {
            Contributor contr = (Contributor) it.next();
            if (contr.changedFromServer()) {
                return true;
            }
        }
        return false;   
    }
    
    /**
     * Queries the server for the user's editing rootnode_id
     /
    private void fetchEditingRootNodeId() {
        CheckNetConnection checker = new CheckNetConnection();
        if (checker.isConnected() <= 0) {
            editingRootNodeId = -1;
            hasConnection = false;
            return;
        }
        try {
            byte[] bytes = HttpRequestMaker.makeHttpRequest(getExternalServicePrefix() + "TreeGrowServerUtils&user_id=" + getUserName() + "&password=" + getPassword() + "&" + RequestParameters.OPTYPE + "=" + RequestParameters.EDITING_ROOTNODE_ID);
            if (bytes == null) {
                throw new RuntimeException();
            }
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new ByteArrayInputStream(bytes));
            Element rootElmt = doc.getRootElement();
            boolean result = UserValidationChecker.checkValidation(rootElmt);
            if (!result) {
                throw new RuntimeException();
            } else {
                editingRootNodeId = Integer.parseInt(rootElmt.getAttributeValue(XMLConstants.ROOTNODE_ID));
                System.out.println("editingRootNodeId=" + editingRootNodeId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            String SERVER_PROBLEMS = controller.getMsgString("SERVER_PROBLEMS");
            JOptionPane.showMessageDialog(null, SERVER_PROBLEMS, "Server Problems", JOptionPane.ERROR_MESSAGE);
            editingRootNodeId = -1;
        }
    }*/
    
    /**
     * Opens the preferences window
     */
    public void showPreferencesWindow() {
        prefFrame = null;
        prefFrame = new PreferencesFrame("User Information");
        prefFrame.setLocation(50,50);
        prefFrame.setSize(400,150);
        prefFrame.setVisible(true);
    }
    
    /** 
     * Returns the preferencesFrame
     *
     * @return the preferencesFrame
     */
    public PreferencesFrame getPreferencesFrame() {
        return prefFrame;
    }
    
    public void openFileManager() {
        openFileManager(false);
    }

    /**
     * Opens the file manager, or asks for a password if the user isn't 
     * validated
     * 
     * @param doSubmit Whether to automatically submit the previously open file
     * for publication
     */
    public void openFileManager(final boolean doSubmit) {
        if((getUserName() == null) || (getUserName().equals(""))) {
            showPreferencesWindow();
            return;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                managerFrame = new ToLManagerFrame(doSubmit);
                managerFrame.setLocation(50,50);
                managerFrame.setSize((int) managerFrame.getSize().getWidth(),500);
                managerFrame.setVisible(true);
                managerFrame.toFront();
                managerFrame.requestFocus();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }
    
    public void submitBatch(UploadBatch ub) {
        boolean result = ub.doSubmit();
        if (!result) {
            String SUBMIT_ERROR = controller.getMsgString("SUBMIT_ERROR");
            JOptionPane.showMessageDialog(getManagerFrame(), SUBMIT_ERROR, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            Vector name = new Vector();
            name.add(ub.rootGroup);
            String SUBMIT_SUCCESS = controller.getMsgString("SUBMIT_SUCCESS", name);
            JOptionPane.showMessageDialog(getManagerFrame(), SUBMIT_SUCCESS, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        getManagerFrame().setCursor(Cursor.getDefaultCursor());
    }
    
    /*private void submitLastOpenBatch() {
        // Get the upload batch object from the server and submit it.
        Hashtable hash = new Hashtable();
        int downloadId = Controller.getController().getDownloadId();
        hash.put(RequestParameters.DOWNLOAD_ID, "" + downloadId);
        String url = HttpRequestMaker.getServerUtilsUrlString(RequestParameters.UPLOAD_BATCH, hash);
        byte[] bytes = HttpRequestMaker.makeHttpRequest(url);
        try {
	        Element root = new SAXBuilder().build(new ByteArrayInputStream(bytes)).getRootElement();
	        if (root != null) {
	            UploadBatch ub;
	            if (root.getName().equals(XMLConstants.UPLOAD_BATCH)) {
	                ub = TreeGrowXMLReader.getUploadBatchFromElement(root);
	                submitBatch(ub);
	            }
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }     
    }*/
    
    /**
     * Returns the currently open ManagerFrame
     *
     * @return the currently open ManagerFrame
     */
    public ToLManagerFrame getManagerFrame() {
        return managerFrame;
    }
    
    /**
     * Sets the ToLManagerframe
     *
     * @param value The frame to set as the filemanager
     */
    public void setManagerFrame(ToLManagerFrame value) {
        managerFrame = value;
    }
    
    public void setExportDone(boolean value) {
        exportDone = value;
    }
    
    public boolean exportDone() {
        return exportDone;
    }

    /**
     * Opens the tree frame from a file open 
     *
     * @param tree the tree to open
     */
    public void openTreeEditor(Tree tree) {
        openTreeEditor(tree, false, true);
    }

    /**
     * Opens the tree frame
     *
     * @param reader The XML reader from which to construct the tree
     * @param showEditor Whether the editor actually gets shown
     */
    public void openTreeEditor(TreeGrowXMLReader reader, boolean showEditor) {
        editableContributorList = new Vector(reader.getEditableContributors());
        Tree tree = new Tree(reader.getTreeStructure(), new ArrayList(reader.getNodeList()));
        tree.setDownloadDate(reader.getDownloadDate());
        tree.setUploadDate(reader.getUploadDate());
        tree.setModifiedDate(reader.getModifiedDate());
        tree.setImages(reader.getImageList());
        openTreeEditor(tree, true, showEditor);
    }        
    
    /**
     * Opens the tree frame
     *
     * @param tree The tree to edit
     * @param isDownload Whether it is during a download or file open
     * @param showEditor Whether the editor actually gets shown
     */
    public void openTreeEditor(Tree tree, boolean isDownload, boolean showEditor) {
        treeEditor = getTreeEditor();
        Dimension maxSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(treeEditor == null) {
            treeEditor = new TreeFrame(tree, isDownload);
            treeEditor.setLocation(30,0);

            Dimension treeSize = TreePanel.getTreePanel().getSize();
            int frameWidth = treeSize.width + 70;
            int frameHeight = treeSize.height+ 40;

            int maxWidth = (int)(.75*maxSize.width);
            if ( frameWidth > maxWidth ){
                frameWidth = maxWidth ;
            }
            int maxHeight = (int)(.75*maxSize.height);
            if ( frameHeight > maxHeight ){
                frameHeight =  maxHeight ;
            }       
            treeEditor.setSize(frameWidth, frameHeight);
        }
        treeEditor.setVisible(showEditor);
        treeEditor.toFront();
        if(managerFrame != null && showEditor) {
            managerFrame.dispose();
            managerFrame = null;
        }
        if (isDownload) {
            if (treeEditor.isVisible()) {
                cancelFrame.toFront();
                setLocationRelativeTo(cancelFrame, treeEditor);
            }
        } else {
            // Not a download, so check the status of existing local nodes
            // and update them with fresh values from the server
            getUpdatedNodes();
            TreePanel.getTreePanel().rebuildTree();
        }
        Dimension preferredSize = TreePanel.getTreePanel().getPreferredSize();
        treeEditor.setSize(new Dimension((int) (Math.min(maxSize.getWidth() - 30, preferredSize.getWidth() + 100)), 
                                        (int) (Math.min(maxSize.getHeight()-30, preferredSize.getHeight() + 50))));
    }
    
    /**
     * Checks the status of local nodes that we don't have checked out that may 
     * have been updated by someone else and changes their statuses accordingly
     * Cover for the other method that uses the currently open tree
     */
    public void getUpdatedNodes() {
        getUpdatedNodes(TreePanel.getTreePanel().getTree());
    }
    
    /**
     * Checks the status of local nodes that we don't have checked out that may 
     * have been updated by someone else and changes their statuses accordingly
     */
    public void getUpdatedNodes(Tree tree) {
        Iterator it = tree.getNodeList().iterator();
        boolean anyNodesToCheck = false;
        ArrayList nodeIds = new ArrayList();
        while (it.hasNext()){
            Node node = (Node) it.next();
            if ( !node.getCheckedOut() || node.getLocked()) {
                nodeIds.add(new Integer(node.getId()));
                anyNodesToCheck = true;
            }
        }
        String idsToCheck = StringUtils.returnCommaJoinedString(nodeIds);
        idsToCheck = StringUtils.removeSpaces(idsToCheck);
        //idsToCheck = idsToCheck.replaceAll(" ", "");
        String queryString = getExternalServicePrefix() + "NodeSearch&" + RequestParameters.CHECKED_IDS + "=" + idsToCheck;
        // Only bother looking if there are nodes to check on
        if (anyNodesToCheck) {
            Element rootElmt = null;
            try {
                URL url = new URL(queryString + "&user_id=" + getUserName() + "&password=" + getPassword());
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(url);
                rootElmt = doc.getRootElement();
            } catch(Exception e) {
                e.printStackTrace();
                return;
            }

            it = rootElmt.getChildren(XMLConstants.MATCH).iterator();
            while (it.hasNext()){
                Element matchElmt = (Element) it.next();
                String idStr = matchElmt.getAttributeValue(XMLConstants.ID);
                if (idStr != null) {
                    int id = new Integer(idStr).intValue();
                    Node node = tree.findNode(id,tree.getNodeList());


                    String pageStr = matchElmt.getAttributeValue(XMLConstants.HASPAGE);
                    if (pageStr!= null){
                        node.setHasPageOnServer( pageStr.equals(XMLConstants.ONE) );
                    }

                    String childCountStr = matchElmt.getAttributeValue(XMLConstants.CHILDCOUNT);
                    if (childCountStr != null) {
                        node.setChildCountOnServer(new Integer(childCountStr).intValue());
                    }

                    Element lock_info = matchElmt.getChild(XMLConstants.LOCK_INFO);
                    if(lock_info != null) {
                        // If the lock has a batch id attached to it, then don't
                        // mark it as locked because this user is an editor and
                        // can download the node
                        String batchId = lock_info.getAttributeValue(XMLConstants.BATCHID);
                        if (StringUtils.isEmpty((batchId))) {
                        	String downloadIdStr = lock_info.getAttributeValue(XMLConstants.DOWNLOAD_ID);
                        	int lockDownloadId = -1;
                        	if (StringUtils.notEmpty(downloadIdStr)) {
                        		lockDownloadId= new Integer(lock_info.getAttributeValue(XMLConstants.DOWNLOAD_ID)).intValue();	
                        	}
	                        if (StringUtils.notEmpty(downloadIdStr) && lockDownloadId == getDownloadId()) {
	                            System.out.println("check in = " + node.getId() + ", " + node.getName());
	                            fileManager.checkInSubtree(node.getId());
	                            node.setLocked(false);
	                        } else {
	                            node.setLocked(true);
	                            String tempString = lock_info.getAttributeValue(XMLConstants.DATE_TIME);
	                            if(tempString != null) {
	                                node.setLockDate(tempString.trim());
	                            }
	
	                            tempString = lock_info.getAttributeValue(XMLConstants.USER);
	                            if(tempString != null) {
	                                node.setLockUser(tempString.trim());
	                            }
	
	                            tempString = lock_info.getAttributeValue(XMLConstants.TYPE);
	                            if(tempString != null) {
	                                node.setLockType(tempString.trim());
	                            }
	                        }
                        }
                    } else {
                        node.setLocked(false);
                    }
                }
            }
        }
    }    
    
    /**
     * Disables file and edit menus during critical server interactions on the
     * Tree editor and all Node editors
     *
     * @param value Whether to enable or disable the menus
     */
    public void setCriticalMenusEnabled(boolean value) {
        ToLMenuBar menuBar;
        if (getTreeEditor() != null) {
            menuBar = (ToLMenuBar) getTreeEditor().getJMenuBar();
            menuBar.setCriticalMenusEnabled(value);
        }
    }
    
    /**
     * Creates a "Windows" menu with all the currently open Page Editors and 
     * the tree editor.  This is used to bring various windows "toFront"
     *
     * @return A new JMenu containing a menu item for all PageEditors and the
     *         TreeEditor
     */
    public JMenu createWindowMenu() {
        JMenu windowMenu = new JMenu("Window");
        JMenuItem treeEditorItem = new JMenuItem("Tree Window");
        treeEditorItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treeEditor.toFront();
            }
        });
        windowMenu.add(treeEditorItem);
        windowMenu.addSeparator();
        JMenu onlineManagersMenu = new JMenu("Online Tools");
        windowMenu.add(onlineManagersMenu);
        onlineManagersMenu.add(createManagerViewerMenuItem("ScientificMaterialsManager", "Scientific Materials Manager"));
        onlineManagersMenu.add(createManagerViewerMenuItem("ImagesManager", "Media Manager"));
        onlineManagersMenu.add(createManagerViewerMenuItem("ScientificRegistrationOverview", "Contributor Registration"));
        return windowMenu;
    }  
   
    /**
     * Creates a JMenuItem that will open a web browser to specified page name
     * @param pageName
     * @param label
     * @return
     */
    private JMenuItem createManagerViewerMenuItem(String pageName, String label) {
        String url = HttpRequestMaker.getExternalFrontEndUrlString(pageName, new Hashtable()); 
        JMenuItem item = new JMenuItem(new WWWAction(label, url));
        return item;
    }    

    /**
     * Checks to see if any nodes have the same name.
     *
     * @param nodes The nodes to look over
     * @return The name that is the same among more than 1 node if it exists,
     *         null otherwise.
     */
    public String sameNodeName(ArrayList nodes) {
        Hashtable nameHash = new Hashtable();
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            String nextName = ((Node) it.next()).getName();
            if (nameHash.containsKey(nextName)) {
                return nextName;
            } else {
                if (nextName != null && !nextName.equals("")) {
                    nameHash.put(nextName, nextName);
                }
            }
        }
        return null;
    }
    
    /**
     * Cover for the other method that just uses the currently open tree
     *
     * @return true if there is more than 1 node with the same name
     */
    public String sameNodeName() {
        return sameNodeName(TreePanel.getTreePanel().getTree().getNodeList());
    }
    
    /**
     * Discard all information about the current open file
     */
    public void removeTreeEditor() {
        treeEditor = null;
        fileName = null;
    }
        
    /**
     * Parses the MySQL date and returns a natural text formatted string
     *
     * @param dateString the string containing the MySQL date
     * @return a formatted natural language string
     */
    public String parseMySQLDate (String dateString) {
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(5, 7)) - 1;
        int day = Integer.parseInt(dateString.substring(8, 10));
        int hours = Integer.parseInt(dateString.substring(11, 13));
        int minutes = Integer.parseInt(dateString.substring(14, 16));
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hours, minutes);
        return dateFormatter.format(calendar.getTime());
    }    
    
    /**
     * Returns the date format instance used throughout the application
     *
     * @return a dateformat instance using the short format
     */
    public DateFormat getDateFormatter () {
        return dateFormatter;
    }
    
    /**
     * Set to true by the abstract menu bar when a cut/paste event occurs.
     *Normally, the ChangeMonitoringTextField will see a return of 
     *focus from the menu as a reason to reassign the "current value"...but the
     *way cut works requires that the textfield ignore the focus event. 
     *After getting focus back from a cut menu, textfields reset the value 
     *to false.
     */
    public void setMenuCuttingPasting(boolean value){
        menuCuttingPasting = value;
    }

    /**
     *Used to allow "cut"/"paste" from menu to work with undo/redo. 
     *
     * @see setMenuCutting
     */
    public boolean getMenuCuttingPasting(){
        return menuCuttingPasting ;
    }    
    
    /**
     * Repaints all panels containing this node, since something changed
     *
     * @param node The node to repaint
     */
    public void repaintNode(Node node) {
        /*if (node.getParent() != null) {
            // If this node had its confidence changed, it may be necessary to 
            // reorder it in its siblings array
            node.getParent().reorderIfNecessary(node);
        }
        */
        TreePanelUpdateManager.getManager().rebuildTreePanels(node);
    }   
    
    /**
     * Returns whether there is a connection to the internet
     *
     * @return Whether there is a connection to the internet
     */
    public boolean hasConnection() {
        return hasConnection;
    }
    
    /**
     * Sets whether there is a connection to the internet
     *
     * @param value Whether there is a connection to the internet
     */
    public void setHasConnection(boolean value) {
        hasConnection = value;
    }
    
    /**
     * Updates the unit increment on the scrollPane parameter
     *
     * @param scrollPane The scrollpane to modify
     * @return The scrollPane
     */
    public JScrollPane updateUnitIncrement(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
        return scrollPane;
    }
    
    /**
     * Returns a new JLabel with the tooltip lightbulb as the icon
     *
     * @param msgname The name of the message to set as the tooltip
     */
    public JLabel getLightbulbLabel(String msgname) {
        JLabel label = new JLabel();
        label.setIcon(lightbulbIcon);
        label.setBorder(BorderFactory.createEmptyBorder());
        
        if (msgname != null) {
            String msg = getMsgString(msgname);
            msg = "<html><table width=\"100%\"><td width=\"500\">" + msg + "</td></table></html>";
            label.setToolTipText(msg);
        }
        return label;
    }
    
    /**
     * Sets up the thumbnail for the image and returns the label
     * @param nodeImg The image to configure
     * @param getLocal Whether to load the file locally or fetch it from the server
     * @return The thumbnail label
     */
    public JLabel setupThumbnail(NodeImage nodeImg, boolean getLocal) {
        JLabel label = null;
        if (getLocal) {
            ImageIcon icon = new ImageIcon(getFileManager().getLocalImagePathForId("" + nodeImg.getId()));
            label = new JLabel(icon);
        } else {
	        // Fetch the thumbnail from the server and wait until it's loaded
	        FetchThumbnailThread thread = new FetchThumbnailThread(nodeImg);
	        thread.start();
	        thread.get();
	        label = new JLabel(nodeImg.getThumbnail().getIcon());
        }
        nodeImg.setThumbnail(label);
        return label;
    }

    
    public String getNodeImageCopyrightString(NodeImage img) {
        String data = "";
        data += "<table>";
        // Do copyright stuff
        if (img.getCopyrightContributorId() != Contributor.BLANK_CONTRIBUTOR.getId()) {
            Contributor contr = Controller.getController().getEditableContributor(img.getCopyrightContributorId());
            if (contr != null) {
                boolean hasLink = StringUtils.notEmpty(contr.getHomepage()) || (StringUtils.notEmpty(contr.getEmail()) && !contr.dontShowEmail());
                String openAnchorString = hasLink ? "<a href=\"http://tolweb.org\">" : "";
                String closeAnchorString = hasLink ? "</a>" : "";
                data += addTableRow("Image copyright", "&copy; " + img.getCopyrightDate() + " " + openAnchorString + contr.getName() + closeAnchorString);
            }
        } else if (StringUtils.notEmpty(img.getCopyrightDate()) || StringUtils.notEmpty(img.getCopyrightOwner()) || img.inPublicDomain()) {
            if (!img.inPublicDomain()) {
                boolean hasLink = StringUtils.notEmpty(img.getCopyrightUrl()) || StringUtils.notEmpty(img.getCopyrightEmail());
                String openAnchorString = hasLink ? "<a href=\"http://tolweb.org\">" : "";
                String closeAnchorString = hasLink ? "</a>" : "";                
                data += addTableRow("Image copyright", "&copy; " + img.getCopyrightDate() + " " + openAnchorString + img.getCopyrightOwner() + closeAnchorString);
            }
        } 
        return data;
    }
    
    private String addTableRow(String name, String value) {
        if (StringUtils.notEmpty(value)) {
          String tableString = "<tr>";
          tableString += "<td width=\"150\">" + HtmlLabel.getGenericFontString() + name + "</font></td>" + "<td>" + HtmlLabel.getGenericFontString() + value + "</font></td>";
          tableString += "</tr>";
          return tableString;
        } else {
            return "";
        }
    }  
   
    /**
     * Returns the static controller instance
     *
     * @return The static Controller instance
     */
    public static Controller getController() {
    	if (controller == null) {
    		controller = new Controller();
    	}
        return controller;
    }  
    
    public String getPreviewUrl(Node node) {
    	String idString = node.getId() > 0 ? ("/" + node.getId() + "") : "";
        return getWorkingPath() + node.getNameOnServer().replace(' ', '_') + idString;        
    }

    /**
     * @return Returns the warnAboutNodeDeletion.
     */
    public boolean getWarnAboutNodeDeletion() {
        return warnAboutNodeDeletion;
    }

    /**
     * @param warnAboutNodeDeletion The warnAboutNodeDeletion to set.
     */
    public void setWarnAboutNodeDeletion(boolean warnAboutNodeDeletion) {
        this.warnAboutNodeDeletion = warnAboutNodeDeletion;
    }

    /**
     * @return Returns the warnAboutPageDeletion.
     */
    public boolean getWarnAboutPageDeletion() {
        return warnAboutPageDeletion;
    }

    /**
     * @param warnAboutPageDeletion The warnAboutPageDeletion to set.
     */
    public void setWarnAboutPageDeletion(boolean warnAboutPageDeletion) {
        this.warnAboutPageDeletion = warnAboutPageDeletion;
    }

	public String getObsoleteMessage() {
		return obsoleteMessage;
	}
	
	public void setObsoleteMessage(String value) {
		obsoleteMessage = value;
	}

	public void showObsoleteMessage() {
        String message = getObsoleteMessage();
        JOptionPane.showMessageDialog(getTreeEditor(), message, "This version of TreeGrow is obsolete", JOptionPane.INFORMATION_MESSAGE, lightbulbIcon);
	}
}

