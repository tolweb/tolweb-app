/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.Movie;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditMovieData extends EditImageData implements PageBeginRenderListener {
    private static final String NO_MOVIE_TYPE = "Select a movie type";
    
    public static IPropertySelectionModel MOVIETYPE_MODEL;
    
    public Movie getMovie() {
        return (Movie) getImage();
    }
    
    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        if (MOVIETYPE_MODEL == null) {
            MOVIETYPE_MODEL = getPropertySelectionFactory().createModelFromList(Movie.MOVIE_TYPES_LIST, NO_MOVIE_TYPE);
        }
    }
    
    public String getHelpPageName() {
        return "MovieHelpMessagesPage";
    }
    
    public String getMediaType() {
        return ImageSearch.MOVIE;
    }
    
    protected String getNoMediaTypeString() {
        return NO_MOVIE_TYPE;
    }
}
