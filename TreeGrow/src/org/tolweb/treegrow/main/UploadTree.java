package org.tolweb.treegrow.main;
import java.io.*;
import java.util.*;

import javax.swing.*;
import org.jdom.*;
import org.tolweb.base.http.BaseHttpRequestMaker;
import org.tolweb.base.xml.BaseXMLReader;
import org.tolweb.base.xml.BaseXMLWriter;
import org.tolweb.treegrow.tree.*;

/**
 * Class responsible for uploading changed data to the server
 */
public class UploadTree {
    private Tree tree;
    
    /**
     * Uploads the document to the server
     *
     * @param doc The XML document to upload
     */
    public UploadTree(Document doc) throws IOException, NotSerializableException, Exception {
        this(doc, null);
    }

    /**
     * Uploads the document and tree to the server
     *
     * @param doc The XML document to upload
     * @param t The tree to upload
     */
    public UploadTree(Document doc, Tree t) throws IOException, NotSerializableException, Exception {
        tree = t;
        TreePanel panel = TreePanel.getTreePanel();
        Controller controller = Controller.getController();

        // before we do the actual upload, check to see if we've deleted any nodes with pages
        boolean result = checkNodesWithPages(t, doc);
        if (result) {
            String plainStr = BaseXMLWriter.getDocumentAsString(doc);
            String LOST_CONNECTION_UPLOAD = controller.getMsgString("LOST_CONNECTION_UPLOAD");
            final boolean testing = false;
            if (testing) {
                File newFile = new File("/tmp/ToLEditor.xml");
                BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
                bw.write(plainStr);
                bw.flush();
                bw.close();                    
            } else {
                String url = controller.getExternalServicePrefix() + "EditorSubmission";
                System.out.println("editor submission url is: " + controller.getExternalServicePrefix() + "EditorSubmission");
                plainStr += "<!--'Uploading_OS="+System.getProperty("os.name")+"'-->";
                System.out.println("plainStr = " + plainStr);
                Hashtable args = new Hashtable();
                args.put("xml", plainStr);

                byte[] bytes = BaseHttpRequestMaker.doPost(url, args);
                if (bytes == null) {
                    throw new Exception(LOST_CONNECTION_UPLOAD);
                }
                
                ArrayList nodes;
    
                if (tree == null) {
                    nodes = TreePanel.getTreePanel().getTree().getNodeList();
                } else {
                    nodes = tree.getNodeList();
                }
                String xmlString = new String(bytes);
                boolean success = false;
                Document xmlDoc = BaseXMLReader.getDocumentFromString(xmlString);
                if (xmlDoc != null) {
                	success = true;
                	String newNodeIds = xmlDoc.getRootElement().getAttributeValue(XMLConstants.NODEID);
                	if (newNodeIds != null) {
                		assignNewNodeIds(newNodeIds);
                	}
                } 
                if (!success) {
                    throw new Exception("ERROR -1, noted by server");
                }
    
                String ST_UPLOAD_SUCCESS = controller.getMsgString("ST_UPLOAD_SUCCESS");
                controller.setStatusMessage(ST_UPLOAD_SUCCESS);
    
                // Update the name on server for all nodes in the tree
                Iterator it = t.getNodeList().iterator();
                while (it.hasNext()) {
                    Node node = (Node) it.next();
                    node.setNameOnServer(node.getName());
                    if (node.hasPage()) {
                        // make sure it knows it has a page on the server now
                        node.setHasPageOnServer(true);
                    }
                }            
                Controller.getController().getUpdatedNodes(tree);
    
                if (panel != null) { 
                    panel.rebuildTree();// repaint(); 
                    Controller.getController().clearAllEdits();
                }
            }
        }
    }
    
    /**
     * Performs a check to see if any nodes with pages have been deleted
     * @param t
     */
    private boolean checkNodesWithPages(Tree t, Document doc) {
        /*boolean returnVal = true;
        Hashtable args = new Hashtable();
        args.put(RequestParameters.DOWNLOAD_ID, "" + Controller.getController().getDownloadId());
        String url = HttpRequestMaker.getServerUtilsUrlString(RequestParameters.NODES_WITH_PAGES, args);
        Document nodesWithPagesDoc = HttpRequestMaker.getHttpResponseAsDocument(url);
        String deletedNodesWithPages = "";
        if (nodesWithPagesDoc != null) {
            for (Iterator iter = nodesWithPagesDoc.getRootElement().getChildren().iterator(); iter.hasNext();) {
                Element nextPageElement = (Element) iter.next();
                Element nodeElement = nextPageElement.getChild(XMLConstants.NODE);
                int nextNodeId = Integer.parseInt(nodeElement.getAttributeValue(XMLConstants.NODEID));
                Node node = t.findNode(nextNodeId);
                if (node == null) {
                    // build a string with info for each of the missing pages that has the person's name who
                    // contributed the page, along with that person's email
                    String nodeName = nodeElement.getAttributeValue(XMLConstants.NAME);
                    Element contributorElement = nextPageElement.getChild(XMLConstants.CONTRIBUTOR);
                    String contributorName = contributorElement.getAttributeValue(XMLConstants.NAME);
                    String contributorEmail = contributorElement.getAttributeValue(XMLConstants.EMAIL);
                    String infoString = nodeName + " page created by " + contributorName + " (" + contributorEmail + ")";
                    deletedNodesWithPages += "<br>" + infoString;
                } else if (!node.hasPage() && !node.getPageWasRemoved()) {
                    node.setHasPageOnServer(true);
                    node.setCheckedOut(true);
                    // here, this node didn't think that it had a page before.  we need to
                    // modify the document to mark these nodes as having pages
                    Element rootElement = doc.getRootElement();
                    Element nodesElement = rootElement.getChild(XMLConstants.NODES);
                    Element nodeElementToFix = getNodeElementNamed(nodesElement, node.getId());
                    nodeElementToFix.setAttribute(XMLConstants.HASPAGE, XMLConstants.ONE);
                }
            }
        }
        if (StringUtils.notEmpty(deletedNodesWithPages)) {
            final String infoMessage = "Deleted nodes that have pages on the server:" + deletedNodesWithPages + "<br/>" + " TreeGrow user is: " + Controller.getController().getUserName();
            // start a thread that will do a post to the server and send the editor an email
            Thread postThread = new Thread() {
                public void run() {
                    Hashtable args = new Hashtable();
                    args.put(RequestParameters.ERROR_MESSAGE, infoMessage);
                    String url = HttpRequestMaker.getServerUtilsUrlString(RequestParameters.EMAIL_UPLOAD_PROBLEM);
                    HttpRequestMaker.doPost(url, args);
                }
            };
            SwingUtilities.invokeLater(postThread);
            Vector msgArgs = new Vector();
            msgArgs.add(deletedNodesWithPages);
            String NODES_WITH_PAGES_MISSING = Controller.getController().getMsgString("NODES_WITH_PAGES_MISSING", msgArgs);
            throw new RuntimeException(NODES_WITH_PAGES_MISSING);
        }
        return returnVal;*/
        return true;
    }
    
    private Element getNodeElementNamed(Element nodesElement, int id) {
        String idString = "" + id;
        for (Iterator iter = nodesElement.getChildren(XMLConstants.NODE).iterator(); iter.hasNext();) {
            Element nextNodeElement = (Element) iter.next();
            if (nextNodeElement.getAttributeValue(XMLConstants.ID).equals(idString)) {
                return nextNodeElement;
            }
        }
        // didn't find it, so iterate again and call recursively over the children
        for (Iterator iter = nodesElement.getChildren(XMLConstants.NODE).iterator(); iter.hasNext();) {
            Element nextNodeElement = (Element) iter.next();
            if (nextNodeElement.getChild(XMLConstants.NODES) != null) {
                Element nodeElement = getNodeElementNamed(nextNodeElement.getChild(XMLConstants.NODES), id);
                if (nodeElement != null) {
                    return nodeElement;
                }
            }
        }
        return null;
    }
    
    private void assignNewNodeIds(String response) {
        StringTokenizer pairs = new StringTokenizer(response,"&");
        StringTokenizer key_value;
        String cur_id, new_id;

        while (pairs.hasMoreTokens()) {
            key_value = new StringTokenizer(pairs.nextToken(),"=");
            cur_id = key_value.nextToken();
            new_id = key_value.nextToken();

            Iterator it = tree.getNodeList().iterator();
            while(it.hasNext()) {
                Node n = (Node) it.next();
                if (("" + n.getId()).equals(cur_id) ) {
                    n.setId(new Integer(new_id).intValue());
                }
            }
        }
    	
    }
    
    /**
     * Does an upload dialog with the root of the currently open file
     *
     * @return true if the upload succeeded, false otherwise
     */
    public static boolean doUploadDialog() {
        return doUploadDialog(null, TreePanel.getTreePanel().getTree().getRoot(), true);
    }
    
    /**
     * Does an upload dialog with the file located at fileName
     *
     * @param fileName The path to the file to upload
     * @return true if the upload succeeded, false otherwise
     */
    public static boolean doUploadDialog(String fileName) {
        return doUploadDialog(fileName, null, true);
    }
    
    public static boolean doUploadDialog(String fileName, boolean doPrompt) {
        return doUploadDialog(fileName, null, doPrompt);
    }
    
    /**
     * Does an upload dialog starting with at the specified root
     *
     * @param The root to start the dialog at
     * @return true if the upload succeeded, false otherwise
     */
    public static boolean doUploadDialog(Node root) {
        return doUploadDialog(null, root, true);
    }
        
    /**
     * Does an upload dialog -- makes sure that there is a net connection
     * checks to make sure the version is current on the file, duplicate node
     * names etc.  Assuming it meets all prerequisites for uploading, an
     * upload occurs.
     *
     * @param fileName The path to the xml file on the disk that contains the
     *        tree to upload
     * @param root The root of the tree to upload (in case a subtree is being 
     *        uploaded)
     */
    public static boolean doUploadDialog(String fileName, Node root, boolean doPrompt) {
        Controller controller = Controller.getController();
        FileManager fileManager = controller.getFileManager();
        TreePanel panel = TreePanel.getTreePanel();
        Tree toUpload = null;
        controller.setWaitCursors();
        controller.setCriticalMenusEnabled(false);

        if (fileName != null) {
            TreeGrowXMLReader reader = new TreeGrowXMLReader(fileName, false);
            reader.gatherContent();
            toUpload = reader.getTree();
        } else {
            toUpload = panel.getTree();
        }
        if (root == null) {
            root = toUpload.getRoot();
        }
        String tempString;
        if (fileName == null) {
            //save to local file.
            tempString = controller.getFileName() + Controller.EXTENSION;
        } else {
            tempString = fileName;
        }

        boolean success = fileManager.getMetadataFromFile(tempString);
        if(!success) {
                resetCursors();            
                return false;
        }

        CheckNetConnection check = new CheckNetConnection();
        if(!(check.isConnected() > 0)) {
                //inform user that there is no net connection
                String NO_NET_CONNECTION = controller.getMsgString("NO_NET_CONNECTION");
                JOptionPane.showMessageDialog(
                                        controller.getTreeEditor(),
                                        NO_NET_CONNECTION,
                                        "Message Window",
                                        JOptionPane.INFORMATION_MESSAGE);
                resetCursors();                
                return false;
        }

        // check for the correct download version.
        String current = fileManager.isVersionCurrent(controller.getDownloadId());

        if((current != null) && (current.equals("false"))) {
            //prompt user
            String BAD_FILE_UPLOAD = controller.getMsgString("BAD_FILE_UPLOAD");
            String MOVE_RECYCLE = controller.getMsgString("MOVE_RECYCLE");
            String[] choices = new String[] { MOVE_RECYCLE };
            int response = RadioOptionPane.showRadioButtonDialog(controller.getTreeEditor(), "What would you like to do?", BAD_FILE_UPLOAD, choices);

            if (response == 0) {
                //ExportFrame exportForm = new ExportFrame(controller.getTreeEditor());
                //exportForm.show();
                fileManager.moveToRecycleBin(tempString);
                String MOVING_RECYCLE_BIN = controller.getMsgString("MOVING_RECYCLE_BIN");
                JOptionPane.showMessageDialog(controller.getTreeEditor(), MOVING_RECYCLE_BIN, "Message Window", JOptionPane.INFORMATION_MESSAGE);
            } else {
                fileManager.moveToRecycleBin(tempString);
                String MOVING_RECYCLE_BIN = controller.getMsgString("MOVING_RECYCLE_BIN");
                JOptionPane.showMessageDialog(controller.getTreeEditor(), MOVING_RECYCLE_BIN, "Message Window", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        boolean changed;
        
        // The difference between these two methods is that one takes into acct
        // whether the images or contributors have changed and the other looks
        // at it from a purely Tree-based perspective
        if (TreePanel.getTreePanel() != null && root == TreePanel.getTreePanel().getTree().getRoot()) {
            changed = controller.getFileChanged();
        } else {
            changed = controller.getFileChanged(root);
        }

        if(!changed) {
                //inform the user that there are no changes to be uploaded.
                String NO_UPLOADABLE_CHANGES = controller.getMsgString("NO_UPLOADABLE_CHANGES");
                JOptionPane.showMessageDialog(
                                        controller.getTreeEditor(),
                                        NO_UPLOADABLE_CHANGES,
                                        "Message Window",
                                        JOptionPane.INFORMATION_MESSAGE);
                resetCursors();
                return false;
        }
        ArrayList nodes = root.getSubtreeNodes();
        nodes.add(root);
        String sameName = controller.sameNodeName(nodes);
        if(sameName != null) {
                Object[] options = {"OK to upload" ,"Cancel upload"};
                Vector name = new Vector();
                name.add(sameName);
                String SAME_NODENAME_QRY = controller.getMsgString("SAME_NODENAME_QRY", name);
                int n = JOptionPane.showOptionDialog(controller.getTreeEditor(),
                                SAME_NODENAME_QRY,
                                "Message",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[1]);

                if (n == JOptionPane.NO_OPTION) {
                    resetCursors();
                    return false;
                }
        }

        if( root != toUpload.getRoot() ) {
            //Attempting to upload a subtree.  If any of the ancestor nodes back 
            // to the tree section's root is unuploaded (has negative id),
            // the user must do a full tree upload if they want to upload this subtree.
            //  Tell them so, and which node is new.
            Node parent = root.getParent();
            Node deepestUnuploadedNode = null;
            while (parent != toUpload.getRoot()) {
                if (parent.getId() < 0) {
                    deepestUnuploadedNode = parent;
                }
                parent = parent.getParent();    
            }

            if (deepestUnuploadedNode != null) {
                Vector args = new Vector(Arrays.asList(new String[] {parent.getName(), root.getName(), deepestUnuploadedNode.getName()}) );
                String ANCESTOR_NOT_UPLOADED = controller.getMsgString("ANCESTOR_NOT_UPLOADED",args);
                JOptionPane.showMessageDialog(
                                        controller.getTreeEditor(),
                                        ANCESTOR_NOT_UPLOADED,
                                        "Unuploaded Ancestor",
                                        JOptionPane.INFORMATION_MESSAGE);
                resetCursors();
                return false;                
            }
        }
  
        UrlXMLWriter upload = new UrlXMLWriter(toUpload);
        boolean result = upload.writeXML(root, doPrompt);
        resetCursors();
        return result;
    }
    
    private static void resetCursors() {
        Controller controller = Controller.getController();
        controller.setDefaultCursors();
        controller.setCriticalMenusEnabled(true);
    }
}
