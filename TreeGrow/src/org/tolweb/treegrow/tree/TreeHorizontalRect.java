package org.tolweb.treegrow.tree;
/*
 * TreeHorizontalRect.java
 *
 * Created on June 3, 2003, 10:03 AM
 */

 /** Mouse-motion monitoring rectangle on the horzontal line going into this
  * object's {@link Node}.
 */
public class TreeHorizontalRect extends AbstractTreeShape {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1294693659023255811L;

	/** Creates a new instance of TreeHorizontalRect
     *
     * @param  x x-position of the rectangle on the TreePanel
     *         y y-position of the rectangle on the TreePanel
     *         width width of the rectangle on the TreePanel
     *         height height of the rectangle on the TreePanel
     *         n the node that this horizontal rect goes into
     **/
    public TreeHorizontalRect(int x, int y,  int width, int height, NodeView v) {
        super(x, y, width, height, v);
    }
    
}
