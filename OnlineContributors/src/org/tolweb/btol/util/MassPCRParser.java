package org.tolweb.btol.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.PCRProtocol;
import org.tolweb.btol.PCRReaction;
import org.tolweb.btol.Primer;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.dao.PCRProtocolDAO;
import org.tolweb.btol.dao.PCRReactionDAO;
import org.tolweb.btol.dao.PrimerDAO;
import org.tolweb.btol.dao.SpecimenExtractionDAO;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class MassPCRParser extends BaseMassParser {
	private static final String NEGATIVE_CONTROL_STRING = "neg";
	private static final String GEL_IMG_EXTENSION = ".jpg";
	private PCRProtocolDAO protocolDAO;
	private SpecimenExtractionDAO specimenExtractionDAO;
	private PrimerDAO primerDAO;
	private PCRReactionDAO pcrReactionDAO;

	/**
	 * parses the string that was previously set with
	 * setStringToParse
	 * @param contr
	 * @param missingProtocols
	 * @param missingPrimers
	 * @return
	 * @throws MassParseException
	 */
	public List<PCRBatch> getBatches(Contributor contr, Set<String> missingProtocols, 
			Set<String> missingPrimers, Set<String> existingReactionCodes, Long projectId) throws MassParseException {
		List<PCRBatch> batches = new ArrayList<PCRBatch>();
		int counter = 0;
		PCRBatch currentBatch = null;
		boolean isLastInBatch = false;
		boolean isFirstInBatch = false;
		Pattern negControlPattern = Pattern.compile(NEGATIVE_CONTROL_STRING,
				Pattern.CASE_INSENSITIVE);

		String notTab = getNotTabRegexString();
		String tabRegex = getTabRegexString();
		String maybeTabRegex = getMaybeTabRegexString();
		String regexString = notTab + tabRegex + notTab + tabRegex + notTab
				+ tabRegex + notTab + tabRegex + notTab + tabRegex + notTab
				+ tabRegex + notTab + tabRegex + notTab + tabRegex + notTab
				+ tabRegex + notTab + tabRegex + notTab + maybeTabRegex + notTab;
		Pattern reactionPattern = Pattern.compile(regexString);
		while (newlineTokenizer.hasMoreTokens()) {
			if (currentBatch == null) {
				currentBatch = new PCRBatch();
				currentBatch.setContributor(contr);
				isFirstInBatch = true;
				isLastInBatch = false;
			} else {
				isFirstInBatch = false;
			}
			PCRReaction newReaction = new PCRReaction();
			String nextLine = newlineTokenizer.nextToken();
			Matcher reactionMatcher = reactionPattern.matcher(nextLine);
			if (!reactionMatcher.find()) {
				System.out.println("no match: " + nextLine);
				continue;
			}
			// get each of the fields and try to set them -- notify the user if
			// there
			// are referential integrity problems
			String reactionCode = reactionMatcher.group(1);
			PCRReaction existingReaction = getPcrReactionDAO().getReactionWithCode(reactionCode);
			if (existingReaction != null) {
				existingReactionCodes.add(reactionCode);
				continue;
			}
			newReaction.setBtolCode(reactionCode);
			String dateString = reactionMatcher.group(2);
			if (counter++ == 0 && StringUtils.notEmpty(dateString)
					&& dateString.equalsIgnoreCase("date")) {
				// the first row is headers, so skip them and go to the next row
				// re-init the current batch var
				currentBatch = null;
				continue;
			}
			if (StringUtils.notEmpty(dateString)) {
				int[] dates = parseDateString(dateString);
				if (dates != null && isFirstInBatch) {
					setDates(currentBatch, dates);
				}
			}
			String protocolName = reactionMatcher.group(3);
			PCRProtocol protocol = getProtocolDAO().getProtocolWithName(
					protocolName, projectId);
			if (protocol != null && isFirstInBatch) {
				currentBatch.setProtocol(protocol);
			} else if (protocol == null && validateData) {
				missingProtocols.add(protocolName);
				/*setErrorMessage("There is no PCR Protocol named "
						+ protocolName
						+ ".  Please check your spelling and try again.");
				return null;*/
			}
			String reactionVolumeString = reactionMatcher.group(4);
			if (StringUtils.notEmpty(reactionVolumeString)) {
				int reactionVolume = -1;
				try {
					reactionVolume = Integer.parseInt(reactionVolumeString);
				} catch (Exception e) {
				}
				if (reactionVolume < 0 && validateData) {
					setErrorMessage("Reaction volume must be greater than 0");
					return null;
				} else if (isFirstInBatch) {
					currentBatch.setReactionVolume(Integer
							.parseInt(reactionVolumeString));
				}
			} else {
				if (validateData) {
					setErrorMessage("Reaction volume is a required field");
					return null;
				}
			}
			String extractionCodeString = reactionMatcher.group(5);
			if (StringUtils.notEmpty(extractionCodeString)) {
				SpecimenExtraction extraction = getSpecimenExtractionDAO().getExtractionWithCollectionAndCode(extractionCodeString, projectId);
				if (extraction != null) {
					isLastInBatch = false;
					if (extraction != null) {
						newReaction.setExtraction(extraction);
					}
				} else {
					if (extractionCodeString
							.equalsIgnoreCase(NEGATIVE_CONTROL_STRING)) {
						newReaction.setIsNegativeControl(true);
						String nextReaction = newlineTokenizer.peek();
						if (StringUtils.isEmpty(nextReaction)) {
							// prevent NPEs if it's already the last line
							nextReaction = "";
						}
						Matcher nextReactionMatcher = negControlPattern
								.matcher(nextReaction);
						if (nextReactionMatcher.find()) {
							// if the next line is a negative control, then we
							// aren't the last in the batch
							isLastInBatch = false;
						} else {
							isLastInBatch = true;
						}
					} else if (validateData) {
						setErrorMessage("Specimen extraction codes should be of the form DNA####, BT#### or CO####, invalid code is: "
								+ extractionCodeString);
						return null;
					}
				}
			}
			Primer forwardPrimer = null;
			String forwardPrimerName = reactionMatcher.group(6);
			if (StringUtils.notEmpty(forwardPrimerName)) {
				forwardPrimer = getPrimerDAO().getPrimerWithName(
						forwardPrimerName, true, projectId);
				if (forwardPrimer == null) {
					forwardPrimer = getPrimerDAO().getPrimerWithSynonym(forwardPrimerName, projectId);
				}
			}
			if (forwardPrimer != null && isFirstInBatch) {
				currentBatch.setForwardPrimer(forwardPrimer);
			} else if (forwardPrimer == null && validateData) {
				missingPrimers.add(forwardPrimerName);
				/*setErrorMessage("Unable to locate forward primer named: "
						+ forwardPrimerName);
				return null;*/
			}
			Primer reversePrimer = null;
			String reversePrimerName = reactionMatcher.group(7);
			if (StringUtils.notEmpty(reversePrimerName)) {
				reversePrimer = getPrimerDAO().getPrimerWithName(
						reversePrimerName, false, projectId);
				if (reversePrimer == null) {
					reversePrimer = getPrimerDAO().getPrimerWithSynonym(reversePrimerName, projectId);
				}
			}
			if (reversePrimer != null && isFirstInBatch) {
				currentBatch.setReversePrimer(reversePrimer);
			} else if (reversePrimer == null && validateData) {
				missingPrimers.add(reversePrimerName);
				/*setErrorMessage("Unable to locate reverse primer named: "
						+ reversePrimerName);
				return null;*/
			}
			String bandIntensityString = reactionMatcher.group(8);
			if (StringUtils.notEmpty(bandIntensityString)) {
				if (StringUtils.getIsNumeric(bandIntensityString)) {
					newReaction.setBandIntensity(Integer
							.parseInt(bandIntensityString));
					newReaction.setReactionResult(PCRReaction.SINGLE_BAND);
				} else {
					if (bandIntensityString.contains("double")) {
						newReaction.setReactionResult(PCRReaction.DOUBLE_BAND);
					} else if (bandIntensityString.contains("multiple")) {
						newReaction
								.setReactionResult(PCRReaction.MULTIPLE_BAND);
					} else if (bandIntensityString.contains("smear")) {
						newReaction.setReactionResult(PCRReaction.SMEAR);
					}
				}
			}
			String sequenceResultString = reactionMatcher.group(9);
			newReaction.setSequencingResults(sequenceResultString);

			String notesString = reactionMatcher.group(10);
			newReaction.setNotes(notesString);
			int groupCount = reactionMatcher.groupCount();
			if (groupCount > 10) {
				String imageFile = reactionMatcher.group(11);
				if (StringUtils.notEmpty(imageFile)) {
					// add a fix to zero-pad the image filename to match naming convention
					imageFile = StringUtils.zeroFillString(imageFile);
					if (!imageFile.endsWith(GEL_IMG_EXTENSION)) {
						// if no file extension, tack one on
						imageFile = imageFile + GEL_IMG_EXTENSION;
					}
					if (isFirstInBatch) {
						currentBatch.setImageFilename1(imageFile);
					} else {
						// not the first, but it could be one of the batches
						// that has more
						// than one image
						String batchImageFile = currentBatch
								.getImageFilename1();
						if (StringUtils.isEmpty(batchImageFile)) {
							currentBatch.setImageFilename1(batchImageFile);
						} else if (StringUtils.notEmpty(batchImageFile)
								&& !batchImageFile.equals(imageFile)) {
							// the case with the 2nd image
							currentBatch.setImageFilename2(imageFile);
						}
					}
				}
			}
			currentBatch.addToReactions(newReaction);
			// add it to the list if the next reaction is a different batch
			// or if we are at the end of the file
			if (isLastInBatch || !newlineTokenizer.hasMoreTokens()) {
				// System.out.println("last in batch? " + isLastInBatch + " more
				// tokens? " + newlineTokenizer.hasMoreTokens());
				batches.add(currentBatch);
				// System.out.println("current batch num reactions: " +
				// currentBatch.getReactions().size());
				//getPCRBatchDAO().saveBatch(currentBatch);
				// reset so a new batch is constructed next time around
				currentBatch = null;
			}

		}
		return batches;
	}	

	public PCRProtocolDAO getProtocolDAO() {
		return protocolDAO;
	}
	public void setProtocolDAO(PCRProtocolDAO dao) {
		this.protocolDAO = dao;
	}

	public SpecimenExtractionDAO getSpecimenExtractionDAO() {
		return specimenExtractionDAO;
	}

	public void setSpecimenExtractionDAO(SpecimenExtractionDAO extractionDAO) {
		this.specimenExtractionDAO = extractionDAO;
	}

	public PrimerDAO getPrimerDAO() {
		return primerDAO;
	}

	public void setPrimerDAO(PrimerDAO primerDAO) {
		this.primerDAO = primerDAO;
	}
	
	public static void main(String[] args) {
		String reactionString = "1	03/23/2006	ST-51-A	25	BT0001	Wg532F	WgAbRX	0			1		";
		String notTab = "([^\\t]*)";
		String tabRegex = "\\t";
		String maybeTabRegex = tabRegex + "?";
		String regexString = notTab + tabRegex + notTab + tabRegex + notTab
				+ tabRegex + notTab + tabRegex + notTab + tabRegex + notTab
				+ tabRegex + notTab + tabRegex + notTab + tabRegex + notTab
				+ tabRegex + notTab + tabRegex + notTab + maybeTabRegex + notTab;
		Pattern reactionPattern = Pattern.compile(regexString);
		Matcher matcher = reactionPattern.matcher(reactionString);
		matcher.find();
		for (int i = 1; i <= matcher.groupCount(); i++) {
			String string = matcher.group(i);
			System.out.println("group #" + i + " is: " + string);
		}
	}

	public PCRReactionDAO getPcrReactionDAO() {
		return pcrReactionDAO;
	}

	public void setPcrReactionDAO(PCRReactionDAO reactionDAO) {
		this.pcrReactionDAO = reactionDAO;
	}
}
