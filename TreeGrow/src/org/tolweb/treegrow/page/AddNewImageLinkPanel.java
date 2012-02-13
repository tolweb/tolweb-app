/*
 * Created on Mar 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.page;

import java.util.Hashtable;

import org.tolweb.treegrow.main.HttpRequestMaker;
import org.tolweb.treegrow.main.HyperlinkPanel;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.tree.Node;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddNewImageLinkPanel extends HyperlinkPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4387670237067464399L;

	public AddNewImageLinkPanel(Node node) {
        super("Add New Image to the Database", "");
        Hashtable args = new Hashtable();
        if (node.getId() > 0) {
            // If the node this page refers to exists in the database,
            // then add it to the request so that the node will automatically be attached
            args.put(RequestParameters.NODE_ID, "" + node.getId());
        }
        String url = HttpRequestMaker.getExternalFrontEndUrlString("UploadNewImage", args);
        setLinkDestination(url);
    }
}
