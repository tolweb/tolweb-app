
package org.tolweb.treegrow.tree;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import layout.TableLayout;
import layout.TableLayoutConstants;

import org.tolweb.treegrow.main.*;

/**
 * JFrame that allows the user to enter the text they'd like to search for
 * in the tree window. They should be able to search for successive nodes
 * with a name containing the string they enter
 */
public class FindNodeFrame extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2679513434518027342L;
	private JTextField patternField;
    private JButton findButton;
    private int findCounter = 1;
    private TreePanel treePanel;
    
    /** Creates a new instance of FindNodeFrame */
    public FindNodeFrame() {
        super(Controller.getController().getTreeEditor(), "Find node in this window", false);
        
        treePanel = TreePanel.getTreePanel();
        
        Container contentPane = getContentPane();
        setResizable(false);
        setSize(250,100);
        
        double [][] size = new double[][] {{10, TableLayoutConstants.FILL, 10}, {10, 20, 10, 30, 10}};
        contentPane.setLayout(new TableLayout(size));
        
        patternField = new JTextField("");
        contentPane.add(patternField, "1,1");
        
        contentPane.add(buildButtonPanel(), "1,3");
    }
        
    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        findButton = new JButton("Find");
        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                find();
            }
        });       
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        if (Controller.getController().isMac()) {
            buttonPanel.add(closeButton);
            buttonPanel.add(findButton);
        } else {
            buttonPanel.add(findButton);
            buttonPanel.add(closeButton);
        }
    
        return buttonPanel;
    }
    

    private void find() {
        //Not particularly efficient...but it doesn't seem to matter
        ArrayList matches = treePanel.getTree().findNodesPartialName(patternField.getText()) ;
        int matchSize = matches.size();
        Node foundNode = null;
        
        while (foundNode == null) {
            if (matchSize < findCounter) {
                break;
            }
            foundNode = (Node)(matches.get(findCounter-1));                
            if (!foundNode.getCheckedOut()) {
                findCounter++;
                foundNode = null;
            }
        }
        
        if (foundNode != null) {
            Controller.getController().getTreeEditor().toFront();
            NodeView view = treePanel.bringNodeToViewPort(foundNode);
            view.getTextField().requestFocus();
            findCounter++;   
            findButton.setText("Find Again");
        } else {
            String noMatch = (findCounter==1 ? "No match found" : "No more matches");
            JOptionPane.showMessageDialog(
                      this,
                      noMatch,
                      "No Matches",
                      JOptionPane.INFORMATION_MESSAGE);            
            findCounter = 1;
            findButton.setText("Find");            
        }
        validate();
        repaint();        

    }
    
}
