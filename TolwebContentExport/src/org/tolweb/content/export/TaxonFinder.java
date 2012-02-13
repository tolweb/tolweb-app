package org.tolweb.content.export;

import java.util.regex.Matcher;

public class TaxonFinder extends AbstractElementFinder {
	public static final String TAXON_REGEX = "<\\?xml[^\\?>]*\\?>(.*)(<taxon[^>]*>(.*)<\\/taxon>)";

	private String taxonText;
	
	public TaxonFinder(String input) {
		taxonText = "";
		Matcher m = createMatcherFor(TAXON_REGEX, input);
		if (m.find()) {
			taxonText = m.group(2);
		}
	}

	public String getTaxonText() {
		return taxonText;
	}
}
