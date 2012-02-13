/*
 * Created on Mar 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.ImageDAO;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ImageOptions extends BasePage implements IExternalPage, ImageInjectable {
    public abstract void setImage(NodeImage img);
    public abstract NodeImage getImage();
    public abstract void setIsWysiwyg(boolean value);
    public abstract boolean getIsWysiwyg();
    public abstract void setIsResize(boolean value);
    public abstract boolean getIsResize();
    public abstract ImageVersion getCurrentVersion();
    
    public void activateExternalPage(Object[] args, IRequestCycle cycle) {
        Integer imageId = (Integer) args[0];
        ImageDAO dao = getImageDAO();
        setImage(dao.getImageWithId(imageId.intValue()));
        Boolean isWysiwyg = (Boolean) args[1];
        setIsWysiwyg(isWysiwyg.booleanValue());
        if (args.length > 2) {
            Boolean isResize = (Boolean) args[2];
            setIsResize(isResize.booleanValue());
        }
    }
    
    public String getMasterTagDescription() {
        return "Master<a class=\"noline\" href=\"#size\">*</a>";
    }
    
    @SuppressWarnings("unchecked")
    public List getVersions() {
        ImageDAO dao = getImageDAO();
        List versions = dao.getUsableVersionsForImage(getImage());
        for (Iterator iter = new ArrayList(versions).iterator(); iter.hasNext();) {
            ImageVersion nextVersion = (ImageVersion) iter.next();
            if (nextVersion.getIsMaster()) {
                versions.remove(nextVersion);
            }
        }
        return versions;
    }
}
