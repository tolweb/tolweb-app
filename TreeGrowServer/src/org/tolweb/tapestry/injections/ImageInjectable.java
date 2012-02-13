package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.ImageDAO;
import org.tolweb.misc.ImageUtils;

public interface ImageInjectable {
    @InjectObject("spring:imageDAO")
    public abstract ImageDAO getImageDAO();
    @InjectObject("spring:imageUtils")
    public abstract ImageUtils getImageUtils();
}
