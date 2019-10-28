package MasterThesis.Thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

public class CompareSample {
	public static void main(String[]args) {
		CompareSample compare = new CompareSample();
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
				RDFNode object = stmt.getObject();
				
				String subjectString  = subject.toString();
				String predicateString = predicate.toString();
				String objectString = object.toString();
				
				String keyObject, keySubject, keyRelation;
				
				if(entities.containsKey(subjectString) || entities.containsKey(objectString)) {
					keySubject = entities.get(subjectString);
					keyObject = entities.get(objectString);
					keyRelation = relations.get(predicateString);
					if(keySubject.isEmpty()) {
						keySubject = Integer.toString(entities.size());
						entities.put(subjectString, keySubject);
					}
					if(keyObject.isEmpty()) {
						keyObject = Integer.toString(entities.size());
						entities.put(objectString, keyObject);
					}
					if(keyRelation.isEmpty()) {
						keyRelation = Integer.toString(relations.size());
						relations.put(predicateString, keyRelation);
					}
					String line = keySubject + " " + keyObject + " " + keyRelation;
					bw.write(line);
					bw.newLine();
				}
				bw.close();
				fw.close();
			}
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