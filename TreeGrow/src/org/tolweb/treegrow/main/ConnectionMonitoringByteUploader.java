/*
 * LostConnectionByteUploader.java
 *
 * Created on October 10, 2003, 11:28 AM
 */

package org.tolweb.treegrow.main;

import java.io.*;

/**
 * Class that uploads some data to a URL.  If the connection dies midstream, 
 * then the stream gets stopped and uploading is cancelled
 */
public class ConnectionMonitoringByteUploader extends ConnectionMonitoringStreamHandler {
    private byte[] bytes;
    private OutputStream uploadStream;
    
    /** 
     * Creates an uploader that writes the specified string to the specified
     * stream
     *
     * @param string The string to write 
     * @param stream The stream to write to
     */
    public ConnectionMonitoringByteUploader(String string, OutputStream stream) {
        bytes = string.getBytes();
        uploadStream = stream;
    }
    
    /**
     * Kicks off the thread and monitors it
     */
    public void doUpload() {
        constructInteractionThread();
        interactionThread.start();
        monitorStreamProgress();
    }
    
    /**
     * Checks to see that upload happened successfully.  Returns false unless
     * the stream was interrupted during upload
     *
     * @return Whether the stream had an error during upload
     */
    public boolean checkError() {
        return error;
    }
    
    /**
     * Constructs the thread that will do the actual data uploading
     */
    protected void constructInteractionThread() {
        interactionThread = new Thread() {
            public void run() {
                for (int i = 0; i < bytes.length; i++) {
                    try {
                        uploadStream.write(bytes[i]);
                        bytesProcessed++;
                    } catch (Exception e) {
                        // If there was a problem, try again.  If it spins for
                        // too long then the monitoring will catch it
                        i--;
                    }
                }
                // Set the flag so the superclass knows to stop monitoring
                done = true;
            }
        };
    }
    
}
