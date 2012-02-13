/*
 * Created on Jun 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ognl.Ognl;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.misc.NodeHelper;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.Keywords;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ImageDataBlock extends BaseComponent implements ImageInjectable, BaseInjectable, NodeInjectable, PageInjectable {
	public static final String COPYRIGHT = "Copyright";
	public abstract void setImage(NodeImage img);
	public abstract NodeImage getImage();
	@SuppressWarnings("unchecked")
	public abstract List getOrderedList();
	@SuppressWarnings("unchecked")
	public abstract void setOrderedList(List value);
	@SuppressWarnings("unchecked")
	public abstract Hashtable getKeysToValues();
	@SuppressWarnings("unchecked")
	public abstract void setKeysToValues(Hashtable value);
	public abstract boolean getGenerateLinks();
	public abstract boolean getIsPageUse();
	public abstract boolean getOnlyShowCopyright();
	public abstract boolean getDontShowNotes();
    public abstract boolean getIsBatchUploadResults();
    public abstract boolean getIsSearchResults();
	
    private static NodeDAO publicNodeDAO;
    private static NodeDAO miscNodeDA0;
    private static PageDAO publicPageDAO;
    
	protected void prepareForRender(IRequestCycle cycle) {
		super.prepareForRender(cycle);
		initDAOs();
		initOrderedList();
	}
	
	protected void initDAOs() {
		publicNodeDAO = getPublicNodeDAO(); // I need access to the node-dao in a static method, so this will due for now
		miscNodeDA0 = getMiscNodeDAO();
		publicPageDAO = getPublicPageDAO();
	}
	
	@SuppressWarnings("unchecked")
	protected void initOrderedList() {
		List list = getOrderedList();
		if (list == null) {
			list = new ArrayList();
		}
		list.clear();
		Hashtable hash = getKeysToValues();
		if (hash == null) {
			hash = new Hashtable();
		}
		hash.clear();
		NodeImage img = getImage();
		byte usePermission = img.getUsePermission();
		ContributorLicenseInfo licInfo = new ContributorLicenseInfo(usePermission);
		boolean isCC = licInfo.isCreativeCommons(); //usePermission >= NodeImage.CC_BY20;
		if (getOnlyShowCopyright()) {
			addIfAvailable(list, hash, "Copyright", getTextPreparer().getCopyrightOwnerString(img, getGenerateLinks(), false, false));
		} else {
			addIfAvailable(list, hash, "Scientific Name", getScientificNameString(img, getIsPageUse()));
            if (!getIsSearchResults()) {
    			addIfAvailable(list, hash, "Location", img.getGeoLocation());
    			addIfAvailable(list, hash, "Comments", img.getComments());
    			addIfAvailable(list, hash, "Reference", img.getReference());
    			addIfAvailable(list, hash, "Creator", img.getCreator());		
    			addIfAvailable(list, hash, "Acknowledgements", img.getAcknowledgements());
    			addIfAvailable(list, hash, "Specimen Condition", getAliveString(img));	
    			addIfAvailable(list, hash, "Identified By", img.getIdentifier());		
    			addIfAvailable(list, hash, "Behavior", img.getBehavior());		
    			if (img.getSex() != null && !img.getSex().equalsIgnoreCase(NodeImage.UNKNOWN)) {
    				addIfAvailable(list, hash, "Sex", img.getSex());
    			}
    			addIfAvailable(list, hash, "Life Cycle Stage", img.getStage());
    			addIfAvailable(list, hash, "Body Part", img.getBodyPart());
    			addIfAvailable(list, hash, "View", img.getView());
    			addIfAvailable(list, hash, "Size", img.getSize());
    			addIfAvailable(list, hash, "Collection", img.getCollection());
    			addIfAvailable(list, hash, "Type", img.getType());
    			addIfAvailable(list, hash, "Collector", img.getCollector());
    			String url = img.getSourceCollectionUrl();    			
				if (StringUtils.notEmpty(url)) {
					url = StringUtils.getProperHttpUrl(url);
					String title = img.getSourceCollectionTitle();					
					String linkString = StringUtils.getLinkMarkup(url, title);
					addIfAvailable(list, hash, "Source", linkString);
				}
				if (img.getSourceDbId() != null && img.getSourceDbId().intValue() > 0) {
					ForeignDatabase db = getMiscNodeDAO().getForeignDatabaseWithId(img.getSourceDbId());
					if (db != null) {
						String dbUrl = db.getUrl();
						if (StringUtils.notEmpty(dbUrl)) {
							String linkString = StringUtils.getLinkMarkup(dbUrl, db.getName());
							addIfAvailable(list, hash, "Source Collection", linkString);
						}
					}
				}
				// want CC use to show up for tillus
				if (isCC) {
					addIfAvailable(list, hash, StringUtils.capitalizeString(img.getMediaTypeDescription()) + " Use", "" + (getUsePermissionString(img)));
				}
            }
			addIfAvailable(list, hash, COPYRIGHT, getTextPreparer().getCopyrightOwnerString(img, getGenerateLinks(), false, false));
			if (!getIsPageUse()) {
				// do it here only if not CC since it will have been done already if CC
				if (!isCC || getIsSearchResults()) {
					addIfAvailable(list, hash, StringUtils.capitalizeString(img.getMediaTypeDescription()) + " Use", "" + (getUsePermissionString(img)));
				}
                if (!getIsSearchResults()) {
                    addIfAvailable(list, hash, "Creation Date", getCreationDateString(img));
                }
				addIfAvailable(list, hash, "Attached to Group", getNodeNamesString(img, getIsBatchUploadResults(), getUrlBuilder()));
				if (getIsSearchResults()) {
					addIfAvailable(list, hash, "ID", "" + img.getId());
				}
				checkAndAddIfAvailable(list, hash, "Title", "title", img);
				if (img.getMediaType() != NodeImage.IMAGE) {
					// images don't have this field, checkAndAddIfAvailable() is just 
					// swallowing the ognl exception when the value is missing - which 
					// is sort of like converting numbers w/ Integer.parse & try/catch
					// bug-doc: http://bugzilla.tolweb.org/show_bug.cgi?id=2622
					checkAndAddIfAvailable(list, hash, "Description", "description", img);
				}
                if (!getIsSearchResults()) {
    				addIfAvailable(list, hash, StringUtils.capitalizeString(img.getMediaTypeDescription()) + " Type", img.getImageType());
    				if (img.getArtisticInterpretation()) {
    				    addIfAvailable(list, hash, "Artistic Interpretation", Boolean.valueOf(img.getArtisticInterpretation()).toString());
    				}				
    				addIfAvailable(list, hash, StringUtils.capitalizeString(img.getMediaTypeDescription()) + " Content", getContentString(img));
    				addIfAvailable(list, hash, "ToL Learner Level", getLearnerLevelString(img));
    				addIfAvailable(list, hash, "Subject", getSubjectString(img));
    				addIfAvailable(list, hash, "Key Words", img.getKeywords() != null ? img.getKeywords().getAdditionalKeywords() : null);
    				
    				addIfAvailable(list, hash, "Voucher Number", img.getVoucherNumber());
    				addIfAvailable(list, hash, "Voucher Number Collection", img.getVoucherNumberCollection());
    				addIfAvailable(list, hash, "Technical Information", img.getTechnicalInformation());
    				if (img.getMediaType() != NodeImage.IMAGE && img.getMediaType() != NodeImage.DOCUMENT) {
    					// similar to the issue with description... bug-doc: http://bugzilla.tolweb.org/show_bug.cgi?id=2622    					
    					checkAndAddIfAvailable(list, hash, "Running Time", "runningTime", img);
    				}
    				addIfAvailable(list, hash, "Languages", getLanguagesString(img));
    				if (!getDontShowNotes()) {
    					addIfAvailable(list, hash, "ALT Text", img.getAltText());
    				    addIfAvailable(list, hash, "Notes", img.getNotes());
    				}
    				addIfAvailable(list, hash, "ID", "" + img.getId());
                }
			}
		}
		setOrderedList(list);
		setKeysToValues(hash);	    
	}
	
	public String getTinyMceJavascript() {
	    return "javascript:tinyMCE.execCommand('mceInsertContent', false, '<img src=\"/tree/ToLimages/" + getImage().getLocation()  + "\">')";
	}
	
	/**
	 * Used for image subclasses that the image superclass might not have -- so things
	 * don't break if the method doesn't exist 
	 * @param list
	 * @param hash
	 * @param name
	 * @param key
	 * @param media
	 */
	@SuppressWarnings("unchecked")
	protected void checkAndAddIfAvailable(List list, Hashtable hash, String name, String key, NodeImage media) {
	    try {
	        String result = (String) Ognl.getValue(key, media);
	        addIfAvailable(list, hash, name, result);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	@SuppressWarnings("unchecked")
	protected void addIfAvailable(List list, Hashtable hash, String name, String value) {
		if (StringUtils.notEmpty(value)) {
			list.add(name);
			hash.put(name, value);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String getNodeNamesString(NodeImage img, boolean isBatchResults, URLBuilder builder) {
		ArrayList nodesNameList = new ArrayList();
		Iterator it = img.getNodesSet().iterator();
		while (it.hasNext()) {
			MappedNode node = (MappedNode) it.next();
			// if a node name should be italicized, then it should formatted correctly here 
            String name = node.getName();
            Long nodeId = node.getNodeId();
            if (isBatchResults) {
            	if (StringUtils.notEmpty(name)) {
            		String formattedName = node.getItalicizeName() ? "<em>" + name + "</em>" : name;
            		name = formattedName + ": " + 
            			   builder.getWorkingLinkForBranchPage(node, true) + " " + 
 				   		   builder.getMediaLinkForGroup(name, nodeId, img, true);
            	}
            } 
            else {
            	if (StringUtils.notEmpty(name) && builder != null) {
            		boolean exists = publicNodeDAO.getNodeExistsWithId(nodeId);
            		boolean hasPage = publicPageDAO.getNodeHasPage(nodeId);
            		String formattedName = node.getItalicizeName() ? "<em>" + name + "</em>" : name;
            		if (exists && hasPage) {
            			name = formattedName + ": " +
            					builder.getPublicLinkForBranchPage(node, "view page", true) + " " +
            				    builder.getMediaLinkForGroup(name, nodeId, img, true);
            		} else {
	            		Long ccgrpId = NodeHelper.findClosestContainingGroupWithPage(nodeId, miscNodeDA0, publicPageDAO);
	            		MappedNode ccgrp = publicNodeDAO.getNodeWithId(ccgrpId);
	            		String ccgrpName = ccgrp.getName();
	            		String formattedCCGrpName = ccgrp.getItalicizeName() ? "<em>" + ccgrpName + "</em>" : ccgrpName; 
	            		name = formattedName + " (" + formattedCCGrpName + "): " +
	            			builder.getPublicLinkForBranchPage(ccgrp, "view page", true) + " " +  
	            			builder.getMediaLinkForGroup(ccgrpName, ccgrpId, img, true);
            		}
            	}
            }            
            if (StringUtils.notEmpty(name)) {
            	nodesNameList.add(name);
            }
		}	  
		return StringUtils.returnHtmlBreakJoinedString(nodesNameList);		
	}
	
	@SuppressWarnings("unchecked")
	public static String getLanguagesString(NodeImage img) {
	    if (Document.class.isInstance(img) || Movie.class.isInstance(img) || Sound.class.isInstance(img)) {
		    List list = new ArrayList();
		    try {
		        addLanguageString(list, "isEnglish", "English", img);
		        addLanguageString(list, "isSpanish", "Spanish", img);
		        addLanguageString(list, "isGerman", "German", img);
		        addLanguageString(list, "isFrench", "French", img);		        
		        String otherLanguages = (String) Ognl.getValue("otherLanguage", img);
		        if (StringUtils.notEmpty(otherLanguages)) {
		            list.add(otherLanguages);
		        }
		    } catch (Exception e) {
		        
		    }
		    return StringUtils.returnCommaJoinedString(list);
	    } else {
	        return null;
	    }
	}
	
	@SuppressWarnings("unchecked")
	private static void addLanguageString(List list, String key, String language, NodeImage img)  throws Exception {
        Boolean isLanguage = (Boolean) Ognl.getValue(key, img);
        if (isLanguage.booleanValue()) {
            list.add(language);
        }
	}
	
	public static String getAliveString(NodeImage img) {
		String returnString = null;
		String alive = img.getAlive();
		if (alive != null && alive.equals(NodeImage.ALIVE)) {
			returnString = "Live Specimen";
		} else if (alive != null && alive.equals(NodeImage.DEAD)) {
			returnString = "Dead Specimen";
		} else if (alive != null && alive.equals(NodeImage.MODEL)) {
		    returnString = "Model";
		} else if (alive != null && alive.equals(NodeImage.FOSSIL)) {
			returnString = alive + (StringUtils.notEmpty(img.getPeriod()) ? " -- Period: " + img.getPeriod() : "");
		} else if (alive != null && !alive.equalsIgnoreCase(NodeImage.UNKNOWN)) {
			returnString =  alive;
		}
		return returnString;
	}
	
	public static String getUsePermissionString(NodeImage img) {
	    byte value = img.getUsePermission();
	    String returnString;
		if (value == NodeImage.RESTRICTED_USE) {
		    returnString = "restricted";
		} else if (value == NodeImage.TOL_USE) {
		    returnString = "ToL use only";
		} else if (value == NodeImage.EVERYWHERE_USE) {
		    returnString = "share with ToL partners";
		} else if (value == NodeImage.ALL_USES) {
			return getPubDomainPermissionString(img);
		} else {
		    return getCcUsePermissionString(img);
		}
		if (img.getModificationPermitted() != null && img.getModificationPermitted().booleanValue()) {
		    returnString += ", modification permitted";
		}
		return returnString;
	}
	
	private static String getCcUsePermissionString(NodeImage image) {
		ContributorLicenseInfo licInfo = new ContributorLicenseInfo(image.getUsePermission());
		String licenseVersion = licInfo.getLicenseVersion();
		String licenseName = licInfo.toString();
		String returnString = "<!-- Creative Commons License -->\n<img src=\"/tree/img/CCsomerights.gif\" alt=\"creative commons\"/> This media file is " + 
				"licensed under the <a href=\"" + ContributorLicenseInfo.linkString(licInfo) + "\">";
		returnString += licenseName;
		returnString += " - " + licenseVersion;
		returnString += "</a>.\n<!-- /Creative Commons License -->\n";
		String rdfLicenseString = getRdfLicenseString(image);
		returnString += rdfLicenseString;
		return returnString;
	}
	
	private static String getPubDomainPermissionString(NodeImage image) {
		ContributorLicenseInfo licInfo = new ContributorLicenseInfo(image.getUsePermission());
		String licenseName = licInfo.toString();
		String returnString = "<!-- Public Domain -->\nThis media file is " + 
			"in the <a href=\"" + ContributorLicenseInfo.linkString(licInfo) + "\">";			
		returnString += licenseName;
		returnString += "</a>.\n<!-- /Public Domain -->\n";
		return returnString;		
	}
	
	private static String getRdfLicenseString(NodeImage image) {
		String rdfString ="<!--\n" + 
				"<rdf:RDF xmlns=\"http://web.resource.org/cc/\"\n" + 
				"    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" + 
				"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" + 
				"<Work rdf:about=\"\">\n";
		rdfString += "<license rdf:resource=\"" + image.getCcLicenseUrl() + "\"/>\n</Work>\n";
		rdfString += "<License rdf:about=\"" + image.getCcLicenseUrl() + "\">\n";
		byte usePermission = image.getUsePermission();
		switch (usePermission) {
			case NodeImage.CC_BY20:
			case NodeImage.CC_BY25: 
				rdfString += "   <requires rdf:resource=\"http://web.resource.org/cc/Attribution\" />\n" + 
					"\n" + 
					"   <permits rdf:resource=\"http://web.resource.org/cc/Reproduction\" />\n" + 
					"   <permits rdf:resource=\"http://web.resource.org/cc/Distribution\" />\n" + 
					"   <permits rdf:resource=\"http://web.resource.org/cc/DerivativeWorks\" />\n" + 
					"\n" + 
					"   <requires rdf:resource=\"http://web.resource.org/cc/Notice\" />"; break;
			case NodeImage.CC_BY_NC20:
			case NodeImage.CC_BY_NC25:
				rdfString += "   <requires rdf:resource=\"http://web.resource.org/cc/Attribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Reproduction\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Distribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/DerivativeWorks\" />\n" + 
						"   <prohibits rdf:resource=\"http://web.resource.org/cc/CommercialUse\" />\n" + 
						"\n" + 
						"   <requires rdf:resource=\"http://web.resource.org/cc/Notice\" />"; break;
			case NodeImage.CC_BY_NC_ND20:
			case NodeImage.CC_BY_NC_ND25:
				rdfString += "   <requires rdf:resource=\"http://web.resource.org/cc/Attribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Reproduction\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Distribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/DerivativeWorks\" />\n" + 
						"   <prohibits rdf:resource=\"http://web.resource.org/cc/CommercialUse\" />\n" + 
						"\n" + 
						"   <requires rdf:resource=\"http://web.resource.org/cc/Notice\" />"; break;
			case NodeImage.CC_BY_NC_SA20:
			case NodeImage.CC_BY_NC_SA25:
				rdfString += "   <requires rdf:resource=\"http://web.resource.org/cc/Attribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Reproduction\" />\n" + 
						"\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Distribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/DerivativeWorks\" />\n" + 
						"   <requires rdf:resource=\"http://web.resource.org/cc/ShareAlike\" />\n" + 
						"   <prohibits rdf:resource=\"http://web.resource.org/cc/CommercialUse\" />\n" + 
						"   <requires rdf:resource=\"http://web.resource.org/cc/Notice\" />"; break;
			case NodeImage.CC_BY_ND20:
			case NodeImage.CC_BY_ND25:
				rdfString += "   <requires rdf:resource=\"http://web.resource.org/cc/Attribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Reproduction\" />\n" + 
						"\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Distribution\" />\n" + 
						"   <requires rdf:resource=\"http://web.resource.org/cc/Notice\" />"; break;
			case NodeImage.CC_BY_SA20:
			case NodeImage.CC_BY_SA25:
				rdfString += "   <requires rdf:resource=\"http://web.resource.org/cc/Attribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Reproduction\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/Distribution\" />\n" + 
						"   <permits rdf:resource=\"http://web.resource.org/cc/DerivativeWorks\" />\n" + 
						"   <requires rdf:resource=\"http://web.resource.org/cc/ShareAlike\" />\n" + 
						"\n" + 
						"   <requires rdf:resource=\"http://web.resource.org/cc/Notice\" />"; break;
		}
		rdfString += "\n</License>\n" + 
				"</rdf:RDF>\n-->";
		return rdfString;
	}
	public static String getCreationDateString(NodeImage img) {
		String returnString = "";
		if (img.getUserCreationDate() != null) {
			returnString += img.getUserCreationDate();
		}
		if (img.getSeason() != null) {
			returnString += " " + img.getSeason();
		}
		if (img.getAdditionalDateTimeInfo() != null) {
			returnString += " " + img.getAdditionalDateTimeInfo();
		}
		return returnString;
	}
	
	@SuppressWarnings("unchecked")
	public static String getSubjectString(NodeImage img) {
		ArrayList list = new ArrayList();	    
		Keywords keywords = img.getKeywords();
		if (StringUtils.notEmpty(keywords.getAdditionalKeywords())) {
		    list.add(keywords.getAdditionalKeywords());
		}
		if (keywords.getBiodiversity()) {
		    list.add("Biodiversity");
		}
		if (keywords.getBiogeography()) {
		    list.add("Biogeography");
		}
		if (keywords.getConservation()) {
		    list.add("Conservation");
		}
		if (keywords.getEcology()) {
		    list.add("Ecology");
		}
		if (keywords.getEvolution()) {
		    list.add("Evolution");
		}
		if (keywords.getGenetics()) {
		    list.add("Genetics");
		}
		if (keywords.getHistology()) {
		    list.add("Histology");
		}
		if (keywords.getLifehistory()) {
		    list.add("Life History");
		}
		if (keywords.getMethods()) {
		    list.add("Methods");
		}
		if (keywords.getMolecular()) {
		    list.add("Molecular");
		}
		if (keywords.getMorphology()) {
		    list.add("Morphology");
		}
		if (keywords.getNeurobiology()) {
		    list.add("Neurobiology");
		}
		if (keywords.getPaleobiology()) {
		    list.add("Paleobiology");
		}
		if (keywords.getPhylogenetics()) {
		    list.add("Phylogenetics");
		}
		if (keywords.getPhysiology()) {
		    list.add("Physiology");
		}
		if (keywords.getTaxonomy()) {
		    list.add("Taxonomy");
		}
		return StringUtils.returnCommaJoinedString(list);
	}
	
	@SuppressWarnings("unchecked")
	public static String getLearnerLevelString(NodeImage media) {
	    if (Document.class.isInstance(media)) {
	        Document doc = (Document) media;
	        List list = new ArrayList();
	        if (doc.getIsBeginner()) {
	            list.add("Beginner");
	        }
	        if (doc.getIsIntermediate()) {
	            list.add("Intermediate");
	        }
	        if (doc.getIsAdvanced()) {
	            list.add("Advanced");
	        }
	        return StringUtils.returnCommaJoinedString(list);
	    } else {
	        return null;
	    }
	}
	
	@SuppressWarnings("unchecked")
	public static String getContentString(NodeImage img) {
		ArrayList list = new ArrayList();
		if (Sound.class.isInstance(img)) {
		    Sound sound = (Sound) img;
		    if (sound.getIsOrganism()) {
		        list.add("Organism");
		    }
		    if (sound.getIsEnvironmental()) {
		        list.add("Environmental");
		    }
		    if (sound.getIsNarrative()) {
		        list.add("Narrative/Interpretation");
		    }
		} else if (Document.class.isInstance(img)) {
		    Document doc = (Document) img;
		    if (doc.getIsResearch()) {
		        list.add("Research Report");
		    }
		    if (doc.getIsLiterature()) {
		        list.add("Literature Review");
		    }
		    if (doc.getIsPresentation()) {
		        list.add("Presentation");
		    }
		    if (doc.getIsTeacherResource()) {
		        list.add("Teacher Resource");
		    }
		    if (doc.getIsLesson()) {
		        list.add("Lesson, Activity or Project");
		    }
		    if (doc.getIsLessonSupport()) {
		        list.add("Lesson/Activity Support Materials");
		    }
		    if (doc.getIsStandards()) {
		        list.add("National or State Education Standards");
		    }
		    if (doc.getIsVocabulary()) {
		        list.add("Vocabulary");
		    }
		    if (doc.getIsLecture()) {
		        list.add("Lecture");
		    }
		    if (doc.getIsDataset()) {
		        list.add("Data set");
		    }
		    if (doc.getIsFiction()) {
		        list.add("Book/article fiction");
		    }
		    if (doc.getIsNonFiction()) {
		        list.add("Book/article nonfiction");
		    }
		    if (StringUtils.notEmpty(doc.getOtherContent())) {
		        list.add(doc.getOtherContent());
		    }
		} else {
			if (img.getIsSpecimen()) {
				list.add("Specimen(s)");
			}
			if (img.getIsBodyParts()) {
				list.add(" Body Parts");
			}
			if (img.getIsUltrastructure()) {
				list.add("Ultrastructure");
			}
			if (img.getIsHabitat()) {
				list.add("Habitat");
			}
			if (img.getIsEquipment()) {
				list.add("Equipment/Apparatus");
			}
			if (img.getIsPeopleWorking()) {
				list.add("People At Work");
			}
		}
		return StringUtils.returnCommaJoinedString(list);
	}
    
    public String getCopyrightOwnerString() {
        return getTextPreparer().getCopyrightOwnerString(getImage(), true, true, false);
    }
    
    public String getImageInsertCode() {
        return getUrlBuilder().getNonJavascriptInsertImageHtml(getImage(), 250);
    }


	

	
	public static String getScientificNameString(NodeImage img, boolean isPageUse) {
	    if (isPageUse) {
	        if (StringUtils.notEmpty(img.getScientificName())) {
	            return img.getScientificName();
	        } else {
	            return getNodeNamesString(img, false, null);
	        }
	    } else {
	        return img.getScientificName();
	    }
	}
	
	/**
	 * Used to calculate the rowspan of the thumbnail (dependent on how many fields the img has)
	 * @return The rowspan of the thumbnail
	 */
	public int getRowspan() {
		return 4;
	}
    
    public boolean getShowOptionsLink() {
        return getImage().getMediaType() == NodeImage.IMAGE;
    }
}
