package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.archive.BranchLeafPageArchiver;
import org.tolweb.dao.ArchivedPageDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PagePusher;
import org.tolweb.dao.TreeGifDAO;
import org.tolweb.misc.URLBuilder;

public interface PageInjectable {
    @InjectObject("spring:workingPageDAO")
    public PageDAO getWorkingPageDAO();
    @InjectObject("spring:publicPageDAO")    
    public PageDAO getPublicPageDAO();
    @InjectObject("spring:pagePusher")
    public PagePusher getPagePusher();	
    @InjectObject("spring:archivedPageDAO")
    public ArchivedPageDAO getArchivedPageDAO();
    @InjectObject("spring:pageArchiver")
    public BranchLeafPageArchiver getPageArchiver();
}
