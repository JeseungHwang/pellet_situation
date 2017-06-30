package selab.nsaf.sa.engine;

import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONObject;
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
	private JSONObject violationVal = new JSONObject(); 
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

	/**
	* Method : ReasonSituation
	* Date : 2017. 4. 10. 오후 8:02:51
	* Author : HJS
	* Description : 온톨로지를 이용하여 상황추론하는 메소드
	* Input Parameter : String ApplicationName
	* @return Violation의 종류에 따라 True or False
	*/
	public JSONObject ReasonSituation(String appName) {
		System.out.println("--- Start Reasoning Situation ---");
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontologyManager.ontologyRepository.getOntology(), new SimpleConfiguration());
		OWLDataFactory factory = ontologyManager.ontologyRepository.getManager().getOWLDataFactory();
		String ontologyID = ontologyManager.ontologyRepository.getOntologyID();
		PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) ontologyManager.ontologyRepository.getManager().getOntologyFormat(ontologyManager.ontologyRepository.getOntology());
		pm.setDefaultPrefix(ontologyID + "#");
		OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(":".concat(appName), pm);
		//최종적으로 어떤 상황인지 추론하는 부분
		//Fail Class 선언
		OWLClass hardFailClass = factory.getOWLClass(":Hard_fail", pm);
		OWLClass softFailClass = factory.getOWLClass(":Soft_fail", pm);
		Set<OWLClass> hardFailClasses = reasoner.getSubClasses(hardFailClass, false).getFlattened();
		Set<OWLClass> softFailClasses = reasoner.getSubClasses(softFailClass, false).getFlattened();
		JSONObject hardfailResult = new JSONObject();
		JSONObject softfailResult = new JSONObject();
		
		for(Iterator<OWLClass> i = hardFailClasses.iterator(); i.hasNext(); ) {	//Hard fail인지 추론하는 부분
			String situationType = i.next().getIRI().getFragment().toString();
			String situation_temp = ":".concat(situationType);
			OWLClass chOMPClass = factory.getOWLClass(situation_temp, pm);
			OWLClassAssertionAxiom axiomToExplain = factory.getOWLClassAssertionAxiom(chOMPClass, namedIndividual);
			if(reasoner.isEntailed(axiomToExplain)) {
				System.out.println(namedIndividual.getIRI().getFragment().toString() + " is violation : " + situation_temp);
				hardfailResult.put(situationType, "true");
				DefaultExplanationGenerator explanationGenerator = 
		                new DefaultExplanationGenerator(ontologyManager.ontologyRepository.getManager(), reasonerFactory, ontologyManager.ontologyRepository.getOntology(), reasoner, new SilentExplanationProgressMonitor()); 
		        Set<OWLAxiom> explanation = explanationGenerator.getExplanation(axiomToExplain); 
		        ExplanationOrderer deo = new ExplanationOrdererImpl(ontologyManager.ontologyRepository.getManager()); 
		        ExplanationTree explanationTree = deo.getOrderedExplanation(axiomToExplain, explanation); 
		        System.out.println(); 
		        System.out.println("-- explanation of reasoning steps --"); 
		        printIndented(explanationTree, "");
		        System.out.println("\n");
			} else {
				System.out.println(namedIndividual.getIRI().getFragment().toString() + " is not in situation " + situation_temp);
			}
			violationVal.put("hardfail", hardfailResult);
		}
		
		for(Iterator<OWLClass> i = softFailClasses.iterator(); i.hasNext(); ) {	//Soft fail인지 추론하는 부분
			String situationType = i.next().getIRI().getFragment().toString();
			String situation_temp = ":".concat(situationType);
			OWLClass chOMPClass = factory.getOWLClass(situation_temp, pm);
			OWLClassAssertionAxiom axiomToExplain = factory.getOWLClassAssertionAxiom(chOMPClass, namedIndividual);
			
			if(reasoner.isEntailed(axiomToExplain)) {
				System.out.println(namedIndividual.getIRI().getFragment().toString() + " is violation : " + situation_temp);
				softfailResult.put(situationType, "true");
				DefaultExplanationGenerator explanationGenerator = 
		                new DefaultExplanationGenerator(ontologyManager.ontologyRepository.getManager(), reasonerFactory, ontologyManager.ontologyRepository.getOntology(), reasoner, new SilentExplanationProgressMonitor()); 
		        Set<OWLAxiom> explanation = explanationGenerator.getExplanation(axiomToExplain); 
		        ExplanationOrderer deo = new ExplanationOrdererImpl(ontologyManager.ontologyRepository.getManager()); 
		        ExplanationTree explanationTree = deo.getOrderedExplanation(axiomToExplain, explanation); 
		        System.out.println(); 
		        System.out.println("-- explanation of reasoning steps --"); 
		        printIndented(explanationTree, "");
		        System.out.println("\n");
			} else {
				System.out.println(namedIndividual.getIRI().getFragment().toString() + " is not in situation " + situation_temp);
			}
			violationVal.put("softfail", softfailResult);
		}
		System.out.println("\n");
		return violationVal;
	}
	
	public JSONObject getViolationVal() {
		return violationVal;
	}
	
	public boolean getResultBySituation(OWLClass situation) {
		if(violationVal.get(situation.toString()).equals("true"))
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
}