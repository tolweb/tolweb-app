/*
 * Created on Jun 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Counter {
    private int count = 1;
    
    public int getNextStepNumber() {
        int currentStep = count;
        count++;
        return currentStep;
    }
    
    public void resetStepNumber() {
        count = 1;
    }
        
}
