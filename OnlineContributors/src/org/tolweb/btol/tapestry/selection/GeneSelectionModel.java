package org.tolweb.btol.tapestry.selection;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.Gene;
import org.tolweb.btol.NamedObject;

public class GeneSelectionModel implements IPropertySelectionModel {
	@SuppressWarnings("unchecked")
	private List genes;
	
	@SuppressWarnings("unchecked")
	public GeneSelectionModel(List genes) {
		this.genes = genes;
		Collections.sort(genes, new Comparator() {
			public int compare(Object o1, Object o2) {
				Gene gene1 = (Gene) o1;
				Gene gene2 = (Gene) o2;
				return gene1.getName().compareTo(gene2.getName());
			} });
	}
	public int getOptionCount() {
		return genes.size();
	}
	public Object getOption(int arg0) {
		return genes.get(arg0);
	}
	public String getLabel(int arg0) {
		return ((Gene) getOption(arg0)).getName();
	}
	public String getValue(int arg0) {
		return ((NamedObject) getOption(arg0)).getId().toString();
	}
	@SuppressWarnings("unchecked")
	public Object translateValue(String arg0) {
		Long id = Long.valueOf(arg0);
		for (Iterator iter = genes.iterator(); iter.hasNext();) {
			NamedObject nextGene = (NamedObject) iter.next();
			if (nextGene.getId().equals(id)) {
				return nextGene;
			}
		}
		return null;
	}

}
