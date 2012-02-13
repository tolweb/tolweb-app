/*
 * Created on May 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.AbstractSupportMaterial;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.SupportMaterial;
import org.tolweb.hibernate.SupportMaterialDocument;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.misc.ReorderHelper;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.treegrow.main.StringUtils;


/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourceEditSupportMaterials extends
		AbstractTreehouseEditingPage implements PageBeginRenderListener, MiscInjectable  {
    public static final String PROGRESS_PROPERTY = "supportMaterialsProgress";
    private IPropertySelectionModel supportMaterialTypeModel;
    public abstract Integer getNumNewFields();
    public abstract void setNumNewFields(Integer value);
    public abstract void setAddSupportDocumentSelected(boolean value);
    public abstract boolean getAddSupportDocumentSelected();
    public abstract SupportMaterial getCurrentSupportMaterial();
    public abstract SupportMaterialDocument getCurrentSupportMaterialDocument();
    public abstract int getIndex();
    public abstract Integer getSupportMaterialMoveUpIndex();
    public abstract Integer getSupportMaterialMoveDownIndex();    
    public abstract Integer getSupportMaterialDeleteIndex();
    public abstract Integer getSupportMaterialDocumentMoveUpIndex();
    public abstract Integer getSupportMaterialDocumentMoveDownIndex();    
    public abstract Integer getSupportMaterialDocumentDeleteIndex();        
    
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}
    
    public TeacherResource getTeacherResource() {
        return (TeacherResource) getTreehouse();
    }
    
    public boolean getSupportMaterialCanMoveDown() {
        return getReorderHelper().getCanMoveRight(getTeacherResource().getSupportMaterials(), getIndex()) ||
        	getReorderHelper().getCanMoveBoth(getTeacherResource().getSupportMaterials(), getIndex());
    }
    
    public boolean getSupportMaterialCanMoveUp() {
        return getReorderHelper().getCanMoveLeft(getTeacherResource().getSupportMaterials(), getIndex()) ||
        getReorderHelper().getCanMoveBoth(getTeacherResource().getSupportMaterials(), getIndex());
    }
    
    public boolean getSupportMaterialDocumentCanMoveDown() {
        return getReorderHelper().getCanMoveRight(getTeacherResource().getSupportMaterialDocuments(), getIndex()) ||
        getReorderHelper().getCanMoveBoth(getTeacherResource().getSupportMaterialDocuments(), getIndex());
    }
    
    public boolean getSupportMaterialDocumentCanMoveUp() {
        return getReorderHelper().getCanMoveLeft(getTeacherResource().getSupportMaterialDocuments(), getIndex()) ||
        getReorderHelper().getCanMoveBoth(getTeacherResource().getSupportMaterialDocuments(), getIndex());
    }    
    
    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        TeacherResource treehouse = (TeacherResource) getTreehouse();
        if (treehouse.getSupportMaterials().size() == 0) {
            // Need to ensure that there is a single support material available
            // for editing, so make sure that's the case
            SupportMaterial newSupport = new SupportMaterial();
            getReorderHelper().addToSet(treehouse.getSupportMaterials(), newSupport);
            doSave();
        }
    }
    
    public void pageValidate(PageEvent event) {}
    
    @SuppressWarnings("unchecked")
    public IPropertySelectionModel getSupportMaterialTypeModel() {
        if (supportMaterialTypeModel == null) {
            final ArrayList list = new ArrayList();
            list.add("Select a support material type");
            list.add("Worksheet");
            list.add("Instructions");
            list.add("Vocabulary Words");
            list.add("Assessment");
            list.add("Rubric");
            list.add("Answer Key");
            list.add("Homework");
            list.add("Lecture Notes");
            list.add("Study Guide");
            list.add("Teacher Guide");
            list.add("Literature Review");
            list.add("Research Report");
            list.add("Data Set");
            list.add("Other");
            supportMaterialTypeModel = new IPropertySelectionModel() {

                public int getOptionCount() {
                    return list.size();
                }

                public Object getOption(int index) {
                    if (index == 0) {
                        return null;
                    } else {
                        return list.get(index);
                    }
                }

                public String getLabel(int index) {
                    return (String) list.get(index);
                }

                public String getValue(int index) {
                    return Integer.toString(index);
                }

                public Object translateValue(String value) {
                    int index = Integer.parseInt(value);
                    return getOption(index);
                }  
            };
        }
        return supportMaterialTypeModel;
    }
    
    public IPrimaryKeyConvertor getSupportMaterialConvertor() {
        return getSupportMaterialConvertor(((TeacherResource) getTreehouse()).getSupportMaterials());
    }
    
    public IPrimaryKeyConvertor getSupportMaterialDocumentConvertor() {
        return getSupportMaterialConvertor(((TeacherResource) getTreehouse()).getSupportMaterialDocuments());
    }
    
    @SuppressWarnings("unchecked")
    public IPrimaryKeyConvertor getSupportMaterialConvertor(final Set sourceSet) {
        return new IPrimaryKeyConvertor() {
            public Object getPrimaryKey(Object objValue) {
                AbstractSupportMaterial material = (AbstractSupportMaterial) objValue;
                return Integer.valueOf(material.getOrder());
            }

            public Object getValue(Object objPrimaryKey) {
                Integer order = (Integer) objPrimaryKey;
                if (order != null) {
	                for (Iterator iter = sourceSet.iterator(); iter.hasNext();) {
	                    AbstractSupportMaterial support = (AbstractSupportMaterial) iter.next();
	                    if (order.intValue() == support.getOrder()) {
	                        return support;
	                    }
	                }
                }
                return new SupportMaterial();
            }
        };
    }
    
    @SuppressWarnings("unchecked")
	public boolean doAdditionalFormProcessing(IRequestCycle cycle) {
	    ReorderHelper helper = getReorderHelper();
	    if(getNumNewFields() != null) {
	        int numFields = getNumNewFields().intValue();
	        SortedSet supportMaterials = ((TeacherResource) getTreehouse()).getSupportMaterials();
	        for (int i = 0; i < numFields; i++) {
	            SupportMaterial newMaterial = new SupportMaterial();
	            // set these since we don't allow nulls in the db
	            newMaterial.setText("");
	            newMaterial.setDocumentType("");
	            newMaterial.setTitle("");
                helper.addToSet(supportMaterials, newMaterial);
            }
	        // Clear it so there is nothing for the next time the form is refreshed.
	        setNumNewFields(null);
	    }
        if (getSupportMaterialDeleteIndex() != null) {
            getReorderHelper().removeObject(getSupportMaterialDeleteIndex().intValue(), getTeacherResource().getSupportMaterials());
        } else if (getSupportMaterialMoveUpIndex() != null) {
            getReorderHelper().doSwap(getSupportMaterialMoveUpIndex().intValue(), true, getTeacherResource().getSupportMaterials());
        } else if (getSupportMaterialMoveDownIndex() != null) {
            getReorderHelper().doSwap(getSupportMaterialMoveDownIndex().intValue(), false, getTeacherResource().getSupportMaterials());
        } else if (getSupportMaterialDocumentDeleteIndex() != null) {
            getReorderHelper().removeObject(getSupportMaterialDocumentDeleteIndex().intValue(), getTeacherResource().getSupportMaterialDocuments());
        } else if (getSupportMaterialDocumentMoveUpIndex() != null) {
            getReorderHelper().doSwap(getSupportMaterialDocumentMoveUpIndex().intValue(), true, getTeacherResource().getSupportMaterialDocuments());
        } else if (getSupportMaterialDocumentMoveDownIndex() != null) {
            getReorderHelper().doSwap(getSupportMaterialDocumentMoveDownIndex().intValue(), false, getTeacherResource().getSupportMaterialDocuments());
        }
	    TeacherResource resource = (TeacherResource) getTreehouse();
	    resource.setSupportMaterials(new TreeSet(resource.getSupportMaterials()));
	    resource.setSupportMaterialDocuments(new TreeSet(resource.getSupportMaterialDocuments()));	    
	    return true;
	} 
	
	public void addSupportDocument(IRequestCycle cycle) {
	    setAddSupportDocumentSelected(true);
	    setOtherEditPageName("TeacherResourceMediaSearchPage");
	}
	
	public void doAddSupportDocument(Document doc, IRequestCycle cycle) {
	    SupportMaterialDocument supportDoc = new SupportMaterialDocument();
	    supportDoc.setDocument(doc);
	    supportDoc.setDocumentType("");
	    ReorderHelper helper = getReorderHelper();
	    helper.addToSet(((TeacherResource) getTreehouse()).getSupportMaterialDocuments(), supportDoc);
	    doSave();
	    cycle.activate(this);
	}
	
	public String getCurrentDocumentString() {
	    SupportMaterialDocument supportDoc = getCurrentSupportMaterialDocument(); 
	    if (supportDoc != null) {
		    Document doc = supportDoc.getDocument();
		    if (StringUtils.notEmpty(doc.getTitle())) {
		        return doc.getTitle();
		    } else {
		        return doc.getLocation();
		    }
	    } else {
	        return "";
	    }
	}
	
	protected void goToOtherEditPageName(IRequestCycle cycle) {
        if (getOtherEditPageName().equals("TeacherResourceMediaSearchPage")) {
	        TeacherResourceMediaSearchPage page = (TeacherResourceMediaSearchPage) cycle.getPage("TeacherResourceMediaSearchPage");
            page.setCallbackType(ImageSearchResults.TR_SUPPORT_DOCUMENT_CALLBACK);
	        cycle.activate(page);
        } else {
            super.goToOtherEditPageName(cycle);
        }
    }	
}
