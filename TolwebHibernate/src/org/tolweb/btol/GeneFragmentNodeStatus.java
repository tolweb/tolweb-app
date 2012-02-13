package org.tolweb.btol;

import org.tolweb.hibernate.PersistentObject;

/**
 * @hibernate.class table="GeneFragmentNodeStatuses"
 * @author lenards
 */
public class GeneFragmentNodeStatus extends PersistentObject {
	public static final int NONE_SELECTED = -1;
	public static final int BTOL_SOURCE = 0;
	public static final int DRM_SOURCE = 1;	
	public static final int GENBANK_SOURCE = 2;
	
	public static final int SEQUENCE_NOT_NEEDED = 0;
	public static final int NO_SEQUENCE = 1;
	public static final int HAVE_BRIGHT_PCR = 2;
	public static final int HAVE_PARTIAL_SEQUENCE = 3;
	public static final int HAVE_SEQUENCE = 4;

	/**
	 * 
	 */
	private static final long serialVersionUID = -287969844498507471L;
	
	private Long nodeId;
	private GeneFragment geneFrag;
	private int status;
	private int source;
	private String sourceDbId;
	private String statusNotes;
	
	/**
	 * Clears all information related to the status of this Gene Fragment / Node mapping.
	 * @author lenards
	 */
	public void clearAllStatusInformation() {
		setId(0L);		
		setNodeId(0L);
		setGeneFragment(null);
		setStatus(SEQUENCE_NOT_NEEDED);
		setSource(NONE_SELECTED);
		setSourceDbId(null);
	}
	
	/**
	 * @hibernate.many-to-one column="geneFragmentId" class="org.tolweb.btol.GeneFragment" cascade="none"
	 * @return
	 */
	public GeneFragment getGeneFragment() {
		return geneFrag;
	}
	public void setGeneFragment(GeneFragment gene) {
		this.geneFrag = gene;
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

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getStatusNotes() {
		return statusNotes;
	}

	public void setStatusNotes(String statusNotes) {
		this.statusNotes = statusNotes;
	}	
}
