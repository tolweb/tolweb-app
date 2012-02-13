package org.tolweb.treegrow.tree;

import java.io.*;
import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.page.*;

/**
 * OtherName is a class that holds the information regarding a synonym 
 * of a node.
 * <p>
 * This class hold the detail as to whether a synonym is a label or not
 * and also what the synonym is.
 *
 */
public class OtherName extends OrderedObject implements AuxiliaryChangedFromServerProvider, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5708020142204617054L;

	/**
    * Holds the synonym
    */
    private String name;

    /** 
     *The authority by which the name is appropriate - usually a 
     * literature reference
     */
    private String authority = "";

    /** 
     * Date of the authority reference
     */
    private int date;

    /** 
     * Node for which the othername is a synonym
     */
    private Node node;

    /**
     * Possible subtitles
     */
    private boolean isImportant;

    /**
     * Possible title to replace the node's actual name
     */        
    private boolean isPreferred;

    /**
    * Holds information as to whether the synonym detail has been changed or not.
    * true  - change has been made.
    * false - no change has been made.
    */
    private boolean changedFromServer;
    
    private int id;

    /**
    * Constructor for this class.
    *
    * @param syn	synonym
    * 	 label	information as to whether this synonym is a label or not.
    */
    public OtherName(Node n, String syn, String auth, int dt) {
        node = n;
        name = syn;
        authority = auth;
        date = dt;
    }

    public OtherName(Node n) {
        this(n,"<<< New Other Name >>>", null, 0);
    }
    
    public OtherName() {
        super();
    }

    public int getId() {
        return id;
    }
    
    public void setId(int value) {
        id = value;
    }

    /**
    * sets the synonym
    *
    * @param syn	synonym
    * @return
    */
    public void setName(String syn) {
        name = syn;
    }

    /**
    * Returns the synonym
    *
    * @hibernate.property
    * @param
    * @return	synonym string
    */
    public String getName() {
    	return name;
    }

    public void setAuthority(String auth) {
        authority = auth;
    }

    /**
     * @hibernate.property
     */
    public String getAuthority() {
        return authority;
    }

    public void setDate(int value) {
        date = value;
    }

    public int getDate() {
        return date;
    }

    public void setIsImportant(boolean value) {
        isImportant = value;
    }

    public boolean isImportant() {
        return isImportant;
    }
    
    /**
     * @hibernate.property column="is_important"
     */
    public boolean getIsImportant() {
        return isImportant();
    }

    public void setIsPreferred(boolean value) {
        isPreferred = value;
    }
    
    /**
     * @hibernate.property column="is_preferred"
     */
    public boolean getIsPreferred() {
        return isPreferred();
    }

    public boolean isPreferred() {
        return isPreferred;
    }

    public boolean auxiliaryChangedFromServer() {
        return node.getOtherNamesChanged();
    }

    public boolean changedFromServer() {
        return changedFromServer;
    }

    public void setAuxiliaryChangedFromServer(boolean value) {
        node.setOtherNamesChanged(value);
    }

    public void setChangedFromServer(boolean value) {
        changedFromServer = value;
    }
    
    public void setNode(Node value) {
        node = value;
    }
    
    public Node getNode() {
        return node;
    }    
}
