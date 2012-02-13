package org.tolweb.btol;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @hibernate.class table="Primers"
 * @author dmandel
 *
 */
public class Primer extends FlexibleDateObject implements Serializable, Defunct {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2549861545343602078L;
	private Gene gene;
	private String sequence;
	private String code;
	private boolean isForward;
	private Date creationDate;
	private Integer developerId;
	private Contributor developer;
	private String reference;
	private Set synonyms = new TreeSet();
	private boolean privateFlag;
	private Contributor createdContributor;
	private Integer createdContributorId;
	private boolean defunct;
	//private String synonyms;
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Contributor getDeveloper() {
		return developer;
	}
	public void setDeveloper(Contributor developer) {
		this.developer = developer;
		setDeveloperId(getSafeId(developer));
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Integer getDeveloperId() {
		return developerId;
	}
	public void setDeveloperId(Integer developerId) {
		this.developerId = developerId;
	}	
	/**
	 * @hibernate.many-to-one column="geneId" class="org.tolweb.btol.Gene" cascade="none"
	 * @return Returns the gene
	 */		
	public Gene getGene() {
		return gene;
	}
	public void setGene(Gene gene) {
		this.gene = gene;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public boolean getIsForward() {
		return isForward;
	}
	public void setIsForward(boolean isForward) {
		this.isForward = isForward;
	}
    public String getDirectionString() {
        if (getIsForward()) {
            return "F";
        } else {
            return "R";
        }        
    }
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
    public String getReverseComplement() {
        String reverseComplement = "";        
        if (StringUtils.notEmpty(getSequence())) {
            String sequence = getSequence();
            for(int i = sequence.length()-1; i >= 0 ;i--) {
                char nucleotide = sequence.charAt(i);
                reverseComplement += complementChar(nucleotide);            
            }
            return reverseComplement;
        }
        return reverseComplement;
    }
    public static char complementChar(char c){
        if (c == 'a')
            return 't';
        else if (c == 'A')
            return 'T';
        else if (c == 'c')
            return 'g';
        else if (c == 'C')
            return 'G';
        else if (c == 'g')
            return 'c';
        else if (c == 'G')
            return 'C';
        else if (c == 't')
            return 'a';
        else if (c == 'T')
            return 'A';

        else if (c == 'v')
            return 'b';
        else if (c == 'V')
            return 'B';
        else if (c == 'd')
            return 'h';
        else if (c == 'D')
            return 'H';
        else if (c == 'h')
            return 'd';
        else if (c == 'H')
            return 'D';
        else if (c == 'b')
            return 'v';
        else if (c == 'B')
            return 'V';
        else if (c == 'm')
            return 'k';
        else if (c == 'M')
            return 'K';
        else if (c == 'r')
            return 'y';
        else if (c == 'R')
            return 'Y';
        else if (c == 'y')
            return 'r';
        else if (c == 'Y')
            return 'R';
        else if (c == 'k')
            return 'm';
        else if (c == 'K')
            return 'M';
        else
            return c;
    }
    public String getSequenceString() {
    	String sequence = getSequence();
    	sequence += " (" + sequence.length() + ")";
    	return sequence;    	
    }
    /**
	 * @hibernate.set table="PrimerSynonyms" lazy="false" sort="natural" cascade="all" order-by="synonym asc"
	 * @hibernate.collection-element type="string" column="synonym"
	 * @hibernate.collection-key column="primerId"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 */    
	public Set getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(Set synonyms) {
		this.synonyms = synonyms;
	}
	public String getSynonymsString() {
		return StringUtils.returnCommaJoinedString(getSynonyms());
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getPrivateFlag() {
		return privateFlag;
	}
	public void setPrivateFlag(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}
	public Contributor getCreatedContributor() {
		return createdContributor;
	}
	public void setCreatedContributor(Contributor createdContributor) {
		if (createdContributor != null) {
			setCreatedContributorId(createdContributor.getId());
		}
		this.createdContributor = createdContributor;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Integer getCreatedContributorId() {
		return createdContributorId;
	}
	public void setCreatedContributorId(Integer createdContributorId) {
		this.createdContributorId = createdContributorId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getDefunct() {
		return defunct;
	}
	public void setDefunct(boolean defunct) {
		this.defunct = defunct;
	}
}
