package org.tolweb.content.export.runner

import org.tolweb.content.export.*
import org.springframework.context.ApplicationContext
import org.tolweb.misc.ApplicationContextFactory

class ExportRunner {
	protected ApplicationContext context
	SingleFileDumper mainDumper
	
	static {
	 	ApplicationContextFactory.init("./applicationContext.xml", true);	 	
	}
 
 	public ExportRunner() {
 		super()
 		context = ApplicationContextFactory.getApplicationContext();
 		initialize()
 	}	
	
 	void initialize() {
		HostInfo hi = (HostInfo) context.getBean("hostInfo")
		ServicesInfo si = (ServicesInfo) context.getBean("servicesInfo")
		DropFileInfo dfi = (DropFileInfo) context.getBean("dropFileInfo")
		
		Exporter exp = new Exporter(hi, si)

		mainDumper = new SingleFileDumper(exp, dfi)
 	}

 	void run() {
 		mainDumper.dumpFile()
 	}
 	
	public static void main(String[] args) {
		ExportRunner runner = new ExportRunner()
		runner.run()
		println "ExportRunner: executed..."		
	}
}