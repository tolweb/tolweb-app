package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.PageContributor;

/**
 * Models the author name sequence component of a page citation. 
 * 
 * The format is bibliographic.  If Andrew Lenards, Lisa Antkow, 
 * and Simon Josep were the authors of a page then the author 
 * citation would be: 
 * 
 *    Lenards, Andrew, Lisa Antkow, and Simon Josep.
 * 
 * If the contributors collection is empty, the author defaults 
 * to being "Tree of Life Web Project." 
 * 
 * @author lenards
 *
 */
public class AuthorCitation implements PageCitationComponent {
	public static final String DEFAULT_AUTHOR = "Tree of Life Web Project.";
	
	private List<String> authorNames; 
	
	/**
	 * Constructs an instance given the author contributors
	 * @param contributors
	 */
	public AuthorCitation(Collection<PageContributor> contributors) {
		authorNames = new ArrayList<String>();
		initializeAuthorNames(contributors, authorNames);
	}

	/**
	 * Returns a formatted list, in bibliographic style, of the author's names. 
	 */
	public String getCitationString() {
		if (!authorNames.isEmpty()) {
			String names = StringUtils.returnCommaAndJoinedString(authorNames);
			return addPeriod(names.trim());
		} 
		return DEFAULT_AUTHOR;
	}

	/**
	 * Returns a formatted version of the argument if it is not null nor empty.
	 * @param names the argument to format
	 * @return a formatted version of the argument
	 */
	private String addPeriod(String names) {
		if (StringUtils.notEmpty(names) && !names.endsWith(".")) {
			return names + ".";
		}
		return names;
	}

	/**
	 * Initializes the internal author names collection given the values from the 
	 * contributors collection. 
	 * @param contributors the data used to seed the internal collection.
	 * @param names the internal collection which is being seeded. 
	 */
	private void initializeAuthorNames(Collection<PageContributor> contributors, List<String> names) {
		int index = 0;
		for(Iterator<PageContributor> itr = contributors.iterator(); itr.hasNext(); index++) {
			PageContributor pageContributor = (PageContributor)itr.next();
			Contributor actualContributor = pageContributor.getContributor();
			
			// we only want to add names if they're credited as authors for the page
			if(pageContributor.isAuthor()) {
				// if it's the very first author, add them as "LastName, FirstName" format
				if (authorNames.isEmpty()) {
					authorNames.add(getLastNameFirstName(actualContributor));
				} else {
					authorNames.add(actualContributor.getDisplayName());
				}
			}
		}
	}

	/**
	 * Returns a formatted version of the contributor's name in the 
	 * format: LastName, FirstName. 
	 * @param contr the contributor to format
	 * @return a formatted version of the contributor's name 
	 */
	private String getLastNameFirstName(Contributor contr) {
		return contr.getLastName() + ", " + contr.getFirstName();
	}
}
