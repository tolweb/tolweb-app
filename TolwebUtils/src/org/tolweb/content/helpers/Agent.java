package org.tolweb.content.helpers;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * A representation of the agent entity associated with a 
 * data object (e.g. content artifacts). 
 * 
 * The specified EOL schema defines the following in regard to agent: 
 * 
 *		Examples of an agent include a person, organization, and software agent which has
 *  	contributed to the creation of the resource. Strongly recommended to include the role the
 *  	agent played in the creation of the resource. Agents should appear in the order they are to
 *  	be displayed. For example, the primary author should be listed first with all subsequent
 *  	authors following.
 *  
 *  	<ul>
 *  		<li>roles: <ul>
 *  			<li>animator</li>
 *  			<li>author</li>
 *  			<li>compiler</li>
 *  			<li>composer</li>
 *  			<li>creator</li>
 *  			<li>director</li>
 *  			<li>editor</li>
 *  			<li>illustrator</li>
 *  			<li>photographer</li>
 *  			<li>project</li>
 *  			<li>publisher</li>
 *  			<li>recorder</li>
 *  			<li>source</li>    
 *  			</ul>
 *  		</li>
 *  	</ul>
 *
 *		Of the roles defined by the EOL schema, we are only supporting "author" and "creator" at the 
 * time of implementation. 
 * 
 * The XML element is defined by EOL as the following: 
 *  
 *  <agent homepage="" logoURL="" role="">{name}</agent>
 * 
 * 		We will not be using the optional logoURL attribute of element. 
 * 
 * @author lenards
 *
 */
public class Agent {
	private String name; 
	private String homepage;
	private AgentRole role;
	
	/**
	 * Constructs an agent representation for the argument mediaFile. 
	 * @param mediaFile
	 */
	public Agent(NodeImage mediaFile) {
		initializeFields(mediaFile);
		// AJL (2010/Feb/23) - this ws changed at the request of EOL
		// role = AgentRole.Creator;
		role = AgentRole.Author;
	}

	/**
	 * Determine the name and homepage based on the following rules: 
	 *
	 * Specified logic for determining the name & homepage are as follows
	 * ------------------------------------------------------------------
	 * if there is an entry in Images.creator: 
	 * 		use that here. In this case, there is no homepage entry. 
	 * else (Images.creator is empty):
	 * 		check if there is an entry in Images.copyright_contributor_id: 
	 * 			if so, name = Contributors.first_name + Contributors.last_name
	 * 			and homepage =  Contributors.homepage
	 * 		else (there is no entry for Images.copyright_contributor_id):
	 * 			use Images.copyright_owner. 
	 * 
	 * If all of these are empty, the image is most likely in the public domain.
	 *
	 * @param mediaFile the containing copyright/agent information we will mine
	 */
	private void initializeFields(NodeImage mediaFile) {
		if (StringUtils.notEmpty(mediaFile.getCreator())) {
			name = mediaFile.getCreator();
		} else if (mediaFile.getCopyrightOwnerContributor() != null) {
			Contributor c = mediaFile.getCopyrightOwnerContributor();
			if (StringUtils.notEmpty(c.getLastName())) {
				name = c.getFirstName() + " " + c.getLastName();
			} else { // then we think this is an institution, not a person
				name = c.getInstitution();
			}
			homepage = c.getHomepage();
		} else if (StringUtils.notEmpty(mediaFile.getCopyrightOwner())) {
			name = mediaFile.getCopyrightOwner();
			if (StringUtils.notEmpty(mediaFile.getCopyrightUrl())) {
				homepage = mediaFile.getCopyrightUrl();
			} else {
				homepage = "";
			}
		} else {
			System.out.println("-> the else case happened!");
		}
	}
	
	/**
	 * Gets the name of the agent responsible for the creation of the media. 
	 * 
	 * This may be the name, first & last name of the contributor, 
	 * a third party, or an institution.
	 *   
	 * @return a string representing the creator name
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/** 
	 * Gets the home page URL for the agent responsible for the creation 
	 * of the media. 
	 * 
	 * @return a string representing a URL for the creator 
	 */
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	
	/**
	 * Gets the defined role of the agent. 
	 * 
	 * For text & media, this is Author.  In previous versions of the web 
	 * services, the role of the agent was identified as Creator. 
	 * 
	 * @return an enumerated type that represents the role of the agent. 
	 */
	public AgentRole getRole() {
		return role;
	}
	public void setRole(AgentRole role) {
		this.role = role;
	}
}
