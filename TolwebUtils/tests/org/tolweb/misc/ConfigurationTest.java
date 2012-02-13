package org.tolweb.misc;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;

public class ConfigurationTest extends TestCase {
	private Configuration config; 
	protected ApplicationContext context;

	static {
		ApplicationContextFactory.init("applicationContext.xml");
	}
	
	public ConfigurationTest(String name) {
		super(name);
		context = ApplicationContextFactory.getApplicationContext();
		config = (Configuration) context.getBean("configuration");
	}
	
	public void testSearchURL() {
		String searchUrlFormat = config.getSearchUrl();
		String actual = String.format(searchUrlFormat, "marsupials");
		assertEquals("search?taxon=marsupials", actual);
	}
}
