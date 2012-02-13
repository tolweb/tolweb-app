/*
 * Created on Mar 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FetchThumbnailThread extends SwingWorker {
    private NodeImage result;
    private JPanel panelToRefresh;
    
    public FetchThumbnailThread(NodeImage r) {
        this(r, null);
    }
    
    public FetchThumbnailThread(NodeImage r, JPanel panel) {
        result = r;
        panelToRefresh = panel;
    }
    
    public Object construct() {
        String destFile = Controller.getController().getFileManager().getLocalImagePathForId("" + result.getId());
        ReadImage ri = new ReadImage(result.getThumbnailUrl(), destFile);
        ri.read();
        result.getThumbnail().setIcon(new ImageIcon(destFile));
        return null;
    }
    
    public void finished() {
        result.getThumbnail().repaint();
        if (panelToRefresh != null) {
            panelToRefresh.repaint();
        }
    }
}
