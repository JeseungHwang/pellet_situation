package selab.nsaf.sa.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;


public class ReasoningEngine {
	
	private static OntologyManager ontologyManager = new OntologyManager();	
	private Map<String, String> returnValue = new HashMap<String, String>();
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

	public Map<String, String> ReasonSituation(String appName) {
		System.out.println("--- Start Reasoning Situation ---");
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontologyManager.ontologyRepository.getOntology(), new SimpleConfiguration());
		OWLDataFactory factory = ontologyManager.ontologyRepository.getManager().getOWLDataFactory();
		String ontologyID = ontologyManager.ontologyRepository.getOntologyID();
		PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) ontologyManager.ontologyRepository.getManager().getOntologyFormat(ontologyManager.ontologyRepository.getOntology());
		pm.setDefaultPrefix(ontologyID + "#");
		

		OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(":".concat(appName), pm);
		System.out.println("Individual : "+namedIndividual.toString());
		//최종적으로 어떤 상황인지 추론하는 부분
		OWLClass situationClass = factory.getOWLClass(":Hard_fail", pm);
		Set<OWLClass> classes = reasoner.getSubClasses(situationClass, false).getFlattened();
		
		for(Iterator<OWLClass> i = classes.iterator(); i.hasNext(); ) {
			String situation_temp = ":".concat(i.next().getIRI().getFragment().toString());
			OWLClass chOMPClass = factory.getOWLClass(situation_temp, pm);
			OWLClassAssertionAxiom axiomToExplain = factory.getOWLClassAssertionAxiom(chOMPClass, namedIndividual);
			
			if(reasoner.isEntailed(axiomToExplain)) {
				System.out.println(namedIndividual.getIRI().getFragment().toString() + " is violation : " + situation_temp);
				returnValue.put(situation_temp, "true");
				DefaultExplanationGenerator explanationGenerator = 
		                new DefaultExplanationGenerator(ontologyManager.ontologyRepository.getManager(), reasonerFactory, ontologyManager.ontologyRepository.getOntology(), reasoner, new SilentExplanationProgressMonitor()); 
		        Set<OWLAxiom> explanation = explanationGenerator.getExplanation(axiomToExplain); 
		        ExplanationOrderer deo = new ExplanationOrdererImpl(ontologyManager.ontologyRepository.getManager()); 
		        ExplanationTree explanationTree = deo.getOrderedExplanation(axiomToExplain, explanation); 
		        System.out.println(); 
		        System.out.println("-- explanation of reasoning steps --"); 
		        printIndented(explanationTree, "");
		        System.out.println("!~~~~:"+explanationTree.toString());
		        System.out.println("\n");
			} else {
				System.out.println(namedIndividual.getIRI().getFragment().toString() + " is not in situation " + situation_temp);
				returnValue.put(situation_temp, "false");
			}
		}
		
		System.out.println("\n");
		return returnValue;
	}
	
	public Map<String, String> getAllReturnValue_RE() {
		return returnValue;
	}
	
	public boolean getResultBySituation(OWLClass situation) {
		if(returnValue.get(situation.toString()).equalsIgnoreCase("true"))
			return true;
		else
			return false;
	}
	
	private static void printIndented(Tree<OWLAxiom> node, String indent) { 
        OWLAxiom axiom = node.getUserObject(); 
        System.out.println(indent + renderer.render(axiom)); 
        if (!node.isLeaf()) { 
            for (Tree<OWLAxiom> child : node.getChildren()) {
                printIndented(child, indent + "    "); 
            } 
        } 
    }
	
	
	/*public void ReasonSituation(String userID, String ontologyID) {
		String ontoID = ontologyID;
		OWLOntologyManager manager = ontologyRepository.getOntologyManagerbyOntoID(ontoID);
		OWLOntology ontology = ontologyRepository.getOntologybyOntoID(ontoID);
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
		OWLDataFactory factory = manager.getOWLDataFactory();
		PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		pm.setDefaultPrefix(ontoID + "#");
		
		OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(":" + userID, pm); 
		
		OWLClass situationClass = factory.getOWLClass(":Situation", pm);
		
		Set<OWLClass> classes = reasoner.getSubClasses(situationClass, false).getFlattened();
		
		for(Iterator i = classes.iterator(); i.hasNext(); ) {
			OWLClass chOMPClass = factory.getOWLClass(":" + i.next(), pm);
			OWLClassAssertionAxiom axiomToExplain = factory.getOWLClassAssertionAxiom(chOMPClass, namedIndividual);
			if(reasoner.isEntailed(axiomToExplain) == true)
				System.out.println(namedIndividual.toString() + " is in situation " + i.next());
		}
        
        //return chOMPClass;
	}*/
	
	
}