
package org.tolweb.treegrow.tree;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.tolweb.treegrow.main.Controller;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;

/**
 *Abstract Class that serves as a basis for TreePanel (the main graphical
 * Tree manipulator), and PreviewTreePanel (shows 1-deep subtrees inside
 *the PageFrames)
 */
public class AbstractTreePanel extends JPanel {
    public static final int TOP_PADDING = 15;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7171185160782665446L;
	/**
     * The default size of the circle.
     */
    public static final int DEFAULT_CIRCLE_DIAMETER = 7;  
    public static final int DEFAULT_FONT_SIZE = 12;
    public static final int DEFAULT_QUESTIONMARK_FONT_SIZE = 12;
    protected int ypos, minX;
    
    protected TreeFrame treeFrame;
    /**
     * The current fontMetrics associated with the panel.
     */
    protected FontMetrics myFontMetrics;
    /**
     * The actual tree model to be drawn.
     */
    protected Tree tree;
    /**
     * Used as a placeholder on the stack for determining drawing locations
     */
    protected NodeView placeHolder;
    /**
     * Used to store arrays of the circles we need to draw
     */
    protected ArrayList circleValues = new ArrayList(); 
    /**
     * Used to store arrays of the Leaf circles we need to draw
     */
    protected ArrayList leafCircleValues = new ArrayList(); 
    /**
     * Used to store arrays of the subgroups missing circles we need to draw
     */
    protected ArrayList incompleteSubgroupsCircleValues = new ArrayList();
    /**
     * Used to store array of the locks we need to draw
     */
    protected ArrayList nodeLockValues = new ArrayList();
    protected ArrayList nodeSubmittedLockValues = new ArrayList();
    protected ArrayList nodeNoPermissionsLockValues = new ArrayList();
    /**
     * Used to store arrays of the black lines we need to draw
     */
    protected ArrayList blackLineValues = new ArrayList();  
    /**
     * Used to store arrays of the gray lines we need to draw
     */
    protected ArrayList grayLineValues = new ArrayList();
    
    protected ArrayList nodeHorizontalLines = new ArrayList();
    /**
     * Used to store points for the subtree indicator images we need to draw
     */    
    protected ArrayList subtreeIndicators = new ArrayList();
    /**
     * Used to store points for the extinction indicator images we need to draw
     */        
    protected ArrayList extinctIndicators = new ArrayList();
    /**
     * Used to store arrays of the rectangles that will be erased in order to draw indications of incertae sedis status
     */
    protected ArrayList confidenceEmptyRects = new ArrayList();
    /**
     * Used to draw rectangles indicating nodes have pages
     */
    protected ArrayList nodePageRects = new ArrayList();
    /**
     * Used to store arrays of the points at which single questionmarks will be drawn to show incertaesedis putative status
     */
    protected ArrayList confidenceSingleQmarkPoints = new ArrayList();  
    /**
     * Used to store arrays of the points at which double questionmarks will be drawn to show incertaesedis uncertain status
     */
    protected ArrayList confidenceDoubleQmarkPoints = new ArrayList();
    /**
     * Used for bookkeeping of possibly elongated nodes due to long labels
     */
    protected ArrayList nodesByTerminalDistance = new ArrayList();
    /**
     * List of view objects for this panel
     */
    protected ArrayList nodeViews = new ArrayList();
    /**
     * Hashtable associating a text field with a node
     */
    protected Hashtable nodeToNodeView = new Hashtable();
    /**
     * The current circle diameter
     */
    protected int circleDiameter = DEFAULT_CIRCLE_DIAMETER;
    /**
     * The color used to draw leaf nodes.
     */
    protected static Color leafGreen = new Color(74,178,57);
    /**
     * The color used to draw under construction nodes.
     */
    protected static Color underConstructionYellow = new Color(255,0,0);//Color.orange;
    /*
     * Used to draw the question mark for incertae sedis nodes
     */
    protected Font questionMarkFont = getQuestionMarkFont(); 
    protected Dimension singleQuestionMarkStringDimension ;
    protected Dimension doubleQuestionMarkStringDimension ;    
    
    protected int rootLineLength;
    
    protected int zoomFactor = 0;    

    /**
     *The little lock image used to indicate a locked node
     */
    private static Image downloadedLockImage;
    private static Image submittedLockImage;
    private static Image noPermissionsLockImage;

    /** 
     *The little horizontal subtree image used to indicate that a node
     *which is terminal on the treepanel has a subtree, which is currently 
     *undownloaded
     */
    private static Image subtreeImage;
    /**
     * Stores the subtree images's width.
     */        
    private static int subtreeImageWidth;
    /**
     * Stores the subtree images's height .
     */    
    private static int subtreeImageHeight;

    /**
     * The little dagger used to indicate extinct status
     */
    protected static Image extinctImage;
    /**
     * Stores the extinct images's width .
     */
    protected static int extinctImageWidth ;
    /**
     * Stores the extinct images's height .
     */
    protected static int extinctImageHeight ;    

    static {
    	try {
	        downloadedLockImage = getImageFromSystemResourcePath("Images/blacklock.gif");
	        submittedLockImage = getImageFromSystemResourcePath("Images/greylock.gif");
	        noPermissionsLockImage = getImageFromSystemResourcePath("Images/redlock.gif");
	
	        subtreeImage = getImageFromSystemResourcePath("Images/haschildren.gif");
	        subtreeImageWidth = subtreeImage.getWidth(null);
	        subtreeImageHeight = subtreeImage.getHeight(null);
	        
	        extinctImage = getImageFromSystemResourcePath("Images/sm_extinct.gif");
	        extinctImageWidth = extinctImage.getWidth(null);
	        extinctImageHeight = extinctImage.getHeight(null);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private static Image getImageFromSystemResourcePath(String path) {
    	ImageIcon imgIcon = new ImageIcon(ClassLoader.getSystemResource(path));
    	return (imgIcon != null) ? imgIcon.getImage() : null;
    }
    
    /** Creates a new instance of AbstractTreePanel */
    public AbstractTreePanel(Tree tr, TreeFrame frame) {
        super();
        tree = tr;
        treeFrame = frame;
        setFont(Controller.getController().getDefaultFont());
        myFontMetrics = getFontMetrics(getFont());
        
        if (questionMarkFont != null) {
            initQMarkDims();        	
        }
        Node dummy = new Node();
        placeHolder = new NodeView(dummy, null, null); 
        setLayout(null);
        setOpaque(true);
        nodesByTerminalDistance.add(new Integer(0));

        // Normally this would be true by default, but for whatever reason the default on OSX is false
        setDoubleBuffered(getUseDoubleBuffering());
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(getUseDoubleBuffering());
        ToolTipManager.sharedInstance().registerComponent(this);
    }

	protected boolean getUseDoubleBuffering() {
		return true;
	}

	protected void initQMarkDims() {
		FontMetrics questionMarkFontMetrics = getFontMetrics(questionMarkFont);
        int questionMarkStringHeight = questionMarkFontMetrics.getHeight()+8 ;
        
        int questionMarkStringWidth = questionMarkFontMetrics.stringWidth("?");
        singleQuestionMarkStringDimension = new Dimension(questionMarkStringWidth, questionMarkStringHeight);

        questionMarkStringWidth = questionMarkFontMetrics.stringWidth("??");
        doubleQuestionMarkStringDimension = new Dimension(questionMarkStringWidth, questionMarkStringHeight);
	}
    
    public Tree getTree() {
        return tree;
    }
    
    public TreeFrame getTreeFrame() {
        return treeFrame;
    }
    
    public int getCircleDiameter() {
        return circleDiameter;
    }

    public int getHalfCircleDiameter() {
        return circleDiameter / 2;
    }
    
    /** 
     *How far to move down for each additional terminal node
     */
    protected int getYStep() {
        return (int)(myFontMetrics.getHeight()*1.5);
    }

    public int getDefaultLineLength() {
        return getDefaultStringWidth() + 5;
    }    
    
    public int getDefaultStringWidth() {
        return myFontMetrics.stringWidth("     ");
    }
    
    protected int getStepX() {
    	return getDefaultLineLength() + getCircleDiameter() / 2;
    }
    
    protected int getHorizlineEndX(NodeView view) {
    	return view.getX();
    }
    
    /**
     * Overridden in case subclasses want to add things to the top of the 
     * diagram
     * @return
     */
    protected int getHeaderPixels() {
    	return 0;
    }
    
    /**
     * Big method that walks the tree of nodeviews, and assigns positions to 
     * each one. Based on these positions, a number of shapes are created and 
     * stored in various "shapes related to nodes". These shapes will be used 
     * during drawing (paintComponent) and mouse movement to understand
     * which nodes are currently being touched by the user
     */
    public void constructLocations() {
        Stack nodeViewStack = new Stack();
        Stack writeAsListStack = new Stack();
        // Used to store the max depth of a subtree for a given node, needed so we know how far left to draw it and nodes
        // at the same distance from Terminal as that node
        Stack maxDepthStack = new Stack();
        int stepY = getYStep();
        int stepX = getDefaultLineLength() + getCircleDiameter()/2; 

        ypos = -(getYStep()/2) + getHeaderPixels();
        int xpos = 0;
        int depth = 0;
        
        NodeView rootNodeView = (NodeView) nodeToNodeView.get(getRootNode());
        if (rootNodeView == null) {
            rootNodeView = new NodeView(getRootNode(), this, null);
            nodeToNodeView.put(getRootNode(), rootNodeView);            
        }
        nodeViewStack.push(rootNodeView);        
        nodeViews.add(rootNodeView);
        
        boolean onWriteAsList = false;
        writeAsListStack.push(new Boolean(onWriteAsList));

        // Go through each node and determine its positioning
        while (!nodeViewStack.isEmpty()) {
            NodeView currentView = (NodeView)nodeViewStack.pop();

            // The placeholder is pushed onto the stack right after a node that needs to deal with its children.
            // If we pop this, it means the next node on the stack will have finished processing its children.
            if (currentView == placeHolder) {
                // Decrease depth since we just finished some children
                depth--;
                // Replace the max depth if our depth is greater than the max depth on the stack
                int maxDepth = ((Integer)maxDepthStack.pop()).intValue();
                if (depth > 0 &&   maxDepth > ((Integer)maxDepthStack.peek()).intValue()) {
                    maxDepthStack.pop();
                    maxDepthStack.push(new Integer(maxDepth));
                }
                
                int distanceFromTerminal = maxDepth - depth;
                currentView = (NodeView)nodeViewStack.pop();
                ArrayList children = currentView.getChildren();
                int firstY = ((NodeView)children.get(0)).getY() ;
                int lastY = ((NodeView)children.get(children.size()-1)).getY();
                // Position the node's x to be its distance from the Terminal * the current x value associated with
                // a level in the tree
                currentView.setX( (distanceFromTerminal * stepX * -1));
                // Position its y value to be halfway between the y values of its first and last child
                currentView.setY( firstY + ((lastY-firstY)/2)  );
                
                
                //used to track list of nodes at the given depth to enable edge stretching in case of long node names
                ArrayList viewsAtDistance;
                if (nodesByTerminalDistance.size() > distanceFromTerminal) {
                    viewsAtDistance = (ArrayList) nodesByTerminalDistance.get(distanceFromTerminal);              
                } else {
                    viewsAtDistance = new ArrayList(10);
                    nodesByTerminalDistance.add(viewsAtDistance);
                }
                viewsAtDistance.add(currentView);                
                
            } else {
                onWriteAsList = ((Boolean)writeAsListStack.pop()).booleanValue();
                currentView.setOnWriteAsListPage(onWriteAsList);
                //System.out.println(currentView.getNode().getName() + ", popping "  + onWriteAsList);                
                if (currentView.isTerminal()) {
                    // We are a Terminal node, so increase the yPosition for leaves and set the Node's position
                    // to be equal to the current x and y
                    ypos += stepY;
                    currentView.setX(xpos);
                    currentView.setY(ypos);
                } else {
                    // Here it is an interior node, so push itself and the placeholder, then push all its children.                    
                    depth++;
                    maxDepthStack.push(new Integer(depth));
                    nodeViewStack.push(currentView);
                    nodeViewStack.push(placeHolder);
                    Node node = currentView.getNode();
                    ArrayList currentChildren = currentView.getNode().getChildren();
                    int size = currentChildren.size();
 
                    for (int i = size - 1; i >= 0; i--) {
                        Node currentNode = (Node) currentChildren.get(i);
                        NodeView currentChildView = (NodeView) nodeToNodeView.get(currentNode);
                        if (currentChildView == null) {
                            currentChildView = new NodeView(currentNode, this);
                            nodeToNodeView.put(currentNode, currentChildView);
                        }
                        if (currentNode.getConfidence() == Node.INCERT_UNSPECIFIED) {
                        	addISPUNodeView(currentChildView, currentView, nodeViewStack);
                        } else {
                        	currentView.addAsFirstChild(currentChildView);
                            nodeViewStack.push(currentChildView);                        	
                        }
                        nodeViews.add(currentChildView);
                        
                        writeAsListStack.push(new Boolean(onWriteAsList));
                        //System.out.println(currentNode.getName() + ", pushing "  + onWriteAsList);
                    }
                }
            }
        }
        
        // this does nothing here, but subclasses may handle ispu nodes differently
        placeIspuNodes();
        
        // Now, shift nodes to handle overlong node names
        minX = shiftNodesByTerminalDistance();
        
        // Add some padding for left positioning        
        minX -= 10 + getDefaultLineLength();
        int maxStringLength = -1;        

        Iterator it = nodeViews.iterator();
        while (it.hasNext()) {
            NodeView currentView = (NodeView)it.next();
            currentView.setX(currentView.getX() - minX);            
        }

        // Add all the circles and lines to our arrays        
        it = nodeViews.iterator();
        while (it.hasNext()) {
            NodeView currentView = (NodeView)it.next();         
            boolean isBlack = getIsBlackLine(currentView);
            ArrayList lineList = (isBlack ? blackLineValues : grayLineValues);
            ArrayList horizLineList = nodeHorizontalLines;

            Node currentNode = currentView.getNode();
            int[] location = new int[] {currentView.getX(), currentView.getY()};
            if (currentNode.getLocked()) {
            	String lockType = currentNode.getLockType();
            	if (lockType.equals(XMLConstants.PERMISSION)) {
            		nodeNoPermissionsLockValues.add(location);
            	} else if (lockType.equals(XMLConstants.SUBMITTED)) {
            		nodeSubmittedLockValues.add(location);
            	} else {
            		nodeLockValues.add(location);
            	}
            } else if (currentNode.getIsLeaf()) {
                leafCircleValues.add(location);
            } else if (currentNode.getHasIncompleteSubgroups()) {
                incompleteSubgroupsCircleValues.add(location);
            } else {
                circleValues.add(location);
            }
            int yLoc = currentView.getY() + getHalfCircleDiameter();
            
            
            int horizLineEndX = getHorizlineEndX(currentView);
            int horizLineStartX;
            if (currentView.getIsRootView()) {//root
                horizLineStartX = currentView.getX() - rootLineLength;
            } else {  //not the root               
                horizLineStartX = currentView.getParent().getX()+ getHalfCircleDiameter();
            }
            int phylesis = currentNode.getPhylesis();                
            //Draw Lines
            if (phylesis == Node.MONOPHYLETIC || phylesis == Node.NOT_MONOPHYLETIC) {
                //lineList.add(new int[] {horizLineStartX, yLoc, horizLineEndX, yLoc});
                horizLineList.add(new NodeHorizontalLine(currentNode, horizLineStartX, yLoc, horizLineEndX));
                if (phylesis == Node.MONOPHYLETIC) {
                   //lineList.add(new int[] {horizLineStartX, yLoc+1, horizLineEndX, yLoc+1});
                   horizLineList.add(new NodeHorizontalLine(currentNode, horizLineStartX, yLoc + 1, horizLineEndX));
                } else {
                    // Connect the vertical line a little further by drawing a small
                    // horizontal one
                    lineList.add(new int[] {horizLineStartX, yLoc+1, horizLineStartX+1, yLoc+1});                
                }
              //  System.out.println("line for " + currentNode.getName() + ": " + horizLineStartX +", "+ yLoc +", "+ horizLineEndX +", "+ yLoc);
            }
            if (phylesis == Node.NOT_MONOPHYLETIC  ||  phylesis == Node.MONOPHYLY_UNCERTAIN)  {
                //lineList.add(new int[] {horizLineStartX, yLoc-2, horizLineEndX, yLoc-2});
                horizLineList.add(new NodeHorizontalLine(currentNode, horizLineStartX, yLoc - 2, horizLineEndX));                
                //lineList.add(new int[] {horizLineStartX, yLoc+2, horizLineEndX, yLoc+2});
                horizLineList.add(new NodeHorizontalLine(currentNode, horizLineStartX, yLoc + 2, horizLineEndX));
                Rectangle boundingRect = new Rectangle(horizLineStartX, yLoc - 2, horizLineEndX - horizLineStartX, 4);
                if (phylesis == Node.NOT_MONOPHYLETIC) {
                    addToNotMonophylecticRects(boundingRect, currentNode);
                } else {
                    addToMonophylyUncertainRects(boundingRect, currentNode);                    
                    int currentX = horizLineStartX;
                    while (currentX < horizLineEndX) {
                        int newX = currentX + getCircleDiameter();
                        //lineList.add(new int[] {currentX, yLoc+2 , newX, yLoc-2});
                        horizLineList.add(new NodeHorizontalLine(currentNode, currentX, yLoc + 2, newX, yLoc - 2));
                        currentX = newX;
                    }
                }
            }

            // Here it is a terminal node and there is a subtree attached, so draw whiskers to indicate this
            if (currentNode.getTermWithSubTree()) {
                addSubtreeIndicator(currentView);
            }
            
            if (currentView.getNode().getExtinct() == Node.EXTINCT) {
                addExtinctIndicator(currentView);
            }

            if (currentNode.getConfidence() == Node.INCERT_PUTATIVE || currentNode.getConfidence() == Node.INCERT_UNSPECIFIED) {
                int currentX = horizLineStartX + 2*getCircleDiameter();
                handleConfidenceLocations(currentNode, yLoc, horizLineEndX, currentX);                    
            }

            if (currentNode.hasPage() || currentNode.getHasPageOnServer()) {
                addPageIndicator(currentView);
            }
            if (currentView.isTerminal()) {
                // keep track of the max length of a Terminal node's name
                int tempStringLength = myFontMetrics.stringWidth(currentNode.getName());
                if (tempStringLength > maxStringLength) {
                    maxStringLength = tempStringLength;
                }
            } else {
                //vertical lines
                ArrayList children = currentView.getChildren();
                int x = currentView.getX() + getHalfCircleDiameter();
                int y1 = ((NodeView) children.get(0)).getY() + getHalfCircleDiameter();
                int y2 = ((NodeView) children.get(children.size() - 1)).getY() + getHalfCircleDiameter();
                NodeView childView = (NodeView) children.get(0);
                isBlack = getIsBlackLine(childView);
                ArrayList vertLineList = isBlack ? blackLineValues : grayLineValues;                
                vertLineList.add(new int[] {x, y1 , x, y2});
                vertLineList.add(new int[] {x+1, y1 , x+1, y2});
            }
            
        }
        // Adjust the minimumX value for the longest Terminal node String -- needed to the scrollbars will show up if they are
        // actually needed
        minX -= maxStringLength;
        placeTextFields();        
        int borderPadding = TOP_PADDING;
        // Set the preferred size so the scrollpane knows if it should show the scrollbars, then revalidate so it redraws.
        Dimension newSize = new Dimension(getPanelWidth(), ypos + borderPadding);
        //System.out.println("new size = " + newSize);
        setPreferredSize(newSize);
        revalidate();
        // For whatever reason, if we dont actually set the size, then we are not seeing the rightmost nodes or their labels.
        setSize(newSize);        
        //repaint();
    }
    /**
     * Used to place various drawing coordinates related to confidence drawing
     * including making the line dotted, and adding question marks
     * @param currentNode
     * @param yLoc
     * @param horizLineEndX
     * @param currentX
     */
	protected void handleConfidenceLocations(Node currentNode, int yLoc, int horizLineEndX, int currentX) {
		int currentEndX = 0;
		Rectangle newRect = null;
		while (currentEndX < horizLineEndX) {
		    int newX = currentX + 4*getCircleDiameter();
		    newRect = new Rectangle(currentX, yLoc-2 , (newX-currentX)/2, 5);
		    confidenceEmptyRects.add(newRect);
		    currentX = newX;
		    currentEndX = currentX + 2*getCircleDiameter();
		}   
		if (currentNode.getConfidence() == Node.INCERT_PUTATIVE) {
		    confidenceSingleQmarkPoints.add(new Point(newRect.x + getHalfCircleDiameter(), newRect.y));
		} else if (currentNode.getConfidence() == Node.INCERT_UNSPECIFIED) {
		    confidenceDoubleQmarkPoints.add(new Point(newRect.x, newRect.y));
		}
	}
    
    public String getToolTipText(MouseEvent event) {
    	if (event != null) {
	    	int x = event.getX();
	    	int y = event.getY();
	    	String text = getToolTipFromMouseLoc(x, y);
	    	if (StringUtils.notEmpty(text)) {
	    		return text;
	    	}
    	}
    	return super.getToolTipText(event);
    }
    
    public String getToolTipText() {
    	Point pos = getMousePosition();
    	String text = null;
    	if (pos != null) {
    		text = getToolTipFromMouseLoc(pos.x, pos.y);
    	}
    	if (StringUtils.notEmpty(text)) {
    		return text;
    	} else {
    		return super.getToolTipText();
    	}
    }

    private String getToolTipFromMouseLoc(int x, int y) {
    	if (getPointOverlapsAnyLock(x, y, nodeLockValues)) {
    		return Controller.getController().getMsgString("NODE_CHECKED_OUT");
    	} else if (getPointOverlapsAnyLock(x, y, nodeNoPermissionsLockValues)) {
    		return Controller.getController().getMsgString("NODE_NO_PERMISSIONS");
    	} else if (getPointOverlapsAnyLock(x, y, nodeSubmittedLockValues)) {
    		return Controller.getController().getMsgString("NODE_SUBMITTED");
    	} else {
    		return null;
    	}
    }
    
    /**
     * Allows subclasses to implement custom ISPU node placement
     */
    protected void placeIspuNodes() {}
    
    /**
     * Default behavior adds a child to its parent's list of views,
     * but this can be overridden in subclasses
     * @param childNodeView
     * @param parentNodeView
     */
    protected void addISPUNodeView(NodeView childNodeView, NodeView parentNodeView, Stack nodeViewStack) {
    	parentNodeView.addAsFirstChild(childNodeView);
    	nodeViewStack.push(childNodeView);
    }
    
    protected int getPanelWidth() {
    	return -1*minX + getDefaultLineLength() + 15;
    }
    
    /**
     * Check to see whether the given view should have its lines drawn in black or gray
     * @param view
     * @return
     */
    protected boolean getIsBlackLine(NodeView view) {
        return !view.getOnWriteAsListPage();
    }
    
    protected Color getColorForNode(Node node) {
    	/*if (node.getPriority() == Node.LOW_PRIORITY) {
    		return Color.black;
    	} else if (node.getPriority() == Node.MEDIUM_PRIORITY) {
    		return Color.orange;
    	} else {
    		return Color.red;
    	}*/
    	return Color.black;
    }
    
    protected void addToMonophylyUncertainRects(Rectangle rect, Node node) {
    }
    protected void addToNotMonophylecticRects(Rectangle rect, Node node) {
    }

    /**
     * Adds an element to a list to show that a subtree indicator image
     * should be drawn for the given node
     */
    protected void addSubtreeIndicator(NodeView currentView) {
        int startX = currentView.getX() + getCircleDiameter();
        int startY = currentView.getY() -2 ;
        subtreeIndicators.add(new Point(startX, startY));
    }

    /**
     * Adds an element to a list to show that a extinction indicator image
     * should be drawn for the given node
     */    
    protected void addExtinctIndicator(NodeView currentView) {
        FloatingTextField txtfld = currentView.getTextField();
        placeTextField(txtfld);
        int startX = txtfld.getX() + txtfld.getWidth() ;
        if (! currentView.isTerminal() ) {
            startX += getCircleDiameter() + getHalfCircleDiameter();
        }
        int startY = currentView.getY() ;
        extinctIndicators.add(new Point(startX, startY));
    }

    /**
     * Adds an element to a list to show that a page indicator (dot in middle
     * of node circle) should be drawn for the given node
     */        
    protected void addPageIndicator(NodeView currentView) {
        Rectangle rect = new Rectangle(currentView.getX() + getHalfCircleDiameter() - 1, currentView.getY() + getHalfCircleDiameter() - 1, 2, 2);
        nodePageRects.add(rect);    
    }
    
    protected Node getRootNode() {
        return tree.getRoot();
    }
    
    /**
     * Places all visible textFields on the screen.
     */
    protected void placeTextFields() {
        FloatingTextField lastTerminalField = null, firstTerminalField = null;
        Collections.sort(nodeViews);
        Iterator it = nodeViews.iterator();
    
        while (it.hasNext()) {
            NodeView nodeView = (NodeView) it.next();
            FloatingTextField textField = nodeView.getTextField();
            placeTextField(textField);

            if (nodeView.isTerminal()) {
                if (firstTerminalField == null) {
                    firstTerminalField = textField;
                }
                textField.setHorizontalAlignment(SwingConstants.LEFT);
                if (lastTerminalField != null) {
                    lastTerminalField.setNextFocusableComponent(textField);
                } 
                lastTerminalField = textField;
            } else {
                textField.setHorizontalAlignment(SwingConstants.RIGHT);
                textField.setNextFocusableComponent(null);
            }                    
        }
        lastTerminalField.setNextFocusableComponent(firstTerminalField);
    }       
    
    /**
     *resize and reposition textfield, corresponding to node properties
     */
    private void placeTextField(FloatingTextField textField) {
        textField.resetPosition();
        textField.resizeTextField();          
    }
    
   /**
     * Aligns the nodes so that all nodes at a given level are shifted away as far as the max distance of a node at that 
     * level
     *
     * @return The minimum x value of the last node (used so we can reset all x values later)
     */
    protected int shiftNodesByTerminalDistance () {
        Iterator it = new ArrayList(nodesByTerminalDistance).iterator();
        // Skip the first element since it's a placeholder.
        it.next();
        int cumulativeShift = 0;
        FontMetrics fm = getFontMetrics(getFont());
        int minX=0;
        
        int rootOverrun=0;        
        while (it.hasNext()) {
            ArrayList currentNodeViews = (ArrayList)it.next();

            Iterator nodeViewIterator = currentNodeViews.iterator();
            int maxOverrun = 0;
            while (nodeViewIterator.hasNext()) {
                NodeView view = (NodeView) nodeViewIterator.next();
                // Shouldn't need to check maxOverrun for the root node
                if (!view.getIsRootView()) {
                    int overrun = fm.stringWidth(view.getNode().getName()) - (view.getX() - view.getParent().getX() - getCircleDiameter());
                    maxOverrun = Math.max(overrun, maxOverrun);
                } else {
                    //root
                    int overrun = calculateRootOverrun(view);
                    rootOverrun = Math.max(overrun, 0);                    
                }
                                
                // Set this here because it is possible the assignment below may not happen
                minX = view.getX();
            }
            
            if (cumulativeShift> 0) {
                nodeViewIterator = currentNodeViews.iterator();
                while (nodeViewIterator.hasNext()) {
                    NodeView view = (NodeView) nodeViewIterator.next();
                    view.setX(view.getX() - cumulativeShift);  // used to say "- rootOverrun"...but that just added an extra space on the line
                    
                    // If we did actually have to shift things, then set this here.
                    minX = view.getX();
                }
            }
            cumulativeShift += maxOverrun;            
        }
        calculateRootLineLength(rootOverrun);
        rootOverrun -= getRootLineLessPixels();
        return minX - rootOverrun;
    }
    
    protected int calculateRootOverrun(NodeView rootView) {
        return getFontMetrics().stringWidth(rootView.getNode().getName()) - getDefaultLineLength();
    }
    
    protected void calculateRootLineLength(int rootOverrun) {
        rootLineLength = rootOverrun + getDefaultLineLength();
    }
    
    protected int getRootLineLessPixels() {
    	return 0;
    }
    
    /*public boolean isTerminal(NodeView view) {
        return view.isTerminal();
    }*/
    
    public FontMetrics getFontMetrics() {
        return myFontMetrics;
    }
        
    /** 
     *Overriding abstract method, so that the full tree will be drawn 
     *(won't stop at 1-page depth)
     */
    public boolean dontDrawChildren(NodeView view) {
        return false;
    }
    
    /**
     * For TreeGrow purposes a 1-pixel line is fine but on the pages we would
     * like a thicker stroke, so allow this to be overridden
     * @return
     */
    public boolean getAdjustStroke() {
    	return false;
    }
    
    /**
     * Can be overridden by subclasses who want a thicker stroke
     * @return
     */
    public BasicStroke getThickerStroke() {
    	return null;
    }
    
    /**
     * Basically just goes through all the lists of things that need to get 
     *drawn (e.g. lines, circles, extinction/subtree indicators), and
     *draws them. 
     *<p>
     *Only draws elements whose position overlaps the area that needs to 
     * be repainted, determined from the graphics object's clip bounds.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Color origColor = g.getColor();
        // Only repaint the areas that can be seen or with the parameters called to repaint(int, int, int, int)
        Rectangle repaintRect = g.getClipBounds();

        //draw the lines first
        Iterator it;
        ArrayList lineVals[] = {blackLineValues, grayLineValues};
        boolean adjustStroke = getAdjustStroke();
    	Stroke stroke = g2d.getStroke();
    	BasicStroke thickerStroke = getThickerStroke();        
        for (int i=0; i<2; i++) {
            //g.setColor(lineVals[i] == blackLineValues ? Color.black : Color.lightGray);
            it = new ArrayList(nodeHorizontalLines).iterator();
            
            while (it.hasNext()) {
                //int[] coords = (int[]) it.next();
            	NodeHorizontalLine nextLine = (NodeHorizontalLine) it.next();
                int repaintMaxX = (int)(repaintRect.getX() + repaintRect.getWidth());
                int repaintMaxY = (int)(repaintRect.getY() + repaintRect.getHeight());
                boolean vertOrHorizInRect = (nextLine.getEndX() >= repaintRect.getX()  && 
                		nextLine.getStartX() <= repaintMaxX   &&
                        nextLine.getEndY() >= repaintRect.getY() &&  nextLine.getStartY() <= repaintMaxY );

                boolean diagInRect = false;   
                boolean isDiag = false;
                if (nextLine.getStartX()!=nextLine.getEndX() && nextLine.getStartY() != nextLine.getEndY()) { //is a diagonal
                	isDiag = true;
                    Point pt1 = new Point(nextLine.getStartX(), nextLine.getStartY());
                    Point pt2 = new Point(nextLine.getEndX(), nextLine.getEndY());
                    diagInRect = ( repaintRect.contains (pt1) ||  repaintRect.contains (pt2))  ;
                }          
                if (vertOrHorizInRect || diagInRect) {
                	Node nextNode = nextLine.getNode();
                	Color currentColor = getColorForNode(nextNode);
                	g.setColor(currentColor);
                	if (adjustStroke && nextNode.getPhylesis() == Node.MONOPHYLETIC) {
                		g2d.setStroke(thickerStroke);
                	}
                    //only paint this line if it in some way overlaps the requests rectangle of repainting                    
                    g.drawLine(nextLine.getStartX(), nextLine.getStartY(), nextLine.getEndX(), nextLine.getEndY());
                    if (adjustStroke) {
                    	g2d.setStroke(stroke);
                    }
                }
            }        
        }
        
        if (adjustStroke) {
        	g2d.setStroke(thickerStroke);
        }
        
        for (int i=0; i<2; i++) {
            g.setColor(lineVals[i] == blackLineValues ? Color.black : getLightGrayColor());
            it = new ArrayList(lineVals[i]).iterator();
            
            while (it.hasNext()) {
                int[] coords = (int[]) it.next();
                int repaintMaxX = (int)(repaintRect.getX() + repaintRect.getWidth());
                int repaintMaxY = (int)(repaintRect.getY() + repaintRect.getHeight());

                boolean vertOrHorizInRect = (coords[2] >= repaintRect.getX()  && coords[0] <= repaintMaxX   &&
                                            coords[3] >= repaintRect.getY() &&  coords[1] <= repaintMaxY );

                boolean diagInRect = false;   

                if (coords[0]!=coords[2] && coords[1]!=coords[3]) { //is a diagonal
                    Point pt1 = new Point(coords[0],coords[1]);
                    Point pt2 = new Point(coords[2],coords[3]);
                    diagInRect = ( repaintRect.contains (pt1) ||  repaintRect.contains (pt2))  ;
                }          
                if (vertOrHorizInRect || diagInRect) {
                    //only paint this line if it in some way overlaps the requests rectangle of repainting                    
                    g.drawLine(coords[0], coords[1], coords[2], coords[3]);
                }
            }        
        }        
        
        if (adjustStroke) {
        	// reset to original stroke
        	g2d.setStroke(stroke);
        }
        
        //clear out the lines to show incertae sedis status
        g.setColor(getBackground());
        it = new ArrayList(confidenceEmptyRects).iterator();
        while (it.hasNext()) {
            Rectangle r = (Rectangle)it.next();        
            if (r.intersects(repaintRect) ) {
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        }
        
        
        
        //draw question marks
        Font origFont = g.getFont();
        g.setFont(getQuestionMarkFont());
        g.setColor(getQMarkFontColor());
        it = new ArrayList(confidenceSingleQmarkPoints).iterator();
        while (it.hasNext()) {
            Point p = (Point)it.next();
            Point p2 = new Point(p.x,p.y-singleQuestionMarkStringDimension.height+8);
            
            Rectangle r = new Rectangle( p2, singleQuestionMarkStringDimension);
            if (repaintRect.intersects(r)) {
                g.drawString("?",p.x, p.y+getCircleDiameter());
            }
        }        
        
        it = new ArrayList(confidenceDoubleQmarkPoints).iterator();
        while (it.hasNext()) {
            Point p = (Point)it.next();
            handleConfidenceDoubleQmark(p, repaintRect, g);
        }                
        g.setFont(origFont);
        g.setColor(Color.black);
        it = new ArrayList(circleValues).iterator(); //copy list to avoid concurrent modification
        conditionallyRepaintCircles(circleValues, repaintRect, g);   

        conditionallyRepaintLocks(g, repaintRect, nodeLockValues, downloadedLockImage);
        conditionallyRepaintLocks(g, repaintRect, nodeSubmittedLockValues, submittedLockImage);
        conditionallyRepaintLocks(g, repaintRect, nodeNoPermissionsLockValues, noPermissionsLockImage);
        
        conditionallyRepaintPoints(g, repaintRect, subtreeIndicators, subtreeImage, subtreeImageWidth, subtreeImageHeight);   
        conditionallyRepaintPoints(g, repaintRect, extinctIndicators, extinctImage, extinctImageWidth, extinctImageHeight);       

        g.setColor(leafGreen);
        conditionallyRepaintCircles(leafCircleValues, repaintRect, g);
        g.setColor(underConstructionYellow);
        conditionallyRepaintCircles(incompleteSubgroupsCircleValues, repaintRect, g);
        
        // Draw rectangles that indicate when nodes have pages.
        drawPageRects(repaintRect, g);
        
        g.setColor(origColor);
    }

	protected Color getLightGrayColor() {
		return Color.lightGray;
	}

	protected Font getQuestionMarkFont() {
		if (questionMarkFont == null) {
			questionMarkFont = new Font("Serif",Font.BOLD,DEFAULT_QUESTIONMARK_FONT_SIZE);
		}
		return questionMarkFont;
	}

	protected void setQuestionMarkFont(Font questionMarkFont) {
		this.questionMarkFont = questionMarkFont;
	}

	protected Color getQMarkFontColor() {
		return Color.red;
	}
    
    

	private void conditionallyRepaintLocks(Graphics g, Rectangle repaintRect, ArrayList locks, Image lockImage) {
		Iterator it;
		it = new ArrayList(locks).iterator();//copy list to avoid concurrent modification
        while (it.hasNext()) {
            int[] coords = (int[]) it.next();            
            if (coords[0] + getCircleDiameter() >= repaintRect.getX()  && coords[0] <= repaintRect.getX() + repaintRect.getWidth()   &&
                coords[1] + getCircleDiameter() >= repaintRect.getY() &&  coords[1] <= repaintRect.getY() + repaintRect.getHeight() ) {
	            //only paint this oval if it in some way overlaps the requests rectangle of repainting
	            g.drawImage(lockImage, coords[0], coords[1]-4, this);
            }
        }
	}
    
    private boolean getPointOverlapsAnyLock(int pointX, int pointY, List locks) {
    	boolean returnVal = false;
    	for (Iterator iter = locks.iterator(); iter.hasNext();) {
			int[] currentLockLoc = (int[]) iter.next();
			int lockX = currentLockLoc[0];
			int lockY = currentLockLoc[1];
			returnVal = pointX >= lockX && pointX <= lockX + getCircleDiameter() &&
				pointY >= lockY && pointY <= lockY + getCircleDiameter();
			if (returnVal) {
				break;
			}
		}
    	return returnVal;
    }

	private void conditionallyRepaintPoints(Graphics g, Rectangle repaintRect, List indicatorList, Image toDraw,
			int imgWidth, int imgHeight) {
		Iterator it = new ArrayList(indicatorList).iterator();
		while (it.hasNext()) {
            Point p = (Point)it.next();            
            if ( p.x + subtreeImageWidth >= repaintRect.getX()  && p.x <= repaintRect.getX() + repaintRect.getWidth()   &&
                p.y + subtreeImageHeight >= repaintRect.getY() &&  p.y <= repaintRect.getY() + repaintRect.getHeight() ) {
                    //only paint this img if it in some way overlaps the requests rectangle of repainting
                    g.drawImage(toDraw, p.x, p.y, this);
            }
        }
	}
    
    protected void handleConfidenceDoubleQmark(Point p, Rectangle repaintRect, Graphics g) {
        Point p2 = new Point(p.x,p.y-doubleQuestionMarkStringDimension.height+8);

        Rectangle r = new Rectangle( p2, doubleQuestionMarkStringDimension);
        if (repaintRect.intersects(r)) {
            g.drawString("??",p.x, p.y+getCircleDiameter());
        }
	}

	protected void conditionallyRepaintCircles(ArrayList circleValues, Rectangle repaintRect, Graphics g) {
        Iterator it = new ArrayList(circleValues).iterator();//copy list to avoid concurrent modification
        while (it.hasNext()) {
            int[] coords = (int[]) it.next();            
            if (getCircleIsContainedByRepaintRect(repaintRect, coords)) {
                //only paint this oval if it in some way overlaps the requests rectangle of repainting
                g.fillOval(coords[0], coords[1], getCircleDiameter(), getCircleDiameter());
            }
        }        
    }
    
    private boolean getCircleIsContainedByRepaintRect(Rectangle repaintRect, int[] coords) {
        return coords[0] + getCircleDiameter() >= repaintRect.getX()  && coords[0] <= repaintRect.getX() + repaintRect.getWidth()   &&
                coords[1] + getCircleDiameter() >= repaintRect.getY() &&  coords[1] <= repaintRect.getY() + repaintRect.getHeight();       
    }
    
    /**
     * Method used to draw the little rectangles that indicate which nodes have
     * pages.
     *
     * @param repaintRect The clipbounds of the current repaint
     * @param g The graphics object to draw on
     */
    protected void drawPageRects(Rectangle repaintRect, Graphics g) {
        g.setColor(getBackground());
        Iterator it = new ArrayList(nodePageRects).iterator();
        while (it.hasNext()) {
            Rectangle r = (Rectangle)it.next();        
            if (r.intersects(repaintRect) ) {
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        }    
    }
    
    /**
     * Used to rebuild and redraw the tree if a resize is necessary
     */
    public void rebuildTree() {
        circleValues.clear();
        nodeLockValues.clear();
        nodeSubmittedLockValues.clear();
        nodeNoPermissionsLockValues.clear();
        subtreeIndicators.clear();
        extinctIndicators.clear();
        leafCircleValues.clear();
        incompleteSubgroupsCircleValues.clear();
        blackLineValues.clear();
        grayLineValues.clear();
        confidenceEmptyRects.clear();
        confidenceSingleQmarkPoints.clear();
        confidenceDoubleQmarkPoints.clear();
        nodesByTerminalDistance.clear();
        nodePageRects.clear();
        nodesByTerminalDistance.add(new Integer(0));
        nodeHorizontalLines.clear();
        Iterator it = nodeViews.iterator();
        while (it.hasNext()) {
            NodeView currentView = (NodeView) it.next();
            currentView.clearChildren();
        }
        minX = 0;
        nodeViews.clear();
        constructLocations();
    }
    
    /**
     * Used fir debugging purposes
     */
    public String getViewsDebugString() {
        String returnString = "";
        Iterator it = nodeViews.iterator();
        int i = 0;
        while (it.hasNext()) {
            returnString += "--- " + i++ + " --- " + it.next().toString() + "\n\n";
        }
        return returnString;
    }
    
    /** 
     *Called by zoomin and zoomout after they've set the zoom factor, to 
     *handle the task of rebuilding/redrawing tree
     * @param font The optional new font to use.  If not bound, a default font is chosen
     */
    protected void zoomUpdate(Font font) {        
        int currentFontSize = (int)(DEFAULT_FONT_SIZE * ( Math.pow(.75,zoomFactor)));
        if (font == null) {
            setFont (new Font(Controller.getController().getDefaultFont().getName(), Font.PLAIN, currentFontSize)); // font was "Default"
        } else {
            setFont(font);
        }
        myFontMetrics = getFontMetrics(getFont());
        //FloatingTextField.resetFontMetrics(this);
        
        int currentQMarkFontSize = (int)(DEFAULT_QUESTIONMARK_FONT_SIZE * ( Math.pow(.75,zoomFactor)));
        questionMarkFont=new Font(Controller.getController().getDefaultFont().getName(),Font.BOLD,currentQMarkFontSize); 
        circleDiameter = DEFAULT_CIRCLE_DIAMETER - zoomFactor;
    }    

    /**
     * Width of the subtree indicator image, used for layout purposes
     */
    public static int getSubtreeImageWidth() {
        return subtreeImageWidth;
    }        
    
}
