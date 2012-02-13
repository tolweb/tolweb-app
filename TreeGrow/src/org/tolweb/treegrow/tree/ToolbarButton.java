package org.tolweb.treegrow.tree;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

import org.tolweb.treegrow.main.undo.*;

/**
 * Buttons used on the Toolbar to the left of the Tree Editor.
 * Manages the selected/unselected images, hotkey images and related cursors.
 */
public class ToolbarButton extends JButton {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1203050503666950095L;
	private static final String ACTIVE_SUFFIX = ".act.gif";
    private static final String INACTIVE_SUFFIX = ".trsp.gif";
       
    protected Icon icon;
    private Icon selectedIcon;
    private Icon altIcon;
    private Icon altSelectedIcon;
    private String desc;
    private String altDesc;
    
    private Cursor cursor;
    private Cursor altCursor;
    
    private boolean isAlt = false;
    
    /**
     * Creates a new instance of ToolbarButton.
     *
     * @param desc Tooltip for the standard button 
     * @param altDesc Tooltip for the button when hotkey is down
     * @param imgName Button image location for the standard button
     * @param altImgName Button image location for the button when 
     * hotkey is down
     * @param cursor Cusror image location for the standard button
     * @param altCursor Cursor image location for the button when 
     * hotkey is down
     */
    public ToolbarButton(String desc, String altDesc, String imgName, String altImgName, Cursor cursor, Cursor altCursor) {
        URL iconPath = ClassLoader.getSystemResource(imgName + INACTIVE_SUFFIX);
        icon = new ImageIcon(iconPath);
        iconPath = ClassLoader.getSystemResource(altImgName + INACTIVE_SUFFIX);
        altIcon = new ImageIcon(iconPath);
        buildSelectedIcons(imgName, altImgName);
        
        this.desc = desc;
        this.altDesc = altDesc;
        
        this.cursor = cursor;
        this.altCursor = altCursor;

        setAction(new TreeAction (desc, icon));
        setText("");
        setToolTipText(desc);
        setMargin(new Insets(0, 0, 0, 0));
        setBackground(Color.white);
    }
    
    public ToolbarButton(String desc, String imgName, Cursor cursor) {
        this(desc, desc, imgName, imgName, cursor, cursor);
    }
    
    protected void buildSelectedIcons(String imgName, String altImgName) {
        selectedIcon = new ImageIcon(ClassLoader.getSystemResource(imgName + ACTIVE_SUFFIX));
        altSelectedIcon = new ImageIcon(ClassLoader.getSystemResource(altImgName + ACTIVE_SUFFIX));
        setSelectedIcon(selectedIcon);
    }
    
    /**
     * A hotkey has been pressed/released. Change the button image to 
     * reflect that change.
     */
    public void setIsAlt(boolean value) {
        isAlt = value;
        if (isAlt) {
            setIcon(altIcon);
            setSelectedIcon(altSelectedIcon);
            setToolTipText(altDesc);
            getAction().putValue(Action.NAME, altDesc);
            setText("");
        } else {
            setIcon(icon);
            setSelectedIcon(selectedIcon);
            setToolTipText(desc);
            getAction().putValue(Action.NAME, desc);
            setText("");
        }
        
    }
    
    /** 
     * Returns the cursor associated with the given button, depending on 
     * hotkey state.
     */
    public Cursor getButtonCursor() {
        if (isAlt) {
            return altCursor;    
        } else {
            return cursor;
        }
    }
}


/**
 *Specialized button for undo action
 */
class UndoToolbarButton extends ToolbarButton {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6648941101326057497L;
	public UndoToolbarButton(String desc, String imgName, Cursor cursor) {
        super(desc, imgName, cursor);
        UndoAction action = TreePanel.getTreePanel().getUndoAction();
        setAction(action);
        setIcon(icon);
        setToolTipText(desc);
        action.setButton(this);
    }
    
    protected void buildSelectedIcons(String imgName, String altImgName) {}
    public void toggleAlt() {}
}

/**
 *Specialized button for redo action
 */
class RedoToolbarButton extends ToolbarButton {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4201729215744297175L;
	public RedoToolbarButton(String desc, String imgName, Cursor cursor) {
        super(desc, imgName, cursor);
        RedoAction action = TreePanel.getTreePanel().getRedoAction();
        setAction(action);
        setIcon(icon);
        setToolTipText(desc);
        action.setButton(this);
    }
    
    protected void buildSelectedIcons(String imgName, String altImgName) {}
    public void toggleAlt() {}
}
