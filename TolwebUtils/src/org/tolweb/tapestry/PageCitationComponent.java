package org.tolweb.tapestry;

/**
 * Defines the common method that all components of 
 * a page citation have. 
 * 
 * @author lenards
 *
 */
public interface PageCitationComponent {
	/**
	 * Returns the string citation for the component.
	 * @return a string representation of the citation component.
	 */
	public String getCitationString();
}
