package org.tolweb.btol;

import org.tolweb.hibernate.PersistentObject;

/**
 * @hibernate.class table="GeneNodeStatuses"
 * @deprecated As of July 2007, replaced by GeneFragmentNodeStatus 
 * @author dmandel
 */
public class GeneNodeStatus extends PersistentObject {
	public static final int NONE_SELECTED = -1;
	public static final int BTOL_SOURCE = 0;
	public static final int DRM_SOURCE = 1;	
	public static final int GENBANK_SOURCE = 2;
	
	public static final int NO_SEQUENCE = 0;
	public static final int HAVE_BRIGHT_PCR = 1;
	public static final int HAVE_PARTIAL_SEQUENCE = 2;
	public static final int HAVE_SEQUENCE = 3;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3649200105380238701L;
	
	private Long nodeId;
	private Gene gene;
	private int status;
	private int source;
	private String sourceDbId;

	
	/**
	 * @hibernate.many-to-one column="geneId" class="org.tolweb.btol.Gene" cascade="none"
	 * @return
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
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getSourceDbId() {
		return sourceDbId;
	}
	public void setSourceDbId(String sourceDbId) {
		this.sourceDbId = sourceDbId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
