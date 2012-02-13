/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class RandomPics extends BaseComponent implements ImageInjectable, BaseInjectable {
    public abstract Object[] getCurrentRandomPic();
    
    public String getImgLocation() {
        return getImageUtils().getUrlPrefix() + (String) getCurrentRandomPic()[0]; 
    }
    
    public String getPageURL() {
    	return getUrlBuilder().getURLForBranchPage(URLBuilder.NO_HOST_PREFIX, (String) getCurrentRandomPic()[1], ((Long) getCurrentRandomPic()[2]));
    }
}
