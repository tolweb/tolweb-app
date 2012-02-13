/*
 * Created on Jun 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.treegrow.main.Keywords;
import org.tolweb.treegrow.main.Languages;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseLearningInfo extends BaseComponent implements TreehouseInjectable, BaseInjectable {
    private static final String LIST_SEPARATOR = ";";
    
    public abstract int getIndex();
    public abstract boolean getIsTeacherResource();
    public abstract void setIsTeacherResource(boolean value);
    public abstract MappedAccessoryPage getTreehouse();
    
    public MappedAccessoryPage getTeacherResource() {
        return getTreehouse();
    }
    
    public void prepareForRender(IRequestCycle cycle) {
        super.prepareForRender(cycle);
        MappedAccessoryPage treehouse = getTreehouse();
        boolean isTeacherResource = TeacherResource.class.isInstance(treehouse); 
        setIsTeacherResource(isTeacherResource);
    }
    
    @SuppressWarnings("unchecked")
    public String getLearnerLevel() {
        List strings = new ArrayList();
        if (getTreehouse().getIsBeginnerLevel()) {
            strings.add("Beginner");
        }
        if (getTreehouse().getIsIntermediateLevel()) {
            strings.add("Intermediate");
        }
        if (getTreehouse().getIsAdvancedLevel()) {
            strings.add("Advanced");
        }
        return StringUtils.returnJoinedString(strings, LIST_SEPARATOR);
    }
    
    @SuppressWarnings("unchecked")
    public List getObjectiveList() {
    	TeacherResource resource = (TeacherResource) getTeacherResource();
        return getTextPreparer().getNewlineSeparatedList(resource.getLearningObjective());
    }
    
    public String getObjectiveString() {
        return getUlListStringWithSemicolons(getObjectiveList(), null);
    }
    
    public boolean getHasObjective() {
    	TeacherResource resource = (TeacherResource) getTeacherResource();
        return StringUtils.notEmpty(resource.getLearningObjective());
    }
    
    @SuppressWarnings("unchecked")
    public String getResourceType() {
        List strings = new ArrayList();
        if (getTeacherResource().getIsClassroom()) {
            strings.add("Classroom resource");
        }
        if (getTeacherResource().getIsLab()) {
            strings.add("Lab resource");
        }
        if (getTeacherResource().getIsField()) {
            strings.add("Field resource");
        }
        if (getTeacherResource().getIsFieldTrip()) {
            strings.add("Field Trip resource");
        }
        if (getTeacherResource().getIsWebBased()) {
            strings.add("Web-based resource");
        }
        if (getTeacherResource().getIsHomeBased()) {
            strings.add("Home-based resource");
        }
        if (getTeacherResource().getIsInformal()) {
            strings.add("Informal learning resource");
        }
        if (getTeacherResource().getIsMuseumBased()) {
            strings.add("Museum-based resource");
        }
        return StringUtils.returnJoinedString(strings, LIST_SEPARATOR);
    }
    
    @SuppressWarnings("unchecked")
    public List getKeywordsList() {
        List strings = new ArrayList();
        Keywords key = getTeacherResource().getKeywords();
        if (key.getEvolution()) {
            strings.add("Evolution");
        }
        if (key.getPhylogenetics()) {
            strings.add("Phylogenetics");
        }
        if (key.getTaxonomy()) {
            strings.add("Taxonomy");
        }
        if (key.getBiodiversity()) {
            strings.add("Biodiversity");
        }
        if (key.getEcology()) {
            strings.add("Ecology");
        }
        if (key.getConservation()) {
            strings.add("Conservation Biology");
        }
        if (key.getBiogeography()) {
            strings.add("Biogeography");
        }
        if (key.getPaleobiology()) {
            strings.add("Paleobiology");
        }
        if (key.getMorphology()) {
            strings.add("Morphology & Anatomy");
        }
        if (key.getLifehistory()) {
            strings.add("Life History & Development");
        }
        if (key.getPhysiology()) {
            strings.add("Physiology");
        }
        if (key.getNeurobiology()) {
            strings.add("Neurobiology & Behavior");
        }
        if (key.getHistology()) {
            strings.add("Histology & Cell Biology");
        }
        if (key.getGenetics()) {
            strings.add("Genetics & Heredity");
        }
        if (key.getMolecular()) {
            strings.add("Molecular Biology & Biochemistry");
        }
        if (key.getMethods()) {
            strings.add("Methods, Techniques, Apparatus");
        }
        if (StringUtils.notEmpty(key.getAdditionalKeywords())) {
            strings.add(key.getAdditionalKeywords());
        }
        return strings;
    }
    
    public String getKeywordsString() {
        return getUlListStringWithSemicolons(getKeywordsList(), null);
    }
    
    @SuppressWarnings("unchecked")
    public List getStateStandardsSubjects() {
        return getTextPreparer().getNewlineSeparatedList(getTeacherResource().getStateStandardsSubjects());
    }
    
    public String getStateStandardsSubjectsString() {
        return getUlListStringWithSemicolons(getStateStandardsSubjects(), null);
    }
    
    public String getNationalStandardsSubjectsString() {
        return getUlListStringWithSemicolons(getNationalStandardsSubjects(), null);
    }
    
    @SuppressWarnings("unchecked")
    public List getNationalStandardsSubjects() {
        return getTextPreparer().getNewlineSeparatedList(getTeacherResource().getNationalStandardsSubjects());
    }
       
    public boolean getHasResourceType() {
        return StringUtils.notEmpty(getResourceType());
    }
    
    public boolean getHasTimeFrame() {
        return StringUtils.notEmpty(getTeacherResource().getTimeFrame());
    }
    
    public boolean getHasSequence() {
        return StringUtils.notEmpty(getTeacherResource().getSequenceContext());
    }
    
    @SuppressWarnings("unchecked")
    public String getSequenceString() {
        List sequenceList = getTextPreparer().getNewlineSeparatedList(getTeacherResource().getSequenceContext());
        return getUlListStringWithSemicolons(sequenceList, null);
    }
    
    @SuppressWarnings("unchecked")
    public List getTimeFrameList() {
        return getTextPreparer().getNewlineSeparatedList(getTeacherResource().getTimeFrame());
    }
    
    public String getTimeFrameString() {
        return getUlListStringWithSemicolons(getTimeFrameList(), null);
    }
    
    public boolean getHasAdditionalTypes() {
        return getAdditionalTypes().size() > 0;
    }
    
    @SuppressWarnings("unchecked")
    public List getAdditionalTypes() {
        return getTreehouse().getAdditionalTypesList();
    }
    
    public String getAdditionalTypesString() {
        return getUlListStringWithSemicolons(getAdditionalTypes(), null);
    }
    
    public boolean getHasCurricularAreas() {
        return getCurricularAreas().size() > 0;
    }
    
    @SuppressWarnings("unchecked")
    public List getCurricularAreas() {
        return getTeacherResource().getCurricularAreas();
    }
    
    public String getCurricularAreasString() {
        return getUlListStringWithSemicolons(getCurricularAreas(), null);
    }
    
    @SuppressWarnings("unchecked")
    public List getLanguages() {
        Languages langs = getTeacherResource().getLanguages();
        ArrayList strings = new ArrayList();
        if (langs.getEnglish()) {
            strings.add("English");
        }
        if (langs.getFrench()) {
            strings.add("French");
        }
        if (langs.getGerman()) {
            strings.add("German");
        }
        if (langs.getSpanish()) {
            strings.add("Spanish");
        }
        if (StringUtils.notEmpty(langs.getOtherLanguage())) {
            strings.add(langs.getOtherLanguage());
        }
        return strings;        
    }
    
    public String getLanguagesString() {
        return getUlListStringWithSemicolons(getLanguages(), null);
    }
    
    @SuppressWarnings("unchecked")
    public List getStrategies() {
        ArrayList strings = new ArrayList();
        if (getTeacherResource().getIsInquiryLearning()) {
            strings.add("Inquiry Learning");
        }
        if (getTeacherResource().getIsHandsOnLearning()) {
            strings.add("Hands-on Learning");
        }
        if (getTeacherResource().getIsVisualInstruction()) {
            strings.add("Visual Instruction");
        }
        if (getTeacherResource().getIsRolePlaying()) {
            strings.add("Role-Playing");
        }
        if (getTeacherResource().getIsMovement()) {
            strings.add("Movement, Play, Games");
        }
        if (getTeacherResource().getIsTechnologyIntegration()) {
            strings.add("Technology Integration/Computer Assisted Instruction");
        }
        if (getTeacherResource().getIsLearningModules()) {
            strings.add("Learning Modules/Centers");
        }
        if (getTeacherResource().getIsDiscussion()) {
            strings.add("Discussion");
        }
        if (getTeacherResource().getIsDemonstration()) {
            strings.add("Demonstration");
        }
        if (getTeacherResource().getIsLecture()) {
            strings.add("Lecture");
        }
        if (getTeacherResource().getIsPresentation()) {
            strings.add("Presentation");
        }
        if (StringUtils.notEmpty(getTeacherResource().getOtherLearningStrategy())) {
            strings.add(getTeacherResource().getOtherLearningStrategy());
        }
        return strings;
    }
    
    public String getStrategiesString() {
        return getUlListStringWithSemicolons(getStrategies(), null);
    }
    
    @SuppressWarnings("unchecked")
    public List getGroupings() {
        List strings = new ArrayList();
        if (getTeacherResource().getIsIndividualized()) {
            strings.add("Individualized instruction");
        }
        if (getTeacherResource().getIsLargeGroup()) {
            strings.add("Large Group instruction");
        }
        if (getTeacherResource().getIsSmallGroup()) {
            strings.add("Small Group instruction");
        }
        if (getTeacherResource().getIsHeterogeneous()) {
            strings.add("Heterogeneous grouping");
        }
        if (getTeacherResource().getIsHomogeneous()) {
            strings.add("Homogeneous grouping");
        }
        if (getTeacherResource().getIsCrossAge()) {
            strings.add("Cross age or multi-age teaching");
        }
        return strings;
    }
    
    public String getGroupingsString() {
        return getUlListStringWithSemicolons(getGroupings(), null);
    }
    
    public boolean getHasComments() {
        return StringUtils.notEmpty(getTreehouse().getComments());
    }
    
    @SuppressWarnings("unchecked")
    private String getUlListStringWithSemicolons(List list, String liClass) {
        return getTextPreparer().getUlListStringWithSemicolons(list, liClass);
    }
}
