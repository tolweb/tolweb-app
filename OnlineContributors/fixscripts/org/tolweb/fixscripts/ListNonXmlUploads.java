package org.tolweb.fixscripts;

import java.util.List;

import org.tolweb.treegrowserver.dao.UploadDAO;

public class ListNonXmlUploads extends AbstractFixScript {
	public static void main(String[] args) {
		ListNonXmlUploads lister = new ListNonXmlUploads();
		lister.listNonXmlUploads();
	}

	private void listNonXmlUploads() {
		UploadDAO dao = (UploadDAO) context.getBean("uploadDAO");
		List<Object[]> uploads = dao.getNonXmlRootNameContributorNameUploadDateAndUploadContents();
		for (Object[] objects : uploads) {
			System.out.println("non xml upload rooted at " + objects[0] + " uploaded by " + objects[1] + objects[2] + " on date " + objects[3] + " is \n" + objects[4] + "\n");
		}
	}
}
