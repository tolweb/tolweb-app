package org.tolweb.hivemind;

import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.btol.Project;
import org.tolweb.btol.dao.ProjectDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;

public class CookieAndContributorSourceImpl extends AppStateManagerAware implements CookieAndContributorSource {
	private static final String COOKIE_ON_VALUE = "1";
	private static final String COOKIE_OFF_VALUE = "0";
	private static final String PROJECT = "project";
	private HttpServletRequest _request;
	private HttpServletResponse _response;
	private int _defaultMaxAge;
	private ContributorDAO contributorDAO;
	private ProjectDAO projectDAO;
	private PasswordUtils passwordUtils;
	private IRequestCycle requestCycle;	

	public String readCookieValue(String name) {
		Cookie cookie = getCookie(name);
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}
	private Cookie getCookie(String name) {
		Cookie[] cookies = _request.getCookies();

		if (cookies == null) {
			return null;
		}
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(name)) {
				return cookies[i];
			}
		}
		return null;
	}

	public void writeCookieValue(String name, String value) {
		writeCookieValue(name, value, _defaultMaxAge);
	}

	public void writeCookieValue(String name, String value, int maxAge) {
		writeCookieValue(name, value, maxAge, null);
	}

	public void removeCookieValue(String name) {
		Cookie cookie = new Cookie(name, null);
		//cookie.setPath(_request.getContextPath() + "/");		
		cookie.setPath("/");
		cookie.setMaxAge(0);
		cookie.setDomain(COOKIE_DOMAIN);
		_response.addCookie(cookie);
		// find the old cookie and invalidate it -- otherwise this will potentially
		// have stale data
		Cookie oldCookie = getCookie(name);
		if (oldCookie != null) {
			oldCookie.setValue("");
		}
	}

	public void setRequest(HttpServletRequest request) {
		_request = request;
	}

	public void setResponse(HttpServletResponse response) {
		_response = response;
	}

	public void setDefaultMaxAge(int defaultMaxAge) {
		_defaultMaxAge = defaultMaxAge;
	}

	public void writeCookieValue(String name, String value, int maxAge,
			String domain) {
		Cookie cookie = new Cookie(name, value);
		//cookie.setPath(_request.getContextPath() + "/");
		cookie.setPath("/");		
		cookie.setMaxAge(maxAge);
		if (domain != null) {
			cookie.setDomain(domain);
		}
		_response.addCookie(cookie);
	}
	
	public void addWorkingCookie() {
        writeCookieValue(CookieAndContributorSource.WORKING_COOKIE, "1", ONE_YEAR, COOKIE_DOMAIN);	    
	}
	public void addWorkingContributorCookie(Contributor contr) {
		addWorkingContributorCookie(contr, DEFAULT_COOKIE_MAX_AGE);
	}
	public void addWorkingContributorCookie(Contributor contr, int cookieMaxAge) {
	    // Encode the cookie value as the contributor's id followed by the MD5 hash of their password
	    String value = contr.getId() + AUTH_COOKIE_SEPARATOR + contr.getPassword();
	    writeCookieValue(CookieAndContributorSource.PERMISSIONS_COOKIE, value, cookieMaxAge, COOKIE_DOMAIN);
	}
	public String getAuthCookieValue() {
		return readCookieValue(CookieAndContributorSource.PERMISSIONS_COOKIE);
	}
	public Contributor getContributorFromAuthCookie() {
		try {
			String cookieValue = getAuthCookieValue();
			if (StringUtils.notEmpty(cookieValue)) {
				String[] pieces = cookieValue.split(AUTH_COOKIE_SEPARATOR);
				if (pieces.length == 1) {
					throw new RuntimeException("missing password, contr id is: " + pieces[0]);
				}
				String contrId = pieces[0];
				String password = pieces[1];
				Contributor contr = getContributorDAO().getContributorWithId(contrId);
				String contributorPassword = contr.getPassword();
				if (contributorPassword.equals(password)) {
					// make sure they get into the session if they aren't already
					if (!getContributorExists()) {
						setContributor(contr);
					}
				    return contr;
				} else {
					String exceptionMessage = "bad password";
					if (contr != null) {
						exceptionMessage += " - contr-id: " + contr.getId();
					}
				    throw new RuntimeException(exceptionMessage);
				}
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}		
	}
	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}

	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
	}
	public boolean getCookieIsEnabled(String cookieName) {
	    String cookieVal = readCookieValue(cookieName);
	    return cookieVal != null && Integer.parseInt(cookieVal) > 0;
	}
	public Contributor getContributorFromSessionOrAuthCookie() {
		Contributor contr = getContributorFromAuthCookie();
		if (contr != null) {
			return contr;
		} else {
			return getContributor(); 
		}
	}
	public Contributor authenticateExternalPage(IRequestCycle cycle) {
	    try {
		    String userId = cycle.getInfrastructure().getRequest().getParameterValue(RequestParameters.USER_ID);
		    String password = cycle.getInfrastructure().getRequest().getParameterValue(RequestParameters.PASSWORD);
		    if (userId == null || password == null) {
		        // Check the service parameters in case it was called that way
		        Object[] parameters = cycle.getListenerParameters();
		        userId = (String) parameters[0];
		        password = (String) parameters[1];
		    }	    	    
		    if (getPasswordUtils().checkPassword(userId, password)) {
		        Contributor contr = getContributorDAO().getContributorWithEmail(userId);
		        loginContributor(contr);
		        return contr;
		    } else {
		        return null;
		    }
	    } catch (Exception e) {
	        return null;
	    }
	}
    
    public void loginContributor(Contributor contr) {
        loginContributor(contr, DEFAULT_COOKIE_MAX_AGE);
    }
    public void loginContributor(Contributor contr, int cookieMaxAge) {
        setContributor(contr);
        addWorkingContributorCookie(contr, cookieMaxAge);
        addWorkingCookie();    	
    }
	public PasswordUtils getPasswordUtils() {
		return passwordUtils;
	}
	public void setPasswordUtils(PasswordUtils passwordUtils) {
		this.passwordUtils = passwordUtils;
	}
	public IRequestCycle getRequestCycle() {
		return requestCycle;
	}
	public void setRequestCycle(IRequestCycle requestCycle) {
		this.requestCycle = requestCycle;
	}
	public String getProjectCookieValue() {
		return readCookieValue(PROJECT_COOKIE);
	}
	public boolean getProjectExists() {
		return getAppStateManager().exists(PROJECT);
	}
	public Project getProject() {
		return (Project) getAppStateManager().get(PROJECT);
	}
	public void setProject(Project value) {
		getAppStateManager().store(PROJECT, value);
	}
	public void loginProject(Project project) {
	    // Encode the cookie value as the contributor's id followed by the MD5 hash of their password
	    String value = project.getId().toString();
	    value = "zxchfbshdFSIOJAFSDL324782SDLKFJSDKLFJS" + AUTH_COOKIE_SEPARATOR + value;
	    writeCookieValue(PROJECT_COOKIE, value, ONE_YEAR, COOKIE_DOMAIN);
	    setProject(project);
	}
	public Project getProjectFromSessionOrProjectCookie() {           
		String cookieValue = getProjectCookieValue();
		if (getProjectExists()) {
			return getProject();
		} else {
			if (StringUtils.notEmpty(cookieValue)) {
				String[] pieces = cookieValue.split(AUTH_COOKIE_SEPARATOR);
				System.out.println("first piece is: " + pieces[0]);
				System.out.println("pieces is: " + Arrays.toString(pieces));
				String projectId = pieces[1];
				Project project = getProjectDAO().getProjectWithId(Long.valueOf(projectId));
				if (project != null) {
					if (!getProjectExists()) {
						setProject(project);
					}
				    return project;
				} else {
				    return null;
				}
			} else {
				return null;
			}
		}
	}	
	public ProjectDAO getProjectDAO() {
		return projectDAO;
	}
	public void setProjectDAO(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}
	public void addShowDnaInformationCookie() {
		writeCookieValue(CookieAndContributorSource.DNA_INFORMATION_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeShowDnaInformationCookie() {
		writeCookieValue(CookieAndContributorSource.DNA_INFORMATION_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public boolean getShowDnaInformationCookieEnabled() {
		String cookieVal  = readCookieValue(CookieAndContributorSource.DNA_INFORMATION_COOKIE);
		return COOKIE_ON_VALUE.equals(cookieVal);
	}
	public boolean getShowDnaInformationCookieExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.DNA_INFORMATION_COOKIE));
	}	
	public void addShowAdultInformationCookie() {
		writeCookieValue(CookieAndContributorSource.ADULT_INFORMATION_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeShowAdultInformationCookie() {
		writeCookieValue(CookieAndContributorSource.ADULT_INFORMATION_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public boolean getShowAdultInformationCookieEnabled() {
		String cookieVal = readCookieValue(CookieAndContributorSource.ADULT_INFORMATION_COOKIE); 
		return COOKIE_ON_VALUE.equals(cookieVal);
	}
	public boolean getShowAdultInformationCookieExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.ADULT_INFORMATION_COOKIE));
	}	
	public void addShowLarvaeInformationCookie() {
		writeCookieValue(CookieAndContributorSource.LARVAE_INFORMATION_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeShowLarvaeInformationCookie() {
		writeCookieValue(CookieAndContributorSource.LARVAE_INFORMATION_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public boolean getShowLarvaeInformationCookieEnabled() {
		String cookieVal = readCookieValue(CookieAndContributorSource.LARVAE_INFORMATION_COOKIE);
		return COOKIE_ON_VALUE.equals(cookieVal);
	}
	public boolean getShowLarvaeInformationCookieExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.LARVAE_INFORMATION_COOKIE));
	}	
	public void addShowGeoDistInformationCookie() {
		writeCookieValue(CookieAndContributorSource.GEO_DIST_INFORMATION_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeShowGeoDistInformationCookie() {
		writeCookieValue(CookieAndContributorSource.GEO_DIST_INFORMATION_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public boolean getShowGeoDistInformationCookieEnabled() {
		String cookieVal = readCookieValue(CookieAndContributorSource.GEO_DIST_INFORMATION_COOKIE);
		return COOKIE_ON_VALUE.equals(cookieVal);
	}
	public boolean getShowGeoDistInformationCookieExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.GEO_DIST_INFORMATION_COOKIE));
	}	
	public void addShowDnaNotesCookie() {
		writeCookieValue(CookieAndContributorSource.DNA_NOTES_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeShowDnaNotesCookie() {
		writeCookieValue(CookieAndContributorSource.DNA_NOTES_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	public boolean getShowDnaNotesCookieEnabled() {
		String cookieVal = readCookieValue(CookieAndContributorSource.DNA_NOTES_COOKIE);
		return COOKIE_ON_VALUE.equals(cookieVal);
	}
	public boolean getShowDnaNotesCookieExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.DNA_NOTES_COOKIE));
	}	
	// methods for managing filter criteria settings (value re display options)
	public void addTierFilterCriteria(Integer tierSelection) {
		writeCookieValue(CookieAndContributorSource.TIER_FILTER_CRITERIA, tierSelection.toString(), ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeTierFilterCriteria() {
		writeCookieValue(CookieAndContributorSource.TIER_FILTER_CRITERIA, "5", ONE_YEAR, COOKIE_DOMAIN);
	}
	public Integer getTierFilterCriteria() {
		String cookieVal = readCookieValue(CookieAndContributorSource.TIER_FILTER_CRITERIA);
		return Integer.valueOf(cookieVal);
	}
	public boolean getTierFilterCriteriaExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.TIER_FILTER_CRITERIA));
	}
	public void addNeededFilterCriteria(Integer neededSelection) {
		writeCookieValue(CookieAndContributorSource.NEEDED_FILTER_CRITERIA, neededSelection.toString(), ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeNeededFilterCriteria() {
		writeCookieValue(CookieAndContributorSource.NEEDED_FILTER_CRITERIA, "0", ONE_YEAR, COOKIE_DOMAIN);
	}
	public Integer getNeededFilterCriteria() {
		String cookieVal = readCookieValue(CookieAndContributorSource.NEEDED_FILTER_CRITERIA);
		return Integer.valueOf(cookieVal);
	}
	public boolean getNeededFilterCriteriaExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.NEEDED_FILTER_CRITERIA));
	}
	public void addDnaSupplierFilterCriteria(int contributorId) {
		writeCookieValue(CookieAndContributorSource.DNA_SUPPLIER_FILTER_CRITERIA, "" + contributorId, ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeDnaSupplierFilterCriteria() {
		removeCookieValue(CookieAndContributorSource.DNA_SUPPLIER_FILTER_CRITERIA);
	}
	public int getDnaSupplierFilterCriteria() {
		String cookieVal = readCookieValue(CookieAndContributorSource.DNA_SUPPLIER_FILTER_CRITERIA);
		return Integer.parseInt(cookieVal);
	}
	public boolean getDnaSupplierFilterCriteriaExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.DNA_SUPPLIER_FILTER_CRITERIA));
	}
	public void addGeoDistFilterCriteria(String geoDist) {
		writeCookieValue(CookieAndContributorSource.GEO_DIST_FILTER_CRITERIA, geoDist, ONE_YEAR, COOKIE_DOMAIN);
	}
	public void removeGeoDistFilterCriteria() {
		removeCookieValue(CookieAndContributorSource.GEO_DIST_FILTER_CRITERIA);
	}
	public String getGeoDistFilterCriteria() {
		return readCookieValue(CookieAndContributorSource.GEO_DIST_FILTER_CRITERIA);
	}
	public boolean getGeoDistFilterCriteriaExists() {
		return StringUtils.notEmpty(readCookieValue(CookieAndContributorSource.GEO_DIST_FILTER_CRITERIA));
	}
	
	@Override
	public void addGlossaryCookie() {
		writeCookieValue(CookieAndContributorSource.GLOSSARY_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);		
	}
	@Override
	public void addInetInfoCookie() {
		writeCookieValue(CookieAndContributorSource.INET_INFO_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);
	}
	@Override
	public void addRandomImagesCookie() {
		writeCookieValue(CookieAndContributorSource.RANDOM_IMAGES_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);		
	}
	@Override
	public void addTaxonListCookie() {
		writeCookieValue(CookieAndContributorSource.TAXON_LIST_COOKIE, COOKIE_ON_VALUE, ONE_YEAR, COOKIE_DOMAIN);		
	}
	@Override
	public void removeGlossaryCookie() {
		writeCookieValue(CookieAndContributorSource.GLOSSARY_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);		
	}
	@Override
	public void removeInetInfoCookie() {
		writeCookieValue(CookieAndContributorSource.INET_INFO_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);		
	}
	@Override
	public void removeRandomImagesCookie() {
		writeCookieValue(CookieAndContributorSource.RANDOM_IMAGES_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);		
	}
	@Override
	public void removeTaxonListCookie() {
		writeCookieValue(CookieAndContributorSource.TAXON_LIST_COOKIE, COOKIE_OFF_VALUE, ONE_YEAR, COOKIE_DOMAIN);		
	}	
}