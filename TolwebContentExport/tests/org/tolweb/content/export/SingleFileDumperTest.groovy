package org.tolweb.content.export

class SingleFileDumperTest extends AbstractApplicationTestContext {
	SingleFileDumper sfd
	
	void setUp() {
		HostInfo hi = (HostInfo) context.getBean("hostInfo")
		ServicesInfo si = (ServicesInfo) context.getBean("servicesInfo")
		Exporter exp = new Exporter(hi, si)
		exp.rootNodeId = 15994 // Marsupials 
		DropFileInfo dfi = (DropFileInfo) context.getBean("dropFileInfo")
		sfd = new SingleFileDumper(exp, dfi)
	}
	
	void test_initialization() {
		assert sfd
	}
	
	void test_dump_file_returned() {
		File f = sfd.dumpFile() 
		assert f
		assertEquals f.name, sfd.fileInfo.fileName
	}
	
	void test_dump_file_created_on_disk() {
		File f = sfd.dumpFile()
		assert new File(sfd.fileInfo.fullPath).exists()
	}
	
	void off_test_running_for_life_on_earth() {
		sfd.basalNodeIdForFileDump(1) // 1 = life on earth
		writeStartTimeToFile()
		
		File f = sfd.dumpFile()
		assert new File(sfd.fileInfo.fullPath).exists()		
		assert f.exists()
		
		writeStopTimeToFile()
	}

	void test_running_for_mammals_and_archiving() {
		sfd.basalNodeIdForFileDump(15040) // 15040 = mammals 14952 = Terrestrial Vertebrates
		writeTimeToDumpDirFile("mammals-start.out")
		
		sfd.fileInfo.fileName = 'mammals-nc-all.xml'
		
		File f = sfd.dumpFile()
		assert new File(sfd.fullPath).exists()
		assert f.exists()
		
		writeTimeToDumpDirFile("mammals-stop.out")
	}
	
	void writeStartTimeToFile() {
		writeTimeToFile(sfd.fileInfo.location, "starttime.out")
	}
	
	void writeStopTimeToFile() {
		writeTimeToFile(sfd.fileInfo.location, "stoptime.out")
	}
	
	void writeTimeToFile(location, filename) {
		new File(location+filename).withWriter { 
			it.writeLine(new Date().toString())
		}		
	}
	
	void writeTimeToDumpDirFile(filename) {
		writeTimeToFile(sfd.fileInfo.location, filename)
	}
	
	void tearDown() {
		sfd = null
	}
}