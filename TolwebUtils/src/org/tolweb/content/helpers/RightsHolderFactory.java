package org.tolweb.content.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.PageContributor;

/**
 * A collection of static methods to help determine the rights holders of 
 * Tree of Life Web Project content (media, pages, and authors).  
 * 
 * @author lenards
 *
 */
public class RightsHolderFactory {
	/**
	 * A representation of the public domain as a rights holder instance. 
	 */
	private static final RightsHolder PUBLIC_DOMAIN = new RightsHolder("public domain", "", "");
	
	/**
	 * Initialize the rights holder object using the contributor details
	 * 
	 * @param c the contributor instance to use when setting values
	 * @param holder the rights holder to set values on 
	 */
	private static void initializeFieldsFor(Contributor c, RightsHolder holder) {
		holder.setName(c.getFirstName() + " " + c.getLastName()); 
		holder.setHomepage(c.getHomepage());
		holder.setContributorProfile(c.getProfileUrl());
		if (!c.dontShowEmail()) {
			holder.setEmail(c.getEmail());
		}
	}
	
	/**
	 * Determines the rights holder object for a media file. 
	 * 
	 * @param mediaFile the media file for which the rights holder is needed. 
	 * 
	 * @return the rights holder for the media file
	 */
	public static RightsHolder createRightsHolderFor(NodeImage mediaFile) {
		RightsHolder holder = new RightsHolder();
		if (mediaFile.getCopyrightOwnerContributor() != null) {
			initializeFieldsFor(mediaFile.getCopyrightOwnerContributor(), holder);
			return holder;
		} else if (StringUtils.notEmpty(mediaFile.getCopyrightOwner())) {
			holder.setName(mediaFile.getCopyrightOwner());
			holder.setHomepage(mediaFile.getCopyrightUrl());
			holder.setEmail(mediaFile.getCopyrightEmail());
			holder.setNonContributor(true);
			return holder;
		} else if (mediaFile.inPublicDomain()) {
			return PUBLIC_DOMAIN;
		}
		return null;
	}
	
	/**
	 * Determines the rights holder object for a page contributor.
	 * 
	 * @param author the page contributor for which the rights holder is needed.
	 *  
	 * @return the rights holder for the page contributor
	 */
	public static RightsHolder createRightsHolderFor(PageContributor author) {
		if (author.getIsCopyOwner()) {
			RightsHolder holder = new RightsHolder();
			initializeFieldsFor(author.getContributor(), holder);
			return holder;
		}
		return null;
	}
	
	/**
	 * Determines a collection rights holder objects for a mapped page.
	 * 
	 * @param mpage the mapped page for which the rights holder is needed.
	 * 
	 * @return a map representing the rights holders for a mapped page. The 
	 * map defines a mapping from rights holder name to rights 
	 * holder object. 
	 */
	public static Map<String, RightsHolder> createRightsHolderFor(MappedPage mpage) {
		Map<String, RightsHolder> holders = new HashMap<String, RightsHolder>();
		SortedSet contributors = mpage.getContributors();
		if (contributors != null && !contributors.isEmpty()) {
			for (Iterator itr = contributors.iterator(); itr.hasNext(); ) {
				PageContributor pageContr = (PageContributor) itr.next();
				if (pageContr.getIsCopyOwner()) {
					RightsHolder h = createRightsHolderFor(pageContr);
					if (h != null) {
						holders.put(h.getName(), h);
					}
				}
			}			
		}
		
		if (StringUtils.notEmpty(mpage.getCopyrightHolder())) {
			String pageCopyright = mpage.getCopyrightHolder() + " " + mpage.getCopyrightDate();
			RightsHolder organizationOrOther = new RightsHolder(pageCopyright, "", "");
			holders.put(organizationOrOther.getName(), organizationOrOther);
		}
		return holders;
	}
}
