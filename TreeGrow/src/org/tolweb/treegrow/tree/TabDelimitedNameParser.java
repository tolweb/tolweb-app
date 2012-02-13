/*
 * Created on Apr 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.tree;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.treegrow.main.FileUtils;
import org.tolweb.treegrow.main.StringUtils;



/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TabDelimitedNameParser {
	public static final String LINE_SEPARATORS = "\n\r\f";
    /**
     * Returns a list of nodes found in the file located at path.
     * Any nodes with a null parent can be presumed to be children
     * of whatever node that the new nodes will be added to. 
     * @param path
     * @return
     */
	public static List parseNames(String path) {
        String fileContents;
        try {
            fileContents = FileUtils.getFileContents(path);    
        } catch (Exception e) {
            return null;
        }
        return parseNamesString(fileContents, System.getProperty("line.separator"));
    }
	
	public static List getAllNodeNames(String namesString) {
		String regexString = "^\\s*([^\\s]+)";
		Pattern regex = Pattern.compile(regexString, Pattern.MULTILINE);
		Matcher matcher = regex.matcher(namesString);
		ArrayList names = new ArrayList();
		while (matcher.find()) {
			names.add(matcher.group(1));
		}
		return names;
	}
	
	public static List parseNamesString(String namesString, String lineSeparator, String extinctIndicatorString,
			String foreignNodeIdSeparator) {
    	// need to remember how many tabs each node had
    	Hashtable nodesToNumTabs = new Hashtable();
    	// keep track of the nodes that are our current ancestors
    	Stack currentAncestorNodes = new Stack();
        List returnList = new ArrayList();    	
        StringTokenizer tokenizer = new StringTokenizer(namesString, lineSeparator);
        // what we saw on the line above us
        int lastNumTabs = 0;
        Node currentParentNode = null;
        Node lastNodeSeen = null;
        String tab = "\t";
        boolean hasExtinctIndicator = StringUtils.notEmpty(extinctIndicatorString);
        while (tokenizer.hasMoreTokens()) {
        	boolean isExtinct = false;
            // what we see on this line
        	int numLeadingTabs = 0;
            String nextLine = tokenizer.nextToken();
            // skip empty lines
            if (StringUtils.isEmpty(nextLine)) {
            	continue;
            }
            Integer foreignNodeId = null;            
            int foreignNodeIdIndex = -1;
            if (foreignNodeIdSeparator != null) {
            	foreignNodeIdIndex = nextLine.indexOf(foreignNodeIdSeparator);
            }
            String foreignDbIdString = null;
            // assume the foreign node id is at the end of the line, so take one + the separator substring
            if (foreignNodeIdIndex != -1) {
                try {
                    foreignDbIdString = nextLine.substring(foreignNodeIdIndex + 1);
                } catch (Exception e) {}
                // strip this info off the line so it doesn't stop the name parser from working
                nextLine = nextLine.substring(0, foreignNodeIdIndex);                                    
            }            
            StringTokenizer tabTokenizer = new StringTokenizer(nextLine, tab, true);
            String leadingToken = tabTokenizer.nextToken();
            if (leadingToken.equals(tab)) {
                while (leadingToken.equals(tab)) {
                    numLeadingTabs++;
                    leadingToken = tabTokenizer.nextToken();
                }
            }
            String name = leadingToken;
            if (hasExtinctIndicator) {
            	if (name.endsWith(extinctIndicatorString)) {
            		name = name.substring(0, name.length() - 1);
            		isExtinct = true;
            	}
            }
            String authority = null;
            String date = null;
            String synonym = null;
            if (tabTokenizer.hasMoreTokens()) {
                // need to eat the next tab
                tabTokenizer.nextToken();
                if (tabTokenizer.hasMoreTokens()) {
                    authority = tabTokenizer.nextToken();
                }
            }
            if (tabTokenizer.hasMoreTokens()) {
                // need to eat the next tab
                tabTokenizer.nextToken();
                if (tabTokenizer.hasMoreTokens()) {
                    date = tabTokenizer.nextToken();
                }
            }
            // check to see if there is a synonym
            if (tabTokenizer.hasMoreTokens()) {
            	tabTokenizer.nextToken();
            	if (tabTokenizer.hasMoreTokens()) {
            		synonym = tabTokenizer.nextToken();
            	}
            }
            Node node = new Node();
            node.setName(name);
            if (isExtinct) {
            	node.setExtinct(Node.EXTINCT);
            }
            nodesToNumTabs.put(node, new Integer(numLeadingTabs));
            if (StringUtils.notEmpty(authority)) {
                node.setNameAuthority(authority);
            }
            if (StringUtils.notEmpty(date)) {
                try {
                    node.setNameDate(Integer.parseInt(date.trim()));
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                }
            }
            if (StringUtils.notEmpty(synonym)) {
            	OtherName otherName = new OtherName();
            	otherName.setName(synonym);
            	otherName.setNode(node);
            	Vector otherNames = new Vector(); 
            	otherNames.add(otherName);
            	node.setOtherNames(otherNames);
            }
            if (foreignDbIdString != null) {
                node.setSourceDbNodeId(foreignDbIdString);
            }
            if (numLeadingTabs > lastNumTabs) {
                // we've run into a new level of node, so add this node to the
                // parent, and mark it as the current parent
                lastNumTabs = numLeadingTabs;
                if (lastNodeSeen != null) {
                	addNodeToChildren(lastNodeSeen, node);
                	currentParentNode = lastNodeSeen;
                } else {
                	currentParentNode = node;
                }
                currentAncestorNodes.push(currentParentNode);
            } else if (numLeadingTabs < lastNumTabs) {
            	// idea here is to pop off until either:
            	// a) the stack is empty or
            	// b) we have more tabs than the last thing popped off
            	boolean keepPopping = true;
            	while (keepPopping && !currentAncestorNodes.isEmpty()) {
            		currentParentNode = (Node) currentAncestorNodes.pop();
            		int ancestorTabs = ((Integer) nodesToNumTabs.get(currentParentNode)).intValue(); 
            		// we're at the same level or further indented
            		if (numLeadingTabs >= ancestorTabs) {
            			keepPopping = false;
            			// if we are further indented, we need to push the parent back on
            			if (numLeadingTabs != ancestorTabs) {
            				currentAncestorNodes.push(currentParentNode);
            			} else {
            				// here, we're at the same level, so grab the parent of
            				// our peer node
            				currentParentNode = currentParentNode.getParent();
            			}
            		}
            	}
            	// if it's empty, then set the parent to null because we have 
            	// less tabs than any of the current ancestors, so we can't possibly be a descendant
            	if (currentAncestorNodes.isEmpty()) {
            		currentParentNode = null;
            	}
                if (currentParentNode != null) {
                	addNodeToChildren(currentParentNode, node);
                }
                lastNumTabs = numLeadingTabs;
            } else if (lastNumTabs == numLeadingTabs) {
                if (currentParentNode != null) {
                    Integer numTabsForParent = (Integer) nodesToNumTabs.get(currentParentNode);
                    if (numLeadingTabs > numTabsForParent.intValue()) {
                        addNodeToChildren(currentParentNode, node);
                    } else {
                        // pop off the current parent since it's an ancestor
                        currentAncestorNodes.pop();
                        // then make this node the current node
                        currentParentNode = node;
                        currentAncestorNodes.push(node);
                    }
                }
            }
            lastNodeSeen = node;
            returnList.add(node);
        }
        // Go through the list and verify that there are no single-child nodes
        for (Iterator iter = new ArrayList(returnList).iterator(); iter.hasNext();) {
            Node currentNode = (Node) iter.next();
            if (currentNode.getChildren().size() == 1) {
                Node newUnnamedChild = new Node();
                currentNode.addToChildren(newUnnamedChild);
                returnList.add(newUnnamedChild);
            }
        }
        return returnList;    	
	}
	
	/**
	 * Adds child to parent and checks to see if child is parenthesized in order
	 * to change the name
	 * @param parentNode
	 * @param childNode
	 */
	private static void addNodeToChildren(Node parentNode, Node childNode) {
		if (parentNode != null && childNode != null) {
			parentNode.addToChildren(childNode);
			String childName = childNode.getName();
			if (StringUtils.notEmpty(childName)) {
				// if the name is parenthesized then add the parent name at the beginning
				if (childName.startsWith("(") && childName.endsWith(")")) {
					String newChildName = parentNode.getName() + " " + childName;
					childNode.setName(newChildName);
				}
			}
		}
	}
    
    public static List parseNamesString(String namesString, String lineSeparator) {
    	return parseNamesString(namesString, lineSeparator, null, null);
    }
}
