package org.tolweb.exporter;

import org.tolweb.hibernate.MappedNode;

public interface TaxonNameDecorator {
	public String getTaxonNameDecorationString(MappedNode sourceNode);
}
