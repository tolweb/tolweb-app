
package org.tolweb.treegrow.main;

import java.beans.*;
import java.awt.*;
import javax.swing.*;
/**
 * see http://javaalmanac.com/egs/java.awt/focus_FocusEvents.html?l=rel 
 */
public class CurrentFocusListener implements PropertyChangeListener {
    private static Component mostRecentlyFocusedComponent;    
    
    
    public CurrentFocusListener() {
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        Component newComp = (Component)evt.getNewValue();
        if (newComp != null) { //gain of focus
            if (newComp instanceof JMenu || newComp instanceof JRootPane) {
                //System.out.println("Menu/Rootpane");
            } else {
                //System.out.println(newComp);
                mostRecentlyFocusedComponent = newComp;
            }
        }
    }
    
    public static Component getFocussedComponent(){
        return mostRecentlyFocusedComponent;
    }    
}




        
