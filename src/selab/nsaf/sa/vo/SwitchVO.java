package selab.nsaf.sa.vo;

public class SwitchVO {
	private String switchID;
	private String switchDPID;
	private String state;
	private double bandwidth;
	private double delay;
	private double packetloss;
	private double jitter;
	public String getSwitchID() {
		return switchID;
	}
	public void setSwitchID(String switchID) {
		this.switchID = switchID;
	}
	public String getSwitchDPID() {
		return switchDPID;
	}
	public void setSwitchDPID(String switchDPID) {
		this.switchDPID = switchDPID;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public double getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}
	public double getDelay() {
		return delay;
	}
	public void setDelay(double delay) {
		this.delay = delay;
	}
	public double getPacketloss() {
		return packetloss;
	}
	public void setPacketloss(double packetloss) {
		this.packetloss = packetloss;
	}
	public double getJitter() {
		return jitter;
	}
	public void setJitter(double jitter) {
		this.jitter = jitter;
	}
	@Override
	public String toString() {
		return "SwitchVO [switchID=" + switchID + ", switchDPID=" + switchDPID
				+ ", state=" + state + ", bandwidth=" + bandwidth + ", delay="
				+ delay + ", packetloss=" + packetloss + ", jitter=" + jitter
				+ "]";
	}
}
