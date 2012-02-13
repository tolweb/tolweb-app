package org.tolweb.btol;

import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @hibernate.class table="PCRBatches"
 * @author dmandel
 */
public class PCRBatch extends FlexibleDateObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -186835837497708868L;
	private PCRProtocol protocol;
	private Integer reactionVolume;
	private Primer forwardPrimer;
	private Primer reversePrimer;
	private SortedSet reactions;
	private Integer contributorId;
	private Contributor contributor;
	private String imageFilename1;
	private String imageFilename2;
	

	/**
	 * @hibernate.many-to-one column="protocolId" class="org.tolweb.btol.PCRProtocol" cascade="none"
	 * @return
	 */
	public PCRProtocol getProtocol() {
		return protocol;
	}
	public void setProtocol(PCRProtocol protocol) {
		this.protocol = protocol;
	}
	/**
	 * @hibernate.property 
	 * @return
	 */	
	public Integer getReactionVolume() {
		return reactionVolume;
	}
	public void setReactionVolume(Integer reactionVolume) {
		this.reactionVolume = reactionVolume;
	}
	/**
	 * @hibernate.many-to-one column="forwardPrimerId" class="org.tolweb.btol.Primer" cascade="none"
	 * @return
	 */	
	public Primer getForwardPrimer() {
		return forwardPrimer;
	}
	public void setForwardPrimer(Primer forwardPrimer) {
		this.forwardPrimer = forwardPrimer;
	}
	/**
	 * @hibernate.many-to-one column="reversePrimerId" class="org.tolweb.btol.Primer" cascade="none"
	 * @return
	 */		
	public Primer getReversePrimer() {
		return reversePrimer;
	}
	public void setReversePrimer(Primer reversePrimer) {
		this.reversePrimer = reversePrimer;
	}
    /**
     * @hibernate.set table="ReactionsToBatches" order-by="reactionId asc" sort="natural" cascade="all"
     * @hibernate.collection-key column="batchId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.PCRReaction" column="reactionId"
     */	
	public SortedSet getReactions() {
		if (reactions == null) {
			reactions = new TreeSet();
		}
		return reactions;
	}
	public void setReactions(SortedSet reactions) {
		this.reactions = reactions;
	}
	public void addToReactions(PCRReaction reaction) {
		getReactions().add(reaction);
	}
	/**
	 * @hibernate.property 
	 * @return
	 */		
	public Integer getContributorId() {
		return contributorId;
	}
	public void setContributorId(Integer contributorId) {
		this.contributorId = contributorId;
	}
	public Contributor getContributor() {
		return contributor;
	}
	public void setContributor(Contributor contributor) {
		this.contributor = contributor;
		if (contributor != null) {
			setContributorId(contributor.getId());
		}
	}
	/**
	 * @hibernate.property 
	 * @return
	 */	
	public String getImageFilename1() {
		return imageFilename1;
	}
	public void setImageFilename1(String imageFilename1) {
		this.imageFilename1 = imageFilename1;
	}
	public boolean getHasImage1() {
		return StringUtils.notEmpty(getImageFilename1());
	}
	/**
	 * @hibernate.property 
	 * @return
	 */	
	public String getImageFilename2() {
		return imageFilename2;
	}
	public void setImageFilename2(String imageFilename2) {
		this.imageFilename2 = imageFilename2;
	}
	public boolean getHasImage2() {
		return StringUtils.notEmpty(getImageFilename2());
	}

}
