/*
 * ShowHideButton.java
 *
 * Created on July 31, 2003, 3:14 PM
 */

package org.tolweb.treegrow.page;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import layout.TableLayout;
import layout.TableLayoutConstants;

/**
 * JPanel subclass that displays a little triangle button that allows the user
 * to hide the editor components contained within the panel
 */
public class ShowHidePanel extends JPanel implements MouseListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2217780212315492865L;
	private JLabel toggleArrow;
    private JPanel togglePanel;
    private JPanel actualPanel;
    private JLabel label;
    private ActionListener additionalListener;
    private static ImageIcon selectedIcon, notSelectedIcon;
    private static final int LABEL_HEIGHT = 20;

    static {
        selectedIcon = new ImageIcon(ClassLoader.getSystemResource("Images/downarrow.gif"));
        notSelectedIcon = new ImageIcon(ClassLoader.getSystemResource("Images/rightarrow.gif"));
    }    

    public ShowHidePanel(String labelStr, JPanel p,  ActionListener listener, boolean isSelected) {    
        this(labelStr, p, listener, isSelected, null);
    }
    
    /** Creates a new instance of ShowHidePanel */
    public ShowHidePanel(String labelStr, JPanel p,  ActionListener listener, boolean isSelected, JLabel additionalLabel) {
        actualPanel = p;
        additionalListener = listener;
        
        toggleArrow = new JLabel(notSelectedIcon);
        toggleArrow.setOpaque(false);
        actualPanel.setVisible(isSelected);
        
        togglePanel = new JPanel();
        togglePanel.setLayout(new BorderLayout());
        label = new JLabel(labelStr);
        label.addMouseListener(this);
        togglePanel.add(BorderLayout.WEST, label);
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(5, 5));
        togglePanel.add(BorderLayout.CENTER,spacerPanel);
        togglePanel.add(BorderLayout.EAST, toggleArrow);
        JPanel toggleHolderPanel = new JPanel();
        toggleHolderPanel.setLayout(new BorderLayout());
        toggleHolderPanel.add(BorderLayout.WEST, togglePanel);
        toggleArrow.addMouseListener(this);
        setLayout(new BorderLayout());
        if (additionalLabel == null) {
            add(BorderLayout.NORTH, toggleHolderPanel);
        } else {
            JPanel holderPanel = new JPanel();
            double[][] size = new double[][] {{TableLayoutConstants.FILL, 10, 30}, {TableLayoutConstants.FILL}};
            holderPanel.setLayout(new TableLayout(size));
            holderPanel.add(toggleHolderPanel, "0,0");
            holderPanel.add(additionalLabel, "2,0");
            //JPanel holderPanel = new JPanel(new BorderLayout());
            //holderPanel.add(toggleHolderPanel, BorderLayout.WEST);
            JPanel holderPanelTwo = new JPanel(new BorderLayout());
            holderPanelTwo.add(holderPanel, BorderLayout.WEST);
            //holderPanel.add(additionalLabel);
            add(BorderLayout.NORTH, holderPanelTwo);
        }
        add(BorderLayout.CENTER, actualPanel);
    }
    
    public ShowHidePanel(String label, JPanel panel, ActionListener listener) {
        this(label, panel, listener, false, null);
    }
    
    public ShowHidePanel(String label, JPanel panel) {
        this(label, panel, null);
    }    
  
    public boolean isSelected() {
        return toggleArrow.getIcon() == selectedIcon;
    }
    
    /**
     * Opens or closes the folder (and the panel contained within) depending
     * on value
     *
     * @param value Whether to open or close the panel
     */
    public void setSelected(boolean value) {
        if ( value ) {
            toggleArrow.setIcon(selectedIcon);
        } else {
            toggleArrow.setIcon(notSelectedIcon);            
        }
        actualPanel.setVisible(value);
        if (!value) {
            // Set the preferred size to be small since the mac doesn't want
            // to cooperate
            setPreferredSize(new Dimension(getPreferredSize().width, LABEL_HEIGHT));
        } else {
            setPreferredSize(new Dimension(getPreferredSize().width, actualPanel.getPreferredSize().height + LABEL_HEIGHT));
        }        
        validate();
        // Normally we could just add the additional listener as a listener to
        // the button, but we want to make sure that things have expanded 
        // before that listener gets called
        if (additionalListener != null) {
            additionalListener.actionPerformed(new ActionEvent(this,0,""));
        }
    }
    
    /**
     * Overridden to hide the actual panel if it is currently selected and
     * to disable the buttons so things cannot be opened.
     *
     * @param value Whether to enable this component or not
     */
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        if (!value) {
            setSelected(false);
        }
        toggleArrow.setEnabled(value);
        label.setEnabled(value);
        Component[] components = actualPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            components[i].setEnabled(value);
        }
    }   
    
    public void mouseClicked(MouseEvent e) {
        if (isEnabled()) {
            setSelected(!isSelected());
        }
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
    }
}
