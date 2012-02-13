package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class TextOnlyDNAStatus extends BasePage implements IExternalPage, NodeInjectable, ProjectInjectable {
	public static final String SUBFAMILY_ENDING = "inae";
	public static final String FAMILY_ENDING = "idae";
	public static final Integer STILL_NEEDED = 1; 
	public static final Integer SUFFICIENT = 2; 
	public abstract String getDisplayString();	
	public abstract void setDisplayString(String value);	

	public abstract String getHeadingString();	
	public abstract void setHeadingString(String value);	
	
	public abstract Set<MappedNode> getStillNeededNodes();
	public abstract void setStillNeededNodes(Set<MappedNode> nodes);
	
	public abstract Long getProjectId();
	public abstract void setProjectId(Long val);
	
	public abstract Integer getMode(); 
	public abstract void setMode(Integer mode);
	
	public abstract String getModeTitle();
	public abstract void setModeTitle(String title);
	
	@SuppressWarnings("unchecked")
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		Long projectId = Long.valueOf(1);		
		Long nodeId = Long.valueOf(8221);
		Integer mode = STILL_NEEDED;
		
		setProjectId(projectId);
		String showAsHtmlText = null;
		if (args.length == 3) {
			showAsHtmlText = args[2] != null ? args[2].toString() : "";
		}
		if (args.length >= 2) {
			nodeId = Long.valueOf(((Number) args[1]).longValue());
		} 
		if (args.length >= 1) {
			mode = Integer.valueOf(((Number) args[0]).intValue());;
			setMode(mode);
		}
		
		boolean showAsHtml = StringUtils.notEmpty(showAsHtmlText);
		
		Collection descendentIds = getMiscNodeDAO().getDescendantIdsForNode(nodeId);
		List additionalFields = getProjectDAO().getAdditionalFieldsForNodeIdsInProject(descendentIds, projectId);
		
		boolean stillNeededMode = mode.equals(STILL_NEEDED);
		boolean showAllMode = !(mode.equals(STILL_NEEDED) || mode.equals(SUFFICIENT));
		List nodes = determineNodesToUse(additionalFields, stillNeededMode,
				showAllMode);
		Collection descIds = getIds(nodes);
		Hashtable<Long, String> familyNames = buildFamilyNameMapping(descIds);
		Hashtable<Long, String> subfamilyNames = buildSubfamilyNameMapping(descIds);
		
		setDisplayString(buildDisplayString(nodes, familyNames, subfamilyNames, showAsHtml));
		
		if (!showAsHtml) {
			setHeadingString(buildHeadingString(showAsHtml));
		}
	}
	
	@SuppressWarnings("unchecked")
	private List determineNodesToUse(List additionalFields,
			boolean stillNeededMode, boolean showAllMode) {
		if (!showAllMode) {
			setModeTitle(stillNeededMode ? "Specimens Still Needed for DNA" : "Have Sufficient DNA");
			return filterNodesByDNAStatus(additionalFields, stillNeededMode);
		} else {
			setModeTitle("All DNA Status");
			return grabNodesForDNAStatus(additionalFields);
		}
	}

	public String getPageTitle() {
		return "BToL Text-Only View: " + getModeTitle();
	}
	
	public String getProjectName() {
		return "BToL";
	}	
	
	private String buildHeadingString(boolean showAsHtml) {
		
		StringBuffer header = new StringBuffer();
		if (showAsHtml) {
			header.append("<tr>");
		}
		header.append(String.format(showAsHtml ? "<th>TreeOrder</th>" : "%1$10s\t", "Tree Order"));
		header.append(String.format(showAsHtml ? "<th>Family Name</th>" : "%1$-18s\t", "Family Name"));
		header.append(String.format(showAsHtml ? "<th>Subfamily</th>" : "%1$-18s\t", "Subfamily"));
		header.append(String.format(showAsHtml ? "<th>Taxa</th>" : "%1$-25s\t", "Taxa"));
		header.append(String.format(showAsHtml ? "<th>Tier</th>" : "%1$-8s\t", "Tier"));
		header.append(showAsHtml ? "<th>DNA Specimen Status</th>" : "DNA Specimen Status\n");
		if (showAsHtml) {
			header.append("</tr>");
		} else {
			header.append(String.format("%1$10s\t", "=========="));
			header.append(String.format("%1$-18s\t", "==========="));
			header.append(String.format("%1$-18s\t", "==========="));
			header.append(String.format("%1$-25s\t", "=================="));
			header.append(String.format("%1$-8s\t", "======"));
			header.append("===================\n");
		}
		return header.toString();		
	}
	
	@SuppressWarnings("unchecked")
	private Collection getIds(List nodes) {
		List<Long> list = new ArrayList<Long>();
		for(Iterator i = nodes.iterator(); i.hasNext(); ) {
			MappedNode mnode = (MappedNode)i.next();
			list.add(Long.valueOf(mnode.getId()));
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private Hashtable<Long, String> buildFamilyNameMapping(Collection descendentIds) {
		List descIdList = new ArrayList(descendentIds);
		Hashtable<Long, String> mapping = new Hashtable<Long, String>();
		for (Iterator i = descIdList.iterator(); i.hasNext(); ) {
			Long id = (Long)i.next();
			mapping.put(id, findFamilyName(id));
		}
		return mapping;
	}
	
	@SuppressWarnings("unchecked")
	private Hashtable<Long, String> buildSubfamilyNameMapping(Collection descendentIds) {
		List descIdList = new ArrayList(descendentIds);
		Hashtable<Long, String> mapping = new Hashtable<Long, String>();
		for (Iterator i = descIdList.iterator(); i.hasNext(); ) {
			Long id = (Long)i.next();
			mapping.put(id, findSubfamilyName(id));
		}
		return mapping;
	}	
	
	private String findSubfamilyName(Long nodeId) {
		if (nodeId == 8221) {
			return "";
		} else {
			MappedNode node = getWorkingNodeDAO().getNodeWithId(nodeId);
			
			String otherName = otherNameIsSubfamilyName(node);
			if (node != null && node.getName().endsWith(SUBFAMILY_ENDING)) {
				return node.getName();
			} else if (node != null && StringUtils.notEmpty(otherName)) {
				return otherName;
			} else if (node != null){
				return findSubfamilyName(node.getParentNodeId());
			} else {
				return "";
			}
		}		
	}
	
	private String findFamilyName(Long nodeId) {
		if (nodeId == 8221) {
			return "";
		} else {
			MappedNode node = getWorkingNodeDAO().getNodeWithId(nodeId);
			
			String otherName = otherNameIsFamilyName(node);
			if (node != null && node.getName().endsWith(FAMILY_ENDING)) {
				return node.getName();
			} else if (node != null && StringUtils.notEmpty(otherName)) {
				return otherName;
			} else if (node != null){
				return findFamilyName(node.getParentNodeId());
			} else {
				return "";
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private String otherNameIsFamilyName(MappedNode node) {
		if (node != null) {
			SortedSet otherNames = node.getSynonyms();
			for (Iterator i = otherNames.iterator(); i.hasNext(); ) {
				MappedOtherName mname = (MappedOtherName)i.next();
				String name = mname.getName();
				if (StringUtils.notEmpty(name) && name.endsWith(FAMILY_ENDING)) {
					return name;
				}
			}
		} 
		return null;
	}

	@SuppressWarnings("unchecked")
	private String otherNameIsSubfamilyName(MappedNode node) {
		if (node != null) {
			SortedSet otherNames = node.getSynonyms();
			for (Iterator i = otherNames.iterator(); i.hasNext(); ) {
				MappedOtherName mname = (MappedOtherName)i.next();
				String name = mname.getName();
				if (StringUtils.notEmpty(name) && name.endsWith(SUBFAMILY_ENDING)) {
					return name;
				}
			}
		} 
		return null;
	}	
	
	private boolean meetsStillNeededCriteria(AdditionalFields fields) {
		return fields != null && 
			fields.getTier() != AdditionalFields.NO_TIER_SET && 
			fields.getDnaSpecimensState() < AdditionalFields.HAVE_ENOUGH;
	}
	
	private boolean meetsSufficientCriteria(AdditionalFields fields) {
		return fields != null && fields.getTier() != AdditionalFields.NO_TIER_SET &&
			fields.getDnaSpecimensState() == AdditionalFields.HAVE_DNA;
	}
	
	@SuppressWarnings("unchecked")
	private List grabNodesForDNAStatus(List additionalFields) {
		Set<AdditionalFields> filtered = new HashSet<AdditionalFields>();
		for (Iterator i = additionalFields.iterator(); i.hasNext(); ) {
			AdditionalFields fields = (AdditionalFields)i.next();
			if (fields != null && fields.getTier() != AdditionalFields.NO_TIER_SET) {
				filtered.add(fields);
			}
		}
		return processAdditionalFieldsSet(filtered);
	}
	
	@SuppressWarnings("unchecked")
	private List filterNodesByDNAStatus(List additionalFields, boolean stillNeeded) {
				
		Set<AdditionalFields> filtered = new HashSet<AdditionalFields>();
		
		for (Iterator i = additionalFields.iterator(); i.hasNext(); ) {
			AdditionalFields fields = (AdditionalFields)i.next();
			if (stillNeeded && meetsStillNeededCriteria(fields)) {
				filtered.add(fields);
			} else	if (!stillNeeded && meetsSufficientCriteria(fields)) {
				filtered.add(fields);
			}
		}
		
		List mnodes = processAdditionalFieldsSet(filtered);
		
		return mnodes;
	}
	
	@SuppressWarnings("unchecked")
	private List processAdditionalFieldsSet(Set<AdditionalFields> filtered) {
		List<Long> ids = new ArrayList<Long>();
		Hashtable<Long, AdditionalFields> idsToFields = new Hashtable<Long, AdditionalFields>(); 
		for (Iterator i = filtered.iterator(); i.hasNext(); ) {
			AdditionalFields fields = (AdditionalFields)i.next();
			ids.add(fields.getNodeId());
			idsToFields.put(fields.getNodeId(), fields);
		}
		
		List mnodes = getWorkingNodeDAO().getNodesWithIds(ids);
		for (Iterator i = mnodes.iterator(); i.hasNext(); ) {
			MappedNode mnode = (MappedNode)i.next();
			if (mnode != null) {
				mnode.setAdditionalFields(idsToFields.get(Long.valueOf(mnode.getId())));
			}
		}
		return mnodes;
	}	
	
	@SuppressWarnings("unchecked")
	private String buildDisplayString(List stillNeededNodes, Hashtable<Long, String> familyNames, Hashtable<Long, String> subfamilyNames, boolean showAsHtml) {
		StringBuffer neededDNA = new StringBuffer(); 
		DNASpecimensStateModel dnaModel = new DNASpecimensStateModel();
		
		if (showAsHtml) {
			neededDNA.append("<table class=\"border\">");
			neededDNA.append(buildHeadingString(showAsHtml));
		}
		
		for(Iterator i = stillNeededNodes.iterator(); i.hasNext(); ) {
			MappedNode mnode = (MappedNode)i.next();
			String name = provideNodeName(mnode);
			if (StringUtils.isEmpty(name)) {
				continue;
			}
			if (showAsHtml) {
				neededDNA.append("<tr>");
			}
			neededDNA.append(output(""+mnode.getTreeOrder(), 10, showAsHtml));
			neededDNA.append(output(familyNames.get(Long.valueOf(mnode.getId())), 18, showAsHtml));
			neededDNA.append(output(subfamilyNames.get(Long.valueOf(mnode.getId())), 18, showAsHtml));
			neededDNA.append(output(name, 25, showAsHtml));
			neededDNA.append(output("Tier " + mnode.getAdditionalFields().getTier(), 8, showAsHtml));
			String dnaState = dnaModel.getLabel(mnode.getAdditionalFields().getDnaSpecimensState()); 
			neededDNA.append(showAsHtml ? "<td>"+dnaState+"</td>" : dnaState);
			if (showAsHtml) {
				neededDNA.append("</tr>");
			} else {
				neededDNA.append('\n');
			}
		}
		if (showAsHtml) {
			neededDNA.append("</table>");
		}
		return neededDNA.toString();
	}
	
	private String output(String value, int spaces, boolean asHtml) {
		if (asHtml) {
			return "<td> " + value + "</td>";
		} else {
			String format = "%1$-" + spaces + "s\t";
			return String.format(format, value);
		}
			
	}
	
	@SuppressWarnings("unchecked")
	private String provideNodeName(MappedNode mnode) {
		if (mnode != null) {
			if (StringUtils.notEmpty(mnode.getName())) {
				return mnode.getName();	
			} else {
				SortedSet otherNames = mnode.getSynonyms();
				if (otherNames != null && !otherNames.isEmpty()) {
					Iterator itr = otherNames.iterator();
					return  itr.next().toString();
				} else {
					return "";
				}				
			}
		} else {
			return "[node-name-unavailable]";
		}
	}	
}
