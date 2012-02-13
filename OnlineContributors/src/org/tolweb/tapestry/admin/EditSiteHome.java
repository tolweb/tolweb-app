package org.tolweb.tapestry.admin;

import java.util.Date;

import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.hibernate.FeatureGroup;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.FeatureGroupCategory;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NewsInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class EditSiteHome extends BasePage implements PageBeginRenderListener, MiscInjectable, NodeInjectable, NewsInjectable {
	private static StringPropertySelectionModel CATEGORY_MODEL;
	
	public abstract String getCategorySelection();
	
	public abstract void setCategorySelection(String cat);
	
	public abstract String getFeatureText();
	
	public abstract void setFeatureText(String text);
	
	public abstract String getDescription();
	
	public abstract void setDescription(String text);
	
	public abstract int getImageId();
	
	public abstract void setImageId(int imageId);

	public abstract void setRootGroupSelected(boolean value);
	
	@Persist("session")
	public abstract boolean getRootGroupSelected();
	
	public abstract void setRootGroup(MappedNode node);
	
	@Persist("session")
	public abstract MappedNode getRootGroup();
	
	public abstract String getRootGroupName();
	
	public abstract void setRootGroupName(String value);
	
	@Bean
	public abstract ValidationDelegate getDelegate();	
	
	public StringPropertySelectionModel getCategoryModel() {
		return CATEGORY_MODEL;
	}
	
	public void pageBeginRender(PageEvent event) {
		CATEGORY_MODEL = getPropertySelectionFactory().createModelFromList(
				FeatureGroupCategory.STRING_VALUES);
	}
	
	public void chooseRootGroup() {
		setRootGroup(null);
		setRootGroupSelected(true);
	}
	
	public void findAndSelectRootGroup() {
		MappedNode rootNode = getWorkingNodeDAO().getFirstNodeExactlyNamed(getRootGroupName());
		if (rootNode != null) {
			setRootGroupSelected(false);
			setRootGroup(rootNode);
			setDescription(rootNode.getDescription());
		} else {
			getDelegate().record("No node named " + getRootGroupName(), ValidationConstraint.CONSISTENCY);
		}
	}
	
	public void saveFeatureGroup() {
		FeatureGroup grp = new FeatureGroup(getRootGroup());
		grp.setGroupDescription(getDescription());
		grp.setFeatureText(getFeatureText());
		grp.setImageId(getImageId());
		grp.setCategory(FeatureGroupCategory.getValueBy(getCategorySelection()));
		grp.setCreatedDate(new Date());
		getFeatureGroupDAO().create(grp);
	}
}
