package org.tolweb.treegrow.tree;
/*
 * TreeVerticalRect.java
 *
 * Created on June 3, 2003, 10:04 AM
 */

 /** Mouse-motion monitoring rectangle on the vertical line coming out of this
  * object's {@link Node}.
 */
public class TreeVerticalRect extends AbstractTreeShape {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6039288879927383620L;
	int childIndex;
    
    /** Creates a new instance of TreeVerticalRect.
     *
     * @param  x x-position of the rectangle on the TreePanel
     *         y y-position of the rectangle on the TreePanel
     *         width width of the rectangle on the TreePanel
     *         height height of the rectangle on the TreePanel
     *         n the node to which this vertical rect is attached 
     *              (parent node of the node that this rect leads to)
     *         index  this rect monitors a vertical line, and may be 
     *              one of many doing so. Index indicates the vertical 
     *              order of this rect among all vertical rects monitoring
     *              that line
     **/
    public TreeVerticalRect(int x, int y, int width, int height, NodeView v, int index) {
        super(x, y, width, height, v);
        childIndex = index;
    }
    
    /** this rect monitors a vertical line, and may be one of many doing so. 
     * "index" indicates the vertical order of this rect among all vertical 
     * rects monitoring that line. <p>
     * For example, if the vertical rectangle is between the parent's first
     * and second child, this will return the index of the first child, 0.
     *
     * @return   the child index
     */
    public int getChildIndex () {
        return childIndex;
    }
    
}
