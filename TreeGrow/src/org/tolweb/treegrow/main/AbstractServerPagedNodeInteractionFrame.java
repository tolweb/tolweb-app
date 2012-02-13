/*
 * UploadCheckInSubtreeFrame.java
 *
 * Created on September 3, 2003, 2:06 PM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.tolweb.treegrow.tree.*;

/**
 * Abstract frame that allows users to either check in or upload subtree
 * nodes
 */
public abstract class AbstractServerPagedNodeInteractionFrame extends ToLJFrame {
    private Vector pagedNodes;
    protected JList nodeSelectionList;
    
    /** Creates a new instance of UploadCheckInSubtreeFrame */
    public AbstractServerPagedNodeInteractionFrame() {
        pagedNodes = getNodeList();
        Collections.sort(pagedNodes, new NodeNameComparator());
        
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(BorderLayout.NORTH, new JLabel(getLabelText()));
        nodeSelectionList = new JList(pagedNodes);
        nodeSelectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nodeSelectionList.setCellRenderer(new NodeCellRenderer());
        getContentPane().add(BorderLayout.CENTER, Controller.getController().updateUnitIncrement(new JScrollPane(nodeSelectionList)));
        
        JPanel buttonsPanel = new JPanel();
        JButton submitButton = new JButton(getSubmitButtonText());
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                interactForSelectedNode();
            }
        });
        buttonsPanel.add(submitButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonsPanel.add(cancelButton);
        
        getContentPane().add(BorderLayout.SOUTH, buttonsPanel);
        pack();
        show();
    }
    
    protected Vector getNodeList() {
       return new Vector(TreePanel.getTreePanel().getTree().getNonNewNodesWithPages());
    }
    
    /**
     * Performs the server interaction for the subclass (checkin or upload)
     */
    public abstract void interactForSelectedNode();
    
    /**
     * Returns the text for the label that has user instruction
     */
    protected abstract String getLabelText();
    
    /**
     * Returns the text for the submit button
     */
    protected abstract String getSubmitButtonText();
}
