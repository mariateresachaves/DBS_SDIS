package Service.Protocols;


public class SetDisk {
	
	public SetDisk(String maximumDiskSpace) {
		// Just a Test
		System.out.println("Maximum disk space: " + maximumDiskSpace + "[KBytes]");
	}

	public void setValue(int maxStorage){
		Utils.Utils.getProperties().setProperty("MaxDiskSpace", maxStorage+"");
	}
	
}
