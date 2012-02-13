package org.tolweb.treegrow.tree;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;
import layout.TableLayout;
import layout.TableLayoutConstants;

import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.tree.undo.*;

/**
 * Frame that enables tree-manipulation and access to node attributes.
 * Contains the menu, toolbar, and TreePanel (housed in a scrollpane)
 * @author  dmandel
 */
public class TreeFrame extends ToLJFrame implements ComponentListener, KeyListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3669788424866405606L;
	private static final int TOOLBAR_HEIGHT_PAD = 120;
    private TreePanel treePanel;
    private JLabel mesgLabel;
    
    private TreeToolbar tb;
    private Tree tree;
    private boolean toolbarEnabled;
    
    private AutoSaveThread autoSaveThread;


    /** Creates new form TreeFrame */
    public TreeFrame(Tree t, boolean isDownload) {
        super("TreeGrow Tree Window");
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(java.awt.Color.white);
        // Used to make sure TreePanel's static initializer gets called
        TreePanel.getTreePanel();
        tree = t;
        //initTree();
        
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    CloseDialog dialog = new CloseDialog();
                    dialog.saveConfirm();
                }
                public void windowDeactivated(WindowEvent e) {
                    tb.setAlternateButtons(false);
                }
           
	    });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        initComponents(isDownload);

        treePanel.revalidate();
        addComponentListener(this);
        setWaitCursors();
    }

    
    /** Called from within the constructor to initialize the form. It builds 
     * the tree from a tree string and node list, then places the menu, toolbar 
     * and treePanel (housed in scrollpane)
     */
    private void initComponents(boolean isDownload) {
       
        JPanel mesgPanel = new JPanel(new BorderLayout());
        mesgLabel = new JLabel("");
        mesgPanel.add(mesgLabel, BorderLayout.WEST);
        getContentPane().add(mesgPanel, BorderLayout.SOUTH);
        
        //tree = new Tree(treeString, nodeList);
        
        treePanel = new TreePanel(tree, this, isDownload);
        treePanel.constructLocations();
        //treePanel = new PreviewTreePanel(tree, this);
        //treePanel.constructLocations();
        JScrollPane scrollPane = Controller.getController().updateUnitIncrement(new JScrollPane(treePanel));        
        scrollPane.setSize(new Dimension(500, 500));
        scrollPane.setPreferredSize(new Dimension(500, 500));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setJMenuBar(new TreeMenuBar(this));      
        JPanel toolbarPanel = new JPanel();
        getContentPane().add(toolbarPanel, BorderLayout.WEST);
        
        toolbarPanel.setBackground(new Color(242,242,255)); //light blue
        toolbarPanel.setLayout(new BorderLayout());
        toolbarPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        JLabel tooltipLabel = Controller.getController().getLightbulbLabel("TREE_FRAME_TOOLTIP");
        double[][] size = new double[][] {{TableLayoutConstants.FILL}, {10, TableLayoutConstants.FILL}};
        JPanel labelPanel = new JPanel(new TableLayout(size));
        labelPanel.setOpaque(false);
        labelPanel.add(tooltipLabel, "0,1,c,t");
        toolbarPanel.add(labelPanel, BorderLayout.CENTER);
     
        tb = new TreeToolbar();
        toolbarPanel.add(tb, BorderLayout.NORTH);
        treePanel.requestFocus();
        
        pack();
    }
    
    /**
     * Sets message at in the bottom bar of the frame
     */
    public void setStatusMesg(String value) {
        mesgLabel.setText(value);
    }
    
    public Tree getTree() {
        return treePanel.getTree();
    }
       
    /**
     *Enable toolbar, generally after disabling it during some server 
     * interaction for which it was disabled
     */
    public void setToolbarEnabled() {
        toolbarEnabled = true;
        tb.enableAllButtons();
        getToolbar().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        getToolbar().returnToPreviousTool();
    }

    /**
     * Disable toolbar, generally during some server interaction     
     */    
    public void setToolbarDisabled() {
        toolbarEnabled = false;
        tb.disableAllButtons();
    }
    
    public boolean getToolbarEnabled() {
        return toolbarEnabled;
    }
    
    public TreeToolbar getToolbar() {
        return tb;
    }
    
    public UndoManager getUndoManager() {
        if (undoManager == null) {
            undoManager = new TreeUndoManager();
        }
        return undoManager;
    }    
    
    /**
     *Return to default cursors, generally after turning on the "wait" cursors
     * during some server interaction.
     */    
    public void setDefaultCursors() {
        setToolbarEnabled();
        ((TreeMenuBar) getJMenuBar()).setFileRelatedItemsEnabled(true);
        getToolbar().setDefaultSelectedTool();        
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     *Turn on the "wait" cursors, generally during some server interaction.
     */        
    public void setWaitCursors() {
        setToolbarDisabled();
        ((TreeMenuBar) getJMenuBar()).setFileRelatedItemsEnabled(false);
        getToolbar().setHandSelectedTool();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        setCursor(waitCursor);
        treePanel.setCursor(waitCursor);
        getToolbar().setCursor(waitCursor);
    }
    
    
    public void setAutoSaveThread(AutoSaveThread saveThread) {
        autoSaveThread = saveThread;
    }
    
    public AutoSaveThread getAutoSaveThread() {
        return autoSaveThread;
    }
    
    /** 
     * before closing frame, clean up. Especially turn off the autosave thread
     */
    public void dispose() {
        if (autoSaveThread != null) {
            autoSaveThread.stopAutoSaving();                   
        }        
        super.dispose();
        TreePanel.nullifyTreePanel();
        Controller controller = Controller.getController();
        controller.removeTreeEditor();
        // Clear some cached values
        controller.setUploadId(-1);
        controller.setEditorBatchId(null);
        controller.setFileChanged(false);
    }
      

    /**
     * Overridden to restrict the frame to a minimum height
     * (note: overriding getMinimumSize() doesn't seem to get the job done)
     */    
    public void componentResized(ComponentEvent e) {
        int width = getWidth();
        int height = getHeight();
        
        int minheight = tb.getHeight() + TOOLBAR_HEIGHT_PAD;
        
        if(height < minheight) {
            setSize(width, minheight);
            dispatchEvent(e);
        }
        treePanel.repaint();
        tb.repaint();
        repaint();
    }   
        
    public void componentShown(ComponentEvent e) {
    }
    
    public void componentHidden(ComponentEvent e) {
    }    
    
    public void componentMoved(ComponentEvent e) {
    }

    /** 
     *monitor for the pressing of modifier keys
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == getModifierKey() ) {
            getToolbar().setAlternateButtons(true);
        } else if (e.getKeyCode() == getMetaKey() ) {
            treePanel.setCursor(Cursor.getDefaultCursor());            
        }
    }
    
    /** 
     *monitor for the release of modifier keys
     */    
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == getModifierKey() ) {
            getToolbar().setAlternateButtons(false);
        } else if (e.getKeyCode() == getMetaKey() ) {
            treePanel.setCursor( TreeToolbar.getSelectedCursor());
        }
    }
    
    /** 
     *used internally to test for modifier key, handling multiple OS key layouts
     */
    private int getModifierKey() {
        if (Controller.getController().isMac() || Controller.getController().isWindows()) {
            return KeyEvent.VK_ALT;
        } else {
            return KeyEvent.VK_CONTROL;
        }       
    }

    /** 
     *used internally to test for meta key, handling multiple OS key layouts
     */    
    private int getMetaKey() {
        if (Controller.getController().isMac()) {
            return KeyEvent.VK_META;
        } else {
            return KeyEvent.VK_SHIFT;
        }       
    }
    
    
    public void keyTyped(KeyEvent e) {
    }
    
    
}



