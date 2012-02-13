/*
 * CheckinSubtreeMenu.java
 *
 * Created on October 22, 2003, 9:12 AM
 */

package org.tolweb.treegrow.main;

import java.util.*;

import org.tolweb.treegrow.tree.*;

/**
 * JMenu that allows users to check in subtrees
 */
public class CheckinSubtreeMenu extends AbstractSubtreeMenu {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6459961953426802137L;

	/** Creates a new instance of CheckinSubtreeMenu */
    public CheckinSubtreeMenu() {
        super("Check in subtree");
    }
    
    protected ArrayList getNodes() {
        return TreePanel.getTreePanel().getTree().getNodesWithPages();
    }
    
    /**
     * Checks in the selected subtree
     */
    protected void interactForSelectedNode(Node node) {
        Controller controller = Controller.getController();
        if (controller.getFileChanged(node)) {
            String UPLOAD_BEFORE_CHECKIN_SUBTREE = controller.getMsgString("UPLOAD_BEFORE_CHECKIN_SUBTREE");
            String UPLOAD_THEN_CHECKIN = controller.getMsgString("UPLOAD_THEN_CHECKIN");
            String CHECKIN_SUBTREE_NO_UPLOAD = controller.getMsgString("CHECKIN_SUBTREE_NO_UPLOAD");
            String CANCEL_NO_CHECKIN = controller.getMsgString("CANCEL_NO_CHECKIN");
            String[] options = new String[] { UPLOAD_THEN_CHECKIN, CHECKIN_SUBTREE_NO_UPLOAD, CANCEL_NO_CHECKIN };
            int response = RadioOptionPane.showRadioButtonDialog(controller.getManagerFrame(), "Upload Before Checkin?", UPLOAD_BEFORE_CHECKIN_SUBTREE, options);

            switch (response) {
                case 0:
                    // Upload
                    boolean result = UploadTree.doUploadDialog();
                    if (!result) {
                        return;
                    }
                case 1:
                    break;
                default:
                    return;
            }
        }
        if (Controller.getController().getFileManager().checkInSubtree(node.getId())) {
            TreePanel panel = TreePanel.getTreePanel();
            if (panel != null) {
                Tree tree = panel.getTree();
                Iterator it = node.getSubtreeNodes().iterator();
                while (it.hasNext()) {
                    Node n = (Node) it.next();
                    tree.removeNode(n);
                    panel.removeNode(n);
                }
                // Set this since the checked-in node now is a terminal with page
                // so these values are used
                node.setChildCountOnServer(node.getChildren().size());
                node.setHasPageOnServer(node.hasPage());
                
                node.setPageObject(null);
                node.removeChildren();
                panel.disableTextFieldForNode(node);
                panel.rebuildTree();
                panel.repaint();
            }
        }
    }    
}

    
