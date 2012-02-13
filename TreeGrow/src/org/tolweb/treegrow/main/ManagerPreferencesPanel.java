/*
 * ManagerPreferencesPanel.java
 *
 * Created on November 19, 2003, 9:16 AM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import javax.swing.*;
import layout.TableLayout;
import layout.TableLayoutConstants;

/**
 *
 * @author  dmandel
 */
public class ManagerPreferencesPanel extends AbstractPreferencesPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7790480053124860317L;
	private JPasswordField confirmNewPasswordField;
            
    
    /** Creates a new instance of ManagerPreferencesPanel */
    public ManagerPreferencesPanel() {
    }
    
    protected void doPanelLayout() {
        setLayout(new TableLayout(new double[][] { {TableLayoutConstants.FILL}, {TableLayoutConstants.FILL, 30, 150}}));
        JPanel choicesPanel = new JPanel();
        choicesPanel.setLayout(new TableLayout(new double[][] { {75, 150, 5, TableLayoutConstants.FILL, 100}, {125, 20, 5, 20, 5, 20, 5, 20, 5, 20} }));

        JLabel userLabel = new JLabel("Username (Email) ");
        choicesPanel.add(userLabel, "1, 1, r");

        userText = new JTextField(30);
        choicesPanel.add(userText, "3, 1, l");
        
        JLabel passwordLabel = new JLabel("Current Password");
        choicesPanel.add(passwordLabel, "1, 3, r");
        
        passwordField = new JPasswordField(30);
        choicesPanel.add(passwordField, "3, 3, l");
        
        JLabel newPasswordLabel = new JLabel("New Password");
        choicesPanel.add(newPasswordLabel, "1, 5, r");
        
        newPasswordField = new JPasswordField(30);
        choicesPanel.add(newPasswordField, "3, 5, l");

        JLabel confirmNewPasswordLabel = new JLabel("Confirm New Password");
        choicesPanel.add(confirmNewPasswordLabel, "1, 7, r");
        
        confirmNewPasswordField = new JPasswordField(30);
        choicesPanel.add(confirmNewPasswordField, "3, 7, l");        
        
        JLabel custCursorLabel = new JLabel("Use custom cursors");
        choicesPanel.add(custCursorLabel, "1, 9, r");

        custCursorChkBox = new JCheckBox();
        choicesPanel.add(custCursorChkBox, "3, 9, l");

        JPanel buttonPanel = new JPanel();                        
        buttonPanel.setLayout(new FlowLayout());

        okButton = new JButton(getOkButtonName());
        okButton.addActionListener(this);

        cancelButton = new JButton(getCancelButtonName());
        cancelButton.addActionListener(this);

        if (!Controller.getController().isMac()) {
            buttonPanel.add(okButton);        
            buttonPanel.add(cancelButton);
        } else {
            buttonPanel.add(cancelButton);            
            buttonPanel.add(okButton);              
        }
        
        bindUIToValues();
        add(choicesPanel, "0,0");
        add(buttonPanel, "0,1");        
    }
    
    protected void bindUIToValues() {
        confirmNewPasswordField.setText("");
        super.bindUIToValues();
    }
    
    protected boolean doServerSubmit() {
        if ((confirmNewPasswordField.getText() == null && newPasswordField.getText() == null) ||
        ((confirmNewPasswordField.getText() != null && newPasswordField.getText() != null) && 
        confirmNewPasswordField.getText().equals(newPasswordField.getText()))) {
            return super.doServerSubmit();
        } else {
            String CHANGE_PASSWORD_NO_MATCH = Controller.getController().getMsgString("CHANGE_PASSWORD_NO_MATCH");
            JOptionPane.showMessageDialog(Controller.getController().getManagerFrame(), CHANGE_PASSWORD_NO_MATCH, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    protected String getCancelButtonName() {
        return "Reset";
    }
    
    protected String getOkButtonName() {
        return "Submit";
    }    
    
    protected String getMissingFieldsMessage() {
        return Controller.getController().getMsgString("PASSWORD_REQUIRED_MANAGER");
    }
    
}
