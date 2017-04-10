package selab.nsaf.sa.engine;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyRepository {
	File file;
	IRI documentIRI;
	String ontologyID;
	OWLOntologyManager manager;
	OWLOntology ontology;
	
	Set<OWLDataProperty> allDataProperties;
	Set<OWLObjectProperty> allObjectProperties;
	
	/*public void saveOntology(String fileDir) throws OWLOntologyCreationException {
		file = new File(fileDir);
		manager = OWLManager.createOWLOntologyManager();
		ontology = manager.loadOntologyFromOntologyDocument(file);
		documentIRI = manager.getOntologyDocumentIRI(ontology);
		ontologyID = ontology.getOntologyID().getOntologyIRI().toString();
		
		System.out.println("documentIRI: " + this.documentIRI);
		System.out.println("ontologyID: " + this.ontologyID);
		System.out.println("manager: " + this.manager);
		System.out.println("ontology: " + this.ontology);
		
	}*/
	
	/*public void saveOntology(File file, OWLOntologyManager manager, OWLOntology ontology) {
		setDocumentIRI(manager.getOntologyDocumentIRI(ontology));
		setOntologyID(ontology.getOntologyID().getOntologyIRI().toString());
		setManager(manager);
		setOntology(ontology);
		System.out.println("documentIRI: " + this.documentIRI);
		System.out.println("ontologyID: " + this.ontologyID);
		System.out.println("manager: " + this.manager);
		System.out.println("ontology: " + this.ontology);
	}	*/
	
	public IRI getDocumentIRI() {
		return documentIRI;
	}
	public void setDocumentIRI(IRI documentIRI) {
		this.documentIRI = documentIRI;
	}
	
	public String getOntologyID() {
		return ontologyID;
	}
	public void setOntologyID(String ontologyID) {
		this.ontologyID = ontologyID;
	}
	
	public OWLOntologyManager getManager() {
		return manager;
	}
	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}
	
	public OWLOntology getOntology() {
		return ontology;
	}
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	
	public void setAllDataProperties(Set<OWLDataProperty> allDataProperties) {
		this.allDataProperties = allDataProperties;
	}
	public Set<OWLDataProperty> getAllDataProperties() {
		return allDataProperties;
	}
	public OWLDataProperty getDataPropertyByName(String name) {
		OWLDataProperty result = null;
		
		Iterator<OWLDataProperty> iter = allDataProperties.iterator();
		while(iter.hasNext()) {
			OWLDataProperty temp = iter.next();
			if(temp.toString().contains(name)) {
				result = temp;
			}
		}		
		return result;
	}
	
	public void setAllObjectProperties(Set<OWLObjectProperty> allObjectProperties) {
		this.allObjectProperties = allObjectProperties;
	}
	public Set<OWLObjectProperty> getAllObjectProperties() {
		return allObjectProperties;
	}
	public OWLObjectProperty getObjectPropertyByName(String name) {
		OWLObjectProperty result = null;
		
		Iterator<OWLObjectProperty> iter = allObjectProperties.iterator();
		while(iter.hasNext()) {
			OWLObjectProperty temp = iter.next();
			if(temp.toString().contains(name)) {
				result = temp;
			}
		}		
		return result;
	}
	
}
	
	/*
	//<documentIRI(온톨로지 파일 저장 위치), ontologyID(온톨로지 아이디)> 
	private Map<IRI, String> ontologyIDSet = Collections.synchronizedMap(new HashMap<IRI, String>());
	//<ontologyID(온톨로지 아이디), manager(온톨로지매니저)>
	private Map<String, OWLOntologyManager> ontologyManagerSet = Collections.synchronizedMap(new HashMap<String, OWLOntologyManager>());
	//<ontologyID(온톨로지 아이디), ontology(온톨로지)>
	private Map<String, OWLOntology> ontologySet = Collections.synchronizedMap(new HashMap<String, OWLOntology>());
	*/
//	private OWLOntology ontology;
//	private IRI documentIRI;
//	private String ontologyID;
	
//	public void saveOntology(File file, OWLOntologyManager manager, OWLOntology ontology) {
//		//OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
//		//OWLOntology ontology1 = manager.loadOntologyFromOntologyDocument(file);
//		IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
//		String ontologyID = ontology.getOntologyID().getOntologyIRI().toString();
//		ontologyIDSet.put(documentIRI, ontologyID);
//		ontologyManagerSet.put(ontologyID, manager);
//		ontologySet.put(ontologyID, ontology);
//	}
	
	/*
	public void saveOntology(File file, OWLOntologyManager manager, OWLOntology ontology) {
		IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
		String ontologyID = ontology.getOntologyID().getOntologyIRI().toString();
		setOntologyIDSet(documentIRI, ontologyID);
		setOntologyManagerSet(ontologyID, manager);
		setOntologySet(ontologyID, ontology);
	}
	
	public void setOntologyIDSet(IRI documentIRI, String ontologyID) {
		ontologyIDSet.put(documentIRI, ontologyID);
	}
	
	public void setOntologyManagerSet(String ontologyID, OWLOntologyManager manager) {
		ontologyManagerSet.put(ontologyID, manager);
	}
	
	public void setOntologySet(String ontolgyID, OWLOntology ontology) {
		ontologySet.put(ontolgyID, ontology);
	}
	
	public String getOntologyIDbyDocIRI(IRI docIRI) {
		return ontologyIDSet.get(docIRI);
	}        
	
	public OWLOntologyManager getOntologyManagerbyOntoID(String ontoID) {
		return ontologyManagerSet.get(ontoID);
	}
	
	public OWLOntologyManager getOntologyManagerbyDocIRI(IRI docIRI) {
		String ontologyID = ontologyIDSet.get(docIRI);
		return ontologyManagerSet.get(ontologyID);
	}
	
	public OWLOntology getOntologybyOntoID(String ontoID) {
		return ontologySet.get(ontoID);
	}
	
	public OWLOntology getOntologybyDocIRI(IRI docIRI) {
		String ontologyID = ontologyIDSet.get(docIRI);
		return ontologySet.get(ontologyID);
	}
	
	
}*/