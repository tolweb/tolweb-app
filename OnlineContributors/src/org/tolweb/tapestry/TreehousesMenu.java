/*
 * Created on Jul 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.MappedAccessoryPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehousesMenu extends LinkedPages {
	@SuppressWarnings("unchecked")
	public abstract void setArticles(List value);
	@SuppressWarnings("unchecked")
    public abstract void setInvestigations(List value);
    @SuppressWarnings("unchecked")
    public abstract List getInvestigations();
    @SuppressWarnings("unchecked")
    public abstract void setStories(List value);
    @SuppressWarnings("unchecked")
    public abstract List getStories();
    @SuppressWarnings("unchecked")
    public abstract void setFunAndGames(List value);
    @SuppressWarnings("unchecked")
    public abstract List getFunAndGames();
    @SuppressWarnings("unchecked")
    public abstract void setArtAndCulture(List value);
    @SuppressWarnings("unchecked")
    public abstract List getArtAndCulture();
    @SuppressWarnings("unchecked")
    public abstract void setTeacherResources(List value);
    @SuppressWarnings("unchecked")
    public abstract List getTeacherResources();
    @SuppressWarnings("unchecked")
    public abstract void setBiographies(List value);
    @SuppressWarnings("unchecked")
    public abstract List getBiographies();
    @SuppressWarnings("unchecked")
    public abstract void setWebquests(List value);
    @SuppressWarnings("unchecked")
    public abstract List getWebquests();
    @SuppressWarnings("unchecked")
    public abstract void setPortfolios(List value);
    @SuppressWarnings("unchecked")
    public abstract List getPortfolios();
	public abstract Object[] getCurrentTreehouse();  
	
	@SuppressWarnings("unchecked")
	public void prepareForRender(IRequestCycle cycle) {
        Iterator it = getTreehouses().iterator();
        List investigations = new ArrayList(), stories = new ArrayList(), funAndGames = new ArrayList(),
        	artAndCulture = new ArrayList(), teacherResources = new ArrayList(), biographies = new ArrayList(),
        	portfolios = new ArrayList(), webquests = new ArrayList();
        while (it.hasNext()) {
            Object[] nextTreehouse = (Object[]) it.next();	
            Byte type = (Byte) nextTreehouse[2];
            byte byteType = type.byteValue();
            switch (byteType) {
            	case MappedAccessoryPage.ARTANDCULTURE: artAndCulture.add(nextTreehouse); break;
            	case MappedAccessoryPage.BIOGRAPHY: biographies.add(nextTreehouse); break;
            	case MappedAccessoryPage.GAME: funAndGames.add(nextTreehouse); break;
            	case MappedAccessoryPage.INVESTIGATION: investigations.add(nextTreehouse); break;
            	case MappedAccessoryPage.STORY: stories.add(nextTreehouse); break;
            	case MappedAccessoryPage.TEACHERRESOURCE: teacherResources.add(nextTreehouse); break;
            	case MappedAccessoryPage.PORTFOLIO: portfolios.add(nextTreehouse); break;
            	case MappedAccessoryPage.WEBQUEST: webquests.add(nextTreehouse); break;
            }
        }
        setArtAndCulture(artAndCulture);
        setBiographies(biographies);
        setFunAndGames(funAndGames);
        setInvestigations(investigations);
        setStories(stories);
        setTeacherResources(teacherResources);
        setPortfolios(portfolios);
        setWebquests(webquests);   
	}
	
    public boolean getHasInvestigations() {
        return getInvestigations().size() > 0;
    }
    public boolean getHasStories() {
        return getStories().size() > 0;
    }
    public boolean getHasArtAndCulture() {
        return getArtAndCulture().size() > 0;
    }
    public boolean getHasTeacherResources() {
        return getTeacherResources().size() > 0;
    }
    public boolean getHasBiographies() {
        return getBiographies().size() > 0;
    }	
	
	public String getLiTreehouseClass() {
	    return getLiBonusPageClass(getCurrentTreehouse());
	}	
}
