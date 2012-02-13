/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.TreeGifDAO;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TreeGif;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treedrawing.TreeDrawer;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreeImg extends BaseComponent implements PageInjectable, BaseInjectable {
    public abstract MappedPage getTolPage();
    public abstract void setTreeGif(TreeGif value);
    public abstract boolean getIsWorking();
    @InjectObject("spring:treeGifDAO")
    public abstract TreeGifDAO getTreeGifDAO();
    @InjectObject("spring:workingTreeDrawer")
    public abstract TreeDrawer getWorkingTreeDrawer();
    @InjectObject("spring:publicTreeDrawer")
    public abstract TreeDrawer getPublicTreeDrawer();    
    
    protected void prepareForRender(IRequestCycle cycle) {
        super.prepareForRender(cycle);
        if (!getConfiguration().isUseNewTreeDrawing()) {
        	// note that this is very very very likely not used anymore.
	        TreeGifDAO dao = getTreeGifDAO();
	        TreeGif gif = dao.getTreeGifForPage(getTolPage(), false, getIsWorking());
	        setTreeGif(gif);
        } else {
	        boolean generate = getIsWorking();
	        TreeGif treeGif = null;
	        if (!generate) {
	            treeGif = getTreeGifDAO().getNewTreeGifForPage(getTolPage());
	        }
	        generate = generate || (treeGif == null);
	        if (generate) {
	            TreeDrawer drawer;
	            if (getIsWorking()) {
	                drawer = getWorkingTreeDrawer();
	            } else {
	            	drawer = getPublicTreeDrawer();                
	            }
	            treeGif = drawer.drawTreeForPage(getTolPage());
	            if (!getIsWorking()) {
	            	getTreeGifDAO().saveTreeGif(treeGif);
	            }
	        }
	        setTreeGif(treeGif);
        }
    }
}
