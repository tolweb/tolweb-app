package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.tolweb.treegrow.main.StringUtils;

public class AlphabeticalObjectArrayPagingState extends AlphabeticalPagingState {
	@SuppressWarnings("unchecked")
	public AlphabeticalObjectArrayPagingState(Collection contributors) {
		super(contributors);
	}
	
    /**
     * The idea is to break up the contributors into lists based
     * on the first letter of their last name or alias
     */ 
	@SuppressWarnings("unchecked")
    protected void initContributors(Collection contributors) {
        List currentContributors = null;
        String firstLetterLastName = null;
        for (Iterator iter = contributors.iterator(); iter.hasNext();) {
            Object[] nextRow = (Object[]) iter.next();
            String lastName = (String) nextRow[0];
            String currentFirstLetterLastName = StringUtils.notEmpty(lastName) ? lastName.substring(0, 1) : null;
            if (StringUtils.isEmpty(currentFirstLetterLastName)) {
            	continue;
            }
            if (firstLetterLastName == null || 
                    !currentFirstLetterLastName.toUpperCase().equals(firstLetterLastName.toUpperCase())) {
                if (currentContributors != null) {
                    contributorsByLastName.add(currentContributors);
                }
                currentContributors = new ArrayList();
                firstLetterLastName = currentFirstLetterLastName; 
            }
            currentContributors.add(nextRow);
            if (!iter.hasNext()) {
            	contributorsByLastName.add(currentContributors);
            }
        }
        
    }	
	
	@SuppressWarnings("unchecked")
    public Object[] getSelectableLetters() {
        ArrayList strings = new ArrayList();
        for (Iterator iter = contributorsByLastName.iterator(); iter.hasNext();) {
            List nextRows = (List) iter.next();
            String lastName = (String) ((Object[]) nextRows.get(0))[0];
            if (lastName != null) {
                strings.add(lastName.substring(0, 1).toUpperCase());
            }
        }
        return strings.toArray();
    }    
}
