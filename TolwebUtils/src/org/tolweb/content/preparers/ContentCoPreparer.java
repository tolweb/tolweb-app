package org.tolweb.content.preparers;

import org.tolweb.content.helpers.DaoBundle;

import nu.xom.Element;

/**
 * Defines the interface of operation provided by all sub-preparers. 
 * 
 * Implemented by AbstractCoPreparer, which provided common functionality 
 * for sub-preparers. 
 * 
 * @see AbstractCoPreparer
 * 
 * @author lenards
 *
 */
public interface ContentCoPreparer {
	/**
	 * Sets the content source a sub-preparer will use when generated their 
	 * prepared element. 
	 * 
	 * @param payload the domain object associated with the content
	 * @param daos the data access object bundle needed to communicate with databases
	 * @param doc the parent XML document to add the prepared element
	 */
	public void setContentSource(Object payload, DaoBundle daos, Element doc);
	
	/**
	 * Kicks off the processing of the content source. 
	 */
	public void processContent();
	
	/**
	 * Returns the result of the sub-preparer actions (an XML representation of 
	 * the content). 
	 * 
	 * @return an XML representation of the content source. 
	 */
	public Element getElement();
}
