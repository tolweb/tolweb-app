/*
 * NodeNameSearcher.java
 *
 * Created on February 6, 2004, 9:39 AM
 */

package org.tolweb.treegrow.main;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;

/**
 *
 * @author  dmandel
 */
public class NodeNameSearcher {
    
    /** Creates a new instance of NodeNameSearcher */
    public NodeNameSearcher() {
    }
    

    /**
     * Calls the search script on the server using the passed-in string
     * as the search string 
     *
     * @param s The String to search
     * @param count Either 0 or 1, depending on whether this is an 
     *        approximate match.  If an exact match doesn't return anything,
     *        the script gets called again with wildcards
     * @return -1 If there was an error, 1 otherwise
     */
    public Object searchDatabase(String s, int count) throws IOException {
        Controller controller = Controller.getController();
        FileManager fileManager = controller.getFileManager();
        int sucess = 1;
        //s = URLEncoder.encode(s);
        s = s.replace(' ', '_');
        System.out.println("replaced spaces... " + s);
        int exactInt = count == 0 ? 1 : 0;
        Hashtable args = new Hashtable();
        args.put(RequestParameters.GROUP, s);
        args.put(RequestParameters.EXACT, "" + exactInt);
        String urlString = HttpRequestMaker.getExternalUrlString("NodeSearch", args);

        Element root = null;
        SAXBuilder builder = null;
        Document doc = null;

        try {
            builder = new SAXBuilder();
            doc = builder.build(new URL(urlString));
            root = doc.getRootElement();
            /*
            XMLOutputter serializer = new XMLOutputter();
            String resultStr = serializer.outputString(doc);
            System.out.println("search result = " +resultStr);                
            */
        } catch(Exception e) {
            //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            System.out.println("error  searching : " + e.getMessage());
            return null;
        }
        
        if (StringUtils.notEmpty(root.getAttributeValue(XMLConstants.NEW_VERSION))) {
            // name searching has been disallowed, so return this element as an error
            return root;
        }

        Iterator it = root.getChildren(XMLConstants.MATCH).iterator();
        if (!it.hasNext()) {
            if(count == 0) {
                // If this is the first time and there is no match found,
                // then try again using an approximate match
                return searchDatabase(s, 1);
            }
            return root;
        }

        Stack stack = new Stack();
        while(it.hasNext()) {
            NodeDetails details = getNewDetails((Element) it.next());
            if (StringUtils.notEmpty(details.getNodeName())) {
                stack.push(details);
            }
        }
        
        return stack;
    }
    
    /**
     * Converts a MATCH element into a NodeDetails object.
     *
     * @param The MATCH Element to convert
     * @return A new NodeDetails object corresponding to the MATCH
     */
    private NodeDetails getNewDetails(Element match) {
        Controller controller = Controller.getController();            
        FileManager fileManager = controller.getFileManager();
        NodeDetails tempDetails = new NodeDetails();
        //String tempString = match.getChild(XMLConstants.NAME).getText();
        String tempString = match.getAttributeValue(XMLConstants.NAME);
        tempDetails.setNodeName(tempString.trim());

        //tempString = match.getChild(XMLConstants.ID).getText();
        tempString = match.getAttributeValue(XMLConstants.ID);
        tempDetails.setNodeId(tempString.trim());

        tempString = match.getAttributeValue(XMLConstants.HASPAGE);
        if (tempString != null && tempString.equals(XMLConstants.ZERO)) {
            // Look for the parent page's node id and name since that is 
            // what will get downloaded
            tempString = match.getAttributeValue(XMLConstants.PARENTPAGE_NAME);
            tempDetails.setParentPageName(tempString);

            tempString = match.getAttributeValue(XMLConstants.PARENTPAGE_NODE_ID);
            tempDetails.setParentPageNodeId(tempString);

        }        
        
        Element lock = match.getChild(XMLConstants.LOCK_INFO);
        if(lock != null) {

            tempString = lock.getAttributeValue(XMLConstants.DOWNLOAD_ID);
            if (tempString != null) {
                tempDetails.setDownloadId(new Integer(tempString).intValue());
            }

            tempString = lock.getAttributeValue(XMLConstants.ROOT);
            if(tempString != null) {
                tempDetails.setRoot(tempString.trim());
            }

            tempString = lock.getAttributeValue(XMLConstants.DATE_TIME);
            if(tempString != null) {
                tempDetails.setTimeStamp(tempString.trim());
            }

            tempString = lock.getAttributeValue(XMLConstants.USER);
            if(tempString != null) {
                tempDetails.setUser(tempString.trim());
            }

            tempString = lock.getAttributeValue(XMLConstants.TYPE);
            if(tempString != null) {
                tempDetails.setType(tempString.trim());
            }
            
            if(tempDetails.getType().equals(XMLConstants.SUBMITTED)) {
                String batchId = lock.getAttributeValue(XMLConstants.BATCHID);
                if (batchId != null && !batchId.equals("")) {
                    tempDetails.setBatchId(batchId);
                    tempDetails.setStatus("APPROVAL_PENDING_EDITING_PRIVILEGES"); //6
                } else {
                    tempDetails.setStatus("APPROVAL_PENDING"); //6
                }
            } else if(!(controller.getUserName().equals(tempDetails.getUser()))) {
                tempDetails.setStatus("CHKD_BY_ELSE");//2
            } else {
                if(!(tempDetails.getNodeName().equals(tempDetails.getRoot()))) {
                    tempDetails.setStatus("CHKD_BY_U_INTERNAL");//3
                } else {
                    String tempFile = tempDetails.getRoot() + ".xml";
                    tempFile = fileManager.getDataPath() + tempDetails.getRoot() + "/" + tempFile;
                    File file = new File(tempFile);
                    if(file.exists()) {
                        tempDetails.setStatus("CHKD_BY_U_AT_ROOT_FILE_EXISTS");//4
                    } else {
                        tempDetails.setStatus("CHKD_BY_U_AT_ROOT_FILE_DOESNT_EXIST");//5
                    }
                }
            } 
        } else {
            String permissions = match.getAttributeValue(XMLConstants.PERMISSIONS);
            if (permissions == null || permissions.equals(XMLConstants.ZERO)) {
                tempDetails.setCanDownload(false);
                tempDetails.setStatus("NO_PERMISSIONS");
            } else {
                tempDetails.setCanDownload(true);
                if (tempDetails.getParentPageName() == null) {
                    tempDetails.setStatus("NOT_CHKD_OUT"); //1
                } else {
                    tempDetails.setStatus("INTERNAL_NODE");
                }
            }
        }  
        return tempDetails;
    }
    
    public String getSearchErrorString(Element root) {
        Element errNumElem = root.getChild("ERRORNUM");
        String errorString = ""; 
        Controller controller = Controller.getController();        
        if (errNumElem != null) {
            if (errNumElem != null) {
                String errnum = errNumElem.getTextTrim();
                System.out.println("errnum is: " + errnum);
                if(errnum.equals("404")) {
                    errorString = controller.getMsgString("NO_RECORD_FOUND");
                } else if(errnum.equals("200200")) {
                    errorString = controller.getMsgString("TOO_MANY_RECORDS");
                }
            }
        } else if (StringUtils.notEmpty(root.getAttributeValue(XMLConstants.NEW_VERSION))){
            // it's the new server code not allowing a download
            errorString = controller.getMsgString("OLD_VERSION_NOT_SUPPORTED");
        }
        return errorString;
    }
}
