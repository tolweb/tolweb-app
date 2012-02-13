package org.tolweb.treegrow.main;

import java.awt.event.*;
import org.tolweb.treegrow.tree.TreePanel;

/**
 * JFrame subclass used to show a window that allows users to change their
 * preferences.
 */
public class PreferencesFrame extends ToLJFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = -239271543030132939L;
	protected PreferencesPanel prefPanel = null;

    public PreferencesFrame(String title) {
        super(title);
        setResizable(false);
        try {
            throw new RuntimeException();
        } catch (Exception e) {
        }
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if( TreePanel.getTreePanel() == null) {
                    System.exit(0);
                }
            }
        });

        prefPanel = new PreferencesPanel(this);
        getContentPane().add(prefPanel);
    }
    
    public PreferencesPanel getPreferencesPanel() {
            return prefPanel;
    }
}
