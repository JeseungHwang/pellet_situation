package selab.nsaf.sa.vo;

public class LinkVO {
	private String linkID;
	private String SrcSwitchDPID;
	private int SrcPort;
	private String DstSwitchDPID;
	private int DstPort;
	private String state;
	private String direction;
	
	public String getLinkID() {
		return linkID;
	}
	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}
	public String getSrcSwitchDPID() {
		return SrcSwitchDPID;
	}
	public void setSrcSwitchDPID(String srcSwitchDPID) {
		SrcSwitchDPID = srcSwitchDPID;
	}
	public int getSrcPort() {
		return SrcPort;
	}
	public void setSrcPort(int srcPort) {
		SrcPort = srcPort;
	}
	public String getDstSwitchDPID() {
		return DstSwitchDPID;
	}
	public void setDstSwitchDPID(String dstSwitchDPID) {
		DstSwitchDPID = dstSwitchDPID;
	}
	public int getDstPort() {
		return DstPort;
	}
	public void setDstPort(int dstPort) {
		DstPort = dstPort;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	@Override
	public String toString() {
		return "LinkVO [linkID=" + linkID + ", SrcSwitchDPID=" + SrcSwitchDPID
				+ ", SrcPort=" + SrcPort + ", DstSwitchDPID=" + DstSwitchDPID
				+ ", DstPort=" + DstPort + ", state=" + state + ", direction="
				+ direction + "]";
	}

}
