package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.contrib.table.model.ITablePagingState;
import org.tolweb.hibernate.Student;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class AlphabeticalPagingState implements ITablePagingState {
	@SuppressWarnings("unchecked")
    protected List contributorsByLastName;
    private int currentPageIndex = 0;
    private boolean hasInstitutions = false;
    
    @SuppressWarnings("unchecked")
    public AlphabeticalPagingState(Collection contributors) {
        contributorsByLastName = new ArrayList();
        initContributors(contributors);
    }
    
    /**
     * The idea is to break up the contributors into lists based
     * on the first letter of their last name or alias
     */
    @SuppressWarnings("unchecked")
    protected void initContributors(Collection contributors) {
        List currentContributors = null;
        String firstLetterLastName = null;
        ArrayList nullLastName = new ArrayList();
        for (Iterator iter = contributors.iterator(); iter.hasNext();) {
            Contributor contr = (Contributor) iter.next();
            String lastName = contr.getLastName();
            if (Student.class.isInstance(contr)) {
                lastName = ((Student) contr).getAlias();
            }
            String currentFirstLetterLastName = StringUtils.notEmpty(lastName) ? lastName.substring(0, 1) : null;
            if (StringUtils.isEmpty(lastName)) {
                nullLastName.add(contr);
                continue;
            } else if (firstLetterLastName == null || 
                    !currentFirstLetterLastName.toUpperCase().equals(firstLetterLastName.toUpperCase())) {
                if (currentContributors != null) {
                    contributorsByLastName.add(currentContributors);
                }
                currentContributors = new ArrayList();
                firstLetterLastName = currentFirstLetterLastName; 
            }
            currentContributors.add(contr);
        }
        if (nullLastName.size() > 0) {
            contributorsByLastName.add(nullLastName);
            setHasInstitutions(true); 
        }
    }
    
    @SuppressWarnings("unchecked")
    public int getPageSize() {
        return ((List) contributorsByLastName.get(getCurrentPage())).size();
    }

    public void setPageSize(int nPageSize) {}

    public int getCurrentPage() {
        return currentPageIndex;
    }

    public void setCurrentPage(int nPage) {
        currentPageIndex = nPage;
    }
    
    public int getPageCount() {
        return contributorsByLastName.size();
    }
    
    @SuppressWarnings("unchecked")
    public Object[] getSelectableLetters() {
        ArrayList strings = new ArrayList();
        for (Iterator iter = contributorsByLastName.iterator(); iter.hasNext();) {
            List contributors = (List) iter.next();
            String lastName = ((Contributor) contributors.get(0)).getLastName();
            if (lastName != null) {
                strings.add(lastName.substring(0, 1).toUpperCase());
            }
        }
        if (hasInstitutions) {
            strings.add("Institutions");
        }
        // check to see if the last element in the last is the nulls
        return strings.toArray();
    }
    
    @SuppressWarnings("unchecked")
    public Iterator getCurrentPageRows() {
        return ((List) contributorsByLastName.get(getCurrentPage())).iterator();
    }

    /**
     * @return Returns the hasInstitutions.
     */
    public boolean getHasInstitutions() {
        return hasInstitutions;
    }

    /**
     * @param hasInstitutions The hasInstitutions to set.
     */
    public void setHasInstitutions(boolean hasInstitutions) {
        this.hasInstitutions = hasInstitutions;
    }
}
