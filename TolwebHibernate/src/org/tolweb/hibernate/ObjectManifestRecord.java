package org.tolweb.hibernate;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.treegrow.main.StringUtils;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

/**
 * @author lenards
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * @hibernate.class table="ObjectManifestLog"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class ObjectManifestRecord extends AbstractObjectManifestRecord implements Serializable {
	private static final long serialVersionUID = -9138919848294221545L;
	private Long basalNodeId; 
	
	public ObjectManifestRecord() {

	}

	private void initializeBasalNodeId() {
    	if (StringUtils.notEmpty(getManifest())) {
    		Pattern pattern = Pattern.compile("basal-node-id=\"([0-9]*)\"", Pattern.CASE_INSENSITIVE);
    		Matcher matcher = pattern.matcher(getManifest());
    		if (matcher.find()) {
    			String match = matcher.group(1);
    			basalNodeId = Long.parseLong(match);
    		}
        }		
	}
	
	/**
	 * @hibernate.property  
	 * @return the basalNodeId
	 */
	public Long getBasalNodeId() {
		if (basalNodeId == null) {
			initializeBasalNodeId();
		}
		return basalNodeId;
	}

	/**
	 * @param basalNodeId the basalNodeId to set
	 */
	public void setBasalNodeId(Long basalNodeId) {
		this.basalNodeId = basalNodeId;
	}
}
