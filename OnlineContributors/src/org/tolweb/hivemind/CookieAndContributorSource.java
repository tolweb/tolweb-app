package org.tolweb.hivemind;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.btol.Project;
import org.tolweb.treegrow.main.Contributor;

public interface CookieAndContributorSource extends org.apache.tapestry.services.CookieSource {
	public static final String AUTH_COOKIE_SEPARATOR = "&";
	public static final String COOKIE_DOMAIN = ".tolweb.org";
//	public static final String COOKIE_DOMAIN = ".library.arizona.edu";
	public static final String COOKIE_PATH = "/";
	public static final int DEFAULT_COOKIE_MAX_AGE = 5 * 60 * 60;
	public static final int ONE_YEAR = 60 * 60 * 24 * 365;
	public static final String GLOSSARY_COOKIE = "Show_glossary";
	public static final String INET_INFO_COOKIE = "Move_internet_info";
	public static final String TAXON_LIST_COOKIE = "Show_taxon_list";
	public static final String RANDOM_IMAGES_COOKIE = "Show_random_images";
	public static final String WORKING_COOKIE = "working";
	public static final String PERMISSIONS_COOKIE = "permissions";
	
	public static final String DNA_INFORMATION_COOKIE = "dna_info_cookie";
	public static final String ADULT_INFORMATION_COOKIE = "adult_info_cookie";
	public static final String LARVAE_INFORMATION_COOKIE = "larvae_info_cookie";
	public static final String GEO_DIST_INFORMATION_COOKIE = "geo_info_cookie";
	public static final String DNA_NOTES_COOKIE = "dna_notes_cookie";
	public static final String TIER_FILTER_CRITERIA = "tier_filter_criteria";
	public static final String NEEDED_FILTER_CRITERIA = "needed_filter_criteria";
	public static final String DNA_SUPPLIER_FILTER_CRITERIA = "dna_supplier_filter_criteria";
	public static final String GEO_DIST_FILTER_CRITERIA = "geo_dist_filter_criteria";
	
	/**
	 * Encoded scientific project id
	 */
	public static final String PROJECT_COOKIE = "project";
	
    /**
     * Returns the value of the first cookie whose name matches. Returns null if no such cookie
     * exists. This method is only aware of cookies that are part of the incoming request; it does
     * not know about additional cookies added since then (via
     * {@link #writeCookieValue(String, String)}).
     */
    String readCookieValue(String name);

    /**
     * Creates or updates a cookie value. The value is stored using a max age (in seconds) defined
     * by the symbol <code>org.apache.tapestry.default-cookie-max-age</code>. The factory default
     * for this value is the equivalent of one week.
     */

    void writeCookieValue(String name, String value);

    /**
     * As with {@link #writeCookieValue(String, String)} but an explicit maximum age may be set.
     * 
     * @param name
     *            the name of the cookie
     * @param value
     *            the value to be stored in the cookie
     * @param maxAge
     *            the maximum age, in seconds, to store the cookie
     */

    void writeCookieValue(String name, String value, int maxAge);
    
    void writeCookieValue(String name, String value, int maxAge, String domain);

    /**
     * Removes a previously written cookie, by writing a new cookie with a maxAge of 0.
     */

    void removeCookieValue(String name);
	
	public void addWorkingCookie();
	public void addWorkingContributorCookie(Contributor contr);
	public void addWorkingContributorCookie(Contributor contr, int cookieMaxAge);
	public String getAuthCookieValue();
	public Contributor getContributorFromAuthCookie();
	public boolean getCookieIsEnabled(String cookieName);
	public Contributor getContributorFromSessionOrAuthCookie();
	public Contributor authenticateExternalPage(IRequestCycle cycle);
	public void loginContributor(Contributor contr);
	public void loginContributor(Contributor contr, int cookieMaxAge);
	
	public Project getProjectFromSessionOrProjectCookie();
	public void loginProject(Project project);
	
	public boolean getShowDnaInformationCookieExists();	
	public boolean getShowDnaInformationCookieEnabled();
	public void addShowDnaInformationCookie();
	public void removeShowDnaInformationCookie();
	
	public boolean getShowAdultInformationCookieExists();	
	public boolean getShowAdultInformationCookieEnabled();
	public void addShowAdultInformationCookie();
	public void removeShowAdultInformationCookie();
	
	public boolean getShowLarvaeInformationCookieExists();
	public boolean getShowLarvaeInformationCookieEnabled();
	public void addShowLarvaeInformationCookie();
	public void removeShowLarvaeInformationCookie();
	
	public boolean getShowGeoDistInformationCookieExists();
	public boolean getShowGeoDistInformationCookieEnabled();
	public void addShowGeoDistInformationCookie();
	public void removeShowGeoDistInformationCookie();

	public boolean getShowDnaNotesCookieExists();
	public boolean getShowDnaNotesCookieEnabled();
	public void addShowDnaNotesCookie();
	public void removeShowDnaNotesCookie();	
	
	public Integer getTierFilterCriteria();
	public boolean getTierFilterCriteriaExists();
	public void addTierFilterCriteria(Integer tierSelection);
	public void removeTierFilterCriteria();
	
	public Integer getNeededFilterCriteria();
	public boolean getNeededFilterCriteriaExists();
	public void addNeededFilterCriteria(Integer neededSelection);
	public void removeNeededFilterCriteria();
	
	public int getDnaSupplierFilterCriteria();
	public boolean getDnaSupplierFilterCriteriaExists();
	public void addDnaSupplierFilterCriteria(int contributorId);
	public void removeDnaSupplierFilterCriteria();
	
	public String getGeoDistFilterCriteria();
	public boolean getGeoDistFilterCriteriaExists();
	public void addGeoDistFilterCriteria(String geoDist);
	public void removeGeoDistFilterCriteria();

	public void addGlossaryCookie();
	public void removeGlossaryCookie();

	public void addRandomImagesCookie();
	public void removeRandomImagesCookie();

	public void addInetInfoCookie();
	public void removeInetInfoCookie();

	public void addTaxonListCookie();
	public void removeTaxonListCookie();
}
