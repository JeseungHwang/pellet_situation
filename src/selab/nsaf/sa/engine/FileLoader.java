package selab.nsaf.sa.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class FileLoader {
	
	static String path = FileLoader.class.getResource("").getPath();
	
	public static ReturnValue loadOntology() throws OWLOntologyCreationException, IOException {
		System.out.println("--- Start Loading Ontology File from Outside ---");
		//System.out.println("Path of Saved Ontology: " + fileDir);
		System.out.println("Path of Saved Ontology: " + path+"nsaf.owl");
		File file = new File(path+"nsaf.owl");

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		System.out.println("ID of Saved Ontology: " + ontology.getOntologyID().toString());

		File savedOntologyFile = new File(path+"/"+ ontology.getOntologyID().getOntologyIRI().getFragment().toString());
		
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(file);
			outputStream = new FileOutputStream(savedOntologyFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileChannel fcin = inputStream.getChannel();
		FileChannel fcout = outputStream.getChannel();
		long size = fcin.size();
		fcin.transferTo(0, size, fcout);
		
		System.out.println("Path of Saved Ontology in System: " + savedOntologyFile.toString() + "\n");
		
		fcout.close();
		fcin.close();
		outputStream.close();
		inputStream.close();
		
		return new ReturnValue(manager, ontology);
	}
		
	public static ReturnValue reloadOntology() throws OWLOntologyCreationException, IOException {
		System.out.println("Reload Ontology: " + path+"nsaf_mapping.owl");
		File file = new File(path+"nsaf_mapping.owl");

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		File savedOntologyFile = new File(path+"/"+ ontology.getOntologyID().getOntologyIRI().getFragment().toString());
		
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(file);
			outputStream = new FileOutputStream(savedOntologyFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileChannel fcin = inputStream.getChannel();
		FileChannel fcout = outputStream.getChannel();
		long size = fcin.size();
		fcin.transferTo(0, size, fcout);
	
		fcout.close();
		fcin.close();
		outputStream.close();
		inputStream.close();
		
		return new ReturnValue(manager, ontology);
	}
}