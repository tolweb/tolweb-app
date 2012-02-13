package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.components.Insert;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class NodeIdSearch extends BaseComponent implements NodeInjectable {
	
	@Persist("session")
	public abstract boolean getRootGroupSelected();
	public abstract void setRootGroupSelected(boolean value);

	@Persist("session")
	public abstract MappedNode getRootGroup();	
	public abstract void setRootGroup(MappedNode node);

	public abstract String getRootGroupName();
	public abstract void setRootGroupName(String value);
	
	@Bean
	public abstract ValidationDelegate getDelegate();

	@Parameter(name="update", required=false)
	public abstract Insert getUpdate();
	
	public void chooseRootGroup() {
		setRootGroup(null);
		setRootGroupSelected(true);
	}
	
	public void findAndSelectRootGroup() {
		MappedNode rootNode = getWorkingNodeDAO().getFirstNodeExactlyNamed(getRootGroupName());
		if (rootNode != null) {
			setRootGroupSelected(false);
			setRootGroup(rootNode);
		} else {
			getDelegate().record("No node named " + getRootGroupName(), ValidationConstraint.CONSISTENCY);
		}
	}	
}
