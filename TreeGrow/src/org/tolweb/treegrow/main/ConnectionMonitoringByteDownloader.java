/*
 * LostConnectionByteDownloader.java
 *
 * Created on October 9, 2003, 4:06 PM
 */

package org.tolweb.treegrow.main;

import java.io.*;
import java.net.*;

/**
 * Class that attempts to download data from some URL.  If there is no activity
 * for 5 seconds, then it closes the connection and sets an error flag
 */
public class ConnectionMonitoringByteDownloader extends ConnectionMonitoringStreamHandler {
    private URLConnection connection;
    private DataInputStream stream;
    private int MAX_DOWNLOAD_SIZE = 512000;
    private NewVersionDownloadFrame statusFrame;
    private byte[] buf;
    private boolean downloadError = false;
    
    /** Creates a new instance of LostConnectionByteDownloader 
     *
     *  @param conn The URLConnection to read data from
     *  @param f The NewVersionDownloadFrame to update download progress on
     */
    public ConnectionMonitoringByteDownloader(URLConnection conn, NewVersionDownloadFrame f) {
        connection = conn;
        statusFrame = f;
    }

    /** Creates a new instance of LostConnectionByteDownloader 
     *
     *  @param conn The URLConnection to read data from
     */
    public ConnectionMonitoringByteDownloader(URLConnection conn) {
        connection = conn;
    }

    /** Creates a new instance of LostConnectionByteDownloader 
     *
     *  @param url The URL to read data from
     *  @param additionalWaitAtFirst Wait longer at the beginning of the 
     *         download in case the server takes a while to respond
     */    
    public ConnectionMonitoringByteDownloader(URL url, boolean additionalWaitAtFirst) {
        super(additionalWaitAtFirst);
        try {
            connection = url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns whether there was an error during the download process -- this
     * is a separate sort of error from the connection dying during download
     *
     * @return Whether or not there was an error during download
     */
    public boolean checkError() {
        return downloadError;
    }
    
    /**
     * Starts the download and continues until the connection has died or 
     * it is finished
     *
     * @return The byte array read from the connection
     */
    public byte[] doDownload() {
        constructInteractionThread();
        interactionThread.start();
        monitorStreamProgress();
        if (!error) {
            // Copy the bytes to a buffer that is the exact size of the bytes
            // already read
            byte[] actualSizeArray = new byte[bytesProcessed];
            System.arraycopy(buf, 0, actualSizeArray, 0, bytesProcessed);
            return actualSizeArray;
        } else {
            return null;
        }
    }
    
    /**
     * Constructs the thread responsible for the download
     */
    protected void constructInteractionThread() {
        interactionThread = new Thread() {
            public void run() {
                try {
                    if (stream == null) {
                        stream = new DataInputStream(connection.getInputStream());
                    }
                    buf = new byte[MAX_DOWNLOAD_SIZE];
                    bytesProcessed = 0;
                    int MAX_BYTES = 2048;
                    byte[] tempBytes = new byte[MAX_BYTES];
                    int tempbytesProcessed = 0;
                    while ((tempbytesProcessed = stream.read(tempBytes, 0, MAX_BYTES)) > 0) {
                        if (statusFrame != null) {
                            statusFrame.updateStatus(tempbytesProcessed);
                        }
                        // Before we do the copy, make sure that there is enough room
                        // in the current buffer
                        if (tempbytesProcessed + bytesProcessed > buf.length) {
                            byte[] newBytes = new byte[buf.length * 2];
                            System.arraycopy(buf, 0, newBytes, 0, bytesProcessed);
                            buf = null;
                            buf = newBytes;
                        }
                        System.arraycopy(tempBytes, 0, buf, bytesProcessed, tempbytesProcessed);
                        bytesProcessed += tempbytesProcessed;
                    }
                    done = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    closeStreams();
                    downloadError = true;
                    done = true;
                }
                closeStreams();
            }

            private void closeStreams() {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (Exception e) {
                }
            }
        };        
    }
    
}
