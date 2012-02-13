/*
 * ApplicationContextTest.java
 *
 * Created on May 3, 2004, 10:58 AM
 */

package org.tolweb.dao;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.springframework.context.ApplicationContext;
import org.tolweb.misc.ApplicationContextFactory;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.main.XMLWriter;

/**
 *
 * @author  dmandel
 */
public abstract class ApplicationContextTestAbstract extends TestCase {
    protected ApplicationContext context;
    
    static {
        ApplicationContextFactory.init("applicationContext.xml", true);
    }    
    
    /** Creates a new instance of ApplicationContextTest */
    public ApplicationContextTestAbstract(String name) {
        super(name);
        context = ApplicationContextFactory.getApplicationContext();        
    }  
    
	protected void checkDates(Date d1, Date d2, boolean checkTime) {
	    if (d1 != null && d2 != null) {
			assertEquals(d1.getYear(), d2.getYear());
			assertEquals(d1.getMonth(), d2.getMonth());
			assertEquals(d1.getDate(), d2.getDate());
			if (checkTime) {
				assertEquals(d1.getHours(), d2.getHours());
				assertEquals(d1.getMinutes(), d2.getMinutes());
				assertEquals(d1.getSeconds(), d2.getSeconds());        	
			}
	    } else {
	        if (!(d1 == null && d2 == null)) {
	            assertTrue(false);
	        }
	    }
	}
    
	protected void checkDates(Date d1, Date d2) {
		checkDates(d1, d2, true);
	}	
	
	
	protected String printOutDocument(Document doc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1000000);
        XMLOutputter serializer = XMLWriter.getXMLOutputter();
        try {
        	serializer.output(doc, out);
        	out.flush();
        	out.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }		
        return out.toString();
	}	
}
