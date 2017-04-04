package Service.Listeners;

import java.util.ArrayList;

public class PacketCollector extends ArrayList<DatedMessage> {

	public void deleteOlderThan(long time) {
		long currentTime = System.currentTimeMillis();
		ArrayList<DatedMessage> toBeRemoved = new ArrayList<DatedMessage>();

		for (DatedMessage x : this) {
			if ((x.getTime() + time) < currentTime) {
				toBeRemoved.add(x);
			}
		}

		this.removeAll(toBeRemoved);
	}

	public ArrayList<String> getLastMessages() {
		ArrayList<String> ret = new ArrayList<String>();

		for (DatedMessage x : this) {

			ret.add(x.getMessage());

		}
		return ret;
	}

	public int numPutchunks() {
		int ret = 0;

		for (DatedMessage x : this) {
			if ((x.getMessage().toUpperCase()).equals("PUTCHUNK"))
				ret++;
		}

		return ret;
	}

	public int numStores(String fileID) {
		int ret = 0;
		//ArrayList<String> agents = new ArrayList<String>();

		for (DatedMessage x : this) {
			String msg = x.getMessage().toUpperCase();

			if (msg.startsWith("STORED")) {
				
				if (msg.split(" ")[3].trim().equalsIgnoreCase(fileID.trim())) {
					
						// falta verificar o peer que está a guardar se é o mesmo ou não
						//&& (!agents.contains(msg.split(" ")[2].trim()))) {
					
					ret++;
					//agents.add(msg.split(" ")[2].trim());
				}
			}
		}

		return ret;
	}

}
