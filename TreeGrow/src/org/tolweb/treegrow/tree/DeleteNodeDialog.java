package org.tolweb.treegrow.tree;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import layout.TableLayout;
import layout.TableLayoutConstants;

import org.tolweb.treegrow.main.Controller;
import org.tolweb.treegrow.main.HtmlLabel;

public class DeleteNodeDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -620206493801519952L;
	private boolean continueWithDelete;
    
    /** Creates the reusable dialog. */
    public DeleteNodeDialog(Frame aFrame) {
        super(aFrame, true);
        setTitle(getDialogTitle());
        double[][] size = new double[][] {{10, TableLayoutConstants.FILL, 10}, {10, TableLayoutConstants.FILL, 10, 30, 10, 30, 10}};
        JPanel holderPanel = new JPanel();        
        holderPanel.setLayout(new TableLayout(size));
        String DELETE_NODES_HARMFUL = getMessage();
        holderPanel.add(new HtmlLabel(DELETE_NODES_HARMFUL), "1,1,c");
        final JCheckBox dontShowAgainCheckbox = new JCheckBox("Dont Show This Message Again");
        dontShowAgainCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean selected = dontShowAgainCheckbox.isSelected();
                toggleWarningMessage(selected);
            }
        });

        holderPanel.add(dontShowAgainCheckbox, "1,3,l");
        JButton closeButton = new JButton("OK");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dismissDialog(true);
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dismissDialog(false);
            }
            
        });
        JPanel buttonPanel = new JPanel();
        if (Controller.getController().isMac()) {
            buttonPanel.add(cancelButton);
            buttonPanel.add(closeButton);
        } else {
            buttonPanel.add(closeButton);            
            buttonPanel.add(cancelButton);            
        }
        holderPanel.add(buttonPanel, "1,5,c");
        getContentPane().add(holderPanel);
        pack();
    }
    
    private void dismissDialog(boolean continueWithDelete) {
        setVisible(false);
        dispose();
        setContinueWithDelete(continueWithDelete);        
    }

    /**
     * @return Returns the continueWithDelete.
     */
    public boolean getContinueWithDelete() {
        return continueWithDelete;
    }

    /**
     * @param continueWithDelete The continueWithDelete to set.
     */
    public void setContinueWithDelete(boolean continueWithDelete) {
        this.continueWithDelete = continueWithDelete;
    }
    
    protected String getMessage() {
        Controller controller = Controller.getController();
        return controller.getMsgString("DELETE_NODES_HARMFUL");
    }
    
    protected void toggleWarningMessage(boolean value) {
        Controller.getController().setWarnAboutNodeDeletion(!value);        
    }
    
    protected String getDialogTitle() {
        return "Deleting Nodes is Potentially Harmful";
    }
}
