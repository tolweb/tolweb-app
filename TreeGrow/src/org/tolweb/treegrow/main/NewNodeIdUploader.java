/*
 * NewNodeIdUploader.java
 *
 * Created on March 15, 2004, 3:24 PM
 */

package org.tolweb.treegrow.main;

import java.util.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import org.tolweb.treegrow.tree.*;

/**
 *
 * @author  dmandel
 */
public class NewNodeIdUploader {
    
    public static boolean upload(Tree tree) {
        // Before any images get uploaded, go ahead and upload new nodes
        String idStr = tree.getNewNodeIdsString();
        
        PostMethod post = new PostMethod(Controller.getController().getCGIPath() + "getNewNodesIds.pl");
        post.addParameter("node_id_list", idStr);
        
        HttpClient client = new HttpClient();
        try {
            client.setConnectionTimeout(30000);
            int status = client.executeMethod(post);
            org.jdom.Element root;
            if (status == HttpStatus.SC_OK) {
                String response = post.getResponseBodyAsString();
                System.out.println("response is: " + response);
                StringTokenizer pairs = new StringTokenizer(response,"&");
                StringTokenizer key_value;
                String cur_id, new_id;
                Enumeration e;

                while (pairs.hasMoreTokens()) {
                    key_value = new StringTokenizer(pairs.nextToken(),"=");
                    cur_id = key_value.nextToken();
                    new_id = key_value.nextToken();

                    Iterator it = tree.getNodeList().iterator();
                    while(it.hasNext()) {
                        Node n = (Node) it.next();
                        if (("" + n.getId()).equals(cur_id) ) {
                            n.setId(new Integer(new_id).intValue());
                        }
                    }
                    
                    it = tree.getChangedNodeImages().iterator();
                    while (it.hasNext()) {
                        NodeImage img = (NodeImage) it.next();
                        Iterator it2 = img.getNodes().iterator();
                        while (it2.hasNext()) {
                            ImageNode in = (ImageNode) it2.next();
                            if ((in.getNodeId() + "").equals(cur_id)) {
                                in.setNodeId(Integer.parseInt(new_id));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
}
