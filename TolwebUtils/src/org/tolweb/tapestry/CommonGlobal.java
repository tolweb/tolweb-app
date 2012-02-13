package org.tolweb.tapestry;

import org.springframework.context.ApplicationContext;
import org.tolweb.misc.ApplicationContextFactory;

public class CommonGlobal {
    protected ApplicationContext context;
	
    static {
        ApplicationContextFactory.init("applicationContext.xml");        
    }
    
    public CommonGlobal() {
        context = ApplicationContextFactory.getApplicationContext();    	
    }

    /**
     * @return Returns the context.
     */
    public ApplicationContext getContext() {
        return context;
    }

    /**
     * @param context The context to set.
     */
    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}