/*
 * PreferencesPanel.java
 *
 * Created on November 19, 2003, 9:11 AM
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
public class PreferencesPanel extends AbstractPreferencesPanel {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4200095730138818928L;

	/** Creates a new instance of PreferencesPanel */
    public PreferencesPanel() {
    }
    
    public PreferencesPanel(PreferencesFrame pFrame) {
        super(pFrame);
    }    
    
    protected void doPanelLayout() {
        JPanel choicesPanel = new JPanel();
        double[][] size = new double[][] {{10, 120, 10, TableLayoutConstants.FILL, 10}, {10, 20, 10, 20, 10}};
        choicesPanel.setLayout(new TableLayout(size));

        JLabel userLabel = new JLabel("Username (Email) ");
        choicesPanel.add(userLabel, "1,1,r");

        userText = new JTextField(20);
        choicesPanel.add(userText, "3,1");
        
        JLabel passwordLabel = new JLabel("Password ");
        choicesPanel.add(passwordLabel, "1,3,r");
        
        passwordField = new JPasswordField(20);
        choicesPanel.add(passwordField, "3,3");
        
        /*JLabel newPasswordLabel = new JLabel("New Password ");
        centeringPanel = new JPanel();
        centeringPanel.setLayout(new BorderLayout());
        centeringPanel.add(newPasswordLabel, BorderLayout.EAST);
        choicesPanel.add(centeringPanel);
        
        newPasswordField = new JPasswordField(10);
        choicesPanel.add(newPasswordField);

        JLabel custCursorLabel = new JLabel("Use custom cursors  ");
        centeringPanel = new JPanel();
        centeringPanel.setLayout(new BorderLayout());
        centeringPanel.add(custCursorLabel, BorderLayout.EAST);
        choicesPanel.add(centeringPanel);

        custCursorChkBox = new JCheckBox();
        choicesPanel.add(custCursorChkBox);*/


        JPanel buttonPanel = new JPanel();                        
        buttonPanel.setLayout(new FlowLayout());

        okButton = new JButton(getOkButtonName());
        okButton.addActionListener(this);

        cancelButton = new JButton(getCancelButtonName());
        cancelButton.addActionListener(this);
        
        if (Controller.getController().isMac()) {
            buttonPanel.add(cancelButton);
            buttonPanel.add(okButton);
        } else {
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
        }
        
        bindUIToValues();
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(choicesPanel,BorderLayout.CENTER);
        centerPanel.add(buttonPanel,BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    protected String getCancelButtonName() {
        return "Cancel";
    }
    
    protected String getOkButtonName() {
        return "Ok";
    }
    
    protected String getMissingFieldsMessage() {
        return Controller.getController().getMsgString("PASSWORD_REQUIRED");        
    }
    
}
