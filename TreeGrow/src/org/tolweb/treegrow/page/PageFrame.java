/*
 * PageFrame.java
 *
 * Created on July 15, 2003, 8:55 AM
 */

package org.tolweb.treegrow.page;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.undo.*;

import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.main.undo.*;
import org.tolweb.treegrow.tree.Node;


/**
 * Page editor frame.  Contains a splitpane with a JTree on the left and any 
 * number of different editor panels on the right
 */
public class PageFrame extends ToLJFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8489310754519465437L;
	private DefaultMutableTreeNode rootNode, pageNode, imagesNode;
    private TreePath rootPath;
    //private TreePath creditsPath;
    //private TreePath textSectionsPath;
    //private TreePath pagePath;
    //private TreePath acksPath;
    //private TreePath copyrightPath;
    private TreePath otherPagesPath;
    //private TreePath imagesPath;
    private TreePath nodeImagesPath;
    //private TreePath referencesPath;
    //private TreePath internetLinksPath;
    private DefaultTreeModel model;
    private JSplitPane splitPane;
    private JTree tree;
    private Node node;
    //private ReferencesPanel referencesPanel;
    //private InternetLinksPanel internetLinksPanel;
    //private NodeDataPanel nodeDataPanel;
    //private CreditsPanel creditsPanel;
    //private TextSectionsPanel textSectionsPanel;
    //private PagePanel pagePanel;
    //private AcknowledgementsPanel acksPanel;
    //private CopyrightPanel copyrightPanel;
    //private TitleIllustrationsPanel imagesPanel;
    private JScrollPane leftScrollPane, rightScrollPane;
    private Hashtable treePathToComponentMapping;
    private Hashtable componentToTreePathMapping;
    private JMenu windowMenu;
    private UndoManager undoManager;
    private UndoAction undoAction;
    private RedoAction redoAction;
    
    
    /**
     * Used as a counter to set titles for page editors of unnamed nodes
     */
    private static int numUnnamed = 1;
    
   
    /**
     * Creates all of the editor panels that will exist if the node actually
     * has a page.  Otherwise, these panels aren't present
     /
    private void initPagePanels() {
        referencesPanel = new ReferencesPanel(this);
        internetLinksPanel = new InternetLinksPanel(this);
        creditsPanel = new CreditsPanel(this);
        textSectionsPanel = new TextSectionsPanel(this);
        pagePanel = new PagePanel(this);
        otherPagesPanel = new OtherPagesPanel(this);
        imagesPanel = new TitleIllustrationsPanel(this);    
    }*/
    
    /**
     * Called to change the title of the frame if the node title changes
     */
    public void updateTitle() {
        if (node.getName() != null && !node.getName().equals("")) {
            setTitle("Node Data Window: " + node.getName());
            rootNode.setUserObject(node.getName());
            repaint();
        } else {
            // Dont bother incrementing the counter if the title has already
            // been set and there is still no name
            if (!getTitle().startsWith("Node Data")) {
                setTitle("Node Data Window: Unnamed " + numUnnamed);
                rootNode.setUserObject("Unnamed " + numUnnamed);
                numUnnamed++;
            }
        }
    }
    
    public Node getNode() {
        return node;
    }
    
    /**
     * Returns the windows menu (used for bringing various windows toFront
     *
     * @return The Windows menu
     */
    public JMenu getWindowMenu() {
        return windowMenu;
    }
  
    /**
     * Removes the page node from the tree
     /
    public void removePage() {
        if (pageNode != null && pageNode.getParent() != null) {
            model.removeNodeFromParent(pageNode);
            // Called to make sure there is no residual data lying around
            removePagePanels();
        }
        nodeDataPanel.removeTitlePreviewPanel();
    }
    
    /**
     * Adds the page node to the tree
     /
    public void addPage() {
        if (pageNode == null) {
            //System.out.println("adding page nodes");
            addPageNodesToTree();
        } else {
            if (model.getIndexOfChild(rootNode, pageNode) == -1) {
                model.insertNodeInto(pageNode, rootNode, rootNode.getChildCount());            
            }
        }
        nodeDataPanel.addTitlePreviewPanel();
        nodeDataPanel.syncPreview();
    }*/
    
    /**
     * Overridden to return true in order to cause events to fire
     */
    public boolean isFocusable() {
        return true;
    }
    
    public void dispose() {
        requestFocus();
    }
    
    /*public void updateList() {
        if (pagePanel != null && pagePanel.getWriteAsListPanel() != null) {
            pagePanel.getWriteAsListPanel().prepareNodes();
        }
    }*/
       
    /**
     * Sets the panel on the right to be whatever is passed-in
     *
     * @param p The panel to show on the right
     */
    public void showAppropriatePanel(AbstractPageEditorPanel p) {
        if (p != null) {
            tree.setSelectionPath((TreePath) componentToTreePathMapping.get(p));
        }
    }
    
    /*public void toFront() {
        if (pagePanel!=null && pagePanel.getWriteAsListPanel() != null) {
            pagePanel.getWriteAsListPanel().prepareNodes();
        }
        super.toFront();
    }
    
    public void showPagePanel() {
        pagePanel.syncValuesToPage();
        tree.setSelectionPath(pagePath);
    }*/

    
    public void showNodeDataPanel() {
        tree.setSelectionPath(rootPath);
    }
    
    /**
     * Called during an upload in order to have the UI updated in case img
     * paths gets updated to their locations on the server instead of local
     * files
     /
    public void syncChangedTextAreas() {
        if (pagePanel != null && model.getIndexOfChild(rootNode, pageNode) != -1) {
            try {
                if (pagePanel != null) {
                    pagePanel.syncValuesToPage();
                }
                if (textSectionsPanel != null) {
                    textSectionsPanel.syncValuesToTextSection();
                }
                if (creditsPanel != null) {
                    creditsPanel.syncValuesToAcknowledgements();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Syncs the text sections panel to the text sections in memory
     /
    public void syncTextSections() {
        if (textSectionsPanel != null) {
            textSectionsPanel.syncTableToTextSections();
        }
    }*/
    
    /**
     * Builds the JTree with the appropriate nodes
     */
    private JTree buildTree() {
        String rootName = (node.getName() != null && !node.getName().equals("")) ? node.getName() : "Unnamed";
        rootNode = new DefaultMutableTreeNode(rootName);
        model = new DefaultTreeModel(rootNode);
        tree = new JTree(model);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new HideIconRenderer());
        rootPath = new TreePath(rootNode.getPath());
        tree.addSelectionPath(rootPath);
        nodeImagesPath = addNonPageSelection("Images");  
        otherPagesPath = addNonPageSelection("Pages");        
        return tree;
    }
    
    /**
     * Removes any accessory pages that had links to the page that was removed
     * in another page editor
     *
     * @param page The accessory page that was removed in another page
     /
    public void removeLinkedAccPage(AccessoryPage page) {
        if (otherPagesPanel == null) {
            //accessoriesPanel = (AccessoryPagesPanel) initializePanel(accessoriesPath);
        }
        Iterator it = node.getPageObject().getAccessoryPagesLinkedTo(page).iterator();
        Vector accPages = node.getPageObject().getAccessoryPages();
        while (it.hasNext()) {
            AccessoryPage currentPage = (AccessoryPage) it.next();
            int index = accPages.indexOf(currentPage);          
            if (index != -1) {
                //not added to the undo stack
                //RemoveTableItemUndoableEdit edit = new RemoveTableItemUndoableEdit(this, accessoriesPanel, node.getPageObject(), /*accessoriesPanel.getTable(), index);
            } else{
                //System.out.println("cant find the supposed linked accessory page");
            }
        }
    }
    
    private void addPageNodesToTree() {
        //String branchOrLeafString = getNode().getIsLeaf() ? "Leaf " : "Branch ";
        //pageNode = new DefaultMutableTreeNode(branchOrLeafString + "Page");
        //model.insertNodeInto(pageNode, rootNode, rootNode.getChildCount());
        //pagePath = new TreePath(pageNode.getPath());
        //tree.scrollPathToVisible(pagePath);
        //createPageNodes();
        //initializePanel(pagePath);
    }*/
    
    /**
    * Function that actually creates the nodes associated with the page.
    * createPageTree() calls this function to create the nodes associated with the page.
    *
    * @param
    * @return
    /
    public void createPageNodes() {
        imagesPath = addPageSelection("Title Illustrations");
        textSectionsPath = addPageSelection("Text Sections");
        referencesPath = addPageSelection("References");
        internetLinksPath = addPageSelection("Internet Links");
        creditsPath = addPageSelection("Authors, Credits");
    }*/
    
    private void addToBothMappings(TreePath path, AbstractPageEditorPanel panel) {
        treePathToComponentMapping.put(path, panel);
        componentToTreePathMapping.put(panel, path);    
    }
    
    private TreePath addNonPageSelection(String name) {
        return addTreeSelection(rootNode, name);        
    }
    
    private TreePath addTreeSelection(MutableTreeNode parentNode, String name) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        model.insertNodeInto(node, parentNode, parentNode.getChildCount());
        TreePath path = new TreePath(node.getPath());
        tree.scrollPathToVisible(path);
        return path;        
    }

    private TreePath addPageSelection(String name) {
        return addTreeSelection(pageNode, name);
    }
    
    /*private void removePagePanels() {
        removeIfNotNull(imagesPanel);
        imagesPanel = null;
        removeIfNotNull(textSectionsPanel);
        textSectionsPanel = null;
        removeIfNotNull(creditsPanel);
        creditsPanel = null;
        removeIfNotNull(pagePanel);
        pagePanel = null;
        removeIfNotNull(referencesPanel);
        referencesPanel = null;
        removeIfNotNull(internetLinksPanel);
        internetLinksPanel = null;
        pageNode = null;
    }*/
    
    private void removeIfNotNull(Object value) {
        if (value != null) {
            treePathToComponentMapping.remove(value);            
        }
    }
    
}

/**
 * TreeCellRenderer subclass that doesn't show the icon
 */
class HideIconRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
        /**
	 * 
	 */
	private static final long serialVersionUID = 1220058464382647462L;

		public Component getTreeCellRendererComponent(
                           JTree tree,
                           Object value,
                           boolean sel,
                           boolean expanded,
                           boolean leaf,
                           int row,
                           boolean hasFocus) {
       		super.getTreeCellRendererComponent(
                           tree, value, sel,
                           expanded, leaf, row,
                           hasFocus);
		setIcon(null);
       		return this;
	}
}