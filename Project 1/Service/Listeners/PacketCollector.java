package Service.Listeners;

import java.util.ArrayList;

public class PacketCollector {
	
	private ArrayList<DatedMessage> packets;
	
	public PacketCollector() {
		packets = new ArrayList<DatedMessage>();
	}
	
	public PacketCollector(ArrayList<DatedMessage> mgs) {
		packets = mgs;
	}
	
	public void deleteOlderThan(long time) {
		long currentTime = System.currentTimeMillis();
		ArrayList<DatedMessage> toBeRemoved = new ArrayList<DatedMessage>();

		for (DatedMessage x : packets) {
			if ((x.getTime() + time) < currentTime) {
				toBeRemoved.add(x);
			}
		}

		packets.removeAll(toBeRemoved);
	}

	/*public ArrayList<String> getLastMessages() {
		ArrayList<String> ret = new ArrayList<String>();

		for (DatedMessage x : packets) {

			ret.add(x.getMessage());

		}
		return ret;
	}*/

	public int numPutchunks() {
		int ret = 0;
		
		for (DatedMessage x : packets) {
			if((x.getMessage().toUpperCase()).equals("PUTCHUNK"))
				ret++;
		}
		
		return ret;
	}
	
	public void add(DatedMessage msg) {
		packets.add(msg);
	}
}
