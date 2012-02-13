package org.tolweb.btol.util;

import java.util.Hashtable;
import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneNodeStatus;

/**
 * @deprecated As of July 2007, replaced by GeneFragmentNodeStatusLookup 
 * @author dmandel
 */
public class GeneNodeStatusLookup {

	private Hashtable<String, GeneNodeStatus> statusLookupTable;

	public void setStatuses(List<GeneNodeStatus> statuses) {
		getStatusLookupTable().clear();
		for (GeneNodeStatus status : statuses) {
			putStatus(status);
		}
	}

	public GeneNodeStatus getStatusForNodeIdAndGene(Long nodeId, Gene gene) {
		return getStatusLookupTable().get(getLookupKeyForNodeIdAndGene(nodeId, gene));
	}

	public void putStatus(GeneNodeStatus status) {
		getStatusLookupTable().put(getLookupKeyForStatus(status), status);
	}

	protected Hashtable<String, GeneNodeStatus> getStatusLookupTable() {
		if (statusLookupTable == null) {
			statusLookupTable = new Hashtable<String, GeneNodeStatus>();
		}
		return statusLookupTable;
	}
	protected String getLookupKeyForStatus(GeneNodeStatus status) {
		return getLookupKeyForNodeIdAndGene(status.getNodeId(), status.getGene());
	}
	protected String getLookupKeyForNodeIdAndGene(Long nodeId, Gene gene) {
		return nodeId + ":" + gene.getId();		
	}
}
