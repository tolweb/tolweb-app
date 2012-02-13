package org.tolweb.treegrow.main;

import java.util.*;

import org.tolweb.treegrow.tree.*;


/**
 * Thread class responsible for reading in a local file and opening a tree
 * editor for that file
 */
public class FileReaderThread extends Thread {
    private String m_FileName = null;
    private int prevUploadId = -1;

    public FileReaderThread(String file) {
	m_FileName = file;
    }
    
    public FileReaderThread(String file, int uploadId) {
        m_FileName = file;
        prevUploadId = uploadId;
    }

    public void run() {
        Controller controller = Controller.getController();
        try {
	    String ST_OPENING_FILE = controller.getMsgString("ST_OPENING_FILE");
	    controller.setStatusMessage(ST_OPENING_FILE);
	    try	{
		Thread.sleep(10);
	    }
	    catch(Exception e) {
                e.printStackTrace();
	    }
            controller.setWaitCursors();

            TreeGrowXMLReader xUtil = new TreeGrowXMLReader(m_FileName, true);
            controller.setEditableContributors(new Vector(xUtil.getEditableContributors()));
            xUtil.gatherContent();

            String treeString = xUtil.getTreeStructure();

            ArrayList actualNodes = new ArrayList();
            ArrayList terminalChildrenNodes = new ArrayList();
            Iterator it = xUtil.getNodeList().iterator();
            while (it.hasNext()) {
                Node node = (Node) it.next();
                if (treeString.indexOf("" + node.getId()) != -1) {
                    actualNodes.add(node);
                } else {
                    terminalChildrenNodes.add(node);
                }
            }
            Tree tree = xUtil.getTree();
            controller.openTreeEditor(tree);
            controller.getTree().setImages(xUtil.getImageList());
            if (prevUploadId != -1) {
                controller.setUploadId(prevUploadId);
            }
	    controller.setDefaultCursors();
            controller.getTreeEditor().setToolbarEnabled();
            it = tree.getNodeList().iterator();
            while (it.hasNext()) {
                Node node = (Node) it.next();
                TreePanel.getTreePanel().setNodeComplete(node);
            }            
            it = tree.getImages().iterator();
            while (it.hasNext()) {
                NodeImage img = (NodeImage) it.next();
                img.initializeThumbnail();
            }        
            controller.setDownloadComplete(true);
        } catch(Exception error) {
            error.printStackTrace();
            controller.setDefaultCursors();
	}
    }
}
