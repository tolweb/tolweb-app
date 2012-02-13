package org.tolweb.tapestry.xml.taxaimport.beans;

import java.util.ArrayList;
import java.util.List;

public class XTNode {
/*
  <node extinct="false" id="72254" confidence="0" phylesis="monophyletic" leaf="true" has-page="true" ancestor-with-page="72231" 
  	italicize-name="true" incomplete-subgroups="false" show-authority="true" show-authority-containing="false" is-new-combination="false" combination-date="null" child-count="0">
    <name>Heliconius pachinus</name>
    <description></description>
    <authority>Salvin</authority>
    <name-comment></name-comment>
    <combination-author></combination-author>
    <auth-date>1871</auth-date>
    <rank></rank>
    <geographical-location></geographical-location>
    <othernames>
      <othername is-important="true" is-preferred="false" sequence="0" date="1871" italicize-name="1">
        <name>Heliconius cydno pachinus></name>
        <authority>Salvin</authority>
        <comments>[Lamas (2004) views &lt;em&gt;pachinus&lt;/em&gt; as a subspecies of &lt;em&gt;H. cydno&lt;/em&gt;]</comments>
      </othername>
    </othernames>
    <nodes />
    <source-information>
	<source-id>some unique identifier</source-id>
	<source-key>some source-specified value</source-key>
    </source-information>
  </node>
 */
	// xml attributes of XTNode
	private Boolean extinct;
	private Long id; 
	private String confidence;
	private String phylesis;
	private Boolean leaf;
	private Boolean hasPage;
	private Long ancestorWithPage;
	private Boolean italicizeName;
	private Boolean incompleteSubgroups;
	private Boolean showAuthority;
	private Boolean showAuthorityContaining;
	private Boolean isNewCombination;
	private String combinationDate;
	private Integer childCount;
	// child elements of XTNode
	private String name;
	private String description;
	private String authority;
	private String nameComment;
	private String combinationAuthor;
	private String authDate;
	private String rank; 
	private XTGeographicDistribution geographicDistribution;
	private XTSourceInformation sourceInformation;
	private List<XTOthername> otherNames;
	private List<XTNode> nodes;
	
	public XTNode() {
		otherNames = new ArrayList<XTOthername>();
		nodes = new ArrayList<XTNode>();
	}
	
	public String toString() {
		return getName() + "(" + getId() + ")";
	}
	
	public void addNode(XTNode child) {
		nodes.add(child);
	}
	
	public void addOthername(XTOthername otherName) {
		otherNames.add(otherName);
	}

	/**
	 * @return the extinct
	 */
	public Boolean getExtinct() {
		return extinct;
	}

	/**
	 * @param extinct the extinct to set
	 */
	public void setExtinct(Boolean extinct) {
		this.extinct = extinct;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the confidence
	 */
	public String getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	/**
	 * @return the phylesis
	 */
	public String getPhylesis() {
		return phylesis;
	}

	/**
	 * @param phylesis the phylesis to set
	 */
	public void setPhylesis(String phylesis) {
		this.phylesis = phylesis;
	}

	/**
	 * @return the leaf
	 */
	public Boolean getLeaf() {
		return leaf;
	}

	/**
	 * @param leaf the leaf to set
	 */
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	/**
	 * @return the hasPage
	 */
	public Boolean getHasPage() {
		return hasPage;
	}

	/**
	 * @param hasPage the hasPage to set
	 */
	public void setHasPage(Boolean hasPage) {
		this.hasPage = hasPage;
	}

	/**
	 * @return the ancestorWithPage
	 */
	public Long getAncestorWithPage() {
		return ancestorWithPage;
	}

	/**
	 * @param ancestorWithPage the ancestorWithPage to set
	 */
	public void setAncestorWithPage(Long ancestorWithPage) {
		this.ancestorWithPage = ancestorWithPage;
	}

	/**
	 * @return the italicizeName
	 */
	public Boolean getItalicizeName() {
		return italicizeName;
	}

	/**
	 * @param italicizeName the italicizeName to set
	 */
	public void setItalicizeName(Boolean italicizeName) {
		this.italicizeName = italicizeName;
	}

	/**
	 * @return the incompleteSubgroups
	 */
	public Boolean getIncompleteSubgroups() {
		return incompleteSubgroups;
	}

	/**
	 * @param incompleteSubgroups the incompleteSubgroups to set
	 */
	public void setIncompleteSubgroups(Boolean incompleteSubgroups) {
		this.incompleteSubgroups = incompleteSubgroups;
	}

	/**
	 * @return the showAuthority
	 */
	public Boolean getShowAuthority() {
		return showAuthority;
	}

	/**
	 * @param showAuthority the showAuthority to set
	 */
	public void setShowAuthority(Boolean showAuthority) {
		this.showAuthority = showAuthority;
	}

	/**
	 * @return the showAuthorityContaining
	 */
	public Boolean getShowAuthorityContaining() {
		return showAuthorityContaining;
	}

	/**
	 * @param showAuthorityContaining the showAuthorityContaining to set
	 */
	public void setShowAuthorityContaining(Boolean showAuthorityContaining) {
		this.showAuthorityContaining = showAuthorityContaining;
	}

	/**
	 * @return the isNewCombination
	 */
	public Boolean getIsNewCombination() {
		return isNewCombination;
	}

	/**
	 * @param isNewCombination the isNewCombination to set
	 */
	public void setIsNewCombination(Boolean isNewCombination) {
		this.isNewCombination = isNewCombination;
	}

	/**
	 * @return the combinationDate
	 */
	public String getCombinationDate() {
		return combinationDate;
	}

	/**
	 * @param combinationDate the combinationDate to set
	 */
	public void setCombinationDate(String combinationDate) {
		this.combinationDate = combinationDate;
	}

	/**
	 * @return the childCount
	 */
	public Integer getChildCount() {
		return childCount;
	}

	/**
	 * @param childCount the childCount to set
	 */
	public void setChildCount(Integer childCount) {
		this.childCount = childCount;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the authority
	 */
	public String getAuthority() {
		return authority;
	}

	/**
	 * @param authority the authority to set
	 */
	public void setAuthority(String authority) {
		this.authority = authority;
	}

	/**
	 * @return the nameComment
	 */
	public String getNameComment() {
		return nameComment;
	}

	/**
	 * @param nameComment the nameComment to set
	 */
	public void setNameComment(String nameComment) {
		this.nameComment = nameComment;
	}

	/**
	 * @return the combinationAuthor
	 */
	public String getCombinationAuthor() {
		return combinationAuthor;
	}

	/**
	 * @param combinationAuthor the combinationAuthor to set
	 */
	public void setCombinationAuthor(String combinationAuthor) {
		this.combinationAuthor = combinationAuthor;
	}

	/**
	 * @return the authDate
	 */
	public String getAuthDate() {
		return authDate;
	}

	/**
	 * @param authDate the authDate to set
	 */
	public void setAuthDate(String authDate) {
		this.authDate = authDate;
	}

	/**
	 * @return the rank
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}

	/**
	 * @return the geographicalDistribution
	 */
	public XTGeographicDistribution getGeographicDistribution() {
		return geographicDistribution;
	}

	/**
	 * @param geographicalDistribution the geographicalDistribution to set
	 */
	public void setGeographicDistribution(XTGeographicDistribution geographicDistribution) {
		this.geographicDistribution = geographicDistribution;
	}

	/**
	 * @return the sourceInformation
	 */
	public XTSourceInformation getSourceInformation() {
		return sourceInformation;
	}

	/**
	 * @param sourceInformation the sourceInformation to set
	 */
	public void setSourceInformation(XTSourceInformation sourceInformation) {
		this.sourceInformation = sourceInformation;
	}

	/**
	 * @return the otherNames
	 */
	public List<XTOthername> getOthernames() {
		return otherNames;
	}

	/**
	 * @param otherNames the otherNames to set
	 */
	public void setOthernames(List<XTOthername> otherNames) {
		this.otherNames = otherNames;
	}

	/**
	 * @return the nodes
	 */
	public List<XTNode> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(List<XTNode> nodes) {
		this.nodes = nodes;
	}
}
