package org.tolweb.hibernate;

import java.io.Serializable;

/**
 * An extension object containing properties of MappedNode 
 * objects. 
 * 
 * Background:
 * As we tweak and extend the tol web app, we end up with the 
 * situation where we are constantly adding columns to NODES. 
 * This is pushing the table to the point where it may become 
 * unruly.  When Btol was started, the move was made to have 
 * AdditionalAttributes associated with the node instances.  
 * This seems like a desirable way to offload change, to a 
 * related "extension" object.  This hibernate object will 
 * begin with a simple few properties (this is where the 
 * geographic distribution information provided by the 
 * Lorenz Carbid catalog was stored).  With time, some of the 
 * columns living in NODES will progressively be moved into 
 * this object (or that is the intent of this, as of April 29, 
 * 2008).     
 * @author lenards
 * @since 4/29/2008
 *
 * @hibernate.class table="ExtendedNodeProperties"
 */
public class ExtendedNodeProperties implements Serializable {
    /** */
	private static final long serialVersionUID = 529196522078993138L;
	private Long id;
    private String geoDistDescription;
    
    /**
     * @hibernate.id generator-class="native" column="extendedNodePropertiesId" unsaved-value="null"
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    
	/**
	 * @return the geoDistDescription
	 * @hibernate.property 
	 */
	public String getGeoDistDescription() {
		return geoDistDescription;
	}
	/**
	 * @param geoDistDescription the geoDistDescription to set
	 */
	public void setGeoDistDescription(String geoDistDescription) {
		this.geoDistDescription = geoDistDescription;
	}	
}
