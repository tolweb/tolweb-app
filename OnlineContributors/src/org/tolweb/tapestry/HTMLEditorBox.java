/*
 * Created on Jun 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class HTMLEditorBox extends BaseComponent {
    public abstract int getIdIndex();
    
    public String getDivTrigger() {
        return "htmleditorinstruct" + getIdIndex();
    }
    public String getH4Trigger() {
        return "htmleditortrigger" + getIdIndex();
    }
    
    public String getMediaDivTrigger() {
        return "addimgsoundmov" + getIdIndex();
    }
    
    public String getMediaH4Trigger() {
        return "nfaddmediatrigger" + getIdIndex();
    }
    
    public String getMediaTriggerClickString() {
        return getShowBranchString(getMediaDivTrigger(), getMediaH4Trigger());
    }
    
    public String getTriggerClickString() {
        return getShowBranchString(getDivTrigger(), getH4Trigger());
    }
    
    private String getShowBranchString(String divTrigger, String h4Trigger) {
        return "showbranch('" + divTrigger + "', 'show', 'hide'); showbranch('" + h4Trigger + "', 'tridown', 'triup')";
    }
}
