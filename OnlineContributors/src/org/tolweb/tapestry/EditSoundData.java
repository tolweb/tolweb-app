/*
 * Created on May 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.Sound;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditSoundData extends EditImageData implements PageBeginRenderListener {
    private static final String NO_SOUND_TYPE = "Select a sound type";
    
    public static IPropertySelectionModel SOUNDTYPE_MODEL;
    
    public Sound getSound() {
        return (Sound) getImage();
    }
    
    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        if (SOUNDTYPE_MODEL == null) {
            SOUNDTYPE_MODEL = getPropertySelectionFactory().createModelFromList(Sound.SOUND_TYPES_LIST, NO_SOUND_TYPE);
        }
    }
    
    public String getHelpPageName() {
        return "SoundHelpMessagesPage";
    }
    
    public String getMediaType() {
        return ImageSearch.SOUND;
    }
    
    protected String getNoMediaTypeString() {
        return NO_SOUND_TYPE;
    }    
}
