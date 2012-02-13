/*
 * CancelDownloadFrame.java
 *
 * Created on August 19, 2003, 4:45 PM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.tolweb.treegrow.tree.*;

/**
 * Frame that pops up during a download that allows the user to cancel that 
 * download
 */
public class CancelDownloadFrame extends ToLJFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6142566714684812926L;
	private JLabel statusLabel;
    
    /** Creates a new instance of CancelDownloadFrame */
    public CancelDownloadFrame() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel labelAndHolderPanel = new JPanel();
        statusLabel = new JLabel("Press the cancel button to cancel the download                    ");
        labelAndHolderPanel.add(statusLabel);
        String version = System.getProperty("java.vm.version");
        if (version.indexOf("1.3") == -1) {
            JProgressBar progress = new JProgressBar();
            progress.setIndeterminate(true);
            labelAndHolderPanel.add(progress);
        }
        panel.add(BorderLayout.CENTER, labelAndHolderPanel);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Controller.getController().cancelDownload();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        panel.add(BorderLayout.SOUTH, buttonPanel);
        getContentPane().add(panel);
        setSize(300, 100);
        pack();
        show();
    }
    
    public void dispose() {
        TreeFrame frame = Controller.getController().getTreeEditor();
        if (frame != null) {
            frame.requestFocus();
            TreePanel.getTreePanel().requestFocus();
        }
        super.dispose();
    }
    
    /**
     * Called by Controller to update the download status
     *
     * @param value The new status to draw on the frame
     */
    public void updateStatus(String value) {
        statusLabel.setText(value);
    }
}
