package org.tolweb.btol.tapestry.selection;

import java.util.List;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.Gene;
import org.tolweb.btol.NamedObject;

public class GeneFilterSelectionModel implements IPropertySelectionModel {
	private List<Gene> genes;
	
	public GeneFilterSelectionModel(List<Gene> value) {
		setGenes(value);
	}	
	public int getOptionCount() {
		return getGenes().size() + 1;
	}

	public Object getOption(int arg0) {
		if (arg0 == 0) {
			return null;
		} else {
			return getGenes().get(arg0 - 1);
		}            
	}
	public String getLabel(int arg0) {
		if (arg0 == 0) {
			return "All Genes";
		} else {
			return ((NamedObject) getOption(arg0)).getDisplayName();
		}
	}
	public String getValue(int arg0) {
		return "" + arg0;
	}
	public Object translateValue(String arg0) {
		return getOption(Integer.parseInt(arg0));
	}
	public List<Gene> getGenes() {
		return genes;
	}
	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}
}
