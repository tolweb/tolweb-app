package org.tolweb.btol.util;


import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneNodeStatus;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @deprecated As of July 2007, replaced by GeneFragmentNodeStatusesSource 
 * @author dmandel
 */
public class GeneNodeStatusesSource extends GeneNodeStatusLookup {
	public String getEditLinkTextForNodeIdAndGene(Long nodeId, Gene gene) {
		GeneNodeStatus status = getStatusForNodeIdAndGene(nodeId, gene);
		return getEditLinkTextForStatus(status);
	}
	public String getEditLinkTextForStatus(GeneNodeStatus status) {
		if (status != null) {
			if (status.getSource() == GeneNodeStatus.BTOL_SOURCE) {
				String value = status.getSourceDbId();
				if (StringUtils.isEmpty(value)) {
					return "&nbsp;";
				} else {
					return value;
				}
			} else if (status.getSource() == GeneNodeStatus.GENBANK_SOURCE) {
				return "GB";
			} else if (status.getSource() == GeneNodeStatus.DRM_SOURCE) {
				return "DRM";
			}
		}
		return "&nbsp;";		
	}
	public String getEditLinkClassForNodeIdAndGene(Long nodeId, Gene gene) {
		GeneNodeStatus status = getStatusForNodeIdAndGene(nodeId, gene);
		return getEditLinkClassForStatus(status);
	}
	public String getEditLinkClassForStatus(GeneNodeStatus status) {
		if (status != null) {
			int value = status.getStatus();
			switch (value) {
			// same color as no specimens
			case GeneNodeStatus.NO_SEQUENCE: return TaxaIndex.NEEDSSPECIMENS;
			// we can create a new class if this isn't sufficient, but it seems
			// to match the excel spreadsheet pretty well
			case GeneNodeStatus.HAVE_BRIGHT_PCR: return "tier1";
			case GeneNodeStatus.HAVE_PARTIAL_SEQUENCE: return TaxaIndex.SPECIMENSNOSUPPLIER;
			case GeneNodeStatus.HAVE_SEQUENCE: return TaxaIndex.HASSPECIMENS;
			}
			return TaxaIndex.INVISIBLE;
		} else {
			return TaxaIndex.INVISIBLE;
		}		
	}	
	/*public String getEditLinkClassForNodeIdGeneAndTier(Long nodeId, Gene gene, int tier) {
		GeneNodeStatus status = getStatusForNodeIdAndGene(nodeId, gene);
		return getEditLinkClassForStatusAndTier(status, tier);
	}
	public String getEditLinkClassForStatusAndTier(GeneNodeStatus status, int tier) {
		if (status != null) {
			int value = status.getStatus();
			if (value == GeneNodeStatus.NO_SEQUENCE) {
				if (tier == AdditionalFields.NO_TIER_SET) {
					return TaxaIndex.NOTIER;
				} else {
					// same color as no specimens
					return TaxaIndex.NEEDSSPECIMENS;
				}
			} else {
				switch (value) {
				// we can create a new class if this isn't sufficient, but it
				// seems
				// to match the excel spreadsheet pretty well
				case GeneNodeStatus.HAVE_BRIGHT_PCR:
					return "tier1";
				case GeneNodeStatus.HAVE_PARTIAL_SEQUENCE:
					return TaxaIndex.SPECIMENSNOSUPPLIER;
				case GeneNodeStatus.HAVE_SEQUENCE:
					return TaxaIndex.HASSPECIMENS;
				}
			}
			return TaxaIndex.INVISIBLE;
		} else {
			return TaxaIndex.INVISIBLE;
		}		
	}*/
	public String getEditLinkIdForNodeIdAndGene(Long nodeId, Gene gene) {
		return getLookupKeyForNodeIdAndGene(nodeId, gene);
	}
	public String getEditLinkIdForStatus(GeneNodeStatus status) {
		return getEditLinkIdForNodeIdAndGene(status.getNodeId(), status.getGene());
	}
}
