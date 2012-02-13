package org.tolweb.btol.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.Project;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.treegrow.main.Contributor;

/**
 * Helper class for negotiating taxon sampling logic
 * @author dmandel
 *
 */
public class TaxonSamplingHelper { 
	
	public static String getHasEnoughSpecimensClass() {
		return "hasspecimens";
	}
	public static String getHasSomeSpecimensClass() {
		return "hasspecimenswrongpersonneedmore";
	}
	public static String getHasEnoughSpecimensWrongPersonClass() {
		return "hasspecimenswrongperson";
	}
	public static String getHasSomeSpecimensWrongPersonClass() {
		return "hasspecimenswrongpersonneedmore";
	}
	public static String getNoSpecimensClass() {
		return "needsspecimens";
	}
	
	
	/**
	 * Loops through the tiers of the nodes on this page and looks at
	 * various characteristics about descendants of the node
	 * @param node
	 * @param nodesToTiers a hashtable to keep track of nodes to tiers
	 * since this is passed down the ancestors
	 * @param nodesToMtGenome a hashtable to keep track of nodes to mtGenome
	 * since this is passed down the ancestors 
	 * @param nodesToAdultClass TODO
	 * @param nodesToDnaClass TODO
	 * @param contributor 
	 * @param project TODO
	 * @param nodesToLarvalCollectionStatus a hashtable to keep track of
	 * @return a list, first element is the min tier for a node and its
	 * descendants
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> calculateValuesForNodeAndDescendants(MappedNode node, Hashtable<MappedNode, Integer> nodesToTiers, 
			Hashtable<MappedNode, Integer> nodesToMtGenome, Hashtable<MappedNode, String> nodesToLarvalClass, 
			Hashtable<MappedNode, String> nodesToAdultClass, Hashtable<MappedNode, String> nodesToDnaClass, 
			Hashtable<MappedNode, String> nodesToMicroCTClass, DescendantCollectionInfo currentDescendantsInfo, 
			Hashtable<Long, List<MappedNode>> nodes, Contributor contributor, Project project) {
		if (currentDescendantsInfo == null) {
			currentDescendantsInfo = new DescendantCollectionInfo();
		}
		List<Integer> returnList = new ArrayList();
		int nodeTier = 10;
		int nodeMtGenome = 0;
		int nodeLarvalCollectionStatus = AdditionalFields.DONT_HAVE_ANY;
		int nodeAdultCollectionStatus = AdditionalFields.DONT_HAVE_ANY;
		int nodeDnaCollectionStatus = AdditionalFields.DONT_HAVE_ANY;
		int nodeMicroCT = 0;
		if (node.getAdditionalFields() != null) {
			nodeTier = node.getAdditionalFields().getTier();
			nodeMtGenome = node.getAdditionalFields().getMtGenomeState();
			nodeLarvalCollectionStatus = node.getAdditionalFields().getLarvalSpecimensState();
			nodeAdultCollectionStatus = node.getAdditionalFields().getAdultSpecimensState();
			nodeDnaCollectionStatus = node.getAdditionalFields().getDnaSpecimensState();
			nodeMicroCT = node.getAdditionalFields().getMicroCTState();
		}
		int minTier = nodeTier;
		int maxMtGenomeState = nodeMtGenome;
		List<MappedNode> children = nodes.get(node.getNodeId());
		if (children != null) {
			for (MappedNode child : children) {
				// create a separate descendantInfo object for each child node
				// so we don't confuse values, but also merge in the values for this
				// particular child into the parent's collection info
				DescendantCollectionInfo nextDescendantsInfo = new DescendantCollectionInfo();
				List<Integer> descendantResults = calculateValuesForNodeAndDescendants(child, nodesToTiers, 
						nodesToMtGenome, nodesToLarvalClass, 
						nodesToAdultClass, nodesToDnaClass, nodesToMicroCTClass, nextDescendantsInfo, nodes,
						contributor, project);
				int minChildTier = descendantResults.get(0);
				int maxChildMtGenomeState = descendantResults.get(1);
				// need to check this since no tier selected is actually a negative number...
				if (minChildTier >= 0 && (minChildTier < minTier || minTier == AdditionalFields.NO_TIER_SET)) {
					minTier = minChildTier;
				}
				if (maxChildMtGenomeState > maxMtGenomeState) {
					maxMtGenomeState = maxChildMtGenomeState;
				}
				// values for the current node
				AdditionalFields childFields = child.getAdditionalFields();
//				debugging output 				
//				if (childFields == null) {
//					System.out.println("\n\n\nchild missing fields is: " + child);
//				}
				currentDescendantsInfo.updateValuesFromCollectionInfo(childFields, contributor, project);
				// then merge in the results of all of the children
				currentDescendantsInfo.mergeValuesFromOtherCollectionInfo(nextDescendantsInfo);
			}
			// want to store a calculated class for the node in the larval
			// specimens 
			if (nodeLarvalCollectionStatus == AdditionalFields.DONT_HAVE_ANY) {
				nodesToLarvalClass.put(node, currentDescendantsInfo.getInternalLarvaeClass());	
			}
			if (nodeAdultCollectionStatus == AdditionalFields.DONT_HAVE_ANY) {
				nodesToAdultClass.put(node, currentDescendantsInfo.getInternalAdultClass());
			}
			if (nodeDnaCollectionStatus == AdditionalFields.DONT_HAVE_ANY) {
				nodesToDnaClass.put(node, currentDescendantsInfo.getInternalDnaClass());
			}
			if (nodeMicroCT == AdditionalFields.MICRO_CT_NEED_SPECIMEN) {
				nodesToMicroCTClass.put(node, currentDescendantsInfo.getInternalMicroCTClass());
			}
		}
		// store the result in the hash for later use
		nodesToTiers.put(node, minTier);
		nodesToMtGenome.put(node, maxMtGenomeState);
		returnList.add(minTier);
		returnList.add(maxMtGenomeState);
		return returnList;
	}	

	private static class DescendantCollectionInfo {
		private boolean hasTierAssigned;
		private boolean hasTier0Assigned;
		private boolean hasAnyWithLarvae;
		private boolean hasAnyTieredWithLarvae;
		private boolean needMoreLarvaeOfSomeTiered;
		private boolean hasAnyWithAdults;
		private boolean hasAnyTieredWithAdults;
		private boolean needMoreAdultsOfSomeTiered;
		private boolean hasAnyWithDna;
		private boolean hasAnyTieredWithDna;
		private boolean needMoreDnaOfSomeTiered;
		private boolean hasAnyWithMicroCT;
		private boolean hasAnyTieredWithMicroCT;
		private boolean needMoreMicroCTofSomeTiered;
		// take into account whether there is a specified supplier
		// in the calculation and whether the specified supplier
		// is the person who has them
		private boolean hasMeSupplierNotEnough;
		private boolean hasMeSupplierNone;
		private boolean hasOtherSupplierNotEnough;
		private boolean hasNoSupplierNotEnough;
		// the person can change to 'Have DNA' and the state of the
		// dna collection is 'Specimens to Molecular Lab, Decision Pending'
		private boolean hasDecisionPendingCanChange;
		
		public boolean getHasTier0Assigned() {
			return hasTier0Assigned;
		}
		public void setHasTier0Assigned(boolean hasTier0) {
			this.hasTier0Assigned = hasTier0;
		}		
		public boolean getHasTierAssigned() {
			return hasTierAssigned;
		}
		public void setHasTierAssigned(boolean hasAnyTiered) {
			this.hasTierAssigned = hasAnyTiered;
		}
		public boolean getHasAnyTieredWithLarvae() {
			return hasAnyTieredWithLarvae;
		}
		public void setHasAnyTieredWithLarvae(boolean hasAnyTieredWithLarvae) {
			this.hasAnyTieredWithLarvae = hasAnyTieredWithLarvae;
		}
		public boolean getHasAnyWithLarvae() {
			return hasAnyWithLarvae;
		}
		public void setHasAnyWithLarvae(boolean hasAnyWithLarvae) {
			this.hasAnyWithLarvae = hasAnyWithLarvae;
		}
		public boolean getNeedMoreLarvaeOfSomeTiered() {
			return needMoreLarvaeOfSomeTiered;
		}
		public void setNeedMoreLarvaeOfSomeTiered(boolean needMoreOfSomeTiered) {
			this.needMoreLarvaeOfSomeTiered = needMoreOfSomeTiered;
		}
		public void mergeValuesFromOtherCollectionInfo(DescendantCollectionInfo other) {
			setHasTierAssigned(getHasTierAssigned() || other.getHasTierAssigned());
			setHasTier0Assigned(getHasTier0Assigned() || other.getHasTier0Assigned());
			setHasAnyTieredWithLarvae(getHasAnyTieredWithLarvae() || other.getHasAnyTieredWithLarvae());
			setHasAnyWithLarvae(getHasAnyWithLarvae() || other.getHasAnyWithLarvae());
			setNeedMoreLarvaeOfSomeTiered(getNeedMoreLarvaeOfSomeTiered() || other.getNeedMoreLarvaeOfSomeTiered());
			setHasAnyTieredWithAdults(getHasAnyTieredWithAdults() || other.getHasAnyTieredWithAdults());
			setHasAnyWithAdults(getHasAnyWithAdults() || other.getHasAnyWithAdults());
			setNeedMoreAdultsOfSomeTiered(getNeedMoreAdultsOfSomeTiered() || other.getNeedMoreAdultsOfSomeTiered());
			setHasAnyTieredWithDna(getHasAnyTieredWithDna() || other.getHasAnyTieredWithDna());
			setHasAnyWithDna(getHasAnyWithDna() || other.getHasAnyWithDna());
			setNeedMoreDnaOfSomeTiered(getNeedMoreDnaOfSomeTiered() || other.getNeedMoreDnaOfSomeTiered());
			setHasAnyTieredWithMicroCT(getHasAnyTieredWithMicroCT() || other.getHasAnyTieredWithMicroCT());
			setHasAnyWithMicroCT(getHasAnyWithMicroCT() || other.getHasAnyWithMicroCT());
			setNeedMoreMicroCTofSomeTiered(getNeedMoreMicroCTofSomeTiered() || other.getNeedMoreMicroCTofSomeTiered());			
			setHasMeSupplierNone(getHasMeSupplierNone() || other.getHasMeSupplierNone());
			setHasMeSupplierNotEnough(getHasMeSupplierNotEnough() || other.getHasMeSupplierNotEnough());
			setHasOtherSupplierNotEnough(getHasOtherSupplierNotEnough() || other.getHasOtherSupplierNotEnough());
			setHasNoSupplierNotEnough(getHasNoSupplierNotEnough() || other.getHasNoSupplierNotEnough());
			setHasDecisionPendingCanChange(getHasDecisionPendingCanChange() || other.getHasDecisionPendingCanChange());
		}
		public boolean getHasAnyTieredWithAdults() {
			return hasAnyTieredWithAdults;
		}
		public void setHasAnyTieredWithAdults(boolean hasAnyTieredWithAdults) {
			this.hasAnyTieredWithAdults = hasAnyTieredWithAdults;
		}
		public boolean getHasAnyTieredWithDna() {
			return hasAnyTieredWithDna;
		}
		public void setHasAnyTieredWithDna(boolean hasAnyTieredWithDna) {
			this.hasAnyTieredWithDna = hasAnyTieredWithDna;
		}
		public boolean getHasAnyWithAdults() {
			return hasAnyWithAdults;
		}
		public void setHasAnyWithAdults(boolean hasAnyWithAdults) {
			this.hasAnyWithAdults = hasAnyWithAdults;
		}
		public boolean getHasAnyWithDna() {
			return hasAnyWithDna;
		}
		public void setHasAnyWithDna(boolean hasAnyWithDna) {
			this.hasAnyWithDna = hasAnyWithDna;
		}
		public boolean getNeedMoreAdultsOfSomeTiered() {
			return needMoreAdultsOfSomeTiered;
		}
		public void setNeedMoreAdultsOfSomeTiered(boolean needMoreAdultsOfSomeTiered) {
			this.needMoreAdultsOfSomeTiered = needMoreAdultsOfSomeTiered;
		}
		public boolean getNeedMoreDnaOfSomeTiered() {
			return needMoreDnaOfSomeTiered;
		}
		public void setNeedMoreDnaOfSomeTiered(boolean needMoreDnaOfSomeTiered) {
			this.needMoreDnaOfSomeTiered = needMoreDnaOfSomeTiered;
		}
		public boolean getHasAnyTieredWithMicroCT() {
			return hasAnyTieredWithMicroCT;
		}
		public void setHasAnyTieredWithMicroCT(boolean hasAnyTieredWithMicroCT) {
			this.hasAnyTieredWithMicroCT = hasAnyTieredWithMicroCT;
		}
		public boolean getHasAnyWithMicroCT() {
			return hasAnyWithMicroCT;
		}
		public void setHasAnyWithMicroCT(boolean hasAnyWithMicroCT) {
			this.hasAnyWithMicroCT = hasAnyWithMicroCT;
		}
		public boolean getNeedMoreMicroCTofSomeTiered() {
			return needMoreMicroCTofSomeTiered;
		}
		public void setNeedMoreMicroCTofSomeTiered(boolean needMoreMicroCTofSomeTiered) {
			this.needMoreMicroCTofSomeTiered = needMoreMicroCTofSomeTiered;
		}		
		public boolean getHasMeSupplierNotEnough() {
			return hasMeSupplierNotEnough;
		}
		public void setHasMeSupplierNotEnough(boolean hasMeSupplierNotEnough) {
			this.hasMeSupplierNotEnough = hasMeSupplierNotEnough;
		}
		public boolean getHasMeSupplierNone() {
			return hasMeSupplierNone;
		}
		public void setHasMeSupplierNone(boolean hasMeSupplierNone) {
			this.hasMeSupplierNone = hasMeSupplierNone;
		}		
		public boolean getHasNoSupplierNotEnough() {
			return hasNoSupplierNotEnough;
		}
		public void setHasNoSupplierNotEnough(boolean hasNoSupplierNotEnough) {
			this.hasNoSupplierNotEnough = hasNoSupplierNotEnough;
		}
		public boolean getHasOtherSupplierNotEnough() {
			return hasOtherSupplierNotEnough;
		}
		public void setHasOtherSupplierNotEnough(boolean hasOtherSupplierNotEnough) {
			this.hasOtherSupplierNotEnough = hasOtherSupplierNotEnough;
		}
		public boolean getHasDecisionPendingCanChange() {
			return hasDecisionPendingCanChange;
		}
		public void setHasDecisionPendingCanChange(boolean hasDecisionPendingCanChange) {
			this.hasDecisionPendingCanChange = hasDecisionPendingCanChange;
		}		
		/**
		 * Updates the stored object fields by checking them against the 
		 * current node's fields and also the contributor who will be viewing
		 * the info 
		 * @param childFields
		 * @param contr
		 * @param project TODO
		 */
		public void updateValuesFromCollectionInfo(AdditionalFields childFields, Contributor contr, Project project) {
			int larvalSpecimensState = childFields.getLarvalSpecimensState();
			int adultSpecimensState = childFields.getAdultSpecimensState();
			int dnaSpecimensState = childFields.getDnaSpecimensState();
			int microCTState = childFields.getMicroCTState();
			setHasTier0Assigned(childFields.getTier() == 0);
			boolean hasTierAssigned = childFields.getTier() != AdditionalFields.NO_TIER_SET;
			boolean hasLarvalSpecimens = larvalSpecimensState != AdditionalFields.DONT_HAVE_ANY;
			boolean hasTieredLarvalSpecimens = hasTierAssigned && hasLarvalSpecimens;
			boolean hasAdultSpecimens = adultSpecimensState != AdditionalFields.DONT_HAVE_ANY;
			boolean hasTieredAdultSpecimens = hasTierAssigned && hasAdultSpecimens;
			boolean hasDnaSpecimens = dnaSpecimensState != AdditionalFields.DONT_HAVE_ANY;
			boolean hasTieredDnaSpecimens = hasTierAssigned && hasDnaSpecimens;			
			boolean hasMicroCTSpecimens = microCTState != AdditionalFields.MICRO_CT_NEED_SPECIMEN;
			boolean hasTieredMicroCTSpecimens = getHasTier0Assigned() && hasMicroCTSpecimens;
			boolean needMoreLarvaeAndIsTiered = hasTierAssigned && 
				(larvalSpecimensState == AdditionalFields.HAVE_SOME || 
						larvalSpecimensState == AdditionalFields.DONT_HAVE_ANY);
			boolean needMoreAdultsAndIsTiered = hasTierAssigned &&
				(adultSpecimensState == AdditionalFields.HAVE_SOME || 
						adultSpecimensState == AdditionalFields.DONT_HAVE_ANY);
			boolean needMoreDnaAndIsTiered = hasTierAssigned &&
				(dnaSpecimensState == AdditionalFields.HAVE_SOME || 
					dnaSpecimensState == AdditionalFields.DONT_HAVE_ANY);
			boolean needMoreMicroCTAndIsTiered = getHasTier0Assigned() && 
				microCTState == AdditionalFields.MICRO_CT_NEED_SPECIMEN;
			// merge in the values for this current node
			setHasTierAssigned(hasTierAssigned || getHasTierAssigned());
			setHasAnyWithLarvae(hasLarvalSpecimens || getHasAnyWithLarvae());
			setHasAnyTieredWithLarvae(hasTieredLarvalSpecimens || getHasAnyTieredWithLarvae()); 
			setNeedMoreLarvaeOfSomeTiered(needMoreLarvaeAndIsTiered || getNeedMoreLarvaeOfSomeTiered());
			setHasAnyWithAdults(hasAdultSpecimens || getHasAnyWithAdults());
			setHasAnyTieredWithAdults(hasTieredAdultSpecimens || getHasAnyTieredWithAdults());
			setNeedMoreAdultsOfSomeTiered(needMoreAdultsAndIsTiered || getNeedMoreAdultsOfSomeTiered());
			setHasAnyWithDna(hasDnaSpecimens || getHasAnyWithDna());
			setHasAnyTieredWithDna(hasTieredDnaSpecimens || getHasAnyTieredWithDna());
			setNeedMoreDnaOfSomeTiered(needMoreDnaAndIsTiered || getNeedMoreDnaOfSomeTiered());
			setHasAnyWithMicroCT(hasMicroCTSpecimens || getHasAnyWithMicroCT());
			setHasAnyTieredWithMicroCT(hasTieredMicroCTSpecimens || getHasAnyTieredWithMicroCT());
			setNeedMoreMicroCTofSomeTiered(needMoreMicroCTAndIsTiered || getNeedMoreMicroCTofSomeTiered());
			if (contr != null && childFields.getDnaPossessionPersonId() != null) {
				// additional changes to accomodate who is the supplier of some group
				boolean hasMeSupplierNotEnough = (childFields.getDnaPossessionPersonId() == contr.getId()) &&
					(dnaSpecimensState == AdditionalFields.HAVE_SOME || dnaSpecimensState == AdditionalFields.HAVE_ENOUGH);
				setHasMeSupplierNotEnough(hasMeSupplierNotEnough || getHasMeSupplierNotEnough());
				boolean hasMeSupplierNone = ((childFields.getDnaPossessionPersonId() == contr.getId()) &&
						(dnaSpecimensState == AdditionalFields.DONT_HAVE_ANY));
				setHasMeSupplierNone(hasMeSupplierNone || getHasMeSupplierNone());
				boolean hasOtherSupplierNotEnough = (childFields.getDnaPossessionPersonId() != null &&
					childFields.getDnaPossessionPersonId() != contr.getId()) && 
					(dnaSpecimensState == AdditionalFields.HAVE_SOME || dnaSpecimensState == AdditionalFields.HAVE_ENOUGH);
				setHasOtherSupplierNotEnough(hasOtherSupplierNotEnough || getHasOtherSupplierNotEnough());
			}
			// user logged-in can change this, so they should get the bright yellow feedback
			if (contr != null && dnaSpecimensState == AdditionalFields.SPECIMENS_TO_LAB && 
					project != null && project.getContributorCanEditDna(contr)) {
				setHasDecisionPendingCanChange(true);
			}
			boolean hasNoSupplierNotEnough = childFields.getDnaPossessionPersonId() == null && 
				(dnaSpecimensState == AdditionalFields.HAVE_SOME || dnaSpecimensState == AdditionalFields.HAVE_ENOUGH); 
			setHasNoSupplierNotEnough(hasNoSupplierNotEnough || getHasNoSupplierNotEnough());
		}
		public String getInternalLarvaeClass() {
			return getInternalSpecimenCollectionClass(getHasTierAssigned(),	getHasAnyWithLarvae(), 
					getHasAnyTieredWithLarvae(), getNeedMoreLarvaeOfSomeTiered());
		}
		public String getInternalAdultClass() {
			return getInternalSpecimenCollectionClass(getHasTierAssigned(),	getHasAnyWithAdults(), 
					getHasAnyTieredWithAdults(), getNeedMoreAdultsOfSomeTiered());
		}
		public String getInternalDnaClass() {
			if (TaxaIndex.RALLY_TROOPS_MODE) {
				// new way of doing things
				return getInternalDnaSpecimenCollectionClass();
			} else {
				return getInternalSpecimenCollectionClass(getHasTierAssigned(), getHasAnyWithDna(), 
						getHasAnyTieredWithDna(), getNeedMoreDnaOfSomeTiered());
			}
		}
		private String getInternalDnaSpecimenCollectionClass() {
			if (getHasMeSupplierNotEnough()) {
				// bright green
				return TaxaIndex.getSpecimensYouSupplierClass();
			} else if (getHasMeSupplierNone()) {
				// bright red
				return TaxaIndex.getNoSpecimensYouSupplierClass();
			} else if (getHasDecisionPendingCanChange()) {
				// bright yellow
				return TaxaIndex.getActionNeededClass();
			} else if (getHasTierAssigned() && !getHasAnyWithDna()) {
				// dark pink
				return getNoSpecimensClass();
			} else if (getHasNoSupplierNotEnough()) {
				// pale pink
				return TaxaIndex.getSpecimensNoSupplierClass();
			} else if (getHasOtherSupplierNotEnough()) {
				// pale green
				return TaxaIndex.getSpecimensOtherSupplierClass();
			} else {
				// not one of the outstanding cases, so go back to the 
				// old way of doing things
				return getInternalSpecimenCollectionClass(getHasTierAssigned(), getHasAnyWithDna(), 
					getHasAnyTieredWithDna(), getNeedMoreDnaOfSomeTiered());
			}
		}
		// TODO figure out the internal class issues - it's only returning the NOTIER value...  
		private String getInternalMicroCTClass() {
			if (getNeedMoreMicroCTofSomeTiered()) {
				// dark pink
				return getNoSpecimensClass();
			} else {
				return TaxaIndex.NOTIER;
			}
		}
		private String getInternalSpecimenCollectionClass(boolean hasTierAssigned, boolean hasAny, 
				boolean hasAnyTiered, boolean needMoreTiered) {
			String returnClass = "";
			// want to figure out the calculated class for this internal node
			// note that the colors on the inferred internal nodes have a different
			// meaning than they do on assigned nodes -- the names of the functions
			if (hasTierAssigned && !hasAny) {
				returnClass = getNoSpecimensClass();
			} else if (hasAny && !hasAnyTiered) {
				if (needMoreTiered) {
					// light peach with burgundy bar
					returnClass = getHasSomeSpecimensWrongPersonClass();
				} else {
					// light peach no burgundy bar
					returnClass = getHasEnoughSpecimensWrongPersonClass();
				}
			} else if (hasAny && hasAnyTiered) {
				if (needMoreTiered) {
					// peach with burgundy bar
					returnClass = getHasSomeSpecimensClass();
				} else {
					// peach no burgundy bar
					returnClass = getHasEnoughSpecimensClass();
				}
			}
			return returnClass;
		}
	}	
}
