package selab.nsaf.sa.engine;

import java.io.ByteArrayOutputStream;
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
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
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
	
	/*
	public void loadOntology() throws OWLOntologyCreationException {
		fileLoader.loadOntology();
	}*/
	
	public OntologyManager() {
		System.out.println("OntologyManager");
	}
	
	//온톨로지 외부 파일시스템에서 불러오도록 FileLoader 클래스에 요청
	//외부에서 읽어온 온톨로지 파일을 시스템 내부에 저장
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
		
		OWLNamedIndividual appIndividual = factory.getOWLNamedIndividual(":#".concat(filteredContext.get("hasAppName").toString()), pm);
		OWLNamedIndividual topologyIndividual = factory.getOWLNamedIndividual(":#".concat(filteredContext.get("hasTopologyID").toString()), pm);

		OWLClass owl_application = factory.getOWLClass(":Application", pm);
		OWLClass owl_appType = factory.getOWLClass(":Application_Type", pm);
		OWLClass owl_nsla = factory.getOWLClass(":NSLA", pm);
		OWLClass owl_Topology = factory.getOWLClass(":Topology", pm);
		OWLClass owl_node= factory.getOWLClass(":Node", pm);
		OWLClass owl_switch= factory.getOWLClass(":Switch", pm);
		OWLClass owl_host= factory.getOWLClass(":Host", pm);
		OWLClass owl_link = factory.getOWLClass(":Link", pm);

		//이 부분이 값을 넣는 부분이구나!!!!!!!!!!!!!!!!!!
		Iterator keys = filteredContext.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next().toString();
			if(key.equals("host")){
				JSONArray hostArray = (JSONArray) filteredContext.get(key);
				for(int i=0; i<hostArray.size(); i++){
					JSONObject hostObj = (JSONObject) hostArray.get(i);
					OWLNamedIndividual hostIndividual = factory.getOWLNamedIndividual(":#".concat(hostObj.get("hasHostID").toString()), pm);
					Iterator hostKeys = hostObj.keySet().iterator();
					while(hostKeys.hasNext()) {
						String hostKey = hostKeys.next().toString();
						OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(hostKey);
						OWLDataPropertyAssertionAxiom dataPropertyAssertion;
						if(hostKey.equals("hasState")){
							boolean val = Boolean.parseBoolean(hostObj.get(hostKey).toString());
							dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, hostIndividual, val);
						}else{
							dataPropertyAssertion= factory.getOWLDataPropertyAssertionAxiom(dataProperty, hostIndividual, hostObj.get(hostKey).toString());
						}
						//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
						
						OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("hasNode");
						OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, topologyIndividual, hostIndividual);
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
					}
				}
			}else if(key.equals("switch")){
				JSONArray switchArray = (JSONArray) filteredContext.get(key);
				for(int i=0; i<switchArray.size(); i++){
					JSONObject switchObj = (JSONObject) switchArray.get(i);
					OWLNamedIndividual switchIndividual = factory.getOWLNamedIndividual(":#".concat(switchObj.get("hasSwitchID").toString()), pm);
					Iterator switchKeys = switchObj.keySet().iterator();
					while(switchKeys.hasNext()) {
						String switchKey = switchKeys.next().toString();
						OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(switchKey);
						OWLDataPropertyAssertionAxiom dataPropertyAssertion;
						if(isNumeric(switchObj.get(switchKey).toString())){
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
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
						
						OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("hasNode");
						OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, topologyIndividual, switchIndividual);
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
					}
				}
			}else if(key.equals("link")){
				JSONArray linkArray = (JSONArray) filteredContext.get(key);
				for(int i=0; i<linkArray.size(); i++){
					JSONObject linkObj = (JSONObject) linkArray.get(i);
					OWLNamedIndividual linkIndividual = factory.getOWLNamedIndividual(":#".concat(linkObj.get("hasLinkID").toString()), pm);
					Iterator linkKeys = linkObj.keySet().iterator();
					while(linkKeys.hasNext()) {
						String linkKey = linkKeys.next().toString();
						OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(linkKey);
						OWLDataPropertyAssertionAxiom dataPropertyAssertion;
						if(isNumeric(linkObj.get(linkKey).toString())){
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
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
						
						OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("hasLink");
						OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, topologyIndividual, linkIndividual);
						ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
					}
				}
			}else if(key.equals("hasTopologyID")){
/*				OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(owl_Topology, topologyIndividual);
				ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), classAssertion);
				StreamDocumentTarget target = new StreamDocumentTarget(new ByteArrayOutputStream());
				ontologyRepository.getManager().saveOntology(ontologyRepository.getOntology(), target);*/
				
				OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(key);
				OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, topologyIndividual, filteredContext.get(key).toString());
				//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
				ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
				
				OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("useTopology");
				OWLObjectPropertyAssertionAxiom objectPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(objProperty, appIndividual, topologyIndividual);
				ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion);
			}else{
				if(key.equals("hasAppIP") || key.equals("hasAdminID") || key.equals("hasAppName")){
					OWLDataProperty dataProperty = ontologyRepository.getDataPropertyByName(key);
					OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, appIndividual, filteredContext.get(key).toString());
					//System.out.println(dataPropertyAssertion.getDataPropertiesInSignature()+"/"+dataPropertyAssertion.getObject());
					ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), dataPropertyAssertion);
				}
			}
		}
		/*
		OWLNamedIndividual appTypeIndividual = factory.getOWLNamedIndividual(":".concat("broadcast"), pm);
		OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(owl_appType, appTypeIndividual);
		ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), classAssertion);
		StreamDocumentTarget target = new StreamDocumentTarget(new ByteArrayOutputStream());
		ontologyRepository.getManager().saveOntology(ontologyRepository.getOntology(), target);
		
		OWLObjectProperty objProperty = ontologyRepository.getObjectPropertyByName("hasApplicationType");
		OWLObjectPropertyAssertionAxiom objectPropertyAssertion1 = factory.getOWLObjectPropertyAssertionAxiom(objProperty, appIndividual, appTypeIndividual);
		ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion1);
		
		OWLNamedIndividual nslaTypeIndividual = factory.getOWLNamedIndividual(":".concat("nsla-01"), pm);
		classAssertion = factory.getOWLClassAssertionAxiom(owl_nsla, nslaTypeIndividual);
		ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), classAssertion);
		target = new StreamDocumentTarget(new ByteArrayOutputStream());
		ontologyRepository.getManager().saveOntology(ontologyRepository.getOntology(), target);
		
		objProperty = ontologyRepository.getObjectPropertyByName("hasNSLA");
		objectPropertyAssertion1 = factory.getOWLObjectPropertyAssertionAxiom(objProperty, appIndividual, nslaTypeIndividual);
		ontologyRepository.getManager().addAxiom(ontologyRepository.getOntology(), objectPropertyAssertion1);*/
		
		
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
	* 1. 메소드명 : getAllDataProperty
	* 2. 작성일 : 2017. 3. 27. 오후 3:03:25
	* 3. 작성자 : HJS
	* 4. 설명 : 온톨로지 내의 모든 Data Property를 읽어오는 메소드
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
	* 1. 메소드명 : getAllObjectProperty
	* 2. 작성일 : 2017. 3. 27. 오후 3:04:00
	* 3. 작성자 : HJS
	* 4. 설명 : 온토롤지 내의 모든 Object Property를 읽어오는 메소드
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
	
	/*
	public boolean checkDataProperty(String key) {
		Iterator iter = ontologyRepository.getAllDataProperties().iterator();
		while(iter.hasNext()) {
			if(iter.next().toString().contains(key))
				return true;
		}
		return false;
	}
	
	public boolean checkObjectProperty(String key) {
		Iterator iter = ontologyRepository.getAllObjectProperties().iterator();
		while(iter.hasNext()) {
			if(iter.next().toString().contains(key))
				return true;
		}
		return false;
	}*/
	
	/*public OntologyManager() {
		System.out.println("====== OntologyManager Start ======\n");
	}
	
	public void run() {
		try {
			System.out.println("OM_run\n");
			loadOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadOntology() throws OWLOntologyCreationException {
		FileLoader fileLoader = new FileLoader();
		fileLoader.start();
	}*/
	
	/*
	public void saveOntology(File file, OWLOntologyManager manager, OWLOntology ontology) {
		IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
		String ontologyID = ontology.getOntologyID().getOntologyIRI().toString();
		ontologyRepository.setOntologyIDSet(documentIRI, ontologyID);
		ontologyRepository.setOntologyManagerSet(ontologyID, manager);
		ontologyRepository.setOntologySet(ontologyID, ontology);
	}*/
	
	public boolean isNumeric(String s) {  
        return s.matches("[-+]?\\d*\\.?\\d+");  
    }  
}