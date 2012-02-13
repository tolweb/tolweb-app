package org.tolweb.treedrawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.ImageIcon;

import org.tolweb.btol.AdditionalFields;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.URLBuilder;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.AbstractTreePanel;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.NodeView;
import org.tolweb.treegrow.tree.Tree;
import org.tolweb.treegrow.tree.TreeFrame;

/**
 * Low-functionality tree panel found on the Page tab of the node/page 
 * data editor
 */
public class TreeImagePanel extends AbstractTreePanel implements MouseListener {   
	private static final int EXTINCT_IMAGE_PADDING = 4;
	/**
	 * 
	 */
	private static final long serialVersionUID = 9082043354199013015L;
	private Node rootNode;
    private Font microsoftArialFont;
    private Font microsoftArialItalicFont;
    @SuppressWarnings("unchecked")
    private Hashtable nodeViewsToLabelPoints = new Hashtable();
    @SuppressWarnings("unchecked")
    private Hashtable pagedNodeViewsToPoints = new Hashtable();
    @SuppressWarnings("unchecked")
    private List monophylyUncertainRects = new ArrayList();
    @SuppressWarnings("unchecked")
    private List notMonophylecticRects = new ArrayList();
    @SuppressWarnings("unchecked")
    private List ispuNodeViews = new ArrayList();
    private PageDAO pageDAO;
    private URLBuilder urlBuilder;
    private Rectangle rootRectangle;
    private boolean isBlackLines = true;
    private boolean drawDescriptions;
    private boolean drawAuthorities;
    private boolean includeSpecimenInfo;
    private Color leadsToComplete = Color.magenta.darker();
    private Color lightGray = new Color(181, 178, 183);
    private Color descriptionLightGray = Color.lightGray.darker();

    private int currentMaxTerminalTextOverrun = 0;
    private int maxTerminalX;
    private static final int X_PADDING = 4;    
    private static final int TERMINAL_ADJUSTMENT = 2;
    private static final int DESCRIPTION_AUTHORITY_PADDING = 5;
    protected Font questionMarkFont;// = new Font("SansSerif", Font.PLAIN, DEFAULT_QUESTIONMARK_FONT_SIZE + 2);
    @SuppressWarnings("unchecked")
	private List confidenceNotMonophyleticRects = new ArrayList();
    @SuppressWarnings("unchecked")
	private List confidenceMonophlyUncertainRects = new ArrayList();

    private Color trunkColor = Color.orange;
    @SuppressWarnings("unchecked")
	private List trunkVertLineList = new ArrayList();
    // This is the "trunk coloring" switch - needs to be refactored out into 
    // an implementation of a tree-colorer or tree-color-strategy, whatever it's 
    // going to be called going forward
	private boolean trunkColoringEnabled = false;
	
    public TreeImagePanel() {
        this(null, null, null, null, null);
    }
    public TreeImagePanel(String arialFontPath, String arialItalicFontPath, String extinctIconPath) {
    	this(null, null, arialFontPath, arialItalicFontPath, extinctIconPath);
    }
   
    /** Creates a new instance of PreviewTreePanel */
    public TreeImagePanel(Tree tr, TreeFrame frame, String arialFontPath, String arialItalicFontPath,
    		String extinctImagePath) {
        super(tr, frame);
        setBackground(Color.white);
        try {
            microsoftArialFont = Font.createFont(Font.TRUETYPE_FONT, new File(arialFontPath));
            microsoftArialItalicFont = Font.createFont(Font.TRUETYPE_FONT, new File(arialItalicFontPath));
            microsoftArialFont = microsoftArialFont.deriveFont(11F);
            microsoftArialItalicFont = microsoftArialItalicFont.deriveFont(11F);
            setFont(microsoftArialFont);
            zoomFactor++;
            zoomUpdate(microsoftArialFont);
            questionMarkFont = microsoftArialFont;
            initQMarkDims();
            extinctImage = new ImageIcon(extinctImagePath).getImage();
	        extinctImageWidth = extinctImage.getWidth(null);
	        extinctImageHeight = extinctImage.getHeight(null);            
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * workaround for jdk1.6 bug!  Also, since there is no repainting
     * for this, no need to be double buffered
     */
	protected boolean getUseDoubleBuffering() {
		return false;
	}
	
    /**
     * Overrides method in abstract class  - gives root of subtree being drawn
     */
    protected Node getRootNode() {
        return rootNode;
    }
    
    public void setRootNode(Node node) {
        this.rootNode = node;
    }
    
    /**
     * Overrides method in abstract class  - causes tree to be drawn 1-deep
     */    
    public boolean dontDrawChildren(NodeView view) {
        return view.getNode() != rootNode && view.getNode().hasPage();
    }
    
    @SuppressWarnings("unchecked")
    protected void addExtinctIndicator(NodeView currentView) {
    	// only draw extinct icons for terminal taxa
    	if (currentView.getIsTerminal()) {
	        Point stringLoc = getStringLoc(currentView);
	        int startX = stringLoc.x;
	        // add in the metrics for the name
	        startX += getStringWidthForNodeView(currentView); // <-- DEVN: BUG: when taxa name italic, the last character covers the extinct icon (this is likely the method needing the fix)
	        if (! currentView.isTerminal() ) {
	            startX += getCircleDiameter() + getHalfCircleDiameter();
	        }
	        startX += X_PADDING;
	        int startY = currentView.getY() ;
	        extinctIndicators.add(new Point(startX, startY));        
    	}
    }
    
    protected void handleConfidenceDoubleQmark(Point p, Rectangle repaintRect, Graphics g) {
    	NodeView rootView = (NodeView) nodeToNodeView.get(getRootNode());
    	int rootX = rootView.getX();
        g.drawString("?", p.x + 7, p.y+getCircleDiameter());
        Color currentColor = g.getColor();
        g.setColor(Color.white);
        // draw white rects back to root in order to clean up any other lines that might have
        // arisen
        g.fillRect(rootX, p.y, p.x - rootX, getYStep());
        g.setColor(currentColor);
	}    
    
    /**
     * For TreeGrow purposes a 1-pixel line is fine but on the pages we would
     * like a thicker stroke, so allow this to be overridden
     * @return
     */
    public boolean getAdjustStroke() {
    	return true;
    }
    
    /**
     * Can be overridden by subclasses who want a thicker stroke
     * @return
     */
    public BasicStroke getThickerStroke() {
    	return new BasicStroke(2.55f);
    }    
    
    /**
     * Overridden to clear the contents of the panel
     */
    @SuppressWarnings("unchecked")
    public void paintComponent(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
        Rectangle clipRect = g.getClipBounds();
        //clipRect.setBounds((int)clipRect.getX(), (int)clipRect.getY() + TOP_PADDING, (int)clipRect.getWidth(), (int)clipRect.getHeight() - TOP_PADDING);
        g.clearRect(0, 0, (int) clipRect.getWidth(), (int) clipRect.getHeight());
        super.paintComponent(g);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);        
        // for antialiasing geometric shapes
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        //g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        // to go for quality over speed
        //g2d.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        
        //g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        //
        //g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        //g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_I)
        
        iterateLabelPoints(g, nodeViewsToLabelPoints, Color.black);
        iterateLabelPoints(g, pagedNodeViewsToPoints, Color.blue);
        NodeView rootView = (NodeView) nodeToNodeView.get(rootNode);
        // draw root triangle
        Color color = g.getColor();
        g.setColor(getIsBlackLines() ? Color.black : getLightGrayColor());
        boolean isTrunkNode = getRootNode().getTrunkNode() == Node.TRUNK_NODE;
        g.setColor(getTrunkColoringEnabled() && isTrunkNode ? trunkColor : g.getColor()); 
        GeneralPath triangle = new GeneralPath();
        int startX = rootView.getX() - getDefaultLineLength() - 5 + getRootLineLessPixels();
        int startY = rootView.getY() + getCircleDiameter() / 2 + 1;
        triangle.moveTo(startX, startY);
        int triRight = startX + 9;
        int triTop = startY - 5;
        int triBottom = startY + 4;
        Stroke stroke = g2d.getStroke();
    	BasicStroke thinnerStroke = new BasicStroke(0.5f);
        g2d.setStroke(thinnerStroke);
        triangle.lineTo(triRight, triTop);
        triangle.lineTo(triRight, triBottom);
        triangle.closePath();
        //triangle.lineTo(startX, startY);
        g2d.setStroke(stroke);
        rootRectangle = triangle.getBounds();
        (g2d).fill(triangle);
        
        // draw over top of the vertical lines if trunk color is enabled
        if (getTrunkColoringEnabled() && isTrunkNode) {
        	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            Stroke preserveStroke = g2d.getStroke();
            BasicStroke thickerStroke = getThickerStroke();
            g2d.setStroke(thickerStroke);
        	g2d.setColor(getTrunkColor());
            Iterator it = trunkVertLineList.iterator();
            
            while (it.hasNext()) {
                int[] coords = (int[]) it.next();
                int repaintMaxX = (int)(clipRect.getX() + clipRect.getWidth());
                int repaintMaxY = (int)(clipRect.getY() + clipRect.getHeight());

                boolean vertOrHorizInRect = (coords[2] >= clipRect.getX()  && coords[0] <= repaintMaxX   &&
                                            coords[3] >= clipRect.getY() &&  coords[1] <= repaintMaxY );

                boolean diagInRect = false;   

                if (coords[0]!=coords[2] && coords[1]!=coords[3]) { //is a diagonal
                    Point pt1 = new Point(coords[0],coords[1]);
                    Point pt2 = new Point(coords[2],coords[3]);
                    diagInRect = (clipRect.contains (pt1) ||  clipRect.contains (pt2));
                }          
                if (vertOrHorizInRect || diagInRect) {
                    //only paint this line if it in some way overlaps the requests rectangle of repainting                    
                    g2d.drawLine(coords[0], coords[1], coords[2], coords[3]);
                }
            }    
            g2d.setStroke(preserveStroke);
        }
        // draw a 2-pixel rect just to the right of the triangle in order to
        // create some separation for the back to root arrow
        g.setColor(Color.white);
        g.fillRect(triRight, triTop, 1, triBottom - triTop);
        if (getIncludeSpecimenInfo()) {
        	drawSpecimenInfo(g);
        }
        g.setColor(color);
    }
    
    @SuppressWarnings("unchecked")
    private void drawSpecimenInfo(Graphics g) {
    	List terminalViews = new ArrayList();
    	for (Iterator iter = nodeViews.iterator(); iter.hasNext();) {
			NodeView nextView = (NodeView) iter.next();
			if (nextView.getIsTerminal()) {
				terminalViews.add(nextView);
			}
		}
    	int squareSize = 12;
    	int relatedSquaresSpacing = 5;
    	int unrelatedSquaresSpacing = 15;
    	int labelYPos = 10;
    	int i = 0;
    	Font currentFont = g.getFont();
    	for (Iterator iter = terminalViews.iterator(); iter.hasNext();) {
			NodeView nextView = (NodeView) iter.next();
			MappedNode node  = (MappedNode) nextView.getNode();
			// DNA rects
			int currentX = maxTerminalX + 10;
			// draw header
			if (i == 0) {
				Font newFont = microsoftArialFont.deriveFont(Font.BOLD);
				g.setFont(newFont);
				g.setColor(Color.black);
				g.drawString("DNA", currentX + 3, labelYPos);
			}
			g.setColor(getColorForDNAPrioritySquare(node));
			g.fillRect(currentX, nextView.getY() - 2, squareSize, squareSize);
			g.setColor(Color.black);			
			currentX += squareSize + relatedSquaresSpacing;
			g.setColor(getColorForDNASpecimensSquare(node));			
			g.fillRect(currentX, nextView.getY() - 2, squareSize, squareSize);
			currentX += squareSize + unrelatedSquaresSpacing;
			// Adults rects
			if (i == 0) {
				g.setColor(Color.black);
				g.drawString("Adults", currentX - 2, labelYPos);
			}
			g.setColor(getColorForAdultPrioritySquare(node));			
			g.fillRect(currentX, nextView.getY() - 2, squareSize, squareSize);
			g.setColor(Color.black);			
			currentX += squareSize + relatedSquaresSpacing;
			g.setColor(getColorForAdultSpecimensSquare(node));			
			g.fillRect(currentX, nextView.getY() - 2, squareSize, squareSize);
			currentX += squareSize + unrelatedSquaresSpacing;
			// Larvae rects
			if (i == 0) {
				g.setColor(Color.black);
				g.drawString("Larvae", currentX - 2, labelYPos);
			}
			g.setColor(getColorForLarvaePrioritySquare(node));
			g.fillRect(currentX, nextView.getY() - 2, squareSize, squareSize);
			currentX += squareSize + relatedSquaresSpacing;
			g.setColor(getColorForLarvaeSpecimensSquare(node));
			g.fillRect(currentX, nextView.getY() - 2, squareSize, squareSize);
			i++;
			g.setColor(Color.black);			
		}
    	g.setFont(currentFont);
    }

    private Color getColorForDNASpecimensSquare(MappedNode node) {
    	return getColorForSpecimens(true, node.getAdditionalFields().getDnaPriority());
    }
    
    private Color getColorForLarvaeSpecimensSquare(MappedNode node) {
    	return getColorForSpecimens(node.getAdditionalFields().getHasLarvalSpecimens(), node.getAdditionalFields().getLarvaePriority());
    }
       
    private Color getColorForAdultSpecimensSquare(MappedNode node) {
    	return getColorForSpecimens(node.getAdditionalFields().getHasPage(), node.getAdditionalFields().getAdultPriority());
    }    
    
    private Color getColorForSpecimens(boolean hasSpecimens, int priority) {
    	if (hasSpecimens) {
    		return Color.blue.darker();
    	} else {
    		if (priority == AdditionalFields.HIGH_PRIORITY) {
    			return Color.red;
    		} else if (priority == AdditionalFields.MEDIUM_PRIORITY) {
    			return new Color(204, 10, 153);
    		} else {
    			return Color.pink;
    		}
    	}
    }
    
    private Color getColorForDNAPrioritySquare(MappedNode node) {
    	return getColorForPriority(node.getAdditionalFields().getDnaPriority());
    }
    
    private Color getColorForLarvaePrioritySquare(MappedNode node) {
    	return getColorForPriority(node.getAdditionalFields().getLarvaePriority());    	
    }
    
    private Color getColorForAdultPrioritySquare(MappedNode node) {
    	return getColorForPriority(node.getAdditionalFields().getAdultPriority());    	
    }
    
    private Color getColorForPriority(int priority) {
    	switch(priority) {
    		case AdditionalFields.HIGH_PRIORITY: return Color.black;
    		case AdditionalFields.MEDIUM_PRIORITY: return Color.darkGray.brighter().brighter();
    		default: return Color.lightGray;
    	}
    }
    
    private int getSpecimenBlockWidth() {
    	return 150;
    }
    
    protected int getHeaderPixels() {
    	return 15;
    }
    
    protected int getPanelWidth() {
    	int additionalWidth = getIncludeSpecimenInfo() ? getSpecimenBlockWidth() : 0;
    	return -1*minX + getTerminalTextOverrun() + getDefaultLineLength() + additionalWidth;
    }
    
    @SuppressWarnings("unchecked")
    private void iterateLabelPoints(Graphics g, Hashtable stringsToPoints, Color desiredFontColor) {
        Color currentColor = g.getColor();
        g.setFont(getFont());
        g.setColor(desiredFontColor);
        for (Iterator iter = stringsToPoints.keySet().iterator(); iter.hasNext();) {
            NodeView nextNodeView = (NodeView) iter.next();
            MappedNode nextNode = (MappedNode) nextNodeView.getNode();            
            int terminalAdjustment = 0;
            boolean isTerminal = nextNodeView.getIsTerminal(); 
            if (isTerminal) {
            	// if they are terminals, get them a little more snug
            	terminalAdjustment = 2;
            }
            Point stringLoc = (Point) stringsToPoints.get(nextNodeView);
            if (getDrawNameInItalics(nextNode)) {
            	g.setFont(microsoftArialItalicFont);
            	// italic font indents things further to the right,
            	// so shift left one pixel to line up with non-italics
            	terminalAdjustment += 1;
            } else {
            	g.setFont(microsoftArialFont);
            } 
            int confidenceHeightAdjustment = 0;
            int confidenceWidthAdjustment = 0;
            // if this is an internal incertae sedis node, adjust to line up with qmark
            if (!nextNodeView.getIsTerminal() && nextNode.getConfidence() == Node.INCERT_PUTATIVE) {
            	confidenceHeightAdjustment = singleQuestionMarkStringDimension.height / 2 - 1;
            	confidenceWidthAdjustment = singleQuestionMarkStringDimension.width + 5;
            }
            // string drawn here should actually be according to whether authority, italics, etc.
            g.drawString(nextNodeView.getNameString(), stringLoc.x - terminalAdjustment - confidenceWidthAdjustment, stringLoc.y + confidenceHeightAdjustment);
            
            int currentX = stringLoc.x - terminalAdjustment + getFontMetrics(g.getFont()).stringWidth(nextNodeView.getNameString());
            
            // reset font in case of authority or description
            g.setFont(microsoftArialFont);
            int stringPadding = DESCRIPTION_AUTHORITY_PADDING;
            // check to see if it has authority
            if (getDrawAuthorities() && StringUtils.notEmpty(nextNodeView.getAuthorityString())) {
            	// draw the authority
            	g.setColor(descriptionLightGray);
            	currentX += stringPadding;
            	g.drawString(nextNodeView.getAuthorityString(), currentX, stringLoc.y);
            	currentX += getFontMetrics(g.getFont()).stringWidth(nextNodeView.getAuthorityString());
            	g.setColor(desiredFontColor);
            }
            // check to see if it has a description
            if (getDrawDescriptions() && isTerminal && StringUtils.notEmpty(nextNode.getDescription()) 
            		&& !"0".equals(nextNode.getDescription())) {
            	// draw the description
            	g.setColor(descriptionLightGray);
            	if (nextNode.getExtinct() == Node.EXTINCT) {
					currentX += extinctImageWidth + EXTINCT_IMAGE_PADDING;
            	}
            	currentX += stringPadding;
            	g.drawString(nextNode.getDescription(), currentX, stringLoc.y);
            	g.setColor(desiredFontColor);
            }
        }        
        g.setColor(currentColor);
    }
    
    protected boolean getDrawNameInItalics(MappedNode node) {
    	if (node.getHasSupertitle()) {
    		return ((MappedOtherName) node.getFirstPreferredOtherName()).getItalicize();
    	} else {
    		return node.getItalicizeName();
    	}
    }
    
    /**
     * Added on to the list of ISPU nodes since we don't want them appearing in the 
     * regular tree
     * @param childNodeView The view for the ISPU node
     * @param parentNodeView Ignored for these purposes
     */
    @SuppressWarnings("unchecked")
    protected void addISPUNodeView(NodeView childNodeView, NodeView parentNodeView, Stack nodeViewStack) {
    	//parentNodeView.addAsFirstChild(childNodeView);
    	ispuNodeViews.add(childNodeView);
    	NodeView rootNodeView = (NodeView) nodeToNodeView.get(getRootNode());
    	childNodeView.setParent(rootNodeView);
    }    
    
    @SuppressWarnings("unchecked")
    protected void addToMonophylyUncertainRects(Rectangle rect, Node node) {
   		monophylyUncertainRects.add(rect);
   		if (node.getConfidence() != Node.INCERT_OFF) {
   			confidenceMonophlyUncertainRects.add(rect);
   		}
    }
    
    @SuppressWarnings("unchecked")
    protected void addToNotMonophylecticRects(Rectangle rect, Node node) {
   		notMonophylecticRects.add(rect);
   		if (node.getConfidence() != Node.INCERT_OFF) {
   			confidenceNotMonophyleticRects.add(rect);
   		}
    }
    
    /**
     * Used to place various drawing coordinates related to confidence drawing
     * including making the line dotted, and adding question marks
     * @param currentNode
     * @param yLoc
     * @param horizLineEndX
     * @param currentX
     */ 
    @SuppressWarnings("unchecked")
    protected void handleConfidenceLocations(Node currentNode, int yLoc, int horizLineEndX, int currentX) {
    	int additionalIspuPixels = 13;
		int leftPadding = currentNode.getConfidence() == Node.INCERT_PUTATIVE ? 5 : additionalIspuPixels;
    	int width = horizLineEndX - currentX - X_PADDING + leftPadding;
    	int qmarkPadding = 5;
		// add the question mark close to the edge of the rectangle
		// 4 pixels is all the rectangle that is needed to draw on the right side
		// 10 pixels is all the rectangle that is needed to draw on the left, so
		// add the clear rect accordingly
		Rectangle newRect = new Rectangle(currentX - leftPadding, yLoc-2 , width, 5);
		int nodePhylesis = currentNode.getPhylesis();
		confidenceEmptyRects.add(newRect);
		if (nodePhylesis == Node.NOT_MONOPHYLETIC) {
			confidenceNotMonophyleticRects.add(newRect);
		} else if (nodePhylesis == Node.MONOPHYLY_UNCERTAIN) {
			confidenceMonophlyUncertainRects.add(newRect);
		}
		confidenceSingleQmarkPoints.add(new Point(newRect.x + width - qmarkPadding - 1, newRect.y + 1));
    }
    
    public void rebuildTree() {
        nodeToNodeView.clear();
        nodeViewsToLabelPoints.clear();
        pagedNodeViewsToPoints.clear();
        monophylyUncertainRects.clear();
        notMonophylecticRects.clear();
    	confidenceNotMonophyleticRects.clear();
    	confidenceMonophlyUncertainRects.clear();
        currentMaxTerminalTextOverrun = 0;
        maxTerminalX = 0;
        ispuNodeViews.clear();
        super.rebuildTree(); 
    }
    
    /**
     * overridden to return the width of the longest
     * description or authority for a terminal taxon
     * in this particular tree image (used for 
     * calculating the panel's dimensions)
     */
    protected int getTerminalTextOverrun() {
    	return currentMaxTerminalTextOverrun;
    }
    
    private Point getStringLoc(NodeView nodeView) {
        int labelX, labelY ;            
        if (!nodeView.isTerminal()) {           
            int labelWidth = getStringWidthForNodeView(nodeView);
            labelX = nodeView.getX() - labelWidth + 1;
            labelY = nodeView.getY();
            nodeView.getPanel().getHalfCircleDiameter();
        } else {
            labelX = nodeView.getX() + 2*X_PADDING + getHalfCircleDiameter();
            labelY = nodeView.getY() + 8;
            if (nodeView.getNode().getTermWithSubTree()) {
                labelX += AbstractTreePanel.getSubtreeImageWidth() + X_PADDING;
            }
        }
        return new Point(labelX, labelY);
    }
    
    private int getStringWidthForNodeView(NodeView view) {
    	FontMetrics metrics = getDrawNameInItalics((MappedNode)view.getNode()) ? getFontMetrics(microsoftArialItalicFont) : getFontMetrics(microsoftArialFont); 
        return metrics.stringWidth(getTreeStringForNodeView(view));
    }
    
    /**
     *  In the context of the tree drawing, we either want:
     *	1) the node name or
     *	2) the name of the supertitle
     * @param view
     * @return
     */
    private String getTreeStringForNodeView(NodeView view) {
    	MappedNode node = (MappedNode) view.getNode();   	
    	if (node.getHasSupertitle()) {
    		return node.getFirstPreferredOtherName().getName();
    	} else {
    		return node.getName();
    	}
    }
    
    protected int getStepX() {
    	return getDefaultLineLength();
    }
    
    protected int getHorizlineEndX(NodeView view) {
    	return view.getX() + getCircleDiameter() / 2;
    }
    
    /**
     * Overridden to place ispu nodes at the bottom of the tree
     */
    @SuppressWarnings("unchecked")
    protected void placeIspuNodes() {
    	int index = 0;
    	// reverse these guys because they are iterated in reverse order in the superclass
    	List reverseIspuViews = new ArrayList(ispuNodeViews);
    	Collections.reverse(reverseIspuViews);
    	for (Iterator iter = reverseIspuViews.iterator(); iter.hasNext();) {
			NodeView nextIspuView = (NodeView) iter.next();
			ypos += getYStep();			
			nextIspuView.setY(ypos);
			index++;
		}
    }    
    
    /**
     * Function that places all visible textFields on the screen then launches a thread to create and place
     * all the non-visible ones.  Also places all image icons.
     */
    @SuppressWarnings("unchecked")
    protected void placeTextFields() {
        for (Iterator iter = nodeViews.iterator(); iter.hasNext();) {
            NodeView nextView = (NodeView) iter.next();
            if (!nextView.getIsRootView() && StringUtils.notEmpty(nextView.getNode().getName())) {
                Point stringLoc = getStringLoc(nextView);
                MappedNode nextNode = (MappedNode) nextView.getNode();
                Point nextStringPoint = getStringLoc(nextView);
                if (nextNode.getHasPage()) {
                    pagedNodeViewsToPoints.put(nextView, nextStringPoint);
                } else {
                    nodeViewsToLabelPoints.put(nextView, nextStringPoint);
                }
            	boolean italicizeName = getDrawNameInItalics(nextNode);
            	Font fontToCheck = italicizeName ? microsoftArialItalicFont : microsoftArialFont;
                String nameString = getTreeStringForNodeView(nextView);
                nextView.setNameString(nameString);                
                // calculate some width and location info about descriptions
                // and possibly authorities
                if (nextView.getIsTerminal()) {
	                int nextTextOverrun = 0;
	                int currentX = stringLoc.x - TERMINAL_ADJUSTMENT + getFontMetrics(fontToCheck).stringWidth(nameString);
	                int standardEndX = currentX;
	                if (currentX > maxTerminalX) {
	                	// store this for specimen boxes
	                	maxTerminalX = currentX;
	                }
	                if (nextNode.getExtinct() == Node.EXTINCT) {
	                	currentX += extinctImageWidth + EXTINCT_IMAGE_PADDING;
	                }
	                int stringPadding = DESCRIPTION_AUTHORITY_PADDING;
	                // check to see if it has authority
	                if (getDrawAuthorities() && nextNode.getShowAuthorityInContainingGroup()) {
	                	// draw the authority
	                	String authString;
	                	if (nextNode.getHasSupertitle()) {
	                		authString = nextNode.getSupertitle().getAuthority();
	                		Integer authYear = nextNode.getSupertitle().getAuthorityYear();
	                		authString += " " + ((authYear != null) ? authYear.toString() : "");
	                	} else {
	                		authString = nextNode.getNameAuthority();
	                		Integer authYear = nextNode.getAuthorityDate();
	                		authString += " " + ((authYear != null) ? authYear.toString() : "");
	                	}
	                	currentX += stringPadding;
	                	currentX += getFontMetrics(microsoftArialFont).stringWidth(authString);
	                	nextView.setAuthorityString(authString);
	                }
	                // check to see if it has a description
	                if (getDrawDescriptions() && StringUtils.notEmpty(nextNode.getDescription())) {
	                	currentX += stringPadding;
	                	currentX += getFontMetrics(microsoftArialFont).stringWidth(nextNode.getDescription());
	                }
	                nextTextOverrun = currentX - standardEndX;
	                // if our particular case is wider than any yet seen, 
	                // store our value as the widest
	                if (nextTextOverrun > currentMaxTerminalTextOverrun) {
	                	currentMaxTerminalTextOverrun = nextTextOverrun;
	                }
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public String getLinkCoords(MappedPage page) {
        FontMetrics fm = getFontMetrics();
        String returnString = "";
        for (Iterator iter = pagedNodeViewsToPoints.keySet().iterator(); iter.hasNext();) {
            NodeView nextNodeView = (NodeView) iter.next();
            int width = getStringWidthForNodeView(nextNodeView);
            int height = fm.getHeight();
            Point startPoint = (Point) pagedNodeViewsToPoints.get(nextNodeView);
            returnString += getLinkAreaString(nextNodeView, startPoint, width, height) + "\n";
        }
        // loop through the extinct icons
        for (Iterator iter = extinctIndicators.iterator(); iter.hasNext();) {
            Point nextExtinctPoint = (Point) iter.next();
            returnString += getExtinctAreaString(nextExtinctPoint) + "\n";
        }
        for (Iterator iter = notMonophylecticRects.iterator(); iter.hasNext();) {
            Rectangle nextRect = (Rectangle) iter.next();
			if (!confidenceMonophlyUncertainRects.contains(nextRect) && 
					!confidenceNotMonophyleticRects.contains(nextRect)) {            
				returnString += getNotMonophyleticAreaString(nextRect);
			}
        }
        for (Iterator iter = monophylyUncertainRects.iterator(); iter.hasNext();) {
            Rectangle nextRect = (Rectangle) iter.next();
			if (!confidenceMonophlyUncertainRects.contains(nextRect) && 
					!confidenceNotMonophyleticRects.contains(nextRect)) {            
				returnString += getMonophylyUncertainAreaString(nextRect);
			}
        }                
        for (Iterator iter = confidenceEmptyRects.iterator(); iter.hasNext();) {
			Rectangle nextRect = (Rectangle) iter.next();
			if (!confidenceMonophlyUncertainRects.contains(nextRect) && 
					!confidenceNotMonophyleticRects.contains(nextRect)) {
				returnString += getConfidenceUncertainAreaString(nextRect);
			}	
		}
        for (Iterator iter = confidenceMonophlyUncertainRects.iterator(); iter.hasNext();) {
			Rectangle nextRect = (Rectangle) iter.next();
			returnString += getConfidenceMonoUncertainAreaString(nextRect);
		}
        for (Iterator iter = confidenceNotMonophyleticRects.iterator(); iter.hasNext();) {
			Rectangle nextRect = (Rectangle) iter.next();
			returnString += getConfidenceNotMonoAreaString(nextRect);
		}

        Object[] nameAndId = getPageDAO().getGroupNameAndNodeIdForPage(page.getParentPageId());
        // don't want to do it for life on earth!
        if (nameAndId != null) {
	        String url = getUrlBuilder().getURLForBranchPage(URLBuilder.NO_HOST_PREFIX, (String) nameAndId[0], (Long) nameAndId[1]);
	        String parentGroupName = (String) nameAndId[0];
	        // add the root rect
	        String parentArrowAreaString = getAreaBeginningString(rootRectangle.x, rootRectangle.y, rootRectangle.x + rootRectangle.width, rootRectangle.y + rootRectangle.height);
	        parentArrowAreaString += " href=\"" + url + "\" alt=\"[down<--]" + parentGroupName + "\" title=\"" + parentGroupName + "\">" + "\n";
	        returnString += parentArrowAreaString; 
        }
        return returnString;
    }
    
    protected Color getColorForNode(Node node) {
    	MappedNode coloredNode = (MappedNode) node;
    	//boolean hasPage = coloredNode.getHasPage();
    	boolean leadsToCompletePage = false; 
    	// if it has a page, check to see if the page leads to a complete page
    	/*if (hasPage) {
    		Long pageId = getPageDAO().getPageIdForNode(coloredNode);
    		leadsToCompletePage = getPageDAO().getPageLeadsToCompletePage(pageId);
    	}*/
    	boolean isTrunkNode = coloredNode.getTrunkNode() == Node.TRUNK_NODE; 
		if (getTrunkColoringEnabled() && isTrunkNode){
			return trunkColor;
    	} else if (leadsToCompletePage && !(coloredNode.getNodeId().equals(((MappedNode) getRootNode()).getNodeId()))) {
			return leadsToComplete;
		} else {
			return getIsBlackLines() ? Color.black : getLightGrayColor();
		}
    }    
    
    /**
     * Overridden to get color based on the status of the page
     * @param view
     * @return
     */
    protected boolean getIsBlackLine(NodeView view) {
        return getIsBlackLines();
    }
    protected Color getLightGrayColor() {
    	return lightGray;
    }
    
    /**
     * Overridden to do nothing since we don't want node circles on these trees
     * @param circleValues
     * @param repaintRect
     * @param g
     */
    @SuppressWarnings("unchecked")
    protected void conditionallyRepaintCircles(ArrayList circleValues, Rectangle repaintRect, Graphics g) {
    }
    
    private String getExtinctAreaString(Point point) {
        String returnString = getAreaBeginningString(point.x, point.y, point.x + extinctImageWidth, point.y + extinctImageHeight);
        return returnString + 
            "href=\"/tree/home.pages/treeinterpret.html#extinct\" alt=\"extinct icon\" onmouseover=\"doTooltip(event,extinct_tooltip)\" onmouseout=\"if(hideTip!=null) hideTip()\">";
    }
    
    private String getMonophylyUncertainAreaString(Rectangle rect) {
        String returnString = getAreaBeginningString(rect);
        return returnString + "href=\"/tree/home.pages/treeinterpret.html#monounc\" alt=\"Monophyly Uncertain\" onmouseover=\"doTooltip(event,monophyly_uncertain_tooltip)\" onmouseout=\"if(hideTip!=null) hideTip()\">";
    }
    
    private String getNotMonophyleticAreaString(Rectangle rect) {
        String returnString = getAreaBeginningString(rect);
        return returnString + "href=\"/tree/home.pages/treeinterpret.html#nonmono\" alt=\"Not Monophyletic\" onmouseover=\"doTooltip(event, not_monophyletic_tooltip)\" onmouseout=\"if(hideTip!=null) hideTip()\">";
    }
	private String getConfidenceUncertainAreaString(Rectangle rect) {
    	String returnString = getAreaBeginningString(rect);
    	//href="/tree/home.pages/treeinterpret.html#nonmono" alt="Phylogenetic position of group is uncertain" onmouseover="doTooltip(event, incert_tooltip)" onmouseout="if(hideTip!=null) hideTip()"
    	return returnString + "href=\"/tree/home.pages/treeinterpret.html#incertae\" alt=\"Phylogenetic position of group is uncertain\" onmouseover=\"doTooltip(event, incert_tooltip)\" onmouseout=\"if(hideTip!=null) hideTip()\">";
    }
	private String getConfidenceNotMonoAreaString(Rectangle rect) {
		String returnString = getAreaBeginningString(rect);
		return returnString + "href=\"/tree/home.pages/treeinterpret.html#nonmono\" alt=\"Phylogenetic position of group is uncertain and group is not monophyletic\" onmouseover=\"doTooltip(event, incert_not_monophyletic_tooltip)\" onmouseout=\"if(hideTip!=null) hideTip()\">";
	}
	private String getConfidenceMonoUncertainAreaString(Rectangle rect) {
		String returnString = getAreaBeginningString(rect);
		return returnString + "href=\"/tree/home.pages/treeinterpret.html#monounc\" alt=\"Phylogenetic position of group is uncertain and monophyly of group is uncertain\" onmouseover=\"doTooltip(event, incert_monophyly_uncertain_tooltip)\" onmouseout=\"if(hideTip!=null) hideTip()\">";

	}
	private String getAreaBeginningString(Rectangle rect) {
		return getAreaBeginningString(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
	}
    private String getAreaBeginningString(int startX, int startY, int endX, int endY) {
        return "<area coords=\"" + startX + "," + startY + "," + 
            endX + "," + endY + "\"";
    }
    
    private String getLinkAreaString(NodeView view, Point startPoint, int width, int height) {
        String treeString = getTreeStringForNodeView(view);
        Long nodeId = ((MappedNode) view.getNode()).getNodeId();
        String returnString = getAreaBeginningString(startPoint.x, startPoint.y, startPoint.x + width, startPoint.y - height);
        String urlString = getUrlBuilder().getURLForBranchPage(URLBuilder.NO_HOST_PREFIX, treeString, nodeId);
        returnString += "href=\"" + urlString + "\" alt=\"[up-->]" + treeString + 
            "\" title=\"" + treeString + "\">";
        return returnString;
    }
    
    protected int getYStep() {
        return (int)(myFontMetrics.getHeight()*1.3);
    }
    
    public int getDefaultLineLength() {
        return getDefaultStringWidth();
    }    
    
    public int getDefaultStringWidth() {
        return myFontMetrics.stringWidth("     ");
    }  
    
    protected int calculateRootOverrun(NodeView rootView) {
        return 0;
    }
    
    protected void calculateRootLineLength(int rootOverrun) {
        rootLineLength = getDefaultLineLength() - getRootLineLessPixels();
    } 
    
    protected int getRootLineLessPixels() {
    	return 6;
    }    
	protected Color getQMarkFontColor() {
		return Color.black;
	}   
	protected Font getQuestionMarkFont() {
		return microsoftArialFont;
	}

    /** Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     */
    public void mouseClicked(MouseEvent e) {
    }
    
    /** Invoked when the mouse enters a component.
     *
     */
    public void mouseEntered(MouseEvent e) {
    }
    
    /** Invoked when the mouse exits a component.
     *
     */
    public void mouseExited(MouseEvent e) {
    }
    
    /** Invoked when a mouse button has been pressed on a component.
     *
     */
    public void mousePressed(MouseEvent e) {
    }
    
    /** Invoked when a mouse button has been released on a component.
     *
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * @return Returns the isBlack.
     */
    public boolean getIsBlackLines() {
        return isBlackLines;
    }

    /**
     * @param isBlack The isBlack to set.
     */
    public void setIsBlackLines(boolean isBlack) {
        this.isBlackLines = isBlack;
    }

	/**
	 * @return Returns the pageDAO.
	 */
	public PageDAO getPageDAO() {
		return pageDAO;
	}

	/**
	 * @param pageDAO The pageDAO to set.
	 */
	public void setPageDAO(PageDAO pageDAO) {
		this.pageDAO = pageDAO;
	}

	public boolean getDrawDescriptions() {
		return drawDescriptions;
	}

	public void setDrawDescriptions(boolean drawDescriptions) {
		this.drawDescriptions = drawDescriptions;
	}
	public boolean getDrawAuthorities() {
		return drawAuthorities;
	}

	public void setDrawAuthorities(boolean drawAuthorities) {
		this.drawAuthorities = drawAuthorities;
	}
	public boolean getIncludeSpecimenInfo() {
		return includeSpecimenInfo;
	}

	public void setIncludeSpecimenInfo(boolean includeSpecimenInfo) {
		this.includeSpecimenInfo = includeSpecimenInfo;
	}

	public URLBuilder getUrlBuilder() {
		return urlBuilder;
	}

	public void setUrlBuilder(URLBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
	}
	
	@SuppressWarnings("unchecked")
	public void constructLocations() {
		super.constructLocations();
		
        // Add all the circles and lines to our arrays        
        Iterator it = nodeViews.iterator();
        while (it.hasNext()) {
            NodeView currentView = (NodeView)it.next();         
            Node currentNode = currentView.getNode();
            
            int yLoc = currentView.getY() + getHalfCircleDiameter();
            int horizLineStartX;
            if (currentView.getIsRootView()) {//root
                horizLineStartX = currentView.getX() - rootLineLength;
            } else {  //not the root               
                horizLineStartX = currentView.getParent().getX()+ getHalfCircleDiameter();
            }            
            
            boolean notPhylesis = currentNode.getPhylesis() != Node.MONOPHYLETIC;               
            boolean isTrunkNode = currentNode.getTrunkNode() == Node.TRUNK_NODE;
            
            // determine lines to draw 
            if (notPhylesis && isTrunkNode) {
	            // Connect the vertical line a little further by drawing a small horizontal one
	            trunkVertLineList.add(new int[] {horizLineStartX, yLoc+1, horizLineStartX+1, yLoc+1});                
            }

            if (!currentView.isTerminal() && isTrunkNode) { 
                //vertical lines
                ArrayList children = currentView.getChildren();
                Iterator childIter = children.iterator();
                                
                while(childIter.hasNext()) {
                	NodeView childCurrView = (NodeView)childIter.next();

                	boolean childIsTrunk = childCurrView.getNode().getTrunkNode() == Node.TRUNK_NODE;
                	if (childIsTrunk) {
                		int x = currentView.getX() + getHalfCircleDiameter();
                		int y1 = childCurrView.getY() + getHalfCircleDiameter();
                		int y2 = currentView.getY() + getHalfCircleDiameter();
                        List vertLineList = trunkVertLineList;                
                        vertLineList.add(new int[] {x, y1 , x, y2});
                        vertLineList.add(new int[] {x+1, y1 , x+1, y2});                		
                	}
                }
            }
            
        }		
	}
	
	public boolean getTrunkColoringEnabled() {
		return trunkColoringEnabled;
	}
	
	public void setTrunkColoringEnabled(boolean trunkColoringEnabled) {
		this.trunkColoringEnabled = trunkColoringEnabled;
	}
	public Color getTrunkColor() {
		return trunkColor;
	}
	public void setTrunkColor(Color trunkColor) {
		this.trunkColor = trunkColor;
	}
}
