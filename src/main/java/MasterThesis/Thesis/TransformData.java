package MasterThesis.Thesis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

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


public class TransformData {
	public static void main(String[] args) {
		TransformData transform = new TransformData();
		transform.transformToOpenKEFormat();
	}
	
	private void transformToOpenKEFormat() {
		String modelLocation = "/home/necker/Documents/Datasets/mappingbased_objects_en_2016_10.ttl";
		Dataset ds = this.createDataset(modelLocation);
		System.out.println("HI");
		Model model = this.loadModel(modelLocation, ds);
		System.out.println("I was here 1");
		ArrayList<String> properties = new ArrayList<String>();
		ArrayList<String> entities = new ArrayList<String>();
		ds.begin(ReadWrite.READ);
		this.storeEntitiesAndProperties(model, properties, entities);
		ds.end();
		System.out.println("I was here 2");
		String train2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/train2id.txt";
		ds.begin(ReadWrite.READ);
		this.writeStatementFile(train2IdLocation, model, entities, properties);
		ds.end();
		System.out.println("I was here 3");
		String diffLocation = "/home/necker/Documents/Datasets/Difference.ttl";
		Dataset dsDiff = this.createDataset(diffLocation);
		Model diff = this.loadModel(diffLocation, dsDiff);
		System.out.println("I was here 4");
		dsDiff.begin(ReadWrite.READ);
		this.storeEntitiesAndProperties(diff, properties, entities);
		dsDiff.end();
		System.out.println("I was here 5");
		String properties2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/relation2id.txt";
		String entities2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/entity2id.txt";
		this.writeListFiles(properties, properties2IdLocation);
		System.out.println("I was here 6");
		this.writeListFiles(entities, entities2IdLocation);
		System.out.println("I was here 7");
		String diff2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/diff2id.txt";
		dsDiff.begin(ReadWrite.READ);
		this.writeStatementFile(diff2IdLocation, diff, entities, properties);
		dsDiff.end();
		System.out.println("I was here 8");
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
	
	private void storeEntitiesAndProperties(Model model, ArrayList<String> properties, ArrayList<String> entities) {
		StmtIterator iter = model.listStatements();
		int i=0;
		while(iter.hasNext() /*&& i <= 1857000*/) {
			Statement stmt = iter.nextStatement();
			Resource res = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode node = stmt.getObject();
			
			entities = this.storeEntity(res, entities);
			properties = this.storeProperty(prop, properties);
			entities = this.storeEntity(node, entities);
			i++;
			if(i%1000 == 0) {
				//break;
				System.out.println(i);
			}
		}
	}
	
	private ArrayList<String> storeProperty(Property prop, ArrayList<String> properties) {
		String propString = prop.toString();
		if(properties.indexOf(propString) < 0) {
			properties.add(propString);
		}
		
		return properties;
	}
	
	private ArrayList<String> storeEntity(Resource res, ArrayList<String> entities) {
		String resString = res.toString();
		if(entities.indexOf(resString) < 0) {
			entities.add(resString);
		}
		
		return entities;
	}
	
	private ArrayList<String> storeEntity(RDFNode node, ArrayList<String> entities) {
		String nodeString = node.toString();
		if(entities.indexOf(nodeString) < 0) {
			entities.add(nodeString);
		}
		
		return entities;
	}
	
	private void writeListFiles(ArrayList<String> list, String location) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(location);
			bw = new BufferedWriter(fw);
			
			bw.write(Integer.toString(list.size()));
			bw.newLine();
			for(int i = 0; i < list.size(); i++ ) {
				String line = "";
				line += list.get(i);
				line += " ";
				line += Integer.toString(i);
				System.out.println(line);
				bw.write(line);
				bw.newLine();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeStatementFile(String location, Model model, ArrayList<String> entities, ArrayList<String> properties) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(location);
			bw = new BufferedWriter(fw);
			StmtIterator iter = model.listStatements();
			bw.write(Long.toString(model.size()));
			bw.newLine();
			int i=0;
			while(iter.hasNext() /*&& i <= 1857000*/) {
				Statement stmt = iter.nextStatement();
				Resource res = stmt.getResource();
				Property prop = stmt.getPredicate();
				RDFNode node = stmt.getObject();
				
				int indexRes = entities.indexOf(res.toString());
				int indexProp = properties.indexOf(prop.toString());
				int indexNode = entities.indexOf(node.toString());
				
				String line = Integer.toString(indexRes);
				line += " ";
				line += Integer.toString(indexNode);
				line += " ";
				line += Integer.toString(indexProp);
				System.out.println(line);
				bw.write(line);
				bw.newLine();
				i++;
				if(i%1000 == 0) {
					//break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
