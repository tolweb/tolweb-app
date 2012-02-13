package org.tolweb.treegrow.tree;

/** Abstract class used to define common mehtods for the mouse-motion 
 * monitoring rectangle used on the TreePanel
 **/
public abstract class AbstractTreeShape extends java.awt.Rectangle implements Comparable {
    
    protected NodeView view;
    
    /** Establsh position and node associated with this shape
     * 
     * @param  x x-position of the rectangle on the TreePanel
     *         y y-position of the rectangle on the TreePanel
     *         width width of the rectangle on the TreePanel
     *         height height of the rectangle on the TreePanel
     *         n the node with which this shape is associated
     **/
    public AbstractTreeShape(int x, int y, int width, int height, NodeView v) {
        super(x, y, width, height);
        view = v;
    }
    
    
    public NodeView getNodeView () {
        return view;
    }
    
    /** Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     *
     * General idea lesser Y value = less than. If Y values are the same, 
     * lesser X-value = less than.
     *
     * Specifically: 
     *      return -1 if this.Y<o.Y
     *      return +1 if this.Y>o.Y
     *      if this.Y == o.Y {
     *          return -1 if this.X<o.X
     *          return +1 if this.X>o.X
     *          return 0 if this.X=o.X
     *      }
     */
    public int compareTo(Object o) {
        AbstractTreeShape ts = (AbstractTreeShape)o;
        
        if  ( getY() < ts.getY() ) {
            return -1; 
        } else if ( getY() > ts.getY() ) {
            return 1; 
        } else { //getY() == ts.getY() 
            if  ( getX() < ts.getX() ) {
                return -1; 
            } else if ( getX() > ts.getX() ) {
                return 1; 
            } else { //getY() == ts.getY()             
                return 0;
            }
        }        
    }
    
}
