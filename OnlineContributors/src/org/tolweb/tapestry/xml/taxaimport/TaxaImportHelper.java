package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.tolweb.attributes.Phylesis;
import org.tolweb.attributes.PositionConfidence;
import org.tolweb.hibernate.ExtendedNodeProperties;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.tapestry.xml.taxaimport.beans.XTGeographicDistribution;
import org.tolweb.tapestry.xml.taxaimport.beans.XTNode;
import org.tolweb.tapestry.xml.taxaimport.beans.XTOthername;
import org.tolweb.tapestry.xml.taxaimport.beans.XTSourceInformation;
import org.tolweb.treegrow.main.StringUtils;



public class TaxaImportHelper {
	
	/**
	 * Copies the data represented in the XML Taxa (Input) Node to the 
	 * MappedNode. 
	 * @param mnode the destination 
	 * @param xtnode the source
	 * @param preserveNodeProperties
	 * @param preserveNodeName
	 */
	public static void copyNodeData(MappedNode mnode, XTNode xtnode, Boolean preserveNodeProperties, Boolean preserveNodeName) {

		copyNodeNameInfo(mnode, xtnode, preserveNodeName, TaxaImportCopySettings.EscapeNames);
		copyNodeProperties(mnode, xtnode, preserveNodeProperties, TaxaImportCopySettings.EscapeNames);
		copyBasicNodeData(mnode, xtnode, TaxaImportCopySettings.EscapeNames);
	}
	
	private static void copyNodeProperties(MappedNode mnode, XTNode xtnode, Boolean preserveNodeProperties, TaxaImportCopySettings settings) {
		/* Node Properties to Preserve:
		 * node_Extinct, node_Confidence, node_Phylesis, node_Leaf, isTrunkNode, rankName.
		 */
		copyExtinctNodeProperty(mnode, xtnode, preserveNodeProperties);
		copyConfidenceNodeProperty(mnode, xtnode, preserveNodeProperties);
		copyPhylesisProperty(mnode, xtnode, preserveNodeProperties);		
		copyLeafNodeProperty(mnode, xtnode, preserveNodeProperties);
		copyRankNameNodeProperty(mnode, xtnode, preserveNodeProperties);
	}
	
	private static void copyNodeNameInfo(MappedNode mnode, XTNode xtnode, Boolean preserveNodeName, TaxaImportCopySettings settings) {
		if (preserveNodeName) {
			copyNodeNamePreserve(mnode, xtnode, settings);
		} else {
			copyNodeNameClobber(mnode, xtnode, settings);
		}
	}
	
	private static void copyNodeNamePreserve(MappedNode mnode, XTNode xtnode, TaxaImportCopySettings settings) {
		/* Node Name to Preserve: (treat as a unit)
		 * authority, auth_year, nameComment, isNewCombination, combinationAuthor, combinationDate.
		 */
		if (shouldCopyNodeNameInfo(mnode, xtnode)) {
			// if we're going to change node-name info - we've gotta clear out everything: clean slate
			clearNodeNameInfo(mnode);
			
			if (StringUtils.notEmpty(xtnode.getAuthDate())) {
				try {
					mnode.setAuthorityDate(Integer.parseInt(xtnode.getAuthDate()));
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			if (xtnode.getIsNewCombination() != null) {
				mnode.setIsNewCombination(xtnode.getIsNewCombination());
			}
			if (StringUtils.notEmpty(xtnode.getAuthority())) {
				if (settings.equals(TaxaImportCopySettings.EscapeNames)) {
					String authority = StringEscapeUtils.unescapeXml(xtnode.getAuthority().trim());
					mnode.setNameAuthority(authority);
				} else {
					mnode.setNameAuthority(xtnode.getAuthority());
				}
			}
			if (StringUtils.notEmpty(xtnode.getCombinationAuthor())) {
				if (settings.equals(TaxaImportCopySettings.EscapeNames)) {
					String comboAuthor = StringEscapeUtils.unescapeXml(xtnode.getCombinationAuthor().trim());
					mnode.setCombinationAuthor(comboAuthor);
				} else {
					mnode.setCombinationAuthor(xtnode.getCombinationAuthor());
				}
			}
			if (StringUtils.notEmpty(xtnode.getCombinationDate())) {
				mnode.setCombinationDate(Integer.parseInt(xtnode.getCombinationDate()));
			}
			if (StringUtils.notEmpty(xtnode.getNameComment())) {
				mnode.setNameComment(xtnode.getNameComment());		
			}
		}
		// always copy if present: 
		//  italicizename, show_name_authority, showAuthorityInContainingGroup
		if (xtnode.getItalicizeName() != null) {
			mnode.setItalicizeName(xtnode.getItalicizeName());
		}		
		if (xtnode.getShowAuthority() != null) {
			mnode.setShowNameAuthority(xtnode.getShowAuthority());
		}
		if (xtnode.getShowAuthorityContaining() != null) {
			mnode.setShowAuthorityInContainingGroup(xtnode.getShowAuthorityContaining());
		}		
	}
	
	private static void clearNodeNameInfo(MappedNode mnode) {
		mnode.setAuthorityDate(null);
		mnode.setIsNewCombination(false);
		mnode.setNameAuthority(null);
		mnode.setCombinationAuthor(null);
		mnode.setCombinationDate(null);
		mnode.setNameComment(null);
		mnode.setItalicizeName(false);
		mnode.setShowNameAuthority(false);
		mnode.setShowAuthorityInContainingGroup(false);
	}
	
	private static boolean shouldCopyNodeNameInfo(MappedNode mnode, XTNode xtnode) {
		// is there something to copy?  if so, we need to copy everything because name info is really a single unit
		return StringUtils.notEmpty(xtnode.getAuthority()) || StringUtils.notEmpty(xtnode.getAuthDate()) ||
			StringUtils.notEmpty(xtnode.getNameComment()) || (xtnode.getIsNewCombination() != null) || 
			StringUtils.notEmpty(xtnode.getCombinationAuthor()) || StringUtils.notEmpty(xtnode.getCombinationDate());
	}
	
	private static void copyNodeNameClobber(MappedNode mnode, XTNode xtnode, TaxaImportCopySettings settings) {
		if (StringUtils.notEmpty(xtnode.getAuthDate())) {
			try {
				mnode.setAuthorityDate(Integer.parseInt(xtnode.getAuthDate()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			mnode.setAuthorityDate(null);
		}
		mnode.setIsNewCombination((Boolean)ObjectUtils.defaultIfNull(xtnode.getIsNewCombination(), false));
		mnode.setItalicizeName((Boolean)ObjectUtils.defaultIfNull(xtnode.getItalicizeName(), false));

		if (settings.equals(TaxaImportCopySettings.EscapeNames)) {
			String authority = StringEscapeUtils.unescapeXml(xtnode.getAuthority());
			mnode.setNameAuthority((String)ObjectUtils.defaultIfNull(authority, ""));
		} else {
			mnode.setNameAuthority((String)ObjectUtils.defaultIfNull(xtnode.getAuthority(), ""));			
		}		
				
		if (settings.equals(TaxaImportCopySettings.EscapeNames)) {
			String comboAuthor = StringEscapeUtils.unescapeXml(xtnode.getCombinationAuthor());
			mnode.setCombinationAuthor((String)ObjectUtils.defaultIfNull(comboAuthor, ""));
		} else {
			mnode.setCombinationAuthor((String)ObjectUtils.defaultIfNull(xtnode.getCombinationAuthor(), ""));
		}
		
		if (StringUtils.notEmpty(xtnode.getCombinationDate())) {
			mnode.setCombinationDate(Integer.parseInt(xtnode.getCombinationDate()));
		} else {
			mnode.setCombinationDate(null);
		}
		mnode.setNameComment(xtnode.getNameComment());		

		mnode.setShowNameAuthority((Boolean)ObjectUtils.defaultIfNull(xtnode.getShowAuthority(), false));
		mnode.setShowAuthorityInContainingGroup((Boolean)ObjectUtils.defaultIfNull(xtnode.getShowAuthorityContaining(), false));
	}
	
	/**
	 * Copies the data represented in the XML Taxa (Input) Node to the 
	 * MappedNode. 
	 * @param mnode the destination 
	 * @param xtnode the source 
	 * @param settings indicates settings options to be used when performing the copy
	 */
	public static void copyBasicNodeData(MappedNode mnode, XTNode xtnode, TaxaImportCopySettings settings) {
		if (settings.equals(TaxaImportCopySettings.EscapeNames)) {
			mnode.setName(StringEscapeUtils.unescapeXml(xtnode.getName().trim()));
		} else {
			mnode.setName(xtnode.getName().trim());			
		}

		mnode.setDescription(xtnode.getDescription());
		
		if (xtnode.getHasPage() != null) {
			mnode.setHasPage(xtnode.getHasPage().booleanValue());
		}
		
		// TODO determine what to do with source information
		XTSourceInformation srcInfo = xtnode.getSourceInformation(); // xml sub-element
		if (srcInfo != null) {
			srcInfo.getSourceId(); // TODO this id should somehow resolve to a record in the ForeignDatabases table
			mnode.setSourceDbNodeId(srcInfo.getSourceKey());
		}
		XTGeographicDistribution geoDist = xtnode.getGeographicDistribution(); // xml sub-element
		if (geoDist != null) {
			if (mnode.getExtendedNodeProperties() == null) {
				mnode.setExtendedNodeProperties(new ExtendedNodeProperties());
			}
			mnode.getExtendedNodeProperties().setGeoDistDescription(geoDist.getDescription());
		}
	}
	
	/**
	 * Used to 'seed' initial data from a MappedNode into an XML Taxa (Input) Node
	 * @param xtnode the destination
	 * @param mnode the source
	 */
	public static void seedNodeData(XTNode xtnode, MappedNode mnode) {
		xtnode.setName(mnode.getName());
		Integer authDate = mnode.getAuthorityDate();
		xtnode.setAuthDate((authDate != null) ? authDate.toString() : "");
		xtnode.setLeaf(mnode.getIsLeaf());
		xtnode.setIsNewCombination(mnode.getIsNewCombination());
		xtnode.setItalicizeName(mnode.getItalicizeName());
		xtnode.setAuthority(mnode.getNameAuthority());
		xtnode.setCombinationAuthor(mnode.getCombinationAuthor());
		xtnode.setCombinationDate((mnode.getCombinationDate() != null) ? mnode.getCombinationDate().toString() : "");
		PositionConfidence confidence = PositionConfidence.fromInt(mnode.getConfidence());
		xtnode.setConfidence(confidence.toString());
		Phylesis phylesis = Phylesis.fromInt(mnode.getPhylesis());
		xtnode.setPhylesis(phylesis.toString());
		xtnode.setDescription(mnode.getDescription());
		xtnode.setExtinct(mnode.getExtinct() == MappedNode.EXTINCT);
		xtnode.setIncompleteSubgroups(mnode.getHasIncompleteSubgroups());
		xtnode.setNameComment(mnode.getNameComment());
		xtnode.setRank(mnode.getRankName());
		xtnode.setShowAuthority(mnode.getShowNameAuthority());
		xtnode.setShowAuthorityContaining(mnode.getShowAuthorityInContainingGroup());
		if (mnode.getSynonyms() != null && !mnode.getSynonyms().isEmpty()) {
			List<XTOthername> othernames = new ArrayList<XTOthername>();
			for (Iterator itr = mnode.getSynonyms().iterator(); itr.hasNext(); ) {
				MappedOtherName moname = (MappedOtherName)itr.next();
				XTOthername othername = new XTOthername();
				othername.setAuthority(moname.getAuthority());
				othername.setComments(moname.getComment());
				othername.setDate(""+moname.getDate());
				othername.setIsImportant(moname.getIsImportant());
				othername.setIsPreferred(moname.getIsPreferred());
				othername.setItalicizeName(moname.getItalicize());
				othername.setName(moname.getName());
				othername.setSequence(moname.getOrder());
				othernames.add(othername);
			}
			xtnode.setOthernames(othernames);
		}
		xtnode.setSourceInformation(new XTSourceInformation());
		xtnode.getSourceInformation().setSourceKey(mnode.getSourceDbNodeId());
	}
	
	/**
	 * Copies the data model by the XML Taxa (Input) Other Name object into a MappedOtherName.
	 * @param moname the destination
	 * @param xtoname the source
	 */
	public static void copyOtherNameData(MappedOtherName moname, XTOthername xtoname) {
		copyOtherNameData(moname, xtoname, TaxaImportCopySettings.EscapeNames);
	}

	/**
	 * Copies the data model by the XML Taxa (Input) Other Name object into a MappedOtherName.
	 * @param moname the destination
	 * @param xtoname the source
	 * @param settings indicates the settings options to use when performing the copy operation
	 */
	public static void copyOtherNameData(MappedOtherName moname, XTOthername xtoname, TaxaImportCopySettings settings) {
		if (settings.equals(TaxaImportCopySettings.EscapeNames)) {
			moname.setAuthority(StringEscapeUtils.unescapeXml(xtoname.getAuthority()));
			moname.setName(StringEscapeUtils.unescapeXml(xtoname.getName()));
			moname.setComment(StringEscapeUtils.unescapeXml(xtoname.getComments()));
		} else {
			moname.setAuthority(xtoname.getAuthority());
			moname.setName(xtoname.getName());
			moname.setComment(xtoname.getComments());
		}
		if (StringUtils.notEmpty(xtoname.getDate())) {
			moname.setAuthorityYear(Integer.parseInt(xtoname.getDate()));
		}
		moname.setIsImportant((Boolean)ObjectUtils.defaultIfNull(xtoname.getIsImportant(), false));
		moname.setIsPreferred((Boolean)ObjectUtils.defaultIfNull(xtoname.getIsPreferred(), false));
		moname.setItalicize((Boolean)ObjectUtils.defaultIfNull(xtoname.getItalicizeName(), false));
		moname.setOrder(xtoname.getSequence());
		
	}
	
	public static void resetNodeProperties(MappedNode mnode) {
		/* reset the following:
		 * authority, auth_year, nameComment, isNewCombination, combinationAuthor, combinationDate, sourceDbNodeId */
		mnode.setNameAuthority("");
		mnode.setAuthorityDate(null);
		mnode.setNameComment("");
		mnode.setIsNewCombination(false);
		mnode.setCombinationAuthor("");
		mnode.setCombinationDate(null);
		mnode.setSourceDbNodeId("");
	}
	
	private static void copyRankNameNodeProperty(MappedNode mnode, XTNode xtnode, Boolean preserveNodeProperties) {
		if (preserveNodeProperties && StringUtils.notEmpty(xtnode.getRank())) {
			mnode.setRankName(xtnode.getRank());
		}
		
		if (!preserveNodeProperties) {
			mnode.setRankName(xtnode.getRank());
		}
	}

	private static void copyPhylesisProperty(MappedNode mnode, XTNode xtnode, Boolean preserveNodeProperties) {
		// I feel the same about this as ConfidenceProperty - but I think this will work now...
		if (!preserveNodeProperties) {
			if (StringUtils.isEmpty(xtnode.getPhylesis())) {
				mnode.setPhylesis(MappedNode.MONOPHYLETIC);
			} else {
				mnode.setPhylesis(Phylesis.toInt(Phylesis.fromString(xtnode.getPhylesis())));
			}
		} else if (preserveNodeProperties && StringUtils.notEmpty(xtnode.getPhylesis())) {
			mnode.setPhylesis(Phylesis.toInt(Phylesis.fromString(xtnode.getPhylesis())));
		}

	}

	private static void copyConfidenceNodeProperty(MappedNode mnode, XTNode xtnode, Boolean preserveNodeProperties) {
		// okay... I'm thoroughly confused with how this should work - the logic is just not making sense, but I think this will work (for now)
		if (!preserveNodeProperties) {
			if (StringUtils.isEmpty(xtnode.getConfidence())) {
				mnode.setConfidence(MappedNode.INCERT_OFF);
			} else {
				int confidence = PositionConfidence.toInt(PositionConfidence.fromString(xtnode.getConfidence()));
				mnode.setConfidence(confidence);
			}
		} else if (preserveNodeProperties && StringUtils.notEmpty(xtnode.getConfidence())) {
			int confidence = PositionConfidence.toInt(PositionConfidence.fromString(xtnode.getConfidence()));
			mnode.setConfidence(confidence);			
		}
	}

	private static void copyExtinctNodeProperty(MappedNode mnode, XTNode xtnode, Boolean preserveNodeProperties) {
		if (preserveNodeProperties && xtnode.getExtinct() != null) { 
			mnode.setExtinct((xtnode.getExtinct()) ? MappedNode.EXTINCT : MappedNode.NOT_EXTINCT);
		}

		if (!preserveNodeProperties) {
			mnode.setExtinct((xtnode.getExtinct() != null && xtnode.getExtinct()) ? MappedNode.EXTINCT : MappedNode.NOT_EXTINCT);
		}
	}

	private static void copyLeafNodeProperty(MappedNode mnode, XTNode xtnode, Boolean preserveNodeProperties) {
		if (preserveNodeProperties && xtnode.getLeaf() != null) {
			mnode.setIsLeaf(xtnode.getLeaf());
		} 
		
		if (!preserveNodeProperties) {
			mnode.setIsLeaf((Boolean)ObjectUtils.defaultIfNull(xtnode.getLeaf(), false));
		}
	}	
}
