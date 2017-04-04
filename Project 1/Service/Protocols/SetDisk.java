package Service.Protocols;

public class SetDisk {

	public SetDisk(String maximumDiskSpace) {
		// Just a Test
		System.out.println("Maximum disk space: " + maximumDiskSpace + "[KBytes]\n");
	}

	public void setValue(int maxStorage) {
		Utils.Util.getProperties().setProperty("MaxDiskSpace", maxStorage + "");
	}

}
