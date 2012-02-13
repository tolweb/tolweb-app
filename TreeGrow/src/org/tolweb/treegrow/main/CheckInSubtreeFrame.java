/*
 * CheckinSubtreeFrame.java
 *
 * Created on September 3, 2003, 2:21 PM
 */

package org.tolweb.treegrow.main;

import java.util.*;
import org.tolweb.treegrow.tree.*;

/**
 * Frame that displays the subtrees a user can check in
 */
public class CheckInSubtreeFrame extends AbstractServerPagedNodeInteractionFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1779110805573391497L;

	/** Creates a new instance of CheckinSubtreeFrame */
    public CheckInSubtreeFrame() {
    }
    
    protected String getLabelText() {
        return "Please select the subtree you would like to check in";
    }
    
    protected String getSubmitButtonText() {
        return "Check In";
    }
    
    /**
     * Checks in the selected subtree
     */
    public void interactForSelectedNode() {
        Controller controller = Controller.getController();
        Node selectedNode = ((Node) nodeSelectionList.getSelectedValue());
        if (selectedNode == null) {
            return;
        }
        if (controller.getFileChanged(selectedNode)) {
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
        controller.setCriticalMenusEnabled(false);
        controller.setWaitCursors();
        if (selectedNode == TreePanel.getTreePanel().getTree().getRoot()) {
            ((ToLMenuBar) Controller.getController().getTreeEditor().getJMenuBar()).doCheckIn();
        } else if (controller.getFileManager().checkInSubtree(selectedNode.getId())) {
            TreePanel panel = TreePanel.getTreePanel();
            if (panel != null) {
                Tree tree = panel.getTree();
                Iterator it = selectedNode.getSubtreeNodes().iterator();
                while (it.hasNext()) {
                    Node node = (Node) it.next();
                    tree.removeNode(node);
                    panel.removeNode(node);
                }
                // Set this since the checked-in node now is a terminal with page
                // so these values are used
                int actualChildren = selectedNode.getChildren().size();
                //selectedNode.setHasPageOnServer(selectedNode.hasPage());
                selectedNode.setCheckedOut(false);
                selectedNode.setPageObject(null);
                selectedNode.removeChildren();
                selectedNode.setChildCountOnServer(actualChildren);
                panel.disableTextFieldForNode(selectedNode);
                panel.rebuildTree();
                panel.repaint();
            }
        }
        controller.setCriticalMenusEnabled(true);
        controller.setDefaultCursors();
        dispose();
    }
}
