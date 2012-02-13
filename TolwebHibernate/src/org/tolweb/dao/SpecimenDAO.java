package org.tolweb.dao;

import java.util.Collection;
import java.util.List;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.Specimen;
import org.tolweb.treegrow.main.Contributor;

@Deprecated
public interface SpecimenDAO {
    public Specimen addSpecimen(MappedNode node, Contributor contr);
    public void saveSpecimens(List specimens);    
    public void saveSpecimen(Specimen specimen);
    public void deleteSpecimenWithId(Long specimenId);
    public List getSpecimensOnPage(MappedPage page);
    public boolean getNodeHasSpecimens(MappedNode node, int type, boolean includeDescendants);    
    public int getNumSpecimensAttachedToNodeIds(Collection nodeIds, int specimenType);
}
