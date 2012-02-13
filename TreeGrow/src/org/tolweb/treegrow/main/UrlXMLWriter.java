/*
 * UrlXMLWriter.java
 *
 * Created on June 24, 2003, 9:32 AM
 */

package org.tolweb.treegrow.main;

import java.util.*;
import javax.swing.*;
import org.jdom.*;
import org.tolweb.treegrow.page.*;
import org.tolweb.treegrow.tree.*;

/**
 * XMLWriter subclass that writes the XML to a URL
 */
public class UrlXMLWriter extends TreeGrowXMLWriter {
    private String imgErrors = "";
    private ArrayList imgsToRemove = new ArrayList();
    private Hashtable oldToNewImageIds;
    private Hashtable imageIdsToNodeImages;
    private Hashtable oldToNewContributorIds;
    
    public UrlXMLWriter() {
        super();
        oldToNewImageIds = new Hashtable();
        imageIdsToNodeImages = new Hashtable();
        oldToNewContributorIds = new Hashtable();
    }
    
    public UrlXMLWriter(Tree t) {
        super(t);
        oldToNewImageIds = new Hashtable();
        imageIdsToNodeImages = new Hashtable();
        oldToNewContributorIds = new Hashtable();        
    }
    
    /**
     * Writes the XML for the tree starting at node root and prompts for a 
     * preview afterwards
     *
     * @param root The root of the tree to write out
     * @return true if writing was successful, false otherwise
     */
    public boolean writeXML(Node root) {
        return writeXML(root, true);
    }
    
    /**
     * Writes the XML for the tree starting at node root
     *
     * @param root The root of the tree to write out
     * @param doPrompt Whether to prompt for preview afterwards
     * @return true if writing was successful, false otherwise
     */
    public boolean writeXML(Node root, boolean doPrompt) {
        Controller controller = Controller.getController();
        FileManager manager = controller.getFileManager();
        controller.setWaitCursors();
        //removeNullLinkedAccPages();
        //removeEmptyAuthors();

        // Write a backup to the local filesystem in case of weirdness
        FileXMLWriter writer;
        String backupName = controller.getFileManager().getDataPath() + "exportbackup.xml";
        if (tree == null) {
            writer = new FileXMLWriter(backupName);
        } else {
            writer = new FileXMLWriter(tree, backupName);        
        }
        writer.writeXML();
        
        // Try to get new ids for the new nodes
        /*boolean result = NewNodeIdUploader.upload(tree);
        if (!result) {
            return false;
        }*/
       
                      
        // In this case there were changes to images or contributors, but not
        // to the actual tree, so don't bother writing it out
        if (!Controller.getController().getFileChanged(root)) {
            if (doPrompt) {
                offerPreview(root); 
            }
            return true;
        }
        
        boolean uploadSuccess = super.writeXML(root);

        /*if (!imgErrors.equals("")) {
            JOptionPane.showMessageDialog(TreePanel.getTreePanel().getTreeFrame(), imgErrors);
            it = imgsToRemove.iterator();
            while (it.hasNext()) {
                PageImage img = (PageImage) it.next();
                img.getPage().getImageList().remove(img);
            }
        }*/

        if (uploadSuccess) {
            if (tree != null) {
                tree.setUploadDate(System.currentTimeMillis());
            }
            if (doPrompt) {
                offerPreview(root); 
            }
        }
        
        /*it = controller.getPageEditorIterator();
        while (it.hasNext()) {
            PageFrame frame = (PageFrame) it.next();
            frame.syncChangedTextAreas();
        }*/
        
        //while we're at it...save the file locally
        if (tree == null) {
            writer = new FileXMLWriter();
        } else {
            writer = new FileXMLWriter(tree);        
        }
        writer.writeXML();
        
        return uploadSuccess;
    }
    
    /**
     * Removes any authors that had the 'Select an author' author.  Then forces
     * a repaint
     /
    private void removeEmptyAuthors() {
        ArrayList pages = tree.getNodesWithPages();
        Iterator it = pages.iterator();
        int emptyId = Contributor.BLANK_CONTRIBUTOR.getId();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            Page page = node.getPageObject();
            Vector authors = page.getContributorList();
            Iterator it2 = new ArrayList(authors).iterator();
            
            while (it2.hasNext()) {
                PageContributor pc = (PageContributor) it2.next();
                if (pc.getContributorId() == emptyId) {
                    authors.remove(pc);
                    PageFrame frame = Controller.getController().getPageEditor(node);
                    if (frame != null) {
                        frame.refreshCreditsPanelTable();
                    }                    
                }
            }
        }
    }*/
      
    /** Writes the given document to a URL
     *
     * @param outDuc The doc to write
     * @return true If the write was successful, false otherwise.
     *
     */
    protected boolean outputDocument(Document outDoc, Node root) {
        Controller controller = Controller.getController();
        String ST_UPLOADING_SERVER = controller.getMsgString("ST_UPLOADING_SERVER");
        controller.setStatusMessage(ST_UPLOADING_SERVER);

        try {
            UploadTree ut=new UploadTree(outDoc, tree);
            //offerPreview();
        } catch(Exception e) {
            e.printStackTrace();
            String ST_UPLOAD_ERR = controller.getMsgString("ST_UPLOAD_ERR");
            controller.setStatusMessage(ST_UPLOAD_ERR);
            Vector tempVec = new Vector(); 
            tempVec.add(e.getMessage());

            String ERROR_UPLOAD_TREE = controller.getMsgString("ERROR_UPLOAD_TREE", tempVec);
            JOptionPane.showMessageDialog(controller.getTreeEditor(), ERROR_UPLOAD_TREE,"Upload Error",
                                                            JOptionPane.ERROR_MESSAGE);
            tempVec.clear();

            controller.setDefaultCursors();
            return false;
        }

        TreePanel treePanel = TreePanel.getTreePanel();
        if (treePanel != null) {
            controller.setChangedToFalse(root);
            treePanel.getUndoManager().discardAllEdits();
            treePanel.getUndoAction().updateUndoState();
            treePanel.getRedoAction().updateRedoState();
            controller.setDefaultCursors();
        }
        
        return true;
    }   
    
    /** Used to indicate whether a subclass of XMLWriter is in file writing mode,
     * in which case more information is written.
     *
     * @return true
     *
     */
    protected boolean saveToFile() {
        return false;
    }
    
    /**
     * Overridden to do nothing since we dont want to include image details
     * in the xml file upload
     */
    protected void finishEncoding(NodeImage img, Element imgElt) {
    }
    
    protected void getFixedLocalContributorId(PageContributor pc) {
        Integer id = new Integer(pc.getContributorId());
        if (oldToNewContributorIds.containsKey(id)) {
            pc.setContributorId(((Integer) oldToNewContributorIds.get(id)).intValue());
        }
    }
    
    /**
     * Returns the changed location of an img that is uploaded to the server
     *
     * @param input The original location of the local img
     * @return The new location of the img
     */
    protected String getFixedLocalFileUrl(String input) {
        // Look for any img tags that have negative ids.  They will get replaced
        // with the new id returned by the server
        String toSearchFor = "<ToLimg id=\"-";
        int index;
        while (input != null && (index = input.indexOf(toSearchFor)) != -1) {
            String firstPart = input.substring(0, index + toSearchFor.length());
            System.out.println("first part is: " + firstPart);
            String secondPart = input.substring(index + toSearchFor.length());
            System.out.println("second part is: " + secondPart);
            int quoteIndex = secondPart.indexOf("\"");
            String number = secondPart.substring(0, quoteIndex);
            number = "-" + number;
            System.out.println("number is: " + number);
            String newNumber = oldToNewImageIds.get(new Integer(number)).toString();
            input = firstPart.substring(0, firstPart.length() - 1) + newNumber + secondPart.substring(quoteIndex);
        }
        return input;    
    }
       
    /**
     * Offers the user the chance to view the recently uploaded changes on the
     * server
     *
     * @param groupName The name of the group to view
     */
    private void offerPreview(Node node) {
        String[] choices = new String[] { "Preview changes in browser", "I'll preview later, thanks" };
        String UPLOAD_SUCCESS = Controller.getController().getMsgString("UPLOAD_SUCCESS");
        JOptionPane optionPane = new JOptionPane(UPLOAD_SUCCESS, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, choices, null);
        JFrame parentFrame;
        if (TreePanel.getTreePanel() != null) {
            parentFrame = TreePanel.getTreePanel().getTreeFrame();
        } else {
            parentFrame = Controller.getController().getManagerFrame();
        }
        JDialog dialog = optionPane.createDialog(null, "Upload Successful");
        //System.out.println("dialog = " + dialog);
        dialog.setModal(true);
        dialog.show();

        Object result = optionPane.getValue();

        if (result instanceof String && result.equals("Preview changes in browser")) {
            Controller controller = Controller.getController();
            String path = HttpRequestMaker.getWorkingPreviewUrlForNode(node);            

            try {
                OpenBrowser ob = new OpenBrowser(path);
                ob.start();
                //LaunchBrowser.openURL(path);    
            } catch (Exception ex) {
            }
        }
    }
    
    /** 
     *Overwrides default method that decides if the entire Ancestors 
     *document-subtree needs to be written out. If to server, no ... the 
     *server doesn't need it, and it's a large hunk of text.
     */
    protected boolean writeOutAncestors () {
        return false;
    } 
    
    protected boolean writeOutContributors() {
        return false;
    }
    
    protected boolean writeOutImages() {
        return false;
    }
}
