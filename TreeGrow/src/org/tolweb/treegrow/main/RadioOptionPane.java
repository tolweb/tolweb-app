/*
 * RadioButtonDialog.java
 *
 * Created on July 1, 2003, 10:03 AM
 */
package org.tolweb.treegrow.main;

import javax.swing.*;

/**
 *
 * @author  twheeler
 */
public class RadioOptionPane {   
    /** Shows a dialog box full of radio buttons.
     *
     * @param frame The JFrame this dialog is associated with
     * @param title The title to be given to the dialog
     * @param message The message presented to the user
     * @param options The array of strings that will be used to give the user choices
     * @param selectedIndex The index into the options array of the radio button that should be initially selected 
     *
     * @return The index of the selection in the options array if something was selected, -1 otherwise.
     */
    public static int showRadioButtonDialog(JFrame frame, String title, String message, String[] options, int selectedIndex) {
        Object[] displayedOptions = new Object[options.length + 2];
        displayedOptions[0] = message;
        displayedOptions[1] = " ";
        ButtonGroup buttons = new ButtonGroup();
        for (int i = 0; i < options.length; i++) {
            JRadioButton button = new JRadioButton(options[i]);
            button.setActionCommand(options[i]);
            displayedOptions[i + 2] = button;
            buttons.add(button);
        }
        ((JRadioButton) displayedOptions[selectedIndex + 2]).setSelected(true);
        String[] okCancel = new String[] { "OK", "Cancel" };
        JOptionPane optionPane = new JOptionPane(displayedOptions, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, okCancel, null);
        JDialog dialog = optionPane.createDialog(frame, title);

        dialog.show();
        dialog.dispose();
        if (optionPane.getValue() == null || optionPane.getValue().equals("Cancel")) {
            return -1;
        } else {
            ButtonModel selectedButton = buttons.getSelection();
            for (int i = 0; i < buttons.getButtonCount(); i++) {
                if (selectedButton.getActionCommand().equals(options[i])) {
                    return i;
                }
            }
            return -1;
        }
    }
    
    /** Shows a dialog box full of radio buttons.
     *
     * @param frame The JFrame this dialog is associated with
     * @param title The title to be given to the dialog
     * @param message The message presented to the user
     * @param options The array of strings that will be used to give the user choices
     *
     * @return The index of the selection in the options array if something was selected, -1 otherwise.
     */
    public static int showRadioButtonDialog(JFrame frame, String title, String message, String[] options) {
        return showRadioButtonDialog(frame, title, message, options, 0);
    }
}
