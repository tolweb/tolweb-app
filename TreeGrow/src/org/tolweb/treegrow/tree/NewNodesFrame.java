package org.tolweb.treegrow.tree;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;

import org.tolweb.treegrow.tree.undo.*;
import org.tolweb.treegrow.main.*;

/**
 * Frame that pops up when the user attempts to add multiple nodes
 * (holds option key down with "add node" tool active, and clicks on node).
 * Allows entry of delimited list of nase, and/or creation of multiple 
 * unnamed nodes.
 */
public class NewNodesFrame extends ToLJFrame implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3873007583124221577L;
	private static final String NEWLINE_STRING = "Newline";
    private static final String COMMA_STRING = "Comma";
    private static final String TAB_STRING = "Tab";
    private static final String OTHER_STRING = "Other";
    
    private JTextField locationField;
    private JTextField otherDelimiterTextField;
    private JTextField numberUnnamedNodesField;
    private ButtonGroup buttonGroup;
    private ButtonModel otherModel;
    private JTextArea newTaxaArea;
    private Node node;
    private JTabbedPane tabbedPane;
    private JPanel unnamedNodesPanel, delimitedNodesPanel;
    
    private static File lastDirectory;    
    
    /** 
     * Creates the frame and lays out it's components
     */
    public NewNodesFrame(Node value, TreeFrame treeFrame) {
        super();
        node = value;
        setTitle("Add Subgroups");
        getContentPane().setLayout(new BorderLayout(0, 10));
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Enter List of Names", addDelimitedNodesPanel());
        tabbedPane.addTab("Add Unnamed Nodes", addUnnamedNodesPanel());
        tabbedPane.addTab("Import Names From File", addImportFromFilePanel());
        getContentPane().add(BorderLayout.CENTER, tabbedPane);
        getContentPane().add(BorderLayout.SOUTH, addButtonsPanel());
        pack();
        show();
        setSize(new Dimension(500, 300));
        toFront();
    }
    
    /**
     * Panel allowing user to select number of unnamed nodes to add
     */
    private JPanel addUnnamedNodesPanel() {
        unnamedNodesPanel = new JPanel(new BorderLayout());
        JPanel internalPanel = new JPanel();
        internalPanel.add(new JLabel("  Add "));
        numberUnnamedNodesField = new JTextField("0", 3);
        numberUnnamedNodesField.setSelectionStart(0);
        numberUnnamedNodesField.setSelectionEnd(1);
        internalPanel.add(numberUnnamedNodesField);
        internalPanel.add(new JLabel("unnamed nodes"));
        unnamedNodesPanel.add(internalPanel, BorderLayout.WEST);
        return unnamedNodesPanel;
    }

    /**
     * Panel allowing user to enter a list of delimeted nodes
     */    
    private JPanel addDelimitedNodesPanel() {
        delimitedNodesPanel = new JPanel(new BorderLayout());
        addDelimitersPanel(delimitedNodesPanel);
        addTextAreaPanel(delimitedNodesPanel);
        return delimitedNodesPanel;
    }
    
    /**
     * Panel allowing user to pick which delimeter they want to use
     */    
    private void addDelimitersPanel(JPanel parent) {
        JPanel delimitersPanel = new JPanel();
        parent.add(BorderLayout.NORTH, delimitersPanel);
        delimitersPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,0));
        
        JLabel label = new javax.swing.JLabel("Pick Delimiter:  ");
        delimitersPanel.add(label);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setMaximumSize(new Dimension(100,80));
        buttonsPanel.setLayout(new java.awt.GridLayout(2,3));
        
        buttonGroup = new javax.swing.ButtonGroup();
        
        addRadioButton(NEWLINE_STRING, buttonsPanel, true);
        addRadioButton(COMMA_STRING, buttonsPanel);
        JRadioButton hiddenBtn = addRadioButton("Hidden", buttonsPanel);
        hiddenBtn.setVisible(false);
        addRadioButton(TAB_STRING, buttonsPanel);
        otherModel = addRadioButton(OTHER_STRING, buttonsPanel).getModel();
             
        delimitersPanel.add( buttonsPanel);
        
        otherDelimiterTextField = new JTextField(1);    
        otherDelimiterTextField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                buttonGroup.setSelected(otherModel, true);
            }
        });
        buttonsPanel.add(otherDelimiterTextField);
    }

    /**
     * Panel containing the textfield into which list of delimited names 
     * is added.
     */    
    private void addTextAreaPanel(JPanel parent) {
        JPanel textAreaPanel = new JPanel();
        parent.add(BorderLayout.CENTER, textAreaPanel);
        textAreaPanel.setLayout(new BorderLayout());
        newTaxaArea = new JTextArea();
        JScrollPane scroller = Controller.getController().updateUnitIncrement(new JScrollPane(newTaxaArea));
        //scroller.add(area);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setPreferredSize(new Dimension(100, 100));
        textAreaPanel.add(BorderLayout.CENTER, scroller);
        textAreaPanel.setPreferredSize(new Dimension(100, 100));

    }
    /**
     * Panel with ok/cancel buttons
     */
    private JPanel addButtonsPanel() {
        JPanel actionButtonsPanel = new JPanel(new BorderLayout());
        //actionButtonsPanel.add(new JSeparator(), BorderLayout.CENTER);
        JPanel internalPanel = new JPanel();
        JButton addNodesButton = new JButton("Add Nodes");
        addNodesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doImport();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        if (Controller.getController().isMac()) {
            internalPanel.add(cancelButton);
            internalPanel.add(addNodesButton);      
        } else {
            internalPanel.add(addNodesButton);            
            internalPanel.add(cancelButton);       
        }
        actionButtonsPanel.add(internalPanel, BorderLayout.SOUTH);
        return actionButtonsPanel;
        //getContentPane().add(BorderLayout.SOUTH, actionButtonsPanel);
    }
    
    /**
     * Used by addDelimitersPanel to add radio button options.
     * - Default is "unselected"
     */
     
    private JRadioButton addRadioButton(String str, JPanel panel) {
        return addRadioButton(str, panel, false);
    }

    /**
     * Used by addDelimitersPanel to add radio button options.
     */    
    private JRadioButton addRadioButton(String str, JPanel panel, boolean isSelected) {
        javax.swing.JRadioButton radio = new javax.swing.JRadioButton(str);
        panel.add(radio);
        buttonGroup.add(radio);
        radio.setActionCommand(str);
        radio.addActionListener(this);
        if (isSelected) {
            radio.setSelected(true);
        }
        return radio;
    }
    
    private JPanel addImportFromFilePanel() {
        JPanel fieldAndButtonPanel = new JPanel(new BorderLayout());
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ToLFileChooser fileChooser = new ToLFileChooser(NewNodesFrame.this, "Choose File");
                if (lastDirectory != null) {
                    fileChooser.setDirectory(lastDirectory);
                }
                fileChooser.show();
                String selectedFileName = fileChooser.getSelectedFileName();
                lastDirectory = fileChooser.getDirectory();
                if (selectedFileName != null && !selectedFileName.endsWith("null")) {
                    locationField.setText(selectedFileName);
                }
            }
        });
        locationField = new JTextField();
        fieldAndButtonPanel.add(locationField, BorderLayout.CENTER);
        fieldAndButtonPanel.add(browseButton, BorderLayout.EAST);
        JPanel holderPanel = new JPanel(new BorderLayout());
        holderPanel.add(fieldAndButtonPanel, BorderLayout.NORTH);
        return holderPanel;
    }

    /**
     * Invokes undoable edit to add the list of delimited and unnamed nodes.
     */    
    private void doImport() {
        Controller controller = Controller.getController();
        ArrayList newNames = new ArrayList();
        Component selectedComponent = tabbedPane.getSelectedComponent();
        List newNodes;
        if (selectedComponent == unnamedNodesPanel) {
            //Add unnamed nodes to the list
            int numFields;
            try {
                numFields = new Integer(numberUnnamedNodesField.getText().trim()).intValue();        
            } catch (Exception ex) {
                numFields = 0;
            }
            if (numFields <= 0) {
                JOptionPane.showMessageDialog(this, controller.getMsgString("NEW_NODES_NO_NUMBER"), "No Names or Numbers Entered", JOptionPane.ERROR_MESSAGE);        
                return;
            }            
            for (int i=0 ; i<numFields; i++) {
                newNames.add("");
            }
            newNodes = createNodesWithNames(newNames);
        } else if (selectedComponent == delimitedNodesPanel) {
            String delimiter;
            String selectedString = buttonGroup.getSelection().getActionCommand();
            if (selectedString.equals(COMMA_STRING)) {
                delimiter = ",";
            } else if (selectedString.equals(TAB_STRING)) {
                delimiter = "\t";
            } else if (selectedString.equals(NEWLINE_STRING)) {
                delimiter = "\n";
            } else {
                delimiter = otherDelimiterTextField.getText();
                if (delimiter == null || delimiter.equals("")) {
                    JOptionPane.showMessageDialog(this, controller.getMsgString("NEW_NODES_NO_CUSTOM_DELIMITER"), "No Delimiter Selected", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            String newTaxaText = newTaxaArea.getText();
            if ((newTaxaText == null || newTaxaText.equals(""))) {
                JOptionPane.showMessageDialog(this, controller.getMsgString("NEW_NODES_NO_NAMES"), "No Names or Numbers Entered", JOptionPane.ERROR_MESSAGE);        
                return;
            }
            StringTokenizer tokenizer = new StringTokenizer(newTaxaText, delimiter);        // do some checking and run import
            while (tokenizer.hasMoreTokens()) {
                String currentString = tokenizer.nextToken();
                newNames.add(currentString.trim());
            }
            newNodes = createNodesWithNames(newNames);
        } else {
            newNodes = TabDelimitedNameParser.parseNames(locationField.getText());
            if (newNodes == null) {
                JOptionPane.showMessageDialog(this, controller.getMsgString("BAD_TAB_FORMAT"), "Problems Reading File", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        AddTaxaUndoableEdit edit = new AddTaxaUndoableEdit(node, newNodes);
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.updateUndoStuff(edit);
        dispose();
    }
    
    private List createNodesWithNames(List names) {
        ArrayList nodes = new ArrayList(); 
        for (Iterator iter = names.iterator(); iter.hasNext();) {
            String nextName = (String) iter.next();
            Node node = new Node();
            node.setName(nextName);
            nodes.add(node);
        }
        return nodes;
    }
    
    /** 
     *Invoked when user selects "other delimeter" radio button
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(OTHER_STRING)) {
            otherDelimiterTextField.setEnabled(true);
            otherDelimiterTextField.requestFocus();
        }
    }
    
}
