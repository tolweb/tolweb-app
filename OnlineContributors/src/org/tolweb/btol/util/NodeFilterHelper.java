package org.tolweb.btol.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.tapestry.selection.NeededFilterSelectionModel;
import org.tolweb.btol.tapestry.selection.TierFilterSelectionModel;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

public class NodeFilterHelper {
	public Hashtable<Long, List<MappedNode>> getNodesFromUserSelection(int tierSelection, int neededSelection, boolean showAllTaxa, 
			MappedNode rootNode, Hashtable<Long, List<MappedNode>> allNodes, String geoSelection) {
		return getNodesFromUserSelection(tierSelection, neededSelection, showAllTaxa, rootNode, allNodes, geoSelection, null, null, null, false);
	}
	/**
	 * Filters a set of nodes based on user input
	 * @param tierSelection What tier selection to filter on
	 * @param neededSelection What needed selection to filter on
	 * @param showAllTaxa Whether to apply to all taxa (including those without sampling info)
	 * @param rootNode The root of the node tree
	 * @param allNodes All of the possible nodes to include
	 * @param geoSelection The geographic selection to filter nodes by
	 * @param selectedGeneFragments The genes to check if there is sequence information available
	 * @param statusesSource The object that contains information about the sequences
	 * @return A filtered hashtable, or null if all nodes match the criteria
	 */	
	public Hashtable<Long, List<MappedNode>> getNodesFromUserSelection(int tierSelection, int neededSelection, boolean showAllTaxa, 
			MappedNode rootNode, Hashtable<Long, List<MappedNode>> allNodes, String geoSelection, List<GeneFragment> selectedGeneFragments, 
			GeneFragmentNodeStatusesSource statusesSource, Contributor dnaSupplier, boolean showExtinctTaxa) {
		int tier = tierSelection;
		if (tier == TierFilterSelectionModel.SHOW_ALL_TIERS && showAllTaxa) {
			// here, it's set to show all, so no need to work
			// just clear the cached tiered nodes
			return null;
		}
		Hashtable<Long, List<MappedNode>> currentNodes = new Hashtable<Long, List<MappedNode>>(allNodes);
		Set<Long> idsToShow = new HashSet<Long>(); 
		// run through the keys and make a copy of all the lists
		for (Long currentNodeId : new HashSet<Long>(currentNodes.keySet())) {
			List<MappedNode> copyOfNodes = new ArrayList<MappedNode>(currentNodes.get(currentNodeId));
			currentNodes.remove(currentNodeId);
			currentNodes.put(currentNodeId, copyOfNodes);
		}
		Long rootNodeId = rootNode.getNodeId();
		// if there is any sort of a tier selection, that overrides the "show all taxa" selection
		if (tier != TierFilterSelectionModel.SHOW_ALL_TIERS || neededSelection != NeededFilterSelectionModel.SHOW_ALL || 
				(selectedGeneFragments != null && statusesSource != null)) {
			int mtGenomeToCheck = AdditionalFields.NO_MT_GENOME;
			if (tier == TierFilterSelectionModel.TIER_0) {
				tier = 0;
			} else if (tier == TierFilterSelectionModel.TIER_1) {
				tier = 1;
			} else if (tier == TierFilterSelectionModel.TIER_1_MT_GENOME_SOON) {
				mtGenomeToCheck = AdditionalFields.MT_GENOME_FIRST_YEAR;
				tier = 1;
			} else if (tier == TierFilterSelectionModel.MT_GENOME_COMPLETE) {
				mtGenomeToCheck = AdditionalFields.HAVE_MT_GENOME;
			} else if (tier == TierFilterSelectionModel.TIER_2) {
				tier = 2;
			} else if (tier == TierFilterSelectionModel.TIER_3) {
				tier = 3;
			}
			filterBasedOnCriteria(currentNodes, rootNodeId, idsToShow, tier, neededSelection, mtGenomeToCheck, geoSelection,
					selectedGeneFragments, statusesSource, dnaSupplier, showExtinctTaxa);
		} else {
			// no tier selection, but not showing all taxa, so filter out taxa
			// with no sampling information attached
			filterBasedOnAnyInfo(currentNodes, rootNodeId, idsToShow, rootNode.getAdditionalFields().getHasAnySamplingInfoAssigned(), 
					false, geoSelection, selectedGeneFragments, statusesSource, dnaSupplier, showExtinctTaxa);
		}
		// walk the nodes hash, removing any node ids that were not found to have the 
		// necessary tier
		for (List<MappedNode> nextNodeList : currentNodes.values()) {
			for (MappedNode node2 : new ArrayList<MappedNode>(nextNodeList)) {
				if (!idsToShow.contains(node2.getNodeId())) {
					nextNodeList.remove(node2);
				}
			}
		}		
		return currentNodes;		
	}
	/**
	 * 
	 * idea here is to iterate over all of the nodes:
	 * if a node matches the tier and mtGenomeState, add it to the set of ids to show
	 * else, check to see if any of its descendants match, if they do, 
	 * then add it to the list
	 * @param nodes The hashtable with the current tree structure
	 * @param nodeId The root node id
	 * @param validIds The set of node ids that match the criteria (populated while iterating)
	 * @param tier The tier that should be restricted
	 * @param neededSelection Whether to filter by needed for adults, dna, or larvae
	 * @param mtGenomeState The mtGenome filter selection
	 * @param geoSelection Filter by those nodes matching a substring for geo location
	 */	
	public boolean filterBasedOnCriteria(Hashtable<Long, List<MappedNode>> nodes, Long nodeId, Set<Long> validIds, 
			int tier, int neededSelection, int mtGenomeState, String geoSelection, List<GeneFragment> selectedGenes, 
			GeneFragmentNodeStatusesSource statusesSource, Contributor dnaSupplier, boolean showExtinctTaxa) {
		List<MappedNode> children = nodes.get(nodeId);
		if (children == null) {
			return false;
		} else {
			boolean anyChildrenMatchCriteria = false;
			for (MappedNode node : children) {
				AdditionalFields fields = node.getAdditionalFields();
				// TODO it looks like the btol app is running into cases where the fields variable is null 
				int nodeTier = fields.getTier();
				int nodeMtGenome = fields.getMtGenomeState();
				boolean nodeMatchesCriteria = false;

				if (mtGenomeState != AdditionalFields.HAVE_MT_GENOME) {
					nodeMatchesCriteria = nodeTier != AdditionalFields.NO_TIER_SET && nodeTier <= tier;
					if (mtGenomeState == AdditionalFields.MT_GENOME_FIRST_YEAR && nodeMatchesCriteria) {
						nodeMatchesCriteria = nodeMtGenome == AdditionalFields.MT_GENOME_FIRST_YEAR;
					}
				} else if (mtGenomeState == AdditionalFields.HAVE_MT_GENOME) {
					nodeMatchesCriteria = nodeMtGenome == AdditionalFields.HAVE_MT_GENOME;
				}
				if (neededSelection != NeededFilterSelectionModel.SHOW_ALL) {
					int neededField = AdditionalFields.HAVE_ENOUGH;
					if (neededSelection == NeededFilterSelectionModel.SHOW_NEEDED_ADULTS) {
						neededField = fields.getAdultSpecimensState();
					} else if (neededSelection == NeededFilterSelectionModel.SHOW_NEEDED_DNA) {
						neededField = fields.getDnaSpecimensState();
					} else if (neededSelection == NeededFilterSelectionModel.SHOW_NEEDED_LARVAE) {
						neededField = fields.getLarvalSpecimensState();
					}
					if (TaxaIndex.RALLY_TROOPS_MODE && neededSelection == NeededFilterSelectionModel.SHOW_NEEDED_DNA) {
						nodeMatchesCriteria = nodeMatchesCriteria && 
							neededField != AdditionalFields.HAVE_DNA && neededField != AdditionalFields.SPECIMENS_TO_LAB;						
					} else {
						nodeMatchesCriteria = nodeMatchesCriteria && 
							neededField != AdditionalFields.HAVE_ENOUGH;					
					}
				}
				boolean childrenMatchCriteria = filterBasedOnCriteria(nodes, node.getNodeId(), validIds, tier, neededSelection, mtGenomeState, geoSelection,
						selectedGenes, statusesSource, dnaSupplier, showExtinctTaxa);
				boolean matchesDnaSupplier =  getMatchesDnaSupplier(fields, dnaSupplier);
				boolean matchesExtinct = getMatchesExtinct(node, showExtinctTaxa);				
				// make sure that it matches the geo location as well
				// otherwise we don't want it in the list
				nodeMatchesCriteria = nodeMatchesCriteria && getHasSequenceStatus(node.getNodeId(), selectedGenes, statusesSource) && 
					getMatchesGeoLocation(fields, geoSelection) && matchesDnaSupplier;
				
				nodeMatchesCriteria = nodeMatchesCriteria || childrenMatchCriteria;	
				
				if (nodeMatchesCriteria && matchesExtinct) {
					validIds.add(node.getNodeId());
				}
				anyChildrenMatchCriteria = anyChildrenMatchCriteria || nodeMatchesCriteria;
			}
			return anyChildrenMatchCriteria;
		}
	}

	/**
	 * Iterate through the tree and include nodes that match any of
	 * the following criteria:
	 * (1) The node itself has some information attached to it (tier or sampling info)
	 * (2) An ancestor of the node has some information attached to it
	 * (3) A descendant of the node has some information attached to it
	 * @param nodes The hashtable that contains the tree structure
	 * @param nodeId The current nodeId being examined
	 * @param validIds This set gets filled with all of the valid ids 
	 * @param ancestorHadInfo Whether any ancestor had information associated with it
	 * @param onlyDescendantInfo Whether to consider only descendant info in the decision to 
	 * add the id to the set (no ancestor or a node itself)
	 * @param geoSelection TODO
	 * @return Whether the descendants have any information attached to them
	 */
	public boolean filterBasedOnAnyInfo(Hashtable<Long, List<MappedNode>> nodes, Long nodeId, Set<Long> validIds, boolean ancestorHadInfo, 
			boolean onlyDescendantInfo, String geoSelection, List<GeneFragment> selectedGenes, GeneFragmentNodeStatusesSource source, 
			Contributor dnaSupplier, boolean showExtinctTaxa) {
		List<MappedNode> children = nodes.get(nodeId);
		boolean anyChildrenHaveInfo = false;		
		if (children != null) {
			for (MappedNode node : children) {
				Long currentNodeId = node.getNodeId();
				AdditionalFields fields = node.getAdditionalFields();
				boolean currentChildMatchesExtinct = getMatchesExtinct(node, showExtinctTaxa);
				boolean currentChildMatchesGeo = getMatchesGeoLocation(fields, geoSelection);
				boolean currentChildHasInfo = fields.getHasAnySamplingInfoAssigned() && currentChildMatchesGeo;
				boolean currentChildHasSequenceStatus = getHasSequenceStatus(nodeId, selectedGenes, source);
				boolean currentChildMatchesDnaSupplier = getMatchesDnaSupplier(fields, dnaSupplier);
				boolean anyCurrentDescendantsHaveInfo = filterBasedOnAnyInfo(nodes, currentNodeId, validIds, ancestorHadInfo || currentChildHasInfo,
						onlyDescendantInfo, geoSelection, selectedGenes, source, dnaSupplier, showExtinctTaxa);
				if (onlyDescendantInfo) {
					anyChildrenHaveInfo = anyChildrenHaveInfo || anyCurrentDescendantsHaveInfo || currentChildHasInfo;
					if (anyCurrentDescendantsHaveInfo) {
						validIds.add(currentNodeId);
					}
				} else {
					boolean currMatch = (currentChildHasInfo && currentChildMatchesGeo && currentChildHasSequenceStatus 
							&& currentChildMatchesDnaSupplier) || ancestorHadInfo || anyCurrentDescendantsHaveInfo; 
					if (currMatch && currentChildMatchesExtinct) {
						validIds.add(currentNodeId);
						anyChildrenHaveInfo = true;
					}
				}
			}
		}
		return anyChildrenHaveInfo;
	}

	private boolean getMatchesExtinct(MappedNode node, boolean showExtinctTaxa) {
		if (node.getExtinct() == Node.EXTINCT)
			return showExtinctTaxa;
		else
			return true;

	}
	
	private boolean getMatchesDnaSupplier(AdditionalFields fields, Contributor dnaSupplier) {
		if (dnaSupplier == null) {
			return true;
		} else if (dnaSupplier.equals(Contributor.BLANK_CONTRIBUTOR)) {
			// if it's 'blank' then we're looking for fields w/ no supplier
			return fields != null && fields.getDnaPossessionPerson() == null;
		} else {
			return fields != null && (fields.getDnaPossessionPersonId() != null && 
				 fields.getDnaPossessionPersonId().equals(dnaSupplier.getId()));
		}
	}
	
	private boolean getMatchesGeoLocation(AdditionalFields fields, String geoLocation) {
		if (StringUtils.isEmpty(geoLocation)) {
			return true;
		} else {
			String fieldsDistribution = fields.getGeographicDistribution();
			if (StringUtils.isEmpty(fieldsDistribution)) {
				return false;
			} else {
				Pattern geoLocationPattern = Pattern.compile(geoLocation, Pattern.CASE_INSENSITIVE);
				Matcher matcher = geoLocationPattern.matcher(fieldsDistribution);
				boolean matches = matcher.find();
				return matches;
			}
		}
	}
	
	private boolean getHasSequenceStatus(Long nodeId, List<GeneFragment> selectedGenes, GeneFragmentNodeStatusesSource statusesSource) {
		if (selectedGenes == null || statusesSource == null) {
			// if these are null it means that we don't care, so everything
			// "has" a sequence status since we want to display everything
			return true;
		}
		for (GeneFragment gene : selectedGenes) {
			if (statusesSource.getStatusForNodeIdAndGeneFragment(nodeId, gene) != null) {
				// has some sequence status, display it
				return true;
			}
		}
		return false;
	}
}
