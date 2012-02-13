/*
 * Created on Jul 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArticleNoteHTMLEditorDelegate extends HTMLEditorDelegate {
    private Long nodeId;
    public ArticleNoteHTMLEditorDelegate(Long nodeId) {
        super();
        this.nodeId = nodeId;
    }
    public String getSearchUrl() {
        String nodeIdString = getNodeId() != null ? getNodeId().toString() : "-1";
        return "/onlinecontributors/app?service=external&page=ArticleNoteImageSearch&sp=l" + nodeIdString + "&sp=T&sp=F";
    }
    /**
     * @return Returns the nodeId.
     */
    public Long getNodeId() {
        return nodeId;
    }
    /**
     * @param nodeId The nodeId to set.
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }    
}
