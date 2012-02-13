package org.tolweb.treegrow.main;

/**
 * Thread class that saves the currently open file
 */
public class FileSave {       
	public void run() {
            Controller controller = Controller.getController();
            try {
                String ST_SAVING_FILE = controller.getMsgString("ST_SAVING_FILE");
                controller.setStatusMessage(ST_SAVING_FILE);

                FileXMLWriter writer = new FileXMLWriter();
                writer.writeXML();

                String ST_SAVED_FILE = controller.getMsgString("ST_SAVED_FILE");
                controller.setStatusMessage(ST_SAVED_FILE);
            } catch(Exception error) {
            }
	}
}
