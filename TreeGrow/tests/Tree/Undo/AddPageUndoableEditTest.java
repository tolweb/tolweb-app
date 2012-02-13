/*
 * AddPageUndoableEditTest.java
 * JUnit based test
 *
 * Created on April 16, 2004, 3:29 PM
 */

import java.util.*;
import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.page.*;
import org.tolweb.treegrow.tree.*;
import org.tolweb.treegrow.tree.undo.*;
import junit.framework.*;

/**
 *
 * @author dmandel
 */
public class AddPageUndoableEditTest extends TestCase {
    private Node testNode;
    
    public AddPageUndoableEditTest(java.lang.String testName) {
        super(testName);
        TreePanel.getTreePanel();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(AddPageUndoableEditTest.class);
        return suite;
    }
    
    /**
     * Test of redo method, of class Tree.Undo.AddPageUndoableEdit.
     */
    public void testRedo() {
        AddPageUndoableEdit edit = new AddPageUndoableEdit(testNode);
        System.out.println("testRedo");
        assertTrue(testNode.hasPage());
        assertTrue(testNode.changedFromServer());
        assertNotNull(testNode.getPageObject());
        edit.undo();
        edit.redo();
        assertTrue(testNode.hasPage());
        assertTrue(testNode.changedFromServer());        
        assertNotNull(testNode.getPageObject());        
    }
    
    /**
     * Test of undo method, of class Tree.Undo.AddPageUndoableEdit.
     */
    public void testUndo() {
        boolean wasChanged = testNode.changedFromServer();
        boolean hadPage = testNode.hasPage();
        boolean hadPageOnServer = testNode.hasPageOnServer();
        AddPageUndoableEdit edit = new AddPageUndoableEdit(testNode);
        edit.undo();
        assertEquals(wasChanged,  testNode.changedFromServer());
        assertEquals(hadPage, testNode.hasPage());
        assertEquals(hadPageOnServer, testNode.hasPageOnServer());
    }
    
    /**
     * Test of updateTreePanel method, of class Tree.Undo.AddPageUndoableEdit.
     */
    public void testUpdateTreePanel() {
        System.out.println("testUpdateTreePanel");
        
        // TODO add your test code below by replacing the default call to fail.
        //fail("The test case is empty.");
    }
    
    /**
     * Test of getPresentationName method, of class Tree.Undo.AddPageUndoableEdit.
     */
    public void testGetPresentationName() {
        System.out.println("testGetPresentationName");
        
        // TODO add your test code below by replacing the default call to fail.
        //fail("The test case is empty.");
    }

    protected void setUp() {
        testNode = new Node(100);
    }    
    
    // TODO add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
    
}
