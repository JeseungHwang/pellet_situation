package selab.nsaf.sa.engine;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

public class OntologyManager {
	public static OntologyRepository ontologyRepository = new OntologyRepository();
	private static FileLoader fileLoader = new FileLoader();
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
	static String path = OntologyManager.class.getResource("").getPath();
	
	public OntologyManager() {
		System.out.println("OntologyManager");
	}
	
	/**
	* Method : saveOntology
	* Date : 2017. 4. 10. 오후 8:01:58
	* Author : HJS
	* Description : 온톨로지 파일을 읽어와 파일을 시스템 내부에 저장하는 메소드
	* Input Parameter :
	* @return void 
	*/
	public void saveOntology() throws IOException {
		try {
			ReturnValue returnValue = fileLoader.loadOntology();
			System.out.println("--- Start Saving Ontology in System ---");
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager = returnValue.getManagerTemp();
			OWLOntology ontology = returnValue.getOntologyTemp();
			IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
			String ontologyID = ontology.getOntologyID().getOntologyIRI().toString();
			
			System.out.println("docIRI = " + documentIRI.toString());
			System.out.println("ontoID = " + ontologyID);
			System.out.println("what = " + documentIRI.getFragment().toString() + "\n");
			ontologyRepository.setManager(manager);
			ontologyRepository.setOntology(ontology);
			ontologyRepository.setDocumentIRI(documentIRI);
			ontologyRepository.setOntologyID(ontologyID);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* Method : reloadOntology
	* Date : 2017. 4. 10. 오후 8:01:26
	* Author : HJS
	* Description : 저장한 온톨로지 다시 불러오는 메소드
	* Input Parameter :
	* @return void 
	*/
	public void reloadOntology() throws IOException {
		try {
			ReturnValue returnValue = fileLoader.reloadOntology();
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager = returnValue.getManagerTemp();
			OWLOntology ontology = returnValue.getOntologyTemp();
			IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
			String ontologyID = ontology.getOntologyID().getOntologyIRI().toString();

			ontologyRepository.setManager(manager);
			ontologyRepository.setOntology(ontology);
			ontologyRepository.setDocumentIRI(documentIRI);
			ontologyRepository.setOntologyID(ontologyID);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateOntology(JSONObject filteredContext) throws OWLOntologyStorageException, IOException {
		System.out.println("--- Start Updating Ontology ---");
		Set<OWLLogicalAxiom> axiomSet = ontologyRepository.getOntology().getLogicalAxioms();
		Set<OWLDataProperty> allDataProperties = getAllDataProperty();
		Set<OWLObjectProperty> allObjectProperties = getAllObjectProperty();
		PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) ontologyRepository.getManager().getOntologyFormat(ontologyRepository.getOntology());
		OWLDataFactory factory = ontologyRepository.getManager().getOWLDataFactory();
		//Individual에 있는 Application Name을 가져와 IRI 주소 형태로 변경
		
		//Individual Property
		OWLNamedIndividual appIndividual = factory.getOWLNamedIndividual(":#".concat(filteredContext.get("hasAppName").toString()), pm);
		OWLNamedIndividual topologyIndividual = factory.getOWLNamedIndividual(":#".concat(filteredContext.get("hasTopologyID").toString()), pm);

		//Class Property
		OWLClass owl_application = factory.getOWLClass(":Application", pm);
		OWLClass owl_appType = factory.getOWLClass(":Application_Type", pm);
		OWLClass owl_nsla = factory.getOWLClass(":NSLA", pm);
		OWLClass owl_Topology = factory.getOWLClass(":Topology", pm);
		OWLClass owl_node= factory.getOWLClass(":Node", pm);
		OWLClass owl_switch= factory.getOWLClass(":Switch", pm);
		OWLClass owl_host= factory.getOWLClass(":Host", pm);
		OWLClass owl_link = factory.getOWLClass(":Link", pm);

		//Individual을 추가하는 부분
		//이전에 Context 요소 도출 후 Mapping된 JSON을 Individual Property로 만들어 추가 하는부분
		Iterator keys = filteredContext.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next().toString();
			if(key.equals("host")){			//Host Individual Property 
				JSONArray hostArray = (JSONArray) filteredContext.get(key);
				for(int i=0; i<hostArray.size(); i++){
					JSONObject hostObj = (JSONObject) hostArray.get(i);
					//Host Individual Property 생성
					OWLNamedIndividual hostIndividual = factory.getOWLNamedIndividual(":#".concat(hostObj.get("hasHostID").toString()), pm);
					Iterator hostKeys = hostObj.keySet().iterator();
					while(hostKeys.hasNext()) {
						String hostKey = hostKeys.next().toString();
						OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(hostKey);	//해당 Key값을 가진 Data Property 객체 생성
						OWLDataPropertyAssertionAxiom dataPropertyAssertion;
						if(hostKey.equals("hasState")){	//해당 Data Property를 Individual Property에 Assert 하는 부분
							boolean val = Boolean.parseBoolean(hostObj.get(hostKey).toString());
							dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, hostIndividual, val);
						}else{
							dataPropertyAssertion= factory.getOWLDataPropertyAssertionAxiom(dataProperty, hostIndividual, hostObj.get(hostKey).toString());
						}
						//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
						// 완성된 Host Individual를 온톨로지에 Add 하는 부분
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);

						//Host Individual를 Topology의 Object Property로 추가 하는 부분
						OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("hasNode");
						OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, topologyIndividual, hostIndividual);
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
					}
				}
			}else if(key.equals("switch")){			//Switch Individual Property
				JSONArray switchArray = (JSONArray) filteredContext.get(key);
				for(int i=0; i<switchArray.size(); i++){
					JSONObject switchObj = (JSONObject) switchArray.get(i);
					//Switch Individual Property 생성
					OWLNamedIndividual switchIndividual = factory.getOWLNamedIndividual(":#".concat(switchObj.get("hasSwitchID").toString()), pm);
					Iterator switchKeys = switchObj.keySet().iterator();
					while(switchKeys.hasNext()) {
						String switchKey = switchKeys.next().toString();
						OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(switchKey);
						OWLDataPropertyAssertionAxiom dataPropertyAssertion;
						if(isNumeric(switchObj.get(switchKey).toString())){	//해당 Data Property를 Individual Property에 Assert 하는 부분
							double val = Double.parseDouble(switchObj.get(switchKey).toString());
							dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, switchIndividual, val);
						}else{
							dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, switchIndividual, switchObj.get(switchKey).toString());
						}
						if(switchKey.equals("hasState")){
							boolean val = Boolean.parseBoolean(switchObj.get(switchKey).toString());
							dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, switchIndividual, val);
						}
						//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
						// 완성된 Switch Individual를 온톨로지에 Add 하는 부분
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
						
						//Switch Individual를 Topology의 Object Property로 추가 하는 부분
						OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("hasNode");
						OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, topologyIndividual, switchIndividual);
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
					}
				}
			}else if(key.equals("link")){			//Link Individual Property
				JSONArray linkArray = (JSONArray) filteredContext.get(key);
				for(int i=0; i<linkArray.size(); i++){
					JSONObject linkObj = (JSONObject) linkArray.get(i);
					//Link Individual Property 생성
					OWLNamedIndividual linkIndividual = factory.getOWLNamedIndividual(":#".concat(linkObj.get("hasLinkID").toString()), pm);
					Iterator linkKeys = linkObj.keySet().iterator();
					while(linkKeys.hasNext()) {
						String linkKey = linkKeys.next().toString();
						OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(linkKey);
						OWLDataPropertyAssertionAxiom dataPropertyAssertion;
						if(isNumeric(linkObj.get(linkKey).toString())){	//해당 Data Property를 Individual Property에 Assert 하는 부분
							int val = Integer.parseInt(linkObj.get(linkKey).toString());
							dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, linkIndividual, val);
						}else{
							dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, linkIndividual, linkObj.get(linkKey).toString());
						}
						if(linkKey.equals("hasState")){
							boolean val = Boolean.parseBoolean(linkObj.get(linkKey).toString());
							dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, linkIndividual, val);
						}
						//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
						// 완성된 Link Individual를 온톨로지에 Add 하는 부분
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
						
						//Link Individual를 Topology의 Object Property로 추가 하는 부분
						OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("hasLink");
						OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, topologyIndividual, linkIndividual);
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
					}
				}
			}else if(key.equals("hasTopologyID")){			//Topology Individual Property
				//Topology Data Property를 생성하여 Topology Individual에 추가
				OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(key);
				OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, topologyIndividual, filteredContext.get(key).toString());
				//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
				ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
				
				//Topology Individual를 Application의 Object Property로 추가 하는 부분
				OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("useTopology");
				OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, appIndividual, topologyIndividual);
				ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
			}else{			//Application Individual Property
				if(key.equals("hasAppIP") || key.equals("hasAdminID") || key.equals("hasAppName")){
					OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(key);
					OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, appIndividual, filteredContext.get(key).toString());
					//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
					ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
				}else{	//Application의 NSLA 값을 매핑하는 부분
					//NSLA Individual 생성
					OWLNamedIndividual nslaIndividual = factory.getOWLNamedIndividual(":#".concat(filteredContext.get("hasAppName").toString()), pm);
					OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(key);
					double val = Double.parseDouble(filteredContext.get(key).toString());
					OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, nslaIndividual, val);
					//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
					ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
					
					//생성한 NSLA Individual을 Application의 Object Property로 추가하는 부분
					OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("hasNSLA");
					OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, appIndividual, nslaIndividual);
					ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
				}
			}
		}
		
		//완성된 Individual Property를 저장할 owl 파일 생성
		File savedOntologyFile = new File(path+"nsaf_mapping.owl");
		OWLOntologyFormat format = ontologyRepository.getManager().getOntologyFormat(ontologyRepository.getOntology());
		OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
		if(format.isPrefixOWLOntologyFormat()) {
			owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
		}
		ontologyRepository.getManager().saveOntology(ontologyRepository.getOntology(), owlxmlFormat, IRI.create(savedOntologyFile.toURI()));
		System.out.println("Set DataProperty Complete !!\n");
	}
	
	/**
	* Method : getAllDataProperty
	* Date : 2017. 4. 10. 오후 7:59:30
	* Author : HJS
	* Description : 온톨로지 내의 모든 Data Property를 읽어오는 메소드
	* Input Parameter :
	* @return Set<OWLDataProperty> 
	*/
	public Set<OWLDataProperty> getAllDataProperty() {
		Set<OWLDataProperty> allDataProperties = new HashSet<OWLDataProperty>();
		for (OWLDataPropertyDomainAxiom dataPropertyAxiom : ontologyRepository.getOntology().getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
			String tempStr = dataPropertyAxiom.getProperty().toString().replace("<", "").replace(">", "");
			IRI tempIRI = IRI.create(tempStr);
			OWLDataFactory factory = ontologyRepository.getManager().getOWLDataFactory();
			OWLDataProperty tempDP = factory.getOWLDataProperty(tempIRI);
			allDataProperties.add(tempDP);
			ontologyRepository.setAllDataProperties(allDataProperties);
		}
		return allDataProperties;
	}
	
	/**
	* Method : getAllObjectProperty
	* Date : 2017. 4. 10. 오후 7:59:49
	* Author : HJS
	* Description : 온토롤지 내의 모든 Object Property를 읽어오는 메소드
	* Input Parameter :
	* @return Set<OWLObjectProperty> 
	*/
	public Set<OWLObjectProperty> getAllObjectProperty() {
		Set<OWLObjectProperty> allObjectProperties = new HashSet<OWLObjectProperty>();
		for(OWLObjectPropertyDomainAxiom objectPropertyAxiom : ontologyRepository.getOntology().getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {		
			String tempStr = objectPropertyAxiom.getProperty().toString().replace("<", "").replace(">", "");
			IRI tempIRI = IRI.create(tempStr);
			OWLDataFactory factory = ontologyRepository.getManager().getOWLDataFactory();
			OWLObjectProperty tempDP = factory.getOWLObjectProperty(tempIRI);
					
			allObjectProperties.add(tempDP);
			ontologyRepository.setAllObjectProperties(allObjectProperties);
		}
		return allObjectProperties;
	}
	
	/**
	* Method : isNumeric
	* Date : 2017. 4. 10. 오후 8:00:08
	* Author : HJS
	* Description : str이 숫자인지아닌지 판별하는 정규식 검사 메소드
	* Input Parameter : String str
	* @return boolean 
	*/
	public boolean isNumeric(String str) {  
        return str.matches("[-+]?\\d*\\.?\\d+");  
    }  
}