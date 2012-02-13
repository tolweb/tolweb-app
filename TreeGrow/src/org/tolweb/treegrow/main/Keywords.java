/*
 * Created on Apr 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import java.io.Serializable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="Keywords"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class Keywords implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2026642559156513223L;
	private Long id;
    private boolean evolution;
    private boolean phylogenetics;
    private boolean taxonomy;
    private boolean biodiversity;
    private boolean ecology;
    private boolean conservation;
    private boolean biogeography;
    private boolean paleobiology;
    private boolean morphology;
    private boolean lifehistory;
    private boolean physiology;
    private boolean neurobiology;
    private boolean histology;
    private boolean genetics;
    private boolean molecular;
    private boolean methods;
    private String additionalKeywords;
    
    public void copyValues(Keywords other) {
        evolution = other.getEvolution();
        phylogenetics = other.getPhylogenetics();
        taxonomy = other.getTaxonomy();
        biodiversity = other.getBiodiversity();
        ecology = other.getEcology();
        conservation = other.getConservation();
        biogeography = other.getBiogeography();
        paleobiology = other.getPaleobiology();
        morphology = other.getMorphology();
        lifehistory = other.getLifehistory();
        physiology = other.getPhysiology();
        neurobiology = other.getNeurobiology();
        histology = other.getHistology();
        genetics = other.getGenetics();
        molecular = other.getMolecular();
        methods = other.getMethods();
        additionalKeywords = other.getAdditionalKeywords();        
    }
    /**
     * @hibernate.property column="additional_keywords"
     * @return Returns the additionalKeywords.
     */
    public String getAdditionalKeywords() {
        return additionalKeywords;
    }
    /**
     * @param additionalKeywords The additionalKeywords to set.
     */
    public void setAdditionalKeywords(String additionalKeywords) {
        this.additionalKeywords = additionalKeywords;
    }
    /**
     * @hibernate.property
     * @return Returns the biodiversity.
     */
    public boolean getBiodiversity() {
        return biodiversity;
    }
    /**
     * @param biodiversity The biodiversity to set.
     */
    public void setBiodiversity(boolean biodiversity) {
        this.biodiversity = biodiversity;
    }
    /**
     * @hibernate.property
     * @return Returns the biogeography.
     */
    public boolean getBiogeography() {
        return biogeography;
    }
    /**
     * @param biogeography The biogeography to set.
     */
    public void setBiogeography(boolean biogeography) {
        this.biogeography = biogeography;
    }
    /**
     * @hibernate.property
     * @return Returns the conservation.
     */
    public boolean getConservation() {
        return conservation;
    }
    /**
     * @param conservation The conservation to set.
     */
    public void setConservation(boolean conservation) {
        this.conservation = conservation;
    }
    /**
     * @hibernate.property
     * @return Returns the ecology.
     */
    public boolean getEcology() {
        return ecology;
    }
    /**
     * @param ecology The ecology to set.
     */
    public void setEcology(boolean ecology) {
        this.ecology = ecology;
    }
    /**
     * @hibernate.property
     * @return Returns the evolution.
     */
    public boolean getEvolution() {
        return evolution;
    }
    /**
     * @param evolution The evolution to set.
     */
    public void setEvolution(boolean evolution) {
        this.evolution = evolution;
    }
    /**
     * @hibernate.property
     * @return Returns the genetics.
     */
    public boolean getGenetics() {
        return genetics;
    }
    /**
     * 
     * @param genetics The genetics to set.
     */
    public void setGenetics(boolean genetics) {
        this.genetics = genetics;
    }
    /**
     * @hibernate.property
     * @return Returns the histology.
     */
    public boolean getHistology() {
        return histology;
    }
    /**
     * @param histology The histology to set.
     */
    public void setHistology(boolean histology) {
        this.histology = histology;
    }
    /**
     * @hibernate.property
     * @return Returns the lifehistory.
     */
    public boolean getLifehistory() {
        return lifehistory;
    }
    /**
     * @param lifehistory The lifehistory to set.
     */
    public void setLifehistory(boolean lifehistory) {
        this.lifehistory = lifehistory;
    }
    /**
     * @hibernate.property
     * @return Returns the methods.
     */
    public boolean getMethods() {
        return methods;
    }
    /**
     * @param methods The methods to set.
     */
    public void setMethods(boolean methods) {
        this.methods = methods;
    }
    /**
     * @hibernate.property
     * @return Returns the molecular.
     */
    public boolean getMolecular() {
        return molecular;
    }
    /**
     * @param molecular The molecular to set.
     */
    public void setMolecular(boolean molecular) {
        this.molecular = molecular;
    }
    /**
     * @hibernate.property
     * @return Returns the morphology.
     */
    public boolean getMorphology() {
        return morphology;
    }
    /**
     * @param morphology The morphology to set.
     */
    public void setMorphology(boolean morphology) {
        this.morphology = morphology;
    }
    /**
     * @hibernate.property
     * @return Returns the neurobiology.
     */
    public boolean getNeurobiology() {
        return neurobiology;
    }
    /**
     * @param neurobiology The neurobiology to set.
     */
    public void setNeurobiology(boolean neurobiology) {
        this.neurobiology = neurobiology;
    }
    /**
     * @hibernate.property
     * @return Returns the paleobiology.
     */
    public boolean getPaleobiology() {
        return paleobiology;
    }
    /**
     * @param paleobiology The paleobiology to set.
     */
    public void setPaleobiology(boolean paleobiology) {
        this.paleobiology = paleobiology;
    }
    /**
     * @hibernate.property
     * @return Returns the phylogenetics.
     */
    public boolean getPhylogenetics() {
        return phylogenetics;
    }
    /**
     * @param phylogenetics The phylogenetics to set.
     */
    public void setPhylogenetics(boolean phylogenetics) {
        this.phylogenetics = phylogenetics;
    }
    /**
     * @hibernate.property
     * @return Returns the physiology.
     */
    public boolean getPhysiology() {
        return physiology;
    }
    /**
     * @param physiology The physiology to set.
     */
    public void setPhysiology(boolean physiology) {
        this.physiology = physiology;
    }
    /**
     * @hibernate.property
     * @return Returns the taxonomy.
     */
    public boolean getTaxonomy() {
        return taxonomy;
    }
    /**
     * @param taxonomy The taxonomy to set.
     */
    public void setTaxonomy(boolean taxonomy) {
        this.taxonomy = taxonomy;
    }
    /**
     * @hibernate.id column="keyword_id"  generator-class="native"
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
}
