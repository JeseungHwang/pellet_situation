package selab.nsaf.sa.vo;

public class HostVO {
	private String hostID;
	private String state;
	public String getHostID() {
		return hostID;
	}
	public void setHostID(String hostID) {
		this.hostID = hostID;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "HostVO [hostID=" + hostID + ", state=" + state + "]";
	}
}
