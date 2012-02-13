/*
 * ConnectionMonitoringStreamHandler.java
 *
 * Created on October 10, 2003, 11:16 AM
 */

package org.tolweb.treegrow.main;

/**
 * Class that is used to make sure that connections don't die during uploading
 * or downloading.  
 */
public abstract class ConnectionMonitoringStreamHandler {
    protected int bytesProcessed = 0;
    protected boolean doAdditionalWait;
    protected boolean done = false;
    protected boolean error = false;
    protected Thread interactionThread;
    
    /** Creates a new instance of ConnectionMonitoringStreamHandler */
    public ConnectionMonitoringStreamHandler(boolean additionalWait) {
        doAdditionalWait = additionalWait;
    }
    
    public ConnectionMonitoringStreamHandler() {}
    
    /**
     * Once the thread has been started, this method makes sure that there is
     * some progress.  If it finds that the connection has dropped midstream,
     * it kills the offending thread
     */
    protected void monitorStreamProgress() {
        int threadCount = 0;
        int currentbytesProcessed = bytesProcessed;
        int zeroCounter = 0;
        // Spin until the downloading is done.  If there is
        // no activity for 10 seconds, then kill the downloading thread and
        // pop an error message -- Spinning because calling read() blocks so
        // that if there is ever an error during read, the application hangs
        while (!done) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {e.printStackTrace();System.out.println("getting interrupted?");}
            if (threadCount == 30 && !done) {
                if (bytesProcessed == currentbytesProcessed) {
                    if (bytesProcessed == 0 && zeroCounter < 6 && doAdditionalWait) {
                        zeroCounter++;
                    } else {
                        if (!done) {
                            error = true;
                            interactionThread.stop();
                            break;
                        }
                    }
                }
                currentbytesProcessed = bytesProcessed;
                threadCount = 0;
            }
            threadCount++;
        }    
    }
    
    /**
     * Constructs the thread that will do the actual stream working
     */
    protected abstract void constructInteractionThread();
}
