package selab.nsaf.sa.vo;

import java.util.Arrays;

public class NodeVO {
	private SwitchVO Switch[];
	private HostVO Host[];

	public SwitchVO[] getSwitch() {
		return Switch;
	}
	public void setSwitch(SwitchVO[] Switch) {
		this.Switch = Switch;
	}
	public HostVO[] getHost() {
		return Host;
	}
	public void setHost(HostVO[] host) {
		this.Host = host;
	}
	public int getHostCnt(){
		return Host.length;
	}
	public int getSwitchCnt(){
		return Switch.length;
	}
	@Override
	public String toString() {
		return "NodeVO [Switch=" + Arrays.toString(Switch) + ", Host="
				+ Arrays.toString(Host) + "]";
	}
}
