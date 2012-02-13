package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.tapestry.CitationCreator;

public interface CitationInjectable {
	@InjectObject("spring:citationCreator")
	public CitationCreator getCitationCreator();
}
