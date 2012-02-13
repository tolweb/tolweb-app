package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.EditHistoryDAO;
import org.tolweb.misc.CacheAccess;
import org.tolweb.misc.Configuration;
import org.tolweb.misc.RendererFactory;
import org.tolweb.misc.TextPreparer;
import org.tolweb.misc.URLBuilder;

public interface BaseInjectable {
    @InjectObject("spring:cacheAccess")
    public CacheAccess getCacheAccess();
    @InjectObject("spring:urlBuilder")
    public URLBuilder getUrlBuilder();    
    @InjectObject("spring:textPreparer")
    public TextPreparer getTextPreparer();
    @InjectObject("spring:configuration")
    public Configuration getConfiguration();
    @InjectObject("spring:editHistoryDAO")
    public EditHistoryDAO getEditHistoryDAO();
    @InjectObject("spring:miscEditHistoryDAO")
    public EditHistoryDAO getMiscEditHistoryDAO();    
    @InjectObject("spring:rendererFactory")
    public RendererFactory getRendererFactory();    
}
