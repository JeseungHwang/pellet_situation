package selab.nsaf.sa.engine;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

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
	

	/**
	* Method : filteringEmptyData
	* Date : 2017. 4. 10. 오후 5:10:11
	* Author : HJS
	* Description : JSON 내 비어있는 Key 제거후 온톨로지에 해당 JSON Value 업데이트
	* Input Parameter : JSON (실제 받아 온 값)
	* @return void 
	*/
	public void filteringEmptyData(JSONObject jsonObject) throws SAXException, IOException, ParserConfigurationException {
		System.out.println("--- Start Filtering Empty Data ---");
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
			// 비어있는 JSON 객체 정리후 온톨로지 업데이트 진행 
			filteredObject = filteringContextInfo(jsonObject);
			System.out.println("Filtered Object : "+filteredObject.toJSONString());
			try {
				//Filtered Object를 온톨로지에 저장
				requestOntologyUpdate(filteredObject);
			} catch (OWLOntologyStorageException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No Context Information!");
		}
	}
	
	/**
	* Method : filteringContextInfo
	* Date : 2017. 4. 10. 오후 5:12:09
	* Author : HJS
	* Description : 정리된 JSON 값에서 온톨로지 Property 형식에 맞게 MappingInfo.xml 파일을 읽어와 Context 요소로 추출
	* Input Parameter : 정리된 JSON
	* @return JSONObject 
	*/
	public JSONObject filteringContextInfo(JSONObject jsonObject) throws SAXException, IOException, ParserConfigurationException {
		System.out.println("--- Start Filtering Context Information in Filtered Event Data ---");
		JSONObject filteredContext = new JSONObject();	//매핑된 JSON 값을 저장하기 위한 변수
		File contextInfoFile = new File(path+"MappingInfo.xml");	//온톨로지 Property 이름 매핑을 하기 위한 xml
		
		//XML 파싱 준비
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(contextInfoFile);
		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();
		System.out.println("XML Root: " + root.getNodeName() + "\n");
		NodeList nList = doc.getElementsByTagName("mapping");
		
		JSONArray linkJSONgroup = new JSONArray();	//Link JSON 객체를 저장할 변수
		JSONArray switchJSONgroup = new JSONArray();	//Switch JSON 객체를 저장할 변수
		JSONArray hostJSONgroup = new JSONArray();	//Host JSON 객체를 저장할 변수
		
		for(int i = 0; i < nList.getLength(); i++) {	//xml의 Key의 갯수만큼 반복문을 통해 Mapping 검사
			Node nNode = nList.item(i);
			if(nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element)nNode;	
				Iterator keys = jsonObject.keySet().iterator();
				while(keys.hasNext()) {
					String key = keys.next().toString();
					if(key.equals(getTagValue("ontologyClass", eElement))) {	//ontologyClass 키값만 비교
						if(key.equals("application")) {							//Application 일때 매핑
							JSONObject appJSON = (JSONObject) jsonObject.get(key);
							Iterator appKeys = appJSON.keySet().iterator();
							while(appKeys.hasNext()) {
								String appKey = appKeys.next().toString();
								if(appKey.equals(getTagValue("json", eElement))) {	//현재 JSON의 Key값과 xml파일의 Key값을 비교하여 같으면
									filteredContext.put(getTagValue("property", eElement),appJSON.get(appKey));	//xml에 저장되어 있는 Property 값을 JSON의 Key으로 두고, 실제 값을 저장
								}
							}//while
						}else if(key.equals("topology")) {	//Topology 일때 매핑
							JSONObject appJSON = (JSONObject) jsonObject.get(key);
							Iterator appKeys = appJSON.keySet().iterator();
							while(appKeys.hasNext()) {
								String appKey = appKeys.next().toString();
								if(appKey.equals(getTagValue("json", eElement))) {	//현재 JSON의 Key값과 xml파일의 Key값을 비교하여 같으면
									filteredContext.put(getTagValue("property", eElement),appJSON.get(appKey)); //xml에 저장되어 있는 Property 값을 JSON의 Key으로 두고, 실제 값을 저장
								}
							}//while
						}else if(key.equals("node")) {	//Node 일때 매핑(Host, Switch의 상위 Key)
							JSONObject nodeArrObj = (JSONObject) jsonObject.get(key);
							Iterator nodeKeys = nodeArrObj.keySet().iterator();
							while(nodeKeys.hasNext()){
								String nodeKey = nodeKeys.next().toString();
								if(nodeKey.equals("switch")){	//Switch 일때
									JSONArray switchArrObj = (JSONArray) nodeArrObj.get(nodeKey);	//switch Key 값을 가진 JSON을 JSON Array 형태로 변경
									for(int j=0 ; j<switchArrObj.size() ; j++){						
										JSONObject switchObj = (JSONObject) switchArrObj.get(j);
										if(!isExistNodeID(switchJSONgroup, switchObj, nodeKey)){	//Switch를 저장하는 JSON Array(switchJSONgroup)에 현재 받아온 Switch(switchArrObj)의 값이 존재하는지 판단
											JSONObject switchJSON = new JSONObject();				//존재하지 않는다면 새로운 JSON을 생성하여 Switch JSON Array에 추가
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
								}else if(nodeKey.equals("host")){	//Host 일때
									JSONArray hostArrObj = (JSONArray) nodeArrObj.get(nodeKey);	//Host Key 값을 가진 JSON을 JSON Array 형태로 변경
									for(int j=0; j<hostArrObj.size(); j++){
										JSONObject hostObj = (JSONObject) hostArrObj.get(j);
										if(!isExistNodeID(hostJSONgroup, hostObj, nodeKey)){	//Host를 저장하는 JSON Array(hostJSONgroup)에 현재 받아온 Host(hostArrObj)의 값이 존재하는지 판단
											JSONObject hostJSON = new JSONObject();				//존재하지 않는다면 새로운 JSON을 생성하여 Host JSON Array에 추가
											hostJSON.put("hasHostID", hostObj.get("hostID"));
											hostJSON.put("hasState", hostObj.get("state"));
											hostJSONgroup.add(hostJSON);
										}
									}
								}
							}
						}else if(key.equals("link")) {		//Link 일때
							JSONArray linkArrObj = (JSONArray) jsonObject.get(key);	//Link Key 값을 가진 JSON을 JSON Array 형태로 변경
							for(int j=0 ; j<linkArrObj.size() ; j++){
								JSONObject linkObj = (JSONObject) linkArrObj.get(j);
								if(!isExistLinkID(linkJSONgroup, linkObj)){						//Link를 저장하는 JSON Array(linkJSONgroup)에 현재 받아온 Link(linkArrObj)의 값이 존재하는지 판단
									JSONObject linkJSON = new JSONObject();						//존재하지 않는다면 새로운 JSON을 생성하여 Link JSON Array에 추가
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
		//Mapping 완료된 JSONgroup(Switch, Host, Link)를 최종 매핑값을 저장하는 filteredContext에 추가
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
	
	public JSONObject inferSituation(String appName) throws OWLOntologyCreationException {
		return (JSONObject) reasoningEngine.ReasonSituation(appName);
	}
	
	public String getTagValue(String sTag, Element eElement) {
		NodeList nList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node)nList.item(0);
			
		return nValue.getNodeValue();
	}
}