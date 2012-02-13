package org.tolweb.content.export

import org.springframework.context.ApplicationContext
import org.tolweb.misc.ApplicationContextFactory

abstract class AbstractApplicationTestContext extends GroovyTestCase {
	protected ApplicationContext context
	
	static {
	 	ApplicationContextFactory.init("applicationContext.xml", true);	 	
	}
 
 	public AbstractApplicationTestContext() {
 		super()
 		context = ApplicationContextFactory.getApplicationContext();
 	}
}