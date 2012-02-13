/*
 * NewVersionDownloadFrame.java
 *
 * Created on September 9, 2003, 10:02 AM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import javax.swing.*;

/**
 * Frame subclass that shows download of new version progress
 * 
 */
public class NewVersionDownloadFrame extends ToLJFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7812951947435801806L;
	JLabel statusLabel = new JLabel();
    int bytesBase;
    int bytesSoFar = 0;
    private String labelText;
    private Thread updateThread;
    
    
    /** Creates a new instance of NewVersionDownloadFrame */
    public NewVersionDownloadFrame(int bytes) {
        super("Downloading New Version");
        bytesBase = bytes/100;
        
        labelText = Controller.getController().getMsgString("DOWNLOADING_NEW_VERSION");
        statusLabel.setText(labelText + "                         ");
        statusLabel.setPreferredSize(new Dimension(statusLabel.getPreferredSize().width, 75));        

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(statusLabel, BorderLayout.CENTER);

        setLocation(400,400);
        setSize(550, 150);
        pack();
        show();
    }
    
    /**
     * Updates the progress with the number of bytes just downloaded
     *
     * @param bytes The number of bytes just downloaded
     */
    public void updateStatus (int bytes) {
        bytesSoFar+=bytes;
        int pct = bytesSoFar/bytesBase;
        //statusLabel.setText(statusPrefix + pct + "%");
    }
    
    public void dispose() {
        if (updateThread != null) {
            updateThread.stop();
        }
    }
    
    public void startSpinningUI() {
        updateThread = new Thread() {
            private int count = 0;
            private boolean incrementing = true;
            private String status0 = ".   .   .            ";
            private String status1 = "    .   .   .        ";            
            private String status2 = "        .   .   .    ";
            private String status3 = "            .   .   .";
            public void run() {
                while (true) {
                    switch (count) {
                        case 0: statusLabel.setText(labelText + status0); break;
                        case 1: statusLabel.setText(labelText + status1); break;
                        case 2: statusLabel.setText(labelText + status2); break;
                        case 3: statusLabel.setText(labelText + status3); break;                        
                    }
                    if (count > 0 && count < 3 && incrementing) {
                        count++;
                    } else if (count > 0 && count < 3 && !incrementing) {
                        count--;
                    } else if (count == 0) {
                        count++;
                        incrementing = true;
                    } else if (count == 3) {
                        count--;
                        incrementing = false;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {}
                }
            }
        };
        updateThread.start();
    }
}
