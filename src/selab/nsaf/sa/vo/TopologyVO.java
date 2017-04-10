package selab.nsaf.sa.vo;


public class TopologyVO {
	private String topologyID;
	private int nodeCnt;
	private int linkCnt;
	
	public String getTopologyID() {
		return topologyID;
	}
	public void setTopologyID(String topologyID) {
		this.topologyID = topologyID;
	}
	public int getNodeCnt() {
		return nodeCnt;
	}
	public void setNodeCnt(int nodeCnt) {
		this.nodeCnt = nodeCnt;
	}
	public int getLinkCnt() {
		return linkCnt;
	}
	public void setLinkCnt(int linkCnt) {
		this.linkCnt = linkCnt;
	}
	@Override
	public String toString() {
		return "TopologyVO [topologyID=" + topologyID + ", nodeCnt=" + nodeCnt
				+ ", linkCnt=" + linkCnt + "]";
	}
}
