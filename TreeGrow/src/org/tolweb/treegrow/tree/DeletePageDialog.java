package org.tolweb.treegrow.tree;

import java.awt.Frame;

import org.tolweb.treegrow.main.Controller;

public class DeletePageDialog extends DeleteNodeDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -78811790251610292L;

	public DeletePageDialog(Frame frame) {
        super(frame);
    }
    protected String getMessage() {
        Controller controller = Controller.getController();
        return controller.getMsgString("DELETE_PAGES_HARMFUL");
    }
    
    protected void toggleWarningMessage(boolean value) {
        Controller.getController().setWarnAboutPageDeletion(!value);        
    }
    
    protected String getDialogTitle() {
        return "Deleting Pages is Potentially Harmful";
    }    
}
