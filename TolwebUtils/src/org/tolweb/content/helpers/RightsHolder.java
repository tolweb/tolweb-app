package org.tolweb.content.helpers;

import org.tolweb.treegrow.main.StringUtils;

/**
 * Lightweight representation of a copyright holder's 
 * homepage and email address.
 *  
 * @author lenards
 */
public class RightsHolder {
	public static final String TO_STRING_FORMAT = "<a href=\"%1$s\">%2$s</a>";
	private static final long serialVersionUID = 3831556250571435537L;
	private String name; 
	private String homepage;
	private String email;
	private String contributorProfile;
	private boolean nonContributor;
	
	/**
	 * Constructs a default instance of the object
	 */
	public RightsHolder() {
		this("", "", "");
	}

	/**
	 * Constructs an instance of the object
	 * @param name name of the rights holder
	 * @param homepage homepage/URL of the rights holder
	 * @param email email address of the rights holder 
	 */
	public RightsHolder(String name, String homepage, String email) {
		super();
		this.name = name;
		this.homepage = homepage;
		this.email = email;
		nonContributor = false; // default, assume they are a contributor
	}

	/**
	 * Returns a string representing the value need to identify the 
	 * rights holder.  
	 * 
	 * This representation may include the homepage or email in addition to 
	 * the name of the rights holder. 
	 * 
	 */
	public String toString() {
		if (hasLinkInfo()) {
			boolean useHomepage = StringUtils.notEmpty(homepage);
			if (useHomepage) {
				return String.format(TO_STRING_FORMAT, 
						getToStringLink(useHomepage), getName());
			} else if (!isNonContributor()) {
				return String.format(TO_STRING_FORMAT, 
						getToStringLink(false), getName());				
			} else {
				return getName();
			}
		} else {
			return getName();
		}
	}
	
	/**
	 * Indicates that the information for linking to the rights holder exists 
	 * 
	 * @return true, the rights holder information to use in hyperlinking; 
	 * otherwise, false. 
	 */
	private boolean hasLinkInfo() {
		return StringUtils.notEmpty(homepage) ||  
			   StringUtils.notEmpty(contributorProfile);
	}
	
	/**
	 * Returns a string representing a hyperlink to the rights holder's 
	 * contact information. 
	 * 
	 * @param useHomepage True indicates the homepage should be used over the 
	 * email address (if both present). False indicates the email address will 
	 * always be used. 
	 * 
	 * @return a string representing the hyperlink to use. 
	 */
	private String getToStringLink(boolean useHomepage) {
		return useHomepage ? getHomepage() : getAlternateLink();
	}

	private String getAlternateLink() {
		return isNonContributor() ? getName() : getContributorProfile();
	}

	/**
	 * Gets the homepage of the rights holder
	 * @return
	 */
	public String getHomepage() {
		return homepage;
	}

	/**
	 * Sets the homepage of the rights holder to the argument
	 * @param homepage
	 */
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	/**
	 * Gets the email of the rights holder
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email of the rights holder to the argument
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the name of the rights holder 
	 * 
	 * The name is either an individual, institution, or organization.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the rights holder
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the contributor profile URL.
	 * @return a string representing the contributor profile URL
	 */
	public String getContributorProfile() {
		return contributorProfile;
	}

	/**
	 * Sets the contributor profile to the argument.
	 * @param contributorProfile a string representing the contributor profile URL
	 */
	public void setContributorProfile(String contributorProfile) {
		this.contributorProfile = contributorProfile;
	}

	/**
	 * Indicates that the rights holder is not a registered Tree of Life Web 
	 * Project Contributor. 
	 * 
	 * @return true, if the rights holder is not a registered contributor; 
	 * otherwise, false.  
	 */
	public boolean isNonContributor() {
		return nonContributor;
	}

	/**
	 * @param nonContributor the nonContributor to set
	 */
	public void setNonContributor(boolean nonContributor) {
		this.nonContributor = nonContributor;
	} 
}
