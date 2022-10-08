package MessagePackage;

import HelperPackage.ConfigStructure;

public class SnapshotHandlerClass extends Thread{

	ConfigStructure conf;
	public SnapshotHandlerClass(ConfigStructure obj) {
		
		this.conf = obj;
		
	}
	
	public void run() {
		
		if(conf.Snapcount == 0) {
			
			conf.Snapcount ++;
		}
		else {
			try {
				conf.Snapcount ++;
				Thread.sleep(conf.snapshotDelay);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("\nSnap Shot Number - " + (conf.Snapcount));
		
		//Starting point for the Snapshot Protocol
		SnapshotProtocolClass.iniateSnapshot(conf);
	}
	
}
