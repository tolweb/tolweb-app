/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Date;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageValidateListener;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.EditHistory;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.treehouse.injections.BetaTreehouseInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseEditor extends AbstractTreehouseEditingPage implements IExternalPage, PageValidateListener, PageBeginRenderListener, 
		UserInjectable, BetaTreehouseInjectable{

	public abstract Contributor getCreatedContributor();
	public abstract void setCreatedContributor(Contributor value);
	public abstract Date getCreationDate();
	public abstract void setCreationDate(Date date);
	public abstract Contributor getLastEditedContributor();
	public abstract void setLastEditedContributor(Contributor lastEditedContributor);
	public abstract Date getLastEditedDate();
	public abstract void setLastEditedDate(Date lastEditedDate);	
	
	public void pageBeginRender(PageEvent event) {
		super.pageBeginRender(event);
		Long editHistoryId = (getTreehouse() != null) ? getTreehouse().getEditHistoryId() : null;
		ContributorDAO contrDAO = getContributorDAO();
		if (editHistoryId != null) {
			EditHistory history = getEditHistoryDAO().getEditHistoryWithId(editHistoryId);
			if (history.getCreatedContributorId() != null) {
				setCreatedContributor(contrDAO.getContributorWithId(history.getCreatedContributorId().toString()));
			}
			if (history.getLastEditedContributorId() != null) {
				setLastEditedContributor(contrDAO.getContributorWithId(history.getLastEditedContributorId().toString()));
			}
			setCreationDate(history.getCreationDate());
			setLastEditedDate(history.getLastEditedDate());
		}
	}
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters != null && parameters.length > 0) {
            Number treehouseId = (Number) parameters[0];
            MappedAccessoryPage treehouse = getWorkingAccessoryPageDAO().getAccessoryPageWithId(treehouseId.intValue());
            setTreehouse(treehouse);
        } else {
	        // In this case they are going to try out the tools
	        // So 1) create a contributor account for them &
	        //    2) create a new treehouse for them
	        Contributor newTestContributor = new Contributor();
	        newTestContributor.setFirstName("Future");
	        newTestContributor.setLastName("Treehouse Builder");
	        newTestContributor.setEmail("test@test.com");
	        newTestContributor.setContributorType(Contributor.TREEHOUSE_CONTRIBUTOR);
	        newTestContributor.setPassword("");
	        getBetaContributorDAO().addContributor(newTestContributor, getMiscEditHistoryDAO()); 
	        setContributor(newTestContributor);
	        setIsToolTryout(true);
	        getAccessoryPageHelper().initializeNewAccessoryPageInstance(newTestContributor, 
	                MappedAccessoryPage.INVESTIGATION, true, getBetaAccPageDAO(),
	                -1, true);
        }
    }
    
    public void pageValidate(PageEvent event) {
        
    }
    
    public void copyDataSelected(IRequestCycle cycle) {
        cycle.activate("CopyTreehouseDataSearch");        
    }
    
    public String getTitle() {
        if (getIsTeacherResource()) {
            if (getIsOther()) {
                return "TREEHOUSE EDITOR TEACHER RESOURCE MAIN PAGE Other Format";
            } else {
                return "TREEHOUSE EDITOR TEACHER RESOURCE MAIN PAGE";
            }
        } else {
            return "TREEHOUSE EDITOR MAIN PAGE";
        }
    }
    
    public String getTotalNumSteps() {
        if (getIsTeacherResource()) {
            if (getIsOther()) {
                return "9";
            } else {
                return "10";
            }
        } else if (getIsWebquest()) {
            return "11";
        } else {
            if (getIsPortfolio()) {
                return "9";
            } else {
                return "8";
            }
        } 
    }
    
    public Block getTeacherResourceDescriptionBlock() {
        String componentName;
        if (getIsOther()) {
            componentName = "otherResourceDescriptionBlock";
        } else if (getIsWebquest()) {
            componentName = "webquestBlock";
        } else {
            componentName = "lessonDescriptionBlock";
        }
        return (Block) getComponents().get(componentName);
    }

	public String getPublicUrl() {
		return getUrlBuilder().getPublicURLForObject(getTreehouse());
	}
}
