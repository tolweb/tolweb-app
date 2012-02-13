/*
 * ChangeMonitoringTextField.java
 *
 * Created on July 20, 2003, 11:05 AM
 */

package org.tolweb.treegrow.main;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Abstract JTextField subclass used to check whether actual editing of a 
 * document occurred between focusGained and focusLost
 */
public class ChangeMonitoringTextField extends JTextField {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5870834803162048061L;
	protected boolean somethingChanged = false;
    protected String originalString;
    
    /** Creates a new instance of ChangeMonitoringTextField */
    public ChangeMonitoringTextField() {
        super();
        addListeners();
    }
    
    public ChangeMonitoringTextField(String val, int initialWidth) {
        super(val, initialWidth);
        addListeners();
    }
    
    public ChangeMonitoringTextField(int initialWidth) {
        super(initialWidth);
        addListeners();
    }
    
    public boolean getSomethingChanged() {
        return somethingChanged;
    }
    
    private void addListeners() {
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                
                if ( Controller.getController().getMenuCuttingPasting()) {
                    // if the text has just been changed by a cut or paste, indicate that it's done
                    Controller.getController().setMenuCuttingPasting(false);
                } else {
                    // only update if the text hasn't just been changed by a cut or paste
                    somethingChanged = false;
                    originalString = getText();
                }
            }
        });
        // If any editing of the document occurs, set the changed var to true
        getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                somethingChanged = !getText().equals(originalString);
            }
            public void insertUpdate(DocumentEvent e) {
                somethingChanged = !getText().equals(originalString);
            }
            public void removeUpdate(DocumentEvent e) {
                somethingChanged = !getText().equals(originalString);
            }
        });    
    }
    
}
