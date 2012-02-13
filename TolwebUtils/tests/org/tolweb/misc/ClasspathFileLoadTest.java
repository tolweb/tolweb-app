package org.tolweb.misc;

import java.io.InputStream;

import junit.framework.TestCase;

public class ClasspathFileLoadTest extends TestCase {

	public void test_load_file_as_resource() {
		InputStream inputDoc = getClass().getResourceAsStream("./dbconnection.properties");
		assertNotNull(inputDoc);
	}
}
