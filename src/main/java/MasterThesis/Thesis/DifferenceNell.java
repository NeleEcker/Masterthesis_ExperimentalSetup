package MasterThesis.Thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
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

public class DifferenceNell {
	public int propertyIndex = 0;
	public int entityIndex = 0;
	public ArrayList<String> negEntities = new ArrayList<String>();
	public ArrayList<String> negRelations = new ArrayList<String>();
	public ArrayList<String> negSamples = new ArrayList<String>();

	public static void main(String[] args) {
		DifferenceNell diff = new DifferenceNell();
		diff.getDifference();

	}
	
	private void getDifference() {
		String modelDirectory = "/data/Datasets/NELL/NELL.08m.1100.esv.csv.NELL.08m.1100.cesv.csv.nt";
		String negSamplesDirectory = "/home/necker/eclipse-workspace/Thesis/Datasets/FeedbackFiles/NellNegSample.txt";
		String train2IdLocation = "/data/Datasets/NELL/TrainingFiles/train2id.txt";
		String test2IdLocation = "/data/Datasets/NELL/TrainingFiles/test2id.txt";
		String valid2IdLocation = "/data/Datasets/NELL/TrainingFiles/valid2id.txt";
		String properties2IdLocation = "/data/Datasets/NELL/TrainingFiles/relation2id.txt";
		String entities2IdLocation = "/data/Datasets/NELL/TrainingFiles/entity2id.txt";
		String diff2IdLocation = "/data/Datasets/NELL/TrainingFiles/diff2id.txt";
		Dataset ds = this.createDataset(modelDirectory);
		Model currentVersion = this.loadModel(modelDirectory, ds);
		//System.out.println(System.currentTimeMillis());
		HashMap<String, Integer> entities = new HashMap<String, Integer>();
		HashMap<String, Integer> properties = new HashMap<String, Integer>();
		
		ds.begin(ReadWrite.READ);
		int index  = this.storeEntitesAndProperties(currentVersion, entities, properties);
		ds.end();
		ds.begin(ReadWrite.READ);
		this.writeStatementFile(train2IdLocation, test2IdLocation, valid2IdLocation, currentVersion, entities, properties, index);
		ds.end();
		this.readNegSamples(negSamplesDirectory, entities, properties);
		try {
			this.writeListFiles(entities, entities2IdLocation);
			this.writeListFiles(properties, properties2IdLocation);
			this.writeStatementFromList(diff2IdLocation, negSamples);
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(index);
		System.out.println(entities.size());
		System.out.println("Negsamples: " + negSamples.size());
	}
	
	private Model loadModel(String modelLocation, Dataset ds) {
		System.out.println("Loading of model started");
		Model model = ds.getDefaultModel();
		FileManager.get().readModel(model, modelLocation);
		System.out.println("Model loading finished");
		return model;
	}
	
	private Dataset createDataset(String modelLocation) {
		String datasetLocation = modelLocation + "_db";
		Dataset ds = TDBFactory.createDataset(datasetLocation);
		System.out.println("database is created");
		return ds;
	}
	
	private void readNegSamples(String location, HashMap<String, Integer> entities, HashMap<String,Integer> properties) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(location));
			String line;
			while((line = br.readLine()) != null) {
				String[]elements = line.split(" ");
				Integer head = entities.get(elements[0]);
				if(head == null) {
					this.addElementToMap(entities, elements[0], true);
				}
				Integer tail = entities.get(elements[2]);
				if(tail == null) {
					this.addElementToMap(entities, elements[2], true);
				}
				Integer relation = properties.get(elements[1]);
				if(relation == null) {
					this.addElementToMap(properties, elements[1], false);
				}
				String transformedNegSample = Integer.toString(entities.get(elements[0])) + " " + Integer.toString(entities.get(elements[2])) + " " + Integer.toString(properties.get(elements[1]));
				//System.out.println(transformedNegSample);
				negSamples.add(transformedNegSample);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private int storeEntitesAndProperties(Model model, HashMap<String, Integer> entities, HashMap<String, Integer> properties) {
		StmtIterator iter = model.listStatements();
		System.out.println("Size of model: " + model.size());
		int startIndex = 0;
		while(iter.hasNext()) {
			//System.out.print("I was here:   " + startIndex);
			
			Statement stmt = iter.nextStatement();
			Resource res = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode node = stmt.getObject();
			String nodeName = node.toString();
			if(node.isURIResource()) {
				Resource object = stmt.getResource();
				nodeName = object.getLocalName();
			}
			
			this.addElementToMap(entities, res.getLocalName(), true);
			this.addElementToMap(entities, nodeName, true);
			this.addElementToMap(properties, prop.getLocalName(), false);
			
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
				String nodeName = node.toString();
				if(node.isURIResource()) {
					Resource object = stmt.getResource();
					nodeName = object.getLocalName();
				}
				
				int indexRes = entities.get(res.getLocalName());
				int indexProp = properties.get(prop.getLocalName());
				int indexNode = entities.get(nodeName);
				
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
				if(i%10000 == 0) {
					bw.flush();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
			fw.close();
		}
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

}
