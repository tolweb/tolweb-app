/*
 * Created on Oct 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Collection;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TitleIllustrations extends BaseComponent implements 
		ImageInjectable, BaseInjectable {
	@SuppressWarnings("unchecked")
    public abstract Collection getIllustrations();
    public abstract TitleIllustration getCurrentIllustration();
    public abstract void setCurrentIllustration(TitleIllustration value);
    public abstract void setIsSingleIllustration(boolean value);
    public abstract boolean getIsSingleIllustration();
    public abstract int getIndex();
    public abstract void setContributor(Contributor contributor);
    
    public String getAltText() {
        if (getCurrentIllustration().getImage() != null) {
            NodeImage img = getCurrentIllustration().getImage();
            if (StringUtils.notEmpty(img.getAltText())) {
                return img.getAltText();
            } else {
                return " ";
            }
        } else {
            return " ";
        }
    }
    
    public void prepareForRender(IRequestCycle cycle) {
        super.prepareForRender(cycle);
        if (getIllustrations() != null && getIllustrations().size() == 1) {
            setIsSingleIllustration(true);
        } else {
            setIsSingleIllustration(false);
        }
    }
    
    public String getCurrentImageLocation() {
        TitleIllustration currentIllustration = getCurrentIllustration();
        ImageVersion version = currentIllustration.getVersion();
        String url;
        if (StringUtils.isEmpty(version.getFileName())) {
            url = getImageDAO().generateAndSaveVersion(version);
        } else {
            url = getImageUtils().getVersionUrl(currentIllustration.getVersion());
        }
        return url;
    }
    
    public String getCurrentImageClass() {
        if (getIsSingleIllustration()) {
            return "singletillus";
        } else {
            return null;
        }
    }
}
