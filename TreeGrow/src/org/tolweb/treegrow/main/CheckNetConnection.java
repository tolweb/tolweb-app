package org.tolweb.treegrow.main;

/**
 * Class that checks to see if a net connection is available
 */
public class CheckNetConnection {
    public int isConnected() {
        if (doRequest()) {
            return 1;
        } else {
            return -1;
        }
    }
    
    public static boolean hasConnection() {
        return doRequest();
    }
    
    private static boolean doRequest() {
        // Download a small file from the server to make sure it's up
        byte[] bytes = HttpRequestMaker.makeHttpRequest(Controller.getController().getPageServicePrefix() + "Home");
        return bytes != null;
    }
}
