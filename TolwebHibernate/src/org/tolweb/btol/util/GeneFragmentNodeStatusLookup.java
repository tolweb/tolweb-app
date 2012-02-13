package org.tolweb.btol.util;

import java.util.Hashtable;
import java.util.List;

import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.GeneFragmentNodeStatus;

/**
 * 
 * @author lenards
 */
public class GeneFragmentNodeStatusLookup {
	private Hashtable<String, GeneFragmentNodeStatus> statusLookupTable;

	public void setStatuses(List<GeneFragmentNodeStatus> statuses) {
		getStatusLookupTable().clear();
		for (GeneFragmentNodeStatus status : statuses) {
			putStatus(status);
		}
	}

	public GeneFragmentNodeStatus getStatusForNodeIdAndGeneFragment(Long nodeId, GeneFragment geneFrag) {
		return getStatusLookupTable().get(getLookupKeyForNodeIdAndGeneFragment(nodeId, geneFrag));
	}

	public void putStatus(GeneFragmentNodeStatus status) {
		getStatusLookupTable().put(getLookupKeyForStatus(status), status);
	}

	protected Hashtable<String, GeneFragmentNodeStatus> getStatusLookupTable() {
		if (statusLookupTable == null) {
			statusLookupTable = new Hashtable<String, GeneFragmentNodeStatus>();
		}
		return statusLookupTable;
	}
	protected String getLookupKeyForStatus(GeneFragmentNodeStatus status) {
		return getLookupKeyForNodeIdAndGeneFragment(status.getNodeId(), status.getGeneFragment());
	}
	protected String getLookupKeyForNodeIdAndGeneFragment(Long nodeId, GeneFragment geneFrag) {
		return nodeId + ":" + geneFrag.getId();		
	}
}
