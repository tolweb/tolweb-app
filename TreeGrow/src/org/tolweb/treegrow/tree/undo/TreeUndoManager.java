package org.tolweb.treegrow.tree.undo;

import javax.swing.undo.*;
import java.util.*;

/**
 * Extends UndoManager, setting undo limit to 2000
 */
public class TreeUndoManager extends UndoManager {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4767272362013893434L;
	private static final int MAX_UNDOS = 2000;
    
    /** Creates a new instance of TreeUndoManager */
    public TreeUndoManager() {
        super();
        setLimit(MAX_UNDOS);
    }
    
    public String toString() {
        Iterator it = edits.iterator();
        int index = 0;
        String returnString = "";
        while (it.hasNext()) {
            returnString += "--- " + index++ + " --- " + it.next().toString() + "\n\n";
        }
        return returnString;
    }
}
