package org.tolweb.content.services;

import org.apache.tapestry.util.ContentType;
import org.tolweb.treegrowserver.tapestry.XMLPage;

/**
 * A base class for all XML content services realized as 
 * Tapestry Pages.  
 * 
 * @author lenards
 */
public abstract class XMLContentBaseService extends XMLPage {
	/**
	 * Returns the MIME content type of 'text/xml' for all subclasses.
	 */
	public ContentType getResponseContentType() {
		return new ContentType("text/xml");
	}
}
