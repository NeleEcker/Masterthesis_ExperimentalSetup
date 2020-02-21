package MasterThesis.Thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

public class CombineSampleWithNegs {
	public static void main(String[]args) {
		CombineSampleWithNegs compare = new CombineSampleWithNegs();
		compare.compare();
	}
	
	private HashMap<String, String> entities;
	private HashMap<String, String> relations;
	
	private void compare() {
		String pathSampleEntities = "./Datasets/DBPediaSample/entity2id.txt";
		String pathSampleRelations = "./Datasets/DBPediaSample/relation2id.txt";
		String pathNegativeSampleModel = "./Datasets/Difference.ttl";
		String diffLocation = "./Datasets/DBPediaSample/diff2id.txt";
		String entityLocation = "./Datasets/DBPediaSample/entity2idNew.txt";
		String relationsLocation = "./Datasets/DBPediaSample/relation2idNew.txt";
		Dataset ds = this.createDataset(pathNegativeSampleModel);
		Model modelNegatives = this.loadModel(pathNegativeSampleModel, ds);
		entities = new HashMap<String, String>();
		relations = new HashMap<String, String>();
		this.fillHashMaps(pathSampleEntities, entities);
		this.fillHashMaps(pathSampleRelations, relations);
		this.compareStatements(modelNegatives, diffLocation);
		this.writeFiles(entityLocation, entities);
		this.writeFiles(relationsLocation, relations);
	}
	
	private Model loadModel(String modelLocation, Dataset ds) {
		Model model = ds.getDefaultModel();
		System.out.println("Hi again");
		FileManager.get().readModel(model, modelLocation);
		System.out.println("And again");
		return model;
	}
	
	private Dataset createDataset(String modelLocation) {
		String datasetlocation = modelLocation + "_db";
		Dataset dataset = TDBFactory.createDataset(datasetlocation);
		return dataset;
	}
	
	private void fillHashMaps(String pathSample, HashMap<String, String> sample) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(pathSample);
			br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null) {
				String[] split = line.split("\\s+");
				System.out.println(split[0] + " " +split[1]);
				sample.put(split[0], split[1]);
				line = br.readLine();
			}
			fr.close();
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void compareStatements(Model model, String diffLocation) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(diffLocation);
			bw = new BufferedWriter(fw);
			StmtIterator iter = model.listStatements();
			while(iter.hasNext()) {
				Statement stmt = iter.next();
				Resource subject = stmt.getSubject();
				Property predicate = stmt.getPredicate();
				Resource object = stmt.getResource();
				
				String subjectString  = subject.getLocalName();
				System.out.println("SubjectString" +subjectString);
				String predicateString = predicate.getLocalName();
				System.out.println("predicateString" +predicateString);
				String objectString = object.getLocalName();
				System.out.println("objectstring " + objectString);
				
				String keyObject, keySubject, keyRelation;
				
				//if(entities.containsKey(subjectString) || entities.containsKey(objectString){
				if(entities.containsKey(subjectString) && entities.containsKey(objectString) && relations.containsKey(predicateString)) {
					keySubject = entities.get(subjectString);
					System.out.println("Key Subject: "+keySubject);
					keyObject = entities.get(objectString);
					System.out.println("Key Object: "+keyObject);
					keyRelation = relations.get(predicateString);
					System.out.println("Key Relation: " + keyRelation);
					if(keySubject == null || keySubject.isEmpty()) {
						keySubject = Integer.toString(entities.size());
						System.out.println("New Key Subject: "+keySubject);
						entities.put(subjectString, keySubject);
					}
					if(keyObject == null || keyObject.isEmpty()) {
						keyObject = Integer.toString(entities.size());
						System.out.println("New Key Object: "+keyObject);
						entities.put(objectString, keyObject);
					}
					if(keyRelation == null || keyRelation.isEmpty()) {
						keyRelation = Integer.toString(relations.size());
						System.out.println("New Key relation: " + keyRelation);
						relations.put(predicateString, keyRelation);
					}
					String line = keySubject + " " + keyObject + " " + keyRelation;
					bw.write(line);
					bw.newLine();
				}
			}
			bw.close();
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeFiles(String location, HashMap<String, String> sample) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(location);
			bw = new BufferedWriter(fw);
			for(String i: sample.keySet()) {
				String line = i + " " + sample.get(i);
				bw.write(line);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
