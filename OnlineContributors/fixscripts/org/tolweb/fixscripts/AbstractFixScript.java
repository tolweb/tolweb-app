package org.tolweb.fixscripts;

import org.springframework.context.ApplicationContext;
import org.tolweb.misc.ApplicationContextFactory;

public abstract class AbstractFixScript {
    protected ApplicationContext context;
    
    static {
        ApplicationContextFactory.init("applicationContext.xml", true);
    }    
    
    /** Creates a new instance of ApplicationContextTest */
    public AbstractFixScript() {
        context = ApplicationContextFactory.getApplicationContext();        
    }
}
