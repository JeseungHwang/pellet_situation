package selab.nsaf.sa.engine;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


final class ReturnValue {
	private OWLOntologyManager manager_temp = OWLManager.createOWLOntologyManager();;
	private OWLOntology ontology_temp;
	
	public ReturnValue(OWLOntologyManager manager_temp, OWLOntology ontology_temp) {
		//manager_temp = OWLManager.createOWLOntologyManager();
		this.manager_temp = manager_temp;
		this.ontology_temp = ontology_temp;
	}
	public OWLOntologyManager getManagerTemp() {
		return manager_temp;
	}
	public OWLOntology getOntologyTemp() {
		return ontology_temp;
	}
}