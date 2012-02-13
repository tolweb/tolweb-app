package org.tolweb.content.helpers;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ImageDAO;
import org.tolweb.misc.ImageUtils;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;

public class MediaContentRepresentationIntegrationTest extends
		ApplicationContextTestAbstract {
	private ImageDAO imageDAO;
	private ImageUtils imageUtils;
	
	public MediaContentRepresentationIntegrationTest(String name) {
		super(name);
		imageDAO = (ImageDAO)context.getBean("imageDAO");
		imageUtils = (ImageUtils)context.getBean("imageUtils");
	}

	/**
	 * Example from Bug #2646
	 * http://bugzilla.tolweb.org/show_bug.cgi?id=2646
	 */
	public void test_description_being_built_for_movies() {
		NodeImage mediaFile = imageDAO.getImageWithId(23965);
		ImageVersion maxVersion = imageDAO.getMaxAllowedVersion(mediaFile);
		String mediaUrl = imageUtils.getVersionUrl(maxVersion);
		MediaContentRepresentation mediaRep = new MediaContentRepresentation(mediaFile, mediaUrl);
		System.out.println(mediaFile.getNodesSet());
		
		System.out.println("output: " + mediaRep.getDescription());
	}
	
/*
		StringBuilder desc = new StringBuilder();
		if (StringUtils.notEmpty(mediaFile.getScientificName())) {
			desc.append("Group: " + mediaFile.getScientificName() + "; ");
		}
		if (StringUtils.notEmpty(mediaFile.getIdentifier())) {
			desc.append("Identified by: " + mediaFile.getIdentifier() + "; ");
		}
		if (!NodeImage.class.isInstance(mediaFile)) {
			DescriptiveMedia descMedia = (DescriptiveMedia)mediaFile;
			if (StringUtils.notEmpty(descMedia.getDescription())) {
				desc.append("Description: " + descMedia.getDescription() + "; ");
			}
		}
		if (StringUtils.notEmpty(mediaFile.getComments())) {
			desc.append("Comments: " + mediaFile.getComments() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getBehavior())) {
			desc.append("Behavior: " + mediaFile.getBehavior() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getSex()) && 
				!"unknown".equals(mediaFile.getSex().toLowerCase())) {
			desc.append("Sex: " + mediaFile.getSex() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getStage())) {
			desc.append("Life Cycle Stage: " + mediaFile.getStage() + "; ");
		}
		if (StringUtils.notEmpty(mediaFile.getBodyPart())) {
			desc.append("Body Part: " + mediaFile.getBodyPart() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getSize())) {
			desc.append("Size: " + mediaFile.getSize() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getView())) {
			desc.append("View: " + mediaFile.getView() + "; ");
		}		
		if (mediaFile.getIsFossil()) {
			desc.append("Fossil: ; ");
		}
		if (StringUtils.notEmpty(mediaFile.getPeriod())) {
			desc.append("Period: " + mediaFile.getPeriod() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getAcknowledgements())) {
			desc.append("Acknowledgements: " + mediaFile.getAcknowledgements() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getCollection())) {
			String collectionAcronym = "";
			if (StringUtils.notEmpty(mediaFile.getCollectionAcronym())) {
				collectionAcronym = " (" + mediaFile.getCollectionAcronym() + ")";
			} 
			desc.append("Collection: " + mediaFile.getCollection() + collectionAcronym + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getType())) {
			desc.append("Type: " + mediaFile.getType() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getCollector())) {
			desc.append("Collector: " + mediaFile.getCollector() + "; ");
		}				
		if (StringUtils.notEmpty(mediaFile.getTechnicalInformation())) {
			desc.append("Technical Information: " + mediaFile.getTechnicalInformation() + "; ");
		}				

 */	
	
	
}
