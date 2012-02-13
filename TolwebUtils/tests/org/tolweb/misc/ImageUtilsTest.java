/*
 * ImageUtilsTest.java
 *
 * Created on May 2, 2004, 1:51 PM
 */

package org.tolweb.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;

/**
 *
 * @author  dmandel
 */
public class ImageUtilsTest extends ApplicationContextTestAbstract {
    private static final String TEST_DIRECTORY = "/tmp/";
    private ImageUtils imgUtils;
    private NodeImage img, img2, img3;
    
    public ImageUtilsTest(String name) {
        super(name);
        System.out.println("library path is: " + System.getProperty("java.library.path"));
        imgUtils = (ImageUtils) context.getBean("imageUtils");
        constructImages();
    }
    
    private void constructImages() {
        img = new NodeImage();
        img.setId(1);
        img.setLocation("Calystegia_dahurica.jpg");
        img2 = new NodeImage();
        img2.setId(2);
        img2.setLocation("logo_java.gif");
        img3 = new NodeImage();
        img3.setId(3);
        img3.setLocation("pnglogo-blk-sml1.png");
    }
    
    public void testGetUniqueServerFilename() {
        doFileTests("johncoltranerules.txt", "johncoltranerules1.txt", "johncoltranerules2.txt");
        doFileTests("charlieparkerbops", "charlieparkerbops1", "charlieparkerbops2");
        doFileTests("c:\\my documents\\somefile.jpg", "somefile1.jpg", "somefile2.jpg");
    }
    
    public void testGetDimensions() {
        constructImages();
        imgUtils.getImageDimensions(img);
        imgUtils.getImageDimensions(img2);
        imgUtils.getImageDimensions(img3);
    }
    
    public void testGetImageVersionFilename() {
        String originalFilename = "foo.jpg";
        assertEquals(imgUtils.getImageVersionFilename(originalFilename, 100), "foo.100a.jpg");
        assertEquals(imgUtils.getImageVersionFilename(originalFilename, 500), "foo.500a.jpg");
    }
    
    public void testGetFileSizeStringFromInt() {
        int twoKb = 2049;
        assertEquals(imgUtils.getFileSizeStringFromInt(twoKb), "2K");
        int overTwoMb = 2202010;
        assertEquals(imgUtils.getFileSizeStringFromInt(overTwoMb), "2.1M");
        int oneMb = 1048576;
        assertEquals(imgUtils.getFileSizeStringFromInt(oneMb), "1.0M");
    }
    
    public void testFillingOutImageVersions() {
        // First case.  Create a new image and give it a master version of over 500px high.  
        // Ensure that it has versions in place for 500-100px.
        NodeImage someNewImage = new NodeImage();
        ImageVersion newImageVersion = new ImageVersion();
        newImageVersion.setHeight(new Integer(577));
        ArrayList versions = new ArrayList();
        versions.add(newImageVersion);
        ((ImageUtilsImpl) imgUtils).addMissingImageVersions(577, 100, versions, someNewImage);
        // Make sure that there are 10 versions now
        assertEquals(versions.size(), 10);
        // And make sure the second one is 500 px high and the last one is 100 px high
        ImageVersion version = (ImageVersion) versions.get(1);
        assertEquals(version.getHeight().intValue(), 500);
        version = (ImageVersion) versions.get(9);
        assertEquals(version.getHeight().intValue(), 100);
        
        // Now try one at 225 an ensure there are 4 versions
        someNewImage = new NodeImage();
        newImageVersion = new ImageVersion();
        newImageVersion.setHeight(new Integer(225));
        versions = new ArrayList();
        versions.add(newImageVersion);
        ((ImageUtilsImpl) imgUtils).addMissingImageVersions(225, 100, versions, someNewImage);
        assertEquals(versions.size(), 4);
        
        // Now check that versions get filled into a gap
        someNewImage = new NodeImage();
        versions = new ArrayList();
        initializeNewImageVersion(versions, 250);
        initializeNewImageVersion(versions, 200);
        initializeNewImageVersion(versions, 150);        
        initializeNewImageVersion(versions, 100);        
        ((ImageUtilsImpl) imgUtils).addMissingImageVersions(423, 250, versions, someNewImage);
        assertEquals(versions.size(), 7);
        
        // Now get rid of image versions (in the odd case that someone would upload a smaller
        // master image -- make sure that the larger versions get removed)
        someNewImage = new NodeImage();
        versions = new ArrayList();
        for (int i = 350; i >= 100; i -= 50) {
            initializeNewImageVersion(versions, i);
        }
        List removedVersions = ((ImageUtilsImpl) imgUtils).removeTooHighImageVersions(250, versions);
        assertEquals(removedVersions.size(), 2);
        assertEquals(versions.size(), 4);
    }
    
    private void initializeNewImageVersion(ArrayList versions, int height) {
        ImageVersion someVersion = new ImageVersion();
        someVersion.setHeight(new Integer(height));
        versions.add(someVersion);
    }
    
    private void doFileTests(String filename, String expectedDifferentFilename, String expectedSecondDifferentFilename) {
        String shouldBeUnique = filename;
        String shouldBeTheSame = imgUtils.getUniqueServerFilename(TEST_DIRECTORY, shouldBeUnique);
		if (filename.lastIndexOf('\\') == -1) {
			assertEquals(shouldBeUnique, shouldBeTheSame);
		}
        
        // Now create a file and verify that things get changed
        File file = new File(TEST_DIRECTORY, shouldBeTheSame);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            String helloWorld = "Hello World!";
            fos.write(helloWorld.getBytes());
            fos.close(); 
        } catch (Exception e) {
            
        }
        
        String shouldBeDifferent = imgUtils.getUniqueServerFilename(TEST_DIRECTORY, shouldBeUnique);
        assertEquals(shouldBeDifferent, expectedDifferentFilename);
        
        // Now create a 2nd file and verify that things get changed
        File file2 = new File(TEST_DIRECTORY, expectedDifferentFilename);
        try {
            FileOutputStream fos = new FileOutputStream(file2);
            String helloWorld = "Hello World!";
            fos.write(helloWorld.getBytes());
            fos.close(); 
        } catch (Exception e) {
            
        }        
        
        shouldBeDifferent = imgUtils.getUniqueServerFilename(TEST_DIRECTORY, shouldBeUnique);
        assertEquals(shouldBeDifferent, expectedSecondDifferentFilename);        
        
        file.delete();
        file2.delete();
    }
}
