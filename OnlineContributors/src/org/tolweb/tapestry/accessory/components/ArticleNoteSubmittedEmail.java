package org.tolweb.tapestry.accessory.components;

import java.util.Collection;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ArticleNoteSubmittedEmail extends BaseComponent implements BaseInjectable {
	@Parameter(required = true)
	public abstract Collection<MappedAccessoryPage> getSubmittedArticleNotes();
	@Parameter(required = true)
	public abstract Contributor getContributor();
	public abstract MappedAccessoryPage getCurrentArticleNote();
	
	public String getCurrentWorkingUrl() {
		return getUrlBuilder().getWorkingURLForObject(getCurrentArticleNote());
	}
}
