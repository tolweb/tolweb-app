package org.tolweb.treegrowserver;

import java.util.ArrayList;
import java.util.List;

public class ParentPageInfo {
    private Long pageId;
    private int currentPageCount = 0;
    private List ispuNodes;
    
    /**
     * @return Returns the pageId.
     */
    public Long getPageId() {
        return pageId;
    }
    /**
     * @param pageId The pageId to set.
     */
    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }
    public int incrementPageCount() {
        return currentPageCount++;
    }
    /**
     * @return Returns the ispuNodes.
     */
    public List getIspuNodes() {
        if (ispuNodes == null) {
            ispuNodes = new ArrayList();
        }
        return ispuNodes;
    }
    /**
     * @param ispuNodes The ispuNodes to set.
     */
    public void setIspuNodes(List ispuNodes) {
        this.ispuNodes = ispuNodes;
    }
}