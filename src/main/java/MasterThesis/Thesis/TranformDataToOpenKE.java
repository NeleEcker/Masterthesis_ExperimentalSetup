package MasterThesis.Thesis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;

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

public class TranformDataToOpenKE {
	public static void main(String[]args) {
		TranformDataToOpenKE transform = new TranformDataToOpenKE();
		transform.transformData();
	}
	
	private void transformData() {
		String modelLocation = "/home/necker/Documents/Datasets/mappingbased_objects_en_2016_10.ttl";
		String train2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/train2id.txt";
		String diffLocation = "/home/necker/Documents/Datasets/Difference.ttl";
		HashSet<String> entities = new HashSet<String>();
		HashSet<String> properties = new HashSet<String>();
		Dataset ds = this.createDataset(modelLocation);
		Model model = this.loadModel(modelLocation, ds);
		ds.begin(ReadWrite.READ);
		int numberStatements = this.storeEntitesAndProperties(model, entities, properties);
		ds.end();
		ArrayList<String> entitiesList = new ArrayList<String>(entities);
		ArrayList<String> propertyList = new ArrayList<String>(properties);
		ds.begin(ReadWrite.READ);
		this.writeStatementFile(train2IdLocation, model, entitiesList, propertyList, numberStatements);
		ds.end();
		Dataset dsDiff = this.createDataset(diffLocation);
		Model modelDiff = this.loadModel(diffLocation, dsDiff);
		dsDiff.begin(ReadWrite.READ);
		int numberStatementsDiff = this.storeEntitesAndProperties(modelDiff, entitiesList, propertyList);
		dsDiff.end();
		String properties2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/relation2id.txt";
		String entities2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/entity2id.txt";
		this.writeListFiles(propertyList, properties2IdLocation);
		System.out.println("I was here 6");
		this.writeListFiles(entitiesList, entities2IdLocation);
		System.out.println("I was here 7");
		String diff2IdLocation = "/home/necker/Documents/Datasets/TrainingFiles/diff2id.txt";
		dsDiff.begin(ReadWrite.READ);
		this.writeStatementFile(diff2IdLocation, modelDiff, entitiesList, propertyList, numberStatementsDiff);
		dsDiff.end();
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
	
	private int storeEntitesAndProperties(Model model, HashSet<String> entities, HashSet<String> properties) {
		StmtIterator iter = model.listStatements();
		int i = 0;
		while(iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource res = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode node = stmt.getObject();
			
			this.addElementToSet(entities, res.toString());
			this.addElementToSet(entities, node.toString());
			this.addElementToSet(properties, prop.toString());
			
			i++;
			if(i%1000 == 0) {
				System.out.println(i);
			}
		}
		return i;
	}
	 
	private HashSet<String> addElementToSet(HashSet<String> set, String elem) {
		if(!set.contains(elem)) {
			set.add(elem);
		}
		
		return set;
	}
	
	private int storeEntitesAndProperties(Model model, ArrayList<String> entities, ArrayList<String> properties) {
		StmtIterator iter = model.listStatements();
		int i = 0;
		while(iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource res = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode node = stmt.getObject();
			
			entities = this.addElementToList(entities, res.toString());
			entities = this.addElementToList(entities, node.toString());
			properties = this.addElementToList(properties, prop.toString());
			
			i++;
			if(i%1000 == 0) {
				System.out.println(i);
			}
		}
		return i;
	}
	
	private ArrayList<String> addElementToList(ArrayList<String> list, String elem) {
		if(!list.contains(elem)) {
			list.add(elem);
		}
		
		return list;
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
				bw.write(line);
				bw.newLine();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeStatementFile(String location, Model model, ArrayList<String> entities, ArrayList<String> properties, int size) {
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
				
				int indexRes = entities.indexOf(res.toString());
				int indexProp = properties.indexOf(prop.toString());
				int indexNode = entities.indexOf(node.toString());
				
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
		}
	}

}
