package selab.nsaf.sa.engine;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
* Class : OntologyRepository
* Date : 2017. 4. 10. 오후 8:05:02
* Author : HJS
* Description : 온톨로지 저장 정보
*/

public class OntologyRepository {
	File file;
	IRI documentIRI;
	String ontologyID;
	OWLOntologyManager manager;
	OWLOntology ontology;
	
	Set<OWLDataProperty> allDataProperties;
	Set<OWLObjectProperty> allObjectProperties;
	
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
