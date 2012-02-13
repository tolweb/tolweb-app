/*
 * Ancestor.java
 *
 * Created on October 1, 2003, 3:18 PM
 */

package org.tolweb.treegrow.main;

import java.io.*;
import java.util.*;

/**
 * Used to store information about ancestor nodes that aren't actually
 * present in the downloaded tree.  Currently used to for linking to ancestor
 * accessory pages
 */
public class Ancestor implements Serializable {    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7906885191641989546L;
	private int nodeID;
    private String name;
    private Vector accessoryPages;
    
    /** Creates a new instance of Ancestor */
    public Ancestor(int nodeID, String name) {
        this.nodeID = nodeID;
        this.name = name;
        accessoryPages = new Vector();
    }
    
    public void addAccPage (int id, String menu) {
        accessoryPages.add(new Ancestor.AncestorAccPage(id,menu));
    }

    public Vector getAccPages() {
        return accessoryPages;
    }
    
    public int getNodeID() {
        return nodeID;
    }
    
    public String getName() {
        return name;
    }
    
    public class AncestorAccPage implements Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = -5851796123959535967L;
		private int id;
        private String menu;
        
        public AncestorAccPage(int id, String menu) {
            this.id = id;
            this.menu = menu;
        }
        
        public String getMenu() {
            return menu;
        }
        
        public int getId() {
            return id;
        }
        
        public Ancestor getAncestor() {
            return Ancestor.this;
        }
    }
    
}

