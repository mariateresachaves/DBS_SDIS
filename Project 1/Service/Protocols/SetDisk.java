package Service.Protocols;


public class SetDisk {
	public SetDisk() {
	}

	public void setValue(int maxStorage){
		Utils.Utils.getProperties().setProperty("MaxDiskSpace", maxStorage+"");
	}
}
