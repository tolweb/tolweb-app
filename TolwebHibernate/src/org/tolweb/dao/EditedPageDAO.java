package org.tolweb.dao;

import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;

public interface EditedPageDAO {
	public void addEditedPageForContributor(Long mappedPageId, Contributor contr);
    public void addEditedPageForContributor(MappedPage page, Contributor contr);
    public void addEditedPageForContributor(MappedAccessoryPage page, Contributor contr);    
    public boolean getContributorHasEditedPage(MappedPage page, Contributor contr);
    public boolean getContributorHasEditedPage(MappedAccessoryPage page, Contributor contr);    
    public List getEditedPageIdsForContributor(Contributor contr, int pageType);
    public void deleteEditedPagesWithIds(Set pageIds, int pageType);
}
