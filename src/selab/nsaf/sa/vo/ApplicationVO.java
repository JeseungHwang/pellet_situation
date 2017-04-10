package selab.nsaf.sa.vo;

import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ApplicationVO {
	private String appName;
	private String appIP;
	private String appType;
	private String adminID;
	private double bandwidth;
	private double packetLoss;
	private double jitter;
	private double delay;
	private TopologyVO Topology;
	private LinkVO Link[];
	private NodeVO Node;
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppIP() {
		return appIP;
	}
	public void setAppIP(String appIP) {
		this.appIP = appIP;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public String getAdminID() {
		return adminID;
	}
	public void setAdminID(String adminID) {
		this.adminID = adminID;
	}
	public double getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}
	public double getPacketLoss() {
		return packetLoss;
	}
	public void setPacketLoss(double packetLoss) {
		this.packetLoss = packetLoss;
	}
	public double getJitter() {
		return jitter;
	}
	public void setJitter(double jitter) {
		this.jitter = jitter;
	}
	public double getDelay() {
		return delay;
	}
	public void setDelay(double delay) {
		this.delay = delay;
	}
	public TopologyVO getTopology() {
		return Topology;
	}
	public void setTopology(TopologyVO topology) {
		Topology = topology;
	}
	public LinkVO[] getLink() {
		return Link;
	}
	public void setLink(LinkVO[] link) {
		Link = link;
	}
	public NodeVO getNode() {
		return Node;
	}
	public void setNode(NodeVO node) {
		Node = node;
	}
	public int getLinkLength(){
		return Link.length;
	}
	
	public JSONObject getApplicationJSON(){
		JSONObject appJSONObj = new JSONObject();
		JSONObject appProfObj = new JSONObject();
		appProfObj.put("appName", appName);
		appProfObj.put("appIP", appIP);
		appProfObj.put("appType", appType);
		appProfObj.put("adminID", adminID);
		appProfObj.put("appName", appName);
		appProfObj.put("bandwidth", bandwidth);
		appProfObj.put("packetLoss", packetLoss);
		appProfObj.put("jitter", jitter);
		appProfObj.put("delay", delay);
		appJSONObj.put("application", appProfObj);
		
		JSONObject topoObj = new JSONObject();
		topoObj.put("topologyID", Topology.getTopologyID());
		topoObj.put("nodeCnt", Topology.getNodeCnt());
		topoObj.put("linkCnt", Topology.getLinkCnt());
		appJSONObj.put("topology", topoObj);
		
		JSONObject nodeObjArr = new JSONObject();
		JSONArray switchObjArr = new JSONArray();
		JSONArray hostObjArr = new JSONArray();
		for(int i=0;i<Node.getSwitchCnt();i++){
			JSONObject switchObj = new JSONObject();
			switchObj.put("switchID", Node.getSwitch()[i].getSwitchID());
			switchObj.put("switchDPID", Node.getSwitch()[i].getSwitchDPID());
			switchObj.put("state", Node.getSwitch()[i].getState());
			switchObj.put("bandwidth", Node.getSwitch()[i].getBandwidth());
			switchObj.put("delay", Node.getSwitch()[i].getDelay());
			switchObj.put("packetLoss", Node.getSwitch()[i].getPacketloss());
			switchObj.put("jitter", Node.getSwitch()[i].getJitter());
			switchObjArr.add(switchObj);
		}
		nodeObjArr.put("switch", switchObjArr);
		
		for(int i=0;i<Node.getHostCnt();i++){
			JSONObject hostObj = new JSONObject();
			hostObj.put("hostID", Node.getHost()[i].getHostID());
			hostObj.put("state", Node.getHost()[i].getState());
			hostObjArr.add(hostObj);
		}
		nodeObjArr.put("host", hostObjArr);
		appJSONObj.put("node", nodeObjArr);
		
		JSONArray linkObjArr = new JSONArray();
		for(int i=0;i<Link.length;i++){
			JSONObject linkObj = new JSONObject();
			linkObj.put("linkID", Link[i].getLinkID());
			linkObj.put("srcSwitchDPID", Link[i].getSrcSwitchDPID());
			linkObj.put("srcPort", Link[i].getSrcPort());
			linkObj.put("dstSwitchDPID", Link[i].getDstSwitchDPID());
			linkObj.put("dstPort", Link[i].getDstPort());
			linkObj.put("state", Link[i].getState());
			linkObj.put("direction", Link[i].getDirection());
			linkObjArr.add(linkObj);
		}
		appJSONObj.put("link", linkObjArr);
		
		
		return appJSONObj;
	}
	@Override
	public String toString() {
		return "ApplicationVO [appName=" + appName + ", appIP=" + appIP
				+ ", appType=" + appType + ", adminID=" + adminID
				+ ", bandwidth=" + bandwidth + ", packetLoss=" + packetLoss
				+ ", jitter=" + jitter + ", delay=" + delay + ", Topology="
				+ Topology + ", Link=" + Arrays.toString(Link) + ", Node="
				+ Node + "]";
	}
	
	/*public JSONObject getApplicationJSON(){
		JSONObject appJSONObj = new JSONObject();
		appJSONObj.put("appName", appName);
		appJSONObj.put("appIP", appIP);
		appJSONObj.put("appType", appType);
		appJSONObj.put("adminID", adminID);
		appJSONObj.put("appName", appName);
		appJSONObj.put("bandwidth", bandwidth);
		appJSONObj.put("packetLoss", packetLoss);
		appJSONObj.put("jitter", jitter);
		appJSONObj.put("delay", delay);
		
		JSONObject topoObj = new JSONObject();
		topoObj.put("topologyID", Topology.getTopologyID());
		topoObj.put("nodeCnt", Topology.getNodeCnt());
		topoObj.put("linkCnt", Topology.getLinkCnt());
		appJSONObj.put("topology", topoObj);
		
		JSONArray nodeObjArr = new JSONArray();
		for(int i=0;i<Node.length;i++){
			JSONObject nodeObj = new JSONObject();
			nodeObj.put("name", Node[i].getName());
			nodeObj.put("IP", Node[i].getIP());
			nodeObj.put("port", Node[i].getPort());
			nodeObj.put("state", Node[i].getState());
			nodeObjArr.add(nodeObj);
		}
		appJSONObj.put("node", nodeObjArr);
		
		JSONArray linkObjArr = new JSONArray();
		for(int i=0;i<Link.length;i++){
			JSONObject linkObj = new JSONObject();
			linkObj.put("name", Link[i].getName());
			linkObj.put("srcSwitch", Link[i].getSrcSwitch());
			linkObj.put("dstSwitch", Link[i].getDstSwitch());
			linkObj.put("state", Link[i].getState());
			linkObj.put("direction", Link[i].getDirection());
			linkObjArr.add(linkObj);
		}
		appJSONObj.put("link", nodeObjArr);
		
		
		return appJSONObj;
	}*/

}
