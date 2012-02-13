package org.tolweb.content.preparers;


import java.util.Iterator;
import java.util.Set;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;

import org.tolweb.content.helpers.DaoBundle;
import org.tolweb.content.helpers.PageContentAttributes;
import org.tolweb.content.helpers.PageContentElements;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class ContributorCoPreparer extends AbstractCoPreparer {
	
	@SuppressWarnings("unchecked")
	public void setContentSource(Object contributorList, DaoBundle daos, Element doc) {
		setContributors((Set)contributorList);
		setDaoBundle(daos);
		setParentElement(doc);
		setPreparedElement(doc);
		setPreparerNamespace(ContentPreparer.NS);
	}
	
	@SuppressWarnings("unchecked")
	public void processContent() {
		Set contributors = getContributors();
		for (Iterator itr = contributors.iterator(); itr.hasNext(); ) {
			Contributor contr = (Contributor)itr.next();
			if (contr != null) {
				Element contrEl = new Element(PageContentElements.CONTRIBUTOR, getPreparerNamespace());
				contrEl.addAttribute(new Attribute(PageContentAttributes.ID, safeToString(contr.getId())));
				contrEl.addAttribute(new Attribute(PageContentAttributes.LAST_NAME, contr.getLastName()));
				contrEl.addAttribute(new Attribute(PageContentAttributes.FIRST_NAME, contr.getFirstName()));
				
				if (contr.getShowEmail()) {
					contrEl.addAttribute(new Attribute(PageContentAttributes.EMAIL, contr.getEmail()));
				}
				
				contrEl.addAttribute(new Attribute(PageContentAttributes.HOMEPAGE, getSafeString(contr.getHomepage())));
				contrEl.addAttribute(new Attribute(PageContentAttributes.TOL_PROFILE, 
						"http://tolweb.org/onlinecontributors/app?page=ContributorDetailPage&service=external&sp=" + contr.getId()));
				
				// only set the attribute if there is, in fact, a portrait to show
				String portraitFilename = contr.getImageFilename();
				if (StringUtils.notEmpty(portraitFilename)) {
					contrEl.addAttribute(new Attribute(PageContentAttributes.PORTRAIT, "http://tolweb.org/contributorsimages/" + contr.getImageFilename()));
				}
				
				Element institutionEl = new Element(PageContentElements.INSTITUTION, getPreparerNamespace());
				institutionEl.appendChild(new Text(contr.getInstitution()));
				contrEl.appendChild(institutionEl);
				
				if (contr.getShowAddress() && contr.getAddress() != null) {
					Element addressEl = new Element(PageContentElements.ADDRESS, getPreparerNamespace());
					String cleanAddress = contr.getAddress().replace("<br/>", ", ");
					cleanAddress = cleanAddress.replace("<br />", ", ");
					cleanAddress = cleanAddress.replace("<br>", ", ");
					addressEl.appendChild(new Text(cleanAddress));
					contrEl.appendChild(addressEl);
				}
				
				getParentElement().appendChild(contrEl);
			}
		}
	}
}
