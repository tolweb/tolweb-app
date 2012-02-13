package org.tolweb.treegrow.tree;
/*
 * TreeCircle.java
 *
 * Created on June 3, 2003, 10:01 AM
 */


 /** Mouse-motion monitoring rrectangle on the circle that represents this
  * object's {@link Node}.
 */
public class TreeCircle extends AbstractTreeShape {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3782927375773018914L;

	/** Creates a new instance of TreeCircle
     *
     * @param  x x-position of the rectangle on the TreePanel
     *         y y-position of the rectangle on the TreePanel
     *         width width of the rectangle on the TreePanel
     *         height height of the rectangle on the TreePanel
     *         n the node that this circle covers
     **/
    public TreeCircle(int x, int y, int width, int height, NodeView v) {
        super(x, y, width, height, v);
    }
    
}
