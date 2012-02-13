package org.tolweb.treegrow.tree;

import java.util.*;

import org.tolweb.treegrow.main.*;

/**
 * Tree is a class that holds information pertaining to the structure
 * and contents of a tree.
 * <p>
 * It provides methods for creating and altering the tree, as well as for
 * fetching information about it's structure and contents
 */
public class Tree {
    private static final String LEFT_PAREN = "(";
    private static final String RIGHT_PAREN = ")";
    private static final String COMMA = ",";

    /**
     *Used to generate newly created node ids
     */
    private static int nextNodeId = -1 ;
    
    /**
     * Stores list of nodes found in tree
     */
    private ArrayList nodeList;

    private ArrayList imageList;
    
    /** 
     *The parenthetical string describing tree structure
     */
    private String treeString;
    
    private Node root;
    private int pageDepth;
    private long downloadDate = -1, modifiedDate = -1, uploadDate = -1;    
    
    /** 
     * Creates a new instance of Tree 
     *
     * @param string The parenthetical string describing tree structure
     * @param list  A list of nodes contained in the tree, unstructured
     */
    public Tree(String string, ArrayList list) {
        treeString = string;
        nodeList = list;
        convertStringToTree();
        
        //figure out if the "next node id" needs to be less than the default (which is -1) used for new nodes
        // ie. -> if we're working with a file that already has unuploaded new nodes
        Iterator it = list.iterator();
        int minID = ((Node)it.next()).getId();
        while (it.hasNext()) {
            minID = Math.min( minID,  ((Node)it.next()).getId());   
        }
        nextNodeId = Math.min(nextNodeId, minID-1);
        imageList = new ArrayList();
    }
    
    public Tree(ArrayList list) {
    	this(list, null);
    }
    
    public Tree(ArrayList list, Node root) {
        nodeList = list;
        imageList = new ArrayList();
        this.root = root;
    }

    private void convertStringToTree() {
        root = buildFamily(treeString, nodeList);
    }
    
    /**
     * Returns the root of building a tree according to a given string.
     * 
     * Converts a parenthetical String that describes TreeStructure into an actual Tree ADT.
     * <p>
     * For example:  
     * <pre>
     *                 a
     *  a(b,c)  -->   / \
     *               b   c
     *  </pre>
     *
     * @param familyString the new string description of the tree
     * @param nodes The nodes to manipulate familial relations
     */
    private Node buildFamily(String familyString, ArrayList nodes) {
    	System.out.println("family string is: " + familyString);
        String lastToken = "";        
        StringTokenizer st = new StringTokenizer(familyString, "" + LEFT_PAREN + RIGHT_PAREN + COMMA , true) ;
        Stack nodeStack = new Stack();
        // Set this so we don't push a null node onto the stack the first time around
        Node currentNode = null, firstNode = null;
        //Node currentParentNode = new Node(-1, -1, -1, -1, false, false, false, "");
        Node currentParentNode = new Node(-1);
        // Needed to indicate if this is the last time around the loop -- when there 
        // are no more tokens left in the tokenizer
        boolean lastTime = false, firstTime = true; 
        String nextToken = st.nextToken();
        while (st.hasMoreTokens() || lastTime || firstTime) {
            String currentToken = nextToken;
            if (st.hasMoreTokens()) {
                nextToken = st.nextToken();
            }
            
             if (currentToken.equals(LEFT_PAREN)){
                 //bookkeeping only.  All the action happens when one of the other delimiters is hit
                 nodeStack.push(currentParentNode);
                 currentParentNode = currentNode;
                 // Reset this so that it is empty until we see the next token
                 currentNode = null;
             } else if (currentToken.equals(COMMA)){
                 if ( !lastToken.equals(RIGHT_PAREN) ) {
                     currentParentNode.addToChildren(currentNode);
                 }
             } else if (currentToken.equals(RIGHT_PAREN)){
                    if ( !lastToken.equals(RIGHT_PAREN) ) {
                        currentParentNode.addToChildren(currentNode);
                    }
                    currentNode = currentParentNode;
                    currentParentNode = (Node) nodeStack.pop();
                    if (currentNode != firstNode) {
                        currentParentNode.addToChildren(currentNode);
                    }
             } else {
                 currentNode = findNode(new Integer(currentToken).intValue(), nodes);
                 if (currentNode == null ) {
                     System.out.println("curr token = " + currentToken);
                 }
                 
                 if (firstNode == null) {
                    firstNode = currentNode;
                 }
                 //System.out.println("currentToken=" + currentToken + " Just found currentNode of: " + currentNode);
                 // Check the nextToken to see if it is a left paren.  This
                 // indicates whether the currentNode is a terminal.  We don't want
                 // to clear the children of terminal nodes since those children
                 // nodes aren't in the tree string and will not get added again.
                 if (nextToken.equals(LEFT_PAREN)) {
                    currentNode.clearChildren();
                 }
                 // Dont clear the parent if this is the first node
                 if (currentNode != firstNode) {
                    currentNode.setParent(null);
                 }
             }
             lastToken = currentToken;
             lastTime = !st.hasMoreTokens() && !lastTime;
             firstTime = false;
        }
        return currentNode;
    } 
    
    public Node findNode(int id) {
        return findNode(id, nodeList);
    }
    
    /**
     * return the node with the id passed as an argument.
     *
     * @param id   the id of the desired node
     *
     * @return  node with the id passed as an argument
     */
    public Node findNode(int id, ArrayList toSearch) {
        Iterator it = toSearch.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * Looks for the NodeImage with the passed-in id
     *
     * @param id The id of the nodeImage to look for
     * @return The NodeImage object if it exists, or null
     */
    public NodeImage findNodeImage(int id) {
        Iterator it = imageList.iterator();
        while (it.hasNext()) {
            NodeImage img = (NodeImage) it.next();
            if (img.getId() == id) {
                return img;
            }
        }
        return null;
    }
    
    /**
     * Look for the node image with the lowest id.  This is used in assigning
     * new ids for images that haven't been uploaded to the server
     *
     * @return The current min node id
     */
    public int getMinNodeImageId() {
        int minNodeId = 0;
        Iterator it = imageList.iterator();
        while (it.hasNext()) {
            NodeImage img = (NodeImage) it.next();
            if (img.getId() < minNodeId) {
                minNodeId = img.getId();
            }
        }        
        return minNodeId;
    }
    
    public Vector getLocalNodeImages() {
        Iterator it = imageList.iterator();
        Vector newList = new Vector();
        while (it.hasNext()) {
            NodeImage nodeImg = (NodeImage) it.next();
            if (nodeImg.isLocal()) {
                newList.add(nodeImg);
            }
        }
        return newList;    
    }
    
    public Vector getChangedNodeImages() {
        Iterator it = imageList.iterator();
        Vector newList = new Vector();
        while (it.hasNext()) {
            NodeImage img = (NodeImage) it.next();
            if (img.changedFromServer()) {
                newList.add(img);
            }
        }
        return newList;    
    }    

    public void mergeImages(List values) {
        Hashtable hash = new Hashtable();
        Iterator it = imageList.iterator();
        while (it.hasNext()) {
            NodeImage img = (NodeImage) it.next();
            hash.put(new Integer(img.getId()), "");
        }
        // Check to see if this exists.  If it doesn't add it
        it = values.iterator();
        while (it.hasNext()) {
            NodeImage img = (NodeImage) it.next();
            if (!hash.containsKey(new Integer(img.getId()))) {
                imageList.add(img);
            }
        }
    }
    
    public ArrayList findNodesExactName(String pattern) {
            return findNodesExactName(pattern, nodeList);
    }

    /**
     * return list of nodes with name exactly matching string 
     *  passed as an argument.
     *
     * @param pattern   the exact name of the desired node(s)
     *
     * @return  list of nodes with name exactly matching pattern
     */    
    public ArrayList findNodesExactName(String pattern, ArrayList toSearch) {
        ArrayList returnList = new ArrayList();
        Iterator it = toSearch.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (pattern.toLowerCase().equals(node.getName().toLowerCase())) {
                returnList.add(node);
            }
        }
        return returnList;
    }    
    

    
    
    public ArrayList findNodesPartialName(String pattern) {
            return findNodesPartialName(pattern, nodeList);
    }

    /**
     * return list of nodes for which the pattern is a substring of 
     * the node's name.
     *
     * @param pattern   a substring found in all desired nodes
     *
     * @return  list of nodes with pattern as a substring
     */    
    public ArrayList findNodesPartialName(String pattern, ArrayList toSearch) {
        ArrayList returnList = new ArrayList();
        Iterator it = toSearch.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.getName().toLowerCase().indexOf(pattern.toLowerCase()) != -1 ) {
                returnList.add(node);
            }
        }
        return returnList;
    }        
    
    /**
     * Converts the Tree ADT to parenthetical String.
     * <p>
     * For example:  
     * <pre>
     *    a
     *   / \     -->   a(b,c)
     *  b   c 
     *  </pre>
     */    
    public String toString() {
        Stack nodeStack = new Stack();
        StringBuffer sb = new StringBuffer();
        nodeStack.push(root);
        ArrayList terminalNodes = getTerminalNodes();
        while (!nodeStack.isEmpty()) {
            Object nodeOrRightParen = nodeStack.pop();
            if (nodeOrRightParen instanceof Node) {
                Node node = (Node) nodeOrRightParen;
                sb.append(node.getId());
                ArrayList chillins = node.getChildren();
                if ( !terminalNodes.contains(node) && chillins != null && chillins.size() > 0) {
                    sb.append(LEFT_PAREN);
                    nodeStack.push(RIGHT_PAREN);
                    int size = chillins.size();
                    for (int i = size - 1; i >= 0; i--) {
                        nodeStack.push(chillins.get(i));
                        if (i != 0) {
                            nodeStack.push(COMMA);
                        }
                    }
                }
            } else {
                sb.append((String) nodeOrRightParen);
            }
        }
        return sb.toString();    
    }
    
    /**
     * Returns string for the 1-page-deep subtree of the given node
     *
     * @param rootNode The node to start at
     */
    public String getSubTreeString( Node rootNode ) {
        
        Stack nodeStack = new Stack();
        StringBuffer sb = new StringBuffer();
        nodeStack.push(rootNode);
        while (!nodeStack.isEmpty()) {
            Object nodeOrRightParen = nodeStack.pop();
            if (nodeOrRightParen instanceof Node) {
                Node node = (Node) nodeOrRightParen;
                sb.append(node.getId());
                ArrayList chillins = node.getChildren();
                if ((node == rootNode || !node.hasPage()) && chillins != null && chillins.size() > 0) {
                    sb.append(LEFT_PAREN);
                    nodeStack.push(RIGHT_PAREN);
                    int size = chillins.size();
                    for (int i = size - 1; i >= 0; i--) {
                        nodeStack.push(chillins.get(i));
                        if (i != 0) {
                            nodeStack.push(COMMA);
                        }
                    }
                }
            } else {
                sb.append((String) nodeOrRightParen);
            }
        }
        return sb.toString();
    }
    
    /**
     * Get list of all terminals on the full tree
     */
    public ArrayList getTerminalNodes() {
        ArrayList terminalNodes = new ArrayList();
        Stack nodeStack = new Stack();
        nodeStack.push(root);
        while (!nodeStack.isEmpty()) {
            Node node = (Node) nodeStack.pop();
            ArrayList chillins = node.getChildren();
            if (chillins.size() == 0) {
                terminalNodes.add(node);
            } else {
                Node firstChild = (Node) chillins.get(0);
                if (findNode(firstChild.getId(), nodeList) == null) {
                    terminalNodes.add(node);
                } else {
                    int size = chillins.size();
                    for (int i = size - 1; i >= 0; i--) {
                        nodeStack.push(chillins.get(i));
                    }
                }
            } 
        }
        return terminalNodes;        
    }
    
    /**
     * Tests if given node is terminal in the full tree
     */
    public boolean isTerminalNode(Node node) {
        ArrayList chillins = node.getChildren();
        if (chillins.size() == 0) {
            return true;
        } else {
            Node firstChild = (Node) chillins.get(0);
            return findNode(firstChild.getId(), nodeList) == null;
        }
    }
    
    /**
     * Returns list of all the nodes that are locked/checked-out
     */
    public ArrayList getCheckedOutNodes(){
        Iterator it = nodeList.iterator();
        ArrayList checkedOutNodes = new ArrayList();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.getCheckedOut()) {
                checkedOutNodes.add(node);
            }
        }    
        return checkedOutNodes;
    }

    /**
     * Returns list of all the nodes that are not locked/checked-out
     */    
    public ArrayList getUnCheckedOutNodes(){
        Iterator it = nodeList.iterator();
        ArrayList unCheckedOutNodes = new ArrayList();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (!node.getCheckedOut()) {
                unCheckedOutNodes.add(node);
            }
        }    
        return unCheckedOutNodes;
    }
    
    public Node getRoot() {
        return root;
    }     
    public void setRoot(Node root) {
    	this.root = root;
    }

    /**
     * Returns all nodes from the full tree
     */    
    public ArrayList getNodeList() {
        return nodeList;
    }

    /**
     * Returns all nodes from the full tree that have attached pages
     */
    public ArrayList getNodesWithPages() {
        ArrayList pageList = new ArrayList();
        Stack nodeStack = new Stack();
        nodeStack.push(root);
        while (!nodeStack.isEmpty()) {
            Node node = (Node) nodeStack.pop();
            Iterator it = node.getChildren().iterator();
            while (it.hasNext()) {
                nodeStack.push(it.next());
            }
            //Page page = node.getPageObject();
            if (node.hasPageOnServer() && node.getCheckedOut()){
                pageList.add(node);
            }
        }
        return pageList;         
    }
    
    /**
     * Returns a comma-separated list of new node ids
     */
    public String getNewNodeIdsString() {
        String idStr = "";
        Iterator it = nodeList.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.getId() < 0) {
                idStr += node.getId() + "";
                if (it.hasNext()) {
                    idStr += ",";
                }
            }
        }
        return idStr;
    }
    
    /**
     * Returns a list of nodes with pages that already exist on the server
     *
     * @return a list of nodes with pages that already exist on the server
     */
    public ArrayList getNonNewNodesWithPages() {
        ArrayList pageList = new ArrayList();
        Iterator it = getNodesWithPages().iterator();
        while (it.hasNext()) {
            Node node = (Node)it.next();
            if (node.getId() > 0) {
                pageList.add(node);
            }
        }
        return pageList;         
    }

    /**
     * Returns all nodes from the full tree that have attached pages and 
     * have some data that has changed from that on the server
     */        
    public ArrayList getChangedNodesWithPages() {
        ArrayList pageList = new ArrayList();
        Iterator it = getNodesWithPages().iterator();
        Controller controller = Controller.getController();
        while (it.hasNext()) {
            Node node = (Node)it.next();
            if (controller.getNodeOneDeepChanged(node)) {
                pageList.add(node);
            }
        }
        return pageList;         
    }     
    
    /**
     * Remove node from list. No impact on the Tree
     */
    public void removeNode(Node node) {
        nodeList.remove(node);
    }

    /**
     * Add node to the list. No impact on the Tree
     */    
    public void addNode(Node node) {
        nodeList.add(node);
    }

    /**
     * Rebuild the node relationships, based on a new parenthetical string.
     */    
    public void updateTree(String newString) {
        treeString = newString;
        convertStringToTree();
    }
    
    
    public int getPageDepth() {
        return pageDepth;
    }
    
    /** 
     * New id for new node
     */
    public static int getNextNodeId() {
        return nextNodeId--;
    }
    
    public long getDownloadDate() {
        return downloadDate;
    }
    
    public void setDownloadDate(long value) {
        downloadDate = value;
    }
    
    public long getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(long value) {
        uploadDate = value;
    }
    
    public long getModifiedDate() {
        return modifiedDate;
    }
    
    public void setModifiedDate(long value) {
        modifiedDate = value;
    }
       
    public void addToImages(NodeImage value) {
        imageList.add(value);
    }
    
    public void removeFromImages(NodeImage value) {
        imageList.remove(value);
    }
    
    public ArrayList getImages() {
        return imageList;
    }
    
    public void setImages(ArrayList value) {
        imageList = value;
    }
    
    public Vector getImagesForNode(int nodeId) {
        Iterator it = imageList.iterator();
        Vector vec = new Vector();
        while (it.hasNext()) {
            NodeImage img = (NodeImage) it.next();
            Iterator it2 = img.getNodes().iterator();
            while (it2.hasNext()) {
                ImageNode in = (ImageNode) it2.next();
                if (in.getNodeId() == nodeId) {
                    vec.add(img);
                    break;
                }
            }
        }
        return vec;
    }
    
    public String getCommaSeparatedNodeIds() {
        ArrayList idsList = new ArrayList();
        for (Iterator iter = getCheckedOutNodes().iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            idsList.add(node.getId() + "");
        }
        String returnString = StringUtils.returnCommaJoinedString(idsList);
        returnString = StringUtils.removeSpaces(returnString);
        return returnString;
    }
}