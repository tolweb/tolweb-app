/*
 * Created on Mar 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * @author dmandel
 *
 * JButton subclass that sets up a wait message and progress bar
 */
public class RefreshButton extends JButton {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1748800373822838591L;
	private JPanel containingPanel;
    private ActionListener additionalListener;
    
    public RefreshButton(JPanel panel, ActionListener listener) {
        super("Refresh");
        containingPanel = panel;
        additionalListener = listener;
        addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               updateContainingPanelUI();
               additionalListener.actionPerformed(e);
           }
        });
    }
    
    public void updateContainingPanelUI() {
        if (containingPanel != null) {
	        containingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
	        containingPanel.removeAll();
	        Dimension preferredSize = containingPanel.getPreferredSize();
	        containingPanel.setPreferredSize(new Dimension(preferredSize.width, 100));        
	        containingPanel.add(new JLabel("Communicating with the server, please wait."));
	        String version = System.getProperty("java.vm.version");
	        if (version.indexOf("1.3") == -1) {
	            JProgressBar progress = new JProgressBar();
	            progress.setIndeterminate(true);
	            containingPanel.add(progress);
	        }
	        containingPanel.revalidate();
	        containingPanel.repaint();        
        }
    }
}
