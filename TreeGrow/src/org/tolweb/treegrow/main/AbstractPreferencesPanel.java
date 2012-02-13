package org.tolweb.treegrow.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.jdom.*;
import org.tolweb.treegrow.tree.*;

/**
 * The panel that allows the user to set their username, password and whether
 * or not they want to use custom cursors
 */
public abstract class AbstractPreferencesPanel extends JPanel implements ActionListener {
    protected JTextField userText;
    protected JPasswordField passwordField, newPasswordField;
    protected JCheckBox custCursorChkBox;
    protected JButton okButton;
    protected JButton cancelButton;
    protected PreferencesFrame prefFrame;
    
    public AbstractPreferencesPanel() {
        this(null);
    }

    public AbstractPreferencesPanel(PreferencesFrame pFrame) {
        prefFrame = pFrame;
        doPanelLayout();
    }
    
    protected abstract String getOkButtonName();
    protected abstract String getCancelButtonName();
    protected abstract void doPanelLayout();
    protected abstract String getMissingFieldsMessage();
    
    protected void bindUIToValues() {
        String user = Controller.getController().getUserName();
        if(user != null) {
            userText.setText(user);
        }
        passwordField.setText("");
        if (newPasswordField != null) {
            newPasswordField.setText("");
        }
        if (custCursorChkBox != null) {
            boolean useCustCursor = Controller.getController().getPreferenceManager().getUseCustCursors();
            custCursorChkBox.setSelected(useCustCursor);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Controller controller = Controller.getController();
        Object source = e.getSource();
        if(source == okButton) {
            if (passwordField.getText() != null && !passwordField.equals("")) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                boolean result = doServerSubmit();
                if (!result) {
                    setCursor(Cursor.getDefaultCursor());
                    return;
                } else {
                    // Show an ok message if it worked and if it's in the
                    // ToLManager
                    if (prefFrame == null) {
                        String PREFERENCES_SUCCESS = Controller.getController().getMsgString("PREFERENCES_SUCCESS");
                        JOptionPane.showMessageDialog(this, PREFERENCES_SUCCESS, "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                    bindUIToValues();
                }
                setCursor(Cursor.getDefaultCursor());
            } else {
                writePreferences(null);
            }
        } else if(source == cancelButton) {
            System.out.println("cancel pressed");
            if( TreePanel.getTreePanel() == null && prefFrame != null) {
                System.exit(0);
            } else if (prefFrame == null) {
                System.out.println("in here");
                bindUIToValues();
                repaint();
            }
        }
        if (prefFrame != null) {
            prefFrame.dispose();
        }
        if( TreePanel.getTreePanel() == null && controller.getManagerFrame() == null) {
            controller.openFileManager();
        }
    }
    
    /**
     * Will check to make sure that they are in fact a valid user
     * and will set their password if they do not have one set.
     */
    protected boolean doServerSubmit() {
        Controller controller = Controller.getController();
        String user = userText.getText().trim();
        String oldPassword = passwordField.getText().trim();
        if (oldPassword == null || oldPassword.equals("")) {
            String fieldsRequired = getMissingFieldsMessage();
            JOptionPane.showMessageDialog(this, fieldsRequired, "Password Required", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String newPassword = "";
        if (newPasswordField != null) {
            newPassword = newPasswordField.getText().trim();
        }

        String encryptedPassword;
        try {
            Element root = UserValidationChecker.sendValidationRequest(user, oldPassword, newPassword);
            if (root == null) {
                String SERVER_PROBLEMS = controller.getMsgString("SERVER_PROBLEMS");
                JOptionPane.showMessageDialog(this, SERVER_PROBLEMS, "Server Problems", JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                Element failureElement = root;
                if (failureElement != null) {
                    if (failureElement.getAttributeValue(XMLConstants.NOID) != null) {
                        String NO_ID = controller.getMsgString("NO_ID");
                        JOptionPane.showMessageDialog(this, NO_ID, "No user with that email", JOptionPane.ERROR_MESSAGE);
                        return false;
                    } else if (failureElement.getAttributeValue(XMLConstants.WRONG_PASSWORD) != null) {
                        String WRONG_PASSWORD = controller.getMsgString("WRONG_PASSWORD");
                        JOptionPane.showMessageDialog(this, WRONG_PASSWORD, "Wrong password", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                Element successElement = root.getChild(XMLConstants.SUCCESS);
                if (successElement == null) {
                    throw new RuntimeException();
                }
                encryptedPassword = successElement.getAttributeValue(XMLConstants.PASSWORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String SERVER_PROBLEMS = controller.getMsgString("SERVER_PROBLEMS");
            JOptionPane.showMessageDialog(this, SERVER_PROBLEMS, "Server Problems", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        writePreferences(encryptedPassword);
        // If it's in the ToLManager, then they could have changed users, so
        // the upload,preview,submit panel and the localfiles panel may need to
        // be updated
        if (controller.getManagerFrame() != null) {
            controller.getManagerFrame().getFilesPanel().initializeMetadataObjects();
            controller.getManagerFrame().getFilesPanel().updateFilesTable();
        }
        return true;
    }
    
    /**
     * Writes preferences to disk
     *
     * @param password The MD5 password hash that the server sent back down
     */
    private void writePreferences(String password) {
        PreferenceManager manager = Controller.getController().getPreferenceManager();
        String user = userText.getText().trim();
        boolean useCustCursors = true;
        if (custCursorChkBox != null) {
            useCustCursors = custCursorChkBox.isSelected();
        }
        manager.setUserName(user);
        System.out.println("just set username to: " + user);
        if (password != null) {
            manager.setPassword(password);
        }
        manager.setUseCustCursors(useCustCursors);
        manager.writePreferencesToDisk();
    }
}
