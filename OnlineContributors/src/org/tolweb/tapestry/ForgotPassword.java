package org.tolweb.tapestry;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.html.BasePage;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ForgotPassword extends BasePage implements UserInjectable {
	public abstract String getEmail();
	
	@InjectPage("PasswordSent")
	public abstract PasswordSent getPasswordSentPage();
	public abstract void setError(String value);
	
	public IPage emailPassword() {
		Contributor contr = getContributorDAO().getContributorWithEmail(getEmail());
		if (contr == null) {
			setError("There is no registered contributor with that email address.");
			return null;
		} else {
			PasswordSent sentPage = getPasswordSentPage();
			sentPage.setContributor(contr);
			sentPage.setWrapperType(AbstractWrappablePage.DEFAULT_WRAPPER);
			return sentPage;
		}
	}
}
