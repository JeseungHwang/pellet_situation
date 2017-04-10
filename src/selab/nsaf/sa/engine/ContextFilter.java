package selab.nsaf.sa.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ContextFilter {
	
	private static OntologyManager ontologyManager = new OntologyManager();
	private static ReasoningEngine reasoningEngine = new ReasoningEngine();
	static String path = ContextFilter.class.getResource("").getPath();
	
	public ContextFilter() {
		System.out.println("ContextFilter");
	}
	
	public void filteringEmptyData(JSONObject jsonObject) throws SAXException, IOException, ParserConfigurationException {
		System.out.println("--- Start Filtering Empty Data ---");
		//JSONObject firstFilteredObject = new JSONObject();
		JSONObject filteredObject = new JSONObject();
		
		if(!jsonObject.isEmpty()) {
			/*Iterator keys = jsonObject.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next().toString();
				if(jsonObject.get(key) != null) {
					firstFilteredObject.put(key, jsonObject.get(key).toString());
					//System.out.println("key : " + key.toString()+" / "+"value : " + jsonObject.get(key));
				}
			}*/
			System.out.println("\n");
			//값들이 입력됐을 때, 추출하고, 온톨로지 업데이트 하도록 전달...
			filteredObject = filteringContextInfo(jsonObject);
			System.out.println("Filtered Object : "+filteredObject.toJSONString());
			try {
				requestOntologyUpdate(filteredObject);
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("No Context Information!");
		}
	}
	
	public JSONObject filteringContextInfo(JSONObject jsonObject) throws SAXException, IOException, ParserConfigurationException {
		System.out.println("--- Start Filtering Context Information in Filtered Event Data ---");
		JSONObject filteredContext = new JSONObject();
		File contextInfoFile = new File(path+"MappingInfo.xml");
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(contextInfoFile);
		doc.getDocumentElement().normalize();
				
		Element root = doc.getDocumentElement();
		System.out.println("XML Root: " + root.getNodeName() + "\n");
		
		NodeList nList = doc.getElementsByTagName("mapping");
		
		JSONArray linkJSONgroup = new JSONArray();
		JSONArray switchJSONgroup = new JSONArray();
		JSONArray hostJSONgroup = new JSONArray();
		
		for(int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if(nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element)nNode;	
				Iterator keys = jsonObject.keySet().iterator();
				while(keys.hasNext()) {
					String key = keys.next().toString();
					if(key.equals(getTagValue("ontologyClass", eElement))) {
						if(key.equals("application")) {
							JSONObject appJSON = (JSONObject) jsonObject.get(key);
							Iterator appKeys = appJSON.keySet().iterator();
							while(appKeys.hasNext()) {
								String appKey = appKeys.next().toString();
								if(appKey.equals(getTagValue("json", eElement))) {
									filteredContext.put(getTagValue("property", eElement),appJSON.get(appKey));
								}
							}//while
						}else if(key.equals("topology")) {
							JSONObject appJSON = (JSONObject) jsonObject.get(key);
							Iterator appKeys = appJSON.keySet().iterator();
							while(appKeys.hasNext()) {
								String appKey = appKeys.next().toString();
								if(appKey.equals(getTagValue("json", eElement))) {
									filteredContext.put(getTagValue("property", eElement),appJSON.get(appKey));
								}
							}//while
						}else if(key.equals("node")) {
							JSONObject nodeArrObj = (JSONObject) jsonObject.get(key);
							Iterator nodeKeys = nodeArrObj.keySet().iterator();
							while(nodeKeys.hasNext()){
								String nodeKey = nodeKeys.next().toString();
								if(nodeKey.equals("switch")){
									JSONArray switchArrObj = (JSONArray) nodeArrObj.get(nodeKey);
									for(int j=0 ; j<switchArrObj.size() ; j++){
										JSONObject switchObj = (JSONObject) switchArrObj.get(j);
										if(!isExistNodeID(switchJSONgroup, switchObj, nodeKey)){
											JSONObject switchJSON = new JSONObject();
											switchJSON.put("hasSwitchID", switchObj.get("switchID"));
											switchJSON.put("hasSwitchDPID", switchObj.get("switchDPID"));
											switchJSON.put("hasState", switchObj.get("state"));
											switchJSON.put("hasBandwidth", switchObj.get("bandwidth"));
											switchJSON.put("hasDelay", switchObj.get("delay"));
											switchJSON.put("hasPacketLoss", switchObj.get("packetLoss"));
											switchJSON.put("hasJitter", switchObj.get("jitter"));
											switchJSONgroup.add(switchJSON);
										}
									}
								}else if(nodeKey.equals("host")){
									JSONArray hostArrObj = (JSONArray) nodeArrObj.get(nodeKey);
									for(int j=0; j<hostArrObj.size(); j++){
										JSONObject hostObj = (JSONObject) hostArrObj.get(j);
										if(!isExistNodeID(hostJSONgroup, hostObj, nodeKey)){
											JSONObject hostJSON = new JSONObject();
											hostJSON.put("hasHostID", hostObj.get("hostID"));
											hostJSON.put("hasState", hostObj.get("state"));
											hostJSONgroup.add(hostJSON);
										}
									}
								}
							}
						}else if(key.equals("link")) {
							JSONArray linkArrObj = (JSONArray) jsonObject.get(key);
							for(int j=0 ; j<linkArrObj.size() ; j++){
								JSONObject linkObj = (JSONObject) linkArrObj.get(j);
								JSONObject linkJSON = new JSONObject();
								if(!isExistLinkID(linkJSONgroup, linkObj)){
									linkJSON.put("hasLinkID", linkObj.get("linkID"));
									linkJSON.put("hasDirection", linkObj.get("direction"));
									linkJSON.put("hasSrcSwitchDPID", linkObj.get("srcSwitchDPID"));
									linkJSON.put("hasDstSwitchDPID", linkObj.get("dstSwitchDPID"));
									linkJSON.put("hasSrcPort", linkObj.get("srcPort"));
									linkJSON.put("hasDstPort", linkObj.get("dstPort"));
									linkJSON.put("hasState", linkObj.get("state"));
									linkJSONgroup.add(linkJSON);
								}
							}
						}
					}
				}//while
			}
		}//for
		filteredContext.put("switch",switchJSONgroup);
		filteredContext.put("host",hostJSONgroup);
		filteredContext.put("link",linkJSONgroup);
		return filteredContext;
	}

	public boolean isExistLinkID(JSONArray linkJSONgroup, JSONObject linkJSON){
		boolean isExist = false;
		if(!linkJSONgroup.isEmpty()){
			for(int z=0;z<linkJSONgroup.size();z++){
				JSONObject obj = (JSONObject) linkJSONgroup.get(z);
				if(obj.get("hasLinkID").toString().equals(linkJSON.get("linkID").toString())){
					isExist = true;
				}
			}
		}
		return isExist;
	}
	
	public boolean isExistNodeID(JSONArray nodeJSONgroup, JSONObject nodeJSON, String nodeType){
		boolean isExist = false;
		String addObjectKey = nodeType+"ID";
		String ontologyKey = "has"+initCap(addObjectKey);	//온톨로지 Data Property 값
		if(!nodeJSONgroup.isEmpty()){
			for(int z=0; z<nodeJSONgroup.size(); z++){
				JSONObject obj = (JSONObject) nodeJSONgroup.get(z);
				if(obj.get(ontologyKey).equals(nodeJSON.get(addObjectKey))){
					isExist = true;
				}
			}
		}
		return isExist;
	}
	
	public String initCap(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
	}

		
	public void requestLoadOntology() throws IOException {
		ontologyManager.saveOntology();
	}
	
	public void requestOntologyUpdate(JSONObject filteredContext) throws OWLOntologyStorageException {
		try {
			ontologyManager.updateOntology(filteredContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void requestReloadOntology() {
		try {
			FileLoader.reloadOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, String> inferSituation(String appName) throws OWLOntologyCreationException {
		return reasoningEngine.ReasonSituation(appName);
	}
	
	public String getTagValue(String sTag, Element eElement) {
		NodeList nList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node)nList.item(0);
			
		return nValue.getNodeValue();
	}
}