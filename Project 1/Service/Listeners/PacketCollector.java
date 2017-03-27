package Service.Listeners;

import java.util.ArrayList;

public class PacketCollector extends ArrayList<DatedMessage> {

	public void deleteOlderThan(long time) {
		long currentTime = System.currentTimeMillis();
		ArrayList<DatedMessage> toBeRemoved = new ArrayList();

		for (DatedMessage x : this) {
			if ((x.getTime() + time) < currentTime) {
				toBeRemoved.add(x);
			}
		}

		this.removeAll(toBeRemoved);
	}

	public ArrayList<String> getLastMessages() {
		ArrayList<String> ret = new ArrayList();

		for (DatedMessage x : this) {

			ret.add(x.getMessage());

		}
		return ret;
	}

}
