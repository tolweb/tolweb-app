/*
 * AbstractSubtreeMenu.java
 *
 * Created on October 22, 2003, 9:24 AM
 */

package org.tolweb.treegrow.main;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import org.tolweb.treegrow.tree.*;

/**
 *
 * @author  dmandel
 */
public abstract class AbstractSubtreeMenu extends JMenu implements ActionListener {
    private static NodeNameComparator comparator;
    private ArrayList nodes;
    
    static {
        comparator = new NodeNameComparator();
    }
    
    /** Creates a new instance of AbstractSubtreeMenu */
    public AbstractSubtreeMenu(String name) {
        super(name);
        addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                System.out.println("menu selected in subtree getting called");
                rebuildItems();
            }
            
            public void menuCanceled(MenuEvent e) {}
            public void menuDeselected(MenuEvent e) {}
        });
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("action listener in subtree getting called");
                rebuildItems();
            }
        });
    }
    
    public void rebuildItems() {
        System.out.println("getting called");                                                                                                                                                                                                                                                                                                                                                                             
        removeAll();
        nodes = getNodes();
        Collections.sort(nodes, comparator);
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            System.out.println("adding: " + node.getName());
            JMenuItem item = new NodeMenuItem(node);
            item.addActionListener(AbstractSubtreeMenu.this);
            add(item);
        }
        validate();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        NodeMenuItem item = (NodeMenuItem) e.getSource();
        interactForSelectedNode(item.getNode());
    }
    
    protected abstract ArrayList getNodes();
    protected abstract void interactForSelectedNode(Node n);
       
    protected class NodeMenuItem extends JMenuItem {
        /**
		 * 
		 */
		private static final long serialVersionUID = 911583398130976655L;
		private Node node;
        
        public NodeMenuItem(Node n) {
            super(n.getName());
            node = n;
        }
        
        public Node getNode() {
            return node;
        }
    }
}
