package org.tolweb.tapestry.treehouse.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.tapestry.injections.TreehouseInjectable;

public interface BetaTreehouseInjectable extends TreehouseInjectable {
	@InjectObject("spring:betaAccessoryPageDAO")
	public AccessoryPageDAO getBetaAccPageDAO();
	@InjectObject("spring:betaContributorDAO")
	public ContributorDAO getBetaContributorDAO();
}
