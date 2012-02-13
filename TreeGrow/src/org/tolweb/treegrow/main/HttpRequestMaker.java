/*
 * HttpRequestMaker.java
 *
 * Created on June 1, 2004, 8:30 PM
 */

package org.tolweb.treegrow.main;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.tolweb.base.http.BaseHttpRequestMaker;
import org.tolweb.treegrow.tree.Node;

/**
 *
 * @author  dmandel
 */
public class HttpRequestMaker extends BaseHttpRequestMaker {
    public static String getWorkingPreviewUrlForNode(Node node) {
        String previewUrl = Controller.getController().getPreviewUrl(node);
        previewUrl = addContributorParameters(previewUrl);
        return previewUrl;
    }
    
    public static Object[] makeHttpRequestAsStream(String url) {
        return (Object[]) makeHttpRequest(url, null, NewVersionDownloader.REALM, false);
    }
    public static byte[] makeHttpRequest(String url) {
        return (byte[]) makeHttpRequest(url, null, NewVersionDownloader.REALM, true);
    }
    public static void flushContributorCacheOnServer(int contributorId) {
    	makeCacheFlushRequest(RequestParameters.CONTRIBUTOR_ID, new Integer(contributorId));
    }
    public static void flushImageCacheOnServer(int imageId) {
    	makeCacheFlushRequest(RequestParameters.IMAGE_ID, new Integer(imageId));
    }
    public static void flushPageCacheOnServer(Long pageId) {
    	flushPageCacheOnServer(pageId.toString());
    }
    public static void flushPageCacheOnServer(String pageIdsString) {
    	makeCacheFlushRequest(RequestParameters.PAGE_ID, pageIdsString);
    }
    public static void flushAccPageCacheOnServer(Long pageId) {
    	makeCacheFlushRequest(RequestParameters.ACCESSORY_PAGE_ID, pageId);
    }    
    private static void makeCacheFlushRequest(String paramName, Object paramValue) {
    	Hashtable args = new Hashtable();
    	args.put(paramName, paramValue);
    	// TODO: for now stick to tap3 urls -- getTap4ExternalUrlString("FlushCache", args);    	
    	String url = getExternalFrontEndUrlString("FlushCache", args, false);
    	System.out.println("url is: " + url);
    	makeHttpRequest(url);
    }
    public static String getExternalFrontEndUrlString(String pageName, Map additionalArgs, boolean addPassword) {
        Controller controller = Controller.getController();
        String prefix = controller.getExternalFrontendServicePrefix();
        return getExternalUrlString(pageName, additionalArgs, prefix, addPassword);
    }
    
    public static String getExternalUrlString(String pageName, Map additionalArgs) {
        Controller controller = Controller.getController();
        String prefix = controller.getExternalServicePrefix();
        return getExternalUrlString(pageName, additionalArgs, prefix, true);
    }
    public static String getTap4ExternalUrlString(String pageName, Map additionalArgs) {
    	String prefix = Controller.getController().getTap4ExternalFrontendServicePrefix();
    	return getExternalUrlString(pageName, additionalArgs, prefix, false);
    }
    public static String getServerUtilsUrlString(String optype) {
        return getServerUtilsUrlString(optype, new Hashtable());
    }
    
    public static String getServerUtilsUrlString(String optype, Map additionalArgs) {
        if (additionalArgs == null) {
            additionalArgs = new Hashtable();
        }
        additionalArgs.put(RequestParameters.OPTYPE, optype);
        return getExternalUrlString("TreeGrowServerUtils", additionalArgs);
    }

    
    public static String getExternalFrontEndUrlString(String pageName) {
        return getExternalFrontEndUrlString(pageName, new HashMap());
    }
    public static String getExternalFrontEndUrlString(String pageName, Map additionalArgs) {
        return getExternalFrontEndUrlString(pageName, additionalArgs, true);
    } 
    public static String addContributorParameters(String originalString) {
        String userName = Controller.getController().getUserName();
        String pass = Controller.getController().getPassword();
        originalString = addParameter(originalString, RequestParameters.USER_ID, userName);
        originalString = addParameter(originalString, RequestParameters.PASSWORD, pass);
        return originalString;
    } 
    private static String getExternalUrlString(String pageName, Map additionalArgs, String prefix, boolean addUserIdPassword) {
    	String userId = addUserIdPassword ? Controller.getController().getUserName() : null;
    	String password = addUserIdPassword ? Controller.getController().getPassword() : null;
    	String url = getExternalUrlString(pageName, additionalArgs, prefix, userId, password);
    	return addParameter(url, RequestParameters.TREEGROW_VERSION, Controller.VERSION + "");
    }
}
