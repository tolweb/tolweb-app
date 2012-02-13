/*
 * TableSelectableEditorPanel.java
 *
 * Created on July 23, 2003, 1:56 PM
 */

package org.tolweb.treegrow.page;

import javax.swing.JLabel;
import javax.swing.JPanel;

import layout.TableLayout;
import layout.TableLayoutConstants;

import org.tolweb.treegrow.main.Controller;
import org.tolweb.treegrow.main.RefreshButton;

/**
 * Panel subclass used when selecting a table item so that some other area of
 * the panel refreshes its values.
 */
public class TableSelectableEditorPanel extends AbstractPageEditorPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5644901147155848339L;
	protected ChangedFromServerProvider selectedObject, oldSelectedObject;
    protected boolean programmaticallyEditing;
    
    /** Creates a new instance of TableSelectableEditorPanel */
    public TableSelectableEditorPanel(PageFrame frame) {
        super(frame);
    }
    
    public boolean setSelectedObject(ChangedFromServerProvider value) {
        if (value != selectedObject) {
            oldSelectedObject = selectedObject;
            selectedObject = value;
            return true;
        }
        return false;
    }
    
    public ChangedFromServerProvider getSelectedObject() {
        return selectedObject;
    }   
    
    protected JPanel constructLabelPanel(String labelName, String helpMessageName, RefreshButton refreshButton) {
        double[][] size = new double[][] {{10, 30, TableLayoutConstants.FILL, 100, 10}, {5, 25, 5}};
        JPanel labelPanel = new JPanel(); 
        labelPanel.setLayout(new TableLayout(size));
        
        labelPanel.add(new JLabel(labelName), "2,1,c");
        JLabel tooltipLabel = Controller.getController().getLightbulbLabel(helpMessageName);
        labelPanel.add(tooltipLabel, "1,1,l");        
        labelPanel.add(refreshButton, "3,1,r");
        return labelPanel;
    }
}
