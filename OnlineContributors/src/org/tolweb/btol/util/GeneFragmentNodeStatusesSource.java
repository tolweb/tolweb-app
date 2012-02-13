package org.tolweb.btol.util;

import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.GeneFragmentNodeStatus;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.treegrow.main.StringUtils;

public class GeneFragmentNodeStatusesSource extends GeneFragmentNodeStatusLookup {
	public String getEditLinkTextForNodeIdAndGeneFragment(Long nodeId, GeneFragment geneFrag) {
		GeneFragmentNodeStatus status = getStatusForNodeIdAndGeneFragment(nodeId, geneFrag);
		return getEditLinkTextForStatus(status);
	}
	public String getEditLinkTextForStatus(GeneFragmentNodeStatus status) {
		if (status != null) {
			if (status.getSource() == GeneFragmentNodeStatus.BTOL_SOURCE) {
				String value = status.getSourceDbId();
				if (StringUtils.isEmpty(value)) {
					return "&nbsp;";
				} else {
					return value;
				}
			} else if (status.getSource() == GeneFragmentNodeStatus.GENBANK_SOURCE) {
				return "GB";
			} else if (status.getSource() == GeneFragmentNodeStatus.DRM_SOURCE) {
				return "DRM";
			}
		}
		return "&nbsp;";		
	}
	public String getEditLinkClassForNodeIdAndGeneFragment(Long nodeId, GeneFragment geneFrag) {
		GeneFragmentNodeStatus status = getStatusForNodeIdAndGeneFragment(nodeId, geneFrag);
		return getEditLinkClassForStatus(status);
	}
	public String getEditLinkClassForStatus(GeneFragmentNodeStatus status) {
		if (status != null) {
			int value = status.getStatus();
			switch (value) {
			// pale grey for no tier same as what we what for sequences that are not needed
			case GeneFragmentNodeStatus.SEQUENCE_NOT_NEEDED: return TaxaIndex.NOTIER;
			// same color as no specimens
			case GeneFragmentNodeStatus.NO_SEQUENCE: return TaxaIndex.NEEDSSPECIMENS;
			// we can create a new class if this isn't sufficient, but it seems
			// to match the excel spreadsheet pretty well
			case GeneFragmentNodeStatus.HAVE_BRIGHT_PCR: return "tier1";
			case GeneFragmentNodeStatus.HAVE_PARTIAL_SEQUENCE: return TaxaIndex.SPECIMENSNOSUPPLIER;
			case GeneFragmentNodeStatus.HAVE_SEQUENCE: return TaxaIndex.HASSPECIMENS;
			}
			return TaxaIndex.INVISIBLE;
		} else {
			return TaxaIndex.INVISIBLE;
		}		
	}	
	public String getEditLinkIdForNodeIdAndGeneFragment(Long nodeId, GeneFragment geneFrag) {
		return getLookupKeyForNodeIdAndGeneFragment(nodeId, geneFrag);
	}
	public String getEditLinkIdForStatus(GeneFragmentNodeStatus status) {
		return getEditLinkIdForNodeIdAndGeneFragment(status.getNodeId(), status.getGeneFragment());
	}
}
