package MasterThesis.Thesis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

public class TransformDataToOpenKE2 {
	
	public int propertyIndex = 0;
	public int entityIndex = 0;
	public static void main(String[]args) {
		TransformDataToOpenKE2 transform = new TransformDataToOpenKE2();
		transform.transformData();
	}
	
	private void transformData() {
		String modelLocation = "/data/Datasets/mappingbased_objects_en_2016_10.ttl";
		String train2IdLocation = "/data/Datasets/TrainingFiles/train2id.txt";
		String test2IdLocation = "/data/Datasets/TrainingFiles/test2id.txt";
		String valid2IdLocation = "/data/Datasets/TrainingFiles/valid2id.txt";
		String diffLocation = "/data/Datasets/Difference.ttl";
		HashMap<String, Integer> entities = new HashMap<String, Integer>();
		HashMap<String, Integer> properties = new HashMap<String, Integer>();
		Dataset ds = this.createDataset(modelLocation);
		Model model = this.loadModel(modelLocation, ds);
		ds.begin(ReadWrite.READ);
		int numberStatements = this.storeEntitesAndProperties(model, entities, properties);
		ds.end();
		//ArrayList<String> entitiesList = new ArrayList<String>(entities);
		//ArrayList<String> propertyList = new ArrayList<String>(properties);
		ds.begin(ReadWrite.READ);
		this.writeStatementFile(train2IdLocation, test2IdLocation, valid2IdLocation, model, entities, properties, numberStatements);
		ds.end();
		Dataset dsDiff = this.createDataset(diffLocation);
		Model modelDiff = this.loadModel(diffLocation, dsDiff);
		dsDiff.begin(ReadWrite.READ);
		int numberStatementsDiff = this.storeEntitesAndProperties(modelDiff, entities, properties);
		dsDiff.end();
		String properties2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/relation2id.txt";
		String entities2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/entity2id.txt";
		try {
			this.writeListFiles(properties, properties2IdLocation);
			System.out.println("I was here 6");
			this.writeListFiles(entities, entities2IdLocation);
			System.out.println("I was here 7");
			String diff2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/diff2id.txt";
			dsDiff.begin(ReadWrite.READ);
			this.writeStatementFile(diff2IdLocation, modelDiff, entities, properties, numberStatementsDiff);
			dsDiff.end();
			System.out.println("EntityIndex: " + this.entityIndex);
			System.out.println("PropertyIndex: " + this.propertyIndex);
		} catch(Exception e) {
			e.printStackTrace();
		}
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
	
	private int storeEntitesAndProperties(Model model, HashMap<String, Integer> entities, HashMap<String, Integer> properties) {
		StmtIterator iter = model.listStatements();
		int startIndex = 0;
		while(iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource res = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode node = stmt.getObject();
			
			this.addElementToMap(entities, res.toString(), true);
			this.addElementToMap(entities, node.toString(), true);
			this.addElementToMap(properties, prop.toString(), false);
			
			startIndex++;
			if(startIndex%1000 == 0) {
				System.out.println(startIndex);
			}
		}
		return startIndex;
	}
	
	private HashMap<String, Integer> addElementToMap(HashMap<String, Integer> map, String elem, boolean isEntity) {
		if(!map.containsKey(elem)) {
			int index = 0;
			if(isEntity) {
				index = this.entityIndex;
			} else {
				index = this.propertyIndex;
			}
			
			map.put(elem, index);
			
			if(isEntity) {
				this.entityIndex++;
			} else {
				this.propertyIndex++;
			}
		}
		return map;
	}
	
	private void writeListFiles(HashMap<String, Integer> list, String location) throws IOException {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(location);
			bw = new BufferedWriter(fw);
			
			bw.write(Integer.toString(list.size()));
			bw.newLine();
			for(String i : list.keySet()) {
				String line = i + " " + Integer.toString(list.get(i));
				bw.write(line);
				bw.newLine();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
			fw.close();
		}
	}
	
	private void writeStatementFile(String location, Model model, HashMap<String, Integer> entities, HashMap<String, Integer> properties, int size) throws IOException {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(location);
			bw = new BufferedWriter(fw);
			StmtIterator iter = model.listStatements();
			bw.write(Long.toString(size));
			System.out.println(size);
			bw.newLine();
			int i=0;
			while(iter.hasNext() /*&& i <= 1857000*/) {
				Statement stmt = iter.nextStatement();
				Resource res = stmt.getSubject();
				Property prop = stmt.getPredicate();
				RDFNode node = stmt.getObject();
				
				int indexRes = entities.get(res.toString());
				int indexProp = properties.get(prop.toString());
				int indexNode = entities.get(node.toString());
				
				String line = Integer.toString(indexRes);
				line += " ";
				line += Integer.toString(indexNode);
				line += " ";
				line += Integer.toString(indexProp);
				bw.write(line);
				bw.newLine();
				i++;
				if(i%100 == 0) {
					System.out.println(i + " " + line);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
			fw.close();
		}
	}

	private void writeStatementFile(String trainingLocation, String testLocation, String validLocation, Model model, HashMap<String, Integer> entities, HashMap<String, Integer> properties, int size) {
		ArrayList<String> training = new ArrayList<String>();
		ArrayList<String> test = new ArrayList<String>();
		ArrayList<String> valid = new ArrayList<String>();
		try {
			StmtIterator iter = model.listStatements();
			System.out.println(size);
			int i=0;
			while(iter.hasNext() /*&& i <= 1857000*/) {
				Statement stmt = iter.nextStatement();
				Resource res = stmt.getSubject();
				Property prop = stmt.getPredicate();
				RDFNode node = stmt.getObject();
				
				int indexRes = entities.get(res.toString());
				int indexProp = properties.get(prop.toString());
				int indexNode = entities.get(node.toString());
				
				String line = Integer.toString(indexRes);
				line += " ";
				line += Integer.toString(indexNode);
				line += " ";
				line += Integer.toString(indexProp);
				
				double random = Math.random();
				if(random <= 0.8) {
					training.add(line);
				} else if(random > 0.8 && random <= 0.9) {
					valid.add(line);
				} else {
					test.add(line);
				}
				i++;
				if(i%100 == 0) {
					System.out.println(i + " " + line);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			this.writeStatementFromList(trainingLocation, training);
			this.writeStatementFromList(validLocation, valid);
			this.writeStatementFromList(testLocation, test);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void writeStatementFromList(String location, ArrayList<String> rows) throws IOException {
		int size = rows.size();
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(location);
			bw = new BufferedWriter(fw);
			bw.write(Integer.toString(size));
			bw.newLine();
			for(int i = 0; i<rows.size(); i++) {
				bw.write(rows.get(i));
				bw.newLine();
				if(i%1000 == 0) {
					System.out.println(i);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
			fw.close();
		}
	}
}
