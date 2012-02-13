package org.tolweb.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.tolweb.dao.ApplicationContextTestAbstract;

public class SpamListMaint extends ApplicationContextTestAbstract {

	public SpamListMaint(String name) {
		super(name);
	}

	public void test() {
		InputStream inputDoc = getClass().getResourceAsStream("./spam_blocklist.txt");
		assertNotNull(inputDoc);
		Scanner scnr = new Scanner(inputDoc);
		List<String> lines = new ArrayList<String>();
		while(scnr.hasNextLine()) {
			lines.add(scnr.nextLine());
		}
		System.out.println(lines.size());
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		for(String email : lines) {
			String[] pieces = email.split("@");
			if (!map.containsKey(pieces[1])) {
				map.put(pieces[1], new ArrayList<String>(Arrays.asList(new String[]{pieces[0]})));
			} else {
				map.get(pieces[1]).add(pieces[0]);
			}
		}
		for(Map.Entry<String, List<String>> entry : map.entrySet()) {
			if (entry.getValue() != null && entry.getValue().size() > 1) {
				System.out.println(entry.getKey() + "=" + entry.getValue());
			}
		}
	}
	
	public static String slurp (InputStream in) throws IOException {
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	}	
}
