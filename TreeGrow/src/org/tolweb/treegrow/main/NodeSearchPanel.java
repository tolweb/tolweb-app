/*
 * NodeSearchPanel.java
 *
 * Created on February 18, 2004, 12:00 PM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import layout.TableLayout;
import org.jdom.*;

import org.tolweb.treegrow.tree.*;

/**
 *
 * @author  dmandel
 */
public class NodeSearchPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4257874007109328110L;
	private DefaultListModel resultsListModel;
    private JList listBox;
    private JTextField nodeNameField;
    private JButton findNodeButton;
    
    /** Creates a new instance of NodeSearchPanel */
    public NodeSearchPanel(String label) {
        double[][] size = {{10, .33, .33, .33, 10}, {10, 25, 10, 175, 10}};
        setLayout(new TableLayout(size));
        JPanel findPanel = new JPanel(new BorderLayout());
        JLabel attachLabel = new JLabel(label);
        findPanel.add(attachLabel, BorderLayout.WEST);
        nodeNameField = new JTextField();
        nodeNameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    findNodeButton.doClick();
                }
            }
        });
        findPanel.add(nodeNameField, BorderLayout.CENTER);
        
        findNodeButton = new JButton("Find node");
        findNodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        findNodeButton.setEnabled(false);
                        doNodeSearch();
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        findNodeButton.setEnabled(true);
                    }
                };
                thread.start();
            }
        });
        
        findPanel.add(findNodeButton, BorderLayout.EAST);
        add(findPanel, "1,1,3,1");
        resultsListModel = new DefaultListModel();
        listBox = new JList(resultsListModel);
        listBox.setCellRenderer(new NodeOrSearchResultCellRenderer());

        add(new JScrollPane(listBox), "1,3,3,3");        
        
    }
    
    public Object getSelectedValue() {
        return listBox.getSelectedValue();
    }
    
    public void addListSelectionListener(ListSelectionListener listener) {
        listBox.addListSelectionListener(listener);
    }
    
    private void doNodeSearch() {
        Controller controller = Controller.getController();
        CheckNetConnection netConnection = new CheckNetConnection();
        resultsListModel.clear();
        if (netConnection.isConnected() < 0) {
            String NO_CONNECTION_LOCAL_NODES_ONLY = controller.getMsgString("NO_CONNECTION_LOCAL_NODES_ONLY");
            JOptionPane.showMessageDialog(this, NO_CONNECTION_LOCAL_NODES_ONLY, "No connection", JOptionPane.INFORMATION_MESSAGE);
        }
        Iterator it = TreePanel.getTreePanel().getTree().getCheckedOutNodes().iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.getName().toLowerCase().indexOf(nodeNameField.getText().toLowerCase()) != -1) {
                resultsListModel.addElement(node);
            }
        }
        if (netConnection.isConnected() > 0) {
            // Search the database for nodes with this name
            NodeNameSearcher searcher = new NodeNameSearcher();
            Object results;
            try {
                results = searcher.searchDatabase(nodeNameField.getText(), 0);
                Object[] currentResults = resultsListModel.toArray();
                if (results == null) {
                    throw new RuntimeException();
                } else if (results instanceof Element) {
                    String errorString = searcher.getSearchErrorString((Element) results);
                    if (!(resultsListModel.getSize() > 0 && errorString.equals(controller.getMsgString("NO_RECORD_FOUND")))) {
                        JOptionPane.showMessageDialog(this, errorString, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (results instanceof Stack) {
                    boolean doAdd;
                    Stack stack = (Stack) results;
                    while (!stack.isEmpty()) {
                        doAdd = true;
                        NodeDetails details = (NodeDetails) stack.pop();
                        for (int i = 0; i < currentResults.length; i++) {
                            if (((Node) currentResults[i]).getName().equals(details.getNodeName())) {
                                doAdd = false;
                            }
                        }
                        if (doAdd) {
                            resultsListModel.addElement(details);
                        }
                    }
                    listBox.setSelectionInterval(0, 0);
                }
            } catch (Exception e) {
                String PROBLEM_SEARCHING = controller.getMsgString("PROBLEM_SEARCHING");
                JOptionPane.showMessageDialog(this, PROBLEM_SEARCHING, "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }
    
    private class NodeOrSearchResultCellRenderer extends DefaultListCellRenderer {
        /**
		 * 
		 */
		private static final long serialVersionUID = 895345196038082566L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Node) {
                ((JLabel) result).setText(((Node) value).getName());
            } else {
                ((JLabel) result).setText(((NodeDetails) value).getNodeName());
            }
            return result;
        }  
    }    
}
