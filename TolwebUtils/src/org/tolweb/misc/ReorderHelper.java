/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import org.tolweb.hibernate.AccessoryPageNode;
import org.tolweb.treegrow.main.OrderedObject;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReorderHelper {
    public boolean getCanMoveLeft(Collection coll, int index) {
        return getCanMoveLeft(coll, index, 0);
    }
    
    public boolean getCanMoveLeft(Collection coll, int index, int lowIndex) {
        return coll.size() > 1 && index == coll.size() - 1 && index > lowIndex;
    }
    
    public boolean getCanMoveRight(Collection coll, int index) {
        return coll.size() > 1 && index == 0;
    }
    
    public boolean getCanMoveBoth(Collection coll, int index) {
        return getCanMoveBoth(coll, index, 0);
    }
    
    public boolean getCanMoveBoth(Collection coll, int index, int lowIndex) {
        return coll.size() > 1 && index > lowIndex && index != (coll.size() - 1);
    }
    
    public Object removeObject(int index, SortedSet set) {
        Object[] array = set.toArray();
        Object objToDelete = array[index];
        set.remove(objToDelete);
        return objToDelete;
    }
    
    public Object getObject(int index, SortedSet set) {
        Object[] array = set.toArray();
        return array[index];
    }
    
	public void setPrimaryGroup(SortedSet set, Long value) {
	    Iterator it = set.iterator();
	    while (it.hasNext()) {
	        AccessoryPageNode node = (AccessoryPageNode) it.next();
	        if (node.getNode().getNodeId().equals(value)) {
	            node.setIsPrimaryAttachedNode(true);
	        } else {
	            node.setIsPrimaryAttachedNode(false);
	        }
	    }
	}
	
	public Long getPrimaryGroup(SortedSet set) {
	    Iterator it = set.iterator();
	    while (it.hasNext()) {
	        AccessoryPageNode node = (AccessoryPageNode) it.next();
	        if (node.getIsPrimaryAttachedNode()) {
	            return node.getNode().getNodeId();
	        }
	    }
	    return null;
	}    
    
    public void doSwap(int index, boolean moveLeft, SortedSet set) {
        int otherIndex = moveLeft ? (index - 1) : (index + 1);
        Object[] array = set.toArray();
        OrderedObject obj = (OrderedObject) array[index];
        OrderedObject otherObj = (OrderedObject) array[otherIndex];
        set.remove(obj);
        set.remove(otherObj);
        int otherOrder = otherObj.getOrder();
        otherObj.setOrder(obj.getOrder());
        obj.setOrder(otherOrder);
        set.add(otherObj);
        set.add(obj);
    } 
    
    public void addToSet(SortedSet set, OrderedObject obj) {
        if (set != null) {
            if (set.isEmpty()) {
                obj.setOrder(0);
            } else {
                OrderedObject last = (OrderedObject) set.last();
                obj.setOrder(last.getOrder() + 1);
            }
            set.add(obj);
        }
    }
}
