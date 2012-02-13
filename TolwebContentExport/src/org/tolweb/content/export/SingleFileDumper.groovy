package org.tolweb.content.export

import java.io.File;

class SingleFileDumper {
	Exporter exporter 
	DropFileInfo fileInfo
	
	public SingleFileDumper(Exporter exp, DropFileInfo info) {
		exporter = exp
		fileInfo = info
	}
	
	File dumpFile() {
		File f = new File(fileInfo.fullPath)
		def output = exporter.export() 
		f.withWriter { out ->
			out.writeLine(output)
		}
		return f
	}
	
	void basalNodeIdForFileDump(Long nodeId) {
		exporter.rootNodeId = nodeId
	}
	
	String getFullPath() {
		return fileInfo.fullPath
	}
}