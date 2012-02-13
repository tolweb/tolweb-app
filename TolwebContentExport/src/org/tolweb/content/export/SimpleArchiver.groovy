package org.tolweb.content.export

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream
import org.apache.tools.tar.TarOutputStream
import org.apache.tools.tar.TarEntry

class SimpleArchiver {
	String fullFilePath
/*	
	boolean create() {
		if (fullFilePath) {
			File originalFile = new File(fullFilePath);
			if (originalFile.exists()) {
				File archiveFile = new File(getArchiveFileName(fullFilePath))
				TarOutputStream tout = null
				try {
					tout = createStream(archiveFile)
					TarEntry entry = new TarEntry(originalFile) //new FileInputStream(originalFile))					
					tout.putNextEntry(entry)
					
					originalFile.readLines().each { line ->
						tout.write(line)
					}
					tout.closeEntry()
				} catch (Exception e) {
					
				} finally {
					tout?.close()
				} 
				return true;
			}
		}
		return false;
	}

	String getArchiveFileName(filename) {
		stripFileExtension(filename)+".tar"//".tar.gz"
	}
	
	String stripFileExtension(String filename) {
		if (filename?.lastIndexOf(".") > 0) {
			return filename?.substring(0, filename?.lastIndexOf("."))
		} else {
			return filename
		}
	}
	
	TarOutputStream createStream(File file) {
		//new TarOutputStream(new GZIPOutputStream(new FileOutputStream(file)))
		new TarOutputStream(new FileOutputStream(file))
	} */
}