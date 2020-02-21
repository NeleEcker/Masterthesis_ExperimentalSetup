package MasterThesis.Thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RandomSampleNegEntities {

	public static void main(String[] args) {
		RandomSampleNegEntities rand = new RandomSampleNegEntities();
		rand.sampleNell();
		rand.sampleDbpedia();
	}
	
	private void sampleNell() {
		String directory = "/home/necker/eclipse-workspace/Thesis/Datasets/FeedbackFiles/NellNegSample.txt";
		String location = "/home/necker/eclipse-workspace/Thesis/Datasets/FeedbackFiles/RandomElementsNellNegSample4.txt";
		ArrayList<String> negSample = this.readNegSample(directory);
		ArrayList<String> randomElements = this.chooseRandom(negSample, 100);
		this.writeElementsToFile(randomElements, location);
	}
	
	private ArrayList<String> readNegSample(String directory){
		ArrayList<String> negSamples = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(directory));
			String line;
			while((line = br.readLine()) != null) {
				negSamples.add(line);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return negSamples;
	}
	
	private ArrayList<String> chooseRandom(ArrayList<String> negSamples, int numberElements) {
		ArrayList<String> randomSelection = new ArrayList<String>();
		for(int i = 0; i<numberElements; i++) {
			Random rand = new Random(); 
	        randomSelection.add(negSamples.get(rand.nextInt(negSamples.size())));
		}
		return randomSelection;
	}
	
	private void writeElementsToFile(ArrayList<String> randomElements, String location) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(location);
			bw = new BufferedWriter(fw);
			
			for(int i = 0; i < randomElements.size(); i++) {
				bw.write(randomElements.get(i));
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sampleDbpedia() {
		String locationNegSamples = "/data/GitHub/OpenKE-master/masterthesis/DBPedia500KNew/diff2id.txt";
		String locationRandomSample = "/data/GitHub/OpenKE-master/masterthesis/DBPedia500KNew/randomNegSampleDBPedia4.txt";
		String entitiesLocation = "/data/GitHub/OpenKE-master/masterthesis/DBPedia500KNew/entity2id.txt";
		String relationsLocation = "/data/GitHub/OpenKE-master/masterthesis/DBPedia500KNew/relation2id.txt";
		HashMap<String, String> entities = this.getElementsFromList(entitiesLocation);
		HashMap<String, String> relations = this.getElementsFromList(relationsLocation);
		ArrayList<String> negSamples = this.readNegSampleFromList(entities, relations, locationNegSamples);
		ArrayList<String> randomSample = this.chooseRandom(negSamples, 100);
		this.writeElementsToFile(randomSample, locationRandomSample);
	}
	
	private HashMap<String, String> getElementsFromList(String location) {
		HashMap<String, String> elements = new HashMap<String,String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(location));
			br.readLine();
			String line;
			while((line = br.readLine()) != null ) {
				String[]parts = line.split("\\s+");
				elements.put(parts[1], parts[0]);
				
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return elements;
	}
	
	private ArrayList<String> readNegSampleFromList(HashMap<String, String> entities, HashMap<String, String> relations, String location) {
		ArrayList<String> negSamples = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(location));
			br.readLine();
			String line;
			while((line = br.readLine()) != null) {
				String[] parts = line.split("\\s+");
				String negSample = entities.get(parts[0]) + " " + relations.get(parts[2]) + " " + entities.get(parts[1]);
				negSamples.add(negSample);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return negSamples;
	}

}
