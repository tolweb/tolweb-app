/*
 * OrderedObject.java
 *
 * Created on October 15, 2003, 2:19 PM
 */

package org.tolweb.treegrow.main;

import java.io.Serializable;

/**
 * Generic class that allows for ordering of objects
 */
public abstract class OrderedObject implements Comparable, Serializable {
    private int order;
    
    /** Creates a new instance of OrderedObject */
    public OrderedObject() {
    }
    
    /**
    * Function to implement the Comparable interface.
    * It compares the order of the two sections. This is useful for sorting the elements
    * in a list of accessory pages.
    *
    * @param o	object of accessoryPage that has to be compared
    * @return	1 if this object is greater, 0 if equal, else return -1
    */
    public int compareTo(Object o) {
        OrderedObject other = (OrderedObject) o;
        if (order > other.getOrder()) {
            return 1;
        } else if ( order < other.getOrder()) {
            return -1;
        } else {
            return 0;
        }

    }
    
    public void setOrder(int i) {
        order = i;
    }

    public int getOrder() {
        return order;
    }   
    
	public boolean doEquals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null) {
			return false;
		} else if (!(o instanceof OrderedObject)) {
			return false;
		} else {
		    OrderedObject other = (OrderedObject) o;
			return getOrder() == other.getOrder();
		}
	}
	
	public int getHashCode() {
		return Integer.valueOf(getOrder()).hashCode();
	}
}
